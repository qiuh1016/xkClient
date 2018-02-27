package com.cetcme.xkclient.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by laohu on 2016/7/29.
 */
public class JsonUtils {
    private static Gson gson = new Gson();
    private static JsonUtils instance = null;

    private JsonUtils() {
    }

    /**
     * 数据接口静态方法
     * @return  返回静态对象
     */
    public static synchronized JsonUtils getInstance() {
        if (instance == null) {
            instance = new JsonUtils();
        }
        return instance;
    }

    /**
     * 泛型反射机制 解析json数据
     * @param <T>  声明泛型方法
     * @param str  json字符串
     * @param clazz  jsonbean解析方法
     * @return  json数据
     */
    public <T> T get(String str, Class<T> clazz)
    {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

        try {
            return (T) gson.fromJson(str, clazz);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static <T> T object(String json, Type type) {
        return gson.fromJson(json, type);
    }


    public static <T> T object(String json, Class<T> classOfT) {
        return gson.fromJson(json, classOfT);
    }


    public static String toJson(Object objcet) {
        return gson.toJson(objcet);
    }


    public static String toJson(Object o, Type typeOfSrc){
        return gson.toJson(o,typeOfSrc);
    }


    public static  <T> ArrayList<T> fromJsonList(String json, Class<T> cls) {
        ArrayList<T> mList = new ArrayList<T>();
        JsonArray array = new JsonParser().parse(json).getAsJsonArray();
        for(final JsonElement elem : array){
            mList.add(gson.fromJson(elem, cls));
        }
        return mList;
    }


    /**
     * 阿里巴巴fastjson转化
     * @param json
     * @param type
     * @param <T>
     * @return
     */
    public static <T> T parseObject(String json, TypeReference<T> type) {
        return JSON.parseObject(json, type);
    }

    public static <T> List<T> parseListObject(String json, Class<T> clazz) {
        return JSON.parseArray(json, clazz);
    }


    public static <T> T parseObject(String json, Class<T> clazz) {
        return JSON.parseObject(json, clazz);
    }


}
