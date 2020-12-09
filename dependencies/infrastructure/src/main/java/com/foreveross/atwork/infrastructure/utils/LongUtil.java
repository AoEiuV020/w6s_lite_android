package com.foreveross.atwork.infrastructure.utils;

/**
 * Created by dasunsy on 2017/5/19.
 */

public class LongUtil {

    public static long parseLong(String longStr) {
        try {
            return Long.parseLong(longStr);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }
}
