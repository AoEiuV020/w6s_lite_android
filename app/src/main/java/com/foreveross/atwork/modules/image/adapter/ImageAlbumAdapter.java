package com.foreveross.atwork.modules.image.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.foreveross.atwork.infrastructure.model.file.MediaBucket;
import com.foreveross.atwork.modules.image.component.ImageAlbumItem;

import java.util.List;

/**
 * Created by ReyZhang on 2015/4/30.
 */
public class ImageAlbumAdapter extends BaseAdapter {

    private static final String TAG = ImageAlbumAdapter.class.getSimpleName();

    private Context mContext;
    private List<MediaBucket> mImageAlbumList;

    public ImageAlbumAdapter(Context context, List<MediaBucket> list) {
        if (context == null || list == null) {
            throw new IllegalArgumentException("invalid argument on " + TAG);
        }
        mContext = context;
        mImageAlbumList = list;
    }

    @Override
    public int getCount() {
        return mImageAlbumList.size();
    }

    @Override
    public Object getItem(int position) {
        return mImageAlbumList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = new ImageAlbumItem(mContext);
        }
        ImageAlbumItem item = (ImageAlbumItem)convertView;
        item.setImageAlbum(mImageAlbumList.get(position));
        return convertView;
    }
}
