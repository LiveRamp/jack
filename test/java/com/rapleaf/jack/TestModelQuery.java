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

  public void testQuery() throws IOException {

    IDatabases dbs = new DatabasesImpl(DATABASE_CONNECTION1);
    dbs.getDatabase1().deleteAll();
    IUserPersistence users = dbs.getDatabase1().users();

    User userA = users.createDefaultInstance().setHandle("A").setBio("Rapper").setNumPosts(1);
    User userB = users.createDefaultInstance().setHandle("B").setBio("Rapper").setNumPosts(2);
    User userC = users.createDefaultInstance().setHandle("C").setBio("Drug dealer").setNumPosts(2);
    User userD = users.createDefaultInstance().setHandle("D").setBio("Cool dentist").setNumPosts(3);
    userA.save();
    userB.save();
    userC.save();
    userD.save();

    Set<User> result;

    // Empty query: should return an empty set
    result = users.query().find();
    assertTrue(result.isEmpty());

    // A simple query
    result = users.query().bio("Cool dentist").find();
    assertEquals(1, result.size());
    assertTrue(result.contains(userD));

    // A chained query
    result = users.query().handle("A").bio("Rapper").num_posts(1).find();
    assertEquals(1, result.size());
    assertTrue(result.contains(userA));

    // A chained query
    result = users.query().bio("Rapper").num_posts(2).find();
    assertEquals(1, result.size());
    assertTrue(result.contains(userB));

    // A query with no results
    result = users.query().num_posts(3).bio("Rapper").find();
    assertTrue(result.isEmpty());
  }
}
