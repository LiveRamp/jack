package com.rapleaf.jack.test_project.database_1.query;

import java.util.Collection;

import com.rapleaf.jack.queries.AbstractDeleteBuilder;
import com.rapleaf.jack.queries.where_operators.IWhereOperator;
import com.rapleaf.jack.queries.where_operators.JackMatchers;
import com.rapleaf.jack.queries.WhereConstraint;
import com.rapleaf.jack.test_project.database_1.iface.ICommentPersistence;
import com.rapleaf.jack.test_project.database_1.models.Comment;


public class CommentDeleteBuilder extends AbstractDeleteBuilder<Comment> {

  public CommentDeleteBuilder(ICommentPersistence caller) {
    super(caller);
  }

  public CommentDeleteBuilder id(Long value) {
    addId(value);
    return this;
  }

  public CommentDeleteBuilder idIn(Collection<Long> values) {
    addIds(values);
    return this;
  }

  public CommentDeleteBuilder content(String value) {
    addWhereConstraint(new WhereConstraint<String>(Comment._Fields.content, JackMatchers.equalTo(value)));
    return this;
  }

  public CommentDeleteBuilder whereContent(IWhereOperator<String> operator) {
    addWhereConstraint(new WhereConstraint<String>(Comment._Fields.content, operator));
    return this;
  }

  public CommentDeleteBuilder commenterId(Integer value) {
    addWhereConstraint(new WhereConstraint<Integer>(Comment._Fields.commenter_id, JackMatchers.equalTo(value)));
    return this;
  }

  public CommentDeleteBuilder whereCommenterId(IWhereOperator<Integer> operator) {
    addWhereConstraint(new WhereConstraint<Integer>(Comment._Fields.commenter_id, operator));
    return this;
  }

  public CommentDeleteBuilder commentedOnId(Long value) {
    addWhereConstraint(new WhereConstraint<Long>(Comment._Fields.commented_on_id, JackMatchers.equalTo(value)));
    return this;
  }

  public CommentDeleteBuilder whereCommentedOnId(IWhereOperator<Long> operator) {
    addWhereConstraint(new WhereConstraint<Long>(Comment._Fields.commented_on_id, operator));
    return this;
  }

  public CommentDeleteBuilder createdAt(Long value) {
    addWhereConstraint(new WhereConstraint<Long>(Comment._Fields.created_at, JackMatchers.equalTo(value)));
    return this;
  }

  public CommentDeleteBuilder whereCreatedAt(IWhereOperator<Long> operator) {
    addWhereConstraint(new WhereConstraint<Long>(Comment._Fields.created_at, operator));
    return this;
  }
}
