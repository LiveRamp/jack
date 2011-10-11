package com.rapleaf.jack;

import com.rapleaf.jack.test_project.DatabasesImpl;
import com.rapleaf.jack.test_project.IDatabases;

/**
 * This test runs all test cases from the superclass on the mock models. Do no put any
 * test cases here unless you have a really good reason to do so.
 */
public class TestAbstractMockDatabaseModel extends BaseDatabaseModelTestCase {

  @Override
  public IDatabases getDBS() {
    return new DatabasesImpl();
  }

}
