package com.foreveross.atwork.modules.aboutatwork.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.fragment.app.Fragment;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ArticleItem;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ShareChatMessage;
import com.foreveross.atwork.manager.OrganizationManager;
import com.foreveross.atwork.modules.aboutatwork.fragment.OrgInviteShareFragment;
import com.foreveross.atwork.modules.web.component.WebSharePopupWindow;
import com.foreveross.atwork.support.SingleFragmentActivity;

/**
 * Created by dasunsy on 16/6/21.
 */
public class OrgInviteShareActivity extends SingleFragmentActivity {
    private OrgInviteShareFragment mFragment;

    private View mTransparentView;
    private View mLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();

    }

    private void initView() {
        mLayout = findViewById(R.id.fragmentContainer);
        mTransparentView = new View(this);
        mTransparentView.setBackgroundColor(Color.BLACK);
        mTransparentView.setAlpha(0.5f);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        addContentView(mTransparentView, lp);
        mTransparentView.setVisibility(View.GONE);
    }

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, OrgInviteShareActivity.class);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        mFragment = new OrgInviteShareFragment();
        return mFragment;
    }

    public void showShareDialog(ArticleItem articleItem) {
        OrganizationManager.getInstance().queryOrgLocalHavingCircle(this, (orgList) -> {

            WebSharePopupWindow webSharePopupWindow = WebSharePopupWindow.newBuilder()
                    .setContext(this)
                    .setFragment(mFragment)
                    .setOrgListHavingCircle(orgList)
                    .setArticleItem(articleItem)
                    .setShareType(ShareChatMessage.ShareType.OrgInviteBody)
                    .setNeedFetchInfoFromRemote(false)
                    .buildWebSharePopupWindow();

            webSharePopupWindow.setHookingFloatMode(false);
            webSharePopupWindow.setCommonModeExcludeRefresh();

//            webSharePopupWindow.showAtLocation(mLayout, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
            webSharePopupWindow.show(getSupportFragmentManager(), "webSharePopupWindow");
//            mTransparentView.setVisibility(View.VISIBLE);
//            webSharePopupWindow.setOnDismissListener(() -> mTransparentView.setVisibility(View.GONE));
        });

    }
}
