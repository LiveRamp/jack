package com.rapleaf.jack.store.field;

import java.util.function.Function;

import com.google.gson.JsonObject;
import org.joda.time.DateTime;

import com.rapleaf.jack.store.JsRecord;
import com.rapleaf.jack.store.executors.RecordUpdater;

public final class JsFields {

  private JsFields() {
  }

  // value fields

  public static JsValueField<Integer> createIntField(String key) {
    return new JsValueField<>(key, RecordUpdater::putInt, JsRecord::getInt);
  }

  public static JsValueField<Long> createLongField(String key) {
    return new JsValueField<>(key, RecordUpdater::putLong, JsRecord::getLong);
  }

  public static JsValueField<String> createStringField(String key) {
    return new JsValueField<>(key, RecordUpdater::putString, JsRecord::getString);
  }

  public static JsValueField<Boolean> createBooleanField(String key) {
    return new JsValueField<>(key, RecordUpdater::putBoolean, JsRecord::getBoolean);
  }

  public static JsValueField<Double> createDoubleField(String key) {
    return new JsValueField<>(key, RecordUpdater::putDouble, JsRecord::getDouble);
  }

  public static JsValueField<DateTime> createDateTimeField(String key) {
    return new JsValueField<>(key, RecordUpdater::putDateTime, JsRecord::getDateTime);
  }

  public static JsValueField<JsonObject> createJsonField(String key) {
    return new JsValueField<>(key, RecordUpdater::putJson, JsRecord::getJson);
  }

  // list fields

  public static JsListField<Integer> createIntListField(String key) {
    return new JsListField<>(key, RecordUpdater::putIntList, JsRecord::getIntList);
  }

  public static JsListField<Long> createLongListField(String key) {
    return new JsListField<>(key, RecordUpdater::putLongList, JsRecord::getLongList);
  }

  public static JsListField<String> createStringListField(String key) {
    return new JsListField<>(key, RecordUpdater::putStringList, JsRecord::getStringList);
  }

  public static JsListField<Boolean> createBooleanListField(String key) {
    return new JsListField<>(key, RecordUpdater::putBooleanList, JsRecord::getBooleanList);
  }

  public static JsListField<Double> createDoubleListField(String key) {
    return new JsListField<>(key, RecordUpdater::putDoubleList, JsRecord::getDoubleList);
  }

  public static JsListField<DateTime> createDateTimeListField(String key) {
    return new JsListField<>(key, RecordUpdater::putDateTimeList, JsRecord::getDateTimeList);
  }

  // enum fields

  public static <E extends Enum<E>> JsEnumNameField<E> createEnumNameField(String key, Function<String, E> nameToEnumFunction) {
    return new JsEnumNameField<>(key, nameToEnumFunction, RecordUpdater::putString, JsRecord::getString);
  }

  public static <E extends Enum<E>> JsEnumValueField<E> createEnumValueField(String key, Function<E, Integer> enumToValueFunction, Function<Integer, E> valueToEnumFunction) {
    return new JsEnumValueField<>(key, enumToValueFunction, valueToEnumFunction, RecordUpdater::putInt, JsRecord::getInt);
  }

}
