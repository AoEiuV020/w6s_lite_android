package com.foreveross.atwork.modules.search.model;

import android.content.Context;
import android.os.Parcel;

import com.foreveross.atwork.infrastructure.model.ShowListItem;

public class SearchMoreItem implements ShowListItem {

    public SearchMoreItem(String titleText, SearchContent searchContent) {
        this.titleText = titleText;
        this.searchContent = searchContent;
    }

    public String titleText;

    public SearchContent searchContent;

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
        dest.writeInt(this.searchContent == null ? -1 : this.searchContent.ordinal());
    }

    protected SearchMoreItem(Parcel in) {
        this.titleText = in.readString();
        int tmpSearchContent = in.readInt();
        this.searchContent = tmpSearchContent == -1 ? null : SearchContent.values()[tmpSearchContent];
    }

    public static final Creator<SearchMoreItem> CREATOR = new Creator<SearchMoreItem>() {
        @Override
        public SearchMoreItem createFromParcel(Parcel source) {
            return new SearchMoreItem(source);
        }

        @Override
        public SearchMoreItem[] newArray(int size) {
            return new SearchMoreItem[size];
        }
    };
}
