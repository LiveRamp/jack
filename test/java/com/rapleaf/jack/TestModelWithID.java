package com.rapleaf.jack;

import com.rapleaf.jack.test_project.DatabasesImpl;
import com.rapleaf.jack.test_project.IDatabases;
import com.rapleaf.jack.test_project.database_1.models.Image;
import com.rapleaf.jack.test_project.database_1.models.Post;
import com.rapleaf.jack.test_project.database_1.models.User;
import junit.framework.TestCase;

import java.io.IOException;

public class TestModelWithID extends TestCase {
  private static final DatabaseConnection DATABASE_CONNECTION1 = new DatabaseConnection("database1");
  private Post postModel;
  private IDatabases dbs;
  private Image imageModel;
  private User userModel;

  @Override
  public void setUp() {
    dbs = new DatabasesImpl(DATABASE_CONNECTION1);
    postModel = new Post(0, "Test Post", 100l, 1, 0l);
    imageModel = new Image(0, null, dbs);
    userModel = new User(0l, "handle", 0l, 0, 0l, 0l, "bio", null, 0.0, 0.0, true);
    try {
      dbs.getDatabase1().deleteAll();
      dbs.getDatabase1().users().save(userModel);
    } catch (IOException e) {
      e.printStackTrace();
      fail("IO Exception");
    }
  }

  public void testFields() {

    try {
      postModel.getField("fake_field");
      fail("Non-existent field should have thrown exception on get");
    } catch (IllegalStateException e) {
    }

    try {
      postModel.setField("fake_field", null);
      fail("Non-existent field should have thrown exception on set");
    } catch (IllegalStateException e) {
    }

    assertFalse(postModel.hasField("fake_field"));
    assertTrue(postModel.hasField("title"));

    try {
      postModel.setField("title", "New Title");
      assertEquals(postModel.getField("title"), "New Title");
    } catch (IllegalStateException e) {
      e.printStackTrace();
      fail("Field not found when it should have been");
    }

    try {
      postModel.setField("title", 3);
      fail("Should have had class cast exception assigning int to string field");
    } catch (ClassCastException e) {
    }
  }

  public void testBelongsToAssociations() throws IOException {

    assertNull(imageModel.getUser());
    imageModel.setUserId(0);
    assertNotNull(imageModel.getUser());
    assertEquals(imageModel.getUser(), userModel);
  }

  public void testUpdatedAt() {
    postModel.setField("updated_at", 0l);
    try {
      dbs.getDatabase1().posts().save(postModel);

      assertTrue("Check updated_at was updated " + postModel.getField("updated_at"),
          Math.abs((Long) postModel.getField("updated_at") - System.currentTimeMillis()) < 1500);
      // give 1.5 second window in case things are slow
    } catch (IOException e) {
      e.printStackTrace();
      fail("IO Exception");
    }
  }

  public void testClearAssociations() throws IOException {

    assertNull(imageModel.getUser());
    imageModel.setUserId(0);
    assertNotNull(imageModel.getUser());
    assertEquals(imageModel.getUser(), userModel);
    imageModel.unsetAssociations();
    try {
      imageModel.getUser();
      fail("A NullPointerException should have been thrown when trying to access the cleared association");
    } catch (NullPointerException e) {
    }
  }

  public void testCovarianceofTypedIdMethod() {
    Image.Id typedId = imageModel.getTypedId();
    ModelIdWrapper modelIdWrapper = ((ModelWithId) imageModel).getTypedId();
  }
}
