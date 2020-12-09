package com.foreveross.atwork.manager.share;/**
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
 */


import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;

import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.share.ExternalShareType;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ArticleItem;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.manager.BaseUiListener;
import com.foreveross.atwork.modules.chat.util.ArticleItemHelper;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.tauth.Tencent;

import java.util.ArrayList;

/**
 * Created by reyzhang22 on 16/6/21.
 */
public class TencentQZoneShare implements ExternalShareType {
    private Activity mActivity;

    private Tencent mTencent;
    private String mQQAppId = AtworkConfig.QQ_APP_ID;

    public TencentQZoneShare(Activity context) {
        mActivity = context;
    }

    public TencentQZoneShare(Activity context, String appId) {
        this(context);
        mQQAppId = appId;
    }

    @Override
    public void shareMessage(ArticleItem articleItem) {
        mTencent = Tencent.createInstance(mQQAppId, BaseApplicationLike.baseContext);
        final Bundle params = new Bundle();
        params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE,QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
        params.putString(QzoneShare.SHARE_TO_QQ_TITLE, articleItem.title);
        params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY,  TextUtils.isEmpty(articleItem.summary) ? "" : articleItem.summary);
        if (articleItem.url.startsWith("http") || articleItem.url.startsWith("https")) {
            params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, articleItem.url);
        } else {
            params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, "http://" + articleItem.url);
        }
        ArrayList<String> imageUrls = new ArrayList<>();
        String imageUrl = ArticleItemHelper.getCoverUrl(articleItem);

        if (!TextUtils.isEmpty(imageUrl)) {
            imageUrls.add(imageUrl);

        }
        params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imageUrls);

        params.putInt(QzoneShare.SHARE_TO_QQ_EXT_INT,  QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN);
        mTencent.shareToQzone(mActivity, params, new BaseUiListener());
    }

    @Override
    public void shareImage(String imageData) {

    }

    @Override
    public void shareTxt(String txtData) {

    }
}

