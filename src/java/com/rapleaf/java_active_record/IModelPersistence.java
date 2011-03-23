package com.rapleaf.db_schemas;

import java.io.IOException;
import java.io.Serializable;
import java.util.Set;


public interface IModelPersistence<T extends ModelWithId> extends Serializable {
  /**
   * Update an existing T instance in the persistence.
   * @param model
   * @return
   * @throws IOException
   */
  public boolean save(T model) throws IOException;

  /**
   * Find the T instance with specified id, or null if there is no such instance.
   * @param id
   * @return
   * @throws IOException
   */
  public T find(long id) throws IOException;

  public void clearCacheById(long id) throws IOException;

  public Set<T> findAllByForeignKey(String foreignKey, long id) throws IOException;

  public void clearCacheByForeignKey(String foreignKey, long id);

  /**
   * Effectively the same as delete(model.getId()).
   * @param model
   * @return
   * @throws IOException
   */
  public boolean delete(T model) throws IOException;

  /**
   * Destroy record with <i>id</i>.
   * @param id
   * @return
   * @throws IOException
   */
  public boolean delete(long id) throws IOException;

  /**
   * Delete all records in this persistence.
   * @return
   * @throws IOException
   */
  public boolean deleteAll() throws IOException;

  public Set<T> findAll() throws IOException;

  public Set<T> findAll(String conditions) throws IOException;
}
