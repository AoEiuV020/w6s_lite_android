package com.foreveross.atwork.infrastructure.model.app.appEnum;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class DisplayModeSerializer implements JsonSerializer<DisplayMode>,
        JsonDeserializer<DisplayMode> {
  
    @Override
    public JsonElement serialize(DisplayMode displayMode, Type arg1,
                                 JsonSerializationContext arg2) {
        return new JsonPrimitive(displayMode.ordinal());
    }  
  
    @Override
    public DisplayMode deserialize(JsonElement json, Type typeOfT,
            JsonDeserializationContext context) throws JsonParseException {  
        return DisplayMode.fromStringValue(json.getAsString());
    }  
  
} 