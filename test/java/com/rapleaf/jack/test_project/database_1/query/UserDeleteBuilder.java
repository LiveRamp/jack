package com.rapleaf.jack.test_project.database_1.query;

import java.util.Collection;
import java.util.Set;

import com.rapleaf.jack.queries.AbstractDeleteBuilder;
import com.rapleaf.jack.queries.where_operators.IWhereOperator;
import com.rapleaf.jack.queries.where_operators.JackMatchers;
import com.rapleaf.jack.queries.WhereConstraint;
import com.rapleaf.jack.test_project.database_1.iface.IUserPersistence;
import com.rapleaf.jack.test_project.database_1.models.User;


public class UserDeleteBuilder extends AbstractDeleteBuilder<User> {

  public UserDeleteBuilder(IUserPersistence caller) {
    super(caller);
  }

  public UserDeleteBuilder id(Long value) {
    addId(value);
    return this;
  }

  public UserDeleteBuilder idIn(Collection<Long> values) {
    addIds(values);
    return this;
  }

  public UserDeleteBuilder handle(String value) {
    addWhereConstraint(new WhereConstraint<String>(User._Fields.handle, JackMatchers.equalTo(value)));
    return this;
  }

  public UserDeleteBuilder whereHandle(IWhereOperator<String> operator) {
    addWhereConstraint(new WhereConstraint<String>(User._Fields.handle, operator));
    return this;
  }

  public UserDeleteBuilder createdAtMillis(Long value) {
    addWhereConstraint(new WhereConstraint<Long>(User._Fields.created_at_millis, JackMatchers.equalTo(value)));
    return this;
  }

  public UserDeleteBuilder whereCreatedAtMillis(IWhereOperator<Long> operator) {
    addWhereConstraint(new WhereConstraint<Long>(User._Fields.created_at_millis, operator));
    return this;
  }

  public UserDeleteBuilder numPosts(Integer value) {
    addWhereConstraint(new WhereConstraint<Integer>(User._Fields.num_posts, JackMatchers.equalTo(value)));
    return this;
  }

  public UserDeleteBuilder whereNumPosts(IWhereOperator<Integer> operator) {
    addWhereConstraint(new WhereConstraint<Integer>(User._Fields.num_posts, operator));
    return this;
  }

  public UserDeleteBuilder someDate(Long value) {
    addWhereConstraint(new WhereConstraint<Long>(User._Fields.some_date, JackMatchers.equalTo(value)));
    return this;
  }

  public UserDeleteBuilder whereSomeDate(IWhereOperator<Long> operator) {
    addWhereConstraint(new WhereConstraint<Long>(User._Fields.some_date, operator));
    return this;
  }

  public UserDeleteBuilder someDatetime(Long value) {
    addWhereConstraint(new WhereConstraint<Long>(User._Fields.some_datetime, JackMatchers.equalTo(value)));
    return this;
  }

  public UserDeleteBuilder whereSomeDatetime(IWhereOperator<Long> operator) {
    addWhereConstraint(new WhereConstraint<Long>(User._Fields.some_datetime, operator));
    return this;
  }

  public UserDeleteBuilder bio(String value) {
    addWhereConstraint(new WhereConstraint<String>(User._Fields.bio, JackMatchers.equalTo(value)));
    return this;
  }

  public UserDeleteBuilder whereBio(IWhereOperator<String> operator) {
    addWhereConstraint(new WhereConstraint<String>(User._Fields.bio, operator));
    return this;
  }

  public UserDeleteBuilder someBinary(byte[] value) {
    addWhereConstraint(new WhereConstraint<byte[]>(User._Fields.some_binary, JackMatchers.equalTo(value)));
    return this;
  }

  public UserDeleteBuilder whereSomeBinary(IWhereOperator<byte[]> operator) {
    addWhereConstraint(new WhereConstraint<byte[]>(User._Fields.some_binary, operator));
    return this;
  }

  public UserDeleteBuilder someFloat(Double value) {
    addWhereConstraint(new WhereConstraint<Double>(User._Fields.some_float, JackMatchers.equalTo(value)));
    return this;
  }

  public UserDeleteBuilder whereSomeFloat(IWhereOperator<Double> operator) {
    addWhereConstraint(new WhereConstraint<Double>(User._Fields.some_float, operator));
    return this;
  }

  public UserDeleteBuilder someDecimal(Double value) {
    addWhereConstraint(new WhereConstraint<Double>(User._Fields.some_decimal, JackMatchers.equalTo(value)));
    return this;
  }

  public UserDeleteBuilder whereSomeDecimal(IWhereOperator<Double> operator) {
    addWhereConstraint(new WhereConstraint<Double>(User._Fields.some_decimal, operator));
    return this;
  }

  public UserDeleteBuilder someBoolean(Boolean value) {
    addWhereConstraint(new WhereConstraint<Boolean>(User._Fields.some_boolean, JackMatchers.equalTo(value)));
    return this;
  }

  public UserDeleteBuilder whereSomeBoolean(IWhereOperator<Boolean> operator) {
    addWhereConstraint(new WhereConstraint<Boolean>(User._Fields.some_boolean, operator));
    return this;
  }
}
