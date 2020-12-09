package com.foreveross.atwork.infrastructure.mail;

import android.app.Application;

/**
 * Created by reyzhang22 on 15/8/1.
 */
public interface K9SetupListener {

    void onK9Setup(Application application, String packageName, String mainActivity);

    void setK9SetupListener(K9Setter k9Setter);

    interface K9Setter {
        void onK9Ready(K9SetupListener listener);
    }
}
