package com.foreveross.atwork.modules.dropbox.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.model.dropbox.Dropbox;
import com.foreveross.atwork.modules.dropbox.fragment.MyDropboxFileFragment;
import com.foreveross.atwork.modules.dropbox.fragment.OrgsDropboxFragment;
import com.foreveross.atwork.modules.dropbox.fragment.UserDropboxFragment;
import com.foreveross.atwork.support.AtWorkFragmentManager;

/**
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@%((((((((#&@@@((#((#@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@%(((((((((((((%@*((((@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@%(((/,/(((((((((@@@*#@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@#(((@@@((((((((((@@((@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@(((((((((((((((((((((@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@(((((((((((((((((((@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@(((((((((((((((*@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 *                       __           __
 * .--.--.--.-----.----.|  |--.-----.|  |.--.--.-----.
 * |  |  |  |  _  |   _||    <|  _  ||  ||  |  |__ --|
 * |________|_____|__|  |__|__|   __||__||_____|_____|
 *                            |__|
 * Created by reyzhang22 on 16/9/7.
 */
public class DropboxActivity extends DropboxBaseActivity {

    public static Intent getIntent(Context context, Dropbox.SourceType sourceType, String sourceId, String domainId) {
        Intent intent = new Intent(context, DropboxActivity.class);
        intent.putExtra(DropboxBaseActivity.KEY_INTENT_SOURCE_TYPE, sourceType);
        intent.putExtra(DropboxBaseActivity.KEY_INTENT_SOURCE_ID, sourceId);
        intent.putExtra(DropboxBaseActivity.KEY_INTENT_DOMAIN_ID, domainId);
        intent.putExtra(DropboxBaseActivity.KEY_INTENT_TITLE, context.getString(R.string.my));
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initFragment();
        initListener();
        setBottomSelector(0);
        setTitleButtonStatus(false);
    }

    protected void initFragment() {
        mFragmentManager = new AtWorkFragmentManager(this, R.id.dropbox_layout);
        if (mMyDropboxFragment == null) {
            mMyDropboxFragment = new MyDropboxFileFragment();
        }
        mCurrentFragment = mMyDropboxFragment;
        mFragmentManager.addFragmentAndAdd2BackStack(mCurrentFragment, UserDropboxFragment.class.getSimpleName());
    }

    private void initListener() {
        mMyFileBtn.setOnClickListener(view -> {
            setBottomSelector(0);

            mCurrentFragment = mMyDropboxFragment;
            mFragmentManager.showFragment(mMyDropboxFragment, UserDropboxFragment.class.getSimpleName());

            mTitle.setText(getString(R.string.my));
            setTitleButtonStatus(false);

        });

        mOrgFileBtn.setOnClickListener(view -> {
            setBottomSelector(1);
            if (mOrgDropboxFragment == null) {
                mOrgDropboxFragment = new OrgsDropboxFragment();
            }
            mCurrentFragment = mOrgDropboxFragment;
            mFragmentManager.showFragment(mOrgDropboxFragment, OrgsDropboxFragment.class.getSimpleName());

            mTitle.setText(getString(R.string.org_file));
            setTitleButtonStatus(false);


        });
    }

    public void changeBottomByDir(boolean inDir) {
        mOrgSwitcherLayout.setVisibility(inDir ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        if (mCurrentFragment instanceof UserDropboxFragment) {
            if (!DisplayMode.Normal.equals(mCurrentDisplayMode)) {
                changeDisplayMode(DisplayMode.Normal);
                return;
            }
            if (!((UserDropboxFragment)mCurrentFragment).handleDropboxDirBackEvent()) {
                finish(true);
                return;
            }
            return;
        }
        finish(true);

    }


}
