package com.foreveross.atwork.infrastructure.newmessage.messageEnum;/**
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


import androidx.annotation.Nullable;

import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.reference.ReferenceMessage;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;

import java.util.Map;

/**
 * Created by reyzhang22 on 16/4/13.
 */
public enum BodyType {
    /**
     * ack回执
     */
    Ack {
        @Override
        public String stringValue() {
            return ACK;
        }
    },
    /**
     * 图文
     */
    Article {
        @Override
        public String stringValue() {
            return ARTICLE;
        }
    },
    /**
     * 命令
     */
    Cmd {
        @Override
        public String stringValue() {
            return CMD;
        }
    },
    /**
     * 用户定义
     */
    Custom {
        @Override
        public String stringValue() {
            return CUSTOM;
        }
    },

    /**
     * 阅后即焚文本
     */
    Burn_Text {
        @Override
        public String stringValue() {
            return BURN_TEXT;
        }
    },

    /**
     * 阅后即焚图片
     */
    Burn_Image {
        @Override
        public String stringValue() {
            return BURN_IMAGE;
        }
    },

    /**
     * 阅后即焚语音
     */
    Burn_Voice {
        @Override
        public String stringValue() {
            return BURN_VOICE;
        }
    },

    /**
     * 文本
     */
    Text {
        @Override
        public String stringValue() {
            return TEXT;
        }
    },
    /**
     * 图片
     */
    Image {
        @Override
        public String stringValue() {
            return IMAGE;
        }
    },
    /**
     * 语音
     */
    Voice {
        @Override
        public String stringValue() {
            return VOICE;
        }
    },
    /**
     * 视频
     */
    Video {
        @Override
        public String stringValue() {
            return VIDEO;
        }
    },
    /**
     * 文件
     */
    File {
        @Override
        public String stringValue() {
            return FILE;
        }
    },
    /**
     * 坐标
     */
    Loc {
        @Override
        public String stringValue() {
            return LOC;
        }
    },
    /**
     * 事件
     */
    Event {
        @Override
        public String stringValue() {
            return EVENT;
        }
    },
    /**
     * 通知
     */
    Notice {
        @Override
        public String stringValue() {
            return NOTICE;
        }
    },

    /**
     * 会议通知
     * */
    MeetingNotice {
        @Override
        public String stringValue() {
            return MEETING_NOTICE;
        }
    },
    /**
     * 语音视频
     */
    Voip {
        @Override
        public String stringValue() {
            return VOIP;
        }
    },
    Share {
        @Override
        public String stringValue() {
            return SHARE;
        }
    },
    System {
        @Override
        public String stringValue() {
            return SYSTEM;
        }
    },

    /**
     * 合并转发
     */
    Multipart {
        @Override
        public String stringValue() {
            return MULTIPART;
        }
    },

    /**
     * 模板消息
     * */
    Template {
        @Override
        public String stringValue() {
            return TEMPLATE;
        }
    },

    /**
     * 必应消息文本消息
     * */
    BingText {
        @Override
        public String stringValue() {
            return BING_TEXT;
        }
    },


    /**
     * 必应消息语音消息
     * */
    BingVoice {
        @Override
        public String stringValue() {
            return BING_VOICE;
        }
    },

    /**
     * 必应消息确认消息
     * */
    BingConfirm {
        @Override
        public String stringValue() {
            return Bing_Confirm;
        }
    },
    /**
     * 3.0 的表情
     * */
    @Deprecated
    Sticker {
        @Override
        public String stringValue() {
            return STICKER;
        }
    },

    /**
     * 4.0 的表情
     * */
    Face {
        @Override
        public String stringValue() {
            return FACE;
        }
    },

    Gather {
        @Override
        public String stringValue() {
            return GATHER;
        }
    },

    /**
     * 引用消息
     * */
    Quoted {
        @Override
        public String stringValue() {
            return QUOTED;
        }
    },

    /**
     * 带comment的文件消息
     * */
    AnnoFile {
        @Override
        public String stringValue() {
            return ANNO_FILE;
        }
    },

    /**
     * 带comment的图片消息
     * */
    AnnoImage {
        @Override
        public String stringValue() {
            return ANNO_IMAGE;
        }
    },

    Doc {
        @Override
        public String stringValue() {
            return DOC;
        }
    },

    /**
     * 未知
     */
    UnKnown {
        @Override
        public String stringValue() {
            return UNKNOWN;
        }
    };

    public abstract String stringValue();

    /**
     * 根据字符串类型获取bodyType类型
     *
     * @param type
     * @return
     */
    public static BodyType toBodyType(String type) {
        if (ACK.equalsIgnoreCase(type)) {
            return Ack;
        }
        if (ARTICLE.equalsIgnoreCase(type)) {
            return Article;
        }
        if (CMD.equalsIgnoreCase(type)) {
            return Cmd;
        }
        if (CUSTOM.equalsIgnoreCase(type)) {
            return Custom;
        }
        if (TEXT.equalsIgnoreCase(type)) {
            return Text;
        }
        if (IMAGE.equalsIgnoreCase(type)) {
            return Image;
        }
        if (VIDEO.equalsIgnoreCase(type)) {
            return Video;
        }
        if (BURN_TEXT.equalsIgnoreCase(type)) {
            return Burn_Text;
        }
        if (BURN_IMAGE.equalsIgnoreCase(type)) {
            return Burn_Image;
        }
        if (BURN_VOICE.equalsIgnoreCase(type)) {
            return Burn_Voice;
        }
        if (VOICE.equalsIgnoreCase(type)) {
            return Voice;
        }
        if (FILE.equalsIgnoreCase(type)) {
            return File;
        }
        if (LOC.equalsIgnoreCase(type)) {
            return Loc;
        }
        if (EVENT.equalsIgnoreCase(type)) {
            return Event;
        }
        if (NOTICE.equalsIgnoreCase(type)) {
            return Notice;
        }

        if(MEETING_NOTICE.equalsIgnoreCase(type)) {
            return MeetingNotice;
        }

        if (VOIP.equalsIgnoreCase(type)) {
            return Voip;
        }
        if (SYSTEM.equalsIgnoreCase(type)) {
            return System;
        }
        if (SHARE.equalsIgnoreCase(type)) {
            return Share;
        }
        if (MULTIPART.equalsIgnoreCase(type)) {
            return Multipart;
        }
        if (TEMPLATE.equalsIgnoreCase(type)) {
            return Template;
        }

        if (BING_TEXT.equalsIgnoreCase(type)) {
            return BingText;
        }

        if (BING_VOICE.equalsIgnoreCase(type)) {
            return BingVoice;
        }

        if(Bing_Confirm.equalsIgnoreCase(type)) {
            return BingConfirm;
        }

        if (STICKER.equalsIgnoreCase(type)) {
            return Sticker;
        }

        if (FACE.equalsIgnoreCase(type)) {
            return Face;
        }

        if(GATHER.equalsIgnoreCase(type)) {
            return Gather;
        }


        if(QUOTED.equalsIgnoreCase(type)) {
            return Quoted;
        }

        if(ANNO_FILE.equalsIgnoreCase(type)) {
            return AnnoFile;
        }

        if(ANNO_IMAGE.equalsIgnoreCase(type)) {
            return AnnoImage;
        }

        if (DOC.equalsIgnoreCase(type)) {
            return Doc;
        }

        return UnKnown;
    }

    public static BodyType makeGenerateBodyCompatible(Map<String, Object> bodyMap, BodyType bodyType) {

        if (bodyMap.containsKey(ChatPostMessage.BURN)) {
            if(BodyType.Text == bodyType) {
                bodyType = BodyType.Burn_Text;
            } else if(BodyType.Image == bodyType) {
                bodyType = BodyType.Burn_Image;

            }else if(BodyType.Voice == bodyType) {
                bodyType = BodyType.Burn_Voice;
            }

        }


        return bodyType;
    }


    /**
     * 接收消息的格式转换兼容
     * */
    public static BodyType makeParseBodyCompatible(@Nullable Map<String, Object> bodyMap, BodyType bodyType) {
        if(BodyType.Burn_Text == bodyType) {
            bodyType = BodyType.Text;
        } else if(BodyType.Burn_Image == bodyType) {
            bodyType = BodyType.Image;

        }else if(BodyType.Burn_Voice == bodyType) {
            bodyType = BodyType.Voice;

        }
        return bodyType;
    }


    public static final String ACK = "ACK";
    public static final String ARTICLE = "ARTICLE";
    public static final String CMD = "CMD";
    public static final String CUSTOM = "CUSTOM";
    public static final String BURN_TEXT = "BURN_TEXT";
    public static final String BURN_IMAGE = "BURN_IMAGE";
    public static final String BURN_VOICE = "BURN_VOICE";
    public static final String TEXT = "TEXT";
    public static final String IMAGE = "IMAGE";
    public static final String VIDEO = "VIDEO";
    public static final String VOICE = "VOICE";
    public static final String FILE = "FILE";
    public static final String LOC = "LOCATION";
    public static final String EVENT = "EVENT";
    public static final String NOTICE = "NOTICE";
    public static final String MEETING_NOTICE = "MEETING_NOTICE";
    public static final String VOIP = "VOIP";
    public static final String SYSTEM = "SYSTEM";
    public static final String SHARE = "SHARE";
    public static final String MULTIPART = "MULTIPART";
    public static final String TEMPLATE = "TEMPLATE";
    public static final String BING_TEXT = "BING_TEXT";
    public static final String BING_VOICE = "BING_VOICE";
    public static final String Bing_Confirm = "Bing_Confirm";
    public static final String STICKER = "STICKER";
    public static final String FACE = "FACE";
    public static final String GATHER = "GATHER";
    public static final String QUOTED = "QUOTED";
    public static final String ANNO_FILE = "ANNO_FILE";
    public static final String ANNO_IMAGE = "ANNO_IMAGE";
    public static final String RED_ENVELOP_ROLLBACK_NOTICE = "RED_ENVELOP_ROLLBACK_NOTICE";
    public static final String DOC = "DOC";
    public static final String UNKNOWN = "UNKNOWN";
}
