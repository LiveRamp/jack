package com.rapleaf.jack.store.executors;

import java.util.List;

import org.junit.Test;

import com.rapleaf.jack.exception.JackRuntimeException;
import com.rapleaf.jack.queries.where_operators.JackMatchers;
import com.rapleaf.jack.store.exceptions.MissingScopeException;
import com.rapleaf.jack.test_project.database_1.models.TestStore;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestScopeModificationExecutor extends BaseExecutorTestCase {

  @Test
  public void testRename() throws Exception {
    createScope("scope0");
    boolean rename = jackStore.withinRoot().renameScope("scope0", "scope1").execute();
    assertTrue(rename);

    List<TestStore> records = transactor.query(db ->
        db.testStore().findAll()
    );

    assertEquals(1, records.size());
    assertEquals("scope1", records.get(0).getValue());
  }

  @Test
  public void testNestedRename() throws Exception {
    jackStore.within("scope0").createScope("scope1").execute();
    boolean rename = jackStore.within("scope0").renameScope("scope1", "scope2").execute();
    assertTrue(rename);

    List<TestStore> records = transactor.query(db ->
        db.testStore().query().whereScope(JackMatchers.isNotNull()).find()
    );

    assertEquals(1, records.size());
    assertEquals("scope2", records.get(0).getValue());
  }

  @Test(expected = MissingScopeException.class)
  public void testNonexistRename() throws Exception {
    jackStore.withinRoot().createScope("scope0").execute();
    jackStore.withinRoot().renameScope("scope1", "scope2").execute();
  }

  @Test(expected = JackRuntimeException.class)
  public void testExistingRename() throws Exception {
    jackStore.withinRoot().createScope("scope0").execute();
    jackStore.withinRoot().createScope("scope1").execute();
    jackStore.withinRoot().renameScope("scope0", "scope1").execute();
  }

}
