package com.foreveross.atwork.modules.app.component;/**
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
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ __           __
 * .--.--.--.-----.----.|  |--.-----.|  |.--.--.-----.
 * |  |  |  |  _  |   _||    <|  _  ||  ||  |  |__ --|
 * |________|_____|__|  |__|__|   __||__||_____|_____|
 * |__|
 */


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.foreveross.atwork.R;
import com.foreveross.atwork.component.popview.PopUpView;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.beeworks.BeeWorksNavigation;
import com.foreveross.atwork.infrastructure.beeworks.BeeworksNaviActionContent;
import com.foreveross.atwork.infrastructure.beeworks.BeeworksNaviBaseAction;
import com.foreveross.atwork.infrastructure.model.WebViewControlAction;
import com.foreveross.atwork.infrastructure.utils.BitmapUtil;
import com.foreveross.atwork.infrastructure.utils.DensityUtil;
import com.foreveross.atwork.infrastructure.utils.DeviceUtil;
import com.foreveross.atwork.infrastructure.utils.Logger;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.ViewCompat;
import com.foreveross.atwork.infrastructure.utils.ViewUtil;
import com.foreveross.atwork.infrastructure.utils.encryption.Base64Util;
import com.foreveross.atwork.modules.app.activity.WebViewActivity;
import com.foreveross.atwork.modules.app.model.WebRightButton;
import com.foreveross.atwork.utils.AtworkUtil;
import com.foreveross.atwork.utils.ImageCacheHelper;
import com.foreveross.atwork.utils.ImageViewUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * 网页头部右边自定义按钮
 * Created by reyzhang22 on 16/3/3.
 */
public class WebTitleBarRightButtonView extends LinearLayout {


    private FrameLayout mViewRight1;

    private FrameLayout mViewRight2;

    private FrameLayout mViewRight3;

    private Context mContext;

    private View mView;

    private boolean mIsFromBeeworks = false;

    private OnActionListener mListener;

    private Boolean mIsLightApp = false;

    private int mFlag = 0;

    private int[] mLargeIcons = new int[]{
            R.mipmap.icon_web_add_large, R.mipmap.icon_web_create_group_large, R.mipmap.icon_web_edit_large,
            R.mipmap.icon_web_more_large, R.mipmap.icon_web_qr_large, R.mipmap.icon_web_refresh_large,
            R.mipmap.icon_web_search_large, R.mipmap.icon_web_setting_large, R.mipmap.icon_web_share_large,
            R.mipmap.icon_back_white, R.mipmap.icon_unknow_large, R.mipmap.icon_forward_large, R.mipmap.icon_select_large,
            R.mipmap.icon_delete_large, R.mipmap.icon_file_large, R.mipmap.icon_photo_large, R.mipmap.icon_star_large,
            R.mipmap.icon_more_circle_large
    };

    public WebTitleBarRightButtonView(Context context) {
        super(context);
        initViews(context);
    }

    public WebTitleBarRightButtonView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews(context);
    }

    public WebTitleBarRightButtonView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews(context);
    }

    public void setScriptActionListener(OnActionListener listener) {
        mListener = listener;
    }

    public List<List<WebRightButton>> setupWebRightButtonsFromJson(JSONArray jsonArray, OnActionListener listener) {
        mIsFromBeeworks = false;
        Logger.e("button", "parse start:" + System.currentTimeMillis());
        List<List<WebRightButton>> rightButtons = new ArrayList<>();
        mListener = listener;

        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONArray iconGroups = jsonArray.getJSONArray(i);
                if (iconGroups == null) {
                    continue;
                }
                List<WebRightButton> subButtons = new ArrayList<>();
                for (int j = 0; j < iconGroups.length(); j++) {
                    JSONObject subJson = iconGroups.getJSONObject(j);
                    if (subJson == null) {
                        continue;
                    }
                    WebRightButton button = new WebRightButton();
                    button.mAction = WebRightButton.Action.fromString(subJson.getString("action"));
                    button.mIcon = subJson.getString("icon");
                    button.mActionValue = subJson.getString("value");
                    button.mTitle = subJson.getString("title");
                    button.mDisable = subJson.optBoolean("disable");
                    subButtons.add(button);

                }
                rightButtons.add(subButtons);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Logger.e("button", "parse end:" + System.currentTimeMillis());
        return rightButtons;
    }

    public List<List<WebRightButton>> getWebRightButtonsFromNavi(BeeWorksNavigation navigation) {
        mIsFromBeeworks = true;
        List<List<WebRightButton>> rightButtons = new ArrayList<>();
        for (int i = 0; i < navigation.mRightActions.size(); i++) {
            BeeworksNaviBaseAction groups = navigation.mRightActions.get(i);
            if (groups == null) {
                continue;
            }
            List<WebRightButton> subButtons = getSubButtons(groups);
            rightButtons.add(subButtons);
        }
        return rightButtons;
    }

    public List<WebRightButton> getSubButtons(BeeworksNaviBaseAction groups) {
        mIsFromBeeworks = true;
        List<WebRightButton> subButtons = new ArrayList<>();
        for (int j = 0; j < groups.mContents.size(); j++) {
            BeeworksNaviActionContent content = groups.mContents.get(j);
            if (content == null) {
                continue;
            }
            subButtons.add(getSubButton(content, groups.mType));
        }
        return subButtons;
    }

    public WebRightButton getSubButton(BeeworksNaviActionContent content, String type) {
        mIsFromBeeworks = true;
        WebRightButton button = new WebRightButton();
        button.mAction = WebRightButton.Action.fromString(content.mAction);
        button.mIcon = content.mIcon;
        button.mActionValue = content.mValue;
        button.mTitle = content.mTitle;
        button.mFontColor = content.mFontColor;
        button.mType = type;
        return button;
    }

    private void initViews(Context context) {
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(R.layout.view_web_titlebar_right, this);
        findView();
    }

    private void findView() {
        mViewRight1 = mView.findViewById(R.id.button_stub1);
        mViewRight2 = mView.findViewById(R.id.button_stub2);
        mViewRight3 = mView.findViewById(R.id.button_stub3);
    }
    public void isLightApp(Boolean isLightApp){
        mIsLightApp = isLightApp;
    }
    public void setFlag(int flag){
        mFlag = flag;
    }

    public void setWebRightButton(List<List<WebRightButton>> mButtons) {
        if (mButtons == null) {
            return;
        }
        if (mButtons.size() == 0) {
            resetShowUp();
            return;
        }
        int i = 0;
        for (List<WebRightButton> buttonList : mButtons) {
            showUpView(i, buttonList);
            i++;
        }
    }

    /**
     * 隐藏所有按钮
     */
    public void resetShowUp() {

        if (mViewRight1 != null) {
            resetChildViews(mViewRight1);
            mViewRight1.setVisibility(View.GONE);
        }
        if (mViewRight2 != null) {
            resetChildViews(mViewRight2);
            mViewRight2.setVisibility(View.GONE);
        }
        if (mViewRight3 != null) {
            resetChildViews(mViewRight3);
            mViewRight3.setVisibility(View.GONE);
        }

    }

    private void resetChildViews(FrameLayout view) {
        if (view == null) {
            return;
        }
        for (int i = 0; i < view.getChildCount(); i++) {
            View childView = view.getChildAt(i);
            if (childView == null) {
                continue;
            }
            if (childView instanceof ImageView) {
                ((ImageView) childView).setImageDrawable(new ColorDrawable(Color.TRANSPARENT));
            }
            if (childView instanceof TextView) {
                ((TextView) childView).setText("");
            }
        }
    }


    private void showUpView(int viewId, List<WebRightButton> subButton) {
        switch (viewId) {
            case 0:
                mViewRight3.setVisibility(View.VISIBLE);
                setUpButton(mViewRight3, subButton);

                break;

            case 1:

                mViewRight2.setVisibility(View.VISIBLE);
                setUpButton(mViewRight2, subButton);
                break;

            case 2:
                mViewRight1.setVisibility(View.VISIBLE);
                setUpButton(mViewRight1, subButton);

                break;
        }
    }

    private void setUpButton(final View parentView, final List<WebRightButton> buttons) {

        if (buttons == null) {
            return;
        }
        if (buttons.isEmpty()) {
            return;
        }
        ImageView ivIcon = parentView.findViewById(R.id.button_group_image);
        TextView tvText = parentView.findViewById(R.id.button_group_text);
        int i = 0;

        final List<WebRightButton> subButtons = new ArrayList<>();
        for (final WebRightButton button : buttons) {
            if (i == 0) {
                boolean failed = initRightBtn(parentView, ivIcon, tvText, button);
                if (failed)
                    return;

                i++;
                continue;
            }
            subButtons.add(button);
            i++;
        }

        if (subButtons.isEmpty()) {
            return;
        }

        ivIcon.setOnClickListener(v -> initPopupSubButton(parentView, subButtons));
        tvText.setOnClickListener(v -> initPopupSubButton(parentView, subButtons));
    }

    private boolean initRightBtn(View parentView, ImageView ivIcon, TextView tvText, WebRightButton button) {
        inflateTitleButton(parentView, ivIcon, tvText, button);
        if (button.mDisable) {
            ViewCompat.setAlpha(ivIcon, 0.5f);
            ViewCompat.setAlpha(tvText, 0.5f);
            ivIcon.setClickable(false);
            tvText.setClickable(false);
            parentView.setClickable(false);

            return true;

        } else {
            ViewCompat.setAlpha(ivIcon, 1f);
            ViewCompat.setAlpha(tvText, 1f);
            ivIcon.setClickable(true);
            tvText.setClickable(true);
            parentView.setClickable(true);
        }
        if (button.mAction == WebRightButton.Action.JS) {
            ivIcon.setOnClickListener(v -> doJsAction(button.mActionValue));
            tvText.setOnClickListener(v -> doJsAction(button.mActionValue));
        }

        if (button.mAction == WebRightButton.Action.System) {
            ivIcon.setOnClickListener(v -> doSystemAction(button.mActionValue));
            tvText.setOnClickListener(v -> doSystemAction(button.mActionValue));
        }

        if (button.mAction == WebRightButton.Action.Url || button.mAction == WebRightButton.Action.Unknown) {
            ivIcon.setOnClickListener(v -> doUrlAction(button));
            tvText.setOnClickListener(v -> doUrlAction(button));
        }
        return false;
    }

    //有文字优先显示文字
    private void inflateTitleButton(View parentView, ImageView ivIcon, TextView tvText, WebRightButton button) {

        if (mIsFromBeeworks && "showTitle".equalsIgnoreCase(button.mType)) {
            if (TextUtils.isEmpty(button.mTitle)) {
                tvText.setVisibility(View.GONE);
                return;
            }
            if (!TextUtils.isEmpty(button.mFontColor)) {
                tvText.setTextColor(Color.parseColor(button.mFontColor));
            }
            ivIcon.setVisibility(View.GONE);
            tvText.setVisibility(View.VISIBLE);

            tvText.setText(button.mTitle);
            tempMakeTextCompatible(parentView, tvText);
            return;
        }

        if(!mIsFromBeeworks) {
            if(!StringUtils.isEmpty(button.mTitle)) {
                ivIcon.setVisibility(View.GONE);
                tvText.setVisibility(View.VISIBLE);

                tvText.setText(button.mTitle);
                tempMakeTextCompatible(parentView, tvText);
                return;
            }
        }

        inflateIconButton(parentView, ivIcon, tvText, button);


    }

    private void tempMakeTextCompatible(View parentView, TextView tvText) {
        if(DeviceUtil.isSamsung_GT_N7100()) {
            parentView.setMinimumWidth(ViewUtil.getTextLength(tvText) + 50);
        }
    }

    private void tempMakeIconCompatible(View parentView, Bitmap bitmap) {
        if(DeviceUtil.isSamsung_GT_N7100()) {
            parentView.setMinimumWidth(bitmap.getWidth() + 50);
        }
    }

    private void inflateIconButton(View parentView, ImageView iconView, TextView textView, WebRightButton button) {
        if (TextUtils.isEmpty(button.mIcon)) {
            iconView.setVisibility(View.GONE);
            return;
        }

        if(button.mIcon.startsWith("base64:")) {
            String iconBase64 = button.mIcon.substring("base64:".length());
            byte[] bmpByte = Base64Util.decode(iconBase64);

            if(0 != bmpByte.length) {
                textView.setVisibility(View.GONE);
                iconView.setVisibility(View.VISIBLE);
                Bitmap bitmap = BitmapUtil.Bytes2Bitmap(bmpByte);

                bitmap = BitmapUtil.setMaxHeight(bitmap, (int) (DensityUtil.dip2px(48) * 0.6));

                iconView.setImageBitmap(bitmap);
                tempMakeIconCompatible(iconView, bitmap);

            } else {
                handleTitleView(button, textView, iconView);
            }

            return;

        }

        int resId = -1;
        try {
            //先从已知icons找
            int pos = Integer.valueOf(button.mIcon);
            if (0 <= pos && pos < 18) {
                setIcon(parentView, iconView, textView, mLargeIcons[pos]);
                return;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        resId = ImageViewUtil.getResourceInt(button.mIcon);

        if (resId != -1) {
            setIcon(parentView, iconView, textView, resId);
            return;
        }

        if (mIsFromBeeworks) {
            //本地没有，可能是beeworks的，需要加上 _
            resId = ImageViewUtil.getResourceInt("_" + button.mIcon.toLowerCase());
            if (resId != -1) {
                setIcon(parentView, iconView, textView, resId);
                return;
            }

            inflateRemoteIcon(iconView, textView, button);

        } else {
            //default
            setIcon(parentView, iconView, textView, mLargeIcons[0]);


        }

    }

    private void setIcon(View parentView, ImageView iconView, TextView textView, int iconRes) {
        textView.setVisibility(View.GONE);
        iconView.setVisibility(View.VISIBLE);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), iconRes);
        iconView.setImageBitmap(bitmap);

        tempMakeIconCompatible(parentView, bitmap);

        AtworkUtil.tempHandleIconColor(iconView);
    }


    private void inflateRemoteIcon(ImageView iconView, TextView textView, WebRightButton button) {
        //如果本地没有，去查一下网络
        ImageCacheHelper.displayImageByMediaRes(button.mIcon, iconView, ImageCacheHelper.getDefaultOptions(), new ImageCacheHelper.ImageLoadedListener() {
            @Override
            public void onImageLoadedComplete(Bitmap bitmap) {
                if (bitmap != null) {
                    textView.setVisibility(View.GONE);
                    iconView.setVisibility(View.VISIBLE);

                    iconView.setImageBitmap(bitmap);
                    tempMakeIconCompatible(iconView, bitmap);

                }
            }

            @Override
            public void onImageLoadedFail() {
                handleTitleView(button, textView, iconView);
            }
        });
    }

    private void handleTitleView(WebRightButton button, TextView textView, ImageView iconView) {
        if (!TextUtils.isEmpty(button.mTitle)) {
            if (!TextUtils.isEmpty(button.mFontColor)) {
                textView.setTextColor(Color.parseColor(button.mFontColor));
            }
            iconView.setVisibility(View.GONE);
            textView.setVisibility(View.VISIBLE);
            textView.setText(button.mTitle);
        }
    }

    //初始化PopUpView，为PopUpView添加按钮和点击事件
    private void initPopupSubButton(final View parentView, final List<WebRightButton> subButtons) {

        PopUpView popUpView = new PopUpView(mContext);
        int i = 0;

        if(mIsLightApp && mFlag == 1){
            popUpView.addPopItem(R.mipmap.icon_phone_single, R.string.refresh, 0);
            popUpView.addPopItem(R.mipmap.icon_info, R.string.app_info, 1);
            i = 2;
        }

        for (WebRightButton button : subButtons) {
            int resId = -1;
            if (!button.mIcon.startsWith("base64:")) {
                try {
                    //先从已知icons找
                    int pos = Integer.valueOf(button.mIcon);
                    if (0 <= pos && pos < 17) {
                        resId = mLargeIcons[pos];
                    } else {
                        resId = ImageViewUtil.getResourceInt(button.mIcon);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                //非 beeworks 时, 找不到 icon 使用默认的第一个
                if (!mIsFromBeeworks) {
                    if(-1 == resId) {
                        resId = mLargeIcons[0];
                    }
                }
            }

            popUpView.addPopItem(resId, button.mIcon, button.mTitle, i);
            i++;
        }

        popUpView.setPopItemOnClickListener((title, pos) -> {

            if (title.equals(getResources().getString(R.string.refresh))) {
                doSystemAction("reload");
                popUpView.dismiss();
                return;
            }

            if (title.equals(getResources().getString(R.string.app_info))) {
                doSystemAction("app_info");
                popUpView.dismiss();
                return;
            }
            WebRightButton webButtonData = subButtons.get(pos);
            if(mIsLightApp && mFlag == 1){
                 webButtonData = subButtons.get(pos-2);
            }

            if (webButtonData == null) {
                return;
            }

            if(webButtonData.mDisable) {
                return;
            }

            if (webButtonData.mAction == WebRightButton.Action.JS) {
                doJsAction(webButtonData.mActionValue);
                popUpView.dismiss();

            }
            if (webButtonData.mAction == WebRightButton.Action.Url) {
                doUrlAction(webButtonData);
                popUpView.dismiss();
            }

            if (webButtonData.mAction == WebRightButton.Action.System) {
                doSystemAction(webButtonData.mActionValue);
                popUpView.dismiss();
            }
        });
        popUpView.pop(parentView);
    }

    private void doJsAction(String action) {
        if (mListener == null) {
            return;
        }
        mListener.onScriptAction(action);
    }

    private void doUrlAction(WebRightButton button) {
        WebViewControlAction webViewControlAction = WebViewControlAction.newAction()
                                                                        .setUrl(button.mActionValue)
                                                                        .setTitle(button.mTitle)
                                                                        .setHideTitle(!mIsFromBeeworks);
        Intent intent = WebViewActivity.getIntent(mContext, webViewControlAction);
        mContext.startActivity(intent);
    }

    private void doSystemAction(String action) {
        if (mListener == null) {
            return;
        }

        mListener.onSystemAction(action);
    }

    public void lockButton(boolean isLock) {
        mViewRight1.setClickable(isLock);
        mViewRight2.setClickable(isLock);
        mViewRight3.setClickable(isLock);

        lockView(mViewRight1, isLock);
        lockView(mViewRight2, isLock);
        lockView(mViewRight3, isLock);
    }

    public void initDefaultRightBtn(OnActionListener listener, boolean lightTheme) {
        setVisibility(VISIBLE);
        mViewRight3.setVisibility(VISIBLE);
        ImageView ivIcon = mViewRight3.findViewById(R.id.button_group_image);
        TextView tvText = mViewRight3.findViewById(R.id.button_group_text);
        tvText.setVisibility(GONE);

        int iconMore;
        if (lightTheme) {
            iconMore = R.mipmap.icon_more_white;

        } else {
            iconMore = R.mipmap.icon_more_dark;

        }
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), iconMore);
        ivIcon.setImageBitmap(bitmap);

        tempMakeIconCompatible(mViewRight3, bitmap);

        mViewRight3.setOnClickListener(v -> listener.onSystemAction("pop_default"));
    }

    private void lockView(FrameLayout view, boolean isLock) {
        if (view == null) {
            return;
        }
        for (int i = 0; i < view.getChildCount(); i++) {
            View childView = view.getChildAt(i);
            if (childView == null) {
                continue;
            }
            childView.setClickable(isLock);
        }
    }


    public interface OnActionListener {
        void onScriptAction(String action);

        void onSystemAction(String action);
    }


}
