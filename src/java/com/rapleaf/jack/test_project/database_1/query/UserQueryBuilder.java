package com.rapleaf.jack.test_project.database_1.query;

import com.rapleaf.jack.AbstractQueryBuilder;
import com.rapleaf.jack.JackMatchers;
import com.rapleaf.jack.QueryConstraint;
import com.rapleaf.jack.test_project.database_1.iface.IUserPersistence;
import com.rapleaf.jack.test_project.database_1.models.User;


public class UserQueryBuilder extends AbstractQueryBuilder<User> {

  public UserQueryBuilder(IUserPersistence caller) {
    super(caller);
  }

  public UserQueryBuilder handle(String value) {
    addConstraint(new QueryConstraint<String>(User._Fields.handle, JackMatchers.equalTo(value)));
    return this;
  }

  public UserQueryBuilder handle(QueryConstraint<String> constraint) {
    addConstraint(constraint);
    return this;
  }

  public UserQueryBuilder createdAtMillis(Long value) {
    addConstraint(new QueryConstraint<Long>(User._Fields.created_at_millis, JackMatchers.equalTo(value)));
    return this;
  }

  public UserQueryBuilder createdAtMillis(QueryConstraint<Long> constraint) {
    addConstraint(constraint);
    return this;
  }

  public UserQueryBuilder numPosts(Integer value) {
    addConstraint(new QueryConstraint<Integer>(User._Fields.num_posts, JackMatchers.equalTo(value)));
    return this;
  }

  public UserQueryBuilder numPosts(QueryConstraint<Integer> constraint) {
    addConstraint(constraint);
    return this;
  }

  public UserQueryBuilder someDate(Long value) {
    addConstraint(new QueryConstraint<Long>(User._Fields.some_date, JackMatchers.equalTo(value)));
    return this;
  }

  public UserQueryBuilder someDate(QueryConstraint<Long> constraint) {
    addConstraint(constraint);
    return this;
  }

  public UserQueryBuilder someDatetime(Long value) {
    addConstraint(new QueryConstraint<Long>(User._Fields.some_datetime, JackMatchers.equalTo(value)));
    return this;
  }

  public UserQueryBuilder someDatetime(QueryConstraint<Long> constraint) {
    addConstraint(constraint);
    return this;
  }

  public UserQueryBuilder bio(String value) {
    addConstraint(new QueryConstraint<String>(User._Fields.bio, JackMatchers.equalTo(value)));
    return this;
  }

  public UserQueryBuilder bio(QueryConstraint<String> constraint) {
    addConstraint(constraint);
    return this;
  }

  public UserQueryBuilder someBinary(byte[] value) {
    addConstraint(new QueryConstraint<byte[]>(User._Fields.some_binary, JackMatchers.equalTo(value)));
    return this;
  }

  public UserQueryBuilder someBinary(QueryConstraint<byte[]> constraint) {
    addConstraint(constraint);
    return this;
  }

  public UserQueryBuilder someFloat(Double value) {
    addConstraint(new QueryConstraint<Double>(User._Fields.some_float, JackMatchers.equalTo(value)));
    return this;
  }

  public UserQueryBuilder someFloat(QueryConstraint<Double> constraint) {
    addConstraint(constraint);
    return this;
  }

  public UserQueryBuilder someDecimal(Double value) {
    addConstraint(new QueryConstraint<Double>(User._Fields.some_decimal, JackMatchers.equalTo(value)));
    return this;
  }

  public UserQueryBuilder someDecimal(QueryConstraint<Double> constraint) {
    addConstraint(constraint);
    return this;
  }

  public UserQueryBuilder someBoolean(Boolean value) {
    addConstraint(new QueryConstraint<Boolean>(User._Fields.some_boolean, JackMatchers.equalTo(value)));
    return this;
  }

  public UserQueryBuilder someBoolean(QueryConstraint<Boolean> constraint) {
    addConstraint(constraint);
    return this;
  }
}
