package com.rapleaf.jack.queries;

import java.io.IOException;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;

import com.rapleaf.jack.test_project.DatabasesImpl;
import com.rapleaf.jack.test_project.database_1.IDatabase1;
import com.rapleaf.jack.test_project.database_1.models.Post;
import com.rapleaf.jack.test_project.database_1.models.User;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class TestGenericInsertion {
  private static final IDatabase1 db = new DatabasesImpl().getDatabase1();
  static {
    db.disableCaching();
  }

  private static final String HANDLE = "HANDLE";
  private static final long CREATED_AT = System.currentTimeMillis();
  private static final int NUM_POSTS = 91;
  private static final long SOME_DATE = Date.valueOf("2014-05-10").getTime();
  private static final long SOME_DATE_TIME = Timestamp.valueOf("2014-05-10 07:00:00").getTime();
  private static final String BIO = "BIO";
  private static final byte[] SOME_BINARY = "binary".getBytes();
  private static final float SOME_FLOAT = 9.1F;
  private static final double SOME_DOUBLE = 9.1;
  private static final boolean SOME_BOOLEAN = true;

  @Before
  public void prepare() throws Exception {
    db.deleteAll();
  }

  @Test
  public void testRecordCreation() throws Exception {
    Insertions insertions = db.createInsertion()
        .into(User.TBL)
        .set(User.HANDLE, HANDLE)
        .set(User.CREATED_AT_MILLIS, CREATED_AT)
        .set(User.NUM_POSTS, NUM_POSTS)
        .set(User.SOME_DATE, SOME_DATE)
        .set(User.SOME_DATETIME, SOME_DATE_TIME)
        .set(User.BIO, "BIO")
        .set(User.SOME_BINARY, SOME_BINARY)
        .set(User.SOME_FLOAT, SOME_DOUBLE)
        .set(User.SOME_DECIMAL, SOME_DOUBLE)
        .set(User.SOME_BOOLEAN, SOME_BOOLEAN)
        .execute();

    User user = db.users().find(insertions.getFirstId());
    assertNotNull(user);
    assertEquals(user.getHandle(), HANDLE);
    assertEquals(user.getCreatedAtMillis().longValue(), CREATED_AT);
    assertEquals(user.getNumPosts(), NUM_POSTS);
    assertEquals(user.getSomeDate().longValue(), SOME_DATE);
    assertEquals(user.getSomeDatetime().longValue(), SOME_DATE_TIME);
    assertEquals(user.getBio(), BIO);
    assertArrayEquals(user.getSomeBinary(), SOME_BINARY);
    assertTrue(Math.abs(user.getSomeFloat() - SOME_DOUBLE) < 10e-5);
    assertTrue(Math.abs(user.getSomeDecimal() - SOME_DOUBLE) < 10e-5);
    assertEquals(user.isSomeBoolean(), SOME_BOOLEAN);
  }

  @Test
  public void testMultipleRows() throws Exception {
    String handle1 = "a";
    String handle2 = "b";
    int numPosts1 = 51;
    int numPosts2 = 52;

    Insertions insertions = db.createInsertion()
        .into(User.TBL)
        .set(User.HANDLE, handle1, handle2)
        .set(User.NUM_POSTS, numPosts1, numPosts2)
        .execute();

    long id1 = insertions.getFirstId();
    User user1 = db.users().find(id1);
    assertEquals(handle1, user1.getHandle());
    assertEquals(numPosts1, user1.getNumPosts());

    long id2 = insertions.getIds().get(1);
    User user2 = db.users().find(id2);
    assertEquals(handle2, user2.getHandle());
    assertEquals(numPosts2, user2.getNumPosts());
  }

  @Test
  public void testColumnCast() throws Exception {
    Insertions insertions = db.createInsertion()
        .into(User.TBL)
        .set(User.HANDLE, HANDLE)
        .set(User.NUM_POSTS.as(Long.class), (long)NUM_POSTS)
        .set(User.SOME_FLOAT.as(Float.class), SOME_FLOAT)
        .execute();

    User user = db.users().find(insertions.getFirstId());
    assertNotNull(user);
    assertEquals(user.getNumPosts(), NUM_POSTS);
    assertTrue(Math.abs(user.getSomeFloat().floatValue() - SOME_FLOAT) < 10e-5);
  }

  @Test
  public void testNullValue() throws Exception {
    Insertions insertions = db.createInsertion()
        .into(User.TBL)
        .set(User.HANDLE, HANDLE)
        .set(User.NUM_POSTS, NUM_POSTS)
        .set(User.BIO, null)
        .set(User.SOME_FLOAT, null)
        .set(User.SOME_BOOLEAN, null)
        .execute();

    User user = db.users().find(insertions.getFirstId());
    assertNotNull(user);
    assertNull(user.getBio());
    assertNull(user.getSomeFloat());
    assertNull(user.isSomeBoolean());
  }

  @Test
  public void testDuplicatedSet() throws Exception {
    String handle = HANDLE + "2";

    Insertions insertions = db.createInsertion()
        .into(User.TBL)
        .set(User.NUM_POSTS, NUM_POSTS)
        .set(User.HANDLE, HANDLE)
        .set(User.HANDLE, handle)
        .execute();

    User user = db.users().find(insertions.getFirstId());
    assertNotNull(user);
    assertEquals(user.getHandle(), handle);
  }

  @Test
  public void testNullOnly() throws Exception {
    List<String> nulls = Lists.newArrayList(null, null, null);
    db.createInsertion()
        .into(Post.TBL)
        .set(Post.TITLE, nulls)
        .execute();
    assertEquals(nulls.size(), db.posts().findAll().size());
  }

  @Test
  public void testEmptyValues() throws Exception {
    db.createInsertion()
        .into(Post.TBL)
        .execute();
    assertEquals(1, db.posts().findAll().size());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEmptyValueList() throws Exception {
    db.createInsertion()
        .into(Post.TBL)
        .set(Post.TITLE, Lists.newArrayList())
        .execute();
  }

  @Test(expected = IOException.class)
  public void testMissingRequiredField() throws Exception {
    db.createInsertion()
        .into(User.TBL)
        .set(User.NUM_POSTS, NUM_POSTS)
        .execute();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInconsistentRowCount() throws Exception {
    db.createInsertion()
        .into(User.TBL)
        .set(User.HANDLE, "a", "b")
        .set(User.NUM_POSTS, 1, 2, 3)
        .execute();
  }

}
