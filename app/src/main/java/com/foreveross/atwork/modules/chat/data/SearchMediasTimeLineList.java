package com.foreveross.atwork.modules.chat.data;

import androidx.annotation.NonNull;

import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.file.ImageItem;
import com.foreveross.atwork.infrastructure.model.file.MediaItem;
import com.foreveross.atwork.infrastructure.model.file.VideoItem;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.BodyType;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ImageChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ImageContentInfo;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.MicroVideoChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.anno.AnnoImageChatMessage;
import com.foreveross.atwork.infrastructure.utils.TimeUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

public class SearchMediasTimeLineList<T> extends ArrayList {

    private List mTimeList = new ArrayList();

    public int timeSize = 0;

    public LinkedHashMap<String, List<MediaItem>> mediaMap = new LinkedHashMap<>();

    @Override
    public boolean addAll(@NonNull Collection c) {
        SearchMediasTimeLineList newList = new SearchMediasTimeLineList();

        List<SearchMessageItemData> mediaList = new ArrayList(c);
        List<MediaItem> mediaItems;
        for (SearchMessageItemData data : mediaList) {
            if (data == null) {
                continue;
            }

            String monthOfDay = TimeUtil.getStringForMillis(data.mMessage.deliveryTime, TimeUtil.getTimeFormat4(BaseApplicationLike.baseContext));
            monthOfDay = TimeUtil.isSameMonth(BaseApplicationLike.baseContext, TimeUtil.getCurrentTimeInMillis(), monthOfDay);
            if (!mTimeList.contains(monthOfDay)) {
                mTimeList.add(monthOfDay);
                SearchMessageItemData searchMediaItemData = new SearchMessageItemData();
                searchMediaItemData.mIsTimeLine = true;
                searchMediaItemData.mName = monthOfDay;
                newList.add(searchMediaItemData);
                SearchMessageItemData sub = new SearchMessageItemData();
                sub.mIsTimeLine = false;
                sub.mName = monthOfDay;
                newList.add(sub);
                mediaItems = new ArrayList<>();
                covertToMediaItem(mediaItems,data);
                mediaMap.put(monthOfDay, mediaItems);
            } else {
                mediaItems = mediaMap.get(monthOfDay);
                covertToMediaItem(mediaItems, data);
            }

        }
        timeSize = mTimeList.size();
        return super.addAll(newList);
    }

    public void covertToMediaItem(List<MediaItem> list, SearchMessageItemData data) {
        if (data.mMessage instanceof AnnoImageChatMessage) {
            AnnoImageChatMessage annoImageChatMessage = (AnnoImageChatMessage) data.mMessage;
            for (ImageContentInfo info : annoImageChatMessage.contentInfos) {
                ImageChatMessage imageChatMessage = new ImageChatMessage();
                imageChatMessage.from = annoImageChatMessage.from;
                imageChatMessage.mFromDomain = annoImageChatMessage.mFromDomain;
                imageChatMessage.mFromType = annoImageChatMessage.mFromType;
                imageChatMessage.to = annoImageChatMessage.to;
                imageChatMessage.mToDomain = annoImageChatMessage.mToDomain;
                imageChatMessage.mToType = annoImageChatMessage.mToType;
                imageChatMessage.deliveryTime = annoImageChatMessage.deliveryTime;
                imageChatMessage.filePath = info.filePath;
                imageChatMessage.mediaId = info.mediaId;
                imageChatMessage.fullImgPath = info.fullImgPath;
                imageChatMessage.isGif =info.isGif;
                imageChatMessage.thumbnailMediaId = info.thumbnailMediaId;
                imageChatMessage.mBodyType = BodyType.Image;
                imageChatMessage.setThumbnails(info.thumbnail);
                ImageItem item = new ImageItem();
                item.message = imageChatMessage;
                item.type = "gif";
                list.add(item);
            }
            return;
        } else if (data.mMessage instanceof MicroVideoChatMessage) {
            VideoItem item = new VideoItem();
            item.message = data.mMessage;
            list.add(item);
            return;
        } else {
            ImageItem item = new ImageItem();
            item.message = data.mMessage;
            if (data.mMessage instanceof ImageChatMessage) {
                if (((ImageChatMessage)data.mMessage).isGif) {
                    item.type = "gif";
                }
            }
            list.add(item);
        }

    }

    public void reset() {
        this.clear();
        mediaMap.clear();
        mTimeList.clear();
        timeSize = 0;
    }


}
