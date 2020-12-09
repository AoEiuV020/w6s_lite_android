package com.w6s

enum class FavoriteType {
    IMAGE {
        override fun StringValueOf(): String {
            return "IMAGE"
        }

        override fun sqlString(): String {
            return " 'IMAGE' or type_ = 'VIDEO' or type_ ='ANNO_IMAGE'"
        }
    },
    VIDEO {
        override fun StringValueOf(): String {
            return "VIDEO"
        }

        override fun sqlString(): String {
            return "'VIDEO' or type_ = 'IMAGE'"
        }

    },
    LINK {
        override fun StringValueOf(): String {
            return "SHARE"
        }

        override fun sqlString(): String  {
            return "'SHARE'"
        }
    },
    FILE {
        override fun StringValueOf(): String {
            return "FILE"
        }

        override fun sqlString(): String  {
            return " 'FILE' or type_ = 'ANNO_FILE'"

        }
    },

    ANNO_FILE {
        override fun StringValueOf(): String {
            return "ANNO_FILE"
        }

        override fun sqlString(): String  {
            return " 'ANNO_FILE' or type_ = 'FILE'"

        }
    },

    CHAT_RECODE {
        override fun StringValueOf(): String {
            return "MULTIPART"
        }

        override fun sqlString(): String  {
            return "'MULTIPART'"
        }
    },
    VOICE {
        override fun StringValueOf(): String {
            return "VOICE"
        }

        override fun sqlString(): String  {
            return "'VOICE'"
        }
    },
    TEXT {
        override fun StringValueOf(): String {
            return "TEXT"
        }

        override fun sqlString(): String  {
            return "'TEXT'"
        }
    },
    QUOTED {
        override fun StringValueOf(): String {
            return "QUOTED"
        }

        override fun sqlString(): String {
            return "'QUOTED'"
        }



    },


    OTHER {
        override fun StringValueOf(): String {
            return "QUOTED"
        }

        override fun sqlString(): String {
            return "'QUOTED' or type_ = 'SHARE'"
        }


    };

    abstract fun sqlString(): String
    abstract fun StringValueOf(): String
}