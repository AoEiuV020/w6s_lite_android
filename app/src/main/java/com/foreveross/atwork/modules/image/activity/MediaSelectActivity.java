package com.foreveross.atwork.modules.image.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.foreverht.workplus.ui.component.statusbar.WorkplusStatusBarHelper;
import com.foreveross.atwork.R;
import com.foreveross.atwork.component.ProgressDialogHelper;
import com.foreveross.atwork.component.popview.WorkplusPopUpView;
import com.foreveross.atwork.cordova.plugin.WorkPlusImagesPlugin;
import com.foreveross.atwork.cordova.plugin.model.ChooseMediasRequest;
import com.foreveross.atwork.cordova.plugin.model.MediaSelectedResponseJson;
import com.foreveross.atwork.infrastructure.model.file.MediaBucket;
import com.foreveross.atwork.infrastructure.model.file.MediaItem;
import com.foreveross.atwork.infrastructure.model.file.SelectMediaType;
import com.foreveross.atwork.infrastructure.model.file.VideoItem;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.BitmapUtil;
import com.foreveross.atwork.infrastructure.utils.ImageShowHelper;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.modules.image.component.ImageAlbumPopupWindow;
import com.foreveross.atwork.modules.image.fragment.MediaSelectFragment;
import com.foreveross.atwork.modules.image.listener.MediaAlbumListener;
import com.foreveross.atwork.modules.image.util.MediaSelectHelper;
import com.foreveross.atwork.support.AtWorkFragmentManager;
import com.foreveross.atwork.support.AtworkBaseActivity;
import com.foreveross.atwork.utils.AtworkToast;
import com.foreveross.atwork.utils.FileHelper;
import com.foreveross.atwork.utils.ImageCacheHelper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ReyZhang on 2015/4/24.
 */
public class MediaSelectActivity extends AtworkBaseActivity implements MediaAlbumListener {

    public static final int FROM_CHAT_DETAIL = 1;

    public static Intent getIntent(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, MediaSelectActivity.class);
        return intent;
    }

    public static final String DATA_SELECT_FULL_MODE = "DATA_SELECT_FULL_MODE";
    public static final String DATA_SELECT_SEND_MODE = "DATA_SELECT_SEND_MODE";
    public static final String DATA_OPEN_FULL_MODE_SELECT = "DATA_OPEN_FULL_MODE_SELECT";
    public static final String DATA_FROM_VIEW = "DATA_FROM_VIEW";
    public static final String DATA_SELECT_MEDIA_TYPE_ADD = "DATA_SELECT_MEDIA_TYPE_ADD";

    public static final String RESULT_SELECT_IMAGE_INTENT = "GET_IMAGE_LIST_FLAG";

    public static final int REQUEST_IMAGE_PREVIEW_CODE = 10001;

    public static final int REQUEST_IMAGE_EDIT_CODE = 10002;


    private View mVFakeStatusBar;
    private ImageView mBack;
    private TextView mTitle;
    private TextView mTvSend;
    private TextView mImageAlbum;
    private TextView mTvPreview;
    private TextView mTvEdit;
    private CheckBox mCbSelectFullSize;
    private TextView mTvFullSize;
    private RelativeLayout mSelectedLayout;
    private LinearLayout mChangeAlbumLayout;


    //相册列表
    private List<MediaBucket> mImageAlbumList = new ArrayList<>();
    //已选图片列表
    public List<MediaItem> mMediaSelectedList = new ArrayList<>();


    private AtWorkFragmentManager mFragmentManager;

    private ImageAlbumPopupWindow mPopupWindow;

    private MediaSelectFragment mImageSelectFragment;

    //设置底层和popupWindow之间的半透明层
    View transparentView;

    private ViewTreeObserver mObserver;

    private int mPreHeight = 100;

    private ProgressDialogHelper mProgressDialogHelper;

    public boolean mIsFromCordova;

    private int mFromView = -1;
    private String mSendMode;


    // 注意新接口才用该两个变量
    public boolean mFromCordovaSingleSelect;
    public boolean mFromCordovaMultiSelect;
    // 图片是否需要crop 处理
    public boolean mIsImageCrop;

    private boolean mIsFullMode = false;
    /**
     * 是否显示勾选原图的按钮
     * */
    private boolean mOpenFullModeSelect = false;

    public List<SelectMediaType> mMediaSelectTypeList = new ArrayList<>();

    public ChooseMediasRequest mChooseMediasRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        ImageCacheHelper.checkPool();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_select);
        initData();

        initView();
        registerListener();

        initFragment();

        setup();
        onBtnUpdate();
    }

    @Override
    public void changeStatusBar() {
        WorkplusStatusBarHelper.setCommonFullScreenStatusBar(this, true);

    }

    public void onBtnUpdate() {
        onSubmitBtnUpdate();
        onEditBtnUpdate();
        onFullModeBtnUpdate();
    }


    private void initView() {
        mVFakeStatusBar = findViewById(R.id.v_fake_statusbar);
        mBack = findViewById(R.id.title_bar_common_back);
        mTitle = findViewById(R.id.title_bar_common_title);
        mTvSend = findViewById(R.id.title_bar_common_right_text);
        mImageAlbum = findViewById(R.id.select_media_album);
        mTvPreview = findViewById(R.id.tv_preview);
        mTvEdit = findViewById(R.id.tv_edit);
        mSelectedLayout = findViewById(R.id.album_select_layout);
        mChangeAlbumLayout = findViewById(R.id.change_album_area);
        mCbSelectFullSize = findViewById(R.id.cb_send_full_image);
        mTvFullSize = findViewById(R.id.tv_full_size);

        mObserver = mSelectedLayout.getViewTreeObserver();
        mObserver.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mSelectedLayout.getViewTreeObserver().removeOnPreDrawListener(this);
                mPreHeight = mSelectedLayout.getMeasuredHeight();
                return true;
            }
        });

        transparentView = new View(this);
        transparentView.setBackgroundColor(Color.BLACK);
        transparentView.setAlpha(0.5f);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        addContentView(transparentView, lp);
        transparentView.setVisibility(View.GONE);

    }

    private void initImageAlbumPopup() {
        mPopupWindow = new ImageAlbumPopupWindow(this, mImageAlbumList, this);
        mPopupWindow.showAtLocation(this.findViewById(R.id.rl_image_select), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, mPreHeight);
        transparentView.setVisibility(View.VISIBLE);
        mPopupWindow.setOnDismissListener(() -> {
//                ScreenUtils.setBackgroundAlpha(ImageSelectActivity.this, 1f);
            transparentView.setVisibility(View.GONE);
        });
    }

    public View getVFakeStatusBar() {
        return mVFakeStatusBar;
    }


    private void setup() {
        mTitle.setText(R.string.select_image);
        mTvSend.setVisibility(View.VISIBLE);
        mTvSend.setText(getSendText());

        mImageAlbum.setFocusable(true);
        mImageAlbum.setClickable(true);

        if (isSingleMode()) {
            mTvSend.setVisibility(View.GONE);
            mTvPreview.setVisibility(View.GONE);
        }

        if (mFromCordovaMultiSelect) {
            mTvPreview.setVisibility(View.GONE);
        }

        if(mOpenFullModeSelect) {
            mCbSelectFullSize.setVisibility(View.VISIBLE);
            mTvFullSize.setVisibility(View.VISIBLE);
        }

        if(mMediaSelectTypeList.contains(SelectMediaType.VIDEO)) {
            mImageAlbum.setText(R.string.all_medias);

        } else {
            mImageAlbum.setText(R.string.all_images);

        }

    }

    public boolean isSingleMode() {
        return mFromCordovaSingleSelect || mImageSelectFragment.isSingleImgSelectLimit();
    }

    private void registerListener() {
        mTvSend.setOnClickListener(mClickListener);
        mImageAlbum.setOnClickListener(mClickListener);
        mBack.setOnClickListener(mClickListener);
        mTvEdit.setOnClickListener(mClickListener);
        mTvPreview.setOnClickListener(mClickListener);
        mChangeAlbumLayout.setOnClickListener(mClickListener);
        mCbSelectFullSize.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if(isChecked) {

//                if(isNoneSelected()) {
//                    AtworkToast.showResToast(R.string.cannot_select_full_img);
//                    mCbSelectFullSize.setChecked(false);
//                    return;
//                }


                if(isFullImgOverLimit()) {
                    AtworkToast.showResToast(R.string.full_img_size_limit, FileHelper.getFileSizeStr(AtworkConfig.CHAT_FULL_IMG_SELECT_LIMIT_SIZE));
                    mCbSelectFullSize.setChecked(false);
                    return;
                }

            }

            mIsFullMode = isChecked;

        });
    }

    private void initData() {
        mFromView = getIntent().getIntExtra(DATA_FROM_VIEW, -1);

        mIsFromCordova = getIntent().getBooleanExtra(WorkPlusImagesPlugin.FROM_CORDOVA_PLUG, false);

        //判断是否是图片单选
        mFromCordovaSingleSelect = this.getIntent().getBooleanExtra(WorkPlusImagesPlugin.ACTION_SINGLE_SELECT_IMAGE_WITH_CROP, false);

        mIsImageCrop = this.getIntent().getBooleanExtra(WorkPlusImagesPlugin.DATA_IMAGE_CROP, false);

        //判断是否是图片多选
        mFromCordovaMultiSelect = this.getIntent().getBooleanExtra(WorkPlusImagesPlugin.ACTION_MULTI_SELECT_IMAGE, false);
        List<MediaItem> imgList = (List<MediaItem>) this.getIntent().getSerializableExtra(WorkPlusImagesPlugin.MULTI_SELECT_LIST);
        if (imgList != null) {
            mMediaSelectedList = imgList;
        }

        mChooseMediasRequest = getIntent().getParcelableExtra(WorkPlusImagesPlugin.DATA_CHOOSE_IMAGE_REQUEST);
        if(null != mChooseMediasRequest) {
            mIsFromCordova = mChooseMediasRequest.mFromCordova;
        }

        mOpenFullModeSelect = getIntent().getBooleanExtra(DATA_OPEN_FULL_MODE_SELECT, false);

        ArrayList<SelectMediaType> selectMediaTypes = (ArrayList<SelectMediaType>) getIntent().getSerializableExtra(DATA_SELECT_MEDIA_TYPE_ADD);
        if (!ListUtil.isEmpty(selectMediaTypes)) {
            mMediaSelectTypeList.addAll(selectMediaTypes);
        }

        mMediaSelectTypeList.add(SelectMediaType.IMG);
    }

    final Activity activity = this;

    private View.OnClickListener mClickListener = v -> {
        int id = v.getId();
        switch (id) {
            case R.id.title_bar_common_back:
                finish();
                //界面回退动画
                activity.overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
                break;

            case R.id.title_bar_common_right_text:
                if(FROM_CHAT_DETAIL == mFromView) {
                    WorkplusPopUpView workplusPopUpView = new WorkplusPopUpView(this);
                    workplusPopUpView.addPopItem(-1, R.string.image_comment, 0);
                    workplusPopUpView.addPopItem(-1, R.string.send_now, 1);

                    workplusPopUpView.setPopItemOnClickListener((title, pos) -> {
                        mSendMode = title;
                        finishAndSendSelectResult();

                    });

                    workplusPopUpView.pop(mTvSend);

                    return;
                }


                if (!mMediaSelectedList.isEmpty()) {
                    finishAndSendSelectResult();
                }
                break;

            case R.id.change_album_area:
            case R.id.select_media_album:
                initImageAlbumPopup();
                break;

            case R.id.tv_preview:
                if (mMediaSelectedList.isEmpty()) {
                    if(mMediaSelectTypeList.contains(SelectMediaType.VIDEO)) {
                        Toast.makeText(MediaSelectActivity.this, getString(R.string.cannot_preview_media), Toast.LENGTH_LONG).show();

                    } else {
                        Toast.makeText(MediaSelectActivity.this, getString(R.string.cannot_preview_img), Toast.LENGTH_LONG).show();

                    }

                    return;
                }
                Intent previewIntent = MediaPreviewActivity.getImagePreviewIntent(MediaSelectActivity.this, MediaPreviewActivity.FromAction.IMAGE_SELECT);
                previewIntent.putExtra(MediaPreviewActivity.INTENT_IMAGE_SELECTED_LIST, (Serializable) mMediaSelectedList);
                previewIntent.putExtra(MediaSelectActivity.DATA_SELECT_FULL_MODE, mIsFullMode);
                previewIntent.putExtra(MediaSelectActivity.DATA_OPEN_FULL_MODE_SELECT, mOpenFullModeSelect);
                previewIntent.putExtra(WorkPlusImagesPlugin.DATA_CHOOSE_IMAGE_REQUEST, mChooseMediasRequest);
                previewIntent.putExtra(WorkPlusImagesPlugin.ACTION_SINGLE_SELECT_IMAGE_WITH_CROP, isSingleMode());
                previewIntent.putExtra(DATA_FROM_VIEW, mFromView);


                startActivityForResult(previewIntent, REQUEST_IMAGE_PREVIEW_CODE);

                break;

            case R.id.tv_edit:
                Intent intent = ImageEditActivity.getIntent(MediaSelectActivity.this, mMediaSelectedList.get(0));
                startActivityForResult(intent, REQUEST_IMAGE_EDIT_CODE);
                break;

            case R.id.cb_send_full_image:

                break;
        }
    };

    public void finishAndSendSelectResult() {
        if (mFromCordovaMultiSelect) {
            multiImageBackToCordovaPlugin(mMediaSelectedList);

        } else if(isSingleMode()) {
            singleImageBackToCordovaPlugin(mMediaSelectedList.get(0));

        } else {
            sendSelectedResult();

        }
    }

    private void initFragment() {
        mFragmentManager = new AtWorkFragmentManager(this, R.id.fragment_select_image);
        mImageSelectFragment = new MediaSelectFragment();
        mFragmentManager.addFragmentAndAdd2BackStack(mImageSelectFragment, MediaSelectFragment.TAG);
    }

    @Override
    public void onAlbumLoadingSuccess(List<MediaBucket> imageAlbumList) {
        if (imageAlbumList == null) {
            return;
        }
        mImageAlbumList = imageAlbumList;
    }

    @Override
    public void onAlbumSelected(int position) {
        mImageSelectFragment.onImageAlbumSelected(position);
        MediaBucket bucket = mImageAlbumList.get(position);
        mImageAlbum.setText(bucket.getBucketName());
    }

    @Override
    public void onBackPressed() {

        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
            return;
        }
        finish();
        //界面回退动画
        this.overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);

    }

    private void sendSelectedResult() {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        if (mIsFromCordova) {
            mProgressDialogHelper = new ProgressDialogHelper(this);
            mProgressDialogHelper.show(R.string.compressing);
            getCompressImagesPath(mMediaSelectedList, bundle, intent);
            return;

        } else {
            bundle.putSerializable(RESULT_SELECT_IMAGE_INTENT, (Serializable) mMediaSelectedList);
            bundle.putBoolean(DATA_SELECT_FULL_MODE, mIsFullMode);

            if(!StringUtils.isEmpty(mSendMode)) {
                bundle.putString(DATA_SELECT_SEND_MODE, mSendMode);
            }
        }


        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
        this.overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (REQUEST_IMAGE_PREVIEW_CODE == requestCode) {
            onPreviewImageResult(resultCode, data);

        } else if(REQUEST_IMAGE_EDIT_CODE == requestCode) {
            onEditImageResult(resultCode, data);

        }
    }



    private void onEditImageResult(int resultCode, Intent data) {
        if (resultCode != RESULT_OK || data == null) {
            return;
        }

        MediaItem imageItem = (MediaItem) data.getSerializableExtra(ImageEditActivity.DATA_IMAGE);

        resetSelectedList(imageItem);

        finishAndSendSelectResult();


    }



    private void onPreviewImageResult(int resultCode, Intent data) {
        if (resultCode != RESULT_OK || data == null) {
            return;
        }
        List<MediaItem> previewSelectedList = (List<MediaItem>) data.getSerializableExtra(MediaPreviewActivity.RESULT_SELECT_PREVIEW_IMAGE_INTENT);
        if (previewSelectedList == null) {
            return;
        }

        resetSelectedList(previewSelectedList);

        boolean isSend = data.getBooleanExtra(MediaPreviewActivity.INTENT_IMAGE_SEND_RESULT, false);
        mIsFullMode = data.getBooleanExtra(DATA_SELECT_FULL_MODE, false);
        mSendMode = data.getStringExtra(DATA_SELECT_SEND_MODE);
        onFullModeBtnUpdate();

        //判断是否是直接发送，如果是直接发送，将图片列表直接发送出去，如果不是，刷新界面
        if (isSend) {

            finishAndSendSelectResult();


        } else {
            if(isSingleMode()) {
                mMediaSelectedList.clear();
            }

            mImageSelectFragment.refresh();
            onBtnUpdate();
        }


    }

    private boolean isFullImgOverLimit() {
        for(MediaItem imageItem : mMediaSelectedList) {
            if (imageItem.isSelected) {
                if(isFullImgOverLimit(imageItem.size)) {
                    return true;
                }
            }
        }

        return false;

    }

    public boolean isFullImgOverLimit(long prepareSelectImg) {

        return AtworkConfig.CHAT_FULL_IMG_SELECT_LIMIT_SIZE < prepareSelectImg;
    }

    private boolean isNoneSelected() {
        boolean isNoneSelected = true;
        for(MediaItem imageItem : mMediaSelectedList) {
            if (imageItem.isSelected) {
                isNoneSelected = false;
                break;
            }
        }

        return isNoneSelected;

    }

    private void onFullModeBtnUpdate() {

        long totalSize = 0;
        for(MediaItem mediaItem : mMediaSelectedList) {
            if (mediaItem.isSelected) {
                totalSize += mediaItem.size;
            }
        }

        if(0 == totalSize) {
//            mIsFullMode = false;
        }


        if (0 != totalSize) {
//            mTvFullSize.setText(String.format(getString(R.string.orig_size), FileHelper.getFileSizeStr(totalSize)));
            mTvFullSize.setText(getString(R.string.original_img));

        } else {

            mTvFullSize.setText(getString(R.string.original_img));
        }

        mCbSelectFullSize.setChecked(mIsFullMode);
    }


    /**
     * 重新组装mSelectedList
     *
     * @param previewSelectedList
     */
    private void resetSelectedList(List<MediaItem> previewSelectedList) {
        mMediaSelectedList.clear();

        mMediaSelectedList.addAll(previewSelectedList);

    }

    private void resetSelectedList(MediaItem imageItem) {
        mMediaSelectedList.clear();
        mMediaSelectedList.add(imageItem);
    }

    /**
     * 压缩选择的图片
     *
     * @param bundle
     * @param intent
     */
    @Deprecated
    public void getCompressImagesPath(final List<MediaItem> imageItems, final Bundle bundle, final Intent intent) {
        new Thread() {
            public void run() {
                final ArrayList<String> compressedPaths = new ArrayList<>();
                for (MediaItem imageItem : imageItems) {
                    compressedPaths.add(ImageShowHelper.imagePluginCompress(MediaSelectActivity.this, imageItem.filePath));
                }
                if (mProgressDialogHelper != null) {
                    MediaSelectActivity.this.runOnUiThread(() -> {
                        mProgressDialogHelper.dismiss();
                        bundle.putSerializable("imagePaths", compressedPaths);
                        intent.putExtras(bundle);
                        setResult(Activity.RESULT_OK, intent);
                        finish();
                    });
                }
            }
        }.start();
    }

    /**
     * 更新发送按钮文件数量
     */
    public void onSubmitBtnUpdate() {
        int count = mMediaSelectedList.size();
        if (0 == count) {
            mTvSend.setText(getSendText());
            // mTvSend.setTextColor(Color.parseColor("#AAAAAA"));
            mTvSend.setTextColor(getResources().getColor(R.color.title_bar_rightest_text_gray));
            //  mTvSend.setBackgroundResource(R.mipmap.bg_green_submit_dark);
        } else {
            String sentText = getSendText() + "(" + count + "/" + getMaxImgChooseCount() + ")";
            mTvSend.setText(sentText);
            mTvSend.setTextColor(getResources().getColor(R.color.common_item_black));
            //  mTvSend.setBackgroundResource(R.drawable.bg_submit_green_selector);
        }
    }

    private int getMaxImgChooseCount() {
        return mImageSelectFragment.getMaxImgChooseCount();
    }

    @NonNull
    public String getSendText() {

        if (mIsFromCordova || mFromCordovaSingleSelect || mFromCordovaMultiSelect) {
            return getString(R.string.done);
        } else {
            return getString(R.string.button_send);

        }
    }

    public  void onEditBtnUpdate() {

        int count = mMediaSelectedList.size();
        if(1 == count) {
            MediaItem mediaItem = mMediaSelectedList.get(0);

            if(!MediaSelectHelper.isStaticImage(mediaItem)) {
                mTvEdit.setVisibility(View.GONE);
                return;
            }


            mTvEdit.setTextColor(ContextCompat.getColor(MediaSelectActivity.this, R.color.white));
            mTvEdit.setVisibility(View.VISIBLE);

        } else {
            mTvEdit.setVisibility(View.GONE);

        }
    }

    /**
     * 返回单个图片路径到Cordova
     */
    public void singleImageBackToCordovaPlugin(MediaItem imageItem) {

        if (TextUtils.isEmpty(imageItem.filePath)) {
            return;
        }
        String compressPath = ImageShowHelper.imagePluginCompress(this, imageItem.filePath);
        setSingleImageResult(compressPath, imageItem, "");
    }


    private void setSingleImageResult(String compressPath, MediaItem imageItem, String mediaId) {
        MediaSelectedResponseJson mediaSelectedResponseJson = new MediaSelectedResponseJson();

        if(imageItem instanceof VideoItem) {
            mediaSelectedResponseJson.key = imageItem.filePath;
            mediaSelectedResponseJson.videoURL = imageItem.filePath;

        } else {
            mediaSelectedResponseJson.key = imageItem.filePath;
            mediaSelectedResponseJson.imageURL = compressPath;
            mediaSelectedResponseJson.mediaId = mediaId;

            BitmapUtil.ImageInfo imageInfo = BitmapUtil.getImageInfo(compressPath);

            mediaSelectedResponseJson.imageInfo = new MediaSelectedResponseJson.ImageInfo();
            mediaSelectedResponseJson.imageInfo.height = imageInfo.height;
            mediaSelectedResponseJson.imageInfo.width = imageInfo.width;
            mediaSelectedResponseJson.imageInfo.size = imageInfo.size;
        }


        Intent intent = new Intent();

        intent.putExtra(WorkPlusImagesPlugin.DATA_SELECT_IMGS, mediaSelectedResponseJson);

        this.setResult(WorkPlusImagesPlugin.SINGLE_SELECT_RESULT_CODE, intent);
        this.finish();
    }

    private void multiImageBackToCordovaPlugin(List<MediaItem> multiImageItems) {
        ArrayList<MediaSelectedResponseJson> imgSelectedList = new ArrayList<>();
        for (MediaItem imageItem : multiImageItems) {
            if(imageItem instanceof VideoItem) {
                MediaSelectedResponseJson mediaSelectedResponseJson = new MediaSelectedResponseJson();
                mediaSelectedResponseJson.key = imageItem.filePath;
                mediaSelectedResponseJson.videoURL = imageItem.filePath;
                imgSelectedList.add(mediaSelectedResponseJson);

                continue;
            }

            String compressPath = ImageShowHelper.imagePluginCompress(this, imageItem.filePath);

            BitmapUtil.ImageInfo imageInfo = BitmapUtil.getImageInfo(compressPath);

            MediaSelectedResponseJson mediaSelectedResponseJson = new MediaSelectedResponseJson();
            mediaSelectedResponseJson.key = imageItem.filePath;
            mediaSelectedResponseJson.imageURL = compressPath;

            mediaSelectedResponseJson.imageInfo = new MediaSelectedResponseJson.ImageInfo();
            mediaSelectedResponseJson.imageInfo.height = imageInfo.height;
            mediaSelectedResponseJson.imageInfo.width = imageInfo.width;
            mediaSelectedResponseJson.imageInfo.size = imageInfo.size;

            imgSelectedList.add(mediaSelectedResponseJson);
        }


        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(WorkPlusImagesPlugin.DATA_SELECT_IMGS, imgSelectedList);
        intent.putExtras(bundle);
        this.setResult(WorkPlusImagesPlugin.MULTI_SELECT_RESULT_CODE, intent);
        this.finish();
    }


    public boolean isFullMode() {
        return mIsFullMode;
    }

    public boolean isOpenFullModeSelect() {
        return mOpenFullModeSelect;
    }


}
