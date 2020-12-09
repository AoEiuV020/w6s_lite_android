package com.foreveross.atwork.infrastructure.newmessage.post.chat;

import android.content.Context;
import android.text.TextUtils;

import com.foreveross.atwork.infrastructure.newmessage.ChatStatus;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.BodyType;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.ChatSendType;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.ParticipantType;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.share.BusinessCardBody;
import com.foreveross.atwork.infrastructure.support.StringConstants;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.TimeUtil;
import com.foreveross.atwork.infrastructure.utils.UrlHandleHelper;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class ShareChatMessage extends ChatPostMessage {

    public final static String SHARE_MESSAGE = "share_message";

    public final static String SHARE_TYPE = "share_type";

    @Expose
    public ArticleItem mArticleItem = new ArticleItem();

    public LinkShareBody mLinkShareBody;

    private LocationBody mLocShareBody;

    private BusinessCardBody mBusinessCardBody;

    private OrgInviteBody mOrgInviteBody;

    @Expose
    public String mShareType;


    public ShareChatMessage() {
        deliveryTime = TimeUtil.getCurrentTimeInMillis();
        deliveryId = UUID.randomUUID().toString();
    }

    public static ShareChatMessage newSendShareMessage(Context context, ArticleItem articleItem, String sendId, String sendDomain,
                                                       String myName, String myAvatar, ParticipantType fromType,
                                                       BodyType bodyType, ShareType shareType) {

        ShareChatMessage shareChatMessage = new ShareChatMessage();
        shareChatMessage.buildSenderInfo(context);
        shareChatMessage.mArticleItem = articleItem;
//        shareChatMessage.from = sendId;
//        shareChatMessage.mFromDomain = sendDomain;
//        shareChatMessage.mMyName = myName;
//        shareChatMessage.mMyAvatar = myAvatar;
//        shareChatMessage.mFromType = fromType;
        shareChatMessage.mBodyType = bodyType;
        shareChatMessage.mShareType = shareType.toString();
        shareChatMessage.chatSendType = ChatSendType.SENDER;
        shareChatMessage.chatStatus = ChatStatus.Sending;
        return shareChatMessage;
    }

    public static ShareChatMessage getShareChatMessageFromArticleItem(ArticleItem articleItem) {
        ShareChatMessage shareChatMessage = new ShareChatMessage();
        shareChatMessage.mArticleItem = articleItem;

        shareChatMessage.mBodyType = BodyType.Share;
        shareChatMessage.mShareType = ShareType.Link.toString();
        shareChatMessage.chatSendType = ChatSendType.SENDER;
        shareChatMessage.chatStatus = ChatStatus.Sending;

        return shareChatMessage;
    }



    public static ShareChatMessage getShareChatMessageFromJson(Map<String, Object> jsonMap) throws Exception {
        ShareChatMessage shareChatMessage = new ShareChatMessage();
        shareChatMessage.initPostTypeMessageValue(jsonMap);
        Map<String, Object> bodyMap = (Map<String, Object>) jsonMap.get(BODY);
        shareChatMessage.initChatTypeMessageValue(bodyMap);

        Map<String, Object> data = (Map<String, Object>) bodyMap.get(SHARE_MESSAGE);
        String shareType = bodyMap.get(SHARE_TYPE).toString();
        shareChatMessage.mShareType = shareType;
        handleShareMessageByType(shareChatMessage, shareType, data);
        shareChatMessage.mDisplayAvatar = (String) bodyMap.get(DISPLAY_AVATAR);
        shareChatMessage.mDisplayName = (String) bodyMap.get(DISPLAY_NAME);
        shareChatMessage.mMyAvatar = (String) bodyMap.get(MY_AVATAR);
        shareChatMessage.mMyName = (String) bodyMap.get(MY_NAME);
        if (bodyMap.containsKey(SOURCE)) {
            shareChatMessage.source = (String)bodyMap.get(SOURCE);
        }
        return shareChatMessage;
    }

    public static ArticleItem getArticleItemFromJson(String shareUrl, JSONObject json) throws Exception {
        ArticleItem articleItem = new ArticleItem();
        articleItem.title = json.optString("title");
        articleItem.url = shareUrl;
        articleItem.mCoverUrl = json.optString("cover_url");
        articleItem.summary = json.optString("summary");
        String forwardMode = json.optString("forward_mode");
        return articleItem;
    }

    public static ShareChatMessage makeFakeShareMessage(String title, String url, String shareAvatar, String summary) {
        ShareChatMessage shareChatMessage = new ShareChatMessage();
        shareChatMessage.mFromType = ParticipantType.App;
        ArticleItem articleItem = new ArticleItem();

        shareChatMessage.mArticleItem = articleItem;
        articleItem.title = title;
        articleItem.url = url;
        articleItem.mCoverUrl = shareAvatar;
        articleItem.summary = summary;

        shareChatMessage.mBodyType = BodyType.Share;
        shareChatMessage.mShareType = ShareType.Link.toString();
        shareChatMessage.chatSendType = ChatSendType.SENDER;
        shareChatMessage.chatStatus = ChatStatus.Sending;


        return shareChatMessage;
    }


    private static void handleShareMessageByType(ShareChatMessage shareChatMessage, String type, Map<String, Object> data) throws Exception {
        shareChatMessage.mArticleItem = new ArticleItem();
        if ("LINK".equalsIgnoreCase(type)) {
            //TODO 以后可能要添加摘要  现在只有标题
            if (data.containsKey("summary")) {
                shareChatMessage.mArticleItem.summary = Objects.requireNonNull(data.get("summary")).toString();
            }
            if (data.containsKey("title")) {
                shareChatMessage.mArticleItem.title = Objects.requireNonNull(data.get("title")).toString();
            }
            if (data.containsKey("url")) {
                shareChatMessage.mArticleItem.url = Objects.requireNonNull(data.get("url")).toString();
            }
            if (data.containsKey("avatar")) {
                shareChatMessage.mArticleItem.mCoverUrl = Objects.requireNonNull(data.get("avatar")).toString();
            }


        } else if ("LOCATION".equalsIgnoreCase(type)) {
            if (data.containsKey("name")) {
                shareChatMessage.mArticleItem.mAoi = Objects.requireNonNull(data.get("name")).toString();
            }
            if (data.containsKey("address")) {
                shareChatMessage.mArticleItem.mAddress = Objects.requireNonNull(data.get("address")).toString();
            }
            if (data.containsKey("latitude")) {
                shareChatMessage.mArticleItem.mLatitude =  (Double) data.get("latitude");
            }
            if (data.containsKey("longitude")) {
                shareChatMessage.mArticleItem.mLongitude = (Double) data.get("longitude");
            }

        } else if ("BUSINESS_CARD".equalsIgnoreCase(type)) {
            if (data.containsKey("avatar")){
                shareChatMessage.mArticleItem.mShareUserAvatar = data.get("avatar").toString();
            }
            if (data.containsKey("user_id")){
                shareChatMessage.mArticleItem.mShareUserId = data.get("user_id").toString();
            }
            if (data.containsKey("domain_id")){
                shareChatMessage.mArticleItem.mShareDomainId = data.get("domain_id").toString();
            }
            if (data.containsKey("name")){
                shareChatMessage.mArticleItem.mShareUserName = data.get("name").toString();
            }
            if (data.containsKey("gender")){
                shareChatMessage.mArticleItem.mShareUserGender = data.get("gender").toString();
            }
            if (data.containsKey("job_title")){
                shareChatMessage.mArticleItem.mShareUserJobTitle = data.get("job_title").toString();
            }
            if (data.containsKey("job_org")){
                shareChatMessage.mArticleItem.mShareUserJobOrgCode = data.get("job_org").toString();
            }
            if (data.containsKey("status")) {
                shareChatMessage.mArticleItem.mShareUserSignature = data.get("status").toString();
            }

        } else if ("INVITE_TO_ORG".equalsIgnoreCase(type)) {
            if (data.containsKey("avatar")) {
                shareChatMessage.mArticleItem.mOrgAvatar = data.get("avatar").toString();
            }
            if (data.containsKey("org_code")) {
                shareChatMessage.mArticleItem.mOrgCode = data.get("org_code").toString();
            }
            if (data.containsKey("name")) {
                shareChatMessage.mArticleItem.mOrgName = data.get("name").toString();
            }
            if (data.containsKey("owner")) {
                shareChatMessage.mArticleItem.mOrgOwner = data.get("owner").toString();
            }
            if (data.containsKey("domain_id")) {
                shareChatMessage.mArticleItem.mOrgDomainId = data.get("domain_id").toString();
            }

            //新规定
            if(data.containsKey("description")) {
                shareChatMessage.mArticleItem.mDescription = data.get("description").toString();
            }

        } else {
            throw new Exception(); //未知类型不处理, 往外部抛出
        }
    }

    @Override
    public Map<String, Object> getChatBody() {
        Map<String, Object> chatMap = new HashMap<>();
        setBasicChatBody(chatMap);

        chatMap.put(SHARE_TYPE, mShareType);
        if ("LINK".equalsIgnoreCase(mShareType)) {
            mLinkShareBody = new LinkShareBody();
            mLinkShareBody.mAvatar = mArticleItem.mCoverUrl;
            //TODO 以后可能要添加摘要  现在只有标题
            mLinkShareBody.mTitle = mArticleItem.title;
            mLinkShareBody.mDigest = mArticleItem.title;
            mLinkShareBody.mUrl = mArticleItem.url;
            mLinkShareBody.mSummary = mArticleItem.summary;
            chatMap.put(SHARE_MESSAGE, mLinkShareBody);
        }
        if ("LOCATION".equalsIgnoreCase(mShareType)) {
            mLocShareBody = new LocationBody();
            mLocShareBody.mAddress = mArticleItem.mAddress;
            mLocShareBody.mName = mArticleItem.mAoi;
            mLocShareBody.mLatitude = mArticleItem.mLatitude;
            mLocShareBody.mLongitude = mArticleItem.mLongitude;
            chatMap.put(SHARE_MESSAGE, mLocShareBody);
        }
        if ("BUSINESS_CARD".equalsIgnoreCase(mShareType)) {
            mBusinessCardBody = new BusinessCardBody();
            mBusinessCardBody.mAvatar = mArticleItem.mShareUserAvatar;
            mBusinessCardBody.mName = mArticleItem.mShareUserName;
            mBusinessCardBody.mUserId = mArticleItem.mShareUserId;
            mBusinessCardBody.mDomainId = mArticleItem.mShareDomainId;
            mBusinessCardBody.mGender = mArticleItem.mShareUserGender;
            mBusinessCardBody.mSignature = mArticleItem.mShareUserSignature;
            mBusinessCardBody.mJobTitle = mArticleItem.mShareUserJobTitle;
            mBusinessCardBody.mJobOrgCode = mArticleItem.mShareUserJobOrgCode;
            chatMap.put(SHARE_MESSAGE, mBusinessCardBody);
        }
        if ("INVITE_TO_ORG".equalsIgnoreCase(mShareType)) {
            mOrgInviteBody = new OrgInviteBody();
            mOrgInviteBody.mAvatar = mArticleItem.mOrgAvatar;
            mOrgInviteBody.mName = mArticleItem.mOrgName;
            mOrgInviteBody.mOrgCode = mArticleItem.mOrgCode;
            mOrgInviteBody.mOwner = mArticleItem.mOrgOwner;
            mOrgInviteBody.mDomainId = mArticleItem.mOrgDomainId;
            mOrgInviteBody.mDescription = mArticleItem.mDescription; //todo 抽离出来, 这样实现太不好了
            mOrgInviteBody.mOrgEmployeeName = mArticleItem.mOrgInviterName;
            chatMap.put(SHARE_MESSAGE, mOrgInviteBody);
        }

        if (!TextUtils.isEmpty(mOrgId)) {
            chatMap.put(ORG_ID, mOrgId);
        }

        return chatMap;
    }

    @Override
    public ChatType getChatType() {
        return ChatType.Share;
    }

    @Override
    public String getSessionShowTitle() {
        if (ShareType.Link.toString().equals(mShareType)) {

            if(isVoteUrl()) {

                if(!StringUtils.isEmpty(mArticleItem.title)) {
                    return StringConstants.SESSION_CONTENT_VOTE_SHARE + mArticleItem.title;
                }

                if(!StringUtils.isEmpty(mArticleItem.summary)) {
                    return StringConstants.SESSION_CONTENT_VOTE_SHARE + mArticleItem.summary;
                }
            }


            if (!StringUtils.isEmpty(mArticleItem.title)) {

                return StringConstants.SESSION_CONTENT_LINK_SHARE + mArticleItem.title;
            }
            return StringConstants.SESSION_CONTENT_LINK_SHARE;
        }
        if (ShareType.Loc.toString().equals(mShareType)) {
            if (!StringUtils.isEmpty(mArticleItem.mAoi)) {
                return StringConstants.SESSION_LOCATION + mArticleItem.mAoi;
            }
            return StringConstants.SESSION_LOCATION;
        }

        if (ShareType.BusinessCard.toString().equals(mShareType)) {
            return StringConstants.SESSION_CONTENT_CARD_SHARE;
        }

        if (ShareType.OrgInviteBody.toString().equals(mShareType)) {
            return StringConstants.SESSION_CONTENT_ORG_SHARE;
        }

        return StringConstants.SESSION_CONTENT_LINK_SHARE;

    }

    @Override
    public String getSearchAbleString() {
        StringBuilder stringBuilder = new StringBuilder();
        if (ShareType.Link.toString().equals(mShareType)) {
            stringBuilder.append(mArticleItem.title).append(mArticleItem.summary);
        }
        return stringBuilder.toString();
    }

    @Override
    public boolean needNotify() {
        return true;
    }

    @Override
    public boolean needCount() {
        return true;
    }




    public ArticleItem getContent() {
        return mArticleItem;
    }

    public String getShareType() {
        return mShareType;
    }


    public boolean isVoteUrl() {
        if (!StringUtils.isEmpty(getContent().url)
                && getContent().url.contains("w6s_url_source=")) {

            String type = getUrlSource(getContent().url, "w6s_url_source=");
            if (type.equalsIgnoreCase("vote")) {
                return true;
            }

        }

        return false;

    }



    private String getUrlSource(String result, String key) {
        int keyStartIndex = result.lastIndexOf(key);
        String value = result.substring(keyStartIndex + key.length());
        int splitCharIndex = value.indexOf("&");
        if(-1 != splitCharIndex) {
            value = value.substring(0, splitCharIndex);
        }
        return value;
    }


    /**
     * 链接分享
     */
    public static class LinkShareBody implements Serializable {
        @SerializedName("avatar")
        @Expose
        public String mAvatar;

        @SerializedName("digest")
        @Expose
        public String mDigest;

        @SerializedName("url")
        @Expose
        public String mUrl;

        @SerializedName("title")
        @Expose
        public String mTitle;

        @SerializedName("summary")
        @Expose
        public String mSummary;
    }


    /**
     * 位置分享
     */
    public static class LocationBody implements Serializable {

        @SerializedName("latitude")
        @Expose
        public double mLatitude;

        @SerializedName("longitude")
        @Expose
        public double mLongitude;

        @SerializedName("address")
        @Expose
        public String mAddress;

        @SerializedName("poi")
        @Expose
        public String mPoi;

        @SerializedName("name")
        @Expose
        public String mName;
    }

    /**
     * 邀请组织
     */
    public static class OrgInviteBody implements Serializable {

        @SerializedName("org_code")
        @Expose
        public String mOrgCode;

        @SerializedName("name")
        @Expose
        public String mName;

        @SerializedName("avatar")
        @Expose
        public String mAvatar;

        @SerializedName("owner")
        @Expose
        public String mOwner;

        @SerializedName("domain_id")
        @Expose
        public String mDomainId;

        @Expose
        @SerializedName("inviter_name")
        public String mOrgEmployeeName;

        @SerializedName("description")
        @Expose
        public String mDescription;
    }

    public enum ShareType implements Serializable {
        Link {
            @Override
            public String toString() {
                return "LINK";
            }
        },
        Loc {
            @Override
            public String toString() {
                return "LOCATION";
            }
        },
        BusinessCard {
            @Override
            public String toString() {
                return "BUSINESS_CARD";
            }
        },
        OrgInviteBody {
            @Override
            public String toString() {
                return "INVITE_TO_ORG";
            }
        };

        public abstract String toString();
    }

}
