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
import java.sql.SQLException;
import java.util.Map;
import java.util.Set;
import java.util.List;

import com.rapleaf.jack.queries.ModelQuery;

public interface IModelPersistence<T extends ModelWithId> extends Serializable {

  public interface RecordSelector<T extends ModelWithId> {
    public boolean selectRecord(T record);
  }

  public ModelWithId create(Map<Enum, Object> fieldsMap) throws IOException;

  /**
   * Update an existing T instance in the persistence.
   *
   * @param model
   * @return
   * @throws IOException
   */
  public boolean save(T model) throws IOException;

  /**
   * Find the T instance with specified id, or null if there is no such instance.
   *
   * @param id
   * @return
   * @throws IOException
   */
  public T find(long id) throws IOException;

  public Set<T> find(Set<Long> ids) throws IOException;

  public Set<T> find(Map<Enum, Object> fieldsMap) throws IOException;

  public Set<T> find(Set<Long> ids, Map<Enum, Object> fieldsMap) throws IOException;

  public Set<T> find(ModelQuery query) throws IOException;
  
  public List<T> findWithOrder(ModelQuery query) throws IOException;

  public void clearCacheById(long id) throws IOException;

  public Set<T> findAllByForeignKey(String foreignKey, long id) throws IOException;

  public Set<T> findAllByForeignKey(String foreignKey, Set<Long> ids) throws IOException;

  public void clearCacheByForeignKey(String foreignKey, long id);

  public void clearForeignKeyCache();

  /**
   * Effectively the same as delete(model.getId()).
   *
   * @param model
   * @return
   * @throws IOException
   */
  public boolean delete(T model) throws IOException;

  /**
   * Destroy record with <i>id</i>.
   *
   * @param id
   * @return
   * @throws IOException
   */
  public boolean delete(long id) throws IOException;

  /**
   * Delete all records in this persistence.
   *
   * @return
   * @throws IOException
   */
  public boolean deleteAll() throws IOException;

  public Set<T> findAll() throws IOException;

  public Set<T> findAll(String conditions) throws IOException;

  public Set<T> findAll(String conditions, RecordSelector<T> selector) throws IOException;

  /**
   * Caching is on by default, and is toggled with enableCaching() and disableCaching().
   * <p/>
   * While caching is disabled, the cache is neither read from nor written to.  However,
   * disableCaching() does not clear the cache, so the cache contents are preserved for when
   * caching is enabled again.
   */
  public boolean isCaching();

  public void enableCaching();

  public void disableCaching();
}
