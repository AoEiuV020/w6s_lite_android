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
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ __           __
 * .--.--.--.-----.----.|  |--.-----.|  |.--.--.-----.
 * |  |  |  |  _  |   _||    <|  _  ||  ||  |  |__ --|
 * |________|_____|__|  |__|__|   __||__||_____|_____|
 * |__|
 */


import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.beeworks.BeeWorks;
import com.foreveross.atwork.infrastructure.interfaces.IBindWbShareCallbackProxy;
import com.foreveross.atwork.infrastructure.interfaces.OnWbShareCallbackProxy;
import com.foreveross.atwork.infrastructure.lifecycle.IBindActivityLifecycleListener;
import com.foreveross.atwork.infrastructure.model.share.ExternalShareType;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ArticleItem;
import com.foreveross.atwork.infrastructure.utils.BitmapUtil;
import com.foreveross.atwork.infrastructure.utils.LogUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.modules.chat.util.ArticleItemHelper;
import com.foreveross.atwork.utils.AtworkToast;
import com.foreveross.atwork.utils.ImageCacheHelper;
import com.sina.weibo.sdk.WbSdk;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.share.WbShareCallback;
import com.sina.weibo.sdk.share.WbShareHandler;
import com.sina.weibo.sdk.utils.Utility;


public class WBShare implements ExternalShareType {

    private Activity mActivity;
    private WbShareHandler mShareHandler;

    public WBShare(Activity activity) {
        mActivity = activity;

        sdkInstall();
    }

    private void sdkInstall() {
        String appKey = BeeWorks.getInstance().config.beeWorksShare.mShareWB.appId;
        String redirectURI = BeeWorks.getInstance().config.beeWorksShare.mShareWB.redirectURI;
        String scope = StringUtils.EMPTY;
        WbSdk.install(mActivity, new AuthInfo(mActivity, appKey, redirectURI, scope));


        mShareHandler = new WbShareHandler(mActivity);
        mShareHandler.registerApp();

        if (mActivity instanceof IBindActivityLifecycleListener && mActivity instanceof WbShareCallback) {
            IBindActivityLifecycleListener bindWebActivityLifecycleListener = (IBindActivityLifecycleListener) mActivity;
            bindWebActivityLifecycleListener.bindOnLifecycleListener(intent -> mShareHandler.doResultIntent(intent, (WbShareCallback) mActivity));
        }

        if(mActivity instanceof IBindWbShareCallbackProxy) {
            IBindWbShareCallbackProxy bindWbShareCallbackProxy = (IBindWbShareCallbackProxy) mActivity;

            bindWbShareCallbackProxy.bindWbShareCallbackProxy(new OnWbShareCallbackProxy() {
                @Override
                public void onWbShareSuccess() {
                    AtworkToast.showToast("分享成功");
                }

                @Override
                public void onWbShareCancel() {
                    AtworkToast.showToast("分享取消");

                }

                @Override
                public void onWbShareFail() {
                    AtworkToast.showToast("分享失败");

                }
            });
        }


    }

    @Override
    public void shareMessage(ArticleItem message) {
        WebpageObject webpageObject = new WebpageObject();
        webpageObject.identify = Utility.generateGUID();
        webpageObject.title = message.title;
        webpageObject.description = message.summary;
        webpageObject.actionUrl = message.url;

        if(message.isCoverUrlFromBase64()) {
            shareToWbFromBase64Cover(message, webpageObject);

        } else {
            shareToWbFromHttpCover(message, webpageObject);
        }

    }

    @Override
    public void shareImage(String imageData) {

    }

    @Override
    public void shareTxt(String txtData) {

    }


    private void shareToWbFromBase64Cover(ArticleItem articleItem, WebpageObject webpageObject) {
        webpageObject.thumbData = articleItem.getCoverByteBase64();
        if(0 == webpageObject.thumbData.length) {
            Bitmap bitmap = BitmapFactory.decodeResource(mActivity.getResources(), R.mipmap.default_link);
            webpageObject.setThumbImage(bitmap);


        } else {
            webpageObject.setThumbImage(BitmapUtil.Bytes2Bitmap(webpageObject.thumbData));
        }

        doShareToWb(webpageObject);

    }

    private void doShareToWb(WebpageObject webpageObject) {
        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
        weiboMessage.mediaObject = webpageObject;
        mShareHandler.shareMessage(weiboMessage, false);
    }


    private void shareToWbFromHttpCover(ArticleItem articleItem, WebpageObject webpageObject) {
        String imageUrl = ArticleItemHelper.getCoverUrl(articleItem);

        ImageCacheHelper.loadImageByUrl(imageUrl, new ImageCacheHelper.ImageLoadedListener() {
            @Override
            public void onImageLoadedComplete(Bitmap bitmap) {
                if (bitmap != null) {
                    webpageObject.setThumbImage(bitmap);
                } else {
                    bitmap = BitmapFactory.decodeResource(mActivity.getResources(), R.mipmap.default_link);
                    webpageObject.setThumbImage(bitmap);
                }

                doShareToWb(webpageObject);
            }

            @Override
            public void onImageLoadedFail() {
                LogUtil.e("wx share~~~ but bitmap parse failed,  url : " + imageUrl);
                Bitmap bitmap = BitmapFactory.decodeResource(mActivity.getResources(), R.mipmap.default_link);
                webpageObject.setThumbImage(bitmap);

                doShareToWb(webpageObject);


            }
        });
    }
}
