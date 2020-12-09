package com.foreveross.atwork.modules.chat.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;

import com.foreverht.threadGear.AsyncTaskThreadPool;
import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.UrlConstantManager;
import com.foreveross.atwork.api.sdk.net.HttpResult;
import com.foreveross.atwork.api.sdk.net.MediaCenterHttpURLConnectionUtil;
import com.foreveross.atwork.api.sdk.net.model.DownloadFileParamsMaker;
import com.foreveross.atwork.api.sdk.net.model.UploadFileParamsMaker;
import com.foreveross.atwork.api.sdk.upload.MediaCenterNetManager;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.Session;
import com.foreveross.atwork.infrastructure.model.SessionType;
import com.foreveross.atwork.infrastructure.model.ShowListItem;
import com.foreveross.atwork.infrastructure.model.discussion.Discussion;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatEnvironment;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.FileTransferChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.MultipartChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.TextChatMessage;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.AtWorkDirUtils;
import com.foreveross.atwork.infrastructure.utils.FileUtil;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.infrastructure.utils.MessageCovertUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.TimeUtil;
import com.foreveross.atwork.infrastructure.utils.aliyun.CdnHelper;
import com.foreveross.atwork.infrastructure.utils.file.FileStreamHelper;
import com.foreveross.atwork.modules.discussion.manager.DiscussionManager;
import com.foreveross.atwork.modules.chat.service.upload.MultipartUploadListener;
import com.foreveross.atwork.utils.ChatMessageHelper;
import com.foreveross.atwork.utils.ContactQueryHelper;
import com.foreveross.atwork.utils.watermark.WaterMarkHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Created by dasunsy on 2017/6/21.
 */

public class MultipartMsgHelper {

    @SuppressLint("StaticFieldLeak")
    public static void loadData(Context context, MultipartChatMessage multipartChatMessage, OnLoadDataListener onLoadDataListener) {
        onLoadDataListener.onStart();

        new AsyncTask<Void, Void, List<ChatPostMessage>>() {

            @Override
            protected List<ChatPostMessage> doInBackground(Void[] params) {


                String dataPath = MultipartMsgHelper.getMultipartPath(multipartChatMessage);

                if (!FileUtil.isExist(dataPath)) {
                    if (StringUtils.isEmpty(multipartChatMessage.mFileId)) {
                        return null;
                    }

                    publishProgress();

                    String downloadUrl = String.format(UrlConstantManager.getInstance().V2_getDownloadUrl(true), multipartChatMessage.mFileId, LoginUserInfo.getInstance().getLoginUserAccessToken(context));
                    downloadUrl = CdnHelper.wrapCdnUrl(downloadUrl);

                    HttpResult httpResult = MediaCenterHttpURLConnectionUtil.getInstance().downloadFile(
                            DownloadFileParamsMaker.Companion.newRequest().setDownloadId(UUID.randomUUID().toString()).setDownloadUrl(downloadUrl).setDownloadPath(dataPath).setEncrypt(AtworkConfig.OPEN_DISK_ENCRYPTION)
                    );

                    if (!httpResult.isNetSuccess()) {
                        return null;
                    }
                }

                String multiStr = new String(FileStreamHelper.readFile(dataPath));
                List<ChatPostMessage> resultList = MessageCovertUtil.coverJsonToMessageList(context, multiStr);
                assembleMessages(resultList, multipartChatMessage);

                Collections.sort(resultList);

                return resultList;
            }

            @Override
            protected void onProgressUpdate(Void... values) {
                onLoadDataListener.onNetStart();
            }

            @Override
            protected void onPostExecute(List<ChatPostMessage> result) {
                if(!ListUtil.isEmpty(result)) {
                    multipartChatMessage.mMsgList = result;
                    onLoadDataListener.onSuccess(result);

                } else {
                    onLoadDataListener.onError();
                }
            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());

    }


    private static void assembleMessages(List<ChatPostMessage> resultList, MultipartChatMessage multipartChatMessage) {
        for(ChatPostMessage chatPostMessage: resultList) {
            chatPostMessage.parentMultipartChatMessage = multipartChatMessage;

            switch (chatPostMessage.parentMultipartChatMessage.multipartType) {
                case MULTIPART:
                    chatPostMessage.chatEnvironment = ChatEnvironment.MULTIPART;
                    break;

                case FAVORITE:
                    chatPostMessage.chatEnvironment = ChatEnvironment.FAVORITE;

                    if(chatPostMessage instanceof FileTransferChatMessage) {
                        ((FileTransferChatMessage)chatPostMessage).expiredTime = -1;
                    }

                    break;
            }

        }
    }

    /**
     * 上传fileid
     */
    public static void doUploadMultiPartFile(final Context context, final MultipartChatMessage message, Session session, final OnMessageFileUploadedListener listener) {
        new AsyncTask<Void, Void, MultipartChatMessage>() {
            @Override
            protected MultipartChatMessage doInBackground(Void... voids) {
                MultipartChatMessage multipartChatMessage = MultipartMsgHelper.assembleMsgsSync(BaseApplicationLike.baseContext, message, session);
                String jsonStr = MessageCovertUtil.covertMessageListToJson(multipartChatMessage.mMsgList);

                FileStreamHelper.saveFile(getMultipartPath(multipartChatMessage), jsonStr.getBytes());
                return multipartChatMessage;
            }

            @Override
            protected void onPostExecute(MultipartChatMessage assembleMessage) {
                String filePath = getMultipartPath(assembleMessage);
                UploadFileParamsMaker uploadFileParamsMaker = UploadFileParamsMaker.newRequest()
                        .setType(MediaCenterNetManager.COMMON_FILE)
                        .setMsgId(assembleMessage.deliveryId)
                        .setFilePath(filePath)
                        .setNeedCheckSum(false);

                MediaCenterNetManager.uploadFile(context, uploadFileParamsMaker);
                MediaCenterNetManager.MediaUploadListener mediaUploadListener
                        = MediaCenterNetManager.getMediaUploadListenerById(assembleMessage.deliveryId, MediaCenterNetManager.UploadType.VOICE);

                if (null == mediaUploadListener) {
                    mediaUploadListener = new MultipartUploadListener(assembleMessage, listener);
                    MediaCenterNetManager.addMediaUploadListener(mediaUploadListener);
                }
            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());

    }

    public static String getTimeRange(List<ChatPostMessage> chatPostMessageList) {
        if(!ListUtil.isEmpty(chatPostMessageList)) {
            String startTime = TimeUtil.getStringForMillis(chatPostMessageList.get(0).deliveryTime, TimeUtil.YYYY_MM_DD);

            if(1 == chatPostMessageList.size()) {
                return startTime;
            } else {
                String endTime =  TimeUtil.getStringForMillis(chatPostMessageList.get(chatPostMessageList.size() - 1).deliveryTime, TimeUtil.YYYY_MM_DD);
                if(startTime.equalsIgnoreCase(endTime)) {
                    return startTime;
                } else {
                    return startTime + " ~ " + endTime;
                }
            }
        }

        return StringUtils.EMPTY;
    }


    @SuppressLint("StaticFieldLeak")
    public static void assembleMsg(Context context, MultipartChatMessage multipartChatMessage, Session session, OnMsgAssembleListener listener) {
        new AsyncTask<Void, Void, MultipartChatMessage>() {
            @Override
            protected MultipartChatMessage doInBackground(Void... voids) {

                MultipartChatMessage messageAssembled = assembleMsgsSync(context, multipartChatMessage, session);
                String jsonStr = MessageCovertUtil.covertMessageListToJson(messageAssembled.mMsgList);

                FileStreamHelper.saveFile(getMultipartPath(messageAssembled), jsonStr.getBytes());
                return messageAssembled;
            }

            @Override
            protected void onPostExecute(MultipartChatMessage multipartChatMessage) {
                listener.onSuccess(multipartChatMessage);
            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }

    @SuppressLint("StaticFieldLeak")
    public static void updateMultipartMsgFileMsgStatus(Context context, MultipartChatMessage multipartChatMessage, FileTransferChatMessage fileTransferChatMessage) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {

                for(ChatPostMessage chatPostMessage : multipartChatMessage.mMsgList) {
                    if(chatPostMessage.equals(fileTransferChatMessage)) {
                        FileTransferChatMessage fileMsgInMultipart = (FileTransferChatMessage) chatPostMessage;
                        fileMsgInMultipart.fileStatus = fileTransferChatMessage.fileStatus;
                        break;
                    }
                }

                String jsonStr = MessageCovertUtil.covertMessageListToJsonWithLocalData(multipartChatMessage.mMsgList);

                FileStreamHelper.saveFile(getMultipartPath(multipartChatMessage), jsonStr.getBytes());
                return null;
            }
        }.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
    }


    public static MultipartChatMessage assembleMsgsSync(Context context, MultipartChatMessage multipartChatMessage, Session session) {
        List<ChatPostMessage> messageList = multipartChatMessage.mMsgList;
        String sessionId = ChatMessageHelper.getChatUser(messageList.get(0)).mUserId;
//        Session session = ChatSessionDataWrap.getInstance().getSession(sessionId, null);

        if(SessionType.Discussion == session.type) {
            Discussion discussion = DiscussionManager.getInstance().queryDiscussionSync(BaseApplicationLike.baseContext, session.identifier);
            if(null != discussion && discussion.showEmployeeInfo()) {
                multipartChatMessage.mSourceOrgCode = discussion.getOrgCodeCompatible();
                multipartChatMessage.mIsFromInternalDiscussion = true;
            }
        }

        makeTitle(context, session, multipartChatMessage);
        makeMsgs(multipartChatMessage, messageList);
        makeContent(multipartChatMessage, messageList);
        makeWatermarkEnable(multipartChatMessage, sessionId, session);

        return multipartChatMessage;
    }

    private static void makeWatermarkEnable(MultipartChatMessage multipartChatMessage, String sessionId, Session session) {
        multipartChatMessage.mWatermarkEnable = WaterMarkHelper.isWatermarkEnable(sessionId, session.type);
    }

    private static void makeTitle(Context context, Session sourceSession, MultipartChatMessage multipartChatMessage) {
        if(SessionType.Discussion == sourceSession.type) {
            multipartChatMessage.mParticipators = new ArrayList<>();
            multipartChatMessage.mParticipators.add(sourceSession.name);

        } else {
            multipartChatMessage.mParticipators = new ArrayList<>();
            multipartChatMessage.mParticipators.add(LoginUserInfo.getInstance().getLoginUserName(context));
            multipartChatMessage.mParticipators.add(sourceSession.name);
        }

        multipartChatMessage.mTitle = getTitle(multipartChatMessage);
    }


    private static void makeMsgs(MultipartChatMessage multipartChatMessage, List<ChatPostMessage> messageList) {
        Collections.sort(messageList);
        String orgCode = null;

        if (multipartChatMessage.mIsFromInternalDiscussion) {
            orgCode = multipartChatMessage.mSourceOrgCode;
        }

        List<String> contentShowUserId = new ArrayList<>();
        for(int i = 0; i < messageList.size(); i++) {
            contentShowUserId.add(messageList.get(i).from);
        }
        //内部群查询雇员才需要 orgCode
        List<? extends ShowListItem> contactList = ContactQueryHelper.queryLocalContactsSync(orgCode, contentShowUserId);
        for(ChatPostMessage chatPostMessage : messageList) {
            ShowListItem contact = getContact(contactList, chatPostMessage.from);

            if (null != contact) {
                String username = contact.getParticipantTitle();
                if(!StringUtils.isEmpty(username)) {
                    chatPostMessage.mMyName = username;
                }

                chatPostMessage.mMyAvatar = contact.getAvatar();

                if (multipartChatMessage.mIsFromInternalDiscussion) {
                    String empName = contact.getTitle();
                    if (!StringUtils.isEmpty(empName)) {
                        chatPostMessage.mMyNameInDiscussion = empName;
                    }

                }
            }

            if(chatPostMessage instanceof TextChatMessage) {
                TextChatMessage textChatMessage = (TextChatMessage) chatPostMessage;
                textChatMessage.text = TextMsgHelper.getVisibleText(textChatMessage);
            }
        }
    }

    private static void makeContent(MultipartChatMessage multipartChatMessage, List<ChatPostMessage> messageList) {
        StringBuilder strBuilder = new StringBuilder();

        for(int i = 0; i < messageList.size(); i++) {
            String fromName = messageList.get(i).mMyNameInDiscussion;
            if(StringUtils.isEmpty(fromName)) {
                fromName = messageList.get(i).mMyName;
            }
            strBuilder.append(fromName).append(":").append(messageList.get(i).getSessionShowTitle());

            if(3 == i) {
                break;
            } else {

                if (i != messageList.size() - 1) {
                    strBuilder.append("\n");
                }
            }

        }

        multipartChatMessage.mContent = strBuilder.toString();
    }

    public static ShowListItem getContact(List<? extends ShowListItem> contactList, String userId) {
        for(ShowListItem contact : contactList) {
            if(contact.getId().equals(userId)) {
                return contact;
            }
        }

        return null;
    }

    public static String getTitle(MultipartChatMessage multipartChatMessage) {
        if(!ListUtil.isEmpty(multipartChatMessage.mParticipators)) {
            if(1 == multipartChatMessage.mParticipators.size()) {
                return AtworkApplicationLike.getResourceString(R.string.multipart_title_show_one, multipartChatMessage.mParticipators.get(0));

            } else {
                return AtworkApplicationLike.getResourceString(R.string.multipart_title_show_two, multipartChatMessage.mParticipators.get(0), multipartChatMessage.mParticipators.get(1));

            }
        }

        return multipartChatMessage.mTitle;
    }


    /**
     * 合并消息路径
     * */
    public static String getMultipartPath(MultipartChatMessage multipartChatMessage) {

        if (!StringUtils.isEmpty(multipartChatMessage.mFileId)) {

            String path = getMultipartMsgFileIdNamePath(multipartChatMessage);
            return path;
        }

        return AtWorkDirUtils.getInstance().getMultipartDir(BaseApplicationLike.baseContext) + multipartChatMessage.deliveryId;
    }

    @NonNull
    private static String getMultipartMsgFileIdNamePath(MultipartChatMessage multipartChatMessage) {
        return AtWorkDirUtils.getInstance().getMultipartDir(BaseApplicationLike.baseContext) + multipartChatMessage.mFileId;
    }

    public interface OnMsgAssembleListener {
        void onSuccess(MultipartChatMessage multipartChatMessage);
    }

    public interface OnLoadDataListener {
        void onStart();
        void onNetStart();
        void onSuccess(List<ChatPostMessage> chatPostMessageList);
        void onError();
    }

    public interface OnMessageFileUploadedListener {
        void onSuccess(MultipartChatMessage message);
        void onError(int errorCode, String errorMsg);
    }
}
