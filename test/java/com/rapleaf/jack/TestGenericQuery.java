package com.rapleaf.jack;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;

import com.rapleaf.jack.queries.QueryOrder;
import com.rapleaf.jack.queries.Record;
import com.rapleaf.jack.queries.Records;
import com.rapleaf.jack.test_project.DatabasesImpl;
import com.rapleaf.jack.test_project.IDatabases;
import com.rapleaf.jack.test_project.database_1.Database1Query;
import com.rapleaf.jack.test_project.database_1.iface.ICommentPersistence;
import com.rapleaf.jack.test_project.database_1.iface.IPostPersistence;
import com.rapleaf.jack.test_project.database_1.iface.IUserPersistence;
import com.rapleaf.jack.test_project.database_1.models.Comment;
import com.rapleaf.jack.test_project.database_1.models.Post;
import com.rapleaf.jack.test_project.database_1.models.User;

import static com.rapleaf.jack.queries.AggregatedColumn.AVG;
import static com.rapleaf.jack.queries.AggregatedColumn.COUNT;
import static com.rapleaf.jack.queries.AggregatedColumn.MAX;
import static com.rapleaf.jack.queries.AggregatedColumn.MIN;
import static com.rapleaf.jack.queries.AggregatedColumn.SUM;
import static com.rapleaf.jack.queries.QueryOrder.ASC;
import static com.rapleaf.jack.queries.QueryOrder.DESC;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TestGenericQuery {
  private static final DatabaseConnection DATABASE_CONNECTION1 = new DatabaseConnection("database1");
  private static final IDatabases dbs = new DatabasesImpl(DATABASE_CONNECTION1);

  private final IUserPersistence users = dbs.getDatabase1().users();
  private final ICommentPersistence comments = dbs.getDatabase1().comments();
  private final IPostPersistence posts = dbs.getDatabase1().posts();

  private User userA, userB, userC, userD, userE, userF, userG, userH;
  private Post postA, postB, postC, postD, postE;
  private Comment commentA, commentB, commentC, commentD;
  private long datetime;
  private Records results1, results2;

  @Before
  public void prepare() throws Exception {
    users.deleteAll();
    comments.deleteAll();
    posts.deleteAll();
    results1 = null;
    results2 = null;
    datetime = System.currentTimeMillis() % 1000 * 1000;  // sql timestamp does not support nano resolution
  }

  @Test
  public void testBasicQuery() throws Exception {
    userA = users.createDefaultInstance().setHandle("A").setBio("Trader").setNumPosts(1);
    userB = users.createDefaultInstance().setHandle("B").setBio("Trader").setNumPosts(2);
    userC = users.createDefaultInstance().setHandle("C").setBio("CEO").setNumPosts(2);
    userD = users.createDefaultInstance().setHandle("D").setBio("Janitor").setNumPosts(3);
    userA.save();
    userB.save();
    userC.save();
    userD.save();

    // query with no select clause should return all the columns
    results1 = Database1Query
        .from(User.TBL)
        .fetch();
    assertFalse(results1.isEmpty());
    assertEquals(11, results1.get(0).columnCount());

    // query with only select clause should return all records with the specified columns
    results1 = Database1Query
        .from(User.TBL)
        .select(User.ID)
        .fetch();
    assertEquals(4, results1.size());
    assertEquals(1, results1.get(0).columnCount());

    // query with no result
    results1 = Database1Query
        .from(User.TBL)
        .where(User.ID.equalTo(999L))
        .fetch();
    assertTrue(results1.isEmpty());

    // query with and clause
    results1 = Database1Query
        .from(User.TBL)
        .where(User.BIO.equalTo("Trader"),
               User.HANDLE.equalTo("B"))
        .fetch();
    assertEquals(1, results1.size());
    assertTrue(userB.getId() == results1.get(0).getLong(User.ID));

    // query with or clause
    results1 = Database1Query
        .from(User.TBL)
        .where(User.HANDLE.equalTo("A")
            .or(User.HANDLE.equalTo("B")))
        .fetch();
    assertEquals(2, results1.size());
    assertEquals(Sets.newHashSet(userA.getId(), userB.getId()), Sets.newHashSet(results1.getLongs(User.ID)));
  }

  @Test
  public void testGetMethodsForNotNullColumns() throws Exception {
    userA = users.create("A", datetime, 15, 2L, datetime, "Assembly Coder", new byte[]{(byte)3}, 1.1, 1.01, true);

    results1 = Database1Query
        .from(User.TBL)
        .select(User.ID, User.HANDLE, User.SOME_DECIMAL, User.SOME_DATETIME, User.NUM_POSTS, User.SOME_BOOLEAN, User.SOME_BINARY)
        .fetch();

    assertEquals(1, results1.size());
    assertEquals(7, results1.get(0).columnCount());

    Record record = results1.get(0);
    assertTrue(record.getLong(User.ID).equals(userA.getId()));
    assertTrue(record.getIntFromLong(User.ID).equals(userA.getIntId()));
    assertTrue(record.getString(User.HANDLE).equals(userA.getHandle()));
    assertTrue(record.getDouble(User.SOME_DECIMAL).equals(userA.getSomeDecimal()));
    assertTrue(record.getLong(User.SOME_DATETIME).equals(userA.getSomeDatetime()));
    assertTrue(record.getInt(User.NUM_POSTS).equals(userA.getNumPosts()));
    assertTrue(record.getBoolean(User.SOME_BOOLEAN).equals(userA.isSomeBoolean()));
    assertTrue(Arrays.toString(record.getByteArray(User.SOME_BINARY)).equals(Arrays.toString(userA.getSomeBinary())));
  }

  @Test
  public void testGetMethodsForNullColumns() throws Exception {
    userA = users.create("A", 15);

    results1 = Database1Query
        .from(User.TBL)
        .select(User.SOME_DECIMAL, User.SOME_DATETIME, User.SOME_BOOLEAN, User.SOME_BINARY)
        .fetch();

    assertEquals(1, results1.size());
    assertEquals(4, results1.get(0).columnCount());

    Record record = results1.get(0);
    assertNull(record.getDouble(User.SOME_DECIMAL));
    assertNull(record.getLong(User.SOME_DATETIME));
    assertNull(record.getBoolean(User.SOME_BOOLEAN));
    assertNull(record.getByteArray(User.SOME_BINARY));
  }

  @Test
  public void testGetMethodsForRecordsWithNullValues() throws Exception {
    userA = users.create("A", datetime, 1, 2L, null, null, new byte[]{(byte)3}, 1.1, null, true);
    userB = users.create("B", datetime + 3, 2, 4L, null, null, new byte[]{(byte)4}, 1.2, null, false);
    userC = users.create("C", datetime - 10, 3, 6L, null, null, new byte[]{(byte)5}, 1.3, null, true);
    userA.save();
    userB.save();
    userC.save();

    results1 = Database1Query
        .from(User.TBL)
        .fetch();

    assertEquals(
        Lists.newArrayList(userA.getId(), userB.getId(), userC.getId()),
        results1.getLongs(User.ID)
    );

    assertEquals(
        Lists.newArrayList((int)userA.getId(), (int)userB.getId(), (int)userC.getId()),
        results1.getIntsFromLongs(User.ID)
    );

    assertEquals(
        Lists.newArrayList(1, 2, 3),
        results1.getInts(User.NUM_POSTS)
    );

    assertEquals(
        Lists.newArrayList("A", "B", "C"),
        results1.getStrings(User.HANDLE)
    );

    List<byte[]> byteArrays = results1.getByteArrays(User.SOME_BINARY);
    assertArrayEquals(new byte[]{(byte)3}, byteArrays.get(0));
    assertArrayEquals(new byte[]{(byte)4}, byteArrays.get(1));
    assertArrayEquals(new byte[]{(byte)5}, byteArrays.get(2));

    List<Double> doubles = results1.getDoubles(User.SOME_FLOAT);
    assertTrue(Math.abs(1.1 - doubles.get(0)) < 0.00001);
    assertTrue(Math.abs(1.2 - doubles.get(1)) < 0.00001);
    assertTrue(Math.abs(1.3 - doubles.get(2)) < 0.00001);

    assertEquals(Lists.newArrayList(true, false, true), results1.getBooleans(User.SOME_BOOLEAN));
  }

  @Test
  public void testQueryOperators() throws Exception {
    User brad = users.createDefaultInstance().setHandle("Brad").setBio("Soccer player").setNumPosts(1).setCreatedAtMillis(1L);
    User brandon = users.createDefaultInstance().setHandle("Brandon").setBio("Formula 1 driver").setNumPosts(2).setCreatedAtMillis(1L).setSomeDatetime(0L);
    User casey = users.createDefaultInstance().setHandle("Casey").setBio("Singer").setNumPosts(2).setCreatedAtMillis(2L);
    User john = users.createDefaultInstance().setHandle("John").setBio("Ice skater").setNumPosts(3).setCreatedAtMillis(2L);
    User james = users.createDefaultInstance().setHandle("James").setBio("Surfer").setNumPosts(5).setCreatedAtMillis(3L).setSomeDatetime(1000000L);
    brad.save();
    brandon.save();
    casey.save();
    john.save();
    james.save();

    // Equal To
    results1 = Database1Query.from(User.TBL).where(User.HANDLE.equalTo("Brad")).fetch();
    assertEquals(1, results1.size());
    assertEquals("Brad", results1.get(0).getString(User.HANDLE));

    // Between
    results1 = Database1Query.from(User.TBL).where(User.NUM_POSTS.between(4, 8)).fetch();
    assertEquals(1, results1.size());
    assertEquals("James", results1.get(0).getString(User.HANDLE));

    // Less Than
    results1 = Database1Query.from(User.TBL).where(User.CREATED_AT_MILLIS.lessThan(2L)).fetch();
    assertEquals(2, results1.size());
    assertEquals(Sets.newHashSet("Brad", "Brandon"), Sets.newHashSet(results1.getStrings(User.HANDLE)));

    // Greater Than
    results1 = Database1Query.from(User.TBL).where(User.CREATED_AT_MILLIS.greaterThan(1L)).fetch();
    assertEquals(3, results1.size());

    // Less Than Or Equal To
    results1 = Database1Query.from(User.TBL).where(User.CREATED_AT_MILLIS.lessThanOrEqualTo(2L)).fetch();
    assertEquals(4, results1.size());

    // Greater Than Or Equal To
    results1 = Database1Query.from(User.TBL).where(User.CREATED_AT_MILLIS.greaterThanOrEqualTo(1L)).fetch();
    assertEquals(5, results1.size());

    // Ends With
    results1 = Database1Query.from(User.TBL).where(User.BIO.endsWith("er")).fetch();
    assertEquals(5, results1.size());

    // StartsWith
    results1 = Database1Query.from(User.TBL).where(User.BIO.startsWith("er")).fetch();
    assertTrue(results1.isEmpty());

    // In with empty collection
    results1 = Database1Query.from(User.TBL).where(User.SOME_DATETIME.in(Collections.<Long>emptySet()))
        .fetch();
    assertTrue(results1.isEmpty());

    // NotIn with empty collection
    try {
      Database1Query.from(User.TBL).where(User.SOME_DATETIME.notIn(Collections.<Long>emptySet())).fetch();
      fail("Using a NotIn operator with an empty collection should throw an exception.");
    } catch (IllegalArgumentException e) {
      //This is expected
    }

    // Contains and In
    results1 = Database1Query
        .from(User.TBL)
        .where(User.BIO.contains("f"),
            User.NUM_POSTS.in(1, 3, 5))
        .fetch();
    assertEquals(1, results1.size());
    assertEquals("James", results1.get(0).getString(User.HANDLE));

    // Not In and Not Equal To
    results1 = Database1Query
        .from(User.TBL)
        .where(User.HANDLE.notIn("Brad", "Brandon", "Jennifer", "John"),
               User.NUM_POSTS.notEqualTo(5))
        .fetch();
    assertEquals(1, results1.size());
    assertEquals("Casey", results1.get(0).getString(User.HANDLE));
    
    results1 = Database1Query.from(User.TBL).where(User.SOME_DATETIME.isNull()).fetch();
    assertEquals(3, results1.size());

    results1 = Database1Query.from(User.TBL).where(User.SOME_DATETIME.isNotNull()).fetch();
    assertEquals(2, results1.size());
    assertEquals(Sets.newHashSet("James", "Brandon"), Sets.newHashSet(results1.getStrings(User.HANDLE)));

    // If a null parameter is passed, an exception should be thrown
    try {
      Database1Query.from(User.TBL).where(User.HANDLE.in(null, "brandon")).fetch();
      fail("an In query with one null parameter should throw an exception");
    } catch (IllegalArgumentException e) {
      // This exception is expected
    }
  }

  @Test
  public void testOrderByClause() throws Exception {
    userA = users.createDefaultInstance().setHandle("A").setBio("CEO").setNumPosts(1).setSomeDecimal(0.9);
    userB = users.createDefaultInstance().setHandle("B").setBio("Engineer").setNumPosts(2).setSomeDecimal(12.1);
    userC = users.createDefaultInstance().setHandle("C").setBio("Analyst").setNumPosts(3).setSomeDecimal(-0.8);
    userD = users.createDefaultInstance().setHandle("D").setBio("Dean").setNumPosts(3).setSomeDecimal(0.9);
    userE = users.createDefaultInstance().setHandle("E").setBio("Associate").setNumPosts(3).setSomeDecimal(1.1);
    userF = users.createDefaultInstance().setHandle("F").setBio("Associate").setNumPosts(6).setSomeDecimal(1.0);
    userG = users.createDefaultInstance().setHandle("G").setBio("Associate").setNumPosts(5).setSomeDecimal(2.0);
    userH = users.createDefaultInstance().setHandle("H").setBio("Associate").setNumPosts(7).setSomeDecimal(0.0);
    userA.save();
    userB.save();
    userC.save();
    userD.save();
    userE.save();
    userF.save();
    userG.save();
    userH.save();

    // A query with no results should return an empty list.
    results1 = Database1Query
        .from(User.TBL)
        .where(User.NUM_POSTS.equalTo(3),
            User.BIO.equalTo("CEO"))
        .orderBy(User.ID)
        .fetch();
    assertTrue(results1.isEmpty());

    // A simple query with single result should return a list with one element.
    results1 = Database1Query
        .from(User.TBL)
        .where(User.BIO.equalTo("Analyst"))
        .orderBy(User.ID)
        .fetch();
    assertEquals(1, results1.size());
    assertEquals("C", results1.get(0).getString(User.HANDLE));

    // A chained query with single result should return a list with one element.
    results1 = Database1Query
        .from(User.TBL)
        .where(User.HANDLE.equalTo("A"),
               User.BIO.equalTo("CEO"),
               User.NUM_POSTS.equalTo(1))
        .orderBy(User.ID)
        .fetch();
    assertEquals(1, results1.size());
    assertEquals("A", results1.get(0).getString(User.HANDLE));

    // A chained query with multiple results ordered by a specific field by default should be ordered in an ascending manner.
    // expected result: [userC, userE, userD]
    results1 = Database1Query
        .from(User.TBL)
        .where(User.NUM_POSTS.equalTo(3))
        .orderBy(User.BIO)
        .fetch();
    results2 = Database1Query
        .from(User.TBL)
        .where(User.NUM_POSTS.equalTo(3))
        .orderBy(User.BIO, ASC)
        .fetch();
    assertEquals(3, results1.size());
    assertEquals("C", results1.get(0).getString(User.HANDLE));
    assertEquals("E", results1.get(1).getString(User.HANDLE));
    assertEquals("D", results1.get(2).getString(User.HANDLE));
    assertTrue(results1.equals(results2));

    // A chained query ordered by a specified field in a descending manner should be ordered accordingly.
    // expected result: [userD, userE, userC]
    results1 = Database1Query
        .from(User.TBL)
        .where(User.NUM_POSTS.equalTo(3))
        .orderBy(User.BIO, DESC)
        .fetch();
    assertEquals(3, results1.size());
    assertEquals("D", results1.get(0).getString(User.HANDLE));
    assertEquals("E", results1.get(1).getString(User.HANDLE));
    assertEquals("C", results1.get(2).getString(User.HANDLE));

    // a chained ordered query ordered by multiple fields should be ordered accordingly.
    // expected result: [userA, userB, userC, userE, userD, userG, userF, userH]
    results1 = Database1Query
        .from(User.TBL)
        .where(User.NUM_POSTS.greaterThan(0))
        .orderBy(User.NUM_POSTS, ASC)
        .orderBy(User.BIO, ASC)
        .fetch();
    assertEquals(8, results1.size());
    assertEquals("A", results1.get(0).getString(User.HANDLE));
    assertEquals("B", results1.get(1).getString(User.HANDLE));
    assertEquals("C", results1.get(2).getString(User.HANDLE));
    assertEquals("E", results1.get(3).getString(User.HANDLE));
    assertEquals("D", results1.get(4).getString(User.HANDLE));
    assertEquals("G", results1.get(5).getString(User.HANDLE));
    assertEquals("F", results1.get(6).getString(User.HANDLE));
    assertEquals("H", results1.get(7).getString(User.HANDLE));

    // a chained ordered query ordered by multiple fields should be ordered accordingly.
    // expected result: [C, H, D, A, F, E, G, B]
    results1 = Database1Query
        .from(User.TBL)
        .where(User.NUM_POSTS.greaterThan(0))
        .orderBy(User.SOME_DECIMAL)
        .orderBy(User.BIO, DESC)
        .fetch();
    assertEquals(8, results1.size());
    assertEquals("C", results1.get(0).getString(User.HANDLE));
    assertEquals("H", results1.get(1).getString(User.HANDLE));
    assertEquals("D", results1.get(2).getString(User.HANDLE));
    assertEquals("A", results1.get(3).getString(User.HANDLE));
    assertEquals("F", results1.get(4).getString(User.HANDLE));
    assertEquals("E", results1.get(5).getString(User.HANDLE));
    assertEquals("G", results1.get(6).getString(User.HANDLE));
    assertEquals("B", results1.get(7).getString(User.HANDLE));
  }

  @Test
  public void testLimitClause() throws Exception {
    int nbUsers = 10;
    User[] sampleUsers = new User[nbUsers];

    for (int i = 0; i < 10; i++) {
      sampleUsers[i] = users.createDefaultInstance().setNumPosts(i);
      sampleUsers[i].save();
    }

    results1 = Database1Query
        .from(User.TBL)
        .where(User.NUM_POSTS.lessThan(5))
        .orderBy(User.NUM_POSTS)
        .limit(3)
        .fetch();
    assertEquals(3, results1.size());
    for (int i = 0; i < results1.size(); i++) {
      assertTrue(results1.get(i).getInt(User.NUM_POSTS) == i);
    }

    results1 = Database1Query
        .from(User.TBL)
        .where(User.NUM_POSTS.greaterThan(3))
        .orderBy(User.NUM_POSTS)
        .limit(2, 3)
        .fetch();
    assertEquals(3, results1.size());
    for (int i = 0; i < results1.size(); i++) {
      assertTrue(results1.get(i).getInt(User.NUM_POSTS) == i + 6);
    }

    results1 = Database1Query
        .from(User.TBL)
        .where(User.NUM_POSTS.lessThan(5))
        .orderBy(User.NUM_POSTS)
        .limit(3)
        .fetch();
    assertEquals(3, results1.size());

    results1 = Database1Query
        .from(User.TBL)
        .where(User.NUM_POSTS.greaterThan(3))
        .orderBy(User.NUM_POSTS)
        .limit(2, 3)
        .fetch();
    assertEquals(3, results1.size());
  }

  @Test
  public void testGroupByClause() throws Exception {
    for (int i = 0; i < 100; i++) {
      User user = users.createDefaultInstance().setHandle(String.valueOf(i % 2)).setNumPosts(i);
      user.save();
    }

    // The SELECT clause must be specified for any query with GROUP BY clause
    try {
      results2 = Database1Query
          .from(User.TBL)
          .groupBy(User.HANDLE)
          .fetch();
      fail();
    } catch (RuntimeException e) {
      // expected
    }

    // A query with GROUP BY clause cannot have a non-aggregated and non-grouped column in the SELECT list
    try {
      // users.bio is illegal
      results2 = Database1Query
          .from(User.TBL)
          .select(User.HANDLE, User.BIO, MAX(User.NUM_POSTS))
          .groupBy(User.HANDLE)
          .fetch();
      fail();
    } catch (RuntimeException e) {
      assertTrue(e.toString().contains("bio"));
    }

    // Test Max
    results2 = Database1Query
        .from(User.TBL)
        .select(User.HANDLE, MAX(User.NUM_POSTS))
        .groupBy(User.HANDLE)
        .orderBy(User.HANDLE)
        .fetch();
    assertEquals(2, results2.size());
    assertTrue(results2.get(0).getString(User.HANDLE).equals("0"));
    assertTrue(results2.get(0).getInt(MAX(User.NUM_POSTS)) == 98);
    assertTrue(results2.get(1).getString(User.HANDLE).equals("1"));
    assertTrue(results2.get(1).getInt(MAX(User.NUM_POSTS)) == 99);

    // Test Min
    results2 = Database1Query
        .from(User.TBL)
        .select(User.HANDLE, MIN(User.NUM_POSTS))
        .groupBy(User.HANDLE)
        .orderBy(User.HANDLE)
        .fetch();
    assertTrue(results2.get(0).getInt(MIN(User.NUM_POSTS)) == 0);
    assertTrue(results2.get(1).getInt(MIN(User.NUM_POSTS)) == 1);

    // Test Count
    results2 = Database1Query
        .from(User.TBL)
        .select(User.HANDLE, COUNT(User.NUM_POSTS))
        .groupBy(User.HANDLE)
        .orderBy(User.HANDLE)
        .fetch();
    assertTrue(results2.get(0).getInt(COUNT(User.NUM_POSTS)) == 50);
    assertTrue(results2.get(1).getInt(COUNT(User.NUM_POSTS)) == 50);

    // Test Sum
    results2 = Database1Query
        .from(User.TBL)
        .select(User.HANDLE, SUM(User.NUM_POSTS))
        .groupBy(User.HANDLE)
        .orderBy(User.HANDLE)
        .fetch();
    assertEquals(2, results2.size());
    assertTrue(results2.get(0).getInt(SUM(User.NUM_POSTS)) == 2450);
    assertTrue(results2.get(1).getInt(SUM(User.NUM_POSTS)) == 2500);

    // Test Avg
    results2 = Database1Query
        .from(User.TBL)
        .select(User.HANDLE, AVG(User.NUM_POSTS))
        .groupBy(User.HANDLE)
        .orderBy(User.HANDLE)
        .fetch();
    assertEquals(2, results2.size());
    assertTrue(results2.get(0).getInt(AVG(User.NUM_POSTS)) == 49);
    assertTrue(results2.get(1).getInt(AVG(User.NUM_POSTS)) == 50);
  }

  @Test
  public void testJoinQuery() throws Exception {
    userA = users.create("A", datetime, 0, 2L, datetime, "Assembly Coder", new byte[]{(byte)3}, 1.1, 1.01, true);
    userB = users.create("B", datetime, 0, 1L, datetime, "Byline Editor", new byte[]{(byte)1}, 2.2, 2.02, true);
    userC = users.create("C", datetime, 0, 4L, datetime, "Code Refactor", new byte[]{(byte)2}, 2.2, 2.02, false);

    postA = posts.create("Post A from User A", datetime, userA.getIntId(), datetime);
    postB = posts.create("Post B from User B", datetime, userB.getIntId(), datetime);
    postC = posts.create("Post C from User B", datetime, userB.getIntId(), datetime);
    postD = posts.create("Post D from User C", datetime, userC.getIntId(), datetime);
    postE = posts.create("Post E from User C", datetime, userC.getIntId(), datetime);

    commentA = comments.create("Comment A on Post B from User A", userA.getIntId(), postB.getIntId(), datetime);
    commentB = comments.create("Comment B on Post B from User B", userB.getIntId(), postB.getIntId(), datetime);
    commentC = comments.create("Comment C on Post C from User B", userB.getIntId(), postC.getIntId(), datetime);
    commentD = comments.create("Comment D on Post E from User C", userC.getIntId(), postE.getIntId(), datetime);

    results1 = Database1Query
        .from(Comment.TBL)
        .leftJoin(User.TBL).on(User.ID, Comment.COMMENTER_ID)
        .leftJoin(Post.TBL).on(Post.ID, Comment.COMMENTED_ON_ID)
        .orderBy(User.HANDLE)
        .orderBy(Post.TITLE, QueryOrder.DESC)
        .select(User.HANDLE, Comment.CONTENT, Post.TITLE)
        .fetch();

    assertEquals(4, results1.size());
    assertEquals(3, results1.get(0).columnCount());

    // the result is: comment A, C, B, D
    Record recordForCommentA = results1.get(0);
    assertEquals(commentA.getContent(), recordForCommentA.getString(Comment.CONTENT));
    assertEquals(userA.getHandle(), recordForCommentA.getString(User.HANDLE));
    assertEquals(postB.getTitle(), recordForCommentA.getString(Post.TITLE));

    Record recordForCommentC = results1.get(1);
    assertEquals(commentC.getContent(), recordForCommentC.getString(Comment.CONTENT));
    assertEquals(userB.getHandle(), recordForCommentC.getString(User.HANDLE));
    assertEquals(postC.getTitle(), recordForCommentC.getString(Post.TITLE));

    Record recordForCommentB = results1.get(2);
    assertEquals(commentB.getContent(), recordForCommentB.getString(Comment.CONTENT));
    assertEquals(userB.getHandle(), recordForCommentB.getString(User.HANDLE));
    assertEquals(postB.getTitle(), recordForCommentB.getString(Post.TITLE));

    Record recordForCommentD = results1.get(3);
    assertEquals(commentD.getContent(), recordForCommentD.getString(Comment.CONTENT));
    assertEquals(userC.getHandle(), recordForCommentD.getString(User.HANDLE));
    assertEquals(postE.getTitle(), recordForCommentD.getString(Post.TITLE));
  }

  @Test
  public void testTableAlias() throws Exception {
    userD = users.createDefaultInstance().setHandle("D").setBio("F");
    userE = users.createDefaultInstance().setHandle("E").setBio("G");
    userF = users.createDefaultInstance().setHandle("F").setBio("H");
    userG = users.createDefaultInstance().setHandle("G").setBio("D");
    userH = users.createDefaultInstance().setHandle("H").setBio("E");
    userD.save();
    userE.save();
    userF.save();
    userG.save();
    userH.save();

    // table alias cannot be null
    try {
      User.Tbl illegalTable = User.Tbl.as(null);
      fail();
    } catch (IllegalArgumentException e) {
      // expected
    }

    // table alias cannot be empty
    try {
      User.Tbl illegalTable = User.Tbl.as("");
      fail();
    } catch (IllegalArgumentException e) {
      // expected
    }

    User.Tbl handlers = User.Tbl.as("handlers");
    User.Tbl bios = User.Tbl.as("bios");

    // test simple table alias
    results1 = Database1Query
        .from(handlers)
        .orderBy(handlers.HANDLE)
        .fetch();
    assertEquals(5, results1.size());
    assertEquals(11, results1.get(0).columnCount());
    assertTrue(results1.get(0).getLong(handlers.ID) == userD.getId());

    // test self join with table alias
    results1 = Database1Query
        .from(handlers)
        .innerJoin(bios).on(handlers.BIO, bios.HANDLE)
        .orderBy(bios.HANDLE)
        .select(handlers.HANDLE, handlers.BIO, bios.HANDLE, bios.BIO)
        .fetch();
    assertEquals(5, results1.size());
    assertTrue(results1.get(0).getString(bios.HANDLE).equals("D"));
    assertTrue(results1.get(0).getString(bios.BIO).equals("F"));
    assertTrue(results1.get(1).getString(bios.HANDLE).equals("E"));
    assertTrue(results1.get(1).getString(bios.BIO).equals("G"));
    assertTrue(results1.get(2).getString(bios.HANDLE).equals("F"));
    assertTrue(results1.get(2).getString(bios.BIO).equals("H"));
    assertTrue(results1.get(3).getString(bios.HANDLE).equals("G"));
    assertTrue(results1.get(3).getString(bios.BIO).equals("D"));
    assertTrue(results1.get(4).getString(bios.HANDLE).equals("H"));
    assertTrue(results1.get(4).getString(bios.BIO).equals("E"));
  }
}
