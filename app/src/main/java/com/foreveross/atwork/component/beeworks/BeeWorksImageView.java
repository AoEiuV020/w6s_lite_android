package com.foreveross.atwork.component.beeworks;/**
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
 */


import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.beeworks.BeeWorksImages;
import com.foreveross.atwork.infrastructure.model.WebViewControlAction;
import com.foreveross.atwork.modules.app.activity.WebViewActivity;
import com.foreveross.atwork.utils.ImageCacheHelper;
import com.foreveross.atwork.utils.ImageViewUtil;

/**
 * Created by reyzhang22 on 16/3/14.
 */
public class BeeWorksImageView extends LinearLayout {

    private Context mContext;

    private ImageView mImageView;

    private BeeWorksImages mBeeWorksImages;

    public BeeWorksImageView(Context context) {
        super(context);
        mContext = context;
        initViews(context);
    }

    public BeeWorksImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initViews(context);
    }

    public BeeWorksImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initViews(context);

    }

    private void initViews(Context context) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_beeworks_imageview, this);
        mImageView = (ImageView)view.findViewById(R.id.beework_imageview);

    }

    private void registerListener() {
        this.setOnClickListener(v -> {
            if (mBeeWorksImages == null) {
                return;
            }
            if ("URL".equalsIgnoreCase(mBeeWorksImages.mActionType) || TextUtils.isEmpty(mBeeWorksImages.mActionType)) {
                if (TextUtils.isEmpty(mBeeWorksImages.mValue)) {
                    return;
                }
                boolean hideTitle = "FULL_SCREEN".equalsIgnoreCase(mBeeWorksImages.mDisplayMode);
                WebViewControlAction webViewControlAction = WebViewControlAction.newAction()
                                                                                .setUrl(mBeeWorksImages.mValue)
                                                                                .setTitle(mBeeWorksImages.mTitle)
                                                                                .setHideTitle(hideTitle)
                                                                                .setNeedShare(false);
                Intent intent = WebViewActivity.getIntent(mContext,  webViewControlAction);
                mContext.startActivity(intent);
            }
        });
    }

    public void setImage(BeeWorksImages image) {
        mBeeWorksImages = image;
        registerListener();
        //首先先查本地
        if (TextUtils.isEmpty(image.mIcon)) {
            int resId = ImageViewUtil.getResourceInt("_" + image.mIcon.toLowerCase());
            if (resId != -1) {
                mImageView.setImageResource(resId);
                return;
            }
        }

        ImageCacheHelper.displayImageByMediaIdNotNeedToken(image.mIcon, mImageView, ImageCacheHelper.getBeeworksDefaultImageOptions(), null);
    }
}
