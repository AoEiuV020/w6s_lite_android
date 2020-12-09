package com.foreveross.atwork.infrastructure.model.voip;

import com.google.gson.annotations.SerializedName;

public class VoipMeetingMemberSettingInfo {

    public static final String VOICE_MUTED = "muted";
    public static final String VOICE_NOT_MUTED = "not_muted";



    @SerializedName("mute_voice")
    public String muteVoice;
}
