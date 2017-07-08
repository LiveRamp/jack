package com.rapleaf.jack.store.executors;

import java.util.List;

import com.google.common.collect.Lists;
import org.junit.Test;

import com.rapleaf.jack.queries.Record;
import com.rapleaf.jack.queries.Records;
import com.rapleaf.jack.store.JsRecord;
import com.rapleaf.jack.store.ValueType;
import com.rapleaf.jack.test_project.database_1.models.TestStore;

import static org.junit.Assert.assertEquals;

public class TestSubRecordCreator extends BaseExecutorTestCase {

  @Test
  public void testSubScopeCreation() throws Exception {
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
            .where(TestStore.TYPE.equalTo(ValueType.SCOPE.value))
            .orderBy(TestStore.VALUE)
            .fetch()
    );
    assertEquals(size, scopeRecords.size());

    for (int i = 0; i < size; ++i) {
      Record record = scopeRecords.get(i);
      JsRecord scope = jsRecords[i];
      assertEquals(scope.getRecordId(), record.get(TestStore.ID));
      // TODO: get name from record
      assertEquals(names.get(i), record.get(TestStore.VALUE));
    }

    Records valueRecords = transactor.query(db ->
        db.createQuery().from(TestStore.TBL)
            .where(TestStore.TYPE.notEqualTo(ValueType.SCOPE.value))
            .orderBy(TestStore.SCOPE)
            .fetch()
    );
    assertEquals(size, scopeRecords.size());

    for (int i = 0; i < size; ++i) {
      Record record = valueRecords.get(i);
      assertEquals(values.get(i), Long.valueOf(record.get(TestStore.VALUE)));
    }
  }

}
