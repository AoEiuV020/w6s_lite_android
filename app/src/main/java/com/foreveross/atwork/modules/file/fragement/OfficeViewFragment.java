package com.foreveross.atwork.modules.file.fragement;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.foreverht.workplus.module.file_share.FileShareAction;
import com.foreverht.workplus.module.file_share.activity.FileShareActivity;
import com.foreverht.workplus.ui.component.dialogFragment.AtworkAlertDialog;
import com.foreverht.workplus.ui.component.popUpView.W6sPopUpView;
import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.manager.DomainSettingsManager;
import com.foreveross.atwork.infrastructure.model.dropbox.Dropbox;
import com.foreveross.atwork.infrastructure.model.file.FileData;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.BodyType;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.ParticipantType;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.FileTransferChatMessage;
import com.foreveross.atwork.infrastructure.permissions.PermissionsManager;
import com.foreveross.atwork.infrastructure.permissions.PermissionsResultAction;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.TimeUtil;
import com.foreveross.atwork.infrastructure.utils.encryption.EncryptCacheDisk;
import com.foreveross.atwork.manager.DropboxManager;
import com.foreveross.atwork.modules.chat.service.ChatFilePermissionService;
import com.foreveross.atwork.modules.downLoad.component.DownLoadFileAttrDialog;
import com.foreveross.atwork.modules.dropbox.activity.SaveToDropboxActivity;
import com.foreveross.atwork.modules.dropbox.fragment.UserDropboxFragment;
import com.foreveross.atwork.modules.file.activity.OfficeViewActivity;
import com.foreveross.atwork.modules.file.dao.RecentFileDaoService;
import com.foreveross.atwork.modules.group.activity.TransferMessageActivity;
import com.foreveross.atwork.modules.group.module.TransferMessageControlAction;
import com.foreveross.atwork.modules.group.module.TransferMessageMode;
import com.foreveross.atwork.modules.image.component.ItemEnlargeImageView;
import com.foreveross.atwork.modules.voip.utils.VoipHelper;
import com.foreveross.atwork.support.BackHandledFragment;
import com.foreveross.atwork.utils.AtworkToast;
import com.foreveross.atwork.utils.AtworkUtil;
import com.foreveross.atwork.utils.ImageCacheHelper;
import com.foreveross.atwork.utils.IntentUtil;
import com.foreveross.watermark.core.WaterMarkUtil;
import com.tencent.smtt.sdk.TbsReaderView;

import java.util.ArrayList;
import java.util.List;

import static com.foreveross.atwork.component.DownloadPagerView.REFRESH_DOWN_LOAD_FILE_LIST;
import static com.foreveross.atwork.modules.downLoad.activity.MyDownLoadActivity.REFRESH_DOWN_LOAD_VIEW_PAGER;
import static com.foreveross.atwork.modules.downLoad.component.DownloadFileDetailView.MSG_DELETE_RECENT_DOWN_LOAD_FILE_SUCCESS;
import static com.foreveross.atwork.modules.file.activity.OfficeViewActivity.FILE_DATA;
import static com.foreveross.atwork.modules.file.activity.OfficeViewActivity.FILE_TRANSFER_CHAT_MESSAGE;
import static com.foreveross.atwork.modules.file.activity.OfficeViewActivity.VIEW_FROM_FILE_DATA_LIST;
import static com.foreveross.atwork.modules.file.activity.OfficeViewActivity.VIEW_FROM_OTHER;

/**
 * Created by dasunsy on 2018/1/9.
 */

public class OfficeViewFragment extends BackHandledFragment implements TbsReaderView.ReaderCallback {

    private RelativeLayout mRlRoot;
    private FrameLayout mFlReaderView;
    private TbsReaderView mTbsReaderView;
    private ItemEnlargeImageView mIvPreview;
    private View mWatermarkView;
    private ImageView mTitleBarChatDetailBack;
    private ImageView mIvMore;

    private String mFilePath;
    private String mFrom;
    private FileTransferChatMessage mFileTransferChatMessage;
    private String mSessionId;
    private int mIntentType = VIEW_FROM_OTHER;

    private FileData mFileData;
    private int mWhat;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mWhat = msg.what;
            switch (mWhat) {
                case MSG_DELETE_RECENT_DOWN_LOAD_FILE_SUCCESS:
                    sendBroadCast();
                    finish();
                    break;

            }
        }
    };

    public static void sendBroadCast() {
        LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).sendBroadcast(new Intent(REFRESH_DOWN_LOAD_VIEW_PAGER));
        LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).sendBroadcast(new Intent(REFRESH_DOWN_LOAD_FILE_LIST));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_office_view, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mIvPreview.setScaleGesture();
        mIvPreview.setOnTagListener(() -> {
            onBackPressed();
            return true;
        });

        initData();
        displayFile();
        shouldDisplayBtnMore();

        if (shouldShowWatermark()) {
            mWatermarkView.setVisibility(View.VISIBLE);
            WaterMarkUtil.setLoginUserWatermark(mActivity, mWatermarkView, -1, ColorUtils.setAlphaComponent(ContextCompat.getColor(getActivity(), R.color.black), 30), "");
        }
        registerListener();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mTbsReaderView.onStop();

    }


    @Override
    protected void findViews(View view) {
        mRlRoot = view.findViewById(R.id.rl_root);
        mTitleBarChatDetailBack = view.findViewById(R.id.title_bar_chat_detail_back);
        mFlReaderView = view.findViewById(R.id.fl_tbs_reader_view);
        mWatermarkView = view.findViewById(R.id.watermark_bg);
        mIvPreview = view.findViewById(R.id.iv_preview);
        mTbsReaderView = new TbsReaderView(getActivity(), this);
        mFlReaderView.addView(mTbsReaderView, 0, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mIvMore = view.findViewById(R.id.title_bar_main_more_btn);
        mIvMore.setImageResource(R.mipmap.icon_more_dark);
    }

    private void initData() {
        Bundle bundle = getArguments();
        if(null != bundle) {
            mFilePath = bundle.getString(OfficeViewActivity.DATA_FILE_PATH);
            mFrom = bundle.getString(OfficeViewActivity.DATA_FROM);
            mIntentType = bundle.getInt(OfficeViewActivity.INTENT_TYPE, VIEW_FROM_OTHER);
            if(mIntentType == VIEW_FROM_FILE_DATA_LIST){
                mFileData = new FileData();
                mFileData = (FileData)bundle.getSerializable(FILE_DATA);
                return;
            }
            if(VIEW_FROM_OTHER != mIntentType){
                mFileTransferChatMessage = new FileTransferChatMessage();
                mFileTransferChatMessage = (FileTransferChatMessage)bundle.getSerializable(FILE_TRANSFER_CHAT_MESSAGE);
                mSessionId = bundle.getString(OfficeViewActivity.SESSION_ID);
            }

        }
    }

    private void shouldDisplayBtnMore(){
        if(mIntentType == VIEW_FROM_FILE_DATA_LIST){
            mIvMore.setVisibility(View.VISIBLE);
            return;
        }
        if(VIEW_FROM_OTHER == mIntentType) {
            mIvMore.setVisibility(View.GONE);
        } else {
            mIvMore.setVisibility(View.VISIBLE);
        }
    }

    private void registerListener() {
        mTitleBarChatDetailBack.setOnClickListener(v -> {
            finish();
        });
        mIvMore.setOnClickListener(v -> {
            if(mIntentType == VIEW_FROM_FILE_DATA_LIST){
                handleMoreBtnWithFileDataClick();
            }else{
                handleMoreBtnClick();
            }

        });
    }

    private void displayFile() {
        if(isImgType(mFilePath)) {
            mIvPreview.setVisibility(View.VISIBLE);
            ImageCacheHelper.displayImage(mFilePath, mIvPreview, ImageCacheHelper.getDefaultImageOptions(false, false, true));
            return;
        }

        displayOffice();
    }

    private void displayOffice() {
        mFlReaderView.setVisibility(View.VISIBLE);

        Bundle bundle = new Bundle();
        bundle.putString("filePath", mFilePath);
        bundle.putString("tempPath", Environment.getExternalStorageDirectory().getPath());
        boolean result = mTbsReaderView.preOpen(parseFormat(mFilePath), false);
        if (result) {
            mTbsReaderView.openFile(bundle);
        }
    }

    private String parseFormat(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    @Override
    protected boolean onBackPressed() {
        finish();
        return false;
    }

    @Override
    public void onCallBackAction(Integer integer, Object o, Object o1) {

    }

    private boolean shouldShowWatermark() {

        if("email".equals(mFrom)) {
            String showWatermark = DomainSettingsManager.getInstance().handleEmailWatermarkFeature();
            return "show".equalsIgnoreCase(showWatermark);
        }

        return false;
    }


    public static boolean isImgType(String path) {
        FileData.FileType fileType = FileData.getFileType(path);
        switch (fileType) {
            case File_Image:
            case File_Gif:
                return true;

            default:
                return false;
        }

    }
    /**
     * Description:初始化“我的下载”的更多按钮的点击弹窗
     */
    private void handleMoreBtnWithFileDataClick(){

        final W6sPopUpView popUpView = new W6sPopUpView(getContext());

        if (DomainSettingsManager.getInstance().handleChatFileTransferEnabled()) {
            popUpView.addPopItem(-1, R.string.forwarding_item, 0);
            //分享
            popUpView.addPopItem(-1, R.string.share, 7);
        }
        //发邮件
        popUpView.addPopItem(-1, R.string.send_email, 2);

        //文件属性
        popUpView.addPopItem(-1, R.string.file_attr, 3);
        //删除
        popUpView.addPopItem(-1, R.string.delete, 4);
        //用其他应用打开
        popUpView.addPopItem(-1, R.string.open_by_other_app, 5);
        //用其他应用分享
        popUpView.addPopItem(-1, R.string.share_by_other_app, 6);

        popUpView.setPopItemOnClickListener((title, pos) -> {

            //转发
            if (title.equals(getResources().getString(R.string.forwarding_item))) {
                List<ChatPostMessage> messages = new ArrayList<>();
                User LoginUser = AtworkApplicationLike.getLoginUserSync();
                long overtime = DomainSettingsManager.getInstance().handleChatFileExpiredFeature() ? TimeUtil.getTimeInMillisDaysAfter(DomainSettingsManager.getInstance().getChatFileExpiredDay()) : -1;
                FileTransferChatMessage message = FileTransferChatMessage.newFileTransferChatMessage(AtworkApplicationLike.baseContext, mFileData, LoginUser, "",
                        ParticipantType.User, ParticipantType.User, "", "", "", BodyType.File, "", overtime, null);
                message.mediaId = mFileData.mediaId;
                messages.add(message);

                TransferMessageControlAction transferMessageControlAction = new TransferMessageControlAction();
                transferMessageControlAction.setSendMessageList(messages);
                transferMessageControlAction.setSendMode(TransferMessageMode.SEND);
                Intent intent = TransferMessageActivity.Companion.getIntent(BaseApplicationLike.baseContext, transferMessageControlAction);
                this.getContext().startActivity(intent);
            }

            if (pos == 2) {
                Dropbox dropbox = Dropbox.convertFromFilePath(getContext(), mFileData.filePath, mFileData.mediaId);
                FragmentActivity fragmentActivity = (FragmentActivity)mActivity;
                doCommandSendEmail(fragmentActivity.getSupportFragmentManager(), dropbox);
            }
            if (pos == 3) {
                doCommandFileAttr();
            }
            if (pos == 4) {
                deleteFile();
            }
            if (pos == 5) {
                openFileByOther();
            }
            if (pos == 6) {
                shareFileToOthers();
            }
            if (pos == 7) {
                shareFile();
            }

            popUpView.dismiss();
        });

        popUpView.pop(mIvMore);
    }

    /**
     * 通过邮件发送
     */
    public void doCommandSendEmail(FragmentManager fragmentManager, Dropbox dropbox) {
        PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(mActivity, new String[]{Manifest.permission.WRITE_CONTACTS}, new PermissionsResultAction() {
            @Override
            public void onGranted() {
                DropboxManager.getInstance().doCommandSendEmail(mActivity, fragmentManager, dropbox);

            }

            @Override
            public void onDenied(String permission) {
                final AtworkAlertDialog alertDialog = AtworkUtil.getAuthSettingAlert(mActivity, permission);
                alertDialog.setOnDismissListener(dialog -> {
                    if(alertDialog.shouldHandleDismissEvent) {
                        DropboxManager.getInstance().doCommandSendEmail(mActivity, fragmentManager, dropbox);


                    }

                });

                alertDialog.show();
            }
        });
    }

    /**
     * 查看文件属性
     */
    public void doCommandFileAttr() {
        FragmentActivity fragmentActivity = (FragmentActivity)mActivity;
        FragmentManager fragmentManager = fragmentActivity.getSupportFragmentManager();
        DownLoadFileAttrDialog dialog = new DownLoadFileAttrDialog();
        dialog.setArguments(dialog.setData(mFileData));
        dialog.show(fragmentManager, "download_attr");
    }

    /**
     * Description:删除本地数据库记录
     */
    public void deleteFile(){
        AtworkAlertDialog dialog = new AtworkAlertDialog(mActivity);
        dialog.setTitleText(R.string.delete_file);
        dialog.setContent(R.string.delete_file_tip);
        dialog.setBrightBtnText(R.string.ok);
        dialog.setDeadBtnText(R.string.cancel);
        dialog.setClickDeadColorListener(dialog1 -> dialog.dismiss());
        dialog.setClickBrightColorListener(dialog1 -> {
            RecentFileDaoService.getInstance().deleteDownloadFileByFileId(mHandler, mFileData.mediaId, mFileData.filePath);
        });
        dialog.show();
    }
    /**
     * 使用其他应用打开
     */
    public void openFileByOther(){
        if (StringUtils.isEmpty(mFileData.filePath)) {
            return;
        }
        if (mFileData.filePath.toLowerCase().endsWith(".apk")) {
            installApk();

        } else {
            if (VoipHelper.isHandlingVoipCall() && (FileData.FileType.File_Audio == mFileData.fileType || FileData.FileType.File_Video == mFileData.fileType)) {
                AtworkToast.showResToast(R.string.alert_is_handling_voip_meeting_click_voip);
            } else {
                EncryptCacheDisk.getInstance().getOriginalFilePathAndCheck(mFileData.filePath, false, fileName -> {
                    IntentUtil.previewIntent(getContext(), fileName, mFileData.fileType.getFileType());
                });
            }
        }
    }

    /**
     * 分享到其他应用
     */
    public void shareFileToOthers(){
        if (StringUtils.isEmpty(mFileData.filePath)) {
            return;
        }
        EncryptCacheDisk.getInstance().getOriginalFilePathAndCheck(mFileData.filePath, false, fileName -> {
            IntentUtil.shareIntent(getContext(), fileName);
        });
    }

    /**
     * 分享文件
     */
    public void shareFile(){
        FileShareAction fileShareAction = new FileShareAction();
        fileShareAction.setDomainId(AtworkConfig.DOMAIN_ID);
        fileShareAction.setOpsId(LoginUserInfo.getInstance().getLoginUserId(mActivity));
        fileShareAction.setType("file_id");
        fileShareAction.setSourceType(Dropbox.SourceType.User);
        fileShareAction.setFileId(mFileData.mediaId);
        FileShareActivity.Companion.startActivity(mActivity, fileShareAction);
    }


    /**
     * Description:初始化更多按钮的点击弹窗
     */
    private void handleMoreBtnClick() {
        final W6sPopUpView popUpView = new W6sPopUpView(getContext());

        if (DomainSettingsManager.getInstance().handleChatFileTransferEnabled()) {
            popUpView.addPopItem(-1, R.string.forwarding_item, 0);
        }

        popUpView.addPopItem(-1, R.string.open_by_other_app, 2);
        popUpView.addPopItem(-1, R.string.share_by_other_app, 3);


        popUpView.setPopItemOnClickListener((title, pos) -> {
            //转发
            if (title.equals(getResources().getString(R.string.forwarding_item))) {
                List<ChatPostMessage> chatPostMessages = new ArrayList<>();
                chatPostMessages.add(mFileTransferChatMessage);

                TransferMessageControlAction transferMessageControlAction = new TransferMessageControlAction();
                transferMessageControlAction.setSendMessageList(chatPostMessages);
                transferMessageControlAction.setSendMode(TransferMessageMode.FORWARD);
                Intent intent = TransferMessageActivity.Companion.getIntent(BaseApplicationLike.baseContext, transferMessageControlAction);

                this.getContext().startActivity(intent);
            }
            if (pos == 1) {
                Dropbox dropbox = Dropbox.convertFromChatPostMessage(mActivity, mFileTransferChatMessage);
                Intent intent = SaveToDropboxActivity.getIntent(mActivity, dropbox, mFileTransferChatMessage);
                mActivity.startActivityForResult(intent, UserDropboxFragment.REQUEST_CODE_COPY_DROPBOX);
            }
            if (pos == 2) {
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
            }
            if (pos == 3) {
                if (StringUtils.isEmpty(mFileTransferChatMessage.filePath)) {
                    return;
                }

                shareOtherApp();
            }
            popUpView.dismiss();
        });

        popUpView.pop(mIvMore);
    }
    /**
     * Description:安装软件
     */
    private void installApk() {

        EncryptCacheDisk.getInstance().getOriginalFilePathAndCheck(mFileTransferChatMessage.filePath, false, fileName -> IntentUtil.installApk(getContext(), mFileTransferChatMessage.filePath));

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
}
