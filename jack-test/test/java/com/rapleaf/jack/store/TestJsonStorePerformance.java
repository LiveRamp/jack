package com.rapleaf.jack.store;

import java.util.List;
import java.util.Random;

import com.google.common.base.Stopwatch;
import org.junit.Before;
import org.junit.Test;

import com.rapleaf.jack.IDb;
import com.rapleaf.jack.JackTestCase;
import com.rapleaf.jack.queries.where_operators.JackMatchers;
import com.rapleaf.jack.test_project.DatabasesImpl;
import com.rapleaf.jack.test_project.database_1.IDatabase1;
import com.rapleaf.jack.test_project.database_1.models.TestStore;
import com.rapleaf.jack.test_project.database_1.models.User;
import com.rapleaf.jack.transaction.ITransactor;

public class TestJsonStorePerformance extends JackTestCase {

  private final Random random = new Random(System.currentTimeMillis());
  private final Stopwatch stopwatch = new Stopwatch();
  private final ITransactor<IDatabase1> transactor = new DatabasesImpl().getDatabase1Transactor().get();
  private final JackStore jackStore = new JackStore(JsTable.from(TestStore.TBL).create());

  @Before
  public void prepare() throws Exception {
    transactor.executeAsTransaction(IDb::deleteAll);
  }

  /**
   * Creation of 2000 records
   * By store: 1535 ms (0.77 ms per record)
   * By model: 588 ms (0.29 ms per record)
   * Difference: 2.61
   * <p>
   * Update of 2000 records
   * By store: 1462 ms (0.73 ms per record)
   * By model: 570 ms (0.29 ms per record)
   * Difference: 2.56
   * <p>
   * Query of 2000 records
   * By store: 40731 ms (20.37 ms per record)
   * By model: 7975 ms (3.99 ms per record)
   * Difference: 5.11
   */
  @Test
  public void testPerformance() throws Exception {
    int size = 100;
    testRecordCreation(size);
    testRecordUpdate(size);
    testRecordQuery(size);
  }

  private void testRecordCreation(int size) throws Exception {
    stopwatch.reset();
    stopwatch.start();
    transactor.executeAsTransaction(db -> {
      for (int i = 0; i < size; ++i) {
        int number = random.nextInt();
        JsScope scope = jackStore.rootScope().createSubScope(String.valueOf(i)).execute(db);
        jackStore.scope(scope).indexRecords()
            .putString("handle", String.valueOf(number))
            .putInt("numPosts", i)
            .putString("bio", String.valueOf(number))
            .execute(db);
      }
    });
    stopwatch.stop();
    long storeMillis = stopwatch.elapsedMillis();

    stopwatch.reset();
    stopwatch.start();
    transactor.executeAsTransaction(db -> {
      for (int i = 0; i < size; ++i) {
        int number = random.nextInt();
        User user = db.users().create(String.valueOf(number), i);
        user.setBio(String.valueOf(number)).save();
      }
    });
    stopwatch.stop();
    long modelMillis = stopwatch.elapsedMillis();

    logDuration("Creation", size, storeMillis, modelMillis);
  }

  private void testRecordUpdate(int size) throws Exception {
    stopwatch.reset();
    stopwatch.start();
    transactor.executeAsTransaction(db -> {
      for (int i = 0; i < size; ++i) {
        int number = random.nextInt();
        jackStore.scope(String.valueOf(i)).indexRecords()
            .putString("handle", String.valueOf(number))
            .putInt("numPosts", number)
            .execute(db);
      }
    });
    stopwatch.stop();
    long storeMillis = stopwatch.elapsedMillis();

    stopwatch.reset();
    List<Long> userIds = transactor.query(db -> db.createQuery().from(User.TBL).fetch().gets(User.ID));
    stopwatch.start();
    transactor.executeAsTransaction(db -> {
      for (long userId : userIds) {
        int number = random.nextInt();
        db.users().find(userId)
            .setHandle(String.valueOf(number))
            .setNumPosts(number)
            .save();
      }
    });
    stopwatch.stop();
    long modelMillis = stopwatch.elapsedMillis();

    logDuration("Update", size, storeMillis, modelMillis);
  }

  private void testRecordQuery(int size) throws Exception {
    stopwatch.reset();
    stopwatch.start();
    transactor.executeAsTransaction(db -> {
      for (int i = 0; i < size; ++i) {
        jackStore.rootScope().querySubScopes()
            .whereRecord("handle", JackMatchers.lessThan(String.valueOf(i)))
            .execute(db);
      }
    });
    stopwatch.stop();
    long storeMillis = stopwatch.elapsedMillis();

    stopwatch.reset();
    stopwatch.start();
    transactor.executeAsTransaction(db -> {
      for (int i = 0; i < size; ++i) {
        db.users().query()
            .whereHandle(JackMatchers.lessThan(String.valueOf(i)))
            .find();
      }
    });
    stopwatch.stop();
    long modelMillis = stopwatch.elapsedMillis();

    logDuration("Query", size, storeMillis, modelMillis);
  }

  private void logDuration(String title, int size, long storeMillis, long modelMillis) {
    System.out.printf("%s of %d records\n", title, size);
    System.out.printf("By store: %d ms (%.2f ms per record)\n", storeMillis, (double)storeMillis / size);
    System.out.printf("By model: %d ms (%.2f ms per record)\n", modelMillis, (double)modelMillis / size);
    System.out.printf("Difference: %.2f\n", (double)storeMillis / modelMillis);
    System.out.println();
  }

}
