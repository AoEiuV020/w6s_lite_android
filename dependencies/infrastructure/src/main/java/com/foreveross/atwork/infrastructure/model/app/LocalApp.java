package com.foreveross.atwork.infrastructure.model.app;

import android.os.Parcel;

import com.foreveross.atwork.infrastructure.model.SessionType;

/**
 * Created by reyzhang22 on 15/8/7.
 */
public class LocalApp extends App {

    public static final String EMAIL_PREFIX= "SYSTEM://WORKPLUS-EMAIL";

    public SessionType type = SessionType.Local;

    public LocalApp() {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.type == null ? -1 : this.type.ordinal());
    }

    protected LocalApp(Parcel in) {
        super(in);
        int tmpType = in.readInt();
        this.type = tmpType == -1 ? null : SessionType.values()[tmpType];
    }

    public static final Creator<LocalApp> CREATOR = new Creator<LocalApp>() {
        @Override
        public LocalApp createFromParcel(Parcel source) {
            return new LocalApp(source);
        }

        @Override
        public LocalApp[] newArray(int size) {
            return new LocalApp[size];
        }
    };
}
