package com.foreveross.atwork.modules.common.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;

import androidx.fragment.app.Fragment;

import com.foreveross.atwork.R;
import com.foreveross.atwork.modules.common.fragment.WorkPlusCameraFragment;
import com.foreveross.atwork.support.SingleFragmentActivity;

/**
 * Created by reyzhang22 on 15/11/12.
 */
public class WorkPlusCameraActivity extends SingleFragmentActivity {


    private onTouchForFragmentListener mTouchListener;

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, WorkPlusCameraActivity.class);
        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected Fragment createFragment() {
        return new WorkPlusCameraFragment();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //界面退出动画
        this.overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
    }



    public void setOnTouchListener(onTouchForFragmentListener listener) {
        mTouchListener = listener;
    }

    public interface onTouchForFragmentListener {
        void onTouchEventForFragment(MotionEvent event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mTouchListener.onTouchEventForFragment(event);
        return super.onTouchEvent(event);
    }
}
