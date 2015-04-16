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
import com.rapleaf.jack.test_project.database_1.iface.IPostPersistence;
import com.rapleaf.jack.test_project.database_1.models.Post;


public class PostQueryBuilder extends AbstractQueryBuilder<Post> implements #I{model_name}QueryBuilder {

  public PostQueryBuilder(IPostPersistence caller) {
    super(caller);
  }

  public PostQueryBuilder select(Post._Fields... fields) {
    for (Post._Fields field : fields){
      addSelectedField(new FieldSelector(field));
    }
    return this;
  }

  public PostQueryBuilder selectAgg(FieldSelector... aggregatedFields) {
    addSelectedFields(aggregatedFields);
    return this;
  }

  public PostQueryBuilder id(Long value) {
    addId(value);
    return this;
  }

  public PostQueryBuilder idIn(Set<Long> values) {
    addIds(values);
    return this;
  }

  public PostQueryBuilder limit(int offset, int nResults) {
    setLimit(new LimitCriterion(offset, nResults));
    return this;
  }

  public PostQueryBuilder limit(int nResults) {
    setLimit(new LimitCriterion(nResults));
    return this;
  }

  public PostQueryBuilder groupBy(Post._Fields... fields) {
    addGroupByFields(fields);
    return this;
  }

  public #IOrdered{model_name}QueryBuilder order() {
    this.addOrder(new OrderCriterion(QueryOrder.ASC));
    return this;
  }
  
  public #IOrdered{model_name}QueryBuilder order(QueryOrder queryOrder) {
    this.addOrder(new OrderCriterion(queryOrder));
    return this;
  }
  
  public #IOrdered{model_name}QueryBuilder orderById() {
    this.addOrder(new OrderCriterion(QueryOrder.ASC));
    return this;
  }
  
  public #IOrdered{model_name}QueryBuilder orderById(QueryOrder queryOrder) {
    this.addOrder(new OrderCriterion(queryOrder));
    return this;
  }

  public PostQueryBuilder title(String value) {
    addWhereConstraint(new WhereConstraint<String>(Post._Fields.title, JackMatchers.equalTo(value)));
    return this;
  }

  public PostQueryBuilder whereTitle(IWhereOperator<String> operator) {
    addWhereConstraint(new WhereConstraint<String>(Post._Fields.title, operator));
    return this;
  }
  
  public #IOrdered{model_name}QueryBuilder orderByTitle() {
    this.addOrder(new OrderCriterion(Post._Fields.title, QueryOrder.ASC));
    return (#IOrdered{model_name}QueryBuilder)this;
  }
  
  public #IOrdered{model_name}QueryBuilder orderByTitle(QueryOrder queryOrder) {
    this.addOrder(new OrderCriterion(Post._Fields.title, queryOrder));
    return (#IOrdered{model_name}QueryBuilder)this;
  }

  public PostQueryBuilder postedAtMillis(Long value) {
    addWhereConstraint(new WhereConstraint<Long>(Post._Fields.posted_at_millis, JackMatchers.equalTo(value)));
    return this;
  }

  public PostQueryBuilder wherePostedAtMillis(IWhereOperator<Long> operator) {
    addWhereConstraint(new WhereConstraint<Long>(Post._Fields.posted_at_millis, operator));
    return this;
  }
  
  public #IOrdered{model_name}QueryBuilder orderByPostedAtMillis() {
    this.addOrder(new OrderCriterion(Post._Fields.posted_at_millis, QueryOrder.ASC));
    return (#IOrdered{model_name}QueryBuilder)this;
  }
  
  public #IOrdered{model_name}QueryBuilder orderByPostedAtMillis(QueryOrder queryOrder) {
    this.addOrder(new OrderCriterion(Post._Fields.posted_at_millis, queryOrder));
    return (#IOrdered{model_name}QueryBuilder)this;
  }

  public PostQueryBuilder userId(Integer value) {
    addWhereConstraint(new WhereConstraint<Integer>(Post._Fields.user_id, JackMatchers.equalTo(value)));
    return this;
  }

  public PostQueryBuilder whereUserId(IWhereOperator<Integer> operator) {
    addWhereConstraint(new WhereConstraint<Integer>(Post._Fields.user_id, operator));
    return this;
  }
  
  public #IOrdered{model_name}QueryBuilder orderByUserId() {
    this.addOrder(new OrderCriterion(Post._Fields.user_id, QueryOrder.ASC));
    return (#IOrdered{model_name}QueryBuilder)this;
  }
  
  public #IOrdered{model_name}QueryBuilder orderByUserId(QueryOrder queryOrder) {
    this.addOrder(new OrderCriterion(Post._Fields.user_id, queryOrder));
    return (#IOrdered{model_name}QueryBuilder)this;
  }

  public PostQueryBuilder updatedAt(Long value) {
    addWhereConstraint(new WhereConstraint<Long>(Post._Fields.updated_at, JackMatchers.equalTo(value)));
    return this;
  }

  public PostQueryBuilder whereUpdatedAt(IWhereOperator<Long> operator) {
    addWhereConstraint(new WhereConstraint<Long>(Post._Fields.updated_at, operator));
    return this;
  }
  
  public #IOrdered{model_name}QueryBuilder orderByUpdatedAt() {
    this.addOrder(new OrderCriterion(Post._Fields.updated_at, QueryOrder.ASC));
    return (#IOrdered{model_name}QueryBuilder)this;
  }
  
  public #IOrdered{model_name}QueryBuilder orderByUpdatedAt(QueryOrder queryOrder) {
    this.addOrder(new OrderCriterion(Post._Fields.updated_at, queryOrder));
    return (#IOrdered{model_name}QueryBuilder)this;
  }
}
