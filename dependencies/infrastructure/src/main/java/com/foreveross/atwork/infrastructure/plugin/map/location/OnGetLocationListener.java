package com.foreveross.atwork.infrastructure.plugin.map.location;

import androidx.annotation.NonNull;

import com.foreveross.atwork.infrastructure.model.location.GetLocationInfo;

public interface OnGetLocationListener {
    void onResult(@NonNull GetLocationInfo getLocationInfo);
}
