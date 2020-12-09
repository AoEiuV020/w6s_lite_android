package com.foreveross.atwork.modules.app.component;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.app.AppBundles;
import com.foreveross.atwork.modules.app.inter.OnAppItemClickEventListener;
import com.foreveross.atwork.modules.app.model.GroupAppItem;
import com.foreveross.atwork.modules.common.component.LightNoticeItemView;
import com.foreveross.atwork.utils.AppIconHelper;

/**
 * app item common view, 只包含 ui 处理
 */
public class AppItemCommonView extends RelativeLayout {

    private View mVIconRoot;
    private ImageView mAppIconView;

    private TextView mAppNameView;

    private ImageView mCustomView;

    private ImageView mIvBioAuthProtected;

    private GroupAppItem mGroupAppItem;

    private Activity mActivity;

    private LightNoticeItemView mNoticeView;

    private OnAppItemClickEventListener mOnAppItemClickEventListener;

    private boolean mCustomMode;

    private AppBundles mAppBundle;



    public AppItemCommonView(Activity context) {
        super(context);
        initView();
        registerListener();
        mActivity = context;
    }


    private void registerListener() {

        mCustomView.setOnClickListener(v -> {
            if(null != mOnAppItemClickEventListener) {
                mOnAppItemClickEventListener.onCustomModeClick(mAppBundle);
            }
        });


    }


    private void initView() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_grid_apps_child, this);
        mVIconRoot = view.findViewById(R.id.v_icon_bg);
        mAppIconView = view.findViewById(R.id.app_icon);
        mAppNameView = view.findViewById(R.id.app_name);
        mCustomView = view.findViewById(R.id.app_remove);
        mIvBioAuthProtected = view.findViewById(R.id.iv_bio_auth_protected);
        mNoticeView = view.findViewById(R.id.app_item_view);

        mNoticeView.setVisibility(GONE);

    }

    public void refreshView(GroupAppItem groupAppItem, final AppBundles appBundle, boolean customMode) {
        boolean avatarNeedLoading = isAvatarNeedLoading(appBundle);
        this.mCustomMode = customMode;
        this.mAppBundle = appBundle;
        this.mGroupAppItem = groupAppItem;

        mAppNameView.setText(appBundle.getTitleI18n(BaseApplicationLike.baseContext));

        mIvBioAuthProtected.setVisibility(GONE);


        if (customMode) {
            mCustomView.setVisibility(VISIBLE);

        } else {
            mCustomView.setVisibility(GONE);



        }


        AppIconHelper.setAppIcon(getContext(), appBundle, mAppIconView, avatarNeedLoading);

    }

    private boolean isAvatarNeedLoading(AppBundles appBundle) {
        boolean avatarNeedLoading = true;
        if (null != this.mAppBundle && this.mAppBundle.equals(appBundle)) {
            avatarNeedLoading = false;
        }
        return avatarNeedLoading;
    }

    public void setOnAppItemClickEventListener(OnAppItemClickEventListener onAppItemClickEventListener) {
        mOnAppItemClickEventListener = onAppItemClickEventListener;
    }

    public ImageView getCustomView() {
        return mCustomView;
    }

    public void showAppShadow() {
        mVIconRoot.setBackgroundResource(R.mipmap.icon_app_shadow_bg);
    }

    public void hideAppShadow() {
        mVIconRoot.setBackgroundResource(0);
    }

    public ImageView getAppIconView() {
        return mAppIconView;
    }

    public TextView getAppNameView() {
        return mAppNameView;
    }
}
