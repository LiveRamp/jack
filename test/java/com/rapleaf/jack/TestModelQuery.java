package com.rapleaf.jack;

import junit.framework.TestCase;
import org.junit.Test;

import com.rapleaf.jack.test_project.DatabasesImpl;
import com.rapleaf.jack.test_project.IDatabases;
import com.rapleaf.jack.test_project.database_1.models.Post;

public class TestModelQuery extends TestCase {

  private static final DatabaseConnection DATABASE_CONNECTION1 = new DatabaseConnection("database1");

  @Test
  public void testQuery() {

    IDatabases dbs = new DatabasesImpl(DATABASE_CONNECTION1);
    Post post1 = new Post(0, "First Post", 100l, 1, 0l);
    Post post2 = new Post(1L, "Second Post", 100l, 2, 0l);

  }
}
