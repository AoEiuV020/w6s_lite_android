package com.foreveross.atwork.infrastructure.utils;


import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ListUtil {

    public static <V> List<V> makeSingleList(V v) {
        List<V> singleList = new ArrayList<>();
        singleList.add(v);
        return singleList;
    }

    /**
     * 获取list大小
     * @param sourceList
     * @return sourceList 返回list的大小, 若是null或者空的话返回 0
     * */
    public static <V> int getSize(Collection<V> sourceList) {
        return null == sourceList ? 0 : sourceList.size();
    }

    /**
     * 判断list是否为空
     * @param sourceList
     * @return 是否为空
     * */
    public static <V> boolean isEmpty(Collection<V> sourceList) {
        return (null == sourceList|| 0 == sourceList.size());
    }

    /**
     * 对比两个 list 是否相同
     * @param sourceList
     * @param otherList
     * @return 是否
     * */
    public static <V> boolean checkUpdate(@Nullable Collection<V> sourceList, @Nullable Collection<V> otherList) {
        if(null == sourceList) {
            sourceList = new ArrayList<>();
        }

        if(null == otherList) {
            otherList = new ArrayList<>();
        }

        return (sourceList.size() != otherList.size() ||  !sourceList.containsAll(otherList));
    }


    public static <V> List<V> subListSafely(List<V> sourceList, int fromIndex, int toIndex) {
        if(toIndex > sourceList.size()) {
            toIndex = sourceList.size();
        }

        try {
            return sourceList.subList(fromIndex, toIndex);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sourceList;
    }
}
