package com.liveramp.jack.testcontainers;

import java.io.IOException;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.testcontainers.containers.MySQLContainer;

import com.rapleaf.jack.BaseDatabaseConnection;
import com.rapleaf.jack.GenericDatabases;
import com.rapleaf.jack.IDb;
import com.rapleaf.jack.JackTestCase;
import com.rapleaf.jack.test_project.DatabasesImpl;
import com.rapleaf.jack.test_project.IDatabases;
import com.rapleaf.jack.test_project.database_1.IDatabase1;
import com.rapleaf.jack.test_project.database_1.impl.Database1Impl;
import com.rapleaf.jack.test_project.database_1.models.Post;
import com.rapleaf.jack.tracking.NoOpAction;
import com.rapleaf.jack.transaction.ITransactor;
import com.rapleaf.jack.transaction.TransactorImpl;

public class TestMysqlContainerDb extends JackTestCase {

  /**
   * Typically, you'll want to use testcontainers to create an alternate {@link IDatabases} for one of your databases.
   *
   * In this example, "database1" would usually be accessed using {@link DatabasesImpl} to get either a transactor or
   * a {@link IDb}. This is typically used to connect to an external MySQL instance.
   *
   * However, for testing, it is often more convenient to use containerized databases so you can run your tests in
   * multiple threads.
   *
   * Calling {@link Database1ContainerDb#get()} returns something that looks very similar to {@link DatabasesImpl},
   * exposing an accessor for both a transactor and a {@link IDb}. Instead of being used to point to an external MySQL
   * instance, these accessors point to a single "database1" MySQL container within the same JVM.
   */
  private static class Database1ContainerDb {
    private static final String DB_NAME = "jack_1";
    private static final LazyLoadingSingletonFactory<MySQLContainer> DB_PROVIDER = new MysqlContainerSingletonProvider(DB_NAME);

    public static ContainerDbImpl<IDatabase1> get() {
      return new ContainerDbImpl<>(new DbBuilder(), DB_PROVIDER, DB_NAME);
    }

    private static class DbBuilder implements ContainerDbImpl.DbBuilder<IDatabase1> {
      @Override
      public IDatabase1 getDb(BaseDatabaseConnection conn, ContainerDbImpl<IDatabase1> genericDatabases) {
        return new Database1Impl(conn, new IDatabases() {
          @Override
          public IDatabase1 getDatabase1() {
            return genericDatabases.getDb();
          }

          @Override
          public ITransactor.Builder<IDatabase1, ?> getDatabase1Transactor() {
            return genericDatabases.getDbTransactor();
          }
        }, new NoOpAction());
      }
    }
  }

  @Test
  public void testDb() throws IOException {
    IDatabase1 db = Database1ContainerDb.get().getDb();
    db.posts().createDefaultInstance().save();

    TransactorImpl<IDatabase1> bangDbTransactor = Database1ContainerDb.get().getDbTransactor().get();
    List<Post> results = bangDbTransactor.query(dbInstance -> dbInstance.posts().findAll());

    Assert.assertEquals(1, results.size());
  }
}
