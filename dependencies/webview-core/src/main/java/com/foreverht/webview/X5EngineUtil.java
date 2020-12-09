package com.foreverht.webview;

import android.content.Context;

import com.foreveross.atwork.infrastructure.utils.LogUtil;
import com.foreveross.atwork.infrastructure.utils.Logger;
import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.TbsDownloader;
import com.tencent.smtt.sdk.TbsListener;

/**
 * Created by dasunsy on 2018/3/1.
 */

public class X5EngineUtil {
    public static void init(Context context) {
        QbSdk.setDownloadWithoutWifi(true);
        TbsDownloader.needDownload(context, true);
        QbSdk.setTbsListener(new TbsListener() {
            @Override
            public void onDownloadFinish(int i) {
                Logger.e("TBS LISTENER", "onDownloadFinish i = " + i);
            }

            @Override
            public void onInstallFinish(int i) {
                Logger.e("TBS LISTENER", "onInstallFinish i = " + i);
            }

            @Override
            public void onDownloadProgress(int i) {
                Logger.e("TBS LISTENER", "onDownloadProgress i = " + i);
            }
        });
        QbSdk.initX5Environment(context, new QbSdk.PreInitCallback() {
            @Override
            public void onCoreInitFinished() {
                LogUtil.e("x5   onCoreInitFinished");
            }

            @Override
            public void onViewInitFinished(boolean b) {
                LogUtil.e("x5   onViewInitFinished   result : " + b);

            }
        });
    }
}
