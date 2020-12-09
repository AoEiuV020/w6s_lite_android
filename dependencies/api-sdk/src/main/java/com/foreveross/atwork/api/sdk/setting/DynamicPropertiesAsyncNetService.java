package com.foreveross.atwork.api.sdk.setting;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.foreverht.threadGear.AsyncTaskThreadPool;
import com.foreveross.atwork.api.sdk.NetWorkFailListener;
import com.foreveross.atwork.api.sdk.net.HttpResult;
import com.foreveross.atwork.api.sdk.util.NetWorkHttpResultHelper;
import com.foreveross.atwork.infrastructure.model.organizationSetting.OrganizationSettings;
import com.foreveross.atwork.infrastructure.model.domain.DomainSettings;
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo;
import com.foreveross.atwork.infrastructure.utils.LogUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Map;

/**
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
 * Created by reyzhang22 on 16/7/8.
 */
public class DynamicPropertiesAsyncNetService {

    public static final String TAG = DynamicPropertiesAsyncNetService.class.getSimpleName();
    public static final String ACTION_INTENT_UPDATE_EXTRA = "ACTION_INTENT_UPDATE_EXTRA";
    public static final String DATA_INTENT_SILENT_UPDATE = "DATA_INTENT_SILENT_UPDATE";

    private static DynamicPropertiesAsyncNetService sInstance = new DynamicPropertiesAsyncNetService();

    public static DynamicPropertiesAsyncNetService getInstance() {
        synchronized (TAG) {
            if (sInstance == null) {
                sInstance = new DynamicPropertiesAsyncNetService();
            }
            return sInstance;
        }
    }


    @SuppressLint("StaticFieldLeak")
    public void getOrganizationSettingsAsync(final Context context, final String userId, final long refreshTime, final onOrganizationSettingsListener listener) {
        new AsyncTask<Void, Void, Map<String, OrganizationSettings>>() {
            @Override
            protected Map<String, OrganizationSettings> doInBackground(Void... params) {
                HttpResult httpResult = DynamicPropertiesSyncNetService.getInstance().getOrganizationsSettings(context, userId, refreshTime);
                Map<String, OrganizationSettings> userOrganizationSettings = PersonalShareInfo.getInstance().getCurrentUserOrganizationsSettings(context);

                if (httpResult != null) {
                    String resultText = NetWorkHttpResultHelper.getResultText(httpResult.result);
                    LogUtil.e("获取到组织设置数据eee:", resultText);
                    if (!TextUtils.isEmpty(resultText) && !"{}".equalsIgnoreCase(resultText)) {

                        Gson gson=new Gson();

                        Map<String, OrganizationSettings> updateSettings = gson.fromJson(resultText, new TypeToken<Map<String, OrganizationSettings>>() {}.getType());
                        if (userOrganizationSettings == null) {
                            userOrganizationSettings = updateSettings;
                            PersonalShareInfo.getInstance().setCurrentUserOrganizationsSettings(context, resultText);
                        } else {
                            userOrganizationSettings.putAll(updateSettings);
                            PersonalShareInfo.getInstance().setCurrentUserOrganizationsSettings(context, new Gson().toJson(userOrganizationSettings));
                        }

                    }

                }
                return userOrganizationSettings;
            }

            @Override
            protected void onPostExecute(Map<String, OrganizationSettings> organizationsSettings) {
                super.onPostExecute(organizationsSettings);
                if (listener == null) {
                    return;
                }
                listener.onOrganizationSettingsCallback(organizationsSettings, userId);
            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }

    public interface OnDomainSettingsListener extends NetWorkFailListener {
        void onDomainSettingsCallback(DomainSettings domainSettings);
        void onDomainSettingsFail();
    }

    public interface onOrganizationSettingsListener extends NetWorkFailListener {
        void onOrganizationSettingsCallback(Map<String, OrganizationSettings> organizationSettings, String orgId);
        void onOrganizationSettingsFail();
    }
}
