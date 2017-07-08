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

public class TestJsonStorePerformance extends JackTestCase {

  private final Random random = new Random(System.currentTimeMillis());
  private final Stopwatch stopwatch = new Stopwatch();
  private final ITransactor<IDatabase1> transactor = new DatabasesImpl().getDatabase1Transactor().get();
  private final JackStore jackStore2 = new JackStore(JsTable.from(TestStore.TBL).create());
  private final Set<Long> recordIds = Sets.newHashSet();
  private final Set<Long> userIds = Sets.newHashSet();

  @Before
  public void prepare() throws Exception {
    transactor.executeAsTransaction(IDb::deleteAll);
  }

  /**
   * Creation of 100000 records
   * By store: 16730 ms (0.17 ms per record)
   * By model: 16965 ms (0.17 ms per record)
   * Difference: 0.99
   * <p>
   * Update of 100000 records
   * By store: 44386 ms (0.44 ms per record)
   * By model: 21601 ms (0.22 ms per record)
   * Difference: 2.05
   * <p>
   * Query (5 times) of 100000 records
   * By store: 1718 ms (0.02 ms per record)
   * By model: 946 ms (0.01 ms per record)
   * Difference: 1.82
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
        Long recordId = jackStore2.rootRecord().createSubRecord()
            .recordName(String.valueOf(i))
            .putString("handle", String.valueOf(number))
            .putInt("numPosts", i)
            .putString("bio", String.valueOf(number))
            .exec(db);
        recordIds.add(recordId);
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
      for (long recordId : recordIds) {
        int number = random.nextInt(50);
        jackStore2.rootRecord().updateSubRecords()
            .whereSubRecordIds(recordId)
            .putString("handle", String.valueOf(number))
            .putInt("numPosts", number)
            .exec(db);
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
        jackStore2.rootRecord()
            .querySubRecords()
            .whereSubRecord("handle", JackMatchers.lessThan(String.valueOf(number)))
            .exec(db);
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
      for (long recordId : recordIds) {
        jackStore2.record(recordId).delete().deleteEntireRecord().exec(db);
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
