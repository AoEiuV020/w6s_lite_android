package com.foreveross.atwork.modules.advertisement.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

import androidx.fragment.app.Fragment;

import com.foreveross.atwork.modules.advertisement.fragment.BootAdvertisementFragment;
import com.foreveross.atwork.support.NoFilterSingleFragmentActivity;

import static com.foreveross.atwork.modules.advertisement.fragment.BootAdvertisementFragment.KEY_AD_ID;
import static com.foreveross.atwork.modules.advertisement.fragment.BootAdvertisementFragment.KEY_AD_NAME;
import static com.foreveross.atwork.modules.advertisement.fragment.BootAdvertisementFragment.KEY_AD_ORG_ID;
import static com.foreveross.atwork.modules.advertisement.fragment.BootAdvertisementFragment.KEY_AD_PATH;
import static com.foreveross.atwork.modules.advertisement.fragment.BootAdvertisementFragment.KEY_AD_SKIP_TIME;
import static com.foreveross.atwork.modules.advertisement.fragment.BootAdvertisementFragment.KEY_AD_TYPE;
import static com.foreveross.atwork.modules.advertisement.fragment.BootAdvertisementFragment.KEY_LINK_URL;

/**
 * Created by reyzhang22 on 17/9/18.
 */

public class AdvertisementActivity extends NoFilterSingleFragmentActivity {

    private Bundle mBundle;
    private BootAdvertisementFragment mFragment;

    /**
     *
     * @param context
     * @param advertisementType
     * @param filePath
     * @param skipTime
     * @param toUrl
     * @return
     */
    public static Intent getIntent(Context context, String orgId, String advertisementId, String advertisementName, String advertisementType, String filePath, int skipTime, String toUrl) {
        Intent intent = new Intent(context, AdvertisementActivity.class);
        intent.putExtra(KEY_AD_ORG_ID, orgId);
        intent.putExtra(KEY_AD_ID, advertisementId);
        intent.putExtra(KEY_AD_NAME, advertisementName);
        intent.putExtra(KEY_AD_TYPE, advertisementType);
        intent.putExtra(KEY_AD_PATH, filePath);
        intent.putExtra(KEY_AD_SKIP_TIME, skipTime);
        intent.putExtra(KEY_LINK_URL, toUrl);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mBundle = getIntent().getExtras();
        super.onCreate(savedInstanceState);
    }

    @Override
    protected Fragment createFragment() {
        mFragment = new BootAdvertisementFragment();
        mFragment.setArguments(mBundle);
        return mFragment;
    }

    @Override
    public boolean onKeyDown(int keyCode,KeyEvent event){
        if(keyCode==KeyEvent.KEYCODE_BACK)
            return true;//不执行父类点击事件
        return super.onKeyDown(keyCode, event);//继续执行父类其他点击事件
    }


    @Override
    public void changeStatusBar() {
        //do nothing
//        super.changeStatusBar();
    }

    @Override
    public boolean needCheckAppLegal() {
        return false;
    }
}
