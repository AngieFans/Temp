/*
 * Copyright (C) 2008 Google Inc.
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

import com.google.gsonnew.Gson;
import com.google.gsonnew.TypeAdapter;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class selects which fields and types to omit. It is configurable,
 * supporting version attributes {@link com.google.gsonnew.annotations.Since} and {@link com.google.gsonnew.annotations.Until}, modifiers,
 * synthetic fields, anonymous and local classes, inner classes, and fields with
 * the {@link com.google.gsonnew.annotations.Expose} annotation.
 *
 * <p>This class is a type adapter factory; types that are excluded will be
 * adapted to null. It may delegate to another type adapter if only one
 * direction is excluded.
 *
 * @author Joel Leitch
 * @author Jesse Wilson
 */
public final class Excluder implements com.google.gsonnew.TypeAdapterFactory, Cloneable {
  private static final double IGNORE_VERSIONS = -1.0d;
  public static final Excluder DEFAULT = new Excluder();

  private double version = IGNORE_VERSIONS;
  private int modifiers = Modifier.TRANSIENT | Modifier.STATIC;
  private boolean serializeInnerClasses = true;
  private boolean requireExpose;
  private List<com.google.gsonnew.ExclusionStrategy> serializationStrategies = Collections.emptyList();
  private List<com.google.gsonnew.ExclusionStrategy> deserializationStrategies = Collections.emptyList();

  @Override protected Excluder clone() {
    try {
      return (Excluder) super.clone();
    } catch (CloneNotSupportedException e) {
      throw new AssertionError();
    }
  }

  public Excluder withVersion(double ignoreVersionsAfter) {
    Excluder result = clone();
    result.version = ignoreVersionsAfter;
    return result;
  }

  public Excluder withModifiers(int... modifiers) {
    Excluder result = clone();
    result.modifiers = 0;
    for (int modifier : modifiers) {
      result.modifiers |= modifier;
    }
    return result;
  }

  public Excluder disableInnerClassSerialization() {
    Excluder result = clone();
    result.serializeInnerClasses = false;
    return result;
  }

  public Excluder excludeFieldsWithoutExposeAnnotation() {
    Excluder result = clone();
    result.requireExpose = true;
    return result;
  }

  public Excluder withExclusionStrategy(com.google.gsonnew.ExclusionStrategy exclusionStrategy,
                                        boolean serialization, boolean deserialization) {
    Excluder result = clone();
    if (serialization) {
      result.serializationStrategies = new ArrayList<com.google.gsonnew.ExclusionStrategy>(serializationStrategies);
      result.serializationStrategies.add(exclusionStrategy);
    }
    if (deserialization) {
      result.deserializationStrategies
          = new ArrayList<com.google.gsonnew.ExclusionStrategy>(deserializationStrategies);
      result.deserializationStrategies.add(exclusionStrategy);
    }
    return result;
  }

  public <T> TypeAdapter<T> create(final Gson gson, final com.google.gsonnew.reflect.TypeToken<T> type) {
    Class<?> rawType = type.getRawType();
    final boolean skipSerialize = excludeClass(rawType, true);
    final boolean skipDeserialize = excludeClass(rawType, false);

    if (!skipSerialize && !skipDeserialize) {
      return null;
    }

    return new TypeAdapter<T>() {
      /** The delegate is lazily created because it may not be needed, and creating it may fail. */
      private TypeAdapter<T> delegate;

      @Override public T read(com.google.gsonnew.stream.JsonReader in) throws IOException {
        if (skipDeserialize) {
          in.skipValue();
          return null;
        }
        return delegate().read(in);
      }

      @Override public void write(com.google.gsonnew.stream.JsonWriter out, T value) throws IOException {
        if (skipSerialize) {
          out.nullValue();
          return;
        }
        delegate().write(out, value);
      }

      private TypeAdapter<T> delegate() {
        TypeAdapter<T> d = delegate;
        return d != null
            ? d
            : (delegate = gson.getDelegateAdapter(Excluder.this, type));
      }
    };
  }

  public boolean excludeField(Field field, boolean serialize) {
    if ((modifiers & field.getModifiers()) != 0) {
      return true;
    }

    if (version != Excluder.IGNORE_VERSIONS
        && !isValidVersion(field.getAnnotation(com.google.gsonnew.annotations.Since.class), field.getAnnotation(com.google.gsonnew.annotations.Until.class))) {
      return true;
    }

    if (field.isSynthetic()) {
      return true;
    }

    if (requireExpose) {
      com.google.gsonnew.annotations.Expose annotation = field.getAnnotation(com.google.gsonnew.annotations.Expose.class);
      if (annotation == null || (serialize ? !annotation.serialize() : !annotation.deserialize())) {
        return true;
      }
    }

    if (!serializeInnerClasses && isInnerClass(field.getType())) {
      return true;
    }

    if (isAnonymousOrLocal(field.getType())) {
      return true;
    }

    List<com.google.gsonnew.ExclusionStrategy> list = serialize ? serializationStrategies : deserializationStrategies;
    if (!list.isEmpty()) {
      com.google.gsonnew.FieldAttributes fieldAttributes = new com.google.gsonnew.FieldAttributes(field);
      for (com.google.gsonnew.ExclusionStrategy exclusionStrategy : list) {
        if (exclusionStrategy.shouldSkipField(fieldAttributes)) {
          return true;
        }
      }
    }

    return false;
  }

  public boolean excludeClass(Class<?> clazz, boolean serialize) {
    if (version != Excluder.IGNORE_VERSIONS
        && !isValidVersion(clazz.getAnnotation(com.google.gsonnew.annotations.Since.class), clazz.getAnnotation(com.google.gsonnew.annotations.Until.class))) {
      return true;
    }

    if (!serializeInnerClasses && isInnerClass(clazz)) {
      return true;
    }

    if (isAnonymousOrLocal(clazz)) {
      return true;
    }

    List<com.google.gsonnew.ExclusionStrategy> list = serialize ? serializationStrategies : deserializationStrategies;
    for (com.google.gsonnew.ExclusionStrategy exclusionStrategy : list) {
      if (exclusionStrategy.shouldSkipClass(clazz)) {
        return true;
      }
    }

    return false;
  }

  private boolean isAnonymousOrLocal(Class<?> clazz) {
    return !Enum.class.isAssignableFrom(clazz)
        && (clazz.isAnonymousClass() || clazz.isLocalClass());
  }

  private boolean isInnerClass(Class<?> clazz) {
    return clazz.isMemberClass() && !isStatic(clazz);
  }

  private boolean isStatic(Class<?> clazz) {
    return (clazz.getModifiers() & Modifier.STATIC) != 0;
  }

  private boolean isValidVersion(com.google.gsonnew.annotations.Since since, com.google.gsonnew.annotations.Until until) {
    return isValidSince(since) && isValidUntil(until);
  }

  private boolean isValidSince(com.google.gsonnew.annotations.Since annotation) {
    if (annotation != null) {
      double annotationVersion = annotation.value();
      if (annotationVersion > version) {
        return false;
      }
    }
    return true;
  }

  private boolean isValidUntil(com.google.gsonnew.annotations.Until annotation) {
    if (annotation != null) {
      double annotationVersion = annotation.value();
      if (annotationVersion <= version) {
        return false;
      }
    }
    return true;
  }
}
