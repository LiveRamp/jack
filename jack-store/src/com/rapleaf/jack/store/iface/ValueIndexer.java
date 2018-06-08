package com.rapleaf.jack.store.iface;

import java.time.LocalDateTime;
import java.util.List;

import com.google.gson.JsonObject;

public interface ValueIndexer<E extends ValueIndexer<E>> {

  E put(String key, Object value);

  E putBoolean(String key, Boolean value);

  E putInt(String key, Integer value);

  E putLong(String key, Long value);

  E putDouble(String key, Double value);

  E putDateTime(String key, LocalDateTime value);

  E putString(String key, String value);

  E putJson(String key, JsonObject json);

  E putList(String key, List<Object> valueList);

  E putBooleanList(String key, List<Boolean> valueList);

  E putIntList(String key, List<Integer> valueList);

  E putLongList(String key, List<Long> valueList);

  E putDoubleList(String key, List<Double> valueList);

  E putDateTimeList(String key, List<LocalDateTime> valueList);

  E putStringList(String key, List<String> valueList);

}
