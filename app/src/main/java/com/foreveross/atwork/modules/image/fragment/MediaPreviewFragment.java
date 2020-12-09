package com.foreveross.atwork.modules.image.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.foreverht.db.service.daoService.FileDaoService;
import com.foreverht.workplus.ui.component.dialogFragment.CommonPopSelectData;
import com.foreverht.workplus.ui.component.dialogFragment.W6sSelectDialogFragment;
import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.net.MediaCenterHttpURLConnectionUtil;
import com.foreveross.atwork.api.sdk.net.model.DownloadFileParamsMaker;
import com.foreveross.atwork.api.sdk.upload.MediaCenterNetManager;
import com.foreveross.atwork.component.ProgressDialogHelper;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.file.ImageItem;
import com.foreveross.atwork.infrastructure.model.file.MediaItem;
import com.foreveross.atwork.infrastructure.model.file.VideoItem;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.ArrayUtil;
import com.foreveross.atwork.infrastructure.utils.AtWorkDirUtils;
import com.foreveross.atwork.infrastructure.utils.BitmapUtil;
import com.foreveross.atwork.infrastructure.utils.FileUtil;
import com.foreveross.atwork.infrastructure.utils.ImageShowHelper;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.infrastructure.utils.LogUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.modules.chat.component.PopupListDialogSupportPack;
import com.foreveross.atwork.modules.file.adapter.EnlargedImageAdapter;
import com.foreveross.atwork.modules.file.component.ItemLargeDetailViewPager;
import com.foreveross.atwork.modules.image.component.GestureDetector;
import com.foreveross.atwork.modules.image.component.ItemEnlargeImageView;
import com.foreveross.atwork.modules.image.component.ItemVideoPreviewView;
import com.foreveross.atwork.modules.image.component.ScaleGestureDetector;
import com.foreveross.atwork.modules.image.listener.ImageSwitchListener;
import com.foreveross.atwork.modules.qrcode.service.QrcodeManager;
import com.foreveross.atwork.qrcode.zxing.decode.BitmapQrcodeDecoder;
import com.foreveross.atwork.support.BackHandledFragment;
import com.foreveross.atwork.utils.AtworkToast;
import com.foreveross.atwork.utils.AtworkUtil;
import com.foreveross.atwork.utils.GifChatHelper;
import com.foreveross.atwork.utils.ImageCacheHelper;
import com.foreveross.watermark.core.WaterMarkUtil;
import com.google.zxing.Result;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import pl.droidsonroids.gif.GifDrawable;

/**
 * Created by ReyZhang on 2015/5/4.
 */
public class MediaPreviewFragment extends BackHandledFragment {

    public static final String TAG = MediaPreviewFragment.class.getSimpleName();

    public static final String ARGUMENT_CURRENT_IMAGE_LIST = "current_image_list";

    public static final String ARGUMENT_CURRENT_IMAGE_POS = "current_image_pos";

    public static final String ARGUMENT_FROM_CORDOVA = "from_cordova";
    public static final String ARGUMENT_SHOW_WATERMARK = "show_watermark";

    private Activity mActivity;

    private ItemLargeDetailViewPager mViewPager;

    private List<MediaItem> mImageList;

    private ImagePagerAdapter mImagePreviewAdapter;

    private ImageSwitchListener mImageSwitchListener;

    private boolean mOnScale = false;

    private boolean mOnPagerScroll = false;

    private boolean mPaused;

    private GestureDetector mGestureDetector;

    private ScaleGestureDetector mScaleGestureDetector;

    private int mCurrentPos;

    private BitmapQrcodeDecoder mQrcodeDecoder;

    private ArrayList<String> mLongClickList = new ArrayList<>();

    private ProgressDialogHelper mProgressDialogHelper;

    private boolean mFromCordova;

    private boolean mShowWatermark;
    private View mWatermark;



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
        mImageSwitchListener = (ImageSwitchListener) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mQrcodeDecoder = new BitmapQrcodeDecoder(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image_preview, container, false);
        findViews(view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupData();

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();

        tryPauseVideo();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        tryReleaseVideo();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    protected void findViews(View view) {
        mViewPager = view.findViewById(R.id.image_preview_viewpager);
        mProgressDialogHelper = new ProgressDialogHelper(getActivity());
        mWatermark = view.findViewById(R.id.watermark_bg);
    }

    private void setupData() {
        mImageList = (List<MediaItem>) getArguments().getSerializable(ARGUMENT_CURRENT_IMAGE_LIST);
        mCurrentPos = getArguments().getInt(ARGUMENT_CURRENT_IMAGE_POS, 0);
        mFromCordova = getArguments().getBoolean(ARGUMENT_FROM_CORDOVA, false);
        mShowWatermark = getArguments().getBoolean(ARGUMENT_SHOW_WATERMARK, false);
        if (mImageList == null) {
            return;
        }
        mImagePreviewAdapter = new ImagePagerAdapter();
        mViewPager.setAdapter(mImagePreviewAdapter);
        mViewPager.setOffscreenPageLimit(1);
        mViewPager.setOnPageChangeListener(new ItemLargeDetailViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                mOnPagerScroll = true;

            }

            @Override
            public void onPageSelected(int position, int prePosition) {
                mCurrentPos = position;
                mImageSwitchListener.onImageSwitch(mCurrentPos);

                MediaItem imageItem = mImageList.get(position);

                if (imageItem instanceof ImageItem) {
                    if (imageItem.filePath.startsWith("http")) {
                        String fileName =  ImageCacheHelper.getImgName(imageItem.filePath);
                        String path = ImageShowHelper.getFullPath(BaseApplicationLike.baseContext, fileName);

                        if (!FileUtil.isExist(path)) {
                            downloadFromUrl(imageItem.filePath, position);

                        }
                    }

                }


                tryPauseVideo(prePosition);

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager.SCROLL_STATE_DRAGGING) {
                    mOnPagerScroll = true;
                } else if (state == ViewPager.SCROLL_STATE_SETTLING) {
                    mOnPagerScroll = false;
                } else {
                    mOnPagerScroll = false;
                }

            }
        });
        setupOnTouchListeners(mViewPager);
        mViewPager.setCurrentItem(mCurrentPos);
        mImageSwitchListener.onImageSwitch(mCurrentPos);
        handleWatermark();
    }

    private int getCurrentItem() {
        return mViewPager.getCurrentItem();
    }



    private void handleWatermark() {
        if (!mShowWatermark) {
            return;
        }
        mWatermark.setVisibility(View.VISIBLE);
        WaterMarkUtil.setLoginUserWatermark(mActivity, mWatermark, -1, -1, "");
        return;
    }

    private class ImageGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public void onLongPress(MotionEvent e) {
            if(mOnScale) {
                return;
            }

            handlePopupImageSelectDialog(mImageList.get(mCurrentPos).filePath);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (mOnScale) {
                return true;
            }
            if (mPaused) {
                return false;
            }
            View currentImageView = getCurrentImageView();
            if(null == currentImageView) {
                return true;
            }

            if (currentImageView instanceof ItemEnlargeImageView) {
                ItemEnlargeImageView imageView = (ItemEnlargeImageView) currentImageView;
                imageView.panBy(-distanceX, -distanceY);
                imageView.center(true, true);
            }


            return true;
        }

        @Override
        public boolean onUp(MotionEvent e) {
            return super.onUp(e);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
//            getActivity().finish();
            return false;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            if (mPaused) {
                return false;
            }
            View currentImageView = getCurrentImageView();
            if(null == currentImageView) {
                return true;
            }
            if (currentImageView instanceof ItemEnlargeImageView) {
                ItemEnlargeImageView imageView = (ItemEnlargeImageView) currentImageView;
                // Switch between the original scale and 3x scale.
                if (imageView.mBaseZoom < 1) {
                    if (imageView.getScale() > 2F) {
                        imageView.zoomTo(1f);
                    } else {
                        imageView.zoomToPoint(3f, e.getX(), e.getY());
                    }
                } else {
                    if (imageView.getScale() > (imageView.mMinZoom + imageView.mMaxZoom) / 2f) {
                        imageView.zoomTo(imageView.mMinZoom);
                    } else {
                        imageView.zoomToPoint(imageView.mMaxZoom, e.getX(),
                                e.getY());
                    }
                }
            }

            return true;
        }
    }


    private void setupOnTouchListeners(View rootView) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR_MR1) {
            mScaleGestureDetector = new ScaleGestureDetector(getActivity(), new ImageOnScaleGestureListener());
        }
        mGestureDetector = new GestureDetector(getActivity(), new ImageGestureListener());

        @SuppressLint("ClickableViewAccessibility") View.OnTouchListener rootListener = (v, event) -> {

            LogUtil.e("OnTouchListener preview ->  " + event.getAction());

            if (!mOnScale) {
                if (!mOnPagerScroll) {
                    mGestureDetector.onTouchEvent(event);
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR_MR1) {
                if (!mOnPagerScroll) {

                    try {
                        mScaleGestureDetector.onTouchEvent(event);
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    }
                }
            }
            View currentImageView = getCurrentImageView();
            if (null == currentImageView) {
                return true;
            }

            if (currentImageView instanceof ItemEnlargeImageView) {
                ItemEnlargeImageView imageView = (ItemEnlargeImageView) currentImageView;


                if (imageView.mBitmapDisplayed.getBitmap() == null) {
                    return true;
                }
                if (!mOnScale) {
                    Matrix m = imageView.getImageViewMatrix();
                    RectF rect = new RectF(0, 0, imageView.mBitmapDisplayed
                            .getBitmap().getWidth(), imageView.mBitmapDisplayed
                            .getBitmap().getHeight());
                    m.mapRect(rect);

                    if (!(rect.right > imageView.getWidth() + 0.1 && rect.left < -0.1)) {
                        try {
                            mViewPager.onTouchEvent(event);
                        } catch (ArrayIndexOutOfBoundsException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else if(currentImageView instanceof ItemVideoPreviewView) {
                try {
                    mViewPager.onTouchEvent(event);
                } catch (ArrayIndexOutOfBoundsException e) {
                    e.printStackTrace();

                }
            }



            return true;
        };

        rootView.setOnTouchListener(rootListener);
    }



    @Nullable
    private View getCurrentImageView() {
        return mImagePreviewAdapter.views.get((mViewPager.getCurrentItem()));
    }

    private class ImageOnScaleGestureListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {


        float currentScale;
        float currentMiddleX;
        float currentMiddleY;

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            View currentImageView = getCurrentImageView();
            if(null == currentImageView) {
                return;
            }

            if (currentImageView instanceof ItemEnlargeImageView) {
                final ItemEnlargeImageView imageView = (ItemEnlargeImageView) currentImageView;

                if (currentScale > imageView.mMaxZoom) {
                    imageView.zoomToNoCenterWithAni(currentScale / imageView.mMaxZoom, 1, currentMiddleX, currentMiddleY);
                    currentScale = imageView.mMaxZoom;
                    imageView.zoomToNoCenterValue(currentScale, currentMiddleX, currentMiddleY);
                } else if (currentScale < imageView.mMinZoom) {
                    imageView.zoomToNoCenterWithAni(currentScale, imageView.mMinZoom, currentMiddleX, currentMiddleY);
                    currentScale = imageView.mMinZoom;
                    imageView.zoomToNoCenterValue(currentScale, currentMiddleX, currentMiddleY);
                } else {
                    imageView.zoomToNoCenter(currentScale, currentMiddleX, currentMiddleY);
                }

                imageView.center(true, true);

                imageView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mOnScale = false;
                    }
                }, 1000);
            }

        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            mOnScale = true;
            return true;
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector, float mx, float my) {
            View currentImageView = getCurrentImageView();
            if(null == currentImageView) {
                return true;
            }

            if (currentImageView instanceof ItemEnlargeImageView) {
                ItemEnlargeImageView imageView = (ItemEnlargeImageView) currentImageView;

                float ns = imageView.getScale() * detector.getScaleFactor();
                currentScale = ns;
                currentMiddleX = mx;
                currentMiddleY = my;

                if (detector.isInProgress()) {
                    imageView.zoomToNoCenter(ns, mx, my);
                }
            }

            return true;
        }
    }

    private class ImagePagerAdapter extends EnlargedImageAdapter {
        public SparseArray<View> views = new SparseArray<>();

        @Override
        public int getCount() {
            return mImageList != null ? mImageList.size() : 0;
        }

        @Override
        public Object instantiateItem(View container, final int position) {


            MediaItem mediaItem = mImageList.get(position);
            if (mediaItem instanceof ImageItem) {
                final ItemEnlargeImageView cellView = new ItemEnlargeImageView(getActivity());
                cellView.setScaleType(ImageView.ScaleType.MATRIX);

                if(mediaItem.filePath.startsWith("http")) {

                    String fileName =  ImageCacheHelper.getImgName(mediaItem.filePath);
                    String path = ImageShowHelper.getFullPath(BaseApplicationLike.baseContext, fileName);

                    if(FileUtil.isExist(path)) {
                        displayLocalImg(cellView, path);

                    } else {
                        //当初次点击进来是viewPaper 的第一个, 并不会触发 viewpager 的 onPageSelected,
                        // 这时, 需要在该处初始化时开始下载原图
                        if ((0 == position)
                                && getCurrentItem() == position) {
                            downloadFromUrl(mediaItem.filePath, position);

                        }


                    }


                } else {
                    displayLocalImg(cellView, mediaItem.filePath);
                }


                cellView.setFocusableInTouchMode(true);
                ((ViewGroup) container).addView(cellView);
                views.put(position, cellView);

                return cellView;

            } else if(mediaItem instanceof VideoItem){
                ItemVideoPreviewView itemVideoPreviewView = new ItemVideoPreviewView(getActivity());

                itemVideoPreviewView.preview((VideoItem) mediaItem);

                ((ViewGroup) container).addView(itemVideoPreviewView);
                views.put(position, itemVideoPreviewView);

                return itemVideoPreviewView;

            }

            return null;
        }

        @Override
        public void destroyItem(View container, int position, Object object) {
            if (object instanceof ItemEnlargeImageView) {
                ItemEnlargeImageView imageView = (ItemEnlargeImageView) object;

                ((ViewGroup) container).removeView(imageView);

            } else if(object instanceof ItemVideoPreviewView) {
                ItemVideoPreviewView itemVideoPreviewView = (ItemVideoPreviewView) object;
                itemVideoPreviewView.release();

                ((ViewGroup) container).removeView(itemVideoPreviewView);

            }

            views.remove(position);
        }

        @Override
        public void startUpdate(View container) {
        }

        @Override
        public void finishUpdate(View container) {
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {
        }
    }

    private void downloadFromUrl(String imgUrl, int position) {

        String fileName =  ImageCacheHelper.getImgName(imgUrl);
        String path = ImageShowHelper.getFullPath(BaseApplicationLike.baseContext, fileName);


        mProgressDialogHelper.show();

        //下载原图片
        MediaCenterNetManager mediaCenterNetManager = new MediaCenterNetManager(BaseApplicationLike.baseContext);
        final String fullPath = ImageShowHelper.getFullPath(mActivity, fileName);
        MediaCenterNetManager.addMediaDownloadListener(new MediaCenterNetManager.MediaDownloadListener() {
            @Override
            public String getMsgId() {
                return fileName;
            }

            @Override
            public void downloadSuccess() {

                mProgressDialogHelper.dismiss();
                ItemEnlargeImageView preImageView = (ItemEnlargeImageView) mImagePreviewAdapter.views.get(position);
                if(null == preImageView) {
                    return;
                }

                displayLocalImg(preImageView, path);

            }

            @Override
            public void downloadFailed(int errorCode, String errorMsg, boolean doRefreshView) {


                mProgressDialogHelper.dismiss();

                if (-99 != errorCode && isAdded()) {
                    AtworkToast.showToast(getString(R.string.network_not_avaluable));
                }
            }

            @Override
            public void downloadProgress(double progress, double value) {
                int progressInt = (int) progress;

            }
        });

        mediaCenterNetManager.downloadFile(
                DownloadFileParamsMaker.Companion.newRequest().setMediaId(imgUrl)
                        .setDownloadId(fileName).setDownloadPath(fullPath).setDownloadType(MediaCenterHttpURLConnectionUtil.DOWNLOAD_TYPE.FILE)
        );
    }


    private void displayLocalImg(ImageView cellView, String imgPath) {
        boolean isGif = GifChatHelper.isGif(imgPath);
        if (isGif) {
            try {
                GifDrawable gifDrawable = new GifDrawable(imgPath);
                Bitmap holderBitmap = gifDrawable.getCurrentFrame();
                cellView.setImageBitmap(holderBitmap);
                cellView.setImageDrawable(gifDrawable);

                holderBitmap.recycle();
                holderBitmap = null;
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {

            DisplayImageOptions options = getLocalDisplayOption(imgPath);

            // 设置图片
            ImageCacheHelper.displayImage(imgPath, cellView, options, new ImageCacheHelper.ImageLoadedListener() {
                @Override
                public void onImageLoadedComplete(Bitmap bitmap) {
//                        cellView.setOnLongClickListener(v -> {
//
//                            handlePopupImageSelectDialog(imageItem.filePath);
//                            return false;
//                        });
                }

                @Override
                public void onImageLoadedFail() {
                    if (isAdded()) {
                        cellView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.loading_cover_size));
                    }
                }
            });

        }
    }

    private DisplayImageOptions getLocalDisplayOption(String imgPath) {
        DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder();
        builder.cacheInMemory(true);
        builder.cacheOnDisk(true);
        builder.considerExifParams(true);
        builder.bitmapConfig(Bitmap.Config.RGB_565);
        File file = new File(imgPath);
        if(AtworkConfig.CHAT_IMG_SHOW_LIMIT < file.length()) {
            builder.imageScaleType(ImageScaleType.NONE_SAFE);

        } else {
            builder.imageScaleType(ImageScaleType.NONE);


        }
        return builder.build();
    }

    private void tryPauseVideo() {
        tryPauseVideo(getCurrentItem());
    }

    private void tryPauseVideo(int pos) {
        View view = mImagePreviewAdapter.views.get((pos));
        if(null == view) {
            return;
        }

        if(view instanceof ItemVideoPreviewView) {
            ItemVideoPreviewView itemVideoPreviewView = (ItemVideoPreviewView) view;
            itemVideoPreviewView.pause();
        }
    }

    private void tryReleaseVideo() {
        View view = getCurrentImageView();
        if(null == view) {
            return;
        }

        if(view instanceof ItemVideoPreviewView) {
            ItemVideoPreviewView itemVideoPreviewView = (ItemVideoPreviewView) view;
            itemVideoPreviewView.release();
        }
    }


    @Override
    protected boolean onBackPressed() {
        getActivity().finish();
        return false;
    }

    @SuppressLint("StaticFieldLeak")
    private void handlePopupImageSelectDialog(String imgPath) {

        if (!mFromCordova) {
            return;
        }
        if (mShowWatermark) {
            return;
        }

        new AsyncTask<Void, Void, Result>() {
            Bitmap bitmap = null;
            @Override
            protected Result doInBackground(Void... params) {
                bitmap = ImageCacheHelper.loadImageSync(imgPath);
                //下面方法尝试去获取是否有二维码result

                Result result = null;
                if (null != bitmap) {
                    result = mQrcodeDecoder.getRawResult(bitmap);
                }
                return result;
            }

            @Override
            protected void onPostExecute(Result result) {

                if (isAdded()) {
                    popImageSelectDialog(result, bitmap, imgPath);
                }

            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void popImageSelectDialog(Result result, final Bitmap bitmap, String imgPath) {
        mLongClickList.clear();
        if (null != result) {
            mLongClickList.add(getResources().getString(R.string.qrcode_recognition));

        }

        if (needDownload()) {
            mLongClickList.add(getResources().getString(R.string.save_to_mobile));
        }

        if (!ListUtil.isEmpty(mLongClickList)) {
            boolean isGif = GifChatHelper.isGif(imgPath);
            W6sSelectDialogFragment W6sSelectDialogFragment = new W6sSelectDialogFragment();
            W6sSelectDialogFragment.setData(new CommonPopSelectData(mLongClickList, null))
                    .setDialogWidth(148)
                    .setTextContentCenter(true)
                    .setOnClickItemListener((position, item) -> {
                        setPopDialogClickEvent(result, bitmap, item, isGif);
                    })
                    .show(getChildFragmentManager(), "VIEW_IMAGE_DIALOG");
        }
    }

    private boolean needDownload() {
        if(AtworkConfig.ENCRYPT_CONFIG.isImageSaveIgnoringEncrypt()) {
            return true;
        }

        return !AtworkConfig.OPEN_DISK_ENCRYPTION;
    }

    /**
     * 设置弹出选择框的点击事件
     */
    @SuppressLint("StaticFieldLeak")
    private void setPopDialogClickEvent(Result result, final Bitmap bitmap, String item, boolean isGif) {
//                if (getResources().getString(R.string.forwarding_item).equals(item)) {
//                    //转发图片
//                    List<ChatPostMessage> chatPostMessages = new ArrayList<>();
//                    chatPostMessages.add(chatPostMessage);
//                    Intent intent = UserSelectActivity.getSendModeIntent(getActivity(), UserSelectActivity.SendMode.FORWARDS, true);
//                    intent.putExtra(UserSelectActivity.DATA_SEND_MESSAGES, (Serializable) chatPostMessages);
//                    startActivity(intent);
//                    return;
//                }
        if (getResources().getString(R.string.save_to_mobile).equals(item)) {
            //保存图片
            AndPermission
                    .with(mActivity)
                    .runtime()
                    .permission(Permission.WRITE_EXTERNAL_STORAGE, Permission.READ_EXTERNAL_STORAGE)
                    .onGranted(granted -> {
                        new AsyncTask<Void, Void, Boolean>() {
                            @Override
                            protected Boolean doInBackground(Void... params) {
                                byte[] content = BitmapUtil.Bitmap2Bytes(bitmap, false);

                                if (ArrayUtil.isEmpty(content)) {
                                    return false;
                                }

                                mProgressDialogHelper.show();

                                String path = ImageShowHelper.saveImageToGalleryAndGetPath(getActivity(), content, null, isGif);
                                if (!StringUtils.isEmpty(path)) {
                                    FileDaoService.getInstance().insertRecentFile(path);
                                }
                                return !StringUtils.isEmpty(path);

                            }

                            @Override
                            protected void onPostExecute(Boolean succeed) {

                                mProgressDialogHelper.dismiss();

                                if (succeed) {
                                    int galleryIndex = AtWorkDirUtils.getInstance().getGalleryDir(LoginUserInfo.getInstance().getLoginUserUserName(mActivity)).indexOf(AtworkConfig.APP_FOLDER);
                                    AtworkToast.showResToast(R.string.save_image_to_mobile_success, AtWorkDirUtils.getInstance().getGalleryDir(LoginUserInfo.getInstance().getLoginUserUserName(mActivity)).substring(galleryIndex));
                                    return;
                                }
                                AtworkToast.showResToast(R.string.save_image_to_mobile_fail);
                            }
                        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    })
                    .onDenied(denied ->  AtworkUtil.popAuthSettingAlert(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE))
                    .start();

            return;
        }
        if (getResources().getString(R.string.qrcode_recognition).equals(item)) {

            if (null != result) {

                String resultText = result.getText();

                final ProgressDialogHelper progressDialog = new ProgressDialogHelper(mActivity);
                progressDialog.show(mActivity.getResources().getString(R.string.loading));

                new Handler().postDelayed(() -> {

                    progressDialog.dismiss();

                    realHandleQrcodeResult(resultText);

                }, 1000L);

            }
            return;
        }

    }

    /**
     * 设置弹出选择框的点击事件
     */
    @SuppressLint("StaticFieldLeak")
    private void setPopDialogClickEvent(Result result, final Bitmap bitmap, PopupListDialogSupportPack commonPopDialog, boolean isGif) {
        commonPopDialog.setOnListItemClickListener(item -> {
            if (isAdded()) {
//                if (getResources().getString(R.string.forwarding_item).equals(item)) {
//                    //转发图片
//                    List<ChatPostMessage> chatPostMessages = new ArrayList<>();
//                    chatPostMessages.add(chatPostMessage);
//                    Intent intent = UserSelectActivity.getSendModeIntent(getActivity(), UserSelectActivity.SendMode.FORWARDS, true);
//                    intent.putExtra(UserSelectActivity.DATA_SEND_MESSAGES, (Serializable) chatPostMessages);
//                    startActivity(intent);
//                    return;
//                }
                if (getResources().getString(R.string.save_to_mobile).equals(item)) {
                    //保存图片

                    new AsyncTask<Void, Void, Boolean>() {
                        @Override
                        protected Boolean doInBackground(Void... params) {
                            byte[] content = BitmapUtil.Bitmap2Bytes(bitmap, false);

                            if (ArrayUtil.isEmpty(content)) {
                                return false;
                            }

                            mProgressDialogHelper.show();

                            String path = ImageShowHelper.saveImageToGalleryAndGetPath(getActivity(), content, null, isGif);
                            if(!StringUtils.isEmpty(path)) {
                                FileDaoService.getInstance().insertRecentFile(path);
                            }
                            return !StringUtils.isEmpty(path);

                        }

                        @Override
                        protected void onPostExecute(Boolean succeed) {

                            mProgressDialogHelper.dismiss();

                            if (succeed) {
                                int galleryIndex = AtWorkDirUtils.getInstance().getGalleryDir(LoginUserInfo.getInstance().getLoginUserUserName(mActivity)).indexOf(AtworkConfig.APP_FOLDER);
                                AtworkToast.showResToast(R.string.save_image_to_mobile_success, AtWorkDirUtils.getInstance().getGalleryDir(LoginUserInfo.getInstance().getLoginUserUserName(mActivity)).substring(galleryIndex));
                                return;
                            }
                            AtworkToast.showResToast(R.string.save_image_to_mobile_fail);
                        }
                    }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    return;
                }
                if (getResources().getString(R.string.qrcode_recognition).equals(item)) {

                    if (null != result) {

                        String resultText = result.getText();

                        final ProgressDialogHelper progressDialog = new ProgressDialogHelper(mActivity);
                        progressDialog.show(mActivity.getResources().getString(R.string.loading));

                        new Handler().postDelayed(() -> {

                            progressDialog.dismiss();

                            realHandleQrcodeResult(resultText);

                        }, 1000L);

                    }
                    return;
                }
            }

        });
    }


    private void realHandleQrcodeResult(String resultText) {
        QrcodeManager.getInstance().handleSelfProtocol(mActivity, resultText);


    }

}
