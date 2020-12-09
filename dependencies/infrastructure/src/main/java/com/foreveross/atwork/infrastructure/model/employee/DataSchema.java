package com.foreveross.atwork.infrastructure.model.employee;/**
 @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@@@@%((((((((#&@@@((#((#@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@@%(((((((((((((%@*((((@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@%(((/,/(((((((((@@@*#@@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@#(((@@@((((((((((@@((@@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@(((((((((((((((((((((@@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@@(((((((((((((((((((@@@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@@@@(((((((((((((((*@@@@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
                       __           __
 .--.--.--.-----.----.|  |--.-----.|  |.--.--.-----.
 |  |  |  |  _  |   _||    <|  _  ||  ||  |  |__ --|
 |________|_____|__|  |__|__|   __||__||_____|_____|
                            |__|
 */


import android.content.Context;

import com.foreveross.atwork.infrastructure.model.i18n.CommonI18nInfoData;
import com.foreveross.atwork.infrastructure.model.i18n.I18nInfo;
import com.foreveross.atwork.infrastructure.utils.language.LanguageSupport;
import com.foreveross.atwork.infrastructure.utils.language.LanguageUtil;
import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.List;

/**
 *
 * Created by reyzhang22 on 15/12/18.
 */
public class DataSchema extends I18nInfo implements Serializable {

    public static final String FRIEND_VISIBLE_RANGE = "FRIEND";

    @SerializedName("id")
    public String mId;

    @SerializedName("type")
    public String type;

    @SerializedName("domain_id")
    public String mDomainId;

    @SerializedName("org_code")
    public String mOrgCode;

    @SerializedName("name")
    public String mName;

    @SerializedName("alias")
    public String mAlias;

    @SerializedName("en_alias")
    public String mEnAlias;

    @SerializedName("tw_alias")
    public String mTwAlias;

    @SerializedName("property")
    public String mProperty;

    @SerializedName("min")
    public int mMin;

    @SerializedName("max")
    public int mMax;

    @SerializedName("sort_order")
    public int mSortOrder;

    @SerializedName("opsable")
    public boolean mOpsable;

    @SerializedName("options")
    public List<String> mOptions;

    @SerializedName("visible_range")
    public String mVisibleRange = "ALL";

    @Nullable
    @Override
    public String getStringName() {
        return mAlias;
    }

    @Nullable
    @Override
    public String getStringTwName() {
        return mTwAlias;
    }

    @Nullable
    @Override
    public String getStringEnName() {
        return mEnAlias;
    }

    public String getAliasI18n(Context context) {
        return getNameI18n(context);
    }

    public CommonI18nInfoData getI18nInfo() {
        CommonI18nInfoData commonI18NInfoData = new CommonI18nInfoData();
        commonI18NInfoData.setName(mAlias);
        commonI18NInfoData.setEnName(mEnAlias);
        commonI18NInfoData.setTwName(mTwAlias);
        return commonI18NInfoData;
    }

}
