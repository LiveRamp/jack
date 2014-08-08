package com.rapleaf.jack.test_project.database_1.query;

import com.rapleaf.jack.SimpleQueryBuilder;

import com.rapleaf.jack.test_project.database_1.models.User;
import com.rapleaf.jack.test_project.database_1.iface.IUserPersistence;


public class UserQueryBuilder extends SimpleQueryBuilder<User> {

  public UserQueryBuilder (IUserPersistence caller) {
    super(caller);
  }

  public UserQueryBuilder handle(String value) {
    fieldsMap.put(User._Fields.handle, value);
    return this;
  }

  public UserQueryBuilder createdAtMillis(Long value) {
    fieldsMap.put(User._Fields.created_at_millis, value);
    return this;
  }

  public UserQueryBuilder numPosts(int value) {
    fieldsMap.put(User._Fields.num_posts, value);
    return this;
  }

  public UserQueryBuilder someDate(Long value) {
    fieldsMap.put(User._Fields.some_date, value);
    return this;
  }

  public UserQueryBuilder someDatetime(Long value) {
    fieldsMap.put(User._Fields.some_datetime, value);
    return this;
  }

  public UserQueryBuilder bio(String value) {
    fieldsMap.put(User._Fields.bio, value);
    return this;
  }

  public UserQueryBuilder someBinary(byte[] value) {
    fieldsMap.put(User._Fields.some_binary, value);
    return this;
  }

  public UserQueryBuilder someFloat(Double value) {
    fieldsMap.put(User._Fields.some_float, value);
    return this;
  }

  public UserQueryBuilder someDecimal(Double value) {
    fieldsMap.put(User._Fields.some_decimal, value);
    return this;
  }

  public UserQueryBuilder someBoolean(Boolean value) {
    fieldsMap.put(User._Fields.some_boolean, value);
    return this;
  }
}
