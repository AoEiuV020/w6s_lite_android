package com.foreveross.atwork.infrastructure.utils;

import com.foreveross.atwork.infrastructure.newmessage.post.chat.TextChatMessage;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

/**
 * Created by dasunsy on 16/9/20.
 */
public class AtUserTypeAdapter implements JsonSerializer<TextChatMessage.AtUser>, JsonDeserializer<TextChatMessage.AtUser> {

    @Override
    public JsonElement serialize(TextChatMessage.AtUser src, Type typeOfSrc, JsonSerializationContext context) {

        JsonObject jsonObject = new JsonObject();
        jsonObject.add("user_id", new JsonPrimitive(src.mUserId));
        jsonObject.add("name", new JsonPrimitive(src.mName));

        return jsonObject;
    }

    @Override
    public TextChatMessage.AtUser deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        String userId = jsonObject.get("user_id").getAsString();
        String name = jsonObject.get("name").getAsString();

        TextChatMessage.AtUser atUser = new TextChatMessage.AtUser(userId, name);
        return atUser;
    }
}
