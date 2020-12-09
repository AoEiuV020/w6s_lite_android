package com.foreveross.atwork.infrastructure.utils;

/**
 * Created by dasunsy on 16/8/8.
 */
public class DoubleUtil {

    /**
     * double to long
     *
     * @param doubleValue
     * return long result
     * */
    public static long toLong(double doubleValue) {
        return Double.valueOf(doubleValue).longValue();
    }
}
