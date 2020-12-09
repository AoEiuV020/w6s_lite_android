package com.foreverht.workplus.ui.component.dialogFragment;


import android.app.Dialog;
import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.foreverht.workplus.ui.component.R;
import com.foreverht.workplus.ui.component.textview.LetterSpacingTextView;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.utils.DensityUtil;
import com.foreveross.atwork.infrastructure.utils.statusbar.StatusBarUtil;

public class W6sAtworkAlertDialog extends Dialog implements AtworkAlertInterface {

    private Context mContext;

    private TextView mTvTitle;
    private View mMainDivider1;
    private View mMainDivider2;
    private View mBottomDivider;
    private RelativeLayout mRlContentBoardText;
    private LetterSpacingTextView mTvContent;
    private RelativeLayout mRlBoardProgress;
    private LinearLayout mLlBottom;
    private TextView mTvDeadColor;
    private TextView mTvBrightColor;
    private TextView mTvProgressDesc;
    private ProgressBar mProgressBar;
    private TextView mTvProgress;

    public boolean shouldHandleDismissEvent = true;

    /**
     * 默认CLASSIC类型
     * */
    public W6sAtworkAlertDialog(Context context){
        this(context, Type.CLASSIC);
    }

    public W6sAtworkAlertDialog(Context context, Type type){
        super(context, R.style.app_alert_dialog);
        mContext = context;

        View view = View.inflate(mContext, R.layout.dialog_setting_alert, null);
        initViews(view);
        registerDefaultListener();

        setType(type);

        StatusBarUtil.setTransparentFullScreen(getWindow());
    }

    public void initViews(View view){
        mTvTitle = view.findViewById(R.id.tv_tittle);
        mMainDivider1 = view.findViewById(R.id.v_vertical_divider_1st);
        mMainDivider2 = view.findViewById(R.id.v_vertical_divider_2nd);
        mBottomDivider = view.findViewById(R.id.v_horizontal_divider);
        mRlContentBoardText = view.findViewById(R.id.rl_content_board_text);
        mTvContent = view.findViewById(R.id.tv_content);
        mRlBoardProgress = view.findViewById(R.id.rl_main_board_progress);
        mLlBottom = view.findViewById(R.id.ll_bottom);
        mTvDeadColor = view.findViewById(R.id.tv_dead_color);
        mTvBrightColor = view.findViewById(R.id.tv_bright_color);

        mTvProgress = view.findViewById(R.id.tv_progress);
        mTvProgressDesc = view.findViewById(R.id.tv_desc);
        mProgressBar = view.findViewById(R.id.pb_loading);
        this.setContentView(view);
    }

    private void registerDefaultListener(){
        mTvDeadColor.setOnClickListener(v -> dismiss());

        mTvBrightColor.setOnClickListener(v -> dismiss());
    }


    public W6sAtworkAlertDialog setType(Type type){
        if(Type.PROGRESS == type){
            mRlBoardProgress.setVisibility(View.VISIBLE);
            mRlContentBoardText.setVisibility(View.GONE);
        } else if(Type.SIMPLE == type){
            mRlBoardProgress.setVisibility(View.GONE);
            mRlContentBoardText.setVisibility(View.VISIBLE);
            hideTitle();
        }
        else {
            mRlBoardProgress.setVisibility(View.GONE);
            mRlContentBoardText.setVisibility(View.VISIBLE);
        }
        return this;
    }

    public W6sAtworkAlertDialog setTitleText(int titleId){
        return setTitleText(mContext.getString(titleId));
    }

    public W6sAtworkAlertDialog setTitleText(String title){
        mTvTitle.setText(title);
        return this;
    }

    public W6sAtworkAlertDialog hideTitle(){
        mTvTitle.setVisibility(View.GONE);
        mMainDivider1.setVisibility(View.GONE);
        return this;
    }

    public W6sAtworkAlertDialog setContent(int contentId){
        return setContent(mContext.getString(contentId));
    }

    public W6sAtworkAlertDialog setContent(String content){
        mTvContent.setText(content);
        adjustContentView();
        return this;
    }

    public W6sAtworkAlertDialog setBrightBtnText(int textId){
        return setBrightBtnText(mContext.getString(textId));
    }

    public W6sAtworkAlertDialog setBrightBtnText(String text){
        mTvBrightColor.setText(text);
        return this;
    }

    public W6sAtworkAlertDialog setBrightBtnTextColor(int color) {
        mTvBrightColor.setTextColor(color);
        return this;
    }

    public W6sAtworkAlertDialog setDeadBtnText(int textId){
        return setDeadBtnText(mContext.getString(textId));
    }

    public W6sAtworkAlertDialog setDeadBtnText(String text){
        mTvDeadColor.setText(text);
        return this;
    }

    public W6sAtworkAlertDialog setDeadBtnTextColor(int color) {
        mTvDeadColor.setTextColor(color);
        return this;
    }

    public W6sAtworkAlertDialog setProgressDesc(int textId){
        return setProgressDesc(mContext.getString(textId));
    }

    public W6sAtworkAlertDialog setProgressDesc(String text){
        mTvProgressDesc.setText(text);
        return this;
    }

    public W6sAtworkAlertDialog showBrightBtn(){
        mBottomDivider.setVisibility(View.VISIBLE);
        mTvBrightColor.setVisibility(View.VISIBLE);
        return this;
    }

    public W6sAtworkAlertDialog showDeadBtn(){
        mBottomDivider.setVisibility(View.VISIBLE);
        mTvDeadColor.setVisibility(View.VISIBLE);
        return this;
    }

    public W6sAtworkAlertDialog hideBrightBtn(){
        mBottomDivider.setVisibility(View.GONE);
        mTvBrightColor.setVisibility(View.GONE);
        return this;
    }

    public W6sAtworkAlertDialog hideDeadBtn(){
        mBottomDivider.setVisibility(View.GONE);
        mTvDeadColor.setVisibility(View.GONE);
        return this;
    }


    public W6sAtworkAlertDialog setMax(int max) {
        mProgressBar.setMax(max);
        return this;
    }

    public void setProgress(int progress) {
        mTvProgress.setText(progress + "/" + mProgressBar.getMax());
        mProgressBar.setProgress(progress);
    }



    public W6sAtworkAlertDialog setClickDeadColorListener(final OnDeadBtnClickListener listener) {
        mTvDeadColor.setOnClickListener(v -> {
            listener.onClick(W6sAtworkAlertDialog.this);
            dismiss();
        });
        return this;
    }

    public W6sAtworkAlertDialog setClickBrightColorListener(final OnBrightBtnClickListener listener) {
        mTvBrightColor.setOnClickListener(v -> {
            listener.onClick(W6sAtworkAlertDialog.this);
            dismiss();
        });
        return this;
    }
    public W6sAtworkAlertDialog setDismissListener(OnDismissListener listener){
        setOnDismissListener(listener);
        return this;
    }

    public W6sAtworkAlertDialog setForbiddenBack() {
        setOnKeyListener((dialog, keyCode, event) -> KeyEvent.KEYCODE_BACK == keyCode);
        return this;
    }


    public W6sAtworkAlertDialog setAlertCanceledOnTouchOutside(boolean cancel) {
        super.setCanceledOnTouchOutside(cancel);
        return this;
    }

    public W6sAtworkAlertDialog setNeverDie() {
        setClickDeadColorListener(dialog -> BaseApplicationLike.sAppLike.exitApp(false));
        this.setCancelable(false);
        return this;
    }

    public W6sAtworkAlertDialog setDialogCancelable(boolean flag) {
        this.setCancelable(flag);
        return this;
    }

    /**
     * 提示内容大于2行时, 为了避免拥挤, 调整字体间距(android L 以下需要自行实现字体间隙)
     * */
    private void adjustContentView(){
        if(2 < mTvContent.getLineCount()){
            mTvContent.setLetterSpacing(1f);
            final ViewTreeObserver vo = mTvContent.getViewTreeObserver();
            vo.addOnGlobalLayoutListener(() -> {
                int dialogWidth = DensityUtil.dip2px(270);
                int pLeft  = (dialogWidth - mTvContent.getMeasuredWidth()) / 2;
                mTvContent.setPadding(pLeft, mTvContent.getPaddingTop(), pLeft, mTvContent.getPaddingBottom());
            });
        }

    }

    public enum Type {
        /** 最典型的有标题, 有内容中间体部分, 以及"确认" "取消"按钮 */
        CLASSIC,
        /** 带有进度条 */
        PROGRESS,
        /** CLASSIC 的基础上少了标题 */
        SIMPLE
    }
}

