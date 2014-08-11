package com.rapleaf.jack;

import java.io.IOException;
import java.util.Set;

import junit.framework.TestCase;

import com.rapleaf.jack.test_project.DatabasesImpl;
import com.rapleaf.jack.test_project.IDatabases;
import com.rapleaf.jack.test_project.database_1.iface.IUserPersistence;
import com.rapleaf.jack.test_project.database_1.models.User;


public class TestModelQuery extends TestCase {

  private static final DatabaseConnection DATABASE_CONNECTION1 = new DatabaseConnection("database1");

  public void testBasicQuery() throws IOException {

    IDatabases dbs = new DatabasesImpl(DATABASE_CONNECTION1);
    dbs.getDatabase1().deleteAll();
    IUserPersistence users = dbs.getDatabase1().users();

    User userA = users.createDefaultInstance().setHandle("A").setBio("Trader").setNumPosts(1);
    User userB = users.createDefaultInstance().setHandle("B").setBio("Trader").setNumPosts(2);
    User userC = users.createDefaultInstance().setHandle("C").setBio("CEO").setNumPosts(2);
    User userD = users.createDefaultInstance().setHandle("D").setBio("Janitor").setNumPosts(3);
    userA.save();
    userB.save();
    userC.save();
    userD.save();

    Set<User> result;

    // an empty query should return an empty set
    result = users.query().find();
    assertTrue(result.isEmpty());

    // A simple query
    result = users.query().bio("Janitor").find();
    assertEquals(1, result.size());
    assertTrue(result.contains(userD));

    // A chained query
    result = users.query().handle("A").bio("Trader").numPosts(1).find();
    assertEquals(1, result.size());
    assertTrue(result.contains(userA));

    // A chained query
    result = users.query().bio("Trader").numPosts(2).find();
    assertEquals(1, result.size());
    assertTrue(result.contains(userB));

    // A query with no results
    result = users.query().numPosts(3).bio("CEO").find();
    assertTrue(result.isEmpty());
  }

  public void testQueryOperators() throws IOException {

    IDatabases dbs = new DatabasesImpl(DATABASE_CONNECTION1);
    dbs.getDatabase1().deleteAll();
    IUserPersistence users = dbs.getDatabase1().users();

    User userA = users.createDefaultInstance().setHandle("Brad").setBio("Soccer player").setNumPosts(1).setCreatedAtMillis(1l);
    User userB = users.createDefaultInstance().setHandle("Brandon").setBio("Formula 1 driver").setNumPosts(2).setCreatedAtMillis(1l);
    User userC = users.createDefaultInstance().setHandle("Casey").setBio("Singer").setNumPosts(2).setCreatedAtMillis(2l);
    User userD = users.createDefaultInstance().setHandle("John").setBio("Ice skater").setNumPosts(3).setCreatedAtMillis(2l);
    User userE = users.createDefaultInstance().setHandle("James").setBio("Database").setNumPosts(3).setCreatedAtMillis(2l);
    userA.save();
    userB.save();
    userC.save();
    userD.save();
    userE.save();

    Set<User> result;

    result = users.query().someBoolean(JackMatchers.greaterThan(false))
        .find();

    for (User user : result) {
      System.out.println(user.getHandle());
    }

  }
}
