package com.foreveross.atwork.infrastructure.model.biometricAuthentication

import com.foreveross.atwork.infrastructure.utils.EnumLookupUtil

enum class BiometricAuthenticationProtectItemType {

    IM {
        override fun intValue(): Int = 0b1
    },

    CONTACT {
        override fun intValue(): Int = 0b10
    },

    WORKBENCH {
        override fun intValue(): Int = 0b100
    },

    APP {
        override fun intValue(): Int = 0b1000
    },

    ME {
        override fun intValue(): Int = 0b10000
    },

    WALLET {
        override fun intValue(): Int = 0b100000
    },

    CIRCLE {
        override fun intValue(): Int = 0b1000000
    },

    EMAIL {
        override fun intValue(): Int = 0b10000000
    },

    FAVORITE {
        override fun intValue(): Int = 0b100000000
    },

    DROPBOX {
        override fun intValue(): Int = 0b1000000000
    },

    FORCE {
        override fun intValue(): Int = 0b10000000000

    };

    abstract fun intValue(): Int


    fun transferToActivityTag(): String = toString().toUpperCase()


    companion object {

        fun getAll(): Int = 0b1111111111

        fun transferToBioAuthProtectType(tag: String): BiometricAuthenticationProtectItemType? {

            return EnumLookupUtil.lookup(BiometricAuthenticationProtectItemType::class.java, tag.toUpperCase())


        }


    }

}