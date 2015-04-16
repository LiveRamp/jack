package com.rapleaf.jack.test_project.database_1.query;

import java.util.Set;

import com.rapleaf.jack.queries.AbstractQueryBuilder;
import com.rapleaf.jack.queries.FieldSelector;
import com.rapleaf.jack.queries.where_operators.IWhereOperator;
import com.rapleaf.jack.queries.where_operators.JackMatchers;
import com.rapleaf.jack.queries.WhereConstraint;
import com.rapleaf.jack.queries.QueryOrder;
import com.rapleaf.jack.queries.OrderCriterion;
import com.rapleaf.jack.queries.LimitCriterion;
import com.rapleaf.jack.test_project.database_1.iface.ICommentPersistence;
import com.rapleaf.jack.test_project.database_1.models.Comment;

public class CommentQueryBuilder extends AbstractQueryBuilder<Comment> implements ICommentQueryBuilder {

  public CommentQueryBuilder(ICommentPersistence caller) {
    super(caller);
  }

  public ICommentQueryBuilder select(Comment._Fields... fields) {
    for (Comment._Fields field : fields){
      addSelectedField(new FieldSelector(field));
    }
    return this;
  }

  public ICommentQueryBuilder selectAgg(FieldSelector... aggregatedFields) {
    addSelectedFields(aggregatedFields);
    return this;
  }

  public ICommentQueryBuilder id(Long value) {
    addId(value);
    return this;
  }

  public ICommentQueryBuilder idIn(Set<Long> values) {
    addIds(values);
    return this;
  }

  public ICommentQueryBuilder limit(int offset, int nResults) {
    setLimit(new LimitCriterion(offset, nResults));
    return this;
  }

  public ICommentQueryBuilder limit(int nResults) {
    setLimit(new LimitCriterion(nResults));
    return this;
  }

  public ICommentQueryBuilder groupBy(Comment._Fields... fields) {
    addGroupByFields(fields);
    return this;
  }

  public IOrderedCommentQueryBuilder order() {
    this.addOrder(new OrderCriterion(QueryOrder.ASC));
    return (IOrderedCommentQueryBuilder)this;
  }
  
  public IOrderedCommentQueryBuilder order(QueryOrder queryOrder) {
    this.addOrder(new OrderCriterion(queryOrder));
    return (IOrderedCommentQueryBuilder)this;
  }
  
  public IOrderedCommentQueryBuilder orderById() {
    this.addOrder(new OrderCriterion(QueryOrder.ASC));
    return (IOrderedCommentQueryBuilder)this;
  }
  
  public IOrderedCommentQueryBuilder orderById(QueryOrder queryOrder) {
    this.addOrder(new OrderCriterion(queryOrder));
    return (IOrderedCommentQueryBuilder)this;
  }

  public ICommentQueryBuilder content(String value) {
    addWhereConstraint(new WhereConstraint<String>(Comment._Fields.content, JackMatchers.equalTo(value)));
    return this;
  }

  public ICommentQueryBuilder whereContent(IWhereOperator<String> operator) {
    addWhereConstraint(new WhereConstraint<String>(Comment._Fields.content, operator));
    return this;
  }
  
  public IOrderedCommentQueryBuilder orderByContent() {
    this.addOrder(new OrderCriterion(Comment._Fields.content, QueryOrder.ASC));
    return (IOrderedCommentQueryBuilder)this;
  }
  
  public IOrderedCommentQueryBuilder orderByContent(QueryOrder queryOrder) {
    this.addOrder(new OrderCriterion(Comment._Fields.content, queryOrder));
    return (IOrderedCommentQueryBuilder)this;
  }

  public ICommentQueryBuilder commenterId(Integer value) {
    addWhereConstraint(new WhereConstraint<Integer>(Comment._Fields.commenter_id, JackMatchers.equalTo(value)));
    return this;
  }

  public ICommentQueryBuilder whereCommenterId(IWhereOperator<Integer> operator) {
    addWhereConstraint(new WhereConstraint<Integer>(Comment._Fields.commenter_id, operator));
    return this;
  }
  
  public IOrderedCommentQueryBuilder orderByCommenterId() {
    this.addOrder(new OrderCriterion(Comment._Fields.commenter_id, QueryOrder.ASC));
    return (IOrderedCommentQueryBuilder)this;
  }
  
  public IOrderedCommentQueryBuilder orderByCommenterId(QueryOrder queryOrder) {
    this.addOrder(new OrderCriterion(Comment._Fields.commenter_id, queryOrder));
    return (IOrderedCommentQueryBuilder)this;
  }

  public ICommentQueryBuilder commentedOnId(Long value) {
    addWhereConstraint(new WhereConstraint<Long>(Comment._Fields.commented_on_id, JackMatchers.equalTo(value)));
    return this;
  }

  public ICommentQueryBuilder whereCommentedOnId(IWhereOperator<Long> operator) {
    addWhereConstraint(new WhereConstraint<Long>(Comment._Fields.commented_on_id, operator));
    return this;
  }
  
  public IOrderedCommentQueryBuilder orderByCommentedOnId() {
    this.addOrder(new OrderCriterion(Comment._Fields.commented_on_id, QueryOrder.ASC));
    return (IOrderedCommentQueryBuilder)this;
  }
  
  public IOrderedCommentQueryBuilder orderByCommentedOnId(QueryOrder queryOrder) {
    this.addOrder(new OrderCriterion(Comment._Fields.commented_on_id, queryOrder));
    return (IOrderedCommentQueryBuilder)this;
  }

  public ICommentQueryBuilder createdAt(Long value) {
    addWhereConstraint(new WhereConstraint<Long>(Comment._Fields.created_at, JackMatchers.equalTo(value)));
    return this;
  }

  public ICommentQueryBuilder whereCreatedAt(IWhereOperator<Long> operator) {
    addWhereConstraint(new WhereConstraint<Long>(Comment._Fields.created_at, operator));
    return this;
  }
  
  public IOrderedCommentQueryBuilder orderByCreatedAt() {
    this.addOrder(new OrderCriterion(Comment._Fields.created_at, QueryOrder.ASC));
    return (IOrderedCommentQueryBuilder)this;
  }
  
  public IOrderedCommentQueryBuilder orderByCreatedAt(QueryOrder queryOrder) {
    this.addOrder(new OrderCriterion(Comment._Fields.created_at, queryOrder));
    return (IOrderedCommentQueryBuilder)this;
  }
}
