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
import com.rapleaf.jack.test_project.database_1.iface.IUserPersistence;
import com.rapleaf.jack.test_project.database_1.models.User;


public class UserQueryBuilder extends AbstractQueryBuilder<User> implements #I{model_name}QueryBuilder {

  public UserQueryBuilder(IUserPersistence caller) {
    super(caller);
  }

  public UserQueryBuilder select(User._Fields... fields) {
    for (User._Fields field : fields){
      addSelectedField(new FieldSelector(field));
    }
    return this;
  }

  public UserQueryBuilder selectAgg(FieldSelector... aggregatedFields) {
    addSelectedFields(aggregatedFields);
    return this;
  }

  public UserQueryBuilder id(Long value) {
    addId(value);
    return this;
  }

  public UserQueryBuilder idIn(Set<Long> values) {
    addIds(values);
    return this;
  }

  public UserQueryBuilder limit(int offset, int nResults) {
    setLimit(new LimitCriterion(offset, nResults));
    return this;
  }

  public UserQueryBuilder limit(int nResults) {
    setLimit(new LimitCriterion(nResults));
    return this;
  }

  public UserQueryBuilder groupBy(User._Fields... fields) {
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

  public UserQueryBuilder handle(String value) {
    addWhereConstraint(new WhereConstraint<String>(User._Fields.handle, JackMatchers.equalTo(value)));
    return this;
  }

  public UserQueryBuilder whereHandle(IWhereOperator<String> operator) {
    addWhereConstraint(new WhereConstraint<String>(User._Fields.handle, operator));
    return this;
  }
  
  public #IOrdered{model_name}QueryBuilder orderByHandle() {
    this.addOrder(new OrderCriterion(User._Fields.handle, QueryOrder.ASC));
    return (#IOrdered{model_name}QueryBuilder)this;
  }
  
  public #IOrdered{model_name}QueryBuilder orderByHandle(QueryOrder queryOrder) {
    this.addOrder(new OrderCriterion(User._Fields.handle, queryOrder));
    return (#IOrdered{model_name}QueryBuilder)this;
  }

  public UserQueryBuilder createdAtMillis(Long value) {
    addWhereConstraint(new WhereConstraint<Long>(User._Fields.created_at_millis, JackMatchers.equalTo(value)));
    return this;
  }

  public UserQueryBuilder whereCreatedAtMillis(IWhereOperator<Long> operator) {
    addWhereConstraint(new WhereConstraint<Long>(User._Fields.created_at_millis, operator));
    return this;
  }
  
  public #IOrdered{model_name}QueryBuilder orderByCreatedAtMillis() {
    this.addOrder(new OrderCriterion(User._Fields.created_at_millis, QueryOrder.ASC));
    return (#IOrdered{model_name}QueryBuilder)this;
  }
  
  public #IOrdered{model_name}QueryBuilder orderByCreatedAtMillis(QueryOrder queryOrder) {
    this.addOrder(new OrderCriterion(User._Fields.created_at_millis, queryOrder));
    return (#IOrdered{model_name}QueryBuilder)this;
  }

  public UserQueryBuilder numPosts(Integer value) {
    addWhereConstraint(new WhereConstraint<Integer>(User._Fields.num_posts, JackMatchers.equalTo(value)));
    return this;
  }

  public UserQueryBuilder whereNumPosts(IWhereOperator<Integer> operator) {
    addWhereConstraint(new WhereConstraint<Integer>(User._Fields.num_posts, operator));
    return this;
  }
  
  public #IOrdered{model_name}QueryBuilder orderByNumPosts() {
    this.addOrder(new OrderCriterion(User._Fields.num_posts, QueryOrder.ASC));
    return (#IOrdered{model_name}QueryBuilder)this;
  }
  
  public #IOrdered{model_name}QueryBuilder orderByNumPosts(QueryOrder queryOrder) {
    this.addOrder(new OrderCriterion(User._Fields.num_posts, queryOrder));
    return (#IOrdered{model_name}QueryBuilder)this;
  }

  public UserQueryBuilder someDate(Long value) {
    addWhereConstraint(new WhereConstraint<Long>(User._Fields.some_date, JackMatchers.equalTo(value)));
    return this;
  }

  public UserQueryBuilder whereSomeDate(IWhereOperator<Long> operator) {
    addWhereConstraint(new WhereConstraint<Long>(User._Fields.some_date, operator));
    return this;
  }
  
  public #IOrdered{model_name}QueryBuilder orderBySomeDate() {
    this.addOrder(new OrderCriterion(User._Fields.some_date, QueryOrder.ASC));
    return (#IOrdered{model_name}QueryBuilder)this;
  }
  
  public #IOrdered{model_name}QueryBuilder orderBySomeDate(QueryOrder queryOrder) {
    this.addOrder(new OrderCriterion(User._Fields.some_date, queryOrder));
    return (#IOrdered{model_name}QueryBuilder)this;
  }

  public UserQueryBuilder someDatetime(Long value) {
    addWhereConstraint(new WhereConstraint<Long>(User._Fields.some_datetime, JackMatchers.equalTo(value)));
    return this;
  }

  public UserQueryBuilder whereSomeDatetime(IWhereOperator<Long> operator) {
    addWhereConstraint(new WhereConstraint<Long>(User._Fields.some_datetime, operator));
    return this;
  }
  
  public #IOrdered{model_name}QueryBuilder orderBySomeDatetime() {
    this.addOrder(new OrderCriterion(User._Fields.some_datetime, QueryOrder.ASC));
    return (#IOrdered{model_name}QueryBuilder)this;
  }
  
  public #IOrdered{model_name}QueryBuilder orderBySomeDatetime(QueryOrder queryOrder) {
    this.addOrder(new OrderCriterion(User._Fields.some_datetime, queryOrder));
    return (#IOrdered{model_name}QueryBuilder)this;
  }

  public UserQueryBuilder bio(String value) {
    addWhereConstraint(new WhereConstraint<String>(User._Fields.bio, JackMatchers.equalTo(value)));
    return this;
  }

  public UserQueryBuilder whereBio(IWhereOperator<String> operator) {
    addWhereConstraint(new WhereConstraint<String>(User._Fields.bio, operator));
    return this;
  }
  
  public #IOrdered{model_name}QueryBuilder orderByBio() {
    this.addOrder(new OrderCriterion(User._Fields.bio, QueryOrder.ASC));
    return (#IOrdered{model_name}QueryBuilder)this;
  }
  
  public #IOrdered{model_name}QueryBuilder orderByBio(QueryOrder queryOrder) {
    this.addOrder(new OrderCriterion(User._Fields.bio, queryOrder));
    return (#IOrdered{model_name}QueryBuilder)this;
  }

  public UserQueryBuilder someBinary(byte[] value) {
    addWhereConstraint(new WhereConstraint<byte[]>(User._Fields.some_binary, JackMatchers.equalTo(value)));
    return this;
  }

  public UserQueryBuilder whereSomeBinary(IWhereOperator<byte[]> operator) {
    addWhereConstraint(new WhereConstraint<byte[]>(User._Fields.some_binary, operator));
    return this;
  }
  
  public #IOrdered{model_name}QueryBuilder orderBySomeBinary() {
    this.addOrder(new OrderCriterion(User._Fields.some_binary, QueryOrder.ASC));
    return (#IOrdered{model_name}QueryBuilder)this;
  }
  
  public #IOrdered{model_name}QueryBuilder orderBySomeBinary(QueryOrder queryOrder) {
    this.addOrder(new OrderCriterion(User._Fields.some_binary, queryOrder));
    return (#IOrdered{model_name}QueryBuilder)this;
  }

  public UserQueryBuilder someFloat(Double value) {
    addWhereConstraint(new WhereConstraint<Double>(User._Fields.some_float, JackMatchers.equalTo(value)));
    return this;
  }

  public UserQueryBuilder whereSomeFloat(IWhereOperator<Double> operator) {
    addWhereConstraint(new WhereConstraint<Double>(User._Fields.some_float, operator));
    return this;
  }
  
  public #IOrdered{model_name}QueryBuilder orderBySomeFloat() {
    this.addOrder(new OrderCriterion(User._Fields.some_float, QueryOrder.ASC));
    return (#IOrdered{model_name}QueryBuilder)this;
  }
  
  public #IOrdered{model_name}QueryBuilder orderBySomeFloat(QueryOrder queryOrder) {
    this.addOrder(new OrderCriterion(User._Fields.some_float, queryOrder));
    return (#IOrdered{model_name}QueryBuilder)this;
  }

  public UserQueryBuilder someDecimal(Double value) {
    addWhereConstraint(new WhereConstraint<Double>(User._Fields.some_decimal, JackMatchers.equalTo(value)));
    return this;
  }

  public UserQueryBuilder whereSomeDecimal(IWhereOperator<Double> operator) {
    addWhereConstraint(new WhereConstraint<Double>(User._Fields.some_decimal, operator));
    return this;
  }
  
  public #IOrdered{model_name}QueryBuilder orderBySomeDecimal() {
    this.addOrder(new OrderCriterion(User._Fields.some_decimal, QueryOrder.ASC));
    return (#IOrdered{model_name}QueryBuilder)this;
  }
  
  public #IOrdered{model_name}QueryBuilder orderBySomeDecimal(QueryOrder queryOrder) {
    this.addOrder(new OrderCriterion(User._Fields.some_decimal, queryOrder));
    return (#IOrdered{model_name}QueryBuilder)this;
  }

  public UserQueryBuilder someBoolean(Boolean value) {
    addWhereConstraint(new WhereConstraint<Boolean>(User._Fields.some_boolean, JackMatchers.equalTo(value)));
    return this;
  }

  public UserQueryBuilder whereSomeBoolean(IWhereOperator<Boolean> operator) {
    addWhereConstraint(new WhereConstraint<Boolean>(User._Fields.some_boolean, operator));
    return this;
  }
  
  public #IOrdered{model_name}QueryBuilder orderBySomeBoolean() {
    this.addOrder(new OrderCriterion(User._Fields.some_boolean, QueryOrder.ASC));
    return (#IOrdered{model_name}QueryBuilder)this;
  }
  
  public #IOrdered{model_name}QueryBuilder orderBySomeBoolean(QueryOrder queryOrder) {
    this.addOrder(new OrderCriterion(User._Fields.some_boolean, queryOrder));
    return (#IOrdered{model_name}QueryBuilder)this;
  }
}
