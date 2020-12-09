package com.foreveross.atwork.infrastructure.manager;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.Albums;
import android.provider.MediaStore.Images.Thumbnails;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.text.TextUtils;

import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.manager.contentObserver.GalleryObserverExternal;
import com.foreveross.atwork.infrastructure.model.file.AudioItem;
import com.foreveross.atwork.infrastructure.model.file.FileData;
import com.foreveross.atwork.infrastructure.model.file.ImageItem;
import com.foreveross.atwork.infrastructure.model.file.MediaBucket;
import com.foreveross.atwork.infrastructure.model.file.MediaItem;
import com.foreveross.atwork.infrastructure.model.file.SelectMediaType;
import com.foreveross.atwork.infrastructure.model.file.VideoItem;
import com.foreveross.atwork.infrastructure.utils.LogUtil;
import com.foreveross.atwork.infrastructure.utils.LongUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


public class FileAlbumService {
    public static final String TAG = FileAlbumService.class.getSimpleName();

    public static final String ALL_MEDIA_ALBUM_KEY = "all media";
    public static final String ALL_IMAGE_ALBUM_KEY = "all img";
    public static final String ALL_VIDEO_ALBUM_KEY = "all video";

    public static final String ACTION_LOAD_ALBUM_END = "ACTION_LOAD_ALBUM_END";

    private static final Object sLoadLock = new Object();

    private static FileAlbumService instance;

    private LinkedHashMap<String, MediaBucket> mMediaBucketMap;
    private int mCountAllInCursor = 0;

    private ScheduledExecutorService mExecutorService = Executors.newScheduledThreadPool(1);
    private ScheduledFuture mTaskFuture;

    private boolean mIsLoading;


    private FileAlbumService() {
    }

    public static FileAlbumService getInstance() {
        if (instance == null) {
            instance = new FileAlbumService();
        }
        return instance;
    }

    /**
     * 初始化
     */
    public void init() {
        ContentResolver contentResolver = BaseApplicationLike.baseContext.getContentResolver();
        contentResolver.registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, new GalleryObserverExternal());
        contentResolver.registerContentObserver(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, true, new GalleryObserverExternal());

    }

    /**
     * 文件获取路径 实现带有抽象方法的枚举
     *
     * @author alawn
     */
    public enum CommonFileUriType {
        /**
         * internal
         */
        internal {
            public String toString() {
                return "internal";
            }
        },
        /**
         * external
         */
        external {
            public String toString() {
                return "external";
            }
        },
        /**
         * download
         */
        downloads {
            public String toString() {
                return "downloads";
            }
        };

        public abstract String toString();
    }

    /***
     * 获取其他文件列表
     *
     * @return
     */
    public ArrayList<FileData> getFileDataList(CommonFileUriType commonFileUriType) {
        ArrayList<FileData> FileDataList = new ArrayList<>();
        String[] columns = new String[]{MediaStore.Files.FileColumns.TITLE, MediaStore.Files.FileColumns.DATA, MediaStore.Files.FileColumns.DATE_ADDED, MediaStore.Files.FileColumns._ID, MediaStore.Files.FileColumns.SIZE,};
        Uri uri = MediaStore.Files.getContentUri(commonFileUriType.toString());
        String selection = null;
        // selection = "(" + MediaStore.Files.FileColumns.MIME_TYPE +
        // "=='text/plain')";//过滤文件
        Cursor cursor = BaseApplicationLike.baseContext.getContentResolver().query(uri, columns, selection, null, MediaStore.Files.FileColumns.DATE_ADDED + " ASC");
        if (cursor != null && cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(MediaStore.Files.FileColumns._ID);
            int dataIndex = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA);
            int titleIndex = cursor.getColumnIndex(MediaStore.Files.FileColumns.TITLE);
            int sizeIndex = cursor.getColumnIndex(MediaStore.Files.FileColumns.SIZE);
            int dateIndex = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATE_ADDED);
            do {
                FileData FileData = new FileData();
                FileData.title = cursor.getString(titleIndex); // 获取文件名，不包含扩展名
                FileData.filePath = cursor.getString(dataIndex); // 获取文件实际路径
                int lastIndex = FileData.filePath.lastIndexOf("/") + 1;
                int endIndex = FileData.filePath.length();
                FileData.title = FileData.filePath.substring(lastIndex, endIndex);
                FileData.fileType = com.foreveross.atwork.infrastructure.model.file.FileData.getFileType(FileData.title);// 设置文件类型
                FileData.size = cursor.getInt(sizeIndex);// 获取文件大小
                FileData.date = cursor.getLong(dateIndex);// 获取date
                if (!TextUtils.isEmpty(FileData.filePath) && new File(FileData.filePath).exists() && FileData.size > 0) {
                    FileDataList.add(FileData);
                }
            } while (cursor.moveToNext()); // 循环获取文件
        }
        if (cursor != null) {
            cursor.close();
        }
        return FileDataList;
    }

    /***
     * 获取所有音频列表
     *
     * @return
     */
    public ArrayList<AudioItem> getAudioItemList() {
        ArrayList<AudioItem> audioItemList = new ArrayList<>();
        // String[] mediaColumns = new String[]
        // {MediaStore.Audio.Media._ID,MediaStore.Audio.Media.SIZE,
        // MediaStore.Audio.Media.ALBUM,MediaStore.Audio.Media.ALBUM_ID,MediaStore.Audio.Media.ALBUM_KEY
        // ,MediaStore.Audio.Media.ARTIST,MediaStore.Audio.Media.ARTIST_ID,MediaStore.Audio.Media.DATA,
        // MediaStore.Audio.Media.MIME_TYPE,MediaStore.Audio.Media.YEAR,MediaStore.Audio.Media.DATE_ADDED};
        Cursor cursor = BaseApplicationLike.baseContext.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Audio.Media.DATE_ADDED + " ASC");
        if (cursor != null && cursor.moveToFirst()) {
            do {
                AudioItem audioItem = new AudioItem();
                audioItem.audioId = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
                // 歌曲的名称 ：MediaStore.Audio.Media.TITL
                audioItem.title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                // 歌曲的专辑名：MediaStore.Audio.Media.ALBUM
                audioItem.album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
                // 歌曲的歌手名： MediaStore.Audio.Media.ARTIST
                audioItem.artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                // 歌曲文件的全路径 ：MediaStore.Audio.Media.DATA
                audioItem.path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));

                // 歌曲的名称 ：MediaStore.Audio.Media.TITL--包含后缀名
                int lastIndex = audioItem.path.lastIndexOf("/") + 1;
                int endIndex = audioItem.path.length();
                audioItem.title = audioItem.path.substring(lastIndex, endIndex);

                // 歌曲文件的名称：MediaStroe.Audio.Media.DISPLAY_NAME
                audioItem.display_name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME));
                // 歌曲文件的发行日期：MediaStore.Audio.Media.YEAR
                audioItem.year = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.YEAR));
                // 歌曲的总播放时长 ：MediaStore.Audio.Media.DURATION
                audioItem.duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                // 歌曲文件的大小 ：MediaStore.Audio.Media.SIZE
                audioItem.size = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));
                audioItem.addDate = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED));
                if (!audioItemList.contains(audioItem)) {
                    audioItemList.add(audioItem);
                }
            } while (cursor.moveToNext());

        }
        if (cursor != null) {
            cursor.close();
        }
        return audioItemList;
    }

    /***
     * 获取所有音频文件数量
     *
     * @return
     */
    public int getAllAudioSizes() {
        return getAudioItemList() != null ? getAudioItemList().size() : 0;
    }

    /***
     * 获取录像、视频缩略图
     *
     * @return
     */
    public ArrayList<VideoItem> getAllVideoItemList() {
        LinkedHashMap<String, MediaBucket> videoBucketHashMap = new LinkedHashMap<>();
        buildVideoBucketList(videoBucketHashMap);
        ArrayList<VideoItem> videoItemList = new ArrayList<>();

        for(MediaBucket mediaBucket : videoBucketHashMap.values()) {

            for(MediaItem mediaItem: mediaBucket.getMediaList()) {
                if(mediaItem instanceof VideoItem) {

                    VideoItem videoItem = (VideoItem) mediaItem;
                    if (!videoItemList.contains(videoItem)) {
                        videoItemList.add(videoItem);
                    }
                }
            }
        }

        return videoItemList;
    }



    /***
     * 获取所有视频数量
     *
     * @return
     */
    public int getAllVideoSizes() {
        return getAllVideoItemList() != null ? getAllVideoItemList().size() : 0;
    }

    /****
     * 得到缩略图 从数据库中得到缩略图
     *
     * @return HashMap<String, String>
     */
    private HashMap<String, String> getImgThumbnail() {
        String[] projection = {Thumbnails._ID, Thumbnails.IMAGE_ID, Thumbnails.DATA};
        Cursor cur = BaseApplicationLike.baseContext.getContentResolver().query(Thumbnails.EXTERNAL_CONTENT_URI, projection, null, null, null);
        // 相册缩略图列表
        HashMap<String, String> thumbnailList = new HashMap<>();
        if (cur != null && cur.moveToFirst()) {
            int _idColumn = cur.getColumnIndex(Thumbnails._ID);
            int image_idColumn = cur.getColumnIndex(Thumbnails.IMAGE_ID);
            int dataColumn = cur.getColumnIndex(Thumbnails.DATA);
            do {
                // Get the field values
                int _id = cur.getInt(_idColumn);
                int image_id = cur.getInt(image_idColumn);
                String image_path = cur.getString(dataColumn);
                thumbnailList.put("" + image_id, image_path);
            } while (cur.moveToNext());
        }
        if (cur != null) {
            cur.close();
        }
        return thumbnailList;
    }

    @NonNull
    private HashMap<String, String> getVideoThumbnail() {
        HashMap<String, String> videoThumbnailList = new HashMap<>();
        String[] thumbColumns = new String[]{MediaStore.Video.Thumbnails.DATA, MediaStore.Video.Thumbnails.VIDEO_ID};
        Cursor videoThumbCursor = BaseApplicationLike.baseContext.getContentResolver().query(MediaStore.Video.Thumbnails.INTERNAL_CONTENT_URI, thumbColumns, null, null, null);
        if (videoThumbCursor != null && videoThumbCursor.moveToFirst()) {
            do {
                int dataColumn = videoThumbCursor.getColumnIndex(MediaStore.Video.Thumbnails.DATA);
                int idColumn = videoThumbCursor.getColumnIndex(MediaStore.Video.Thumbnails.VIDEO_ID);
                videoThumbnailList.put("" + videoThumbCursor.getInt(idColumn), videoThumbCursor.getString(dataColumn));
            } while (videoThumbCursor.moveToNext());
        }
        if (videoThumbCursor != null) {
            videoThumbCursor.close();
        }
        return videoThumbnailList;
    }

    /**
     * 得到原图
     */
    void getAlbum() {
        String[] projection = {Albums._ID, Albums.ALBUM, Albums.ALBUM_ART, Albums.ALBUM_KEY, Albums.ARTIST, Albums.NUMBER_OF_SONGS};
        Cursor cursor = BaseApplicationLike.baseContext.getContentResolver().query(Albums.EXTERNAL_CONTENT_URI, projection, null, null, null);
        if (cursor != null) {
            cursor.close();
        }
        getAlbumColumnData(cursor);

    }

    /**
     * 从本地数据库中得到原图
     *
     * @param cur
     */
    private void getAlbumColumnData(Cursor cur) {
        // 相册专辑列表
        List<HashMap<String, String>> albumList = new ArrayList<>();
        if (cur != null && cur.moveToFirst()) {
            int _idColumn = cur.getColumnIndex(Albums._ID);
            int albumColumn = cur.getColumnIndex(Albums.ALBUM);
            int albumArtColumn = cur.getColumnIndex(Albums.ALBUM_ART);
            int albumKeyColumn = cur.getColumnIndex(Albums.ALBUM_KEY);
            int artistColumn = cur.getColumnIndex(Albums.ARTIST);
            int numOfSongsColumn = cur.getColumnIndex(Albums.NUMBER_OF_SONGS);
            do {
                // Get the field values
                int _id = cur.getInt(_idColumn);
                String album = cur.getString(albumColumn);
                String albumArt = cur.getString(albumArtColumn);
                String albumKey = cur.getString(albumKeyColumn);
                String artist = cur.getString(artistColumn);
                int numOfSongs = cur.getInt(numOfSongsColumn);

                // Do something with the values.
//                Log.i(TAG, _id + " album:" + album + " albumArt:" + albumArt + "albumKey: " + albumKey + " artist: " + artist + " numOfSongs: " + numOfSongs + "---");
                HashMap<String, String> hash = new HashMap<>();
                hash.put("_id", _id + "");
                hash.put("album", album);
                hash.put("albumArt", albumArt);
                hash.put("albumKey", albumKey);
                hash.put("artist", artist);
                hash.put("numOfSongs", numOfSongs + "");
                albumList.add(hash);

            } while (cur.moveToNext());

        }
    }


    @Nullable
    public LinkedHashMap<String, MediaBucket> getAllBucketMap(List<SelectMediaType> selectMediaTypes) {
        if(null == mMediaBucketMap) {
            return null;
        }

        LinkedHashMap<String, MediaBucket> filterBucketMap = new LinkedHashMap<>(mMediaBucketMap);

        if(!selectMediaTypes.contains(SelectMediaType.VIDEO)) {
            filterBucketMap.remove(ALL_MEDIA_ALBUM_KEY);
            filterBucketMap.remove(ALL_VIDEO_ALBUM_KEY);
            return filterBucketMap;
        }

        filterBucketMap.remove(ALL_IMAGE_ALBUM_KEY);

        return filterBucketMap;
    }

    public void checkUpdate() {
        if(null != mTaskFuture) {
            mTaskFuture.cancel(true);
        }

        mTaskFuture = mExecutorService.schedule(new Runnable() {
            @Override
            public void run() {

                synchronized (sLoadLock) {
                    int newSize = queryAllMediaSize();

                    if(mCountAllInCursor != newSize) {
                        //do refresh data
                        buildAllBucketListSafely();


                    }
                }


            }
        }, 2000, TimeUnit.MILLISECONDS);
    }


    public void refreshAllBucketList() {
        if(null != mTaskFuture) {
            mTaskFuture.cancel(true);
        }

        mTaskFuture = mExecutorService.schedule(new Runnable() {
            @Override
            public void run() {
                //do refresh data
                buildAllBucketListSafely();



            }
        }, 2000, TimeUnit.MILLISECONDS);
    }

    public void checkRefreshAllBucketList() {
        if(!isLoading()) {
            refreshAllBucketListImmediately();
        }
    }

    public void refreshAllBucketListImmediately() {
        if(null != mTaskFuture) {
            mTaskFuture.cancel(true);
        }

        mTaskFuture = mExecutorService.schedule(new Runnable() {
            @Override
            public void run() {
                //do refresh data
                buildAllBucketListSafely();



            }
        }, 0, TimeUnit.MILLISECONDS);
    }


    public LinkedHashMap<String, MediaBucket> buildAllBucketListSafely() {
        synchronized (sLoadLock) {
            return buildAllBuckList();
        }
    }

    @NonNull
    private LinkedHashMap<String, MediaBucket> buildAllBuckList() {
        mIsLoading = true;


        long startTime = System.currentTimeMillis();

        mCountAllInCursor = 0;

        LinkedHashMap<String, MediaBucket> mediaBucketMap = new LinkedHashMap<>();
        buildImagesBucketList(mediaBucketMap);

        buildVideoBucketList(mediaBucketMap);


        //firstly, sort all media
        List<MediaItem> allMediaList = mediaBucketMap.get(ALL_MEDIA_ALBUM_KEY).getMediaList();
        Collections.sort(allMediaList, new Comparator<MediaItem>() {
            @Override
            public int compare(MediaItem o1, MediaItem o2) {
                if(o1.takenTimeStamp > o2.takenTimeStamp) {
                    return -1;
                }

                if(o1.takenTimeStamp.equals(o2.takenTimeStamp)) {
                    return 0;
                }

                return 1;
            }
        });

        //then, put it into the corresponding album
        for(MediaItem mediaItem: allMediaList) {
            MediaBucket mediaBucket = mediaBucketMap.get(mediaItem.bucketId);

            if(null != mediaBucket) {
                mediaBucket.getMediaList().add(mediaItem);
                mediaBucket.setCount(mediaBucket.getCount() + 1);
            }
        }

        mMediaBucketMap = mediaBucketMap;


        notifyLoadEnd();

        long endTime = System.currentTimeMillis();


        LogUtil.e(TAG, "get all image use time: " + (endTime - startTime) + " ms   count -> " + mCountAllInCursor);


        mIsLoading = false;

        return mediaBucketMap;
    }


    public LinkedHashMap<String, MediaBucket> buildVideoBucketList(LinkedHashMap<String, MediaBucket> bucketList) {
        long startTime = System.currentTimeMillis();

        String[] mediaColumns = new String[]{MediaStore.Video.Media.DURATION, MediaStore.Video.Media.DATA, MediaStore.Video.Media._ID, MediaStore.Video.Media.BUCKET_ID, MediaStore.Video.Media.BUCKET_DISPLAY_NAME, MediaStore.Video.Media.TITLE, MediaStore.Video.Media.MIME_TYPE, MediaStore.Video.Media.DATE_TAKEN, MediaStore.Video.Media.SIZE};
        String orderString = MediaStore.Video.Media.DATE_ADDED + " DESC";

//        HashMap<String, String> videoThumbnailList = getVideoThumbnail();

        Cursor cursor = BaseApplicationLike.baseContext.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, mediaColumns, null, null, orderString);

        if(null != cursor) {
            mCountAllInCursor += cursor.getCount();
        }

        if (cursor != null && cursor.moveToFirst()) {
            do {

                try {
                    int durationColumn = cursor.getColumnIndex(MediaStore.Video.Media.DURATION);
                    int dataColumn = cursor.getColumnIndex(MediaStore.Video.Media.DATA);
                    int idColumn = cursor.getColumnIndex(MediaStore.Video.Media._ID);
                    int bucketIdColumn = cursor.getColumnIndex(MediaStore.Video.Media.BUCKET_ID);
                    int bucketDisplayColumn = cursor.getColumnIndex(MediaStore.Video.Media.BUCKET_DISPLAY_NAME);
                    int titleColumn = cursor.getColumnIndex(MediaStore.Video.Media.TITLE);
                    int mimeTypeColumn = cursor.getColumnIndex(MediaStore.Video.Media.MIME_TYPE);
                    int takenColumn = cursor.getColumnIndex(MediaStore.Video.Media.DATE_TAKEN);
                    int sizeColumn = cursor.getColumnIndex(MediaStore.Video.Media.SIZE);

                    String bucketName = cursor.getString(bucketDisplayColumn);
                    String bucketId = cursor.getString(bucketIdColumn);

                    if(null == bucketName) {
                        bucketName = StringUtils.EMPTY;
                    }

                    MediaBucket mediaBucket = bucketList.get(bucketId);
                    MediaBucket allMediaBucket = bucketList.get(ALL_MEDIA_ALBUM_KEY);
                    MediaBucket allVideoBucket = bucketList.get(ALL_VIDEO_ALBUM_KEY);


                    if(null == allMediaBucket) {
                        allMediaBucket = new MediaBucket();
                        bucketList.put(ALL_MEDIA_ALBUM_KEY, allMediaBucket);
                        allMediaBucket.setBucketName("图片和视频");
                    }


                    if(null == allVideoBucket) {
                        allVideoBucket = new MediaBucket();
                        bucketList.put(ALL_VIDEO_ALBUM_KEY, allVideoBucket);
                        allVideoBucket.setBucketName("所有视频");
                    }

                    if (null == mediaBucket) {
                        mediaBucket = new MediaBucket();
                        bucketList.put(bucketId, mediaBucket);
                        mediaBucket.setBucketName(bucketName);
                    }


                    VideoItem videoItem = new VideoItem();
                    videoItem.dbId = cursor.getInt(idColumn);
                    videoItem.filePath = cursor.getString(dataColumn);
                    int lastIndex = videoItem.filePath.lastIndexOf("/") + 1;
                    int endIndex = videoItem.filePath.length();
                    videoItem.title = videoItem.filePath.substring(lastIndex, endIndex);
                    videoItem.type = cursor.getString(mimeTypeColumn);
                    videoItem.takenTimeStamp = cursor.getLong(takenColumn);
                    videoItem.size = cursor.getInt(sizeColumn);
//                videoItem.thumbnailPath = videoThumbnailList.get(videoItem.dbId);
                    videoItem.setDuration(cursor.getLong(durationColumn));
                    videoItem.bucketId = bucketId;

                    allMediaBucket.setCount(allMediaBucket.getCount() + 1);
                    allMediaBucket.getMediaList().add(videoItem);

                    allVideoBucket.setCount(allVideoBucket.getCount() + 1);
                    allVideoBucket.getMediaList().add(videoItem);
                } catch (Exception e) {
                    e.printStackTrace();
                }


            } while (cursor.moveToNext());
        }


        if (cursor != null) {
            cursor.close();
        }


        long endTime = System.currentTimeMillis();
        LogUtil.e(TAG, "get all video use time: " + (endTime - startTime) + " ms");

        return bucketList;
    }


    /**
     * 得到图片集
     *
     * @return
     */
    public LinkedHashMap<String, MediaBucket> buildImagesBucketList(LinkedHashMap<String, MediaBucket> bucketList) {
        long startTime = System.currentTimeMillis();
        // 获取缩略图索引
//        HashMap<String, String> thumbnailList = getImgThumbnail();
        // 构造相册索引
        String columns[] = new String[]{MediaStore.Images.Media._ID, MediaStore.Images.Media.BUCKET_ID, MediaStore.Images.Media.DATA, MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media.TITLE, MediaStore.Images.Media.SIZE, MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.Images.Media.DATE_TAKEN, MediaStore.Images.Media.MIME_TYPE};
        String orderString = MediaStore.Images.Media.DATE_ADDED + " DESC";
        // 得到一个游标
        Cursor cursor = BaseApplicationLike.baseContext.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, orderString);

        if(null != cursor) {
            mCountAllInCursor += cursor.getCount();
        }

        if (null != cursor && cursor.moveToFirst()) {
            // 获取指定列的索引
            int photoIDIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
            int photoPathIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            int photoNameIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
            int photoTitleIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.TITLE);
            int photoSizeIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE);
            int bucketDisplayNameIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
            int bucketIdIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID);
            int photoTakenDateIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN);
            int mimeTypeIndex = cursor.getColumnIndex(MediaStore.Images.Media.MIME_TYPE);

            do {

                try {
                    int _id = cursor.getInt(photoIDIndex);
                    String name = cursor.getString(photoNameIndex);
                    String path = cursor.getString(photoPathIndex);
                    String title = cursor.getString(photoTitleIndex);
                    int size = cursor.getInt(photoSizeIndex);
                    String bucketName = cursor.getString(bucketDisplayNameIndex);

                    if(null == bucketName) {
                        bucketName = StringUtils.EMPTY;
                    }

                    String bucketId = cursor.getString(bucketIdIndex);
                    String mimeType = cursor.getString(mimeTypeIndex);

                    File file = new File(path);
                    //当文件不存在, 或者文件大小为0时,忽略掉
                    if (!file.exists() || 0 == size && 0 == file.length()) {
                        continue;
                    }

                    String photoTakenDateStr = cursor.getString(photoTakenDateIndex);
                    long takenTimeStamp = LongUtil.parseLong(photoTakenDateStr);
//                LogUtil.e(TAG, _id + ", bucketId: " + bucketId + " name:" + name + " path:" + path + " title: " + title + " size: " + size + " mediaBucket: " + bucketName + "taken_date:" + takenTimeStamp + "---");

                    MediaBucket mediaBucket = bucketList.get(bucketId);
                    MediaBucket allMediaBucket = bucketList.get(ALL_MEDIA_ALBUM_KEY);
                    MediaBucket allImageBucket = bucketList.get(ALL_IMAGE_ALBUM_KEY);
                    MediaBucket allVideoBucket = bucketList.get(ALL_VIDEO_ALBUM_KEY);


                    if(null == allMediaBucket) {
                        allMediaBucket = new MediaBucket();

                        allMediaBucket.setBucketName("图片和视频");
                        bucketList.put(ALL_MEDIA_ALBUM_KEY, allMediaBucket);

                    }

                    if(null == allVideoBucket) {
                        allVideoBucket = new MediaBucket();
                        bucketList.put(ALL_VIDEO_ALBUM_KEY, allVideoBucket);
                        allVideoBucket.setBucketName("所有视频");
                    }

                    if(null == allImageBucket) {
                        allImageBucket = new MediaBucket();
                        allImageBucket.setBucketName("所有图片");
                        bucketList.put(ALL_IMAGE_ALBUM_KEY, allImageBucket);


                    }

                    if (null == mediaBucket) {
                        mediaBucket = new MediaBucket();
                        bucketList.put(bucketId, mediaBucket);
                        mediaBucket.setBucketName(bucketName);
                    }

                    MediaItem imageItem = new ImageItem();
                    imageItem.dbId = _id;
                    imageItem.filePath = path;
//                imageItem.thumbnailPath = thumbnailList.get(_id);
                    imageItem.takenTimeStamp = takenTimeStamp;
                    imageItem.title = title;
                    int lastIndex = imageItem.filePath.lastIndexOf("/") + 1;
                    int endIndex = imageItem.filePath.length();
                    imageItem.title = imageItem.filePath.substring(lastIndex, endIndex);
                    imageItem.size = size;
                    imageItem.bucketId = bucketId;
                    imageItem.type = mimeType;

                    allMediaBucket.setCount(allMediaBucket.getCount() + 1);
                    allMediaBucket.getMediaList().add(imageItem);

                    allImageBucket.setCount(allImageBucket.getCount() + 1);
                    allImageBucket.getMediaList().add(imageItem);

                } catch (Exception e) {
                    e.printStackTrace();
                }


            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }


        long endTime = System.currentTimeMillis();
        LogUtil.e(TAG, "get all image use time: " + (endTime - startTime) + " ms");
        return bucketList;
    }

    public static ContentValues getLastInsertImageCv(Context context) {
        ContentValues cv = new ContentValues();
        // 构造相册索引
        String columns[] = new String[]{MediaStore.Images.Media._ID, MediaStore.Images.Media.BUCKET_ID, MediaStore.Images.Media.PICASA_ID, MediaStore.Images.Media.DATA, MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media.TITLE, MediaStore.Images.Media.SIZE, MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.Images.Media.DATE_TAKEN};
        String orderString = MediaStore.Images.Media.DATE_ADDED + " DESC";
        // 得到一个游标
        Cursor cur = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null,
                null, orderString);

        if (cur != null && cur.moveToFirst()) {
            // 获取指定列的索引
            int photoIDIndex = cur.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
            int photoPathIndex = cur.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            int photoNameIndex = cur.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
            int photoTitleIndex = cur.getColumnIndexOrThrow(MediaStore.Images.Media.TITLE);
            int photoSizeIndex = cur.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE);
            int bucketDisplayNameIndex = cur.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
            int bucketIdIndex = cur.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID);
            int picasaIdIndex = cur.getColumnIndexOrThrow(MediaStore.Images.Media.PICASA_ID);
            int photoTakenDateIndex = cur.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN);

            cv.put(MediaStore.Images.Media._ID, cur.getInt(photoIDIndex));
            cv.put(MediaStore.Images.Media.DISPLAY_NAME, cur.getString(photoNameIndex));
            cv.put(MediaStore.Images.Media.DATA, cur.getString(photoPathIndex));
            cv.put(MediaStore.Images.Media.TITLE, cur.getString(photoTitleIndex));
            cv.put(MediaStore.Images.Media.SIZE, cur.getInt(photoSizeIndex));
            cv.put(MediaStore.Images.Media.BUCKET_DISPLAY_NAME, cur.getString(bucketDisplayNameIndex));
            cv.put(MediaStore.Images.Media.BUCKET_ID, cur.getString(bucketIdIndex));
            cv.put(MediaStore.Images.Media.PICASA_ID, cur.getString(picasaIdIndex));
        }
        if (cur != null) {
            cur.close();
        }

        return cv;

    }


    public static ContentValues getLastInsertVideoCv(Context context) {
        ContentValues cv = new ContentValues();
        // 构造相册索引
        String columns[] = new String[]{MediaStore.Video.Media._ID, MediaStore.Video.Media.BUCKET_ID, MediaStore.Video.Media.DATA, MediaStore.Video.Media.DISPLAY_NAME, MediaStore.Video.Media.TITLE, MediaStore.Video.Media.SIZE, MediaStore.Video.Media.BUCKET_DISPLAY_NAME, MediaStore.Video.Media.DATE_TAKEN};
        String orderString = MediaStore.Video.Media.DATE_ADDED + " DESC";
        // 得到一个游标
        Cursor cur = context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, columns, null,
                null, orderString);

        if (cur != null && cur.moveToFirst()) {
            // 获取指定列的索引
            int photoIDIndex = cur.getColumnIndexOrThrow(MediaStore.Video.Media._ID);
            int photoPathIndex = cur.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            int photoNameIndex = cur.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME);
            int photoTitleIndex = cur.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE);
            int photoSizeIndex = cur.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE);
            int bucketDisplayNameIndex = cur.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME);
            int bucketIdIndex = cur.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_ID);
            int photoTakenDateIndex = cur.getColumnIndex(MediaStore.Video.Media.DATE_TAKEN);

            cv.put(MediaStore.Images.Media._ID, cur.getInt(photoIDIndex));
            cv.put(MediaStore.Images.Media.DISPLAY_NAME, cur.getString(photoNameIndex));
            cv.put(MediaStore.Images.Media.DATA, cur.getString(photoPathIndex));
            cv.put(MediaStore.Images.Media.TITLE, cur.getString(photoTitleIndex));
            cv.put(MediaStore.Images.Media.SIZE, cur.getInt(photoSizeIndex));
            cv.put(MediaStore.Images.Media.BUCKET_DISPLAY_NAME, cur.getString(bucketDisplayNameIndex));
            cv.put(MediaStore.Images.Media.BUCKET_ID, cur.getString(bucketIdIndex));
        }
        if (cur != null) {
            cur.close();
        }

        return cv;

    }





    public void notifyLoadEnd() {
        LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).sendBroadcast(new Intent(ACTION_LOAD_ALBUM_END));
    }


    public int queryAllMediaSize() {
        return queryImgCount() + queryVideoCount();
    }


    public int queryImgCount() {
        int count = 0;
        Cursor cursor = null;
        try {
            cursor = MediaStore.Images.Media.query(BaseApplicationLike.baseContext.getContentResolver(), MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[] {"COUNT(_id)"}, null, null, null);
            if (cursor != null) {
                if (cursor.moveToNext()) {
                    count += cursor.getInt(0);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return count;

    }


    public int queryVideoCount() {
        int count = 0;
        Cursor cursor = null;
        try {
            cursor = MediaStore.Images.Media.query(BaseApplicationLike.baseContext.getContentResolver(), MediaStore.Video.Media.EXTERNAL_CONTENT_URI, new String[] {"COUNT(_id)"}, null, null, null);
            if (cursor != null) {
                if (cursor.moveToNext()) {
                    count += cursor.getInt(0);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return count;

    }

    public boolean isLoading() {
        return mIsLoading;
    }
}
