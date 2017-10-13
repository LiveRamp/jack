package com.rapleaf.jack.queries;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLRecoverableException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rapleaf.jack.BaseDatabaseConnection;

public class GenericDeletion extends AbstractExecution {
  private static final Logger LOG = LoggerFactory.getLogger(GenericDeletion.class);

  private final boolean allowBulkOperation;
  private final Table table;
  private final List<GenericConstraint> whereConstraints;
  private final List<Object> whereParameters;

  private GenericDeletion(BaseDatabaseConnection dbConnection, boolean allowBulkOperation, Table table) {
    super(dbConnection);
    this.allowBulkOperation = allowBulkOperation;
    this.table = table;
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

    public GenericDeletion from(Table table) {
      return new GenericDeletion(dbConnection, allowBulkOperation, table);
    }
  }

  public GenericDeletion where(GenericConstraint constraint, GenericConstraint... constraints) {
    this.whereConstraints.add(constraint);
    this.whereParameters.addAll(constraint.getParameters());
    for (GenericConstraint genericConstraint : constraints) {
      this.whereConstraints.add(genericConstraint);
      this.whereParameters.addAll(genericConstraint.getParameters());
    }
    return this;
  }

  public Deletions execute() throws IOException {
    checkBulkOperation(allowBulkOperation, whereConstraints);

    int retryCount = 0;
    PreparedStatement preparedStatement = getPreparedStatement(Optional.empty());

    while (true) {
      try {
        return DeletionFetcher.getDeletionResults(preparedStatement, dbConnection);
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
    return getFromClause() +
        getWhereClause();
  }

  private String getFromClause() {
    return "DELETE FROM " + table.getName() + " ";
  }

  private String getWhereClause() {
    return getClauseFromQueryConditions(whereConstraints, "WHERE ", " AND ", " ");
  }

  @Override
  protected Collection<Object> getParameters() {
    return whereParameters;
  }
}
