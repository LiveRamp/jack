package com.rapleaf.jack.test_project.database_1.query;

import java.util.Set;

import com.rapleaf.jack.AbstractQueryBuilder;
import com.rapleaf.jack.IQueryOperator;
import com.rapleaf.jack.JackMatchers;
import com.rapleaf.jack.QueryConstraint;
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

  public UserQueryBuilder handle(String value) {
    addConstraint(new QueryConstraint<String>(User._Fields.handle, JackMatchers.equalTo(value)));
    return this;
  }

  public UserQueryBuilder handle(IQueryOperator<String> operator) {
    addConstraint(new QueryConstraint<String>(User._Fields.handle, operator));
    return this;
  }

  public UserQueryBuilder createdAtMillis(Long value) {
    addConstraint(new QueryConstraint<Long>(User._Fields.created_at_millis, JackMatchers.equalTo(value)));
    return this;
  }

  public UserQueryBuilder createdAtMillis(IQueryOperator<Long> operator) {
    addConstraint(new QueryConstraint<Long>(User._Fields.created_at_millis, operator));
    return this;
  }

  public UserQueryBuilder numPosts(Integer value) {
    addConstraint(new QueryConstraint<Integer>(User._Fields.num_posts, JackMatchers.equalTo(value)));
    return this;
  }

  public UserQueryBuilder numPosts(IQueryOperator<Integer> operator) {
    addConstraint(new QueryConstraint<Integer>(User._Fields.num_posts, operator));
    return this;
  }

  public UserQueryBuilder someDate(Long value) {
    addConstraint(new QueryConstraint<Long>(User._Fields.some_date, JackMatchers.equalTo(value)));
    return this;
  }

  public UserQueryBuilder someDate(IQueryOperator<Long> operator) {
    addConstraint(new QueryConstraint<Long>(User._Fields.some_date, operator));
    return this;
  }

  public UserQueryBuilder someDatetime(Long value) {
    addConstraint(new QueryConstraint<Long>(User._Fields.some_datetime, JackMatchers.equalTo(value)));
    return this;
  }

  public UserQueryBuilder someDatetime(IQueryOperator<Long> operator) {
    addConstraint(new QueryConstraint<Long>(User._Fields.some_datetime, operator));
    return this;
  }

  public UserQueryBuilder bio(String value) {
    addConstraint(new QueryConstraint<String>(User._Fields.bio, JackMatchers.equalTo(value)));
    return this;
  }

  public UserQueryBuilder bio(IQueryOperator<String> operator) {
    addConstraint(new QueryConstraint<String>(User._Fields.bio, operator));
    return this;
  }

  public UserQueryBuilder someBinary(byte[] value) {
    addConstraint(new QueryConstraint<byte[]>(User._Fields.some_binary, JackMatchers.equalTo(value)));
    return this;
  }

  public UserQueryBuilder someBinary(IQueryOperator<byte[]> operator) {
    addConstraint(new QueryConstraint<byte[]>(User._Fields.some_binary, operator));
    return this;
  }

  public UserQueryBuilder someFloat(Double value) {
    addConstraint(new QueryConstraint<Double>(User._Fields.some_float, JackMatchers.equalTo(value)));
    return this;
  }

  public UserQueryBuilder someFloat(IQueryOperator<Double> operator) {
    addConstraint(new QueryConstraint<Double>(User._Fields.some_float, operator));
    return this;
  }

  public UserQueryBuilder someDecimal(Double value) {
    addConstraint(new QueryConstraint<Double>(User._Fields.some_decimal, JackMatchers.equalTo(value)));
    return this;
  }

  public UserQueryBuilder someDecimal(IQueryOperator<Double> operator) {
    addConstraint(new QueryConstraint<Double>(User._Fields.some_decimal, operator));
    return this;
  }

  public UserQueryBuilder someBoolean(Boolean value) {
    addConstraint(new QueryConstraint<Boolean>(User._Fields.some_boolean, JackMatchers.equalTo(value)));
    return this;
  }

  public UserQueryBuilder someBoolean(IQueryOperator<Boolean> operator) {
    addConstraint(new QueryConstraint<Boolean>(User._Fields.some_boolean, operator));
    return this;
  }
}
