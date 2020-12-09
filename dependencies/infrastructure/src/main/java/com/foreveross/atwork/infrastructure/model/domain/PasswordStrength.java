package com.foreveross.atwork.infrastructure.model.domain;

/**
 * Created by dasunsy on 2017/4/7.
 */

/**
 * 密码强度
 * */
public enum PasswordStrength {

    WEAK,

    MIDDLE,

    STRONG;

    public static PasswordStrength valueOfStr(String value) {
        if("WEAK".equalsIgnoreCase(value)) {
            return WEAK;

        } else if("STRONG".equalsIgnoreCase(value)) {
            return STRONG;

        } else {
            return MIDDLE;
        }
    }
}


