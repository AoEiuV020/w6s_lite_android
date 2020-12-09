package com.foreveross.atwork.infrastructure.newmessage;


public enum ChatStatus {

    Sended {
        @Override
        public int intValue() {
            return 0;
        }
    },


    Not_Send {
        @Override
        public int intValue() {
            return 1;
        }
    },


    Sending {
        @Override
        public int intValue() {
            return 2;
        }
    },

    UnDo {
        @Override
        public int intValue() {
            return 3;
        }
    },

    Hide {
        @Override
        public int intValue() {
            return 4;
        }
    },


    At_All {
        @Override
        public int intValue() {
            return 5;
        }
    },

    Self_Send {
        @Override
        public int intValue() {
            return 6;
        }
    },

    Peer_Read {
        @Override
        public int intValue() {
            return 7;
        }
    };



    public static ChatStatus fromIntValue(int value) {
        if (value == 0) {
            return Sended;
        } else if (value == 1) {
            return Not_Send;
        } else if (value == 2) {
            return Sending;
        } else if (value ==3) {
            return UnDo;
        } else if (value ==4) {
            return Hide;
        } else if( value == 5) {
            return At_All;
        } else if( value == 6) {
            return Self_Send;
        } else if(value == 7) {
            return Peer_Read;
        }

        return null;
    }

    public abstract int intValue();
}
