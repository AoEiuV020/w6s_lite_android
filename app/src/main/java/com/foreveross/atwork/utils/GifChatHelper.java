package com.foreveross.atwork.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Movie;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.foreverht.cache.BitmapCache;
import com.foreverht.threadGear.ImageThreadPoolExecutor;
import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.net.MediaCenterHttpURLConnectionUtil;
import com.foreveross.atwork.api.sdk.upload.MediaCenterSyncNetService;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.FileTransferChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ImageChatMessage;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.ArrayUtil;
import com.foreveross.atwork.infrastructure.utils.FileUtil;
import com.foreveross.atwork.infrastructure.utils.GifShowHelper;
import com.foreveross.atwork.infrastructure.utils.ImageShowHelper;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.file.FileStreamHelper;

import java.io.File;
import java.io.IOException;

import pl.droidsonroids.gif.GifDrawable;

/**
 * Created by dasunsy on 15/12/18.
 */
public class GifChatHelper {
    public final static int THRESHOLD_SIZE = 1024 << 10; //kb

    public static boolean isGif(String gifPath) {
        Movie movie = Movie.decodeFile(gifPath);

        return movie != null;
    }


    //TODO 统一用 mediaId 去维护本地的图片数据, 避免重复的写读
    public static byte[] getChatMsgGifByte(Context context, ChatPostMessage chatPostMessage) {
        byte[] gifBytes = new byte[0];
        String mediaId = StringUtils.EMPTY;
        if(chatPostMessage instanceof FileTransferChatMessage) {
            FileTransferChatMessage fileTransferChatMessage = (FileTransferChatMessage) chatPostMessage;
            mediaId = fileTransferChatMessage.mediaId;
            gifBytes = FileStreamHelper.readFile(fileTransferChatMessage.filePath);

        } else if(chatPostMessage instanceof ImageChatMessage) {
            ImageChatMessage imageChatMessage = (ImageChatMessage) chatPostMessage;
            if(!StringUtils.isEmpty(imageChatMessage.filePath) && FileUtil.isExist(imageChatMessage.filePath)) {
                gifBytes = FileStreamHelper.readFile(imageChatMessage.filePath);
            }

            mediaId = ((ImageChatMessage)chatPostMessage).mediaId;
        }

        if (ArrayUtil.isEmpty(gifBytes)) {
            gifBytes = GifShowHelper.getGifByte(context, chatPostMessage.deliveryId, mediaId);
        }

        return gifBytes;
    }

    public static boolean isOverSize(String gifPath) {
        File file = new File(gifPath);
        return file.exists() && THRESHOLD_SIZE < file.length();

    }

    @SuppressLint("StaticFieldLeak")
    public static void show(final Context context, final ImageView view, final ImageChatMessage imageChatMessage, final OnLoadGifListener onLoadGifListener) {
        //update cover firstly
        showThumb(context, view, imageChatMessage, onLoadGifListener);

        //secondly, load the gif
        new AsyncTask<Void, Integer, byte[]>() {
            @Override
            protected byte[] doInBackground(Void... params) {
                //update cover firstly
//                publishProgress(0);

                byte[] gifByte = getChatMsgGifByte(context, imageChatMessage);

                if(ArrayUtil.isEmpty(gifByte) && !MediaCenterHttpURLConnectionUtil.getInstance().isDownloading(imageChatMessage.mediaId)) {
//                    publishProgress(0);
                    //新规则下超过300kb 的 gif 不自动下载
                    if(AtworkConfig.GIF_AUTO_DOWNLOAD_THRESHOLD_SIZE < imageChatMessage.info.size) {
                        publishProgress(1);
                        return null;
                    }

                    MediaCenterSyncNetService mediaCenterSyncNetService = MediaCenterSyncNetService.getInstance();

                    String filePath = ImageShowHelper.getOriginalPath(context, imageChatMessage.mediaId);
                    mediaCenterSyncNetService.getMediaContent(BaseApplicationLike.baseContext, imageChatMessage.mediaId, filePath, MediaCenterHttpURLConnectionUtil.DOWNLOAD_TYPE.ORIGINAL);

                    gifByte = ImageShowHelper.getOriginalImage(context, imageChatMessage.mediaId);
                }

                publishProgress(2);

                return gifByte;
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                if(null != view.getTag() && imageChatMessage.deliveryId.equals(view.getTag())) {

                    if(0 == values[0]) {
                        //first show the cover of gif
                        showThumb(context, view, imageChatMessage, onLoadGifListener);
                    } else if(1 == values[0]) {
                        if(null != onLoadGifListener) {
                            onLoadGifListener.overSize(true);
                        }

                    }  else if(2 == values[0]) {
                        if (null != onLoadGifListener) {
                            onLoadGifListener.overSize(false);
                        }

                    }

                }


            }

            @Override
            protected void onPostExecute(byte[] gifByte) {
                if(null != view.getTag() && imageChatMessage.deliveryId.equals(view.getTag())) {

                    if(!ArrayUtil.isEmpty(gifByte)) {
                        try {
                            GifDrawable gifDrawable = new GifDrawable(gifByte);
                            view.setImageDrawable(gifDrawable);

                            Bitmap coverBitmap = gifDrawable.getCurrentFrame();
                            if(null != onLoadGifListener) {
                                onLoadGifListener.success(coverBitmap);
                            }

                            coverBitmap.recycle();
                            coverBitmap = null;

                        } catch (IOException e) {
                            e.printStackTrace();

                            if(null != onLoadGifListener) {
                                onLoadGifListener.failed();
                            }
                        }
                    }

                }
            }
        }.executeOnExecutor(ImageThreadPoolExecutor.getInstance());

    }

    private static void showThumb(Context context, ImageView view, ImageChatMessage imageChatMessage, OnLoadGifListener mOnLoadGifListener) {
        Bitmap thumbBitmap = BitmapCache.getBitmapCache().getContentBitmap(context, imageChatMessage);

        if(null != view.getTag() && imageChatMessage.deliveryId.equals(view.getTag())) {
            if(null != thumbBitmap) {
                view.setImageBitmap(thumbBitmap);

                if(null != mOnLoadGifListener) {
                    mOnLoadGifListener.settingThumb(thumbBitmap);
                }
            } else {
                //set loading or error holding cover
                view.setImageBitmap(BitmapFactory.decodeResource(view.getResources(), R.mipmap.loading_gray_holding));
            }

        }


    }


    interface OnLoadGifListener {
        void settingThumb(Bitmap bitmap);
        void overSize(boolean isOverSize);
        void success(Bitmap coverBitmap);
        void failed();
    }
}
