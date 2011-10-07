package com.rapleaf.jack;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

import com.rapleaf.jack.test_project.DatabasesImpl;
import com.rapleaf.jack.test_project.IDatabases;
import com.rapleaf.jack.test_project.database_1.iface.ICommentPersistence;
import com.rapleaf.jack.test_project.database_1.iface.IUserPersistence;
import com.rapleaf.jack.test_project.database_1.models.Comment;
import com.rapleaf.jack.test_project.database_1.models.User;

public class TestAbstractDatabaseModel extends BaseDatabaseModelTestCase {
  private static final DatabaseConnection DATABASE_CONNECTION1 = new DatabaseConnection("database1");
  
  @Override
  public IDatabases getDBS() {
    return new DatabasesImpl(DATABASE_CONNECTION1);
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

  public void testFindWithFieldsMap() throws IOException {
    IUserPersistence users = dbs.getDatabase1().users();

    User u1 = users.create("a_handle", 2);
    User u2 = users.create("another_handle", 2);

    Set<User> found = users.find(new HashMap<User._Fields, Object>(){{put(User._Fields.handle, "a_handle");}});
    assertEquals(1, found.size());
    assertTrue(found.contains(u1));

    found = users.find(new HashMap<User._Fields, Object>(){{put(User._Fields.num_posts, 2);}});
    assertEquals(2, found.size());
    assertTrue(found.contains(u1));
    assertTrue(found.contains(u2));
  }

}
