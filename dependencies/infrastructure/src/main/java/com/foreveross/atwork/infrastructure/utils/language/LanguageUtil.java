package com.foreveross.atwork.infrastructure.utils.language;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import androidx.annotation.NonNull;
import android.util.DisplayMetrics;

import com.foreveross.atwork.infrastructure.shared.CommonShareInfo;

import java.util.Locale;

/**
 * Created by dasunsy on 2017/4/20.
 */

public class LanguageUtil {

    public static Locale getLocale(Context context) {
        return context.getResources().getConfiguration().locale;
    }

    public static int getLanguageSupport(Context context) {
        Locale locale = getLocale(context);
        return getLanguageSupport(locale);
    }

    private static int getLanguageSupport(Locale locale) {
        String lang = locale.getCountry();

        if (lang.toLowerCase().endsWith("hk") || lang.toLowerCase().endsWith("tw")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if("Hans".equalsIgnoreCase(locale.getScript())) {
                    return LanguageSupport.SIMPLIFIED_CHINESE;

                } else {
                    return LanguageSupport.TRADITIONAL_CHINESE;

                }
            } else {
                return LanguageSupport.TRADITIONAL_CHINESE;

            }
        }

        if (lang.toLowerCase().endsWith("cn")) {
            return LanguageSupport.SIMPLIFIED_CHINESE;
        }
        return LanguageSupport.ENGLISH;
    }

    /**
     * 是否使用的是中文系统
     *
     * @param context
     * @return
     */
    public static boolean isZhLocal(Context context) {
        Locale locale = getLocale(context);
        String language = locale.getLanguage();
        return language.endsWith("zh");
    }



    /**
     * @see #getWorkplusLocaleTag(Locale)
     * */
    public static String getWorkplusLocaleTag(Context context) {
        Locale locale = getLocale(context);
        return getWorkplusLocaleTag(locale);
    }

    /**
     * 获取 workplus 约定使用的语言标记
     * zh-cn: 简体中文
     * zh-rtw: 繁体中文
     * en: 英文
     *
     * @param locale
     * @return 语言标记
     * */
    @NonNull
    private static String getWorkplusLocaleTag(Locale locale) {
        int languageSupport = getLanguageSupport(locale);
        if(LanguageSupport.SIMPLIFIED_CHINESE == languageSupport) {
            return "zh-cn";

        } else if(LanguageSupport.TRADITIONAL_CHINESE == languageSupport){
            return "zh-rtw";

        } else {
            return "en";

        }
    }

    /**
     * 获取用户设置选择的语言
     * @param context
     * @return locale
     * */
    public static Locale getCurrentSettingLocale(Context context) {
        Locale locale;
        int currentSetting = CommonShareInfo.getLanguageSetting(context);
        if (LanguageSetting.SIMPLIFIED_CHINESE == currentSetting) {
            locale = Locale.SIMPLIFIED_CHINESE;

        } else if (LanguageSetting.TRADITIONAL_CHINESE == currentSetting) {
            locale = Locale.TRADITIONAL_CHINESE;

        } else if (LanguageSetting.ENGLISH == currentSetting) {
            locale = Locale.ENGLISH;

        } else {
            locale = Resources.getSystem().getConfiguration().locale;
        }
        return locale;
    }


    public static void checkLanguageSetting(Context context) {
        Locale currentLocale = getLocale(context);
        String currentLocaleTag = getWorkplusLocaleTag(currentLocale);
        Locale currentSettingLocale = getCurrentSettingLocale(context);
        String currentSettingLocaleTag = getWorkplusLocaleTag(currentSettingLocale);

        if (!currentLocaleTag.equalsIgnoreCase(currentSettingLocaleTag)) {
            resetDefault(context);
        }
    }

    public static void resetDefault(Context context) {
        changeLanguage(context, getCurrentSettingLocale(context));
    }

    public static void changeLanguage(Context context, Locale locale) {
        updateContextLanguage(context, locale);
        if (Build.VERSION_CODES.O <= Build.VERSION.SDK_INT) {
            updateContextLanguage(context.getApplicationContext(), locale);
        }

    }

    private static void updateContextLanguage(Context context, Locale locale) {
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
        config.locale = locale;
        resources.updateConfiguration(config, dm);
    }
}
