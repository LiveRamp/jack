package com.rapleaf.jack.transaction;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.Test;

import com.rapleaf.jack.JackTestCase;
import com.rapleaf.jack.test_project.DatabasesImpl;
import com.rapleaf.jack.test_project.database_1.IDatabase1;

import static org.junit.Assert.assertEquals;

public class TestTransactorImplBuilder extends JackTestCase {

  @Test
  public void testParameterSpecification() {
    TransactorImpl.Builder<IDatabase1> builder = new DatabasesImpl().getDatabase1Transactor();
    Random random = ThreadLocalRandom.current();
    int maxTotalConnections = random.nextInt(20);
    int minIdleConnections = random.nextInt(10);

    // parameter should have the specified value
    builder.setMaxTotalConnections(maxTotalConnections)
        .setMinIdleConnections(minIdleConnections);
    assertEquals(maxTotalConnections, builder.maxTotalConnections);
    assertEquals(minIdleConnections, builder.minIdleConnections);

    // parameter values are not changed after transactor construction
    builder.get();
    assertEquals(maxTotalConnections, builder.maxTotalConnections);
    assertEquals(minIdleConnections, builder.minIdleConnections);

    // parameter values can be updated
    builder.setMaxTotalConnections(maxTotalConnections + 1)
        .setMinIdleConnections(minIdleConnections - 1);
    assertEquals(maxTotalConnections + 1, builder.maxTotalConnections);
    assertEquals(minIdleConnections - 1, builder.minIdleConnections);
  }

}
