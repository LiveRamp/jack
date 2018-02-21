package com.rapleaf.jack.store.iface;

import java.util.List;

import com.google.gson.JsonObject;
import org.joda.time.DateTime;

public interface ValueContainer<E extends ValueContainer<E>> {

  Object get(String key);

  Boolean getBoolean(String key);

  Integer getInt(String key);

  Long getLong(String key);

  Double getDouble(String key);

  DateTime getDateTime(String key);

  String getString(String key);

  JsonObject getJson(String key);

  List getList(String key);

  List<Boolean> getBooleanList(String key);

  List<Integer> getIntList(String key);

  List<Long> getLongList(String key);

  List<Double> getDoubleList(String key);

  List<DateTime> getDateTimeList(String key);

  List<String> getStringList(String key);

}
