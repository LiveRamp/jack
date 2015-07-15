package com.rapleaf.jack;

import com.google.common.collect.Sets;
import com.rapleaf.jack.queries.ModelDelete;
import com.rapleaf.jack.queries.QueryOrder;
import com.rapleaf.jack.queries.WhereConstraint;
import com.rapleaf.jack.queries.where_operators.EqualTo;
import com.rapleaf.jack.queries.where_operators.In;
import com.rapleaf.jack.queries.where_operators.JackMatchers;
import com.rapleaf.jack.test_project.DatabasesImpl;
import com.rapleaf.jack.test_project.IDatabases;
import com.rapleaf.jack.test_project.database_1.iface.IUserPersistence;
import com.rapleaf.jack.test_project.database_1.models.Post;
import com.rapleaf.jack.test_project.database_1.models.User;
import junit.framework.TestCase;

import java.io.IOException;
import java.util.List;

public class TestModelDelete extends TestCase {

  private static final IDatabases dbs = new DatabasesImpl();

  public void testDeleteStatement() {
    String tableName = "posts";

    ModelDelete deleteStatement = new ModelDelete();
    deleteStatement.addIds(Sets.newHashSet(1L, 50L, 3L, 10L));
    deleteStatement.addConstraint(new WhereConstraint<>(Post._Fields.title, new EqualTo<>("Obama")));
    deleteStatement.addConstraint(new WhereConstraint<>(Post._Fields.user_id, new EqualTo<>(5)));

    String expectedStatement = "DELETE FROM posts WHERE (id in (1,50,3,10) AND title = ? AND user_id = ?)".toLowerCase();
    assertEquals(expectedStatement, deleteStatement.getStatement(tableName).trim().toLowerCase());
  }

  public void testIdOnlyStatement() {
    String tableName = "posts";

    ModelDelete deleteStatement = new ModelDelete();
    deleteStatement.addIds(Sets.newHashSet(1L, 50L, 3L));
    deleteStatement.addId(10L);

    String expectedStatement = "DELETE FROM posts WHERE (id in (1,50,3,10))".toLowerCase();
    assertEquals(expectedStatement, deleteStatement.getStatement(tableName).trim().toLowerCase());
  }

  public void testWhereConditionOnlyStatement() {
    String tableName = "posts";

    ModelDelete deleteStatement = new ModelDelete();
    deleteStatement.addConstraint(new WhereConstraint<>(Post._Fields.title, new EqualTo<>("Obama")));
    deleteStatement.addConstraint(new WhereConstraint<>(Post._Fields.user_id, new In<>(5, 10)));

    String expectedStatement = "DELETE FROM posts WHERE (title = ? AND user_id IN (?, ?))".toLowerCase();
    assertEquals(expectedStatement, deleteStatement.getStatement(tableName).trim().toLowerCase());
  }

  public void testDeleteWithDeleteStatement() throws IOException {
    IUserPersistence users = dbs.getDatabase1().users();
    users.deleteAll();

    User userA = users.createDefaultInstance().setHandle("A").setBio("Trader").setNumPosts(1);
    User userB = users.createDefaultInstance().setHandle("B").setBio("Trader").setNumPosts(2);
    User userC = users.createDefaultInstance().setHandle("C").setBio("CEO").setNumPosts(2);
    User userD = users.createDefaultInstance().setHandle("D").setBio("Janitor").setNumPosts(3);
    userA.save();
    userB.save();
    userC.save();
    userD.save();

    ModelDelete delete = new ModelDelete();
    delete.addConstraint(new WhereConstraint<>(User._Fields.handle, JackMatchers.equalTo("B")));
    users.delete(delete);
    List<User> allUsers = users.findAll();
    assertEquals(3, allUsers.size());
  }

}
