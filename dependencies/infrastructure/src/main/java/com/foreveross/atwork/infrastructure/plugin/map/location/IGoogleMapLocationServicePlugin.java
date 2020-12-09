package com.foreveross.atwork.infrastructure.plugin.map.location;

import android.content.Context;

import com.foreveross.atwork.infrastructure.plugin.WorkplusPlugin;

import java.util.List;

public interface IGoogleMapLocationServicePlugin extends WorkplusPlugin {
    void startLocation(final Context context, long blockTime, List<String> illegalInstalledList, final OnGetLocationListener getLocationListener);
}
