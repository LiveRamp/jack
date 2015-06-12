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
import java.nio.ByteBuffer;
import java.util.Set;

import org.apache.commons.lang.builder.HashCodeBuilder;

import com.rapleaf.jack.util.JackUtility;

public abstract class ModelWithId<T extends ModelWithId, D extends GenericDatabases> implements Serializable {
  protected D databases;
  transient protected int cachedHashCode = 0;
  private boolean created = false;

  protected ModelWithId(D databases) {
    this.databases = databases;
  }

  public long getId() {
    return getAttributes().getId();
  }

  public int getIntId() {
    return JackUtility.safeLongToInt(getId());
  }

  public abstract ModelIdWrapper getTypedId();

  @Override
  public int hashCode() {
    if (cachedHashCode == 0) {
      HashCodeBuilder hcb = new HashCodeBuilder();
      hcb.append(this.getClass().getName());
      hcb.append(getId());
      for (Enum field : getFieldSet()) {
        hcb.append(field.name());
        Object value = getField(field.name());
        if (value != null) {
          hcb.append(value);
        }
      }
      cachedHashCode = hcb.toHashCode();
    }
    return cachedHashCode;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    ModelWithId other = (ModelWithId) obj;
    return equals(other);
  }

  public boolean equals(ModelWithId obj) {
    if(obj == null) return false;
    if(!this.getClass().getName().equals(obj.getClass().getName())) {
      return false;
    }
    if(getId() != obj.getId()) {
      return false;
    }

    for (Enum field : getFieldSet()) {
      Object value1 = getField(field.name());
      Object value2 = obj.getField(field.name());
      if (value1 != null) {
        if (value1 instanceof byte[]) {
          value1 = ByteBuffer.wrap((byte[]) value1);
          value2 = ByteBuffer.wrap((byte[]) value2);
        }
        if(!value1.equals(value2)) {
          return false;
        }
      } else {
        if(value2 != null) {
          return false;
        }
      }
    }
    return true;
  }

  public abstract AttributesWithId getAttributes();

  public abstract T getCopy();

  public abstract T getCopy(D databases);

  public abstract boolean save() throws IOException;

  public abstract Object getField(String fieldName);

  public abstract boolean hasField(String fieldName);

  public abstract void setField(String fieldName, Object value);

  public abstract Set<Enum> getFieldSet();

  public abstract void unsetAssociations();

  protected static byte[] copyBinary(final byte[] orig) {
    if (orig == null) {
      return null;
    }

    byte[] copy = new byte[orig.length];
    System.arraycopy(orig, 0, copy, 0, orig.length);
    return copy;
  }

  public boolean isCreated() {
    return created;
  }

  public void setCreated(boolean created) {
    this.created = created;
  }

  protected void unsetDatabaseReference() {
    this.databases = null;
  }
}
