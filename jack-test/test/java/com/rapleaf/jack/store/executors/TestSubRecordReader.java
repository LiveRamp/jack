package com.rapleaf.jack.store.executors;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonObject;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rapleaf.jack.exception.SqlExecutionFailureException;
import com.rapleaf.jack.store.JsConstants;
import com.rapleaf.jack.store.JsRecord;
import com.rapleaf.jack.store.exceptions.InvalidRecordException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestSubRecordReader extends BaseExecutorTestCase {
  private static final Logger LOG = LoggerFactory.getLogger(TestSubRecordReader.class);

  @Test
  public void testReadSubScopes() throws Exception {
    String parentRecord = "custom_record";
    String jsonKey1 = "key1";
    String jsonKey2 = "key2";
    String jsonKey3 = "key3";

    long parentScopeId = createSubScope(Optional.empty(), Optional.of(parentRecord));

    int size = 20;
    List<JsRecord> allJsRecords = Lists.newLinkedList();
    transactor.executeAsTransaction(db -> {
      for (long i = 0L; i < size; ++i) {
        JsRecord record = jackStore2.record(parentScopeId)
            .createSubRecord()
            .recordName(String.valueOf(i))
            .putLong(LONG_KEY, i)
            .putString(STRING_KEY, String.valueOf(i))
            .putJson(JSON_KEY, JSON_PARSER.parse(String.format("{%s: {%s: %d}, %s: [1, 2, 3]}", jsonKey1, jsonKey2, i, jsonKey3)).getAsJsonObject())
            .execute(db);
        allJsRecords.add(record);
      }
    });

    Random random = new Random(System.currentTimeMillis());
    int lo = Math.max(1, random.nextInt(size / 2));
    int hi = Math.min(size, lo + size / 5 + random.nextInt(size));
    LOG.info("Range: [{}, {})", lo, hi);
    List<JsRecord> subRecords = allJsRecords.subList(lo, hi);
    List<Long> subScopeIds = subRecords.stream().map(JsRecord::getRecordId).collect(Collectors.toList());
    jsRecords = transactor.queryAsTransaction(db -> {
      SubRecordReader reader = jackStore2.record(parentScopeId).readSubRecords();
      for (long subScopeId : subScopeIds) {
        reader.whereSubRecordIds(Collections.singleton(subScopeId));
      }
      return reader.execute(db);
    });
    assertEquals(subRecords.size(), jsRecords.size());
    for (int i = 0; i < subRecords.size(); ++i) {
      int index = i + lo;
      JsRecord jsRecord = jsRecords.get(i);
      long recordId = subRecords.get(i).getRecordId();
      // TODO: test record name from record
      String intString = String.valueOf(index);

      assertEquals(recordId, jsRecord.getRecordId().longValue());
      assertEquals(index, jsRecord.getLong(LONG_KEY).intValue());
      assertEquals(intString, jsRecord.getString(STRING_KEY));

      JsonObject json = jsRecord.getJson(JSON_KEY);
      int jsonKey3Value = json.get(jsonKey1).getAsJsonObject().get(jsonKey2).getAsJsonPrimitive().getAsNumber().intValue();
      assertEquals(index, jsonKey3Value);
    }
  }

  @Test
  public void testZeroSubScope() throws Exception {
    jsRecords = transactor.queryAsTransaction(db ->
        jackStore2.rootRecord().readSubRecords().execute(db)
    );
    assertEquals(JsConstants.ROOT_RECORD_ID, jsRecords.getParentRecordId());
    assertTrue(jsRecords.isEmpty());
  }

  @Test
  public void testEmptySubScopes() throws Exception {
    jsRecords = transactor.queryAsTransaction(db ->
        jackStore2.rootRecord().readSubRecords().execute(db)
    );

    int size = Math.max(3, RANDOM.nextInt(5));
    Set<Long> subScopeIds = Sets.newHashSet();
    Long parentScopeId = transactor.queryAsTransaction(db -> {
      long recordId = jackStore2.rootRecord().createSubRecord().execute(db).getRecordId();
      for (int i = 0; i < size; ++i) {
        subScopeIds.add(jackStore2.record(recordId).createSubRecord().execute(db).getRecordId());
      }
      return recordId;
    });

    jsRecords = transactor.queryAsTransaction(db -> jackStore2.record(parentScopeId).readSubRecords().execute(db));
    assertEquals(subScopeIds, Sets.newHashSet(this.jsRecords.getRecordIds()));
    assertEquals(parentScopeId, jsRecords.getParentRecordId());
    for (int i = 0; i < size; ++i) {
      JsRecord jsRecord = this.jsRecords.get(i);
      assertTrue(jsRecord.isEmpty());
    }
  }

  @Test
  public void testInvalidSubScopeIds() throws Exception {
    try {
      jsRecords = transactor.queryAsTransaction(db ->
          jackStore2.rootRecord()
              .readSubRecords()
              .whereSubRecordIds(Collections.singleton(5001L))
              .execute(db)
      );
    } catch (SqlExecutionFailureException e) {
      assertTrue(e.getCause() instanceof InvalidRecordException);
    }
  }

}
