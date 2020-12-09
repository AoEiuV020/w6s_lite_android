package com.foreveross.atwork.infrastructure.utils;

import java.util.List;
import java.util.Map;

/**
 * Created by dasunsy on 16/9/9.
 */
public class MapUtil {

    public static <V> void update(Map<String, List<V>> map, String key, List<V> vList) {
        if(map.containsKey(key)) {
            List<V> inVList = map.get(key);
            inVList.addAll(vList);

        } else {
            map.put(key, vList);
        }
    }

    /**
     * 判断 map 是否为空
     * @param sourceList
     * @return 是否为空
     * */
    public static boolean isEmpty(Map sourceList) {
        return (null == sourceList|| 0 == sourceList.size());
    }
}
