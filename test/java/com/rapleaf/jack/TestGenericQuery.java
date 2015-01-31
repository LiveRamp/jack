package com.rapleaf.jack;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Sets;
import junit.framework.TestCase;

import com.rapleaf.jack.queries.GenericQuery;

import com.rapleaf.jack.queries.QueryEntry;
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

    User userA = users.createDefaultInstance().setHandle("A").setBio("Assembly Coder").setNumPosts(10);
    User userB = users.createDefaultInstance().setHandle("B").setBio("Snowman Builder").setNumPosts(30);
    User userC = users.createDefaultInstance().setHandle("C").setBio("Spaceship Pilot").setNumPosts(20);
    User userD = users.createDefaultInstance().setHandle("D").setBio("PDP-10 Hacker").setNumPosts(40);
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

    List<QueryEntry> results = GenericQuery.create(DATABASE_CONNECTION1)
        .from(User.class)
        .leftJoin(Comment.class, User.ID, Comment.COMMENTER_ID)
        .where(User.BIO, in(Sets.newHashSet("Assembly Coder", "Spaceship Pilot", "PDP-10 Hacker")))
        .where(Comment.CONTENT, isNotNull())
        .orderBy(User.NUM_POSTS, DESC)
        .orderBy(Comment.ID)
        .select(User.ID, User.BIO, User.HANDLE, User.NUM_POSTS, Comment.ID, Comment.COMMENTER_ID, Comment.CONTENT)
        .fetch();

    for (QueryEntry entry : results) {
      System.out.println(entry.getString(User.BIO));
      System.out.println(entry.getLong(User.ID));
      System.out.println(entry.getInteger(User.NUM_POSTS));
    }
  }
}
