package com.rapleaf.jack.store.executors;

import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rapleaf.jack.store.JsRecord;
import com.rapleaf.jack.store.JsRecords;
import com.rapleaf.jack.store.JsScope;

import static org.junit.Assert.assertEquals;

public class TestSubScopeReaderExecutor extends BaseExecutorTestCase {
  private static final Logger LOG = LoggerFactory.getLogger(TestSubScopeReaderExecutor.class);

  @Test
  public void testGetRecords() throws Exception {
    String customScope = "custom_scope";
    String key1 = "key1";
    String key2 = "key2";
    String key3 = "key3";

    int size = 20;
    List<JsScope> scopes = Lists.newArrayListWithCapacity(size);
    transactor.executeAsTransaction(db -> {
      for (long i = 0L; i < size; ++i) {
        JsScope scope = jackStore.scope(customScope).createSubScope(String.valueOf(i)).execute(db);
        scopes.add(scope);
        jackStore.scope(scope).indexRecords()
            .putLong(LONG_KEY, i)
            .putString(STRING_KEY, String.valueOf(i))
            .putJson(JSON_KEY, JSON_PARSER.parse(String.format("{%s: {%s: %d}, %s: [1, 2, 3]}", key1, key2, i, key3)).getAsJsonObject())
            .execute(db);
      }
    });

    Random random = new Random(System.currentTimeMillis());
    int lo = Math.max(1, random.nextInt(size / 2));
    int hi = Math.min(size, lo + size / 5 + random.nextInt(size));
    LOG.info("Range: [{}, {})", lo, hi);
    List<JsScope> subScopes = scopes.subList(lo, hi);
    JsRecords jsRecords = transactor.queryAsTransaction(db -> jackStore.scope(customScope).readSubScopes(subScopes).execute(db));
    assertEquals(subScopes.size(), jsRecords.size());
    for (int i = 0; i < subScopes.size(); ++i) {
      String scopeName = subScopes.get(i).getScopeName();
      int scopeNameInt = Integer.valueOf(scopeName);
      JsRecord jsRecord = jsRecords.get(i);
      long scopeId = subScopes.get(i).getScopeId();

      assertEquals(scopeId, jsRecord.getScopeId().longValue());
      assertEquals(scopeNameInt, jsRecord.getLong(LONG_KEY).intValue());
      assertEquals(scopeName, jsRecord.getString(STRING_KEY));

      JsonObject json = jsRecord.getJson(JSON_KEY);
      int jsonKey3Value = json.get(key1).getAsJsonObject().get(key2).getAsJsonPrimitive().getAsNumber().intValue();
      assertEquals(scopeNameInt, jsonKey3Value);
    }
  }

}
