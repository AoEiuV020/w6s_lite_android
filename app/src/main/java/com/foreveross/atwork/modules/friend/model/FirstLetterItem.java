package com.foreveross.atwork.modules.friend.model;

import android.content.Context;
import android.os.Parcel;

import com.foreveross.atwork.infrastructure.model.ShowListItem;

/**
 * Created by dasunsy on 16/5/19.
 */
public class FirstLetterItem implements ShowListItem {

    public FirstLetterItem(String letter) {
        this.mLetter = letter;
    }

    public String mLetter = "";

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public String getTitleI18n(Context context) {
        return null;
    }

    @Override
    public String getTitlePinyin() {
        return null;
    }

    @Override
    public String getParticipantTitle() {
        return null;
    }

    @Override
    public String getInfo() {
        return null;
    }

    @Override
    public String getAvatar() {
        return null;
    }

    @Override
    public String getId() {
        return null;
    }

    @Override
    public String getDomainId() {
        return null;
    }

    @Override
    public String getStatus() {
        return null;
    }

    @Override
    public boolean isSelect() {
        return false;
    }

    @Override
    public void select(boolean isSelect) {

    }

    @Override
    public boolean isOnline() {
        return false;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mLetter);
    }

    protected FirstLetterItem(Parcel in) {
        this.mLetter = in.readString();
    }

    public static final Creator<FirstLetterItem> CREATOR = new Creator<FirstLetterItem>() {
        @Override
        public FirstLetterItem createFromParcel(Parcel source) {
            return new FirstLetterItem(source);
        }

        @Override
        public FirstLetterItem[] newArray(int size) {
            return new FirstLetterItem[size];
        }
    };
}
