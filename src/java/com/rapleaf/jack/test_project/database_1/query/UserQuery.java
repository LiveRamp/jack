package com.rapleaf.jack.test_project.database_1.query;

import com.rapleaf.jack.AbstractModelQuery;

import com.rapleaf.jack.test_project.database_1.models.User;
import com.rapleaf.jack.test_project.database_1.iface.IUserPersistence;


public class UserQuery extends AbstractModelQuery<User> {

  public UserQuery (IUserPersistence caller) {
    super(caller);
  }

  public UserQuery handle(String value) {
    fieldsMap.put(User._Fields.handle, value);
    return this;
  }

  public UserQuery createdAtMillis(Long value) {
    fieldsMap.put(User._Fields.created_at_millis, value);
    return this;
  }

  public UserQuery numPosts(int value) {
    fieldsMap.put(User._Fields.num_posts, value);
    return this;
  }

  public UserQuery someDate(Long value) {
    fieldsMap.put(User._Fields.some_date, value);
    return this;
  }

  public UserQuery someDatetime(Long value) {
    fieldsMap.put(User._Fields.some_datetime, value);
    return this;
  }

  public UserQuery bio(String value) {
    fieldsMap.put(User._Fields.bio, value);
    return this;
  }

  public UserQuery someBinary(byte[] value) {
    fieldsMap.put(User._Fields.some_binary, value);
    return this;
  }

  public UserQuery someFloat(Double value) {
    fieldsMap.put(User._Fields.some_float, value);
    return this;
  }

  public UserQuery someDecimal(Double value) {
    fieldsMap.put(User._Fields.some_decimal, value);
    return this;
  }

  public UserQuery someBoolean(Boolean value) {
    fieldsMap.put(User._Fields.some_boolean, value);
    return this;
  }
}
