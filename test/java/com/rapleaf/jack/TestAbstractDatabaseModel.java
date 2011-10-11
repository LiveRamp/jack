package com.rapleaf.jack;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import com.rapleaf.jack.DatabaseConnection;
import com.rapleaf.jack.test_project.DatabasesImpl;
import com.rapleaf.jack.test_project.IDatabases;
import com.rapleaf.jack.test_project.database_1.iface.ICommentPersistence;
import com.rapleaf.jack.test_project.database_1.iface.IImagePersistence;
import com.rapleaf.jack.test_project.database_1.iface.IPostPersistence;
import com.rapleaf.jack.test_project.database_1.iface.IUserPersistence;
import com.rapleaf.jack.test_project.database_1.models.Comment;
import com.rapleaf.jack.test_project.database_1.models.Image;
import com.rapleaf.jack.test_project.database_1.models.Post;
import com.rapleaf.jack.test_project.database_1.models.User;

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
    assertTrue(bryand.isSomeBoolean());
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
    assertTrue(bryand_again.isSomeBoolean());
  }

  public void testFindCache() throws Exception {
    IUserPersistence users = dbs.getDatabase1().users();
    User user = users.create("bryand", System.currentTimeMillis(), 5, System.currentTimeMillis() + 10, System.currentTimeMillis() + 20, "this is a relatively long string", new byte[]{5, 4, 3, 2, 1}, 1.2d, true);

    User u1 = users.find(user.getId());
    User u2 = users.find(user.getId());
    assertTrue(u1 == u2);
  }

  public void testFindAllByForeignKey() throws Exception {
    ICommentPersistence comments = dbs.getDatabase1().comments();
    int userId = 1;
    Comment c1 = comments.create("comment1", userId, 1);
    Comment c2 = comments.create("comment2", userId, 1);
    Comment c3 = comments.create("comment3", userId, 1);

    Set<Comment> userComments = comments.findAllByForeignKey("commenter_id", userId);
    assertEquals(3, userComments.size());
    //TODO: test that elements of set are correct
  }

  public void testFindAllByForeignKeyCache() throws Exception {
    ICommentPersistence comments = dbs.getDatabase1().comments();
    int userId = 1;
    comments.create("comment1", userId, 1);
    comments.create("comment2", userId, 1);
    comments.create("comment3", userId, 1);

    Set<Comment> c1 = comments.findAllByForeignKey("commenter_id", userId);
    Set<Comment> c2 = comments.findAllByForeignKey("commenter_id", userId);
    assertTrue(c1 == c2);
  }

  public void testFindAllFromCache() throws Exception {
    IUserPersistence users = dbs.getDatabase1().users();
    User u1 = users.create("bryand", System.currentTimeMillis(), 5, System.currentTimeMillis() + 10, System.currentTimeMillis() + 20, "this is a relatively long string", new byte[]{5, 4, 3, 2, 1}, 1.2d, true);
    User u2 = users.create("thomask", System.currentTimeMillis(), 5, System.currentTimeMillis() + 10, System.currentTimeMillis() + 20, "this is a relatively long string", new byte[]{5, 4, 3, 2, 1}, 1.2d, true);
    User u3 = users.create("emilyl", System.currentTimeMillis(), 5, System.currentTimeMillis() + 10, System.currentTimeMillis() + 20, "this is a relatively long string", new byte[]{5, 4, 3, 2, 1}, 1.2d, true);

    // fills cache
    User u1_1 = users.find(u1.getId());
    User u2_1 = users.find(u2.getId());
    User u3_1 = users.find(u3.getId());
    
    Set<User> allUsers = users.findAll();
    assertTrue(allUsers.contains(u1));
    assertTrue(allUsers.contains(u2));
    assertTrue(allUsers.contains(u3));
    
    // make sure findAll returned cached objects
    int numFound = 0;
    for (User user : allUsers) {
      if (user == u1_1) {
        numFound++;
      } else if (user == u2_1) {
        numFound++;
      } else if (user == u3_1) {
        numFound++;
      }
    }
    assertEquals("findAll did not return cached objects for all 3 users!", 3, numFound);
  }

  public void testFindAllAndCache() throws Exception {
    IUserPersistence users = dbs.getDatabase1().users();
    User u1 = users.create("bryand", System.currentTimeMillis(), 5, System.currentTimeMillis() + 10, System.currentTimeMillis() + 20, "this is a relatively long string", new byte[]{5, 4, 3, 2, 1}, 1.2d, true);
    User u2 = users.create("thomask", System.currentTimeMillis(), 5, System.currentTimeMillis() + 10, System.currentTimeMillis() + 20, "this is a relatively long string", new byte[]{5, 4, 3, 2, 1}, 1.2d, true);
    User u3 = users.create("emilyl", System.currentTimeMillis(), 5, System.currentTimeMillis() + 10, System.currentTimeMillis() + 20, "this is a relatively long string", new byte[]{5, 4, 3, 2, 1}, 1.2d, true);

    Set<User> allUsers = users.findAll();
    assertTrue(allUsers.contains(u1));
    assertTrue(allUsers.contains(u2));
    assertTrue(allUsers.contains(u3));

    User u1_1 = users.find(u1.getId());
    // make sure caching worked as expected
    boolean found = false;
    for (User user : allUsers) {
      if (user == u1_1) {
        found = true;
        break;
      }
    }
    assertTrue("No User instance equivalent to u1_1 was found!", found);
  }

  public void testFindAllWithConditions() throws Exception {
    IUserPersistence users = dbs.getDatabase1().users();
    User u1 = users.create("bryand", System.currentTimeMillis(), 5, System.currentTimeMillis() + 10, System.currentTimeMillis() + 20, "this is a relatively long string", new byte[]{5, 4, 3, 2, 1}, 1.2d, true);
    User u2 = users.create("thomask", System.currentTimeMillis(), 5, System.currentTimeMillis() + 10, System.currentTimeMillis() + 20, "this is a relatively long string", new byte[]{5, 4, 3, 2, 1}, 1.2d, true);
    users.create("emilyl", System.currentTimeMillis(), 5, System.currentTimeMillis() + 10, System.currentTimeMillis() + 20, "this is a relatively long string", new byte[]{5, 4, 3, 2, 1}, 1.2d, true);

    assertEquals(Collections.singleton(u1), users.findAll("handle = 'bryand'"));
    assertEquals(Collections.singleton(u2), users.findAll("handle = 'thomask'"));
  }

  public void testBelongsTo() throws Exception {
    IUserPersistence users = dbs.getDatabase1().users();
    User u1 = users.create("bryand", System.currentTimeMillis(), 5, System.currentTimeMillis() + 10, System.currentTimeMillis() + 20, "this is a relatively long string", new byte[]{5, 4, 3, 2, 1}, 1.2d, true);

    IPostPersistence posts = dbs.getDatabase1().posts();
    Post p1 = posts.create("title", System.currentTimeMillis(), u1.getId());
    assertEquals(u1, p1.getUser());
  }

  public void testHasOne() throws Exception {
    IUserPersistence users = dbs.getDatabase1().users();
    User u1 = users.create("bryand", System.currentTimeMillis(), 5, System.currentTimeMillis() + 10, System.currentTimeMillis() + 20, "this is a relatively long string", new byte[]{5, 4, 3, 2, 1}, 1.2d, true);

    IImagePersistence images = dbs.getDatabase1().images();
    Image image = images.create(u1.getId());
    assertEquals(u1, image.getUser());
  }

  public void testHasMany() throws Exception {
    IUserPersistence users = dbs.getDatabase1().users();
    User u1 = users.create("bryand", System.currentTimeMillis(), 5, System.currentTimeMillis() + 10, System.currentTimeMillis() + 20, "this is a relatively long string", new byte[]{5, 4, 3, 2, 1}, 1.2d, true);

    IPostPersistence posts = dbs.getDatabase1().posts();
    Post p1 = posts.create("title1", System.currentTimeMillis(), u1.getId());
    Post p2 = posts.create("title2", System.currentTimeMillis(), u1.getId());
    Post p3 = posts.create("title3", System.currentTimeMillis(), u1.getId());

    assertEquals(new HashSet<Post>(Arrays.asList(p1, p2, p3)), u1.getPosts());
  }

  public void testFindByForeignKey() throws Exception {
    IPostPersistence posts = dbs.getDatabase1().posts();
    Post post = posts.create("title", 0L, 1);

    assertTrue(posts.findAllByForeignKey("user_id", -1).isEmpty());
    assertEquals(Collections.singleton(post), posts.findAllByForeignKey("user_id", 1));
  }

  public void testNullTreatment() throws Exception {
    IPostPersistence posts = dbs.getDatabase1().posts();
    Post post = posts.create(null, 10L, 1);
    assertNotNull(post);
    post.setUserId(null);
    posts.save(post);
    post = posts.find(post.getId());
    assertNull(post.getUserId());
    assertNull(post.getUser());
  }

  public void testDelete() throws Exception {
    IPostPersistence posts = dbs.getDatabase1().posts();
    Post post = posts.create(null, 10L, 1);
    int id = post.getId();
    posts.delete(id);
    assertNull(posts.find(id));
  }
}
