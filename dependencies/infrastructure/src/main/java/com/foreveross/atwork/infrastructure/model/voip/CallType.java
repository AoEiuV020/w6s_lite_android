package com.foreveross.atwork.infrastructure.model.voip;

/**
 * 会议类型
 */
public enum CallType {
    CallType_None(0), CallType_Audio(1), CallType_Video(2), CallType_DesktopShare(4);

    private int value;

    CallType(int value) {
        this.value = value;
    }


    public static CallType valueOf(int value) {
        switch (value) {
            case 0:
                return CallType_None;
            case 1:
                return CallType_Audio;
            case 2:
                return CallType_Video;
            case 4:
                return CallType_DesktopShare;
            default:
                return CallType_None;
        }
    }

    public int value() {
        return this.value;
    }
}
