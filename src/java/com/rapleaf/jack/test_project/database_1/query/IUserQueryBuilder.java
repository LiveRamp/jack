package com.rapleaf.jack.test_project.database_1.query;

import java.util.Set;

import com.rapleaf.jack.queries.FieldSelector;
import com.rapleaf.jack.queries.where_operators.IWhereOperator;
import com.rapleaf.jack.queries.QueryOrder;
import com.rapleaf.jack.test_project.database_1.models.User;

public interface IUserQueryBuilder {

  UserQueryBuilder select(User._Fields... fields);

  UserQueryBuilder selectAgg(FieldSelector... aggregatedFields);

  UserQueryBuilder id(Long value);

  UserQueryBuilder idIn(Set<Long> values);

  UserQueryBuilder limit(int offset, int nResults);

  UserQueryBuilder limit(int nResults);

  UserQueryBuilder groupBy(User._Fields... fields);

  IOrderedUserQueryBuilder order();

  IOrderedUserQueryBuilder order(QueryOrder queryOrder);

  IOrderedUserQueryBuilder orderById();

  IOrderedUserQueryBuilder orderById(QueryOrder queryOrder);

  UserQueryBuilder handle(String value);

  UserQueryBuilder whereHandle(IWhereOperator<String> operator);

  IOrderedUserQueryBuilder orderByHandle();

  IOrderedUserQueryBuilder orderByHandle(QueryOrder queryOrder);

  UserQueryBuilder createdAtMillis(Long value);

  UserQueryBuilder whereCreatedAtMillis(IWhereOperator<Long> operator);

  IOrderedUserQueryBuilder orderByCreatedAtMillis();

  IOrderedUserQueryBuilder orderByCreatedAtMillis(QueryOrder queryOrder);

  UserQueryBuilder numPosts(Integer value);

  UserQueryBuilder whereNumPosts(IWhereOperator<Integer> operator);

  IOrderedUserQueryBuilder orderByNumPosts();

  IOrderedUserQueryBuilder orderByNumPosts(QueryOrder queryOrder);

  UserQueryBuilder someDate(Long value);

  UserQueryBuilder whereSomeDate(IWhereOperator<Long> operator);

  IOrderedUserQueryBuilder orderBySomeDate();

  IOrderedUserQueryBuilder orderBySomeDate(QueryOrder queryOrder);

  UserQueryBuilder someDatetime(Long value);

  UserQueryBuilder whereSomeDatetime(IWhereOperator<Long> operator);

  IOrderedUserQueryBuilder orderBySomeDatetime();

  IOrderedUserQueryBuilder orderBySomeDatetime(QueryOrder queryOrder);

  UserQueryBuilder bio(String value);

  UserQueryBuilder whereBio(IWhereOperator<String> operator);

  IOrderedUserQueryBuilder orderByBio();

  IOrderedUserQueryBuilder orderByBio(QueryOrder queryOrder);

  UserQueryBuilder someBinary(byte[] value);

  UserQueryBuilder whereSomeBinary(IWhereOperator<byte[]> operator);

  IOrderedUserQueryBuilder orderBySomeBinary();

  IOrderedUserQueryBuilder orderBySomeBinary(QueryOrder queryOrder);

  UserQueryBuilder someFloat(Double value);

  UserQueryBuilder whereSomeFloat(IWhereOperator<Double> operator);

  IOrderedUserQueryBuilder orderBySomeFloat();

  IOrderedUserQueryBuilder orderBySomeFloat(QueryOrder queryOrder);

  UserQueryBuilder someDecimal(Double value);

  UserQueryBuilder whereSomeDecimal(IWhereOperator<Double> operator);

  IOrderedUserQueryBuilder orderBySomeDecimal();

  IOrderedUserQueryBuilder orderBySomeDecimal(QueryOrder queryOrder);

  UserQueryBuilder someBoolean(Boolean value);

  UserQueryBuilder whereSomeBoolean(IWhereOperator<Boolean> operator);

  IOrderedUserQueryBuilder orderBySomeBoolean();

  IOrderedUserQueryBuilder orderBySomeBoolean(QueryOrder queryOrder);
}
