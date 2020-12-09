package com.foreveross.atwork.modules.image.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.foreveross.atwork.infrastructure.model.file.MediaItem;
import com.foreveross.atwork.modules.image.component.MediaSelectItemView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ReyZhang on 2015/4/28.
 */
public class MediaSelectAdapter extends BaseAdapter {


    private static final String TAG = MediaSelectAdapter.class.getSimpleName();

    private Context mContext;

    private List<MediaItem> mMediaList;
    private List<MediaItem> mSelectedList;
    private boolean mIsSingleFromCordova;

    private List<MediaSelectItemView> mediaSelectItems = new ArrayList<>();

    private boolean mSelectMode = true;

    public MediaSelectAdapter(Context context, List<MediaItem> mediaList, List<MediaItem> selectedList) {
        if (context == null || mediaList == null || selectedList == null) {
            throw new IllegalArgumentException("invalid argument on " + TAG);
        }
        mContext = context;
        mMediaList = mediaList;
        mSelectedList = selectedList;
        setSelectedStatus();
    }

    public void setImageList(List<MediaItem> imageList, List<MediaItem> selectedList) {
        mMediaList = imageList;
        mSelectedList = selectedList;
        setSelectedStatus();
        notifyDataSetChanged();
    }

    public void setSelectMode(boolean selectMode) {
        mSelectMode = selectMode;
        notifyDataSetChanged();
    }

    private void setSelectedStatus() {
        for (MediaItem imageItem : mMediaList) {
            if (imageItem == null) {
                continue;
            }
            imageItem.isSelected = false;
            for (MediaItem selectedItem : mSelectedList) {
                if (selectedItem == null || selectedItem.filePath == null) {
                    continue;
                }
                if (selectedItem.filePath.equals(imageItem.filePath)) {
                    imageItem.isSelected = true;
                    break;
                }
            }
        }
    }

    @Override
    public int getCount() {
        return mMediaList.size();
    }

    @Override
    public Object getItem(int position) {
        return mMediaList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = new MediaSelectItemView(mContext);
            mediaSelectItems.add((MediaSelectItemView) convertView);
        }
        MediaSelectItemView item = (MediaSelectItemView) convertView;
        item.setData(mMediaList.get(position));

        if (mIsSingleFromCordova || !mSelectMode){
            ((MediaSelectItemView) convertView).setCheckBoxUnShow();
        }
        return convertView;
    }

    public void setIsFromCordova(boolean isSingle){
        mIsSingleFromCordova = isSingle;
    }

}
