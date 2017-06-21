package com.rapleaf.jack.transaction;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Stopwatch;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rapleaf.jack.IDb;
import com.rapleaf.jack.JackTestCase;
import com.rapleaf.jack.test_project.DatabasesImpl;
import com.rapleaf.jack.test_project.database_1.IDatabase1;
import com.rapleaf.jack.test_project.database_1.models.User;

public class TestTransactorMetrics extends JackTestCase {

  private TransactorImpl.Builder<IDatabase1> transactorBuilder = new DatabasesImpl().getDatabase1Transactor();
  private ExecutorService executorService;
  private static final Logger LOG = LoggerFactory.getLogger(TestDbPoolManager.class);

  @Before
  public void prepare() throws Exception {
    transactorBuilder.get().query(IDb::deleteAll);
  }

  @After
  public void cleanup() throws Exception {
    executorService = null;
  }

  @Test
  public void testQueryOrder() throws Exception {
    TransactorImpl<IDatabase1> transactor = transactorBuilder.get();

    String expectedBio = "test";

    transactor.execute(db -> {
      User user = db.users().createDefaultInstance();
      user.setBio(expectedBio).save();
      sleepMillis(200);
    });

    transactor.execute(db -> {
      User user = db.users().createDefaultInstance();
      user.setBio(expectedBio).save();
      sleepMillis(100);
    });

    transactor.execute(db -> {
      User user = db.users().createDefaultInstance();
      user.setBio(expectedBio).save();
    });

    TransactorMetrics queryMetrics = transactor.getQueryMetrics();
    int lastLine = 0;
    for (StackTraceElement queryStackTraceElement : queryMetrics.getLongestQueries().keySet()) {
      assert (queryStackTraceElement.getLineNumber() > lastLine);
      lastLine = queryStackTraceElement.getLineNumber();
    }
    transactor.close();
  }

  @Test
  public void testQueryNumber() throws Exception {
    TransactorImpl<IDatabase1> transactor = transactorBuilder.get();
    String expectedBio = "test";

    Stopwatch stopwatch = new Stopwatch().start();

    transactor.execute(db -> {
      User user = db.users().createDefaultInstance();
      user.setBio(expectedBio).save();
    });

    System.out.println(stopwatch.elapsedTime(TimeUnit.MILLISECONDS));
//
//    transactor.execute(db -> {
//      User user = db.users().createDefaultInstance();
//      user.setBio(expectedBio).save();
//    });

    System.out.println(stopwatch.elapsedTime(TimeUnit.MILLISECONDS));

    TransactorMetrics queryMetrics = transactor.getQueryMetrics();
    System.out.println(stopwatch.elapsedTime(TimeUnit.MILLISECONDS));
    assert (queryMetrics.getLongestQueries().size() == 1);
    transactor.close();
    System.out.println(stopwatch.elapsedTime(TimeUnit.MILLISECONDS));
  }


}
