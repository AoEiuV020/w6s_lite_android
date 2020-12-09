package com.foreveross.atwork.modules.image.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.foreverht.threadGear.AsyncTaskThreadPool;
import com.foreverht.workplus.ui.component.dialogFragment.AtworkAlertDialog;
import com.foreveross.atwork.R;
import com.foreveross.atwork.component.ProgressDialogHelper;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.file.MediaItem;
import com.foreveross.atwork.infrastructure.utils.BitmapUtil;
import com.foreveross.atwork.infrastructure.utils.DensityUtil;
import com.foreveross.atwork.infrastructure.utils.ImageShowHelper;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.modules.image.activity.ImageEditActivity;
import com.foreveross.atwork.modules.image.component.ImageEditTextInputDialogFragment;
import com.foreveross.atwork.modules.image.component.ItemEnlargeImageView;
import com.foreveross.atwork.support.BackHandledFragment;
import com.foreveross.atwork.utils.ImageCacheHelper;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import java.util.HashMap;
import java.util.Map;

import cn.jarlen.photoedit.operate.ImageObject;
import cn.jarlen.photoedit.operate.MultiInputTextView;
import cn.jarlen.photoedit.operate.OperateUtils;
import cn.jarlen.photoedit.operate.TextObject;
import cn.jarlen.photoedit.scrawl.GraffitiView;

/**
 * Created by dasunsy on 2016/11/9.
 */

public class ImageEditFragment extends BackHandledFragment {

    public static final String ACTION_REFRESH_INPUT_TEXT = "action_refresh_input_text";

    private ItemEnlargeImageView mImagePreview;
    private ImageView mIvRollback;
    private View mVWhite;
    private FrameLayout mFlWhite;
    private View mVRed;
    private FrameLayout mFlRed;
    private View mVOrange;
    private FrameLayout mFlOrange;
    private View mVYellow;
    private FrameLayout mFlYellow;
    private View mVGreen;
    private FrameLayout mFlGreen;
    private View mVBlue;
    private FrameLayout mFlBlue;
    private View mVViolet;
    private FrameLayout mFlViolet;
    private HorizontalScrollView mHsSubsetFunction;
    private TextView mTvCancel;
    private ImageView mIvGraffiti;
    private ImageView mIvTextInput;
    private TextView mTvSend;
    private RelativeLayout mLlFunctionBottom;
    private LinearLayout mLlImageEditShow;

    private MediaItem mImageItem;

    private GraffitiView mVDraw;
    private MultiInputTextView mVMultiInputText;

    private Mode mMode = Mode.DRAW;
    private Color mColor = Color.WHITE;

    private HashMap<Color, FrameLayout> mColorViewMap = new HashMap<>();

    private Bitmap mBitmap;

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            mVMultiInputText.showAllTextObjs();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        registerBroadcast();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image_edit, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        registerListener();
        initData();

        refreshUIMode(Mode.DRAW);


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder();
        builder.considerExifParams(true);

        // 设置图片
        ImageCacheHelper.loadImage(mImageItem.filePath, null, builder.build(), new ImageCacheHelper.ImageLoadedListener() {

            @Override
            public void onImageLoadedComplete(Bitmap bitmap) {
                mBitmap = OperateUtils.compressionFiller(getActivity(), bitmap, mVDraw);

                doAction(Mode.DRAW);

//                mImagePreview.setImageBitmap(bitmap);
            }

            @Override
            public void onImageLoadedFail() {


            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        unregisterBroadcast();
    }

    @Override
    protected void findViews(View view) {
        this.mLlFunctionBottom = view.findViewById(R.id.ll_function_bottom);
        this.mTvSend = view.findViewById(R.id.tv_send);
        this.mIvTextInput = view.findViewById(R.id.iv_text_input);
        this.mIvGraffiti = view.findViewById(R.id.iv_graffiti);
        this.mTvCancel = view.findViewById(R.id.tv_cancel);
        this.mHsSubsetFunction = view.findViewById(R.id.hs_subset_function);
        this.mFlViolet = view.findViewById(R.id.fl_violet);
        this.mVViolet = view.findViewById(R.id.v_violet);
        this.mFlBlue = view.findViewById(R.id.fl_blue);
        this.mVBlue = view.findViewById(R.id.v_blue);
        this.mFlGreen = view.findViewById(R.id.fl_green);
        this.mVGreen = view.findViewById(R.id.v_green);
        this.mFlYellow = view.findViewById(R.id.fl_yellow);
        this.mVYellow = view.findViewById(R.id.v_yellow);
        this.mFlOrange = view.findViewById(R.id.fl_orange);
        this.mVOrange = view.findViewById(R.id.v_orange);
        this.mFlRed = view.findViewById(R.id.fl_red);
        this.mVRed = view.findViewById(R.id.v_red);
        this.mFlWhite = view.findViewById(R.id.fl_white);
        this.mVWhite = view.findViewById(R.id.v_white);
        this.mIvRollback = view.findViewById(R.id.iv_rollback);
        this.mImagePreview = view.findViewById(R.id.image_preview);
        this.mVDraw = view.findViewById(R.id.v_draw);
        this.mVMultiInputText = view.findViewById(R.id.v_add_text);
        this.mLlImageEditShow = view.findViewById(R.id.ll_image_edit_show);

    }

    private void registerListener() {
        mIvRollback.setOnClickListener((v)->{
            mVDraw.undo();
        });

        mTvCancel.setOnClickListener((v) -> {
            handleFinish();
        });

        mTvSend.setOnClickListener((v)->{
            boolean isModified = false;


            if(Mode.TEXT == mMode) {
                if(mVMultiInputText.isModified()) {
                    mBitmap = mVMultiInputText.getBitmapByView();
                    isModified = true;
                }
            } else if(Mode.DRAW == mMode) {
                if(mVDraw.isModified()) {
                    mBitmap = mVDraw.getGraffitiBitmap();
                    isModified = true;
                }

            }

            if(isModified) {
                ProgressDialogHelper progressDialogHelper = new ProgressDialogHelper(getActivity());
                progressDialogHelper.show();

                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected String doInBackground(Void... params) {
                        String path = null;
                        byte[] content = BitmapUtil.Bitmap2Bytes(mBitmap);

                        if (content != null && content.length != 0) {
                            path = ImageShowHelper.saveImageToGalleryAndGetPath(BaseApplicationLike.baseContext, content, null, false);
                        }

                        return path;
                    }

                    @Override
                    protected void onPostExecute(String path) {
                        progressDialogHelper.dismiss();

                        if(!StringUtils.isEmpty(path)) {
                            mImageItem.filePath = path;

                            finishAndSetResult();
                        }
                    }
                }.executeOnExecutor(AsyncTaskThreadPool.getInstance());

            } else {
                finishAndSetResult();
            }




        });

        mFlWhite.setOnClickListener((v) -> {
            refreshColor(Color.WHITE);

            refreshPen();

        });

        mFlRed.setOnClickListener((v) -> {
            refreshColor(Color.RED);

            refreshPen();

        });

        mFlGreen.setOnClickListener((v) -> {
            refreshColor(Color.GREEN);

            refreshPen();

        });

        mFlBlue.setOnClickListener((v) -> {
            refreshColor(Color.BLUE);

            refreshPen();

        });

        mFlOrange.setOnClickListener((v) -> {
            refreshColor(Color.ORANGE);

            refreshPen();

        });

        mFlViolet.setOnClickListener((v) -> {

            refreshColor(Color.VIOLET);

            refreshPen();

        });

        mFlYellow.setOnClickListener((v) -> {

            refreshColor(Color.YELLOW);

            refreshPen();

        });

        mIvGraffiti.setOnClickListener((v) -> {
            if (Mode.DRAW != mMode && null != mBitmap) {
                refreshUIMode(Mode.DRAW);

                doAction(Mode.DRAW);
            }

        });

        mIvTextInput.setOnClickListener((v) -> {
            if (Mode.TEXT != mMode && null != mBitmap) {
                refreshUIMode(Mode.TEXT);

                doAction(Mode.TEXT);
            }

        });


    }

    private void handleFinish() {
        if(mVMultiInputText.isModified() || mVDraw.isModified()) {
            AtworkAlertDialog dialog = new AtworkAlertDialog(getActivity(), AtworkAlertDialog.Type.SIMPLE);
            dialog.setContent(R.string.not_yet_save_content)
                    .setBrightBtnText(R.string.give_up)
                    .setClickBrightColorListener(dialog1 -> {
                finish();
            })
            .show();

        } else {
            finish();

        }
    }

    private void finishAndSetResult() {
        Intent intent = new Intent();
        intent.putExtra(ImageEditActivity.DATA_IMAGE,  mImageItem);
        getActivity().setResult(Activity.RESULT_OK, intent);
        finish();
    }

    private void refreshColor(Color color) {
        mColor = color;
        refreshFlUI(color);
    }


    private void initData() {
        Bundle bundle = getArguments();
        mImageItem = (MediaItem) bundle.getSerializable(ImageEditActivity.DATA_IMAGE);

        initColorFlMap();
    }


    private void refreshUIMode(Mode mode) {
        mMode = mode;
        switch (mode) {
            case DRAW:
                mIvGraffiti.setImageResource(R.mipmap.icon_graffiti_open);
                mIvTextInput.setImageResource(R.mipmap.icon_text_input_close);

                mIvRollback.setVisibility(View.VISIBLE);

                mVDraw.setVisibility(View.VISIBLE);
                mVMultiInputText.setVisibility(View.GONE);

                refreshColor(Color.RED);

                break;

            case TEXT:
                mIvGraffiti.setImageResource(R.mipmap.icon_graffiti_close);
                mIvTextInput.setImageResource(R.mipmap.icon_text_input_open);

                mIvRollback.setVisibility(View.GONE);

                mVDraw.setVisibility(View.GONE);
                mVMultiInputText.setVisibility(View.VISIBLE);

                refreshColor(Color.RED);


                break;
        }
    }

    private void doAction(Mode mode) {
        switch (mode) {
            case DRAW:
                if(mVMultiInputText.isModified()) {
                    mBitmap = mVMultiInputText.getBitmapByView();
                    mVMultiInputText.clearInputText();
                }

                LinearLayout.LayoutParams layoutParamsDraw = new LinearLayout.LayoutParams(
                        mBitmap.getWidth(), mBitmap.getHeight());

                mVDraw.setLayoutParams(layoutParamsDraw);

                mVDraw.setBitmap(mBitmap);
//                mCasualWaterUtil = new ScrawlTools(getActivity(), mVDraw, mBitmap);


                refreshPen();

                break;


            case TEXT:
                if(mVDraw.isModified()) {
                    mBitmap = mVDraw.getGraffitiBitmap();
                    mVDraw.clearPath();
                }

                mVMultiInputText.setBitmap(mBitmap);
                LinearLayout.LayoutParams layoutParamsText = new LinearLayout.LayoutParams(
                        mBitmap.getWidth(), mBitmap.getHeight());
                mVMultiInputText.setLayoutParams(layoutParamsText);
                mVMultiInputText.setMultiAdd(true); //设置此参数，可以添加多个文字
                mVMultiInputText.setColor(getColor());
                mVMultiInputText.setOnEditTextListener(tObject -> {

                    mVMultiInputText.hideNewTextObjs();

                    ImageEditTextInputDialogFragment dialogFragment = new ImageEditTextInputDialogFragment();

                    Bundle bundle = new Bundle();
                    bundle.putParcelable(ImageEditTextInputDialogFragment.DATA_TEXT_OBJ, tObject);
                    dialogFragment.setArguments(bundle);
                    dialogFragment.show(getChildFragmentManager(), "imgedit_edittext");

                });

                int x = mBitmap.getWidth() / 2;
                int y = mBitmap.getHeight() / 2;
                mVMultiInputText.newTextObj(x, y);

                break;
        }
    }

    private void refreshPen() {
        if (Mode.DRAW == mMode) {

            mVDraw.setColor(getColor());

        } else if(Mode.TEXT == mMode) {
            mVMultiInputText.setColor(getColor());

            ImageObject imageObject = mVMultiInputText.getSelected();

            if(null != imageObject && imageObject instanceof TextObject) {
                TextObject textObject = (TextObject) imageObject;
                textObject.setColor(getColor());
                textObject.setTextSize(DensityUtil.sp2px(24));
                textObject.commit();
                mVMultiInputText.invalidate();
            }
        }
    }

    public static void refreshInputTexts() {
        LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).sendBroadcast(new Intent(ACTION_REFRESH_INPUT_TEXT));
    }

    private void registerBroadcast() {
        LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).registerReceiver(mBroadcastReceiver, new IntentFilter(ACTION_REFRESH_INPUT_TEXT));
    }

    private void unregisterBroadcast() {
        LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).unregisterReceiver(mBroadcastReceiver);

    }


    @Override
    protected boolean onBackPressed() {
        handleFinish();
        return true;
    }


    private void refreshFlUI(Color color) {
        for (Map.Entry<Color, FrameLayout> entry : mColorViewMap.entrySet()) {
            if (color == entry.getKey()) {
                entry.getValue().setBackgroundResource(R.mipmap.icon_color_picked);
            } else {
                entry.getValue().setBackgroundResource(0);

            }
        }
    }

    private int getColor() {
        int colorInt = ContextCompat.getColor(getActivity(), R.color.white);
        switch (mColor) {
            case WHITE:
                colorInt = ContextCompat.getColor(getActivity(), R.color.white);
                break;

            case RED:
                colorInt = ContextCompat.getColor(getActivity(), R.color.red);
                break;

            case GREEN:
                colorInt = ContextCompat.getColor(getActivity(), R.color.green);
                break;

            case YELLOW:
                colorInt = ContextCompat.getColor(getActivity(), R.color.yellow);
                break;

            case BLUE:
                colorInt = ContextCompat.getColor(getActivity(), R.color.blue);
                break;

            case VIOLET:
                colorInt = ContextCompat.getColor(getActivity(), R.color.violet);
                break;

            case ORANGE:
                colorInt = ContextCompat.getColor(getActivity(), R.color.orange);
                break;
        }

        return colorInt;
    }

    private void initColorFlMap() {
        mColorViewMap.put(Color.BLUE, mFlBlue);
        mColorViewMap.put(Color.WHITE, mFlWhite);
        mColorViewMap.put(Color.GREEN, mFlGreen);
        mColorViewMap.put(Color.RED, mFlRed);
        mColorViewMap.put(Color.ORANGE, mFlOrange);
        mColorViewMap.put(Color.YELLOW, mFlYellow);
        mColorViewMap.put(Color.VIOLET, mFlViolet);
    }

    enum Color {

        WHITE,

        RED,

        GREEN,

        YELLOW,

        BLUE,

        VIOLET,

        ORANGE


    }

    enum Mode {

        /**
         * 涂鸦
         */
        DRAW,


        /**
         * 添加文字
         */
        TEXT
    }
}
