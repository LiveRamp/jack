package com.rapleaf.jack;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;
import junit.framework.TestCase;

import com.rapleaf.jack.generic_queries.GenericQuery;

import com.rapleaf.jack.test_project.DatabasesImpl;
import com.rapleaf.jack.test_project.IDatabases;
import com.rapleaf.jack.test_project.database_1.iface.ICommentPersistence;
import com.rapleaf.jack.test_project.database_1.iface.IUserPersistence;
import com.rapleaf.jack.test_project.database_1.models.Comment;
import com.rapleaf.jack.test_project.database_1.models.User;

import static com.rapleaf.jack.queries.QueryOrder.*;
import static com.rapleaf.jack.queries.where_operators.JackMatchers.*;

public class TestGenericQuery extends TestCase {

  private static final DatabaseConnection DATABASE_CONNECTION1 = new DatabaseConnection("database1");

  public void test() throws IOException, SQLException {
    IDatabases dbs = new DatabasesImpl(DATABASE_CONNECTION1);
    IUserPersistence users = dbs.getDatabase1().users();
    ICommentPersistence comments = dbs.getDatabase1().comments();
    users.deleteAll();
    comments.deleteAll();

    User userA = users.createDefaultInstance().setHandle("A").setBio("Football Coach").setNumPosts(10);
    User userB = users.createDefaultInstance().setHandle("B").setBio("Snowman Builder").setNumPosts(30);
    User userC = users.createDefaultInstance().setHandle("C").setBio("Spaceship Rider").setNumPosts(20);
    User userD = users.createDefaultInstance().setHandle("D").setBio("PDP-10 Engineer").setNumPosts(40);
    userA.save();
    userB.save();
    userC.save();
    userD.save();

    Comment commentA = comments.createDefaultInstance().setCommenterId(ModelWithId.safeLongToInt(userA.getId())).setContent("comments");
    Comment commentB = comments.createDefaultInstance().setCommenterId(ModelWithId.safeLongToInt(userB.getId())).setContent("comments");
    Comment commentC = comments.createDefaultInstance().setCommenterId(ModelWithId.safeLongToInt(userB.getId())).setContent("comments");
    Comment commentD = comments.createDefaultInstance().setCommenterId(ModelWithId.safeLongToInt(userC.getId())).setContent("comments");
    Comment commentE = comments.createDefaultInstance().setCommenterId(ModelWithId.safeLongToInt(userD.getId())).setContent("comments");
    commentA.save();
    commentB.save();
    commentC.save();
    commentD.save();
    commentE.save();

    List<Map<ModelField, Object>> results = GenericQuery.create(DATABASE_CONNECTION1)
        .from(User.class)
        .leftJoin(Comment.class, User.id(), Comment.commenter_id())
        .where(User.bio(), in(Sets.newHashSet("Football Coach", "Snowman Builder", "Spaceship Rider")))
        .where(Comment.content(), isNotNull())
        .orderBy(User.num_posts(), DESC)
        .orderBy(Comment.id())
        .select(User.id(), User.bio(), User.handle(), User.num_posts(), Comment.id(), Comment.commenter_id(), Comment.content())
        .fetch();

    for (Map<ModelField, Object> record : results) {
      System.out.println(record.toString());
    }
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
