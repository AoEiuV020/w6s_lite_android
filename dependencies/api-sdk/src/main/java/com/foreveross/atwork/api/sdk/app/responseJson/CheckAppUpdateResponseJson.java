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
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 *                       __           __
 * .--.--.--.-----.----.|  |--.-----.|  |.--.--.-----.
 * |  |  |  |  _  |   _||    <|  _  ||  ||  |  |__ --|
 * |________|_____|__|  |__|__|   __||__||_____|_____|
 *                            |__|
 */


import android.os.Parcel;

import com.foreveross.atwork.api.sdk.model.BasicResponseJSON;
import com.foreveross.atwork.infrastructure.model.app.App;
import com.foreveross.atwork.infrastructure.model.app.Shortcut;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CheckAppUpdateResponseJson extends BasicResponseJSON {

    @SerializedName("result")
    public Result result;

    public static class Result implements android.os.Parcelable {
        @SerializedName("deleted_access_list")
        public List<String> mDeleteAccessList;

        @SerializedName("deleted_admin_list")
        public List<String> mDeleteAdminList;

        @SerializedName("new_access_list")
        public List<App> mNewAccessList;

        @SerializedName("new_admin_list")
        public List<App> mNewAdminList;

        @SerializedName("updated_access_list")
        public List<App> mUpdatedAccessList;

        @SerializedName("updated_admin_list")
        public List<App> mUpdatedAdminList;

        @SerializedName("shortcut_list")
        public List<Shortcut> mShortcutList;


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeStringList(this.mDeleteAccessList);
            dest.writeStringList(this.mDeleteAdminList);
            dest.writeTypedList(this.mNewAccessList);
            dest.writeTypedList(this.mNewAdminList);
            dest.writeTypedList(this.mUpdatedAccessList);
            dest.writeTypedList(this.mUpdatedAdminList);
        }

        public Result() {
        }

        protected Result(Parcel in) {
            this.mDeleteAccessList = in.createStringArrayList();
            this.mDeleteAdminList = in.createStringArrayList();
            this.mNewAccessList = in.createTypedArrayList(App.CREATOR);
            this.mNewAdminList = in.createTypedArrayList(App.CREATOR);
            this.mUpdatedAccessList = in.createTypedArrayList(App.CREATOR);
            this.mUpdatedAdminList = in.createTypedArrayList(App.CREATOR);
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

    public CheckAppUpdateResponseJson() {
    }

    protected CheckAppUpdateResponseJson(Parcel in) {
        super(in);
        this.result = in.readParcelable(Result.class.getClassLoader());
    }

    public static final Creator<CheckAppUpdateResponseJson> CREATOR = new Creator<CheckAppUpdateResponseJson>() {
        @Override
        public CheckAppUpdateResponseJson createFromParcel(Parcel source) {
            return new CheckAppUpdateResponseJson(source);
        }

        @Override
        public CheckAppUpdateResponseJson[] newArray(int size) {
            return new CheckAppUpdateResponseJson[size];
        }
    };
}
