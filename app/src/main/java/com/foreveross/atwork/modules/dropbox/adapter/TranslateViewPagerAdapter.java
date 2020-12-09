package com.foreveross.atwork.modules.dropbox.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.viewpager.widget.PagerAdapter;

import com.foreveross.atwork.utils.ImageCacheHelper;

import java.util.ArrayList;
import java.util.List;

import uk.co.senab.photoview.PhotoView;

/**
 * Created by reyzhang22 on 17/4/25.
 */

public class TranslateViewPagerAdapter extends PagerAdapter {

    private List<PhotoView> mImageViews;

    private List<String> mTranslateList;

    private Context mContext;

    public TranslateViewPagerAdapter(Context context, List<String> translateList) {
        mContext = context;
        mTranslateList = translateList;
        init();
    }

    public void setList(List<String> translateList) {
        for (int i = 0; i < translateList.size(); i++) {
            String id = translateList.get(i);
            if (!mTranslateList.contains(id)) {
                mTranslateList.add(id);
                PhotoView photoView = new PhotoView(mContext);
                mImageViews.add(photoView);
            }

        }
        notifyDataSetChanged();
    }

    private void init() {
        mImageViews = new ArrayList<>();
        for (int i = 0; i < mTranslateList.size(); i++) {
            PhotoView photoView = new PhotoView(mContext);
            mImageViews.add(photoView);
        }
    }

    @Override
    public int getCount() {
        return mTranslateList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        PhotoView photoView  = mImageViews.get(position);
        ImageCacheHelper.displayImageByMediaId(mTranslateList.get(position), photoView, ImageCacheHelper.getDropboxImageOptions());
        container.addView(mImageViews.get(position));
        return photoView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

        container.removeView(mImageViews.get(position));
    }
}
