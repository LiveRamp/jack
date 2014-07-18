package com.rapleaf.jack.test_project.database_1.query;

import com.rapleaf.jack.AbstractModelQuery;

import com.rapleaf.jack.test_project.database_1.models.Post;
import com.rapleaf.jack.test_project.database_1.iface.IPostPersistence;


public class PostQuery extends AbstractModelQuery<Post> {

  public PostQuery (IPostPersistence caller) {
    super(caller);
  }

  public PostQuery title(String value) {
    fieldsMap.put(Post._Fields.title, value);
    return this;
  }

  public PostQuery posted_at_millis(Long value) {
    fieldsMap.put(Post._Fields.posted_at_millis, value);
    return this;
  }

  public PostQuery user_id(Integer value) {
    fieldsMap.put(Post._Fields.user_id, value);
    return this;
  }

  public PostQuery updated_at(Long value) {
    fieldsMap.put(Post._Fields.updated_at, value);
    return this;
  }
}
