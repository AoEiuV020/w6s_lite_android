package com.foreveross.atwork.modules.common.activity;

import androidx.fragment.app.Fragment;

import com.foreveross.atwork.R;
import com.foreveross.atwork.modules.common.fragment.AtworkCameraFragment;
import com.foreveross.atwork.support.SingleFragmentActivity;

/**
 * Created by reyzhang22 on 15/11/11.
 */
public class AtworkCamera extends SingleFragmentActivity {


    @Override
    protected Fragment createFragment() {
        return new AtworkCameraFragment();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //界面退出动画
        this.overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
    }

}
