package com.foreveross.atwork.manager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Base64;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.foreverht.cache.DiscussionCache;
import com.foreverht.cache.DropboxCache;
import com.foreverht.db.service.daoService.FileDaoService;
import com.foreverht.db.service.repository.DropboxConfigRepository;
import com.foreverht.db.service.repository.DropboxRepository;
import com.foreverht.db.service.repository.MessageRepository;
import com.foreverht.threadGear.AsyncTaskThreadPool;
import com.foreverht.workplus.module.file_share.FileShareAction;
import com.foreverht.workplus.ui.component.dialogFragment.CommonPopSelectData;
import com.foreverht.workplus.ui.component.dialogFragment.W6sSelectDialogFragment;
import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.BaseNetWorkListener;
import com.foreveross.atwork.api.sdk.dropbox.DropboxAsyncNetService;
import com.foreveross.atwork.api.sdk.dropbox.DropboxSyncNetService;
import com.foreveross.atwork.api.sdk.dropbox.requestJson.DropboxRequestJson;
import com.foreveross.atwork.api.sdk.dropbox.requestJson.FileTranslateRequest;
import com.foreveross.atwork.api.sdk.dropbox.responseJson.DropboxResponse;
import com.foreveross.atwork.api.sdk.net.HttpResult;
import com.foreveross.atwork.api.sdk.net.model.UploadFileParamsMaker;
import com.foreveross.atwork.api.sdk.upload.MediaCenterNetManager;
import com.foreveross.atwork.api.sdk.util.NetworkHttpResultErrorHandler;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.manager.DomainSettingsManager;
import com.foreveross.atwork.infrastructure.model.Session;
import com.foreveross.atwork.infrastructure.model.discussion.Discussion;
import com.foreveross.atwork.infrastructure.model.dropbox.Dropbox;
import com.foreveross.atwork.infrastructure.model.dropbox.DropboxConfig;
import com.foreveross.atwork.infrastructure.model.dropbox.ShareItemRequester;
import com.foreveross.atwork.infrastructure.model.file.FileData;
import com.foreveross.atwork.infrastructure.model.file.SDCardFileData;
import com.foreveross.atwork.infrastructure.model.orgization.Organization;
import com.foreveross.atwork.infrastructure.model.user.LoginUserBasic;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.BodyType;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.ParticipantType;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.FileTransferChatMessage;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.AtWorkDirUtils;
import com.foreveross.atwork.infrastructure.utils.BitmapUtil;
import com.foreveross.atwork.infrastructure.utils.ImageShowHelper;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.infrastructure.utils.TimeUtil;
import com.foreveross.atwork.infrastructure.utils.encryption.EncryptCacheDisk;
import com.foreveross.atwork.infrastructure.utils.encryption.MD5Utils;
import com.foreveross.atwork.modules.discussion.manager.DiscussionManager;
import com.foreveross.atwork.modules.dropbox.activity.DropboxModifyActivity;
import com.foreveross.atwork.modules.dropbox.activity.SaveToDropboxActivity;
import com.foreveross.atwork.modules.dropbox.adapter.DropboxFileAdapter;
import com.foreveross.atwork.modules.dropbox.component.DropboxFileAttrDialog;
import com.foreveross.atwork.modules.dropbox.fragment.UserDropboxFragment;
import com.foreveross.atwork.modules.group.activity.TransferMessageActivity;
import com.foreveross.atwork.modules.group.module.TransferMessageControlAction;
import com.foreveross.atwork.modules.group.module.TransferMessageMode;
import com.foreveross.atwork.utils.AtworkToast;
import com.foreveross.atwork.utils.FileHelper;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static com.foreveross.atwork.AtworkApplicationLike.getResourceString;
import static com.foreveross.atwork.api.sdk.dropbox.DropboxAsyncNetService.parseRecords;
import static com.foreveross.atwork.modules.dropbox.fragment.UserDropboxFragment.REQUEST_CODE_COPY_DROPBOX;
import static com.foreveross.atwork.modules.dropbox.fragment.UserDropboxFragment.REQUEST_CODE_MODIFY_DROPBOX;

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
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 *                       __           __
 * .--.--.--.-----.----.|  |--.-----.|  |.--.--.-----.
 * |  |  |  |  _  |   _||    <|  _  ||  ||  |  |__ --|
 * |________|_____|__|  |__|__|   __||__||_____|_____|
 *                            |__|
 * Created by reyzhang22 on 16/9/14.
 */
public class DropboxManager {

    private static final DropboxManager sInstance = new DropboxManager();

    public static DropboxManager getInstance() {
        return sInstance;
    }

    private static boolean isAnalysing = false;

    /**
     * 批量插入更新网盘信息
     * @param activity
     * @param sourceId
     * @param dropboxes
     */
    public void batchInsertDropboxes(Context activity, String sourceId, List<Dropbox> dropboxes, boolean readOnly) {
        if (ListUtil.isEmpty(dropboxes)) {
            return;
        }
        DropboxRepository.getInstance().batchInsertDropboxes(dropboxes);
        long lastRefreshTime = -1;
        if (!ListUtil.isEmpty(dropboxes)) {
            lastRefreshTime = dropboxes.get(0).mLastModifyTime;
        }
        DropboxConfig config = new DropboxConfig();
        config.mSourceId = sourceId;
        config.mLastRefreshTime = lastRefreshTime;
        config.mReadOnly = readOnly;
        DropboxConfigRepository.getInstance().insertOrUpdateDropboxConfig(config);
    }

    /**
     * 异步更新一个网盘状态
     * @param dropbox
     */
    public void updateDropbox(Dropbox dropbox) {
        DropboxRepository.getInstance().insertOrUpdateDropbox(dropbox);
    }

    /**
     * 通过fileId获取本地的网盘文件信息(tip 同步)
     * @param fileId
     * @return
     */
    public Dropbox getLocalDropboxByFileId(String fileId) {
        Dropbox dropbox = DropboxCache.getInstance().getDropboxCache(fileId);
        if (dropbox == null) {
            dropbox = DropboxRepository.getInstance().getDropboxByFileId(fileId);
        }
        return dropbox;
    }

    /**
     * 搜索网盘
     * @param context
     * @param domainId
     * @param sourceType
     * @param sourceId
     * @param key
     * @param listener
     * @return
     */
    public void searchDropboxByName(Context context, String domainId, Dropbox.SourceType sourceType, String sourceId, String key, String value, OnSearchOnlineListener listener) {
        DropboxConfig dropboxConfig = DropboxConfigManager.getInstance().syncGetDropboxConfigBySourceId(context, sourceId);
        new AsyncTask<Void, Void, HttpResult>() {
            @Override
            protected HttpResult doInBackground(Void... params) {
                //1. search online

                return DropboxSyncNetService.getInstance().getDropboxByParams(context, domainId, sourceType.toString(), sourceId, "", value,
                        "", "", "", "", "", true, "-1", "", "", dropboxConfig);
            }

            @Override
            protected void onPostExecute(HttpResult httpResult) {
                if (listener == null) {
                    return;
                }

                if (httpResult.isNetSuccess()) {
                    List<Dropbox>dropboxes = new ArrayList<>();
                    parseRecords(context, dropboxes, dropboxConfig, httpResult);
                    listener.onSearchOnlineResult(key, dropboxes);
                }

            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }

    /**
     * 重命名
     * @param context
     * @param dropbox
     * @param newName
     * @param listener
     */
    public void renameDropboxFile(Context context, Dropbox dropbox, String newName, DropboxAsyncNetService.OnDropboxListener listener) {
        DropboxRequestJson requestJson = new DropboxRequestJson();
        requestJson.mName = newName;
        DropboxRequestJson.OptUser optUser = new DropboxRequestJson.OptUser();
        optUser.nName = LoginUserInfo.getInstance().getLoginUserBasic(context).mName;
        requestJson.mUser = optUser;
        DropboxAsyncNetService.getInstance().renameDropbox(context, dropbox, new Gson().toJson(requestJson), listener);
    }

    /**
     * 删除网盘
     * @param context
     * @param dropboxList
     * @param listener
     */
    public void deleteDropboxFile(Context context, List<String> dropboxList, String parentId, String domainId, final Dropbox.SourceType sourceType, final String sourceId, DropboxAsyncNetService.OnDropboxListener listener) {
        DropboxRequestJson requestJson = new DropboxRequestJson();
        requestJson.mParent = parentId;
        requestJson.mIds = dropboxList.toArray(new String[]{});
        DropboxRequestJson.OptUser optUser = new DropboxRequestJson.OptUser();
        optUser.nName = LoginUserInfo.getInstance().getLoginUserBasic(context).mName;
        requestJson.mUser = optUser;
        DropboxAsyncNetService.getInstance().removeDropbox(context, domainId, sourceType.toString(), sourceId, new Gson().toJson(requestJson), listener);
    }

    /**
     *  删除本地网盘记录
     * @param context
     * @param deleteList
     */
    public void deleteLocalDropboxFile(Context context, List<String> deleteList) {
        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... params) {
                DropboxRepository.getInstance().batchDeleteDropboxList(deleteList);
                return null;
            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }



    /**
     * 通过sourceId查询该id下的所有网盘信息
     * @param context
     * @param sourceId
     * @param listener
     */
    public void queryDropboxBySourceId(Context context, String sourceId, OnQueryFilesListener listener) {
        new AsyncTask<Void, Void, List<Dropbox>>() {
            @Override
            protected List<Dropbox> doInBackground(Void... params) {
                return DropboxRepository.getInstance().getDropboxBySourceId(sourceId);
            }

            @Override
            protected void onPostExecute(List<Dropbox> dropboxes) {
                if (listener == null) {
                    return;
                }
                listener.onQueryFileList(dropboxes);
            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }

    public void getIsGoingOverdueDropboxes( OnQueryFilesListener listener) {
        if (isAnalysing) {
            return;
        }
        new AsyncTask<Void, Void, List<Dropbox>>() {

            @Override
            protected List<Dropbox> doInBackground(Void... voids) {
                isAnalysing = true;
                List<Dropbox> datas = DropboxRepository.getInstance().getIsGoingOverdueDropbox();
                DropboxRepository.getInstance().batchInsertDropboxes(datas);
                return datas;
            }

            @Override
            protected void onPostExecute(List<Dropbox> dropboxes) {
                isAnalysing = false;
                super.onPostExecute(dropboxes);
                if (listener == null) {
                    return;
                }
                listener.onQueryFileList(dropboxes);
            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }

    /**
     * 移动网盘
     * @param context
     * @param movedList
     * @param lastParentId
     * @param newParentId
     * @param domainId
     * @param sourceType
     * @param sourceId
     * @param listener
     */
    public void moveDropboxFile(Context context, Set<String> movedList, String lastParentId, String newParentId, String domainId, Dropbox.SourceType sourceType, String sourceId, DropboxAsyncNetService.OnDropboxListener listener) {
        DropboxRequestJson requestJson = new DropboxRequestJson();
        requestJson.mParent = lastParentId;
        String[] ids = new String[movedList.size()];
        int i = 0;
        for (String id : movedList) {
            ids[i] = id;
            i++;
        }
        requestJson.mIds = ids;
        requestJson.mTargetParent = newParentId;
        DropboxRequestJson.OptUser optUser = new DropboxRequestJson.OptUser();
        optUser.nName = LoginUserInfo.getInstance().getLoginUserBasic(context).mName;
        requestJson.mUser = optUser;
        DropboxConfig dropboxConfig = DropboxConfigManager.getInstance().syncGetDropboxConfigBySourceId(context, sourceId);
        DropboxAsyncNetService.getInstance().moveDropbox(context, domainId, sourceType.toString(), sourceId, new Gson().toJson(requestJson), dropboxConfig, listener);
    }

    /**
     * 通过api请求复制网盘
     * @param context
     * @param dropbox
     * @param targetDomainId
     * @param targetSourceType
     * @param targetSource
     * @param targetParent
     * @param listener
     */
    public void copyDroboxFile(Context context, Dropbox dropbox, String targetDomainId, Dropbox.SourceType targetSourceType, String targetSource, String targetParent, DropboxAsyncNetService.OnDropboxListener listener) {
        if (dropbox == null) {
            return;
        }
        DropboxRequestJson requestJson = new DropboxRequestJson();
        requestJson.mParent = dropbox.mParentId;
        String[] ids = new String[1];
        ids[0] = dropbox.mFileId;
        requestJson.mTargetSourceId = targetSource;
        requestJson.mTargetSourceType = targetSourceType.simpleString();
        requestJson.mTargetParent = targetParent;
        requestJson.mIds = ids;
        DropboxRequestJson.OptUser optUser = new DropboxRequestJson.OptUser();
        optUser.nName = LoginUserInfo.getInstance().getLoginUserBasic(context).mName;
        requestJson.mUser = optUser;
        DropboxAsyncNetService.getInstance().copyDropbox(context, dropbox.mDomainId, dropbox.mSourceType.toString(), dropbox.mSourceId, new Gson().toJson(requestJson), listener);
    }

    /**
     * 查询本地网盘sourceId下的所有目录
     * @param context
     * @param listener
     */
    public void queryDropboxDirs(Context context, String sourceId, OnQueryFilesListener listener) {
        new AsyncTask<Void, Void, List<Dropbox>>() {
            @Override
            protected List<Dropbox> doInBackground(Void... params) {
                return DropboxRepository.getInstance().getDropboxDirs(sourceId);
            }

            @Override
            protected void onPostExecute(List<Dropbox> dropboxes) {
                if (listener == null) {
                    return;
                }
                listener.onQueryFileList(dropboxes);
            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }

    /**
     * 通过api设置网盘权限 读写，水印等
     * @param context
     * @param domainId
     * @param sourceType
     * @param opsUserId
     * @param readonly          是否显示只读
     * @param showWatermark     是否显示水印
     * @param listener
     */
    public void setDropboxRW(Context context, String domainId, Dropbox.SourceType sourceType, String opsUserId, boolean readonly, boolean showWatermark, DropboxAsyncNetService.OnDropboxListener listener) {
        JSONObject json = new JSONObject();
        try {
            json.put("readonly", readonly);
            json.put("show_watermark", showWatermark);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        DropboxAsyncNetService.getInstance().setDropboxRWSetting(context, domainId, sourceType, opsUserId, json.toString(), listener);
    }

    public void translateFile(Context context, FileTranslateRequest request, DropboxAsyncNetService.OnFileTranslateListener listener) {
        DropboxAsyncNetService.getInstance().translateFile(context, request, listener);
    }

    public void shareDropboxFile(Context context, FileShareAction action, String requestParams, DropboxAsyncNetService.OnDropboxSharedListener listener) {
        DropboxAsyncNetService.getInstance().shareDropbox(context, action.getDomainId(), action.getSourceType(), action.getOpsId(), requestParams, listener);
    }

    /**
     * 通过api上传文件到网盘 (file data方式)
     * @param activity
     * @param fileDatas
     * @param domainId
     * @param sourceType
     * @param sourceId
     * @param listener
     */
    public void uploadFileDataToDropbox(Activity activity, List<FileData> fileDatas, String domainId, Dropbox.SourceType sourceType, String sourceId, String currentParentId,  OnFileUploadListener listener) {
        List<Dropbox> uploadList = new ArrayList<>();
        for (FileData fileData : fileDatas) {
            uploadList.add(Dropbox.convertFromFileData(activity, fileData, sourceId, sourceType, currentParentId));
        }
        uploadFileToDropbox(activity, uploadList, domainId, sourceType, sourceId, currentParentId, false, listener);
    }

    /**
     * 通过api上传网盘 (Dropbox 方式)
     * @param activity
     * @param dropboxes
     * @param domainId
     * @param sourceType
     * @param sourceId
     * @param currentParentId
     * @param update
     * @param listener
     */
    public void uploadFileToDropbox(Activity activity, List<Dropbox> dropboxes, String domainId, Dropbox.SourceType sourceType, String sourceId, String currentParentId, boolean update, OnFileUploadListener listener) {
        if (dropboxes == null) {
            return;
        }
        new AsyncTask<Void, Void, List<Dropbox>>() {
            @Override
            protected List<Dropbox> doInBackground(Void... voids) {
                DropboxRepository.getInstance().batchInsertDropboxes(dropboxes);
                return dropboxes;
            }

            @Override
            protected void onPostExecute(List<Dropbox> uploadList) {
                if (listener != null && !update) {
                    //refresh the list UI..
                    listener.onFileStartUploading(uploadList);
                }
                startUploadFile(activity, uploadList, domainId, sourceType, sourceId, currentParentId, listener);
            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }

    /**
     * 通过api下载网盘文件到本地
     * @param activity
     * @param dropbox
     * @param listener
     */
    public void downloadFileFromDropbox(Activity activity, Dropbox dropbox, OnFileDownloadListener listener) {
        new AsyncTask<Void, Void, Dropbox>() {
            @Override
            protected Dropbox doInBackground(Void... params) {
                //更新数据库
                dropbox.mDownloadStatus = Dropbox.DownloadStatus.Downloading;
                DropboxRepository.getInstance().insertOrUpdateDropbox(dropbox);
                return dropbox;
            }

            @Override
            protected void onPostExecute(Dropbox downloadDropbox) {
                super.onPostExecute(downloadDropbox);
                startDownloadFile(activity, downloadDropbox, listener);
            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }

    /**
     * 筛选本地网盘类型
     * @param context
     * @param fileType
     * @param sourceId
     * @param parentId
     * @param listener
     */
    public void getFileByFileType(Context context, Dropbox.DropboxFileType fileType, String sourceId, String parentId, OnQueryFilesListener listener) {
        new AsyncTask<Void, Void, List<Dropbox>>() {
            @Override
            protected List<Dropbox> doInBackground(Void... params) {
                return DropboxRepository.getInstance().getDropboxByFileTypeInSourceId(sourceId, fileType, parentId);
            }

            @Override
            protected void onPostExecute(List<Dropbox> dropboxes) {

                if (listener ==  null) {
                    return;
                }
                listener.onQueryFileList(dropboxes);
            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }


    /**
     * 获取分享文件列表
     * @param context
     * @param requester
     * @param listener
     */
    public void getDropboxShareItems(Context context, ShareItemRequester requester, DropboxAsyncNetService.OnFetchShareItemsListener listener) {
        DropboxAsyncNetService.getInstance().getDropboxShareItems(context, requester, listener);
    }

    /**
     * 添加上传监听接口
     * @param activity
     * @param dropbox
     * @param domainId
     * @param sourceType
     * @param sourceId
     * @param listener
     */
    private void addDropboxUploadListener(Activity activity, Dropbox dropbox, String domainId, Dropbox.SourceType sourceType, String sourceId, String currentParentId, OnFileUploadListener listener) {
        if (Dropbox.UploadStatus.Uploading.equals(dropbox.mUploadStatus)) {
            MediaCenterNetManager.MediaUploadListener mediaUploadListener
                    = MediaCenterNetManager.getMediaUploadListenerById(dropbox.mFileId, MediaCenterNetManager.UploadType.CHAT_FILE);

            if (null == mediaUploadListener) {
                mediaUploadListener = new DropboxFileUploadListener(activity, dropbox, domainId, sourceType, sourceId, currentParentId, listener);
                MediaCenterNetManager.addMediaUploadListener(mediaUploadListener);

            }
        }

    }

    /**
     * 判断该网盘文件是否属于"我"所上传的
     * @param context
     * @param dropbox
     * @return
     */
    public static boolean isMyDropbox(Context context, Dropbox dropbox) {
        if (dropbox == null && dropbox.mOwnerId != null) {
            return false;
        }
        return dropbox.mOwnerId.equalsIgnoreCase(LoginUserInfo.getInstance().getLoginUserId(context));
    }

    /**
     * 判断是否有操作权限
     * @param dropboxConfig
     * @return
     */
    public static boolean hasOpsAuth(DropboxConfig dropboxConfig) {
        return  isAdmin(BaseApplicationLike.baseContext, dropboxConfig.mSourceId) || isMyDiscussion(BaseApplicationLike.baseContext, dropboxConfig.mSourceId)|| !dropboxConfig.mReadOnly;
    }

    /**
     * 从消息体重保存文件到网盘
     * @param context
     * @param session
     * @param fileTransferChatMessage
     */
    public void saveToDropboxFromMessage(Context context, Session session, FileTransferChatMessage fileTransferChatMessage, boolean showToast) {
        Dropbox dropbox = Dropbox.convertFromChatPostMessage(context, fileTransferChatMessage);
        DomainSettingsManager.PanSettingsType panSettingsType = getPanSettingTypeBySourceId(context, dropbox.mSourceId, dropbox.mSourceType);
        if (isOverPanItemLimitAndAlert(dropbox.mFileSize, panSettingsType)) {
            return;
        }
        DropboxRequestJson json = new DropboxRequestJson();
        json.mDigest = MD5Utils.getDigest(dropbox.mLocalPath);
        json.mSize = dropbox.mFileSize;
        json.mFileId = fileTransferChatMessage.mediaId;
        json.mName = dropbox.mFileName;
        json.mParent = "";

        DropboxRequestJson.OptUser user = new DropboxRequestJson.OptUser();
        LoginUserBasic basic = LoginUserInfo.getInstance().getLoginUserBasic(context);
        user.nName = basic.mName;
        json.mUser = user;
        DropboxConfig dropboxConfig = DropboxConfigManager.getInstance().syncGetDropboxConfigBySourceId(context, session.identifier);
        DropboxAsyncNetService.getInstance().makeDropboxFileOrDir(context, session.mDomainId, Dropbox.SourceType.Discussion, session.identifier, 0, new Gson().toJson(json), dropboxConfig, false, new DropboxAsyncNetService.OnDropboxListener() {
            @Override
            public void onDropboxOpsSuccess(List<Dropbox> dropboxes) {
                if (dropboxes == null) {
                    return;
                }
                Dropbox dropbox1= dropboxes.get(0);
                dropboxConfig.mLastRefreshTime = dropbox1.mLastModifyTime;
                DropboxConfigRepository.getInstance().insertOrUpdateDropboxConfig(dropboxConfig);
                dropbox1.mLocalPath = fileTransferChatMessage.filePath;
                DropboxRepository.getInstance().insertOrUpdateDropbox(dropbox1);
                fileTransferChatMessage.dropboxFileId = dropboxes.get(0).mFileId;
                MessageRepository.getInstance().insertOrUpdateMessage(context, fileTransferChatMessage);
            }

            @Override
            public void onDropboxOpsFail(int status) {
                if (status == 204003) {

//                    AtworkToast.showResToast(R.string.no_right_ops_this_folder);
//                    return;
                }
                if (status == 204014 || status == 204015) {
                    if (showToast) {
                        toastDropboxOverlimit(context, dropbox.mSourceId, dropbox.mSourceType, dropbox.mFileSize, status);
                    }
                }
            }
        });

    }

    /**
     * 发送给联系人
     * @param  context
     * @param dropbox
     */
    public void doCommandSendToContact(Context context, Dropbox dropbox) {
        List<ChatPostMessage> messages = new ArrayList<>();
        FileData fileData = FileData.fromDropbox(dropbox);
        User LoginUser = AtworkApplicationLike.getLoginUserSync();
        long overtime = DomainSettingsManager.getInstance().handleChatFileExpiredFeature() ? TimeUtil.getTimeInMillisDaysAfter(DomainSettingsManager.getInstance().getChatFileExpiredDay()) : -1;
        FileTransferChatMessage message = FileTransferChatMessage.newFileTransferChatMessage(AtworkApplicationLike.baseContext, fileData, LoginUser, "",
                ParticipantType.User, ParticipantType.User, "", "", "", BodyType.File, "", overtime, null);
        message.mediaId = dropbox.mMediaId;
        messages.add(message);


        TransferMessageControlAction transferMessageControlAction = new TransferMessageControlAction();
        transferMessageControlAction.setSendMessageList(messages);
        transferMessageControlAction.setSendMode(TransferMessageMode.SEND);
        Intent intent = TransferMessageActivity.Companion.getIntent(BaseApplicationLike.baseContext, transferMessageControlAction);

        context.startActivity(intent);
    }

    /**
     * 通过邮件发送
     */
    public void doCommandSendEmail(Activity context, Fragment fragment, Dropbox dropbox) {
    }
    public void doCommandSendEmail(Activity context, FragmentManager fragmentManager, Dropbox dropbox) {
    }

    /**
     * 保存到网盘
     */
    public void doCommandSaveToDropbox(Activity activity, Dropbox dropbox) {
        activity.startActivityForResult(SaveToDropboxActivity.getIntent(activity, dropbox, null),REQUEST_CODE_COPY_DROPBOX );
    }

    /**
     * 查看文件属性
     */
    public void doCommandFileAttr(Fragment fragment, Dropbox dropbox) {
        DropboxFileAttrDialog dialog = new DropboxFileAttrDialog();
        dialog.setArguments(dialog.setData(dropbox));
        dialog.show(fragment.getFragmentManager(), "dropbox_attr");
    }

    /**
     * 重命名
     */
    public void doCommandRename(Activity activity, Dropbox dropbox) {
        Intent intent = DropboxModifyActivity.getIntent(activity, DropboxModifyActivity.ModifyAction.Rename,
                dropbox, dropbox.mSourceId, dropbox.mDomainId, dropbox.mSourceType, "");
        activity.startActivityForResult(intent, REQUEST_CODE_MODIFY_DROPBOX);
        activity.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }

    /**
     * 是否有权利对当前的网盘文件进行操作（主要包括是否是管理员以及是否网盘文件属于我的）
     * @param dropbox
     * @return
     */
    public boolean hasRightToDoCommand(Context context, Dropbox dropbox, String sourceId) {
        boolean isAdmin = OrganizationManager.getInstance().isLoginAdminOrgSync(context, sourceId);
        boolean isMyDropbox = isMyDropbox(context, dropbox);
        boolean isDiscussionOwner = isDiscussionOwner(context, dropbox.mSourceType, sourceId);
        return isAdmin || isMyDropbox || isDiscussionOwner;
    }

    public boolean isDiscussionOwner(Context context, Dropbox.SourceType sourceType, String sourceId) {
        if (!Dropbox.SourceType.Discussion.equals(sourceType)) {
            return false;
        }
        Discussion discussion = DiscussionManager.getInstance().queryDiscussionSync(context, sourceId);
        if (discussion == null || discussion.mOwner == null || discussion.mOwner.mUserId == null)  {
            return false;
        }
        return discussion.mOwner.mUserId.equalsIgnoreCase(LoginUserInfo.getInstance().getLoginUserId(context));
    }

    private static boolean isMyDiscussion(Context context, String sourceId) {
        Discussion discussion = DiscussionCache.getInstance().getDiscussionCache(sourceId);
        return discussion != null && discussion.mOwner != null && User.isYou(BaseApplicationLike.baseContext, discussion.mOwner.mUserId);
    }

    private static boolean isAdmin(Context context, String sourceId) {
        return OrganizationManager.getInstance().isLoginAdminOrgSync(context, sourceId);
    }


    public void popupLongClick(Activity activity, Fragment fragment, Dropbox.SourceType sourceType, String sourceId, DropboxConfig dropboxConfig, Dropbox dropbox, OnItemLongClickListener listener) {
        boolean auth = false;
        if (Dropbox.SourceType.Organization.equals(sourceType)) {
            auth = isAdmin(activity, sourceId);
        }
        if (Dropbox.SourceType.Discussion.equals(sourceType)) {
            auth = isMyDiscussion(activity, sourceId);
        }
        if (!auth && dropboxConfig.mReadOnly) {
            return;
        }

        ArrayList<String> itemList = new ArrayList<>();
        String[] array = null;
        if (dropbox.mIsDir) {
            array = activity.getResources().getStringArray(R.array.dropbox_item_dir_long_click);
        } else {
            if (!DomainSettingsManager.getInstance().handleEmailSettingsFeature()) {
                array = activity.getResources().getStringArray(R.array.dropbox_item_long_click_without_email);
            } else {
                array = activity.getResources().getStringArray(R.array.dropbox_item_long_click);
            }
        }
        itemList.addAll(Arrays.asList(array));

        if(!AtworkConfig.OPEN_DROPBOX) {
            itemList.remove(activity.getString(R.string.save_to_dropbox));
        }


        W6sSelectDialogFragment w6sSelectDialogFragment = new W6sSelectDialogFragment();
        w6sSelectDialogFragment.setData(new CommonPopSelectData(itemList, null))
                .setOnClickItemListener((position, value) ->  {

                    if (TextUtils.isEmpty(value)) {
                        return;
                    }
                    listener.onItemLongClickCallback(value, dropbox);

                })
                .show(fragment.getFragmentManager(), "TEXT_POP_DIALOG");
    }


    /**
     * 添加网盘下载监听
     * @param activity
     * @param dropbox
     * @param downloadPath
     * @param downloadListener
     */
    private void addDropboxDownloadlistener(Activity activity, Dropbox dropbox, String downloadPath, OnFileDownloadListener downloadListener) {
        if (Dropbox.DownloadStatus.Downloading.equals(dropbox.mDownloadStatus)) {
            MediaCenterNetManager.MediaDownloadListener mediaDownloadListener
                    = MediaCenterNetManager.getMediaDownloadListenerById(dropbox.mFileId);
            if (mediaDownloadListener == null) {
                mediaDownloadListener = new DropboxDownloadListener(activity, dropbox, downloadPath, downloadListener);
                MediaCenterNetManager.addMediaDownloadListener(mediaDownloadListener);
            }
        }
    }

    /**
     * 中断网盘上传
     * @param activity
     * @param dropbox
     * @param fileAdapter
     */
    public void breakDropboxUpload(Activity activity, Dropbox dropbox, DropboxFileAdapter fileAdapter) {
        AtworkToast.showToast(activity.getString(R.string.transform_cancel));
        pauseUpload(activity, dropbox);
        //delete temp data
        DropboxRepository.getInstance().deleteDropboxByFileId(dropbox.mFileId);
        activity.runOnUiThread(() -> fileAdapter.notifyDataSetChanged());
    }

    public void pauseUpload(Activity activity, Dropbox dropbox) {
        MediaCenterNetManager mediaCenterNetManager  = new MediaCenterNetManager(activity);
        mediaCenterNetManager.brokenDownloadingOrUploading(dropbox.mFileId);
    }

    /**
     * 中断网盘下载
     * @param activity
     * @param dropbox
     */
    public void breakDropboxDownload(Activity activity, Dropbox dropbox) {
        AtworkToast.showToast(activity.getString(R.string.pause_download_file));
        pauseDownload(activity, dropbox);
        dropbox.mDownloadStatus = Dropbox.DownloadStatus.Pause;
        updateDropbox(dropbox);
    }

    public void pauseDownload(Activity activity, Dropbox dropbox) {
        MediaCenterNetManager mediaCenterNetManager  = new MediaCenterNetManager(activity);
        mediaCenterNetManager.brokenDownloadingOrUploading(dropbox.mFileId);
    }

    @SuppressLint("StaticFieldLeak")
    public void getDropboxInfo(Context context, Dropbox dropbox, BaseNetWorkListener<Dropbox> listener) {
        new AsyncTask<Void, Void, HttpResult>() {
            @Override
            protected HttpResult doInBackground(Void... voids) {

                HttpResult dropboxInfo = DropboxSyncNetService.getInstance().getDropboxInfo(context, dropbox.mDomainId, dropbox.mSourceType.toString(), dropbox.mSourceId, dropbox.mFileId);

                return dropboxInfo;
            }

            @Override
            protected void onPostExecute(HttpResult httpResult) {
                if(httpResult.isRequestSuccess()) {
                    DropboxResponse dropboxResponse = (DropboxResponse) httpResult.resultResponse;
                    if(dropboxResponse.isLegal()) {
                        listener.onSuccess(dropboxResponse.result);
                        return;
                    }
                }


                NetworkHttpResultErrorHandler.handleHttpError(httpResult, listener);

            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }

    public void queryDorpboxBySortedMode(String sourceId, UserDropboxFragment.SortedMode sortedMode, OnQueryFilesListener listener) {
        new AsyncTask<Void, Void, List<Dropbox>>() {
            @Override
            protected List<Dropbox> doInBackground(Void... params) {
                if (sortedMode == UserDropboxFragment.SortedMode.Time) {
                    return DropboxRepository.getInstance().sortedDropboxByTime(sourceId);
                }
                return DropboxRepository.getInstance().sortedDropboxByName(sourceId);
            }

            @Override
            protected void onPostExecute(List<Dropbox> dropboxes) {
                super.onPostExecute(dropboxes);
                if (listener == null) {
                    return;
                }
                listener.onQueryFileList(dropboxes);
            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());

    }


    private void updateRecentFileDB(Dropbox dropbox) {
        if (dropbox == null) {
            return;
        }
        SDCardFileData.FileInfo fileInfo = new SDCardFileData.FileInfo(dropbox.mLocalPath);
        FileData fileData = fileInfo.getFileData(fileInfo);
        if (fileData == null) {
            return;
        }
        fileData.from = dropbox.mOwnerName;
        fileData.mediaId = dropbox.mMediaId;
        FileDaoService.getInstance().insertRecentFile(fileData);
    }

    public class DropboxFileUploadListener implements MediaCenterNetManager.MediaUploadListener {

        private Dropbox mDropboxFile;
        private Activity mActivity;
        private String mDomainId;
        private Dropbox.SourceType mSourceType;
        private String mSourceId;
        private OnFileUploadListener mListener;
        private String mCurrentParentId;

        public DropboxFileUploadListener(Activity activity, Dropbox dropboxFile, String domainId, Dropbox.SourceType sourceType, String sourceId, String currentParentId, OnFileUploadListener listener) {
            this.mDropboxFile = dropboxFile;
            this.mActivity = activity;
            mDomainId = domainId;
            mSourceType = sourceType;
            mSourceId = sourceId;
            mListener = listener;
            mCurrentParentId = currentParentId;
        }

        @Override
        public String getMsgId() {
            return mDropboxFile.mFileId;
        }

        @Override
        public MediaCenterNetManager.UploadType getType() {
            return MediaCenterNetManager.UploadType.CHAT_FILE;
        }

        @Override
        public void uploadSuccess(String mediaInfo) {
            mDropboxFile.mMediaId = mediaInfo;
            mDropboxFile.mUploadStatus = Dropbox.UploadStatus.Uploaded;
            //TODO...  Upload file to remote dropbox
            DropboxRequestJson json = new DropboxRequestJson();
            json.mDigest = MD5Utils.getDigest(mDropboxFile.mLocalPath);
            json.mSize = mDropboxFile.mFileSize;
            json.mFileId = mediaInfo;
            json.mName = mDropboxFile.mFileName;
            json.mParent = mCurrentParentId;
//            if (!TextUtils.isEmpty(mDropboxFile.mThumbnailMediaId)) {
//                json.mThumbnail = mDropboxFile.mThumbnailMediaId;
//            }
            String thumbnailBase = new String(Base64.encode(ImageShowHelper.compressImageForDropboxThumbnail(mDropboxFile.mLocalPath), android.util.Base64.DEFAULT));
            if (!TextUtils.isEmpty(thumbnailBase)) {
                json.mThumbnail = thumbnailBase;
            }
            DropboxRequestJson.OptUser user = new DropboxRequestJson.OptUser();
            LoginUserBasic basic = LoginUserInfo.getInstance().getLoginUserBasic(mActivity);
            user.nName = basic.mName;
            json.mUser = user;
            DropboxConfig dropboxConfig = DropboxConfigManager.getInstance().syncGetDropboxConfigBySourceId(mActivity, mSourceId);
            DropboxAsyncNetService.getInstance().makeDropboxFileOrDir(mActivity, mDomainId, mSourceType, mSourceId, 0, new Gson().toJson(json), dropboxConfig, true, new DropboxAsyncNetService.OnDropboxListener() {
                @Override
                public void onDropboxOpsSuccess(List<Dropbox> dropboxes) {
                    if (dropboxes == null) {
                        return;
                    }
                    boolean readOnly = false;
                    if (dropboxConfig != null) {
                        DropboxCache.getInstance().setDropboxConfigCache(dropboxConfig);
                        readOnly = dropboxConfig.mReadOnly;
                    }
                    updateDataAndUI(dropboxes, readOnly);
                }

                @Override
                public void onDropboxOpsFail(int status) {
                    if (status == 204014 || status == 204015) {
                        DropboxManager.getInstance().toastDropboxOverlimit(mActivity, mDropboxFile.mSourceId, mDropboxFile.mSourceType, mDropboxFile.mFileSize, status);
                    }
                    mListener.OnFileUploadFail(mDropboxFile);
                }
            });

            MediaCenterNetManager.removeMediaUploadListener(this);
        }

        @Override
        public void uploadFailed(int errorCode, String errorMsg, boolean doRefreshView) {
            if (errorCode != -99) {
                AtworkToast.showToast(mActivity.getString(R.string.upload_dropbox_file_error));
                mDropboxFile.mUploadStatus = Dropbox.UploadStatus.Fail;

                MediaCenterNetManager.removeUploadFailList(getMsgId());

            }

            if (errorCode == -99) {

            }

            mListener.OnFileUploadFail(mDropboxFile);
            MediaCenterNetManager.removeMediaUploadListener(this);
        }

        @Override
        public void uploadProgress(double progress) {
            mDropboxFile.mUploadBreakPoint = new Double(progress).longValue();
            updateDropbox(mDropboxFile);
            mListener.onFileUploadingProgress(mDropboxFile);
        }


        /**
         * 异步更新数据库和UI
         * @param dropboxes
         */
        private void updateDataAndUI(List<Dropbox> dropboxes, boolean readOnly) {
            DropboxRepository.getInstance().deleteDropboxByFileId(mDropboxFile.mFileId);
            List<Dropbox> newList = DropboxRepository.getInstance().getDropboxBySourceId(mSourceId);
            if (!ListUtil.isEmpty(dropboxes)) {
                dropboxes.get(0).mLocalPath = mDropboxFile.mLocalPath;
                DropboxManager.getInstance().batchInsertDropboxes(mActivity, mSourceId, dropboxes, readOnly);
            }
            newList.addAll(0, dropboxes);
            mListener.refreshView(newList);
        }

    }

    public class DropboxDownloadListener implements MediaCenterNetManager.MediaDownloadListener {

        private Dropbox mDropbox;
        private String mDownloadPath;
        private Activity mActivity;
        private OnFileDownloadListener mListener;

        public DropboxDownloadListener(Activity activity, Dropbox dropbox, String downloadPath, OnFileDownloadListener listener) {
            this.mDropbox = dropbox;
            this.mActivity = activity;
            mListener = listener;
            mDownloadPath = downloadPath;
        }


        @Override
        public String getMsgId() {
            return mDropbox.mFileId;
        }

        @Override
        public void downloadSuccess() {
            //update status
            mDropbox.mDownloadStatus = Dropbox.DownloadStatus.Downloaded;
            mDropbox.mLocalPath = mDownloadPath;
            //update db
            List<Dropbox> dropboxes = new ArrayList<>();
            dropboxes.add(mDropbox);
            DropboxConfig dropboxConfig = DropboxConfigManager.getInstance().syncGetDropboxConfigBySourceId(mActivity, mDropbox.mSourceId);
            DropboxManager.getInstance().batchInsertDropboxes(mActivity, mDropbox.mSourceId, dropboxes, dropboxConfig != null && dropboxConfig.mReadOnly);

            updateRecentFileDB(mDropbox);

            mListener.onFileDownloaded(mDropbox);

        }

        @Override
        public void downloadFailed(int errorCode, String errorMsg, boolean doRefreshView) {
            if (errorCode != -99) {
                AtworkToast.showToast(mActivity.getString(R.string.download_dropbox_file_error));
                mDropbox.mDownloadStatus = Dropbox.DownloadStatus.Fail;
                MediaCenterNetManager.removeUploadFailList(getMsgId());
            }
            if (errorCode == -99) {
                mDropbox.mDownloadStatus = Dropbox.DownloadStatus.Pause;
            }
            MediaCenterNetManager.removeMediaDownloadListener(this);
            updateDropbox(mDropbox);
            mListener.onFileDownloadPause(mDropbox);
//            LocalBroadcastManager.getInstance(mActivity).sendBroadcast(new Intent(UserDropboxFragment.ACTION_DATA_FRESH));

        }

        @Override
        public void downloadProgress(double progress, double value) {
            mDropbox.mDownloadBreakPoint = new Double(value).longValue();
            updateDropbox(mDropbox);
            mListener.onFileDownloadingProgress(mDropbox.mDownloadBreakPoint);
        }
    }

    /**
     * 开始网盘的文件的上传
     * @param activity
     * @param dropboxes
     * @param domainId
     * @param sourceType
     * @param sourceId
     * @param currentParentId
     * @param listener
     */
    private void startUploadFile(Activity activity, List<Dropbox> dropboxes, String domainId, Dropbox.SourceType sourceType, String sourceId, String currentParentId, OnFileUploadListener listener) {
        MediaCenterNetManager manager = new MediaCenterNetManager(activity);
        for (Dropbox dropbox : dropboxes) {
            String filePath = EncryptCacheDisk.getInstance().getOriginalFilePathAndCheck(dropbox.mLocalPath, true);
            long fileSize = new File(filePath).length();

            manager.resumeUploadFile(activity, dropbox.mFileId, dropbox.mDomainId, dropbox.mSourceType, dropbox.mSourceId, "", dropbox.mMediaId, filePath, dropbox.mUploadBreakPoint, fileSize, ((errorCode, errorMsg) -> {}));
            addDropboxUploadListener(activity, dropbox, domainId, sourceType, sourceId, currentParentId, listener);
        }
    }

    @Deprecated
    private void uploadImageThumbnail(Activity activity, Dropbox dropbox, OnImageThumbnailListener listener) {
        if (!TextUtils.isEmpty(dropbox.mThumbnailMediaId)) {
            listener.imageThumbnailUploadSuccess(dropbox);
            return;
        }
        compressImage(activity, dropbox, new OnImageFileCompressListener() {
            @Override
            public void onImagetCompressed(Dropbox data) {

//                manager.uploadFile(activity, MediaCenterNetManager.IMAGE_FILE, data.mFileId + "_thumbnail", ImageShowHelper.getOriginalPath(activity, dropbox.mFileId), true, (errorCode, errorMsg) -> {
//
//                });

                UploadFileParamsMaker uploadFileParamsMaker = UploadFileParamsMaker.newRequest()
                        .setType(MediaCenterNetManager.IMAGE_FILE)
                        .setMsgId(data.mFileId + "_thumbnail")
                        .setFilePath(ImageShowHelper.getOriginalPath(activity, dropbox.mFileId))
                        .setNeedCheckSum(true);

                MediaCenterNetManager.uploadFile(activity, uploadFileParamsMaker);

                MediaCenterNetManager.addMediaUploadListener(new MediaCenterNetManager.MediaUploadListener() {
                    @Override
                    public String getMsgId() {
                        return data.mFileId + "_thumbnail";
                    }

                    @Override
                    public MediaCenterNetManager.UploadType getType() {
                        return MediaCenterNetManager.UploadType.CHAT_IMAGE;
                    }

                    @Override
                    public void uploadSuccess(String mediaInfo) {
                        data.mThumbnailMediaId = mediaInfo;
                        listener.imageThumbnailUploadSuccess(data);
                        MediaCenterNetManager.removeMediaUploadListener(this);
                    }

                    @Override
                    public void uploadFailed(int errorCode, String errorMsg, boolean doRefreshView) {
                        MediaCenterNetManager.removeUploadFailList(data.mFileId + "_thumbnail");
                        listener.imageThumbnailUploadFail();
                    }

                    @Override
                    public void uploadProgress(double progress) {

                    }
                });
            }
        });

    }

    private void compressImage(Activity activity, Dropbox dropbox, OnImageFileCompressListener listener) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                //先旋转缩略图, 提前显示在列表中
                Bitmap thumbBitmap = ImageShowHelper.getRotateImageBitmap(dropbox.mLocalPath, true);

                //再旋转大图
                Bitmap bitmap = ImageShowHelper.getRotateImageBitmap(dropbox.mLocalPath, false);
                byte[] content = BitmapUtil.Bitmap2Bytes(bitmap);

                byte[] originalContent = ImageShowHelper.compressImageForOriginal(content);
                String path = ImageShowHelper.saveOriginalImage(activity, dropbox.mFileId, originalContent);
                bitmap.recycle();
                bitmap = null;
                thumbBitmap.recycle();
                thumbBitmap = null;
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                listener.onImagetCompressed(dropbox);
            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());

    }

    /**
     * 开始网盘文件的下载
     * @param activity
     * @param dropbox
     * @param downloadListener
     */
    private void startDownloadFile(Activity activity, Dropbox dropbox, OnFileDownloadListener downloadListener) {
        downloadListener.onFileStartDownloading(dropbox);
        MediaCenterNetManager manager = new MediaCenterNetManager(activity);
        String downloadPath = AtWorkDirUtils.getInstance().getDropboxDir(LoginUserInfo.getInstance().getLoginUserUserName(activity), getDropboxAbsPath(activity, dropbox));
        File file  = new File(downloadPath);
        if (file.exists()) {
            file.delete();
        }
//        manager.downloadFile(dropbox.mMediaId, dropbox.mFileId, downloadPath, MediaCenterHttpURLConnectionUtil.DOWNLOAD_TYPE.FILE);
        StringBuilder absPath = new StringBuilder();
        //absPath.append(downloadPath).append(".").append(dropbox.mExtension);
        absPath.append(downloadPath);
        manager.resumeDownloadFile(dropbox.mMediaId,  dropbox.mFileId, dropbox.mDomainId, dropbox.mSourceType, dropbox.mSourceId, dropbox.mDownloadBreakPoint, dropbox.mFileSize, absPath.toString());
        addDropboxDownloadlistener(activity, dropbox, absPath.toString(), downloadListener);
    }

    /**
     * 通过循环递归方式获取网盘的绝对路径
     * @param context
     * @param dropbox
     * @return
     */
    public String getDropboxAbsPath(Context context, Dropbox dropbox) {
        StringBuilder distPath = new StringBuilder(File.separator);
        if (dropbox == null) {
            return distPath.toString();
        }

        if (Dropbox.SourceType.User.equals(dropbox.mSourceType)) {
            distPath.append(context.getString(R.string.my_file)).append(File.separator);
        }
        //组织文件目录
        if (Dropbox.SourceType.Organization.equals(dropbox.mSourceType)) {
            distPath.append(context.getString(R.string.org_file)).append(File.separator);
            Organization organization = OrganizationManager.getInstance().getOrganizationSyncByOrgCode(context, dropbox.mSourceId);
            if (organization != null) {
                distPath.append(organization.getNameI18n(context)).append(File.separator).append(context.getString(R.string.public_area)).append(File.separator);
            }
        }

        if (Dropbox.SourceType.Discussion.equals(dropbox.mSourceType)) {
            Discussion discussion = DiscussionManager.getInstance().queryDiscussionSync(context, dropbox.mSourceId);
            if (discussion != null) {
                Organization organization = OrganizationManager.getInstance().getOrganizationSyncByOrgCode(context, discussion.getOrgCodeCompatible());
                if (organization != null) {
                    distPath.append(organization.getNameI18n(context)).append(File.separator).append(discussion.mName).append(File.separator);
                } else {
                    distPath.append(context.getString(R.string.group_file2)).append(File.separator).append(discussion.mName).append(File.separator);
                }
            }
        }

        String absPath = getDropboxRootPath("", dropbox);
        return distPath.append(absPath).toString();
    }

    /**
     * 递归获取网盘的主目录路径
     * @param path
     * @param dropbox
     * @return
     */
    private String getDropboxRootPath(String path, Dropbox dropbox) {
        StringBuilder stringBuilder = new StringBuilder(path);
        if (dropbox == null) {
            return stringBuilder.toString();
        }
        if (!TextUtils.isEmpty(dropbox.mParentId)) {
            stringBuilder.insert(0, File.separator + dropbox.mFileName);
            Dropbox rootDropbox = getLocalDropboxByFileId(dropbox.mParentId);
            return getDropboxRootPath(stringBuilder.toString(), rootDropbox);
        }
        return stringBuilder.insert(0, dropbox.mFileName).toString();
    }

    public DomainSettingsManager.PanSettingsType getPanSettingTypeBySourceId(Context context, String sourceId, Dropbox.SourceType sourceType) {
        DomainSettingsManager.PanSettingsType panSettingsType = DomainSettingsManager.PanSettingsType.User;
        switch (sourceType) {
            case Organization:
                panSettingsType = DomainSettingsManager.PanSettingsType.Organization;
                break;

            case Discussion:
                Discussion discussion = DiscussionManager.getInstance().queryDiscussionSync(context, sourceId);
                if (discussion == null) {
                    break;
                }

                panSettingsType = discussion.isInternalDiscussion() ? DomainSettingsManager.PanSettingsType.InternalDiscussion : DomainSettingsManager.PanSettingsType.UserDiscussion;
                break;

            case User:
                panSettingsType = DomainSettingsManager.PanSettingsType.User;
                break;
        }
        return panSettingsType;
    }

    public String getPanUsedAndTotalLimit(String used, DomainSettingsManager.PanSettingsType panSettingsType) {
        String resultText = "";
        if (TextUtils.isEmpty(used)) {
            return resultText;
        }
        String total = "";
        String usedText = "";
        try {
            usedText = FileHelper.getFileSizeStr(Long.valueOf(used));
            switch (panSettingsType) {

                case User:
                    total = FileHelper.getFileSizeStr(DomainSettingsManager.getInstance().getUserTotalLimit());
                    break;

                case Organization:
                    total = FileHelper.getFileSizeStr(DomainSettingsManager.getInstance().getOrgTotalLimit());
                    break;

                case InternalDiscussion:
                    total = FileHelper.getFileSizeStr(DomainSettingsManager.getInstance().getInternalDiscussionTotalLimit());
                    break;

                case UserDiscussion:
                    total = FileHelper.getFileSizeStr(DomainSettingsManager.getInstance().getUserDiscussionTotalLimit());
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return resultText;
        }

        return getResourceString(R.string.drop_box_rent_space, usedText, total) ;
    }

    public boolean isOverPanItemLimitAndAlert(long fileSize, DomainSettingsManager.PanSettingsType panSettingsType) {
        long limit = Long.MAX_VALUE;
        String tip1 = "";
        switch (panSettingsType) {

            case User:
                tip1 = getResourceString(R.string.user_dropbox);
                limit = DomainSettingsManager.getInstance().getUserItemLimit();
                break;

            case Organization:
                tip1 = getResourceString(R.string.organization_dropbox);
                limit = DomainSettingsManager.getInstance().getOrgItemLimit();
                break;

            case InternalDiscussion:
                tip1 = getResourceString(R.string.internal_discussion_dropbox);
                limit = DomainSettingsManager.getInstance().getInternalDiscussionItemLimit();
                break;

            case UserDiscussion:
                tip1 = getResourceString(R.string.user_discussion_dropbox);
                limit = DomainSettingsManager.getInstance().getUserDiscussionItemLimit();
                break;
        }
        if (fileSize > limit) {
            AtworkToast.showResToast(R.string.dropbox_item_over_limit, tip1, FileHelper.getFileSizeStr(limit));
        }
        return fileSize > limit;
    }

    public String getDropboxPanSettingName(Context context, String sourceId, Dropbox.SourceType sourceType) {
        DomainSettingsManager.PanSettingsType panSettingsType = DropboxManager.getInstance().getPanSettingTypeBySourceId(context, sourceId, sourceType);
        String name = "";
        switch (panSettingsType) {
            case User:
                name = getResourceString(R.string.user_dropbox);
                break;

            case Organization:
                name = getResourceString(R.string.organization_dropbox);
                break;

            case InternalDiscussion:
                name = getResourceString(R.string.internal_discussion_dropbox);
                break;

            case UserDiscussion:
                name = getResourceString(R.string.user_discussion_dropbox);
                break;

        }
        return name;
    }

    public void toastDropboxOverlimit(Context context, String sourceId, Dropbox.SourceType sourceType, long fileSize, int status) {
        if (status == 204015) {
            DropboxManager.getInstance().isOverPanItemLimitAndAlert(fileSize, DropboxManager.getInstance().getPanSettingTypeBySourceId(context, sourceId, sourceType));
            return;
        }
        String name = DropboxManager.getInstance().getDropboxPanSettingName(context, sourceId, sourceType);
        if (status == 204014) {
            AtworkToast.showResToast(R.string.dropbox_already_fully, name);
            return;
        }
    }



    /**
     * 网盘下载监听
     */
    public interface OnFileDownloadListener {
        void onFileStartDownloading(Dropbox dropbox);
        void onFileDownloadingProgress(long progress);
        void onFileDownloaded(Dropbox dropbox);
        void onFileDownloadPause(Dropbox dropbox);
    }

    /**
     * 网盘上传监听
     */
    public interface OnFileUploadListener {
        void onFileStartUploading(List<Dropbox> newList);
        void onFileUploadingProgress(Dropbox dropbox);
        void refreshView(List<Dropbox> dropboxList);
        void OnFileUploadFail(Dropbox dropbox);
    }

    /**
     * 网盘查询监听
     */
    public interface OnQueryFilesListener {
        void onQueryFileList(List<Dropbox> dropboxList);
    }

    public interface GetDropboxMediaIdListener {
        void getDropboxMediaIdResult(String mediaId);
    }

    /**
     * 网盘搜索监听
     */
    public interface OnSearchOnlineListener {
        void onSearchOnlineResult(String searchKey, List<Dropbox> dropboxList);
    }

    public interface OnItemLongClickListener {
        void onItemLongClickCallback(String value, Dropbox dropbox);
    }

    public interface OnImageThumbnailListener {
        void imageThumbnailUploadSuccess(Dropbox dropbox);
        void imageThumbnailUploadFail();
    }

    public interface OnImageFileCompressListener {
        void onImagetCompressed(Dropbox dropbox);
    }

}