package com.foreveross.atwork.modules.common.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import androidx.fragment.app.Fragment;

import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.net.model.UploadFileParamsMaker;
import com.foreveross.atwork.api.sdk.upload.MediaCenterNetManager;
import com.foreveross.atwork.component.ProgressDialogHelper;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.utils.UriCompat;
import com.foreveross.atwork.modules.aboutme.fragment.AvatarPopupFragment;
import com.foreveross.atwork.modules.chat.util.BurnModeHelper;
import com.foreveross.atwork.modules.file.activity.FileSelectActivity;
import com.foreveross.atwork.support.BackHandledFragment;
import com.foreveross.atwork.utils.AtworkUtil;
import com.foreveross.atwork.utils.CropHelper;
import com.foreveross.atwork.utils.IntentUtil;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

import java.io.File;
import java.util.UUID;

/**
 * Created by dasunsy on 2017/3/23.
 */

public abstract class ChangeAvatarFragment extends BackHandledFragment implements AvatarPopupFragment.OnPhotographPathListener{

    public static final int REQUEST_CODE_CHOSE_PHOTO = 0x321;
    public static final int REQUEST_CODE_CAMERA = 0x322;
    public static final int REQUEST_CODE_CROP = 0x333;

    private String mPhotographPath;
    private String mCropOutputPath;
    private Uri mImagePathUriOriginal;

    private ProgressDialogHelper mUpdatingHelper;

    protected abstract void changeAvatar(final String mediaId);

    protected  String getUploadingTip() {
        return getStrings(R.string.updating_avatar);
    }

    protected  String getUploadFailTip() {
        return getStrings(R.string.upload_avatar_fail);
    }

    protected ProgressDialogHelper getUpdatingHelper() {
        return mUpdatingHelper;
    }


    protected String getCropOutputPath() {
        return mCropOutputPath;
    }

    protected void dismissUpdatingHelper() {
        if(null != mUpdatingHelper) {
            mUpdatingHelper.dismiss();
        }
    }

    protected void startChangeAvatar() {
        //使用对话框fragment
        AvatarPopupFragment pwDialog = new AvatarPopupFragment();
        pwDialog.setPhotographPathListener(this);
        pwDialog.show(this.getChildFragmentManager(), "TEXT_POP_DIALOG");
    }

    @Override
    public void onPhotographPathListen(String path) {
        mPhotographPath = path;
    }

    @Override
    public Fragment getCurrentFragment() {
        return this;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_CANCELED) {
            return;
        }
        if (resultCode != Activity.RESULT_OK) {
            onPhotographResult(data);
            return;
        }

        if (requestCode == REQUEST_CODE_CHOSE_PHOTO) {
            onChosePhotoResult(data);
            return;
        }

        if (requestCode == REQUEST_CODE_CAMERA) {
            onPhotographResult(data);
            return;
        }

        if (requestCode == REQUEST_CODE_CROP) {
            onCropResult(data);
            return;
        }
    }

    private void onChosePhotoResult(Intent data) {
        AndPermission
                .with(mActivity)
                .runtime()
                .permission(Permission.WRITE_EXTERNAL_STORAGE, Permission.READ_EXTERNAL_STORAGE)
                .onGranted(granted -> {
                    Uri uri = data.getData();
                    if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) && "com.android.providers.media.documents".equals(uri.getAuthority())) {
                        Uri tempUri = IntentUtil.changeDocumentsToNormal(uri);
                        if (tempUri != null)
                            uri = tempUri;
                    }
                    mImagePathUriOriginal = uri;

                    Intent intent = IntentUtil.getCropImageIntent(mActivity, uri);
                    Uri outputUri = intent.getParcelableExtra("output");
                    mCropOutputPath = outputUri.getPath();
                    startActivityForResult(intent, REQUEST_CODE_CROP);
                })
                .onDenied(denied ->  AtworkUtil.popAuthSettingAlert(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE))
                .start();

    }

    private void onPhotographResult(Intent data) {
        try {
//            mPhotographPath = data.getStringExtra(PhotoPreviewActivity.PREVIEW_PHOTO_PATH_INTENT);
            File file = new File(mPhotographPath);
            if (null != file) {
                Uri uri = UriCompat.getFileUriCompat(getActivity(), file);
                mImagePathUriOriginal = uri;
                Intent intent = IntentUtil.getCropImageIntent(mActivity, uri);
                Uri outputUri = intent.getParcelableExtra("output");
                mCropOutputPath = outputUri.getPath();

                startActivityForResult(intent, REQUEST_CODE_CROP);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onCropResult(Intent data) {
        mUpdatingHelper = new ProgressDialogHelper(getActivity());
        mUpdatingHelper.show(getUploadingTip());

        CropHelper.makeCropIntentCompatible(data, mCropOutputPath, mImagePathUriOriginal);
        String filePath = CropHelper.getCropFilePath(getActivity(), data);

        uploadNewAvatar(filePath);


    }



    protected void uploadNewAvatar(String filePath) {
        final String messageID = UUID.randomUUID().toString();
//        service.uploadFile(getActivity(), MediaCenterNetManager.IMAGE_FILE, messageID, filePath, false, (errorCode, errorMsg)
//                -> ErrorHandleUtil.handleError(ErrorHandleUtil.Module.Media, errorCode, errorMsg));



        UploadFileParamsMaker uploadFileParamsMaker = UploadFileParamsMaker.newRequest()
                .setType(MediaCenterNetManager.IMAGE_FILE)
                .setMsgId(messageID)
                .setFilePath(filePath)
                .setNeedCheckSum(false)
                .setExpireLimit(-1);

        MediaCenterNetManager.uploadFile(BaseApplicationLike.baseContext, uploadFileParamsMaker);

        MediaCenterNetManager.addMediaUploadListener(new MediaCenterNetManager.MediaUploadListener() {
            @Override
            public String getMsgId() {
                return messageID;
            }

            @Override
            public MediaCenterNetManager.UploadType getType() {
                return null;
            }

            @Override
            public void uploadSuccess(String mediaInfo) {
                changeAvatar(mediaInfo);
            }

            @Override
            public void uploadFailed(int errorCode, String errorMsg, boolean doRefreshView) {

                try {
                    if (doRefreshView) {
                        toastOver(getUploadFailTip());

                        mUpdatingHelper.dismiss();

                        MediaCenterNetManager.removeUploadFailList(messageID);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    mUpdatingHelper.dismiss();

                }

            }

            @Override
            public void uploadProgress(double progress) {

            }
        });
    }
}
