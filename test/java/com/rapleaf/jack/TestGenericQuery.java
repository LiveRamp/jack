package com.rapleaf.jack;

import java.io.IOException;
import java.sql.SQLException;

import junit.framework.TestCase;

import com.rapleaf.jack.generic_queries.GenericQuery;

import com.rapleaf.jack.test_project.IDatabases;
import com.rapleaf.jack.test_project.database_1.iface.IUserPersistence;
import com.rapleaf.jack.test_project.database_1.models.Comment;
import com.rapleaf.jack.test_project.database_1.models.User;

import static com.rapleaf.jack.IModelField.*;
import static com.rapleaf.jack.queries.QueryOrder.*;
import static com.rapleaf.jack.queries.where_operators.JackMatchers.*;

public class TestGenericQuery extends TestCase {

  private static final DatabaseConnection DATABASE_CONNECTION1 = new DatabaseConnection("database1");

  public void test() throws IOException, SQLException {
    String statement = GenericQuery.create(DATABASE_CONNECTION1)
        .from(User.class)
        .join(Comment.class, fieldKey(User.class), field(Comment.class, Comment._Fields.commenter_id))
        .where(field(User.class, User._Fields.bio), equalTo("Trader"))
        .orderBy(field(User.class, User._Fields.num_posts), DESC)
        .orderBy(fieldKey(Comment.class))
        .getSqlStatement(true);

    System.out.println(statement);
  }

  public void testBasicQuery(IDatabases dbs) throws IOException, SQLException {

    IUserPersistence users = dbs.getDatabase1().users();
    users.deleteAll();

    User userA = users.createDefaultInstance().setHandle("A").setBio("Trader").setNumPosts(1);
    User userB = users.createDefaultInstance().setHandle("B").setBio("Trader").setNumPosts(2);
    User userC = users.createDefaultInstance().setHandle("C").setBio("CEO").setNumPosts(2);
    User userD = users.createDefaultInstance().setHandle("D").setBio("Janitor").setNumPosts(3);
    userA.save();
    userB.save();
    userC.save();
    userD.save();
  }
}
