package com.foreveross.atwork.api.sdk.discussion.requestJson;

import android.os.Parcel;
import android.os.Parcelable;

import com.foreveross.atwork.api.sdk.configSetting.conversationSetting.model.ConversionConfigSettingParticipant;
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

public class DiscussionSettingsRequest implements Parcelable {

    @SerializedName("show_nickname")
    public Boolean mShowNickname = null;

    @SerializedName("weixin_sync_enabled")
    public Boolean mWeChatSyncEnable = null;

    @SerializedName("conversation_settings")
    public ConversationSettings mConversationSettings = null;

    @SerializedName("show_watermark")
    public Boolean mWatermarkEnable = null;

    @SerializedName("owner_control")
    public Boolean mOwnerControl = null;


    public static class ConversationSettings implements Parcelable {

        @SerializedName("notify_enabled")
        public Boolean mNotifyEnabled = null;


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

        public static final Parcelable.Creator<ConversationSettings> CREATOR = new Parcelable.Creator<ConversationSettings>() {
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

    public DiscussionSettingsRequest() {
    }

    protected DiscussionSettingsRequest(Parcel in) {
        this.mShowNickname = in.readByte() != 0;
        this.mWeChatSyncEnable = in.readByte() != 0;
        this.mConversationSettings = in.readParcelable(ConversationSettings.class.getClassLoader());
        this.mWatermarkEnable = in.readByte() != 0;
        this.mOwnerControl = in.readByte() != 0;
    }

    public static final Parcelable.Creator<DiscussionSettingsRequest> CREATOR = new Parcelable.Creator<DiscussionSettingsRequest>() {
        @Override
        public DiscussionSettingsRequest createFromParcel(Parcel source) {
            return new DiscussionSettingsRequest(source);
        }

        @Override
        public DiscussionSettingsRequest[] newArray(int size) {
            return new DiscussionSettingsRequest[size];
        }
    };
}
