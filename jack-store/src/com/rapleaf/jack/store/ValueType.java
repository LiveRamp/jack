package com.rapleaf.jack.store;

public enum ValueType {

  BOOLEAN, INT, LONG, DOUBLE, DATETIME, STRING,

  JSON_STRING(Category.JSON), JSON_BOOLEAN(Category.JSON), JSON_NUMBER(Category.JSON),
  JSON_EMPTY(Category.JSON), JSON_NULL(Category.JSON),

  BOOLEAN_LIST(Category.LIST), INT_LIST(Category.LIST), LONG_LIST(Category.LIST),
  DOUBLE_LIST(Category.LIST), DATETIME_LIST(Category.LIST), STRING_LIST(Category.LIST);

  private final Category category;

  ValueType() {
    this.category = Category.PRIMITIVE;
  }

  ValueType(Category category) {
    this.category = category;
  }

  public Category getCategory() {
    return category;
  }

  public boolean isPrimitive() {
    return category == Category.PRIMITIVE;
  }

  public boolean isJson() {
    return category == Category.JSON;
  }

  public boolean isList() {
    return category == Category.LIST;
  }

  public enum Category {
    PRIMITIVE, JSON, LIST
  }

}
