package com.rapleaf.jack;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import com.rapleaf.jack.test_project.DatabasesImpl;
import com.rapleaf.jack.test_project.IDatabases;
import com.rapleaf.jack.test_project.MockDatabasesImpl;
import com.rapleaf.jack.test_project.database_1.iface.IUserPersistence;
import com.rapleaf.jack.test_project.database_1.models.User;

import static com.rapleaf.jack.JackMatchers.*;


public class TestModelQuery extends TestCase {

  private static final DatabaseConnection DATABASE_CONNECTION1 = new DatabaseConnection("database1");

  public void testDbImplQueries() throws IOException {
    IDatabases dbs = new DatabasesImpl(DATABASE_CONNECTION1);

    testBasicQuery(dbs);
    testQueryOperators(dbs);
    testQueryById(dbs);
  }

  public void testMockDbQueries() throws IOException {
    IDatabases dbs = new MockDatabasesImpl();

    testBasicQuery(dbs);
    testQueryOperators(dbs);
    testQueryById(dbs);
  }

  public void testBasicQuery(IDatabases dbs) throws IOException {

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

  public void testQueryOperators(IDatabases dbs) throws IOException {

    IUserPersistence users = dbs.getDatabase1().users();
    users.deleteAll();

    User brad = users.createDefaultInstance().setHandle("Brad").setBio("Soccer player").setNumPosts(1).setCreatedAtMillis(1l);
    User brandon = users.createDefaultInstance().setHandle("Brandon").setBio("Formula 1 driver").setNumPosts(2).setCreatedAtMillis(1l).setSomeDatetime(0l);
    User casey = users.createDefaultInstance().setHandle("Casey").setBio("Singer").setNumPosts(2).setCreatedAtMillis(2l);
    User john = users.createDefaultInstance().setHandle("John").setBio("Ice skater").setNumPosts(3).setCreatedAtMillis(2l);
    User james = users.createDefaultInstance().setHandle("James").setBio("Surfer").setNumPosts(5).setCreatedAtMillis(3l).setSomeDatetime(1000000l);
    brad.save();
    brandon.save();
    casey.save();
    john.save();
    james.save();

    Set<User> result;

    // Equal To
    result = users.query().handle(equalTo("Brad")).find();
    assertEquals(1, result.size());
    assertTrue(result.contains(brad));

    // Between
    result = users.query().numPosts(between(4, 8)).find();
    assertEquals(1, result.size());
    assertTrue(result.contains(james));

    // Less Than
    result = users.query().createdAtMillis(lessThan(2l)).find();
    assertEquals(2, result.size());
    assertTrue(result.contains(brad));
    assertTrue(result.contains(brandon));

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
    assertTrue(result.isEmpty());

    // Contains and In
    result = users.query().bio(contains("f"))
        .numPosts(in(1, 3, 5))
        .find();
    assertEquals(1, result.size());
    assertTrue(result.contains(james));

    // Not In and Not Equal To
    result = users.query().handle(notIn("Brad", "Brandon", "Jennifer", "John"))
        .numPosts(notEqualTo(5))
        .find();
    assertEquals(1, result.size());
    assertTrue(result.contains(casey));

    result = users.query().someDatetime(JackMatchers.<Long>isNull()).find();
    assertEquals(3, result.size());

    result = users.query().someDatetime(JackMatchers.<Long>isNotNull()).find();
    assertEquals(2, result.size());
    assertTrue(result.contains(brandon));
    assertTrue(result.contains(james));

    // If a null parameter is passed, an exeception should be thrown
    try {
      users.query().handle(in(null, "brandon")).find();
      fail("an In query with one null parameter should throw an exception");
    } catch (IllegalArgumentException e) {
      // This exception is expected
    }
  }

  public void testQueryById(IDatabases dbs) throws IOException {
    IUserPersistence users = dbs.getDatabase1().users();
    users.deleteAll();

    User[] sampleUsers = new User[5];
    for (int i = 0; i < sampleUsers.length; i++) {
      sampleUsers[i] = users.createDefaultInstance().setNumPosts(i % 2);
      sampleUsers[i].save();
    }

    Set<User> result;

    // Query by one id
    result = users.query().id(sampleUsers[0].getId()).find();
    assertEquals(1, result.size());
    assertTrue(result.contains(sampleUsers[0]));

    // Query by several ids
    Set<Long> sampleIds = new HashSet<Long>();
    sampleIds.add(sampleUsers[0].getId());
    sampleIds.add(sampleUsers[3].getId());
    sampleIds.add(sampleUsers[4].getId());
    result = users.query().id(sampleIds).find();
    assertEquals(3, result.size());
    assertTrue(result.contains(sampleUsers[0]));
    assertTrue(result.contains(sampleUsers[3]));
    assertTrue(result.contains(sampleUsers[4]));

    //Query by several ids and constraints
    Set<Long> sampleIds2 = new HashSet<Long>();
    sampleIds2.add(sampleUsers[2].getId());
    sampleIds2.add(sampleUsers[3].getId());

    result = users.query()
        .numPosts(greaterThan(0))
        .id(sampleIds2)
        .find();
    assertEquals(1, result.size());
    assertTrue(result.contains(sampleUsers[3]));

  }
}
