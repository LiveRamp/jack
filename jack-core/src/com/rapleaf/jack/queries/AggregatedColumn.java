package com.rapleaf.jack.queries;

public class AggregatedColumn<T> extends Column<T> {

  private enum Function {
    COUNT, AVG, SUM, MAX, MIN
  }

  private final Function function;
  private final String sqlKeyword;
  private final String alias;

  private AggregatedColumn(Column column, Function function, String sqlKeyword, String alias) {
    super(column.table, column.field, column.type);
    this.function = function;
    this.sqlKeyword = sqlKeyword;
    this.alias = alias;
  }

  private AggregatedColumn(Column column, Function function) {
    this(column, function, createSqlKeyword(column, function), createAlias(column, function));
  }

  public static <T> AggregatedColumn<Integer> COUNT(Column<T> column) {
    return new AggregatedColumn<>(column.as(Integer.class), Function.COUNT);
  }

  public static <T extends Number> AggregatedColumn<Number> AVG(Column<T> column) {
    return new AggregatedColumn<>(column, Function.AVG);
  }

  public static <T extends Number> AggregatedColumn<T> SUM(Column<T> column) {
    return new AggregatedColumn<>(column, Function.SUM);
  }

  public static <T extends Number> AggregatedColumn<T> MAX(Column<T> column) {
    return new AggregatedColumn<>(column, Function.MAX);
  }

  public static <T extends Number> AggregatedColumn<T> MIN(Column<T> column) {
    return new AggregatedColumn<>(column, Function.MIN);
  }

  @Override
  AggregatedColumn<T> forTable(String tableAlias) {
    return new AggregatedColumn<>(super.forTable(tableAlias), function, alias, alias);
  }

  /**
   * @return SQL keyword for select clause. For aggregated column,
   * it is in the form of "column AS alias".
   */
  @Override
  String getSelectKeyword() {
    return sqlKeyword + " AS " + alias;
  }

  /**
   * @return column alias for select clause. For aggregated column,
   * it is just the alias.
   */
  @Override
  String getSelectAlias() {
    return alias;
  }

  @Override
  public String getSqlKeyword() {
    return sqlKeyword;
  }

  /**
   * @return the sql expression that applies the function on the column.
   * E.g. MAX(users.id)
   */
  private static String createSqlKeyword(Column column, Function function) {
    return String.format("%s(%s)", function.toString(), column.getSqlKeyword());
  }

  /**
   * @return an alias that concatenate function and column name with underscore.
   * E.g. MAX(users.id) => users_id_max
   */
  private static String createAlias(Column column, Function function) {
    return column.getSqlKeyword().replaceAll("\\.", "_") + "_" + function.name().toLowerCase();
  }
}
