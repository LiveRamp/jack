package com.rapleaf.jack.queries;

import org.junit.Test;

import com.rapleaf.jack.test_project.database_1.models.User;

import static org.junit.Assert.assertEquals;

public class TestColumn {
  @Test
  public void testAs() {
    assertEquals(Long.class, User.ID.type);
    assertEquals(Integer.class, User.ID.as(Integer.class).type);
  }

  @Test
  public void testForTable() {
    assertEquals("users", User.ID.table);
    // table name is updated
    assertEquals("user_table", User.ID.forTable("user_table").table);
    // table field and type are the same
    assertEquals(User.ID.field, User.ID.forTable("user_table").field);
    assertEquals(User.ID.type, User.ID.forTable("user_table").type);
  }

  @Test
  public void testSqlKeywordMethods() {
    // id column
    Column<Long> nullTableIdColumn = Column.fromId(null);
    assertEquals("id", nullTableIdColumn.getSelectKeyword());
    assertEquals("id", nullTableIdColumn.getSelectAlias());
    assertEquals("id", nullTableIdColumn.getSqlKeyword());

    assertEquals("users.id", User.ID.getSelectKeyword());
    assertEquals("users.id", User.ID.getSelectAlias());
    assertEquals("users.id", User.ID.getSqlKeyword());

    Column<Long> aliasedIdColumn = User.Tbl.as("user_table").ID;
    assertEquals("user_table.id", aliasedIdColumn.getSelectKeyword());
    assertEquals("user_table.id", aliasedIdColumn.getSelectAlias());
    assertEquals("user_table.id", aliasedIdColumn.getSqlKeyword());

    // non id column
    Column<String> nullTableColumn = Column.fromField(null, User._Fields.bio, String.class);
    assertEquals("bio", nullTableColumn.getSelectKeyword());
    assertEquals("bio", nullTableColumn.getSelectAlias());
    assertEquals("bio", nullTableColumn.getSqlKeyword());

    assertEquals("users.bio", User.BIO.getSelectKeyword());
    assertEquals("users.bio", User.BIO.getSelectAlias());
    assertEquals("users.bio", User.BIO.getSqlKeyword());

    Column<String> aliasedColumn = User.Tbl.as("user_table").BIO;
    assertEquals("user_table.bio", aliasedColumn.getSelectKeyword());
    assertEquals("user_table.bio", aliasedColumn.getSelectAlias());
    assertEquals("user_table.bio", aliasedColumn.getSqlKeyword());
  }
}
