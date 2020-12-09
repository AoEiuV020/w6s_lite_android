package com.foreveross.atwork.infrastructure.model.voip;

/**
 * Created by quanshi on 12/9/15.
 */
public enum MessageType {
    MessageType_None(0),
    MessageType_InAnotherCall(1),
    MessageType_Accept(2),
    MessageType_Rejected(3),
    MessageType_Cancelled(4);
    private int value;

    MessageType(int value) {
        this.value = value;
    }


    public static MessageType valueOf(int value) {
        switch (value) {
            case 0:
                return MessageType_None;
            case 1:
                return MessageType_InAnotherCall;
            case 2:
                return MessageType_Accept;
            case 3:
                return MessageType_Rejected;
            default:
                return null;
        }
    }

    public int value() {
        return this.value;
    }

}
