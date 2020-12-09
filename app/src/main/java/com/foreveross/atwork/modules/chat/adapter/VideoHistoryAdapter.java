package com.foreveross.atwork.modules.chat.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.foreverht.threadGear.ImageThreadPoolExecutor;
import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.AtWorkDirUtils;
import com.foreveross.atwork.infrastructure.utils.BitmapUtil;
import com.foreveross.atwork.infrastructure.utils.FileUtil;
import com.foreveross.atwork.infrastructure.utils.ImageShowHelper;
import com.foreveross.atwork.infrastructure.utils.encryption.EncryptCacheDisk;
import com.foreveross.atwork.infrastructure.utils.file.FileStreamHelper;
import com.foreveross.atwork.modules.chat.component.chat.PopupMicroVideoHistoryDialog;
import com.foreveross.atwork.modules.chat.component.chat.PopupMicroVideoRecordingDialog;
import com.foreveross.atwork.utils.FileHelper;
import com.foreveross.atwork.utils.ImageCacheHelper;

import java.io.File;
import java.util.List;

/**
 * Created by dasunsy on 15/12/29.
 */
public class VideoHistoryAdapter extends BaseAdapter{
    private PopupMicroVideoRecordingDialog.OnMicroVideoTakingListener mMicroVideoTakingListener;

    private PopupMicroVideoHistoryDialog mDialog;
    private List<String> mFileNameList;

    private boolean mIsEditMode = false;
    private int mSendModePosition = -1;

    private Context mContext;

    public VideoHistoryAdapter(Context context, PopupMicroVideoHistoryDialog dialog, List<String> fileNameList) {
        mDialog = dialog;
        mFileNameList = fileNameList;
        mContext = context;
    }

    @Override
    public int getCount() {
        return mFileNameList.size();
    }

    @Override
    public String getItem(int position) {
        return mFileNameList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return -1;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if(null == convertView) {
            LayoutInflater inflater = (LayoutInflater) mDialog.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_video_select, null);
            holder = new ViewHolder();

            holder.flItemVideo = convertView.findViewById(R.id.fl_item_video);
            holder.ivVideoCover = convertView.findViewById(R.id.iv_cover);
            holder.ivDel = convertView.findViewById(R.id.iv_del);
            holder.tvSend = convertView.findViewById(R.id.tv_tip_send);


            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        registerListener(position, holder);

        final String coverPath = ImageShowHelper.getThumbnailPath(mContext, getItem(position));
        if(mIsEditMode) {
            holder.ivDel.setVisibility(View.VISIBLE);

        } else {
            holder.ivDel.setVisibility(View.GONE);

        }

        if(mSendModePosition == position) {
            holder.tvSend.setVisibility(View.VISIBLE);

        } else {
            holder.tvSend.setVisibility(View.GONE);

        }
        if(!"plus_button".equalsIgnoreCase(getItem(position))) {
            holder.ivVideoCover.setScaleType(ImageView.ScaleType.CENTER_CROP);
            ImageCacheHelper.displayImage(coverPath, holder.ivVideoCover, ImageCacheHelper.getRectOptions(R.mipmap.loading_chat_size, -1), new ImageCacheHelper.ImageLoadedListener() {
                @Override
                public void onImageLoadedComplete(Bitmap bitmap) {

                }

                @Override
                public void onImageLoadedFail() {
                    //失败说明找不到相关的剪影图片，尝试从影片中剪影
                    final String path = AtWorkDirUtils.getInstance().getMicroVideoHistoryDir(mContext) + getItem(position) + ".mp4";
                    showImgFromRecreateThumb(path, coverPath, holder);


                }
            });

        } else {
            holder.ivVideoCover.setScaleType(ImageView.ScaleType.FIT_XY);
            holder.ivVideoCover.setImageResource(R.mipmap.video_add);
        }

        handlePlusButton(getItem(position), holder);
        return convertView;
    }

    @SuppressLint("StaticFieldLeak")
    private void showImgFromRecreateThumb(final String videoPath, final String coverPath, final ViewHolder holder) {

        if (!FileUtil.isExist(videoPath)) {
            return;
        }

        new AsyncTask<Void, Bitmap, Bitmap>() {
            @Override
            protected Bitmap doInBackground(Void... params) {
                String originalVideoPath = EncryptCacheDisk.getInstance().getOriginalFilePathAndCheck(videoPath, false);

                Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(originalVideoPath, MediaStore.Images.Thumbnails.MINI_KIND);
                byte[] thumbByte = BitmapUtil.compressImageForQuality(bitmap, AtworkConfig.CHAT_THUMB_SIZE);
                FileStreamHelper.saveFile(coverPath, thumbByte);
                return bitmap;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                if(null != bitmap) {
                    ImageCacheHelper.displayImage(coverPath, holder.ivVideoCover, ImageCacheHelper.getRoundOptions(R.mipmap.loading_chat_size, -1));
                }

            }
        }.executeOnExecutor(ImageThreadPoolExecutor.getInstance());
    }

    private void registerListener(final int position, ViewHolder holder) {
        holder.ivDel.setOnClickListener(v -> {
            File videoFile = FileHelper.getMicroVideoFileSendById(mContext, getItem(position));
            if(videoFile.exists()) {

                if(videoFile.delete()){
                    mFileNameList.remove(getItem(position));
                    notifyDataSetChanged();

                }

            }
        });

        holder.ivVideoCover.setOnClickListener(v -> {
            if("plus_button".equalsIgnoreCase(getItem(position))) {
                mDialog.dismiss();

            } else {
                if(!mIsEditMode) {
                    mSendModePosition = position;
                    notifyDataSetChanged();
                }
            }
        });

        holder.tvSend.setOnClickListener(v -> {
            if(null != mMicroVideoTakingListener) {
                mMicroVideoTakingListener.onMicroVideoFile(false, FileHelper.getMicroVideoFileSendPathById(mContext, getItem(position)));

                mSendModePosition = -1;
                notifyDataSetChanged();
            }
        });
    }

    public void setEditMode(boolean isEditMode) {
        mIsEditMode = isEditMode;
    }

    public void setSendModePos(int pos) {
        mSendModePosition = pos;
    }

    public boolean getEditMode() {
        return mIsEditMode;
    }

    public static class ViewHolder {
        FrameLayout flItemVideo;
        ImageView ivVideoCover;
        ImageView ivDel;
        TextView tvSend;
    }

    public void setMicroVideoTakingListener(PopupMicroVideoRecordingDialog.OnMicroVideoTakingListener listener) {
        mMicroVideoTakingListener = listener;
    }

    private void handlePlusButton(String itemName, ViewHolder holder) {
        if("plus_button".equalsIgnoreCase(itemName) && mIsEditMode) {
            holder.flItemVideo.setVisibility(View.GONE);

        } else {
            holder.flItemVideo.setVisibility(View.VISIBLE);

        }
    }

}
