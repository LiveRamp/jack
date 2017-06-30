package com.rapleaf.jack.store.field;

import java.util.function.Function;

import com.google.gson.JsonObject;
import org.joda.time.DateTime;

import com.rapleaf.jack.store.JsRecord;
import com.rapleaf.jack.store.executors.RecordIndexExecutor;

public final class JsFields {

  private JsFields() {
  }

  // value fields

  public static JsValueField<Integer> createIntField(String key) {
    return new JsValueField<>(key, RecordIndexExecutor::putInt, JsRecord::getInt);
  }

  public static JsValueField<Long> createLongField(String key) {
    return new JsValueField<>(key, RecordIndexExecutor::putLong, JsRecord::getLong);
  }

  public static JsValueField<String> createStringField(String key) {
    return new JsValueField<>(key, RecordIndexExecutor::putString, JsRecord::getString);
  }

  public static JsValueField<Boolean> createBooleanField(String key) {
    return new JsValueField<>(key, RecordIndexExecutor::putBoolean, JsRecord::getBoolean);
  }

  public static JsValueField<Double> createDoubleField(String key) {
    return new JsValueField<>(key, RecordIndexExecutor::putDouble, JsRecord::getDouble);
  }

  public static JsValueField<DateTime> createDateTimeField(String key) {
    return new JsValueField<>(key, RecordIndexExecutor::putDateTime, JsRecord::getDateTime);
  }

  public static JsValueField<JsonObject> createJsonField(String key) {
    return new JsValueField<>(key, RecordIndexExecutor::putJson, JsRecord::getJson);
  }

  // list fields

  public static JsListField<Integer> createIntListField(String key) {
    return new JsListField<>(key, RecordIndexExecutor::putIntList, JsRecord::getIntList);
  }

  public static JsListField<Long> createLongListField(String key) {
    return new JsListField<>(key, RecordIndexExecutor::putLongList, JsRecord::getLongList);
  }

  public static JsListField<String> createStringListField(String key) {
    return new JsListField<>(key, RecordIndexExecutor::putStringList, JsRecord::getStringList);
  }

  public static JsListField<Boolean> createBooleanListField(String key) {
    return new JsListField<>(key, RecordIndexExecutor::putBooleanList, JsRecord::getBooleanList);
  }

  public static JsListField<Double> createDoubleListField(String key) {
    return new JsListField<>(key, RecordIndexExecutor::putDoubleList, JsRecord::getDoubleList);
  }

  public static JsListField<DateTime> createDateTimeListField(String key) {
    return new JsListField<>(key, RecordIndexExecutor::putDateTimeList, JsRecord::getDateTimeList);
  }

  // enum fields

  public static <E extends Enum<E>> JsEnumNameField<E> createEnumNameField(String key, Function<String, E> nameToEnumFunction) {
    return new JsEnumNameField<>(key, nameToEnumFunction, RecordIndexExecutor::putString, JsRecord::getString);
  }

  public static <E extends Enum<E>> JsEnumValueField<E> createEnumValueField(String key, Function<E, Integer> enumToValueFunction, Function<Integer, E> valueToEnumFunction) {
    return new JsEnumValueField<>(key, enumToValueFunction, valueToEnumFunction, RecordIndexExecutor::putInt, JsRecord::getInt);
  }

}
