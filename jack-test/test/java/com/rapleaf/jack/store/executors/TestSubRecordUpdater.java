package com.rapleaf.jack.store.executors;

import java.util.Optional;
import java.util.stream.Collectors;

import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;

import com.rapleaf.jack.exception.BulkOperationException;
import com.rapleaf.jack.exception.SqlExecutionFailureException;
import com.rapleaf.jack.store.JsRecord;
import com.rapleaf.jack.store.exceptions.InvalidRecordException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestSubRecordUpdater extends BaseExecutorTestCase {

  private long parentScope;
  private long s1;
  private long s2;
  private long s3;

  @Before
  public void createSubScopes() throws Exception {
    parentScope = createSubScope(Optional.empty(), Optional.empty());
    s1 = createSubScope(Optional.of(parentScope), Optional.empty());
    s2 = createSubScope(Optional.of(parentScope), Optional.empty());
    s3 = createSubScope(Optional.of(parentScope), Optional.empty());
  }

  @Test
  public void testUpdateNothing() throws Exception {
    jsRecords = transactor.queryAsTransaction(db -> jackStore2.rootRecord().updateSubRecords().execute(db));
    assertTrue(jsRecords.isEmpty());
  }

  @Test
  public void testUpdate() throws Exception {
    // insert new value
    jsRecords = transactor.queryAsTransaction(db ->
        jackStore2.record(parentScope)
            .updateSubRecords()
            .whereSubRecordIds(s1, s2)
            .whereSubRecordIds(Sets.newHashSet(s3))
            .put(LONG_KEY, LONG_VALUE)
            .put(DOUBLE_LIST_KEY, DOUBLE_LIST_VALUE)
            .put(JSON_KEY, JSON_VALUE)
            .execute(db)
    );
    assertEquals(3, jsRecords.size());
    assertEquals(Sets.newHashSet(s1, s2, s3), Sets.newHashSet(jsRecords.getRecordIds()));
    for (JsRecord jsRecord : jsRecords) {
      assertEquals(Sets.newHashSet(LONG_KEY, DOUBLE_LIST_KEY, JSON_KEY), jsRecord.keySet());
      assertEquals(LONG_VALUE, jsRecord.getLong(LONG_KEY).longValue());
      assertEquals(DOUBLE_LIST_VALUE, jsRecord.getDoubleList(DOUBLE_LIST_KEY));
      assertEquals(JSON_VALUE, jsRecord.getJson(JSON_KEY));
    }

    // update s1 and s3
    jsRecords = transactor.queryAsTransaction(db ->
        jackStore2.record(parentScope)
            .updateSubRecords()
            .whereSubRecordIds(s1)
            .whereSubRecordIds(Sets.newHashSet(s3))
            .put(STRING_KEY, BOOLEAN_LIST_VALUE)
            .put(DOUBLE_LIST_KEY, DOUBLE_LIST_VALUE)
            .put(JSON_KEY, STRING_VALUE)
            .put(INT_KEY, INT_VALUE)
            .execute(db)
    );

    assertEquals(2, jsRecords.size());
    assertEquals(Sets.newHashSet(s1, s3), Sets.newHashSet(jsRecords.getRecordIds()));
    for (JsRecord jsRecord : jsRecords) {
      assertEquals(Sets.newHashSet(LONG_KEY, STRING_KEY, DOUBLE_LIST_KEY, JSON_KEY, INT_KEY), jsRecord.keySet());
      assertEquals(LONG_VALUE, jsRecord.getLong(LONG_KEY).longValue());
      assertEquals(BOOLEAN_LIST_VALUE, jsRecord.getBooleanList(STRING_KEY));
      assertEquals(DOUBLE_LIST_VALUE, jsRecord.getDoubleList(DOUBLE_LIST_KEY));
      assertEquals(STRING_VALUE, jsRecord.getString(JSON_KEY));
      assertEquals(INT_VALUE, jsRecord.getInt(INT_KEY).intValue());
    }

    // s2 is not updated
    jsRecords = transactor.queryAsTransaction(db ->
        jackStore2.record(parentScope)
            .readSubRecords()
            .whereSubRecordIds(s2)
            .execute(db)
    );
    assertEquals(1, jsRecords.size());
    assertEquals(Sets.newHashSet(LONG_KEY, DOUBLE_LIST_KEY, JSON_KEY), jsRecords.getOnly().keySet());
    assertEquals(LONG_VALUE, jsRecords.getOnly().getLong(LONG_KEY).longValue());
    assertEquals(DOUBLE_LIST_VALUE, jsRecords.getOnly().getDoubleList(DOUBLE_LIST_KEY));
    assertEquals(JSON_VALUE, jsRecords.getOnly().getJson(JSON_KEY));
  }

  @Test
  public void testBulkUpdate() throws Exception {
    try {
      transactor.executeAsTransaction(db ->
          jackStore2.record(parentScope)
              .updateSubRecords()
              .put(STRING_KEY, STRING_VALUE)
              .execute(db)
      );
    } catch (SqlExecutionFailureException e) {
      assertTrue(e.getCause() instanceof BulkOperationException);
    }

    jsRecords = transactor.queryAsTransaction(db ->
        jackStore2.record(parentScope)
            .updateSubRecords()
            .allowBulkUpdate()
            .put(STRING_KEY, STRING_VALUE)
            .execute(db)
    );
    assertEquals(3, jsRecords.size());
    assertEquals(Sets.newHashSet(s1, s2, s3), Sets.newHashSet(jsRecords.getRecordIds()));
    assertEquals(Sets.newHashSet(STRING_VALUE), jsRecords.stream().map(r -> r.getString(STRING_KEY)).collect(Collectors.toSet()));
  }

  @Test
  public void testInvalidSubScopes() throws Exception {
    try {
      transactor.executeAsTransaction(db -> {
        jackStore2.record(parentScope)
            .updateSubRecords()
            .whereSubRecordIds(Sets.newHashSet(100L))
            .put(STRING_KEY, STRING_VALUE)
            .execute(db);
      });
    } catch (SqlExecutionFailureException e) {
      assertTrue(e.getCause() instanceof InvalidRecordException);
    }
  }

}
