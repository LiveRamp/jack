package com.rapleaf.jack;

import com.rapleaf.jack.test_project.DatabasesImpl;
import com.rapleaf.jack.test_project.IDatabases;
import com.rapleaf.jack.test_project.database_1.iface.IUserPersistence;
import com.rapleaf.jack.test_project.database_1.models.User;

import java.util.Collections;

/**
 * This test runs all test cases from the superclass on the mock models. Do no put any
 * test cases here unless you have a really good reason to do so.
 */
public class TestAbstractMockDatabaseModel extends com.rapleaf.jack.BaseDatabaseModelTestCase {

  @Override
  public IDatabases getDBS() {
    return new DatabasesImpl();
  }

  // this test is duplicated because of some weirdness with single quote escaping that appears contstrained to the mock mysql.
  public void testFindAllWithEscapedQuotesInStrings() throws Exception {
    IUserPersistence users = dbs.getDatabase1().users();
    User u1 = users.create("brya'nd", System.currentTimeMillis(), 5, System.currentTimeMillis() + 10, System.currentTimeMillis() + 20, "this is a relatively long string", new byte[]{5, 4, 3, 2, 1}, 1.2d, 3.4d, true);
    User u2 = users.create("thoma\"sk", null, 5, System.currentTimeMillis() + 10, System.currentTimeMillis() + 20, "this is a relatively long string", new byte[]{5, 4, 3, 2, 1}, 1.2d, 3.4d, true);

    assertEquals(Collections.singleton(u1), users.findAll("handle = 'brya\\'nd'"));
    assertEquals(Collections.singleton(u2), users.findAll("handle = 'thoma\"sk'"));
    assertEquals(Collections.singleton(u1), users.findAll("handle = 'brya\\'nd'"));
    assertEquals(Collections.singleton(u2), users.findAll("handle = 'thoma\"sk'"));
  }

}
