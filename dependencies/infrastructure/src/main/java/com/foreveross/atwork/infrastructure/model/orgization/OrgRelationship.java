package com.foreveross.atwork.infrastructure.model.orgization;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dasunsy on 16/5/12.
 */
public class OrgRelationship implements Parcelable{
    public String mOrgCode;
    public String mUserId;
    public int mType;
    public long mTime;

    public static class Type {
        public static final int APPLYING = 2;
        public static final int ADMIN = 1;
        public static final int EMPLOYEE = 0;
    }

    public OrgRelationship() {
    }

    @NonNull
    public static List<OrgRelationship> produceOrgRelationshipListByCodeList(String userId, int type, List<String> orgCodeList) {
        List<OrgRelationship> orgRelationshipList = new ArrayList<>();
        for(String orgCode : orgCodeList) {
            OrgRelationship relationship = new OrgRelationship();
            relationship.mOrgCode = orgCode;
            relationship.mUserId = userId;
            relationship.mType = type;

            orgRelationshipList.add(relationship);
        }

        return orgRelationshipList;
    }

    @NonNull
    public static List<OrgRelationship> produceOrgRelationshipListByOrgList(String userId, int type, List<Organization> organizationList) {
        List<OrgRelationship> orgRelationshipList = new ArrayList<>();
        if (organizationList == null) {
            return orgRelationshipList;
        }
        for(Organization org : organizationList) {
            OrgRelationship relationship = new OrgRelationship();
            relationship.mOrgCode = org.mOrgCode;
            relationship.mUserId = userId;
            relationship.mType = type;

            orgRelationshipList.add(relationship);
        }

        return orgRelationshipList;
    }

    public static ArrayList<String> getOrgCodeList(List<OrgRelationship> orgRelationList) {
        ArrayList<String> orgCodeList = new ArrayList<>();
        for(OrgRelationship orgRelationship : orgRelationList) {
            orgCodeList.add(orgRelationship.mOrgCode);
        }

        return orgCodeList;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mOrgCode);
        dest.writeString(this.mUserId);
        dest.writeInt(this.mType);
    }

    protected OrgRelationship(Parcel in) {
        this.mOrgCode = in.readString();
        this.mUserId = in.readString();
        this.mType = in.readInt();
    }

    public static final Creator<OrgRelationship> CREATOR = new Creator<OrgRelationship>() {
        @Override
        public OrgRelationship createFromParcel(Parcel source) {
            return new OrgRelationship(source);
        }

        @Override
        public OrgRelationship[] newArray(int size) {
            return new OrgRelationship[size];
        }
    };
}
