package com.rapleaf.jack.store.executors;

import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonObject;
import org.joda.time.DateTime;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rapleaf.jack.store.JsRecord;
import com.rapleaf.jack.store.JsRecords;
import com.rapleaf.jack.store.JsScope;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class TestRecordGetterExecutor extends BaseExecutorTestCase {
  private static final Logger LOG = LoggerFactory.getLogger(TestRecordGetterExecutor.class);

  @Test
  public void testValues() throws Exception {
    jackStore.scope("scope").indexRecord()
        .putBoolean(BOOLEAN_KEY, BOOLEAN_VALUE)
        .putInt(INT_KEY, INT_VALUE)
        .putLong(LONG_KEY, LONG_VALUE)
        .putDouble(DOUBLE_KEY, DOUBLE_VALUE)
        .putDateTime(DATETIME_KEY, DATETIME_VALUE)
        .putString(STRING_KEY, STRING_VALUE)
        .putJson(JSON_KEY, JSON_VALUE)
        .putBooleanList(BOOLEAN_LIST_KEY, BOOLEAN_LIST_VALUE)
        .putIntList(INT_LIST_KEY, INT_LIST_VALUE)
        .putLongList(LONG_LIST_KEY, LONG_LIST_VALUE)
        .putDoubleList(DOUBLE_LIST_KEY, DOUBLE_LIST_VALUE)
        .putDateTimeList(DATETIME_LIST_KEY, DATETIME_LIST_VALUE)
        .putStringList(STRING_LIST_KEY, STRING_LIST_VALUE)
        .execute();

    JsRecord record = jackStore.scope("scope").getRecord().get();

    assertEquals(BOOLEAN_VALUE, record.getBoolean(BOOLEAN_KEY));
    assertEquals(BOOLEAN_VALUE, record.get(BOOLEAN_KEY));

    assertEquals(INT_VALUE, (int)record.getInt(INT_KEY));
    assertEquals(INT_VALUE, record.get(INT_KEY));

    assertEquals(LONG_VALUE, (long)record.getLong(LONG_KEY));
    assertEquals(LONG_VALUE, record.get(LONG_KEY));

    assertEquals(DOUBLE_VALUE, record.getDouble(DOUBLE_KEY), 10e-5);
    assertEquals(DOUBLE_VALUE, (double)record.get(DOUBLE_KEY), 10e-5);

    assertTrue(DATETIME_VALUE.isEqual(record.getDateTime(DATETIME_KEY)));
    assertTrue(DATETIME_VALUE.isEqual((DateTime)record.get(DATETIME_KEY)));

    assertEquals(STRING_VALUE, record.getString(STRING_KEY));
    assertEquals(STRING_VALUE, record.get(STRING_KEY));

    assertEquals(JSON_VALUE, record.getJson(JSON_KEY));
    assertEquals(JSON_VALUE, record.get(JSON_KEY));

    assertEquals(BOOLEAN_LIST_VALUE, record.getBooleanList(BOOLEAN_LIST_KEY));
    assertEquals(BOOLEAN_LIST_VALUE, record.getList(BOOLEAN_LIST_KEY));

    assertEquals(INT_LIST_VALUE, record.getIntList(INT_LIST_KEY));
    assertEquals(INT_LIST_VALUE, record.getList(INT_LIST_KEY));

    assertEquals(LONG_LIST_VALUE, record.getLongList(LONG_LIST_KEY));
    assertEquals(LONG_LIST_VALUE, record.getList(LONG_LIST_KEY));

    assertEquals(DOUBLE_LIST_VALUE, record.getDoubleList(DOUBLE_LIST_KEY));
    assertEquals(DOUBLE_LIST_VALUE, record.getList(DOUBLE_LIST_KEY));

    assertEquals(DATETIME_LIST_VALUE.stream().map(DateTime::getMillis).collect(Collectors.toSet()), record.getDateTimeList(DATETIME_LIST_KEY).stream().map(DateTime::getMillis).collect(Collectors.toSet()));
    assertEquals(DATETIME_LIST_VALUE.stream().map(DateTime::getMillis).collect(Collectors.toSet()), ((List<DateTime>)record.getList(DATETIME_LIST_KEY)).stream().map(DateTime::getMillis).collect(Collectors.toSet()));

    assertEquals(STRING_LIST_VALUE, record.getStringList(STRING_LIST_KEY));
    assertEquals(STRING_LIST_VALUE, record.getList(STRING_LIST_KEY));
  }

  @Test
  public void testNullValues() throws Exception {
    jackStore.scope("scope").indexRecord()
        .putBoolean(BOOLEAN_KEY, null)
        .putInt(INT_KEY, null)
        .putLong(LONG_KEY, null)
        .putDouble(DOUBLE_KEY, null)
        .putDateTime(DATETIME_KEY, null)
        .putString(STRING_KEY, null)
        .putBooleanList(BOOLEAN_LIST_KEY, null)
        .putIntList(INT_LIST_KEY, null)
        .putLongList(LONG_LIST_KEY, null)
        .putDoubleList(DOUBLE_LIST_KEY, null)
        .putDateTimeList(DATETIME_LIST_KEY, null)
        .putStringList(STRING_LIST_KEY, null)
        .execute();

    JsRecord record = jackStore.scope("scope").getRecord().get();

    assertNull(record.getBoolean(BOOLEAN_KEY));
    assertNull(record.get(BOOLEAN_KEY));

    assertNull(record.getInt(INT_KEY));
    assertNull(record.get(INT_KEY));

    assertNull(record.getLong(LONG_KEY));
    assertNull(record.get(LONG_KEY));

    assertNull(record.getDouble(DOUBLE_KEY));
    assertNull(record.get(DOUBLE_KEY));

    assertNull(record.getDateTime(DATETIME_KEY));
    assertNull(record.get(DATETIME_KEY));

    assertNull(record.getString(STRING_KEY));
    assertNull(record.get(STRING_KEY));

    assertTrue(record.getBooleanList(BOOLEAN_LIST_KEY).isEmpty());
    assertTrue(record.getList(BOOLEAN_LIST_KEY).isEmpty());

    assertTrue(record.getIntList(INT_LIST_KEY).isEmpty());
    assertTrue(record.getList(INT_LIST_KEY).isEmpty());

    assertTrue(record.getLongList(LONG_LIST_KEY).isEmpty());
    assertTrue(record.getList(LONG_LIST_KEY).isEmpty());

    assertTrue(record.getDoubleList(DOUBLE_LIST_KEY).isEmpty());
    assertTrue(record.getList(DOUBLE_LIST_KEY).isEmpty());

    assertTrue(record.getDateTimeList(DATETIME_LIST_KEY).isEmpty());
    assertTrue(record.getList(DATETIME_LIST_KEY).isEmpty());

    assertTrue(record.getStringList(STRING_LIST_KEY).isEmpty());
    assertTrue(record.getList(STRING_LIST_KEY).isEmpty());
  }

  @Test(expected = NullPointerException.class)
  public void testNullJson() throws Exception {
    jackStore.scope("scope").indexRecord().putJson("key", null).execute();
  }

  @Test
  public void testSelectedKeys() throws Exception {
    jackStore.rootScope().indexRecord()
        .putBoolean(BOOLEAN_KEY, BOOLEAN_VALUE)
        .putJson(JSON_KEY, JSON_VALUE)
        .putLongList(LONG_LIST_KEY, LONG_LIST_VALUE)
        .putDateTimeList(DATETIME_LIST_KEY, DATETIME_LIST_VALUE)
        .putStringList(STRING_LIST_KEY, STRING_LIST_VALUE)
        .execute();

    Set<String> keySet = Sets.newHashSet(BOOLEAN_KEY, LONG_LIST_KEY, JSON_KEY);

    JsRecord record = jackStore.rootScope().getRecord().select(keySet).get();
    assertEquals(BOOLEAN_VALUE, record.getBoolean(BOOLEAN_KEY));
    assertEquals(LONG_LIST_VALUE, record.getLongList(LONG_LIST_KEY));
    assertEquals(JSON_VALUE, record.getJson(JSON_KEY));
    assertEquals(keySet, record.keySet());

    record = jackStore.rootScope().getRecord().select("invalid_key").get();
    assertEquals(0, record.keySet().size());
  }

  @Test
  public void testGetRecords() throws Exception {
    String customScope = "custom_scope";
    String key1 = "key1";
    String key2 = "key2";
    String key3 = "key3";

    int size = 20;
    List<JsScope> scopes = Lists.newArrayListWithCapacity(size);
    for (long i = 0L; i < size; ++i) {
      JsScope scope = jackStore.scope(customScope).createScope(String.valueOf(i)).execute();
      scopes.add(scope);
      jackStore.scope(scope).indexRecord()
          .putLong(LONG_KEY, i)
          .putString(STRING_KEY, String.valueOf(i))
          .putJson(JSON_KEY, JSON_PARSER.parse(String.format("{%s: {%s: %d}, %s: [1, 2, 3]}", key1, key2, i, key3)).getAsJsonObject())
          .execute();
    }

    Random random = new Random(System.currentTimeMillis());
    int lo = Math.max(1, random.nextInt(size / 2));
    int hi = Math.min(size, lo + size / 5 + random.nextInt(size));
    LOG.info("Range: [{}, {})", lo, hi);
    List<JsScope> subScopes = scopes.subList(lo, hi);
    JsRecords jsRecords = jackStore.scope(customScope).getRecord().gets(subScopes);
    assertEquals(subScopes.size(), jsRecords.size());
    for (int i = 0; i < subScopes.size(); ++i) {
      String scopeName = subScopes.get(i).getScopeName();
      int scopeNameInt = Integer.valueOf(scopeName);
      JsRecord jsRecord = jsRecords.get(i);

      assertEquals(scopeNameInt, jsRecord.getLong(LONG_KEY).intValue());
      assertEquals(scopeName, jsRecord.getString(STRING_KEY));

      JsonObject json = jsRecord.getJson(JSON_KEY);
      int jsonKey3Value = json.get(key1).getAsJsonObject().get(key2).getAsJsonPrimitive().getAsNumber().intValue();
      assertEquals(scopeNameInt, jsonKey3Value);
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGetInvalidKey() throws Exception {
    JsRecord record = jackStore.rootScope().getRecord().select("invalid_key").get();
    record.get("invalid_key");
  }

}
