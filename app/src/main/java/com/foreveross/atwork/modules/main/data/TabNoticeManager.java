package com.foreveross.atwork.modules.main.data;


import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.ArraySet;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.foreveross.atwork.api.sdk.app.model.LightNoticeData;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.Session;
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.modules.common.lightapp.LightNoticeMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by lingen on 15/11/18.
 */
public class TabNoticeManager {

    private final static String APP_ID_FOUND_COLLEAGUE = "FOUND_COLLEAGUE"; //区分每个组织


    /**
     * "关于我"红点
     * */
    public final static String APP_ID_ABOUT_ME_CHECK_UPDATE = "AboutMe_checkUpdate";

    /**
     * 消息列表红点
     * */
    public final static String APP_ID_UNREAD_UPDATE = "Unread_update";


    public static String TAB_REFRESH_NOTICE = "TAB_REFRESH_NOTICE";

    public static String TAB_REFRESH_TAB_ID = "TAB_REFRESH_TAB_ID";

    private TabNoticeManager() {

    }

    private static TabNoticeManager sTabNoticeManager = new TabNoticeManager();


    public static TabNoticeManager getInstance() {
        return sTabNoticeManager;
    }

    /**
     * 存储每个轻应用的红点结果(key: appId,  value: LightNoticeJson)
     */
    private ConcurrentHashMap<String, LightNoticeData> mLightNoticeResult = new ConcurrentHashMap<>();


    /**
     * 存储TAB与轻应用之间的关系(key: orgCode, value : <tabId, appIdList>), 最终统计时使用
     */
    private ConcurrentHashMap<String, Map<String, Set<String>>> mOrgTabLightMapping = new ConcurrentHashMap<>();


    @NonNull
    private Map<String, Set<String>> getCurrentOrgTabLightMap(Context context) {
        String currentCode = PersonalShareInfo.getInstance().getCurrentOrg(context);

        Map<String, Set<String>> lightMap = mOrgTabLightMapping.get(currentCode);
        if(null == lightMap) {
            lightMap = new HashMap<>();
            mOrgTabLightMapping.put(currentCode, lightMap);
        }

        return lightMap;
    }


    /**
     * 注册一个轻应用
     *
     * @param tabId
     * @param lightAppId
     */
    public void registerLightNoticeMapping(String tabId, String lightAppId) {
        Context context = BaseApplicationLike.baseContext;

        Map<String, Set<String>> currentOrgTabLightMap = getCurrentOrgTabLightMap(context);
        Set<String> lights = currentOrgTabLightMap.get(tabId);
        if (lights == null) {
            lights = new ArraySet<>();
            currentOrgTabLightMap.put(tabId, lights);
        }
        lights.add(lightAppId);


    }


    /**
     * 注册一个轻应用
     *
     * @param lightNoticeMapping
     *
     * */
    public void registerLightNoticeMapping(LightNoticeMapping lightNoticeMapping) {
        registerLightNoticeMapping(lightNoticeMapping.getTabIds(), lightNoticeMapping.getAppId());
    }

    /**
     * 注册一个轻应用
     *
     * @param tabIds
     * @param lightAppId
     */
    public void registerLightNoticeMapping(List<String> tabIds, String lightAppId) {
        for(String tabId : tabIds) {
            registerLightNoticeMapping(tabId, lightAppId);
        }
    }

    /**
     * 清除整个 tab 的红点结果
     * */
    public void unregisterTab(String tabId) {
        Map<String, Set<String>> currentOrgTabLightMap = getCurrentOrgTabLightMap(BaseApplicationLike.baseContext);
        currentOrgTabLightMap.remove(tabId);
    }

    /**
     * 清除tab 关系里的对应轻应用的红点结果
     * */
    public void unregisterLightNotice(String lightAppId) {
        Map<String, Set<String>> currentOrgTabLightMap = getCurrentOrgTabLightMap(BaseApplicationLike.baseContext);
        for(Map.Entry<String, Set<String>> tabLightMapEntry : currentOrgTabLightMap.entrySet()) {
            String tabId = tabLightMapEntry.getKey();
            Set<String> lights = tabLightMapEntry.getValue();
            if(!ListUtil.isEmpty(lights) && lights.remove(lightAppId)) {
                update(tabId);
            }

        }
    }

    public void unregisterLightNotice(String tabId, String lightAppId) {
        Map<String, Set<String>> currentOrgTabLightMap = getCurrentOrgTabLightMap(BaseApplicationLike.baseContext);
        Set<String> lights = currentOrgTabLightMap.get(tabId);
        if(!ListUtil.isEmpty(lights)) {
            lights.remove(lightAppId);
        }

    }

    /**
     * 保存一个红点结果
     *
     * @param lightNoticeId
     * @param lightNoticeData
     */
    public void saveLightNoticeResult(String lightNoticeId, LightNoticeData lightNoticeData) {
        if (null != lightNoticeData) {
            mLightNoticeResult.put(lightNoticeId, lightNoticeData);
        }
    }

    /**
     * 检查红点数据是否需要更新
     * @param lightNoticeId
     * @param newLightNoticeData
     * @return 返回是否需要更新
     * */
    public boolean checkLightNoticeUpdate(String lightNoticeId, LightNoticeData newLightNoticeData) {
        LightNoticeData lightNoticeJson = getLightNoticeData(lightNoticeId);


        if(null == newLightNoticeData || !newLightNoticeData.isLegal()) {
            return true;
        }

        if(null == lightNoticeJson || !lightNoticeJson.isLegal()) {
            return true;
        }

        return !newLightNoticeData.tip.equals(lightNoticeJson.tip);
    }

    @Nullable
    public LightNoticeData getLightNoticeData(String lightNoticeId) {
        if(null == lightNoticeId) {
            return null;
        }

        return mLightNoticeResult.get(lightNoticeId);
    }

    /**
     * 返回在正确范围内的红点数据(已注册对应tab)
     * */
    @Nullable
    public LightNoticeData getLightNoticeDataInRange(String tabId, String lightNoticeId) {
        Map<String, Set<String>> currentOrgTabLightMap = getCurrentOrgTabLightMap(BaseApplicationLike.baseContext);
        Set<String> lightsRegisterInMap = currentOrgTabLightMap.get(tabId);
        if(null != lightsRegisterInMap && lightsRegisterInMap.contains(lightNoticeId)) {
            return getLightNoticeData(lightNoticeId);

        } else {
            return null;
        }
    }

    public LightNoticeData getTabNotice(String tabId) {

        boolean dot = false;
        boolean icon = false;
        int count = 0;
        //原则 ，如果有做任意数字，则显示数字
        //如果没有数字，但有红点，显示红点
        //否则，显示NOTHING
        Set<String> lights = getCurrentOrgTabLightMap(BaseApplicationLike.baseContext).get(tabId);
        if (!ListUtil.isEmpty(lights)) {
            for (String lightId : lights) {
                LightNoticeData lightNoticeJson = getLightNoticeData(lightId);

                if (null == lightNoticeJson) {
                    continue;
                }

                if (lightNoticeJson.isDigit()) {
                    count += Integer.parseInt(lightNoticeJson.tip.num);

                } else if (lightNoticeJson.isDot()) {

                    dot = true;
                } else if (lightNoticeJson.isIcon()) {

                    icon = true;
                } else if (lightNoticeJson.isNothing()) {

                }
            }

        }

        if (count == 0) {
            if (dot) {
                return LightNoticeData.createDotLightNotice();

            } else if (icon) {
                return LightNoticeData.createDotLightNotice();

            } else {
                return LightNoticeData.createNothing();

            }
        } else {
            return LightNoticeData.createNumLightNotice(count);

        }
    }

    public void update(LightNoticeMapping lightNoticeMapping, LightNoticeData lightNoticeData) {
        //保存此次结果
        TabNoticeManager.getInstance().saveLightNoticeResult(lightNoticeMapping.getAppId(), lightNoticeData);
        update(lightNoticeMapping.getTabIds());


    }

    /**
     * 刷新 tab 红点
     * @param tabId
     * */
    public void update(String tabId) {
        //通知刷新结果
        Intent intent = new Intent(TAB_REFRESH_NOTICE);
        intent.putExtra(TAB_REFRESH_TAB_ID, tabId);
        LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).sendBroadcast(intent);
    }

    /**
     * 刷新 tab 红点
     * @param tabIds
     * */
    public void update(List<String> tabIds) {
        for(String tabId : tabIds) {
            update(tabId);
        }
    }


    @NonNull
    public String getCircleAppId(Context context) {
        String currentOrgCode = PersonalShareInfo.getInstance().getCurrentOrg(context);

        return currentOrgCode + "_" + TabNoticeManager.APP_ID_FOUND_COLLEAGUE;
    }

    public String getEmailId() {
        return Session.EMAIL_APP_ID;
    }


    public void clear() {
        mLightNoticeResult.clear();
        mOrgTabLightMapping.clear();
    }
}
