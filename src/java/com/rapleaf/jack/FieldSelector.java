package com.rapleaf.jack;

public class FieldSelector {
  private Enum field;
  private String aggregatorKeyword;

  public FieldSelector(Enum field, String aggregatorKeyword) {
    this.field = field;
    this.aggregatorKeyword = aggregatorKeyword;
  }

  public Enum getField() {
    return field;
  }

  public String getSqlClause() {
    if (field == null) {
      return aggregatorKeyword + "(*)";
    }
    if (aggregatorKeyword == null) {
      return getField().name();
    }
    // Hack to keep the name of the column in order to be able to access it later
    return aggregatorKeyword + "(" + field + ") AS " + field;
  }

  public boolean hasSameField(FieldSelector selector) {
    return selector.getField() == null ? field == null : selector.getField().equals(field);
  }
}
