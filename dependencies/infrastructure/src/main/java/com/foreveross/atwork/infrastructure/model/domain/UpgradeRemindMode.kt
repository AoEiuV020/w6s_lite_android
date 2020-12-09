package com.foreveross.atwork.infrastructure.model.domain

enum class UpgradeRemindMode {

    NEVER,

    ONCE,

    //default
    REPEATED;


    companion object {
        fun parse(upgradeRemindMode: String): UpgradeRemindMode = when(upgradeRemindMode.toUpperCase()) {

            "NEVER" -> NEVER
            "ONCE" -> ONCE
            "REPEATED" -> REPEATED
            else -> {
                REPEATED
            }
        }
    }
}