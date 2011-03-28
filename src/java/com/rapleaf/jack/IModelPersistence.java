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
