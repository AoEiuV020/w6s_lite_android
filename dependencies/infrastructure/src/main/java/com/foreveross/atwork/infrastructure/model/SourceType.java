package com.foreveross.atwork.infrastructure.model;

import java.io.Serializable;

/**
 * Created by dasunsy on 2017/9/6.
 */

public enum SourceType implements Serializable{

    DISCUSSION,

    USER,

    UNKNOWN;


    public static SourceType createFrom(SessionType type) {
        if(SessionType.User == type) {
            return USER;
        }


        if(SessionType.Discussion == type) {
            return DISCUSSION;
        }

        return UNKNOWN;
    }
}
