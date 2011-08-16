package com.rapleaf.jack;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import junit.framework.TestCase;

import com.rapleaf.jack.test_project.IDatabases;
import com.rapleaf.jack.test_project.database_1.iface.ICommentPersistence;
import com.rapleaf.jack.test_project.database_1.iface.IImagePersistence;
import com.rapleaf.jack.test_project.database_1.iface.IPostPersistence;
import com.rapleaf.jack.test_project.database_1.iface.IUserPersistence;
import com.rapleaf.jack.test_project.database_1.models.Comment;
import com.rapleaf.jack.test_project.database_1.models.Image;
import com.rapleaf.jack.test_project.database_1.models.Post;
import com.rapleaf.jack.test_project.database_1.models.User;

public abstract class BaseDatabaseModelTestCase extends TestCase {

  protected final IDatabases dbs = getDBS();
  
  public abstract IDatabases getDBS();

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    dbs.getDatabase1().deleteAll();
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
    assertEquals(5, bryand.getNumPosts());
    assertEquals(Long.valueOf(t1), bryand.getSomeDate());
    assertEquals(Long.valueOf(t2), bryand.getSomeDatetime());
    assertEquals("this is a relatively long string", bryand.getBio());
    assertEquals(ByteBuffer.wrap(someBinary), ByteBuffer.wrap(bryand.getSomeBinary()));
    assertEquals(1.2, bryand.getSomeFloat());
    assertTrue(bryand.isSomeBoolean());
    
    assertTrue(bryand == users.find(bryand.getId()));
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
    assertEquals(5, bryand_again.getNumPosts());
    // need to figure out what the appropriate rounding is...
//    assertEquals(Long.valueOf(t1), bryand_again.getSomeDate());
    // need to figure out what the appropriate rounding is...
//    assertEquals(Long.valueOf(t2), bryand_again.getSomeDatetime());
    assertEquals("this is a relatively long string", bryand_again.getBio());
    assertEquals(ByteBuffer.wrap(someBinary), ByteBuffer.wrap(bryand_again.getSomeBinary()));
    assertEquals(1.2, bryand_again.getSomeFloat());
    assertTrue(bryand_again.isSomeBoolean());
  }
  
  public void testFindEmptySet() throws Exception {
    IUserPersistence users = dbs.getDatabase1().users();
    Set<User> foundValues = users.find(new HashSet<Integer>());
    assertEquals(0, foundValues.size());
  }
  
  public void testFindSet() throws Exception {
    IUserPersistence users = dbs.getDatabase1().users();
    long t0 = System.currentTimeMillis();
    long t1 = t0 + 10;
    long t2 = t0 + 20;
    byte[] someBinary = new byte[]{5, 4, 3, 2, 1};
    User bryand = users.create("bryand", t0, 5, t1, t2, "this is a relatively long string", someBinary, 1.2d, true);
    User notBryand = users.create("notBryand", t0, 3, t1, t2, "another relatively long string", someBinary, 1.2d, true);
    users.create("unwanted", t0, 0, t1, t2, "yet another relatively long string", someBinary, 1.2d, true);

    users.clearCacheById(bryand.getId());
    users.clearCacheById(notBryand.getId());
    Set<Integer> keysToSearch = new HashSet<Integer>();
    keysToSearch.add(bryand.getId());
    keysToSearch.add(notBryand.getId());
    Set<User> foundValues = users.find(keysToSearch);
    
    assertEquals(2, foundValues.size());
    Iterator<User> iter = foundValues.iterator();
    User bryand_again = null;
    User notBryand_again = null;
    while(iter.hasNext()) {
      User curUser = iter.next();
      if(curUser.getId() == bryand.getId()) {
        bryand_again = curUser;
      } else if(curUser.getId() == notBryand.getId()) {
        notBryand_again = curUser;
      } else {
        fail("Unexpected user id: " + curUser.getId());
      }
    }
    assertNotNull(bryand_again);
    assertNotNull(notBryand_again);

    assertEquals(bryand.getId(), bryand_again.getId());
    assertEquals("bryand", bryand_again.getHandle());
    assertEquals(Long.valueOf(t0), bryand_again.getCreatedAtMillis());
    assertEquals(5, bryand_again.getNumPosts());
    // need to figure out what the appropriate rounding is...
//    assertEquals(Long.valueOf(t1), bryand_again.getSomeDate());
    // need to figure out what the appropriate rounding is...
//    assertEquals(Long.valueOf(t2), bryand_again.getSomeDatetime());
    assertEquals("this is a relatively long string", bryand_again.getBio());
    assertEquals(ByteBuffer.wrap(someBinary), ByteBuffer.wrap(bryand_again.getSomeBinary()));
    assertEquals(1.2, bryand_again.getSomeFloat());
    assertTrue(bryand_again.isSomeBoolean());
    
    assertEquals(notBryand.getId(), notBryand_again.getId());
    assertEquals("notBryand", notBryand_again.getHandle());
    assertEquals(Long.valueOf(t0), notBryand_again.getCreatedAtMillis());
    assertEquals(3, notBryand_again.getNumPosts());
    // need to figure out what the appropriate rounding is...
//    assertEquals(Long.valueOf(t1), bryand_again.getSomeDate());
    // need to figure out what the appropriate rounding is...
//    assertEquals(Long.valueOf(t2), bryand_again.getSomeDatetime());
    assertEquals("another relatively long string", notBryand_again.getBio());
    assertEquals(ByteBuffer.wrap(someBinary), ByteBuffer.wrap(notBryand_again.getSomeBinary()));
    assertEquals(1.2, notBryand_again.getSomeFloat());
    assertTrue(notBryand_again.isSomeBoolean());
  }


  public void testFindSetFromCache() throws Exception {
    IUserPersistence users = dbs.getDatabase1().users();
    long t0 = System.currentTimeMillis();
    long t1 = t0 + 10;
    long t2 = t0 + 20;
    byte[] someBinary = new byte[]{5, 4, 3, 2, 1};
    User bryand = users.create("bryand", t0, 5, t1, t2, "this is a relatively long string", someBinary, 1.2d, true);
    User notBryand = users.create("notBryand", t0, 3, t1, t2, "another relatively long string", someBinary, 1.2d, true);
    users.create("unwanted", t0, 0, t1, t2, "yet another relatively long string", someBinary, 1.2d, true);

    Set<Integer> keysToSearch = new HashSet<Integer>();
    keysToSearch.add(bryand.getId());
    keysToSearch.add(notBryand.getId());
    Set<User> foundValues = users.find(keysToSearch);
    
    assertEquals(2, foundValues.size());
    Iterator<User> iter = foundValues.iterator();
    User bryand_again = users.find(bryand.getId());
    User notBryand_again = users.find(notBryand.getId());
    while(iter.hasNext()) {
      User curUser = iter.next();
      if(curUser.getId() == bryand.getId()) {
        assertTrue(bryand_again == curUser);
      } else if(curUser.getId() == notBryand.getId()) {
        assertTrue(notBryand_again == curUser);
      } else {
        fail("Unexpected user id: " + curUser.getId());
      }
    }
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
    assertTrue(userComments.contains(c1));
    assertTrue(userComments.contains(c2));
    assertTrue(userComments.contains(c3));
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

  public void testFindAllByForeignKeyFromSet() throws Exception {
    ICommentPersistence comments = dbs.getDatabase1().comments();
    int userId = 1;
    int otherUserId = 2;
    Comment c1 = comments.create("comment1", userId, 1);
    Comment c2 = comments.create("comment2", userId, 1);
    Comment c3 = comments.create("comment3", userId, 1);
    Comment c4 = comments.create("comment4", otherUserId, 1);
    Comment c5 = comments.create("comment5", 3, 1);

    Set<Integer> commenterIds = new HashSet<Integer>();
    commenterIds.add(userId);
    commenterIds.add(otherUserId);
    Set<Comment> userComments = comments.findAllByForeignKey("commenter_id", commenterIds);
    assertEquals(4, userComments.size());
    assertTrue(userComments.contains(c1));
    assertTrue(userComments.contains(c2));
    assertTrue(userComments.contains(c3));
    assertTrue(userComments.contains(c4));
    assertFalse(userComments.contains(c5));

    Set<Comment> userCommentsSecondQuery = comments.findAllByForeignKey("commenter_id", commenterIds);
    assertEquals(4, userCommentsSecondQuery.size());
    assertTrue(userCommentsSecondQuery.contains(c1));
    assertTrue(userCommentsSecondQuery.contains(c2));
    assertTrue(userCommentsSecondQuery.contains(c3));
    assertTrue(userCommentsSecondQuery.contains(c4));
    assertFalse(userCommentsSecondQuery.contains(c5));
  }

  public void testFindAllWithConditions() throws Exception {
    IUserPersistence users = dbs.getDatabase1().users();
    User u1 = users.create("bryand", System.currentTimeMillis(), 5, System.currentTimeMillis() + 10, System.currentTimeMillis() + 20, "this is a relatively long string", new byte[]{5, 4, 3, 2, 1}, 1.2d, true);
    User u2 = users.create("thomask", System.currentTimeMillis(), 5, System.currentTimeMillis() + 10, System.currentTimeMillis() + 20, "this is a relatively long string", new byte[]{5, 4, 3, 2, 1}, 1.2d, true);
    users.create("emilyl", System.currentTimeMillis(), 5, System.currentTimeMillis() + 10, System.currentTimeMillis() + 20, "this is a relatively long string", new byte[]{5, 4, 3, 2, 1}, 1.2d, true);

    assertEquals(Collections.singleton(u1), users.findAll("handle = 'bryand'"));
    assertEquals(Collections.singleton(u2), users.findAll("handle = 'thomask'"));
    assertEquals(Collections.singleton(u2), users.findAll("handle = 'thomask' AND num_posts=5"));
    assertEquals(Collections.singleton(u2), users.findAll("handle = 'thomask' AND created_at_millis<=" + System.currentTimeMillis()));
    assertEquals(Collections.singleton(u2), users.findAll("handle != 'bryand' AND handle != 'emilyl' AND created_at_millis<=" + System.currentTimeMillis()));
    assertEquals(Collections.singleton(u2), users.findAll("handle != 'bryand' AND handle != 'emilyl' AND 0.5 < 1.0e1"));
  }
  
  public void testFindAllWithNullValues() throws Exception {
    IUserPersistence users = dbs.getDatabase1().users();
    User u1 = users.create("bryand", System.currentTimeMillis(), 5, System.currentTimeMillis() + 10, System.currentTimeMillis() + 20, "this is a relatively long string", new byte[]{5, 4, 3, 2, 1}, 1.2d, true);
    User u2 = users.create("thomask", null, 5, System.currentTimeMillis() + 10, System.currentTimeMillis() + 20, "this is a relatively long string", new byte[]{5, 4, 3, 2, 1}, 1.2d, true);

    assertEquals(Collections.singleton(u1), users.findAll("created_at_millis IS NOT NULL"));
    assertEquals(Collections.singleton(u2), users.findAll("created_at_millis IS NULL"));
    assertEquals(Collections.singleton(u1), users.findAll("(created_at_millis IS NULL) IS NOT TRUE"));
    assertEquals(Collections.singleton(u2), users.findAll("(created_at_millis IS NULL) IS TRUE"));
  }
  
  public void testFindAllWithLikeConditions() throws Exception {
    IUserPersistence users = dbs.getDatabase1().users();
    User u1 = users.create("bryand", System.currentTimeMillis(), 5, System.currentTimeMillis() + 10, System.currentTimeMillis() + 20, "this is a relatively long string", new byte[]{5, 4, 3, 2, 1}, 1.2d, true);
    User u2 = users.create("thomask", null, 5, System.currentTimeMillis() + 10, System.currentTimeMillis() + 20, "this is a relatively long string", new byte[]{5, 4, 3, 2, 1}, 1.2d, true);
    User u3 = users.create("as%df", null, 5, System.currentTimeMillis() + 10, System.currentTimeMillis() + 20, "this is a relatively long string", new byte[]{5, 4, 3, 2, 1}, 1.2d, true);

    assertEquals(Collections.singleton(u1), users.findAll("handle LIKE \"bryan_\""));
    assertEquals(Collections.singleton(u2), users.findAll("handle LIKE \"%o%m%as%\""));
    assertEquals(Collections.singleton(u2), users.findAll("handle NOT LIKE \"bryan_\" AND handle != 'as%df'"));
    assertEquals(Collections.singleton(u1), users.findAll("handle NOT LIKE \"%omas%\" AND handle != 'as%df'"));
    assertEquals(Collections.EMPTY_SET, users.findAll("handle LIKE \"%/tmp/directory/1%\""));
    
  }
  
  public void testFindAllWithNumericInConditions() throws Exception {
    IUserPersistence users = dbs.getDatabase1().users();
    User u1 = users.create("bryand", System.currentTimeMillis(), 5, System.currentTimeMillis() + 10, System.currentTimeMillis() + 20, "this is a relatively long string", new byte[]{5, 4, 3, 2, 1}, 1.2d, true);
    User u2 = users.create("thomask", null, 3, System.currentTimeMillis() + 10, System.currentTimeMillis() + 20, "this is a relatively long string", new byte[]{5, 4, 3, 2, 1}, 1.2d, true);

    assertEquals(Collections.singleton(u1), users.findAll("5 > 4 AND num_posts in (1 , 5)"));
    assertEquals(Collections.singleton(u2), users.findAll("5 < 4 OR num_posts in (3 , 7)"));
    assertEquals(Collections.singleton(u2), users.findAll("num_posts not in (1 , 5) OR 5 < 4"));
    assertEquals(Collections.singleton(u1), users.findAll("num_posts not in (3 , 7) AND 5 > 4"));
  }

  public void testFindAllWithStringInConditions() throws Exception {
    IUserPersistence users = dbs.getDatabase1().users();
    User u1 = users.create("bryand", System.currentTimeMillis(), 5, System.currentTimeMillis() + 10, System.currentTimeMillis() + 20, "this is a relatively long string", new byte[]{5, 4, 3, 2, 1}, 1.2d, true);
    User u2 = users.create("thomask", null, 3, System.currentTimeMillis() + 10, System.currentTimeMillis() + 20, "this is a relatively long string", new byte[]{5, 4, 3, 2, 1}, 1.2d, true);

    assertEquals(Collections.singleton(u1), users.findAll("5 > 4 AND handle in (\"bryand\" , 'asdf')"));
    assertEquals(Collections.singleton(u2), users.findAll("5 < 4 OR handle in ('thomask' , 'aswer')"));
    assertEquals(Collections.singleton(u2), users.findAll("handle not in ('asd' , 'bryand') OR 5 < 4"));
    assertEquals(Collections.singleton(u1), users.findAll("handle not in (\"thomask\" , 'wers') AND 5 > 4"));
  }
  
  public void testFindAllWithEscapedQuotesInStrings() throws Exception {
    IUserPersistence users = dbs.getDatabase1().users();
    User u1 = users.create("brya'nd", System.currentTimeMillis(), 5, System.currentTimeMillis() + 10, System.currentTimeMillis() + 20, "this is a relatively long string", new byte[]{5, 4, 3, 2, 1}, 1.2d, true);
    User u2 = users.create("thoma\"sk", null, 5, System.currentTimeMillis() + 10, System.currentTimeMillis() + 20, "this is a relatively long string", new byte[]{5, 4, 3, 2, 1}, 1.2d, true);

    assertEquals(Collections.singleton(u1), users.findAll("handle = 'brya\\\'nd'"));
    assertEquals(Collections.singleton(u2), users.findAll("handle = 'thoma\"sk'"));
    assertEquals(Collections.singleton(u1), users.findAll("handle = \"brya\'nd\""));
    assertEquals(Collections.singleton(u2), users.findAll("handle = \"thoma\\\"sk\""));
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
  
  public void testSave() throws Exception {
    IPostPersistence posts = dbs.getDatabase1().posts();
    Post post = posts.create(null, 10L, 1);
    int id = post.getId();
    post.setPostedAtMillis(20L);
    dbs.getDatabase1().posts().save(post);
    assertEquals(Long.valueOf(20), posts.find(id).getPostedAtMillis());
  }
  
  public void testInsertOnSave() throws Exception {
    IPostPersistence posts = dbs.getDatabase1().posts();
    Post post = new Post(50, "Post", 20L, 100, dbs);
    posts.save(post);
    assertEquals(post, posts.find(50));
  }
}
