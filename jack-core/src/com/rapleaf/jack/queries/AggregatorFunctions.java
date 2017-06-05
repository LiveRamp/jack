package com.rapleaf.jack.queries;

public class AggregatorFunctions {
  public static FieldSelector count(Enum field) {
    return new FieldSelector(field, "COUNT");
  }

  public static FieldSelector sum(Enum field) {
    return new FieldSelector(field, "SUM");
  }

  public static FieldSelector avg(Enum field) {
    return new FieldSelector(field, "AVG");
  }

  public static FieldSelector min(Enum field) {
    return new FieldSelector(field, "MIN");
  }

  public static FieldSelector max(Enum field) {
    return new FieldSelector(field, "MAX");
  }
}
