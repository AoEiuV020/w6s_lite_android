package com.foreveross.atwork.infrastructure.utils.chat;

import com.foreveross.atwork.infrastructure.newmessage.post.chat.TemplateMessage;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by reyzhang22 on 17/8/18.
 */

public class TemplateDataHelper {

    public static final String INDEX_PREFIX = "{{";
    public static final String INDEX_DATA = ".DATA}}";
    public static final String KEYWORD = "keyword";
    public static final String KEY_COLOR = "color";
    public static final String KEY_FONT_SIZE = "font_size";
    public static final String KEY_VALUE = "value";
    public static final String KEY_TEXT_STYLE = "text_style";
    public static final String KEY_ALIGN = "align";
    public static final String KEY_CONTENT = "content";
    public static final String KEY_NAME = "name";
    public static final String KEY = "key";

    public static List<TemplateMessage.TemplateContent> parseTemplateContent(ArrayList<LinkedTreeMap> contents) {
        List<TemplateMessage.TemplateContent> templateContents = new ArrayList<>();
        if (ListUtil.isEmpty(contents)) {
            return templateContents;
        }
        for (LinkedTreeMap map : contents) {
            TemplateMessage.TemplateContent templateContent = new TemplateMessage.TemplateContent();
            setTemplateContentValue(templateContent, map);
            templateContents.add(templateContent);
        }
        return templateContents;
    }

    public static List<TemplateMessage.TemplateData> parseTemplateDataList(ArrayList<LinkedTreeMap> templateList) {
        List<TemplateMessage.TemplateData> list = new ArrayList<>();
        if (ListUtil.isEmpty(templateList)) {
            return list;
        }
        for (LinkedTreeMap map : templateList) {
            TemplateMessage.TemplateData templateData = new TemplateMessage.TemplateData();
            setTemplateDataValue(templateData, map);
            list.add(templateData);
        }
        return list;
    }

    public static List<TemplateMessage.TemplateActions> parseTemplateActionList(ArrayList<LinkedTreeMap> actionList) {
        List<TemplateMessage.TemplateActions> list = new ArrayList<>();
        if (ListUtil.isEmpty(actionList)) {
            return list;
        }
        for (LinkedTreeMap map : actionList) {
            TemplateMessage.TemplateActions action = new TemplateMessage.TemplateActions();
            setTemplateAction(action, map);
            list.add(action);
        }
        return list;
    }

    private static void setTemplateDataValue(TemplateMessage.TemplateData templateData, LinkedTreeMap map) {
        if (map.containsKey(KEY)) {
            templateData.mKey = (String) map.get(KEY);
        }
        if (map.containsKey(KEY_COLOR)) {
            templateData.mColor = (String) map.get(KEY_COLOR);
        }
        if (map.containsKey(KEY_FONT_SIZE)) {
            templateData.mFontSize = (String) map.get(KEY_FONT_SIZE);
        }
        if (map.containsKey(KEY_VALUE)) {
            templateData.mValue = (String) map.get(KEY_VALUE);
        }
        if (map.containsKey(KEY_TEXT_STYLE)) {
            templateData.mTextStyle = (String) map.get(KEY_TEXT_STYLE);
        }
    }

    private static void setTemplateContentValue(TemplateMessage.TemplateContent templateContent, LinkedTreeMap map) {
        if (map.containsKey(KEY_ALIGN)) {
            templateContent.mAlign = (String) map.get(KEY_ALIGN);
        }
        if (map.containsKey(KEY_CONTENT)) {
            templateContent.mContent = (String) map.get(KEY_CONTENT);
        }
    }

    private static void setTemplateAction(TemplateMessage.TemplateActions templateAction, LinkedTreeMap map) {
        if (map.containsKey(KEY_NAME)) {
            templateAction.mName = (String) map.get(KEY_NAME);
        }
        if (map.containsKey(KEY_VALUE)) {
            templateAction.mValue = (String) map.get(KEY_VALUE);
        }
        if (map.containsKey(KEY_COLOR)) {
            templateAction.mColor = (String) map.get(KEY_COLOR);
        }
    }
}
