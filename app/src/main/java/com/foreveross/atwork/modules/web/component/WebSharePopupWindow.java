package com.foreveross.atwork.modules.web.component;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.foreverht.workplus.ui.component.dialogFragment.BasicUIDialogFragment;
import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.model.WebViewControlAction;
import com.foreveross.atwork.infrastructure.utils.statusbar.StatusBarUtil;
import com.foreveross.atwork.modules.web.adapter.WebSharePopupAdapter;
import com.foreveross.atwork.modules.web.model.WebShareBuilder;

import org.jetbrains.annotations.NotNull;


public class WebSharePopupWindow extends BasicUIDialogFragment {

    private Context mContext;

    private RelativeLayout mRlRoot;

    private GridView mGridView;

    private TextView mCancelView;

    private WebSharePopupAdapter mWebSharePopupAdapter;

    private WebShareBuilder mBuilder;


    private Boolean mFloatMode;

    private boolean mNeedCommonModeExcludeRefresh;

    private boolean mNeedSetPreLoadMode;

    private boolean mNeedSetInnerShareModeList;

    private Integer mFrom;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_NoTitleBar);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View popupView = inflater.inflate(R.layout.web_popupview, container, false);
        mRlRoot = popupView.findViewById(R.id.rl_root);
        mGridView = popupView.findViewById(R.id.web_item_grid_view);
        mCancelView = popupView.findViewById(R.id.web_popup_cancel);

        return popupView;
    }

    @Override
    public void onViewCreated(@NotNull View view, @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(0));

        if(null == mBuilder) {
            dismiss();
            return;
        }

        mBuilder.setWebSharePopupWindow(this);

        mWebSharePopupAdapter = new WebSharePopupAdapter(mBuilder);
        mWebSharePopupAdapter.setItemData(mBuilder.getArticleItem());

        mGridView.setAdapter(mWebSharePopupAdapter);

        mRlRoot.setOnClickListener(v -> dismiss());
        mCancelView.setOnClickListener(v -> dismiss());


        if(null != mFloatMode) {
            mWebSharePopupAdapter.setHookingFloatMode(mFloatMode);
        }

        if(mNeedCommonModeExcludeRefresh) {
            mWebSharePopupAdapter.setCommonModeExcludeRefresh();

        }

        if(mNeedSetPreLoadMode) {
            mWebSharePopupAdapter.setPreLoadMode();
        }

        if(mNeedSetInnerShareModeList) {
            mWebSharePopupAdapter.setInnerShareMode();
        }

        if(null != mFrom) {
            mWebSharePopupAdapter.setCommonMode(mFrom);

        }
    }

    @Override
    public void changeStatusBar(View view) {
        StatusBarUtil.setTransparentFullScreen(getDialog().getWindow());
    }

    public void setBuilder(WebShareBuilder builder) {
        this.mBuilder = builder;
    }

    //    public void WebSharePopupWindow(WebShareBuilder builder) {
//        mContext = builder.getContext();
//
//        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View popupView = inflater.inflate(R.layout.web_popupview, null);
//        mGridView = popupView.findViewById(R.id.web_item_grid_view);
//        mCancelView = popupView.findViewById(R.id.web_popup_cancel);
//
//        builder.setWebSharePopupWindow(this);
//        mWebSharePopupAdapter = new WebSharePopupAdapter(builder);
//        mWebSharePopupAdapter.setItemData(builder.getArticleItem());
//
//
//        mGridView.setAdapter(mWebSharePopupAdapter);
//        mCancelView.setOnClickListener(v -> dismiss());
//    }

    public static WebShareBuilder newBuilder() {
        return new WebShareBuilder();
    }

    public void setCommonModeList() {
        this.mFrom = WebViewControlAction.FROM_OTHER;
//        mWebSharePopupAdapter.setCommonMode();
    }

    public void setCommonModeList(int from) {
        this.mFrom = from;
    }

    public void setInnerShareModeList() {
        mNeedSetInnerShareModeList = true;

    }


    public void setPreLoadMode() {
        this.mNeedSetPreLoadMode = true;

    }

    public void setCommonModeExcludeRefresh() {
        this.mNeedCommonModeExcludeRefresh = true;
    }


    public void setHookingFloatMode(boolean floatMode) {
        this.mFloatMode = floatMode;

    }




}
