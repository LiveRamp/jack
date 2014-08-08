package com.rapleaf.jack.test_project.database_1.query;

import com.rapleaf.jack.AbstractQueryBuilder;
import com.rapleaf.jack.ISqlOperator;
import com.rapleaf.jack.JackMatchers;
import com.rapleaf.jack.QueryConstraint;
import com.rapleaf.jack.test_project.database_1.iface.ICommentPersistence;
import com.rapleaf.jack.test_project.database_1.models.Comment;


public class CommentQueryBuilder extends AbstractQueryBuilder<Comment> {

  public CommentQueryBuilder(ICommentPersistence caller) {
    super(caller);
  }

  public CommentQueryBuilder content(String value) {
    addConstraint(new QueryConstraint<String>(Comment._Fields.content, JackMatchers.equalTo(value)));
    return this;
  }

  public CommentQueryBuilder content(ISqlOperator<String> operator) {
    addConstraint(new QueryConstraint<String>(Comment._Fields.content, operator));
    return this;
  }

  public CommentQueryBuilder commenterId(Integer value) {
    addConstraint(new QueryConstraint<Integer>(Comment._Fields.commenter_id, JackMatchers.equalTo(value)));
    return this;
  }

  public CommentQueryBuilder commenterId(ISqlOperator<Integer> operator) {
    addConstraint(new QueryConstraint<Integer>(Comment._Fields.commenter_id, operator));
    return this;
  }

  public CommentQueryBuilder commentedOnId(Long value) {
    addConstraint(new QueryConstraint<Long>(Comment._Fields.commented_on_id, JackMatchers.equalTo(value)));
    return this;
  }

  public CommentQueryBuilder commentedOnId(ISqlOperator<Long> operator) {
    addConstraint(new QueryConstraint<Long>(Comment._Fields.commented_on_id, operator));
    return this;
  }

  public CommentQueryBuilder createdAt(Long value) {
    addConstraint(new QueryConstraint<Long>(Comment._Fields.created_at, JackMatchers.equalTo(value)));
    return this;
  }

  public CommentQueryBuilder createdAt(ISqlOperator<Long> operator) {
    addConstraint(new QueryConstraint<Long>(Comment._Fields.created_at, operator));
    return this;
  }
}
