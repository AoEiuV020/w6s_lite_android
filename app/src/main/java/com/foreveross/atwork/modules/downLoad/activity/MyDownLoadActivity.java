package com.foreveross.atwork.modules.downLoad.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.foreveross.atwork.modules.downLoad.fragment.MyDownLoadFragment;
import com.foreveross.atwork.support.SingleFragmentActivity;

/**
 * Created by wuzejie on 2020/1/8.
 * Description:我的下载
 */

public class MyDownLoadActivity extends SingleFragmentActivity {

    //广播action：
    public static final String REFRESH_DOWN_LOAD_VIEW_PAGER = "REFRESH_DOWN_LOAD_VIEW_PAGER";

    public static int mCurrentViewPagerPosition = 3;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

    }

    public static Intent getIntent(Context context) {
        return new Intent(context, MyDownLoadActivity.class);
    }

    @Override
    protected Fragment createFragment() {
        return new MyDownLoadFragment();
    }

}
