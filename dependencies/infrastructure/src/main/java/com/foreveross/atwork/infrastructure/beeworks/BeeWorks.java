package com.foreveross.atwork.infrastructure.beeworks;

import android.content.Context;
import androidx.annotation.Nullable;
import android.text.TextUtils;

import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.beeworks.settingPage.BeeWorksSettingPage;
import com.foreveross.atwork.infrastructure.shared.BeeWorksPreviewData;
import com.foreveross.atwork.infrastructure.shared.BeeWorksPublicData;
import com.foreveross.atwork.infrastructure.shared.dev.BeeWorksTestApiData;
import com.foreveross.atwork.infrastructure.shared.SettingInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.google.gson.annotations.SerializedName;
import com.w6s.beeworks.BeeworksApn;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Created by lingen on 15/12/21.
 */
public class BeeWorks {

    private static final Object sLock = new Object();

    private static BeeWorks beeWorks = null;

    @SerializedName("appBase")
    public BeeWorksAppBase beeWorksAppBase;

    @SerializedName("config")
    public BeeWorksConfig config;

    @SerializedName("basePermissions")
    public List<BeeWorksBasePermission> basePermissions;

    @SerializedName("loginPage")
    public BeeWorksLoginPage mLoginPage;

    @SerializedName("settingPage")
    public BeeWorksSettingPage mSettingPage;

    @SerializedName("serviceAppHistoricalMessage")
    public BeeworksServiceAppHistoricalMessage mBeeworksServiceAppHistoricalMessage;

    @SerializedName("tabs")
    public List<BeeWorksTab> tabs;

    @SerializedName("appId")
    public String beeWorksId;

    @SerializedName("versionId")
    public String versionId;

    @SerializedName("androidPush")
    public BeeworksApn beeworksApn;

    private BeeWorks() {

    }

    public boolean isBeeWorksFaceBioSettingEnable() {
        return config != null && config.beeWorksSetting != null && config.beeWorksSetting.getFaceBioSetting().getEnable();
    }

    public static BeeWorks getInstance() {
        synchronized (sLock) {
            if (null == beeWorks) {
                beeWorks = new BeeWorks();
            }
            return beeWorks;
        }
    }



    public void initBeeWorks(String data) {
        initBeeWorks(data, null);
    }

    public void initBeeWorks(String data, @Nullable OnBeeWorksAssembleListener processor) {
        try {
            BeeWorks beeWorks = BeeWorks.getInstance();
            JSONObject root = new JSONObject(data);
            beeWorks.beeWorksId = root.optString("appId");
            beeWorks.config = BeeWorksConfig.createInstance(root.optJSONObject("config"));
            beeWorks.tabs = BeeWorksTab.getBeeWorksTab(root.optJSONArray("tabs"));

            beeWorks.beeWorksAppBase = BeeWorksAppBase.createInstance(root.optJSONObject("appBase"));
            beeWorks.versionId = root.optString("versionId");
            beeWorks.beeworksApn = BeeworksApn.createInstance(root.optJSONObject("androidPush"));
            beeWorks.mSettingPage = BeeWorksHelper.createInstance(root.optJSONObject("settingPage"), BeeWorksSettingPage.class);
            beeWorks.mLoginPage = BeeWorksLoginPage.createInstance(root.optJSONObject("loginPage"));
            beeWorks.basePermissions = BeeWorksBasePermission.getBeeWorksBasePermission(root.optJSONArray("basePermissions"));

            beeWorks.mBeeworksServiceAppHistoricalMessage = BeeWorksHelper.createInstance(root.optJSONObject("serviceAppHistoricalMessage"), BeeworksServiceAppHistoricalMessage.class);

            if(null != processor) {
                processor.process(beeWorks);
            }
            

            AtworkConfig.initConfig();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Nullable
    public BeeWorksTab getBeeWorksTabById(Context context, String id) {
        if (TextUtils.isEmpty(id)) {
            return null;
        }
//        if (beeWorks.tabs == null) {
//            reInitBeeWorks(context);
//        }
        for (BeeWorksTab tab : tabs) {
            if (tab == null) {
                continue;
            }
            if (!id.equalsIgnoreCase(tab.id)) {
                continue;
            }
            return tab;
        }
        return null;
    }


    /**
     * 读取本地文件中JSON字符串
     *
     * @return
     */
    public static String getBeeWorksJson() {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            BufferedReader bf = new BufferedReader(new InputStreamReader(BeeWorks.class.getResourceAsStream("/assets/BeeWorks.json")));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }


    public void reInitBeeWorks(Context context) {
        reInitBeeWorks(context, null);
    }

    /**
     * 重新给beeworks赋值
     */
    public void reInitBeeWorks(Context context, @Nullable OnBeeWorksAssembleListener processor) {
        if (context == null) {
            context = BaseApplicationLike.baseContext;
        }
        if (SettingInfo.getInstance().getUserSettings(context).mIsDevMode
                && !TextUtils.isEmpty(BeeWorksPreviewData.getInstance().getBeeWorksPreviewData(context))) {
            initBeeWorks(BeeWorksPreviewData.getInstance().getBeeWorksPreviewData(context), processor);
            return;
        }


        if (!TextUtils.isEmpty(BeeWorksPublicData.getInstance().getBeeWorksPublicData(context))) {
            try {
                JSONObject devRoot = new JSONObject(BeeWorksPublicData.getInstance().getBeeWorksPublicData(context));
                JSONObject originRoot = new JSONObject(BeeWorks.getBeeWorksJson());
                if (devRoot.optLong("createDate") > originRoot.optLong("createDate")) {
                    initBeeWorks(BeeWorksPublicData.getInstance().getBeeWorksPublicData(context), processor);
                } else {
                    initBeeWorks(BeeWorks.getBeeWorksJson(), processor);
                    BeeWorksPublicData.getInstance().clearBeeWorksPublicData(context);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


            return;
        }


        if(!TextUtils.isEmpty(BeeWorksTestApiData.getInstance().getCurrentBeeworksTestApiData(context))) {
            initBeeWorks(BeeWorksTestApiData.getInstance().getCurrentBeeworksTestApiData(context), processor);
            return;
        }


        initBeeWorks(BeeWorks.getBeeWorksJson(), processor);

    }

    public boolean containsTab(String id) {
        for(BeeWorksTab beeWorksTab : tabs) {
            if(id.equals(beeWorksTab.id)) {
                return true;
            }
        }

        return false;
    }

    public int getTabIndexById(String id) {
        for(int i = 0; i < tabs.size(); i++) {
            BeeWorksTab beeWorksTab = tabs.get(i);
            if(id.equals(beeWorksTab.id)) {
                return i;
            }
        }

        return -1;
    }


    public int getTabIndexByTabId(String tabId) {
        for(int i = 0; i < tabs.size(); i++) {
            BeeWorksTab beeWorksTab = tabs.get(i);
            if(tabId.equalsIgnoreCase(beeWorksTab.tabId)) {
                return i;
            }
        }

        return -1;
    }

    public boolean removeTab(String id) {
        BeeWorksTab beeWorksTabRemoved = null;
        for(BeeWorksTab beeWorksTab : tabs) {
            if(id.equals(beeWorksTab.id)) {
                beeWorksTabRemoved = beeWorksTab;
                break;
            }
        }

        if(null != beeWorksTabRemoved) {
            return tabs.remove(beeWorksTabRemoved);
        }

        return false;


    }


    public interface OnBeeWorksAssembleListener {
        void process(BeeWorks beeWorks);
    }

}
