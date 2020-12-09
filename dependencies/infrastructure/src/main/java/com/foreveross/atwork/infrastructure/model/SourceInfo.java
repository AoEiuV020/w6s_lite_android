package com.foreveross.atwork.infrastructure.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by dasunsy on 2018/1/3.
 */

public class SourceInfo implements Parcelable{

    public SourceType mSourceType;

    public String mSourceId;

    public String mDomainId;



    public String mName;

    public static SourceInfo newInstance() {
        return new SourceInfo();
    }

    public static SourceInfo createFrom(Session session) {
        SourceType sourceType = SourceType.createFrom(session.type);
        SourceInfo sourceInfo = SourceInfo.newInstance()
                .setSourceType(sourceType)
                .setSourceId(session.identifier)
                .setDomainId(session.mDomainId)
                .setName(session.name);

        return sourceInfo;
    }


    public SourceInfo setSourceType(SourceType sourceType) {
        mSourceType = sourceType;
        return this;
    }

    public SourceInfo setSourceId(String sourceId) {
        mSourceId = sourceId;
        return this;
    }

    public SourceInfo setDomainId(String domainId) {
        mDomainId = domainId;
        return this;
    }

    public SourceInfo setName(String name) {
        mName = name;
        return this;
    }

    public SourceInfo() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mSourceType == null ? -1 : this.mSourceType.ordinal());
        dest.writeString(this.mSourceId);
        dest.writeString(this.mDomainId);
        dest.writeString(this.mName);
    }

    protected SourceInfo(Parcel in) {
        int tmpMSourceType = in.readInt();
        this.mSourceType = tmpMSourceType == -1 ? null : SourceType.values()[tmpMSourceType];
        this.mSourceId = in.readString();
        this.mDomainId = in.readString();
        this.mName = in.readString();
    }

    public static final Creator<SourceInfo> CREATOR = new Creator<SourceInfo>() {
        @Override
        public SourceInfo createFromParcel(Parcel source) {
            return new SourceInfo(source);
        }

        @Override
        public SourceInfo[] newArray(int size) {
            return new SourceInfo[size];
        }
    };
}
