package com.foreveross.atwork.infrastructure.newmessage.post.chat;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.foreveross.atwork.infrastructure.model.ShowListItem;
import com.foreveross.atwork.infrastructure.model.translate.TextTranslateStatus;
import com.foreveross.atwork.infrastructure.model.user.UserHandleInfo;
import com.foreveross.atwork.infrastructure.newmessage.ChatStatus;
import com.foreveross.atwork.infrastructure.newmessage.ReadStatus;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.BodyType;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.ChatSendType;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.ParticipantType;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.support.StringConstants;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.TimeUtil;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.internal.LinkedTreeMap;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by lingen on 15/4/16.
 * Description:
 */
public class TextChatMessage extends ChatPostMessage implements IAtContactMessage {

    public static final int AT = 1;
    public static final int COMMON = 0;

    private static final String TEXT_TYPE = "text_type";
    public static final String AT_CONTACTS = "at_contacts";
    public static final String AT_ALL = "at_all";
    private static final String ROUTE_LABELS = "route_labels";
    private static final String ROUTE_LINKS = "route_links";

    public static final String UNKNOWN_MESSAGE_CONTENT = "==known message==";

    @Expose
    public String text;

    //0表示普通消息  1 表示AT (text_type), 旧字段, 后面版本去除掉
    @Expose
    @Deprecated
    public int textType;

    //at人员 (at_contacts)
    public ArrayList<AtUser> mAtUserList;

    @Expose
    public boolean atAll;

    @Expose
    public TextTranslateStatus mTextTranslate;

    @Expose
    public boolean isUnknown = false;

    @Expose
    public List<String> routeLabels;

    @Expose
    public List<String> routeLinks;

    public TextChatMessage() {
        deliveryTime = TimeUtil.getCurrentTimeInMillis();
        deliveryId = UUID.randomUUID().toString();
    }

    public static TextChatMessage getTextChatMessageFromJson(Map<String, Object> jsonMap) throws JSONException {
        TextChatMessage textChatMessage = new TextChatMessage();
        textChatMessage.initPostTypeMessageValue(jsonMap);

        Map<String, Object> bodyMap = (Map<String, Object>) jsonMap.get(BODY);

        textChatMessage.initChatTypeMessageValue(bodyMap);
        textChatMessage.text = (String) bodyMap.get(CONTENT);

        if (bodyMap.containsKey(TEXT_TYPE)) {
            textChatMessage.textType = ((Double) bodyMap.get(TEXT_TYPE)).intValue();
        }

        if (bodyMap.containsKey(AT_ALL)) {
            textChatMessage.atAll = parseBoolean(String.valueOf(bodyMap.get(AT_ALL))) ;
        }


        if (bodyMap.containsKey(AT_CONTACTS)) {
            textChatMessage.mAtUserList = (ArrayList<AtUser>) bodyMap.get(AT_CONTACTS);

        }
        if (bodyMap.containsKey(SOURCE)) {
            textChatMessage.source = (String) bodyMap.get(SOURCE);
        }


        if (bodyMap.containsKey(ORG_ID)) {
            textChatMessage.mOrgId = (String) bodyMap.get(ORG_ID);

        }

        if(!StringUtils.isEmpty(textChatMessage.mDeletionPolicy) && bodyMap.containsKey(BURN)) {
            textChatMessage.mBurnInfo = BurnInfo.parseFromMap((Map<String, Object>) bodyMap.get(BURN));
        }


        if (bodyMap.containsKey(ROUTE_LABELS)) {
            textChatMessage.routeLabels = (ArrayList<String>) bodyMap.get(ROUTE_LABELS);

        }


        if (bodyMap.containsKey(ROUTE_LINKS)) {
            textChatMessage.routeLinks = (ArrayList<String>) bodyMap.get(ROUTE_LINKS);

        }

        //todo just test
//        textChatMessage.routeLabels = new ArrayList<>();
//        textChatMessage.routeLabels.add("hello");
//        textChatMessage.routeLabels.add("孙智");
//        textChatMessage.routeLabels.add("hello");
//
//        textChatMessage.routeLinks = new ArrayList<>();
//        textChatMessage.routeLinks.add("www.baidu.com");
//        textChatMessage.routeLinks.add("www.hao123.com");
//        textChatMessage.routeLinks.add("www.workplus.io");


        return textChatMessage;
    }



    /**
     * 生成无法解析的消息
     *
     * @return
     */
    public static TextChatMessage generaUnknownMessage(Map<String, Object> jsonMap) {
        TextChatMessage textChatMessage = new TextChatMessage();
        textChatMessage.initPostTypeMessageValue(jsonMap);
        textChatMessage.text = StringConstants.UNKNOWN_MSG;
        textChatMessage.mBodyType = BodyType.Text;
        textChatMessage.isUnknown = true;
        return textChatMessage;

    }


    public static boolean parseBoolean(String s) {
        return "true".equalsIgnoreCase(s) || "1".equalsIgnoreCase(s) || "1.0".equalsIgnoreCase(s);
    }

    public static TextChatMessage newSendTextMessage(Context context, String text
            , String to, String toDomain, ParticipantType toType, String toAvatar, String toName
            , BodyType bodyType, String orgId, boolean isBurn, long readTime, long deletionOnTime
            , String bingCreatorId) {

        TextChatMessage textChatMessage = new TextChatMessage();
        textChatMessage.buildSenderInfo(context);
        textChatMessage.text = text;
        textChatMessage.to = to;
        textChatMessage.mToType = toType;
        textChatMessage.mToDomain = toDomain;
        textChatMessage.chatSendType = ChatSendType.SENDER;
        textChatMessage.chatStatus = ChatStatus.Sending;
        textChatMessage.mBodyType = bodyType;
        textChatMessage.mOrgId = orgId;
        textChatMessage.read = ReadStatus.AbsolutelyRead;
        textChatMessage.mDisplayAvatar = toAvatar;
        textChatMessage.mDisplayName = toName;
        textChatMessage.mBingCreatorId = bingCreatorId;
        if (isBurn) {
            textChatMessage.mBurnInfo = new BurnInfo();
            textChatMessage.mBurnInfo.mReadTime = readTime;
            textChatMessage.mDeletionPolicy = "LOGICAL";
            textChatMessage.mDeletionOn = deletionOnTime;
        }
        return textChatMessage;
    }

    public static TextChatMessage newSendTextMessage(Context context, String text, String to, String toDomain,
                                                     ParticipantType toType, String orgId, ShowListItem receiverContact, boolean burn, long readTime, long deletionOnTime, String bingCreatorId) {
        String toAvatar = StringUtils.EMPTY;
        String toName = StringUtils.EMPTY;
        if (null != receiverContact) {
            toAvatar = receiverContact.getAvatar();
            toName = receiverContact.getTitle();
        }
        return newSendTextMessage(context, text, to, toDomain, toType, toAvatar, toName, BodyType.Text, orgId, burn, readTime, deletionOnTime, bingCreatorId);
    }

    public void setAtUsers(List<UserHandleInfo> handleInfoList) {
        mAtUserList = new ArrayList<>();
        for (UserHandleInfo handleInfo : handleInfoList) {
            mAtUserList.add(new AtUser(handleInfo.mUserId, handleInfo.mShowName));

        }
    }

    public void setAtAll(boolean atAll) {
        this.atAll = atAll;
    }

    @Override
    public Map<String, Object> getChatBody() {
        Map<String, Object> chatBody = new HashMap<>();
        chatBody.put(CONTENT, text);
        chatBody.put(TEXT_TYPE, textType);
        chatBody.put(AT_CONTACTS, mAtUserList);
        chatBody.put(AT_ALL, atAll);

        if (!TextUtils.isEmpty(mOrgId)) {
            chatBody.put(ORG_ID, mOrgId);
        }

        if(isBurn()) {
            chatBody.put(BURN, mBurnInfo.getChatMapBody());
        }
        setBasicChatBody(chatBody);


        return chatBody;
    }

    @Override
    public void reGenerate(Context context, String senderId, String receiverId, String receiverDomainId, ParticipantType fromType, ParticipantType toType, BodyType bodyType, String orgId, ShowListItem chatItem, String myName, String myAvatar) {
        super.reGenerate(context, senderId, receiverId, receiverDomainId, fromType, toType, bodyType, orgId, chatItem, myName, myAvatar);

        mTextTranslate = null;
        isUnknown = false;
    }

    @Override
    public ChatType getChatType() {
        return ChatType.Text;
    }

    @Override
    public String getSessionShowTitle() {
        if (isUnknown) {
            return StringConstants.SESSION_CONTENT_UNKNOWN_MESSAGE;
        }

        if (UNKNOWN_MESSAGE_CONTENT.equalsIgnoreCase(text)) {
            return StringConstants.SESSION_CONTENT_UNKNOWN_MESSAGE;
        }

        if(isBurn()) {
            return StringConstants.SESSION_CONTENT_BURN_MESSAGE;
        }

        if (StringUtils.isEmpty(text)) {
            return StringUtils.EMPTY;
        }


        return text;
    }

    @Override
    public String getSearchAbleString() {
        if(isBurn() || isUnknown) {
            return StringUtils.EMPTY;
        }
        return text;
    }

    @Override
    public boolean needNotify() {
        return true;
    }

    @Override
    public boolean needCount() {
        return true;
    }




    static public class AtUser implements Parcelable {
        @SerializedName("user_id")
        public String mUserId;

        @SerializedName("name")
        public String mName;

        public AtUser(String userId, String name) {
            this.mUserId = userId;
            mName = name;
        }


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.mUserId);
            dest.writeString(this.mName);
        }

        protected AtUser(Parcel in) {
            this.mUserId = in.readString();
            this.mName = in.readString();
        }

        public static final Parcelable.Creator<AtUser> CREATOR = new Parcelable.Creator<AtUser>() {
            @Override
            public AtUser createFromParcel(Parcel source) {
                return new AtUser(source);
            }

            @Override
            public AtUser[] newArray(int size) {
                return new AtUser[size];
            }
        };
    }

    @Override
    public String getContent() {
        return text;
    }

    @Override
    public boolean containAtMe(Context context) {
        String meUserId = LoginUserInfo.getInstance().getLoginUserId(context);
        return containAtUser(meUserId);
    }

    public boolean containAtUser(String userId) {
        boolean result = false;
        if (null != mAtUserList) {
            try {
                for (int i = 0; i < mAtUserList.size(); i++) {
                    Object object = mAtUserList.get(i);
                    if (object instanceof AtUser) {
                        AtUser atUser = (AtUser) object;
                        if (userId.equals(atUser.mUserId)) {
                            result = true;
                            break;
                        }
                    }
                    if (object instanceof LinkedTreeMap) {
                        LinkedTreeMap<String, String> map = (LinkedTreeMap) object;
                        String mapUserId = map.get("user_id");
                        if (userId.equalsIgnoreCase(mapUserId)) {
                            result = true;
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


        }

        return result;
    }

    @Override
    public boolean isAtMe(Context context) {
        String meUserId = LoginUserInfo.getInstance().getLoginUserId(context);
        return (atAll && !meUserId.equals(from)) || (containAtUser(meUserId));
    }


    /**
     * 是否正在翻译中
     * */
    public boolean isTranslating() {
        return null != mTextTranslate && mTextTranslate.mTranslating;
    }

    /**
     * 翻译的 UI 是否显示
     * */
    public boolean isTranslateStatusVisible() {
        return null != mTextTranslate && mTextTranslate.mVisible;

    }

    /**
     * 得到翻译结果
     * */
    public String getTranslatedResult() {
        if(null != mTextTranslate) {
            return mTextTranslate.mResult;
        }

        return StringUtils.EMPTY;
    }

    /**
     * 得到翻译的语种
     * */
    public String getTranslatedLanguage() {
        if(null != mTextTranslate && null !=  mTextTranslate.mTranslationLanguage) {
            return mTextTranslate.mTranslationLanguage;
        }

        return StringUtils.EMPTY;
    }



    public void showTranslateStatus(boolean visible) {
        if(null != mTextTranslate) {
            mTextTranslate.mVisible = visible;
        }
    }
}
