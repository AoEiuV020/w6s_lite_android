package com.foreveross.atwork.infrastructure.model.clickStatistics

enum class Type {

    NEWS_SUMMARY {
        override fun initValue(): Int {
            return 2
        }
    },
    APP {
        override fun initValue(): Int {
            return 1
        }
    },

    UNKNOWN {
        override fun initValue(): Int {
           return -1
        }

    };


    abstract fun initValue(): Int


    companion object {
        fun valueOf(type: Int): Type {
            return when(type) {
                1 -> APP
                2 -> NEWS_SUMMARY
                else -> UNKNOWN
            }
        }
    }
}