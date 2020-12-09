package com.foreveross.atwork.modules.chat.util;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.foreveross.atwork.infrastructure.model.Session;
import com.foreveross.atwork.infrastructure.model.SessionType;
import com.foreveross.atwork.infrastructure.model.WebViewControlAction;
import com.foreveross.atwork.infrastructure.model.app.App;
import com.foreveross.atwork.infrastructure.model.app.LightApp;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ArticleChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ArticleItem;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.modules.app.activity.WebViewActivity;
import com.foreveross.atwork.utils.ImageCacheHelper;

/**
 * Created by dasunsy on 2017/5/27.
 */

public class ArticleItemHelper {


    public static void startWebActivity(Context context, App app, ArticleChatMessage articleChatMessage, ArticleItem articleItem) {
        boolean needFullScreen = ArticleItem.DisplayMode.FULL_SCREEN.equals(articleItem.displayMode);
        String url = getUrl(articleItem);

        WebViewControlAction webViewControlAction = WebViewControlAction.newAction()
                .setUrl(url)
                .setTitle(app.getTitle())
                .setHideTitle(needFullScreen)
                .setSessionId(app.mAppId)
                .setMessageId(articleChatMessage.deliveryId)
                .setArticleItem(articleItem);

        if(app instanceof LightApp) {
            LightAppSessionHelper.startActivityFromLightApp(context, (LightApp) app, webViewControlAction);
            return;
        }

        Intent intent = WebViewActivity.getIntent(context, webViewControlAction);
        context.startActivity(intent);

    }

    public static void startWebActivity(Context context, Session session, ArticleChatMessage articleChatMessage, ArticleItem articleItem) {

        boolean needFullScreen = ArticleItem.DisplayMode.FULL_SCREEN.equals(articleItem.displayMode);
        String url = getUrl(articleItem);
        WebViewControlAction webViewControlAction = WebViewControlAction.newAction()
                .setUrl(url)
                .setTitle(session.name)
                .setHideTitle(needFullScreen)
                .setSessionId(session.identifier)
                .setMessageId(articleChatMessage.deliveryId)
                .setArticleItem(articleItem);

        if(SessionType.LightApp.equals(session.type)) {
            LightAppSessionHelper.startActivityFromLightAppSession(context, session, webViewControlAction);
            return;
        }
        Intent intent = WebViewActivity.getIntent(context, webViewControlAction);
        context.startActivity(intent);
    }

    private static String getUrl(ArticleItem articleItem) {
        if (!TextUtils.isEmpty(articleItem.url)) {
            return articleItem.url;

        //说明这时候后台有配置跳转的url,比如日程管理
        }
        if(!TextUtils.isEmpty(articleItem.contentSource) && articleItem.contentSource.startsWith("local://")) {
            return articleItem.contentSource;

        }
        return AtworkConfig.ARTICLE_URL + articleItem.id;
    }



    public static String getCoverUrl(ArticleItem articleItem) {
        if (!StringUtils.isEmpty(articleItem.mCoverUrl)) {
            return articleItem.mCoverUrl;
        }


        return ImageCacheHelper.getMediaUrl(articleItem.coverMediaId);

    }

}
