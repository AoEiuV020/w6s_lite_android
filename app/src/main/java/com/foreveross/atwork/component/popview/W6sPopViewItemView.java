package com.foreveross.atwork.component.popview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.utils.BitmapUtil;
import com.foreveross.atwork.infrastructure.utils.DensityUtil;
import com.foreveross.atwork.infrastructure.utils.encryption.Base64Util;
import com.foreveross.atwork.utils.ImageCacheHelper;
import com.foreveross.atwork.utils.ImageViewUtil;

/**
 * Created by wuzejie on 19/9/16.
 * Description:
 * Pop View Item
 */
public class W6sPopViewItemView extends LinearLayout {

    private ImageView mIvIcon;

    private TextView mTvTitle;

    public View mVLine;

    private int mIconResId;

    private int mTitleResId;


    public W6sPopViewItemView(Context context) {
        super(context);
        initView();
    }

    public W6sPopViewItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
        initAttrs(context, attrs);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs,
                R.styleable.PopViewItemView);
        mIconResId = typedArray.getResourceId(R.styleable.PopViewItemView_popIcon, 0);
        mTitleResId = typedArray.getResourceId(R.styleable.PopViewItemView_popTitle, 0);

        if (mIconResId != 0 && mTitleResId != 0) {
            setItem(mIconResId, mTitleResId);
        }

        typedArray.recycle();
    }

    private void initView() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.component_w6s_pop_view_item, this);
        mIvIcon = view.findViewById(R.id.pop_view_icon);
        mTvTitle = view.findViewById(R.id.pop_view_title);
        mVLine = view.findViewById(R.id.line_pop_view);


    }


    public void setItem(int iconResId, int titleResId) {
        mIvIcon.setImageResource(iconResId);
        String str = getResources().getString(titleResId);
        mTvTitle.setText(str);
    }

    public void setItem(int iconResId, String titleStr) {
        mIvIcon.setImageResource(iconResId);
        mTvTitle.setText(titleStr);
    }

    public void setItem(int iconResId, String resName, String titleStr) {
        if (iconResId == -1) {
            //本地没有，可能是beeworks的，需要加上p_
            iconResId = ImageViewUtil.getResourceInt("_" + resName.toLowerCase());
            if (iconResId != -1) {
                mIvIcon.setImageResource(iconResId);
                mTvTitle.setText(titleStr);

                return;
            }


            if(resName.startsWith("base64:")) {
                String iconBase64 = resName.substring("base64:".length());
                byte[] bmpByte = Base64Util.decode(iconBase64);

                if(0 != bmpByte.length) {
                    setBitmapBase64(bmpByte);

                    mTvTitle.setText(titleStr);

                    return;
                }

            }


            //如果本地没有，去查一下网络
            ImageCacheHelper.displayImageByMediaRes(resName, mIvIcon, ImageCacheHelper.getImageOptions(), new ImageCacheHelper.ImageLoadedListener() {
                @Override
                public void onImageLoadedComplete(Bitmap bitmap) {
                    mIvIcon.setImageBitmap(bitmap);
                }

                @Override
                public void onImageLoadedFail() {

                }
            });
        }
        mTvTitle.setText(titleStr);
    }

    private void setBitmapBase64(byte[] bmpByte) {
        Bitmap bitmap = BitmapUtil.Bytes2Bitmap(bmpByte);
        if (null != bitmap) {
            int length = (int) (DensityUtil.dip2px(36) * 0.83);
            if(90 < length) {
                length = 90;
            }
            mIvIcon.setImageBitmap(Bitmap.createScaledBitmap(bitmap, length, length, false));
        }
    }

    public void setIcon(Bitmap bitmap) {
        mIvIcon.setImageBitmap(bitmap);
    }
    public void setTitle(String titleStr) {
        mTvTitle.setText(titleStr);
    }

    public String getTitle() {
        return mTvTitle.getText().toString();
    }

}
