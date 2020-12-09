package com.foreveross.atwork.infrastructure.model.app.appEnum;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class BundleTypeSerializer implements JsonSerializer<BundleType>,
        JsonDeserializer<BundleType> {
  
    @Override
    public JsonElement serialize(BundleType bundleType, Type arg1,
            JsonSerializationContext arg2) {  
        return new JsonPrimitive(bundleType.ordinal());
    }  
  
    @Override
    public BundleType deserialize(JsonElement json, Type typeOfT,
            JsonDeserializationContext context) throws JsonParseException {  
        return BundleType.toBundleType(json.getAsString());
    }  
  
} 