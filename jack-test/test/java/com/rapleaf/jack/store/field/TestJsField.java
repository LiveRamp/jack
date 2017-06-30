package com.rapleaf.jack.store.field;

import java.util.List;
import java.util.stream.Collectors;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import com.rapleaf.jack.store.JsRecord;
import com.rapleaf.jack.store.executors.BaseExecutorTestCase;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestJsField extends BaseExecutorTestCase {

  @Before
  public void prepare() throws Exception {
    transactor.execute(db -> jackStore.rootScope().deleteSubScopes().allowBulk().allowRecursion().execute(db));
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
      field.getPutFunction().apply(jackStore.rootScope().indexRecords(), value).execute(db);
      return jackStore.rootScope().readScope().execute(db);
    });

    if (value instanceof DateTime) {
      assertTrue(((DateTime)value).isEqual((DateTime)field.getReadFunction().apply(record)));
    } else {
      assertEquals(value, field.getReadFunction().apply(record));
    }
  }

  private <T> void testListField(JsListField<T> field, String key, List<T> value) throws Exception {
    assertEquals(key, field.getKey());

    JsRecord record = transactor.queryAsTransaction(db -> {
      field.getPutFunction().apply(jackStore.rootScope().indexRecords(), value).execute(db);
      return jackStore.rootScope().readScope().execute(db);
    });

    if (value.get(0) instanceof DateTime) {
      assertEquals(
          value.stream().map(Object::toString).collect(Collectors.toList()),
          field.getReadFunction().apply(record).stream().map(Object::toString).collect(Collectors.toList())
      );
    } else {
      assertEquals(value, field.getReadFunction().apply(record));
    }
  }

}
