package com.foreveross.atwork.infrastructure.utils;

import java.util.HashMap;

/**
 * Created by dasunsy on 2018/3/6.
 */

public class CommonUtil {

    private static HashMap<String, Long> sTagLastClickTime = new HashMap<>();


    public static boolean isFastClick(int fastTime) {
        return isFastClick("common", fastTime);
    }


    public static boolean isFastClick(String tag, long fastGap) {
        long time = System.currentTimeMillis();

        if(sTagLastClickTime.containsKey(tag)) {
            long lastTime = sTagLastClickTime.get(tag);
            long clickGap = time - lastTime;


            if(fastGap > clickGap) {

                return true;
            }
        }

        sTagLastClickTime.clear();
        sTagLastClickTime.put(tag, time);

        return false;
    }

    public static boolean isDoubleClick(String tag) {
        return isFastClick(tag,500);
    }
}
