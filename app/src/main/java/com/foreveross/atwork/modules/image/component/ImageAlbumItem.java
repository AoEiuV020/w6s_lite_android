package com.foreveross.atwork.modules.image.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.model.file.MediaBucket;
import com.foreveross.atwork.infrastructure.model.file.MediaItem;
import com.foreveross.atwork.modules.image.adapter.ImageAlbumAdapter;
import com.foreveross.atwork.utils.ImageCacheHelper;

import java.util.List;

/**
 * 图片相册选择item
 * Created by ReyZhang on 2015/4/30.
 */
public class ImageAlbumItem extends RelativeLayout {

    private static final String TAG = ImageAlbumAdapter.class.getSimpleName();

    private ImageView mAlbumImage;
    private TextView mAlbumName;
    private TextView mAlbumTotal;
    private Context mContext;

    public ImageAlbumItem(Context context) {
        super(context);
        mContext = context;
        initView(context);
    }

    public ImageAlbumItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView(context);
    }

    public ImageAlbumItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_image_album, this);
        mAlbumImage = (ImageView)view.findViewById(R.id.album_image);
        mAlbumName = (TextView)view.findViewById(R.id.image_album_name);
        mAlbumTotal = (TextView)view.findViewById(R.id.image_total);
    }

    public void setImageAlbum(MediaBucket bucket) {
        if (bucket == null) {
            return;
        }

        List<MediaItem> imageList = bucket.getMediaList();
        if (imageList == null) {
            return;
        }
        int total = imageList.size();
        mAlbumTotal.setText(String.format(mContext.getString(R.string.total_page), total + ""));
        mAlbumName.setText(bucket.getBucketName());
        for (MediaItem imageItem : imageList) {
            if (imageItem == null) {
                continue;
            }
            ImageCacheHelper.displayImage(imageItem.filePath, mAlbumImage, ImageCacheHelper.getDefaultImageOptions(true, false, true));
            break;
        }
    }

}
