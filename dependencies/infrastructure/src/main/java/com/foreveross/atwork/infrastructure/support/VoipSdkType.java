package com.foreveross.atwork.infrastructure.support;

import com.foreveross.atwork.infrastructure.newmessage.post.voip.GateWay;

/**
 * Created by dasunsy on 16/9/20.
 */
public enum VoipSdkType {
    QSY {
        @Override
        public int intValue() {
            return 0;
        }
    }

    , AGORA {
        @Override
        public int intValue() {
            return 1;
        }
    }
    , BIZCONF {
        @Override
        public int intValue() {
            return 2;
        }

    }, ZOOM {
        @Override
        public int intValue() {
            return 3;
        }
    }
    ,


    UNKNOWN {
        @Override
        public int intValue() {
            return -1;
        }
    };

    public abstract int intValue();


    public static VoipSdkType parse(String value) {
        if(GateWay.GATE_WAY_AGORA.equalsIgnoreCase(value)) {
            return AGORA;
        }

        if(GateWay.GATE_WAY_QUANSHIYUN.equalsIgnoreCase(value) || "quanshi".equalsIgnoreCase(value)) {
            return QSY;
        }

        if(GateWay.GATE_WAY_BIZCONF.equalsIgnoreCase(value)) {
            return BIZCONF;
        }

        if(GateWay.GATE_WAY_ZOOM.equalsIgnoreCase(value)) {
            return ZOOM;
        }

        return UNKNOWN;


    }
}
