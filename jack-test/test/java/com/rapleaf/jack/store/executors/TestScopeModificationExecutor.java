package com.rapleaf.jack.store.executors;

import java.util.List;

import org.junit.Test;

import com.rapleaf.jack.exception.JackRuntimeException;
import com.rapleaf.jack.exception.SqlExecutionFailureException;
import com.rapleaf.jack.queries.where_operators.JackMatchers;
import com.rapleaf.jack.store.exceptions.MissingScopeException;
import com.rapleaf.jack.test_project.database_1.models.TestStore;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TestScopeModificationExecutor extends BaseExecutorTestCase {

  @Test
  public void testRename() throws Exception {
    createScope("scope0");
    boolean rename = transactor.query(db -> jackStore.rootScope().renameSubScope("scope0", "scope1").execute(db));
    assertTrue(rename);

    List<TestStore> records = transactor.query(db ->
        db.testStore().findAll()
    );

    assertEquals(1, records.size());
    assertEquals("scope1", records.get(0).getValue());
  }

  @Test
  public void testNestedRename() throws Exception {
    boolean rename = transactor.queryAsTransaction(db -> {
      jackStore.scope("scope0").createSubScope("scope1").execute(db);
      return jackStore.scope("scope0").renameSubScope("scope1", "scope2").execute(db);
    });
    assertTrue(rename);

    List<TestStore> records = transactor.query(db ->
        db.testStore().query().whereScope(JackMatchers.isNotNull()).find()
    );

    assertEquals(1, records.size());
    assertEquals("scope2", records.get(0).getValue());
  }

  @Test
  public void testNonexistRename() throws Exception {
    try {
      transactor.executeAsTransaction(db -> {
        jackStore.rootScope().createSubScope("scope0").execute(db);
        jackStore.rootScope().renameSubScope("scope1", "scope2").execute(db);
      });
      fail();
    } catch (SqlExecutionFailureException e) {
      assertTrue(e.getCause() instanceof MissingScopeException);
    }
  }

  @Test
  public void testExistingRename() throws Exception {
    try {
      transactor.executeAsTransaction(db -> {
        jackStore.rootScope().createSubScope("scope0").execute(db);
        jackStore.rootScope().createSubScope("scope1").execute(db);
        jackStore.rootScope().renameSubScope("scope0", "scope1").execute(db);
      });
      fail();
    } catch (SqlExecutionFailureException e) {
      assertTrue(e.getCause() instanceof JackRuntimeException);
    }
  }

}
