package com.foreveross.atwork.infrastructure.newmessage.messageEnum;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class OperationSerializer implements JsonSerializer<Operation>,
        JsonDeserializer<Operation> {
  
    @Override
    public JsonElement serialize(Operation operation, Type arg1,
            JsonSerializationContext arg2) {  
        return new JsonPrimitive(operation.ordinal());
    }  
  
    @Override
    public Operation deserialize(JsonElement json, Type typeOfT,
            JsonDeserializationContext context) throws JsonParseException {  
        return Operation.fromStringValue(json.getAsString());
    }  
  
} 