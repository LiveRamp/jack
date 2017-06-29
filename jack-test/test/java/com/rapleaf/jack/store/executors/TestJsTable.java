package com.rapleaf.jack.store.executors;

import org.junit.Test;

import com.rapleaf.jack.store.JsTable;
import com.rapleaf.jack.test_project.database_1.models.TestStore;
import com.rapleaf.jack.test_project.database_1.models.User;

import static org.junit.Assert.*;

public class TestJsTable {

  private JsTable table;

  @Test
  public void testValidTable() throws Exception {
    table = JsTable.from(TestStore.TBL).create();
    assertEquals(TestStore.TBL.ID.getSqlKeyword(), table.idColumn.getSqlKeyword());
    assertEquals(TestStore.TBL.SCOPE.getSqlKeyword(), table.scopeColumn.getSqlKeyword());
    assertEquals(TestStore.TBL.TYPE.getSqlKeyword(), table.typeColumn.getSqlKeyword());
    assertEquals(TestStore.TBL.KEY.getSqlKeyword(), table.keyColumn.getSqlKeyword());
    assertEquals(TestStore.TBL.VALUE.getSqlKeyword(), table.valueColumn.getSqlKeyword());

    // switch key and value columns
    table = JsTable.from(TestStore.TBL)
        .setKeyColumn(TestStore.VALUE)
        .setValueColumn(TestStore.KEY)
        .create();
    assertEquals(TestStore.TBL.KEY.getSqlKeyword(), table.valueColumn.getSqlKeyword());
    assertEquals(TestStore.TBL.VALUE.getSqlKeyword(), table.keyColumn.getSqlKeyword());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidTable() throws Exception {
    table = JsTable.from(User.TBL).create();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidColumn() throws Exception {
    table = JsTable.from(TestStore.TBL)
        .setScopeColumn(User.NUM_POSTS.as(Long.class))
        .create();
  }

}
