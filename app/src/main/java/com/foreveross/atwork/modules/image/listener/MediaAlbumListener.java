package com.foreveross.atwork.modules.image.listener;

import com.foreveross.atwork.infrastructure.model.file.MediaBucket;

import java.util.List;

/**
 * 相册加载监听
 * Created by ReyZhang on 2015/4/30.
 */
public interface MediaAlbumListener {
    void onAlbumLoadingSuccess(List<MediaBucket> imageAlbumList);

    void onAlbumSelected(int position);
}
