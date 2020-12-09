package com.foreveross.atwork.infrastructure.newmessage.post.chat;

import androidx.annotation.Nullable;
import android.util.Log;

import com.foreveross.atwork.infrastructure.model.Employee;
import com.foreveross.atwork.infrastructure.model.orgization.Organization;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.infrastructure.utils.RegSchemaHelper;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.encryption.Base64Util;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by lingen on 15/6/3.
 * Description:
 */
public class ArticleItem implements Serializable {

    /**
     * 源消息id
     * */
    @Expose
    public String msgId;

    /**
     * 文章ID
     */
    @Expose
    public String id;

    /**
     * 文章标题
     */
    @Expose
    public String title;

    /**
     * 文章摘要
     */
    @SerializedName("summary")
    @Expose
    public String summary;

    /**
     * 文章作者
     */
    @Expose
    public String author;

    /**
     * 文章封面
     */
    @SerializedName("cover_media_id")
    @Expose
    public String coverMediaId;

    /**
     * 是否显示封面
     */
    @SerializedName("show_cover")
    @Expose
    public boolean showCover;

    /**
     * 文章内容
     */
    @Expose
    public String content;

    /**
     * 文章源URL
     */
    @SerializedName("content_source")
    @Expose
    public String contentSource;

    /**
     * 文章时间
     */
    @SerializedName("create_time")
    @Expose
    public long createTime;

    /**
     * 跳转url
     */
    @SerializedName("url")
    @Expose
    public String url;

    /**
     * 封面url
     */
    @SerializedName("cover_url")
    @Expose
    public String mCoverUrl;

    @Expose
    public DisplayMode displayMode;

    @Expose
    public String mDescription;

    @SerializedName("address")
    @Expose
    public String mAddress;

    @SerializedName("poi")
    @Expose
    public String mPoi;

    @SerializedName("aoi")
    @Expose
    public String mAoi;

    @Expose
    public double mLatitude;

    @Expose
    public double mLongitude;

    @Expose
    public String mShareUserJobOrgCode;

    @Expose
    public String mShareUserJobTitle;

    @Expose
    public String mShareUserSignature;

    @Expose
    public String mShareUserGender;

    @Expose
    public String mShareUserAvatar;

    @Expose
    public String mShareUserName;

    @Expose
    public String mShareDomainId;

    @Expose
    public String mShareUserId;

    @Expose
    @SerializedName("org_avatar")
    public String mOrgAvatar;

    @Expose
    @SerializedName("org_code")
    public String mOrgCode;

    @Expose
    @SerializedName("org_name")
    public String mOrgName;

    @Expose
    @SerializedName("org_owner")
    public String mOrgOwner;

    @Expose
    @SerializedName("org_domainId")
    public String mOrgDomainId;

    @Expose
    @SerializedName("inviter_name")
    public String mOrgInviterName;

    @SerializedName("lang")
    public String mLang;

    @SerializedName("forward_mode")
    public String mForwardMode;

    public static ArticleItem getArticleItemFromMap(Map<String, Object> valueMap) {
        ArticleItem articleItem = new ArticleItem();
        try {
            articleItem.id = (String) valueMap.get("id");
            articleItem.title = (String) valueMap.get("title");
            articleItem.summary = (String) valueMap.get("summary");
            articleItem.author = (String) valueMap.get("author");
            articleItem.coverMediaId = (String) valueMap.get("cover_media_id");
            if (valueMap.containsKey("show_cover")) {
                articleItem.showCover = (boolean) valueMap.get("show_cover");
            }
            articleItem.content = (String) valueMap.get("content");
            articleItem.contentSource = (String) valueMap.get("content_source");
            if (valueMap.containsKey("create_time")) {
                articleItem.createTime = ((Double) valueMap.get("create_time")).longValue();
            }
            articleItem.url = (String) valueMap.get("url");
            articleItem.mCoverUrl = (String) valueMap.get("cover_url");
            articleItem.displayMode = DisplayMode.fromStringValue((String) valueMap.get("display_mode"));
        } catch (Exception e) {
            Log.d("ARTICLE", "解析消息错误" + valueMap.toString());
        }
        return articleItem;
    }

    public boolean isShareAllMatch() {
        if (StringUtils.isEmpty(title)) {
            return false;
        }

        if (StringUtils.isEmpty(mCoverUrl) || !RegSchemaHelper.isUrlLink(mCoverUrl)) {
            return false;
        }

        if (StringUtils.isEmpty(summary)) {
            return false;
        }

        return true;
    }



    public boolean isCoverUrlFromLocal() {
        if (StringUtils.isEmpty(mCoverUrl)) {
            return false;
        }

        return mCoverUrl.startsWith("file://");
    }

    public boolean isCoverUrlFromBase64() {
        if (StringUtils.isEmpty(mCoverUrl)) {
            return false;
        }

        return mCoverUrl.startsWith("base64://");
    }


    public String getCoverUrlLocal() {
        if (isCoverUrlFromLocal()) {
            return mCoverUrl.substring("file://".length());
        }

        return StringUtils.EMPTY;
    }

    public byte[] getCoverByteBase64() {
        if(isCoverUrlFromBase64()) {
            return Base64Util.decode(mCoverUrl.substring("base64://".length()));
        }

        return new byte[0];
    }

    public void setBusinessCardData(User user, @Nullable Employee selectEmp) {
        if (null != user) {
            mShareUserAvatar = user.mAvatar;
            mShareUserId = user.mUserId;
            mShareUserName = user.mName;
            mShareDomainId = user.mDomainId;
            mShareUserGender = user.mGender;
            mShareUserSignature = user.getSignature();
        }

        if(null != selectEmp) {
            mShareUserJobTitle = selectEmp.getLastJobTitle();
            mShareUserJobOrgCode = selectEmp.orgCode;
        }

    }

    public void setOrgInviteData(Organization organization) {
        mOrgAvatar = organization.mLogo;
        mOrgCode = organization.mOrgCode;
        mOrgName = organization.mName;
        mOrgOwner = organization.mOwner;
        mOrgDomainId = organization.mDomainId;
    }

    public enum DisplayMode {

        FULL_SCREEN {
            @Override
            public int intValue() {
                return 1;
            }
        },

        DEFAULT {
            @Override
            public int intValue() {
                return 0;
            }
        };

        public abstract int intValue();

        public static DisplayMode fromIntValue(int value) {
            if (value == 0) {
                return DEFAULT;
            } else if (value == 1) {
                return FULL_SCREEN;
            }
            return DEFAULT;
        }

        public static DisplayMode fromStringValue(String value) {
            if ("FULL_SCREEN".equalsIgnoreCase(value)) {
                return FULL_SCREEN;
            }

            return DEFAULT;
        }
    }

}
