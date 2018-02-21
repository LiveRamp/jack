package com.rapleaf.jack.store.executors;

import java.util.Collections;
import java.util.Optional;

import com.google.common.collect.Sets;
import org.junit.Test;

import com.rapleaf.jack.exception.JackRuntimeException;
import com.rapleaf.jack.exception.SqlExecutionFailureException;
import com.rapleaf.jack.store.JsConstants;
import com.rapleaf.jack.store.JsRecord;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TestRecordDeleter extends BaseExecutorTestCase {

  @Test
  public void testDeleteKey() throws Exception {
    // test deletion under root record
    createRecordAndTest(JsConstants.ROOT_RECORD_ID);

    // test deletion under custom record
    createRecordAndTest(createSubScope(Optional.empty(), Optional.empty()));
  }

  private void createRecordAndTest(Long recordId) throws Exception {
    // create a record
    JsRecord record = transactor.queryAsTransaction(db ->
        jackStore.record(recordId).update()
            .put(LONG_KEY, LONG_VALUE)
            .put(DOUBLE_KEY, DOUBLE_VALUE)
            .put(STRING_LIST_KEY, STRING_LIST_VALUE)
            .put(DOUBLE_LIST_KEY, DOUBLE_LIST_VALUE)
            .put(JSON_KEY, JSON_VALUE)
            .execute(db)
    );
    assertEquals(Sets.newHashSet(LONG_KEY, DOUBLE_KEY, STRING_LIST_KEY, DOUBLE_LIST_KEY, JSON_KEY), record.keySet());

    // delete primitive value key
    record = transactor.queryAsTransaction(db -> {
      jackStore.record(recordId)
          .delete()
          .deleteKey(LONG_KEY)
          .execute(db);
      return jackStore.record(recordId)
          .read()
          .execute(db);
    });
    assertEquals(Sets.newHashSet(DOUBLE_KEY, STRING_LIST_KEY, DOUBLE_LIST_KEY, JSON_KEY), record.keySet());

    // delete list key
    record = transactor.queryAsTransaction(db -> {
      jackStore.record(recordId)
          .delete()
          .deleteKey(Collections.singleton(STRING_LIST_KEY))
          .execute(db);
      return jackStore.record(recordId)
          .read()
          .execute(db);
    });
    assertEquals(Sets.newHashSet(DOUBLE_KEY, DOUBLE_LIST_KEY, JSON_KEY), record.keySet());

    // delete json key
    record = transactor.queryAsTransaction(db -> {
      jackStore.record(recordId)
          .delete()
          .deleteKey(JSON_KEY)
          .execute(db);
      return jackStore.record(recordId)
          .read()
          .execute(db);
    });
    assertEquals(Sets.newHashSet(DOUBLE_KEY, DOUBLE_LIST_KEY), record.keySet());

    // delete non-existing key
    record = transactor.queryAsTransaction(db -> {
      jackStore.record(recordId)
          .delete()
          .deleteKey(LONG_KEY, STRING_LIST_KEY, JSON_KEY)
          .execute(db);
      return jackStore.record(recordId)
          .read()
          .execute(db);
    });
    assertEquals(Sets.newHashSet(DOUBLE_KEY, DOUBLE_LIST_KEY), record.keySet());

    // delete all keys
    record = transactor.queryAsTransaction(db -> {
      jackStore.record(recordId)
          .delete()
          .deleteAllKeys()
          .execute(db);
      return jackStore.record(recordId)
          .read()
          .execute(db);
    });
    assertTrue(record.isEmpty());

    // delete non-existing key in empty record
    record = transactor.queryAsTransaction(db -> {
      jackStore.record(recordId)
          .delete()
          .deleteKey(STRING_KEY)
          .execute(db);
      return jackStore.record(recordId)
          .read()
          .execute(db);
    });
    assertTrue(record.isEmpty());
  }

  @Test
  public void testDeleteRecord() throws Exception {
    /*
     * Create root and three nested records
     * root ─── n1 ─── n2 ─── n3
     */
    JsRecord rootRecord = transactor.queryAsTransaction(db ->
        jackStore.rootRecord().update()
            .put(LONG_KEY, LONG_VALUE)
            .execute(db)
    );

    long n1 = transactor.queryAsTransaction(db ->
        jackStore.rootRecord().createSubRecord()
            .put(DOUBLE_KEY, DOUBLE_VALUE)
            .execute(db)
            .getRecordId()
    );

    long n2 = transactor.queryAsTransaction(db ->
        jackStore.record(n1).createSubRecord()
            .put(BOOLEAN_KEY, BOOLEAN_VALUE)
            .execute(db)
            .getRecordId()
    );

    long n3 = transactor.queryAsTransaction(db ->
        jackStore.record(n2).createSubRecord()
            .put(BOOLEAN_KEY, BOOLEAN_VALUE)
            .execute(db)
            .getRecordId()
    );

    // delete n2 without recursion will fail
    try {
      transactor.executeAsTransaction(db ->
          jackStore.record(n2).delete()
              .deleteEntireRecord()
              .execute(db)
      );
      fail();
    } catch (SqlExecutionFailureException e) {
      assertTrue(e.getCause() instanceof JackRuntimeException);
    }

    // delete n2 with recursion will succeed
    transactor.queryAsTransaction(db ->
        jackStore.record(n2).delete()
            .deleteEntireRecord(true)
            .execute(db)
    );

    // delete root without recursion will fail
    try {
      transactor.executeAsTransaction(db ->
          jackStore.rootRecord().delete()
              .deleteEntireRecord()
              .execute(db)
      );
      fail();
    } catch (SqlExecutionFailureException e) {
      assertTrue(e.getCause() instanceof JackRuntimeException);
    }

    // delete root with recursion will succeed
    transactor.queryAsTransaction(db ->
        jackStore.rootRecord().delete()
            .deleteEntireRecord(true)
            .execute(db)
    );
  }

}
