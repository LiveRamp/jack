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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * This test runs all test cases from the superclass on the real (not mock) models. Do no put any
 * test cases here unless you have a really good reason to do so.
 */
public class TestAbstractDatabaseModel extends BaseDatabaseModelTestCase {
  private static final String DB_NAME_KEY = "database1";
  private static final LoggingMysqlConnection DATABASE_CONNECTION1 = new LoggingMysqlConnection(DB_NAME_KEY);

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

  @Test
  public void testIgnoreNullWhenCreatingModel() throws IOException {
    DATABASE_CONNECTION1.clearPreparedStatements();
    User user1 = dbs.getDatabase1().users().create("handle", null, 100, null, null, null, null, null, null, null);
    User user2 = dbs.getDatabase1().users().create("handle", 100);

    // check insertion statement
    String expectedStatement = "INSERT INTO users (`handle`, `num_posts`) VALUES(?, ?);";
    List<String> actualStatements = DATABASE_CONNECTION1.getPreparedStatements();
    assertEquals(2, actualStatements.size());
    assertEquals(expectedStatement, actualStatements.get(0));
    assertEquals(expectedStatement, actualStatements.get(1));

    // check model
    assertEquals("handle", user1.getHandle());
    assertNull(user1.getCreatedAtMillis());
    assertEquals("handle", user2.getHandle());
    assertNull(user2.getCreatedAtMillis());
  }

  @Test
  public void testIgnoreNullWhenSavingNewModel() throws IOException {
    DATABASE_CONNECTION1.clearPreparedStatements();
    User user = new User(1000L, "handle", null, 100, null, null, null, null, null, null, null);
    dbs.getDatabase1().users().save(user);

    // check insertion statement
    String expectedStatement = "INSERT INTO users (`handle`, `num_posts` , id) VALUES(?, ?, ?);";
    List<String> actualStatements = DATABASE_CONNECTION1.getPreparedStatements();
    assertEquals(1, actualStatements.size());
    assertEquals(expectedStatement, actualStatements.get(0));

    // check model
    assertEquals("handle", user.getHandle());
    assertNull(user.getCreatedAtMillis());
  }

  @Test
  public void testIncludeNullWhenUpdatingExistingModel() throws IOException {
    User user = dbs.getDatabase1().users().create("handle", 100);
    DATABASE_CONNECTION1.clearPreparedStatements();
    dbs.getDatabase1().users().save(user);

    List<String> actualStatements = DATABASE_CONNECTION1.getPreparedStatements();
    assertEquals(1, actualStatements.size());
    // Field created_at_millis is null, but it should be included in the save statement
    assertNull(user.getCreatedAtMillis());
    assertTrue(actualStatements.get(0).contains("created_at_millis"));
  }

}
