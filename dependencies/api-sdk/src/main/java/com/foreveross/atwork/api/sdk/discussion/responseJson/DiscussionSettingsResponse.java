package com.foreveross.atwork.api.sdk.discussion.responseJson;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
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
 * Created by reyzhang22 on 2016/10/27.
 */

public class DiscussionSettingsResponse implements Parcelable {

    @SerializedName("show_nickname")
    public boolean mShowNickname;

    @SerializedName("weixin_sync_enabled")
    public boolean mWeChatSyncEnable;

    @SerializedName("conversation_settings")
    public ConversationSettings mConversationSettings;

    @SerializedName("show_watermark")
    public boolean mWatermarkEnable;

    @SerializedName("owner_control")
    public boolean mOwnerControl;


    public static class ConversationSettings implements Parcelable {

        @SerializedName("notify_enabled")
        public boolean mNotifyEnabled;


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeByte(this.mNotifyEnabled ? (byte) 1 : (byte) 0);
        }

        public ConversationSettings() {
        }

        protected ConversationSettings(Parcel in) {
            this.mNotifyEnabled = in.readByte() != 0;
        }

        public static final Creator<ConversationSettings> CREATOR = new Creator<ConversationSettings>() {
            @Override
            public ConversationSettings createFromParcel(Parcel source) {
                return new ConversationSettings(source);
            }

            @Override
            public ConversationSettings[] newArray(int size) {
                return new ConversationSettings[size];
            }
        };
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.mShowNickname ? (byte) 1 : (byte) 0);
        dest.writeByte(this.mWeChatSyncEnable ? (byte) 1 : (byte) 0);
        dest.writeParcelable(this.mConversationSettings, flags);
        dest.writeByte(this.mWatermarkEnable ? (byte) 1 : (byte) 0);
        dest.writeByte(this.mOwnerControl ? (byte) 1 : (byte) 0);
    }

    public DiscussionSettingsResponse() {
    }

    protected DiscussionSettingsResponse(Parcel in) {
        this.mShowNickname = in.readByte() != 0;
        this.mWeChatSyncEnable = in.readByte() != 0;
        this.mConversationSettings = in.readParcelable(ConversationSettings.class.getClassLoader());
        this.mWatermarkEnable = in.readByte() != 0;
        this.mOwnerControl = in.readByte() != 0;
    }

    public static final Creator<DiscussionSettingsResponse> CREATOR = new Creator<DiscussionSettingsResponse>() {
        @Override
        public DiscussionSettingsResponse createFromParcel(Parcel source) {
            return new DiscussionSettingsResponse(source);
        }

        @Override
        public DiscussionSettingsResponse[] newArray(int size) {
            return new DiscussionSettingsResponse[size];
        }
    };
}
