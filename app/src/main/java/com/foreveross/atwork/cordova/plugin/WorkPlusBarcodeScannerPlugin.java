package com.foreveross.atwork.cordova.plugin;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.org.apache.http.HttpResponse;
import android.org.apache.http.util.EntityUtils;

import com.foreverht.threadGear.AsyncTaskThreadPool;
import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.UrlConstantManager;
import com.foreveross.atwork.cordova.plugin.qrcode.QRCodeHttpUtils;
import com.foreveross.atwork.cordova.plugin.qrcode.QRCodeResponseData;
import com.foreveross.atwork.infrastructure.permissions.PermissionsManager;
import com.foreveross.atwork.infrastructure.permissions.PermissionsResultAction;
import com.foreveross.atwork.infrastructure.utils.ImageShowHelper;
import com.foreveross.atwork.infrastructure.utils.JsonUtil;
import com.foreveross.atwork.infrastructure.utils.encryption.Base64Util;
import com.foreveross.atwork.modules.qrcode.activity.QrcodeScanActivity;
import com.foreveross.atwork.modules.voip.utils.VoipHelper;
import com.foreveross.atwork.utils.AtworkToast;
import com.foreveross.atwork.utils.AtworkUtil;
import com.foreveross.atwork.utils.IntentUtil;

import org.apache.cordova.CallbackContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lingen on 15/12/28.
 */
public class WorkPlusBarcodeScannerPlugin extends WorkPlusCordovaPlugin {

    public static String SCANNER = "scanner";

    public static final String DATA_FROM_CORDOVA = "DATA_FROM_CORDOVA";

    private static final int QR_CODE_CAMERA_REQUEST_CODE = 0x150;

    public static String QR_CODE = "cordovaQRCode";

    /**
     * 控制该接口是直接返回结果, 还是像原生扫描那样, 跳转相对页面或者处理相关行为
     * */
    public static final String DATA_IS_NATIVE_ACTION = "DATA_IS_NATIVE_ACTION";

    private CallbackContext mCallbackContext;

    private static final int SCANNER_REQUEST_CODE = 1;

    private String mPhotoPath;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if(!requestCordovaAuth()){
            callbackContext.error(CORDOVA_NOT_AUTH);
            return true;
        }

        mCallbackContext = callbackContext;

        if (action.equals(SCANNER)) {
            if (!VoipHelper.isHandlingVoipCall()) {
                PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(cordova.getActivity() , new String[]{Manifest.permission.CAMERA}, new PermissionsResultAction() {
                    @Override
                    public void onGranted() {
                        try {
                            JSONObject object = args.optJSONObject(0);

                            Intent intent = QrcodeScanActivity.getIntent(cordova.getActivity());
                            intent.putExtra(DATA_FROM_CORDOVA, true);
                            intent.putExtra(DATA_IS_NATIVE_ACTION, object != null && ("native".equalsIgnoreCase(object.optString("type"))));
                            cordova.startActivityForResult(WorkPlusBarcodeScannerPlugin.this, intent, SCANNER_REQUEST_CODE);

                        } catch (Exception e) {
                            e.printStackTrace();

                            callbackContext.error();

                        }

                    }

                    @Override
                    public void onDenied(String permission) {
                        AtworkUtil.popAuthSettingAlert(cordova.getActivity(), permission);
                    }
                });

            } else {
                AtworkToast.showResToast(R.string.alert_is_handling_voip_meeting_click_voip);
            }
            return true;
        }

        if(action.equals(QR_CODE)){
            if(VoipHelper.isHandingVideoVoipCall()) {
                AtworkToast.showResToast(R.string.alert_is_handling_voip_meeting_click_voip);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("code", 0);
                jsonObject.put("message", R.string.alert_is_handling_voip_meeting_click_voip);
                callbackContext.error(jsonObject);
                return true;
            }

            PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(cordova.getActivity(), new String[]{Manifest.permission.CAMERA}, new PermissionsResultAction() {
                @Override
                public void onGranted() {
                    try {
                        mPhotoPath = IntentUtil.camera(cordova.getActivity(), WorkPlusBarcodeScannerPlugin.this, QR_CODE_CAMERA_REQUEST_CODE);
                    } catch (Exception e) {
                        errorQRCodeJson(e.getMessage());
                        e.printStackTrace();
                    }

                }

                @Override
                public void onDenied(String permission) {
                    AtworkUtil.popAuthSettingAlert(cordova.getActivity(), permission);
                }
            });

        }

        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (null != intent && resultCode == Activity.RESULT_OK ) {
            if(requestCode == SCANNER_REQUEST_CODE) {
                String result = intent.getStringExtra("result");
                mCallbackContext.success(result);
            }
        }
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == QR_CODE_CAMERA_REQUEST_CODE) {
                AsyncTaskThreadPool.getInstance().execute(this::postDataQRCode);
            }
        }
    }

    private void postDataQRCode(){
        byte[] imgByte = ImageShowHelper.getByteFromFile(mPhotoPath, ImageShowHelper.ORIGINAL_TARGET_WIDTH, ImageShowHelper.ORIGINAL_TARGET_HEIGHT, ImageShowHelper.ORIGINAL_TARGET_SIZE);
        String imgBase64Str = Base64Util.encode(imgByte);
        String host = "https://kuai.xiaohuaerai.com";
        String path = "/kuai";
        String method = "POST";
        String appcode = UrlConstantManager.getInstance().getYSKJAppCode();
        Map<String, String> headers = new HashMap<String, String>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appcode);
        //根据API的要求，定义相对应的Content-Type
        headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        Map<String, String> querys = new HashMap<String, String>();
        Map<String, String> bodys = new HashMap<String, String>();
        bodys.put("speed", "nor");
        bodys.put("src", imgBase64Str);
        bodys.put("type", "nor");


        try {
            HttpResponse response = QRCodeHttpUtils.doPost(host, path, method, headers, querys, bodys);
            //获取response的body
            String entity = EntityUtils.toString(response.getEntity());
            QRCodeResponseData qrCodeResponseData = JsonUtil.fromJson(entity, QRCodeResponseData.class);
            if (qrCodeResponseData != null && qrCodeResponseData.getStatus() != null) {
                if(qrCodeResponseData.getStatus() == 200) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        JSONObject jsonValue = new JSONObject();
                        jsonValue.put("result", qrCodeResponseData.getMsg());
                        jsonObject.put("code", 0);
                        jsonObject.put("message", "扫描成功");
                        jsonObject.put("value", jsonValue);
                    } catch (JSONException jsonException) {
                        jsonException.printStackTrace();
                    }
                    mCallbackContext.error(jsonObject);
                }else{
                    errorQRCodeJson("扫描解析失败");
                }
            }else{
                errorQRCodeJson("扫描解析失败");
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    private void errorQRCodeJson(String msg){
        JSONObject jsonError = new JSONObject();
        try {
            jsonError.put("code", -1);
            jsonError.put("message", msg);
        } catch (JSONException jsonException) {
            jsonException.printStackTrace();
        }
        mCallbackContext.error(jsonError);
    }

}
