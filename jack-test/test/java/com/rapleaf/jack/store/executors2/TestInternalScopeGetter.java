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

  @Test
  public void testGetNestedScopeIds() throws Exception {
    /*
     * s1 ─┬─ s11 ─┬─ s111 --- s1111
     *     |       └─ s112
     *     └─ s12
     * s2
     */
    long s1 = createSubScope(Optional.empty(), Optional.empty());
    long s2 = createSubScope(Optional.empty(), Optional.empty());
    long s11 = createSubScope(Optional.of(s1), Optional.empty());
    long s12 = createSubScope(Optional.of(s1), Optional.empty());
    long s111 = createSubScope(Optional.of(s11), Optional.empty());
    long s112 = createSubScope(Optional.of(s11), Optional.empty());
    long s1111 = createSubScope(Optional.of(s111), Optional.empty());

    transactor.executeAsTransaction(db -> {
      assertEquals(Sets.newHashSet(s11, s12, s111, s112, s1111), InternalScopeGetter.getNestedScopeIds(db, table, Collections.singleton(s1)));
      assertEquals(Sets.newHashSet(), InternalScopeGetter.getNestedScopeIds(db, table, Collections.singleton(s2)));
      assertEquals(Sets.newHashSet(s111, s112, s1111), InternalScopeGetter.getNestedScopeIds(db, table, Collections.singleton(s11)));
      assertEquals(Sets.newHashSet(s1111), InternalScopeGetter.getNestedScopeIds(db, table, Collections.singleton(s111)));
      assertEquals(Sets.newHashSet(), InternalScopeGetter.getNestedScopeIds(db, table, Collections.singleton(s1111)));
      assertEquals(Sets.newHashSet(), InternalScopeGetter.getNestedScopeIds(db, table, Collections.singleton(s112)));
      assertEquals(Sets.newHashSet(), InternalScopeGetter.getNestedScopeIds(db, table, Collections.singleton(s12)));
    });
  }

}
