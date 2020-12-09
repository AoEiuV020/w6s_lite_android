
package com.foreveross.atwork.modules.downLoad.component;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.foreverht.workplus.module.file_share.FileShareAction;
import com.foreverht.workplus.module.file_share.activity.FileShareActivity;
import com.foreverht.workplus.ui.component.dialogFragment.AtworkAlertDialog;
import com.foreverht.workplus.ui.component.popUpView.W6sPopUpView;
import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.R;
import com.foreveross.atwork.component.ProgressDialogHelper;
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
import com.foreveross.atwork.infrastructure.utils.ImageShowHelper;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.TimeUtil;
import com.foreveross.atwork.infrastructure.utils.encryption.EncryptCacheDisk;
import com.foreveross.atwork.manager.DropboxManager;
import com.foreveross.atwork.modules.dropbox.activity.SaveToDropboxActivity;
import com.foreveross.atwork.modules.dropbox.fragment.UserDropboxFragment;
import com.foreveross.atwork.modules.file.dao.RecentFileDaoService;
import com.foreveross.atwork.modules.file.util.FileMediaTypeUtil;
import com.foreveross.atwork.modules.group.activity.TransferMessageActivity;
import com.foreveross.atwork.modules.group.module.TransferMessageControlAction;
import com.foreveross.atwork.modules.group.module.TransferMessageMode;
import com.foreveross.atwork.modules.voip.utils.VoipHelper;
import com.foreveross.atwork.utils.AtworkToast;
import com.foreveross.atwork.utils.AtworkUtil;
import com.foreveross.atwork.utils.GifChatHelper;
import com.foreveross.atwork.utils.IntentUtil;
import com.foreveross.atwork.utils.OfficeHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import pl.droidsonroids.gif.GifDrawable;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

import static com.foreveross.atwork.component.DownloadPagerView.REFRESH_DOWN_LOAD_FILE_LIST;
import static com.foreveross.atwork.modules.downLoad.activity.MyDownLoadActivity.REFRESH_DOWN_LOAD_VIEW_PAGER;

/**
 * Created by wuzejie on 20/1/13.
 * Description:我的下载的文件详情
 */
public class DownloadFileDetailView extends LinearLayout {

    public static final int MSG_DELETE_RECENT_DOWN_LOAD_FILE_SUCCESS = 0x1010;
    public static final int MSG_GET_RECENT_DOWN_LOAD_FILES_SUCCESS = 0x1020;

    private Activity mActivity;

    private FileData mFileData;

    private ImageView mIvMore;
    //文件图标
    private ImageView mIconView;
    //文件名
    private TextView mFileNameView;
    //文件大小
    private TextView mTvFileSize;
    //文件过期时间
    private TextView mTvOverdueTime;
    //用第三方应用打开按扭
    private TextView mOpenOtherAppButton;
    //用第三方应用分享按扭
    private TextView mShareOtherAppButton;

    //文件详情页
    private LinearLayout mFileTransferLayout;
    //TODO：begin:图片预览
    //图片预览
    private LinearLayout mImageDetailLayout;
    private PhotoView mImagePhotoView;
    private PhotoViewAttacher mViewAttacher;
    private ProgressDialogHelper mProgressDialogHelper;

    private void showGif(FileData fileData) {
        boolean isGif = GifChatHelper.isGif(fileData.filePath);
        if (isGif) {
            showGif(mImagePhotoView, fileData.filePath);
            return;
        }
        displayLocalImage(fileData);
    }
    private void displayLocalImage(FileData fileData) {
        if (!TextUtils.isEmpty(fileData.filePath)) {
            mProgressDialogHelper.dismiss();
            Bitmap bitmap = ImageShowHelper.getRotateImageBitmap(fileData.filePath, false);
            if (bitmap!= null) {
                displayImage(bitmap);
                return;
            }
        }
        AtworkToast.showResToast(R.string.load_image_timeout);
        mProgressDialogHelper.dismiss();

    }
    private void displayImage(Bitmap bitmap) {
        if (mImagePhotoView == null) {
            return;
        }
        mImagePhotoView.setImageBitmap(bitmap);

        //TODO：水印
//        if (isWatermarkEnable()) {
//            mWatermarkView.setVisibility(VISIBLE);
//            WaterMarkHelper.setEmployeeWatermarkByOrgId(mActivity, mWatermarkView, mDropbox.mSourceId);
//        }

    }
    private void showGif(ImageView view, String path) {
        try {
            //TODO：水印
//            if (isWatermarkEnable()) {
//                mWatermarkView.setVisibility(VISIBLE);
//                WaterMarkHelper.setEmployeeWatermarkByOrgId(mActivity, mWatermarkView, mDropbox.mSourceId);
//            }
            GifDrawable gifDrawable = new GifDrawable(path);
            Bitmap holderBitmap = gifDrawable.getCurrentFrame();
            view.setImageBitmap(holderBitmap);
            view.setImageDrawable(gifDrawable);
            holderBitmap.recycle();
            holderBitmap = null;

        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e2) {
            e2.printStackTrace();
        }
        mProgressDialogHelper.dismiss();
    }


    //TODO：end:图片预览


    //文件详情
    private static String FILE_TITLE = BaseApplicationLike.baseContext.getResources().getString(R.string.file_transfer_file_title);


    private int mWhat;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mWhat = msg.what;
            switch (mWhat) {

                case MSG_DELETE_RECENT_DOWN_LOAD_FILE_SUCCESS:
                    sendBroadCast();
                    break;
            }
        }
    };
    public static void sendBroadCast() {
        LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).sendBroadcast(new Intent(REFRESH_DOWN_LOAD_VIEW_PAGER));
        LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).sendBroadcast(new Intent(REFRESH_DOWN_LOAD_FILE_LIST));
    }

    public DownloadFileDetailView(Activity context, FileData fileData) {
        super(context);
        setOrientation(LinearLayout.VERTICAL);
        mActivity = context;
        mFileData = fileData;
        initView();
        registerListener();
        //previewLocal();
    }
    public void onResume() {

    }
    private void initView() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.fragment_file_transfer, this);
        mIvMore = view.findViewById(R.id.title_bar_main_more_btn);
        mIvMore.setVisibility(VISIBLE);
        mIconView = view.findViewById(R.id.file_transfer_file_icon);
        mFileNameView = view.findViewById(R.id.file_transfer_file_name);
        mTvFileSize = view.findViewById(R.id.file_size);
        mTvFileSize.setVisibility(GONE);
        mTvOverdueTime = view.findViewById(R.id.overdue_time);
        mTvOverdueTime.setVisibility(GONE);
        mOpenOtherAppButton = view.findViewById(R.id.file_transfer_open_with_others);
        mOpenOtherAppButton.setVisibility(VISIBLE);
        mShareOtherAppButton = view.findViewById(R.id.file_transfer_share_with_others);
        mShareOtherAppButton.setVisibility(VISIBLE);
        ((TextView) (view.findViewById(R.id.title_bar_chat_detail_name))).setText(FILE_TITLE);
        mFileNameView.setVisibility(VISIBLE);
        mFileNameView.setText(mFileData.title);
        mIconView.setImageResource(FileMediaTypeUtil.getFileTypeIcon(mFileData.fileType));
        mFileTransferLayout = view.findViewById(R.id.file_transfer_layout);
        mImageDetailLayout = view.findViewById(R.id.image_detail_layout);
        mImagePhotoView = mImageDetailLayout.findViewById(R.id.image_photo_view);
        mViewAttacher = new PhotoViewAttacher(mImagePhotoView);
        mProgressDialogHelper = new ProgressDialogHelper(mActivity);

        //判断是否图片预览
//        if(mFileData.fileType == FileData.FileType.File_Image){
//            mProgressDialogHelper.show();
//            mFileTransferLayout.setVisibility(GONE);
//            mImageDetailLayout.setVisibility(VISIBLE);
//            showGif(mFileData);
//        }
    }

    private void registerListener() {

        if (mViewAttacher == null) {
            mViewAttacher = new PhotoViewAttacher(mImagePhotoView);
        }
        mViewAttacher.setOnLongClickListener(v -> {

            handleMoreBtnClick();
            return true;
        });
        mViewAttacher.setOnPhotoTapListener((view, x, y) -> {this.destroy();});

        mIvMore.setOnClickListener(v -> {
            handleMoreBtnClick();
        });
        mOpenOtherAppButton.setOnClickListener(v -> {
            if (StringUtils.isEmpty(mFileData.filePath)) {
                return;
            }
            if (mFileData.filePath.toLowerCase().endsWith(".apk")) {

                installApk();

            } else {
                if (VoipHelper.isHandlingVoipCall() && (FileData.FileType.File_Audio == mFileData.fileType || FileData.FileType.File_Video == mFileData.fileType)) {
                    AtworkToast.showResToast(R.string.alert_is_handling_voip_meeting_click_voip);

                } else {

                    previewOtherApp();

                }
            }

        });
        mShareOtherAppButton.setOnClickListener(v -> {
            if (StringUtils.isEmpty(mFileData.filePath)) {
                return;
            }

            shareOtherApp();
        });
    }

    /**
     * Description:初始化更多按钮的点击弹窗
     */
    private void handleMoreBtnClick() {
        final W6sPopUpView popUpView = new W6sPopUpView(getContext());

        if (DomainSettingsManager.getInstance().handleChatFileTransferEnabled()) {
            popUpView.addPopItem(-1, R.string.forwarding_item, 0);
            //分享
            popUpView.addPopItem(-1, R.string.share, 5);
        }
        //发邮件
        popUpView.addPopItem(-1, R.string.send_email, 2);

        //文件属性
        popUpView.addPopItem(-1, R.string.file_attr, 3);
        //删除
        popUpView.addPopItem(-1, R.string.delete, 4);



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

            if (pos == 1) {

                FileTransferChatMessage fileTransferChatMessage = FileTransferChatMessage.getFIleTransferChatMessageFromFileData(mFileData);
                Dropbox dropboxTransfer = Dropbox.convertFromChatPostMessage(mActivity, fileTransferChatMessage);
                Intent intent = SaveToDropboxActivity.getIntent(mActivity, dropboxTransfer, fileTransferChatMessage);
                mActivity.startActivityForResult(intent, UserDropboxFragment.REQUEST_CODE_COPY_DROPBOX);

            }
            if (pos == 2) {
                Dropbox dropbox = Dropbox.convertFromFilePath(mActivity, mFileData.filePath, mFileData.mediaId);
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
                shareFile();
            }


            popUpView.dismiss();
        });

        popUpView.pop(mIvMore);
    }
    /**
     * Description:本地预览
     */
    private void previewLocal() {
        if (OfficeHelper.isSupportType(mFileData.filePath)) {
            EncryptCacheDisk.getInstance().getOriginalFilePathAndCheck(mFileData.filePath, false, fileName -> {

                OfficeHelper.previewByX5(getContext(), fileName);

            });
        }
        //TODO:后缀名为mp4
        if(mFileData.fileType == FileData.FileType.File_Video){
            EncryptCacheDisk.getInstance().getOriginalFilePathAndCheck(mFileData.filePath, false, fileName -> {
                IntentUtil.previewIntent(getContext(), fileName, mFileData.fileType.getFileType());
            });
        }

    }
    /**
     * Description:安装软件
     */
    private void installApk() {

        EncryptCacheDisk.getInstance().getOriginalFilePathAndCheck(mFileData.filePath, false, fileName -> IntentUtil.installApk(getContext(), mFileData.filePath));

    }
    /**
     * Description:使用其他应用进行预览
     */
    private void previewOtherApp() {

        EncryptCacheDisk.getInstance().getOriginalFilePathAndCheck(mFileData.filePath, false, fileName -> {
            IntentUtil.previewIntent(getContext(), fileName, mFileData.fileType.getFileType());
        });

    }
    /**
     * Description:分享到其他应用上
     */
    private void shareOtherApp() {

        EncryptCacheDisk.getInstance().getOriginalFilePathAndCheck(mFileData.filePath, false, fileName -> {
            IntentUtil.shareIntent(getContext(), fileName);
        });

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
     * 删除文件
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


    public void setFileData(FileData fileData){
        mFileData = fileData;
    }

    public void destroy(){
        mViewAttacher = null;
    }
}
