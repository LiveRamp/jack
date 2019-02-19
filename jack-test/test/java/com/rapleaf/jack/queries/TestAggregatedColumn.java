package com.rapleaf.jack.queries;

import org.junit.Test;

import com.rapleaf.jack.test_project.database_1.models.User;

import static org.junit.Assert.assertEquals;

public class TestAggregatedColumn {
  @Test
  public void testAs() {
    AggregatedColumn<Long> maxUserId = AggregatedColumn.MAX(User.ID);
    assertEquals(Long.class, maxUserId.type);
    assertEquals(Integer.class, maxUserId.as(Integer.class).type);
  }

  @Test
  public void testForTable() {
    AggregatedColumn<Long> maxUserId = AggregatedColumn.MAX(User.ID);
    assertEquals("users", maxUserId.table);

    AggregatedColumn<Long> aliasedTable = maxUserId.forTable("user_table");
    // table name is updated
    assertEquals("user_table", aliasedTable.table);
    // table field and type are the same
    assertEquals(maxUserId.field, aliasedTable.field);
    assertEquals(maxUserId.type, aliasedTable.type);
  }

  @Test
  public void testGetSqlKeyword() {
    assertEquals("MAX(users.id) AS users_id_max", AggregatedColumn.MAX(User.ID).getSelectKeyword());
    assertEquals("users_id_max", AggregatedColumn.MAX(User.ID).getSelectAlias());
    assertEquals("MAX(users.id)", AggregatedColumn.MAX(User.ID).getSqlKeyword());
  }
}
