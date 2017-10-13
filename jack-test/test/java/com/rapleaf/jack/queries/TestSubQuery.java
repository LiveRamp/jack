package com.rapleaf.jack.queries;

import org.joda.time.DateTime;
import org.junit.Test;

import com.rapleaf.jack.test_project.database_1.models.User;

public class TestSubQuery extends TestGenericQuery {

  @Test
  public void testSubQuery() throws Exception {
    GenericQuery subQuery = db.createQuery().from(User.TBL)
        .where(User.ID.greaterThan(0L))
        .select(AggregatedColumn.MIN(User.ID));

    GenericQuery query = db.createQuery().from(User.TBL)
        .where(User.ID.greaterThan(subQuery));

    System.out.println(query.getSqlStatement());
    System.out.println(query.fetch());
  }

  @Test
  public void testDate() throws Exception {
    userA = users.createDefaultInstance().setSomeDatetime(DateTime.parse("2017-09-10").getMillis());
    userB = users.createDefaultInstance().setSomeDatetime(DateTime.parse("2017-09-11").getMillis());
    userC = users.createDefaultInstance().setSomeDatetime(DateTime.parse("2017-09-12").getMillis());
    userA.save();
    userB.save();
    userC.save();

    GenericQuery query = db.createQuery()
        .from(User.TBL)
        .where(User.SOME_DATETIME.equalTo(
            db.createQuery()
                .from(User.TBL)
                .select(AggregatedColumn.MAX(User.SOME_DATETIME))
            )
        );

    System.out.println(query.getQueryStatement());
    System.out.println(userC.getSomeDatetime());
    System.out.println(query.fetch().get(0).get(User.SOME_DATETIME));
  }

}
