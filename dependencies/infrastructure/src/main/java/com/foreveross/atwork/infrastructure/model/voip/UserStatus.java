package com.foreveross.atwork.infrastructure.model.voip;

/**
 * 呼状态
 */
public enum UserStatus {
    UserStatus_NotJoined(0),
    UserStatus_Joined(1),
    UserStatus_Rejected(2),
    UserStatus_Left(3);

    private int value;

    UserStatus(int value) {
        this.value = value;
    }

    public static UserStatus valueOf(int value) {
        switch (value) {
            case 0:
                return UserStatus_NotJoined;
            case 1:
                return UserStatus_Joined;
            case 2:
                return UserStatus_Rejected;
            case 3:
                return UserStatus_Left;
             default:
                return UserStatus_NotJoined;
        }
    }

    public int value() {
        return this.value;
    }

}
