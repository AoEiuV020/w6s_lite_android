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
 * Created by reyzhang22 on 16/4/7.
 */
public class BeeWorksPreviewData {

    private static final String TAG = BeeWorksPreviewData.class.getSimpleName();

    private static BeeWorksPreviewData sInstance = new BeeWorksPreviewData();

    private static final String SP_BEEWORKS_PREVIEW_FILE = "SP_BEEWORKS_PREVIEW_FILE";

    private static final String BEEWORKS_PREVIEW_DATA = "BEEWORKS_PREVIEW_DATA";

    public static BeeWorksPreviewData getInstance() {
        synchronized (TAG) {
            if (sInstance == null) {
                sInstance = new BeeWorksPreviewData();
            }
            return sInstance;
        }
    }

    /**
     * 保存预览版本数据
     * @param context
     * @param data
     */
    public void setBeeWorksPreviewData(Context context, String data) {
        PreferencesUtils.putString(context, SP_BEEWORKS_PREVIEW_FILE, BEEWORKS_PREVIEW_DATA, data);
    }

    /**
     * 获取预览版本数据
     * @param context
     * @return
     */
    public String getBeeWorksPreviewData(Context context) {
        return PreferencesUtils.getString(context, SP_BEEWORKS_PREVIEW_FILE, BEEWORKS_PREVIEW_DATA, "");
    }

    /**
     * 清除预览版本数据
     * @param context
     */
    public void clearBeeWorksPreviewData(Context context) {
        PreferencesUtils.clear(context, SP_BEEWORKS_PREVIEW_FILE);
    }
}
