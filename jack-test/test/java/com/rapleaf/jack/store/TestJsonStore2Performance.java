package com.rapleaf.jack.store;

import java.util.Random;
import java.util.Set;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;

import com.rapleaf.jack.IDb;
import com.rapleaf.jack.JackTestCase;
import com.rapleaf.jack.queries.where_operators.JackMatchers;
import com.rapleaf.jack.test_project.DatabasesImpl;
import com.rapleaf.jack.test_project.database_1.IDatabase1;
import com.rapleaf.jack.test_project.database_1.models.TestStore;
import com.rapleaf.jack.test_project.database_1.models.User;
import com.rapleaf.jack.transaction.IExecution;
import com.rapleaf.jack.transaction.ITransactor;

public class TestJsonStore2Performance extends JackTestCase {

  private final Random random = new Random(System.currentTimeMillis());
  private final Stopwatch stopwatch = new Stopwatch();
  private final ITransactor<IDatabase1> transactor = new DatabasesImpl().getDatabase1Transactor().get();
  private final JackStore2 jackStore2 = new JackStore2(JsTable.from(TestStore.TBL).create());
  private final Set<Long> scopeIds = Sets.newHashSet();
  private final Set<Long> userIds = Sets.newHashSet();

  @Before
  public void prepare() throws Exception {
    transactor.executeAsTransaction(IDb::deleteAll);
  }

  /**
   * Creation of 100000 records
   * By store: 20133 ms (0.20 ms per record)
   * By model: 17907 ms (0.18 ms per record)
   * Difference: 1.12
   * <p>
   * Update of 100000 records
   * By store: 76783 ms (0.77 ms per record)
   * By model: 24269 ms (0.24 ms per record)
   * Difference: 3.16
   * <p>
   * Query (5 times) of 100000 records
   * By store: 12292 ms (2458.40 ms per record)
   * By model: 765 ms (153.00 ms per record)
   * Difference: 16.07
   * <p>
   * Deletion of 100000 records
   * By store: 43240 ms (0.43 ms per record)
   * By model: 6926 ms (0.07 ms per record)
   * Difference: 6.24
   */
  @Test
  public void testPerformance() throws Exception {
    int size = 200;
    testRecordCreation(size);
    testRecordUpdate(size);
    testRecordQuery(5, size);
    testRecordDeletion(size);
  }

  private void testRecordCreation(int size) throws Exception {
    IExecution<IDatabase1> storeExecution = db -> {
      for (int i = 0; i < size; ++i) {
        int number = random.nextInt(50);
        JsRecord record = jackStore2.rootScope().createSubScope()
            .scopeName(String.valueOf(i))
            .putString("handle", String.valueOf(number))
            .putInt("numPosts", i)
            .putString("bio", String.valueOf(number))
            .execute(db);
        scopeIds.add(record.getScopeId());
      }
    };

    IExecution<IDatabase1> modelExecution = db -> {
      for (int i = 0; i < size; ++i) {
        int number = random.nextInt(50);
        User user = db.users().create(String.valueOf(number), i);
        user.setBio(String.valueOf(number)).save();
        userIds.add(user.getId());
      }
    };

    runComparison("Creation", size, storeExecution, modelExecution);
  }

  private void testRecordUpdate(int size) throws Exception {
    IExecution<IDatabase1> storeExecution = db -> {
      for (long scopeId : scopeIds) {
        int number = random.nextInt(50);
        jackStore2.rootScope().updateSubScopes()
            .whereSubScopeIds(scopeId)
            .putString("handle", String.valueOf(number))
            .putInt("numPosts", number)
            .execute(db);
      }
    };

    IExecution<IDatabase1> modelExecution = db -> {
      for (long userId : userIds) {
        int number = random.nextInt(50);
        db.users().find(userId)
            .setHandle(String.valueOf(number))
            .setNumPosts(number)
            .save();
      }
    };

    runComparison("Update", size, storeExecution, modelExecution);
  }

  private void testRecordQuery(int queryCount, int size) throws Exception {
    IExecution<IDatabase1> storeExecution = db -> {
      for (int i = 0; i < queryCount; ++i) {
        int number = random.nextInt(50);
        jackStore2.rootScope()
            .querySubScopes()
            .whereSubRecord("handle", JackMatchers.lessThan(String.valueOf(number)))
            .execute(db);
      }
    };

    IExecution<IDatabase1> modelExecution = db -> {
      for (int i = 0; i < queryCount; ++i) {
        int number = random.nextInt(50);
        db.users().query()
            .whereHandle(JackMatchers.lessThan(String.valueOf(number)))
            .find();
      }
    };

    runComparison("Query (" + queryCount + " times)", size, storeExecution, modelExecution);
  }

  private void testRecordDeletion(int size) throws Exception {
    IExecution<IDatabase1> storeExecution = db -> {
      for (long scopeId : scopeIds) {
        jackStore2.scope(scopeId).delete().deleteEntireRecord().execute(db);
      }
    };

    IExecution<IDatabase1> modelExecution = db -> {
      for (long userId : userIds) {
        db.users().delete().id(userId).execute();
      }
    };

    runComparison("Deletion", size, storeExecution, modelExecution);
  }

  private void runComparison(String title, int size, IExecution<IDatabase1> storeExecution, IExecution<IDatabase1> modelExecution) throws Exception {
    stopwatch.reset();
    stopwatch.start();
    transactor.executeAsTransaction(storeExecution);
    stopwatch.stop();
    long storeMillis = stopwatch.elapsedMillis();

    stopwatch.reset();
    stopwatch.start();
    transactor.executeAsTransaction(modelExecution);
    stopwatch.stop();
    long modelMillis = stopwatch.elapsedMillis();

    logDuration(title, size, storeMillis, modelMillis);
  }

  private void logDuration(String title, int size, long storeMillis, long modelMillis) {
    System.out.printf("%s of %d records\n", title, size);
    System.out.printf("By store: %d ms (%.2f ms per record)\n", storeMillis, (double)storeMillis / size);
    System.out.printf("By model: %d ms (%.2f ms per record)\n", modelMillis, (double)modelMillis / size);
    System.out.printf("Difference: %.2f\n", (double)storeMillis / modelMillis);
    System.out.println();
  }

}
