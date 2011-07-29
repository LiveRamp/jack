package com.rapleaf.jack;

import com.rapleaf.jack.test_project.DatabasesImpl;
import com.rapleaf.jack.test_project.IDatabases;

public class TestAbstractMockDatabaseModel extends BaseDatabaseModelTestCase {

  @Override
  public IDatabases getDBS() {
    return new DatabasesImpl();
  }

}
