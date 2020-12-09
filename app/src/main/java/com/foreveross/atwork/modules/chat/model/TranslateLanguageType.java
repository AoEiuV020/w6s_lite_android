package com.foreveross.atwork.modules.chat.model;

import android.content.Context;

import com.foreveross.atwork.infrastructure.utils.language.LanguageSupport;
import com.foreveross.atwork.infrastructure.utils.language.LanguageUtil;

/**
 * Created by wuzejie on 19/12/6.
 * Description:智能翻译
 */
public class TranslateLanguageType {

    private String mChName;//中文名
    private String mName;//语言名称
    private String mShortName;//语言简名
    private Boolean mIsSelected;//是否被选择

    public TranslateLanguageType(String mChName, String mName, String mShortName, Boolean mIsSelected){
        this.mChName = mChName;
        this.mName = mName;
        this.mShortName = mShortName;
        this.mIsSelected = mIsSelected;
    }

    public Boolean getIsSelected() {
        return mIsSelected;
    }

    public void setIsSelected(Boolean mIsSelected) {
        this.mIsSelected = mIsSelected;
    }

    public String getChName() {
        return mChName;
    }

    public String getName() {
        return mName;
    }

    public String getShortName() {
        return mShortName;
    }

    public void setChName(String mChName) {
        this.mChName = mChName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public void setShortName(String mShortName) {
        this.mShortName = mShortName;
    }


    /**
     * Description:根据语种的缩写，获取语种的名称
     * @param shortName
     * @return
     */
    public static String getTranslateText( String shortName){
        String mName = "";
        switch (shortName){
            case "zh-CHS":
                mName = "简体中文";
                break;
            case "en":
                mName = "English";
                break;
            case "ja":
                mName = "日本語";
                break;
            case "ko":
                mName = "한국어";
                break;
            case "fr":
                mName = "Français";
                break;
            case "es":
                mName = "Español";
                break;
            case "pt":
                mName = "Português";
                break;
            case "ru":
                mName = "русский язык";
                break;
            case "vi":
                mName = "Ngôn ngữ Việt";
                break;
            case "de":
                mName = "Deutsch";
                break;
            case "ar":
                mName = "اللغة العربية";
                break;
            case "id":
                mName = "Bahasa Indonesia";
                break;
            case "it":
                mName = "italiano";
                break;
        }
        return mName;
    }

    /**
     * Description:获取系统语言的简称
     * @return
     */
    public static String getLocalLanguageShortName(Context context){
        String languageShortName = "";
        switch (LanguageUtil.getLanguageSupport(context)) {

            case LanguageSupport.SIMPLIFIED_CHINESE:
            case LanguageSupport.TRADITIONAL_CHINESE:
                languageShortName = "zh-CHS";
                break;

            default:
                languageShortName = "en";

        }
        return languageShortName;
    }

    /**
     * Descripiton:使用枚举，规范并使语种和代表的数字的对应关系更加清晰
     */
    public enum TranslateLanguage {
        /**
         * 语种为空
         */
        NO(0, ""),
        /**
         * 简体中文
         */
        ZHCHS(1, "zh-CHS"),

        /**
         * 英语
         */
        EN(2, "en"),

        /**
         * 日文
         */
        JA(3, "ja"),

        /**
         * 韩文
         */
        KO(4, "ko"),

        /**
         * 法文
         */
        FR(5, "fr"),

        /**
         * 西班牙
         */
        ES(6, "es"),

        /**
         * 葡萄牙文
         */
        PT(7, "pt"),

        /**
         * 俄文
         */
        RU(8, "ru"),

        /**
         * 越南文
         */
        VI(9, "vi"),

        /**
         * 德文
         */
        DE(10, "de"),

        /**
         * 阿拉伯文
         */
        AR(11, "ar"),

        /**
         * 印尼文
         */
        ID(12, "id"),

        /**
         * 意大利文
         */
        IT(13, "it");

        private int value;
        private String name;

        TranslateLanguage(int value, String name) {
            this.value = value;
            this.name = name;
        }

        public int getValue() {
            return this.value;
        }

        public String getName() {
            return this.name;
        }

        /**
         * Descripiton:根据语种简称返回对应的数字
         * @param strTranslateLanguageType
         * @return
         */
        public static int getTranslateLanguageValue(String strTranslateLanguageType){
            if(ZHCHS.getName().equalsIgnoreCase(strTranslateLanguageType))
                return ZHCHS.getValue();
            if(EN.getName().equalsIgnoreCase(strTranslateLanguageType))
                return EN.getValue();
            if(JA.getName().equalsIgnoreCase(strTranslateLanguageType))
                return JA.getValue();
            if(KO.getName().equalsIgnoreCase(strTranslateLanguageType))
                return KO.getValue();
            if(FR.getName().equalsIgnoreCase(strTranslateLanguageType))
                return FR.getValue();
            if(ES.getName().equalsIgnoreCase(strTranslateLanguageType))
                return ES.getValue();
            if(PT.getName().equalsIgnoreCase(strTranslateLanguageType))
                return PT.getValue();
            if(RU.getName().equalsIgnoreCase(strTranslateLanguageType))
                return RU.getValue();
            if(VI.getName().equalsIgnoreCase(strTranslateLanguageType))
                return VI.getValue();
            if(DE.getName().equalsIgnoreCase(strTranslateLanguageType))
                return DE.getValue();
            if(AR.getName().equalsIgnoreCase(strTranslateLanguageType))
                return AR.getValue();
            if(ID.getName().equalsIgnoreCase(strTranslateLanguageType))
                return ID.getValue();
            if(IT.getName().equalsIgnoreCase(strTranslateLanguageType))
                return IT.getValue();

            return NO.getValue();
        }

        /**
         * Descripiton:根据对应的数字返回语种简称
         * @param value
         * @return
         */
        public static String getTranslateLanguageShortName(int value){
            if(ZHCHS.getValue() == value)
                return ZHCHS.getName();
            if(EN.getValue() == value)
                return EN.getName();
            if(JA.getValue() == value)
                return JA.getName();
            if(KO.getValue() == value)
                return KO.getName();
            if(FR.getValue() == value)
                return FR.getName();
            if(ES.getValue() == value)
                return ES.getName();
            if(PT.getValue() == value)
                return PT.getName();
            if(RU.getValue() == value)
                return RU.getName();
            if(VI.getValue() == value)
                return VI.getName();
            if(DE.getValue() == value)
                return DE.getName();
            if(AR.getValue() == value)
                return AR.getName();
            if(ID.getValue() == value)
                return ID.getName();
            if(IT.getValue() == value)
                return IT.getName();

            return NO.getName();
        }
    }

}
