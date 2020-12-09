package com.foreveross.atwork.infrastructure.utils;


public class ArrayUtil {
    /**
     *判断array是否为空
     * @param sourceArray
     * @return 是否为空
     * */
    public static <V> boolean isEmpty(V[] sourceArray) {
        return (null == sourceArray|| 0 == sourceArray.length);
    }

    public static  boolean isEmpty(byte[] sourceArray) {
        return (null == sourceArray|| 0 == sourceArray.length);
    }

}
