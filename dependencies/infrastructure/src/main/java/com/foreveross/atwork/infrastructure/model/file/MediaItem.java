package com.foreveross.atwork.infrastructure.model.file;

import android.os.Parcel;
import android.os.Parcelable;

import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;

import java.io.Serializable;
import java.util.UUID;

/**
 * 一个图片对象
 */
abstract public class MediaItem implements Serializable {
	public MediaItem()
	{
		identifier=UUID.randomUUID().toString();
	}
	public String identifier;
	public int dbId;
	public long size;
//	public int width = -1;
//	public int height = -1;
	public String thumbnailPath;
	public String filePath;
	public Long takenTimeStamp;
	public String title;
	public boolean isSelected = false;
	public String type;
	public String bucketId;
	public byte[] thumbnail;
	public String mediaType;
	public String thumbnailMediaId;

	public ChatPostMessage message;


	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		MediaItem mediaItem = (MediaItem) o;

		return filePath != null ? filePath.equals(mediaItem.filePath) : mediaItem.filePath == null;
	}

	@Override
	public int hashCode() {
		return filePath != null ? filePath.hashCode() : 0;
	}


}
