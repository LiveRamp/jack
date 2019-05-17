package com.rapleaf.jack.queries;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLRecoverableException;
import java.util.List;
import java.util.Optional;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rapleaf.jack.BaseDatabaseConnection;

public class GenericDeletion extends AbstractExecution implements JoinableExecution {
  private static final Logger LOG = LoggerFactory.getLogger(GenericDeletion.class);

  private final boolean allowBulkOperation;
  private final List<Table> includedTables;
  private final List<JoinCondition> joinConditions;
  private final List<GenericConstraint> whereConstraints;
  private final List<Object> parameters;

  private GenericDeletion(BaseDatabaseConnection dbConnection, boolean allowBulkOperation, AbstractTable table) {
    super(dbConnection);
    this.allowBulkOperation = allowBulkOperation;
    this.includedTables = Lists.newArrayList(table);
    this.joinConditions = Lists.newArrayList();
    this.whereConstraints = Lists.newLinkedList();
    this.parameters = Lists.newLinkedList();
  }

  public static Builder create(BaseDatabaseConnection dbConnection, boolean allowBulkOperation) {
    return new Builder(dbConnection, allowBulkOperation);
  }

  @Override
  public void addParameters(List parameters) {
    this.parameters.addAll(parameters);
  }

  @Override
  public void addJoinCondition(JoinCondition joinCondition) {
    this.includedTables.add(joinCondition.getTable());
    this.joinConditions.add(joinCondition);
  }

  public static class Builder {
    private final BaseDatabaseConnection dbConnection;
    private final boolean allowBulkOperation;

    public Builder(BaseDatabaseConnection dbConnection, boolean allowBulkOperation) {
      this.dbConnection = dbConnection;
      this.allowBulkOperation = allowBulkOperation;
    }

    public GenericDeletion from(AbstractTable table) {
      return new GenericDeletion(dbConnection, allowBulkOperation, table);
    }
  }

  public JoinConditionBuilder<GenericDeletion> leftJoin(Table table) {
    this.parameters.addAll(table.getParameters());
    return new JoinConditionBuilder<>(this, JoinType.LEFT_JOIN, table);
  }

  public JoinConditionBuilder<GenericDeletion> rightJoin(Table table) {
    this.parameters.addAll(table.getParameters());
    return new JoinConditionBuilder<>(this, JoinType.RIGHT_JOIN, table);
  }

  public JoinConditionBuilder<GenericDeletion> innerJoin(Table table) {
    this.parameters.addAll(table.getParameters());
    return new JoinConditionBuilder<>(this, JoinType.INNER_JOIN, table);
  }

  public GenericDeletion where(GenericConstraint constraint, GenericConstraint... constraints) {
    this.whereConstraints.add(constraint);
    this.parameters.addAll(constraint.getParameters());
    for (GenericConstraint genericConstraint : constraints) {
      this.whereConstraints.add(genericConstraint);
      this.parameters.addAll(genericConstraint.getParameters());
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
  public String getQueryStatement() {
    return getFromClause() +
        getJoinClause() +
        getWhereClause();
  }

  private String getFromClause() {
    Table firstTable = includedTables.get(0);
    return "DELETE " + firstTable.getName() + " FROM " + firstTable.getName() + " ";
  }

  private String getJoinClause() {
    return getClauseFromQueryConditions(joinConditions, "", "", " ");
  }

  private String getWhereClause() {
    return getClauseFromQueryConditions(whereConstraints, "WHERE ", " AND ", " ");
  }

  @Override
  protected List<Object> getParameters() {
    return parameters;
  }
}
