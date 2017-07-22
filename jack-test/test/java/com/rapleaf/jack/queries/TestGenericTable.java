package com.rapleaf.jack.queries;

import org.junit.Test;

import com.rapleaf.jack.test_project.database_1.models.User;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class TestGenericTable {

  @Test
  public void test() throws Exception {
    UserTable table1 = new UserTable(User.TBL, User.HANDLE, User.NUM_POSTS);
    UserTable table2 = new UserTable(User.TBL, User.HANDLE, User.NUM_POSTS);
    assertEquals(table1.hashCode(), table2.hashCode());
    assertEquals(table1, table2);

    table1 = table1.as("alias");
    assertNotEquals(table1.hashCode(), table2.hashCode());
    assertNotEquals(table1, table2);

    table2 = table2.as("alias");
    assertEquals(table1.hashCode(), table2.hashCode());
    assertEquals(table1, table2);
  }

  private static class UserTable extends GenericTable<UserTable> {

    private final Column<String> handle;
    private final Column<Integer> numPosts;

    private enum DefaultField {
      handle, num_posts
    }

    private UserTable(Table<?, ?> table, Column<String> handle, Column<Integer> numPosts) {
      super(table, UserTable.class, handle, numPosts);
      this.handle = handle;
      this.numPosts = numPosts;
    }

    @Override
    public UserTable as(String alias) {
      return new UserTable(getAliasTable(alias), handle, numPosts);
    }
  }

}
