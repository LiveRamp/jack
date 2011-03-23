package com.rapleaf.db_schemas;

import java.util.Collections;
import java.util.Set;

import com.rapleaf.db_schemas.maindb.iface.ICustomerDataSetPersistence;
import com.rapleaf.db_schemas.maindb.models.Customer;
import com.rapleaf.db_schemas.maindb.models.CustomerDataSet;
import com.rapleaf.support.DatabaseConnection;

public class TestAbstractDatabaseModel extends DbSchemasTestCase {
  private static final DatabaseConnection DATABASE_CONNECTION1 = new DatabaseConnection("maindb");
  private static final DatabaseConnection DATABASE_CONNECTION2 = new DatabaseConnection("spruce_db");
  private final IDatabases dbs = new DatabasesImpl(DATABASE_CONNECTION1, DATABASE_CONNECTION2);

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    dbs.getMainDb().customers().deleteAll();
    dbs.getMainDb().customerDataSets().deleteAll();
  }

  public void testIt() throws Exception {
    ICustomerDataSetPersistence cdsp = dbs.getMainDb().customerDataSets();

    // test creation of models
    CustomerDataSet cds1 = cdsp.create(1, 2, 3, "name", "description", null);
    CustomerDataSet cds2 = cdsp.create(10, 20, 30, "other_name", "other_description", null);

    assertEquals(Integer.valueOf(2), cds1.getCdsImportRequestId());
    assertEquals(Integer.valueOf(1), cds1.getCustomerId());
    assertEquals(Integer.valueOf(3), cds1.getNumRecords());
    assertTrue(cds1.getId() > 0);

    // test caching
    cds1 = cdsp.find(cds1.getId());
    CustomerDataSet anotherInstance = cdsp.find(cds1.getId());
    assertTrue(anotherInstance == cds1);

    // test findAll
    Set<CustomerDataSet> all = cdsp.findAll();
    cds1 = cdsp.find(cds1.getId());
    assertEquals(2, all.size());
    assertTrue(all.contains(cds1));
    assertTrue(all.contains(cds2));
    // make sure caching worked as expected
    boolean found = false;
    for (CustomerDataSet cdsIter : all) {
      if (cdsIter == cds1) {
        found = true;
        break;
      }
    }
    assertTrue("No CDS instance equivalent to cds1 was found!", found);

    assertEquals(Collections.singleton(cds1), cdsp.findAll("name = \"name\""));
    assertEquals(Collections.singleton(cds2), cdsp.findAll("num_records > 10"));
    assertEquals(Collections.EMPTY_SET, cdsp.findAll("description = \"I dont' exist\""));

    // test belongs_to associations
    Customer cust = dbs.getMainDb().customers().create("test", "username", "blah", 1L, 0, "", 1, "", "", "", "", "", null, null, 1, 1, 1L, null, 1, 1, 1, 1, 1, "test", 1, false);
    cds1.setCustomerId((int)cust.getId());
    cdsp.save(cds1);

    CustomerDataSet cdsX = cdsp.find(cds1.getId());
    assertEquals(cust.getId(), cdsX.getCustomer().getId());

    // test has_many associations
//    cust.get

    // test finding a saved record
    CustomerDataSet foundCDS = cdsp.find(cds1.getId());
    assertEquals(cds1.getId(), foundCDS.getId());
    assertEquals(cds1.getNumRecords(), foundCDS.getNumRecords());
    assertEquals(cds1.getCdsImportRequestId(), foundCDS.getCdsImportRequestId());
    assertEquals(cds1.getCustomerId(), foundCDS.getCustomerId());

    // test lookup by foreign key
    foundCDS.setCdsImportRequestId(7);
    assertTrue(cdsp.save(foundCDS));
    cds1 = cdsp.find(foundCDS.getId());
    assertEquals(Integer.valueOf(7), cds1.getCdsImportRequestId());

    assertTrue(cdsp.findAllByForeignKey("cds_import_request_id", 2).isEmpty());
    assertEquals(Collections.singleton(foundCDS), cdsp.findAllByForeignKey("cds_import_request_id", 7));

    assertNotNull(cdsp.create(10, 1, 2, "name", "description", null));

    // test saving and deleting nulls
    foundCDS.setNumRecords(null);
    assertTrue(cdsp.save(foundCDS));
    foundCDS = cdsp.find(foundCDS.getId());
    assertEquals(null, foundCDS.getNumRecords());

    // test delete
    long id = foundCDS.getId();
    cdsp.delete(foundCDS);
    foundCDS = cdsp.find(id);
    assertNull(foundCDS);
  }
}
