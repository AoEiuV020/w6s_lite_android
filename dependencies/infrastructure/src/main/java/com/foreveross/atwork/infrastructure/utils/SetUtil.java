package com.foreveross.atwork.infrastructure.utils;

import java.util.Set;

public class SetUtil {
    /**
     * 判断set是否为空
     * @param sourceSet
     * @return 是否为空
     * */
    public static <V> boolean isEmpty(Set<V> sourceSet) {
        return (null == sourceSet|| 0 == sourceSet.size());
    }

}
