package com.foreveross.atwork.modules.qrcode.service;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.UrlConstantManager;
import com.foreveross.atwork.api.sdk.net.HttpResult;
import com.foreveross.atwork.api.sdk.qrcode.QrcodeAsyncNetService;
import com.foreveross.atwork.api.sdk.qrcode.QrcodeSyncNetService;
import com.foreveross.atwork.api.sdk.qrcode.responseModel.GetDiscussionJoinQrcodeResponseJson;
import com.foreveross.atwork.api.sdk.qrcode.responseModel.GetQrCodeJoinInfoResponse;
import com.foreveross.atwork.api.sdk.qrcode.responseModel.WorkplusQrCodeInfo;
import com.foreveross.atwork.api.sdk.util.NetworkHttpResultErrorHandler;
import com.foreveross.atwork.component.ProgressDialogHelper;
import com.foreveross.atwork.db.daoService.DiscussionDaoService;
import com.foreveross.atwork.infrastructure.manager.zoom.ZoomManager;
import com.foreveross.atwork.infrastructure.model.WebViewControlAction;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.infrastructure.model.zoom.HandleMeetingInfo;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.BitmapUtil;
import com.foreveross.atwork.infrastructure.utils.LogUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.PatternUtils;
import com.foreveross.atwork.infrastructure.utils.TimeUtil;
import com.foreveross.atwork.modules.discussion.manager.DiscussionManager;
import com.foreveross.atwork.manager.UserManager;
import com.foreveross.atwork.modules.app.activity.WebViewActivity;
import com.foreveross.atwork.modules.app.route.UrlRouteHelper;
import com.foreveross.atwork.modules.chat.activity.DiscussionScanAddActivity;
import com.foreveross.atwork.modules.contact.activity.PersonalInfoActivity;
import com.foreveross.atwork.modules.lite.activity.LiteBindScanActivity;
import com.foreveross.atwork.modules.lite.manager.LiteManager;
import com.foreveross.atwork.modules.lite.module.LiteBindConfig;
import com.foreveross.atwork.modules.qrcode.activity.QrInvoiceActivity;
import com.foreveross.atwork.modules.qrcode.activity.QrLoginActivity;
import com.foreveross.atwork.modules.qrcode.activity.ScanResultActivity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by dasunsy on 16/2/18.
 */
public class QrcodeManager {
    private static final String ACTION_DISCUSSION_JUMP = "#/DISCUSSION";
    private static final String ACTION_USER_JUMP = "#/USER";
    private static final String ACTION_QR_LOGIN="#/qr-login";
    public static final String OCT_RESULT_PREFIX = "EPASS-";
    public static final String ACTION_MEETING_JUMP = "#/Meeting";

    private static final Object sLock = new Object();
    private static QrcodeManager sInstance;
    private QrcodeManager() {

    }

    public static QrcodeManager getInstance() {
        synchronized (sLock) {
            if(null == sInstance) {
                sInstance = new QrcodeManager();
            }

        }

        return sInstance;

    }


    @SuppressLint("StaticFieldLeak")
    public void getDiscussionJoinQrcode(final Context ctx, final String discussionId, final String domainId, final QrcodeAsyncNetService.OnGetQrcodeListener onGetQrcodeListener) {
        new AsyncTask<Void, Void, HttpResult>() {
            @Override
            protected HttpResult doInBackground(Void... params) {
                String inviterName = LoginUserInfo.getInstance().getLoginUserName(ctx);

                try {
                    return QrcodeSyncNetService.getInstance().getDiscussionJoinQrcode(ctx, discussionId, domainId, URLEncoder.encode(inviterName, "utf-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(HttpResult httpResult) {
                if(httpResult.isRequestSuccess()) {
                    GetDiscussionJoinQrcodeResponseJson response = (GetDiscussionJoinQrcodeResponseJson) httpResult.resultResponse;
                    Bitmap qrBitmap = BitmapUtil.strToBitmap(response.result.content);
                    if(null != qrBitmap) {
                        onGetQrcodeListener.success(qrBitmap, TimeUtil.getCurrentTimeInMillis() + response.result.getSurvivalSeconds() * 1000);
                        return;
                    }


                }

                NetworkHttpResultErrorHandler.handleHttpError(httpResult, onGetQrcodeListener);


            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void handleSelfProtocol(final Activity act, String result) {
        handleSelfProtocol(act, result, null);
    }

    public void handleSelfProtocol(final Activity act, String result, @Nullable final OnQrcodeScanListener onQrcodeScanListener) {
        ProgressDialogHelper progressDialogHelper = new ProgressDialogHelper(act);
        progressDialogHelper.show();



        if(LiteManager.INSTANCE.matchBindRule(result)) {
            progressDialogHelper.dismiss();

            LiteBindConfig liteBindConfig = LiteManager.INSTANCE.produceBindConfig(result);

            LogUtil.e("绑定 -> " + liteBindConfig);


            Intent intent = LiteBindScanActivity.getIntent(act, liteBindConfig);
            act.startActivity(intent);

//            new AtworkAlertDialog(act, AtworkAlertDialog.Type.SIMPLE)
//                    .setContent(act.getString(R.string.bind_lite_config, liteBindConfig.getDomainName()))
//                    .setClickBrightColorListener(dialog -> {
//                        LiteManager.INSTANCE.updateBindConfig(liteBindConfig);
//                        AtworkToast.showToast("绑定成功");
//
//                        Intent dataIntent = new Intent();
//                        dataIntent.putExtra("LiteBindConfig", liteBindConfig);
//                        act.setResult(0, dataIntent);
//                        act.finish();
//
//                    })
//                    .setClickDeadColorListener(dialog -> {
//                        if(null != onQrcodeScanListener) {
//                            onQrcodeScanListener.onRestartQrScan();
//                        }
//                    })
//                    .show();


            return;
        }

        if(result.contains(ACTION_MEETING_JUMP)) {
            progressDialogHelper.dismiss();

            String key = getKey(result);
            String action = getLastValue(result, "action=");
            String meetingUrl = null;
            if(result.contains("url=")){
                meetingUrl = getUrl(result);
            }

            if(action.equalsIgnoreCase("join")){

                if(!StringUtils.isEmpty(key)) {

                    WebViewControlAction webViewControlAction = WebViewControlAction.newAction()
                            .setUrl(AtworkConfig.ZOOM_CONFIG.getDetailUrl() + "?confId=" + key)
                            .setNeedShare(false)
                            .setHideTitle(false);

                    UrlRouteHelper.routeUrl(act, webViewControlAction);

                    return;
                }


                String userId = LoginUserInfo.getInstance().getLoginUserId(act);
                String userName = LoginUserInfo.getInstance().getLoginUserUserName(act);
                HandleMeetingInfo handleMeetingInfo = new HandleMeetingInfo(userId, userName, null, null, key, null, null, meetingUrl);
                ZoomManager.INSTANCE.joinMeeting(act, handleMeetingInfo);
                act.finish();
            }

            return;


        }


        if(result.contains(ACTION_DISCUSSION_JUMP)) {
            String key = getKey(result);
            String addresser = getLastValue(result, "from=");

            QrcodeAsyncNetService.getInstance().getQrcodeRelativeInfo(act, key, addresser, new QrcodeAsyncNetService.OnGetQrcodeRelativeInfoListener() {
                @Override
                public void success(GetQrCodeJoinInfoResponse response) {
                    final WorkplusQrCodeInfo info = response.result.props;
                    info.setPinCode(key);

                    DiscussionDaoService.getInstance().queryDiscussionBasicInfo(info.getDiscussionId(), discussion -> {
                        if(null == discussion) {
                            goDiscussionAddAct(info, act);
                            progressDialogHelper.dismiss();

                        } else {
                            DiscussionManager.getInstance().goChatDetailAct(act, discussion);
                            //已经在群组里, 直接跳转打开
                            progressDialogHelper.dismiss();
                        }

                    });

                }


                @Override
                public void networkFail(int errorCode, String errorMsg) {
                    showNotMatch(act, result);
                }
            });
            return;
        }

        if (result.contains(ACTION_USER_JUMP)){
            String key = getKey(result);
            String addresser = getLastValue(result, "from=");

            QrcodeAsyncNetService.getInstance().getQrcodeRelativeInfo(act, key, addresser, new QrcodeAsyncNetService.OnGetQrcodeRelativeInfoListener() {
                @Override
                public void success(GetQrCodeJoinInfoResponse response) {
                    final WorkplusQrCodeInfo info = response.result.props;
                    User user = UserManager.getInstance().queryLocalUser(info.getUserId());
                    if (user == null) {
                        user = new User();
                        user.mUserId = info.getUserId();
                        user.mAvatar = info.avatar;
                        user.mName = info.name;
                        user.mDomainId = info.getDomainId();
                    }
                    act.startActivity(PersonalInfoActivity.getIntent(act, user));
                    progressDialogHelper.dismiss();
                    act.finish();
                }


                @Override
                public void networkFail(int errorCode, String errorMsg) {
                    progressDialogHelper.dismiss();
                    showNotMatch(act, result);

                }
            });
            return;
        }

        if (result.contains(ACTION_QR_LOGIN)) {
            String qrCode = getKey(result);
            String from = getLastValue(result, "from=");

            QrcodeAsyncNetService.getInstance().qrLogin(act, qrCode, "", new QrcodeAsyncNetService.OnQrLoginListener() {
                @Override
                public void networkFail(int errorCode, String errorMsg) {
                    progressDialogHelper.dismiss();
                    showNotMatch(act, result);

                }

                @Override
                public void success() {
                    //跳转登录页面
                    Intent intent = QrLoginActivity.getIntent(act, qrCode, from);
                    act.startActivity(intent);
                    act.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                    act.finish();
                }

            });
            return;
        }

        if (result.startsWith(OCT_RESULT_PREFIX)) {
            progressDialogHelper.dismiss();
            String ePassUrl = UrlConstantManager.getInstance().getOctQrResultUrl(result);
            WebViewControlAction webViewControlAction = WebViewControlAction.newAction().setUrl(ePassUrl);
            Intent intent = WebViewActivity.getIntent(act, webViewControlAction);
            act.startActivity(intent);
            act.finish();
            return;
        }

        progressDialogHelper.dismiss();

        showNotMatch(act, result);
    }

    private void showNotMatch(Activity activity, String resultString) {
        if (PatternUtils.isUrlLink(resultString)) {

            WebViewControlAction webViewControlAction = WebViewControlAction.newAction().setUrl(resultString);
            Intent intent = WebViewActivity.getIntent(activity, webViewControlAction);
            activity.startActivity(intent);
            activity.onBackPressed();
            return;
        }
        if (resultString.contains(",")) {
            String[] results = resultString.split(",");
            if (results.length == 9 && "01".equalsIgnoreCase(results[0]) && results[7].length() == 4) {
                Intent intent = QrInvoiceActivity.Companion.getIntent(activity, results);
                activity.startActivity(intent);
                activity.onBackPressed();
                return;
            }
            showScarnResult(activity, resultString);
            activity.onBackPressed();
            return;
        }
        showScarnResult(activity, resultString);

    }

    private void showScarnResult(Activity activity, String resultString) {
        Intent intent = ScanResultActivity.getIntent(activity, resultString);
        activity.startActivity(intent);
    }



    private void goDiscussionAddAct(WorkplusQrCodeInfo info, Activity act) {
        Intent intent = DiscussionScanAddActivity.getIntent(act, info);
        act.startActivity(intent);
        act.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }

    @NonNull
    private String getKey(String result) {
        int keyStartIndex = result.indexOf("id=");
        int keyLastIndex = result.indexOf("&");
        return result.substring(keyStartIndex + 3, keyLastIndex);
    }

    private String getUrl(String result) {
        int keyStartIndex = result.indexOf("url=");
        return result.substring(keyStartIndex + 4);
    }

    private String getLastValue(String result, String key) {
        int keyStartIndex = result.lastIndexOf(key);
        String value = result.substring(keyStartIndex + key.length());
        int splitCharIndex = value.indexOf("&");

        if(-1 != splitCharIndex) {
            value = value.substring(0, splitCharIndex);
        }
        return value;
    }


    public interface OnQrcodeScanListener {
        void onRestartQrScan();
    }

}
