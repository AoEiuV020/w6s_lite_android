package com.foreveross.atwork.infrastructure.shared;/**
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@%((((((((#&@@@((#((#@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@%(((((((((((((%@*((((@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@%(((/,/(((((((((@@@*#@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@#(((@@@((((((((((@@((@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@(((((((((((((((((((((@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@(((((((((((((((((((@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@(((((((((((((((*@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 *                       __           __
 * .--.--.--.-----.----.|  |--.-----.|  |.--.--.-----.
 * |  |  |  |  _  |   _||    <|  _  ||  ||  |  |__ --|
 * |________|_____|__|  |__|__|   __||__||_____|_____|
 *                            |__|
 */


import android.content.Context;

import com.foreveross.atwork.infrastructure.utils.PreferencesUtils;

/**
 * BeeWorks发布数据
 * Created by reyzhang22 on 16/4/7.
 */
public class BeeWorksPublicData {

    private static final String TAG = BeeWorksPublicData.class.getSimpleName();

    private static BeeWorksPublicData sInstance = new BeeWorksPublicData();

    private static final String SP_BEEWORKS_PUBLIC_FILE = "SP_BEEWORKS_PUBLIC_FILE";

    private static final String BEEWORKS_PUBLIC_DATA = "BEEWORKS_PUBLIC_DATA";

    public static BeeWorksPublicData getInstance() {
        synchronized (TAG) {
            if (sInstance == null) {
                sInstance = new BeeWorksPublicData();
            }
            return sInstance;
        }
    }

    /**
     * 保存发布版本数据
     * @param context
     * @param data
     */
    public void setBeeWorksPublicData(Context context, String data) {
        PreferencesUtils.putString(context, SP_BEEWORKS_PUBLIC_FILE, BEEWORKS_PUBLIC_DATA, data);
    }

    /**
     * 获取发布版本数据
     * @param context
     * @return
     */
    public String getBeeWorksPublicData(Context context) {
        return PreferencesUtils.getString(context, SP_BEEWORKS_PUBLIC_FILE, BEEWORKS_PUBLIC_DATA, "");
    }

    /**
     * 清除发布版本数据
     * @param context
     */
    public void clearBeeWorksPublicData(Context context) {
        PreferencesUtils.clear(context, SP_BEEWORKS_PUBLIC_FILE);
    }

}
