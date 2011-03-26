package com.rapleaf.java_active_record;

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
  public T find(int id) throws IOException;

  public void clearCacheById(int id) throws IOException;

  public Set<T> findAllByForeignKey(String foreignKey, int id) throws IOException;

  public void clearCacheByForeignKey(String foreignKey, int id);

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
  public boolean delete(int id) throws IOException;

  /**
   * Delete all records in this persistence.
   * @return
   * @throws IOException
   */
  public boolean deleteAll() throws IOException;

  public Set<T> findAll() throws IOException;

  public Set<T> findAll(String conditions) throws IOException;
}
