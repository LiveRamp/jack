package com.rapleaf.jack.test_project.database_1.query;

import com.rapleaf.jack.AbstractQueryBuilder;

import com.rapleaf.jack.test_project.database_1.models.User;
import com.rapleaf.jack.test_project.database_1.iface.IUserPersistence;


public class UserQueryBuilder extends AbstractQueryBuilder<User> {

  public UserQueryBuilder (IUserPersistence caller) {
    super(caller);
  }

  public UserQueryBuilder handle(String value) {
    addConstraint(JackMatchers.equalTo(value));
    return this;
  }

  public UserQueryBuilder handle(QueryConstraint<String value) {
    addConstraint(JackMatchers.equalTo(value));
    return this;
  }

  public UserQueryBuilder createdAtMillis(Long value) {
    addConstraint(JackMatchers.equalTo(value));
    return this;
  }

  public UserQueryBuilder createdAtMillis(QueryConstraint<Long value) {
    addConstraint(JackMatchers.equalTo(value));
    return this;
  }

  public UserQueryBuilder numPosts(int value) {
    addConstraint(JackMatchers.equalTo(value));
    return this;
  }

  public UserQueryBuilder numPosts(QueryConstraint<Integer value) {
    addConstraint(JackMatchers.equalTo(value));
    return this;
  }

  public UserQueryBuilder someDate(Long value) {
    addConstraint(JackMatchers.equalTo(value));
    return this;
  }

  public UserQueryBuilder someDate(QueryConstraint<Long value) {
    addConstraint(JackMatchers.equalTo(value));
    return this;
  }

  public UserQueryBuilder someDatetime(Long value) {
    addConstraint(JackMatchers.equalTo(value));
    return this;
  }

  public UserQueryBuilder someDatetime(QueryConstraint<Long value) {
    addConstraint(JackMatchers.equalTo(value));
    return this;
  }

  public UserQueryBuilder bio(String value) {
    addConstraint(JackMatchers.equalTo(value));
    return this;
  }

  public UserQueryBuilder bio(QueryConstraint<String value) {
    addConstraint(JackMatchers.equalTo(value));
    return this;
  }

  public UserQueryBuilder someBinary(byte[] value) {
    addConstraint(JackMatchers.equalTo(value));
    return this;
  }

  public UserQueryBuilder someBinary(QueryConstraint<byte[] value) {
    addConstraint(JackMatchers.equalTo(value));
    return this;
  }

  public UserQueryBuilder someFloat(Double value) {
    addConstraint(JackMatchers.equalTo(value));
    return this;
  }

  public UserQueryBuilder someFloat(QueryConstraint<Double value) {
    addConstraint(JackMatchers.equalTo(value));
    return this;
  }

  public UserQueryBuilder someDecimal(Double value) {
    addConstraint(JackMatchers.equalTo(value));
    return this;
  }

  public UserQueryBuilder someDecimal(QueryConstraint<Double value) {
    addConstraint(JackMatchers.equalTo(value));
    return this;
  }

  public UserQueryBuilder someBoolean(Boolean value) {
    addConstraint(JackMatchers.equalTo(value));
    return this;
  }

  public UserQueryBuilder someBoolean(QueryConstraint<Boolean value) {
    addConstraint(JackMatchers.equalTo(value));
    return this;
  }
}
