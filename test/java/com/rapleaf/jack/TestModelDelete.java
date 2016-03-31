package com.rapleaf.jack;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;
import junit.framework.TestCase;

import com.rapleaf.jack.queries.ModelDelete;
import com.rapleaf.jack.queries.WhereConstraint;
import com.rapleaf.jack.test_project.DatabasesImpl;
import com.rapleaf.jack.test_project.IDatabases;
import com.rapleaf.jack.test_project.database_1.iface.IUserPersistence;
import com.rapleaf.jack.test_project.database_1.models.Post;
import com.rapleaf.jack.test_project.database_1.models.User;

import static com.rapleaf.jack.queries.where_operators.JackMatchers.equalTo;
import static com.rapleaf.jack.queries.where_operators.JackMatchers.greaterThan;
import static com.rapleaf.jack.queries.where_operators.JackMatchers.in;
import static com.rapleaf.jack.queries.where_operators.JackMatchers.lessThan;

public class TestModelDelete extends TestCase {

  private static final IDatabases dbs = new DatabasesImpl();
  private IUserPersistence users;

  public void testDeleteStatement() {
    String tableName = "posts";

    ModelDelete deleteStatement = new ModelDelete();
    deleteStatement.addIds(Sets.newHashSet(1L, 50L, 3L, 10L));
    deleteStatement.addConstraint(new WhereConstraint<>(Post._Fields.title, equalTo("Obama")));
    deleteStatement.addConstraint(new WhereConstraint<>(Post._Fields.user_id, equalTo(5)));

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
    deleteStatement.addConstraint(new WhereConstraint<>(Post._Fields.title, equalTo("Obama")));
    deleteStatement.addConstraint(new WhereConstraint<>(Post._Fields.user_id, in(5, 10)));

    String expectedStatement = "DELETE FROM posts WHERE (title = ? AND user_id IN (?, ?))".toLowerCase();
    assertEquals(expectedStatement, deleteStatement.getStatement(tableName).trim().toLowerCase());
  }

  public void testDeleteWithDeleteStatement() throws IOException {
    IUserPersistence users = populateDatabase();

    ModelDelete delete = new ModelDelete();
    delete.addConstraint(new WhereConstraint<>(User._Fields.handle, equalTo("B")));
    users.delete(delete);
    List<User> allUsers = users.findAll();
    assertEquals(3, allUsers.size());
  }

  public void testDeleteAll() throws IOException, SQLException {
    populateDatabase();

    assertEquals(4, users.findAll().size());
    // an empty query will delete everything
    users.delete().execute();
    assertEquals(0, users.findAll().size());
  }

  public void testDeleteById() throws IOException, SQLException {
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

    // Query by several ids
    Set<Long> idsToDelete = new HashSet<Long>();
    idsToDelete.add(userA.getId());
    idsToDelete.add(userC.getId());
    // Delete two users by ID
    users.delete().idIn(idsToDelete).id(userD.getId()).execute();
    assertEquals(1, users.findAll().size());
  }

  public void testDeleteByCondition() throws IOException, SQLException {
    populateDatabase();

    // Delete two users by number of posts
    users.delete().whereNumPosts(greaterThan(1)).whereNumPosts(lessThan(3)).execute();
    assertEquals(2, users.findAll().size());
  }

  public void testModelDeleteClearsCache() throws IOException {
    IUserPersistence users = dbs.getDatabase1().users();
    users.deleteAll();

    users.enableCaching();
    User userA = users.createDefaultInstance().setHandle("A").setBio("Trader").setNumPosts(1);
    User userB = users.createDefaultInstance().setHandle("B").setBio("Trader").setNumPosts(2);
    User userC = users.createDefaultInstance().setHandle("C").setBio("CEO").setNumPosts(2);
    User userD = users.createDefaultInstance().setHandle("D").setBio("Janitor").setNumPosts(3);
    userA.save();
    userB.save();
    userC.save();
    userD.save();

    users.delete().whereNumPosts(greaterThan(1)).whereNumPosts(lessThan(3)).execute();
    assertEquals(2, users.findAll().size());

    assertNull(users.find(userB.getId()));
    assertNull(users.find(userC.getId()));
    assertNotNull(users.find(userA.getId()));
    assertNotNull(users.find(userD.getId()));
  }

  private IUserPersistence populateDatabase() throws IOException {
    users = dbs.getDatabase1().users();
    users.deleteAll();

    User userA = users.createDefaultInstance().setHandle("A").setBio("Trader").setNumPosts(1);
    User userB = users.createDefaultInstance().setHandle("B").setBio("Trader").setNumPosts(2);
    User userC = users.createDefaultInstance().setHandle("C").setBio("CEO").setNumPosts(2);
    User userD = users.createDefaultInstance().setHandle("D").setBio("Janitor").setNumPosts(3);
    userA.save();
    userB.save();
    userC.save();
    userD.save();
    return users;
  }

}
