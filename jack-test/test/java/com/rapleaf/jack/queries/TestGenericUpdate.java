package com.rapleaf.jack.queries;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import com.rapleaf.jack.exception.BulkOperationException;
import com.rapleaf.jack.test_project.DatabasesImpl;
import com.rapleaf.jack.test_project.database_1.IDatabase1;
import com.rapleaf.jack.test_project.database_1.models.Post;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class TestGenericUpdate {
  private static final IDatabase1 db = new DatabasesImpl().getDatabase1();

  static {
    db.disableCaching();
  }

  private static final String TITLE_1 = "title1";
  private static final String TITLE_2 = "title2";
  private static final int USER_ID_1 = 51;
  private static final int USER_ID_2 = 52;
  private static final long DATE = DateTime.parse("2017-08-17").getMillis();

  private Post post1;
  private Post post2;

  @Before
  public void prepare() throws Exception {
    db.deleteAll();
    db.setBulkOperation(false);
    post1 = db.posts().create();
    post1.setTitle(TITLE_1).setUserId(USER_ID_1).setPostedAtMillis(DATE).save();
    post2 = db.posts().create();
    post2.setTitle(TITLE_2).setUserId(USER_ID_2).save();
  }

  @Test
  public void testUpdateAllWithBulkOperation() throws Exception {
    db.setBulkOperation(true);
    Updates updates = db.createUpdate()
        .table(Post.TBL)
        .set(Post.USER_ID, USER_ID_1 * 10)
        .execute();

    assertEquals(2, updates.getUpdatedRowCount());
    assertEquals(USER_ID_1 * 10, db.posts().find(post1.getId()).getUserId().longValue());
    assertEquals(USER_ID_1 * 10, db.posts().find(post2.getId()).getUserId().longValue());
  }

  @Test(expected = BulkOperationException.class)
  public void testUpdateAllWithoutBulkOperation() throws Exception {
    db.createUpdate()
        .table(Post.TBL)
        .set(Post.USER_ID, USER_ID_1 * 10)
        .execute();
  }

  @Test
  public void testUpdateDate() throws Exception {
    long newDate = DateTime.parse("2017-09-10").getMillis();
    assertEquals(DATE, db.posts().find(post1.getId()).getPostedAtMillis().longValue());
    Updates updates = db.createUpdate()
        .table(Post.TBL)
        .set(Post.POSTED_AT_MILLIS, newDate)
        .where(Post.ID.equalTo(post1.getId()))
        .execute();

    assertEquals(1, updates.getUpdatedRowCount());
    assertEquals(newDate, db.posts().find(post1.getId()).getPostedAtMillis().longValue());
  }

  @Test
  public void testWhereConstraint() throws Exception {
    Updates updates = db.createUpdate()
        .table(Post.TBL)
        .set(Post.USER_ID, USER_ID_1 * 10)
        .set(Post.TITLE, TITLE_1 + TITLE_2)
        .where(Post.TITLE.equalTo(TITLE_1))
        .execute();

    assertEquals(1, updates.getUpdatedRowCount());
    assertEquals(USER_ID_2, db.posts().find(post2.getId()).getUserId().longValue());
    assertEquals(TITLE_2, db.posts().find(post2.getId()).getTitle());
    assertEquals(USER_ID_1 * 10, db.posts().find(post1.getId()).getUserId().longValue());
    assertEquals(TITLE_1 + TITLE_2, db.posts().find(post1.getId()).getTitle());
  }

  @Test
  public void testNullValue() throws Exception {
    Updates updates = db.createUpdate()
        .table(Post.TBL)
        .set(Post.TITLE, null)
        .where(Post.USER_ID.equalTo(USER_ID_1))
        .execute();

    assertEquals(1, updates.getUpdatedRowCount());
    assertNull(db.posts().find(post1.getId()).getTitle());
  }

}
