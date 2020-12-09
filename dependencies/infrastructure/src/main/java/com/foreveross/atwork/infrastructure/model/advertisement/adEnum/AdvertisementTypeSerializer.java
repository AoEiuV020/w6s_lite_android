package com.foreveross.atwork.infrastructure.model.advertisement.adEnum;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class AdvertisementTypeSerializer implements JsonSerializer<AdvertisementType>,
        JsonDeserializer<AdvertisementType> {
  
    // 对象转为Json时调用,实现JsonSerializer<AdvertisementType>接口
    @Override  
    public JsonElement serialize(AdvertisementType advertisementType, Type arg1,
            JsonSerializationContext arg2) {  
        return new JsonPrimitive(advertisementType.ordinal());
    }  
  
    // json转为对象时调用,实现JsonDeserializer<AdvertisementType>接口
    @Override  
    public AdvertisementType deserialize(JsonElement json, Type typeOfT,
            JsonDeserializationContext context) throws JsonParseException {  
        return AdvertisementType.fromString(json.getAsString());
    }  
  
} 