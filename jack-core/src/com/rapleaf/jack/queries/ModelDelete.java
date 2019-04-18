package com.rapleaf.jack.queries;

import java.util.Collection;

/**
 * Represents a delete statement that may not explicitly list IDs.
 */
public class ModelDelete {
  private WhereClause whereClause;

  /**
   * This class should only be constructed in {@link AbstractDeleteBuilder}.
   */
  ModelDelete() {
    this.whereClause = new WhereClause();
  }

  public WhereClause getWhereClause() {
    return this.whereClause;
  }

  public void addConstraint(WhereConstraint constraint) {
    whereClause.addConstraint(constraint);
  }

  public void addIds(Collection<Long> ids) {
    whereClause.addIds(ids);
  }

  public void addId(Long id) {
    whereClause.addId(id);
  }

  public String getStatement(String tableName) {
    String statement = "DELETE FROM " + tableName + " ";
    statement += whereClause.getSqlString();
    return statement;
  }
}
