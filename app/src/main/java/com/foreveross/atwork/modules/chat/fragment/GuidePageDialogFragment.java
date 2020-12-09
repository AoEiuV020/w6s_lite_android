package com.foreveross.atwork.modules.chat.fragment;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.foreveross.atwork.R;
import com.foreveross.atwork.component.BasicDialogFragment;
import com.foreveross.atwork.component.UnreadImageView;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.statusbar.StatusBarUtil;

/**
 * Created by dasunsy on 2017/12/25.
 */

public class GuidePageDialogFragment extends BasicDialogFragment {

    private View mVRoot;
    private LinearLayout mllGuideArea;
    private UnreadImageView mVGuideIcon;
    private TextView mTvContentGuide;
    private View mVGuideLine;

    private int mVGuideIconRes = -1;
    private String mGuideContent = StringUtils.EMPTY;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //设置Dialog占用全屏
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Light_NoTitleBar);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_guide_page_chat_list, container, false);
        findViews(view);
        registerListener();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //该方法需要放在onViewCreated比较合适, 若在 onStart 在部分机型(如:小米3)会出现闪烁的情况
        getDialog().getWindow().setBackgroundDrawableResource(R.color.transparent_70);

        refreshView();
    }


    private void findViews(View view) {
        mVRoot = view.findViewById(R.id.rl_root);
        mllGuideArea = view.findViewById(R.id.ll_guide_area);
        mVGuideIcon = view.findViewById(R.id.iv_guide_icon);
//        mVGuideIcon.setIcon(R.mipmap.icon_bing_white);

        mTvContentGuide = view.findViewById(R.id.tv_guide);
        mVGuideLine = view.findViewById(R.id.v_line);
    }


    private void registerListener() {
        mVRoot.setOnClickListener((v)-> {
            GuidePageDialogFragment.this.dismiss();
        });

        mllGuideArea.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {

                int bingGuideEntryX = mVGuideIcon.getLeft() + (mVGuideIcon.getWidth() / 2);

                int floatTextGuideX = mllGuideArea.getLeft() + (mllGuideArea.getWidth() / 2);

                if (floatTextGuideX > bingGuideEntryX) {
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mllGuideArea.getLayoutParams();
                    layoutParams.rightMargin = floatTextGuideX - bingGuideEntryX;

                    mllGuideArea.setLayoutParams(layoutParams);

                } else if (floatTextGuideX < bingGuideEntryX) {
                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mVGuideLine.getLayoutParams();
                    layoutParams.gravity = Gravity.NO_GRAVITY;
                    layoutParams.leftMargin = mVGuideLine.getLeft() + bingGuideEntryX - floatTextGuideX;

                    mVGuideLine.setLayoutParams(layoutParams);
                }

                mllGuideArea.getViewTreeObserver().removeOnPreDrawListener(this);
                return false;
            }
        });


    }

    @Override
    protected void changeStatusBar(View view) {
        StatusBarUtil.setColorNoTranslucent((ViewGroup) view, getDialog().getWindow(), ContextCompat.getColor(BaseApplicationLike.baseContext, R.color.transparent_70));

    }

    public void setGuideIconRes(int mVGuideIconRes) {
        this.mVGuideIconRes = mVGuideIconRes;
    }

    public void setGuideContent(String mGuideContent) {
        this.mGuideContent = mGuideContent;
    }

    private void refreshView() {
        mVGuideIcon.setIcon(mVGuideIconRes);
        mTvContentGuide.setText(mGuideContent);
    }
}
