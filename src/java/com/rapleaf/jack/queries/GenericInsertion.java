package com.rapleaf.jack.queries;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLRecoverableException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rapleaf.jack.BaseDatabaseConnection;
import com.rapleaf.jack.util.JackUtility;

public class GenericInsertion extends BaseExecution {
  private static final Logger LOG = LoggerFactory.getLogger(GenericInsertion.class);

  private final Table table;
  private final Map<Column, Object> values;

  public GenericInsertion(BaseDatabaseConnection dbConnection, Table table) {
    super(dbConnection);
    this.table = table;
    this.values = Maps.newLinkedHashMap();
  }

  public static Builder create(BaseDatabaseConnection dbConnection) {
    return new Builder(dbConnection);
  }

  public static class Builder {
    private final BaseDatabaseConnection dbConnection;

    public Builder(BaseDatabaseConnection dbConnection) {
      this.dbConnection = dbConnection;
    }

    public GenericInsertion into(Table table) {
      return new GenericInsertion(dbConnection, table);
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

  public long execute() throws IOException {
    int retryCount = 0;
    PreparedStatement preparedStatement = getPreparedStatement(Optional.of(Statement.RETURN_GENERATED_KEYS));

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

  @Override
  protected String getQueryStatement() {
    return getInsertClause()
        + getColumnsClause()
        + getValuesClause();
  }

  @Override
  protected Collection<Object> getParameters() {
    return values.values();
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
