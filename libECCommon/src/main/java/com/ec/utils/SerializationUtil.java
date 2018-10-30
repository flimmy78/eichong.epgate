package com.ec.utils;

import com.google.gson.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

public class SerializationUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(SerializationUtil.class);

    private static final Gson GSON = new GsonBuilder().setDateFormat("yyyy/MM/dd HH:mm:ss").create();

    public static JsonObject string2JsObj(String jsonStr) {
        return GSON.fromJson(jsonStr, JsonElement.class).getAsJsonObject();
    }

    public static String gson2String(Object object) {
        return GSON.toJson(object);
    }

    /**
     * new TypeToken<Collection<Integer>>(){}.getType();
     **/
    public static <T> T gson2Object(String jsonString, Type type) {
        try {
            return GSON.fromJson(jsonString, type);
        } catch (JsonSyntaxException e) {
            LOGGER.warn("Gson string to object error, string : {} to object type {}", jsonString, type);
            throw e;
        }
    }

    public static <T> T gson2Object(String jsonString, Class<T> clazz) {
        try {
            return GSON.fromJson(jsonString, clazz);
        } catch (JsonSyntaxException e) {
            LOGGER.warn("Gson string to object error, string : {} to object type {}", jsonString, clazz.getCanonicalName());
            throw e;
        }
    }

    public static  <T> List<T> gson2ObjectList(String jsonString, Class<T> clazz) {
        try {
            return GSON.fromJson(jsonString, new ParameterizedTypeImpl(clazz));
        } catch (JsonSyntaxException e) {
            throw e;
        }
    }

    private static class ParameterizedTypeImpl implements ParameterizedType {
        Class clazz;

        public ParameterizedTypeImpl(Class clz) {
            clazz = clz;
        }

        @Override
        public Type[] getActualTypeArguments() {
            return new Type[]{clazz};
        }

        @Override
        public Type getRawType() {
            return List.class;
        }

        @Override
        public Type getOwnerType() {
            return null;
        }
    }
}
