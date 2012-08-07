package com.rapleaf.jack;

import java.io.IOException;

import junit.framework.TestCase;

import com.rapleaf.jack.test_project.DatabasesImpl;
import com.rapleaf.jack.test_project.IDatabases;
import com.rapleaf.jack.test_project.database_1.models.Post;

public class TestModelWithID extends TestCase {
  private static final DatabaseConnection DATABASE_CONNECTION1 = new DatabaseConnection("database1");
  private Post model;
  private IDatabases dbs;

  @Override
  public void setUp() {
    model = new Post(0, "Test Post", 100l, 1, 0l);
    dbs = new DatabasesImpl(DATABASE_CONNECTION1);
    try {
      dbs.getDatabase1().deleteAll();
    } catch (IOException e) {
      e.printStackTrace();
      fail("IO Exception");
    }
  }

  public void testFields(){

    try{
      model.getField("fake_field");
      fail("Non-existent field should have thrown exception on get");
    } catch (IllegalStateException e) {}

    try {
      model.setField("fake_field", null);
      fail("Non-existent field should have thrown exception on set");
    } catch (IllegalStateException e) {
    }

    assertFalse(model.hasField("fake_field"));
    assertTrue(model.hasField("title"));

    try{
      model.setField("title", "New Title");
      assertEquals(model.getField("title"), "New Title");
    } catch (IllegalStateException e) {
      e.printStackTrace();
      fail("Field not found when it should have been");
    }

    try {
      model.setField("title", 3);
      fail("Should have had class cast exception assigning int to string field");
    } catch (ClassCastException e) {
    }

  }

  public void testUpdatedAt() {
    model.setField("updated_at", 0l);
    try {
      dbs.getDatabase1().posts().save(model);
      assertTrue("Check updated_at was updated",
          Math.abs((Long) model.getField("updated_at") - System.currentTimeMillis()) < 1500);
      // give 1.5 second window in case things are slow
    } catch (IOException e) {
      e.printStackTrace();
      fail("IO Exception");
    }
  }

}
