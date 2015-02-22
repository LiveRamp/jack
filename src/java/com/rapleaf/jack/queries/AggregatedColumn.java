package com.rapleaf.jack.queries;

import com.rapleaf.jack.Column;

public class AggregatedColumn extends Column {

  private enum Function {
    COUNT, AVG, SUM, MAX, MIN
  }

  private final Function function;

  private AggregatedColumn(Column column, Function function) {
    super(column);
    this.function = function;
  }

  public static AggregatedColumn COUNT(Column column) {
    return new AggregatedColumn(column, Function.COUNT);
  }

  public static AggregatedColumn AVG(Column column) {
    return new AggregatedColumn(column, Function.AVG);
  }

  public static AggregatedColumn SUM(Column column) {
    return new AggregatedColumn(column, Function.SUM);
  }

  public static AggregatedColumn MAX(Column column) {
    return new AggregatedColumn(column, Function.MAX);
  }

  public static AggregatedColumn MIN(Column column) {
    return new AggregatedColumn(column, Function.MIN);
  }

  @Override
  public String getSqlKeyword() {
    return function.toString() + "(" + super.getSqlKeyword() + ")";
  }
}
