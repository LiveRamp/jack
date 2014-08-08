package com.rapleaf.jack.test_project.database_1.query;

import com.rapleaf.jack.AbstractQueryBuilder;

import com.rapleaf.jack.test_project.database_1.models.Comment;
import com.rapleaf.jack.test_project.database_1.iface.ICommentPersistence;


public class CommentQueryBuilder extends AbstractQueryBuilder<Comment> {

  public CommentQueryBuilder (ICommentPersistence caller) {
    super(caller);
  }

  public CommentQueryBuilder content(String value) {
    addConstraint(JackMatchers.equalTo(value));
    return this;
  }

  public CommentQueryBuilder content(QueryConstraint<String value) {
    addConstraint(JackMatchers.equalTo(value));
    return this;
  }

  public CommentQueryBuilder commenterId(int value) {
    addConstraint(JackMatchers.equalTo(value));
    return this;
  }

  public CommentQueryBuilder commenterId(QueryConstraint<Integer value) {
    addConstraint(JackMatchers.equalTo(value));
    return this;
  }

  public CommentQueryBuilder commentedOnId(long value) {
    addConstraint(JackMatchers.equalTo(value));
    return this;
  }

  public CommentQueryBuilder commentedOnId(QueryConstraint<Long value) {
    addConstraint(JackMatchers.equalTo(value));
    return this;
  }

  public CommentQueryBuilder createdAt(long value) {
    addConstraint(JackMatchers.equalTo(value));
    return this;
  }

  public CommentQueryBuilder createdAt(QueryConstraint<Long value) {
    addConstraint(JackMatchers.equalTo(value));
    return this;
  }
}
