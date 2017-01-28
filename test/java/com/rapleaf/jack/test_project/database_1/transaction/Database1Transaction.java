package com.rapleaf.jack.test_project.database_1.transaction;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import com.rapleaf.jack.queries.Column;
import com.rapleaf.jack.queries.GenericQuery;
import com.rapleaf.jack.queries.Records;
import com.rapleaf.jack.test_project.database_1.IDatabase1;
import com.rapleaf.jack.test_project.database_1.iface.ICommentPersistence;
import com.rapleaf.jack.test_project.database_1.iface.IImagePersistence;
import com.rapleaf.jack.test_project.database_1.iface.ILockableModelPersistence;
import com.rapleaf.jack.test_project.database_1.iface.IPostPersistence;
import com.rapleaf.jack.test_project.database_1.iface.IUserPersistence;
import com.rapleaf.jack.transaction.ITransaction;

public class Database1Transaction implements ITransaction<IDatabase1> {

  private final IDatabase1 database1;

  public Database1Transaction(IDatabase1 database1) {
    this.database1 = database1;
    database1.setAutoCommit(false);
  }

  public GenericQuery.Builder createQuery() {
    return database1.createQuery();
  }

  public Records findBySql(String statement, List<?> params, Set<Column> columns) throws IOException {
    return database1.findBySql(statement, params, columns);
  }

  public ICommentPersistence comments(){
    return database1.comments();
  }

  public IImagePersistence images(){
    return database1.images();
  }

  public ILockableModelPersistence lockableModels(){
    return database1.lockableModels();
  }

  public IPostPersistence posts(){
    return database1.posts();
  }

  public IUserPersistence users(){
    return database1.users();
  }

  public boolean deleteAll() throws IOException {
    return database1.deleteAll();
  }

  @Override
  public void commit() {
    database1.commit();
  }

  @Override
  public void rollback() {
    database1.rollback();
  }

  @Override
  public void close() {
    database1.commit();
    database1.setAutoCommit(true);
  }
}
