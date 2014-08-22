package com.rapleaf.jack;

public class AggregatorFunction {
  private Enum field;
  private String aggregatorKeyword;

  public AggregatorFunction(Enum field, String aggregatorKeyword) {
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
    // Hack to keep the name of the column in order to be able to access it later
    return aggregatorKeyword + "(" + field + ") AS " + field;
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