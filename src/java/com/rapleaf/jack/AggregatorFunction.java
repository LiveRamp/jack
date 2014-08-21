package com.rapleaf.jack;

public class AggregatorFunction {
  private Enum field;
  private String sqlKeyword;

  public AggregatorFunction(Enum field, String sqlKeyword) {
    this.field = field;
    this.sqlKeyword = sqlKeyword;
  }

  public String getSqlKeyword() {
    return sqlKeyword;
  }

  public Enum getField() {
    return field;
  }

  public String getsqlClause() {
    if (field == null) {
      return sqlKeyword + "(*)";
    }
    return sqlKeyword + "(" + field + ")";
  }

  public static AggregatorFunction count(Enum field) {
    return new AggregatorFunction(field, "COUNT");
  }

  public static AggregatorFunction countAll() {
    return count(null);
  }

  public static AggregatorFunction sum(Enum field) {
    return new AggregatorFunction(field, "SUM");
  }

  public static AggregatorFunction avg(Enum field) {
    return new AggregatorFunction(field, "AVG");
  }

  public static AggregatorFunction min(Enum field) {
    return new AggregatorFunction(field, "MIN");
  }

  public static AggregatorFunction max(Enum field) {
    return new AggregatorFunction(field, "MAX");
  }
}
