package com.foreveross.atwork.infrastructure.model.discussion;/**
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


import android.content.Context;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.Nullable;
import android.text.TextUtils;

import com.foreveross.atwork.infrastructure.model.ShowListItem;
import com.foreveross.atwork.infrastructure.model.employee.MoreInfo;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.utils.ContactHelper;
import com.foreveross.atwork.infrastructure.utils.FirstLetterUtil;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 群组
 * Created by reyzhang22 on 16/3/25.
 */
public class Discussion implements Parcelable, ShowListItem, Comparable {

    @SerializedName("discussion_id")
    public String mDiscussionId = StringUtils.EMPTY;

    @SerializedName("domain_id")
    public String mDomainId;

    @SerializedName("name")
    public String mName = StringUtils.EMPTY;

    @SerializedName("type")
    public String mType = StringUtils.EMPTY;

    @SerializedName("avatar")
    public String mAvatar = StringUtils.EMPTY;

    @SerializedName("notice")
    public String mNotice = StringUtils.EMPTY;

    @SerializedName("intro")
    public String mIntro = StringUtils.EMPTY;

    @SerializedName("pinyin")
    public String mPinyin = StringUtils.EMPTY;

    @SerializedName("initial")
    public String mInitial = StringUtils.EMPTY;

    @SerializedName("creator")
    public DiscussionCreator mCreator;

    @SerializedName("owner")
    public DiscussionOwner mOwner;

    @SerializedName("time_info")
    public TimeInfo mTimerInfo;

    @SerializedName("more_info")
    public MoreInfo mMoreInfo;


    /**
     * 组织ID (主要用于内部群)
     * */
    @SerializedName("org_id")
    private String mOrgId = StringUtils.EMPTY;

    /**
     * 群组关联组织的code(模板群创建时, 能够关联org code)
     * */
    @SerializedName("owner_code")
    private String mOwnerCode;

    @SerializedName("members")
    public CopyOnWriteArrayList<DiscussionMember> mMemberList = new CopyOnWriteArrayList<>();


    public List<ShowListItem> mMemberContactList = new ArrayList<>();

    public boolean mSelect;

    public void refreshMemberContactsDetailInfo(List<? extends ShowListItem> memberContactList) {
        mMemberContactList.clear();
        mMemberContactList.addAll(memberContactList);
    }

    public List<ShowListItem> changeMembersToContacts() {
        List<ShowListItem> contactList = new ArrayList<>();
        if (this.mMemberList == null) {
            return contactList;
        }

        contactList.addAll(this.mMemberList);

        return contactList;
    }

    public Discussion() {
    }

    @Override
    public int compareTo(Object another) {
        if (another == null || !(another instanceof Discussion)) {
            return -1;
        }
        Discussion other = (Discussion) another;

        if(StringUtils.isEmpty(mName)) {
            return 1;
        }

        if(StringUtils.isEmpty(other.mName)) {
            return -1;
        }



        if(isInternalDiscussion() && !other.isInternalDiscussion()) {
            return -1;
        }

        if(!isInternalDiscussion() && other.isInternalDiscussion()) {
            return 1;
        }

        return getCompareResultByName(other);
    }

    private int getCompareResultByName(Discussion other) {
        return FirstLetterUtil.getFullLetter(mName).compareTo(FirstLetterUtil.getFullLetter(other.mName));
    }

    private int getCompareResultByCreateTime(Discussion other) {
        long resultLong = other.mTimerInfo.mCreateTime - mTimerInfo.mCreateTime;

        int result;
        if(0 < resultLong) {
            result = 1;
        } else if(0 == resultLong){
            result = 0;
        } else {
            result = -1;
        }
        return result;
    }

    @Override
    public String getTitle() {
        return mName;
    }

    @Override
    public String getTitleI18n(Context context) {
        return getTitle();
    }

    @Override
    public String getTitlePinyin() {
        return null;
    }

    @Override
    public String getParticipantTitle() {
        return mName;
    }

    @Override
    public String getInfo() {
        return "";
    }

    @Override
    public String getAvatar() {
        return mAvatar;
    }

    @Override
    public String getId() {
        return mDiscussionId;
    }

    @Override
    public String getDomainId() {
        return mDomainId;
    }

    @Override
    public String getStatus() {
        return null;
    }

    @Override
    public boolean isSelect() {
        return mSelect;
    }

    @Override
    public void select(boolean isSelect) {
        mSelect = isSelect;
    }

    public void select() {
        mSelect = !mSelect;
    }

    @Override
    public boolean isOnline() {
        return false;
    }

    /**
     * 是否是群主
     * */
    public boolean isOwner(String userId) {
        return null != mOwner && mOwner.mUserId.equalsIgnoreCase(userId);
    }

    /**
     * 当前登陆者是否是群组
     * */
    public boolean isYouOwner(Context context) {
        return isOwner(LoginUserInfo.getInstance().getLoginUserId(context));
    }

    /**
     * 是否是内部群
     * @return
     */
    public boolean isInternalDiscussion() {
        return DiscussionType.INTERNAL_ORG.equalsIgnoreCase(mType) || DiscussionType.SYSTEM.equalsIgnoreCase(mType) || !StringUtils.isEmpty(mOrgId);
    }


    public boolean showEmployeeInfo() {
        return !StringUtils.isEmpty(getOrgCodeCompatible());
    }



    public boolean hasLabel() {


        if(isInternalDiscussion()) {
            return true;
        }

        return false;
    }

    public List<String> toMemberIdList() {
        List<String> memberIdList = new ArrayList<>();

        if (!ListUtil.isEmpty(mMemberList)) {
            for (DiscussionMember member : mMemberList) {
                memberIdList.add(member.userId);
            }

        }
        return memberIdList;
    }

    public List<String> toMemberNameList() {
        List<String> memberName = new ArrayList<>();

        if (!ListUtil.isEmpty(mMemberList)) {
            for (DiscussionMember member : mMemberList) {
                memberName.add(member.name);
            }

        }
        return memberName;
    }

    public void removeMember(ShowListItem contact){
        DiscussionMember memberDeleted = null;
        ShowListItem contactDeleted = null;

        for(DiscussionMember member : mMemberList) {
            if(member.userId.equals(contact.getId())) {
                memberDeleted = member;
                break;
            }
        }

        mMemberList.remove(memberDeleted);

        for(ShowListItem contactEach : mMemberContactList) {
            if(contactEach.getId().equals(contact.getId())) {
                contactDeleted = contactEach;
                break;
            }
        }
        mMemberContactList.remove(contactDeleted);

    }

    /**
     * 群组添加人员
     *
     * @param userList
     */
    public void addMemberUserList(String discussionId, List<User> userList) {
        for (User user : userList) {
            if (!mMemberContactList.contains(user)) {
                DiscussionMember groupMember = new DiscussionMember(discussionId, user.mUserId, user.mDomainId);
                mMemberList.add(groupMember);
                mMemberContactList.add(user);


            }
        }

        ContactHelper.sort(mMemberContactList);
    }

    @Nullable
    public DiscussionMember findMemberInfo(String userId) {
        for(DiscussionMember member : mMemberList) {
            if(userId.equalsIgnoreCase(member.getId())) {
                return member;
            }
        }

        return null;
    }

    public void setOrgId(String mOrgId) {
        this.mOrgId = mOrgId;
    }

    public void setOwnerCode(String ownerCode) {
        this.mOwnerCode = ownerCode;
    }

    public String getOrgId() {
        return mOrgId;
    }

    public String getOwnerCode() {
        return mOwnerCode;
    }

    @Nullable
    public String getOrgCodeCompatible() {
        if(!StringUtils.isEmpty(mOrgId)) {
            return mOrgId;
        }

        return mOwnerCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Discussion that = (Discussion) o;

        return mDiscussionId.equals(that.mDiscussionId);
    }

    @Override
    public int hashCode() {
        return mDiscussionId.hashCode();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mDiscussionId);
        dest.writeString(this.mDomainId);
        dest.writeString(this.mName);
        dest.writeString(this.mType);
        dest.writeString(this.mAvatar);
        dest.writeString(this.mNotice);
        dest.writeString(this.mIntro);
        dest.writeString(this.mPinyin);
        dest.writeString(this.mInitial);
        dest.writeParcelable(this.mCreator, flags);
        dest.writeParcelable(this.mOwner, flags);
        dest.writeParcelable(this.mTimerInfo, flags);
        dest.writeParcelable(this.mMoreInfo, flags);
        dest.writeString(this.mOrgId);
        dest.writeTypedList(this.mMemberList);

        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("member_contacts", (ArrayList<? extends Parcelable>) this.mMemberContactList);
        dest.writeBundle(bundle);

        dest.writeByte(this.mSelect ? (byte) 1 : (byte) 0);

    }

    protected Discussion(Parcel in) {
        this.mDiscussionId = in.readString();
        this.mDomainId = in.readString();
        this.mName = in.readString();
        this.mType = in.readString();
        this.mAvatar = in.readString();
        this.mNotice = in.readString();
        this.mIntro = in.readString();
        this.mPinyin = in.readString();
        this.mInitial = in.readString();
        this.mCreator = in.readParcelable(DiscussionCreator.class.getClassLoader());
        this.mOwner = in.readParcelable(DiscussionOwner.class.getClassLoader());
        this.mTimerInfo = in.readParcelable(TimeInfo.class.getClassLoader());
        this.mMoreInfo = in.readParcelable(MoreInfo.class.getClassLoader());
        this.mOrgId = in.readString();
        this.mMemberList = new CopyOnWriteArrayList<>(in.createTypedArrayList(DiscussionMember.CREATOR));
        this.mMemberContactList = in.readBundle(getClass().getClassLoader()).getParcelableArrayList("member_contacts");
        this.mSelect = in.readByte() != 0;


    }

    public static final Creator<Discussion> CREATOR = new Creator<Discussion>() {
        @Override
        public Discussion createFromParcel(Parcel source) {
            return new Discussion(source);
        }

        @Override
        public Discussion[] newArray(int size) {
            return new Discussion[size];
        }
    };
}
