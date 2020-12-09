package com.foreveross.atwork.infrastructure.model.app.appEnum;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class BannerTypeSerializer implements JsonSerializer<BannerType>,
        JsonDeserializer<BannerType> {
  
    @Override
    public JsonElement serialize(BannerType bannerType, Type arg1,
                                 JsonSerializationContext arg2) {
        return new JsonPrimitive(bannerType.ordinal());
    }  
  
    @Override
    public BannerType deserialize(JsonElement json, Type typeOfT,
            JsonDeserializationContext context) throws JsonParseException {  
        return BannerType.valueOf(json.getAsString());
    }  
  
} 