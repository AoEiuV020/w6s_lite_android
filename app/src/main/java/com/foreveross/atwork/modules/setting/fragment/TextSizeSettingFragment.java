package com.foreveross.atwork.modules.setting.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.R;
import com.foreveross.atwork.component.ResponseOnTouch;
import com.foreveross.atwork.component.WorkplusSwitchCompat;
import com.foreveross.atwork.component.seekbar.sliding.SlidingSeekBar;
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo;
import com.foreveross.atwork.modules.main.activity.MainActivity;
import com.foreveross.atwork.support.BackHandledFragment;

/**
 * Created by reyzhang22 on 2017/12/5.
 */

public class TextSizeSettingFragment extends BackHandledFragment implements ResponseOnTouch {

    private Activity mActivity;

    private ImageView mBack;
    private TextView mTitle;
    private TextView mSave;
    private SlidingSeekBar mSlidingSeekBar;
    private TextView mPreview1;
    private TextView mPreview2;
    private TextView mPreview3;
    private WorkplusSwitchCompat mVSwitchSetSyncWebviewFontSize;

    private int mCurrentTextSizeLevel;

    private boolean mForbiddenHandle = false;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_common_textsize_setting, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        mCurrentTextSizeLevel = PersonalShareInfo.getInstance().getTextSizeLevel(mActivity);
        changePreviewTextSize();
        mSlidingSeekBar.setThumbIndices(mCurrentTextSizeLevel, mCurrentTextSizeLevel);

        mVSwitchSetSyncWebviewFontSize.setChecked(PersonalShareInfo.getInstance().getCommonTextSizeSyncWebview(AtworkApplicationLike.baseContext));

//        MainActivity.recreateMainPage();
        registerListener();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void findViews(View view) {
        mBack = view.findViewById(R.id.title_bar_common_back);
        mTitle = view.findViewById(R.id.title_bar_common_title);
        mTitle.setText(R.string.set_text_size);
        mSave = view.findViewById(R.id.title_bar_common_right_text);
        mSave.setText(getString(R.string.done));
        mSave.setVisibility(View.VISIBLE);

        mPreview1 = view.findViewById(R.id.preview1);
        mPreview2 = view.findViewById(R.id.preview2);
        mPreview3 = view.findViewById(R.id.preview3);
        mSlidingSeekBar = view.findViewById(R.id.textsize_setting_bar);
        mVSwitchSetSyncWebviewFontSize = view.findViewById(R.id.v_switch_set_sync_webview_font_size);
    }

    private void registerListener() {
        mBack.setOnClickListener(view ->{
            onBackPressed();
        });

        mSave.setOnClickListener(view -> {
            if(mForbiddenHandle) {
                return;
            }

            mForbiddenHandle = true;

            PersonalShareInfo.getInstance().setTextSizeLevel(mActivity, mCurrentTextSizeLevel);

//            if(PersonalShareInfo.getInstance().getCommonTextSizeSyncWebview(mActivity)) {
//                PersonalShareInfo.getInstance().setWebviewTextSizeLevel(mActivity, mCurrentTextSizeLevel);
//            }

            MainActivity.recreateMainPage();
            new Handler().postDelayed(() -> {
                Intent intent = MainActivity.getMainActivityIntent(getActivity(), true);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }, 100);
        });

        mSlidingSeekBar.setOnRangeBarChangeListener((rangeBar, leftThumbIndex, rightThumbIndex) -> {
            mSlidingSeekBar.setThumbIndices(leftThumbIndex, rightThumbIndex);
            mCurrentTextSizeLevel = leftThumbIndex;
            changePreviewTextSize();
        });


        mVSwitchSetSyncWebviewFontSize.setOnClickNotPerformToggle(() -> {
            mVSwitchSetSyncWebviewFontSize.toggle();
            PersonalShareInfo.getInstance().setCommonTextSizeSyncWebview(AtworkApplicationLike.baseContext, mVSwitchSetSyncWebviewFontSize.isChecked());

            if(mVSwitchSetSyncWebviewFontSize.isChecked()) {
                toast(R.string.open_font_size_sync_webview_successfully);

            } else {
                toast(R.string.close_font_size_sync_webview_successfully);

            }
        });
    }

    private void changePreviewTextSize() {
        new Handler().postDelayed(() -> mActivity.runOnUiThread(() -> {
            float textSize = 0;
            switch (mCurrentTextSizeLevel) {
                case 0:
                    textSize = 12;
                    break;

                case 1:
                    textSize = 14;
                    break;

                case 2:
                    textSize = 16;
                    break;

                case 3:
                    textSize = 18;
                    break;

                case 4:
                    textSize = 20;
                    break;

                case 5:
                    textSize = 22;
                    break;
            }
            mPreview1.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
            mPreview2.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
            mPreview3.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
        }), 100);
    }


    @Override
    protected boolean onBackPressed() {
        if (!mForbiddenHandle) {
            finish();
        }
        return false;
    }

    @Override
    public void onTouchResponse(int volume) {

    }

}
