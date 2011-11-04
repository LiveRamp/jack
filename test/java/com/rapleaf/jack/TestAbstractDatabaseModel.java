package com.rapleaf.jack;

import java.io.IOException;
import java.util.Set;

import com.rapleaf.jack.test_project.DatabasesImpl;
import com.rapleaf.jack.test_project.IDatabases;
import com.rapleaf.jack.test_project.database_1.iface.ICommentPersistence;
import com.rapleaf.jack.test_project.database_1.models.Comment;

/**
 * This test runs all test cases from the superclass on the real (not mock) models. Do no put any
 * test cases here unless you have a really good reason to do so.
 */
public class TestAbstractDatabaseModel extends BaseDatabaseModelTestCase {
  private static final DatabaseConnection DATABASE_CONNECTION1 = new DatabaseConnection("database1");
  
  @Override
  public IDatabases getDBS() {
    return new DatabasesImpl(DATABASE_CONNECTION1);
  }

  public void testFindAllByForeignKeyCache() throws Exception {
    ICommentPersistence comments = dbs.getDatabase1().comments();
    int userId = 1;
    comments.create("comment1", userId, 1, 1);
    comments.create("comment2", userId, 1, 1);
    comments.create("comment3", userId, 1, 1);

    Set<Comment> c1 = comments.findAllByForeignKey("commenter_id", userId);
    Set<Comment> c2 = comments.findAllByForeignKey("commenter_id", userId);
    assertTrue(c1 == c2);
  }
  
  public void testSetAutoCommit() {
    assertTrue("auto-commit should default to true", dbs.getDatabase1().getAutoCommit());
    dbs.getDatabase1().setAutoCommit(false);
    
    try {
      assertFalse("auto-commit should be false after being set so", dbs.getDatabase1().getAutoCommit());
    } finally {
      dbs.getDatabase1().setAutoCommit(true);
    }
  }
  
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
  
}
