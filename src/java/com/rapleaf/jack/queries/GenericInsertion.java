package com.rapleaf.jack.queries;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLRecoverableException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.Map;

import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rapleaf.jack.BaseDatabaseConnection;
import com.rapleaf.jack.util.JackUtility;

public class GenericInsertion {
  private static final Logger LOG = LoggerFactory.getLogger(GenericInsertion.class);
  private static int MAX_CONNECTION_RETRIES = 1;

  private final BaseDatabaseConnection dbConnection;
  private final Table table;
  private final Map<Column, Object> values;

  public <T> GenericInsertion(BaseDatabaseConnection dbConnection, Table table, Column<T> column, T value) {
    this.dbConnection = dbConnection;
    this.table = table;
    this.values = Maps.newLinkedHashMap();
    values.put(column, value);
  }

  public static Builder create(BaseDatabaseConnection dbConnection) {
    return new Builder(dbConnection);
  }

  public static class Builder {
    private final BaseDatabaseConnection dbConnection;

    public Builder(BaseDatabaseConnection dbConnection) {
      this.dbConnection = dbConnection;
    }

    public Setter into(Table table) {
      return new Setter(dbConnection, table);
    }
  }

  public static class Setter {
    private final BaseDatabaseConnection dbConnection;
    private final Table table;

    Setter(BaseDatabaseConnection dbConnection, Table table) {
      this.dbConnection = dbConnection;
      this.table = table;
    }

    public <T> GenericInsertion set(Column<T> column, T value) {
      return new GenericInsertion(dbConnection, table, column, value);
    }
  }

  public <T> GenericInsertion set(Column<T> column, T value) {
    Object processedValue;
    if (column.isDateColumn()) {
      processedValue = JackUtility.FORMATTER_FUNCTION_MAP.get(column.getType()).apply(Long.class.cast(value));
    } else {
      processedValue = value;
    }
    values.put(column, processedValue);
    return this;
  }

  public String getSqlStatement() throws IOException {
    return this.getPreparedStatement().toString();
  }

  public long execute() throws IOException {
    int retryCount = 0;
    PreparedStatement preparedStatement = getPreparedStatement();

    while (true) {
      try {
        return InsertionFetcher.getCreationResults(preparedStatement, dbConnection);
      } catch (SQLRecoverableException e) {
        LOG.error(e.toString());
        if (++retryCount > MAX_CONNECTION_RETRIES) {
          throw new IOException(e);
        }
      } catch (SQLException e) {
        throw new IOException(e);
      }
    }
  }

  private PreparedStatement getPreparedStatement() throws IOException {
    PreparedStatement preparedStatement = dbConnection.getPreparedStatement(getQueryStatement(), Statement.RETURN_GENERATED_KEYS);
    setStatementParameters(preparedStatement);
    return preparedStatement;
  }

  private void setStatementParameters(PreparedStatement preparedStatement) throws IOException {
    int index = 0;
    for (Object parameter : values.values()) {
      if (parameter == null) {
        continue;
      }
      try {
        preparedStatement.setObject(++index, parameter);
      } catch (SQLException e) {
        throw new IOException(e);
      }
    }
  }

  private String getQueryStatement() {
    return getInsertClause()
        + getColumnsClause()
        + getValuesClause();
  }

  private String getInsertClause() {
    return "INSERT INTO " + table.getName() + " ";
  }

  private String getColumnsClause() {
    return GenericQuery.getClauseFromColumns(values.keySet(), "(", ", ", ") ");
  }

  private String getValuesClause() {
    StringBuilder clause = new StringBuilder("VALUES (");
    Iterator<Object> it = values.values().iterator();
    while (it.hasNext()) {
      Object parameter = it.next();
      if (parameter == null) {
        clause.append("NULL");
      } else {
        clause.append("?");
      }
      if (it.hasNext()) {
        clause.append(", ");
      }
    }
    return clause.append(")").toString();
  }

}
