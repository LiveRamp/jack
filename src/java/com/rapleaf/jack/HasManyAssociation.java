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

public class HasManyAssociation<T extends ModelWithId> implements Serializable {
  private final IModelPersistence<T> persistence;
  private final String foreignKey;
  private final long id;
  private Set<T> cachedList;

  public HasManyAssociation(IModelPersistence<T> persistence,
      String foreignKey, long id) {
    this.persistence = persistence;
    this.foreignKey = foreignKey;
    this.id = id;
  }

  public Set<T> get() throws IOException {
    if (cachedList == null) {
      cachedList = persistence.findAllByForeignKey(foreignKey, id);
    }
    return cachedList;
  }

  public void clearCache() throws IOException {
    persistence.clearCacheByForeignKey(foreignKey, id);
    cachedList = null;
  }
}
