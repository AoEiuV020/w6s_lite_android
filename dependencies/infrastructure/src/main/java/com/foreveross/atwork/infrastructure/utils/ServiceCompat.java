package com.foreveross.atwork.infrastructure.utils;

import android.content.Context;
import android.content.Intent;

public class ServiceCompat {


    public static void startServiceCompat(Context context, Class<?> serviceCls) {
        startServiceCompat(context, new Intent(context, serviceCls));
    }

    public static void startServiceCompat(Context context, Intent intent) {
        try {

            context.startService(intent);

        } catch (Exception e) {
            e.printStackTrace();

            LogUtil.e(e.getLocalizedMessage());
        }
    }
}
