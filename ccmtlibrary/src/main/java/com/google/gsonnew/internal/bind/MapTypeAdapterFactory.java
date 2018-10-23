/*
 * Copyright (C) 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.gsonnew.internal.bind;

import com.google.gsonnew.Gson;
import com.google.gsonnew.TypeAdapter;
import com.google.gsonnew.internal.JsonReaderInternalAccess;
import com.google.gsonnew.internal.Streams;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Adapts maps to either JSON objects or JSON arrays.
 *
 * <h3>Maps as JSON objects</h3>
 * For primitive keys or when complex map key serialization is not enabled, this
 * converts Java {@link Map Maps} to JSON Objects. This requires that map keys
 * can be serialized as strings; this is insufficient for some key types. For
 * example, consider a map whose keys are points on a grid. The default JSON
 * form encodes reasonably: <pre>   {@code
 *   Map<Point, String> original = new LinkedHashMap<Point, String>();
 *   original.put(new Point(5, 6), "a");
 *   original.put(new Point(8, 8), "b");
 *   System.out.println(gson.toJson(original, type));
 * }</pre>
 * The above code prints this JSON object:<pre>   {@code
 *   {
 *     "(5,6)": "a",
 *     "(8,8)": "b"
 *   }
 * }</pre>
 * But GSON is unable to deserialize this value because the JSON string name is
 * just the {@link Object#toString() toString()} of the map key. Attempting to
 * convert the above JSON to an object fails with a parse exception:
 * <pre>JsonParseException: Expecting object found: "(5,6)"
 *   at com.google.gson.JsonObjectDeserializationVisitor.visitFieldUsingCustomHandler
 *   at com.google.gson.ObjectNavigator.navigateClassFields
 *   ...</pre>
 *
 * <h3>Maps as JSON arrays</h3>
 * An alternative approach taken by this type adapter when it is required and
 * complex map key serialization is enabled is to encode maps as arrays of map
 * entries. Each map entry is a two element array containing a key and a value.
 * This approach is more flexible because any type can be used as the map's key;
 * not just strings. But it's also less portable because the receiver of such
 * JSON must be aware of the map entry convention.
 *
 * <p>Register this adapter when you are creating your GSON instance.
 * <pre>   {@code
 *   Gson gson = new GsonBuilder()
 *     .registerTypeAdapter(Map.class, new MapAsArrayTypeAdapter())
 *     .create();
 * }</pre>
 * This will change the structure of the JSON emitted by the code above. Now we
 * get an array. In this case the arrays elements are map entries:
 * <pre>   {@code
 *   [
 *     [
 *       {
 *         "x": 5,
 *         "y": 6
 *       },
 *       "a",
 *     ],
 *     [
 *       {
 *         "x": 8,
 *         "y": 8
 *       },
 *       "b"
 *     ]
 *   ]
 * }</pre>
 * This format will serialize and deserialize just fine as long as this adapter
 * is registered.
 */
public final class MapTypeAdapterFactory implements com.google.gsonnew.TypeAdapterFactory {
  private final com.google.gsonnew.internal.ConstructorConstructor constructorConstructor;
  private final boolean complexMapKeySerialization;

  public MapTypeAdapterFactory(com.google.gsonnew.internal.ConstructorConstructor constructorConstructor,
                               boolean complexMapKeySerialization) {
    this.constructorConstructor = constructorConstructor;
    this.complexMapKeySerialization = complexMapKeySerialization;
  }

  public <T> TypeAdapter<T> create(Gson gson, com.google.gsonnew.reflect.TypeToken<T> typeToken) {
    Type type = typeToken.getType();

    Class<? super T> rawType = typeToken.getRawType();
    if (!Map.class.isAssignableFrom(rawType)) {
      return null;
    }

    Class<?> rawTypeOfSrc = com.google.gsonnew.internal.$Gson$Types.getRawType(type);
    Type[] keyAndValueTypes = com.google.gsonnew.internal.$Gson$Types.getMapKeyAndValueTypes(type, rawTypeOfSrc);
    TypeAdapter<?> keyAdapter = getKeyAdapter(gson, keyAndValueTypes[0]);
    TypeAdapter<?> valueAdapter = gson.getAdapter(com.google.gsonnew.reflect.TypeToken.get(keyAndValueTypes[1]));
    com.google.gsonnew.internal.ObjectConstructor<T> constructor = constructorConstructor.get(typeToken);

    @SuppressWarnings({"unchecked", "rawtypes"})
    // we don't define a type parameter for the key or value types
    TypeAdapter<T> result = new Adapter(gson, keyAndValueTypes[0], keyAdapter,
        keyAndValueTypes[1], valueAdapter, constructor);
    return result;
  }

  /**
   * Returns a type adapter that writes the value as a string.
   */
  private TypeAdapter<?> getKeyAdapter(Gson context, Type keyType) {
    return (keyType == boolean.class || keyType == Boolean.class)
        ? TypeAdapters.BOOLEAN_AS_STRING
        : context.getAdapter(com.google.gsonnew.reflect.TypeToken.get(keyType));
  }

  private final class Adapter<K, V> extends TypeAdapter<Map<K, V>> {
    private final Gson context;
    private final Type keyType;
    private final Type valueType;
    private final TypeAdapter<K> keyTypeAdapter;
    private final TypeAdapter<V> valueTypeAdapter;
    private final com.google.gsonnew.internal.ObjectConstructor<? extends Map<K, V>> constructor;

    public Adapter(Gson context, Type keyType, TypeAdapter<K> keyTypeAdapter,
        Type valueType, TypeAdapter<V> valueTypeAdapter,
        com.google.gsonnew.internal.ObjectConstructor<? extends Map<K, V>> constructor) {
      this.context = context;
      this.keyType = keyType;
      this.valueType = valueType;
      this.keyTypeAdapter =
        new TypeAdapterRuntimeTypeWrapper<K>(context, keyTypeAdapter, keyType);
      this.valueTypeAdapter =
        new TypeAdapterRuntimeTypeWrapper<V>(context, valueTypeAdapter, valueType);
      this.constructor = constructor;
    }

    public Map<K, V> read(com.google.gsonnew.stream.JsonReader in) throws IOException {
      com.google.gsonnew.stream.JsonToken peek = in.peek();
      if (peek == com.google.gsonnew.stream.JsonToken.NULL) {
        in.nextNull();
        return null;
      }

      Map<K, V> map = constructor.construct();

      if (peek == com.google.gsonnew.stream.JsonToken.BEGIN_ARRAY) {
        in.beginArray();
        while (in.hasNext()) {
          in.beginArray(); // entry array
          K key = keyTypeAdapter.read(in);
          V value = valueTypeAdapter.read(in);
          V replaced = map.put(key, value);
          if (replaced != null) {
            throw new com.google.gsonnew.JsonSyntaxException("duplicate key: " + key);
          }
          in.endArray();
        }
        in.endArray();
      } else {
        in.beginObject();
        while (in.hasNext()) {
          JsonReaderInternalAccess.INSTANCE.promoteNameToValue(in);
          K key = keyTypeAdapter.read(in);
          V value = valueTypeAdapter.read(in);
          V replaced = map.put(key, value);
          if (replaced != null) {
            throw new com.google.gsonnew.JsonSyntaxException("duplicate key: " + key);
          }
        }
        in.endObject();
      }
      return map;
    }

    public void write(com.google.gsonnew.stream.JsonWriter out, Map<K, V> map) throws IOException {
      if (map == null) {
        out.nullValue();
        return;
      }

      if (!complexMapKeySerialization) {
        out.beginObject();
        for (Map.Entry<K, V> entry : map.entrySet()) {
          out.name(String.valueOf(entry.getKey()));
          valueTypeAdapter.write(out, entry.getValue());
        }
        out.endObject();
        return;
      }

      boolean hasComplexKeys = false;
      List<com.google.gsonnew.JsonElement> keys = new ArrayList<com.google.gsonnew.JsonElement>(map.size());

      List<V> values = new ArrayList<V>(map.size());
      for (Map.Entry<K, V> entry : map.entrySet()) {
        com.google.gsonnew.JsonElement keyElement = keyTypeAdapter.toJsonTree(entry.getKey());
        keys.add(keyElement);
        values.add(entry.getValue());
        hasComplexKeys |= keyElement.isJsonArray() || keyElement.isJsonObject();
      }

      if (hasComplexKeys) {
        out.beginArray();
        for (int i = 0; i < keys.size(); i++) {
          out.beginArray(); // entry array
          Streams.write(keys.get(i), out);
          valueTypeAdapter.write(out, values.get(i));
          out.endArray();
        }
        out.endArray();
      } else {
        out.beginObject();
        for (int i = 0; i < keys.size(); i++) {
          com.google.gsonnew.JsonElement keyElement = keys.get(i);
          out.name(keyToString(keyElement));
          valueTypeAdapter.write(out, values.get(i));
        }
        out.endObject();
      }
    }

    private String keyToString(com.google.gsonnew.JsonElement keyElement) {
      if (keyElement.isJsonPrimitive()) {
        com.google.gsonnew.JsonPrimitive primitive = keyElement.getAsJsonPrimitive();
        if (primitive.isNumber()) {
          return String.valueOf(primitive.getAsNumber());
        } else if (primitive.isBoolean()) {
          return Boolean.toString(primitive.getAsBoolean());
        } else if (primitive.isString()) {
          return primitive.getAsString();
        } else {
          throw new AssertionError();
        }
      } else if (keyElement.isJsonNull()) {
        return "null";
      } else {
        throw new AssertionError();
      }
    }
  }
}