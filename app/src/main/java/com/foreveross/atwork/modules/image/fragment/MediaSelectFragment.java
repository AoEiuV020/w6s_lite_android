package com.foreveross.atwork.modules.image.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.foreveross.atwork.R;
import com.foreveross.atwork.component.ProgressDialogHelper;
import com.foreveross.atwork.cordova.plugin.WorkPlusImagesPlugin;
import com.foreveross.atwork.cordova.plugin.model.ChooseMediasRequest;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.manager.FileAlbumService;
import com.foreveross.atwork.infrastructure.model.file.FileData;
import com.foreveross.atwork.infrastructure.model.file.MediaBucket;
import com.foreveross.atwork.infrastructure.model.file.MediaItem;
import com.foreveross.atwork.infrastructure.model.file.SelectMediaType;
import com.foreveross.atwork.infrastructure.model.file.VideoItem;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.modules.chat.util.FileDataUtil;
import com.foreveross.atwork.modules.file.activity.FileSelectActivity;
import com.foreveross.atwork.modules.file.fragement.BasicFileSelectFragment;
import com.foreveross.atwork.modules.image.activity.MediaPreviewActivity;
import com.foreveross.atwork.modules.image.activity.MediaSelectActivity;
import com.foreveross.atwork.modules.image.adapter.MediaSelectAdapter;
import com.foreveross.atwork.modules.image.component.MediaSelectItemView;
import com.foreveross.atwork.modules.image.listener.MediaAlbumListener;
import com.foreveross.atwork.utils.FileHelper;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * ????????????????????????
 * ???????????????????????????????????????????????????????????????????????????????????????????????????????????????Fragment
 * ???????????????????????????FileSelectActivity???ImageSelectActivity?????????????????????????????????Activity?????????????????????????????????
 * Created by ReyZhang on 2015/4/28.
 */
public class MediaSelectFragment extends BasicFileSelectFragment {

    public static final String TAG = MediaSelectFragment.class.getSimpleName();

    private static final int MSG_LOADING_IMAGE_BUCKET_COMPLETE = 0x456;

    private GridView mMediaGridView;

    private MediaSelectAdapter mMediaSelectAdapter;
    private Activity mActivity;

    //????????????
    public List<MediaBucket> mMediaBucket = new ArrayList<>();
    //????????????
    private List<MediaItem> mMediaList = new ArrayList<>();
    //??????????????????
    private FileAlbumService mFileAlbumHelper;
    //  ?????????????????????
    private MediaAlbumListener mMediaAlbumListener;
    //??????????????????
    private List<MediaItem> mSelectedMediaList;
    //??????????????????
    private List<FileData> mSelectedFileList;

    //??????activity????????? ????????????activity???
    private boolean mIsHandleImageFile = false;

    //cordova????????????????????????activity???
    private boolean mCordovaSingleImage = false;

    private boolean mIsImageCrop = false;

    private ChooseMediasRequest mChooseMediasRequest;
    private boolean mIsFromCordova;

    private int mWhat;

    private List<SelectMediaType> mMediaSelectTypeList = new ArrayList<>();

    private ProgressDialogHelper mProgressDialogHelper;

    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mWhat = msg.what;
            switch (mWhat) {
                case MSG_LOADING_IMAGE_BUCKET_COMPLETE:
                    break;
            }
        }
    };


    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(FileAlbumService.ACTION_LOAD_ALBUM_END.equals(action)) {
                refreshData();
            }
        }
    };

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mFileAlbumHelper = FileAlbumService.getInstance();
        mActivity = activity;

        //todo activity fragment ?????????????????????
        if (activity instanceof MediaSelectActivity) {
            mMediaAlbumListener = (MediaSelectActivity) activity;
            MediaSelectActivity mediaSelectActivity = (MediaSelectActivity) mActivity;
            mSelectedMediaList = mediaSelectActivity.mMediaSelectedList;
            mCordovaSingleImage = mediaSelectActivity.mFromCordovaSingleSelect;
            mIsImageCrop = mediaSelectActivity.mIsImageCrop;

            mChooseMediasRequest = mediaSelectActivity.mChooseMediasRequest;
            mIsFromCordova = mediaSelectActivity.mIsFromCordova;
            mMediaSelectTypeList = mediaSelectActivity.mMediaSelectTypeList;

        } else if (activity instanceof FileSelectActivity) {
            mIsHandleImageFile = true;

            mSelectedFileList = ((FileSelectActivity) activity).mSelectedFileData;
            convertFileDataList2ImageItemList();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_media, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initData();

        registerBroadcast();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        unregisterBroadcast();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeMessages(mWhat);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    @Override
    protected void findViews(View view) {
        mMediaGridView = view.findViewById(R.id.media_gridview);
        mMediaSelectAdapter = new MediaSelectAdapter(mActivity, mMediaList, mSelectedMediaList);
        mMediaSelectAdapter.setIsFromCordova(mCordovaSingleImage || mIsFromCordova && isSingleImgSelectLimit());
        mMediaGridView.setAdapter(mMediaSelectAdapter);

        mMediaGridView.setOnItemClickListener(mItemClickListener);
    }

    @Override
    protected View getFakeStatusBar() {
        if(mActivity instanceof MediaSelectActivity) {
            MediaSelectActivity mediaSelectActivity = (MediaSelectActivity) mActivity;
            return mediaSelectActivity.getVFakeStatusBar();
        }
        return null;
    }

    private void registerBroadcast() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(FileAlbumService.ACTION_LOAD_ALBUM_END);

        LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).registerReceiver(mBroadcastReceiver, intentFilter);
    }

    private void unregisterBroadcast() {
        LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).unregisterReceiver(mBroadcastReceiver);

    }

    private AdapterView.OnItemClickListener mItemClickListener = (parent, view, position, id) -> {
        MediaSelectItemView item = (MediaSelectItemView) view;
        MediaItem mediaItem = mMediaList.get(position);
        if (mediaItem == null) {
            return;
        }

        if (mediaItem instanceof VideoItem) {
            VideoItem videoItem = (VideoItem) mediaItem;
            if(AtworkConfig.VIDEO_IN_CHAT_MAX_SELECT < videoItem.getDuration()) {
                toast(R.string.select_video_duration_max);
                return;
            }
        }


        if(mIsHandleImageFile) {

            handleFileItemClick(item, mediaItem);

        } else {
            handleImageItemClick(item, mediaItem);

        }

    };

    private void handleSingleSelectImg(MediaItem imageItem) {
        if (mIsImageCrop) {
            ((MediaSelectActivity) mActivity).singleImageBackToCordovaPlugin(imageItem);

        } else {
            List<MediaItem> singleList = new ArrayList<>();
            singleList.add(imageItem);
            imageItem.isSelected = true;

            Intent previewIntent = MediaPreviewActivity.getImagePreviewIntent(mActivity, MediaPreviewActivity.FromAction.IMAGE_SELECT);
            previewIntent.putExtra(MediaPreviewActivity.INTENT_IMAGE_SELECTED_LIST, (Serializable) singleList);
            previewIntent.putExtra(MediaSelectActivity.DATA_SELECT_FULL_MODE, ((MediaSelectActivity) mActivity).isFullMode());
            previewIntent.putExtra(MediaSelectActivity.DATA_OPEN_FULL_MODE_SELECT, ((MediaSelectActivity) mActivity).isOpenFullModeSelect());
            previewIntent.putExtra(WorkPlusImagesPlugin.DATA_CHOOSE_IMAGE_REQUEST, mChooseMediasRequest);
            previewIntent.putExtra(WorkPlusImagesPlugin.ACTION_SINGLE_SELECT_IMAGE_WITH_CROP, ((MediaSelectActivity) mActivity).isSingleMode());
            mActivity.startActivityForResult(previewIntent, MediaSelectActivity.REQUEST_IMAGE_PREVIEW_CODE);

        }
    }

    private void handleFileItemClick(MediaSelectItemView itemView, MediaItem imageItem) {
        if (!imageItem.isSelected) {
            FileData fileData = FileDataUtil.convertImageItem2FileData(imageItem);
            if(checkFileSelected(fileData)) {
                return;
            }

        }

        imageItem.isSelected = !imageItem.isSelected;
        refreshFileItemList(imageItem);
        refreshMediaItemList(itemView, imageItem);
    }

    /**
     * ????????????????????????
     *
     * @param imageItem
     */
    private void refreshFileItemList(MediaItem imageItem) {
        FileData fileData = FileDataUtil.convertImageItem2FileData(imageItem);
        refreshFileItemList(fileData);
    }

    /**
     * ???????????????????????????
     */
    private void handleImageItemClick(MediaSelectItemView item, MediaItem mediaItem) {
        if (!mediaItem.isSelected) {

            if (checkMediaSelected(mediaItem)) {
                return;
            }
        }

        mediaItem.isSelected = !mediaItem.isSelected;

        refreshMediaItemList(item, mediaItem);
    }


    private void refreshMediaItemList(MediaSelectItemView selectItem, MediaItem mediaItem) {

        boolean isCheckBefore = false;
        for (MediaItem selectedMediaItem : mSelectedMediaList) {
            if (selectedMediaItem == null) {
                continue;
            }
            if (mediaItem.filePath.equalsIgnoreCase(selectedMediaItem.filePath)) {
                isCheckBefore = true;

                mSelectedMediaList.remove(selectedMediaItem);
                selectItem.setChecked(false);
                if (mIsHandleImageFile) {
                    ((FileSelectActivity) mActivity).onSelectFileSizeUpdate();
                } else {
                    ((MediaSelectActivity) mActivity).onBtnUpdate();
                }
                break;
            }
        }
        if (isCheckBefore) {
            return;
        }
        makeFileSizeCompatible(mediaItem);

        mSelectedMediaList.add(mediaItem);
        selectItem.setChecked(true);
        if (mIsHandleImageFile) {
            ((FileSelectActivity) mActivity).onSelectFileSizeUpdate();
        } else {
            ((MediaSelectActivity) mActivity).onBtnUpdate();
        }
    }

    private void makeFileSizeCompatible(MediaItem mediaItem) {
        if (mediaItem.size == 0) {
            File file = new File(mediaItem.filePath);
            if (file.exists()) {
                mediaItem.size = file.length();
            }
        }
    }

    /**
     * ??????????????????????????????????????????
     */
    private void initData() {
        mProgressDialogHelper = new ProgressDialogHelper(mActivity);
        mProgressDialogHelper.show(R.string.images_loading);

        refreshData();

    }

    private void refreshData() {
        if(0 < mMediaSelectAdapter.getCount()) {
            return;
        }


        LinkedHashMap<String, MediaBucket> allBuckets = FileAlbumService.getInstance().getAllBucketMap(mMediaSelectTypeList);
        if(null != allBuckets) {
            mMediaBucket = new ArrayList<>(allBuckets.values());
            if (mMediaSelectTypeList.contains(SelectMediaType.VIDEO)) {
                mMediaList = allBuckets.get(FileAlbumService.ALL_MEDIA_ALBUM_KEY).getMediaList();

            } else {
                mMediaList = allBuckets.get(FileAlbumService.ALL_IMAGE_ALBUM_KEY).getMediaList();

            }


            if (mMediaSelectAdapter != null) {
                mMediaSelectAdapter.setImageList(mMediaList, mSelectedMediaList);
                if (!mIsHandleImageFile) {
                    mMediaAlbumListener.onAlbumLoadingSuccess(mMediaBucket);
                }
            }


            mProgressDialogHelper.dismiss();


        } else {
            FileAlbumService.getInstance().checkRefreshAllBucketList();
        }
    }

    /**
     * ?????????????????????
     *
     * @param pos
     */
    public void onImageAlbumSelected(int pos) {
        if (mMediaBucket == null || mMediaSelectAdapter == null || mActivity == null) {
            return;
        }
        MediaBucket bucket = mMediaBucket.get(pos);
        if (bucket == null) {
            return;
        }
        mMediaList = bucket.getMediaList();
        mMediaSelectAdapter.setImageList(mMediaList, mSelectedMediaList);
    }

    /**
     * ?????????????????????
     */
    public void refresh() {
        if (mMediaBucket == null || mMediaSelectAdapter == null || mActivity == null) {
            return;
        }
        mMediaSelectAdapter.setImageList(mMediaList, mSelectedMediaList);
    }

    /**
     * ???????????????????????????????????????????????????????????????
     */
    private void convertFileDataList2ImageItemList() {
        //??????????????????new??????list
        mSelectedMediaList = null;
        mSelectedMediaList = new ArrayList<>();
        if (mSelectedFileList == null) {
            return;
        }
        if (mSelectedFileList.isEmpty()) {
            return;
        }

        for (FileData fileData : mSelectedFileList) {
            if (fileData == null) {
                continue;
            }
            //???????????????????????????
            if (!fileData.fileType.equals(FileData.FileType.File_Image)) {
                return;
            }
            MediaItem imageItem = FileDataUtil.convertFileData2ImageItem(fileData);
            mSelectedMediaList.add(imageItem);
        }
    }


    /**
     * ???????????????????????????????????????
     *
     * @param fileData
     */
    private void refreshFileItemList(FileData fileData) {
        List<FileData> fileDataRemovedList = new ArrayList<>();
        for (FileData data : mSelectedFileList) {
            if (data == null) {
                continue;
            }
            if (data.equals(fileData)) {
                fileDataRemovedList.add(data);
            }
        }
        if (!ListUtil.isEmpty(fileDataRemovedList)) {
            mSelectedFileList.removeAll(fileDataRemovedList);
            return;
        }

        if (fileData.size == 0) {
            File file = new File(fileData.filePath);
            if (file.exists()) {
                fileData.size = file.length();
            }
        }
        mSelectedFileList.add(fileData);
    }

    @Override
    protected boolean onBackPressed() {
        getActivity().finish();
        return false;
    }

    @Override
    protected void refreshFileData(FileData fileData) {
        refreshFileItemList(fileData);
    }

    @Override
    protected List<FileData> getFileSelectedList() {
        return mSelectedFileList;
    }


    /**
     * ??????????????????????????????????????????
     * @param imageItem
     * @return ?????? "????????????"
     * */
    protected boolean checkMediaSelected(MediaItem imageItem) {
        if(mCordovaSingleImage || isSingleImgSelectLimit()) {

            if(isImgTotalChooseSizeThreshold(imageItem)) {
                toast(getString(R.string.max_total_select_image_size, FileHelper.getFileSizeStr(getMaxImgTotalChooseSize())));
                return true;
            }

            handleSingleSelectImg(imageItem);

            return true;
        }

        if (isImgChosenCountThreshold()) {
            if (mMediaSelectTypeList.contains(SelectMediaType.VIDEO)) {
                toast(getString(R.string.max_select_media, getMaxImgChooseCount() + ""));

            } else {
                toast(getString(R.string.max_select_images, getMaxImgChooseCount() + ""));

            }
            return true;
        }

        if(isImgSingleChooseSizeThreshold(imageItem)) {
            toast(getString(R.string.max_single_select_image_size, FileHelper.getFileSizeStr(getMaxImgSingleChooseSize())));
            return true;
        }

        if(isImgTotalChooseSizeThreshold(imageItem)) {
            toast(getString(R.string.max_total_select_image_size, FileHelper.getFileSizeStr(getMaxImgTotalChooseSize())));
            return true;
        }

        if(mActivity instanceof MediaSelectActivity) {
            MediaSelectActivity imageSelectActivity = ((MediaSelectActivity)mActivity);

            if(imageSelectActivity.isFullMode() && imageSelectActivity.isFullImgOverLimit(imageItem.size)) {
                toast(getString(R.string.full_img_size_limit, FileHelper.getFileSizeStr(AtworkConfig.CHAT_FULL_IMG_SELECT_LIMIT_SIZE)));
                return true;
            }
        }

        return false;
    }

    private boolean isImgChosenCountThreshold() {
        return mSelectedMediaList.size() >= getMaxImgChooseCount();
    }

    private boolean isImgSingleChooseSizeThreshold(MediaItem selectImageItem) {
        long maxSingleChooseSize = getMaxImgSingleChooseSize();
        return -1 != maxSingleChooseSize && selectImageItem.size > maxSingleChooseSize;
    }

    private boolean isImgTotalChooseSizeThreshold(MediaItem selectImageItem) {
        long maxTotalChooseSize = getMaxImgTotalChooseSize();
        if(-1 != maxTotalChooseSize) {
            //get total size
            int totalSize = 0;
            for(MediaItem imageItem : mSelectedMediaList) {
                totalSize += imageItem.size;
            }

            totalSize += selectImageItem.size;

            return totalSize > maxTotalChooseSize;
        }

        return  false;
    }



    public boolean isChooseImageLimit() {
        return null != mChooseMediasRequest;
    }

    public boolean isSingleImgSelectLimit() {
        return isChooseImageLimit() && mChooseMediasRequest.isSingleType();
    }

    public int getMaxImgChooseCount() {
        if(isChooseImageLimit()) {
            return mChooseMediasRequest.mFileLimit.mMaxSelectCount;
        }

        return 9;
    }

    public long getMaxImgSingleChooseSize() {
        if(isChooseImageLimit()) {
            return mChooseMediasRequest.mFileLimit.mSingleSelectSize;
        }

        return -1;
    }

    public long getMaxImgTotalChooseSize() {
        if(isChooseImageLimit()) {
            return mChooseMediasRequest.mFileLimit.mTotalSelectSize;
        }

        return -1;
    }

}
