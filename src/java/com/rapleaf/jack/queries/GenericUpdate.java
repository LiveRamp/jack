package com.rapleaf.jack.queries;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLRecoverableException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rapleaf.jack.BaseDatabaseConnection;

public class GenericUpdate extends BaseExecution {
  private static final Logger LOG = LoggerFactory.getLogger(GenericUpdate.class);

  private final boolean allowBulkOperation;
  private final Table table;
  private final Map<Column, Object> values;
  private final List<GenericConstraint> whereConstraints;
  private final List<Object> whereParameters;

  private GenericUpdate(BaseDatabaseConnection dbConnection, boolean allowBulkOperation, Table table) {
    super(dbConnection);
    this.allowBulkOperation = allowBulkOperation;
    this.table = table;
    this.values = Maps.newLinkedHashMap();
    this.whereConstraints = Lists.newLinkedList();
    this.whereParameters = Lists.newLinkedList();
  }

  public static Builder create(BaseDatabaseConnection dbConnection, boolean allowBulkOperation) {
    return new Builder(dbConnection, allowBulkOperation);
  }

  public static class Builder {
    private final BaseDatabaseConnection dbConnection;
    private final boolean allowBulkOperation;

    public Builder(BaseDatabaseConnection dbConnection, boolean allowBulkOperation) {
      this.dbConnection = dbConnection;
      this.allowBulkOperation = allowBulkOperation;
    }

    public GenericUpdate table(Table table) {
      return new GenericUpdate(dbConnection, allowBulkOperation, table);
    }
  }

  public <T> GenericUpdate set(Column<T> column, T value) {
    this.values.put(column, value);
    return this;
  }

  public GenericUpdate where(GenericConstraint constraint, GenericConstraint... constraints) {
    this.whereConstraints.add(constraint);
    this.whereParameters.addAll(constraint.getParameters());
    for (GenericConstraint genericConstraint : constraints) {
      this.whereConstraints.add(genericConstraint);
      this.whereParameters.addAll(genericConstraint.getParameters());
    }
    return this;
  }

  public Updates execute() throws IOException {
    if (!allowBulkOperation) {
      Preconditions.checkState(
          !whereConstraints.isEmpty(),
          "Bulk operation is not allowed; either enable it, or specify at least one where constraint"
      );
    }

    int retryCount = 0;
    PreparedStatement preparedStatement = getPreparedStatement(Optional.empty());

    while (true) {
      try {
        return UpdateFetcher.getUpdateResults(preparedStatement, dbConnection);
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
    return getUpdateClause() +
        getSetClause() +
        getWhereClause();
  }

  private String getUpdateClause() {
    return "UPDATE " + table.getName() + " ";
  }

  private String getSetClause() {
    StringBuilder clause = new StringBuilder("SET ");

    Iterator<Map.Entry<Column, Object>> it = values.entrySet().iterator();
    while (it.hasNext()) {
      Map.Entry<Column, Object> entry = it.next();
      clause.append(entry.getKey().getSqlKeyword()).append(" = ");
      if (entry.getValue() == null) {
        clause.append("NULL");
      } else {
        clause.append("?");
      }
      if (it.hasNext()) {
        clause.append(", ");
      }
    }

    return clause.append(" ").toString();
  }

  private String getWhereClause() {
    return getClauseFromQueryConditions(whereConstraints, "WHERE ", " AND ", " ");
  }

  @Override
  protected Collection<Object> getParameters() {
    List<Object> parameters = Lists.newLinkedList();
    parameters.addAll(values.values());
    parameters.addAll(whereParameters);
    return parameters;
  }
}
