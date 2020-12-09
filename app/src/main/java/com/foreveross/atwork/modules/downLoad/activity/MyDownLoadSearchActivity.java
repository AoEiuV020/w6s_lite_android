package com.foreveross.atwork.modules.downLoad.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.foreveross.atwork.modules.downLoad.fragment.MyDownLoadSearchFragment;
import com.foreveross.atwork.support.SingleFragmentActivity;

/**
 * Created by wuzejie on 2020/1/14.
 * Description:我的下载的搜索页面
 */

public class MyDownLoadSearchActivity extends SingleFragmentActivity {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

    }

    public static Intent getIntent(Context context) {
        return new Intent(context, MyDownLoadSearchActivity.class);
    }

    @Override
    protected Fragment createFragment() {
        return new MyDownLoadSearchFragment();
    }

}
