package com.rapleaf.jack;

import java.io.IOException;
import java.util.Set;

import junit.framework.TestCase;

import com.rapleaf.jack.test_project.DatabasesImpl;
import com.rapleaf.jack.test_project.IDatabases;
import com.rapleaf.jack.test_project.database_1.iface.IUserPersistence;
import com.rapleaf.jack.test_project.database_1.models.User;

import static com.rapleaf.jack.JackMatchers.*;


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
    User userB = users.createDefaultInstance().setHandle("Brandon").setBio("Formula 1 driver").setNumPosts(2).setCreatedAtMillis(1l).setSomeDate(1000000000000l);
    User userC = users.createDefaultInstance().setHandle("Casey").setBio("Singer").setNumPosts(2).setCreatedAtMillis(2l);
    User userD = users.createDefaultInstance().setHandle("John").setBio("Ice skater").setNumPosts(3).setCreatedAtMillis(2l);
    User userE = users.createDefaultInstance().setHandle("James").setBio("Surfer").setNumPosts(5).setCreatedAtMillis(3l).setSomeDate(1l);
    userA.save();
    userB.save();
    userC.save();
    userD.save();
    userE.save();

    Set<User> result;

    // Equal To
    result = users.query().handle(equalTo("Brad")).find();
    assertEquals("Brad", result.iterator().next().getHandle());

    // Between
    result = users.query().numPosts(between(4, 8)).find();
    assertEquals("James", result.iterator().next().getHandle());

    // Less Than
    result = users.query().createdAtMillis(lessThan(2l)).find();
    assertEquals(2, result.size());

    // Greater Than
    result = users.query().createdAtMillis(greaterThan(1l)).find();
    assertEquals(3, result.size());

    // Less Than Or Equal To
    result = users.query().createdAtMillis(lessThanOrEqualTo(2l)).find();
    assertEquals(4, result.size());

    // Greater Than Or Equal To
    result = users.query().createdAtMillis(greaterThanOrEqualTo(1l)).find();
    assertEquals(5, result.size());

    // Ends With
    result = users.query().bio(endsWith("er")).find();
    assertEquals(5, result.size());

    // StartsWith
    result = users.query().bio(startsWith("er")).find();
    assertEquals(0, result.size());

    // Contains and In
    result = users.query().bio(contains("f"))
        .numPosts(in(1, 3, 5))
        .find();
    assertEquals("James", result.iterator().next().getHandle());

    // Not In and Not Equal To
    result = users.query().handle(notIn("Brad", "Brandon", "Jennifer", "John"))
        .numPosts(notEqualTo(5))
        .find();
    assertEquals("Casey", result.iterator().next().getHandle());

    result = users.query().someDate(isNull()).find();
    assertEquals(3, result.size());

    result = users.query().someDate(isNotNull()).find();
    assertEquals(2, result.size());

    // If a null parameter is passed, an exeception should be thrown
    try {
      users.query().handle(in(null, "brandon")).find();
      fail("an In query with one null parameter should throw an exception");
    } catch (IllegalArgumentException e) {
      // This is expected
    }

  }
}
