package com.rapleaf.jack.queries;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.rapleaf.jack.exception.BulkOperationException;
import com.rapleaf.jack.test_project.DatabasesImpl;
import com.rapleaf.jack.test_project.database_1.IDatabase1;
import com.rapleaf.jack.test_project.database_1.models.Post;
import com.rapleaf.jack.test_project.database_1.models.User;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class TestGenericDeletion {

  private static final IDatabase1 db = new DatabasesImpl().getDatabase1();

  static {
    db.disableCaching();
  }

  private static final String TITLE_1 = "title1";
  private static final String TITLE_2 = "title2";
  private static final String TITLE_3 = "title3";
  private static final int USER_ID_1 = 51;
  private static final int USER_ID_2 = 52;
  private static final int USER_ID_3 = 53;

  private Post post1;
  private Post post2;
  private Post post3;

  @Before
  public void prepare() throws Exception {
    db.deleteAll();
    db.setBulkOperation(false);
    post1 = db.posts().create();
    post1.setTitle(TITLE_1).setUserId(USER_ID_1).save();
    post2 = db.posts().create();
    post2.setTitle(TITLE_2).setUserId(USER_ID_2).save();
    post3 = db.posts().create();
    post3.setTitle(TITLE_3).setUserId(USER_ID_3).save();
  }

  @Test
  public void testDeleteAllWithBulkOperation() throws Exception {
    db.setBulkOperation(true);
    Deletions deletions = db.createDeletion()
        .from(Post.TBL)
        .execute();

    assertEquals(2, deletions.getDeletedRowCount());
    assertEquals(0, db.posts().findAll().size());
  }

  @Test(expected = BulkOperationException.class)
  public void testDeleteAllWithoutBulkOperation() throws Exception {
    db.createDeletion().from(Post.TBL).execute();
  }

  @Test
  public void testWhereConstraint() throws Exception {
    Deletions deletions1 = db.createDeletion()
        .from(Post.TBL)
        .where(Post.TITLE.equalTo(TITLE_1))
        .execute();

    assertEquals(1, deletions1.getDeletedRowCount());
    assertEquals(1, db.posts().findAll().size());
    assertNull(db.posts().find(post1.getId()));
    assertNotNull(db.posts().find(post2.getId()));

    Deletions deletions2 = db.createDeletion()
        .from(Post.TBL)
        .where(Post.TITLE.equalTo(TITLE_1))
        .execute();

    assertEquals(0, deletions2.getDeletedRowCount());
    assertEquals(1, db.posts().findAll().size());
    assertNull(db.posts().find(post1.getId()));
    assertNotNull(db.posts().find(post2.getId()));

    Deletions deletions3 = db.createDeletion()
        .from(Post.TBL)
        .where(Post.TITLE.isNull())
        .execute();

    assertEquals(0, deletions3.getDeletedRowCount());
  }

  @Test
  public void testDeleteWithJoin() throws Exception {
    User userA = db.users().create("A", 1);
    User userB = db.users().create("B", 2);
    User userC = db.users().create("C", 3);

    post1.setUserId(userA.getIntId()).save();
    post2.setUserId(userB.getIntId()).save();
    post3.setUserId(userC.getIntId()).save();

    // delete post2 and post3
    Deletions deletions = db.createDeletion()
        .from(Post.TBL)
        .innerJoin(User.TBL)
        .on(User.ID.as(Integer.class).equalTo(Post.USER_ID))
        .where(User.NUM_POSTS.greaterThan(1))
        .execute();

    // posts should have been deleted correctly
    assertEquals(2, deletions.getDeletedRowCount());
    List<Post> remainingPosts = db.posts().findAll();

    assertEquals(1, remainingPosts.size());
    assertEquals(post1, remainingPosts.get(0));

    // users should not have been deleted
    List<User> remainingUsers = db.users().findAll();
    assertEquals(3, remainingUsers.size());
    assertEquals(userA, remainingUsers.get(0));
    assertEquals(userB, remainingUsers.get(1));
    assertEquals(userC, remainingUsers.get(2));
  }

}
