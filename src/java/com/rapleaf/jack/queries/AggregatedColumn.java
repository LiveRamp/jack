package com.rapleaf.jack.queries;

public class AggregatedColumn<T> extends Column<T> {

  private enum Function {
    COUNT, AVG, SUM, MAX, MIN
  }

  private final Function function;

  private AggregatedColumn(Column column, Function function) {
    super(column);
    this.function = function;
  }

  public static AggregatedColumn<Integer> COUNT(Column column) {
    return new AggregatedColumn<Integer>(column, Function.COUNT);
  }

  public static <T extends Number> AggregatedColumn<Number> AVG(Column<T> column) {
    return new AggregatedColumn<Number>(column, Function.AVG);
  }

  public static <T extends Number> AggregatedColumn<T> SUM(Column<T> column) {
    return new AggregatedColumn<T>(column, Function.SUM);
  }

  public static <T extends Number> AggregatedColumn<T> MAX(Column<T> column) {
    return new AggregatedColumn<T>(column, Function.MAX);
  }

  public static <T extends Number> AggregatedColumn<T> MIN(Column<T> column) {
    return new AggregatedColumn<T>(column, Function.MIN);
  }

  @Override
  public String getSqlKeyword() {
    return function.toString() + "(" + super.getSqlKeyword() + ")";
  }
}
