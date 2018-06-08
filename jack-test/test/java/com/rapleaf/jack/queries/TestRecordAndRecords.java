package com.rapleaf.jack.queries;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;

import com.rapleaf.jack.test_project.DatabasesImpl;
import com.rapleaf.jack.test_project.database_1.IDatabase1;
import com.rapleaf.jack.test_project.database_1.iface.IPostPersistence;
import com.rapleaf.jack.test_project.database_1.iface.IUserPersistence;
import com.rapleaf.jack.test_project.database_1.models.Comment;
import com.rapleaf.jack.test_project.database_1.models.Post;
import com.rapleaf.jack.test_project.database_1.models.User;
import com.rapleaf.jack.util.JackUtility;

import static com.rapleaf.jack.queries.QueryOrder.ASC;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class TestRecordAndRecords {
  private static final IDatabase1 db = new DatabasesImpl().getDatabase1();

  private static final double DELTA = 0.000001;

  private final IUserPersistence users = db.users();
  private final IPostPersistence posts = db.posts();

  private User userA, userB, userC;
  private Post post;
  private long date, datetime;
  private Records results;

  @Before
  public void prepare() throws Exception {
    users.deleteAll();
    posts.deleteAll();
    results = null;
    // mysql with version < 5.6.4 does not support nano second resolution
    datetime = Timestamp.valueOf("2015-03-20 14:23:00").getTime();
    date = JackUtility.DATE_TO_MILLIS.apply(LocalDate.parse("2015-04-16"));
  }

  @Test
  public void testGetMethodsForNotNullColumns() throws Exception {
    userA = users.create("A", datetime, 15, date, datetime, "Assembly Coder", new byte[]{(byte)3}, 1.1, 1.01, true);

    results = db.createQuery()
        .from(User.TBL)
        .select(User.ID, User.HANDLE, User.SOME_DECIMAL, User.SOME_DATE, User.SOME_DATETIME, User.NUM_POSTS, User.SOME_BOOLEAN, User.SOME_BINARY)
        .fetch();

    assertEquals(1, results.size());
    assertEquals(8, results.get(0).columnCount());

    Record record = results.get(0);
    assertTrue(record.getLong(User.ID).equals(userA.getId()));
    assertTrue(record.get(User.ID).equals(userA.getId()));

    assertTrue(record.getIntFromLong(User.ID).equals(userA.getIntId()));

    assertTrue(record.getString(User.HANDLE).equals(userA.getHandle()));
    assertTrue(record.get(User.HANDLE).equals(userA.getHandle()));

    assertTrue(record.getDouble(User.SOME_DECIMAL).equals(userA.getSomeDecimal()));
    assertTrue(record.get(User.SOME_DECIMAL).equals(userA.getSomeDecimal()));

    assertTrue(record.getLong(User.SOME_DATE).equals(userA.getSomeDate()));
    assertTrue(record.get(User.SOME_DATE).equals(userA.getSomeDate()));

    assertTrue(record.getLong(User.SOME_DATETIME).equals(userA.getSomeDatetime()));
    assertTrue(record.get(User.SOME_DATETIME).equals(userA.getSomeDatetime()));

    assertTrue(record.getInt(User.NUM_POSTS).equals(userA.getNumPosts()));
    assertTrue(record.get(User.NUM_POSTS).equals(userA.getNumPosts()));

    assertTrue(record.getBoolean(User.SOME_BOOLEAN).equals(userA.isSomeBoolean()));
    assertTrue(record.get(User.SOME_BOOLEAN).equals(userA.isSomeBoolean()));

    assertArrayEquals(userA.getSomeBinary(), record.getByteArray(User.SOME_BINARY));
    assertArrayEquals(userA.getSomeBinary(), record.get(User.SOME_BINARY));
  }

  @Test
  public void testGetMethodsForNullColumns() throws Exception {
    userA = users.create("A", 15);

    results = db.createQuery()
        .from(User.TBL)
        .select(User.SOME_DECIMAL, User.SOME_DATETIME, User.SOME_BOOLEAN, User.SOME_BINARY)
        .fetch();

    assertEquals(1, results.size());
    assertEquals(4, results.get(0).columnCount());

    Record record = results.get(0);
    assertNull(record.getDouble(User.SOME_DECIMAL));
    assertNull(record.get(User.SOME_DECIMAL));

    assertNull(record.getLong(User.SOME_DATETIME));
    assertNull(record.get(User.SOME_DATETIME));

    assertNull(record.getBoolean(User.SOME_BOOLEAN));
    assertNull(record.get(User.SOME_BOOLEAN));

    assertNull(record.getByteArray(User.SOME_BINARY));
    assertNull(record.get(User.SOME_BINARY));
  }

  @Test
  public void testGetMethodsForRecordsWithNullValues() throws Exception {
    userA = users.create("A", datetime, 1, 2L, null, null, new byte[]{(byte)3}, 1.1, null, true);
    userB = users.create("B", datetime + 3, 2, 4L, null, null, new byte[]{(byte)4}, 1.2, null, false);
    userC = users.create("C", datetime - 10, 3, 6L, null, null, new byte[]{(byte)5}, 1.3, null, true);
    userA.save();
    userB.save();
    userC.save();

    results = db.createQuery()
        .from(User.TBL)
        .fetch();

    assertEquals(
        Lists.newArrayList(userA.getId(), userB.getId(), userC.getId()),
        results.getLongs(User.ID)
    );
    assertEquals(
        Lists.newArrayList(userA.getId(), userB.getId(), userC.getId()),
        results.gets(User.ID)
    );

    assertEquals(
        Lists.newArrayList((int)userA.getId(), (int)userB.getId(), (int)userC.getId()),
        results.getIntsFromLongs(User.ID)
    );

    assertEquals(
        Lists.newArrayList(1, 2, 3),
        results.getInts(User.NUM_POSTS)
    );
    assertEquals(
        Lists.newArrayList(1, 2, 3),
        results.gets(User.NUM_POSTS)
    );

    assertEquals(
        Lists.newArrayList("A", "B", "C"),
        results.getStrings(User.HANDLE)
    );
    assertEquals(
        Lists.newArrayList("A", "B", "C"),
        results.gets(User.HANDLE)
    );

    List<byte[]> byteArrays = results.getByteArrays(User.SOME_BINARY);
    assertArrayEquals(new byte[]{(byte)3}, byteArrays.get(0));
    assertArrayEquals(new byte[]{(byte)4}, byteArrays.get(1));
    assertArrayEquals(new byte[]{(byte)5}, byteArrays.get(2));
    byteArrays = results.gets(User.SOME_BINARY);
    assertArrayEquals(new byte[]{(byte)3}, byteArrays.get(0));
    assertArrayEquals(new byte[]{(byte)4}, byteArrays.get(1));
    assertArrayEquals(new byte[]{(byte)5}, byteArrays.get(2));

    List<Double> doubles = results.getDoubles(User.SOME_FLOAT);
    assertEquals(1.1, doubles.get(0), DELTA);
    assertEquals(1.2, doubles.get(1), DELTA);
    assertEquals(1.3, doubles.get(2), DELTA);
    doubles = results.gets(User.SOME_FLOAT);
    assertEquals(1.1, doubles.get(0), DELTA);
    assertEquals(1.2, doubles.get(1), DELTA);
    assertEquals(1.3, doubles.get(2), DELTA);

    assertEquals(Lists.newArrayList(true, false, true), results.getBooleans(User.SOME_BOOLEAN));
    assertEquals(Lists.newArrayList(true, false, true), results.gets(User.SOME_BOOLEAN));

    assertEquals(
        Lists.newArrayList(null, null, null),
        results.getLongs(User.SOME_DATETIME)
    );
    assertEquals(
        Lists.newArrayList(null, null, null),
        results.gets(User.SOME_DATETIME)
    );

    assertEquals(
        Lists.newArrayList(null, null, null),
        results.getStrings(User.BIO)
    );
    assertEquals(
        Lists.newArrayList(null, null, null),
        results.gets(User.BIO)
    );

    assertEquals(
        Lists.newArrayList(null, null, null),
        results.getDoubles(User.SOME_DECIMAL)
    );
    assertEquals(
        Lists.newArrayList(null, null, null),
        results.gets(User.SOME_DECIMAL)
    );
  }

  @Test
  public void testModelAndAttributeFromRecord() throws Exception {
    userA = users.create("A", datetime, 1, date, datetime, "Assembly Coder", new byte[]{(byte)1, (byte)2, (byte)3}, 1.1, 1.01, true);
    post = posts.create("Post A from User A", date, userA.getIntId(), datetime);
    Record record = db.createQuery()
        .from(User.TBL)
        .innerJoin(Post.TBL).on(Post.USER_ID.equalTo(User.ID.as(Integer.class)))
        .orderBy(User.SOME_DATETIME, ASC)
        .fetch()
        .get(0);
    User.Attributes userAttrLhs = userA.getAttributes();
    User.Attributes userAttrRhs = record.getAttributes(User.TBL);

    assertEquals(userAttrLhs.getId(), userAttrRhs.getId());
    assertEquals(userAttrLhs.getHandle(), userAttrRhs.getHandle());
    assertEquals(userAttrLhs.getCreatedAtMillis(), userAttrRhs.getCreatedAtMillis());
    assertEquals(userAttrLhs.getSomeDate(), userAttrRhs.getSomeDate());
    assertEquals(userAttrLhs.getSomeDatetime(), userAttrRhs.getSomeDatetime());
    assertEquals(userAttrLhs.getBio(), userAttrRhs.getBio());
    assertArrayEquals(userAttrLhs.getSomeBinary(), userAttrRhs.getSomeBinary());
    assertEquals(userAttrLhs.getSomeFloat(), userAttrRhs.getSomeFloat(), DELTA);
    assertEquals(userAttrLhs.getSomeDecimal(), userAttrRhs.getSomeDecimal());
    assertEquals(userAttrLhs.isSomeBoolean(), userAttrRhs.isSomeBoolean());

    Post.Attributes postAttrLhs = post.getAttributes();
    Post.Attributes postAttrRhs = record.getAttributes(Post.TBL);
    assertEquals(postAttrLhs.getId(), postAttrRhs.getId());
    assertEquals(postAttrLhs.getTitle(), postAttrRhs.getTitle());
    assertEquals(postAttrLhs.getPostedAtMillis(), postAttrRhs.getPostedAtMillis());
    assertEquals(postAttrLhs.getUserId(), postAttrRhs.getUserId());
    assertEquals(postAttrLhs.getUpdatedAt(), postAttrRhs.getUpdatedAt());

    Comment.Attributes commentAttr = record.getAttributes(Comment.TBL);
    assertNull(commentAttr);

    User modelFromRecord = record.getModel(User.TBL, db.getDatabases());
    String newHandle = "new handle";
    modelFromRecord.setHandle(newHandle).save();
    assertEquals(users.find(modelFromRecord.getId()).getHandle(), newHandle);

    Comment comment = record.getModel(Comment.TBL, db.getDatabases());
    assertNull(comment);
  }
}
