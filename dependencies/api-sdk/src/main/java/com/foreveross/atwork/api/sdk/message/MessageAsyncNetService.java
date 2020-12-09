package com.foreveross.atwork.api.sdk.message;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import androidx.annotation.Nullable;
import android.text.TextUtils;

import com.foreverht.threadGear.AsyncTaskThreadPool;
import com.foreverht.threadGear.HighPriorityCachedTreadPoolExecutor;
import com.foreverht.threadGear.OfflineMessagesSessionStrategyThreadPool;
import com.foreveross.atwork.api.sdk.BaseCallBackNetWorkListener;
import com.foreveross.atwork.api.sdk.NetWorkFailListener;
import com.foreveross.atwork.api.sdk.UrlConstantManager;
import com.foreveross.atwork.api.sdk.message.model.EmergencyMessageConfirmRequest;
import com.foreveross.atwork.api.sdk.message.model.MessagesResult;
import com.foreveross.atwork.api.sdk.message.model.OfflineMessageResponseJson;
import com.foreveross.atwork.api.sdk.message.model.QueryMessageHistoryRequest;
import com.foreveross.atwork.api.sdk.message.model.QueryMessageHistoryResponse;
import com.foreveross.atwork.api.sdk.message.model.QueryMessageHistoryResult;
import com.foreveross.atwork.api.sdk.message.model.QueryMessageResult;
import com.foreveross.atwork.api.sdk.message.model.QueryMessageTagResponse;
import com.foreveross.atwork.api.sdk.message.model.QueryMessageTagResult;
import com.foreveross.atwork.api.sdk.message.model.QueryMessagesOnSessionRequest;
import com.foreveross.atwork.api.sdk.message.model.QuerySessionListRequest;
import com.foreveross.atwork.api.sdk.net.HttpResult;
import com.foreveross.atwork.api.sdk.net.HttpURLConnectionComponent;
import com.foreveross.atwork.api.sdk.util.NetWorkHttpResultHelper;
import com.foreveross.atwork.api.sdk.util.NetworkHttpResultErrorHandler;
import com.foreveross.atwork.infrastructure.model.user.LoginToken;
import com.foreveross.atwork.infrastructure.model.user.UserHandleBasic;
import com.foreveross.atwork.infrastructure.newmessage.PostTypeMessage;
import com.foreveross.atwork.infrastructure.newmessage.ReadStatus;
import com.foreveross.atwork.infrastructure.newmessage.ReceiptMessage;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.ParticipantType;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.NotifyPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.VoipPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ArticleChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ArticleItem;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ShareChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.VoiceChatMessage;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.CloneUtil;
import com.foreveross.atwork.infrastructure.utils.JsonUtil;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.infrastructure.utils.Logger;
import com.foreveross.atwork.infrastructure.utils.MessageCovertUtil;
import com.foreveross.atwork.infrastructure.utils.chat.BasicMsgHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.w6s.module.MessageTags;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
/**
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@%((((((((#&@@@((#((#@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@%(((((((((((((%@*((((@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@%(((/,/(((((((((@@@*#@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@#(((@@@((((((((((@@((@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@(((((((((((((((((((((@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@(((((((((((((((((((@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@(((((((((((((((*@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ __           __
 * .--.--.--.-----.----.|  |--.-----.|  |.--.--.-----.
 * |  |  |  |  _  |   _||    <|  _  ||  ||  |  |__ --|
 * |________|_____|__|  |__|__|   __||__||_____|_____|
 * |__|
 */

/**
 * Created by lingen on 15/4/25.
 * <p/>
 * 消息的网络处理服务，主要用于收取离线消息等操作
 */
public class MessageAsyncNetService {



    public static MessagesResult queryRoamingMessagesSync(Context context, String include, String exclude, String sort, long begin, long end, String participantDomain, ParticipantType participantType, String participantId, int limit) {
        LoginToken loginToken = LoginUserInfo.getInstance().getLoginToken(context);
        String url = String.format(UrlConstantManager.getInstance().V2_queryRoamingMessages(include, exclude, sort, begin, end), loginToken.mClientId, participantDomain, participantType.stringValue(),
                participantId, limit, loginToken.mAccessToken);

        HttpResult httpResult = HttpURLConnectionComponent.getInstance().getHttp(url, 1 * 60 * 1000);
        MessagesResult messagesResult = new MessagesResult();

        if (httpResult.isNetSuccess()) {
            OfflineMessageResponseJson messageResponseJson = OfflineMessageResponseJson.createInstance(httpResult.result);
            if (messageResponseJson != null && messageResponseJson.status == 0) {

                messagesResult.mSuccess = true;

                messagesResult.mRealOfflineMsgSize = messageResponseJson.result.messagesArr.length();

                messagesResult.mPostTypeMessages = messageResponseJson.toPostTypeMessage(context);
                messagesResult.mReceipts = new Gson().fromJson(messageResponseJson.result.receipts, new TypeToken<HashMap<String, String>>() {
                }.getType());


                for(PostTypeMessage postTypeMessage : messagesResult.mPostTypeMessages) {
                    handleOfflineMessageReadStatus(messagesResult.mReceiptMessages, postTypeMessage, messagesResult);

                }

            } else {
                messagesResult.mSuccess = false;

            }

        } else {
            messagesResult.mSuccess = false;
        }

        messagesResult.mHttpResult = httpResult;

        return messagesResult;
    }




    /**
     * 离线拉取消息, 分页获取消息
     *
     * @param getOfflineMessageListener
     */
    @SuppressLint("StaticFieldLeak")
    public static void getMessagesPerPage(final Context context, final GetMessagePerPageListener getOfflineMessageListener, final long begin, final String lastMsgId) {
        final LoginToken token = LoginUserInfo.getInstance().getLoginToken(context);
        final String url = String.format(UrlConstantManager.getInstance().V2_queryOfflineMessages(), token.mClientId, TextUtils.isEmpty(String.valueOf(begin)) ? "-1" : begin, AtworkConfig.COUNT_SYNC_MESSAGE_BATCH, token.mAccessToken);

        new AsyncTask<Void, Void, MessagesResult>() {
            @Override
            protected MessagesResult doInBackground(Void... params) {
                Logger.e("getOfflineMessage", "start get offline message ");
                HttpResult httpResult = null;
                if (!TextUtils.isEmpty(lastMsgId)) {
                    httpResult = HttpURLConnectionComponent.getInstance().getHttp(url + "&anchor=" + lastMsgId);

                } else {
                    httpResult = HttpURLConnectionComponent.getInstance().getHttp(url);
                }

                return produceMessagesResult(context, httpResult);
            }

            @Override
            protected void onPostExecute(MessagesResult messagesResult) {
                getOfflineMessageListener.getMessagePerPage(messagesResult, begin);
            }
        }.executeOnExecutor(HighPriorityCachedTreadPoolExecutor.getInstance());
    }

    @NotNull
    public static MessagesResult produceMessagesResult(Context context, HttpResult httpResult) {
        MessagesResult messagesResult = new MessagesResult();
        if (httpResult.isNetSuccess()) {
            OfflineMessageResponseJson offlineMessageResponseJson = OfflineMessageResponseJson.createInstance(httpResult.result);
            if (offlineMessageResponseJson != null && offlineMessageResponseJson.status == 0) {
                messagesResult.mSuccess = true;

                messagesResult.mRealOfflineMsgSize = offlineMessageResponseJson.result.messagesArr.length();

                messagesResult.mPostTypeMessages = offlineMessageResponseJson.toPostTypeMessage(context);
                messagesResult.mReceipts = new Gson().fromJson(offlineMessageResponseJson.result.receipts, new TypeToken<HashMap<String, String>>() {
                }.getType());

                for(PostTypeMessage postTypeMessage : messagesResult.mPostTypeMessages) {
                    handleOfflineMessageReadStatus(messagesResult.mReceiptMessages, postTypeMessage, messagesResult);

                }


                return messagesResult;
            } else {
                messagesResult.mSuccess = false;
            }
        } else {
            Logger.e("getOfflineMessage", "get offline message fail because " + httpResult.result);
            messagesResult.mSuccess = false;
        }

        messagesResult.mHttpResult = httpResult;

        return messagesResult;
    }




    public static void handleOfflineMessageReadStatus(@Nullable List<ReceiptMessage> receiptMessages, PostTypeMessage postTypeMessage, MessagesResult messagesResult) {
        if(BasicMsgHelper.isSender(postTypeMessage)) {
            return;
        }

        String keyId = postTypeMessage.getMsgReadDeliveryId();

        String receipt = messagesResult.mReceipts.get(keyId);
        if (!TextUtils.isEmpty(receipt) && "READ".equalsIgnoreCase(receipt)) {
            try {
                //tag read status from remote
                if (postTypeMessage instanceof ChatPostMessage) {
                    ((ChatPostMessage) postTypeMessage).read = ReadStatus.AbsolutelyRead;

                    if(postTypeMessage instanceof VoiceChatMessage) {
                        ((VoiceChatMessage)postTypeMessage).play = true;
                    }

                } else if (postTypeMessage instanceof NotifyPostMessage) {
                    ((NotifyPostMessage) postTypeMessage).read = ReadStatus.AbsolutelyRead;


                } else if(postTypeMessage instanceof VoipPostMessage) {
                    ((VoipPostMessage)postTypeMessage).read = ReadStatus.AbsolutelyRead;
                }

                UserHandleBasic chatUser = BasicMsgHelper.getChatUser(postTypeMessage);
                if (null != receiptMessages) {
                    receiptMessages.add(getReceiptMessage(keyId, postTypeMessage.from, chatUser.mUserId));
                }

                //处理已读的消息的 session
//                            handleReadMsg(postTypeMessage);


            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {
            if(postTypeMessage instanceof VoiceChatMessage) {
                ((VoiceChatMessage)postTypeMessage).play = false;
            }
        }

        if (TextUtils.isEmpty(receipt)) {
            //兼容旧的表结构, 空的 receipt 视为已读
            if (postTypeMessage instanceof ChatPostMessage) {
                ((ChatPostMessage) postTypeMessage).read = ReadStatus.AbsolutelyRead;
            }
        }
    }

    private static ReceiptMessage getReceiptMessage(String messageId, String from, String sessionId) throws JSONException {

        ReceiptMessage receiptMessage = new ReceiptMessage();
        receiptMessage.msgId = messageId;
        receiptMessage.timestamp = System.currentTimeMillis();
        receiptMessage.receiveFrom = from;
        receiptMessage.sessionIdentifier = sessionId;

        return receiptMessage;
    }

    /**
     * 查看阅后即焚消息的权限
     */
    @SuppressLint("StaticFieldLeak")
    public static void queryBurnMessageAuth(final Context context, final String messageId, final BaseCallBackNetWorkListener netWorkListener) {
        new AsyncTask<Void, Void, HttpResult>() {
            @Override
            protected HttpResult doInBackground(Void... params) {

                return MessageSyncNetService.queryBurnMessageAuth(context, messageId);
            }

            @Override
            protected void onPostExecute(HttpResult httpResult) {
                if (httpResult.isRequestSuccess()) {
                    netWorkListener.onSuccess();
                } else {
                    NetworkHttpResultErrorHandler.handleHttpError(httpResult, netWorkListener);
                }
            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }

    @SuppressLint("StaticFieldLeak")
    public static void parseUrlForShare(final Context context, final String shareUrl, final OnParseUrlForShareListener listener) {
        new AsyncTask<Void, Void, HttpResult>() {
            @Override
            protected HttpResult doInBackground(Void... params) {

                return MessageSyncNetService.parseShareUrl(context, shareUrl);
            }

            @Override
            protected void onPostExecute(HttpResult httpResult) {
                if (httpResult.isRequestSuccess()) {
                    String result = NetWorkHttpResultHelper.getResultText(httpResult.result);
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        listener.onParseSuccess(ShareChatMessage.getArticleItemFromJson(shareUrl, jsonObject));
                        return;

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }


                NetworkHttpResultErrorHandler.handleHttpError(httpResult, listener);
            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }

    @SuppressLint("StaticFieldLeak")
    public static void confirmEmergencyMessage(final Context context, final EmergencyMessageConfirmRequest emergencyMessageConfirmRequest, final BaseCallBackNetWorkListener callBackNetWorkListener) {
        new AsyncTask<Void, Void, HttpResult>() {
            @Override
            protected HttpResult doInBackground(Void... voids) {
                return MessageSyncNetService.confirmEmergencyMessage(context, emergencyMessageConfirmRequest);
            }

            @Override
            protected void onPostExecute(HttpResult httpResult) {
                if (httpResult.isRequestSuccess()) {
                    callBackNetWorkListener.onSuccess();
                    return;
                }

                NetworkHttpResultErrorHandler.handleHttpError(httpResult, callBackNetWorkListener);

            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }


    @SuppressLint("StaticFieldLeak")
    public static void queryMessageHistory(final Context context, final QueryMessageHistoryRequest queryMessageHistoryRequest, final GetHistoryMessageListener getHistoryMessageListener) {
        new AsyncTask<Void, Void, QueryMessageResult>() {
            @Override
            protected QueryMessageResult doInBackground(Void... voids) {
                HttpResult httpResult = MessageSyncNetService.queryMessageHistory(context, queryMessageHistoryRequest);

                QueryMessageResult messagesResult = new QueryMessageResult();
                messagesResult.setHttpResult(httpResult);


                if (httpResult.isRequestSuccess()) {

                    QueryMessageHistoryResponse queryMessageHistoryResponse = (QueryMessageHistoryResponse) httpResult.resultResponse;
                    QueryMessageHistoryResult result = queryMessageHistoryResponse.getResult();
                    if (null != result) {
                        List<ChatPostMessage> msgList = new ArrayList<>(MessageCovertUtil.coverJsonToMessageHistoryList(JsonUtil.toJsonList(result.getRecords())));
                        List<ChatPostMessage> msgListFilter = new ArrayList<>();

                        for(ChatPostMessage msg: msgList) {
                            if(msg instanceof ArticleChatMessage) {
                                msgListFilter.addAll(splitArticleMessage((ArticleChatMessage) msg));

                            } else {
                                msgListFilter.add(msg);
                            }
                        }


                        messagesResult.setRealOfflineMsgSize(result.getRecords().size());
                        messagesResult.setPostTypeMessages(msgListFilter);
                        messagesResult.setSuccess(true);
                    }

                }


                return messagesResult;
            }

            @Override
            protected void onPostExecute(QueryMessageResult messagesResult) {
                if (messagesResult.getSuccess()) {
                    getHistoryMessageListener.getHistoryMessageSuccess(messagesResult.getPostTypeMessages(), messagesResult.getRealOfflineMsgSize());

                    return;
                }


                NetworkHttpResultErrorHandler.handleHttpError(messagesResult.getHttpResult(), getHistoryMessageListener);
            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }

    public static void queryMessageTags(final Context context, final String orgId, final String appId, final OnMessageTagsListener listener) {
        new AsyncTask<Void, Void, List<MessageTags>>() {
            @Override
            protected List<MessageTags> doInBackground(Void... voids) {
                HttpResult httpResult = MessageSyncNetService.queryMessageTags(context, orgId, appId);

                if (httpResult.isRequestSuccess()) {
                    QueryMessageTagResponse queryMessageTagResponse = (QueryMessageTagResponse) httpResult.resultResponse;
                    QueryMessageTagResult result = queryMessageTagResponse.getResult();
                    return result.getRecords();

                }
                return null;
            }

            @Override
            protected void onPostExecute(List<MessageTags> records) {
                if (listener == null || records == null) {
                    return;
                }
                listener.getMessageTagsSuccess(records);
            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }

    private static List<ArticleChatMessage> splitArticleMessage(ArticleChatMessage articleChatMessage) {

        List<ArticleChatMessage> multiArticleMsgs = new ArrayList<>();

        for (ArticleItem articleItem : articleChatMessage.articles) {
            ArticleChatMessage newMsg = CloneUtil.cloneTo(articleChatMessage);
            newMsg.articles = ListUtil.makeSingleList(articleItem);

            multiArticleMsgs.add(newMsg);
        }

        return multiArticleMsgs;
    }


    public interface OnParseUrlForShareListener extends NetWorkFailListener {
        void onParseSuccess(ArticleItem articleItem);
    }


    public interface GetMessagePerPageListener {
        /**
         * 分页收取离线消息
         *
         * @param messagesResult
         * @param begin
         */
        void getMessagePerPage(MessagesResult messagesResult, long begin);
    }


    /**
     * 查询历史消息监听
     */
    public interface GetHistoryMessageListener extends NetWorkFailListener {

        void getHistoryMessageSuccess(List<ChatPostMessage> historyMessages, int realOfflineSize);

    }

    public interface OnMessageTagsListener extends NetWorkFailListener {
        void getMessageTagsSuccess(List<MessageTags> tagsList);
    }


}
