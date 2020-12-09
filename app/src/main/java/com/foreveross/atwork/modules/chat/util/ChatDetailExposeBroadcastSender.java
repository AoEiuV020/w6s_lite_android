package com.foreveross.atwork.modules.chat.util;

import android.content.Context;
import android.content.Intent;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.Session;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.modules.chat.activity.ChatDetailActivity;
import com.foreveross.atwork.modules.chat.fragment.ChatDetailFragment;
import com.foreveross.atwork.modules.chat.fragment.MultiPartDetailFragment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dasunsy on 2017/8/24.
 */

public class ChatDetailExposeBroadcastSender {
    /**
     * 更改 User 的通知
     */
    public static void changeUser(Context context, User user) {
        Intent intent = new Intent(ChatDetailFragment.USER_CHANGED);
        intent.putExtra(ChatDetailFragment.DATA_USER, user);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

    }

    public static void changeSession(Context context, Session session) {
        Intent intent = new Intent(ChatDetailFragment.SESSION_CHANGED);
        intent.putExtra(ChatDetailFragment.DATA_SESSION, session);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    public static void finishView(Context context, String sessionId) {
        Intent intent = new Intent(ChatDetailFragment.ACTION_FINISH);
        intent.putExtra(ChatDetailActivity.IDENTIFIER, sessionId);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    public static void doNotCheckSession(Context context, String sessionId) {
        Intent intent = new Intent(ChatDetailFragment.ACTION_DO_NOT_CHECK_SESSION);
        intent.putExtra(ChatDetailActivity.IDENTIFIER, sessionId);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    public static void refreshMessageListViewUI() {
        LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).sendBroadcast(new Intent(ChatDetailFragment.REFRESH_MESSAGE_LIST));

    }

    public static void refreshMultipartMessageViewUI() {
        LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).sendBroadcast(new Intent(MultiPartDetailFragment.REFRESH_MULTIPART_MESSAGE_LIST));

    }

    public static void clearMessageList() {
        LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).sendBroadcast(new Intent(ChatDetailFragment.ACTION_CLEAR_MESSAGE_LIST));

    }

    public static void clearAtContacts() {
        LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).sendBroadcast(new Intent(ChatDetailFragment.ACTION_CLEAR_AT_DATA));

    }

    public static void burnMessage(ChatPostMessage message) {
        List<ChatPostMessage> singleList = new ArrayList<>();
        singleList.add(message);
        burnMessages(singleList);
    }

    public static void burnMessages(List<ChatPostMessage> messageList) {
        Intent intent = new Intent(ChatDetailFragment.DELETE_MESSAGES);
        intent.putExtra(ChatDetailFragment.INTENT_BATCH_MESSAGES, (Serializable) messageList);
        LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).sendBroadcast(intent);
    }

    public static void confirmEmergencyMessage(String msgId) {
        Intent intent = new Intent(ChatDetailFragment.ACTION_EMERGENCY_MESSAGE_CONFIRMED);
        intent.putExtra(ChatDetailFragment.DATA_MSG_ID, msgId);
        LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).sendBroadcast(intent);

    }

    //是否显示底层和popupWindow之间的半透明层
    public static void transparentViewMessage(String msgId,int type) {
        Intent intent = new Intent(ChatDetailFragment.TRANSPARENT_POP);
        intent.putExtra(ChatDetailFragment.TRANSPARENT_POP_DATA, msgId);
        intent.putExtra(ChatDetailFragment.TRANSPARENT_POP_TYPE, type);
        LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).sendBroadcast(intent);

    }
}
