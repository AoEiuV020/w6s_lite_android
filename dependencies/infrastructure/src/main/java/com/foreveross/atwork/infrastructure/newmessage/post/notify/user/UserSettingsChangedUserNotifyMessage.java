package com.foreveross.atwork.infrastructure.newmessage.post.notify.user;

import java.util.Map;

public class UserSettingsChangedUserNotifyMessage extends UserNotifyMessage {

    public UserSettings userSettings;

    public static UserSettingsChangedUserNotifyMessage getUserSettingChangedUserNotifyMessageFromJson(Map<String, Object> jsonMap) {

        UserSettingsChangedUserNotifyMessage notifyMessage = new UserSettingsChangedUserNotifyMessage();
        notifyMessage.pareInfo(jsonMap);

        return notifyMessage;
    }


    protected void pareInfo(Map<String, Object> jsonMap) {
        this.initPostTypeMessageValue(jsonMap);
        Map<String, Object> bodyMap = (Map<String, Object>) jsonMap.get(BODY);
        userSettings = UserSettings.parseInfo(bodyMap);

    }
}
