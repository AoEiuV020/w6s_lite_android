package com.foreveross.atwork.infrastructure.model.app.appEnum;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class RecommendModeSerializer implements JsonSerializer<RecommendMode>,
        JsonDeserializer<RecommendMode> {
  
    @Override
    public JsonElement serialize(RecommendMode appType, Type arg1,
            JsonSerializationContext arg2) {  
        return new JsonPrimitive(appType.ordinal());
    }  
  
    @Override
    public RecommendMode deserialize(JsonElement json, Type typeOfT,
            JsonDeserializationContext context) throws JsonParseException {  
        return RecommendMode.toRecommendMode(json.getAsInt());
    }  
  
} 