package com.rapleaf.jack;

import com.rapleaf.jack.test_project.IDatabases;
import com.rapleaf.jack.test_project.MockDatabasesImpl;

public class TestAbstractMockDatabaseModel extends BaseDatabaseModelTestCase {

  @Override
  public IDatabases getDBS() {
    return new MockDatabasesImpl();
  }

}
