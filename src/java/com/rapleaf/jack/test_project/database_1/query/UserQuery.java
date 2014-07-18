package com.rapleaf.jack.test_project.database_1.query;

import com.rapleaf.jack.AbstractModelQuery;

import com.rapleaf.jack.test_project.database_1.models.User;
import com.rapleaf.jack.test_project.database_1.iface.IUserPersistence;


public class UserQuery extends AbstractModelQuery {

  public UserQuery (IUserPersistence caller) {
    super(caller);
  }

  public UserQuery handle(String value) {
    fieldsMap.put(User._Fields.handle, value);
    return this;
  }

  public UserQuery created_at_millis(Long value) {
    fieldsMap.put(User._Fields.created_at_millis, value);
    return this;
  }

  public UserQuery num_posts(int value) {
    fieldsMap.put(User._Fields.num_posts, value);
    return this;
  }

  public UserQuery some_date(Long value) {
    fieldsMap.put(User._Fields.some_date, value);
    return this;
  }

  public UserQuery some_datetime(Long value) {
    fieldsMap.put(User._Fields.some_datetime, value);
    return this;
  }

  public UserQuery bio(String value) {
    fieldsMap.put(User._Fields.bio, value);
    return this;
  }

  public UserQuery some_binary(byte[] value) {
    fieldsMap.put(User._Fields.some_binary, value);
    return this;
  }

  public UserQuery some_float(Double value) {
    fieldsMap.put(User._Fields.some_float, value);
    return this;
  }

  public UserQuery some_decimal(Double value) {
    fieldsMap.put(User._Fields.some_decimal, value);
    return this;
  }

  public UserQuery some_boolean(Boolean value) {
    fieldsMap.put(User._Fields.some_boolean, value);
    return this;
  }
}
