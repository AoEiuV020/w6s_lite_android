package com.foreveross.atwork.modules.setting.activity;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import com.foreveross.atwork.modules.setting.fragment.CalendarSettingFragment;
import com.foreveross.atwork.support.SingleFragmentActivity;

public class CalendarSettingsActivity extends SingleFragmentActivity {

    public static void startCalendarSettings(Context context) {
        Intent intent =  new Intent(context, CalendarSettingsActivity.class);
        context.startActivity(intent);
    }


    @Override
    protected Fragment createFragment() {
        return new CalendarSettingFragment();
    }
}
