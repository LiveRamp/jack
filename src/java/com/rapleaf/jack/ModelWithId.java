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

import java.io.Serializable;
import java.util.Set;

import org.apache.commons.lang.builder.HashCodeBuilder;

public abstract class ModelWithId implements Serializable {
  private final int id;

  protected ModelWithId(int id) {
    this.id = id;
  }

  public int getId() {
    return id;
  }

//  @Override
//  public int hashCode() {
//    final int prime = 31;
//    long result = 1;
//    result = prime * result + id;
//    return (int) result;
//  }

  @Override
  public int hashCode() {
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
    return hcb.toHashCode();
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
    if (id != other.id)
      return false;
    return true;
  }

  public abstract ModelWithId getCopy();
  
  public abstract Object getField(String fieldName);

  public abstract Set<Enum> getFieldSet();

  protected static byte[] copyBinary(final byte[] orig) {
    if (orig == null) {
      return null;
    }

    byte[] copy = new byte[orig.length];
    System.arraycopy(orig, 0, copy, 0, orig.length);
    return copy;
  }
}
