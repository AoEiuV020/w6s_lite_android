package com.foreveross.atwork.infrastructure.beeworks;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.foreveross.atwork.infrastructure.utils.JsonUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;

import org.json.JSONObject;

/**
 * Created by dasunsy on 2016/11/9.
 */

public class BeeWorksHelper {


    @Nullable
    public static <T>T createInstance(@Nullable JSONObject jsonObject , Class<T> classOfT) {
        if(null == jsonObject) {
            return null;
        }

        return JsonUtil.fromJson(jsonObject.toString(), classOfT);
    }

    public static String getString(Context context, String valueString) {
        String valueName = valueString;

        if(null != valueName && valueName.startsWith("local://")) {
            String resStr = valueName.substring("local://".length());
            valueName = StringUtils.getStringFromResource(context, resStr);
        }

        return valueName;
    }

    @NonNull
    public static String getCopyright(Context context, boolean needCompanyUrl) {
        BeeWorksCopyright beeWorksCopyright = BeeWorks.getInstance().config.copyright;

        String companyCopyright = beeWorksCopyright.getCompanyCopyright(context);
        companyCopyright = getString(context, companyCopyright);

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(companyCopyright);

        if(needCompanyUrl) {
            stringBuilder.append("\n").append(beeWorksCopyright.companyUrl);
        }

        return stringBuilder.toString();
    }

    public static boolean isBeeWorksDefinitionPackage() {
        return !StringUtils.isEmpty(BeeWorks.getInstance().config.beeWorksUrl);
    }

}
