package com.rapleaf.jack;

import java.util.Arrays;
import java.util.List;

import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;

import com.rapleaf.jack.queries.GenericQuery;
import com.rapleaf.jack.queries.QueryEntry;
import com.rapleaf.jack.test_project.DatabasesImpl;
import com.rapleaf.jack.test_project.IDatabases;
import com.rapleaf.jack.test_project.database_1.iface.ICommentPersistence;
import com.rapleaf.jack.test_project.database_1.iface.IPostPersistence;
import com.rapleaf.jack.test_project.database_1.iface.IUserPersistence;
import com.rapleaf.jack.test_project.database_1.models.Comment;
import com.rapleaf.jack.test_project.database_1.models.Post;
import com.rapleaf.jack.test_project.database_1.models.User;

import static org.junit.Assert.*;

public class TestGenericQuery {

  private static final DatabaseConnection DATABASE_CONNECTION1 = new DatabaseConnection("database1");
  private static final IDatabases dbs = new DatabasesImpl(DATABASE_CONNECTION1);
  private static final GenericQuery genericQuery = GenericQuery.create(DATABASE_CONNECTION1);

  private static final IUserPersistence users = dbs.getDatabase1().users();
  private static final ICommentPersistence comments = dbs.getDatabase1().comments();
  private static final IPostPersistence posts = dbs.getDatabase1().posts();

  private User userA, userB, userC, userD, userE;
  private Post postA, postB, postC, postD, postE;
  private Comment commentA, commentB, commentC, commentD;
  private long datetime;
  private List<QueryEntry> results;

  @Before
  public void prepare() throws Exception {
    users.deleteAll();
    comments.deleteAll();
    posts.deleteAll();
    results = Lists.newArrayList();
    datetime = System.currentTimeMillis() % 1000 * 1000;  // sql timestamp does not support nano resolution
  }

  @Test
  public void testBasicQuery() throws Exception {
    int userRecordCount = 5;
    for (int i = 0; i < userRecordCount; ++i) {
      users.createDefaultInstance();
    }

    // query with no select clause should return an empty list
    results = genericQuery.from(User.class).fetch();
    assertTrue(results.isEmpty());

    // query with only select clause should return all records with the specified field
    results = genericQuery.from(User.class).select(User.ID).fetch();
    assertEquals(userRecordCount, results.size());
  }

  @Test
  public void testGetMethodsForNotNullFields() throws Exception {
    userA = users.create("A", datetime, 15, 2L, datetime, "Assembly Coder", new byte[]{(byte)3}, 1.1, 1.01, true);

    results = genericQuery
        .from(User.class)
        .select(User.ID, User.HANDLE, User.SOME_DECIMAL, User.SOME_DATETIME, User.NUM_POSTS, User.SOME_BOOLEAN, User.SOME_BINARY)
        .fetch();

    assertEquals(1, results.size());

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

    results = genericQuery
        .from(User.class)
        .select(User.SOME_DECIMAL, User.SOME_DATETIME, User.SOME_BOOLEAN, User.SOME_BINARY)
        .fetch();

    assertEquals(1, results.size());

    QueryEntry entry = results.get(0);

    assertNull(entry.getDouble(User.SOME_DECIMAL));
    assertNull(entry.getLong(User.SOME_DATETIME));
    assertNull(entry.getBoolean(User.SOME_BOOLEAN));
    assertNull(entry.getByteArray(User.SOME_BINARY));
  }

  public void testJoinQuery() throws Exception {
    userA = users.create("A", datetime, 0, 2L, datetime, "Assembly Coder", new byte[]{(byte)3}, 1.1, 1.01, true);
    userB = users.create("B", datetime, 0, 1L, datetime, "Byline Editor", new byte[]{(byte)1}, 2.2, 2.02, true);
    userC = users.create("C", datetime, 0, 4L, datetime, "Code Refactor", new byte[]{(byte)2}, 2.2, 2.02, false);

    postA = posts.create("Post from User A", datetime, userA.getIntId(), datetime);
    postB = posts.create("Post from User B", datetime, userB.getIntId(), datetime);
    postC = posts.create("Post from User B", datetime, userB.getIntId(), datetime);
    postD = posts.create("Post from User C", datetime, userC.getIntId(), datetime);
    postE = posts.create("Post from User C", datetime, userC.getIntId(), datetime);

    commentA = comments.create("Comment on Post B from User A", userA.getIntId(), postB.getIntId(), datetime);
    commentB = comments.create("Comment on Post B from User B", userB.getIntId(), postB.getIntId(), datetime);
    commentC = comments.create("Comment on Post C from User C", userC.getIntId(), postC.getIntId(), datetime);
    commentD = comments.create("Comment on Post E from User C", userC.getIntId(), postE.getIntId(), datetime);
  }
}