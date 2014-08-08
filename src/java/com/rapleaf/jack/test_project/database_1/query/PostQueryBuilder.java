package com.rapleaf.jack.test_project.database_1.query;

import com.rapleaf.jack.AbstractQueryBuilder;

import com.rapleaf.jack.JackMatchers;
import com.rapleaf.jack.test_project.database_1.models.Post;
import com.rapleaf.jack.test_project.database_1.iface.IPostPersistence;


public class PostQueryBuilder extends AbstractQueryBuilder<Post> {

  public PostQueryBuilder (IPostPersistence caller) {
    super(caller);
  }

  public PostQueryBuilder title(String value) {
    addConstraint(JackMatchers.equalTo(value));
    return this;
  }

  public PostQueryBuilder title(QueryConstraint<String value) {
    addConstraint(JackMatchers.equalTo(value));
    return this;
  }

  public PostQueryBuilder postedAtMillis(Long value) {
    addConstraint(JackMatchers.equalTo(value));
    return this;
  }

  public PostQueryBuilder postedAtMillis(QueryConstraint<Long value) {
    addConstraint(JackMatchers.equalTo(value));
    return this;
  }

  public PostQueryBuilder userId(Integer value) {
    addConstraint(JackMatchers.equalTo(value));
    return this;
  }

  public PostQueryBuilder userId(QueryConstraint<Integer value) {
    addConstraint(JackMatchers.equalTo(value));
    return this;
  }

  public PostQueryBuilder updatedAt(Long value) {
    addConstraint(JackMatchers.equalTo(value));
    return this;
  }

  public PostQueryBuilder updatedAt(QueryConstraint<Long value) {
    addConstraint(JackMatchers.equalTo(value));
    return this;
  }
}
