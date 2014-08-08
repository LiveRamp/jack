package com.rapleaf.jack.test_project.database_1.query;

import com.rapleaf.jack.SimpleQueryBuilder;

import com.rapleaf.jack.test_project.database_1.models.Post;
import com.rapleaf.jack.test_project.database_1.iface.IPostPersistence;


public class PostQueryBuilder extends SimpleQueryBuilder<Post> {

  public PostQueryBuilder (IPostPersistence caller) {
    super(caller);
  }

  public PostQueryBuilder title(String value) {
    fieldsMap.put(Post._Fields.title, value);
    return this;
  }

  public PostQueryBuilder postedAtMillis(Long value) {
    fieldsMap.put(Post._Fields.posted_at_millis, value);
    return this;
  }

  public PostQueryBuilder userId(Integer value) {
    fieldsMap.put(Post._Fields.user_id, value);
    return this;
  }

  public PostQueryBuilder updatedAt(Long value) {
    fieldsMap.put(Post._Fields.updated_at, value);
    return this;
  }
}
