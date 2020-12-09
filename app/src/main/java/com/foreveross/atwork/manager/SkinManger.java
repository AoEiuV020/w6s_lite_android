package com.foreveross.atwork.manager;

import android.content.Context;
import android.os.AsyncTask;

import com.foreveross.atwork.infrastructure.manager.OrganizationSettingsManager;
import com.foreveross.atwork.infrastructure.model.organizationSetting.ThemeSettings;
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.theme.interfaces.OnLoadSkinListener;
import com.foreveross.theme.manager.SkinMaster;
import com.foreveross.theme.model.Theme;
import com.foreveross.theme.model.ThemeType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by dasunsy on 2016/9/28.
 */

public class SkinManger {
    private static Object sLock = new Object();

    public static SkinManger sInstance = null;

    public static SkinManger getInstance() {
        /**
         * double check
         * */
        if (null == sInstance) {
            synchronized (sLock) {
                if (null == sInstance) {
                    sInstance = new SkinManger();
                }

            }
        }

        return sInstance;
    }

    public void init(Context context) {
        SkinMaster.getInstance().init(context);
        Theme theme = getSystemTheme(PersonalShareInfo.getInstance().getCurrentOrg(context));

        SkinMaster.getInstance().setCurrentTheme(theme);
        SkinMaster.getInstance().notifySkinChange(theme);
    }


    public void load(final String orgCode, final OnLoadSkinListener onLoadSkinListener) {

        new AsyncTask<String, Void, Theme>() {

            @Override
            protected void onPreExecute() {
                if (onLoadSkinListener != null) {
                    onLoadSkinListener.onStart();
                }
            }

            @Override
            protected Theme doInBackground(String... params) {

                Theme theme = getSystemTheme(orgCode);

                return theme;

            }

            @Override
            protected void onPostExecute(Theme theme) {
                if (theme == null) {
                    if (onLoadSkinListener != null) {
                        onLoadSkinListener.onFailed();
                    }
                    return;
                }


                if (!theme.equals(SkinMaster.getInstance().getCurrentTheme())) {
                    SkinMaster.getInstance().setCurrentTheme(theme);

                    SkinMaster.getInstance().notifySkinChange(theme);
                }

                if (onLoadSkinListener != null) {
                    onLoadSkinListener.onSuccess();
                }


            }
        }.execute(orgCode);
    }

    public void clean() {
        SkinMaster.getInstance().setCurrentTheme(getDefaultTheme());
    }



    /**
     * get the system theme which packaged in apk
     * */
    public Theme getSystemTheme(String orgCode) {
        if(!AtworkConfig.SKIN) {
            return getDefaultTheme();
        }

        Theme theme = null;
        ThemeSettings themeSettings = OrganizationSettingsManager.getInstance().getThemeSetting(orgCode);

        if(null == themeSettings) {
            theme = getDefaultTheme();

        } else {

            String themeJson = loadSystemThemeJson(themeSettings.mThemeName);
            if(!StringUtils.isEmpty(themeJson)) {
                theme = SkinMaster.getInstance().parseTheme(themeJson, themeSettings.mThemeName, ThemeType.SYSTEM);

            } else {
                theme = SkinMaster.getInstance().getCurrentTheme();

            }
        }

        return theme;
    }

    public Theme getDefaultTheme() {
        Theme theme;
        String defaultThemeName = AtworkConfig.DEFAULT_THEME.toString();
        String themeJson = loadSystemThemeJson(defaultThemeName);
        theme = SkinMaster.getInstance().parseTheme(themeJson, defaultThemeName, ThemeType.SYSTEM);
        return theme;
    }

    /**
     * 若不存在主题, 则返回空字符("")
     * */
    public String loadSystemThemeJson(String themeName) {
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bf = null;
        try {
            bf = new BufferedReader(new InputStreamReader(SkinManger.class.getResourceAsStream("/assets/theme/system/" + themeName + "/theme.json")));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            if(null != bf) {
                try {
                    bf.close();
                    bf = null;

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
        return stringBuilder.toString();

    }


}
