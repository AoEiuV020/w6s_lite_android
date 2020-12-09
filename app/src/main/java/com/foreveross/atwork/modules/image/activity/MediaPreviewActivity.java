package com.foreveross.atwork.modules.image.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.foreveross.atwork.R;
import com.foreveross.atwork.component.popview.WorkplusPopUpView;
import com.foreveross.atwork.cordova.plugin.WorkPlusImagesPlugin;
import com.foreveross.atwork.cordova.plugin.model.ChooseMediasRequest;
import com.foreveross.atwork.infrastructure.model.file.MediaItem;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.modules.image.fragment.MediaPreviewFragment;
import com.foreveross.atwork.modules.image.listener.ImageSwitchListener;
import com.foreveross.atwork.modules.image.util.MediaSelectHelper;
import com.foreveross.atwork.support.AtWorkFragmentManager;
import com.foreveross.atwork.support.AtworkBaseActivity;
import com.foreveross.atwork.utils.AtworkToast;
import com.foreveross.atwork.utils.FileHelper;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ReyZhang on 2015/5/4.
 */
public class MediaPreviewActivity extends AtworkBaseActivity implements ImageSwitchListener {

    public static final String TAG = MediaPreviewActivity.class.getSimpleName();

    private static final int REQUEST_IMAGE_EDIT_CODE = 10001;

    public static final String RESULT_SELECT_PREVIEW_IMAGE_INTENT = "result_select_preview_image_intent";

    public static final String INTENT_IMAGE_SELECTED_LIST = "image_select_list";

    public static final String INTENT_IMAGE_SEND_RESULT = "image_send";

    public static final String ACTION_FROM_WHERE = "action_from_where";

    public static final String DATA_IMG_PATH = "data_img_path";

    private String mSendMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);
        initView();
        registerListener();

        setup();
        initFragment();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMediaPreSelectedList != null) {
            mMediaPreSelectedList.clear();
            mMediaPreSelectedList = null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(REQUEST_IMAGE_EDIT_CODE == requestCode) {
            onActivityForEditImage(resultCode, data);
        }

    }

    private void onActivityForEditImage(int resultCode, Intent data) {
        if (resultCode != RESULT_OK || data == null) {
            return;
        }

        MediaItem imageItem = (MediaItem) data.getSerializableExtra(ImageEditActivity.DATA_IMAGE);
        mMediaPreSelectedList.clear();
        mMediaPreSelectedList.add(imageItem);

        Intent intent = new Intent();
        finishAndSetResult(intent, true);

    }


    public static Intent getImagePreviewIntent(Context context, FromAction fromAction) {
        Intent intent = new Intent(context, MediaPreviewActivity.class);
        intent.putExtra(ACTION_FROM_WHERE, fromAction);
        return intent;
    }


    private ImageView mBack;
    private TextView mTvSend;
    private TextView mTitle;
    private CheckBox mCbSelectImg;
    private TextView mTvFullSize;
    private TextView mLeftTitle;
    private CheckBox mCbSendFullImg;
    private TextView mTvSelectImg;
    private TextView mTvEdit;

    //已选媒体列表
    public List<MediaItem> mMediaPreSelectedList;

    private MediaPreviewFragment mImagePreviewFragment;

    private AtWorkFragmentManager mFragmentManager;

    private int mCurrentPos = 0;

    private FromAction mFromAction = FromAction.IMAGE_SELECT;

    private boolean mIsFullMode = false;

    private boolean mIsSingleMode = false;

    private int mFromView = -1;

    /**
     * 是否显示勾选原图的按钮
     * */
    private boolean mOpenFullModeSelect = false;
    private ChooseMediasRequest mChooseMediasRequest;

    boolean mFromCordova;
    boolean mShowWatermark;

    private void initView() {
        mBack = findViewById(R.id.title_bar_common_back);
        mTitle = findViewById(R.id.title_bar_common_title);
        mTvSend = findViewById(R.id.title_bar_common_right_text);
        mCbSelectImg = findViewById(R.id.cb_select);
        mTvFullSize = findViewById(R.id.tv_image_org_size);
        mLeftTitle = findViewById(R.id.title_bar_common_left_title);
        mCbSendFullImg = findViewById(R.id.cb_send_full_image);
        mTvSelectImg = findViewById(R.id.select_text);
        mTvEdit = findViewById(R.id.tv_edit);
    }

    private void setup() {
        mTvSend.setVisibility(View.VISIBLE);
        mTvSend.setText(getString(R.string.button_send));
        Intent intent = getIntent();
        mFromCordova = intent.getBooleanExtra(WorkPlusImagesPlugin.IMAGES_FROM_CORDOVA_CODE, false);
        mShowWatermark = intent.getBooleanExtra(WorkPlusImagesPlugin.SHOW_WATERMARK, false);
        mFromAction = (FromAction) intent.getSerializableExtra(ACTION_FROM_WHERE);
        mIsFullMode = intent.getBooleanExtra(MediaSelectActivity.DATA_SELECT_FULL_MODE, false);
        mOpenFullModeSelect = getIntent().getBooleanExtra(MediaSelectActivity.DATA_OPEN_FULL_MODE_SELECT, false);
        mChooseMediasRequest = getIntent().getParcelableExtra(WorkPlusImagesPlugin.DATA_CHOOSE_IMAGE_REQUEST);
        mIsSingleMode = getIntent().getBooleanExtra(WorkPlusImagesPlugin.ACTION_SINGLE_SELECT_IMAGE_WITH_CROP, false);
        mFromView = getIntent().getIntExtra(MediaSelectActivity.DATA_FROM_VIEW, -1);

        //判断是否来自cordova
        if (mFromCordova) {
            mMediaPreSelectedList = (List<MediaItem>) this.getIntent().getSerializableExtra(WorkPlusImagesPlugin.ACTION_SHOW_IMAGE);
            mCurrentPos = getIntent().getIntExtra(WorkPlusImagesPlugin.ACTION_POSITION, 0);

            if(0 > mCurrentPos || mMediaPreSelectedList.size() <= mCurrentPos) {
                mCurrentPos = 0;
            }

            mTvFullSize.setVisibility(View.GONE);
            mCbSendFullImg.setVisibility(View.GONE);
            mTvSelectImg.setVisibility(View.GONE);
            mCbSelectImg.setVisibility(View.GONE);
            mTvSend.setVisibility(View.GONE);

            if(AtworkConfig.SCREEN_ORIENTATION_USER_SENSOR) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
            }


        } else {
            mMediaPreSelectedList = (List<MediaItem>) intent.getSerializableExtra(INTENT_IMAGE_SELECTED_LIST);
        }

        mTitle.setText("");
        mTvSend.setTextColor(getResources().getColor(R.color.common_item_black));
        mLeftTitle.setVisibility(View.VISIBLE);

        if (FromAction.CAMERA == mFromAction) {
            if (!ListUtil.isEmpty(mMediaPreSelectedList)) {
                mMediaPreSelectedList.get(0).isSelected = true;

            }
            mTvSelectImg.setVisibility(View.GONE);
            mCbSelectImg.setVisibility(View.GONE);
        } else {
            mLeftTitle.setText("1/" + mMediaPreSelectedList.size());

        }

        if(mOpenFullModeSelect) {
            mCbSendFullImg.setVisibility(View.VISIBLE);
            mTvFullSize.setVisibility(View.VISIBLE);
        } else {
            mCbSendFullImg.setVisibility(View.GONE);
            mTvFullSize.setVisibility(View.GONE);
        }

    }

    private void registerListener() {
        mTvSend.setOnClickListener(mClickListener);
        mBack.setOnClickListener(mClickListener);
        mCbSelectImg.setOnClickListener(mClickListener);
        mTvEdit.setOnClickListener(mClickListener);
        mCbSendFullImg.setOnClickListener(mClickListener);

        mCbSendFullImg.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if(isChecked) {

//                if(isNoneSelected()) {
//                    AtworkToast.showResToast(R.string.cannot_select_full_img);
//                    mCbSendFullImg.setChecked(false);
//                    return;
//                }



                if(isFullImgOverLimit()) {
                    AtworkToast.showResToast(R.string.full_img_size_limit, FileHelper.getFileSizeStr(AtworkConfig.CHAT_FULL_IMG_SELECT_LIMIT_SIZE));
                    mCbSendFullImg.setChecked(false);
                    return;
                }

            }

            mIsFullMode = isChecked;
        });
    }

    final Activity activity = this;

    private View.OnClickListener mClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            int id = v.getId();
            Intent intent = new Intent();
            switch (id) {
                case R.id.title_bar_common_back:
                    finishAndSetResult(intent, false);
                    break;

                case R.id.title_bar_common_right_text:
                    int count = 0;
                    for (MediaItem item : mMediaPreSelectedList) {
                        if (item == null) {
                            continue;
                        }
                        if (item.isSelected) {
                            count++;
                        }
                    }

                    if (0 != count) {


                        if(MediaSelectActivity.FROM_CHAT_DETAIL == mFromView) {
                            WorkplusPopUpView workplusPopUpView = new WorkplusPopUpView(MediaPreviewActivity.this);
                            workplusPopUpView.addPopItem(-1, R.string.image_comment, 0);
                            workplusPopUpView.addPopItem(-1, R.string.send_now, 1);

                            Intent finalIntent = intent;
                            workplusPopUpView.setPopItemOnClickListener((title, pos) -> {
                                mSendMode = title;
                                finishAndSetResult(finalIntent, true);

//                                finishAndSendSelectResult();

                            });

                            workplusPopUpView.pop(mTvSend);

                            return;
                        }


                        finishAndSetResult(intent, true);
                    }
                    break;

                case R.id.cb_select:
                    MediaItem item = mMediaPreSelectedList.get(mCurrentPos);
                    if (item == null) {
                        return;
                    }
                    if (checkLimit(item)) return;

                    item.isSelected = !item.isSelected;
                    onBtnUpdate();
                    break;

                case R.id.tv_edit:
                    intent = ImageEditActivity.getIntent(MediaPreviewActivity.this, mMediaPreSelectedList.get(mCurrentPos));
                    startActivityForResult(intent, REQUEST_IMAGE_EDIT_CODE);
                    break;

                case R.id.cb_send_full_image:

                    break;
            }
        }

    };

    private boolean checkLimit(MediaItem item) {
        if(!item.isSelected) {
            if(mIsFullMode && isFullImgOverLimit(item.size)) {
                AtworkToast.showResToast(R.string.full_img_size_limit, FileHelper.getFileSizeStr(AtworkConfig.CHAT_FULL_IMG_SELECT_LIMIT_SIZE));

                mCbSelectImg.setChecked(false);
                return true;
            }
        }
        return false;
    }

    public void finishAndSetResult(Intent intent, boolean isSend) {

        if(!StringUtils.isEmpty(mSendMode)) {
            intent.putExtra(MediaSelectActivity.DATA_SELECT_SEND_MODE, mSendMode);
        }

        if (FromAction.IMAGE_SELECT == mFromAction) {
            intent.putExtra(RESULT_SELECT_PREVIEW_IMAGE_INTENT, (Serializable) getImgSelectedList());
            intent.putExtra(INTENT_IMAGE_SEND_RESULT, isSend);
            intent.putExtra(MediaSelectActivity.DATA_SELECT_FULL_MODE, mIsFullMode);
            setResult(RESULT_OK, intent);
            finish();
            //界面回退动画
            activity.overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);

        } else if(FromAction.CAMERA == mFromAction){
            if (isSend) {
                intent.putExtra(DATA_IMG_PATH, mMediaPreSelectedList.get(0).filePath);
                setResult(RESULT_OK, intent);
            }

            finish();
            //界面回退动画
            activity.overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
        }
    }

    public void onBtnUpdate() {
        onSubmitBtnUpdate();
        onEditBtnUpdate();
        onFullModeBtnUpdate();
    }


    private void initFragment() {
        mFragmentManager = new AtWorkFragmentManager(this, R.id.fragment_preview_image);
        mImagePreviewFragment = new MediaPreviewFragment();
        if (mMediaPreSelectedList == null) {
            return;
        }
        onBtnUpdate();
        Bundle bundle = new Bundle();

        bundle.putSerializable(MediaPreviewFragment.ARGUMENT_CURRENT_IMAGE_LIST, (Serializable) mMediaPreSelectedList);
        bundle.putInt(MediaPreviewFragment.ARGUMENT_CURRENT_IMAGE_POS, mCurrentPos);
        bundle.putBoolean(MediaPreviewFragment.ARGUMENT_FROM_CORDOVA, mFromCordova);
        bundle.putBoolean(MediaPreviewFragment.ARGUMENT_SHOW_WATERMARK, mShowWatermark);

        mImagePreviewFragment.setArguments(bundle);
        mFragmentManager.addFragmentAndAdd2BackStack(mImagePreviewFragment, MediaPreviewFragment.TAG);
    }

    @Override
    public void onImageSwitch(int pos) {
        if(mMediaPreSelectedList.size() <= pos || pos < 0) {
            return;
        }

        MediaItem currentMediaItem = mMediaPreSelectedList.get(pos);
        if (currentMediaItem == null) {
            return;
        }
        if (currentMediaItem.size == 0) {
            File file = new File(currentMediaItem.filePath);
            if (file != null && file.exists()) {
                currentMediaItem.size = file.length();
            }
        }
        mCurrentPos = pos;
        mCbSelectImg.setChecked(currentMediaItem.isSelected);

        if (FromAction.CAMERA != mFromAction) {
            mLeftTitle.setText((pos + 1) + "/" + mMediaPreSelectedList.size());
        }

        handleFullImgCheckbox(currentMediaItem);
        onEditBtnUpdate();
    }

    private void handleFullImgCheckbox(MediaItem mediaItem) {
        if(mFromCordova || !mOpenFullModeSelect || !MediaSelectHelper.isStaticImage(mediaItem)) {
            mTvFullSize.setVisibility(View.GONE);
            mCbSendFullImg.setVisibility(View.GONE);
            return;
        }


        mTvFullSize.setVisibility(View.VISIBLE);
        mCbSendFullImg.setVisibility(View.VISIBLE);
    }

    private void onSubmitBtnUpdate() {

        int count = 0;
        for (MediaItem item : mMediaPreSelectedList) {
            if (item == null) {
                continue;
            }
            if (item.isSelected) {
                count++;
            }
        }
        if (count == 0) {
            mTvSend.setText(getString(R.string.button_send));
            mTvSend.setTextColor(getResources().getColor(R.color.title_bar_rightest_text_gray));
            return;
        }

        if (FromAction.CAMERA != mFromAction) {
            String sentText;
            if (isSingleImgSelectLimit()) {
                sentText = getString(R.string.button_send);
            } else {
                sentText = getString(R.string.button_send) + "(" + count + "/" + getMaxImgChooseCount() + ")";
            }
            mTvSend.setTextColor(getResources().getColor(R.color.common_item_black));
            mTvSend.setText(sentText);
        }

    }

    private void onEditBtnUpdate() {
        MediaItem currentMediaItem = mMediaPreSelectedList.get(mCurrentPos);

        if(!MediaSelectHelper.isStaticImage(currentMediaItem)) {
            mTvEdit.setVisibility(View.GONE);
            return;
        }

        int count = 0;
        int posSelected = -1;
        for (int i = 0; i < mMediaPreSelectedList.size(); i++) {
            MediaItem item = mMediaPreSelectedList.get(i);
            if (item == null) {
                continue;
            }
            if (item.isSelected) {
                count++;

                posSelected = i;
            }
        }

        if (1 == count && mCurrentPos == posSelected) {
            mTvEdit.setTextColor(ContextCompat.getColor(MediaPreviewActivity.this, R.color.white));
            mTvEdit.setVisibility(View.VISIBLE);

        } else {

            mTvEdit.setVisibility(View.GONE);

        }
    }

    private boolean isFullImgOverLimit() {
        for(MediaItem imageItem : mMediaPreSelectedList) {
            if (imageItem.isSelected) {
                if(isFullImgOverLimit(imageItem.size)) {
                    return true;
                }
            }
        }

        return false;

    }

    private boolean isFullImgOverLimit(long prepareSelectImg) {

        return AtworkConfig.CHAT_FULL_IMG_SELECT_LIMIT_SIZE < prepareSelectImg;
    }

    private boolean isNoneSelected() {
        boolean isNoneSelected = true;
        for(MediaItem imageItem : mMediaPreSelectedList) {
            if (imageItem.isSelected) {
                isNoneSelected = false;
                break;
            }
        }

        return isNoneSelected;

    }

    private void onFullModeBtnUpdate() {
        long totalSize = 0;
        for(MediaItem imageItem : mMediaPreSelectedList) {
            if (imageItem.isSelected) {
                totalSize += imageItem.size;
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

        mCbSendFullImg.setChecked(mIsFullMode);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        finishAndSetResult(intent, false);
    }

    public List<MediaItem> getImgSelectedList() {
        List<MediaItem> imgSelectedList = new ArrayList<>();
        for(MediaItem imageItem : mMediaPreSelectedList) {
            if(imageItem.isSelected) {
                imgSelectedList.add(imageItem);
            }
        }

        return imgSelectedList;
    }


    public boolean isChooseImageLimit() {
        return null != mChooseMediasRequest;
    }

    public boolean isSingleImgSelectLimit() {
        return isChooseImageLimit() && mChooseMediasRequest.isSingleType() || mIsSingleMode;
    }

    public int getMaxImgChooseCount() {
        if(isChooseImageLimit()) {
            return mChooseMediasRequest.mFileLimit.mMaxSelectCount;
        }

        return 9;
    }



    /**
     * 打开预览的途径
     * */
    public enum FromAction {

        CAMERA,

        IMAGE_SELECT

    }
}
