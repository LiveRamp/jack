package com.rapleaf.jack.store.executors;

import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import com.rapleaf.jack.IDb;
import com.rapleaf.jack.test_project.database_1.IDatabase1;
import com.rapleaf.jack.test_project.database_1.models.TestStore;

import static org.junit.Assert.assertEquals;

public class TestRecordIndexExecutor extends BaseExecutorTestCase {

  @FunctionalInterface
  private interface RecordIndexExecution<DB extends IDb, VALUE> {
    RecordIndexExecutor<DB> apply(RecordIndexExecutor<DB> executor, String key, VALUE value);
  }

  @FunctionalInterface
  private interface RecordIndexListExecution<DB extends IDb, VALUE> {
    RecordIndexExecutor<DB> apply(RecordIndexExecutor<DB> executor, String key, List<VALUE> value);
  }

  @Test
  public void testInsertion() throws Exception {
    testInsertion(false);
    testInsertion(true);
  }

  private void testInsertion(boolean isNull) throws Exception {
    testInsertion(BOOLEAN_KEY, BOOLEAN_VALUE, isNull, RecordIndexExecutor::putBoolean);
    testInsertion(INT_KEY, INT_VALUE, isNull, RecordIndexExecutor::putInt);
    testInsertion(LONG_KEY, LONG_VALUE, isNull, RecordIndexExecutor::putLong);
    testInsertion(DOUBLE_KEY, DOUBLE_VALUE, isNull, RecordIndexExecutor::putDouble);
    testInsertion(DATETIME_KEY, DATETIME_VALUE, isNull, RecordIndexExecutor::putDateTime);
    testInsertion(STRING_KEY, STRING_VALUE, isNull, RecordIndexExecutor::putString);

    testListInsertion(BOOLEAN_LIST_KEY, BOOLEAN_LIST_VALUE, isNull, RecordIndexExecutor::putBooleanList);
    testListInsertion(INT_LIST_KEY, INT_LIST_VALUE, isNull, RecordIndexExecutor::putIntList);
    testListInsertion(LONG_LIST_KEY, LONG_LIST_VALUE, isNull, RecordIndexExecutor::putLongList);
    testListInsertion(DOUBLE_LIST_KEY, DOUBLE_LIST_VALUE, isNull, RecordIndexExecutor::putDoubleList);
    testListInsertion(DATETIME_LIST_KEY, DATETIME_LIST_VALUE, isNull, RecordIndexExecutor::putDateTimeList);
    testListInsertion(STRING_LIST_KEY, STRING_LIST_VALUE, isNull, RecordIndexExecutor::putStringList);
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
    jackStore.within("scope").indexRecord().putJson(JSON_KEY, JSON_VALUE).execute();
    records = transactor.query(db -> db.createQuery().from(TestStore.TBL).where(TestStore.KEY.startsWith(JSON_KEY)).fetch());
    assertEquals(StringUtils.countMatches(JSON_STRING, ",") + 1, records.size());
  }

  @Test(expected = NullPointerException.class)
  public void testNullJsonInsertion() throws Exception {
    testInsertion(JSON_KEY, JSON_VALUE, true, RecordIndexExecutor::putJson);
  }

  @Test(expected = NullPointerException.class)
  public void testInsertNullWithPutObjectMethod() throws Exception {
    jackStore.withinRoot().indexRecord().put("key", (Object)null).execute();
  }

  @Test(expected = NullPointerException.class)
  public void testInsertNullWithPutObjectListMethod() throws Exception {
    jackStore.withinRoot().indexRecord().put("key", (List<Object>)null).execute();
  }

  private <T> void testInsertion(String key, T value, boolean isNull, RecordIndexExecution<IDatabase1, T> execution) {
    RecordIndexExecutor<IDatabase1> executor = jackStore.within("scope").indexRecord();
    execution.apply(executor, key, isNull ? null : value).execute();
    records = transactor.query(db -> db.createQuery().from(TestStore.TBL).where(TestStore.KEY.equalTo(key)).fetch());
    assertEquals(1, records.size());
    assertEquals(isNull ? null : String.valueOf(value), records.get(0).get(TestStore.VALUE));
  }

  private <T> void testListInsertion(String key, List<T> listValue, boolean isNull, RecordIndexListExecution<IDatabase1, T> execution) {
    RecordIndexExecutor<IDatabase1> executor = jackStore.within("scope").indexRecord();
    execution.apply(executor, key, isNull ? null : listValue).execute();
    records = transactor.query(db -> db.createQuery().from(TestStore.TBL).where(TestStore.KEY.equalTo(key)).fetch());
    assertEquals(isNull ? 1 : listValue.size(), records.size());
    assertEquals(isNull ? Sets.newHashSet((Object)null) : listValue.stream().map(String::valueOf).collect(Collectors.toSet()), Sets.newHashSet(records.gets(TestStore.VALUE)));
  }

  private void testGenericInsertion(String key, Object value) throws Exception {
    jackStore.within("scope").indexRecord().put(key, value).execute();
    records = transactor.query(db -> db.createQuery().from(TestStore.TBL).where(TestStore.KEY.equalTo(key)).fetch());
    assertEquals(1, records.size());
    assertEquals(String.valueOf(value), records.get(0).get(TestStore.VALUE));
  }

  private void testGenericListInsertion(String key, List valueList) throws Exception {
    jackStore.within("scope").indexRecord().putList(key, valueList).execute();
    records = transactor.query(db -> db.createQuery().from(TestStore.TBL).where(TestStore.KEY.equalTo(key)).fetch());
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

    // primitive value
    jackStore.withinRoot().indexRecord().put(key, oldValue).execute();
    jackStore.withinRoot().indexRecord().put(key, newValue).execute();
    records = transactor.query(db -> db.createQuery().from(TestStore.TBL).where(TestStore.KEY.equalTo(key)).fetch());
    assertEquals(1, records.size());
    assertEquals(String.valueOf(newValue), records.get(0).get(TestStore.VALUE));

    // list value
    jackStore.withinRoot().indexRecord().putIntList(key, oldList).execute();
    jackStore.withinRoot().indexRecord().putIntList(key, newList).execute();
    records = transactor.query(db -> db.createQuery().from(TestStore.TBL).where(TestStore.KEY.equalTo(key)).fetch());
    assertEquals(newList.size(), records.size());
    assertEquals(newList.stream().map(Long::toString).collect(Collectors.toSet()), Sets.newHashSet(records.gets(TestStore.VALUE)));

    // primitive to list
    jackStore.withinRoot().indexRecord().putIntList(key, newList).execute();
    records = transactor.query(db -> db.createQuery().from(TestStore.TBL).where(TestStore.KEY.equalTo(key)).fetch());
    assertEquals(newList.size(), records.size());
    assertEquals(newList.stream().map(Long::toString).collect(Collectors.toSet()), Sets.newHashSet(records.gets(TestStore.VALUE)));

    // list to primitive
    jackStore.withinRoot().indexRecord().put(key, newValue).execute();
    records = transactor.query(db -> db.createQuery().from(TestStore.TBL).where(TestStore.KEY.equalTo(key)).fetch());
    assertEquals(1, records.size());
    assertEquals(String.valueOf(newValue), records.get(0).get(TestStore.VALUE));
  }

}
