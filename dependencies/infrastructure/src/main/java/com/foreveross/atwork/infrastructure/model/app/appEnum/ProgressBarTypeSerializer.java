package com.foreveross.atwork.infrastructure.model.app.appEnum;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class ProgressBarTypeSerializer implements JsonSerializer<ProgressBarType>,
        JsonDeserializer<ProgressBarType> {
  
    @Override
    public JsonElement serialize(ProgressBarType progressBarType, Type arg1,
                                 JsonSerializationContext arg2) {
        return new JsonPrimitive(progressBarType.ordinal());
    }  
  
    @Override
    public ProgressBarType deserialize(JsonElement json, Type typeOfT,
            JsonDeserializationContext context) throws JsonParseException {  
        return ProgressBarType.valueOf(json.getAsString());
    }  
  
} 