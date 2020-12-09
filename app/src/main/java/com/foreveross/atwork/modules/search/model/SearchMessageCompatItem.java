package com.foreveross.atwork.modules.search.model;

import android.content.Context;
import android.os.Parcel;

import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.model.Session;
import com.foreveross.atwork.infrastructure.model.ShowListItem;

import java.util.ArrayList;

public class SearchMessageCompatItem implements ShowListItem {

    public Session session;

    public ArrayList<SearchMessageItem> mMessageList = new ArrayList<>();

    @Override
    public String getTitle() {
        return session.name;
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
        return session.name;
    }

    @Override
    public String getInfo() {

        return AtworkApplicationLike.getResourceString(R.string.relate_message_history, mMessageList.size() + "");
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

    public SearchMessageCompatItem() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.session, flags);
        dest.writeTypedList(this.mMessageList);
    }

    protected SearchMessageCompatItem(Parcel in) {
        this.session = in.readParcelable(Session.class.getClassLoader());
        this.mMessageList = in.createTypedArrayList(SearchMessageItem.CREATOR);
    }

    public static final Creator<SearchMessageCompatItem> CREATOR = new Creator<SearchMessageCompatItem>() {
        @Override
        public SearchMessageCompatItem createFromParcel(Parcel source) {
            return new SearchMessageCompatItem(source);
        }

        @Override
        public SearchMessageCompatItem[] newArray(int size) {
            return new SearchMessageCompatItem[size];
        }
    };
}
