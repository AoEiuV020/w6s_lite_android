package com.foreveross.atwork.infrastructure.newmessage;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lingen on 15/4/16.
 * Description:
 */
public class ConnectAckTypeMessage extends HasTimestampResponse {

    @Override
    public int getMsgType() {
        return Message.CONNECT_ACK_TYPE;
    }




}
