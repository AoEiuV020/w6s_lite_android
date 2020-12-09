package com.foreveross.atwork.infrastructure.model.voip;

/**
 * 呼状态
 */
public enum CallState {
    CallState_Idle(0),
    CallState_Init(1),  //prepare the room
    CallState_StartCall(2),
    CallState_Waiting(3),  //wait for 1 vs 1 attendee
    CallState_Calling(4),  // in progress
    CallState_Disconnected(5),  // network is disconnect
    CallState_ReConnecting(6),  // is reconnecting, when reconnect succeeded, go back to CallState_Calling state
    CallState_Ending(7),
    CallState_Ended(8), // ended
    CallState_ReConnecting_Successfully(9);

    private int value;

    CallState(int value) {
        this.value = value;
    }

    public static CallState valueOf(int value) {
        switch (value) {
            case 0:
                return CallState_Idle;
            case 1:
                return CallState_Init;
            case 2:
                return CallState_StartCall;
            case 3:
                return CallState_Waiting;
            case 4:
                return CallState_Calling;
            case 5:
                return CallState_ReConnecting;
            case 6:
                return CallState_ReConnecting;
            case 7:
                return CallState_Ending;
            case 8:
                return CallState_Ended;
            case 9:
                return CallState_ReConnecting_Successfully;
            default:
                return CallState_Idle;
        }
    }

    public int value() {
        return this.value;
    }

}
