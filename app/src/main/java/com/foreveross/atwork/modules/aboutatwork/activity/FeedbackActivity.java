package com.foreveross.atwork.modules.aboutatwork.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.foreveross.atwork.modules.aboutatwork.fragment.FeedbackFragment;
import com.foreveross.atwork.support.SingleFragmentActivity;

/**
 * Created by ReyZhang on 2015/5/11.
 */
public class FeedbackActivity extends SingleFragmentActivity {

    private static final String TAG = FeedbackActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, FeedbackActivity.class);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        return new FeedbackFragment();
    }

}
