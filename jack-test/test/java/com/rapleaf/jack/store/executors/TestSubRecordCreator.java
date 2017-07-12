package com.rapleaf.jack.store.executors;

import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import org.joda.time.DateTime;
import org.junit.Test;

import com.rapleaf.jack.queries.Record;
import com.rapleaf.jack.queries.Records;
import com.rapleaf.jack.store.JsRecord;
import com.rapleaf.jack.store.ValueType;
import com.rapleaf.jack.test_project.database_1.models.TestStore;

import static org.junit.Assert.assertEquals;

public class TestSubRecordCreator extends BaseExecutorTestCase {

  @Test
  public void testSubScopeCreation2() throws Exception {
    int size = Math.max(3, RANDOM.nextInt(10));
    List<String> names = Lists.newArrayListWithCapacity(size);
    List<Long> values = Lists.newArrayListWithCapacity(size);

    final JsRecord[] jsRecords = new JsRecord[size];
    transactor.executeAsTransaction(db -> {
      for (int i = 0; i < size; ++i) {
        String name = "record" + i;
        long value = RANDOM.nextLong();
        names.add(name);
        values.add(value);

        jsRecords[i] = jackStore2.rootRecord()
            .createSubRecord()
            .recordName(name)
            .put(LONG_KEY, value)
            .execute(db);
      }
    });

    Records scopeRecords = transactor.query(db ->
        db.createQuery().from(TestStore.TBL)
            .where(TestStore.ENTRY_TYPE.equalTo(ValueType.SCOPE.value))
            .orderBy(TestStore.ENTRY_VALUE)
            .fetch()
    );
    assertEquals(size, scopeRecords.size());

    for (int i = 0; i < size; ++i) {
      Record record = scopeRecords.get(i);
      JsRecord scope = jsRecords[i];
      assertEquals(scope.getRecordId(), record.get(TestStore.ID));
      // TODO: get name from record
      assertEquals(names.get(i), record.get(TestStore.ENTRY_VALUE));
    }

    Records valueRecords = transactor.query(db ->
        db.createQuery().from(TestStore.TBL)
            .where(TestStore.ENTRY_TYPE.notEqualTo(ValueType.SCOPE.value))
            .orderBy(TestStore.ENTRY_SCOPE)
            .fetch()
    );
    assertEquals(size, scopeRecords.size());

    for (int i = 0; i < size; ++i) {
      Record record = valueRecords.get(i);
      assertEquals(values.get(i), Long.valueOf(record.get(TestStore.ENTRY_VALUE)));
    }
  }

  @Test
  public void testCreateSubRecordAndRead() throws Exception {
    JsRecord jsRecord = transactor.queryAsTransaction(db ->
        jackStore2.rootRecord().createSubRecord()
            .put(BOOLEAN_KEY, BOOLEAN_VALUE)
            .put(INT_KEY, INT_VALUE)
            .put(LONG_KEY, LONG_VALUE)
            .put(DOUBLE_KEY, DOUBLE_VALUE)
            .put(DATETIME_KEY, DATETIME_VALUE)
            .put(STRING_KEY, STRING_VALUE)
            .put(JSON_KEY, JSON_VALUE)
            .putList(BOOLEAN_LIST_KEY, BOOLEAN_LIST_VALUE)
            .putList(INT_LIST_KEY, INT_LIST_VALUE)
            .putList(LONG_LIST_KEY, LONG_LIST_VALUE)
            .putList(DOUBLE_LIST_KEY, DOUBLE_LIST_VALUE)
            .putList(DATETIME_LIST_KEY, DATETIME_LIST_VALUE)
            .putList(STRING_LIST_KEY, STRING_LIST_VALUE)
            .execute(db)
    );

    assertEquals(BOOLEAN_VALUE, jsRecord.get(BOOLEAN_KEY));
    assertEquals(INT_VALUE, jsRecord.get(INT_KEY));
    assertEquals(LONG_VALUE, jsRecord.get(LONG_KEY));
    assertEquals(DOUBLE_VALUE, jsRecord.get(DOUBLE_KEY));
    assertEquals(DATETIME_VALUE.getMillis(), ((DateTime)jsRecord.get(DATETIME_KEY)).getMillis());
    assertEquals(STRING_VALUE, jsRecord.get(STRING_KEY));

    assertEquals(JSON_VALUE, jsRecord.get(JSON_KEY));

    assertEquals(BOOLEAN_LIST_VALUE, jsRecord.getList(BOOLEAN_LIST_KEY));
    assertEquals(INT_LIST_VALUE, jsRecord.getList(INT_LIST_KEY));
    assertEquals(LONG_LIST_VALUE, jsRecord.getList(LONG_LIST_KEY));
    assertEquals(DOUBLE_LIST_VALUE, jsRecord.getList(DOUBLE_LIST_KEY));
    assertEquals(DATETIME_LIST_VALUE.stream().map(DateTime::getMillis).collect(Collectors.toList()), ((List<DateTime>)jsRecord.getList(DATETIME_LIST_KEY)).stream().map(DateTime::getMillis).collect(Collectors.toList()));
    assertEquals(STRING_LIST_VALUE, jsRecord.getList(STRING_LIST_KEY));
  }

}
