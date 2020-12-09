package com.foreveross.atwork.modules.file.fragement;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.foreverht.workplus.ui.component.dialogFragment.AtworkAlertDialog;
import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.UrlConstantManager;
import com.foreveross.atwork.api.sdk.dropbox.DropboxAsyncNetService;
import com.foreveross.atwork.api.sdk.dropbox.requestJson.FileTranslateRequest;
import com.foreveross.atwork.api.sdk.net.MediaCenterHttpURLConnectionUtil;
import com.foreveross.atwork.api.sdk.net.model.DownloadFileParamsMaker;
import com.foreveross.atwork.api.sdk.upload.MediaCenterNetManager;
import com.foreveross.atwork.component.ProgressDialogHelper;
import com.foreveross.atwork.component.popview.PopUpView;
import com.foreveross.atwork.component.viewPager.ViewPagerFixed;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.manager.DomainSettingsManager;
import com.foreveross.atwork.infrastructure.model.WebViewControlAction;
import com.foreveross.atwork.infrastructure.model.dropbox.Dropbox;
import com.foreveross.atwork.infrastructure.model.file.FileData;
import com.foreveross.atwork.infrastructure.model.file.FileStatusInfo;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.FileStatus;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.FileTransferChatMessage;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.AtWorkDirUtils;
import com.foreveross.atwork.infrastructure.utils.FileUtil;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.infrastructure.utils.LogUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.encryption.EncryptCacheDisk;
import com.foreveross.atwork.manager.DropboxManager;
import com.foreveross.atwork.modules.app.route.UrlRouteHelper;
import com.foreveross.atwork.modules.bing.listener.UpdateFileDataListener;
import com.foreveross.atwork.modules.bing.listener.download.CommonFileStatusViewDownloadListener;
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


public class CommonFileStatusView extends LinearLayout {


    //下载原文件
    private static  String RE_DOWNLOAD_FILE = BaseApplicationLike.baseContext.getResources().getString(R.string.file_transfer_re_download_file);
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

    //文件图标
    private ImageView mIconView;
    //文件名
    private TextView mFileNameView;

    //发送文件或恢复发送   接收文件或恢复接收
    private TextView mSendOrDownloadButton;

    //用第三方应用打开按扭
    private TextView mOpenOtherAppButton;
    private TextView mShareOtherAppButton;

    private TextView mOpenLocalButton;

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

    private FileStatusInfo mFileStatusInfo;


    private String mSessionId;

    private ImageView mIvMore;

    private Activity mActivity;

    private View mButtonGroups;

    private View mLlOnlinePreviewGroups;
    private TextView mTvSupportPreview;
    private TextView mTvPreviewOnline;

    private View mFileDetailView;

    private ViewPagerFixed mTranslateViewPager;
    private TranslateViewPagerAdapter mTranslateViewPagerAdapter;

    private ProgressDialogHelper mProgressDialogHelper;

    private View mWatermarkView;

    private TextView mOverdueTime;
    private TextView mOverdueHint;

    private UpdateFileDataListener mUpdateFileDataListener;

    private List<TextView> mControlFunctionBtnList = new ArrayList<>();

    private boolean mLoadMore = false;

    public CommonFileStatusView(Activity context) {
        super(context);

        setOrientation(LinearLayout.VERTICAL);

        mActivity = context;
        initView();
        registerListener();
    }

    public void onResume() {
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
    }



    private void registerListener() {
        mOpenLocalButton.setOnClickListener(v -> {
            previewLocal();
        });

        mSendOrDownloadButton.setOnClickListener(v -> {
            FileStatus fileStatus = mFileStatusInfo.getFileStatus();
            //重新发送
            if (FileStatus.SEND_CANCEL.equals(fileStatus) || FileStatus.SEND_FAIL.equals(fileStatus)
                    || FileStatus.NOT_SENT.equals(fileStatus)) {
//                reSending();
                refreshView();

                return;
            }

            //下载原文件
            if (FileStatus.DOWNLOAD_CANCEL.equals(fileStatus) || FileStatus.DOWNLOAD_FAIL.equals(fileStatus)
                    || FileStatus.NOT_DOWNLOAD.equals(fileStatus) || FileStatus.SENDED.equals(fileStatus)) {
                downloadFile();
                refreshView();
                return;
            }

        });

        //预览文件功能
        mOpenOtherAppButton.setOnClickListener(v -> {
            if (StringUtils.isEmpty(mFileStatusInfo.getPath())) {
                return;
            }
            if (mFileStatusInfo.getPath().toLowerCase().endsWith(".apk")) {

                installApk();

            } else {
                if (VoipHelper.isHandlingVoipCall() && (FileData.FileType.File_Audio == mFileStatusInfo.getFileType() || FileData.FileType.File_Video == mFileStatusInfo.getFileType())) {
                    AtworkToast.showResToast(R.string.alert_is_handling_voip_meeting_click_voip);

                } else {

                    previewOtherApp();

                }
            }

        });


        mShareOtherAppButton.setOnClickListener(v -> {
            if (StringUtils.isEmpty(mFileStatusInfo.getPath())) {
                return;
            }

            shareOtherApp();
        });


        mTvPreviewOnline.setOnClickListener(view -> {
            if(OfficeHelper.shouldOffice365Preview()) {
                String url = String.format(UrlConstantManager.getInstance().officePreviewUrl(), MediaCenterNetManager.getDownloadUrl(getContext(), mFileStatusInfo.getMediaId()));
                UrlRouteHelper.routeUrl(getContext(), WebViewControlAction.newAction().setUrl(url));
                return;
            }


            mProgressDialogHelper.show();
            FileTranslateRequest request = new FileTranslateRequest();
            request.mFileType = mFileStatusInfo.getFileType().getString();
            request.mMediaId = mFileStatusInfo.getMediaId();
            loadTranslateData(request);

        });


        //取消上传或下载功能
        mCancelView.setOnClickListener(v -> new AtworkAlertDialog(getContext(), AtworkAlertDialog.Type.SIMPLE)
                .setContent(R.string.cancel_file_transfer)
                .setClickBrightColorListener(dialog -> {

                    MediaCenterNetManager mediaCenterNetManager = new MediaCenterNetManager(BaseApplicationLike.baseContext);

                    if(FileStatus.DOWNLOADING.equals(mFileStatusInfo.getFileStatus())) {
                        mFileStatusInfo.setFileStatus(FileStatus.NOT_DOWNLOAD);
                        refreshViewAndUpdateData();
                    }

                    mediaCenterNetManager.brokenDownloadingOrUploading(mFileStatusInfo.getKeyId());

                    refreshView();

                }).show());



        mIvMore.setOnClickListener(v -> {
            handleMoreBtnClick();
        });


    }

    private void handleMoreBtnClick() {
        final PopUpView popUpView = new PopUpView(getContext());

        if (handleDomainChatFileTransferEnabled()) {
            popUpView.addPopItem(R.mipmap.icon_share, R.string.forwarding_item, 0);
        }

        popUpView.setPopItemOnClickListener((title, pos) -> {



            FileTransferChatMessage fileTransferChatMessage = FileTransferChatMessage.getFileTransferChatMessageFromFileStatusInfo(mFileStatusInfo);

            //转发
            if (title.equals(getResources().getString(R.string.forwarding_item))) {
                List<ChatPostMessage> chatPostMessages = new ArrayList<>();
                chatPostMessages.add(fileTransferChatMessage);

                TransferMessageControlAction transferMessageControlAction = new TransferMessageControlAction();
                transferMessageControlAction.setSendMessageList(chatPostMessages);
                transferMessageControlAction.setSendMode(TransferMessageMode.FORWARD);
                Intent intent = TransferMessageActivity.Companion.getIntent(BaseApplicationLike.baseContext, transferMessageControlAction);


                CommonFileStatusView.this.getContext().startActivity(intent);
            }

            if (pos == 1) {
                Dropbox dropbox = Dropbox.convertFromChatPostMessage(mActivity, fileTransferChatMessage);
                Intent intent = SaveToDropboxActivity.getIntent(mActivity, dropbox, fileTransferChatMessage);
                mActivity.startActivityForResult(intent, UserDropboxFragment.REQUEST_CODE_COPY_DROPBOX);
            }
            popUpView.dismiss();
        });

        popUpView.pop(mIvMore);
    }




    private void previewOtherApp() {

        EncryptCacheDisk.getInstance().getOriginalFilePathAndCheck(mFileStatusInfo.getPath(), false, fileName -> IntentUtil.previewIntent(getContext(), fileName, mFileStatusInfo.getFileType().getFileType()));

    }


    private void shareOtherApp() {
        EncryptCacheDisk.getInstance().getOriginalFilePathAndCheck(mFileStatusInfo.getPath(), false, fileName -> IntentUtil.shareIntent(getContext(), fileName));
    }


    private void previewLocal() {
        EncryptCacheDisk.getInstance().getOriginalFilePathAndCheck(mFileStatusInfo.getPath(), false, fileName -> {

            OfficeHelper.previewByX5(getContext(), fileName);

        });
    }

    private void installApk() {

        EncryptCacheDisk.getInstance().getOriginalFilePathAndCheck(mFileStatusInfo.getPath(), false, fileName -> IntentUtil.installApk(getContext(), mFileStatusInfo.getPath()));

    }

    private void showPreview(FileTranslateRequest request, int totalPage,  List<String> translateList) {
        if(AtworkConfig.SCREEN_ORIENTATION_USER_SENSOR) {
            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
        }

        if (handleWaterChatFileWatermarkFeature()) {

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


    private void downloadFile() {
        AndPermission
                .with(mActivity)
                .runtime()
                .permission(Permission.WRITE_EXTERNAL_STORAGE, Permission.READ_EXTERNAL_STORAGE)
                .onGranted(data -> {
                    if (isFileNotValid()) {
                        return;
                    }

                    mFileStatusInfo.setProgress(0);
                    mFileStatusInfo.setFileStatus(FileStatus.DOWNLOADING);

                    refreshView();
//        refreshViewAndUpdateData();



                    MediaCenterNetManager.removeMediaDownloadListenerById(mFileStatusInfo.getKeyId());
                    CommonFileStatusViewDownloadListener mediaDownloadListener = new CommonFileStatusViewDownloadListener(mFileStatusInfo, mUpdateFileDataListener);
                    MediaCenterNetManager.addMediaDownloadListener(mediaDownloadListener);

                    //重新开启下载监听
                    MediaCenterNetManager mediaCenterNetManager = new MediaCenterNetManager(BaseApplicationLike.baseContext);

                    if (StringUtils.isEmpty(mFileStatusInfo.getPath())) {
                        String filePath = FileUtil.getFileInfoByChecking(mFileStatusInfo.getName(), AtWorkDirUtils.getInstance().getFiles(mActivity)).filePath;
                        mFileStatusInfo.setPath(filePath);
                    }

                    mediaCenterNetManager.downloadFile(
                            DownloadFileParamsMaker.Companion
                                    .newRequest()
                                    .setMediaId(mFileStatusInfo.getMediaId())
                                    .setDownloadId(mFileStatusInfo.getKeyId())
                                    .setDownloadPath(mFileStatusInfo.getPath())
                                    .setDownloadType(MediaCenterHttpURLConnectionUtil.DOWNLOAD_TYPE.FILE));
                })
                .onDenied(data ->  AtworkUtil.popAuthSettingAlert(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE))
                .start();
    }


    /**
     * 刷新 listview 并且更新数据库
     */
    private void refreshViewAndUpdateData() {
        mUpdateFileDataListener.update(mFileStatusInfo);
    }



    private void initView() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.fragment_file_transfer, this);
        mIconView = view.findViewById(R.id.file_transfer_file_icon);
        mFileNameView = view.findViewById(R.id.file_transfer_file_name);
        mSendOrDownloadButton = view.findViewById(R.id.file_transfer_re_send);
        mOpenOtherAppButton = view.findViewById(R.id.file_transfer_open_with_others);
        mShareOtherAppButton = view.findViewById(R.id.file_transfer_share_with_others);
        mOpenLocalButton = view.findViewById(R.id.file_transfer_open_local);
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

        mButtonGroups = view.findViewById(R.id.button_group);
        mTvPreviewOnline = view.findViewById(R.id.preview_online);
        mLlOnlinePreviewGroups = view.findViewById(R.id.file_prieview_group);
        mTvSupportPreview = view.findViewById(R.id.is_support_preview_online);

        mTranslateViewPager = view.findViewById(R.id.preview_file_pager);

        mProgressDialogHelper = new ProgressDialogHelper(mActivity);

        mWatermarkView = view.findViewById(R.id.watermark_view);

        mOverdueTime = view.findViewById(R.id.overdue_time);
        mOverdueHint = view.findViewById(R.id.overdue_time_hint);

        mOverdueTime.setVisibility(GONE);
        mOverdueHint.setVisibility(GONE);


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


    public void setFileStatusInfo(@Nullable String sessionId, FileStatusInfo fileStatusInfo) {
        mSessionId = sessionId;
        mFileStatusInfo = fileStatusInfo;
        refreshView();
    }

    public void refreshView() {
        showFileStatusView();
        refreshPreviewOnlineFunctionUI();
        mIconView.setImageResource(FileMediaTypeUtil.getFileTypeIcon(mFileStatusInfo.getFileType()));
        mFileNameView.setText(StringUtils.middleEllipse(mFileStatusInfo.getName(), 40, 10, 18, 15));

        refreshProgressUI();

        if(needHideBtnMore()) {
            mIvMore.setVisibility(GONE);
        } else {
            mIvMore.setVisibility(VISIBLE);

        }

    }

    private void refreshPreviewOnlineFunctionUI() {

        mTvSupportPreview.setText(OfficeHelper.isOnlineSupportType(mFileStatusInfo.getFileType()) ?
                AtworkApplicationLike.getResourceString(R.string.preview_tip) : AtworkApplicationLike.getResourceString(R.string.not_support_preview_online));

        if(OfficeHelper.isOnlineSupportType(mFileStatusInfo.getFileType())){
            mTvSupportPreview.setVisibility(GONE);

        }

        mTvPreviewOnline.setVisibility(OfficeHelper.isOnlineSupportType(mFileStatusInfo.getFileType()) ? VISIBLE : GONE);

        if(shouldVisiblePreviewOnlineBtn()) {
            mLlOnlinePreviewGroups.setVisibility(VISIBLE);

        } else {
            mLlOnlinePreviewGroups.setVisibility(GONE);

        }

        refreshControlFunctionBtnBg();

    }


    private void refreshProgressUI() {
        if (FileStatus.SENDING.equals(mFileStatusInfo.getFileStatus()) || FileStatus.DOWNLOADING.equals(mFileStatusInfo.getFileStatus())) {
            mProgressBar.setVisibility(VISIBLE);
            mProgressBar.setProgress(mFileStatusInfo.getProgress());
            initProgressText((mFileStatusInfo.getSize() * mFileStatusInfo.getProgress()) / 100);
        } else {
            mProgressBar.setVisibility(GONE);
        }
    }

    private void initProgressText(long size) {
        StringBuffer progressTextBuffer = new StringBuffer();
        if (FileStatus.SENDING.equals(mFileStatusInfo.getFileStatus())) {
//            progressTextBuffer.append(TITLE_SENDING);
            mTvDownloadLabel.setText(TITLE_SENDING);
        } else {
//            progressTextBuffer.append(TITLE_DOWNLOADING);
            mTvDownloadLabel.setText(TITLE_DOWNLOADING);
        }
        progressTextBuffer.append("(" + ChatMessageHelper.getMBOrKBString(size) + "/" + ChatMessageHelper.getMBOrKBString(mFileStatusInfo.getSize()) + ")");
        mDownloadText.setText(progressTextBuffer.toString());
    }

    /**
     * 根据文件不同的状态，在界面上显示不同的元素
     */
    private void showFileStatusView() {
        if (mFileStatusInfo.getFileStatus() == null) {
            mFileStatusInfo.setFileStatus(FileStatus.NOT_DOWNLOAD);
            return;
        }

        //下载中
        if (mFileStatusInfo.getFileStatus().equals(FileStatus.DOWNLOADING)) {
            mProgressView.setVisibility(VISIBLE);
            hideOpenFileBtn();
            hideSendOrDownloadBtn();
            return;
        }

        //未下载
        if (mFileStatusInfo.getFileStatus().equals(FileStatus.NOT_DOWNLOAD) ||
                mFileStatusInfo.getFileStatus().equals(FileStatus.DOWNLOAD_FAIL)) {
            mProgressView.setVisibility(GONE);
            hideOpenFileBtn();
            showDownloadBtn();
            mSendOrDownloadButton.setText(String.format(BaseApplicationLike.baseContext.getResources().getString(R.string.file_transfer_download_file), ChatMessageHelper.getMBOrKBString(mFileStatusInfo.getSize())));
            return;
        }

        if (FileStatus.DOWNLOAD_CANCEL.equals(mFileStatusInfo.getFileStatus())) {
            mProgressView.setVisibility(GONE);
            hideOpenFileBtn();
            showDownloadBtn();
            mSendOrDownloadButton.setText(RE_DOWNLOAD_FILE);
        }


        //已下载
        if (mFileStatusInfo.getFileStatus().equals(FileStatus.DOWNLOADED)) {
            //如果是转发未下载的文件，先判断文件的存在性
            if (!isFileExist()) {
                mProgressView.setVisibility(GONE);
                hideOpenFileBtn();
                showDownloadBtn();
                mSendOrDownloadButton.setText(String.format(BaseApplicationLike.baseContext.getResources().getString(R.string.file_transfer_download_file), ChatMessageHelper.getMBOrKBString(mFileStatusInfo.getSize())));
                return;
            }
            mProgressView.setVisibility(GONE);
            hideSendOrDownloadBtn();

            showOpenFileBtn();
            return;
        }


        if (FileStatus.SEND_CANCEL.equals(mFileStatusInfo.getFileStatus())) {
            mProgressView.setVisibility(GONE);
            hideOpenFileBtn();
            showSendBtn();
            mSendOrDownloadButton.setText(RE_SEND_AGAIN);
        }

        //未发送
        if (mFileStatusInfo.getFileStatus().equals(FileStatus.NOT_SENT) ||
                mFileStatusInfo.getFileStatus().equals(FileStatus.SEND_FAIL)) {
            mProgressView.setVisibility(GONE);
            hideOpenFileBtn();
            showSendBtn();
            mSendOrDownloadButton.setText(RE_SEND_FILE);
            return;
        }


        //发送中
        if (mFileStatusInfo.getFileStatus().equals(FileStatus.SENDING)) {
            mProgressView.setVisibility(VISIBLE);
            hideOpenFileBtn();
            hideSendOrDownloadBtn();
            mCancelView.setVisibility(GONE);
            return;
        }

        //已发送
        if (mFileStatusInfo.getFileStatus().equals(FileStatus.SENDED)) {
            //如果是转发未下载的文件，先判断文件的存在性
            if (!isFileExist()) {
                mProgressView.setVisibility(GONE);
                hideOpenFileBtn();
                showSendBtn();
                mSendOrDownloadButton.setText(String.format(BaseApplicationLike.baseContext.getResources().getString(R.string.file_transfer_download_file), ChatMessageHelper.getMBOrKBString(mFileStatusInfo.getSize())));
                return;
            }
            mProgressView.setVisibility(GONE);
            showOpenFileBtn();
            hideSendOrDownloadBtn();
            return;
        }
    }

    private void hideOpenFileBtn() {
        mOpenOtherAppButton.setVisibility(GONE);
        mShareOtherAppButton.setVisibility(GONE);
        mOpenLocalButton.setVisibility(GONE);
        mTvActionWithOtherTip.setVisibility(GONE);

        refreshControlFunctionBtnBg();

    }

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



    private boolean isFileExist() {
        if (TextUtils.isEmpty(mFileStatusInfo.getPath())) {
            return false;
        }
        File file = new File(mFileStatusInfo.getPath());
        return file.exists();
    }

    private boolean isFileNotValid() {
        if (StringUtils.isEmpty(mFileStatusInfo.getMediaId())) {
            AtworkToast.showToast(getResources().getString(R.string.not_allowed_download));
            return true;
        }

        return false;
    }

    public void setUpdateFileDataListener(UpdateFileDataListener updateFileDataListener) {
        this.mUpdateFileDataListener = updateFileDataListener;
    }



    private boolean shouldVisibleOpenOtherApp() {
//        if(AtworkConfig.OPEN_DISK_ENCRYPTION) {
//            return false;
//        }

        if(!handleDomainChatFileExternalOpenEnabled()) {
            return false;
        }

        return true;
    }



    private boolean shouldVisiblePreviewOnlineBtn() {
        if(!handleDomainChatFileOnlineViewEnabled()) {
            return false;
        }

        if(shouldVisibleOpenLocalButton()) {
            return false;
        }


        if (OfficeHelper.shouldOffice365Preview()
                && !OfficeHelper.isOffice365OnlinePreviewSupportType(mFileStatusInfo.getName())) {
            return false;
        }

        return true;
    }

    private boolean shouldVisibleOpenLocalButton() {
        return OfficeHelper.isSupportType(mFileStatusInfo.getPath()) && isFileExist();
    }


    private void hideSendOrDownloadBtn() {
//        mSendOrDownloadButton.setVisibility(GONE);
        setControlBtnViewVisibilityAndRefreshBg(mSendOrDownloadButton, GONE);
    }


    private void showSendBtn() {


//        mSendOrDownloadButton.setVisibility(VISIBLE);

        setControlBtnViewVisibilityAndRefreshBg(mSendOrDownloadButton, VISIBLE);

    }

    private void showDownloadBtn() {


        if(!handleDomainChatFileDownloadEnabled()) {
            return;
        }

//        mSendOrDownloadButton.setVisibility(VISIBLE);
        setControlBtnViewVisibilityAndRefreshBg(mSendOrDownloadButton, VISIBLE);

    }

    private boolean handleWaterChatFileWatermarkFeature() {
        return DomainSettingsManager.getInstance().handleChatFileWatermarkFeature();
    }

    private boolean handleDomainChatFileExternalOpenEnabled() {
        if(!mFileStatusInfo.needW6sChatFileBehavior()) {
            return true;
        }

        return DomainSettingsManager.getInstance().handleChatFileExternalOpenEnabled();
    }


    private boolean handleDomainChatFileTransferEnabled() {
        if(!mFileStatusInfo.needW6sChatFileBehavior()) {
            return true;
        }

        return DomainSettingsManager.getInstance().handleChatFileTransferEnabled();
    }

    private boolean handleDomainChatFileDownloadEnabled() {
        if(!mFileStatusInfo.needW6sChatFileBehavior()) {
            return true;
        }


        return DomainSettingsManager.getInstance().handleChatFileDownloadEnabled();
    }


    private boolean handleDomainChatFileOnlineViewEnabled() {
        if(!mFileStatusInfo.needW6sChatFileBehavior()) {
            return true;
        }


        return DomainSettingsManager.getInstance().handleChatFileOnlineViewEnabled();
    }

    private boolean needHideBtnMore() {


        if(!mFileStatusInfo.needActionMore()) {
            return true;
        }

        //when no transfer and save_to_dropbox permissions, we need hide the more btn
        if(DomainSettingsManager.getInstance().handleChatFileTransferEnabled()) {
            return false;
        }


        return true;
    }

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



}
