package com.foreveross.atwork.api.sdk.app.responseJson;/**
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@%((((((((#&@@@((#((#@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@%(((((((((((((%@*((((@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@%(((/,/(((((((((@@@*#@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@#(((@@@((((((((((@@((@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@(((((((((((((((((((((@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@(((((((((((((((((((@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@(((((((((((((((*@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ __           __
 * .--.--.--.-----.----.|  |--.-----.|  |.--.--.-----.
 * |  |  |  |  _  |   _||    <|  _  ||  ||  |  |__ --|
 * |________|_____|__|  |__|__|   __||__||_____|_____|
 * |__|
 */


import android.os.Parcel;
import android.os.Parcelable;

import com.foreveross.atwork.api.sdk.model.BasicResponseJSON;
import com.foreveross.atwork.infrastructure.model.app.App;
import com.foreveross.atwork.infrastructure.model.app.Shortcut;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by reyzhang22 on 16/4/13.
 */
public class AppListResponseJson extends BasicResponseJSON {

    @SerializedName("result")
    public Result result;


    public static class Result implements Parcelable {

        @SerializedName("access_list")
        public List<App> mAccessList;

        @SerializedName("admin_list")
        public List<App> mAdminList;

        @SerializedName("shortcut_list")
        public List<Shortcut> mShortcutList;

        public Result() {
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeTypedList(this.mAccessList);
            dest.writeTypedList(this.mAdminList);
            dest.writeList(this.mShortcutList);
        }

        protected Result(Parcel in) {
            this.mAccessList = in.createTypedArrayList(App.CREATOR);
            this.mAdminList = in.createTypedArrayList(App.CREATOR);
            this.mShortcutList = new ArrayList<Shortcut>();
            in.readList(this.mShortcutList, Shortcut.class.getClassLoader());
        }

        public static final Creator<Result> CREATOR = new Creator<Result>() {
            @Override
            public Result createFromParcel(Parcel source) {
                return new Result(source);
            }

            @Override
            public Result[] newArray(int size) {
                return new Result[size];
            }
        };
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(this.result, flags);
    }

    public AppListResponseJson() {
    }

    protected AppListResponseJson(Parcel in) {
        super(in);
        this.result = in.readParcelable(Result.class.getClassLoader());
    }

    public static final Creator<AppListResponseJson> CREATOR = new Creator<AppListResponseJson>() {
        @Override
        public AppListResponseJson createFromParcel(Parcel source) {
            return new AppListResponseJson(source);
        }

        @Override
        public AppListResponseJson[] newArray(int size) {
            return new AppListResponseJson[size];
        }
    };
}
