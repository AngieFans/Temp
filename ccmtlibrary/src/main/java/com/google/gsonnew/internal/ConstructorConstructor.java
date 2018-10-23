/*
 * Copyright (C) 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.gsonnew.internal;

import com.google.gsonnew.InstanceCreator;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Returns a function that can construct an instance of a requested type.
 */
public final class ConstructorConstructor {
  private final Map<Type, InstanceCreator<?>> instanceCreators;

  public ConstructorConstructor(Map<Type, InstanceCreator<?>> instanceCreators) {
    this.instanceCreators = instanceCreators;
  }

  public <T> com.google.gsonnew.internal.ObjectConstructor<T> get(com.google.gsonnew.reflect.TypeToken<T> typeToken) {
    final Type type = typeToken.getType();
    final Class<? super T> rawType = typeToken.getRawType();

    // first try an instance creator

    @SuppressWarnings("unchecked") // types must agree
    final InstanceCreator<T> typeCreator = (InstanceCreator<T>) instanceCreators.get(type);
    if (typeCreator != null) {
      return new com.google.gsonnew.internal.ObjectConstructor<T>() {
        public T construct() {
          return typeCreator.createInstance(type);
        }
      };
    }

    // Next try raw type match for instance creators
    @SuppressWarnings("unchecked") // types must agree
    final InstanceCreator<T> rawTypeCreator =
        (InstanceCreator<T>) instanceCreators.get(rawType);
    if (rawTypeCreator != null) {
      return new com.google.gsonnew.internal.ObjectConstructor<T>() {
        public T construct() {
          return rawTypeCreator.createInstance(type);
        }
      };
    }

    com.google.gsonnew.internal.ObjectConstructor<T> defaultConstructor = newDefaultConstructor(rawType);
    if (defaultConstructor != null) {
      return defaultConstructor;
    }

    com.google.gsonnew.internal.ObjectConstructor<T> defaultImplementation = newDefaultImplementationConstructor(type, rawType);
    if (defaultImplementation != null) {
      return defaultImplementation;
    }

    // finally try unsafe
    return newUnsafeAllocator(type, rawType);
  }

  private <T> com.google.gsonnew.internal.ObjectConstructor<T> newDefaultConstructor(Class<? super T> rawType) {
    try {
      final Constructor<? super T> constructor = rawType.getDeclaredConstructor();
      if (!constructor.isAccessible()) {
        constructor.setAccessible(true);
      }
      return new com.google.gsonnew.internal.ObjectConstructor<T>() {
        @SuppressWarnings("unchecked") // T is the same raw type as is requested
        public T construct() {
          try {
            Object[] args = null;
            return (T) constructor.newInstance(args);
          } catch (InstantiationException e) {
            throw new RuntimeException("Failed to invoke " + constructor + " with no args", e);
          } catch (InvocationTargetException e) {
            throw new RuntimeException("Failed to invoke " + constructor + " with no args",
                e.getTargetException());
          } catch (IllegalAccessException e) {
            throw new AssertionError(e);
          }
        }
      };
    } catch (NoSuchMethodException e) {
      return null;
    }
  }

  /**
   * Constructors for common interface types like Map and List and their
   * subytpes.
   */
  @SuppressWarnings("unchecked") // use runtime checks to guarantee that 'T' is what it is
  private <T> com.google.gsonnew.internal.ObjectConstructor<T> newDefaultImplementationConstructor(
      Type type, Class<? super T> rawType) {
    if (Collection.class.isAssignableFrom(rawType)) {
      if (SortedSet.class.isAssignableFrom(rawType)) {
        return new com.google.gsonnew.internal.ObjectConstructor<T>() {
          public T construct() {
            return (T) new TreeSet<Object>();
          }
        };
      } else if (Set.class.isAssignableFrom(rawType)) {
        return new com.google.gsonnew.internal.ObjectConstructor<T>() {
          public T construct() {
            return (T) new LinkedHashSet<Object>();
          }
        };
      } else if (Queue.class.isAssignableFrom(rawType)) {
        return new com.google.gsonnew.internal.ObjectConstructor<T>() {
          public T construct() {
            return (T) new LinkedList<Object>();
          }
        };
      } else {
        return new com.google.gsonnew.internal.ObjectConstructor<T>() {
          public T construct() {
            return (T) new ArrayList<Object>();
          }
        };
      }
    }

    if (Map.class.isAssignableFrom(rawType)) {
      if (SortedMap.class.isAssignableFrom(rawType)) {
        return new com.google.gsonnew.internal.ObjectConstructor<T>() {
          public T construct() {
            return (T) new TreeMap<Object, Object>();
          }
        };
      } else if (type instanceof ParameterizedType && !(String.class.isAssignableFrom(
          com.google.gsonnew.reflect.TypeToken.get(((ParameterizedType) type).getActualTypeArguments()[0]).getRawType()))) {
        return new com.google.gsonnew.internal.ObjectConstructor<T>() {
          public T construct() {
            return (T) new LinkedHashMap<Object, Object>();
          }
        };
      } else {
        return new com.google.gsonnew.internal.ObjectConstructor<T>() {
          public T construct() {
            return (T) new LinkedTreeMap<String, Object>();
          }
        };
      }
    }

    return null;
  }


  private <T> com.google.gsonnew.internal.ObjectConstructor<T> newUnsafeAllocator(
      final Type type, final Class<? super T> rawType) {
    return new com.google.gsonnew.internal.ObjectConstructor<T>() {
      private final com.google.gsonnew.internal.UnsafeAllocator unsafeAllocator = com.google.gsonnew.internal.UnsafeAllocator.create();
      @SuppressWarnings("unchecked")
      public T construct() {
        try {
          Object newInstance = unsafeAllocator.newInstance(rawType);
          return (T) newInstance;
        } catch (Exception e) {
          throw new RuntimeException(("Unable to invoke no-args constructor for " + type + ". "
              + "Register an InstanceCreator with Gson for this type may fix this problem."), e);
        }
      }
    };
  }

  @Override public String toString() {
    return instanceCreators.toString();
  }
}
