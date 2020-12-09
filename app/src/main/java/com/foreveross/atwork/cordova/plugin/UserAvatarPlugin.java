package com.foreveross.atwork.cordova.plugin;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.permissions.PermissionsManager;
import com.foreveross.atwork.infrastructure.permissions.PermissionsResultAction;
import com.foreveross.atwork.infrastructure.utils.LogUtil;
import com.foreveross.atwork.infrastructure.utils.UriCompat;
import com.foreveross.atwork.modules.voip.utils.VoipHelper;
import com.foreveross.atwork.utils.AtworkToast;
import com.foreveross.atwork.utils.AtworkUtil;
import com.foreveross.atwork.utils.CropHelper;
import com.foreveross.atwork.utils.IntentUtil;

import org.apache.cordova.CallbackContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * Created by lingen on 15/5/1.
 */
public class UserAvatarPlugin extends WorkPlusCordovaPlugin {

    private static final String CHANGE_AVATAR_BY_PHOTO_ALBUM = "changeAvatarByPhotoAlbum";

    private static final String CHANGE_AVATAR_BY_TAKE_PHOTO = "changeAvatarByTakePhoto";

    private static final int CHANGE_AVATAR_BY_PHOTO_ALBUM_REQUEST_CODE = 0x110;

    private static final int CHANGE_AVATAR_BY_TAKE_PHOTO_REQUEST_CODE = 0x120;

    private static final int AVATAR_CORP_REQUEST = 0x130;

    private CallbackContext mCallbackContext;

    private String mPhotoPath;
    private String mCropOutputPath;
    private Uri mImagePathUriOriginal;


    @Override
    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
        if(!requestCordovaAuth()){
            callbackContext.error(CORDOVA_NOT_AUTH);
            return true;
        }

        mCallbackContext = callbackContext;
        if (action.equals(CHANGE_AVATAR_BY_PHOTO_ALBUM)) {
            try {
                Intent intent = IntentUtil.getPhotoFromLibraryIntent();
                this.cordova.startActivityForResult(this, intent, CHANGE_AVATAR_BY_PHOTO_ALBUM_REQUEST_CODE);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                JSONObject jsonError = new JSONObject();
                jsonError.put("error", e.getMessage());
                callbackContext.error(jsonError);
                return false;
            }
        }

        if (action.equals(CHANGE_AVATAR_BY_TAKE_PHOTO)) {
            if(VoipHelper.isHandingVideoVoipCall()) {
                AtworkToast.showResToast(R.string.alert_is_handling_voip_meeting_click_voip);
                return true;
            }

            PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(cordova.getActivity(), new String[]{Manifest.permission.CAMERA}, new PermissionsResultAction() {
                @Override
                public void onGranted() {
                    try {
                        mPhotoPath = IntentUtil.camera(cordova.getActivity(), UserAvatarPlugin.this, CHANGE_AVATAR_BY_TAKE_PHOTO_REQUEST_CODE);
                    } catch (Exception e) {
                        e.printStackTrace();
                        JSONObject jsonError = new JSONObject();
                        try {
                            jsonError.put("error", e.getMessage());
                        } catch (JSONException jsonException) {
                            jsonException.printStackTrace();
                        }
                        callbackContext.error(jsonError);
                    }

                }

                @Override
                public void onDenied(String permission) {
                    AtworkUtil.popAuthSettingAlert(cordova.getActivity(), permission);
                }
            });

            return true;
        }
        return super.execute(action, args, callbackContext);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == Activity.RESULT_OK) {
            Uri uri = null;
            if (requestCode == CHANGE_AVATAR_BY_TAKE_PHOTO_REQUEST_CODE) {
                onCameraResult(intent);
                return;
            }
            if (requestCode == CHANGE_AVATAR_BY_PHOTO_ALBUM_REQUEST_CODE) {
                onPhotoAlbumResult(intent);
                return;
            }
            if (requestCode == AVATAR_CORP_REQUEST) {
                onCorpResult(intent);
            }
        }
    }

    private void onCameraResult(Intent intent) {
        try {
            File file = new File(mPhotoPath);
            if (file != null) {
                Uri uri = UriCompat.getFileUriCompat(cordova.getActivity(), file);
                getCropAvatar(uri);
            }
        } catch (Exception e) {
            LogUtil.i("test", e.getMessage());
            mCallbackContext.error("change user avatar fail by taking photo");
        }
    }

    private void onPhotoAlbumResult(Intent intent) {
        Uri uri = intent.getData();
        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) && "com.android.providers.media.documents".equals(uri.getAuthority())) {
            Uri tempUri = IntentUtil.changeDocumentsToNormal(uri);
            if (tempUri != null)
                uri = tempUri;
        }
        getCropAvatar(uri);
    }

    private void onCorpResult(Intent intent) {
        if (null == intent) {
            return;
        }

        CropHelper.makeCropIntentCompatible(intent, mCropOutputPath, mImagePathUriOriginal);
        String path = CropHelper.getCropFilePath(cordova.getActivity(), intent);

        if (new File(path).exists()) {
            JSONObject json = new JSONObject();
            try {
                json.put("success", path);
            } catch (JSONException e) {
                mCallbackContext.error(e.getMessage());
                e.printStackTrace();
            }
            mCallbackContext.success(json);
        } else {
            JSONObject json = new JSONObject();
            try {
                json.put("error", "cannot get the image");
            } catch (JSONException e) {
                mCallbackContext.error(e.getMessage());
                e.printStackTrace();
            }
            mCallbackContext.error(json);
        }
    }

    private void getCropAvatar(Uri uri) {
        mImagePathUriOriginal = uri;

        Intent cropIntent = IntentUtil.getCropImageIntent(cordova.getActivity(), uri);
        Uri outputUri = cropIntent.getParcelableExtra("output");
        mCropOutputPath = outputUri.getPath();

        this.cordova.startActivityForResult(this, cropIntent, AVATAR_CORP_REQUEST);
    }
}
