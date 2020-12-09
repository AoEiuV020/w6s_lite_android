package com.foreveross.atwork.wxapi;/**
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
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ __           __
 * .--.--.--.-----.----.|  |--.-----.|  |.--.--.-----.
 * |  |  |  |  _  |   _||    <|  _  ||  ||  |  |__ --|
 * |________|_____|__|  |__|__|   __||__||_____|_____|
 * |__|
 */


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.foreveross.atwork.cordova.plugin.WxOrQQPlugin;
import com.foreveross.atwork.cordova.plugin.model.WxResponse;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.LogUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.utils.CordovaHelper;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.apache.cordova.CallbackContext;

/**
 * Created by reyzhang22 on 16/3/11.
 */
public class WXEntryBasicActivity extends Activity implements IWXAPIEventHandler {

    private IWXAPI mApi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String appId = null;
        if (StringUtils.isEmpty(WxOrQQPlugin.Companion.getAppId())) {
            appId = AtworkConfig.WX_APP_ID;
        } else {
            appId = WxOrQQPlugin.Companion.getAppId();

        }
        mApi = WXAPIFactory.createWXAPI(this, appId, true);

        handleIntent(getIntent());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        WxOrQQPlugin.Companion.release();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        boolean result = false;
        try {
            result = mApi.handleIntent(intent, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(!result) {
            finish();
        }
    }

    @Override
    public void onReq(BaseReq baseReq) {
//        Toast.makeText(this, "baseReq = " + baseReq.getType(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResp(BaseResp baseResp) {
//        int result = 0;
        LogUtil.e("baseResp = " + baseResp.getType());

        CallbackContext callbackContext = WxOrQQPlugin.Companion.getCurrentCallbackContext();
        if (null != callbackContext) {
            WxResponse payResponse = new WxResponse(baseResp.errCode, baseResp.errStr);
            CordovaHelper.doSuccess(payResponse, callbackContext);
        }

        finish();

    }

}
