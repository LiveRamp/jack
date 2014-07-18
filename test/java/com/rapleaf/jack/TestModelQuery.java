package com.rapleaf.jack;

import junit.framework.TestCase;

import com.rapleaf.jack.test_project.IDatabases;
import com.rapleaf.jack.test_project.database_1.models.Image;
import com.rapleaf.jack.test_project.database_1.models.Post;
import com.rapleaf.jack.test_project.database_1.models.User;

public class TestModelQuery extends TestCase {

  private static final DatabaseConnection DATABASE_CONNECTION1 = new DatabaseConnection("database1");
  private Post postModel;
  private IDatabases dbs;
  private Image imageModel;
  private User userModel;

  
}
