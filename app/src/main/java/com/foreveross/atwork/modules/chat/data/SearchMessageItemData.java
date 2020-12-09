package com.foreveross.atwork.modules.chat.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.utils.StringUtils;

import static com.foreveross.atwork.infrastructure.newmessage.messageEnum.BodyType.TEXT;


public class SearchMessageItemData implements Parcelable {

    public String mName = StringUtils.EMPTY;

    public String mType = TEXT;

    public boolean mIsTimeLine;

    public ChatPostMessage mMessage;

    public long msgTime;

    public SearchMessageItemData() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mName);
        dest.writeString(this.mType);
        dest.writeByte(this.mIsTimeLine ? (byte) 1 : (byte) 0);
        dest.writeSerializable(this.mMessage);
        dest.writeLong(this.msgTime);
    }

    protected SearchMessageItemData(Parcel in) {
        this.mName = in.readString();
        this.mType = in.readString();
        this.mIsTimeLine = in.readByte() != 0;
        this.mMessage = (ChatPostMessage) in.readSerializable();
        this.msgTime = in.readLong();
    }

    public static final Creator<SearchMessageItemData> CREATOR = new Creator<SearchMessageItemData>() {
        @Override
        public SearchMessageItemData createFromParcel(Parcel source) {
            return new SearchMessageItemData(source);
        }

        @Override
        public SearchMessageItemData[] newArray(int size) {
            return new SearchMessageItemData[size];
        }
    };
}
