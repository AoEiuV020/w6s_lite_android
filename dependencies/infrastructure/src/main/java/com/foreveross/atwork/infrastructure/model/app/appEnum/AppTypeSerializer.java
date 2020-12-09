package com.foreveross.atwork.infrastructure.model.app.appEnum;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class AppTypeSerializer implements JsonSerializer<AppType>,
        JsonDeserializer<AppType> {
  
    // 对象转为Json时调用,实现JsonSerializer<AppKind>接口
    @Override  
    public JsonElement serialize(AppType appType, Type arg1,
            JsonSerializationContext arg2) {  
        return new JsonPrimitive(appType.ordinal());
    }  
  
    // json转为对象时调用,实现JsonDeserializer<AppKind>接口
    @Override  
    public AppType deserialize(JsonElement json, Type typeOfT,
            JsonDeserializationContext context) throws JsonParseException {  
        return AppType.toAppType(json.getAsInt());
    }  
  
} 