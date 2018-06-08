package com.rapleaf.jack.store.executors;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.Test;

import com.rapleaf.jack.store.JsRecord;
import com.rapleaf.jack.store.ValueType;
import com.rapleaf.jack.store.json.JsonDbConstants;
import com.rapleaf.jack.store.json.JsonDbHelper;
import com.rapleaf.jack.store.json.JsonDbTuple;
import com.rapleaf.jack.test_project.database_1.models.TestStore;
import com.rapleaf.jack.util.JackUtility;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class TestRecordReader extends BaseExecutorTestCase {

  @Test
  public void testScopeReader() throws Exception {
    testScopeReader(true);
    testScopeReader(false);
  }

  private void testScopeReader(boolean nullValue) throws Exception {
    createAndReadValue(LONG_KEY, ValueType.LONG, LONG_VALUE, nullValue);
    createAndReadValue(INT_KEY, ValueType.INT, INT_VALUE, nullValue);
    createAndReadValue(DOUBLE_KEY, ValueType.DOUBLE, DOUBLE_VALUE, nullValue);
    createAndReadValue(STRING_KEY, ValueType.STRING, STRING_VALUE, nullValue);
    createAndReadValue(BOOLEAN_KEY, ValueType.BOOLEAN, BOOLEAN_VALUE, nullValue);
    createAndReadValue(DATETIME_KEY, ValueType.DATETIME, DATETIME_VALUE, nullValue);

    createAndReadList(LONG_LIST_KEY, ValueType.LONG_LIST, LONG_LIST_VALUE, nullValue);
    createAndReadList(INT_LIST_KEY, ValueType.INT_LIST, INT_LIST_VALUE, nullValue);
    createAndReadList(DOUBLE_LIST_KEY, ValueType.DOUBLE_LIST, DOUBLE_LIST_VALUE, nullValue);
    createAndReadList(STRING_LIST_KEY, ValueType.STRING_LIST, STRING_LIST_VALUE, nullValue);
    createAndReadList(BOOLEAN_LIST_KEY, ValueType.BOOLEAN_LIST, BOOLEAN_LIST_VALUE, nullValue);
    createAndReadList(DATETIME_LIST_KEY, ValueType.DATETIME_LIST, DATETIME_LIST_VALUE, nullValue);
  }

  private void createAndReadValue(String key, ValueType type, Object value, boolean nullValue) throws Exception {
    String recordName = "new record";
    long recordId = createSubScope(Optional.empty(), Optional.of(recordName));

    JsRecord record = transactor.queryAsTransaction(db -> {
      // create new record
      db.createInsertion().into(TestStore.TBL)
          .set(TestStore.ENTRY_SCOPE, recordId)
          .set(TestStore.ENTRY_TYPE, type.value)
          .set(TestStore.ENTRY_KEY, key)
          .set(TestStore.ENTRY_VALUE, nullValue ? null : value.toString())
          .execute();
      // read back new record
      return jackStore.record(recordId).read().execute(db);
    });

    assertEquals(recordId, record.getRecordId().longValue());
    if (nullValue) {
      assertNull(record.get(key));
    } else if (value instanceof LocalDateTime) {
      assertEquals(JackUtility.DATETIME_TO_MILLIS.apply((LocalDateTime)value), JackUtility.DATETIME_TO_MILLIS.apply((LocalDateTime)record.get(key)));
    } else {
      assertEquals(value, record.get(key));
    }
  }

  @SuppressWarnings("unchecked")
  private void createAndReadList(String key, ValueType type, List values, boolean nullValue) throws Exception {
    String recordName = "new record";
    long recordId = createSubScope(Optional.empty(), Optional.of(recordName));

    JsRecord record = transactor.queryAsTransaction(db -> {
      // create new list record
      db.createInsertion().into(TestStore.TBL)
          .set(TestStore.ENTRY_SCOPE, Collections.nCopies(values.size(), recordId))
          .set(TestStore.ENTRY_TYPE, Collections.nCopies(values.size(), type.value))
          .set(TestStore.ENTRY_KEY, Collections.nCopies(values.size(), key))
          .set(TestStore.ENTRY_VALUE, nullValue ? Collections.nCopies(values.size(), null) : ((List<Object>)values).stream().map(Object::toString).collect(Collectors.toList()))
          .execute();
      // read back new record
      return jackStore.record(recordId).read().execute(db);
    });

    assertEquals(recordId, record.getRecordId().longValue());
    if (nullValue) {
      assertTrue(record.getList(key).isEmpty());
    } else if (values.get(0) instanceof LocalDateTime) {
      assertEquals(
          ((List<Object>)values).stream().map(v -> JackUtility.DATETIME_TO_MILLIS.apply((LocalDateTime)v)).collect(Collectors.toList()),
          ((List<Object>)record.getList(key)).stream().map(v -> JackUtility.DATETIME_TO_MILLIS.apply(((LocalDateTime)v))).collect(Collectors.toList())
      );
    } else {
      assertEquals(values, record.getList(key));
    }
  }

  @Test
  public void testJsonReader() throws Exception {
    List<JsonDbTuple> tuples = JsonDbHelper.toTupleList(JSON_VALUE);

    JsRecord record = transactor.queryAsTransaction(db -> {
      // create new list record
      db.createInsertion().into(TestStore.TBL)
          .set(TestStore.ENTRY_SCOPE, Collections.nCopies(tuples.size(), null))
          .set(TestStore.ENTRY_TYPE, tuples.stream().map(t -> t.getType().value).collect(Collectors.toList()))
          .set(TestStore.ENTRY_KEY, tuples.stream().map(t -> JSON_KEY + JsonDbConstants.PATH_SEPARATOR + t.getFullPaths()).collect(Collectors.toList()))
          .set(TestStore.ENTRY_VALUE, tuples.stream().map(JsonDbTuple::getValue).collect(Collectors.toList()))
          .execute();
      // read back new record
      return jackStore.rootRecord().read().execute(db);
    });

    assertEquals(JSON_VALUE, record.getJson(JSON_KEY));
    assertEquals(JSON_VALUE, record.get(JSON_KEY));
  }

  @Test
  public void testKeySelection() throws Exception {
    List<String> selectedKeys = Lists.newArrayList(STRING_KEY, LONG_KEY);

    JsRecord record = transactor.queryAsTransaction(db -> {
      // create new list record
      db.createInsertion().into(TestStore.TBL)
          .set(TestStore.ENTRY_SCOPE, null, null, null)
          .set(TestStore.ENTRY_TYPE, ValueType.STRING.value, ValueType.LONG.value, ValueType.BOOLEAN.value)
          .set(TestStore.ENTRY_KEY, STRING_KEY, LONG_KEY, BOOLEAN_KEY)
          .set(TestStore.ENTRY_VALUE, STRING_VALUE, String.valueOf(LONG_VALUE), String.valueOf(BOOLEAN_VALUE))
          .execute();
      // read back new record
      return jackStore.rootRecord().read()
          .selectKey(selectedKeys.get(0))
          .selectKey(Collections.singleton(selectedKeys.get(1)))
          .execute(db);
    });

    assertEquals(Sets.newHashSet(selectedKeys), record.keySet());
  }

}
