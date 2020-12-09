package com.foreveross.atwork.cordova.plugin;

import android.content.Intent;
import android.net.Uri;

import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.util.NetGsonHelper;
import com.foreveross.atwork.cordova.plugin.model.OpenWebVRequest;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.WebViewControlAction;
import com.foreveross.atwork.infrastructure.model.app.appEnum.DisplayMode;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.utils.AtWorkDirUtils;
import com.foreveross.atwork.infrastructure.utils.FileUtil;
import com.foreveross.atwork.infrastructure.utils.ImageShowHelper;
import com.foreveross.atwork.infrastructure.utils.LogUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.modules.app.activity.WebViewActivity;
import com.foreveross.atwork.modules.voip.utils.VoipHelper;
import com.foreveross.atwork.utils.AtworkToast;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * 同事圈调用插件
 *
 * @author zhanglifa
 */
public class AssociatesPlugin extends CordovaPlugin {

    //拍照
    private static final String ACTION_TAKE_PHOTO = "takePhoto";

    private static final String ACTION_OPEN_LOCAL_URL = "openLocalURL";

    private static final String ACTION_OPEN_WEBVIEW_URL = "openWebView";

    private static final String ACTION_CLEAN_UP_COMPRESS_IMAGE = "cleanCompressImage";

    private static final int TAKE_PHOTO_REQUEST_CODE = 0x130;

    private static final int TAKE_PHOTO_EDIT_REQUEST_CODE = 0x131;

    private static final String ASSETS_ASSOCIATES_DIR_PATH = "file:///file:///android_asset/colleague-circle";

    private CallbackContext mCallbackContext;

    private File mTakePhotoPath;

    @Override
    public boolean execute(String action, final JSONArray jsonArr, CallbackContext callbackContext) throws JSONException {
        mCallbackContext = callbackContext;
        if (ACTION_TAKE_PHOTO.equalsIgnoreCase(action)) {
            if (VoipHelper.isHandingVideoVoipCall()) {
                AtworkToast.showResToast(R.string.alert_is_handling_voip_meeting_click_voip);
                return true;
            }

            try {
                System.gc();
                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                mTakePhotoPath = new File(AtWorkDirUtils.getInstance().getImageDir(cordova.getActivity()), System.currentTimeMillis() + "_workplus.jpg");
                Uri targetFileUri = null;
                if (mTakePhotoPath != null) {
                    targetFileUri = Uri.fromFile(mTakePhotoPath);
                }
                // 在SDCard 创建文件失败。
                if (targetFileUri == null) {
                    JSONObject jsonError = new JSONObject();
                    jsonError.put("msg", "在SDCard 创建文件失败");
                    callbackContext.error(jsonError);
                    return false;
                }
                intent.putExtra("return-data", true);
                intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, targetFileUri);
                this.cordova.startActivityForResult(this, intent, TAKE_PHOTO_REQUEST_CODE);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                JSONObject jsonError = new JSONObject();
                jsonError.put("msg", e.getMessage());
                callbackContext.error(jsonError);
                return false;
            }
        }

        if (ACTION_OPEN_LOCAL_URL.equalsIgnoreCase(action)) {
            String openPath = jsonArr.optJSONObject(0).optString("localURL");
            StringBuffer url = new StringBuffer();
            url.append(ASSETS_ASSOCIATES_DIR_PATH);
            url.append(openPath);
            this.webView.loadUrl(openPath);
        }

        if (ACTION_OPEN_WEBVIEW_URL.equalsIgnoreCase(action)) {
            this.cordova.getThreadPool().execute(() -> {


                OpenWebVRequest request = NetGsonHelper.fromCordovaJson(jsonArr, OpenWebVRequest.class);

                if(null != request && !StringUtils.isEmpty(request.mUrl)) {
                    boolean hideTitle = DisplayMode.FULL_SCREEN.equals(request.mDisplayMode);
                    WebViewControlAction webViewControlAction = WebViewControlAction.newAction().setUrl(request.mUrl).setTitle(request.mTitle).setHideTitle(hideTitle);
                    Intent intent = WebViewActivity.getIntent(BaseApplicationLike.baseContext, webViewControlAction);
                    cordova.getActivity().startActivity(intent);

                } else {

                }


            });

        }

        if (ACTION_CLEAN_UP_COMPRESS_IMAGE.equalsIgnoreCase(action)) {
            this.cordova.getThreadPool().execute(() -> FileUtil.deleteFile(new File(AtWorkDirUtils.getInstance().getCompressImageDir(LoginUserInfo.getInstance().getLoginUserUserName(cordova.getActivity()))), false));
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        LogUtil.d("return from camera");
        if (TAKE_PHOTO_REQUEST_CODE == requestCode) {
            onActivityForCamera();

        } else if (TAKE_PHOTO_EDIT_REQUEST_CODE == requestCode) {
            onActivityForCameraEdit();

        }
    }

    private void onActivityForCamera() {
        onActivityForCameraEdit();
    }

    private void onActivityForCameraEdit() {
        try {
            String photoPath = mTakePhotoPath.getPath();
            File file = new File(photoPath);
            LogUtil.d("take photo done, the path = " + photoPath);
            if (file != null) {
                String path = ImageShowHelper.imagePluginCompress(cordova.getActivity(), photoPath);

                JSONObject json = new JSONObject();
                try {
                    json.put("success", path);
                } catch (JSONException e) {
                    mCallbackContext.error(e.getMessage());
                    e.printStackTrace();
                }
                mCallbackContext.success(json);
            }
        } catch (Exception e) {
            mCallbackContext.error("change user avatar fail by taking photo");
        }
    }

}
