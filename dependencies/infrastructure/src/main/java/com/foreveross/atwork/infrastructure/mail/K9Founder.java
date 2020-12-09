package com.foreveross.atwork.infrastructure.mail;

import android.app.Application;

/**
 * Created by reyzhang22 on 15/8/1.
 */
public class K9Founder {

    public static final String TAG = K9Founder.class.getSimpleName();

    public static K9Founder sFounder = new K9Founder();

    public static K9Founder getInstance() {
        synchronized (TAG) {
            if (sFounder == null) {
                sFounder = new K9Founder();
            }
            return sFounder;
        }
    }

    public void setupK9(Application application, String mainActivity, String packageName) {

    }
}
