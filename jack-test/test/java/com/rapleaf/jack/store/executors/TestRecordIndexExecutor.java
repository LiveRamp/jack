package com.rapleaf.jack.store.executors;

import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.joda.time.DateTime;
import org.junit.Test;

import com.rapleaf.jack.queries.Records;
import com.rapleaf.jack.test_project.database_1.models.TestStore;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class TestRecordIndexExecutor extends BaseExecutorTestCase {

  @Test
  public void testInsertion() throws Exception {
    testInsertion(false);
    testInsertion(true);
  }

  private void testInsertion(boolean isNull) throws Exception {
    // boolean
    jackStore.within("scope").indexRecord().putBoolean(BOOLEAN_KEY, isNull ? null : BOOLEAN_VALUE).execute();
    records = transactor.query(db -> db.createQuery().from(TestStore.TBL).where(TestStore.KEY.equalTo(BOOLEAN_KEY)).fetch());
    assertEquals(1, records.size());
    assertEquals(isNull ? null : String.valueOf(BOOLEAN_VALUE), records.get(0).get(TestStore.VALUE));

    // int
    jackStore.within("scope").indexRecord().putInt(INT_KEY, isNull ? null : INT_VALUE).execute();
    records = transactor.query(db -> db.createQuery().from(TestStore.TBL).where(TestStore.KEY.equalTo(INT_KEY)).fetch());
    assertEquals(1, records.size());
    assertEquals(isNull ? null : String.valueOf(INT_VALUE), records.get(0).get(TestStore.VALUE));

    // long
    jackStore.within("scope").indexRecord().putLong(LONG_KEY, isNull ? null : LONG_VALUE).execute();
    records = transactor.query(db -> db.createQuery().from(TestStore.TBL).where(TestStore.KEY.equalTo(LONG_KEY)).fetch());
    assertEquals(1, records.size());
    assertEquals(isNull ? null : String.valueOf(LONG_VALUE), records.get(0).get(TestStore.VALUE));

    // double
    jackStore.within("scope").indexRecord().putDouble(DOUBLE_KEY, isNull ? null : DOUBLE_VALUE).execute();
    records = transactor.query(db -> db.createQuery().from(TestStore.TBL).where(TestStore.KEY.equalTo(DOUBLE_KEY)).fetch());
    assertEquals(1, records.size());
    assertEquals(isNull ? null : String.valueOf(DOUBLE_VALUE), records.get(0).get(TestStore.VALUE));

    // datetime
    jackStore.within("scope").indexRecord().putDateTime(DATETIME_KEY, isNull ? null : DATETIME_VALUE).execute();
    records = transactor.query(db -> db.createQuery().from(TestStore.TBL).where(TestStore.KEY.equalTo(DATETIME_KEY)).fetch());
    assertEquals(1, records.size());
    assertEquals(isNull ? null : String.valueOf(DATETIME_VALUE), records.get(0).get(TestStore.VALUE));

    // string
    jackStore.within("scope").indexRecord().putString(STRING_KEY, isNull ? null : STRING_VALUE).execute();
    records = transactor.query(db -> db.createQuery().from(TestStore.TBL).where(TestStore.KEY.equalTo(STRING_KEY)).fetch());
    assertEquals(1, records.size());
    assertEquals(isNull ? null : String.valueOf(STRING_VALUE), records.get(0).get(TestStore.VALUE));

    // boolean list
    jackStore.within("scope").indexRecord().putBooleanList(BOOLEAN_LIST_KEY, isNull ? null : BOOLEAN_LIST_VALUE).execute();
    records = transactor.query(db -> db.createQuery().from(TestStore.TBL).where(TestStore.KEY.equalTo(BOOLEAN_LIST_KEY)).fetch());
    assertEquals(isNull ? 1 : BOOLEAN_LIST_VALUE.size(), records.size());
    assertEquals(isNull ? Sets.newHashSet((Object)null) : BOOLEAN_LIST_VALUE.stream().map(String::valueOf).collect(Collectors.toSet()), Sets.newHashSet(records.gets(TestStore.VALUE)));

    // int list
    jackStore.within("scope").indexRecord().putIntList(INT_LIST_KEY, isNull ? null : INT_LIST_VALUE).execute();
    records = transactor.query(db -> db.createQuery().from(TestStore.TBL).where(TestStore.KEY.equalTo(INT_LIST_KEY)).fetch());
    assertEquals(isNull ? 1 : INT_LIST_VALUE.size(), records.size());
    assertEquals(isNull ? Sets.newHashSet((Object)null) : INT_LIST_VALUE.stream().map(String::valueOf).collect(Collectors.toSet()), Sets.newHashSet(records.gets(TestStore.VALUE)));

    // long list
    jackStore.within("scope").indexRecord().putLongList(LONG_LIST_KEY, isNull ? null : LONG_LIST_VALUE).execute();
    records = transactor.query(db -> db.createQuery().from(TestStore.TBL).where(TestStore.KEY.equalTo(LONG_LIST_KEY)).fetch());
    assertEquals(isNull ? 1 : LONG_LIST_VALUE.size(), records.size());
    assertEquals(isNull ? Sets.newHashSet((Object)null) : LONG_LIST_VALUE.stream().map(String::valueOf).collect(Collectors.toSet()), Sets.newHashSet(records.gets(TestStore.VALUE)));

    // double list
    jackStore.within("scope").indexRecord().putDoubleList(DOUBLE_LIST_KEY, isNull ? null : DOUBLE_LIST_VALUE).execute();
    records = transactor.query(db -> db.createQuery().from(TestStore.TBL).where(TestStore.KEY.equalTo(DOUBLE_LIST_KEY)).fetch());
    assertEquals(isNull ? 1 : DOUBLE_LIST_VALUE.size(), records.size());
    assertEquals(isNull ? Sets.newHashSet((Object)null) : DOUBLE_LIST_VALUE.stream().map(String::valueOf).collect(Collectors.toSet()), Sets.newHashSet(records.gets(TestStore.VALUE)));

    // datetime list
    jackStore.within("scope").indexRecord().putDateTimeList(DATETIME_LIST_KEY, isNull ? null : DATETIME_LIST_VALUE).execute();
    records = transactor.query(db -> db.createQuery().from(TestStore.TBL).where(TestStore.KEY.equalTo(DATETIME_LIST_KEY)).fetch());
    assertEquals(isNull ? 1 : DATETIME_LIST_VALUE.size(), records.size());
    assertEquals(isNull ? Sets.newHashSet((Object)null) : DATETIME_LIST_VALUE.stream().map(String::valueOf).collect(Collectors.toSet()), Sets.newHashSet(records.gets(TestStore.VALUE)));

    // string list
    jackStore.within("scope").indexRecord().putStringList(STRING_LIST_KEY, isNull ? null : STRING_LIST_VALUE).execute();
    records = transactor.query(db -> db.createQuery().from(TestStore.TBL).where(TestStore.KEY.equalTo(STRING_LIST_KEY)).fetch());
    assertEquals(isNull ? 1 : STRING_LIST_VALUE.size(), records.size());
    assertEquals(isNull ? Sets.newHashSet((Object)null) : Sets.newHashSet(STRING_LIST_VALUE), Sets.newHashSet(records.gets(TestStore.VALUE)));
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

  @Test(expected = NullPointerException.class)
  public void testInsertNullWithPutObjectMethod() throws Exception {
    jackStore.withinRoot().indexRecord().put("key", (Object)null).execute();
  }

  @Test(expected = NullPointerException.class)
  public void testInsertNullWithPutObjectListMethod() throws Exception {
    jackStore.withinRoot().indexRecord().put("key", (List<Object>)null).execute();
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
