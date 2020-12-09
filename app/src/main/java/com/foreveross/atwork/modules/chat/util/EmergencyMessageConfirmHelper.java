package com.foreveross.atwork.modules.chat.util;

import android.annotation.SuppressLint;
import android.os.AsyncTask;

import com.foreverht.cache.MessageCache;
import com.foreverht.db.service.repository.EmergencyMessageUnconfirmedRepository;
import com.foreverht.db.service.repository.MessageRepository;
import com.foreverht.db.service.repository.SessionRepository;
import com.foreverht.threadGear.AsyncTaskThreadPool;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.Session;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.TextChatMessage;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.modules.chat.dao.ChatDaoService;
import com.foreveross.atwork.modules.chat.data.ChatSessionDataWrap;

import java.util.List;

/**
 * Created by dasunsy on 2017/11/24.
 */

public class EmergencyMessageConfirmHelper {


    /**
     * 更新紧急呼叫消息确认状态并且同步至数据库
     * */
    @SuppressLint("StaticFieldLeak")
    public static void updateConfirmResultAndUpdateDbSync(String sourceId, String msgId) {
        ChatPostMessage msgCache = MessageCache.getInstance().findMessage(sourceId, msgId);
        if(null != msgCache) {
            msgCache.setEmergencyConfirm();
        }

        ChatPostMessage msgDb = MessageRepository.getInstance().queryMessage(BaseApplicationLike.baseContext, sourceId, msgId);
        if(null != msgDb) {
            msgDb.setEmergencyConfirm();
            MessageRepository.getInstance().updateMessage(msgDb);
        }

        EmergencyMessageUnconfirmedRepository.getInstance().deleteEmergencyMessages(ListUtil.makeSingleList(msgId));

        checkSessionUpdateSync(sourceId);

        ChatDetailExposeBroadcastSender.confirmEmergencyMessage(msgId);
        ChatDetailExposeBroadcastSender.refreshMessageListViewUI();

    }

    @SuppressLint("StaticFieldLeak")
    private static void checkSessionUpdate(String sourceId) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                checkSessionUpdateSync(sourceId);

                return null;
            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }

    private static void checkSessionUpdateSync(String sourceId) {
        Session session = ChatSessionDataWrap.getInstance().getSession(sourceId, null);
        if(null != session && Session.ShowType.Emergency == session.lastMessageShowType) {
            if(EmergencyMessageUnconfirmedRepository.getInstance().hasEmergencyDirtyCall(sourceId)) {
                return;
            }

            session.lastMessageShowType = Session.ShowType.Text;

            SessionRepository.getInstance().updateSession(session);
            SessionRefreshHelper.notifyRefreshSessionAndCount();
        }
    }

    /**
     * 更新紧急呼叫消息确认状态并且同步至数据库
     * */
    public static void updateConfirmResultAndUpdateDb(TextChatMessage textChatMessage) {

        textChatMessage.setEmergencyConfirm();

        ChatDaoService.getInstance().insertOrUpdateMessage(BaseApplicationLike.baseContext, textChatMessage);

        ChatDaoService.getInstance().deleteEmergencyMessages(ListUtil.makeSingleList(textChatMessage.deliveryId));

        checkSessionUpdate(textChatMessage.from);

        ChatDetailExposeBroadcastSender.confirmEmergencyMessage(textChatMessage.deliveryId);
        ChatDetailExposeBroadcastSender.refreshMessageListViewUI();

    }


    @SuppressLint("StaticFieldLeak")
    public static void queryUnConfirmedList(String sessionId, OnQueryUnConfirmedCountListener listener) {
        new AsyncTask<Void, Void, List<ChatPostMessage>>() {
            @Override
            protected List<ChatPostMessage> doInBackground(Void... voids) {

                return EmergencyMessageUnconfirmedRepository.getInstance().queryUnconfirmedEmergencyMsgList(BaseApplicationLike.baseContext, sessionId);
            }

            @Override
            protected void onPostExecute(List<ChatPostMessage> result) {
                listener.result(result);
            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }

    public interface OnQueryUnConfirmedCountListener {
        void result(List<ChatPostMessage> chatPostMessageList);
    }
}
