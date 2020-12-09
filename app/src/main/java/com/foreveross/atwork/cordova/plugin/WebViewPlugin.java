package com.foreveross.atwork.cordova.plugin;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.foreverht.webview.WebkitSdkUtil;
import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.BuildConfig;
import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.users.UserAsyncNetService;
import com.foreveross.atwork.api.sdk.util.NetGsonHelper;
import com.foreveross.atwork.cordova.plugin.model.ChangeOrientationRequest;
import com.foreveross.atwork.cordova.plugin.model.CopyTextRequest;
import com.foreveross.atwork.cordova.plugin.model.CordovaBasicResponse;
import com.foreveross.atwork.cordova.plugin.model.CordovaGetShareItem;
import com.foreveross.atwork.cordova.plugin.model.GetShareItemsResponse;
import com.foreveross.atwork.cordova.plugin.model.OpenWebVRequest;
import com.foreveross.atwork.cordova.plugin.model.SetLightNoticeDataRequest;
import com.foreveross.atwork.cordova.plugin.model.SetRfchinaCookieRequest;
import com.foreveross.atwork.cordova.plugin.model.VoiceRecordResultResponse;
import com.foreveross.atwork.cordova.plugin.model.WxOrQQShareRequest;
import com.foreveross.atwork.cordova.plugin.pay.model.CordovaWxConstant;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.beeworks.BeeWorks;
import com.foreveross.atwork.infrastructure.model.WebViewControlAction;
import com.foreveross.atwork.infrastructure.model.app.appEnum.DisplayMode;
import com.foreveross.atwork.infrastructure.model.user.LoginToken;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ArticleItem;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.CustomerHelper;
import com.foreveross.atwork.infrastructure.utils.LogUtil;
import com.foreveross.atwork.infrastructure.utils.ScreenUtils;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.encryption.Base64Util;
import com.foreveross.atwork.infrastructure.utils.encryption.DESUtil;
import com.foreveross.atwork.infrastructure.webview.OnWebActivityActionListener;
import com.foreveross.atwork.manager.AgreementManager;
import com.foreveross.atwork.manager.OrganizationSettingsHelper;
import com.foreveross.atwork.manager.SkinManger;
import com.foreveross.atwork.manager.UserManager;
import com.foreveross.atwork.manager.share.TencentQQShare;
import com.foreveross.atwork.manager.share.TencentQZoneShare;
import com.foreveross.atwork.manager.share.WXSessionShare;
import com.foreveross.atwork.manager.share.WXTimeLineShare;
import com.foreveross.atwork.modules.app.activity.LandscapeWebViewActivity;
import com.foreveross.atwork.modules.app.activity.WebViewActivity;
import com.foreveross.atwork.modules.app.activity.WebViewBaseActivity;
import com.foreveross.atwork.modules.app.util.AppRefreshHelper;
import com.foreveross.atwork.modules.common.fragment.VoiceRecordDialogFragment;
import com.foreveross.atwork.modules.common.lightapp.LightNoticeMapping;
import com.foreveross.atwork.modules.common.lightapp.SimpleLightNoticeMapping;
import com.foreveross.atwork.modules.main.activity.MainActivity;
import com.foreveross.atwork.modules.main.data.TabNoticeManager;
import com.foreveross.atwork.modules.main.helper.TabHelper;
import com.foreveross.atwork.modules.main.service.HandleLoginService;
import com.foreveross.atwork.utils.ErrorHandleUtil;
import com.foreveross.atwork.utils.IntentUtil;
import com.google.gson.Gson;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.apache.cordova.CallbackContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import kotlin.Unit;
import kotlin.collections.CollectionsKt;


/**
 * 同事圈调用插件
 *
 * @author zhanglifa
 */
public class WebViewPlugin extends WxOrQQPlugin {

    private static final String ACTION_OPEN_LOCAL_URL = "openLocalURL";

    private static final String ACTION_OPEN_WEBVIEW_URL = "openWebView";

    private static final String ACTION_OPEN_WEBVIEW_URL_NEED_AUTH = "openWebViewNeedAuth";

    private static final String ACTION_SET_RFCHINA_COOKIES = "setRfchinaCookies";

    private static final String ACTION_LOCK_TITLE_BAR = "navigation";

    private static final String ACTION_LEFT_BUTTON = "leftButton";

    private static final String ACTION_CHANGE_LEFT_BUTTON = "changeLeftButton";

    private static final String ACTION_TITLE = "title";

    private static final String ACTION_RIGHT_BUTTON = "rightButtons";

    private static final String ACTION_RIGHT_BUTTON_1 = "rightButtons1";

    private static final String ACTION_RESET_RIGHT_BUTTON = "clearRightButtons";

    private static final String ACTION_RESET_LEFT_BUTTON = "clearLeftButton";

    private static final String EXIT_WEB_VIEW = "exit";

    private static final String ACTION_SHARE = "share";

    private static final String ACTION_GET_SHARE_ITEMS = "getShareItems";

    private static final String ACTION_TO_MAIN_ACTIVITY = "toActivity";

    private static final String ACTION_VISIBLE_LEFT_BUTTON = "visibleLeftButton";

    private static final String ACTION_CHANGE_ORIENTATION = "changeOrientation";

    private static final String ACTION_SHOW_WATERMARK = "addWaterMask";

    private static final String ACTION_REMOVE_WATERMARK = "removeWaterMask";

    private static final String ACTION_SET_FORWARD_MODE = "setForwardMode";

    private static final String ACTION_WX_SHARE = "wxShare";

    private static final String ACTION_QQ_SHARE = "qqShare";


    private static final String ACTION_SET_BADGE = "setBadge";

    private static final String ACTION_VOICE_TO_TEXT = "voiceToText";

    private static final String ACTION_COPY_TEXT = "copyText";


    private static final String ACTION_REGISTER_SHAKE_LISTENER = "registerShakeListener";

    private static final String ACTION_UNREGISTER_SHAKE_LISTENER = "unregisterShakeListener";

    public static final String INTENT_KEY_NEXT_URL = "INTENT_KEY_NEXT_URL";

    private CallbackContext mCallbackContext;

    @Override
    public boolean execute(String action, final JSONArray jsonArr, CallbackContext callbackContext) throws JSONException {
        mCallbackContext = callbackContext;
        if (ACTION_OPEN_LOCAL_URL.equalsIgnoreCase(action)) {
            handleOpenLocalUrl(jsonArr);
            return true;

        }

        if (ACTION_OPEN_WEBVIEW_URL.equalsIgnoreCase(action)) {
            handleOpenWebUrl(jsonArr);
            return true;
        }

        //只有富力集团才开放此接口
        if (BuildConfig.DEBUG ||CustomerHelper.isRfchina(cordova.getActivity())) {

            if (ACTION_OPEN_WEBVIEW_URL_NEED_AUTH.equalsIgnoreCase(action)) {

                LogUtil.e("action ->   " + action + "  data -> " + jsonArr.toString());

                handleOpenWebUrlNeedAuth(jsonArr);
                return true;
            }

            if (ACTION_SET_RFCHINA_COOKIES.equalsIgnoreCase(action)) {
                setRfchinaCookies(jsonArr, callbackContext);
                return true;
            }
        }

        if (EXIT_WEB_VIEW.equalsIgnoreCase(action)) {
            handleExitWeb();
            return true;
        }

        if (ACTION_SHARE.equalsIgnoreCase(action)) {
            handleShare(jsonArr);
            return true;
        }

        if(ACTION_GET_SHARE_ITEMS.equalsIgnoreCase(action)) {
            handleGetShareItems(jsonArr, callbackContext);
            return true;
        }

        if (ACTION_TITLE.equalsIgnoreCase(action)) {

            handleTitle(jsonArr);
            return true;
        }

        if (ACTION_LEFT_BUTTON.equalsIgnoreCase(action)) {
            handleLeftButton(jsonArr);
            return true;
        }
        if (ACTION_LOCK_TITLE_BAR.equalsIgnoreCase(action)) {
            handleLockTitleBar(jsonArr);
            return true;
        }

        if (ACTION_RIGHT_BUTTON.equalsIgnoreCase(action)) {
            handleRightButtons(jsonArr);
            return true;
        }

        if (ACTION_RIGHT_BUTTON_1.equalsIgnoreCase(action)) {
            handleRightButtons1(jsonArr);
            return true;
        }

        if (ACTION_CHANGE_LEFT_BUTTON.equals(action)) {
            changeLeftButton(jsonArr);
            return true;
        }

        if (ACTION_RESET_RIGHT_BUTTON.equalsIgnoreCase(action)) {
            handleResetRightButton();
            return true;
        }

        if (ACTION_RESET_LEFT_BUTTON.equalsIgnoreCase(action)) {
            handleResetLeftButton();
            return true;
        }
        if (ACTION_TO_MAIN_ACTIVITY.equalsIgnoreCase(action)) {
            toMainActivity(jsonArr);
            return true;
        }
        if (ACTION_VISIBLE_LEFT_BUTTON.equalsIgnoreCase(action)) {
            setLetButtonVisible(jsonArr);
            return true;
        }

        if(ACTION_CHANGE_ORIENTATION.equalsIgnoreCase(action)) {
            changeOrientation(jsonArr);
            return true;

        }

        if (ACTION_SHOW_WATERMARK.equalsIgnoreCase(action)) {
            showWatermark(jsonArr);
            return true;
        }

        if (ACTION_REMOVE_WATERMARK.equalsIgnoreCase(action)) {
            removeWatermark(jsonArr);
            return true;
        }

        if (ACTION_SET_FORWARD_MODE.equalsIgnoreCase(action)) {
            handleSetForwardMode(jsonArr);
            return true;
        }

        if(ACTION_WX_SHARE.equalsIgnoreCase(action)) {
            wxShare(jsonArr, callbackContext);
            return true;
        }

        if(ACTION_QQ_SHARE.equalsIgnoreCase(action)) {
            qqShare(jsonArr, callbackContext);
            return true;
        }

        if (ACTION_SET_BADGE.equalsIgnoreCase(action)) {
            showBadgeOnTab(jsonArr);
            return true;
        }


        if(ACTION_VOICE_TO_TEXT.equalsIgnoreCase(action)) {
            voiceToText(jsonArr, callbackContext);
            return true;
        }


        if(ACTION_REGISTER_SHAKE_LISTENER.equalsIgnoreCase(action)) {
            registerShake();
            return true;
        }

        if(ACTION_UNREGISTER_SHAKE_LISTENER.equalsIgnoreCase(action)) {
            unregisterShake();
            return true;
        }

        if(ACTION_COPY_TEXT.equalsIgnoreCase(action)) {
            copyText(jsonArr, callbackContext);
            return true;
        }

        return false;
    }

    private void copyText(JSONArray jsonArray, CallbackContext callbackContext) {

        CopyTextRequest copyTextRequest = NetGsonHelper.fromCordovaJson(jsonArray, CopyTextRequest.class);
        if(null == copyTextRequest) {
            callbackContext.error();
            return;
        }

        ClipboardManager cmb = (ClipboardManager) BaseApplicationLike.baseContext.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("newPlainTextLabel", copyTextRequest.getText());
        cmb.setPrimaryClip(clipData);

        callbackContext.success();
    }

    private void handleSetForwardMode(JSONArray jsonArray) {
        String forwardMode = jsonArray.optJSONObject(0).optString("forward_mode");
        ((WebViewBaseActivity) cordova.getActivity()).setForwardMode(forwardMode);
        return;
    }

    private void changeOrientation(JSONArray jsonArr) {

        LogUtil.e("action ->   " + "changeOrientation" + "  data -> " + jsonArr.toString());


        ChangeOrientationRequest changeOrientationRequest = NetGsonHelper.fromCordovaJson(jsonArr, ChangeOrientationRequest.class);
        if(null != changeOrientationRequest) {
            //因 ios 不支持 lock 跟 isLandScape 同时处理, 所以暂时不考虑 lock
            ScreenUtils.landscapeMode(cordova.getActivity(), changeOrientationRequest.mLandscape, changeOrientationRequest.mLock);
        }
    }

    private void setLetButtonVisible(final JSONArray jsonArray) {
        this.cordova.getThreadPool().execute(() -> {
            JSONObject json = jsonArray.optJSONObject(0);
            boolean showBack = json.optBoolean("showBack", true);
            boolean showClose = json.optBoolean("showClose", true);
            ((WebViewBaseActivity) cordova.getActivity()).onLeftButtonVisible(showBack, showClose);
        });
    }

    /**
     * 处理打开本地url
     * @param jsonArr
     */
    private void handleOpenLocalUrl(JSONArray jsonArr) {
        String openPath = jsonArr.optJSONObject(0).optString("localURL");
        if(!StringUtils.isEmpty(openPath)) {
            if (openPath.startsWith("local://")) {
                openPath = "file:///android_asset/www/" + openPath.substring("local://".length());
            }

            this.webView.loadUrl(openPath);

        } else {

            mCallbackContext.error();
        }

    }

    /**
     * 处理打开网络地址url
     * @param jsonArr
     */
    private void handleOpenWebUrl(final JSONArray jsonArr) {

        LogUtil.e("action ->   " + "handleOpenWebUrl" + "  data -> " + jsonArr.toString());


        this.cordova.getThreadPool().execute(() -> {
            OpenWebVRequest request = NetGsonHelper.fromCordovaJson(jsonArr, OpenWebVRequest.class);

            if(null != request && !StringUtils.isEmpty(request.mUrl)) {

                if(request.isUseSystemWebview()) {
                    IntentUtil.routeSystemWebView(cordova.getActivity(), request.mUrl);
                    return;
                }


                boolean hideTitle = DisplayMode.FULL_SCREEN.equals(request.mDisplayMode);
                WebViewControlAction webViewControlAction = WebViewControlAction.newAction()
                        .setUrl(request.mUrl)
                        .setTitle(request.mTitle)
                        .setHideTitle(hideTitle)
                        .setOrientation(request.mOrientation)
                        .setNeedShare(!"1".equalsIgnoreCase(request.mHideShare));

                Intent intent;
                if (null != request.mOrientation && ScreenUtils.ORIENTATION_LOCK_LANDSCAPE == request.mOrientation) {
                    intent = LandscapeWebViewActivity.getIntent(BaseApplicationLike.baseContext, webViewControlAction);
                } else {
                    intent = WebViewActivity.getIntent(BaseApplicationLike.baseContext, webViewControlAction);
                }

                cordova.getActivity().startActivity(intent);
                return;
            }
            mCallbackContext.error(-1);
        });
    }

    /**
     * 处理打开网络地址url(需要http auth授权)
     * @param jsonArr
     */
    private void handleOpenWebUrlNeedAuth(final JSONArray jsonArr) {
        this.cordova.getThreadPool().execute(() -> {
            OpenWebVRequest openWebVRequest = NetGsonHelper.fromCordovaJson(jsonArr, OpenWebVRequest.class);

            if (null != openWebVRequest) {

                boolean hideTitle = DisplayMode.FULL_SCREEN.equals(openWebVRequest.mDisplayMode);
                WebViewControlAction action = WebViewControlAction.newAction()
                        .setUrl(openWebVRequest.mUrl)
                        .setTitle(openWebVRequest.mTitle)
                        .setHideTitle(hideTitle)
                        .setOrientation(openWebVRequest.mOrientation)
                        .setNeedAuth(openWebVRequest.mNeedAuth);


                cordova.getActivity().startActivity(WebViewActivity.getIntent(BaseApplicationLike.baseContext, action));
            }

        });
    }

    private void setRfchinaCookies(final JSONArray jsonArr, CallbackContext callbackContext) {

        SetRfchinaCookieRequest setRfchinaCookieRequest = NetGsonHelper.fromCordovaJson(jsonArr, SetRfchinaCookieRequest.class);


        String key = "5dI2LJtk/z/gIEa0XsN66lASdvNHBmUV";
        String username = LoginUserInfo.getInstance().getLoginUserRealUserName(cordova.getActivity());

        String encodeUserName = StringUtils.EMPTY;
        try {
            encodeUserName = Base64Util.encode(DESUtil.des3EncodeECB(Base64Util.decode(key), username.getBytes()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        LogUtil.e("encode", "username ->" + username + " 3des encode-> "+ encodeUserName);

        String cookieValue = "UserID=" + encodeUserName + "&UserName=" + "" + "&TicketData=" + "";

        //default
        String cookieKey = "sso.rfchina.com";
        String domain = ".rfchina.com";

        if(null != setRfchinaCookieRequest) {
            if(!StringUtils.isEmpty(setRfchinaCookieRequest.mKey)) {
                cookieKey = setRfchinaCookieRequest.mKey;

            }

            if(!StringUtils.isEmpty(setRfchinaCookieRequest.mDomain)) {
                domain = setRfchinaCookieRequest.mDomain;
            }
        }


        WebkitSdkUtil.setCookies(webView.getView(), "http://" + domain,  cookieKey + "=" + cookieValue + ";Domain=" + domain);


        callbackContext.success();

    }

    /**
     * 处理关闭网页动作
     */
    private void handleExitWeb() {
        Intent intent = new Intent();
        cordova.getActivity().setResult(Activity.RESULT_OK, intent);
        cordova.getActivity().finish();
    }


    /**
     * 处理锁定titleBar动作
     * @param jsonArray
     */
    private void handleLockTitleBar(final JSONArray jsonArray) {
        try {
            Object object = jsonArray.get(0);
            if (object == null) {
                return;
            }
            boolean lockTitleBar = "lock".equalsIgnoreCase((String) object);
            ((WebViewBaseActivity) cordova.getActivity()).onTitleBarLock(lockTitleBar);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 处理头部标题
     * @param jsonArray
     */
    private void handleTitle(JSONArray jsonArray) {
        try {
            Object object = jsonArray.get(0);
            if (object == null) {
                return;
            }
            ((WebViewBaseActivity) cordova.getActivity()).onTitleChange((String) object);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void handleLeftButton(final JSONArray leftButton) {
        try {
            Object object = leftButton.get(0);
            if (object == null) {
                return;
            }
            String leftActionJs = (String) object;
            if(!leftActionJs.contains("(") && !leftActionJs.contains(")")) {
                leftActionJs += "()";
            }

            ((WebViewBaseActivity) cordova.getActivity()).onLeftButtonChange(leftActionJs);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void handleRightButtons(final JSONArray jsonArray) {
//        try {
//            if(jsonArray.get(0) instanceof JSONArray){
//                final JSONArray iconGroups = jsonArray.getJSONArray(0);
//                final JSONObject iconObject = iconGroups.getJSONObject(0);
//                if(iconObject.has("is_keep_original_menus")){
//                    if(verifyFlag(iconObject.get("is_keep_original_menus"))){
//                        mCallbackContext.error();
//                        return;
//                    }
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        ((WebViewBaseActivity)cordova.getActivity()).onRightButtonChange(jsonArray);

    }

    private void handleRightButtons1(final JSONArray jsonArray) {
        try {
            if(jsonArray.get(0) instanceof JSONObject){
                final JSONObject iconGroups = jsonArray.getJSONObject(0);
                if(iconGroups.has("flag")){
                    if(verifyFlag(iconGroups.get("flag"))){
                        mCallbackContext.error();
                        return;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        ((WebViewBaseActivity)cordova.getActivity()).onRightButtonChange1(jsonArray);

    }

    /**
     * Description:判断参数object是否为空，是否是integer类型
     * @param object
     * @return
     */
    private Boolean verifyFlag(Object object){
        if(StringUtils.isEmpty((String)object) || object instanceof Integer){
            return false;
        }
        else{
            return true;
        }

    }

    private void changeLeftButton(final JSONArray jsonArray) {
        ((WebViewBaseActivity)cordova.getActivity()).onChangeLeftButton(jsonArray);
    }

    private void showWatermark(JSONArray jsonArray) {
        try {
            JSONObject json = jsonArray.optJSONObject(0);
            String textColor = json.optString("textColor", "-1");
            String orgId = json.optString("orgId", "");
            int textSize = json.optInt("fontSize", -1);
            int paddingTop = json.optInt("verticalPadding", -1);
            double alpha = json.optDouble("alpha", 0);
            String addValue = json.optString("addValue", "");
            ((WebViewBaseActivity)cordova.getActivity()).setWatermark(true, orgId, textColor, textSize, paddingTop, alpha, addValue);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void removeWatermark(JSONArray jsonArray) {
        ((WebViewBaseActivity)cordova.getActivity()).setWatermark(false, "", "", -1, -1, 0, "");
    }


    private void handleShare(JSONArray jsonArray) {
        cordova.getActivity().runOnUiThread(() -> {

            JSONObject jsonObject = jsonArray.optJSONObject(0);
            ArticleItem articleItem = new Gson().fromJson(jsonObject.toString(), ArticleItem.class);

            if(StringUtils.isEmpty(articleItem.url)) {
                mCallbackContext.error();
                return;

            }


            String shareDirectlyType = StringUtils.EMPTY;
            String shareSessionId = StringUtils.EMPTY;
            int scope = 1;

            try {
                scope = jsonObject.optInt("scope", 1);

                shareDirectlyType = jsonObject.optString("directly");
                JSONObject directSessionJson = jsonObject.optJSONObject("direct_session");
                if (null != directSessionJson) {
                    shareSessionId = directSessionJson.optString("id");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }


            ((WebViewActivity)cordova.getActivity()).showSharePopupFromCordova(articleItem, scope, shareDirectlyType, shareSessionId, mCallbackContext);
        });

    }

    private void handleGetShareItems(JSONArray jsonArray, CallbackContext callbackContext) {
        List<String> shareTypes = new ArrayList<>();
        shareTypes.add("W6S_CONTACT");
        if(BeeWorks.getInstance().config.beeWorksShare.mShareWX.enable) {
            shareTypes.add("WX_CONTACT");
            shareTypes.add("WX_CIRCLE");

        }

        if(BeeWorks.getInstance().config.beeWorksShare.mShareQQ.mShareAndroid.enable) {
            shareTypes.add("QQ_CONTACT");
            shareTypes.add("QQ_ZONE");
        }

        shareTypes.add("SYSTEM_WEBVIEW");
        shareTypes.add("COPY_LINK");

        List<CordovaGetShareItem> shareItems = CollectionsKt.map(shareTypes, CordovaGetShareItem::new);
        callbackContext.success(new GetShareItemsResponse(shareItems.toArray(new CordovaGetShareItem[0])));


    }

    private void handleResetRightButton() {
        ((WebViewBaseActivity)cordova.getActivity()).onRightButtonReset(cordova.getActivity(), () -> mCallbackContext.success());
    }

    private void handleResetLeftButton() {
        ((WebViewBaseActivity)cordova.getActivity()).onLeftButtonReset(cordova.getActivity(), () -> mCallbackContext.success());
    }

    private void toMainActivity(final JSONArray jsonArray) {
        this.cordova.getActivity().runOnUiThread(() -> {
            JSONObject json = jsonArray.optJSONObject(0);

            if(null == json) {
                mCallbackContext.error();
            }

            String activity = json.optString("activity");
            String url = json.optString("next_url");




            Intent intent = new Intent();
            if ("toMain".equalsIgnoreCase(activity)) {
                String accessToken = json.optString("access_token");
                String clientId = json.optString("client_id");
                String username = json.optString("username");
                String name = json.optString("name");
                LoginToken loginToken = new LoginToken();
                loginToken.mAccessToken = accessToken;
                loginToken.mClientId = clientId;

                LoginUserInfo.getInstance().setLoginUserBasic(cordova.getActivity(), clientId, AtworkConfig.DOMAIN_ID, username, username, name, null, null);
                if (!TextUtils.isEmpty(accessToken) && !TextUtils.isEmpty(clientId)) {
                    LoginUserInfo.getInstance().setLoginToken(cordova.getActivity(), loginToken);
                }
                intent.setClass(cordova.getActivity(), MainActivity.class);

                SkinManger.getInstance().clean();

                if(!StringUtils.isEmpty(url)) {
                    AgreementManager.SHOULD_CHECK_AGREEMENT = false;
                    //下次进来app时, 再做检查
                    PersonalShareInfo.getInstance().setNeedCheckSignedAgreement(AtworkApplicationLike.baseContext, true);
                }

            }
            if ("toOrg".equalsIgnoreCase(activity)) {
                String orgCode = json.optString("orgcode");
                boolean needSetCurrentOrg = json.optBoolean("needSetCurrentOrg");

                if(StringUtils.isEmpty(PersonalShareInfo.getInstance().getCurrentOrg(AtworkApplicationLike.baseContext))) {
                    PersonalShareInfo.getInstance().setCurrentOrg(AtworkApplicationLike.baseContext, orgCode);
                }

                AppRefreshHelper.refreshAppAbsolutely(orgCode, true);

                if (needSetCurrentOrg && !StringUtils.isEmpty(orgCode)){
                    OrganizationSettingsHelper.getInstance().setCurrentOrgCodeAndRefreshSetting(cordova.getActivity(), orgCode, false);
                }

//                intent.putExtra(MainActivity.ACTION_TO_FRAGMENT, "orgFragment");
//                cordova.getActivity().setResult(Activity.RESULT_OK, intent);
                cordova.getActivity().overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                cordova.getActivity().finish();
                return;
            }
            if ("toPersonInfo".equalsIgnoreCase(activity)) {

            }

            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra(HandleLoginService.DATA_FROM_LOGIN, true);
            intent.putExtra(INTENT_KEY_NEXT_URL, url);
            finish(intent);
            //将当前用户存入
            UserManager.getInstance().asyncFetchLoginUserFromRemote(cordova.getActivity(), new UserAsyncNetService.OnQueryUserListener() {
                @Override
                public void networkFail(int errorCode, String errorMsg) {
                    ErrorHandleUtil.handleBaseError(errorCode, errorMsg);
                }

                @Override
                public void onSuccess(@NonNull User loginUser) {
                    LoginUserInfo.getInstance().setLoginUserBasic(cordova.getActivity(), loginUser.mUserId, loginUser.mDomainId, null, loginUser.mUsername, loginUser.mName, loginUser.mAvatar, loginUser.mSignature);
                }


            });
        });
    }

    private void finish(Intent intent) {
        cordova.getActivity().startActivity(intent);
        cordova.getActivity().overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
        cordova.getActivity().finish();
    }


    private void showBadgeOnTab(final JSONArray jsonArray) {

        SetLightNoticeDataRequest setLightNoticeDataRequest = NetGsonHelper.fromCordovaJson(jsonArray, SetLightNoticeDataRequest.class);
        if(null != setLightNoticeDataRequest) {
            String id = TabHelper.getId(setLightNoticeDataRequest.getTabId(BaseApplicationLike.baseContext));
            LightNoticeMapping lightNoticeMapping = SimpleLightNoticeMapping.createInstance(id, "oct_portal");
            TabNoticeManager.getInstance().update(lightNoticeMapping, setLightNoticeDataRequest);
        }

    }


    private void qqShare(JSONArray jsonArray, CallbackContext callbackContext) {
        WxOrQQShareRequest qqShareRequest = NetGsonHelper.fromCordovaJson(jsonArray, WxOrQQShareRequest.class);
        if(null == qqShareRequest) {
            callbackContext.error();
            return;
        }

        if (qqShareRequest.isUrlShare()) {
            qqUrlShare(qqShareRequest, callbackContext);
            return;
        }

    }

    private void qqUrlShare(WxOrQQShareRequest qqShareRequest, CallbackContext callbackContext) {
        ArticleItem articleItem = new ArticleItem();
        articleItem.url = qqShareRequest.getUrl();

        articleItem.title = qqShareRequest.getTitle();
        articleItem.summary = qqShareRequest.getDescription();

        articleItem.mCoverUrl = qqShareRequest.getThumb();
        articleItem.coverMediaId = qqShareRequest.getThumbMediaId();

        String appId;
        if (StringUtils.isEmpty(qqShareRequest.getAppId())) {
            appId = BeeWorks.getInstance().config.beeWorksShare.mShareQQ.mShareAndroid.appId;

        } else {

            appId = qqShareRequest.getAppId();
        }

        Companion.setCurrentCallbackContext(callbackContext);
        Companion.setAppId(appId);

        // 0表示会话 1表示QQ空间
        switch (qqShareRequest.getScene()) {
            case 0:
                TencentQQShare tencentQQShare = new TencentQQShare(cordova.getActivity(), appId);
                tencentQQShare.shareMessage(articleItem);
                break;

            case 1:
                TencentQZoneShare tencentQZoneShare = new TencentQZoneShare(cordova.getActivity(), appId);
                tencentQZoneShare.shareMessage(articleItem);
                break;
        }


    }

    private void wxShare(JSONArray jsonArray, CallbackContext callbackContext) {
        WxOrQQShareRequest wxShareRequest = NetGsonHelper.fromCordovaJson(jsonArray, WxOrQQShareRequest.class);
        if(null == wxShareRequest) {
            callbackContext.error();
            return;
        }


        if (wxShareRequest.isUrlShare()) {
            wxUrlShare(wxShareRequest, callbackContext);
            return;
        }

        if(wxShareRequest.isImageShare()) {
            wxImageShare(wxShareRequest, callbackContext);

            return;

        }

        if(wxShareRequest.isTxtShare()) {
            wxTxtShare(wxShareRequest, callbackContext);

            return;

        }

    }

    private void wxImageShare(WxOrQQShareRequest wxShareRequest, CallbackContext callbackContext) {
        if(null == wxShareRequest.getData()) {
            callbackContext.error();
            return;
        }

        String appId;
        if (StringUtils.isEmpty(wxShareRequest.getAppId())) {
            appId = BeeWorks.getInstance().config.beeWorksShare.mShareWX.appId;

        } else {

            appId = wxShareRequest.getAppId();
        }

        Companion.setCurrentCallbackContext(callbackContext);
        Companion.setAppId(appId);

        IWXAPI wxapi = WXAPIFactory.createWXAPI(cordova.getActivity(), appId);
        if(!wxapi.isWXAppInstalled()) {
            callbackContext.error(new CordovaBasicResponse(CordovaWxConstant.ERROR_CODE_APP_NOT_INSTALLED, CordovaWxConstant.errorCodeInfos.get(CordovaWxConstant.ERROR_CODE_APP_NOT_INSTALLED)));
            return;
        }

        // 0表示会话 1表示朋友圈 2表示收藏
        switch (wxShareRequest.getScene()) {
            case 0:
                WXSessionShare wxSessionShare = new WXSessionShare(cordova.getActivity(), wxShareRequest.getAppId());
                wxSessionShare.shareImage(wxShareRequest.getData().getImage());
                break;

            case 1:

                WXTimeLineShare wxTimeLineShare = new WXTimeLineShare(cordova.getActivity(), wxShareRequest.getAppId());
                wxTimeLineShare.shareImage(wxShareRequest.getData().getImage());
                break;
        }
    }

    private void wxTxtShare(WxOrQQShareRequest wxShareRequest, CallbackContext callbackContext) {


        String appId;
        if (StringUtils.isEmpty(wxShareRequest.getAppId())) {
            appId = BeeWorks.getInstance().config.beeWorksShare.mShareWX.appId;

        } else {

            appId = wxShareRequest.getAppId();
        }

        Companion.setCurrentCallbackContext(callbackContext);
        Companion.setAppId(appId);

        IWXAPI wxapi = WXAPIFactory.createWXAPI(cordova.getActivity(), appId);
        //检查微信是否已安装
        if(!wxapi.isWXAppInstalled()) {
            callbackContext.error(new CordovaBasicResponse(CordovaWxConstant.ERROR_CODE_APP_NOT_INSTALLED, CordovaWxConstant.errorCodeInfos.get(CordovaWxConstant.ERROR_CODE_APP_NOT_INSTALLED)));
            return;
        }


        // 0表示会话 1表示朋友圈 2表示收藏
        switch (wxShareRequest.getScene()) {
            case 0:
                WXSessionShare wxSessionShare = new WXSessionShare(cordova.getActivity(), wxShareRequest.getAppId());
                wxSessionShare.shareTxt(wxShareRequest.getDescription());
                break;
        }
    }

    private void wxUrlShare(WxOrQQShareRequest wxShareRequest, CallbackContext callbackContext) {
        ArticleItem articleItem = new ArticleItem();
        articleItem.url = wxShareRequest.getUrl();

        articleItem.title = wxShareRequest.getTitle();
        articleItem.summary = wxShareRequest.getDescription();
        articleItem.mCoverUrl = wxShareRequest.getThumb();

        String appId;
        if (StringUtils.isEmpty(wxShareRequest.getAppId())) {
            appId = BeeWorks.getInstance().config.beeWorksShare.mShareWX.appId;

        } else {

            appId = wxShareRequest.getAppId();
        }

        Companion.setCurrentCallbackContext(callbackContext);
        Companion.setAppId(appId);

        IWXAPI wxapi = WXAPIFactory.createWXAPI(cordova.getActivity(), appId);
        if(!wxapi.isWXAppInstalled()) {
            callbackContext.error(new CordovaBasicResponse(CordovaWxConstant.ERROR_CODE_APP_NOT_INSTALLED, CordovaWxConstant.errorCodeInfos.get(CordovaWxConstant.ERROR_CODE_APP_NOT_INSTALLED)));
            return;
        }


        // 0表示会话 1表示朋友圈 2表示收藏
        switch (wxShareRequest.getScene()) {
            case 0:
                WXSessionShare wxSessionShare = new WXSessionShare(cordova.getActivity(), wxShareRequest.getAppId());
                wxSessionShare.shareMessage(articleItem);
                break;

            case 1:

                WXTimeLineShare wxTimeLineShare = new WXTimeLineShare(cordova.getActivity(), wxShareRequest.getAppId());
                wxTimeLineShare.shareMessage(articleItem);
                break;
        }
    }


    private void voiceToText(JSONArray jsonArray, CallbackContext callbackContext) {
        Fragment fragment = cordova.getFragment();
        if(null == fragment) {
            callbackContext.error();
            return;
        }

        FragmentManager fragmentManager = fragment.getFragmentManager();
        if(null == fragmentManager) {
            callbackContext.error();
            return;
        }

        VoiceRecordDialogFragment voiceRecordDialogFragment = new VoiceRecordDialogFragment();
        voiceRecordDialogFragment.setPublishAction(result -> {
            if(null == result) {
                callbackContext.error();

            } else {
                VoiceRecordResultResponse resultResponse = new VoiceRecordResultResponse(result);
                callbackContext.success(resultResponse);

            }


            return Unit.INSTANCE;
        });

        voiceRecordDialogFragment.show(fragmentManager, "voiceRecordDialogPop");
    }


    private void registerShake() {
        if(cordova.getActivity() instanceof OnWebActivityActionListener) {
            OnWebActivityActionListener onWebActivityActionListener = (OnWebActivityActionListener) cordova.getActivity();
            onWebActivityActionListener.registerShake();
        }
    }

    private void unregisterShake() {
        if(cordova.getActivity() instanceof OnWebActivityActionListener) {
            OnWebActivityActionListener onWebActivityActionListener = (OnWebActivityActionListener) cordova.getActivity();
            onWebActivityActionListener.unregisterShake();
        }
    }

    public interface ActionCallbackListener {
        void onActionSuccess();
    }

}
