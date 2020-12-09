package com.foreveross.atwork.infrastructure.newmessage.post.chat;

import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dasunsy on 2017/4/27.
 */

public class BurnInfo implements Serializable, Cloneable {

    @Expose
    @SerializedName("read_time")
    public long mReadTime = 0;

    public static BurnInfo parseFromMap(Map<String, Object> bodyMap) {
        BurnInfo burnInfo = new BurnInfo();
        if (bodyMap.containsKey(ChatPostMessage.READ_TIME)) {
            burnInfo.mReadTime = ((Double) bodyMap.get(ChatPostMessage.READ_TIME)).longValue();
        }

        return burnInfo;
    }


    public Map<String, Object> getChatMapBody() {
        Map<String, Object> bodyMap = new HashMap<>();
        bodyMap.put(ChatPostMessage.READ_TIME, mReadTime);
        return bodyMap;
    }
}
