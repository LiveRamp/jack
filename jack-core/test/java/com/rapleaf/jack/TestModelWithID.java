package com.rapleaf.jack;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.rapleaf.jack.test_project.DatabasesImpl;
import com.rapleaf.jack.test_project.IDatabases;
import com.rapleaf.jack.test_project.database_1.models.Image;
import com.rapleaf.jack.test_project.database_1.models.Post;
import com.rapleaf.jack.test_project.database_1.models.User;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TestModelWithID {
  private static final DatabaseConnection DATABASE_CONNECTION1 = new DatabaseConnection("database1");
  private Post postModel;
  private Post postModelx;
  private IDatabases dbs;
  private Image imageModel;
  private User userModel;
  private User userModelx;

  @Before
  public void setUp() {
    dbs = new DatabasesImpl(DATABASE_CONNECTION1);
    postModelx = new Post(15, "Test Post", 100l, 1, 0l);
    postModel = new Post(0, "Test Post", 100l, 1, 0l);
    imageModel = new Image(0, null, dbs);
    userModelx = new User(2134l, "handle", 0l, 0, 0l, 0l, "bio2", null, 0.0, 0.0, true);
    userModel = new User(0l, "handle", 0l, 0, 0l, 0l, "bio", null, 0.0, 0.0, true);

    try {
      dbs.getDatabase1().deleteAll();
      dbs.getDatabase1().users().save(userModel);
      dbs.getDatabase1().users().save(userModelx);
    } catch (IOException e) {
      e.printStackTrace();
      fail("IO Exception");
    }
  }

  @Test
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

  @Test
  public void testBelongsToAssociations() throws IOException {

    assertNull(imageModel.getUser());
    imageModel.setUserId(0);
    assertNotNull(imageModel.getUser());
    assertEquals(imageModel.getUser(), userModel);
  }

  @Test
  public void testUpdatedAt() {
    postModel.setField("updated_at", 0l);
    try {
      dbs.getDatabase1().posts().save(postModel);

      assertTrue("Check updated_at was updated " + postModel.getField("updated_at"),
          Math.abs((Long)postModel.getField("updated_at") - System.currentTimeMillis()) < 1500);
      // give 1.5 second window in case things are slow
    } catch (IOException e) {
      e.printStackTrace();
      fail("IO Exception");
    }
  }

  @Test
  public void testManualUpdatedAt() throws IOException {
    postModel.setField("updated_at", 0l);
    long updateTime = 47l;
    dbs.getDatabase1().posts().save(updateTime, postModel);

    assertEquals("Check updated_at was updated " + postModel.getField("updated_at"),
        updateTime, ((Long)postModel.getField("updated_at")).longValue());
  }

  @Test
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

  @Test
  public void testCovarianceofTypedIdMethod() {
    Image.Id typedId = imageModel.getTypedId();
    ModelIdWrapper modelIdWrapper = ((ModelWithId)imageModel).getTypedId();
  }

  @Test
  public void testIdToString() {
    assertEquals("<Image.Id: 1>", new Image.Id(1l).toString());
  }

  @Test
  public void testToString() throws IOException {
    assertTrue("check Post toString output",
        postModelx.toString().equals("<Post id: 15 title: Test Post posted_at_millis: 100 user_id: 1 updated_at: 0>"));
    assertTrue("check Post toString output",
        postModel.toString().equals("<Post id: 0 title: Test Post posted_at_millis: 100 user_id: 1 updated_at: 0>"));
    assertTrue("check User toString output",
        userModelx.toString().equals("<User id: 2134 handle: handle created_at_millis: 0 num_posts: 0 some_date: 0 some_datetime: 0 bio: bio2 some_binary: null some_float: 0.0 some_decimal: 0.0 some_boolean: true>"));
    User userModel1 = dbs.getDatabase1().users().find(2134);
    assertTrue("check User toString output",
        userModel1.toString().equals("<User id: 2134 handle: handle created_at_millis: 0 num_posts: 0 some_date: 0 some_datetime: 0 bio: bio2 some_binary: null some_float: 0.0 some_decimal: 0.0 some_boolean: true>"));
    User userModel2 = dbs.getDatabase1().users().find(0);
    assertTrue("check User toString output",
        userModel2.toString().equals("<User id: 0 handle: handle created_at_millis: 0 num_posts: 0 some_date: 0 some_datetime: 0 bio: bio some_binary: null some_float: 0.0 some_decimal: 0.0 some_boolean: true>"));

  }
}
