package com.foreveross.atwork.infrastructure.model.domain;

/**
 * 相关使用设置(是否需要修改密码, 是否需要设置手势密码)（不提醒，提醒，强制）
 */
public enum CommonUsingSetting {

    DISABLED,

    ENABLED,

    FORCE;


    public static CommonUsingSetting valueOfStr(String value) {
        if("FORCE".equalsIgnoreCase(value)) {
            return FORCE;

        } else if("ENABLED".equalsIgnoreCase(value)) {
            return ENABLED;

        } else {
            return DISABLED;
        }
    }
}
