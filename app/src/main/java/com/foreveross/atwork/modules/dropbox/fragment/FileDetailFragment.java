package com.foreveross.atwork.modules.dropbox.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.foreverht.cache.DropboxCache;
import com.foreverht.cache.WatermarkCache;
import com.foreverht.db.service.repository.DropboxRepository;
import com.foreverht.workplus.ui.component.dialogFragment.AtworkAlertDialog;
import com.foreverht.workplus.ui.component.dialogFragment.CommonPopSelectData;
import com.foreverht.workplus.ui.component.dialogFragment.W6sSelectDialogFragment;
import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.BaseNetWorkListener;
import com.foreveross.atwork.api.sdk.UrlConstantManager;
import com.foreveross.atwork.api.sdk.dropbox.DropboxAsyncNetService;
import com.foreveross.atwork.api.sdk.dropbox.requestJson.FileTranslateRequest;
import com.foreveross.atwork.api.sdk.upload.MediaCenterNetManager;
import com.foreveross.atwork.component.ProgressDialogHelper;
import com.foreveross.atwork.component.viewPager.ViewPagerFixed;
import com.foreveross.atwork.infrastructure.beeworks.BeeWorks;
import com.foreveross.atwork.infrastructure.manager.DomainSettingsManager;
import com.foreveross.atwork.infrastructure.model.FilePreviewOnlineData;
import com.foreveross.atwork.infrastructure.model.Watermark;
import com.foreveross.atwork.infrastructure.model.WebViewControlAction;
import com.foreveross.atwork.infrastructure.model.dropbox.Dropbox;
import com.foreveross.atwork.infrastructure.model.dropbox.DropboxConfig;
import com.foreveross.atwork.infrastructure.model.file.FileData;
import com.foreveross.atwork.infrastructure.permissions.PermissionsManager;
import com.foreveross.atwork.infrastructure.permissions.PermissionsResultAction;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.AtWorkDirUtils;
import com.foreveross.atwork.infrastructure.utils.DensityUtil;
import com.foreveross.atwork.infrastructure.utils.FileUtil;
import com.foreveross.atwork.infrastructure.utils.ImageShowHelper;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.ViewUtil;
import com.foreveross.atwork.infrastructure.utils.encryption.EncryptCacheDisk;
import com.foreveross.atwork.manager.DropboxManager;
import com.foreveross.atwork.modules.app.activity.WebViewActivity;
import com.foreveross.atwork.modules.app.route.UrlRouteHelper;
import com.foreveross.atwork.modules.dropbox.activity.DropboxBaseActivity;
import com.foreveross.atwork.modules.dropbox.activity.FileDetailActivity;
import com.foreveross.atwork.modules.dropbox.activity.MoveToDropboxActivity;
import com.foreveross.atwork.modules.dropbox.activity.SaveToDropboxActivity;
import com.foreveross.atwork.modules.dropbox.adapter.TranslateViewPagerAdapter;
import com.foreveross.atwork.modules.file.util.FileMediaTypeUtil;
import com.foreveross.atwork.modules.voip.utils.VoipHelper;
import com.foreveross.atwork.support.BackHandledFragment;
import com.foreveross.atwork.utils.AtworkToast;
import com.foreveross.atwork.utils.AtworkUtil;
import com.foreveross.atwork.utils.ErrorHandleUtil;
import com.foreveross.atwork.utils.GifChatHelper;
import com.foreveross.atwork.utils.ImageCacheHelper;
import com.foreveross.atwork.utils.IntentUtil;
import com.foreveross.atwork.utils.OfficeHelper;
import com.foreveross.atwork.utils.ThemeResourceHelper;
import com.foreveross.atwork.utils.watermark.WaterMarkHelper;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pl.droidsonroids.gif.GifDrawable;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.foreveross.atwork.modules.dropbox.activity.MoveToDropboxActivity.REQUEST_CODE_MOVE_DROPBOX;
import static com.foreveross.atwork.modules.dropbox.fragment.UserDropboxFragment.KEY_INTENT_MODIFY_DROPBOX_CALLBACK;
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
 *                           |__|
 * Created by reyzhang22 on 16/9/26.
 */

public class FileDetailFragment extends BackHandledFragment {

    private static final String DATA_PREVIEW_MODE = "DATA_PREVIEW_MODE";
    private static final String DATA_PREVIEW_TRANSLATE_LIST = "DATA_PREVIEW_TRANSLATE_LIST";

    private static final int REQUEST_CODE_COPY_DROPBOX_FILE_DETAIL = 0X231;

    private View mFileDetailLayout;

    private View mImageDetailLayout;

    private Dropbox mDropbox;

    private ImageView mBack;

    private TextView mTitle;

    private ImageView mMore;

    private View mFunctionView;
    private View mDownloadFunction;
    private TextView mDownloadText;
    private View mSendToContactFunction;
    private View mSaveToDropboxFunction;
    private View mVLineSaveToDropboxFunction;
    private View mMoreFunction;
    private View mLlOpenByOtherApp;

    private ImageView mIvOpenByOtherApp;
    private ImageView mIvFileDownload;
    private ImageView mIvSave;
    private ImageView mIvMore;
    private ImageView mIvSendToContact;


    private View mDownloadLayout;
    private TextView mPause;
    private TextView mDownloadProgress;

    private ImageView mFileIcon;
    private TextView mFileName;
    private TextView mSupportPreviewOnline;
    private TextView mPreviewOnline;
    private TextView mTvFileOpenLocal;

    private View mFileDetailView;
    private ViewPagerFixed mTranslateViewPager;
    private TranslateViewPagerAdapter mTranslateViewPagerAdapter;

    private ProgressDialogHelper mProgressDialogHelper;
    private PhotoView mImagePhotoView;

    private PhotoViewAttacher mViewAttacher;

    private DropboxConfig mDropboxConfig = new DropboxConfig();
    private View mBottomView;

    private View mWatermarkView;

    private Watermark mWatermark = new Watermark();

    private boolean mIsPreviewMode = false;

    private boolean mLoadMore = false;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_file_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        handleBundle();
        initData();
        registerListener();

        refreshUI();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Dropbox.DownloadStatus.Downloading.equals(mDropbox.mDownloadStatus)) {
            DropboxManager.getInstance().pauseDownload(mActivity, mDropbox);
            mDropbox = DropboxCache.getInstance().getDropboxCache(mDropbox.mFileId);
            onDropboxDownload();

            return;
        }
        changeBottomLayout(false);
    }

    @Override
    protected void findViews(View view) {
        mFileDetailLayout = view.findViewById(R.id.file_detail_layout);
        mBack = mFileDetailLayout.findViewById(R.id.title_bar_chat_detail_back);
        mTitle = mFileDetailLayout.findViewById(R.id.title_bar_chat_detail_name);
        mMore = mFileDetailLayout.findViewById(R.id.title_bar_main_more_btn);
        mMore.setVisibility(GONE);
        mFileIcon = mFileDetailLayout.findViewById(R.id.file_type_icon);
        mFileName = mFileDetailLayout.findViewById(R.id.file_name);
        mSupportPreviewOnline = mFileDetailLayout.findViewById(R.id.is_support_preview_online);
        mPreviewOnline = mFileDetailLayout.findViewById(R.id.preview_online);
        mTvFileOpenLocal = mFileDetailLayout.findViewById(R.id.file_transfer_open_local);
        mFunctionView = mFileDetailLayout.findViewById(R.id.file_detail_bottom_function);
        mLlOpenByOtherApp = mFunctionView.findViewById(R.id.ll_open_by_other_app);
        mIvOpenByOtherApp = mFunctionView.findViewById(R.id.iv_file_open_with_others);
        mIvFileDownload = mFunctionView.findViewById(R.id.iv_file_download);
        mDownloadFunction = mFunctionView.findViewById(R.id.download_function);
        mDownloadText = mDownloadFunction.findViewById(R.id.download_text);
        mIvSendToContact = mFunctionView.findViewById(R.id.iv_send_to_contact);
        mSendToContactFunction = mFunctionView.findViewById(R.id.send_to_contact_function);
        mIvSave = mFunctionView.findViewById(R.id.iv_save);
        mSaveToDropboxFunction = mFunctionView.findViewById(R.id.save_to_dropbox_function);
        mVLineSaveToDropboxFunction = mFunctionView.findViewById(R.id.v_line_save_to_dropbox_function);
        mIvMore = mFunctionView.findViewById(R.id.iv_more);
        mMoreFunction = mFunctionView.findViewById(R.id.more_function);
        mDownloadLayout = view.findViewById(R.id.file_detail_download_layout);
        mDownloadProgress = mDownloadLayout.findViewById(R.id.download_progress);
        mPause = mDownloadLayout.findViewById(R.id.download_pause);
        mTranslateViewPager = view.findViewById(R.id.preview_image_pager);
        mFileDetailView = view.findViewById(R.id.file_detail_view);
        mBottomView = view.findViewById(R.id.file_detail_bottom_layout);

        mImageDetailLayout = view.findViewById(R.id.image_detail_layout);
        mImagePhotoView = mImageDetailLayout.findViewById(R.id.image_photo_view);
        mViewAttacher = new PhotoViewAttacher(mImagePhotoView);
        mProgressDialogHelper = new ProgressDialogHelper(mActivity);
        mWatermarkView = view.findViewById(R.id.watermark_view);
    }

    private void refreshUI() {
        int size = DensityUtil.dip2px(25);
        Drawable saveDrawable = ThemeResourceHelper.getThemeResourceBitmapDrawable(getActivity(), "file_save_to_disk", size);
        Drawable moreDrawable = ThemeResourceHelper.getThemeResourceBitmapDrawable(getActivity(), "file_more", size);
        Drawable sendToContactDrawable = ThemeResourceHelper.getThemeResourceBitmapDrawable(getActivity(), "file_send_to_contact", size);
        Drawable downloadDrawable = ThemeResourceHelper.getThemeResourceBitmapDrawable(getActivity(), "file_download", size);
        Drawable pauseDrawable = ThemeResourceHelper.getThemeResourceBitmapDrawable(getActivity(), "file_pause_download", size);
        Drawable openOtherDrawable = ThemeResourceHelper.getThemeResourceBitmapDrawable(getActivity(), "file_open_with_others", size);

        if(null != saveDrawable) {
            mIvSave.setImageDrawable(saveDrawable);
        }

        if(null != moreDrawable) {
            mIvMore.setImageDrawable(moreDrawable);

        }

        if(null != sendToContactDrawable) {
            mIvSendToContact.setImageDrawable(sendToContactDrawable);
        }

        if(null != downloadDrawable) {
            mIvFileDownload.setImageDrawable(downloadDrawable);
        }

        if(null != pauseDrawable) {
            mPause.setCompoundDrawablesWithIntrinsicBounds(null, pauseDrawable, null, null);
        }

        if(null != openOtherDrawable) {
            mIvOpenByOtherApp.setImageDrawable(openOtherDrawable);
        }

        ViewUtil.setVisible(mSaveToDropboxFunction, AtworkConfig.OPEN_DROPBOX);
        ViewUtil.setVisible(mVLineSaveToDropboxFunction, AtworkConfig.OPEN_DROPBOX);
    }

    private void handleBundle() {
        Bundle bundle = getArguments();
        if (bundle == null) {
            return;
        }
        mDropbox = bundle.getParcelable(FileDetailActivity.INTENT_KEY_DROPBOX_FILE);
        //主动再查一次数据库，因为，有可能存在的是当前的网盘文件的某些状态，在上一个页面传过来的不是正真的当前状态，比如下载状态，下载进度等；
        mDropbox = DropboxRepository.getInstance().getDropboxByFileId(mDropbox.mFileId);
        mDropboxConfig = (DropboxConfig)bundle.getSerializable(FileDetailActivity.INTENT_KEY_DROPBOX_CONFIG);
        if (!DropboxManager.hasOpsAuth(mDropboxConfig)) {
            mBottomView.setVisibility(View.GONE);
        }
        mWatermark.mSourceId = mDropbox.mSourceId;
        mWatermark.mType = Watermark.Type.DROPBOX;
    }

    private void initData() {
        if (choseViewByFileType()) {

            mProgressDialogHelper.show();

            if(AtworkConfig.SCREEN_ORIENTATION_USER_SENSOR) {
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
            }

            //如果是GIF 并且本地存在该gif文件
            if ("gif".equalsIgnoreCase(mDropbox.mExtension)) {
                if (!TextUtils.isEmpty(mDropbox.mLocalPath) && new File(mDropbox.mLocalPath).exists()) {
                    showGif();
                    return;
                }
                downloadDropbox(mDropbox);
                return;
            }
            displayImageByMediaId();
            return;
        }

//        StatusBarUtil.setColorNoTranslucent(getActivity(), SkinHelper.getMainColor());

        mTitle.setText(R.string.file_detail);
        mFileIcon.setBackgroundResource(FileMediaTypeUtil.getFileTypeByExtension(mDropbox.mExtension));
        mFileName.setText(StringUtils.middleEllipse(mDropbox.mFileName, 40, 10, 12, 12));

        mSupportPreviewOnline.setText(isSupportPreviewOnline() ? getString(R.string.preview_tip) : showPreviewOnlineTip());
        handlePreviewOnlineView();
    }

    private void handlePreviewOnlineView() {
        mPreviewOnline.setVisibility(isSupportPreviewOnline() ? VISIBLE : GONE);
    }

    private boolean isSupportPreviewOnline() {
        return Dropbox.DropboxFileType.File.equals(mDropbox.mFileType);
    }

    private String showPreviewOnlineTip() {
        if (DropboxManager.hasOpsAuth(mDropboxConfig)) {
            return getString(R.string.not_support_preview_online);
        }
        return getString(R.string.not_support_preview_online_no_right);
    }

    private void showGif() {
        boolean isGif = GifChatHelper.isGif(mDropbox.mLocalPath);
        if (isGif) {
            showGif(mImagePhotoView, mDropbox.mLocalPath);
            return;
        }
        displayLocalImage();
    }

    private void displayImageByMediaId() {

        if (!TextUtils.isEmpty(mDropbox.mFileId)) {
            String accessToken = LoginUserInfo.getInstance().getLoginUserAccessToken(mActivity);
            String url = String.format(UrlConstantManager.getInstance().dropboxMediaFile(), mDropbox.mDomainId, mDropbox.mSourceType.toString(), mDropbox.mSourceId, mDropbox.mFileId, accessToken);
            ImageCacheHelper.displayImage(url, mImagePhotoView, ImageCacheHelper.getDropboxImageOptions(), new ImageCacheHelper.ImageLoadedListener() {
                @Override
                public void onImageLoadedComplete(Bitmap bitmap) {
                    mProgressDialogHelper.dismiss();
                    displayImage(bitmap);

                }

                @Override
                public void onImageLoadedFail() {
                    if (TextUtils.isEmpty(mDropbox.mLocalPath)) {
                        AtworkToast.showResToast(R.string.load_image_timeout);
                        mProgressDialogHelper.dismiss();
                        return;
                    }
                    displayLocalImage();
                }


            });
            return;
        }
        displayLocalImage();
    }

    private void displayLocalImage() {
        if (!TextUtils.isEmpty(mDropbox.mLocalPath)) {
            mProgressDialogHelper.dismiss();
            Bitmap bitmap = ImageShowHelper.getRotateImageBitmap(mDropbox.mLocalPath, false);
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

        if (isWatermarkEnable()) {
            mWatermarkView.setVisibility(VISIBLE);
            WaterMarkHelper.setEmployeeWatermarkByOrgId(mActivity, mWatermarkView, mDropbox.mSourceId);
        }

    }

    private void onImageLongClick() {
        String[] array = null;
        if (!DomainSettingsManager.getInstance().handleEmailSettingsFeature()) {
            array = getResources().getStringArray(R.array.dropbox_image_detail_more_without_email);
        } else {
            array = getResources().getStringArray(R.array.dropbox_image_detail_more);
        }
        popUp(array);
    }

    private void popUp(String[] array) {
        ArrayList<String> itemList = new ArrayList<>();
        itemList.addAll(Arrays.asList(array));

        if (!DomainSettingsManager.getInstance().handleEmailSettingsFeature()) {
            itemList.remove(getString(R.string.send_email));
        }

        if(!needSaveImg()) {
            itemList.remove(getString(R.string.save_to_phone));
        }

        StringBuilder name = new StringBuilder(mDropbox.mFileName);
        if (!TextUtils.isEmpty(mDropbox.mExtension)) {
            if (!mDropbox.mExtension.startsWith(".")) {
                name.append(".");
            }
            name.append(mDropbox.mExtension);
        }

        W6sSelectDialogFragment w6sSelectDialogFragment = new W6sSelectDialogFragment();
        w6sSelectDialogFragment.setData(new CommonPopSelectData(itemList, null))
                .setOnClickItemListener((position, value) -> {
                    if (TextUtils.isEmpty(value)) {
                        return;
                    }
                    dropboxItemClick(value);
                })
                .show(getFragmentManager(), "TEXT_POP_DIALOG");
    }

    private boolean needSaveImg() {
        if(AtworkConfig.ENCRYPT_CONFIG.isImageSaveIgnoringEncrypt()) {
            return true;
        }

        if(AtworkConfig.OPEN_DISK_ENCRYPTION) {
            return false;
        }

        return true;
    }

    private void dropboxItemClick(String tag) {
        if (getString(R.string.send_to_contact).equalsIgnoreCase(tag)) {
            DropboxManager.getInstance().doCommandSendToContact(mActivity, mDropbox);
            return;
        }

        if (getString(R.string.send_email).equalsIgnoreCase(tag)) {

            PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(mActivity, new String[]{Manifest.permission.WRITE_CONTACTS}, new PermissionsResultAction() {
                @Override
                public void onGranted() {
                    DropboxManager.getInstance().doCommandSendEmail(mActivity, FileDetailFragment.this, mDropbox);

                }

                @Override
                public void onDenied(String permission) {
                    final AtworkAlertDialog alertDialog = AtworkUtil.getAuthSettingAlert(mActivity, permission);
                    alertDialog.setOnDismissListener(dialog -> {
                        if(alertDialog.shouldHandleDismissEvent) {
                            DropboxManager.getInstance().doCommandSendEmail(mActivity, FileDetailFragment.this, mDropbox);


                        }

                    });

                    alertDialog.show();
                }
            });


            return;
        }

        if (getString(R.string.rename).equalsIgnoreCase(tag)) {
            if (!DropboxManager.getInstance().hasRightToDoCommand(mActivity, mDropbox, mDropbox.mSourceId)) {
                AtworkToast.showResToast(R.string.no_right_rename_file);
                return;
            }
            DropboxManager.getInstance().doCommandRename(mActivity, mDropbox);
            return;
        }

        if (getString(R.string.move).equalsIgnoreCase(tag)) {
            if (!DropboxManager.getInstance().hasRightToDoCommand(mActivity, mDropbox, mDropbox.mSourceId)) {
                AtworkToast.showResToast(R.string.no_right_move_file);
                return;
            }
            doCommandMove(mDropbox);
            return;
        }

        if (getString(R.string.save_to_phone).equalsIgnoreCase(tag)) {
            doCommandSaveToPhone(mDropbox);
            return;
        }

        if (getString(R.string.delete).equalsIgnoreCase(tag)) {
            if (!DropboxManager.getInstance().hasRightToDoCommand(mActivity, mDropbox, mDropbox.mSourceId)) {
                AtworkToast.showResToast(R.string.no_right_delete_file);
                return;
            }
            AtworkAlertDialog dialog = new AtworkAlertDialog(mActivity);
            dialog.setTitleText(R.string.delete_these_files);
            dialog.setContent(R.string.delete_these_files_message);
            dialog.setBrightBtnText(R.string.ok);
            dialog.setDeadBtnText(R.string.cancel);
            dialog.setClickDeadColorListener(dialog1 -> dialog.dismiss());
            dialog.setClickBrightColorListener(dialog1 -> {
                onDropboxDelete();
            });
            dialog.show();

            return;
        }

        if (getString(R.string.file_attr).equalsIgnoreCase(tag)) {
            DropboxManager.getInstance().doCommandFileAttr(this, mDropbox);
            return;
        }
    }


    private boolean choseViewByFileType() {
        boolean isImageType = null != mDropbox && Dropbox.DropboxFileType.Image.equals(mDropbox.mFileType);
        mFileDetailLayout.setVisibility(isImageType ? GONE : VISIBLE);
        mImageDetailLayout.setVisibility(isImageType ? VISIBLE : GONE);
        return isImageType;
    }

    private void registerListener() {
        mBack.setOnClickListener(view -> {
            onBackPressed();
        });

        mDownloadFunction.setOnClickListener(view -> {
            onDropboxDownload();
        });

        mSendToContactFunction.setOnClickListener(view -> {
            DropboxManager.getInstance().doCommandSendToContact(mActivity, mDropbox);
        });

        mSaveToDropboxFunction.setOnClickListener(view -> {
            doCommandSaveToDropbox(mDropbox);
        });

        mMoreFunction.setOnClickListener(view -> {
            if (!DomainSettingsManager.getInstance().handleEmailSettingsFeature()) {
                popUp(getResources().getStringArray(R.array.dropbox_file_detail_more_without_email));
                return;
            }
            popUp(getResources().getStringArray(R.array.dropbox_file_detail_more));
        });

        mPreviewOnline.setOnClickListener(view -> {
            translateFile();
        });

        mPause.setOnClickListener(view -> {
            if (Dropbox.DownloadStatus.Downloading.equals( mDropbox.mDownloadStatus)) {
                AtworkToast.showResToast(R.string.pause_download_file);
                DropboxManager.getInstance().breakDropboxDownload(mActivity, mDropbox);
                changeBottomLayout(false);
                return;
            }
            onDropboxDownload();
        });

        mLlOpenByOtherApp.setOnClickListener(view -> {
            if (StringUtils.isEmpty(mDropbox.mLocalPath)) {
                return;
            }
            if (mDropbox.mExtension.endsWith(".apk")) {
                IntentUtil.installApk(getContext(), mDropbox.mLocalPath);

            } else {
                FileData.FileType fileTypeByExtension = FileData.getFileTypeByExtension(mDropbox.mExtension);


                if (VoipHelper.isHandlingVoipCall() && (FileData.FileType.File_Audio == fileTypeByExtension || FileData.FileType.File_Video == fileTypeByExtension)) {
                    AtworkToast.showResToast(R.string.alert_is_handling_voip_meeting_click_voip);

                } else {

                    EncryptCacheDisk.getInstance().getOriginalFilePathAndCheck(mDropbox.mLocalPath, false, fileName -> IntentUtil.previewIntent(getContext(), fileName, fileTypeByExtension.getFileType()));

                }


            }
        });

        mTvFileOpenLocal.setOnClickListener(v -> {
            previewLocal();
        });

        if (mViewAttacher == null) {
            mViewAttacher = new PhotoViewAttacher(mImagePhotoView);
        }
        mViewAttacher.setOnLongClickListener(v -> {
            if (!DropboxManager.hasOpsAuth(mDropboxConfig)) {
                return true;
            }
            onImageLongClick();
            return true;
        });
        mViewAttacher.setOnPhotoTapListener((view, x, y) -> {onBackPressed();});
    }

    private void onlyofficePreviewOnline(){
        String downloadUrl = String.format(UrlConstantManager.getInstance().dropboxMediaFile(), mDropbox.mDomainId, mDropbox.mSourceType, mDropbox.mSourceId, mDropbox.mFileId, LoginUserInfo.getInstance().getLoginUserAccessToken(getContext()));
        FilePreviewOnlineData filePreviewOnlineData = new FilePreviewOnlineData(mDropbox.mFileName,mDropbox.mFileId,mDropbox.mExtension,downloadUrl);

        String urlService = "file:///android_asset/www/archive/index.html";
        WebViewControlAction webViewControlActionService = WebViewControlAction.newAction().setUrl(urlService).setFilePreviewOnlineData(filePreviewOnlineData);
        startActivity(WebViewActivity.getIntent(getActivity(), webViewControlActionService));
    }

    private void translateFile() {
        mProgressDialogHelper.show();

        FileTranslateRequest request = new FileTranslateRequest();

        request.mFileType = FileData.getFileTypeByExtension(mDropbox.mExtension).getString();
        if(!StringUtils.isEmpty(mDropbox.mMediaId)) {
            request.mMediaId = mDropbox.mMediaId;
            doTranslateFile(request);
            return;
        }

        DropboxManager.getInstance().getDropboxInfo(mActivity, mDropbox, new BaseNetWorkListener<Dropbox>() {
            @Override
            public void onSuccess(Dropbox dropbox) {
                request.mMediaId = dropbox.mMediaId;
                doTranslateFile(request);

            }

            @Override
            public void networkFail(int errorCode, String errorMsg) {
                mProgressDialogHelper.dismiss();

                ErrorHandleUtil.handleError(ErrorHandleUtil.Module.Dropbox, errorCode, errorMsg);


            }
        });
    }

    private void doTranslateFile(FileTranslateRequest request) {
        if(BeeWorks.getInstance().config.isLite()) {
            mProgressDialogHelper.dismiss();


            String url = String.format(UrlConstantManager.getInstance().officePreviewUrl(), MediaCenterNetManager.getDownloadUrl(getContext(), request.mMediaId));
            UrlRouteHelper.routeUrl(mActivity, WebViewControlAction.newAction().setUrl(url));
            return;
        }

        if("ONLYOFFICE".equalsIgnoreCase(DomainSettingsManager.getInstance().getFileOnlinePreviewFeature())) {
            mProgressDialogHelper.dismiss();

            onlyofficePreviewOnline();
            return;
        }



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

    private void previewLocal() {
        EncryptCacheDisk.getInstance().getOriginalFilePathAndCheck(mDropbox.mLocalPath, false, fileName -> {

            OfficeHelper.previewByX5(getContext(), fileName);

        });
    }

    private void showPreview(FileTranslateRequest request, int totalPage,  List<String> translateList) {
        if (isAdded()) {
            if(AtworkConfig.SCREEN_ORIENTATION_USER_SENSOR) {
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
            }

            mIsPreviewMode = true;
            if (isWatermarkEnable()) {
                mWatermarkView.setVisibility(VISIBLE);
                WaterMarkHelper.setEmployeeWatermarkByOrgId(mActivity, mWatermarkView, mDropbox.mSourceId);
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
                        doTranslateFile(request);
                    }
                }

                @Override
                public void onPageScrollStateChanged(int i) {

                }
            });
        }
    }

    private void onDropboxDownload() {
        mDropbox.mDownloadStatus = Dropbox.DownloadStatus.Downloading;
        changeBottomLayout(true);
        updateProgress();
        DropboxManager.getInstance().downloadFileFromDropbox(mActivity, mDropbox, new DropboxManager.OnFileDownloadListener() {
            @Override
            public void onFileStartDownloading(Dropbox dropbox) {
                mDropbox = dropbox;

            }

            @Override
            public void onFileDownloadingProgress(long progress) {
                mDropbox.mDownloadBreakPoint = progress;
                updateProgress();
            }

            @Override

            public void onFileDownloaded(Dropbox dropbox) {
                changeBottomLayout(false);
                DropboxBaseActivity.refreshDropboxData();
            }

            @Override
            public void onFileDownloadPause(Dropbox dropbox) {
                mDropbox = dropbox;
                changeBottomLayout(false);
            }
        });
    }


    /**
     * 移动文件
     */
    private void doCommandMove(Dropbox dropbox) {
        ArrayList<String> moveList = new ArrayList<>();
        moveList.add(dropbox.mFileId);
        MoveToDropboxActivity.actionDropboxMove(mActivity, dropbox.mDomainId, dropbox.mSourceId, dropbox.mSourceType, mDropbox.mParentId, moveList);
    }

    private void doCommandSaveToPhone(Dropbox dropbox) {
        if (!TextUtils.isEmpty(dropbox.mLocalPath)) {
            File file = new File(dropbox.mLocalPath);
            if (file.exists()) {

                String destPath = AtWorkDirUtils.getInstance().getDropboxDir(LoginUserInfo.getInstance().getLoginUserUserName(mActivity), DropboxManager.getInstance().getDropboxAbsPath(mActivity, dropbox));
                if (!TextUtils.isEmpty(dropbox.mExtension) ) {
                    destPath = destPath + "." + dropbox.mExtension;
                }
                try {
                    //如果同一份文件，重命名+1
                    if (file.getAbsolutePath().equalsIgnoreCase(destPath)) {
                        int i = 1;
                        while(true) {
                            String newDestPath = AtWorkDirUtils.getInstance().getDropboxDir(LoginUserInfo.getInstance().getLoginUserUserName(mActivity), DropboxManager.getInstance().getDropboxAbsPath(mActivity, dropbox)) + "(" + i +")";
                            if (!TextUtils.isEmpty(dropbox.mExtension) ) {
                                newDestPath = newDestPath + "." + dropbox.mExtension;
                            }
                            File newFile = new File(newDestPath);
                            if (!newFile.exists()) {
                                destPath = newFile.getAbsolutePath();
                                break;
                            }
                            i++;
                        }
                    }
                    FileUtil.copyFile(file.getAbsolutePath(), destPath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                File destFile = new File(destPath);
                mDropbox.mLocalPath = destPath;
                DropboxManager.getInstance().updateDropbox(mDropbox);
                DropboxBaseActivity.refreshDropboxData();

                AtworkToast.showResToast(R.string.save_image_to_mobile_success, destFile.getParent());
                if ("gif".equalsIgnoreCase(mDropbox.mExtension)) {
                    showGif();
                }
                return;
            }
        }
        AndPermission
                .with(mActivity)
                .runtime()
                .permission(Permission.WRITE_EXTERNAL_STORAGE, Permission.READ_EXTERNAL_STORAGE)
                .onGranted(granted -> {
                    downloadDropbox(dropbox);
                })
                .onDenied(denied ->  AtworkUtil.popAuthSettingAlert(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE))
                .start();


    }

    private void downloadDropbox(Dropbox dropbox) {
        mProgressDialogHelper.show();
        DropboxManager.getInstance().downloadFileFromDropbox(mActivity, dropbox, new DropboxManager.OnFileDownloadListener() {
            @Override
            public void onFileStartDownloading(Dropbox dropbox) {
                mDropbox = dropbox;
            }

            @Override
            public void onFileDownloadingProgress(long progress) {

            }

            @Override

            public void onFileDownloaded(Dropbox dropbox) {
                mProgressDialogHelper.dismiss();
                File file = new File(dropbox.mLocalPath);
                mDropbox = dropbox;
                DropboxRepository.getInstance().insertOrUpdateDropbox(dropbox);

                DropboxBaseActivity.refreshDropboxData();

                if ("gif".equalsIgnoreCase(dropbox.mExtension)) {
                    showGif();
                    return;
                }
                AtworkToast.showResToast(R.string.save_image_to_mobile_success, file.getParent());
            }

            @Override
            public void onFileDownloadPause(Dropbox dropbox) {
                mDropbox = dropbox;
                mProgressDialogHelper.dismiss();
                AtworkToast.showResToast(R.string.download_org_image_fail);

            }
        });
    }


    @Override
    protected boolean onBackPressed() {
        if (mDropbox != null &&
                (Dropbox.SourceType.Discussion.equals(mDropbox.mSourceType) ||
                        Dropbox.SourceType.Organization.equals(mDropbox.mSourceType))) {
            mActivity.setResult(Activity.RESULT_OK);
        }
        finish(true);
        return false;

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_MODIFY_DROPBOX && resultCode == Activity.RESULT_OK) {
            List<Dropbox> list = data.getParcelableArrayListExtra(KEY_INTENT_MODIFY_DROPBOX_CALLBACK);
            if (ListUtil.isEmpty(list)) {
                return;
            }
            mDropbox = list.get(0);
            mFileName.setText(StringUtils.middleEllipse(mDropbox.mFileName, 40, 10, 12, 12));
            DropboxBaseActivity.refreshDropboxData();
            return;
        }

        if (requestCode == REQUEST_CODE_COPY_DROPBOX_FILE_DETAIL || requestCode == REQUEST_CODE_MOVE_DROPBOX && resultCode == Activity.RESULT_OK) {
            DropboxBaseActivity.refreshDropboxData();
            return;
        }
    }

    public void onDropboxDelete() {
        List<String> deleteList = new ArrayList<>();
        deleteList.add(mDropbox.mFileId);
        DropboxManager.getInstance().deleteDropboxFile(mActivity, deleteList, mDropbox.mParentId, mDropbox.mDomainId, mDropbox.mSourceType, mDropbox.mSourceId, new DropboxAsyncNetService.OnDropboxListener() {
            @Override
            public void onDropboxOpsSuccess(List<Dropbox> dropboxes) {
                DropboxManager.getInstance().deleteLocalDropboxFile(mActivity, deleteList);
                DropboxBaseActivity.refreshDropboxData();
                mActivity.finish();
            }

            @Override
            public void onDropboxOpsFail(int status) {
                if (status == 204003) {
                    if (DropboxManager.isMyDropbox(mActivity, mDropbox)) {
                        AtworkToast.showResToast(R.string.no_right_ops_this_folder);
                        return;
                    }
                    AtworkToast.showResToast(R.string.no_right_delete_file);
                    return;
                }
            }
        });
    }

    public void changeBottomLayout(boolean isDownloadMode) {

        mDownloadLayout.setVisibility(isDownloadMode ? VISIBLE : GONE);
        mFunctionView.setVisibility(isDownloadMode ? GONE : VISIBLE);
        if (Dropbox.DownloadStatus.Pause.equals(mDropbox.mDownloadStatus) || Dropbox.DownloadStatus.Fail.equals(mDropbox.mDownloadStatus)) {
            mDownloadText.setText(AtworkApplicationLike.getResourceString(R.string.download_continue));
        }



        if (Dropbox.DownloadStatus.Downloaded.equals(mDropbox.mDownloadStatus) || Dropbox.UploadStatus.Not_Upload.equals(mDropbox.mUploadStatus)) {
            if (TextUtils.isEmpty(mDropbox.mLocalPath)) {
                return;
            }
            File file = new File(mDropbox.mLocalPath);
            if (!file.exists()) {
                return;
            }
            mDownloadFunction.setVisibility(GONE);


            if (!AtworkConfig.OPEN_DISK_ENCRYPTION) {
                mLlOpenByOtherApp.setVisibility(VISIBLE);
            }

            if(OfficeHelper.isSupportType(mDropbox.mLocalPath)) {
                mTvFileOpenLocal.setVisibility(VISIBLE);
                mPreviewOnline.setVisibility(VISIBLE);
                mSupportPreviewOnline.setVisibility(GONE);

            } else {
                mTvFileOpenLocal.setVisibility(GONE);
                handlePreviewOnlineView();
                mSupportPreviewOnline.setVisibility(VISIBLE);
            }
        }
    }

    private void updateProgress() {
        if (!isAdded()) {
            return;
        }
        StringBuilder stringBuilder = new StringBuilder(getString(R.string.dropbox_downloading));
        stringBuilder.append("(").append(FileUtil.formatFromSize((int)mDropbox.mDownloadBreakPoint));
        stringBuilder.append("/");
        stringBuilder.append(FileUtil.formatFromSize(mDropbox.mFileSize)).append(")");
        mDownloadProgress.setText(stringBuilder.toString());
    }

    /**
     * 保存到网盘
     */
    private void doCommandSaveToDropbox(Dropbox dropbox) {
        mActivity.startActivityForResult(SaveToDropboxActivity.getIntent(mActivity, dropbox, null),REQUEST_CODE_COPY_DROPBOX_FILE_DETAIL );
    }


    private void showGif(ImageView view, String path) {
        try {
            if (isWatermarkEnable()) {
                mWatermarkView.setVisibility(VISIBLE);
                WaterMarkHelper.setEmployeeWatermarkByOrgId(mActivity, mWatermarkView, mDropbox.mSourceId);
            }
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

    private boolean isWatermarkEnable() {
        if(AtworkConfig.DROPBOX_CONFIG.isForcedShowWaterMark()) {
            return true;
        }

        return WatermarkCache.getInstance().getWatermarkConfigCache(mWatermark);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mViewAttacher = null;
        if (mImagePhotoView != null) {
            mImagePhotoView.destroyDrawingCache();
        }
        mImagePhotoView = null;

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(DATA_PREVIEW_MODE, mIsPreviewMode);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if (null != savedInstanceState) {
            mIsPreviewMode = savedInstanceState.getBoolean(DATA_PREVIEW_MODE, false);

        }

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

    }
}
