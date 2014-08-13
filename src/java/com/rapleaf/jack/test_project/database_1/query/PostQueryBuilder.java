package com.rapleaf.jack.test_project.database_1.query;

import java.util.Set;

import com.rapleaf.jack.AbstractQueryBuilder;
import com.rapleaf.jack.IQueryOperator;
import com.rapleaf.jack.JackMatchers;
import com.rapleaf.jack.QueryConstraint;
import com.rapleaf.jack.test_project.database_1.iface.IPostPersistence;
import com.rapleaf.jack.test_project.database_1.models.Post;


public class PostQueryBuilder extends AbstractQueryBuilder<Post> {

  public PostQueryBuilder(IPostPersistence caller) {
    super(caller);
  }

  public PostQueryBuilder id(Long value) {
    addId(value);
    return this;
  }

  public PostQueryBuilder id(Set<Long> values) {
    addIds(values);
    return this;
  }

  public PostQueryBuilder title(String value) {
    addConstraint(new QueryConstraint<String>(Post._Fields.title, JackMatchers.equalTo(value)));
    return this;
  }

  public PostQueryBuilder title(IQueryOperator<String> operator) {
    addConstraint(new QueryConstraint<String>(Post._Fields.title, operator));
    return this;
  }

  public PostQueryBuilder postedAtMillis(Long value) {
    addConstraint(new QueryConstraint<Long>(Post._Fields.posted_at_millis, JackMatchers.equalTo(value)));
    return this;
  }

  public PostQueryBuilder postedAtMillis(IQueryOperator<Long> operator) {
    addConstraint(new QueryConstraint<Long>(Post._Fields.posted_at_millis, operator));
    return this;
  }

  public PostQueryBuilder userId(Integer value) {
    addConstraint(new QueryConstraint<Integer>(Post._Fields.user_id, JackMatchers.equalTo(value)));
    return this;
  }

  public PostQueryBuilder userId(IQueryOperator<Integer> operator) {
    addConstraint(new QueryConstraint<Integer>(Post._Fields.user_id, operator));
    return this;
  }

  public PostQueryBuilder updatedAt(Long value) {
    addConstraint(new QueryConstraint<Long>(Post._Fields.updated_at, JackMatchers.equalTo(value)));
    return this;
  }

  public PostQueryBuilder updatedAt(IQueryOperator<Long> operator) {
    addConstraint(new QueryConstraint<Long>(Post._Fields.updated_at, operator));
    return this;
  }
}
