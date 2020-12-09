package com.foreveross.atwork.api.sdk.users.responseJson;

import android.os.Parcel;

import com.foreveross.atwork.api.sdk.model.BasicResponseJSON;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by dasunsy on 16/6/3.
 */
public class FriendSyncResponse extends BasicResponseJSON{
    @SerializedName("result")
    public List<FriendSyncItemJson> result;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeTypedList(this.result);
    }

    public FriendSyncResponse() {
    }

    protected FriendSyncResponse(Parcel in) {
        super(in);
        this.result = in.createTypedArrayList(FriendSyncItemJson.CREATOR);
    }

    public static final Creator<FriendSyncResponse> CREATOR = new Creator<FriendSyncResponse>() {
        @Override
        public FriendSyncResponse createFromParcel(Parcel source) {
            return new FriendSyncResponse(source);
        }

        @Override
        public FriendSyncResponse[] newArray(int size) {
            return new FriendSyncResponse[size];
        }
    };
}
