package com.foreverht.webview;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.core.graphics.ColorUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.foreveross.atwork.infrastructure.model.app.AppBundles;
import com.foreveross.atwork.infrastructure.model.app.LightApp;
import com.foreveross.atwork.infrastructure.model.app.appEnum.BannerType;
import com.foreveross.atwork.infrastructure.model.app.appEnum.ProgressBarType;
import com.foreveross.atwork.infrastructure.model.user.LoginUserBasic;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.CustomerHelper;
import com.foreveross.atwork.infrastructure.utils.ViewUtil;
import com.foreveross.atwork.infrastructure.utils.language.LanguageUtil;
import com.foreveross.atwork.infrastructure.utils.statusbar.StatusBarUtil;
import com.foreveross.atwork.infrastructure.webview.WorkplusDownloadListener;
import com.foreveross.theme.manager.SkinHelper;
import com.foreveross.watermark.core.DrawWaterMark;
import com.foreveross.watermark.core.WaterMarkUtil;

import java.util.Locale;
import java.util.Map;

/**
 * Created by dasunsy on 2017/10/27.
 */

public abstract class WebviewCoreFragment extends CordovaEngineFragment implements WorkplusDownloadListener {



    private static final String ORG_ID_KEY = "org_id_";

    private OnWebViewFragmentCreate mFragmentCreateListener;

    private Locale mLastLocale;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLastLocale = LanguageUtil.getLocale(getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();

        handleViewWillAppear();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mFragmentCreateListener != null) {
            mFragmentCreateListener.onFragmentCreate();
        }

        handleOverallWatermark();
    }


    protected void handleOverallWatermark() {
        if (CustomerHelper.isHighSecurity(mActivity)) {
            View watermarkView = getWatermarkView();
            watermarkView.setVisibility(View.VISIBLE);
            WaterMarkUtil.setLoginUserWatermark(mActivity, watermarkView, "");

        }
    }


    @Override
    public void setWatermark(boolean show, String name, String num, String textColor, int textSize, int paddingTop, double alpha, String addValue) {
        View watermarkView = getWatermarkView();
        watermarkView.setVisibility(View.VISIBLE);
        int color = -1;
        try {
            color = Color.parseColor(textColor);
        } catch (Exception e) {
            e.printStackTrace();
        }
        DrawWaterMark drawWaterMark = new DrawWaterMark(name, num, -1, color, textSize, paddingTop, alpha, addValue);
        WaterMarkUtil.setWaterMark(getActivity(), watermarkView, drawWaterMark);
    }


    @Override
    public void showWatermark(boolean show) {
        getWatermarkView().setVisibility(show ? View.VISIBLE : View.GONE);
    }

    public View getTitleView() {
        return null;
    }

    public TextView getTitleTv() {
        return null;
    }


    @Override
    public void setFragmentCreateListener(OnWebViewFragmentCreate listener) {
        mFragmentCreateListener = listener;
    }


    @Override
    public String getNavigationColor() {
        return GetNavigationColorJs.JS;
    }

    @Override
    protected boolean isNeedHandled(String url) {
        return url != null && url.contains("{{username}}") || null != url && url.contains("{{username/name}}");
    }

    @Override
    protected String handleWebFormUrl(String url) {
        LoginUserBasic user = LoginUserInfo.getInstance().getLoginUserBasic(mActivity);
        if (url != null && url.contains("{{username}}")) {

            url = url.replace("{{username}}", user.mUsername);
            return url;
        }
        if (null != url && url.contains("{{username/name}}")) {
            url = url.replace("{{username/name}}", user.mUsername + "/"
                    + user.mName);

        }
        return url;
    }

    /**
     * 解析url中的org_id_
     * 如果url没有，则赋值为当前组织的org_id;
     *
     * @param urlParams
     * @return
     */
    @Override
    protected String parseParams(Map<String, String> urlParams) {
        if (urlParams != null && urlParams.containsKey(ORG_ID_KEY)) {
            Log.e("LoadUrl", "from_url ::" + urlParams.get(ORG_ID_KEY) + "");
            return urlParams.get(ORG_ID_KEY);
        }
//        String currentOrgCode = PersonalShareInfo.getInstance().getCurrentOrg(mActivity);
//        Log.e("LoadUrl", "current  ::" + currentOrgCode);
        return null;
    }

    @Override
    protected void refreshProgressBarColor(AppBundles appBundle) {
        try {

            int parseColor = SkinHelper.getMainColor();
            parseColor = ColorUtils.setAlphaComponent(parseColor, 150);

            if (null != appBundle) {
                if (ProgressBarType.CUSTOM.equals(appBundle.mSettings.mMobileBehaviour.mProgressBarType)) {

                    parseColor = Color.parseColor(appBundle.mSettings.mMobileBehaviour.mProgressBarColor);

                } else if (ProgressBarType.DEFAULT.equals(appBundle.mSettings.mMobileBehaviour.mProgressBarType)) {

                    if (BannerType.CUSTOM_COLOR.equals(appBundle.mSettings.mMobileBehaviour.mBannerType)) {
                        parseColor = Color.parseColor(appBundle.mSettings.mMobileBehaviour.mBannerProp);
                        parseColor = ColorUtils.setAlphaComponent(parseColor, 150);

                    }
                }
            }

            setProgressColor(parseColor);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setProgressColor(int color) {
        LayerDrawable layerDrawable = (LayerDrawable) getResources().getDrawable(R.drawable.progressbar_webview_loading);
        ClipDrawable clipDrawable = (ClipDrawable) layerDrawable.findDrawableByLayerId(android.R.id.progress);
        clipDrawable.setColorFilter(color, PorterDuff.Mode.SRC);
        getProgressBarLoading().setProgressDrawable(layerDrawable);
    }


    /**
     * webview 全屏时statusbar 的特性只在 API19以上支持
     * */
    @Override
    protected void initVFakeStatusBar(boolean needHideTitle) {

        if(!needHideTitle) {
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            ViewUtil.setHeight(getVFakeStatusBar(), StatusBarUtil.getStatusBarHeight(getActivity()));
            makeKeyboardCompatible();
        }
    }

    @Override
    protected void checkClear(View webview, boolean forcedClear) {

        if(forcedClear) {
            WebkitSdkUtil.clearResourceCache(webview);
            WebkitSdkUtil.clearCookies();
            return;
        }

        FragmentActivity activity = getActivity();
        if (LoginUserInfo.getInstance().isUserNeedClearWebview(activity)) {
            WebkitSdkUtil.clearResourceCache(webview);

            LoginUserInfo.getInstance().setUserNeedClearWebview(activity, false);

        }
    }


    protected void handleViewWillAppear() {
        if(AtworkConfig.WEBVIEW_CONFIG.isCommandMainWebviewAppearCallback()) {
            loadJS("viewWillAppear()");
        }
    }
}
