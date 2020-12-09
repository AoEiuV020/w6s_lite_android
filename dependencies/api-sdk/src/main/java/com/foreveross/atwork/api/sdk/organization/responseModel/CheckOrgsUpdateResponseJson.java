package com.foreveross.atwork.api.sdk.organization.responseModel;/**
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
import android.os.Parcelable;

import com.foreveross.atwork.api.sdk.model.BasicResponseJSON;
import com.foreveross.atwork.infrastructure.model.orgization.Organization;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by reyzhang22 on 16/5/11.
 */
public class CheckOrgsUpdateResponseJson extends BasicResponseJSON {

    @SerializedName("result")
    public Result result;

    public static class Result implements Parcelable {
        @SerializedName("domain_id")
        public String domainId;

        @SerializedName("refresh")
        public List<Organization> refresh;

        @SerializedName("remove")
        public List<String> remove;

        @SerializedName("admins")
        public List<String> admins = new ArrayList<>();

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.domainId);
            dest.writeTypedList(this.refresh);
            dest.writeStringList(this.remove);
            dest.writeStringList(this.admins);
        }

        public Result() {
        }

        protected Result(Parcel in) {
            this.domainId = in.readString();
            this.refresh = in.createTypedArrayList(Organization.CREATOR);
            this.remove = in.createStringArrayList();
            this.admins = in.createStringArrayList();
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






}
