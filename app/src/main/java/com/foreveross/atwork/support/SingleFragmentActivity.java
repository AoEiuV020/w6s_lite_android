package com.foreveross.atwork.support;


import android.os.Bundle;
import android.view.KeyEvent;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.foreveross.atwork.R;

/**
 * Created by lingen on 15/3/19.
 * 单Fragment的Activity父类
 */
public abstract class SingleFragmentActivity extends AtworkBaseActivity {

    private Fragment mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
        initFragment();
    }

    protected abstract Fragment createFragment();

    public Fragment getFragment() {
        return mFragment;
    }


    private void initFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        mFragment = fragmentManager.findFragmentById(R.id.fragmentContainer);
        if (null == mFragment) {
            mFragment = createFragment();

            initBundleData();

            fragmentManager.beginTransaction().add(R.id.fragmentContainer, mFragment).commit();
        }
    }

    private void initBundleData() {
        Bundle data = getIntent().getExtras();
        if (null != data) {

            if(null == mFragment.getArguments()) {
                mFragment.setArguments(data);
            }
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mFragment instanceof BackHandledFragment) {
            BackHandledFragment backHandledFragment = (BackHandledFragment) mFragment;
            return backHandledFragment.onBackPressed();
        }
        return super.onKeyDown(keyCode, event);
    }

}
