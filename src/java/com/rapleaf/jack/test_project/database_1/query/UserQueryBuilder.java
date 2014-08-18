package com.rapleaf.jack.test_project.database_1.query;

import java.util.Set;

import com.rapleaf.jack.AbstractQueryBuilder;
import com.rapleaf.jack.IQueryOperator;
import com.rapleaf.jack.JackMatchers;
import com.rapleaf.jack.QueryConstraint;
import com.rapleaf.jack.QueryOrder;
import com.rapleaf.jack.QueryOrderConstraint;
import com.rapleaf.jack.test_project.database_1.iface.IUserPersistence;
import com.rapleaf.jack.test_project.database_1.models.User;


public class UserQueryBuilder extends AbstractQueryBuilder<User> {

  public UserQueryBuilder(IUserPersistence caller) {
    super(caller);
  }

  public UserQueryBuilder id(Long value) {
    addId(value);
    return this;
  }

  public UserQueryBuilder id(Set<Long> values) {
    addIds(values);
    return this;
  }

  public UserQueryBuilder order() {
    this.addOrder(new QueryOrderConstraint(null, QueryOrder.ASC));
    return this;
  }
  
  public UserQueryBuilder order(QueryOrder queryOrder) {
    this.addOrder(new QueryOrderConstraint(null, queryOrder));
    return this;
  }
  
  public UserQueryBuilder orderById() {
    return order();
  }
  
  public UserQueryBuilder orderById(QueryOrder queryOrder) {
    return order(queryOrder);
  }

  public UserQueryBuilder handle(String value) {
    if(value == null) {
      addConstraint(new QueryConstraint<String>(User._Fields.handle, JackMatchers.<String>isNull()));
    }
    else {
      addConstraint(new QueryConstraint<String>(User._Fields.handle, JackMatchers.equalTo(value)));
    }
    return this;
  }

  public UserQueryBuilder handle(IQueryOperator<String> operator) {
    addConstraint(new QueryConstraint<String>(User._Fields.handle, operator));
    return this;
  }
  
  public UserQueryBuilder orderByHandle() {
    this.addOrder(new QueryOrderConstraint(User._Fields.handle, QueryOrder.ASC));
    return this;
  }
  
  public UserQueryBuilder orderByHandle(QueryOrder queryOrder) {
    this.addOrder(new QueryOrderConstraint(User._Fields.handle, queryOrder));
    return this;
  }

  public UserQueryBuilder createdAtMillis(Long value) {
    if(value == null) {
      addConstraint(new QueryConstraint<Long>(User._Fields.created_at_millis, JackMatchers.<Long>isNull()));
    }
    else {
      addConstraint(new QueryConstraint<Long>(User._Fields.created_at_millis, JackMatchers.equalTo(value)));
    }
    return this;
  }

  public UserQueryBuilder createdAtMillis(IQueryOperator<Long> operator) {
    addConstraint(new QueryConstraint<Long>(User._Fields.created_at_millis, operator));
    return this;
  }
  
  public UserQueryBuilder orderByCreatedAtMillis() {
    this.addOrder(new QueryOrderConstraint(User._Fields.created_at_millis, QueryOrder.ASC));
    return this;
  }
  
  public UserQueryBuilder orderByCreatedAtMillis(QueryOrder queryOrder) {
    this.addOrder(new QueryOrderConstraint(User._Fields.created_at_millis, queryOrder));
    return this;
  }

  public UserQueryBuilder numPosts(Integer value) {
    if(value == null) {
      addConstraint(new QueryConstraint<Integer>(User._Fields.num_posts, JackMatchers.<Integer>isNull()));
    }
    else {
      addConstraint(new QueryConstraint<Integer>(User._Fields.num_posts, JackMatchers.equalTo(value)));
    }
    return this;
  }

  public UserQueryBuilder numPosts(IQueryOperator<Integer> operator) {
    addConstraint(new QueryConstraint<Integer>(User._Fields.num_posts, operator));
    return this;
  }
  
  public UserQueryBuilder orderByNumPosts() {
    this.addOrder(new QueryOrderConstraint(User._Fields.num_posts, QueryOrder.ASC));
    return this;
  }
  
  public UserQueryBuilder orderByNumPosts(QueryOrder queryOrder) {
    this.addOrder(new QueryOrderConstraint(User._Fields.num_posts, queryOrder));
    return this;
  }

  public UserQueryBuilder someDate(Long value) {
    if(value == null) {
      addConstraint(new QueryConstraint<Long>(User._Fields.some_date, JackMatchers.<Long>isNull()));
    }
    else {
      addConstraint(new QueryConstraint<Long>(User._Fields.some_date, JackMatchers.equalTo(value)));
    }
    return this;
  }

  public UserQueryBuilder someDate(IQueryOperator<Long> operator) {
    addConstraint(new QueryConstraint<Long>(User._Fields.some_date, operator));
    return this;
  }
  
  public UserQueryBuilder orderBySomeDate() {
    this.addOrder(new QueryOrderConstraint(User._Fields.some_date, QueryOrder.ASC));
    return this;
  }
  
  public UserQueryBuilder orderBySomeDate(QueryOrder queryOrder) {
    this.addOrder(new QueryOrderConstraint(User._Fields.some_date, queryOrder));
    return this;
  }

  public UserQueryBuilder someDatetime(Long value) {
    if(value == null) {
      addConstraint(new QueryConstraint<Long>(User._Fields.some_datetime, JackMatchers.<Long>isNull()));
    }
    else {
      addConstraint(new QueryConstraint<Long>(User._Fields.some_datetime, JackMatchers.equalTo(value)));
    }
    return this;
  }

  public UserQueryBuilder someDatetime(IQueryOperator<Long> operator) {
    addConstraint(new QueryConstraint<Long>(User._Fields.some_datetime, operator));
    return this;
  }
  
  public UserQueryBuilder orderBySomeDatetime() {
    this.addOrder(new QueryOrderConstraint(User._Fields.some_datetime, QueryOrder.ASC));
    return this;
  }
  
  public UserQueryBuilder orderBySomeDatetime(QueryOrder queryOrder) {
    this.addOrder(new QueryOrderConstraint(User._Fields.some_datetime, queryOrder));
    return this;
  }

  public UserQueryBuilder bio(String value) {
    if(value == null) {
      addConstraint(new QueryConstraint<String>(User._Fields.bio, JackMatchers.<String>isNull()));
    }
    else {
      addConstraint(new QueryConstraint<String>(User._Fields.bio, JackMatchers.equalTo(value)));
    }
    return this;
  }

  public UserQueryBuilder bio(IQueryOperator<String> operator) {
    addConstraint(new QueryConstraint<String>(User._Fields.bio, operator));
    return this;
  }
  
  public UserQueryBuilder orderByBio() {
    this.addOrder(new QueryOrderConstraint(User._Fields.bio, QueryOrder.ASC));
    return this;
  }
  
  public UserQueryBuilder orderByBio(QueryOrder queryOrder) {
    this.addOrder(new QueryOrderConstraint(User._Fields.bio, queryOrder));
    return this;
  }

  public UserQueryBuilder someBinary(byte[] value) {
    if(value == null) {
      addConstraint(new QueryConstraint<byte[]>(User._Fields.some_binary, JackMatchers.<byte[]>isNull()));
    }
    else {
      addConstraint(new QueryConstraint<byte[]>(User._Fields.some_binary, JackMatchers.equalTo(value)));
    }
    return this;
  }

  public UserQueryBuilder someBinary(IQueryOperator<byte[]> operator) {
    addConstraint(new QueryConstraint<byte[]>(User._Fields.some_binary, operator));
    return this;
  }
  
  public UserQueryBuilder orderBySomeBinary() {
    this.addOrder(new QueryOrderConstraint(User._Fields.some_binary, QueryOrder.ASC));
    return this;
  }
  
  public UserQueryBuilder orderBySomeBinary(QueryOrder queryOrder) {
    this.addOrder(new QueryOrderConstraint(User._Fields.some_binary, queryOrder));
    return this;
  }

  public UserQueryBuilder someFloat(Double value) {
    if(value == null) {
      addConstraint(new QueryConstraint<Double>(User._Fields.some_float, JackMatchers.<Double>isNull()));
    }
    else {
      addConstraint(new QueryConstraint<Double>(User._Fields.some_float, JackMatchers.equalTo(value)));
    }
    return this;
  }

  public UserQueryBuilder someFloat(IQueryOperator<Double> operator) {
    addConstraint(new QueryConstraint<Double>(User._Fields.some_float, operator));
    return this;
  }
  
  public UserQueryBuilder orderBySomeFloat() {
    this.addOrder(new QueryOrderConstraint(User._Fields.some_float, QueryOrder.ASC));
    return this;
  }
  
  public UserQueryBuilder orderBySomeFloat(QueryOrder queryOrder) {
    this.addOrder(new QueryOrderConstraint(User._Fields.some_float, queryOrder));
    return this;
  }

  public UserQueryBuilder someDecimal(Double value) {
    if(value == null) {
      addConstraint(new QueryConstraint<Double>(User._Fields.some_decimal, JackMatchers.<Double>isNull()));
    }
    else {
      addConstraint(new QueryConstraint<Double>(User._Fields.some_decimal, JackMatchers.equalTo(value)));
    }
    return this;
  }

  public UserQueryBuilder someDecimal(IQueryOperator<Double> operator) {
    addConstraint(new QueryConstraint<Double>(User._Fields.some_decimal, operator));
    return this;
  }
  
  public UserQueryBuilder orderBySomeDecimal() {
    this.addOrder(new QueryOrderConstraint(User._Fields.some_decimal, QueryOrder.ASC));
    return this;
  }
  
  public UserQueryBuilder orderBySomeDecimal(QueryOrder queryOrder) {
    this.addOrder(new QueryOrderConstraint(User._Fields.some_decimal, queryOrder));
    return this;
  }

  public UserQueryBuilder someBoolean(Boolean value) {
    if(value == null) {
      addConstraint(new QueryConstraint<Boolean>(User._Fields.some_boolean, JackMatchers.<Boolean>isNull()));
    }
    else {
      addConstraint(new QueryConstraint<Boolean>(User._Fields.some_boolean, JackMatchers.equalTo(value)));
    }
    return this;
  }

  public UserQueryBuilder someBoolean(IQueryOperator<Boolean> operator) {
    addConstraint(new QueryConstraint<Boolean>(User._Fields.some_boolean, operator));
    return this;
  }
  
  public UserQueryBuilder orderBySomeBoolean() {
    this.addOrder(new QueryOrderConstraint(User._Fields.some_boolean, QueryOrder.ASC));
    return this;
  }
  
  public UserQueryBuilder orderBySomeBoolean(QueryOrder queryOrder) {
    this.addOrder(new QueryOrderConstraint(User._Fields.some_boolean, queryOrder));
    return this;
  }
}
