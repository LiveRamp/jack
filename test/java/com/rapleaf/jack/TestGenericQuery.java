package com.rapleaf.jack;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.rapleaf.jack.queries.GenericQuery;
import com.rapleaf.jack.queries.QueryEntry;
import com.rapleaf.jack.queries.QueryOrder;
import com.rapleaf.jack.test_project.DatabasesImpl;
import com.rapleaf.jack.test_project.IDatabases;
import com.rapleaf.jack.test_project.database_1.iface.ICommentPersistence;
import com.rapleaf.jack.test_project.database_1.iface.IPostPersistence;
import com.rapleaf.jack.test_project.database_1.iface.IUserPersistence;
import com.rapleaf.jack.test_project.database_1.models.Comment;
import com.rapleaf.jack.test_project.database_1.models.Post;
import com.rapleaf.jack.test_project.database_1.models.User;

import static org.junit.Assert.*;
import static com.rapleaf.jack.queries.where_operators.JackMatchers.*;

public class TestGenericQuery {

  private static final DatabaseConnection DATABASE_CONNECTION1 = new DatabaseConnection("database1");
  private static final IDatabases dbs = new DatabasesImpl(DATABASE_CONNECTION1);

  private final IUserPersistence users = dbs.getDatabase1().users();
  private final ICommentPersistence comments = dbs.getDatabase1().comments();
  private final IPostPersistence posts = dbs.getDatabase1().posts();

  private GenericQuery genericQuery;
  private User userA, userB, userC;
  private Post postA, postB, postC, postD, postE;
  private Comment commentA, commentB, commentC, commentD;
  private long datetime;
  private List<QueryEntry> results;

  private GenericQuery createGenericQuery() {
    return GenericQuery.create(DATABASE_CONNECTION1);
  }

  @Before
  public void prepare() throws Exception {
    users.deleteAll();
    comments.deleteAll();
    posts.deleteAll();
    results = null;
    datetime = System.currentTimeMillis() % 1000 * 1000;  // sql timestamp does not support nano resolution
  }

  @Test
  public void testBasicQuery() throws Exception {
    User userA = users.createDefaultInstance().setHandle("A").setBio("Trader").setNumPosts(1);
    User userB = users.createDefaultInstance().setHandle("B").setBio("Trader").setNumPosts(2);
    User userC = users.createDefaultInstance().setHandle("C").setBio("CEO").setNumPosts(2);
    User userD = users.createDefaultInstance().setHandle("D").setBio("Janitor").setNumPosts(3);
    userA.save();
    userB.save();
    userC.save();
    userD.save();

    // query with no select clause should return all the model fields
    results = createGenericQuery().from(User.TABLE).fetch();
    assertFalse(results.isEmpty());
    assertEquals(11, results.get(0).fieldCount());

    // query with only select clause should return all records with the specified field
    results = createGenericQuery().from(User.TABLE).select(User.ID).fetch();
    assertEquals(4, results.size());
    assertEquals(1, results.get(0).fieldCount());

    // query with no result
    results = createGenericQuery().from(User.TABLE).where(User.ID, equalTo(999L)).fetch();
    assertTrue(results.isEmpty());
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
    results = createGenericQuery().from(User.TABLE).where(User.HANDLE, equalTo("Brad")).fetch();
    assertEquals(1, results.size());
    assertEquals("Brad", results.get(0).getString(User.HANDLE));

    // Between
    results = createGenericQuery().from(User.TABLE).where(User.NUM_POSTS, between(4, 8)).fetch();
    assertEquals(1, results.size());
    assertEquals("James", results.get(0).getString(User.HANDLE));

    // Less Than
    results = createGenericQuery().from(User.TABLE).where(User.CREATED_AT_MILLIS, lessThan(2L)).fetch();
    assertEquals(2, results.size());
    for (QueryEntry entry : results) {
      assertTrue(entry.getString(User.HANDLE).equals("Brandon") || entry.getString(User.HANDLE).equals("Brad"));
    }

    // Greater Than
    results = createGenericQuery().from(User.TABLE).where(User.CREATED_AT_MILLIS, greaterThan(1L)).fetch();
    assertEquals(3, results.size());

    // Less Than Or Equal To
    results = createGenericQuery().from(User.TABLE).where(User.CREATED_AT_MILLIS, lessThanOrEqualTo(2L)).fetch();
    assertEquals(4, results.size());

    // Greater Than Or Equal To
    results = createGenericQuery().from(User.TABLE).where(User.CREATED_AT_MILLIS, greaterThanOrEqualTo(1L)).fetch();
    assertEquals(5, results.size());

    // Ends With
    results = createGenericQuery().from(User.TABLE).where(User.BIO, endsWith("er")).fetch();
    assertEquals(5, results.size());

    // StartsWith
    results = createGenericQuery().from(User.TABLE).where(User.BIO, startsWith("er")).fetch();
    assertTrue(results.isEmpty());

    // In with empty collection
    results = createGenericQuery().from(User.TABLE).where(User.SOME_DATETIME, in(Collections.<Long>emptySet()))
        .fetch();
    assertTrue(results.isEmpty());

    // NotIn with empty collection
    try {
      createGenericQuery().from(User.TABLE).where(User.SOME_DATETIME, notIn(Collections.<Long>emptySet())).fetch();
      fail("Using a NotIn operator with an empty collection should throw an exception.");
    } catch (IllegalArgumentException e) {
      //This is expected
    }

    // Contains and In
    results = createGenericQuery()
        .from(User.TABLE)
        .where(User.BIO, contains("f"))
        .and(User.NUM_POSTS, in(1, 3, 5))
        .fetch();
    assertEquals(1, results.size());
    assertEquals("James", results.get(0).getString(User.HANDLE));

    // Not In and Not Equal To
    results = createGenericQuery()
        .from(User.TABLE)
        .where(User.HANDLE, notIn("Brad", "Brandon", "Jennifer", "John"))
        .and(User.NUM_POSTS, notEqualTo(5))
        .fetch();
    assertEquals(1, results.size());
    assertEquals("Casey", results.get(0).getString(User.HANDLE));
    
    results = createGenericQuery().from(User.TABLE).where(User.SOME_DATETIME, isNull()).fetch();
    assertEquals(3, results.size());

    results = createGenericQuery().from(User.TABLE).where(User.SOME_DATETIME, isNotNull()).fetch();
    assertEquals(2, results.size());
    for (QueryEntry entry : results) {
      assertTrue(entry.getString(User.HANDLE).equals("Brandon") || entry.getString(User.HANDLE).equals("James"));
    }

    // If a null parameter is passed, an exception should be thrown
    try {
      createGenericQuery().from(User.TABLE).where(User.HANDLE, in(null, "brandon")).fetch();
      fail("an In query with one null parameter should throw an exception");
    } catch (IllegalArgumentException e) {
      // This exception is expected
    }
  }

  @Test
  public void testGetMethodsForNotNullFields() throws Exception {
    userA = users.create("A", datetime, 15, 2L, datetime, "Assembly Coder", new byte[]{(byte)3}, 1.1, 1.01, true);

    results = createGenericQuery()
        .from(User.TABLE)
        .select(User.ID, User.HANDLE, User.SOME_DECIMAL, User.SOME_DATETIME, User.NUM_POSTS, User.SOME_BOOLEAN, User.SOME_BINARY)
        .fetch();

    assertEquals(1, results.size());
    assertEquals(7, results.get(0).fieldCount());

    QueryEntry entry = results.get(0);
    assertTrue(entry.getLong(User.ID).equals(userA.getId()));
    assertTrue(entry.getIntFromLong(User.ID).equals(userA.getIntId()));
    assertTrue(entry.getString(User.HANDLE).equals(userA.getHandle()));
    assertTrue(entry.getDouble(User.SOME_DECIMAL).equals(userA.getSomeDecimal()));
    assertTrue(entry.getLong(User.SOME_DATETIME).equals(userA.getSomeDatetime()));
    assertTrue(entry.getInt(User.NUM_POSTS).equals(userA.getNumPosts()));
    assertTrue(entry.getBoolean(User.SOME_BOOLEAN).equals(userA.isSomeBoolean()));
    assertTrue(Arrays.toString(entry.getByteArray(User.SOME_BINARY)).equals(Arrays.toString(userA.getSomeBinary())));
  }

  @Test
  public void testGetMethodsForNullFields() throws Exception {
    userA = users.create("A", 15);

    results = createGenericQuery()
        .from(User.TABLE)
        .select(User.SOME_DECIMAL, User.SOME_DATETIME, User.SOME_BOOLEAN, User.SOME_BINARY)
        .fetch();

    assertEquals(1, results.size());
    assertEquals(4, results.get(0).fieldCount());

    QueryEntry entry = results.get(0);
    assertNull(entry.getDouble(User.SOME_DECIMAL));
    assertNull(entry.getLong(User.SOME_DATETIME));
    assertNull(entry.getBoolean(User.SOME_BOOLEAN));
    assertNull(entry.getByteArray(User.SOME_BINARY));
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

    results = createGenericQuery()
        .from(Comment.TABLE)
        .leftJoin(User.TABLE).on(User.ID, Comment.COMMENTER_ID)
        .leftJoin(Post.TABLE).on(Post.ID, Comment.COMMENTED_ON_ID)
        .orderBy(User.HANDLE)
        .orderBy(Post.TITLE, QueryOrder.DESC)
        .select(User.HANDLE, Comment.CONTENT, Post.TITLE)
        .fetch();

    assertEquals(4, results.size());
    assertEquals(3, results.get(0).fieldCount());

    // the result is: comment A, C, B, D
    QueryEntry entryForCommentA = results.get(0);
    assertEquals(commentA.getContent(), entryForCommentA.getString(Comment.CONTENT));
    assertEquals(userA.getHandle(), entryForCommentA.getString(User.HANDLE));
    assertEquals(postB.getTitle(), entryForCommentA.getString(Post.TITLE));

    QueryEntry entryForCommentC = results.get(1);
    assertEquals(commentC.getContent(), entryForCommentC.getString(Comment.CONTENT));
    assertEquals(userB.getHandle(), entryForCommentC.getString(User.HANDLE));
    assertEquals(postC.getTitle(), entryForCommentC.getString(Post.TITLE));

    QueryEntry entryForCommentB = results.get(2);
    assertEquals(commentB.getContent(), entryForCommentB.getString(Comment.CONTENT));
    assertEquals(userB.getHandle(), entryForCommentB.getString(User.HANDLE));
    assertEquals(postB.getTitle(), entryForCommentB.getString(Post.TITLE));

    QueryEntry entryForCommentD = results.get(3);
    assertEquals(commentD.getContent(), entryForCommentD.getString(Comment.CONTENT));
    assertEquals(userC.getHandle(), entryForCommentD.getString(User.HANDLE));
    assertEquals(postE.getTitle(), entryForCommentD.getString(Post.TITLE));
  }
}
