package com.foreveross.atwork.api.sdk.users.responseJson;/**
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


import com.foreveross.atwork.infrastructure.model.user.User;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户星标返回json集合处理
 * Created by reyzhang22 on 16/4/9.
 */
public class ContactSyncItemJson {

    @SerializedName("id")
    public String mId;

    @SerializedName("contact")
    public ContactInfo mContactInfo;

    public class ContactInfo {
        @SerializedName("domain_id")
        public String mDomainId;

        @SerializedName("user_id")
        public String mUserId;

        @SerializedName("name")
        public String mName;

        @SerializedName("pinyin")
        public String mPinyin;

        @SerializedName("initial")
        public String mInitial;

        @SerializedName("first_letter")
        public String mFirstLetter;

        @SerializedName("phone")
        public String mPhone;

        @SerializedName("avatar")
        public String mAvatar;

    }

    @SerializedName("status")
    public String mStatus;

    @SerializedName("create_time")
    public long mCreateTime;

    @SerializedName("modify_time")
    public long mModifyTime;

    public static List<User> toUserList(List<ContactSyncItemJson> friendInfoList) {
        List<User> userList = new ArrayList<>();

        for(ContactSyncItemJson friendSyncItemJson : friendInfoList) {
            User user = new User();
            user.mUserId = friendSyncItemJson.mContactInfo.mUserId;
            user.mDomainId = friendSyncItemJson.mContactInfo.mDomainId;
            user.mAvatar = friendSyncItemJson.mContactInfo.mAvatar;
            user.mPinyin = friendSyncItemJson.mContactInfo.mPinyin;
            user.mInitial = friendSyncItemJson.mContactInfo.mInitial;
            user.mFirstLetter = friendSyncItemJson.mContactInfo.mFirstLetter;
            user.mName = friendSyncItemJson.mContactInfo.mName;
            user.mPhone = friendSyncItemJson.mContactInfo.mPhone;
            user.mStatus = friendSyncItemJson.mStatus;

            userList.add(user);
        }

        return userList;
    }

}
