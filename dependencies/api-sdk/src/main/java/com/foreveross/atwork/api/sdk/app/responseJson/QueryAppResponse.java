package com.foreveross.atwork.api.sdk.app.responseJson;

import android.os.Parcel;

import com.foreveross.atwork.api.sdk.model.BasicResponseJSON;
import com.foreveross.atwork.infrastructure.model.app.App;
import com.google.gson.annotations.SerializedName;

/**
 * Created by dasunsy on 16/6/22.
 */
public class QueryAppResponse extends BasicResponseJSON {
    @SerializedName("result")
    public App result;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(this.result, flags);
    }

    public QueryAppResponse() {
    }

    protected QueryAppResponse(Parcel in) {
        super(in);
        this.result = in.readParcelable(App.class.getClassLoader());
    }

    public static final Creator<QueryAppResponse> CREATOR = new Creator<QueryAppResponse>() {
        @Override
        public QueryAppResponse createFromParcel(Parcel source) {
            return new QueryAppResponse(source);
        }

        @Override
        public QueryAppResponse[] newArray(int size) {
            return new QueryAppResponse[size];
        }
    };
}
