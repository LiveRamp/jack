package com.rapleaf.jack;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

import com.rapleaf.jack.test_project.DatabasesImpl;
import com.rapleaf.jack.test_project.IDatabases;
import com.rapleaf.jack.test_project.database_1.iface.ICommentPersistence;
import com.rapleaf.jack.test_project.database_1.iface.ILockableModelPersistence;
import com.rapleaf.jack.test_project.database_1.iface.IUserPersistence;
import com.rapleaf.jack.test_project.database_1.models.Comment;
import com.rapleaf.jack.test_project.database_1.models.LockableModel;
import com.rapleaf.jack.test_project.database_1.models.User;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * This test runs all test cases from the superclass on the real (not mock) models. Do no put any
 * test cases here unless you have a really good reason to do so.
 */
public class TestAbstractDatabaseModel extends BaseDatabaseModelTestCase {
  private static final DatabaseConnection DATABASE_CONNECTION1 = new MysqlDatabaseConnection("database1");

  @Override
  public IDatabases getDBS() {
    return new DatabasesImpl(DATABASE_CONNECTION1);
  }

  @Test
  public void testIsEmpty() throws IOException {
    ICommentPersistence comments = dbs.getDatabase1().comments();
    assertTrue(comments.isEmpty());

    comments.create("comment", 1, 1, 1);
    assertFalse(comments.isEmpty());
  }

  @Test
  public void testFindAllByForeignKeyCache() throws Exception {
    ICommentPersistence comments = dbs.getDatabase1().comments();
    int userId = 1;
    comments.create("comment1", userId, 1, 1);
    comments.create("comment2", userId, 1, 1);
    comments.create("comment3", userId, 1, 1);

    List<Comment> c1 = comments.findAllByForeignKey("commenter_id", userId);
    List<Comment> c2 = comments.findAllByForeignKey("commenter_id", userId);
    assertTrue(c1 == c2);
  }

  @Test
  public void testSetAutoCommit() {
    assertTrue("auto-commit should default to true", dbs.getDatabase1().getAutoCommit());
    dbs.getDatabase1().setAutoCommit(false);

    try {
      assertFalse("auto-commit should be false after being set so", dbs.getDatabase1().getAutoCommit());
    } finally {
      dbs.getDatabase1().setAutoCommit(true);
    }
  }

  @Test
  public void testTransactionRollback() throws IOException {
    dbs.getDatabase1().setAutoCommit(false);

    ICommentPersistence comments = dbs.getDatabase1().comments();
    try {
      comments.create("comment1", 1, 1, 1);
      assertEquals("There should be 1 record during transaction", 1, comments.findAll().size());

      dbs.getDatabase1().rollback();

      assertEquals("There should be 0 records after rollback", 0, comments.findAll().size());
    } finally {
      dbs.getDatabase1().rollback();
      dbs.getDatabase1().setAutoCommit(true);
    }
  }

  @Test
  public void testOptimisticLockingHasNoEffectWhileCaching() throws IOException {
    final IUserPersistence users = dbs.getDatabase1().users();
    final User user = users.create("handle1", 1);
    user.setCreatedAtMillis(1L);
    if (!user.save()) {
      fail("Failed to setup test properly");
    }

    final User user1 = users.find(user.getId());
    final User user2 = users.find(user.getId());

    user1.setCreatedAtMillis(2L);
    user1.save();

    user2.setCreatedAtMillis(3L);
    user2.save();

    final User finalUser = users.find(user.getId());

    assertEquals(3L, finalUser.getCreatedAtMillis().longValue());

    final ILockableModelPersistence lockableModels = dbs.getDatabase1().lockableModels();
    final LockableModel lockableModel = lockableModels.createDefaultInstance();
    lockableModel.setMessage("Original");
    if (!lockableModel.save()) {
      fail("Failed to setup test properly");
    }

    final LockableModel lockableModel1 = lockableModels.find(lockableModel.getId());
    final LockableModel lockableModel2 = lockableModels.find(lockableModel.getId());

    lockableModel1.setMessage("First");
    lockableModel2.setMessage("Second");
    final boolean firstSuccess = lockableModel1.save();
    final boolean secondSuccess = lockableModel2.save();

    final LockableModel finalLockableModel = lockableModels.find(lockableModel.getId());

    assertTrue(firstSuccess);
    assertTrue(secondSuccess);
    assertEquals("Second", finalLockableModel.getMessage());
  }

  @Test
  public void testOptimisticLockingShouldNotAffectNonLockableModels() throws IOException {
    final IDatabases dbs = getDBS(); // new instance since caching will affect results
    dbs.getDatabase1().disableCaching();
    final IUserPersistence users = dbs.getDatabase1().users();
    final User user = users.create("handle1", 1);
    user.setCreatedAtMillis(1L);
    if (!user.save()) {
      fail("Failed to setup test properly");
    }

    final User user1 = users.find(user.getId());
    final User user2 = users.find(user.getId());

    user1.setCreatedAtMillis(2L);
    user1.save();

    user2.setCreatedAtMillis(3L);
    user2.save();

    final User finalUser = users.find(user.getId());

    assertEquals(3L, finalUser.getCreatedAtMillis().longValue());
  }

  @Test
  public void testOptimisticLockingShouldPreventClobberingLockableModels() throws IOException {
    final IDatabases dbs = getDBS(); // new instance since caching will affect results
    dbs.getDatabase1().disableCaching();
    final ILockableModelPersistence lockableModels = dbs.getDatabase1().lockableModels();
    final LockableModel lockableModel = lockableModels.createDefaultInstance();
    lockableModel.setMessage("Original");
    if (!lockableModel.save()) {
      fail("Failed to setup test properly");
    }

    final LockableModel lockableModel1 = lockableModels.find(lockableModel.getId());
    final LockableModel lockableModel2 = lockableModels.find(lockableModel.getId());

    lockableModel1.setMessage("First");
    lockableModel2.setMessage("Second");
    final boolean firstSuccess = lockableModel1.save();

    final LockableModel lockableModel2Copy = lockableModel2.getCopy();
    final boolean secondSuccess = lockableModel2.save();

    final LockableModel finalLockableModel = lockableModels.find(lockableModel.getId());

    assertTrue(firstSuccess); // first should succeed
    assertFalse(secondSuccess); // second should fail
    assertEquals("First", finalLockableModel.getMessage()); // first success should have taken effect
    assertEquals(lockableModel2Copy, lockableModel2); // second should be unaffected by saveAttempt
  }
}
