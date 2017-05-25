package com.rapleaf.jack;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.Test;

import com.rapleaf.jack.queries.Column;
import com.rapleaf.jack.queries.Record;
import com.rapleaf.jack.queries.Records;
import com.rapleaf.jack.test_project.DatabasesImpl;
import com.rapleaf.jack.test_project.IDatabases;
import com.rapleaf.jack.test_project.database_1.IDatabase1;
import com.rapleaf.jack.test_project.database_1.models.Post;
import com.rapleaf.jack.test_project.database_1.models.User;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestDbImpl extends BaseDatabaseModelTestCase {
  private static final DatabaseConnection DATABASE_CONNECTION1 = new DatabaseConnection("database1");

  @Override
  public IDatabases getDBS() {
    return new DatabasesImpl(DATABASE_CONNECTION1);
  }

  @Test
  public void testClose() throws Exception {
    assertFalse(DATABASE_CONNECTION1.conn == null);
    assertFalse(DATABASE_CONNECTION1.conn.isClosed());

    getDBS().getDatabase1().close();
    assertTrue(DATABASE_CONNECTION1.conn == null);
  }

  @Test
  public void testFindBySql() throws SQLException, IOException {
    final IDatabase1 database1 = getDBS().getDatabase1();
    final User user1 = database1.users().create("auser", 400);
    final User user2 = database1.users().create("someuser", 300);
    final Post post1 = database1.posts().create("atitle", 1L, user1.getIntId(), 300L);
    final Post post2 = database1.posts().create("secondtitle", 2L, user1.getIntId(), 303L);
    database1.posts().create("anothertitle", 1L, user2.getIntId(), 300L);
    final Records records = database1.findBySql("SELECT * FROM posts WHERE posts.user_id IN (SELECT users.id FROM users WHERE users.num_posts > ?)", Lists.newArrayList(300), Sets.<Column>newHashSet(Post.ID, Post.TITLE));
    List<String> titles = Lists.newArrayList();
    List<Long> ids = Lists.newArrayList();
    for (Record record : records) {
      titles.add(record.getString(Post.TITLE));
      ids.add(record.getLong(Post.ID));
    }

    assertEquals(Lists.newArrayList("atitle", "secondtitle"), titles);
    assertEquals(Lists.newArrayList(post1.getId(), post2.getId()), ids);
  }
}
