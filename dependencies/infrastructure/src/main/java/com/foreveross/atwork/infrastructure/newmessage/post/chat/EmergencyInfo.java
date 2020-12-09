package com.foreveross.atwork.infrastructure.newmessage.post.chat;

import androidx.annotation.Nullable;

import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by dasunsy on 2017/11/23.
 */

public class EmergencyInfo implements Serializable {
    /**
     * 是否是紧急消息:)
     */
    @Expose
    public boolean mEmergency = true;

    /**
     * 紧急消息推送的频率
     */
    @Expose
    public int mRepeatPerSeconds;

    @Expose
    public String mPlanId = StringUtils.EMPTY;


    /**
     * 是否已经确认过
     */
    @Expose
    public boolean mConfirmed = false;

    @Nullable
    public static EmergencyInfo parse(Map<String, Object> bodyMap) {
        if (bodyMap.containsKey(ChatPostMessage.EMERGENCY)) {
            EmergencyInfo emergencyInfo = EmergencyInfo.newInstance()
                    .setEmergency(ChatPostMessage.getBoolean(bodyMap, ChatPostMessage.EMERGENCY))
                    .setPlanId(ChatPostMessage.getString(bodyMap, ChatPostMessage.PLAN_ID))
                    .setRepeatPerSeconds(ChatPostMessage.getInt(bodyMap, ChatPostMessage.REPEAT_PER_SECONDS));

            return emergencyInfo;
        }

        return null;
    }

    public static EmergencyInfo newInstance() {
        return new EmergencyInfo();
    }

    public EmergencyInfo setEmergency(boolean emergency) {
        mEmergency = emergency;
        return this;
    }

    public EmergencyInfo setRepeatPerSeconds(int repeatPerSeconds) {
        mRepeatPerSeconds = repeatPerSeconds;
        return this;
    }

    public EmergencyInfo setPlanId(String planId) {
        mPlanId = planId;
        return this;
    }
}
