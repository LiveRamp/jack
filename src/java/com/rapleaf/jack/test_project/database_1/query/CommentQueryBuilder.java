package com.rapleaf.jack.test_project.database_1.query;

import java.util.Set;

import com.rapleaf.jack.AbstractQueryBuilder;
import com.rapleaf.jack.IQueryOperator;
import com.rapleaf.jack.JackMatchers;
import com.rapleaf.jack.QueryConstraint;
import com.rapleaf.jack.test_project.database_1.iface.ICommentPersistence;
import com.rapleaf.jack.test_project.database_1.models.Comment;


public class CommentQueryBuilder extends AbstractQueryBuilder<Comment> {

  public CommentQueryBuilder(ICommentPersistence caller) {
    super(caller);
  }

  public CommentQueryBuilder id(Long value) {
    addId(value);
    return this;
  }

  public CommentQueryBuilder id(Set<Long> values) {
    addIds(values);
    return this;
  }

  public CommentQueryBuilder content(String value) {
    if(value == null) {
      addConstraint(new QueryConstraint<String>(Comment._Fields.content, JackMatchers.<String>isNull()));
    }
    else {
      addConstraint(new QueryConstraint<String>(Comment._Fields.content, JackMatchers.equalTo(value)));
    }
    return this;
  }

  public CommentQueryBuilder content(IQueryOperator<String> operator) {
    addConstraint(new QueryConstraint<String>(Comment._Fields.content, operator));
    return this;
  }

  public CommentQueryBuilder commenterId(Integer value) {
    if(value == null) {
      addConstraint(new QueryConstraint<Integer>(Comment._Fields.commenter_id, JackMatchers.<Integer>isNull()));
    }
    else {
      addConstraint(new QueryConstraint<Integer>(Comment._Fields.commenter_id, JackMatchers.equalTo(value)));
    }
    return this;
  }

  public CommentQueryBuilder commenterId(IQueryOperator<Integer> operator) {
    addConstraint(new QueryConstraint<Integer>(Comment._Fields.commenter_id, operator));
    return this;
  }

  public CommentQueryBuilder commentedOnId(Long value) {
    if(value == null) {
      addConstraint(new QueryConstraint<Long>(Comment._Fields.commented_on_id, JackMatchers.<Long>isNull()));
    }
    else {
      addConstraint(new QueryConstraint<Long>(Comment._Fields.commented_on_id, JackMatchers.equalTo(value)));
    }
    return this;
  }

  public CommentQueryBuilder commentedOnId(IQueryOperator<Long> operator) {
    addConstraint(new QueryConstraint<Long>(Comment._Fields.commented_on_id, operator));
    return this;
  }

  public CommentQueryBuilder createdAt(Long value) {
    if(value == null) {
      addConstraint(new QueryConstraint<Long>(Comment._Fields.created_at, JackMatchers.<Long>isNull()));
    }
    else {
      addConstraint(new QueryConstraint<Long>(Comment._Fields.created_at, JackMatchers.equalTo(value)));
    }
    return this;
  }

  public CommentQueryBuilder createdAt(IQueryOperator<Long> operator) {
    addConstraint(new QueryConstraint<Long>(Comment._Fields.created_at, operator));
    return this;
  }
}
