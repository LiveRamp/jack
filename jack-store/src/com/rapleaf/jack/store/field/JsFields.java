package com.rapleaf.jack.store.field;

import java.time.LocalDateTime;
import java.util.function.Function;

import com.google.gson.JsonObject;

import com.rapleaf.jack.store.JsRecord;
import com.rapleaf.jack.store.iface.ValueIndexer;

public final class JsFields {

  private JsFields() {
  }

  // value fields

  public static JsValueField<Integer> createIntField(String key) {
    return new JsValueField<>(key, ValueIndexer::putInt, JsRecord::getInt);
  }

  public static JsValueField<Long> createLongField(String key) {
    return new JsValueField<>(key, ValueIndexer::putLong, JsRecord::getLong);
  }

  public static JsValueField<String> createStringField(String key) {
    return new JsValueField<>(key, ValueIndexer::putString, JsRecord::getString);
  }

  public static JsValueField<Boolean> createBooleanField(String key) {
    return new JsValueField<>(key, ValueIndexer::putBoolean, JsRecord::getBoolean);
  }

  public static JsValueField<Double> createDoubleField(String key) {
    return new JsValueField<>(key, ValueIndexer::putDouble, JsRecord::getDouble);
  }

  public static JsValueField<LocalDateTime> createDateTimeField(String key) {
    return new JsValueField<>(key, ValueIndexer::putDateTime, JsRecord::getDateTime);
  }

  public static JsValueField<JsonObject> createJsonField(String key) {
    return new JsValueField<>(key, ValueIndexer::putJson, JsRecord::getJson);
  }

  // list fields

  public static JsListField<Integer> createIntListField(String key) {
    return new JsListField<>(key, ValueIndexer::putIntList, JsRecord::getIntList);
  }

  public static JsListField<Long> createLongListField(String key) {
    return new JsListField<>(key, ValueIndexer::putLongList, JsRecord::getLongList);
  }

  public static JsListField<String> createStringListField(String key) {
    return new JsListField<>(key, ValueIndexer::putStringList, JsRecord::getStringList);
  }

  public static JsListField<Boolean> createBooleanListField(String key) {
    return new JsListField<>(key, ValueIndexer::putBooleanList, JsRecord::getBooleanList);
  }

  public static JsListField<Double> createDoubleListField(String key) {
    return new JsListField<>(key, ValueIndexer::putDoubleList, JsRecord::getDoubleList);
  }

  public static JsListField<LocalDateTime> createDateTimeListField(String key) {
    return new JsListField<>(key, ValueIndexer::putDateTimeList, JsRecord::getDateTimeList);
  }

  // enum fields

  public static <E extends Enum<E>> JsEnumNameField<E> createEnumNameField(String key, Function<String, E> nameToEnumFunction) {
    return new JsEnumNameField<>(key, nameToEnumFunction, ValueIndexer::putString, JsRecord::getString);
  }

  public static <E extends Enum<E>> JsEnumValueField<E> createEnumValueField(String key, Function<E, Integer> enumToValueFunction, Function<Integer, E> valueToEnumFunction) {
    return new JsEnumValueField<>(key, enumToValueFunction, valueToEnumFunction, ValueIndexer::putInt, JsRecord::getInt);
  }

}
