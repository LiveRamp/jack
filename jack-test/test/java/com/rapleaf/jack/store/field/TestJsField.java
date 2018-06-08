package com.rapleaf.jack.store.field;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;

import com.rapleaf.jack.store.JsRecord;
import com.rapleaf.jack.store.executors.BaseExecutorTestCase;
import com.rapleaf.jack.store.executors.RecordUpdater;
import com.rapleaf.jack.util.JackUtility;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestJsField extends BaseExecutorTestCase {

  @Before
  public void prepare() throws Exception {
    transactor.execute(db -> jackStore.rootRecord().deleteSubRecords().allowBulkDeletion().deleteEntireRecord(true).execute(db));
  }

  @Test
  public void testValueField() throws Exception {
    testValueField(JsFields.createIntField(INT_KEY), INT_KEY, INT_VALUE);
    testValueField(JsFields.createLongField(LONG_KEY), LONG_KEY, LONG_VALUE);
    testValueField(JsFields.createDoubleField(DOUBLE_KEY), DOUBLE_KEY, DOUBLE_VALUE);
    testValueField(JsFields.createBooleanField(BOOLEAN_KEY), BOOLEAN_KEY, BOOLEAN_VALUE);
    testValueField(JsFields.createDateTimeField(DATETIME_KEY), DATETIME_KEY, DATETIME_VALUE);
    testValueField(JsFields.createStringField(STRING_KEY), STRING_KEY, STRING_VALUE);
    testValueField(JsFields.createJsonField(JSON_KEY), JSON_KEY, JSON_VALUE);
  }

  @Test
  public void testListField() throws Exception {
    testListField(JsFields.createIntListField(INT_LIST_KEY), INT_LIST_KEY, INT_LIST_VALUE);
    testListField(JsFields.createLongListField(LONG_LIST_KEY), LONG_LIST_KEY, LONG_LIST_VALUE);
    testListField(JsFields.createDoubleListField(DOUBLE_LIST_KEY), DOUBLE_LIST_KEY, DOUBLE_LIST_VALUE);
    testListField(JsFields.createBooleanListField(BOOLEAN_LIST_KEY), BOOLEAN_LIST_KEY, BOOLEAN_LIST_VALUE);
    testListField(JsFields.createDateTimeListField(DATETIME_LIST_KEY), DATETIME_LIST_KEY, DATETIME_LIST_VALUE);
    testListField(JsFields.createStringListField(STRING_LIST_KEY), STRING_LIST_KEY, STRING_LIST_VALUE);
  }

  private <T> void testValueField(JsValueField<T> field, String key, T value) throws Exception {
    assertEquals(key, field.getKey());

    JsRecord record = transactor.queryAsTransaction(db -> {
      RecordUpdater updater = jackStore.rootRecord().update();
      field.getPutFunction().apply(updater, value);
      updater.exec(db);
      return jackStore.rootRecord().read().execute(db);
    });

    if (value instanceof LocalDateTime) {
      assertTrue(((LocalDateTime)value).isEqual((LocalDateTime)field.getReadFunction().apply(record)));
    } else {
      assertEquals(value, field.getReadFunction().apply(record));
    }
  }

  private <T> void testListField(JsListField<T> field, String key, List<T> values) throws Exception {
    assertEquals(key, field.getKey());

    JsRecord record = transactor.queryAsTransaction(db -> {
      RecordUpdater updater = jackStore.rootRecord().update();
      field.getPutFunction().apply(updater, values);
      updater.exec(db);
      return jackStore.rootRecord().read().execute(db);
    });

    if (values.get(0) instanceof LocalDateTime) {

      assertEquals(
          values.stream().map(v -> JackUtility.DATETIME_TO_MILLIS.apply((LocalDateTime)v)).collect(Collectors.toList()),
          field.getReadFunction().apply(record).stream().map(v -> JackUtility.DATETIME_TO_MILLIS.apply((LocalDateTime)v)).collect(Collectors.toList())
      );
    } else {
      assertEquals(values, field.getReadFunction().apply(record));
    }
  }

}
