//
// Copyright 2011 Rapleaf
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.rapleaf.jack;

import java.io.IOException;
import java.io.Serializable;

public class BelongsToAssociation<T extends ModelWithId> implements Serializable {
  private final IModelPersistence<T> persistence;
  private Long id;
  private T cache;

  public BelongsToAssociation(IModelPersistence<T> persistence, Long id) {
    this.persistence = persistence;
    this.id = id;
  }

  public T get() throws IOException {
    if (id == null)
      return null;
    if (cache == null) {
      cache = persistence.find(id);
    }
    return cache;
  }

  public void setOwnerId(Long id) {
    this.id = id;
  }

  public void setOwnerId(Integer id) {
    this.id = id == null ? null : id.longValue();
  }

  public void clearCache() throws IOException {
    persistence.clearCacheById(id);
    cache = null;
  }
}
