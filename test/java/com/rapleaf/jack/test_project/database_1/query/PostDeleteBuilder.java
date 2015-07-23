package com.rapleaf.jack.test_project.database_1.query;

import java.util.Set;

import com.rapleaf.jack.queries.AbstractDeleteBuilder;
import com.rapleaf.jack.queries.where_operators.IWhereOperator;
import com.rapleaf.jack.queries.where_operators.JackMatchers;
import com.rapleaf.jack.queries.WhereConstraint;
import com.rapleaf.jack.test_project.database_1.iface.IPostPersistence;
import com.rapleaf.jack.test_project.database_1.models.Post;


public class PostDeleteBuilder extends AbstractDeleteBuilder<Post> {

  public PostDeleteBuilder(IPostPersistence caller) {
    super(caller);
  }

  public PostDeleteBuilder id(Long value) {
    addId(value);
    return this;
  }

  public PostDeleteBuilder idIn(Set<Long> values) {
    addIds(values);
    return this;
  }

  public PostDeleteBuilder title(String value) {
    addWhereConstraint(new WhereConstraint<String>(Post._Fields.title, JackMatchers.equalTo(value)));
    return this;
  }

  public PostDeleteBuilder whereTitle(IWhereOperator<String> operator) {
    addWhereConstraint(new WhereConstraint<String>(Post._Fields.title, operator));
    return this;
  }

  public PostDeleteBuilder postedAtMillis(Long value) {
    addWhereConstraint(new WhereConstraint<Long>(Post._Fields.posted_at_millis, JackMatchers.equalTo(value)));
    return this;
  }

  public PostDeleteBuilder wherePostedAtMillis(IWhereOperator<Long> operator) {
    addWhereConstraint(new WhereConstraint<Long>(Post._Fields.posted_at_millis, operator));
    return this;
  }

  public PostDeleteBuilder userId(Integer value) {
    addWhereConstraint(new WhereConstraint<Integer>(Post._Fields.user_id, JackMatchers.equalTo(value)));
    return this;
  }

  public PostDeleteBuilder whereUserId(IWhereOperator<Integer> operator) {
    addWhereConstraint(new WhereConstraint<Integer>(Post._Fields.user_id, operator));
    return this;
  }

  public PostDeleteBuilder updatedAt(Long value) {
    addWhereConstraint(new WhereConstraint<Long>(Post._Fields.updated_at, JackMatchers.equalTo(value)));
    return this;
  }

  public PostDeleteBuilder whereUpdatedAt(IWhereOperator<Long> operator) {
    addWhereConstraint(new WhereConstraint<Long>(Post._Fields.updated_at, operator));
    return this;
  }
}
