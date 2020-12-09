package com.foreveross.atwork.modules.search.model;

import android.content.Context;
import android.os.Parcel;

import com.foreveross.atwork.infrastructure.model.ShowListItem;

/**
 * Created by lingen on 15/3/27.
 * Description:
 */
public class SearchTextTitleItem implements ShowListItem {

    public SearchTextTitleItem(String titleText) {
        this.titleText = titleText;
    }

    public String titleText;

    public boolean center;
    @Override
    public String getTitle() {
        return titleText;
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
        return titleText;
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
        dest.writeString(this.titleText);
        dest.writeByte(this.center ? (byte) 1 : (byte) 0);
    }

    protected SearchTextTitleItem(Parcel in) {
        this.titleText = in.readString();
        this.center = in.readByte() != 0;
    }

    public static final Creator<SearchTextTitleItem> CREATOR = new Creator<SearchTextTitleItem>() {
        @Override
        public SearchTextTitleItem createFromParcel(Parcel source) {
            return new SearchTextTitleItem(source);
        }

        @Override
        public SearchTextTitleItem[] newArray(int size) {
            return new SearchTextTitleItem[size];
        }
    };
}
