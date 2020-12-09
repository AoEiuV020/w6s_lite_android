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
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.share.ExternalShareType;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ArticleItem;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.AppUtil;
import com.foreveross.atwork.manager.BaseUiListener;
import com.foreveross.atwork.modules.chat.util.ArticleItemHelper;
import com.tencent.connect.share.QQShare;
import com.tencent.tauth.Tencent;

/**
 * Created by reyzhang22 on 16/3/10.
 */
public class TencentQQShare implements ExternalShareType {

    private Activity mActivity;

    private Tencent mTencent;
    private String mQQAppId = AtworkConfig.QQ_APP_ID;

    public TencentQQShare(Activity context) {
        mActivity = context;
    }


    public TencentQQShare(Activity context, String appId) {
        this(context);
        mQQAppId = appId;
    }

    @Override
    public void shareMessage(ArticleItem articleItem) {
        mTencent = Tencent.createInstance(mQQAppId, BaseApplicationLike.baseContext);
        final Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        params.putString(QQShare.SHARE_TO_QQ_TITLE, articleItem.title);
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, TextUtils.isEmpty(articleItem.summary) ? "" : articleItem.summary);
        if (articleItem.url.startsWith("http") || articleItem.url.startsWith("https")) {
            params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, articleItem.url);
        } else {
            params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, "http://" + articleItem.url);
        }

        String imageUrl = ArticleItemHelper.getCoverUrl(articleItem);

        if (!TextUtils.isEmpty(imageUrl)) {
            params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, imageUrl);
        }
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, AppUtil.getAppName(mActivity));


        mTencent.shareToQQ(mActivity, params, new BaseUiListener());
    }

    @Override
    public void shareImage(String imageData) {
        
    }

    @Override
    public void shareTxt(String txtData) {
        if (AppUtil.isQQClientAvailable(mActivity)) {
            Intent intent = new Intent("android.intent.action.SEND");
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
            intent.putExtra(Intent.EXTRA_TEXT, txtData);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setComponent(new ComponentName("com.tencent.mobileqq", "com.tencent.mobileqq.activity.JumpActivity"));
            mActivity.startActivity(intent);
            return;
        }
        Toast.makeText(mActivity, "您需要安装QQ客户端", Toast.LENGTH_LONG).show();
    }

    public void login(){
    }



}


