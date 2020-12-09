
package com.foreveross.atwork.modules.chat.component;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import com.foreverht.db.service.daoService.FileDaoService;
import com.foreverht.workplus.ui.component.popUpView.W6sPopUpView;
import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.UrlConstantManager;
import com.foreveross.atwork.api.sdk.dropbox.DropboxAsyncNetService;
import com.foreveross.atwork.api.sdk.dropbox.requestJson.FileTranslateRequest;
import com.foreveross.atwork.api.sdk.net.MediaCenterHttpURLConnectionUtil;
import com.foreveross.atwork.api.sdk.net.model.DownloadFileParamsMaker;
import com.foreveross.atwork.api.sdk.net.model.UploadFileParamsMaker;
import com.foreveross.atwork.api.sdk.upload.MediaCenterNetManager;
import com.foreveross.atwork.component.ProgressDialogHelper;
import com.foreveross.atwork.component.viewPager.ViewPagerFixed;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.manager.DomainSettingsManager;
import com.foreveross.atwork.infrastructure.model.WebViewControlAction;
import com.foreveross.atwork.infrastructure.model.dropbox.Dropbox;
import com.foreveross.atwork.infrastructure.model.file.FileData;
import com.foreveross.atwork.infrastructure.model.file.SDCardFileData;
import com.foreveross.atwork.infrastructure.newmessage.ChatStatus;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.ChatSendType;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.FileStatus;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.FileTransferChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.MultipartChatMessage;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.AtWorkDirUtils;
import com.foreveross.atwork.infrastructure.utils.DensityUtil;
import com.foreveross.atwork.infrastructure.utils.FileUtil;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.infrastructure.utils.LogUtil;
import com.foreveross.atwork.infrastructure.utils.ScreenUtils;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.TimeUtil;
import com.foreveross.atwork.infrastructure.utils.encryption.EncryptCacheDisk;
import com.foreveross.atwork.infrastructure.utils.statusbar.StatusBarUtil;
import com.foreveross.atwork.manager.DropboxManager;
import com.foreveross.atwork.modules.app.route.UrlRouteHelper;
import com.foreveross.atwork.modules.chat.dao.ChatDaoService;
import com.foreveross.atwork.modules.chat.data.ChatSessionDataWrap;
import com.foreveross.atwork.modules.chat.fragment.ChatDetailFragment;
import com.foreveross.atwork.modules.chat.service.FileDownloadNotifyService;
import com.foreveross.atwork.modules.chat.util.ChatDetailExposeBroadcastSender;
import com.foreveross.atwork.modules.chat.util.MultipartMsgHelper;
import com.foreveross.atwork.modules.dropbox.activity.SaveToDropboxActivity;
import com.foreveross.atwork.modules.dropbox.adapter.TranslateViewPagerAdapter;
import com.foreveross.atwork.modules.dropbox.fragment.UserDropboxFragment;
import com.foreveross.atwork.modules.file.util.FileMediaTypeUtil;
import com.foreveross.atwork.modules.group.activity.TransferMessageActivity;
import com.foreveross.atwork.modules.group.module.TransferMessageControlAction;
import com.foreveross.atwork.modules.group.module.TransferMessageMode;
import com.foreveross.atwork.modules.voip.utils.VoipHelper;
import com.foreveross.atwork.utils.AtworkToast;
import com.foreveross.atwork.utils.AtworkUtil;
import com.foreveross.atwork.utils.ChatMessageHelper;
import com.foreveross.atwork.utils.IntentUtil;
import com.foreveross.atwork.utils.OfficeHelper;
import com.foreveross.atwork.utils.watermark.WaterMarkHelper;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import kotlin.collections.CollectionsKt;

import static com.foreveross.atwork.modules.file.activity.OfficeViewActivity.VIEW_FROM_FILE_DETAIL;
import static com.foreveross.atwork.modules.file.activity.OfficeViewActivity.VIEW_FROM_OTHER;

/**
 * Created by lingen on 15/4/28.
 * Description:
 */
public class FileStatusView extends LinearLayout {

    public static final String REDOWNLOAD = ".redownload";
    public static final String BUNDLE_FILE_MESSAGE = "bundle_file_message";

    //文件预先下载的临界大小，单位为“MB”
    private static final int MAXIMUMDOWNLOADCAPACITY = 10;

    //下载原文件
    private static String RE_DOWNLOAD_FILE = BaseApplicationLike.baseContext.getResources().getString(R.string.file_transfer_re_download_file);
    //用第三方应用打开
    private static String OPEN_WITH_OTHER = BaseApplicationLike.baseContext.getResources().getString(R.string.open_by_other_app);
    //原生本地打开
    private static String PREVIEW_LOCAL = BaseApplicationLike.baseContext.getResources().getString(R.string.preview_file_in_detail_view);

    //重新发送
    private static String RE_SEND_FILE = BaseApplicationLike.baseContext.getResources().getString(R.string.file_transfer_re_send_file);
    private static String RE_SEND_AGAIN = BaseApplicationLike.baseContext.getResources().getString(R.string.file_transfer_re_send_cancel_file);
    //文件详情
    private static String FILE_TITLE = BaseApplicationLike.baseContext.getResources().getString(R.string.file_transfer_file_title);
    private static String TITLE_DOWNLOADING = BaseApplicationLike.baseContext.getResources().getString(R.string.file_transfer_status_downloading);
    private static String TITLE_SENDING = BaseApplicationLike.baseContext.getResources().getString(R.string.file_transfer_status_sending);

    private RelativeLayout mRlMainArea;
    //文件图标
    private ImageView mIconView;
    //文件名
    private TextView mFileNameView;

    //发送文件或恢复发送   接收文件或恢复接收
    private TextView mSendOrDownloadButton;

    private TextView mOpenLocalButton;

    //用第三方应用打开按扭
    private TextView mOpenOtherAppButton;
    private TextView mShareOtherAppButton;


    private TextView mTvActionWithOtherTip;

    //进度条VIEW
    private View mProgressView;

    private TextView mTvDownloadLabel;
    //下载文件标题
    private TextView mDownloadText;

    //进度条
    private ProgressBar mProgressBar;

    //取消上传或下载
    private ImageView mCancelView;

    private FileTransferChatMessage mFileTransferChatMessage;

    private MultipartChatMessage mMultipartChatMessage;

    private String mSessionId;

    private ImageView mIvMore;

    private boolean mReDownloading;

    private Activity mActivity;

    public boolean mIsViewPause = false;

    private View mLlLocalPreviewAndHandleButtonGroups;

    private View mLlOnlinePreviewGroups;
    private TextView mTvSupportPreviewOnline;
    private TextView mTvPreviewOnline;
    private TextView mTvNotSupportPreviewOnline;
    private TextView mTvFileSize;

    private View mFileDetailView;

    private ViewPagerFixed mTranslateViewPager;
    private TranslateViewPagerAdapter mTranslateViewPagerAdapter;

    private ProgressDialogHelper mProgressDialogHelper;

    private View mWatermarkView;

    private List<String> mTranslateList;

    private TextView mTvOverdueTime;
    private TextView mTvOverdueHint;

    private List<TextView> mControlFunctionBtnList = new ArrayList<>();

    private boolean mLoadMore = false;
    private int mIntentType = VIEW_FROM_OTHER;

    public FileStatusView(Activity context) {
        super(context);

        setOrientation(LinearLayout.VERTICAL);

        mActivity = context;
        initView();
//        shouldAutoPreview();
        registerListener();
        mIsViewPause = false;
    }

    public void onResume() {
        RE_DOWNLOAD_FILE = BaseApplicationLike.baseContext.getResources().getString(R.string.file_transfer_re_download_file);
        //用第三方应用打开
        OPEN_WITH_OTHER = BaseApplicationLike.baseContext.getResources().getString(R.string.open_by_other_app);

        //使用原生本地打开
        PREVIEW_LOCAL = BaseApplicationLike.baseContext.getResources().getString(R.string.preview_file_in_detail_view);

        //重新发送
        RE_SEND_FILE = BaseApplicationLike.baseContext.getResources().getString(R.string.file_transfer_re_send_file);
        RE_SEND_AGAIN = BaseApplicationLike.baseContext.getResources().getString(R.string.file_transfer_re_send_cancel_file);
        //文件详情
        FILE_TITLE = BaseApplicationLike.baseContext.getResources().getString(R.string.file_transfer_file_title);
        TITLE_DOWNLOADING = BaseApplicationLike.baseContext.getResources().getString(R.string.file_transfer_status_downloading);
        TITLE_SENDING = BaseApplicationLike.baseContext.getResources().getString(R.string.file_transfer_status_sending);
        mIsViewPause = false;
    }


    /**
     * Description:更新最近文件本地数据库
     * @param message 文件信息
     * @param isDownLoad 是否已经下载
     */
    private void updateRecentFileDB(FileTransferChatMessage message, boolean isDownLoad) {
        SDCardFileData.FileInfo fileInfo = new SDCardFileData.FileInfo(message.filePath);
        FileData fileData = fileInfo.getFileData(fileInfo);
        if (fileData == null) {
            return;
        }
        fileData.from = message.from;
        fileData.to = message.to;
        fileData.mediaId = message.mediaId;
        if (isDownLoad) {
            fileData.isDownload = 1;
        }
        FileDaoService.getInstance().insertRecentFile(fileData);
    }

    /**
     * Description:更新发送监听器，判断文件状态
     */
    private void refreshSendingListener() {

        if (FileStatus.SENDING.equals(mFileTransferChatMessage.fileStatus)) {
            MediaCenterNetManager.removeMediaUploadListenerById(getKeyDeliveryId(), MediaCenterNetManager.UploadType.STATUS_VIEW_FILE);

            FileStatusViewUploadListener mediaUploadListener = new FileStatusViewUploadListener();
            MediaCenterNetManager.addMediaUploadListener(mediaUploadListener);
        }
    }

    /**
     * Description:更新下载监听器，判断文件状态
     */
    private void refreshDownloadListener() {

        if (FileStatus.DOWNLOADING.equals(mFileTransferChatMessage.fileStatus) || mReDownloading) {
            MediaCenterNetManager.removeMediaDownloadListenerById(getKeyDeliveryId());

            FileStatusViewDownloadListener mediaDownloadListener = new FileStatusViewDownloadListener();
            MediaCenterNetManager.addMediaDownloadListener(mediaDownloadListener);
            ChatSessionDataWrap.getInstance().updateChatInFileStreaming(mSessionId, mFileTransferChatMessage.deliveryId);


        }
    }

    /**
     * Description:监听器
     */
    private void registerListener() {

        mOpenLocalButton.setOnClickListener(v -> {
            previewLocal(VIEW_FROM_FILE_DETAIL);
        });

        mIvMore.setOnClickListener(v -> {
            handleMoreBtnClick();
        });

        mSendOrDownloadButton.setOnClickListener(v -> {
            FileStatus fileStatus = mFileTransferChatMessage.fileStatus;
            //重新发送
            if (FileStatus.SEND_CANCEL.equals(fileStatus) || FileStatus.SEND_FAIL.equals(fileStatus)
                    || FileStatus.NOT_SENT.equals(fileStatus)) {
                reSending();
                refreshView();

                //通知聊天列表注册 listener
                Intent intent = new Intent(ChatDetailFragment.ADD_SENDING_LISTENER);
                intent.putExtra(BUNDLE_FILE_MESSAGE, mFileTransferChatMessage);
                LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).sendBroadcast(intent);
                return;
            }

            //下载原文件
            if (FileStatus.DOWNLOAD_CANCEL.equals(fileStatus) || FileStatus.DOWNLOAD_FAIL.equals(fileStatus)
                    || FileStatus.NOT_DOWNLOAD.equals(fileStatus) || FileStatus.SENDED.equals(fileStatus) || !isFileExist()) {
                downloadFile();
                refreshView();
                return;
            }


        });

        mOpenOtherAppButton.setOnClickListener(v -> {
            if (StringUtils.isEmpty(mFileTransferChatMessage.filePath)) {
                return;
            }
            if (mFileTransferChatMessage.filePath.toLowerCase().endsWith(".apk")) {

                installApk();

            } else {
                if (VoipHelper.isHandlingVoipCall() && (FileData.FileType.File_Audio == mFileTransferChatMessage.fileType || FileData.FileType.File_Video == mFileTransferChatMessage.fileType)) {
                    AtworkToast.showResToast(R.string.alert_is_handling_voip_meeting_click_voip);

                } else {

                    previewOtherApp();

                }
            }

        });

        mShareOtherAppButton.setOnClickListener(v -> {
            if (StringUtils.isEmpty(mFileTransferChatMessage.filePath)) {
                return;
            }

            shareOtherApp();
        });

        mCancelView.setOnClickListener(v -> {

            if (mFileTransferChatMessage.fileStatus.equals(FileStatus.PAUSE)) {
                downloadFile();
                mCancelView.setImageResource(R.mipmap.icon_search_red_del_hover);
                return;
            }
            if (mFileTransferChatMessage.fileStatus.equals(FileStatus.DOWNLOADING)) {
                mFileTransferChatMessage.fileStatus = FileStatus.PAUSE;
                mCancelView.setImageResource(R.mipmap.icon_file_download_continue);
            }

            MediaCenterNetManager mediaCenterNetManager = new MediaCenterNetManager(BaseApplicationLike.baseContext);

            //如果是接收(合并转发消息的 chatSendType 为 null, 此时视为RECEIVER 来处理)
            if (ChatSendType.RECEIVER.equals(mFileTransferChatMessage.chatSendType) || null == mFileTransferChatMessage.chatSendType) {

                if (!mReDownloading && FileStatus.DOWNLOADING.equals(mFileTransferChatMessage.fileStatus)) {
                    refreshChatItemView();
                }
            }
            //如果是发送
            else if (ChatSendType.SENDER.equals(mFileTransferChatMessage.chatSendType)) {
                if (!mReDownloading) {

                    //当已经是已经发送但未下载的情况(转发或者同步消息时)
                    refreshChatItemView();
                }

            }

            mediaCenterNetManager.brokenDownloadingOrUploading(getKeyDeliveryId());

            refreshView();
        });


        mTvPreviewOnline.setOnClickListener(view -> {
            if(OfficeHelper.shouldOffice365Preview()) {
                String url = String.format(UrlConstantManager.getInstance().officePreviewUrl(), MediaCenterNetManager.getDownloadUrl(getContext(), mFileTransferChatMessage.mediaId));
                UrlRouteHelper.routeUrl(getContext(), WebViewControlAction.newAction().setUrl(url));
                return;
            }


            mProgressDialogHelper.show();

            FileTranslateRequest request = new FileTranslateRequest();
            request.mFileType = mFileTransferChatMessage.fileType.getString();
            request.mMediaId =mFileTransferChatMessage.mediaId;

            loadTranslateData(request);
        });
    }

    /**
     * Description:在线预览
     * @param request
     */
    private void loadTranslateData(final FileTranslateRequest request) {
        DropboxManager.getInstance().translateFile(mActivity, request, new DropboxAsyncNetService.OnFileTranslateListener() {
            @Override
            public void onFileTranslateSuccess(int totalPage, List<String> translateList) {

                mProgressDialogHelper.dismiss();
                if (ListUtil.isEmpty(translateList)) {
                    return;
                }

                showPreview(request, totalPage, translateList);
            }

            @Override
            public void networkFail(int errorCode, String errorMsg) {
                mProgressDialogHelper.dismiss();
                if (errorCode == 209531) {
                    mLoadMore = false;
                    return;
                }
                if (errorCode == 202401) {
                    AtworkToast.showResToast(R.string.file_transform_fail);
                    return;
                }
                AtworkToast.showResToast(R.string.dropbox_network_error);
            }
        });
    }

    /**
     * Description:尝试下载原文件
     */
    private void tryDownloadFile() {
        FileStatus fileStatus = mFileTransferChatMessage.fileStatus;

        if(FileStatus.SENDING.equals(fileStatus)) {
            return;
        }


        if(FileStatus.DOWNLOADING.equals(fileStatus)) {
            return;
        }

        if (!isFileExist()) {
            mFileTransferChatMessage.progress = 0;
            mFileTransferChatMessage.breakPoint = 0;
            ChatDaoService.getInstance().updateMessage(mFileTransferChatMessage);
        }

        if (TimeUtil.isOverdueDate(TimeUtil.getCurrentTimeInMillis(), mFileTransferChatMessage.expiredTime)) {
            return;
        }

        if(!DomainSettingsManager.getInstance().handleChatFileDownloadEnabled()) {
            return;
        }

        //下载原文件
        if (FileStatus.DOWNLOAD_CANCEL.equals(fileStatus) || FileStatus.DOWNLOAD_FAIL.equals(fileStatus)
                || FileStatus.NOT_DOWNLOAD.equals(fileStatus)  || !isFileExist()) {
            downloadFile();
            refreshView();
            return;
        }
    }

    /**
     * Description:初始化更多按钮的点击弹窗
     */
    private void handleMoreBtnClick() {

        final W6sPopUpView popUpView = new W6sPopUpView(getContext());

        if (DomainSettingsManager.getInstance().handleChatFileTransferEnabled()) {
            popUpView.addPopItem(-1, R.string.forwarding_item, 0);
        }
        popUpView.setPopItemOnClickListener((title, pos) -> {
            //转发
            if (title.equals(getResources().getString(R.string.forwarding_item))) {
                List<ChatPostMessage> chatPostMessages = new ArrayList<>();
                chatPostMessages.add(mFileTransferChatMessage);

                TransferMessageControlAction transferMessageControlAction = new TransferMessageControlAction();
                transferMessageControlAction.setSendMessageList(chatPostMessages);
                transferMessageControlAction.setSendMode(TransferMessageMode.FORWARD);
                Intent intent = TransferMessageActivity.Companion.getIntent(BaseApplicationLike.baseContext, transferMessageControlAction);


                FileStatusView.this.getContext().startActivity(intent);
            }

            if (pos == 1) {
                Dropbox dropbox = Dropbox.convertFromChatPostMessage(mActivity, mFileTransferChatMessage);
                Intent intent = SaveToDropboxActivity.getIntent(mActivity, dropbox, mFileTransferChatMessage);
                mActivity.startActivityForResult(intent, UserDropboxFragment.REQUEST_CODE_COPY_DROPBOX);
            }

            popUpView.dismiss();
        });

        popUpView.pop(mIvMore);
    }


    /**
     * Description; 在线预览
     * @param request
     * @param totalPage
     * @param translateList
     */
    private void showPreview(FileTranslateRequest request, int totalPage,  List<String> translateList) {
        if (AtworkConfig.SCREEN_ORIENTATION_USER_SENSOR) {
            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
        }

        if (DomainSettingsManager.getInstance().handleChatFileWatermarkFeature()) {
            WaterMarkHelper.setDiscussionWatermark(mActivity, mWatermarkView, mSessionId);
        }

        mTranslateViewPager.setVisibility(VISIBLE);
        mFileDetailView.setVisibility(GONE);
        if (mTranslateViewPagerAdapter == null) {
            mTranslateViewPagerAdapter = new TranslateViewPagerAdapter(mActivity, translateList);
            mTranslateViewPager.setAdapter(mTranslateViewPagerAdapter);
        } else {
            mTranslateViewPagerAdapter.setList(translateList);
        }
        if (mTranslateViewPagerAdapter.getCount() < totalPage) {
            mLoadMore = true;
            request.mSkip = mTranslateViewPagerAdapter.getCount();
        } else{
            mLoadMore = false;
        }

        mTranslateViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                if (i == (mTranslateViewPagerAdapter.getCount() - 1) && mLoadMore) {
                    loadTranslateData(request);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    /**
     * Description:使用其他应用进行预览
     */
    private void previewOtherApp() {

        EncryptCacheDisk.getInstance().getOriginalFilePathAndCheck(mFileTransferChatMessage.filePath, false, fileName -> {
            IntentUtil.previewIntent(getContext(), fileName, mFileTransferChatMessage.fileType.getFileType());
        });

    }

    /**
     * Description:分享到其他应用上
     */
    private void shareOtherApp() {

        EncryptCacheDisk.getInstance().getOriginalFilePathAndCheck(mFileTransferChatMessage.filePath, false, fileName -> {
            IntentUtil.shareIntent(getContext(), fileName);
        });

    }


    /**
     * Description:本地预览
     * TODO:
     */
    private void previewLocal(int intentType) {
        mIntentType = VIEW_FROM_OTHER;
        if(VIEW_FROM_OTHER != intentType){
            EncryptCacheDisk.getInstance().getOriginalFilePathAndCheck(mFileTransferChatMessage.filePath, false, fileName -> {

                OfficeHelper.previewByX5(getContext(), fileName, mSessionId, mFileTransferChatMessage, intentType);

            });
        }

    }

    /**
     * Description:安装软件
     */
    private void installApk() {

        EncryptCacheDisk.getInstance().getOriginalFilePathAndCheck(mFileTransferChatMessage.filePath, false, fileName -> IntentUtil.installApk(getContext(), mFileTransferChatMessage.filePath));

    }

    /**
     * 刷新 listview 并且更新数据库
     */
    private void refreshChatItemView() {
        updateFileMsgData();
        notifyRefreshProgressUI();
    }

    public void notifyRefreshProgressUI() {
        if (null != mFileTransferChatMessage && mFileTransferChatMessage.isBingReplyType()) {
            return;
        }

        ChatDetailExposeBroadcastSender.refreshMessageListViewUI();
    }

    private void updateFileMsgData() {
        if (null == mMultipartChatMessage) {
            ChatDaoService.getInstance().insertOrUpdateMessage(BaseApplicationLike.baseContext, mFileTransferChatMessage);
        } else {
            MultipartMsgHelper.updateMultipartMsgFileMsgStatus(BaseApplicationLike.baseContext, mMultipartChatMessage, mFileTransferChatMessage);
        }
    }

    /**
     * Descriiption:重新发送
     */
    private void reSending() {
        mFileTransferChatMessage.progress = 0;
//        mediaCenterNetManager.uploadFile(getContext(), MediaCenterNetManager.COMMON_FILE, getKeyDeliveryId(), mFileTransferChatMessage.filePath, true, (errorCode, errorMsg) -> ErrorHandleUtil.handleError(ErrorHandleUtil.Module.Media, errorCode, errorMsg));

        UploadFileParamsMaker uploadFileParamsMaker = UploadFileParamsMaker.newRequest()
                .setType(MediaCenterNetManager.COMMON_FILE)
                .setMsgId(getKeyDeliveryId())
                .setFilePath(mFileTransferChatMessage.filePath)
                .setNeedCheckSum(true);

        MediaCenterNetManager.uploadFile(BaseApplicationLike.baseContext, uploadFileParamsMaker);

        mFileTransferChatMessage.fileStatus = FileStatus.SENDING;
        mFileTransferChatMessage.chatStatus = ChatStatus.Sending;

        refreshChatItemView();
        //重新开启下载监听
        refreshSendingListener();
    }


    /**
     * Description:下载文件
     */
    private void downloadFile() {
        AndPermission
                .with(mActivity)
                .runtime()
                .permission(Permission.WRITE_EXTERNAL_STORAGE, Permission.READ_EXTERNAL_STORAGE)
                .onGranted(data -> {
                    if (isFileNotValid()) {
                        return;
                    }

                    mFileTransferChatMessage.fileStatus = FileStatus.DOWNLOADING;

                    refreshView();
                    refreshChatItemView();
                    //重新开启下载监听
                    refreshDownloadListener();
                    MediaCenterNetManager mediaCenterNetManager = new MediaCenterNetManager(BaseApplicationLike.baseContext);

                    if (mFileTransferChatMessage.breakPoint == -1) {
                        mFileTransferChatMessage.filePath = FileUtil.getFileInfoByChecking(mFileTransferChatMessage.name, AtWorkDirUtils.getInstance().getChatFiles(mActivity)).filePath;
                    } else {
                        mFileTransferChatMessage.filePath = AtWorkDirUtils.getInstance().getChatFiles(mActivity) + mFileTransferChatMessage.name;
                    }
                    File tempFile = new File(mFileTransferChatMessage.filePath);
                    if (tempFile.exists()) {
                        mFileTransferChatMessage.breakPoint = tempFile.length();
                    }
                    mediaCenterNetManager.downloadFile(
                            DownloadFileParamsMaker.Companion.newRequest().setDownloadId(getKeyDeliveryId()).setMediaId(mFileTransferChatMessage.mediaId)
                                    .setDownloadPath(mFileTransferChatMessage.filePath).setDownloadType(MediaCenterHttpURLConnectionUtil.DOWNLOAD_TYPE.FILE)
                                    .setDownloadPos(mFileTransferChatMessage.breakPoint).setFileSize(mFileTransferChatMessage.size)
                    );
                })
                .onDenied(data ->  AtworkUtil.popAuthSettingAlert(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE))
                .start();

    }

    private boolean isFileNotValid() {
        if (mFileTransferChatMessage.mediaId == null) {
            AtworkToast.showToast(getResources().getString(R.string.not_allowed_download));
            return true;
        }

        if (TimeUtil.isOverdueDate(TimeUtil.getCurrentTimeInMillis(), mFileTransferChatMessage.expiredTime)) {
            return true;
        }

        if(!DomainSettingsManager.getInstance().handleChatFileDownloadEnabled()) {
            return true;
        }

        return false;
    }


    private void initView() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.fragment_file_transfer, this);
        mRlMainArea = view.findViewById(R.id.rl_main_area);
        mIconView = view.findViewById(R.id.file_transfer_file_icon);
        mFileNameView = view.findViewById(R.id.file_transfer_file_name);
        mSendOrDownloadButton = view.findViewById(R.id.file_transfer_re_send);
        mOpenLocalButton = view.findViewById(R.id.file_transfer_open_local);
        mOpenOtherAppButton = view.findViewById(R.id.file_transfer_open_with_others);
        mShareOtherAppButton = view.findViewById(R.id.file_transfer_share_with_others);
        mTvActionWithOtherTip = view.findViewById(R.id.tv_action_with_other_tip);
        mTvDownloadLabel = view.findViewById(R.id.tv_label_downloading);
        mDownloadText = view.findViewById(R.id.file_transfer_download_text);
        mProgressBar = view.findViewById(R.id.file_transfer_download_progress);
        mProgressView = view.findViewById(R.id.file_transfer_download_progress_view);
        mCancelView = view.findViewById(R.id.file_transfer_cancel);
        ((TextView) (view.findViewById(R.id.title_bar_chat_detail_name))).setText(FILE_TITLE);
        mIvMore = view.findViewById(R.id.title_bar_main_more_btn);
        mIvMore.setImageResource(R.mipmap.icon_more_dark);

        mFileDetailView = view.findViewById(R.id.file_detail_group);

        mLlLocalPreviewAndHandleButtonGroups = view.findViewById(R.id.button_group);
        mTvPreviewOnline = view.findViewById(R.id.preview_online);
        mLlOnlinePreviewGroups = view.findViewById(R.id.file_prieview_group);
        mTvSupportPreviewOnline = view.findViewById(R.id.is_support_preview_online);
        mTvNotSupportPreviewOnline = view.findViewById(R.id.not_support_preview_online);
        mTvFileSize = view.findViewById(R.id.file_size);

        mTranslateViewPager = view.findViewById(R.id.preview_file_pager);

        mProgressDialogHelper = new ProgressDialogHelper(mActivity);

        mWatermarkView = view.findViewById(R.id.watermark_view);

        mTvOverdueTime = view.findViewById(R.id.overdue_time);
        mTvOverdueHint = view.findViewById(R.id.overdue_time_hint);

        int height = ScreenUtils.getScreenHeight(AtworkApplicationLike.baseContext) - DensityUtil.dip2px(48) - StatusBarUtil.getStatusBarHeight(AtworkApplicationLike.baseContext);
//        ViewUtil.setHeight(mRlMainArea, height);
        mRlMainArea.setMinimumHeight(height * 6 / 7);


        mControlFunctionBtnList.add(mTvPreviewOnline);
        mControlFunctionBtnList.add(mSendOrDownloadButton);
        mControlFunctionBtnList.add(mOpenLocalButton);
        mControlFunctionBtnList.add(mOpenOtherAppButton);
        mControlFunctionBtnList.add(mShareOtherAppButton);


    }

    private void setControlBtnViewVisibilityAndRefreshBg(View view, int visibility) {
        view.setVisibility(visibility);
        refreshControlFunctionBtnBg();
    }

    private  void refreshControlFunctionBtnBg() {
        List<TextView> showControlFunctionBtnList = CollectionsKt.filter(mControlFunctionBtnList, textView -> {
            if(mTvPreviewOnline == textView) {
                return mLlOnlinePreviewGroups.isShown() && mTvPreviewOnline.isShown();
            }
            return textView.isShown();
        });

        LogUtil.e("showControlFunctionBtnList size -> " + showControlFunctionBtnList.size());

        for(int i = 0; i < showControlFunctionBtnList.size(); i++) {

            TextView btnControl = showControlFunctionBtnList.get(i);
            if(0 == i) {
                btnControl.setBackgroundResource(R.drawable.shape_common_blue);
                btnControl.setTextColor(ContextCompat.getColor(AtworkApplicationLike.baseContext, R.color.white));
            } else {
                btnControl.setBackgroundResource(R.drawable.shape_common_white);
                btnControl.setTextColor(ContextCompat.getColor(AtworkApplicationLike.baseContext, R.color.common_text_color));
            }
        }

    }

    private void tryShowActionWithOtherTipView() {
        if(!StringUtils.isEmpty(mTvActionWithOtherTip.getText().toString())) {
            mTvActionWithOtherTip.setVisibility(VISIBLE);
        }
    }

    /**
     * Description:改变文件的状态
     */
    private void changeFileStatus() {
        boolean showOverdue = mFileTransferChatMessage.expiredTime > 0;
        boolean isOverdue = TimeUtil.isOverdueDate(TimeUtil.getCurrentTimeInMillis(), mFileTransferChatMessage.expiredTime);

        mTvOverdueTime.setVisibility(!shouldVisibleOpenLocalButton() && showOverdue && !isFileExist() ? VISIBLE : GONE);
        mTvOverdueTime.setText(AtworkApplicationLike.getResourceString(R.string.file_expires, TimeUtil.getStringForMillis(mFileTransferChatMessage.expiredTime, TimeUtil.YYYY_MM_DD)));
        mTvOverdueHint.setVisibility(!shouldVisibleOpenLocalButton() && isOverdue ? VISIBLE : GONE);

        mTvSupportPreviewOnline.setText(OfficeHelper.isOnlineSupportType(mFileTransferChatMessage.fileType) ?
                AtworkApplicationLike.getResourceString(R.string.preview_tip) : AtworkApplicationLike.getResourceString(R.string.not_support_preview_online));

        if(OfficeHelper.isOnlineSupportType(mFileTransferChatMessage.fileType)){
            mTvSupportPreviewOnline.setVisibility(GONE);

        }else{
            if(mSendOrDownloadButton.getVisibility() == VISIBLE)
                mTvNotSupportPreviewOnline.setVisibility(VISIBLE);
        }


        mTvPreviewOnline.setVisibility(OfficeHelper.isOnlineSupportType(mFileTransferChatMessage.fileType) ? VISIBLE : GONE);

//        if(mTvPreviewOnline.getVisibility() == VISIBLE){
//            mSendOrDownloadButton.setBackgroundResource(R.drawable.shape_common_white);
//            mOpenOtherAppButton.setBackgroundResource(R.drawable.shape_common_white);
//            mShareOtherAppButton.setBackgroundResource(R.drawable.shape_common_white);
//            mSendOrDownloadButton.setTextColor(getResources().getColor(R.color.file_common_text_color));
//            mOpenOtherAppButton.setTextColor(getResources().getColor(R.color.file_common_text_color));
//            mShareOtherAppButton.setTextColor(getResources().getColor(R.color.file_common_text_color));
//        }
//        if(mOpenOtherAppButton.getVisibility() == VISIBLE){
//            mShareOtherAppButton.setBackgroundResource(R.drawable.shape_common_white);
//            mShareOtherAppButton.setTextColor(getResources().getColor(R.color.file_common_text_color));
//        }


        if(shouldVisiblePreviewOnlineBtn()) {
            mLlOnlinePreviewGroups.setVisibility(VISIBLE);
        } else {
            mLlOnlinePreviewGroups.setVisibility(GONE);

        }


        if(needHideBtnMore()) {
            mIvMore.setVisibility(GONE);

        } else {
            mIvMore.setVisibility(VISIBLE);

        }

        refreshControlFunctionBtnBg();

    }


    /**
     * Description:刷新界面
     */
    private void refreshView() {
        showFileStatusView();
        changeFileStatus();
        mIconView.setImageResource(FileMediaTypeUtil.getFileTypeIcon(mFileTransferChatMessage));
        mFileNameView.setText(StringUtils.middleEllipse(mFileTransferChatMessage.name, 40, 10, 18, 15));
        refreshProgressUI();

    }

    /**
     * Description:更新进度条
     */
    void refreshProgressUI() {
        if (FileStatus.SENDING.equals(mFileTransferChatMessage.fileStatus) || FileStatus.DOWNLOADING.equals(mFileTransferChatMessage.fileStatus) || mReDownloading
            || FileStatus.PAUSE.equals(mFileTransferChatMessage.fileStatus)) {
            mProgressView.setVisibility(VISIBLE);
            mProgressBar.setVisibility(VISIBLE);
            mProgressBar.setProgress(mFileTransferChatMessage.progress);
            initProgressText((mFileTransferChatMessage.size * mFileTransferChatMessage.progress) / 100);
            mCancelView.setImageResource(FileStatus.PAUSE.equals(mFileTransferChatMessage.fileStatus) ?
                    R.mipmap.icon_file_download_continue : R.mipmap.icon_search_red_del_hover);
        } else {
            mProgressBar.setVisibility(GONE);
            shouldAutoPreview(mIntentType);
        }
    }

    /**
     * Description:初始化进度条文本
     * @param size
     */
    private void initProgressText(long size) {
        StringBuffer progressTextBuffer = new StringBuffer();
        if (FileStatus.SENDING.equals(mFileTransferChatMessage.fileStatus)) {
//            progressTextBuffer.append(TITLE_SENDING);
            mTvDownloadLabel.setText(TITLE_SENDING);
        } else {
//            progressTextBuffer.append(TITLE_DOWNLOADING);
            mTvDownloadLabel.setText(TITLE_DOWNLOADING);
        }
        progressTextBuffer.append("(" + ChatMessageHelper.getMBOrKBString(size) + "/" + ChatMessageHelper.getMBOrKBString(mFileTransferChatMessage.size) + ")");
        mDownloadText.setText(progressTextBuffer.toString());
    }

    /**
     * 根据文件不同的状态，在界面上显示不同的元素
     */
    private void showFileStatusView() {
        if (mFileTransferChatMessage.fileStatus == null) {
            mFileTransferChatMessage.fileStatus = FileStatus.NOT_DOWNLOAD;
            return;
        }
        if (mFileTransferChatMessage.fileStatus.equals(FileStatus.PAUSE)) {
            return;
        }
        //下载中
        if (mFileTransferChatMessage.fileStatus.equals(FileStatus.DOWNLOADING) || mReDownloading) {
            mProgressView.setVisibility(VISIBLE);
            hideOpenFileBtn();
            hideSendOrDownloadBtn();
            return;
        }

        //未下载
        if (mFileTransferChatMessage.fileStatus.equals(FileStatus.NOT_DOWNLOAD) ||
                mFileTransferChatMessage.fileStatus.equals(FileStatus.DOWNLOAD_FAIL) ||
                mFileTransferChatMessage.fileStatus.equals(FileStatus.SENDED) && !isFileExist()) {
            mProgressView.setVisibility(GONE);
            hideOpenFileBtn();
            showDownloadBtn();
            mSendOrDownloadButton.setText(R.string.file_download_file);
            mTvFileSize.setText(String.format(BaseApplicationLike.baseContext.getResources().getString(R.string.file_transfer_download_file), ChatMessageHelper.getMBOrKBString(mFileTransferChatMessage.size)));
            return;
        }

        if (FileStatus.DOWNLOAD_CANCEL.equals(mFileTransferChatMessage.fileStatus)) {
            mProgressView.setVisibility(GONE);
            hideOpenFileBtn();
            showDownloadBtn();
            mSendOrDownloadButton.setText(RE_DOWNLOAD_FILE);
        }


        //已下载
        if (mFileTransferChatMessage.fileStatus.equals(FileStatus.DOWNLOADED)) {
            //如果是转发未下载的文件，先判断文件的存在性
            if (!isFileExist()) {
                mProgressView.setVisibility(GONE);
                hideOpenFileBtn();
                showDownloadBtn();
                mSendOrDownloadButton.setText(R.string.file_download_file);
                mTvFileSize.setText(String.format(BaseApplicationLike.baseContext.getResources().getString(R.string.file_transfer_download_file), ChatMessageHelper.getMBOrKBString(mFileTransferChatMessage.size)));
                return;
            }
            mProgressView.setVisibility(GONE);
            hideSendOrDownloadBtn();

            showOpenFileBtn();

            return;
        }


        if (FileStatus.SEND_CANCEL.equals(mFileTransferChatMessage.fileStatus)) {
            mProgressView.setVisibility(GONE);
            hideOpenFileBtn();
            showSendBtn();
            mSendOrDownloadButton.setText(RE_SEND_AGAIN);
        }

        //未发送
        if (mFileTransferChatMessage.fileStatus.equals(FileStatus.NOT_SENT) ||
                mFileTransferChatMessage.fileStatus.equals(FileStatus.SEND_FAIL)) {
            mProgressView.setVisibility(GONE);
            hideOpenFileBtn();
            showSendBtn();
            mSendOrDownloadButton.setText(RE_SEND_FILE);
            return;
        }


        //发送中
        if (mFileTransferChatMessage.fileStatus.equals(FileStatus.SENDING)) {
            mProgressView.setVisibility(VISIBLE);
            hideOpenFileBtn();
            hideSendOrDownloadBtn();
            return;
        }

        //已发送
        if (mFileTransferChatMessage.fileStatus.equals(FileStatus.SENDED)) {
            //如果是转发未下载的文件，先判断文件的存在性
            if (!isFileExist()) {
                mProgressView.setVisibility(GONE);
                hideOpenFileBtn();
                showDownloadBtn();
                mSendOrDownloadButton.setText(R.string.file_download_file);
                mTvFileSize.setText(String.format(BaseApplicationLike.baseContext.getResources().getString(R.string.file_transfer_download_file), ChatMessageHelper.getMBOrKBString(mFileTransferChatMessage.size)));
                return;
            }
            mProgressView.setVisibility(GONE);
            showOpenFileBtn();
            hideSendOrDownloadBtn();
            return;
        }
    }


    /**
     * Description:显示发送按钮
     */
    private void showSendBtn() {
        boolean isOverdue = TimeUtil.isOverdueDate(TimeUtil.getCurrentTimeInMillis(), mFileTransferChatMessage.expiredTime);
        if(isOverdue) {
            return;
        }

        setControlBtnViewVisibilityAndRefreshBg(mSendOrDownloadButton, VISIBLE);
    }

    /**
     * Description：显示下载按钮
     */
    private void showDownloadBtn() {

        boolean isOverdue = TimeUtil.isOverdueDate(TimeUtil.getCurrentTimeInMillis(), mFileTransferChatMessage.expiredTime);

        if(isOverdue) {
            return;
        }

        if(!DomainSettingsManager.getInstance().handleChatFileDownloadEnabled()) {
            return;
        }

//        mSendOrDownloadButton.setVisibility(VISIBLE);
        setControlBtnViewVisibilityAndRefreshBg(mSendOrDownloadButton, VISIBLE);
    }


    /**
     * Description：隐藏发送或者下载按钮
     */
    private void hideSendOrDownloadBtn() {
//        mSendOrDownloadButton.setVisibility(GONE);
        setControlBtnViewVisibilityAndRefreshBg(mSendOrDownloadButton, GONE);

        mTvNotSupportPreviewOnline.setVisibility(GONE);
    }

    /**
     * Description：隐藏打开文件按钮
     */
    private void hideOpenFileBtn() {
        mOpenOtherAppButton.setVisibility(GONE);
        mShareOtherAppButton.setVisibility(GONE);
        mOpenLocalButton.setVisibility(GONE);
        mTvActionWithOtherTip.setVisibility(GONE);

        refreshControlFunctionBtnBg();
    }

    /**
     * Description：显示打开文件按钮
     */
    private void showOpenFileBtn() {

        if (shouldVisibleOpenOtherApp()) {
            mOpenOtherAppButton.setVisibility(VISIBLE);
            mShareOtherAppButton.setVisibility(VISIBLE);
            tryShowActionWithOtherTipView();
        }

        if(shouldVisibleOpenLocalButton()) {
            mOpenLocalButton.setText(PREVIEW_LOCAL);
            mOpenLocalButton.setVisibility(VISIBLE);
        }

        refreshControlFunctionBtnBg();

    }

    /**
     * Description：文件是否存在
     * @return
     */
    private boolean isFileExist() {
        if (TextUtils.isEmpty(mFileTransferChatMessage.filePath)) {
            return false;
        }
        File file = new File(mFileTransferChatMessage.filePath);
        return file.exists();
    }

    /**
     * Description：设置文件消息体
     * @param sessionId
     * @param fileTransferChatMessage
     * @param multipartChatMessage
     */
    public void setChatMessage(String sessionId, FileTransferChatMessage fileTransferChatMessage, MultipartChatMessage multipartChatMessage, int intentType) {
        this.mSessionId = sessionId;
        this.mFileTransferChatMessage = fileTransferChatMessage;
        this.mMultipartChatMessage = multipartChatMessage;
        this.mIntentType = intentType;

        mReDownloading = MediaCenterNetManager.isReDownloading(fileTransferChatMessage.deliveryId);

        if(AtworkConfig.FILE_CONFIG.isAutoDownloadInChatFileView()) {
            tryDownloadFile();
        }

        refreshSendingListener();
        refreshDownloadListener();

        refreshView();
        changeFileStatus();


    }


    /**
     * 下载逻辑依赖的 key id, 当来自合并消息的时候, 使用"合并消息"的 id 加上"文件消息"的 id 组合起来,
     * 用于区分多个文件消息相同 id 的情况
     */
    private String getKeyDeliveryId() {
        if (null != mMultipartChatMessage) {
            return mMultipartChatMessage.deliveryId + "_" + mFileTransferChatMessage.deliveryId;
        }

        return mFileTransferChatMessage.deliveryId;
    }


    private boolean isImageModel() {
        return FileData.FileType.File_Image.equals(mFileTransferChatMessage.fileType) && mFileTransferChatMessage.thumbnail != null && mFileTransferChatMessage.thumbnail.length > 0;
    }

    /**
     * Description：文件状态界面下载监听器
     */
    public class FileStatusViewDownloadListener implements MediaCenterNetManager.MediaDownloadListener {

        @Override
        public String getMsgId() {
            return getKeyDeliveryId();
        }

        @Override
        public void downloadSuccess() {
            mFileTransferChatMessage.progress = 0;
            mFileTransferChatMessage.breakPoint = 0;
            ChatDaoService.getInstance().updateMessage(mFileTransferChatMessage);
            if (mReDownloading) {
                mReDownloading = false;
                File oldFile = new File(mFileTransferChatMessage.filePath);
                File newFile = new File(mFileTransferChatMessage.tmpDownloadPath);
                oldFile.delete();
                newFile.renameTo(oldFile);
            }

            if (FileStatus.DOWNLOADING.equals(mFileTransferChatMessage.fileStatus)) {
//                mFileTransferChatMessage.filePath = AtWorkDirUtils.getInstance().getFiles(mActivity) + mFileTransferChatMessage.name;
                mFileTransferChatMessage.fileStatus = FileStatus.DOWNLOADED;
                mFileTransferChatMessage.chatStatus = ChatStatus.Sended;

                updateFileMsgData();
            }

            MediaCenterNetManager.removeMediaDownloadListener(this);
            ChatSessionDataWrap.getInstance().removeChatInFileStreaming(mSessionId, mFileTransferChatMessage.deliveryId);


            updateRecentFileDB(mFileTransferChatMessage, true);
            refreshView();

            refreshChatItemView();

            FileDownloadNotifyService.handleFileDownloadSuccessfullyNotify(mFileTransferChatMessage);

        }

        @Override
        public void downloadFailed(int errorCode, String errorMsg, boolean doRefreshView) {
            if (mFileTransferChatMessage.fileStatus.equals(FileStatus.PAUSE) || mFileTransferChatMessage.breakPoint > 0) {
                mFileTransferChatMessage.fileStatus = FileStatus.PAUSE;
                mCancelView.setImageResource(R.mipmap.icon_file_download_continue);
                return;
            }
            if (errorCode != -99) {
                AtworkToast.showToast(getResources().getString(R.string.download_file_fail));
            }

            //重新下载
            if (mReDownloading) {
                mReDownloading = false;
            }
            //非重新下载
            else {
                if (ChatStatus.Sending.equals(mFileTransferChatMessage.chatStatus)) {
                    if (errorCode == -99) {
                        mFileTransferChatMessage.fileStatus = FileStatus.NOT_DOWNLOAD;
                    } else {
                        mFileTransferChatMessage.fileStatus = FileStatus.DOWNLOAD_FAIL;
                    }

                    mFileTransferChatMessage.chatStatus = ChatStatus.Not_Send;
                }


                if (FileStatus.DOWNLOADING.equals(mFileTransferChatMessage.fileStatus)) {
                    if (errorCode == -99) {
                        mFileTransferChatMessage.fileStatus = FileStatus.NOT_DOWNLOAD;
                    } else {
                        mFileTransferChatMessage.fileStatus = FileStatus.DOWNLOAD_FAIL;
                    }

                    updateFileMsgData();
                }
            }
            MediaCenterNetManager.removeMediaDownloadListener(this);
            ChatSessionDataWrap.getInstance().removeChatInFileStreaming(mSessionId, mFileTransferChatMessage.deliveryId);

            if (doRefreshView) {
                refreshView();
            }
        }


        @Override
        public void downloadProgress(double progress, double size) {
            Log.e("download progress", "progress:" + progress + " size:" + size);

            mFileTransferChatMessage.progress = (int) progress;
            if (mIsViewPause) {
                notifyRefreshProgressUI();
            }
            refreshProgressUI();
        }
    }


    /**
     * Description：文件状态界面上传（发送）监听器
     */
    public class FileStatusViewUploadListener implements MediaCenterNetManager.MediaUploadListener {
        @Override
        public String getMsgId() {
            return getKeyDeliveryId();
        }

        @Override
        public MediaCenterNetManager.UploadType getType() {
            return MediaCenterNetManager.UploadType.STATUS_VIEW_FILE;
        }

        @Override
        public void uploadSuccess(String mediaInfo) {
            mFileTransferChatMessage.mediaId = mediaInfo;
            mFileTransferChatMessage.fileStatus = FileStatus.SENDED;
            refreshView();
            updateRecentFileDB(mFileTransferChatMessage, false);
            refreshChatItemView();
        }

        @Override
        public void uploadFailed(int errorCode, String errorMsg, boolean doRefreshView) {
            //-99表示主动取消，不提示
            if (errorCode != -99) {
                AtworkToast.showToast(getResources().getString(R.string.upload_file_error));
                if (!ChatStatus.Sended.equals(mFileTransferChatMessage.chatStatus)) {
                    mFileTransferChatMessage.fileStatus = FileStatus.SEND_FAIL;
                }

                MediaCenterNetManager.removeUploadFailList(getMsgId());
            } else {
                if (!ChatStatus.Sended.equals(mFileTransferChatMessage.chatStatus)) {
                    mFileTransferChatMessage.fileStatus = FileStatus.SEND_CANCEL;
                }
            }

            if (!ChatStatus.Sended.equals(mFileTransferChatMessage.chatStatus)) {
                mFileTransferChatMessage.chatStatus = ChatStatus.Not_Send;
            }
            if (doRefreshView) {
                refreshView();
                ChatSessionDataWrap.getInstance().notifyMessageSendFail(getKeyDeliveryId());
                refreshChatItemView();
            }

        }

        @Override
        public void uploadProgress(double progress) {
            mFileTransferChatMessage.progress = (int) progress;
            refreshView();
        }
    }


    /**
     * Description：是否需要隐藏点击更多的按钮
     * @return
     */
    private boolean needHideBtnMore() {
        boolean isOverdue = TimeUtil.isOverdueDate(TimeUtil.getCurrentTimeInMillis(), mFileTransferChatMessage.expiredTime);

        if(isOverdue) {
            return true;
        }


        //when no transfer and save_to_dropbox permissions, we need hide the more btn
        if(DomainSettingsManager.getInstance().handleChatFileTransferEnabled()) {
            return false;
        }


        return true;
    }

    private boolean shouldVisibleOpenLocalButton() {
//        if(!WebkitSdkUtil.isX5Init()) {
//            return false;
//        }

        if(!OfficeHelper.isSupportType(mFileTransferChatMessage.filePath)) {
            return false;
        }

        if(!isFileExist()) {
            return false;
        }


        return true;
    }

    private boolean shouldVisibleOpenOtherApp() {
//        if(AtworkConfig.OPEN_DISK_ENCRYPTION) {
//            return false;
//        }

        if(!DomainSettingsManager.getInstance().handleChatFileExternalOpenEnabled()) {
            return false;
        }

        return true;
    }


    private boolean shouldVisiblePreviewOnlineBtn() {
        boolean isOverdue = TimeUtil.isOverdueDate(TimeUtil.getCurrentTimeInMillis(), mFileTransferChatMessage.expiredTime);
        if(isOverdue) {
            return false;
        }


        if(!DomainSettingsManager.getInstance().handleChatFileOnlineViewEnabled()) {
            return false;
        }

        if(shouldVisibleOpenLocalButton()) {
            return false;
        }


        //单机版使用office 的在线预览, 不支持txt 以及 pdf
        if (OfficeHelper.shouldOffice365Preview()
                && !OfficeHelper.isOffice365OnlinePreviewSupportType(mFileTransferChatMessage.name)) {
            return false;
        }

        return true;
    }

    /**
     * Description:判断是否进入页面就提前下载
     * @return
     */
    public Boolean shouldDownloadInAdvance(){
        int maximumDownloadCapacity = MAXIMUMDOWNLOADCAPACITY * 1024 * 1024;
        if(mFileTransferChatMessage.size < maximumDownloadCapacity){

            //下载原文件
            FileStatus fileStatus = mFileTransferChatMessage.fileStatus;
            if (FileStatus.DOWNLOAD_CANCEL.equals(fileStatus) || FileStatus.DOWNLOAD_FAIL.equals(fileStatus)
                    || FileStatus.NOT_DOWNLOAD.equals(fileStatus) || FileStatus.SENDED.equals(fileStatus) || !isFileExist()) {
                downloadFile();
                refreshView();
            }

            return true;
        }else{
            return false;
        }
    }

    /**
     * Description:是否自动预览（10M以下、支持预览的文件）
     */
    public void shouldAutoPreview(int intentType){
        if(shouldVisibleOpenLocalButton() && intentType == VIEW_FROM_FILE_DETAIL){
            previewLocal(intentType);
        }
    }

}
