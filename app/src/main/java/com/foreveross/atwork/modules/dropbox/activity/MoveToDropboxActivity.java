package com.foreveross.atwork.modules.dropbox.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.model.discussion.Discussion;
import com.foreveross.atwork.infrastructure.model.dropbox.Dropbox;
import com.foreveross.atwork.infrastructure.model.orgization.Organization;
import com.foreveross.atwork.infrastructure.utils.CommonUtil;
import com.foreveross.atwork.modules.discussion.manager.DiscussionManager;
import com.foreveross.atwork.manager.OrganizationManager;
import com.foreveross.atwork.modules.dropbox.fragment.UserDropboxFragment;
import com.foreveross.atwork.support.AtWorkFragmentManager;

import java.util.ArrayList;

import static android.view.View.GONE;

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
 *                        __           __
 * .--.--.--.-----.----.|  |--.-----.|  |.--.--.-----.
 * |  |  |  |  _  |   _||    <|  _  ||  ||  |  |__ --|
 * |________|_____|__|  |__|__|   __||__||_____|_____|
 * |__|
 * Created by reyzhang22 on 2016/11/22.
 */

public class MoveToDropboxActivity extends DropboxBaseActivity  {

    public static final int REQUEST_CODE_MOVE_DROPBOX = 10030;

    public static void actionDropboxMove(Activity activity, String domainId, String sourceId, Dropbox.SourceType sourceType, String lastParentId, ArrayList<String> movedList) {
        Intent intent = new Intent(activity, MoveToDropboxActivity.class);
        intent.putExtra(KEY_INTENT_DOMAIN_ID, domainId);
        intent.putExtra(KEY_INTENT_SOURCE_ID, sourceId);
        intent.putExtra(KEY_INTENT_SOURCE_TYPE, sourceType);
        intent.putExtra(KEY_INTENT_MOVE_LAST_PARENT_ID, lastParentId);
        intent.putStringArrayListExtra(KEY_INTENT_MOVE_LIST, movedList);
        intent.putExtra(KEY_INTENT_MOVE_OR_COPY, true);
        setTitle(activity, intent, sourceType, sourceId);
        activity.startActivityForResult(intent, REQUEST_CODE_MOVE_DROPBOX);
    }

    private static void setTitle(Activity activity, Intent intent, Dropbox.SourceType sourceType, String sourceId) {
        String title = activity.getString(R.string.please_select_dir);
        String subTitle = "";
        if ( Dropbox.SourceType.Organization.equals(sourceType)) {
            Organization organization = OrganizationManager.getInstance().getOrganizationSyncByOrgCode(activity, sourceId);
            title = activity.getString(R.string.public_area);
            subTitle = organization.getNameI18n(activity);
        }
        if (Dropbox.SourceType.Discussion.equals(sourceType)) {
            Discussion discussion = DiscussionManager.getInstance().queryDiscussionSync(activity, sourceId);
            title = activity.getString(R.string.group_file2);
            subTitle = discussion.mName;
        }
        intent.putExtra(KEY_INTENT_TITLE, title);
        intent.putExtra(KEY_INTENT_SUB_TITLE, subTitle);
    }

    private Bundle mBundle;

    public void changeBackVisual(boolean showCancel){
        mCancelBtn.setVisibility(showCancel ? View.VISIBLE : GONE);
        mBackBtn.setVisibility(showCancel ? GONE : View.VISIBLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mBundle = getIntent().getExtras();
        super.onCreate(savedInstanceState);
        initFragment();
        initListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initView();
    }

    public void initView() {
        new Handler().postDelayed(() -> {
            mOrgSwitcherLayout.setVisibility(GONE);
            mIconLayout.setVisibility(GONE);
            changeTitleVisual();
            changeBottomVisual(mCurrentDisplayMode);
            changeBackVisual(true);
        }, 10);

        mCurrentDisplayMode = DisplayMode.Move;
    }


    private void initFragment() {
        mFragmentManager = new AtWorkFragmentManager(this, R.id.dropbox_layout);
        if (mUserDropboxFragment == null) {
            mUserDropboxFragment = new UserDropboxFragment();
            mUserDropboxFragment.setArguments(mBundle);
        }
        mCurrentFragment = mUserDropboxFragment;
        mFragmentManager.addFragmentAndAdd2BackStack(mCurrentFragment, UserDropboxFragment.class.getSimpleName());
    }

    private void initListener() {
        mMoveToBtn.setOnClickListener(view -> {

            if(CommonUtil.isFastClick(1500)) {
                return;
            }

            mUserDropboxFragment.onDropboxMove();
        });

        mCancelBtn.setOnClickListener(view -> {
            DropboxBaseActivity.refreshDropboxData();
            finish();
        });
        mBackBtn.setOnClickListener(view -> {
            onBackPressed();
        });
    }

    @Override
    public void onBackPressed() {
        if (!mUserDropboxFragment.handleDropboxDirBackEvent()) {
            DropboxBaseActivity.refreshDropboxData();
            finish();
            //界面回退动画
            this.overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
            return;
        }
        return;
    }

}
