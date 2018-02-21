//
// Copyright 2011 Rapleaf
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.rapleaf.jack;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.rapleaf.jack.queries.ModelDelete;
import com.rapleaf.jack.queries.ModelQuery;

public interface IModelPersistence<T extends ModelWithId> extends Serializable {

  @FunctionalInterface
  interface RecordSelector<T extends ModelWithId> {
    boolean selectRecord(T record);
  }

  ModelWithId create(Map<Enum, Object> fieldsMap) throws IOException;

  /**
   * Update an existing T instance in the persistence.
   *
   * @param model
   * @return
   * @throws IOException
   */
  boolean save(T model) throws IOException;

  /**
   * Update an existing T instance in the persistence. This method allows you to manually specify an update time
   * and should only be used for testing.
   *
   * @param updateTimeMillis
   * @param model
   * @return
   * @throws IOException
   */
  boolean save(long updateTimeMillis, T model) throws IOException;

  /**
   * Find the T instance with specified id, or null if there is no such instance.
   *
   * @param id
   * @return
   * @throws IOException
   */
  T find(long id) throws IOException;

  List<T> find(Set<Long> ids) throws IOException;

  List<T> find(Map<Enum, Object> fieldsMap) throws IOException;

  List<T> find(Set<Long> ids, Map<Enum, Object> fieldsMap) throws IOException;

  List<T> find(ModelQuery query) throws IOException;

  List<T> findWithOrder(ModelQuery query) throws IOException;

  boolean delete(ModelDelete delete) throws IOException;

  void clearCacheById(long id) throws IOException;

  List<T> findAllByForeignKey(String foreignKey, long id) throws IOException;

  List<T> findAllByForeignKey(String foreignKey, Set<Long> ids) throws IOException;

  void clearCacheByForeignKey(String foreignKey, long id);

  void clearForeignKeyCache();

  /**
   * Effectively the same as delete(model.getId()).
   *
   * @param model
   * @return
   * @throws IOException
   */
  boolean delete(T model) throws IOException;

  /**
   * Destroy record with <i>id</i>.
   *
   * @param id
   * @return
   * @throws IOException
   */
  boolean delete(long id) throws IOException;

  /**
   * Delete all records in this persistence.
   *
   * @return
   * @throws IOException
   */
  boolean deleteAll() throws IOException;

  List<T> findAll() throws IOException;

  List<T> findAll(String conditions) throws IOException;

  List<T> findAll(String conditions, RecordSelector<T> selector) throws IOException;

  /**
   * Caching is on by default, and is toggled with enableCaching() and disableCaching().
   * <p/>
   * While caching is disabled, the cache is neither read from nor written to.  However,
   * disableCaching() does not clear the cache, so the cache contents are preserved for when
   * caching is enabled again.
   */
  boolean isCaching();

  void enableCaching();

  void disableCaching();

  boolean isEmpty() throws IOException;
}
