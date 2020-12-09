package com.foreveross.atwork.infrastructure.model.voip;

/**
 * 会议类型
 */
public enum PhoneState {
    PhoneState_Idle(0),    //电话空闲
    PhoneState_Disconnect(1),  //电话断开
    PhoneState_Connecting(2),  //正在连接电话
    PhoneState_Connected(3);  //电话正常通话

    private int value;

    PhoneState(int value) {
        this.value = value;
    }


    public static PhoneState valueOf(int value) {
        switch (value) {
            case 0:
                return PhoneState_Idle;
            case 1:
                return PhoneState_Disconnect;
            case 2:
                return PhoneState_Connecting;
            case 3:
                return PhoneState_Connected;
            default:
                return PhoneState_Idle;
        }
    }

    public int value() {
        return this.value;
    }
}
