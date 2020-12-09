package com.foreveross.atwork.infrastructure.newmessage.post.notify.user;

import androidx.annotation.Nullable;

import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class UserSettings {

    public final static String USER_SETTINGS = "user_settings";
    public final static String MODIFY_TIME = "modify_time";

    @SerializedName("chat_settings")
    public ChatSettings chatSettings;

    @SerializedName("modify_time")
    public long modifyTime;


    @Nullable
    public static UserSettings parseInfo(Map<String, Object> bodyMap) {

        UserSettings userSettings = new UserSettings();

        if(bodyMap.containsKey(USER_SETTINGS)) {
            Map<String, Object> userSettingMap = (Map<String, Object>) bodyMap.get(USER_SETTINGS);
            userSettings.chatSettings = ChatSettings.pareInfo(userSettingMap);
            userSettings.modifyTime = ChatPostMessage.getLong(userSettingMap, MODIFY_TIME);

        }


        return userSettings;
    }
}
