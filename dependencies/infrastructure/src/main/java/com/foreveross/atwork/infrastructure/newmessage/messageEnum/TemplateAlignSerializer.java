package com.foreveross.atwork.infrastructure.newmessage.messageEnum;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

/**
 * Created by reyzhang22 on 17/8/16.
 */

public class TemplateAlignSerializer implements JsonSerializer<TemplateAlign>,
        JsonDeserializer<TemplateAlign> {

    // 对象转为Json时调用,实现JsonSerializer<AppKind>接口
    @Override
    public JsonElement serialize(TemplateAlign templateAlign, Type arg1,
                                 JsonSerializationContext arg2) {
        return new JsonPrimitive(templateAlign.ordinal());
    }

    // json转为对象时调用,实现JsonDeserializer<AppKind>接口
    @Override
    public TemplateAlign deserialize(JsonElement json, Type typeOfT,
                               JsonDeserializationContext context) throws JsonParseException {
        return TemplateAlign.fromStringValue(json.getAsString());
    }
}
