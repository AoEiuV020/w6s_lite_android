package com.foreveross.atwork.modules.contact.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.foreverht.workplus.ui.component.statusbar.WorkplusStatusBarHelper;
import com.foreveross.atwork.R;
import com.foreveross.atwork.modules.contact.fragment.UserAvatarPreviewFragment;
import com.foreveross.atwork.support.SingleFragmentActivity;

/**
 * Created by reyzhang22 on 15/6/11.
 */
public class UserAvatarPreviewActivity extends SingleFragmentActivity {

    private static final String TAG = UserAvatarPreviewActivity.class.getSimpleName();

    public static final String BUNDLE_AVATAR_ID = "BUNDLE_AVATAR_ID";

    private Bundle mBundle;

    private UserAvatarPreviewFragment mFragment;

    public static Intent getIntent(Context context, String avatarId) {
        Intent intent = new Intent(context, UserAvatarPreviewActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_AVATAR_ID, avatarId);
        intent.putExtras(bundle);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(R.anim.scale_in, R.anim.scale_out);
        mBundle = getIntent().getExtras();
        super.onCreate(savedInstanceState);

    }

    @Override
    protected Fragment createFragment() {
        mFragment = new UserAvatarPreviewFragment();
        mFragment.setArguments(mBundle);
        return mFragment;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.overridePendingTransition(R.anim.fade_scale_in, R.anim.fade_scale_out);
    }

    @Override
    public void changeStatusBar() {
        WorkplusStatusBarHelper.setCommonFullScreenStatusBar(this, false);
    }
}
