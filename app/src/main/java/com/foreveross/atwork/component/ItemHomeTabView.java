package com.foreveross.atwork.component;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.DensityUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.utils.AtworkUtil;
import com.foreveross.atwork.utils.ImageCacheHelper;
import com.foreveross.atwork.utils.ImageViewUtil;
import com.foreveross.atwork.utils.ThemeResourceHelper;
import com.foreveross.theme.manager.SkinMaster;
import com.foreveross.theme.model.Theme;

/**
 * 首页面的自定义tab
 */
public class ItemHomeTabView extends RelativeLayout {

    //图标及文件
    private TextView mTvTab;
    //新消息数
    public NewMessageView mTvPlus;
    //红点
    public ImageView mNewMessage;

    private String mTextImage;

    private String mSelectedImage;

    private String mTextImageFromBeeworks;

    private String mSelectImageFromBeeworks;

    private int index;

    private String title;

    private Activity mActivity;

    private boolean mSelected;

    private String mTabId;

    private boolean mIsBeeWorksRes;

    public ItemHomeTabView(Activity activity) {
        super(activity);
        mActivity = activity;
        initView();
    }

    public ItemHomeTabView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public void setTabId(String tabId) {
        this.mTabId = tabId;
    }

    public String getTabId() {
        return mTabId;
    }

    public void setTitle(String title) {
        title = AtworkUtil.tempMakeI18n(title);
        mTvTab.setText(title);
    }


    public void updateTitleImageUI() {
        setTabUI(false);
    }


    public void updateSelectedTitleImageUI() {
        setTabUI(true);
    }

    private void setTabUI(boolean isSelected) {
        setTabText();

        setTabIcon(isSelected);
    }

    private void setTabIcon(boolean isSelected) {
        if (isTabNeedBeeWorksRes(isSelected)) {
            String resource;
            if (isSelected) {
                resource = mSelectImageFromBeeworks;

            } else {
                resource = mTextImageFromBeeworks;

            }

            setTabBeeworksIcon(resource);

        } else {
//            setSkinIcon(isSelected);

            setCommonIcon(isSelected);
        }

    }

    private void setCommonIcon(boolean isSelected) {
        int resource;
        if (isSelected) {
            resource = ImageViewUtil.getResourceInt(mSelectedImage);
        } else {
            resource = ImageViewUtil.getResourceInt(mTextImage);
        }

        try {
            mTvTab.setCompoundDrawablesWithIntrinsicBounds(0, resource, 0, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setSkinIcon(boolean isSelected) {
        String resource;
        if (isSelected) {
            resource = ThemeResourceHelper.getCurrentThemeResourcePath(mSelectedImage, true);
        } else {
            resource = ThemeResourceHelper.getCurrentThemeResourcePath(mTextImage, true);

        }
        setTabCommonIcon(resource);
    }

    private void setTabCommonIcon(final String resource) {
        if (resource.startsWith("assets") || resource.startsWith("file")) {
            int size = DensityUtil.dip2px(30);
            ImageCacheHelper.loadImage(resource, null, ImageCacheHelper.getDefaultImageOptions(false, true, true), new ImageCacheHelper.ImageLoadedListener() {
                @Override
                public void onImageLoadedComplete(Bitmap bitmap) {
                    BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), bitmap);
                    bitmapDrawable.setBounds(0, 0, size, size);

                    mTvTab.setCompoundDrawables(null, bitmapDrawable, null, null);
                }

                @Override
                public void onImageLoadedFail() {
                    setTabBeeworksIcon(resource);

                }
            });

        }
    }

    private void setTabBeeworksIcon(String resource) {

        Bitmap bitmap = ImageViewUtil.getImageFromAssetsFile(resource);
        if (bitmap != null) {
            int size = DensityUtil.dip2px(30);

            BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), bitmap);
            bitmapDrawable.setBounds(0, 0, size, size);

            mTvTab.setCompoundDrawables(null, bitmapDrawable, null, null);
            return;
        }

        int resId = ImageViewUtil.getResourceInt(resource);

        if (resId != -1) {
            Bitmap resourceBmp = BitmapFactory.decodeResource(getResources(), resId);

            mTvTab.setCompoundDrawablesWithIntrinsicBounds(null, new BitmapDrawable(getResources(), resourceBmp), null, null);
            return;
        }

        //todo beeworks这块 待优化
        ImageCacheHelper.loadImageByMediaNotNeedToken(resource, ImageCacheHelper.getBeeworksDefaultIconOptions(), new ImageCacheHelper.ImageLoadedListener() {
            @Override
            public void onImageLoadedComplete(Bitmap bitmap) {

                mTvTab.postDelayed(() -> {
                    BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), bitmap);
                    bitmapDrawable.setBounds(0, 0, DensityUtil.dip2px( 30), DensityUtil.dip2px(30));
                    mTvTab.setCompoundDrawables(null, bitmapDrawable, null, null);
                }, 20);
            }

            @Override
            public void onImageLoadedFail() {

            }
        });
    }


    private void setTabText() {
//        ColorStateList colorStateList = SkinMaster.getInstance().makeHomeTabSelector(SkinHelper.getTabActiveColor(), SkinHelper.getTabInactiveColor());
        int tabActiveColor = ContextCompat.getColor(BaseApplicationLike.baseContext, R.color.tab_active_color);
        int tabInactiveColor = ContextCompat.getColor(BaseApplicationLike.baseContext, R.color.tab_inactive_color);
        ColorStateList colorStateList = SkinMaster.getInstance().makeHomeTabSelector(tabActiveColor, tabInactiveColor);
        mTvTab.setTextColor(colorStateList);
    }


    public void setIndex(int index) {
        this.index = index;
    }

    private void initView() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_home_tab, this);
        mTvTab = view.findViewById(R.id.item_home_tab_title);
        mTvPlus = view.findViewById(R.id.item_home_tab_plus_view);
        mNewMessage = view.findViewById(R.id.newMessageTip);
        mNewMessage.setVisibility(View.GONE);

        mTvPlus.setVisibility(GONE);
        mTvTab.setText(title);
    }

    public void setNum(int num) {
        mTvPlus.setVisibility(VISIBLE);
        mTvPlus.setNum(num);
        mNewMessage.setVisibility(GONE);
    }

    public int getIndex() {
        return this.index;
    }


    public void refreshSelected() {
        setSelected(mSelected);
    }

    public void setSelected(boolean isSelected) {
        super.setSelected(isSelected);
        mSelected = isSelected;

        if (isSelected) {
            updateSelectedTitleImageUI();
        } else {
            updateTitleImageUI();
        }
    }

    public boolean isSelected() {
        return mSelected;
    }


    public void showNewMessage() {
        if (mNewMessage != null) {
            mNewMessage.setVisibility(View.VISIBLE);
            mTvPlus.setVisibility(GONE);
        }
    }

    public void showNothing() {
        mNewMessage.setVisibility(GONE);
        mTvPlus.setVisibility(GONE);
    }

    public void hideNewMessage() {
        if (mNewMessage != null) {
            mNewMessage.setVisibility(View.GONE);
        }
    }

    public void setTextImageResource(String image) {
        this.mTextImage = image;

    }


    public void setSelectedImageResource(String selectedImage) {
        this.mSelectedImage = selectedImage;
    }

    public void setTextImageResourceFromBeeworks(String image) {
        this.mTextImageFromBeeworks = image;
    }

    public void setSelectedImageResourceFromBeeworks(String selectedImage) {
        this.mSelectImageFromBeeworks = selectedImage;
    }

    public void setIsBeeWorksRes(boolean isBeeWorksRes) {
        this.mIsBeeWorksRes = isBeeWorksRes;
    }

    public Boolean hasBeeWorksRes(boolean isSelected) {
        if (isSelected) {
            return !StringUtils.isEmpty(mSelectImageFromBeeworks);

        } else {
            return !StringUtils.isEmpty(mTextImageFromBeeworks);

        }
    }

    public boolean isTabNeedBeeWorksRes(boolean isSelected) {
        Theme theme = SkinMaster.getInstance().getCurrentTheme();
        return null != theme && isTabNeedBeeWorksRes(theme, isSelected);
    }

    public boolean isTabNeedBeeWorksRes(@NonNull Theme theme, boolean isSelected) {
        return theme.mName.equalsIgnoreCase(AtworkConfig.DEFAULT_THEME.toString()) && hasBeeWorksRes(isSelected);
    }
}
