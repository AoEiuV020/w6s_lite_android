package com.foreveross.atwork.infrastructure.shared.dev;/**
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
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.encryption.Base64Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class BeeWorksTestApiData {

    private static final String TAG = BeeWorksTestApiData.class.getSimpleName();

    private static BeeWorksTestApiData sInstance = new BeeWorksTestApiData();

    private static final int VERSION = 19;

    private static final String SP_BEEWORKS_TEST_API_FILE = "SP_BEEWORKS_TEST_API_FILE";

    private static final String DATA_CURRENT_TEST_API_DATA_KEY = "DATA_CURRENT_TEST_API_DATA_KEY";

    public static BeeWorksTestApiData getInstance() {
        synchronized (TAG) {
            if (sInstance == null) {
                sInstance = new BeeWorksTestApiData();
            }
            return sInstance;
        }
    }

    /**
     * ????????????????????????
     * @param context
     * @param key
     * @param data
     */
    public void setBeeWorksTestApiData(Context context, String key,  String data) {
        key = Base64Util.encode(key.getBytes());
        PreferencesUtils.putString(context, getSpFile(), key, data);
    }



    /**
     * ????????????????????????
     * @param context
     * @param key
     * @return
     */
    public String getBeeWorksTestApiData(Context context, String key) {
        key = Base64Util.encode(key.getBytes());
        return PreferencesUtils.getString(context, getSpFile(), key, "");
    }


    public String getCurrentBeeworksTestApiData(Context context) {
        String key = getCurrentTestApiDataKeyShow(context);
        return getBeeWorksTestApiData(context, key);
    }


    /**
     * ??????????????????????????? key
     * @param context
     * @param currentKey
     * */
    public void setCurrentTestApiDataKey(Context context, String currentKey) {
        currentKey = Base64Util.encode(currentKey.getBytes());
        PreferencesUtils.putString(context, getSpFile(), DATA_CURRENT_TEST_API_DATA_KEY, currentKey);
    }


    /**
     * ??????????????????????????? key, ????????????, ????????? base64???????????????
     * @param context
     * */
    public String getCurrentTestApiDataKeyShow(Context context) {
        String stringFromSp = getCurrentTestApiDataKey(context);
        return new String(Base64Util.decode(stringFromSp));
    }

    public String getCurrentTestApiDataKey(Context context) {
        return PreferencesUtils.getString(context, getSpFile(), DATA_CURRENT_TEST_API_DATA_KEY, StringUtils.EMPTY);
    }


    public List<String> getTestApiKeyList(Context context) {
        Map<String, ?> keyValueMap = PreferencesUtils.getAll(context, getSpFile());
        keyValueMap.remove(DATA_CURRENT_TEST_API_DATA_KEY);
        Set<String> keyEncryptedSet = keyValueMap.keySet();
        List<String> keyDecryptedList = new ArrayList<>();

        for(String keyEncrypted: keyEncryptedSet) {
            keyDecryptedList.add(new String(Base64Util.decode(keyEncrypted)));
        }

        return keyDecryptedList;
    }


    public void removeBeeWorksTestApiData(Context context, String key) {
        key = Base64Util.encode(key.getBytes());
        PreferencesUtils.remove(context, getSpFile(), key);
    }

    /**
     * ????????????????????????
     * @param context
     */
    public void clearBeeWorksTestApiData(Context context) {
        PreferencesUtils.clear(context, getSpFile());
    }


    private String getSpFile() {
        return SP_BEEWORKS_TEST_API_FILE + "_v" + VERSION;
    }

}
