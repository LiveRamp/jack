package com.rapleaf.jack.store.field;

import java.util.function.BiFunction;
import java.util.function.Function;

import com.rapleaf.jack.store.JsRecord;
import com.rapleaf.jack.store.iface.InsertValue;

/**
 * Convert enum to value using {@link Function} Enum -> Integer
 * Convert value to enum using {@link Function} Integer -> Enum
 * <p>
 * Value CAN be different from {@link Enum#ordinal}, e.g. thrift TEnum
 */
public class JsEnumValueField<E extends Enum<E>> extends AbstractJsField<E> {

  JsEnumValueField(String key, Function<E, Integer> enumToValueFunction, Function<Integer, E> valueToEnumFunction, InsertValue<Integer> putMethod, BiFunction<JsRecord, String, Integer> getMethod) {
    super(key, convertPutMethod(enumToValueFunction, putMethod), convertGetMethod(valueToEnumFunction, getMethod));
  }

  private static <E extends Enum> InsertValue<E> convertPutMethod(Function<E, Integer> enumToValueFunction, InsertValue<Integer> putMethod) {
    return (executor, key, e) -> putMethod.apply(executor, key, enumToValueFunction.apply(e));
  }

  private static <E extends Enum<E>> BiFunction<JsRecord, String, E> convertGetMethod(Function<Integer, E> enumFunction, BiFunction<JsRecord, String, Integer> getMethod) {
    return (record, key) -> {
      int enumValue = getMethod.apply(record, key);
      return enumFunction.apply(enumValue);
    };
  }

}
