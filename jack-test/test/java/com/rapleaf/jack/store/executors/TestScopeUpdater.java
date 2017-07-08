package com.rapleaf.jack.store.executors;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonObject;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import com.rapleaf.jack.exception.SqlExecutionFailureException;
import com.rapleaf.jack.queries.Record;
import com.rapleaf.jack.store.JsRecord;
import com.rapleaf.jack.store.exceptions.InvalidScopeException;
import com.rapleaf.jack.store.json.JsonDbConstants;
import com.rapleaf.jack.store.json.JsonDbHelper;
import com.rapleaf.jack.store.json.JsonDbTuple;
import com.rapleaf.jack.store.util.InsertList;
import com.rapleaf.jack.store.util.InsertValue;
import com.rapleaf.jack.test_project.database_1.models.TestStore;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TestScopeUpdater extends BaseExecutorTestCase {

  @Test
  public void testInsertion() throws Exception {
    testInsertion(false);
    testInsertion(true);
  }

  private void testInsertion(boolean isNull) throws Exception {
    testInsertion(BOOLEAN_KEY, BOOLEAN_VALUE, isNull, ScopeUpdater::putBoolean);
    testInsertion(INT_KEY, INT_VALUE, isNull, ScopeUpdater::putInt);
    testInsertion(LONG_KEY, LONG_VALUE, isNull, ScopeUpdater::putLong);
    testInsertion(DOUBLE_KEY, DOUBLE_VALUE, isNull, ScopeUpdater::putDouble);
    testInsertion(DATETIME_KEY, DATETIME_VALUE, isNull, ScopeUpdater::putDateTime);
    testInsertion(STRING_KEY, STRING_VALUE, isNull, ScopeUpdater::putString);

    testListInsertion(BOOLEAN_LIST_KEY, BOOLEAN_LIST_VALUE, isNull, ScopeUpdater::putBooleanList);
    testListInsertion(INT_LIST_KEY, INT_LIST_VALUE, isNull, ScopeUpdater::putIntList);
    testListInsertion(LONG_LIST_KEY, LONG_LIST_VALUE, isNull, ScopeUpdater::putLongList);
    testListInsertion(DOUBLE_LIST_KEY, DOUBLE_LIST_VALUE, isNull, ScopeUpdater::putDoubleList);
    testListInsertion(DATETIME_LIST_KEY, DATETIME_LIST_VALUE, isNull, ScopeUpdater::putDateTimeList);
    testListInsertion(STRING_LIST_KEY, STRING_LIST_VALUE, isNull, ScopeUpdater::putStringList);
  }

  @Test
  public void testGenericInsertion() throws Exception {
    // primitive value
    testGenericInsertion(BOOLEAN_KEY, BOOLEAN_VALUE);
    testGenericInsertion(INT_KEY, INT_VALUE);
    testGenericInsertion(LONG_KEY, LONG_VALUE);
    testGenericInsertion(DOUBLE_KEY, DOUBLE_VALUE);
    testGenericInsertion(DATETIME_KEY, DATETIME_VALUE);
    testGenericInsertion(STRING_KEY, STRING_VALUE);

    // list value
    testGenericListInsertion(BOOLEAN_LIST_KEY, BOOLEAN_LIST_VALUE);
    testGenericListInsertion(INT_LIST_KEY, INT_LIST_VALUE);
    testGenericListInsertion(LONG_LIST_KEY, LONG_LIST_VALUE);
    testGenericListInsertion(DOUBLE_LIST_KEY, DOUBLE_LIST_VALUE);
    testGenericListInsertion(DATETIME_LIST_KEY, DATETIME_LIST_VALUE);
    testGenericListInsertion(STRING_LIST_KEY, STRING_LIST_VALUE);
  }

  @Test
  public void testJsonInsertion() throws Exception {
    List<JsonDbTuple> tuples = JsonDbHelper.toTupleList(JSON_VALUE);
    Map<String, String> tupleMap = Maps.newHashMap();
    for (JsonDbTuple tuple : tuples) {
      // key is prepended to json tuple path
      tupleMap.put(String.format("%s%s%s", JSON_KEY, JsonDbConstants.PATH_SEPARATOR, tuple.getFullPaths()), tuple.getValue());
    }

    records = transactor.queryAsTransaction(db -> {
      jackStore2.rootScope().update().putJson(JSON_KEY, JSON_VALUE).execute(db);
      return db.createQuery().from(TestStore.TBL).where(TestStore.KEY.startsWith(JSON_KEY)).fetch();
    });
    assertEquals(StringUtils.countMatches(JSON_STRING, ",") + 1, records.size());
    for (Record record : records) {
      String key = record.get(TestStore.KEY);
      String value = record.get(TestStore.VALUE);
      assertTrue(tupleMap.containsKey(key));
      if (value == null) {
        assertNull(tupleMap.get(key));
      } else {
        assertEquals(tupleMap.get(key), value);
      }
    }
  }

  @Test
  public void testNullJsonInsertion() throws Exception {
    try {
      testInsertion(JSON_KEY, JSON_VALUE, true, ScopeUpdater::putJson);
      fail();
    } catch (SqlExecutionFailureException e) {
      assertTrue(e.getCause() instanceof NullPointerException);
    }
  }

  @Test
  public void testInsertNullWithPutObjectMethod() throws Exception {
    try {
      transactor.execute(db -> jackStore2.rootScope().update().put("key", (Object)null).execute(db));
      fail();
    } catch (SqlExecutionFailureException e) {
      assertTrue(e.getCause() instanceof NullPointerException);
    }
  }

  @Test
  public void testInsertNullWithPutObjectListMethod() throws Exception {
    try {
      transactor.execute(db -> jackStore2.rootScope().update().put("key", (List<Object>)null).execute(db));
      fail();
    } catch (SqlExecutionFailureException e) {
      assertTrue(e.getCause() instanceof NullPointerException);
    }
  }

  private <T> void testInsertion(String key, T value, boolean isNull, InsertValue<T> execution) {
    ScopeUpdater executor = jackStore2.rootScope().update();
    records = transactor.queryAsTransaction(db -> {
      execution.apply(executor, key, isNull ? null : value);
      executor.execute(db);
      return db.createQuery().from(TestStore.TBL).where(TestStore.KEY.equalTo(key)).fetch();
    });
    assertEquals(1, records.size());
    assertEquals(isNull ? null : String.valueOf(value), records.get(0).get(TestStore.VALUE));
  }

  private <T> void testListInsertion(String key, List<T> listValue, boolean isNull, InsertList<T> execution) {
    ScopeUpdater executor = jackStore2.rootScope().update();
    records = transactor.queryAsTransaction(db -> {
      execution.apply(executor, key, isNull ? null : listValue);
      executor.execute(db);
      return db.createQuery().from(TestStore.TBL).where(TestStore.KEY.equalTo(key)).fetch();
    });
    assertEquals(isNull ? 1 : listValue.size(), records.size());
    assertEquals(isNull ? Sets.newHashSet((Object)null) : listValue.stream().map(String::valueOf).collect(Collectors.toSet()), Sets.newHashSet(records.gets(TestStore.VALUE)));
  }

  private void testGenericInsertion(String key, Object value) throws Exception {
    records = transactor.queryAsTransaction(db -> {
      jackStore2.rootScope().update().put(key, value).execute(db);
      return db.createQuery().from(TestStore.TBL).where(TestStore.KEY.equalTo(key)).fetch();
    });
    assertEquals(1, records.size());
    assertEquals(String.valueOf(value), records.get(0).get(TestStore.VALUE));
  }

  @SuppressWarnings("unchecked")
  private void testGenericListInsertion(String key, List valueList) throws Exception {
    records = transactor.queryAsTransaction(db -> {
      jackStore2.rootScope().update().putList(key, valueList).execute(db);
      return db.createQuery().from(TestStore.TBL).where(TestStore.KEY.equalTo(key)).fetch();
    });
    assertEquals(valueList.size(), records.size());
    assertEquals(valueList.stream().map(String::valueOf).collect(Collectors.toSet()), Sets.newHashSet(records.gets(TestStore.VALUE)));
  }

  @Test
  public void testUpdate() throws Exception {
    String key = "key";
    int oldValue = 10;
    int newValue = 100;
    List<Integer> oldList = Lists.newArrayList(10, 20, 30);
    List<Integer> newList = Lists.newArrayList(10, 40, 50, 60);
    JsonObject oldJson = JSON_PARSER.parse(JSON_STRING).getAsJsonObject();
    JsonObject newJson = JSON_PARSER.parse(JSON_STRING).getAsJsonObject();
    newJson.addProperty("key1", "new_value");

    // primitive value
    records = transactor.queryAsTransaction(db -> {
      jackStore2.rootScope().update().put(key, oldValue).execute(db);
      jackStore2.rootScope().update().put(key, newValue).execute(db);
      return db.createQuery().from(TestStore.TBL).where(TestStore.KEY.equalTo(key)).fetch();
    });
    assertEquals(1, records.size());
    assertEquals(String.valueOf(newValue), records.get(0).get(TestStore.VALUE));

    // list value
    records = transactor.queryAsTransaction(db -> {
      jackStore2.rootScope().update().putIntList(key, oldList).execute(db);
      jackStore2.rootScope().update().putIntList(key, newList).execute(db);
      return db.createQuery().from(TestStore.TBL).where(TestStore.KEY.equalTo(key)).fetch();
    });
    assertEquals(newList.size(), records.size());
    assertEquals(newList.stream().map(Long::toString).collect(Collectors.toSet()), Sets.newHashSet(records.gets(TestStore.VALUE)));

    // json value
    JsRecord jsRecord = transactor.queryAsTransaction(db -> {
      jackStore2.rootScope().update().putJson(key, oldJson).execute(db);
      jackStore2.rootScope().update().putJson(key, newJson).execute(db);
      return jackStore2.rootScope().read().execute(db);
    });
    assertEquals(Sets.newHashSet(key), jsRecord.keySet());
    assertEquals(newJson, jsRecord.getJson(key));

    // primitive to list
    records = transactor.queryAsTransaction(db -> {
      jackStore2.rootScope().update().putIntList(key, newList).execute(db);
      return db.createQuery().from(TestStore.TBL).where(TestStore.KEY.equalTo(key)).fetch();
    });
    assertEquals(newList.size(), records.size());
    assertEquals(newList.stream().map(Long::toString).collect(Collectors.toSet()), Sets.newHashSet(records.gets(TestStore.VALUE)));

    // list to primitive
    records = transactor.queryAsTransaction(db -> {
      jackStore2.rootScope().update().put(key, newValue).execute(db);
      return db.createQuery().from(TestStore.TBL).where(TestStore.KEY.equalTo(key)).fetch();
    });
    assertEquals(1, records.size());
    assertEquals(String.valueOf(newValue), records.get(0).get(TestStore.VALUE));

    // primitive to json
    jsRecord = transactor.queryAsTransaction(db -> {
      jackStore2.rootScope().update().put(key, oldJson).execute(db);
      return jackStore2.rootScope().read().execute(db);
    });
    assertEquals(oldJson, jsRecord.getJson(key));

    // json to list
    records = transactor.queryAsTransaction(db -> {
      jackStore2.rootScope().update().put(key, oldList).execute(db);
      return db.createQuery().from(TestStore.TBL).where(TestStore.KEY.equalTo(key)).fetch();
    });
    assertEquals(oldList.size(), records.size());
    assertEquals(oldList.stream().map(Long::toString).collect(Collectors.toSet()), Sets.newHashSet(records.gets(TestStore.VALUE)));

    // list to json
    jsRecord = transactor.queryAsTransaction(db -> {
      jackStore2.rootScope().update().put(key, newJson).execute(db);
      return jackStore2.rootScope().read().execute(db);
    });
    assertEquals(newJson, jsRecord.getJson(key));
  }

  @Test
  public void testInsertIntoNonExistingScope() throws Exception {
    try {
      transactor.queryAsTransaction(db -> {
        jackStore2.scope(100L).update()
            .put(LONG_KEY, LONG_VALUE)
            .execute(db);
        return jackStore2.scope(100L).read().execute(db);
      });
      fail();
    } catch (SqlExecutionFailureException e) {
      assertTrue(e.getCause() instanceof InvalidScopeException);
    }
  }

}
