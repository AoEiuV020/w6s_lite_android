package com.foreveross.atwork.infrastructure.newmessage.post.chat;

import android.text.TextUtils;

import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.support.StringConstants;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by lingen on 15/6/3.
 * Description:
 */
public class ArticleChatMessage extends ChatPostMessage {

    public static final String ORG_ID_ ="org_id_";
    private final static String ARTICLES = "articles";
    private final static String FORWARD_MODE = "forward_mode";

    public String mOrgId_;

    @Expose
    public List<ArticleItem> articles = new ArrayList<>();




    public static ArticleChatMessage getArticleChatMessageFromJson(Map<String, Object> jsonMap) {
        ArticleChatMessage articleChatMessage = new ArticleChatMessage();
        articleChatMessage.initPostTypeMessageValue(jsonMap);
        Map<String, Object> bodyMap = (Map<String, Object>) jsonMap.get(BODY);

        articleChatMessage.initChatTypeMessageValue(bodyMap);

        List<Map<String, Object>> values = (List<Map<String, Object>>) bodyMap.get(ARTICLES);

        if (bodyMap.containsKey(ORG_ID_)) {
            articleChatMessage.mOrgId_ = (String) bodyMap.get(ORG_ID_);
        }

        if (bodyMap.containsKey(ORG_ID)) {
            articleChatMessage.mOrgId = (String) bodyMap.get(ORG_ID);
        }

        makeArticleItemUrlCompatible(articleChatMessage, values);

        if (bodyMap.containsKey(SOURCE)) {
            articleChatMessage.source = (String)bodyMap.get(SOURCE);
        }

        return articleChatMessage;
    }

    private static void makeArticleItemUrlCompatible(ArticleChatMessage articleChatMessage, List<Map<String, Object>> values) {
        for (Map<String, Object> valueMap : values) {
            //获得有数据的ArticleItem对象
            ArticleItem articleItem = ArticleItem.getArticleItemFromMap(valueMap);
            articleItem.msgId = articleChatMessage.deliveryId;

            if (!TextUtils.isEmpty(articleChatMessage.mOrgId_) && !articleItem.url.contains("&org_id_")) {

                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(articleItem.url);
                stringBuilder.append("&org_id_=").append(articleChatMessage.mOrgId_);
                articleItem.url = stringBuilder.toString();
            }
            articleChatMessage.addArticleItem(articleItem);
        }
    }

    public void addArticleItem(ArticleItem articleItem) {
        articles.add(articleItem);
    }

    @Override
    public ChatType getChatType() {
        return ChatType.Article;
    }

    @Override
    public String getSessionShowTitle() {
        if (articles != null && articles.size() > 0) {
            String title = articles.get(0).title;

            if (!StringUtils.isEmpty(title)) {
                return title;
            }
        }
        return StringConstants.SESSION_CONTENT_ARTICLE;
    }

    @Override
    public String getSearchAbleString() {
        return StringUtils.EMPTY;
    }

    @Override
    public boolean needNotify() {
        return true;
    }

    @Override
    public boolean needCount() {
        return true;
    }



    @Override
    public Map<String, Object> getChatBody() {
        return null;
    }



}
