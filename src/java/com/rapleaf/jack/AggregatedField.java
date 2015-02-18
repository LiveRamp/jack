package com.rapleaf.jack;

public class AggregatedField extends ModelField {

  private enum Function {
    COUNT, AVE, SUM, MAX, MIN
  }

  private final Function function;

  private AggregatedField(ModelField modelField, Function function) {
    super(modelField.getModel(), null, modelField.getField(), modelField.getType());
    this.function = function;
  }

  public static AggregatedField COUNT(ModelField modelField) {
    return new AggregatedField(modelField, Function.COUNT);
  }

  public static AggregatedField AVE(ModelField modelField) {
    return new AggregatedField(modelField, Function.AVE);
  }

  public static AggregatedField SUM(ModelField modelField) {
    return new AggregatedField(modelField, Function.SUM);
  }

  public static AggregatedField MAX(ModelField modelField) {
    return new AggregatedField(modelField, Function.MAX);
  }

  public static AggregatedField MIN(ModelField modelField) {
    return new AggregatedField(modelField, Function.MIN);
  }

  @Override
  public String getSqlKeyword() {
    return function.toString() + "(" + super.getSqlKeyword() + ")";
  }
}
