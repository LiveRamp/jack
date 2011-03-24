package com.rapleaf.java_active_record;

import java.nio.ByteBuffer;

import junit.framework.TestCase;

import com.rapleaf.java_active_record.test_project.DatabasesImpl;
import com.rapleaf.java_active_record.test_project.IDatabases;
import com.rapleaf.java_active_record.test_project.database_1.iface.IUserPersistence;
import com.rapleaf.java_active_record.test_project.database_1.models.User;

public class TestAbstractDatabaseModel extends TestCase {
  private static final DatabaseConnection DATABASE_CONNECTION1 = new DatabaseConnection("database1");
//  private static final DatabaseConnection DATABASE_CONNECTION2 = new DatabaseConnection("spruce_db");
  private final IDatabases dbs = new DatabasesImpl(DATABASE_CONNECTION1);

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    dbs.getDatabase1().users().deleteAll();
    dbs.getDatabase1().posts().deleteAll();
    dbs.getDatabase1().comments().deleteAll();
    dbs.getDatabase1().followers().deleteAll();
  }

  public void testCreate() throws Exception {
    IUserPersistence users = dbs.getDatabase1().users();
    long t0 = System.currentTimeMillis();
    long t1 = t0 + 10;
    long t2 = t0 + 20;
    byte[] someBinary = new byte[]{5, 4, 3, 2, 1};
    User bryand = users.create("bryand", t0, 5, t1, t2, "this is a relatively long string", someBinary, 1.2d, true);
    assertEquals("bryand", bryand.getHandle());
    assertEquals(Long.valueOf(t0), bryand.getCreatedAtMillis());
    assertEquals(Integer.valueOf(5), bryand.getNumPosts());
    assertEquals(Long.valueOf(t1), bryand.getSomeDate());
    assertEquals(Long.valueOf(t2), bryand.getSomeDatetime());
    assertEquals("this is a relatively long string", bryand.getBio());
    assertEquals(ByteBuffer.wrap(someBinary), ByteBuffer.wrap(bryand.getSomeBinary()));
    assertEquals(1.2, bryand.getSomeFloat());
    assertTrue(bryand.getSomeBoolean());
  }

  public void testFind() throws Exception {
    IUserPersistence users = dbs.getDatabase1().users();
    long t0 = System.currentTimeMillis();
    long t1 = t0 + 10;
    long t2 = t0 + 20;
    byte[] someBinary = new byte[]{5, 4, 3, 2, 1};
    User bryand = users.create("bryand", t0, 5, t1, t2, "this is a relatively long string", someBinary, 1.2d, true);

    User bryand_again = users.find(bryand.getId());
    assertEquals(bryand.getId(), bryand_again.getId());
    assertEquals("bryand", bryand_again.getHandle());
    assertEquals(Long.valueOf(t0), bryand_again.getCreatedAtMillis());
    assertEquals(Integer.valueOf(5), bryand_again.getNumPosts());
    // need to figure out what the appropriate rounding is...
//    assertEquals(Long.valueOf(t1), bryand_again.getSomeDate());
    // need to figure out what the appropriate roudning is...
//    assertEquals(Long.valueOf(t2), bryand_again.getSomeDatetime());
    assertEquals("this is a relatively long string", bryand_again.getBio());
    assertEquals(ByteBuffer.wrap(someBinary), ByteBuffer.wrap(bryand_again.getSomeBinary()));
    assertEquals(1.2, bryand_again.getSomeFloat());
    assertTrue(bryand_again.getSomeBoolean());
  }

  public void oldTestIt() throws Exception {
//    ICustomerDataSetPersistence cdsp = dbs.getMainDb().customerDataSets();
//
//    // test creation of models
//    CustomerDataSet cds1 = cdsp.create(1, 2, 3, "name", "description", null);
//    CustomerDataSet cds2 = cdsp.create(10, 20, 30, "other_name", "other_description", null);
//
//    assertEquals(Integer.valueOf(2), cds1.getCdsImportRequestId());
//    assertEquals(Integer.valueOf(1), cds1.getCustomerId());
//    assertEquals(Integer.valueOf(3), cds1.getNumRecords());
//    assertTrue(cds1.getId() > 0);
//
//    // test caching
//    cds1 = cdsp.find(cds1.getId());
//    CustomerDataSet anotherInstance = cdsp.find(cds1.getId());
//    assertTrue(anotherInstance == cds1);
//
//    // test findAll
//    Set<CustomerDataSet> all = cdsp.findAll();
//    cds1 = cdsp.find(cds1.getId());
//    assertEquals(2, all.size());
//    assertTrue(all.contains(cds1));
//    assertTrue(all.contains(cds2));
//    // make sure caching worked as expected
//    boolean found = false;
//    for (CustomerDataSet cdsIter : all) {
//      if (cdsIter == cds1) {
//        found = true;
//        break;
//      }
//    }
//    assertTrue("No CDS instance equivalent to cds1 was found!", found);
//
//    assertEquals(Collections.singleton(cds1), cdsp.findAll("name = \"name\""));
//    assertEquals(Collections.singleton(cds2), cdsp.findAll("num_records > 10"));
//    assertEquals(Collections.EMPTY_SET, cdsp.findAll("description = \"I dont' exist\""));
//
//    // test belongs_to associations
//    Customer cust = dbs.getMainDb().customers().create("test", "username", "blah", 1L, 0, "", 1, "", "", "", "", "", null, null, 1, 1, 1L, null, 1, 1, 1, 1, 1, "test", 1, false);
//    cds1.setCustomerId((int)cust.getId());
//    cdsp.save(cds1);
//
//    CustomerDataSet cdsX = cdsp.find(cds1.getId());
//    assertEquals(cust.getId(), cdsX.getCustomer().getId());
//
//    // test has_many associations
////    cust.get
//
//    // test finding a saved record
//    CustomerDataSet foundCDS = cdsp.find(cds1.getId());
//    assertEquals(cds1.getId(), foundCDS.getId());
//    assertEquals(cds1.getNumRecords(), foundCDS.getNumRecords());
//    assertEquals(cds1.getCdsImportRequestId(), foundCDS.getCdsImportRequestId());
//    assertEquals(cds1.getCustomerId(), foundCDS.getCustomerId());
//
//    // test lookup by foreign key
//    foundCDS.setCdsImportRequestId(7);
//    assertTrue(cdsp.save(foundCDS));
//    cds1 = cdsp.find(foundCDS.getId());
//    assertEquals(Integer.valueOf(7), cds1.getCdsImportRequestId());
//
//    assertTrue(cdsp.findAllByForeignKey("cds_import_request_id", 2).isEmpty());
//    assertEquals(Collections.singleton(foundCDS), cdsp.findAllByForeignKey("cds_import_request_id", 7));
//
//    assertNotNull(cdsp.create(10, 1, 2, "name", "description", null));
//
//    // test saving and deleting nulls
//    foundCDS.setNumRecords(null);
//    assertTrue(cdsp.save(foundCDS));
//    foundCDS = cdsp.find(foundCDS.getId());
//    assertEquals(null, foundCDS.getNumRecords());
//
//    // test delete
//    long id = foundCDS.getId();
//    cdsp.delete(foundCDS);
//    foundCDS = cdsp.find(id);
//    assertNull(foundCDS);
  }
}
