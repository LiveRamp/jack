package com.rapleaf.jack.queries;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.rapleaf.jack.test_project.DatabasesImpl;
import com.rapleaf.jack.test_project.database_1.IDatabase1;
import com.rapleaf.jack.test_project.database_1.models.Post;

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
  private static final int USER_ID_1 = 51;
  private static final int USER_ID_2 = 52;

  private Post post1;
  private Post post2;

  @Before
  public void prepare() throws Exception {
    db.deleteAll();
    db.setBulkOperation(false);
    post1 = db.posts().create();
    post1.setTitle(TITLE_1).setUserId(USER_ID_1).save();
    post2 = db.posts().create();
    post2.setTitle(TITLE_2).setUserId(USER_ID_2).save();
  }

  @Test
  public void testDeleteAllWithBulkOperation() throws Exception {
    db.setBulkOperation(true);
    Deletions deletions = db.createDeletion()
        .from(Post.TBL)
        .execute();

    Assert.assertEquals(2, deletions.getDeletedRowCount());
    assertEquals(0, db.posts().findAll().size());
  }

  @Test(expected = IllegalStateException.class)
  public void testDeleteAllWithoutBulkOperation() throws Exception {
    db.createDeletion().from(Post.TBL).execute();
  }

  @Test
  public void testWhereConstraint() throws Exception {
    Deletions deletions1 = db.createDeletion()
        .from(Post.TBL)
        .where(Post.TITLE.equalTo(TITLE_1))
        .execute();

    Assert.assertEquals(1, deletions1.getDeletedRowCount());
    assertEquals(1, db.posts().findAll().size());
    assertNull(db.posts().find(post1.getId()));
    assertNotNull(db.posts().find(post2.getId()));

    Deletions deletions2 = db.createDeletion()
        .from(Post.TBL)
        .where(Post.TITLE.equalTo(TITLE_1))
        .execute();

    Assert.assertEquals(0, deletions2.getDeletedRowCount());
    assertEquals(1, db.posts().findAll().size());
    assertNull(db.posts().find(post1.getId()));
    assertNotNull(db.posts().find(post2.getId()));

    Deletions deletions3 = db.createDeletion()
        .from(Post.TBL)
        .where(Post.TITLE.isNull())
        .execute();

    Assert.assertEquals(0, deletions3.getDeletedRowCount());
  }

}
