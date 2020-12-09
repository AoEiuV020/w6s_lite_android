package com.foreveross.atwork.api.sdk.message.model;

import androidx.annotation.Nullable;

import com.foreveross.atwork.api.sdk.net.HttpResult;
import com.foreveross.atwork.infrastructure.newmessage.PostTypeMessage;
import com.foreveross.atwork.infrastructure.newmessage.ReceiptMessage;
import com.foreveross.atwork.infrastructure.utils.ListUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dasunsy on 2017/10/13.
 */

public class MessagesResult {
    /**
     * 消息列表
     */
    public List<PostTypeMessage> mPostTypeMessages = new ArrayList<>();

    /**
     * 回执列表
     */
    public Map<String, String> mReceipts = new HashMap<>();

    public List<ReceiptMessage> mReceiptMessages = new ArrayList<>();

    public boolean mSuccess;

    public int mRealOfflineMsgSize;

    public HttpResult mHttpResult;

    public String mSessionIdPullingMessages;


    @Nullable
    public PostTypeMessage getLastMessage() {
        PostTypeMessage lastPostTypeMessage = null;
        if (!ListUtil.isEmpty(mPostTypeMessages)) {
            lastPostTypeMessage = mPostTypeMessages.get(mPostTypeMessages.size() - 1);
        }

        return lastPostTypeMessage;
    }

    @Nullable
    public PostTypeMessage getFirstMessage() {
        PostTypeMessage firstPostTypeMessage = null;
        if (!ListUtil.isEmpty(mPostTypeMessages)) {
            firstPostTypeMessage = mPostTypeMessages.get(0);
        }

        return firstPostTypeMessage;
    }

}
