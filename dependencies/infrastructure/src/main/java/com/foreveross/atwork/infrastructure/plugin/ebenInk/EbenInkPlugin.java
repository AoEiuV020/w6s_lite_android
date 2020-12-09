package com.foreveross.atwork.infrastructure.plugin.ebenInk;

import android.content.Context;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentManager;

import com.foreveross.atwork.infrastructure.plugin.WorkplusPlugin;
import com.w6s.inter.OnPenalResultCallback;

public class EbenInkPlugin {

    public interface IEbenInkPlugin extends WorkplusPlugin {
        void showPanel(Context context, FragmentManager fragmentManager, OnPenalResultCallback callback);
    }
}
