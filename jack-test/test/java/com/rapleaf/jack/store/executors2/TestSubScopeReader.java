package com.rapleaf.jack.store.executors2;

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
import com.rapleaf.jack.store.exceptions.InvalidScopeException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestSubScopeReader extends BaseExecutorTestCase2 {
  private static final Logger LOG = LoggerFactory.getLogger(TestSubScopeReader.class);

  @Test
  public void testReadSubScopes() throws Exception {
    String parentScope = "custom_scope";
    String jsonKey1 = "key1";
    String jsonKey2 = "key2";
    String jsonKey3 = "key3";

    long parentScopeId = createSubScope(Optional.empty(), Optional.of(parentScope));

    int size = 20;
    List<JsRecord> allJsRecords = Lists.newLinkedList();
    transactor.executeAsTransaction(db -> {
      for (long i = 0L; i < size; ++i) {
        JsRecord record = jackStore2.scope(parentScopeId)
            .createSubScope()
            .scopeName(String.valueOf(i))
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
    List<Long> subScopeIds = subRecords.stream().map(JsRecord::getScopeId).collect(Collectors.toList());
    jsRecords = transactor.queryAsTransaction(db ->
        jackStore2.scope(parentScopeId)
            .readSubScopes()
            .whereSubScopeIds(subScopeIds)
            .execute(db)
    );
    assertEquals(subRecords.size(), jsRecords.size());
    for (int i = 0; i < subRecords.size(); ++i) {
      int index = i + lo;
      JsRecord jsRecord = jsRecords.get(i);

      // TODO: test scope name from record
      String intString = String.valueOf(index);
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
        jackStore2.rootScope().readSubScopes().execute(db)
    );
    assertEquals(JsConstants.ROOT_SCOPE.getScopeId(), jsRecords.getParentScopeId());
    assertTrue(jsRecords.isEmpty());
  }

  @Test
  public void testEmptySubScopes() throws Exception {
    jsRecords = transactor.queryAsTransaction(db ->
        jackStore2.rootScope().readSubScopes().execute(db)
    );

    int size = Math.max(3, RANDOM.nextInt(5));
    Set<Long> subScopeIds = Sets.newHashSet();
    Long parentScopeId = transactor.queryAsTransaction(db -> {
      long scopeId = jackStore2.rootScope().createSubScope().execute(db).getScopeId();
      for (int i = 0; i < size; ++i) {
        subScopeIds.add(jackStore2.scope(scopeId).createSubScope().execute(db).getScopeId());
      }
      return scopeId;
    });

    jsRecords = transactor.queryAsTransaction(db -> jackStore2.scope(parentScopeId).readSubScopes().execute(db));
    assertEquals(subScopeIds, Sets.newHashSet(this.jsRecords.getRecordScopeIds()));
    assertEquals(parentScopeId, jsRecords.getParentScopeId());
    for (int i = 0; i < size; ++i) {
      JsRecord jsRecord = this.jsRecords.get(i);
      assertTrue(jsRecord.isEmpty());
    }
  }

  @Test
  public void testInvalidSubScopeIds() throws Exception {
    try {
      jsRecords = transactor.queryAsTransaction(db ->
          jackStore2.rootScope()
              .readSubScopes()
              .whereSubScopeIds(Collections.singleton(5001L))
              .execute(db)
      );
    } catch (SqlExecutionFailureException e) {
      assertTrue(e.getCause() instanceof InvalidScopeException);
    }
  }

  @Test
  public void testIgnoreInvalidSubScopeIds() throws Exception {
    jsRecords = transactor.queryAsTransaction(db ->
        jackStore2.rootScope()
            .readSubScopes()
            .whereSubScopeIds(Collections.singleton(5001L))
            .ignoreInvalidSubScopes()
            .execute(db)
    );
  }

}
