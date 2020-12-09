package com.foreveross.atwork.api.sdk.qrcode;

import android.content.Context;

import com.foreveross.atwork.api.sdk.UrlConstantManager;
import com.foreveross.atwork.api.sdk.model.BasicResponseJSON;
import com.foreveross.atwork.api.sdk.net.BaseSyncNetService;
import com.foreveross.atwork.api.sdk.net.HttpResult;
import com.foreveross.atwork.api.sdk.net.HttpURLConnectionComponent;
import com.foreveross.atwork.api.sdk.qrcode.responseModel.GetDiscussionJoinQrcodeResponseJson;
import com.foreveross.atwork.api.sdk.qrcode.responseModel.GetQrCodeJoinInfoResponse;
import com.foreveross.atwork.api.sdk.qrcode.responseModel.PersonalQrcodeResponseJson;
import com.foreveross.atwork.api.sdk.util.NetGsonHelper;
import com.foreveross.atwork.api.sdk.util.NetWorkHttpResultHelper;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;

/**
 * Created by dasunsy on 16/4/28.
 */
public class QrcodeSyncNetService extends BaseSyncNetService {
    private static final Object sLock = new Object();

    private static final String FORMAT_IMG = "image";

    private static final String FORMAT_URL = "url";

    private static QrcodeSyncNetService sInstance;

    private UrlConstantManager mUrlConstantManager = UrlConstantManager.getInstance();


    private QrcodeSyncNetService() {

    }

    public static QrcodeSyncNetService getInstance() {
        synchronized (sLock) {
            if(null == sInstance) {
                sInstance = new QrcodeSyncNetService();
            }

            return sInstance;
        }
    }

    /**
     * 根据扫描得到的 id pin 去后台拿二维码对应的信息
     * @param ctx
     * @param id
     * @param addresser
     *
     * @return httpResult
     * */
    public HttpResult getQrcodeRelativeInfo(Context ctx, String id, String addresser) {
        String getUrl = String.format(mUrlConstantManager.V2_getInfoFromQrPinId(), id, addresser, LoginUserInfo.getInstance().getLoginUserAccessToken(ctx));
        HttpResult httpResult = HttpURLConnectionComponent.getInstance().getHttp(getUrl);
        if(httpResult.isNetSuccess()) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, GetQrCodeJoinInfoResponse.class));
        }

        return httpResult;
    }

    /**
     * 获取群二维码
     * @param ctx
     * @param discussionId
     * @param domainId
     * @param inviterName
     *
     * @return httpResult
     * */
    public HttpResult getDiscussionJoinQrcode(Context ctx, String discussionId, String domainId, String inviterName) {
        String getUrl = String.format(mUrlConstantManager.V2_getDiscussionQrcode(), discussionId, domainId, inviterName, LoginUserInfo.getInstance().getLoginUserAccessToken(ctx));
        HttpResult httpResult = HttpURLConnectionComponent.getInstance().getHttp(getUrl);
        if(httpResult.isNetSuccess()) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, GetDiscussionJoinQrcodeResponseJson.class));
        }

        return httpResult;
    }


    /**
     * 获取个人信息的二维码
     * @param context
     * @param userId
     *
     * @return result
     * */
    public HttpResult getPersonalQrcode(Context context , String userId){
        String url = String.format(UrlConstantManager.getInstance().V2_fetchShareUserUrl(),userId, LoginUserInfo.getInstance().getLoginUserAccessToken(context), FORMAT_IMG);
        HttpResult result = HttpURLConnectionComponent.getInstance().getHttp(url);
        if (result.isNetSuccess()) {
            result.result(NetGsonHelper.fromNetJson(result.result, PersonalQrcodeResponseJson.class));
        }

        return result;
    }

    /**
     * 二维码登录
     * @param context
     * @param params
     *
     * @return httpResult
     * */
    public HttpResult qrLogin(Context context, String params) {
        String url = String.format(mUrlConstantManager.V2_QrLogin(), LoginUserInfo.getInstance().getLoginUserAccessToken(context));
        HttpResult httpResult = HttpURLConnectionComponent.getInstance().postHttp(url, params);
        if(httpResult.isNetSuccess()) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, BasicResponseJSON.class));
        }

        return httpResult;
    }

    public String getOrgQrUrl(Context context ,String orgId){
        String url = String.format(UrlConstantManager.getInstance().V2_fetchShareOrgUrl(),orgId,LoginUserInfo.getInstance().getLoginUserAccessToken(context),FORMAT_URL, URLEncoder.encode(LoginUserInfo.getInstance().getLoginUserBasic(context).mName));
        HttpResult result = HttpURLConnectionComponent.getInstance().getHttp(url);
        if (isHttpError(result)) {
            return null;
        }
        String resultText = NetWorkHttpResultHelper.getResultText(result.result);
        String codeUrl = "";
        try {
            JSONObject jsonObject = new JSONObject(resultText);
            codeUrl = jsonObject.optString("content");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return codeUrl;
    }

}
