package com.foreveross.atwork.infrastructure.model.wallet;

/**
 * Created by dasunsy on 2018/1/4.
 */

public enum  RedEnvelopeStatus {
    /**
     * 已过期
     * */
    EXPIRED,

    /**
     * 自己已经拆开过, 并且拿到了钱
     * */
    SELF_OPENED_GET_MONEY,


    /**
     * 自己已经拆开过, 但黑仔了
     * */
    SELF_OPENED_NO_MONEY,


    /**
     * 自己未拆开过
     * */
    NOT_OPENED,


    /**
     * 自己发送的单人红包
     * */
    SELF_USER
}
