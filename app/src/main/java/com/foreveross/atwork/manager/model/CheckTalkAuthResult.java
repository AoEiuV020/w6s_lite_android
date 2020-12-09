package com.foreveross.atwork.manager.model;

/**
 * Created by dasunsy on 2017/8/8.
 */

public enum CheckTalkAuthResult {

    CAN_TALK {
        @Override
        public boolean isSureState() {
            return true;
        }
    },


    CANNOT_TALK {
        @Override
        public boolean isSureState() {
            return true;
        }
    },

    MAY_TALK {
        @Override
        public boolean isSureState() {
            return false;
        }
    },

    MAY_NOT_TALK {
        @Override
        public boolean isSureState() {
            return false;
        }
    },

    NETWORK_FAILED {
        @Override
        public boolean isSureState() {
            return false;
        }
    };

    public abstract boolean isSureState();

}
