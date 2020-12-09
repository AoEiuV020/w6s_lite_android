package com.foreveross.atwork.infrastructure.utils;

import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;

/**
 * Created by dasunsy on 2017/7/21.
 */

public class InterceptHelper {

    public static boolean sIsLocking  = false;

    public static boolean isTimeToLock() {
        return AtworkConfig.LOCK_DURATION < System.currentTimeMillis() - LoginUserInfo.getInstance().mLastCodeLockTime;
    }

}
