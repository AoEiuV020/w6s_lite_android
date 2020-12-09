package com.foreveross.atwork.modules.voip.support.qsy.utils;

import com.tang.gnettangsdk.IGNetTangUser;

/**
 * Created by dasunsy on 16/8/4.
 */
public class IGnetTangUserHelper {

    public static String getUserId(IGNetTangUser igNetTangUser) {
        return igNetTangUser.getUserName();
    }
}
