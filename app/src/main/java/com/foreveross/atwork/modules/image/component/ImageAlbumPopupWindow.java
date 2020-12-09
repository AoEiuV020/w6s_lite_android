package com.foreveross.atwork.modules.image.component;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.PaintDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.model.file.MediaBucket;
import com.foreveross.atwork.infrastructure.utils.DensityUtil;
import com.foreveross.atwork.modules.image.adapter.ImageAlbumAdapter;
import com.foreveross.atwork.modules.image.listener.MediaAlbumListener;

import java.util.List;

/**
 * 相册弹出选择相片popup
 * Created by ReyZhang on 2015/4/30.
 */

public class ImageAlbumPopupWindow extends PopupWindow {
    private ListView mPopupListView;
    private ImageAlbumAdapter mImageAlbumAdapter;

    public ImageAlbumPopupWindow (Activity activity, List<MediaBucket> imageBucketList, final MediaAlbumListener listener) {
        LayoutInflater inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.component_image_album, null);
        mPopupListView = (ListView)popupView.findViewById(R.id.image_album_listview);
        mImageAlbumAdapter = new ImageAlbumAdapter(activity, imageBucketList);
        mPopupListView.setAdapter(mImageAlbumAdapter);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(DensityUtil.dip2px(500));
        setFocusable(true);
        setContentView(popupView);
        setBackgroundDrawable(new PaintDrawable());
        setOutsideTouchable(true);
//        ScreenUtils.setBackgroundAlpha(activity, 0.5f);

        mPopupListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listener.onAlbumSelected(position);
                dismiss();
            }
        });
    }

}
