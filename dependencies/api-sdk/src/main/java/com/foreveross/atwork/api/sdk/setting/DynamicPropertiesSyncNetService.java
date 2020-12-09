package com.foreveross.atwork.api.sdk.setting;

import android.content.Context;

import com.foreveross.atwork.api.sdk.UrlConstantManager;
import com.foreveross.atwork.api.sdk.net.HttpResult;
import com.foreveross.atwork.api.sdk.net.HttpURLConnectionComponent;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.AppUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;

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
 *                            |__|
 * Created by reyzhang22 on 16/7/8.
 * 异步域设置
 *
 */
public class DynamicPropertiesSyncNetService {

    public static final String TAG = DynamicPropertiesSyncNetService.class.getSimpleName();

    private static DynamicPropertiesSyncNetService sInstance = new DynamicPropertiesSyncNetService();

    public static DynamicPropertiesSyncNetService getInstance() {
        synchronized (TAG) {
            if (sInstance == null) {
                sInstance = new DynamicPropertiesSyncNetService();
            }
            return sInstance;
        }
    }

    /**
     * 获取域设置api接口
     * @param context
     * @param pkgName
     * @return
     */
    public HttpResult getDomainSettings(Context context, String pkgName) {
        String url = String.format(UrlConstantManager.getInstance().V2_getDomainSettings(), AtworkConfig.DOMAIN_ID, pkgName);

        String loginUserId = LoginUserInfo.getInstance().getLoginUserId(context);
        if(!StringUtils.isEmpty(loginUserId)) {
            url += ("&client_id=" + loginUserId);
        }

        url += ("&_v=" + AppUtil.getVersionCode(BaseApplicationLike.baseContext));

        HttpResult httpResult = HttpURLConnectionComponent.getInstance().getHttp(url);

        if (httpResult.isNetFail() || httpResult.isNetError() || httpResult.status != 0) {
            return null;
        }
        return httpResult;
    }

    /**
     * 获取组织设置api接口
     * @param context
     * @param orgId
     * @return
     */
    public HttpResult getOrganizationsSettings(Context context, String orgId, long refreshTime) {
        String token = LoginUserInfo.getInstance().getLoginUserAccessToken(context);
        final String url = String.format(UrlConstantManager.getInstance().V2_getCurrentUserOrganizationsSettings(), orgId, token, refreshTime);
        HttpResult httpResult = HttpURLConnectionComponent.getInstance().getHttp(url);

        if (httpResult.isNetFail() || httpResult.isNetError() || httpResult.status != 0) {
            return null;
        }
        return httpResult;
    }

}
