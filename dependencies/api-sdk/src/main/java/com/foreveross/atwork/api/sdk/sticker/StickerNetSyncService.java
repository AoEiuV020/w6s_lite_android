package com.foreveross.atwork.api.sdk.sticker;

import android.content.Context;

import com.foreveross.atwork.api.sdk.UrlConstantManager;
import com.foreveross.atwork.api.sdk.net.HttpResult;
import com.foreveross.atwork.api.sdk.net.HttpURLConnectionComponent;
import com.foreveross.atwork.api.sdk.sticker.responseJson.StickerAlbumResult;
import com.foreveross.atwork.api.sdk.util.NetGsonHelper;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;

public class StickerNetSyncService {

    private static volatile StickerNetSyncService sInstance = new StickerNetSyncService();

    public static StickerNetSyncService getInstance() {
        return sInstance;
    }

    /**
     * 检查专辑更新列表
     */
    public HttpResult checkStickerAlbums(Context context, String params) {
        String accessToken = LoginUserInfo.getInstance().getLoginUserAccessToken(context);
        String url = String.format(UrlConstantManager.getInstance().checkStickerAlbumsUrl(), accessToken);
        HttpResult result = HttpURLConnectionComponent.getInstance().postHttp(url, params);
        if (result.isNetSuccess()) {
            result.result(NetGsonHelper.fromNetJson(result.result, StickerAlbumResult.class));
        }
        return result;
    }


}
