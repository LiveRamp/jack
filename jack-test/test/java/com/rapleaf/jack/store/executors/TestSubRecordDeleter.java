package com.rapleaf.jack.store.executors;

import java.util.Collections;

import com.google.common.collect.Sets;
import org.junit.Test;

import com.rapleaf.jack.exception.BulkOperationException;
import com.rapleaf.jack.exception.JackRuntimeException;
import com.rapleaf.jack.exception.SqlExecutionFailureException;
import com.rapleaf.jack.store.JsRecord;
import com.rapleaf.jack.store.exceptions.InvalidRecordException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TestSubRecordDeleter extends BaseExecutorTestCase {

  @Test
  public void testDeleteKey() throws Exception {
    // create three records
    JsRecord r1 = transactor.queryAsTransaction(db ->
        jackStore.rootRecord().createSubRecord()
            .put(LONG_KEY, LONG_VALUE)
            .put(DOUBLE_KEY, DOUBLE_VALUE)
            .put(STRING_LIST_KEY, STRING_LIST_VALUE)
            .put(DOUBLE_LIST_KEY, DOUBLE_LIST_VALUE)
            .put(JSON_KEY, JSON_VALUE)
            .execute(db)
    );
    long s1 = r1.getRecordId();
    assertEquals(Sets.newHashSet(LONG_KEY, DOUBLE_KEY, STRING_LIST_KEY, DOUBLE_LIST_KEY, JSON_KEY), r1.keySet());

    JsRecord r2 = transactor.queryAsTransaction(db ->
        jackStore.rootRecord().createSubRecord()
            .put(LONG_KEY, LONG_VALUE)
            .put(DOUBLE_KEY, DOUBLE_VALUE)
            .put(STRING_LIST_KEY, STRING_LIST_VALUE)
            .put(DOUBLE_LIST_KEY, DOUBLE_LIST_VALUE)
            .put(JSON_KEY, JSON_VALUE)
            .execute(db)
    );
    long s2 = r2.getRecordId();
    assertEquals(Sets.newHashSet(LONG_KEY, DOUBLE_KEY, STRING_LIST_KEY, DOUBLE_LIST_KEY, JSON_KEY), r2.keySet());

    JsRecord r3 = transactor.queryAsTransaction(db ->
        jackStore.rootRecord().createSubRecord()
            .put(LONG_KEY, LONG_VALUE)
            .put(DOUBLE_KEY, DOUBLE_VALUE)
            .put(STRING_LIST_KEY, STRING_LIST_VALUE)
            .put(DOUBLE_LIST_KEY, DOUBLE_LIST_VALUE)
            .put(JSON_KEY, JSON_VALUE)
            .execute(db)
    );
    long s3 = r3.getRecordId();
    assertEquals(Sets.newHashSet(LONG_KEY, DOUBLE_KEY, STRING_LIST_KEY, DOUBLE_LIST_KEY, JSON_KEY), r3.keySet());

    // delete primitive value key in record 1 and 2
    transactor.execute(db -> {
      jackStore.rootRecord()
          .deleteSubRecords()
          .whereSubRecordIds(s1)
          .whereSubRecordIds(Collections.singleton(s2))
          .deleteKey(LONG_KEY)
          .execute(db);
      jsRecords = jackStore.rootRecord()
          .readSubRecords()
          .whereSubRecordIds(s1, s2)
          .execute(db);
      for (JsRecord jsRecord : jsRecords) {
        assertEquals(Sets.newHashSet(DOUBLE_KEY, STRING_LIST_KEY, DOUBLE_LIST_KEY, JSON_KEY), jsRecord.keySet());
      }
      jsRecords = jackStore.rootRecord()
          .readSubRecords()
          .whereSubRecordIds(s3)
          .execute(db);
      assertEquals(Sets.newHashSet(LONG_KEY, DOUBLE_KEY, STRING_LIST_KEY, DOUBLE_LIST_KEY, JSON_KEY), jsRecords.getOnly().keySet());
    });

    // delete list key in record 1 and 2
    transactor.execute(db -> {
      jackStore.rootRecord()
          .deleteSubRecords()
          .whereSubRecordIds(s1)
          .whereSubRecordIds(Collections.singleton(s2))
          .deleteKey(STRING_LIST_KEY)
          .execute(db);
      jsRecords = jackStore.rootRecord()
          .readSubRecords()
          .whereSubRecordIds(s1, s2)
          .execute(db);
      for (JsRecord jsRecord : jsRecords) {
        assertEquals(Sets.newHashSet(DOUBLE_KEY, DOUBLE_LIST_KEY, JSON_KEY), jsRecord.keySet());
      }
      jsRecords = jackStore.rootRecord()
          .readSubRecords()
          .whereSubRecordIds(s3)
          .execute(db);
      assertEquals(Sets.newHashSet(LONG_KEY, DOUBLE_KEY, STRING_LIST_KEY, DOUBLE_LIST_KEY, JSON_KEY), jsRecords.getOnly().keySet());
    });

    // delete json key in record 1 and 2
    transactor.execute(db -> {
      jackStore.rootRecord()
          .deleteSubRecords()
          .whereSubRecordIds(s1)
          .whereSubRecordIds(Collections.singleton(s2))
          .deleteKey(JSON_KEY)
          .execute(db);
      jsRecords = jackStore.rootRecord()
          .readSubRecords()
          .whereSubRecordIds(s1, s2)
          .execute(db);
      for (JsRecord jsRecord : jsRecords) {
        assertEquals(Sets.newHashSet(DOUBLE_KEY, DOUBLE_LIST_KEY), jsRecord.keySet());
      }
      jsRecords = jackStore.rootRecord()
          .readSubRecords()
          .whereSubRecordIds(s3)
          .execute(db);
      assertEquals(Sets.newHashSet(LONG_KEY, DOUBLE_KEY, STRING_LIST_KEY, DOUBLE_LIST_KEY, JSON_KEY), jsRecords.getOnly().keySet());
    });

    // delete non-existing key in record 1 and 2
    transactor.execute(db -> {
      jackStore.rootRecord()
          .deleteSubRecords()
          .whereSubRecordIds(s1)
          .whereSubRecordIds(Collections.singleton(s2))
          .deleteKey(LONG_KEY)
          .execute(db);
      jsRecords = jackStore.rootRecord()
          .readSubRecords()
          .whereSubRecordIds(s1, s2)
          .execute(db);
      for (JsRecord jsRecord : jsRecords) {
        assertEquals(Sets.newHashSet(DOUBLE_KEY, DOUBLE_LIST_KEY), jsRecord.keySet());
      }
      jsRecords = jackStore.rootRecord()
          .readSubRecords()
          .whereSubRecordIds(s3)
          .execute(db);
      assertEquals(Sets.newHashSet(LONG_KEY, DOUBLE_KEY, STRING_LIST_KEY, DOUBLE_LIST_KEY, JSON_KEY), jsRecords.getOnly().keySet());
    });

    // delete key in all records
    transactor.execute(db -> {
      jackStore.rootRecord()
          .deleteSubRecords()
          .allowBulkDeletion()
          .deleteKey(DOUBLE_KEY)
          .execute(db);
      jsRecords = jackStore.rootRecord()
          .readSubRecords()
          .whereSubRecordIds(s1, s2)
          .execute(db);
      for (JsRecord jsRecord : jsRecords) {
        assertEquals(Sets.newHashSet(DOUBLE_LIST_KEY), jsRecord.keySet());
      }
      jsRecords = jackStore.rootRecord()
          .readSubRecords()
          .whereSubRecordIds(s3)
          .execute(db);
      assertEquals(Sets.newHashSet(LONG_KEY, STRING_LIST_KEY, DOUBLE_LIST_KEY, JSON_KEY), jsRecords.getOnly().keySet());
    });

    // delete all keys in record 1 and 2
    transactor.execute(db -> {
      jackStore.rootRecord()
          .deleteSubRecords()
          .whereSubRecordIds(s1, s2)
          .deleteAllKeys()
          .execute(db);
      jsRecords = jackStore.rootRecord()
          .readSubRecords()
          .whereSubRecordIds(s1, s2)
          .execute(db);
      for (JsRecord jsRecord : jsRecords) {
        assertTrue(jsRecord.isEmpty());
      }
      jsRecords = jackStore.rootRecord()
          .readSubRecords()
          .whereSubRecordIds(s3)
          .execute(db);
      assertEquals(Sets.newHashSet(LONG_KEY, STRING_LIST_KEY, DOUBLE_LIST_KEY, JSON_KEY), jsRecords.getOnly().keySet());
    });

    // delete non-existing key in empty record 1 and 2
    transactor.execute(db -> {
      jackStore.rootRecord()
          .deleteSubRecords()
          .whereSubRecordIds(s1, s2)
          .deleteKey(LONG_KEY)
          .execute(db);
      jsRecords = jackStore.rootRecord()
          .readSubRecords()
          .whereSubRecordIds(s1, s2)
          .execute(db);
      for (JsRecord jsRecord : jsRecords) {
        assertTrue(jsRecord.isEmpty());
      }
      jsRecords = jackStore.rootRecord()
          .readSubRecords()
          .whereSubRecordIds(s3)
          .execute(db);
      assertEquals(Sets.newHashSet(LONG_KEY, STRING_LIST_KEY, DOUBLE_LIST_KEY, JSON_KEY), jsRecords.getOnly().keySet());
    });

    // delete all keys in all records
    transactor.execute(db -> {
      jackStore.rootRecord()
          .deleteSubRecords()
          .deleteAllKeys()
          .allowBulkDeletion()
          .execute(db);
      jsRecords = jackStore.rootRecord()
          .readSubRecords()
          .execute(db);
      assertEquals(3, jsRecords.size());
      for (JsRecord jsRecord : jsRecords) {
        assertTrue(jsRecord.isEmpty());
      }
    });
  }

  @Test
  public void testDeleteRecord() throws Exception {
    /*
     * Create records
     * root ─┬─ r1 ─── r11
     *       ├─ r2
     *       └─ r3
     */
    JsRecord r1 = transactor.queryAsTransaction(db ->
        jackStore.rootRecord().createSubRecord()
            .put(LONG_KEY, LONG_VALUE)
            .execute(db)
    );
    long s1 = r1.getRecordId();

    JsRecord r11 = transactor.queryAsTransaction(db ->
        jackStore.record(s1).createSubRecord()
            .put(LONG_KEY, LONG_VALUE)
            .execute(db)
    );
    long s11 = r11.getRecordId();

    JsRecord r2 = transactor.queryAsTransaction(db ->
        jackStore.rootRecord().createSubRecord()
            .put(LONG_KEY, LONG_VALUE)
            .execute(db)
    );
    long s2 = r2.getRecordId();

    JsRecord r3 = transactor.queryAsTransaction(db ->
        jackStore.rootRecord().createSubRecord()
            .put(LONG_KEY, LONG_VALUE)
            .execute(db)
    );
    long s3 = r3.getRecordId();

    // delete s1 without recursion will fail
    try {
      transactor.executeAsTransaction(db ->
          jackStore.rootRecord().deleteSubRecords()
              .whereSubRecordIds(s1)
              .deleteEntireRecord()
              .execute(db)
      );
      fail();
    } catch (SqlExecutionFailureException e) {
      assertTrue(e.getCause() instanceof JackRuntimeException);
    }

    // delete s1 with recursion will succeed
    jsRecords = transactor.queryAsTransaction(db -> {
      jackStore.rootRecord().deleteSubRecords()
          .whereSubRecordIds(s1)
          .deleteEntireRecord(true)
          .execute(db);
      return jackStore.rootRecord().readSubRecords()
          .execute(db);
    });
    assertEquals(Sets.newHashSet(s2, s3), Sets.newHashSet(jsRecords.getRecordIds()));

    // delete all records without allowing bulk deletion will fail
    try {
      transactor.queryAsTransaction(db -> {
        jackStore.rootRecord().deleteSubRecords()
            .deleteEntireRecord(true)
            .execute(db);
        return jackStore.rootRecord().readSubRecords()
            .execute(db);
      });
      fail();
    } catch (SqlExecutionFailureException e) {
      assertTrue(e.getCause() instanceof BulkOperationException);
    }

    // delete all records allowing bulk deletion will succeed
    jsRecords = transactor.queryAsTransaction(db -> {
      jackStore.rootRecord().deleteSubRecords()
          .deleteEntireRecord(true)
          .allowBulkDeletion()
          .execute(db);
      return jackStore.rootRecord().readSubRecords()
          .execute(db);
    });
    assertTrue(jsRecords.isEmpty());
  }

  @Test
  public void testNoBulkDeletion() throws Exception {
    try {
      transactor.execute(db -> jackStore.rootRecord().deleteSubRecords().execute(db));
      fail();
    } catch (SqlExecutionFailureException e) {
      assertTrue(e.getCause() instanceof BulkOperationException);
    }
  }

  @Test
  public void testNoDeletion() throws Exception {
    try {
      transactor.query(db -> jackStore.rootRecord().deleteSubRecords().allowBulkDeletion().execute(db));
      transactor.executeAsTransaction(db -> jackStore.rootRecord().deleteSubRecords().allowBulkDeletion().execute(db));
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  public void testInvalidSubScope() throws Exception {
    try {
      transactor.executeAsTransaction(db -> jackStore.rootRecord().deleteSubRecords().whereSubRecordIds(100L).execute(db));
      fail();
    } catch (SqlExecutionFailureException e) {
      assertTrue(e.getCause() instanceof InvalidRecordException);
    }
  }

}
