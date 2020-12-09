package com.foreveross.atwork.infrastructure.model.workbench

enum class WorkbenchGridType {
    TYPE_1_4 {

        override fun toString(): String = "1_4"
    },

    TYPE_2_4 {
        override fun toString(): String = "2_4"
    },

    TYPE_N_4 {
        override fun toString(): String = "n_4"
    };

    abstract override fun toString(): String

    companion object {

        fun parse(type: String): WorkbenchGridType = when (type) {
            "2_4" -> TYPE_2_4
            "1_4" -> TYPE_1_4
            "n_4" -> TYPE_N_4
            else -> TYPE_N_4
        }



    }
}