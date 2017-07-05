package com.rapleaf.jack.store.executors2;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestInternalScopeGetter extends BaseExecutorTestCase2 {
  private static final Logger LOG = LoggerFactory.getLogger(TestInternalScopeGetter.class);

  @Test
  public void testGetAllSubScopeIds() throws Exception {
    // empty parent scope
    long parentScopeId = createSubScope(Optional.empty(), Optional.empty());
    assertTrue(transactor.queryAsTransaction(db -> InternalScopeGetter.getAllSubScopeIds(db, table, parentScopeId)).isEmpty());

    // parent scope with sub scopes
    Set<Long> subScopeIds = Sets.newHashSet();
    int size = Math.max(3, RANDOM.nextInt(5));
    transactor.executeAsTransaction(db -> {
      for (int i = 0; i < size; ++i) {
        subScopeIds.add(jackStore2.scope(parentScopeId).createSubScope().execute(db).getScopeId());
      }
    });
    assertEquals(subScopeIds, transactor.queryAsTransaction(db -> InternalScopeGetter.getAllSubScopeIds(db, table, parentScopeId)));
  }

  @Test
  public void testGetValidSubScopeIds() throws Exception {
    long parentScopeId = createSubScope(Optional.empty(), Optional.empty());
    int size = 20;
    List<Long> subScopeIds = Lists.newArrayListWithCapacity(20);
    for (int i = 0; i < size; ++i) {
      subScopeIds.add(createSubScope(Optional.of(parentScopeId), Optional.empty()));
    }

    // all sub scope IDs are valid
    Random random = new Random(System.currentTimeMillis());
    int lo = Math.max(1, random.nextInt(size / 2));
    int hi = Math.min(size, lo + size / 5 + random.nextInt(size));
    LOG.info("Range: [{}, {})", lo, hi);
    List<Long> selectedSubScopeIds = subScopeIds.subList(lo, hi);
    Set<Long> expectedSubScopeIds1 = Sets.newHashSet(selectedSubScopeIds);
    Set<Long> actualSubScopeIds1 = transactor.queryAsTransaction(db -> InternalScopeGetter.getValidSubScopeIds(db, table, parentScopeId, expectedSubScopeIds1));
    assertEquals(expectedSubScopeIds1, actualSubScopeIds1);

    // invalid sub scope IDs are filtered out
    long maxScopeId = Collections.max(selectedSubScopeIds);
    Set<Long> expectedSubScopeIds2 = Sets.newHashSet(maxScopeId);
    Set<Long> actualSubScopeIds2 = transactor.queryAsTransaction(db -> InternalScopeGetter.getValidSubScopeIds(db, table, parentScopeId, Sets.newHashSet(maxScopeId, maxScopeId + 100L, maxScopeId + 200L)));
    assertEquals(expectedSubScopeIds2, actualSubScopeIds2);
  }

}
