package com.foreveross.atwork.infrastructure.model;

import androidx.annotation.Nullable;

import com.foreveross.atwork.infrastructure.newmessage.messageEnum.ParticipantType;

import java.io.Serializable;

/**
 * Created by lingen on 15/4/3.
 * Description:
 */
public enum SessionType implements Serializable {

    /**
     * 用户
     */
    User {
        @Override
        public int initValue() {
            return 1;
        }

        @Override
        public ParticipantType getToType() {
            return ParticipantType.User;
        }

        @Override
        public ParticipantType getFromType() {
            return ParticipantType.User;
        }

        @Override
        public int getQueryHistoryMessageType() {
            return 1;
        }

        @Override
        public boolean isApp() {
            return false;
        }
    },

    /**
     * 群组
     */
    Discussion {
        @Override
        public int initValue() {
            return 2;
        }

        @Override
        public ParticipantType getToType() {
            return ParticipantType.Discussion;
        }

        @Override
        public ParticipantType getFromType() {
            return ParticipantType.User;
        }

        @Override
        public int getQueryHistoryMessageType() {
            return 3;
        }

        @Override
        public boolean isApp() {
            return false;
        }
    },

    /**
     * 服务号
     */
    Service {
        @Override
        public int initValue() {
            return 32;
        }

        @Override
        public ParticipantType getToType() {
            return ParticipantType.App;
        }

        @Override
        public ParticipantType getFromType() {
            return ParticipantType.User;
        }

        @Override
        public int getQueryHistoryMessageType() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isApp() {
            return true;
        }
    },

    /**
     * 轻应用
     */
    LightApp {
        @Override
        public int initValue() {
            return 31;
        }

        @Override
        public ParticipantType getToType() {
            return ParticipantType.App;
        }

        @Override
        public ParticipantType getFromType() {
            return ParticipantType.User;
        }

        @Override
        public int getQueryHistoryMessageType() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isApp() {
            return true;
        }
    },

    /**
     * 原生应用
     */
    NativeApp {
        @Override
        public int initValue() {
            return 33;
        }

        @Override
        public ParticipantType getToType() {
            throw new UnsupportedOperationException();
        }

        @Override
        public ParticipantType getFromType() {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getQueryHistoryMessageType() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isApp() {
            return true;
        }
    },

    /**
     * 本地应用
     */
    Local {
        @Override
        public int initValue() {
            return 34;
        }

        @Override
        public ParticipantType getToType() {
            throw new UnsupportedOperationException();
        }

        @Override
        public ParticipantType getFromType() {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getQueryHistoryMessageType() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isApp() {
            return true;
        }
    },

    SystemApp {
        @Override
        public int initValue() {
            return 35;
        }

        @Override
        public ParticipantType getToType() {
            throw new UnsupportedOperationException();
        }

        @Override
        public ParticipantType getFromType() {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getQueryHistoryMessageType() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isApp() {
            return true;
        }
    },

    Notice {
        @Override
        public int initValue() {
            return 36;
        }

        @Override
        public ParticipantType getToType() {
            return ParticipantType.System;
        }

        @Override
        public ParticipantType getFromType() {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getQueryHistoryMessageType() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isApp() {
            return false;
        }
    },

    Custom {
        @Override
        public int initValue() {
            return 37;
        }

        @Override
        public ParticipantType getToType() {
            return ParticipantType.System;
        }

        @Override
        public ParticipantType getFromType() {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getQueryHistoryMessageType() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isApp() {
            return false;
        }
    };

    @Nullable
    public static SessionType valueOf(int value) {
        if (value == 1) {
            return User;
        }
        if (value == 2) {
            return Discussion;
        }
        if (value == 31) {
            return LightApp;
        }
        if (value == 32) {
            return Service;
        }
        if (value == 33) {
            return NativeApp;
        }
        if (value == 34) {
            return Local;
        }
        if (value == 35) {
            return SystemApp;
        }
        if(value == 36) {
            return Notice;
        }

        if(value == 37) {
            return Custom;
        }

        return null;
    }

    @Nullable
    public static SessionType toType(String sessionType) {
        if ("user".equalsIgnoreCase(sessionType)) {
            return User;
        }
        if ("discussion".equalsIgnoreCase(sessionType)) {
            return Discussion;
        }
        if ("lightApp".equalsIgnoreCase(sessionType)) {
            return LightApp;
        }
        if ("service".equalsIgnoreCase(sessionType)) {
            return Service;
        }
        if ("nativeApp".equalsIgnoreCase(sessionType)) {
            return NativeApp;
        }
        if ("local".equalsIgnoreCase(sessionType)) {
            return Local;
        }
        if ("systemApp".equalsIgnoreCase(sessionType)) {
            return SystemApp;
        }
        if("notice".equalsIgnoreCase(sessionType)) {
            return Notice;
        }

        if("custom".equalsIgnoreCase(sessionType)) {
            return Custom;
        }

        return null;
    }

    public abstract int initValue();

    public abstract ParticipantType getToType();

    public abstract ParticipantType getFromType();

    public abstract int getQueryHistoryMessageType();

    public abstract boolean isApp();

}
