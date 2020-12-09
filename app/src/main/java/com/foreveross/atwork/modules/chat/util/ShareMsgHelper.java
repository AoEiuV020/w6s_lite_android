package com.foreveross.atwork.modules.chat.util;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.foreveross.atwork.api.sdk.UrlConstantManager;
import com.foreveross.atwork.infrastructure.model.WebViewControlAction;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ArticleItem;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ShareChatMessage;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.language.LanguageUtil;
import com.foreveross.atwork.modules.app.activity.WebViewActivity;
import com.foreveross.atwork.modules.app.route.UrlRouteHelper;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by dasunsy on 2017/6/23.
 */

public class ShareMsgHelper {

    public static void jumpOrgInvite(Context context, ShareChatMessage shareChatMessage) {
        ArticleItem articleItem = shareChatMessage.getContent();
        String title = "";
        if (null != articleItem) {
            title = articleItem.title;
            String lang = articleItem.mLang;
            if (TextUtils.isEmpty(lang)) {
                lang = LanguageUtil.getWorkplusLocaleTag(context);
            }
            String orgName = null;
            try {
                orgName = URLEncoder.encode(articleItem.mOrgName, "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();

                orgName = articleItem.mOrgName;
            }
            String coverMedia = articleItem.coverMediaId;
            if(StringUtils.isEmpty(coverMedia)) {
                coverMedia = articleItem.mOrgAvatar;
            }
            String url = String.format(UrlConstantManager.getInstance().V2_shareChatOrgInviteUrl(), articleItem.mOrgCode, orgName, coverMedia, articleItem.mOrgDomainId, lang);
            WebViewControlAction webViewControlAction = WebViewControlAction
                    .newAction()
                    .setUrl(url)
                    .setTitle(title)
                    .setArticleItem(shareChatMessage.getContent());

            Intent urlIntent = WebViewActivity.getIntent(context, webViewControlAction);
            context.startActivity(urlIntent);
        }
    }

    public static void jumpLinkShare(Context context, ShareChatMessage shareChatMessage) {
        ArticleItem articleItem = shareChatMessage.getContent();
        String title = "";
        if (null != articleItem) {
            title = articleItem.title;
        }

        WebViewControlAction webViewControlAction = WebViewControlAction.newAction()
                .setUrl(shareChatMessage.getContent().url)
                .setTitle(title)
                .setMessageId(shareChatMessage.deliveryId)
                .setArticleItem(shareChatMessage.getContent());


        UrlRouteHelper.routeUrl(context, webViewControlAction);

    }


}
