package com.foreveross.atwork.manager;

import android.content.Context;

import androidx.fragment.app.FragmentManager;

import com.foreveross.atwork.infrastructure.plugin.WorkplusPlugin;
import com.foreveross.atwork.infrastructure.plugin.WorkplusPluginCore;
import com.foreveross.atwork.infrastructure.plugin.ebenInk.EbenInkPlugin;
import com.w6s.inter.OnPenalResultCallback;

public class EbenInkManager {

    private EbenInkPlugin.IEbenInkPlugin mPlugin;

    private static final EbenInkManager sInstance = new EbenInkManager();

    public static EbenInkManager getInstance() {
        return sInstance;
    }

    public void initEbenInk() {
        try {
            WorkplusPluginCore.registerPresenterPlugin("com.w6s.plugin.ebenink.EbenInkPresenter");
        } catch (Exception e) {
            e.printStackTrace();
        }
        WorkplusPlugin plugin = WorkplusPluginCore.getPlugin(EbenInkPlugin.IEbenInkPlugin.class);
        if (plugin != null) {
            mPlugin = (EbenInkPlugin.IEbenInkPlugin)plugin;
        }
    }

    public void showPanel(Context context, FragmentManager fragmentManager, OnPenalResultCallback callback) {
        if (mPlugin == null) {
            return;
        }
        mPlugin.showPanel(context, fragmentManager, callback);
    }
}
