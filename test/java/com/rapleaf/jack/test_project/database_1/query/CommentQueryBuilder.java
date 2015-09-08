package com.rapleaf.jack.test_project.database_1.query;

import java.util.Set;

import com.rapleaf.jack.queries.AbstractQueryBuilder;
import com.rapleaf.jack.queries.Column;
import com.rapleaf.jack.queries.FieldSelector;
import com.rapleaf.jack.queries.where_operators.IWhereOperator;
import com.rapleaf.jack.queries.where_operators.JackMatchers;
import com.rapleaf.jack.queries.WhereConstraint;
import com.rapleaf.jack.queries.QueryOrder;
import com.rapleaf.jack.queries.OrderCriterion;
import com.rapleaf.jack.queries.LimitCriterion;
import com.rapleaf.jack.test_project.database_1.iface.ICommentPersistence;
import com.rapleaf.jack.test_project.database_1.models.Comment;


public class CommentQueryBuilder extends AbstractQueryBuilder<Comment> {

  public CommentQueryBuilder(ICommentPersistence caller) {
    super(caller);
  }

  public CommentQueryBuilder select(Comment._Fields... fields) {
    for (Comment._Fields field : fields){
      addSelectedField(new FieldSelector(field));
    }
    return this;
  }

  public CommentQueryBuilder selectAgg(FieldSelector... aggregatedFields) {
    addSelectedFields(aggregatedFields);
    return this;
  }

  public CommentQueryBuilder id(Long value) {
    addId(value);
    return this;
  }

  public CommentQueryBuilder idIn(Set<Long> values) {
    addIds(values);
    return this;
  }

  public CommentQueryBuilder whereId(IWhereOperator<Long> operator) {
    addWhereConstraint(new WhereConstraint<Long>(Column.fromId(null), operator, null));
    return this;
  }

  public CommentQueryBuilder limit(int offset, int nResults) {
    setLimit(new LimitCriterion(offset, nResults));
    return this;
  }

  public CommentQueryBuilder limit(int nResults) {
    setLimit(new LimitCriterion(nResults));
    return this;
  }

  public CommentQueryBuilder groupBy(Comment._Fields... fields) {
    addGroupByFields(fields);
    return this;
  }

  public CommentQueryBuilder order() {
    this.addOrder(new OrderCriterion(QueryOrder.ASC));
    return this;
  }

  public CommentQueryBuilder order(QueryOrder queryOrder) {
    this.addOrder(new OrderCriterion(queryOrder));
    return this;
  }

  public CommentQueryBuilder orderById() {
    this.addOrder(new OrderCriterion(QueryOrder.ASC));
    return this;
  }

  public CommentQueryBuilder orderById(QueryOrder queryOrder) {
    this.addOrder(new OrderCriterion(queryOrder));
    return this;
  }

  public CommentQueryBuilder content(String value) {
    addWhereConstraint(new WhereConstraint<String>(Comment._Fields.content, JackMatchers.equalTo(value)));
    return this;
  }

  public CommentQueryBuilder whereContent(IWhereOperator<String> operator) {
    addWhereConstraint(new WhereConstraint<String>(Comment._Fields.content, operator));
    return this;
  }

  public CommentQueryBuilder orderByContent() {
    this.addOrder(new OrderCriterion(Comment._Fields.content, QueryOrder.ASC));
    return this;
  }

  public CommentQueryBuilder orderByContent(QueryOrder queryOrder) {
    this.addOrder(new OrderCriterion(Comment._Fields.content, queryOrder));
    return this;
  }

  public CommentQueryBuilder commenterId(Integer value) {
    addWhereConstraint(new WhereConstraint<Integer>(Comment._Fields.commenter_id, JackMatchers.equalTo(value)));
    return this;
  }

  public CommentQueryBuilder whereCommenterId(IWhereOperator<Integer> operator) {
    addWhereConstraint(new WhereConstraint<Integer>(Comment._Fields.commenter_id, operator));
    return this;
  }

  public CommentQueryBuilder orderByCommenterId() {
    this.addOrder(new OrderCriterion(Comment._Fields.commenter_id, QueryOrder.ASC));
    return this;
  }

  public CommentQueryBuilder orderByCommenterId(QueryOrder queryOrder) {
    this.addOrder(new OrderCriterion(Comment._Fields.commenter_id, queryOrder));
    return this;
  }

  public CommentQueryBuilder commentedOnId(Long value) {
    addWhereConstraint(new WhereConstraint<Long>(Comment._Fields.commented_on_id, JackMatchers.equalTo(value)));
    return this;
  }

  public CommentQueryBuilder whereCommentedOnId(IWhereOperator<Long> operator) {
    addWhereConstraint(new WhereConstraint<Long>(Comment._Fields.commented_on_id, operator));
    return this;
  }

  public CommentQueryBuilder orderByCommentedOnId() {
    this.addOrder(new OrderCriterion(Comment._Fields.commented_on_id, QueryOrder.ASC));
    return this;
  }

  public CommentQueryBuilder orderByCommentedOnId(QueryOrder queryOrder) {
    this.addOrder(new OrderCriterion(Comment._Fields.commented_on_id, queryOrder));
    return this;
  }

  public CommentQueryBuilder createdAt(Long value) {
    addWhereConstraint(new WhereConstraint<Long>(Comment._Fields.created_at, JackMatchers.equalTo(value)));
    return this;
  }

  public CommentQueryBuilder whereCreatedAt(IWhereOperator<Long> operator) {
    addWhereConstraint(new WhereConstraint<Long>(Comment._Fields.created_at, operator));
    return this;
  }

  public CommentQueryBuilder orderByCreatedAt() {
    this.addOrder(new OrderCriterion(Comment._Fields.created_at, QueryOrder.ASC));
    return this;
  }

  public CommentQueryBuilder orderByCreatedAt(QueryOrder queryOrder) {
    this.addOrder(new OrderCriterion(Comment._Fields.created_at, queryOrder));
    return this;
  }
}
