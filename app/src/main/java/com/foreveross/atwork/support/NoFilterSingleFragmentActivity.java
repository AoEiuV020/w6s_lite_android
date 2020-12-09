package com.foreveross.atwork.support;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.foreveross.atwork.R;

/**
 * Created by lingen on 15/4/26.
 */
public abstract class NoFilterSingleFragmentActivity extends BaseActivity {

    private Fragment fragment;

    protected abstract Fragment createFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            // Activity was brought to front and not created,
            // Thus finishing this will get us to the last viewed activity
            finish();
            return;
        }
        setContentView(R.layout.activity_fragment);
        initFragment();
    }


    private void initFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragment = fragmentManager.findFragmentById(R.id.fragmentContainer);
        if (fragment == null) {
            fragment = createFragment();
            fragmentManager.beginTransaction().add(R.id.fragmentContainer, fragment).commit();
        }
    }

    @Override
    public void onBackPressed() {
        if (fragment instanceof BackHandledFragment) {
            BackHandledFragment backHandledFragment = (BackHandledFragment) fragment;
            backHandledFragment.onBackPressed();
        }
        super.onBackPressed();
    }

    public void setFullScreen() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
}

