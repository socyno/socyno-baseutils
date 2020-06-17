package com.socyno.base.bscmixutil;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class JsonUtil {
    
    private static Gson gsonDefault = new GsonBuilder().disableHtmlEscaping().create();
    private static Gson gsonSeNulls = new GsonBuilder().disableHtmlEscaping().serializeNulls().create();
    private static Gson gsonPretty = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
    private static Gson gsonPrettyNulls = new GsonBuilder().disableHtmlEscaping().serializeNulls().setPrettyPrinting()
            .create();
    private static Gson gsonFieldNamingPolicy = new GsonBuilder().disableHtmlEscaping()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
    
    /**
     * JSON 反序列化对象
     */
    public static <T> T fromJson(String json, Type typeOfT) {
        return gsonDefault.fromJson(json, typeOfT);
    }
    
    /**
     * JSON 反序列化对象
     */
    public static <T> T fromObject(Object json, Type clazz) {
        if (json instanceof JsonElement) {
            return gsonDefault.fromJson((JsonElement) json, clazz);
        }
        return fromJson(gsonDefault.toJson(json), clazz);
    }
    
    /**
     * JSON 序列化对象
     */
    public static String toJson(Object obj) {
        return gsonDefault.toJson(obj);
    }
    
    /**
     * JSON 序列化对象
     */
    public static <T> T toJsonByFieldNamingPolicy(String json, Type typeOfT) {
        return gsonFieldNamingPolicy.fromJson(json, typeOfT);
    }
    
    /**
     * JSON 序列化对象
     */
    public static <T> T fromJsonByFieldNamingPolicy(JsonElement json, Type typeOfT) {
        return gsonFieldNamingPolicy.fromJson(json, typeOfT);
    }
    
    /**
     * JSON 序列化对象
     */
    public static String toJson(Object obj, boolean serializeNulls) {
        return serializeNulls ? gsonSeNulls.toJson(obj) : gsonDefault.toJson(obj);
    }
    
    /**
     * JSON 序列化对象
     */
    public static JsonElement toJsonElement(Object obj) {
        return gsonDefault.toJsonTree(obj);
    }
    
    /**
     * JSON 序列化对象
     */
    public static String toPrettyJson(Object obj) {
        return gsonPretty.toJson(obj);
    }
    
    /**
     * JSON 序列化对象
     */
    public static String toPrettyJson(Object obj, boolean serializeNulls) {
        return serializeNulls ? gsonPrettyNulls.toJson(obj) : gsonPretty.toJson(obj);
    }
    
    /**
     * 获取对象中指定的属性，并强制转换为 boolean 类型
     */
    public static boolean getJsBoolean(JsonObject data, String name) {
        JsonElement val = null;
        if (data == null || (val = data.get(name)) == null) {
            return false;
        }
        if (val.isJsonPrimitive() && ((JsonPrimitive) val).isBoolean()) {
            return ((JsonPrimitive) val).getAsBoolean();
        }
        return CommonUtil.parseBoolean(getJstring(data, name));
    }
    
    /**
     * 获取对象中指定的属性，并强制转换为 String 类型
     */
    public static String getJstring(JsonObject data, String name) {
        JsonElement val = null;
        if (data == null || (val = data.get(name)) == null) {
            return null;
        }
        ;
        if (!val.isJsonPrimitive() || !((JsonPrimitive) val).isString()) {
            return val.toString();
        }
        return ((JsonPrimitive) val).getAsString();
    }
    
    /**
     * 获取对象中指定的属性，并强制转换为 Long 类型
     */
    public static Long getJsLong(JsonObject data, String name) {
        JsonElement val = null;
        if (data == null || (val = data.get(name)) == null) {
            return null;
        }
        ;
        if (val.isJsonPrimitive() && ((JsonPrimitive) val).isNumber()) {
            return ((JsonPrimitive) val).getAsLong();
        }
        return null;
    }
    
    /**
     * 将Json数据中的属性名称按照Java属性命名规范转换
     */
    public static JsonElement convertByFieldNaming(JsonElement json) {
        if (json == null) {
            return null;
        }
        if (json.isJsonObject()) {
            HashMap<String, Object> data = new HashMap<String, Object>();
            for (Map.Entry<String, JsonElement> e : ((JsonObject) json).entrySet()) {
                data.put(StringUtils.applyFieldNamingPolicy(e.getKey()), convertByFieldNaming(e.getValue()));
            }
            return fromObject(data, JsonElement.class);
        } else if (json.isJsonArray()) {
            JsonArray array = new JsonArray();
            for (JsonElement e : (JsonArray) json) {
                array.add(convertByFieldNaming(e));
            }
            return array;
        } else {
            return json;
        }
    }
    
}
