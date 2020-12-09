package com.foreveross.atwork.api.sdk.auth.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.foreveross.atwork.api.sdk.model.BasicResponseJSON;
import com.foreveross.atwork.infrastructure.model.user.LoginToken;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.google.gson.annotations.SerializedName;


public class LoginTokenResponseJSON extends BasicResponseJSON {

    @SerializedName("result")
    public LoginToken mLoginToken;

    public void saveToShared(Context context) {
        if (!TextUtils.isEmpty(mLoginToken.mAccessToken) && !TextUtils.isEmpty(mLoginToken.mClientId)) {
            LoginUserInfo.getInstance().setLoginToken(context, mLoginToken);
        }

    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.status);
        dest.writeString(this.message);
        dest.writeParcelable(this.mLoginToken, flags);
    }

    public LoginTokenResponseJSON() {
    }

    protected LoginTokenResponseJSON(Parcel in) {
        this.status = in.readInt();
        this.message = in.readString();
        this.mLoginToken = in.readParcelable(LoginToken.class.getClassLoader());
    }

    public static final Parcelable.Creator<LoginTokenResponseJSON> CREATOR = new Parcelable.Creator<LoginTokenResponseJSON>() {
        @Override
        public LoginTokenResponseJSON createFromParcel(Parcel source) {
            return new LoginTokenResponseJSON(source);
        }

        @Override
        public LoginTokenResponseJSON[] newArray(int size) {
            return new LoginTokenResponseJSON[size];
        }
    };
}


