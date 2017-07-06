package com.rapleaf.jack.store;

import java.util.List;
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
import com.rapleaf.jack.transaction.ITransactor;

public class TestJsonStore2Performance extends JackTestCase {

  private final Random random = new Random(System.currentTimeMillis());
  private final Stopwatch stopwatch = new Stopwatch();
  private final ITransactor<IDatabase1> transactor = new DatabasesImpl().getDatabase1Transactor().get();
  private final JackStore2 jackStore2 = new JackStore2(JsTable.from(TestStore.TBL).create());
  private final Set<Long> scopeIds = Sets.newHashSet();

  @Before
  public void prepare() throws Exception {
    transactor.executeAsTransaction(IDb::deleteAll);
  }

  @Test
  public void testPerformance() throws Exception {
    int size = 50;
    testRecordCreation(size);
    testRecordUpdate(size);
    testRecordQuery(size);
  }

  private void testRecordCreation(int size) throws Exception {
    stopwatch.reset();
    stopwatch.start();
    transactor.executeAsTransaction(db -> {
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
    });
    stopwatch.stop();
    long storeMillis = stopwatch.elapsedMillis();

    stopwatch.reset();
    stopwatch.start();
    transactor.executeAsTransaction(db -> {
      for (int i = 0; i < size; ++i) {
        int number = random.nextInt(50);
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
      for (long scopeId : scopeIds) {
        int number = random.nextInt(50);
        jackStore2.rootScope().updateSubScopes()
            .whereSubScopeIds(scopeId)
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
        int number = random.nextInt(50);
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
        int number = random.nextInt(50);
        jackStore2.rootScope()
            .querySubScopes()
            .whereSubRecord("handle", JackMatchers.lessThan(String.valueOf(number)))
            .execute(db);
      }
    });
    stopwatch.stop();
    long storeMillis = stopwatch.elapsedMillis();

    stopwatch.reset();
    stopwatch.start();
    transactor.executeAsTransaction(db -> {
      for (int i = 0; i < size; ++i) {
        int number = random.nextInt(50);
        db.users().query()
            .whereHandle(JackMatchers.lessThan(String.valueOf(number)))
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
