package com.foreveross.atwork.modules.chat.dao;

import android.annotation.SuppressLint;
import android.os.AsyncTask;

import com.foreverht.db.service.BaseDbService;
import com.foreverht.db.service.repository.ReceiptRepository;
import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.discussion.DiscussionReadUnRead;
import com.foreveross.atwork.infrastructure.newmessage.ReceiptMessage;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.shared.ReadAckPersonShareInfo;

import java.util.List;
import java.util.UUID;

/**
 * Created by lingen on 15/5/14.
 * Description:
 */
public class ReceiptDaoService extends BaseDbService{

    private ReceiptDaoService() {

    }

    private static ReceiptDaoService receiptDaoService = new ReceiptDaoService();

    public static ReceiptDaoService getInstance() {
        return receiptDaoService;
    }

    public void queryIsMsgRead(final String sessionIdentifier, final String msgId, final QueryP2PReceiptIsReadListener queryP2PReceiptIsReadListener) {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                return ReceiptRepository.getInstance().isP2PMessageRead(sessionIdentifier, msgId);
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                queryP2PReceiptIsReadListener.isRead(aBoolean);
            }

        }.executeOnExecutor(mDbExecutor);
    }

    /**
     * 单聊中查询已读回执信息
     *
     * @param sessionIdentifier
     * @param msgId
     * @param queryP2PReceiptListener
     */
    @SuppressLint("StaticFieldLeak")
    public void queryP2PReceipt(final String sessionIdentifier, final String msgId, long msgTime, final QueryP2PReceiptListener queryP2PReceiptListener) {

        ReadAckPersonShareInfo.ReadAckInfo readAckInfo = ReadAckPersonShareInfo.INSTANCE.getReadAckInfo(AtworkApplicationLike.baseContext, sessionIdentifier);
        if(null != readAckInfo && readAckInfo.getTargetReadTime() >= msgTime) {
            ReceiptMessage receiptMessage = new ReceiptMessage();
            receiptMessage.identifier = UUID.randomUUID().toString();
            queryP2PReceiptListener.queryP2PReceiptSuccess(receiptMessage);
            return;
        }


        new AsyncTask<Void, Void, ReceiptMessage>() {
            @Override
            protected ReceiptMessage doInBackground(Void... params) {
                ReceiptMessage receiptMessage = ReceiptRepository.getInstance().queryP2PReceiptMessage(sessionIdentifier, msgId);
                return receiptMessage;
            }

            @Override
            protected void onPostExecute(ReceiptMessage receiptMessage) {
                queryP2PReceiptListener.queryP2PReceiptSuccess(receiptMessage);
            }
        }.executeOnExecutor(mDbExecutor);
    }

    /**
     * 群聊中，查询某个消息的已读回执数量
     *
     * @param sessionIdentifier
     * @param msgId
     * @param queryGroupReceiptCoutnListener
     */
    public void queryGroupReceiptCount(final String sessionIdentifier, final String msgId, final QueryGroupReceiptCountListener queryGroupReceiptCoutnListener) {
        new AsyncTask<Void, Void, DiscussionReadUnRead>() {
            @Override
            protected DiscussionReadUnRead doInBackground(Void... params) {
                String me = LoginUserInfo.getInstance().getLoginUserId(BaseApplicationLike.baseContext);
                return ReceiptRepository.getInstance().queryDiscussionReadUnRead(me,sessionIdentifier, msgId);
            }

            @Override
            protected void onPostExecute(DiscussionReadUnRead discussionReadUnRead) {
                queryGroupReceiptCoutnListener.queryGroupReceiptCountSuccess(discussionReadUnRead);
            }
        }.executeOnExecutor(mDbExecutor);
    }

    /**
     * 群聊中，查询已读回执信息
     *
     * @param sessionIdentifier
     * @param msgId
     * @param queryGroupReceiptListener
     */
    public void queryGroupReceipt(final String sessionIdentifier, final String msgId, final QueryGroupReceiptListener queryGroupReceiptListener) {
        new AsyncTask<Void, Void, List<ReceiptMessage>>() {
            @Override
            protected List<ReceiptMessage> doInBackground(Void... params) {
                return ReceiptRepository.getInstance().queryGroupReceiptMessages(sessionIdentifier, msgId, null);
            }

            @Override
            protected void onPostExecute(List<ReceiptMessage> receiptMessages) {
                queryGroupReceiptListener.queryGroupReceiptSuccess(receiptMessages);
            }
        }.executeOnExecutor(mDbExecutor);
    }

    /**
     * 查询 单聊中查询指定MSGID是否已读
     */
    public interface QueryP2PReceiptIsReadListener {
        void isRead(boolean read);
    }

    /**
     * 查询 单聊中已读回执接口
     */
    public interface QueryP2PReceiptListener {
        void queryP2PReceiptSuccess(ReceiptMessage receiptMessage);
    }

    /**
     * 查询 群组已读回执数量
     */
    public interface QueryGroupReceiptCountListener {
        void queryGroupReceiptCountSuccess(DiscussionReadUnRead discussionReadUnRead);
    }

    /**
     * 查询 群组已读回执信息
     */
    public interface QueryGroupReceiptListener {

        void queryGroupReceiptSuccess(List<ReceiptMessage> receiptMessageLis);

    }
}
