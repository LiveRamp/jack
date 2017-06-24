package com.rapleaf.jack.transaction;

import java.util.concurrent.ExecutorService;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rapleaf.jack.IDb;
import com.rapleaf.jack.JackTestCase;
import com.rapleaf.jack.test_project.DatabasesImpl;
import com.rapleaf.jack.test_project.database_1.IDatabase1;

public class TestTransactorMetrics extends JackTestCase {

  private TransactorImpl.Builder<IDatabase1> transactorBuilder = new DatabasesImpl().getDatabase1Transactor();
  private ExecutorService executorService;
  private static final Logger LOG = LoggerFactory.getLogger(TestTransactorMetrics.class);

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

    transactor.execute(db -> {sleepMillis(200);});
    transactor.execute(db -> {sleepMillis(100);});
    transactor.execute(db -> {});

    TransactorMetrics queryMetrics = transactor.getQueryMetrics();
    int lastLine = 0;
    transactor.close();
    for (TransactorMetricElement query : queryMetrics.getLongestQueries()) {
      assert (query.getQueryTrace().getLineNumber() > lastLine);
      lastLine = query.getQueryTrace().getLineNumber();
    }
    transactor.close();
  }

  @Test
  public void testMaxAverageExecutionTime() throws Exception {
    TransactorImpl<IDatabase1> transactor = transactorBuilder.get();

    transactor.execute(db -> {Thread.sleep(200);});
    
    TransactorMetrics queryMetrics = transactor.getQueryMetrics();
    double maxExecutionTime = queryMetrics.getLongestQueries().getFirst().getAverageExecutionTime();
    LOG.info("max execution time : " + maxExecutionTime);
    assert ((maxExecutionTime >= 190) && (maxExecutionTime < 230));
    transactor.close();
  }
}