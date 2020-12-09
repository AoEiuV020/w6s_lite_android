package com.foreveross.atwork.infrastructure.model.app.appEnum;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class DistributeModeSerializer implements JsonSerializer<DistributeMode>,
        JsonDeserializer<DistributeMode> {
  
    @Override
    public JsonElement serialize(DistributeMode displayMode, Type arg1,
            JsonSerializationContext arg2) {  
        return new JsonPrimitive(displayMode.ordinal());
    }  
  
    @Override
    public DistributeMode deserialize(JsonElement json, Type typeOfT,
            JsonDeserializationContext context) throws JsonParseException {  
        return DistributeMode.getDisplayMode(json.getAsInt());
    }  
  
} 