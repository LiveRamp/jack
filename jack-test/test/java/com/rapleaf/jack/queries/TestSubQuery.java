package com.rapleaf.jack.queries;

import java.io.IOException;

import com.google.common.collect.Lists;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import com.rapleaf.jack.JackTestCase;
import com.rapleaf.jack.test_project.DatabasesImpl;
import com.rapleaf.jack.test_project.database_1.IDatabase1;
import com.rapleaf.jack.test_project.database_1.iface.IPostPersistence;
import com.rapleaf.jack.test_project.database_1.iface.IUserPersistence;
import com.rapleaf.jack.test_project.database_1.models.Post;
import com.rapleaf.jack.test_project.database_1.models.User;

import static org.junit.Assert.assertEquals;

public class TestSubQuery extends JackTestCase {
  private static final IDatabase1 db = new DatabasesImpl().getDatabase1();

  private final IUserPersistence users = db.users();
  private final IPostPersistence posts = db.posts();

  private User userA, userB, userC;
  private Post postA, postB, postC;

  private Records records;

  @Before
  public void prepare() throws Exception {
    users.deleteAll();
    posts.deleteAll();

    userA = users.createDefaultInstance().setNumPosts(12).setBio("A").setSomeDatetime(DateTime.parse("2017-09-10").getMillis());
    userB = users.createDefaultInstance().setNumPosts(11).setBio("B").setSomeDatetime(DateTime.parse("2017-09-11").getMillis());
    userC = users.createDefaultInstance().setNumPosts(10).setBio("C").setSomeDatetime(DateTime.parse("2017-09-12").getMillis());
    userA.save();
    userB.save();
    userC.save();

    postA = posts.createDefaultInstance().setUserId(userA.getIntId()).setTitle("title1");
    postB = posts.createDefaultInstance().setUserId(userB.getIntId()).setTitle("title2");
    postC = posts.createDefaultInstance().setUserId(userC.getIntId()).setTitle("title3");
    postA.save();
    postB.save();
    postC.save();
  }

  @Test
  public void testWhereClauseSingleValueSubQuery() throws Exception {
    /*
     * single sub query with one result
     */
    SingleValue<Long> userId = db.createQuery()
        .from(User.TBL)
        .where(User.BIO.equalTo("B"))
        .asSingleValue(User.ID);

    records = db.createQuery()
        .from(User.TBL)
        .where(User.ID.equalTo(userId))
        .fetch();

    assertEquals(1, records.size());
    assertEquals(userB, records.get(0).getModel(User.TBL, db.getDatabases()));

    /*
     * single sub query with aggregation
     */
    SingleValue<Integer> maxNumPosts = db.createQuery()
        .from(User.TBL)
        .asSingleValue(AggregatedColumn.MAX(User.NUM_POSTS));

    records = db.createQuery()
        .from(User.TBL)
        .where(User.NUM_POSTS.equalTo(maxNumPosts))
        .fetch();

    assertEquals(1, records.size());
    assertEquals(userA, records.get(0).getModel(User.TBL, db.getDatabases()));

    /*
     * test date column
     */
    SingleValue<Long> maxDatetime = db.createQuery()
        .from(User.TBL)
        .asSingleValue(AggregatedColumn.MAX(User.SOME_DATETIME));

    records = db.createQuery()
        .from(User.TBL)
        .where(User.SOME_DATETIME.equalTo(maxDatetime))
        .fetch();

    assertEquals(1, records.size());
    assertEquals(userC, records.get(0).getModel(User.TBL, db.getDatabases()));
  }

  @Test(expected = IOException.class)
  public void testWhereClauseSingleValueSubQueryException() throws Exception {
    /*
     * exception will be thrown when multi value is incorrectly assigned to single value,
     */
    SingleValue<Long> userIds = db.createQuery()
        .from(User.TBL)
        .asSingleValue(User.ID);

    records = db.createQuery()
        .from(User.TBL)
        .where(User.ID.equalTo(userIds))
        .fetch();
  }

  @Test
  public void testWhereClauseMultiValueSubQuery() throws Exception {
    /*
     * single-row sub query
     */
    MultiValue<Long> userId = db.createQuery()
        .from(User.TBL)
        .where(User.BIO.equalTo("A"))
        .asMultiValue(User.ID);

    records = db.createQuery()
        .from(User.TBL)
        .where(User.ID.in(userId))
        .fetch();

    assertEquals(1, records.size());
    assertEquals(userA, records.get(0).getModel(User.TBL, db.getDatabases()));

    /*
     * multiple-row sub query
     */
    MultiValue<Long> userIds = db.createQuery()
        .from(User.TBL)
        .where(User.BIO.notEqualTo("A"))
        .asMultiValue(User.ID);

    records = db.createQuery()
        .from(User.TBL)
        .where(User.ID.in(userIds))
        .orderBy(User.ID)
        .fetch();

    assertEquals(2, records.size());
    assertEquals(userB, records.get(0).getModel(User.TBL, db.getDatabases()));
    assertEquals(userC, records.get(1).getModel(User.TBL, db.getDatabases()));
  }

  @Test
  public void testFromClauseSubQuery() throws Exception {
    /*
     * sub query with where clause
     */
    // sub query result: userB, userC
    SubTable subQuery = db.createQuery()
        .from(User.TBL)
        .where(User.SOME_DATETIME.notEqualTo(DateTime.parse("2017-09-10").getMillis()))
        .asSubTable("subQuery");

    records = db.createQuery()
        .from(subQuery)
        .fetch();

    assertEquals(2, records.size());
    assertEquals(userB, records.get(0).getModel(subQuery.model(User.TBL), db.getDatabases()));
    assertEquals(userB, records.get(0).getModel(User.Tbl.as("subQuery"), db.getDatabases()));
    assertEquals(userC, records.get(1).getModel(subQuery.model(User.TBL), db.getDatabases()));
    assertEquals(userC, records.get(1).getModel(User.Tbl.as("subQuery"), db.getDatabases()));

    /*
     * sub query with select clause
     */
    // sub query result: userB, userC
    SubTable subQueryWithSelectClause = db.createQuery()
        .from(User.TBL)
        .where(User.NUM_POSTS.notEqualTo(12))
        .select(User.ID, User.NUM_POSTS)
        .asSubTable("selectClause");

    records = db.createQuery()
        .from(subQueryWithSelectClause)
        .select(subQueryWithSelectClause.column(User.ID), subQueryWithSelectClause.column(User.NUM_POSTS))
        .fetch();

    assertEquals(2, records.size());
    assertEquals(Lists.newArrayList(userB.getId(), userC.getId()), records.gets(subQueryWithSelectClause.column(User.ID)));
    assertEquals(Lists.newArrayList(userB.getNumPosts(), userC.getNumPosts()), records.gets(subQueryWithSelectClause.column(User.NUM_POSTS)));

    /*
     * sub query with order and limit clause
     */
    // sub query result: userC, userB
    SubTable subQueryWithOrderClause = db.createQuery()
        .from(User.TBL)
        .orderBy(User.BIO, QueryOrder.DESC)
        .limit(2)
        .asSubTable("orderClause");

    records = db.createQuery()
        .from(subQueryWithOrderClause)
        .fetch();

    assertEquals(2, records.size());
    assertEquals(userC, records.get(0).getModel(subQueryWithOrderClause.model(User.TBL), db.getDatabases()));
    assertEquals(userB, records.get(1).getModel(subQueryWithOrderClause.model(User.TBL), db.getDatabases()));

    /*
     * nested sub query
     */
    // t1 result: userB, userC
    SubTable t1 = db.createQuery()
        .from(User.TBL)
        .where(User.BIO.notEqualTo("A"))
        .asSubTable("t1");

    // t2 result: userB
    SubTable t2 = db.createQuery()
        .from(t1)
        .where(t1.column(User.NUM_POSTS).greaterThan(10))
        .asSubTable("t2");

    // final result: userB
    records = db.createQuery()
        .from(t2)
        .fetch();

    assertEquals(1, records.size());
    assertEquals(userB, records.get(0).getModel(t2.model(User.TBL), db.getDatabases()));
  }

  @Test
  public void testJoinClauseSubQuery() throws Exception {
    // post table result: postB, postC
    SubTable postTable = db.createQuery()
        .from(Post.TBL)
        .where(Post.USER_ID.notEqualTo(userA.getIntId()))
        // order by clause does not affect the final query result
        .orderBy(Post.ID)
        .asSubTable("post_table");

    // query result: userC / postC, userB / postB
    records = db.createQuery()
        .from(User.TBL)
        .innerJoin(postTable)
        .on(postTable.column(Post.USER_ID).as(Long.class).equalTo(User.ID))
        .orderBy(postTable.column(Post.USER_ID), QueryOrder.DESC)
        .fetch();

    assertEquals(2, records.size());
    assertEquals(userC, records.get(0).getModel(User.TBL, db.getDatabases()));
    assertEquals(postC, records.get(0).getModel(Post.Tbl.as("post_table"), db.getDatabases()));
    assertEquals(postC, records.get(0).getModel(postTable.model(Post.TBL), db.getDatabases()));
    assertEquals(userB, records.get(1).getModel(User.TBL, db.getDatabases()));
    assertEquals(postB, records.get(1).getModel(Post.Tbl.as("post_table"), db.getDatabases()));
    assertEquals(postB, records.get(1).getModel(postTable.model(Post.TBL), db.getDatabases()));
  }

  @Test
  public void testAggregatedColumnSubQuery() throws Exception {
    SubTable postTable = db.createQuery()
        .from(Post.TBL)
        .groupBy(Post.USER_ID)
        .select(Post.USER_ID, AggregatedColumn.COUNT(Post.ID))
        .asSubTable("post_table");

    // both columns refer to "COUNT(posts.id)" for the subquery table
    Column<Integer> postCountColumn1 = AggregatedColumn.COUNT(Post.ID).forTable("post_table");
    Column<Integer> postCountColumn2 = postTable.column(AggregatedColumn.COUNT(Post.ID));

    records = db.createQuery()
        .from(postTable)
        .select(postCountColumn1)
        .fetch();

    for (Record record : records) {
      assertEquals(1, record.get(postCountColumn1).intValue());
      assertEquals(1, record.get(postCountColumn2).intValue());
    }
  }
}
