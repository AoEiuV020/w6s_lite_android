package com.foreveross.atwork.infrastructure.model.advertisement.adEnum;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

/**
 * Created by reyzhang22 on 17/9/16.
 */

public class AdvertisementOpsTypeSerializer implements JsonSerializer<AdvertisementOpsType>,
        JsonDeserializer<AdvertisementOpsType> {
    // 对象转为Json时调用,实现JsonSerializer<advertisementOpsType>接口
    @Override
    public JsonElement serialize(AdvertisementOpsType advertisementOpsType, Type arg1,
                                 JsonSerializationContext arg2) {
        return new JsonPrimitive(advertisementOpsType.ordinal());
    }

    // json转为对象时调用,实现JsonDeserializer<advertisementOpsType>接口
    @Override
    public AdvertisementOpsType deserialize(JsonElement json, Type typeOfT,
                                         JsonDeserializationContext context) throws JsonParseException {
        return AdvertisementOpsType.fromString(json.getAsString());
    }
}
