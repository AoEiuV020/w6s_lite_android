package com.foreveross.atwork.modules.aboutme.component;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.app.model.LightNoticeData;
import com.foreveross.atwork.component.WorkplusSwitchCompat;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.modules.aboutme.model.MeFunctionItem;
import com.foreveross.atwork.modules.common.component.LightNoticeItemView;
import com.foreveross.atwork.utils.ImageCacheHelper;

/**
 * Created by lingen on 15/11/16.
 */
public class MeFunctionsItemView extends RelativeLayout {

    public ImageView mIvIcon;
    public TextView mTVFunctionName;
    public TextView mTvNameRightest;
    public ImageView mIvArrow;
    public WorkplusSwitchCompat mSwitchButton;
    public View mVDaggerUp;
    public View mVDaggerBottom;
    public ProgressBar mProgressBar;

    public LightNoticeItemView mLightNoticeItemView;

    public MeFunctionsItemView(Context context) {
        super(context);
        initView();
    }

    private void initView() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_about_me_functions, this);
        mProgressBar = view.findViewById(R.id.me_progressbar);
        mTVFunctionName = view.findViewById(R.id.me_function_name);
        mTvNameRightest = view.findViewById(R.id.tv_name_rightest);
        mIvIcon = view.findViewById(R.id.about_me_function_icon);
        mLightNoticeItemView = view.findViewById(R.id.me_notice_view);
        mSwitchButton = view.findViewById(R.id.me_switcher_button);
//        mSwitchButton.setButtonStatusWithAnim(false);
        mIvArrow = view.findViewById(R.id.arrow_right);
        mVDaggerUp = view.findViewById(R.id.v_dagger_up);
        mVDaggerBottom = view.findViewById(R.id.v_dagger_bottom);
    }

    public void refreshLightNotice(LightNoticeData json) {
        if (null != json) {
            mLightNoticeItemView.refreshLightNotice(json);
        }

    }

    public void refreshItemView(MeFunctionItem meFunctionItem) {
        mTVFunctionName.setText(meFunctionItem.getTitle(BaseApplicationLike.baseContext));
        if (-1 != meFunctionItem.getIconRes()) {
            mIvIcon.setImageResource(meFunctionItem.getIconRes());
//            ImageCacheHelper.displayImage("drawable://" + meFunctionItem.getIconRes(), mIvIcon, ImageCacheHelper.getRectOptions(R.mipmap.loading_icon_size));

        } else if (!StringUtils.isEmpty(meFunctionItem.getIconMedia())) {
            ImageCacheHelper.displayImageByMediaId(meFunctionItem.getIconMedia(), mIvIcon, ImageCacheHelper.getRectOptions(R.mipmap.loading_icon_size));
        }


        if (meFunctionItem.mDaggerUp) {
            mVDaggerUp.setVisibility(VISIBLE);

        } else {
            mVDaggerUp.setVisibility(GONE);

        }

        if (StringUtils.isEmpty(meFunctionItem.getTextRightest())) {
            mTvNameRightest.setVisibility(GONE);
        } else {
            mTvNameRightest.setVisibility(VISIBLE);
            mTvNameRightest.setText(meFunctionItem.getTextRightest());

        }

        mProgressBar.setVisibility(View.GONE);
    }

    public void showDot() {
        mLightNoticeItemView.showDot();
    }

    public void showNothing() {
        mLightNoticeItemView.showNothing();
    }

    public void showSwitchButton(boolean isVpnItem, boolean showProgress) {
        if (!isVpnItem) {
            mSwitchButton.setVisibility(GONE);
            mIvArrow.setVisibility(GONE);
            return;
        }
        mIvArrow.setVisibility(GONE);
        if (showProgress) {
            mProgressBar.setVisibility(VISIBLE);
            mSwitchButton.setVisibility(GONE);
        } else {
            mProgressBar.setVisibility(GONE);
            mSwitchButton.setVisibility(VISIBLE);
        }

        boolean vpnShouldOpen = LoginUserInfo.getInstance().getVpnShouldOpen(getContext());
        mSwitchButton.setChecked(vpnShouldOpen);



    }
}
