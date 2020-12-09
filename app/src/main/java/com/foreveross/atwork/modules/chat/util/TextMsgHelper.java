package com.foreveross.atwork.modules.chat.util;

import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.TextChatMessage;
import com.foreveross.atwork.infrastructure.support.StringConstants;
import com.foreveross.atwork.infrastructure.utils.StringUtils;

/**
 * Created by dasunsy on 2017/6/29.
 */

public class TextMsgHelper {


    public static String getShowText(TextChatMessage textChatMessage) {
        String text = textChatMessage.text;
        if (textChatMessage.atAll) {
            text = text.replace(StringConstants.SESSION_CONTENT_AT_ALL, "@" + AtworkApplicationLike.getResourceString(R.string.at_all_group));

        } else if (textChatMessage.isUnknown) {
            text = AtworkApplicationLike.getResourceString(R.string.unknown_message);

        } else if (TextChatMessage.UNKNOWN_MESSAGE_CONTENT.equals(text)) { //旧版本处理方式
            text = AtworkApplicationLike.getResourceString(R.string.unknown_message);
        }

        return text;
    }

    public static void resetText(TextChatMessage textChatMessage) {
        textChatMessage.text = getVisibleText(textChatMessage);
    }

    public static String getVisibleText(TextChatMessage textChatMessage) {
        if(null != textChatMessage.mTextTranslate && textChatMessage.mTextTranslate.mVisible && !StringUtils.isEmpty(textChatMessage.mTextTranslate.mResult)) {
            //return getShowText(textChatMessage) + "\n\n" + textChatMessage.mTextTranslate.mResult;
            return getShowText(textChatMessage);
        }

        return getShowText(textChatMessage);
    }
}
