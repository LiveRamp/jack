package com.rapleaf.jack.store;

public enum ValueType {
  SCOPE(0),

  BOOLEAN(10), INT(20), LONG(30), DOUBLE(40), DATETIME(50), STRING(60),

  JSON_STRING(Category.JSON, 100), JSON_BOOLEAN(Category.JSON, 110), JSON_NUMBER(Category.JSON, 120),
  JSON_EMPTY(Category.JSON, 130), JSON_NULL(Category.JSON, 140),

  BOOLEAN_LIST(Category.LIST, 1000), INT_LIST(Category.LIST, 1010), LONG_LIST(Category.LIST, 1020),
  DOUBLE_LIST(Category.LIST, 1030), DATETIME_LIST(Category.LIST, 1040), STRING_LIST(Category.LIST, 1050);

  public final Category category;
  public final int value;

  ValueType(int value) {
    this.category = Category.PRIMITIVE;
    this.value = value;
  }

  ValueType(Category category, int value) {
    this.category = category;
    this.value = value;
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

  public static ValueType findByValue(int value) {
    switch (value) {
      case 0:
        return SCOPE;
      case 10:
        return BOOLEAN;
      case 20:
        return INT;
      case 30:
        return LONG;
      case 40:
        return DOUBLE;
      case 50:
        return DATETIME;
      case 60:
        return STRING;
      case 100:
        return JSON_STRING;
      case 110:
        return JSON_BOOLEAN;
      case 120:
        return JSON_NUMBER;
      case 130:
        return JSON_EMPTY;
      case 140:
        return JSON_NULL;
      case 1000:
        return BOOLEAN_LIST;
      case 1010:
        return INT_LIST;
      case 1020:
        return LONG_LIST;
      case 1030:
        return DOUBLE_LIST;
      case 1040:
        return DATETIME_LIST;
      case 1050:
        return STRING_LIST;
      default:
        throw new IllegalArgumentException("Unexpected value " + value);
    }
  }

  public enum Category {
    PRIMITIVE, JSON, LIST
  }

}
