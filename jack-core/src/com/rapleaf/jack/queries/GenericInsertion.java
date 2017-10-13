package com.rapleaf.jack.queries;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLRecoverableException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rapleaf.jack.BaseDatabaseConnection;
import com.rapleaf.jack.util.JackUtility;

public class GenericInsertion extends AbstractExecution {
  private static final Logger LOG = LoggerFactory.getLogger(GenericInsertion.class);

  private final Table table;
  private final Map<Column, List<Object>> values;
  private int rowCount = 0;

  private GenericInsertion(BaseDatabaseConnection dbConnection, Table table) {
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

  @SafeVarargs
  public final <T> GenericInsertion set(Column<T> column, T value, T... values) {
    checkRowCount(1 + values.length);
    List<T> allValues = Lists.newArrayListWithCapacity(1 + values.length);
    allValues.add(value);
    allValues.addAll(Arrays.asList(values));
    return set(column, allValues);
  }

  public <T> GenericInsertion set(Column<T> column, List<T> values) {
    if (values == null) {
      checkRowCount(1);
      List<T> processedValues = Lists.newArrayListWithCapacity(1);
      processedValues.add(null);
      return set(column, processedValues);
    }

    Preconditions.checkArgument(!values.isEmpty(), "Must provide at least one value");

    checkRowCount(values.size());
    List<Object> processedValues = Lists.newArrayListWithCapacity(values.size());
    for (T value : values) {
      if (column.isDateColumn()) {
        processedValues.add(JackUtility.FORMATTER_FUNCTION_MAP.get(column.getType()).apply(Long.class.cast(value)));
      } else {
        processedValues.add(value);
      }
    }
    this.values.put(column, processedValues);

    return this;
  }

  private void checkRowCount(int newRowCount) {
    if (rowCount == 0) {
      rowCount = newRowCount;
    } else {
      Preconditions.checkArgument(newRowCount == rowCount);
    }
  }

  public Insertions execute() throws IOException {
    int retryCount = 0;
    PreparedStatement preparedStatement = getPreparedStatement(Optional.of(Statement.RETURN_GENERATED_KEYS));

    while (true) {
      try {
        return InsertionFetcher.getCreationResults(preparedStatement, rowCount, dbConnection);
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
  public String getRawStatement() {
    return getInsertClause()
        + getColumnsClause()
        + getValuesClause();
  }

  @Override
  protected Collection<Object> getParameters() {
    List<Object> parameters = Lists.newLinkedList();
    for (int i = 0; i < rowCount; ++i) {
      for (List<Object> list : values.values()) {
        parameters.add(list.get(i));
      }
    }
    return parameters;
  }

  private String getInsertClause() {
    return "INSERT INTO " + table.getName() + " ";
  }

  private String getColumnsClause() {
    return getClauseFromColumns(values.keySet(), "(", ", ", ") ");
  }

  private String getValuesClause() {
    StringBuilder clause = new StringBuilder("VALUES ");

    if (rowCount == 0) {
      clause.append("()");
    }

    for (int i = 0; i < rowCount; ++i) {
      clause.append("(");
      Iterator<List<Object>> it = values.values().iterator();
      while (it.hasNext()) {
        Object parameter = it.next().get(i);
        if (parameter == null) {
          clause.append("NULL");
        } else {
          clause.append("?");
        }
        if (it.hasNext()) {
          clause.append(", ");
        }
      }
      clause.append(")");
      if (i < rowCount - 1) {
        clause.append(", ");
      }
    }

    return clause.toString();
  }

}
