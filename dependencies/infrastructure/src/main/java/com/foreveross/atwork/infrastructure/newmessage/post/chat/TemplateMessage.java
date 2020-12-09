package com.foreveross.atwork.infrastructure.newmessage.post.chat;

import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.chat.TemplateDataHelper;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.internal.LinkedTreeMap;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by reyzhang22 on 17/8/16.
 */

public class TemplateMessage extends ChatPostMessage {

    public static final String DATA = "data";
    public static final String TITLE = "title";
    public static final String TYPE = "type";
    public static final String EMPHASIS_NAME = "emphasis_keyword";
    public static final String CONTENTS = "contents";
    public static final String ORG_ID = "org_id";
    public static final String TEMPLATE_ID = "template_id";
    public static final String TEMPLATE_TYPE = "template_type";
    public static final String ACTIONS = "actions";
    public static final String TOP_TITLE = "top_title";
    public static final String TOP_AVATAR = "top_avatar";
    public static final String TOP_COLOR = "top_color";

    @Expose
    public ArrayList<TemplateData> mDataList;

    /**
     * 模板标题
     */
    @Expose
    public String mTitle;

    /**
     * 模板类型
     */
    @Expose
    public String mType;

    /**
     * 高亮关键字
     */
    @Expose
    public String mEmphasisKeyword;
    /**
     * 模板内容
     */
    @Expose
    public List<List<TemplateContent>> mTemplateContents;
    /**
     * 模板ID
     */
    @Expose
    public String mTemplateId;
    /**
     * 模板类型
     */
    @Expose
    public String mTemplateType = "DEFAULT";
    /**
     * 模板动作
     */
    @Expose
    public List<TemplateActions> mTemplateActions;
    /**
     * 模板对应应用名称
     */
    @Expose
    public String mTopTitle;
    /**
     * 模板对应应用头像
     */
    @Expose
    public String mTopAvatar;
    /**
     * 模板顶部颜色
     */
    @Expose
    public String mTopColor;

    public static ChatPostMessage getTemplateMessageFromJson(Map<String, Object> jsonMap) throws JSONException {
        TemplateMessage templateMessage = new TemplateMessage();
        templateMessage.initPostTypeMessageValue(jsonMap);

        Map<String, Object> bodyMap = (Map<String, Object>) jsonMap.get(BODY);

        templateMessage.initChatTypeMessageValue(bodyMap);

        if (bodyMap.containsKey(TEMPLATE_TYPE)) {
            templateMessage.mTemplateType = (String)bodyMap.get(TEMPLATE_TYPE);
        }

//        if (TextUtils.isEmpty(templateMessage.mTemplateType) || !"DEFAULT".equalsIgnoreCase(templateMessage.mTemplateType)) {
//            return TextChatMessage.generaUnknownMessage(jsonMap);
//        }

        if (bodyMap.containsKey(DATA)) {
            ArrayList list = (ArrayList) bodyMap.get(DATA);
            templateMessage.mDataList = (ArrayList<TemplateData>) TemplateDataHelper.parseTemplateDataList(list);
        }

        if (bodyMap.containsKey(TITLE)) {
            templateMessage.mTitle = (String) bodyMap.get(TITLE);
        }

        if (bodyMap.containsKey(TYPE)) {
            templateMessage.mType = (String) bodyMap.get(TYPE);
        }

        if (bodyMap.containsKey(EMPHASIS_NAME)) {
            templateMessage.mEmphasisKeyword = (String)bodyMap.get(EMPHASIS_NAME);
        }

        if (bodyMap.containsKey(CONTENTS)) {
            List<List<TemplateContent>> templateContentList = new LinkedList<>();
            ArrayList<ArrayList> contentList = (ArrayList)bodyMap.get(CONTENTS);
            for (ArrayList<LinkedTreeMap> list : contentList) {
                templateContentList.add(TemplateDataHelper.parseTemplateContent(list));
            }
            templateMessage.mTemplateContents = templateContentList;
        }

        if (bodyMap.containsKey(TEMPLATE_ID)) {
            templateMessage.mTemplateId = (String)bodyMap.get(TEMPLATE_ID);
        }

        if (bodyMap.containsKey(ACTIONS)) {
            ArrayList<LinkedTreeMap> actionsList = (ArrayList)bodyMap.get(ACTIONS);
            templateMessage.mTemplateActions = TemplateDataHelper.parseTemplateActionList(actionsList);
        }

        if (bodyMap.containsKey(TOP_AVATAR)) {
            templateMessage.mTopAvatar = (String)bodyMap.get(TOP_AVATAR);
        }

        if (bodyMap.containsKey(TOP_TITLE)) {
            templateMessage.mTopTitle = (String)bodyMap.get(TOP_TITLE);
        }

        if (bodyMap.containsKey(TOP_COLOR)) {
            templateMessage.mTopColor = (String)bodyMap.get(TOP_COLOR);
        }

        return templateMessage;
    }

    @Override
    public Map<String, Object> getChatBody() {
        return null;
    }

    @Override
    public ChatType getChatType() {
        return ChatType.TEMPLATE;
    }

    @Override
    public String getSessionShowTitle() {
        return mTitle;
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



    public static class TemplateContent {
        @Expose
        @SerializedName("align")
        public String mAlign;
        @Expose
        @SerializedName("content")
        public String mContent;
    }

    public static class TemplateActions {
        @Expose
        @SerializedName("name")
        public String mName;

        @Expose
        @SerializedName("value")
        public String mValue;

        @Expose
        @SerializedName("color")
        public String mColor;
    }

    public static class TemplateData {
        @Expose
        @SerializedName("key")
        public String mKey = "";

        @Expose
        @SerializedName("color")
        public String mColor = "#222222";

        @Expose
        @SerializedName("font_size")
        public String mFontSize = "14";

        @Expose
        @SerializedName("value")
        public String mValue = "";

        @Expose
        @SerializedName("text_style")
        public String mTextStyle = "normal";

    }


}
