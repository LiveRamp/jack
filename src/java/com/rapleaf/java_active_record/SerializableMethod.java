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
package com.rapleaf.java_active_record;

import java.io.Serializable;
import java.lang.reflect.Method;

public final class SerializableMethod implements Serializable {
  private static final Class<?>[] NO_ARGS = new Class<?>[0];
  private transient Method actualMethod;
  private final String methodName;
  private final String className;

  public SerializableMethod(Class klass, String methodName) {
    this.methodName = methodName;
    this.className = klass.getName();
  }

  public Method getMethod() {
    if (actualMethod == null) {
      try {
        actualMethod = Class.forName(className).getMethod(methodName, NO_ARGS);
      } catch (Exception e) {
        throw new IllegalStateException(e);
      }
    }
    return actualMethod;
  }
}
