package com.rapleaf.jack;

import org.junit.Test;

import com.rapleaf.jack.test_project.DatabasesImpl;
import com.rapleaf.jack.test_project.IDatabases;
import com.rapleaf.jack.test_project.database_1.iface.ICommentPersistence;
import com.rapleaf.jack.test_project.database_1.impl.BaseCommentPersistenceImpl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestLazyLoadPersistence {

  private static final DatabaseConnection DATABASE_CONNECTION1 = new MysqlDatabaseConnection("database1");
  private static final IDatabases dbs = new DatabasesImpl(DATABASE_CONNECTION1);

  @Test
  public void testSwitchCachingBehavior() {
    LazyLoadPersistence<ICommentPersistence, IDatabases> lazyLoadPersistence = this.getLazyLoadPersistence();
    lazyLoadPersistence.disableCaching();
    assertFalse(lazyLoadPersistence.get().isCaching());
    lazyLoadPersistence.get().enableCaching();
    assertTrue(lazyLoadPersistence.get().isCaching());
  }

  @Test
  public void testDisableCachingBeforeInstantiation() {
    LazyLoadPersistence<ICommentPersistence, IDatabases> lazyLoadPersistence = this.getLazyLoadPersistence();
    lazyLoadPersistence.disableCaching();
    assertFalse(lazyLoadPersistence.get().isCaching());
  }

  @Test
  public void testDisableCachingAfterInstantiation() {
    LazyLoadPersistence<ICommentPersistence, IDatabases> lazyLoadPersistence = this.getLazyLoadPersistence();
    assertTrue(lazyLoadPersistence.get().isCaching());
    lazyLoadPersistence.disableCaching();
    assertFalse(lazyLoadPersistence.get().isCaching());
  }

  private LazyLoadPersistence<ICommentPersistence, IDatabases> getLazyLoadPersistence() {
    return new LazyLoadPersistence<ICommentPersistence, IDatabases>(DATABASE_CONNECTION1, dbs) {
      @Override
      protected ICommentPersistence build(BaseDatabaseConnection conn, IDatabases databases) {
        return new BaseCommentPersistenceImpl(conn, databases);
      }
    };
  }

}
