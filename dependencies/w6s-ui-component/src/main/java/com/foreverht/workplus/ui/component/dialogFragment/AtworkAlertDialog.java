package com.foreverht.workplus.ui.component.dialogFragment;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.foreverht.workplus.ui.component.R;
import com.foreverht.workplus.ui.component.textview.LetterSpacingTextView;
import com.foreveross.atwork.infrastructure.utils.DensityUtil;
import com.foreveross.atwork.infrastructure.utils.ScreenUtils;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.ViewUtil;
import com.foreveross.atwork.infrastructure.utils.statusbar.StatusBarUtil;

public class AtworkAlertDialog extends Dialog implements AtworkAlertInterface {

    private Context mContext;

    private ScrollView mSwContentBoardProgress;
    private TextView mTvTitle;
    //private View mMainDivider1;
    // private View mMainDivider2;
//    private View mBottomDivider;
    private RelativeLayout mRlContentBoardText;
    private LetterSpacingTextView mTvContent;
    private RelativeLayout mRlBoardProgress;
    // private LinearLayout mLlBottom;
    private TextView mTvDeadColor;
    private TextView mTvBrightColor;
    private TextView mTvProgressDesc;
    private ProgressBar mProgressBar;
    private TextView mTvProgress;

    private View mRlContentBoardInput;
    public EditText mEtInputContent;
    private ImageView mAllDelBtn;
    private View mViewUnderLine;
    private TextView mTvInputContent;

    public boolean shouldHandleDismissEvent = true;

    private OnEditInputListener mInputListener;
    private Type mType;

    /**
     * 默认CLASSIC类型
     * */
    public AtworkAlertDialog(Context context){
        this(context, Type.CLASSIC);
    }

    public void setOnEditInputListener(OnEditInputListener listener) {
        mInputListener = listener;
    }


    public AtworkAlertDialog(Context context, Type type){
        super(context, R.style.app_alert_dialog);
        mContext = context;
        View view = View.inflate(mContext, R.layout.dialog_common_alert, null);
        initViews(view);
        registerDefaultListener();

        setType(type);


        StatusBarUtil.setTransparentFullScreenAndStatusBar(getWindow(), true);
    }

    public void initViews(View view){
        mSwContentBoardProgress = view.findViewById(R.id.sw_content_board_progress);
        mTvTitle = view.findViewById(R.id.tv_tittle);
        //mMainDivider1 = view.findViewById(R.id.v_vertical_divider_1st);
        // mMainDivider2 = view.findViewById(R.id.v_vertical_divider_2nd);
//        mBottomDivider = view.findViewById(R.id.v_horizontal_divider);
        mRlContentBoardText = view.findViewById(R.id.rl_content_board_text);
        mTvContent = view.findViewById(R.id.tv_content);
        mRlBoardProgress = view.findViewById(R.id.rl_main_board_progress);
        // mLlBottom = view.findViewById(R.id.ll_bottom);
        mTvDeadColor = view.findViewById(R.id.tv_dead_color);
        mTvBrightColor = view.findViewById(R.id.tv_bright_color);

        mTvProgress = view.findViewById(R.id.tv_progress);
        mTvProgressDesc = view.findViewById(R.id.tv_desc);
        mProgressBar = view.findViewById(R.id.pb_loading);

        mRlContentBoardInput = view.findViewById(R.id.rl_main_board_input);
        mEtInputContent = mRlContentBoardInput.findViewById(R.id.et_input_text);
        mAllDelBtn = view.findViewById(R.id.all_del_Btn);
        mViewUnderLine = view.findViewById(R.id.view_under_line);
        mTvInputContent = mRlContentBoardInput.findViewById(R.id.tv_input_content);

        this.setContentView(view);
    }

    private void registerDefaultListener(){
        mTvDeadColor.setOnClickListener(v -> dismiss());

        mTvBrightColor.setOnClickListener(v -> dismiss());

        mAllDelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEtInputContent.setText("");
            }
        });

        mEtInputContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable content) {
                ViewUtil.setVisible(mAllDelBtn, !StringUtils.isEmpty(content.toString()));
            }
        });
    }


    public AtworkAlertDialog setType(Type type){
        mType = type;
        if (Type.PROGRESS == mType) {
            mRlBoardProgress.setVisibility(View.VISIBLE);
            mRlContentBoardText.setVisibility(View.GONE);
            mRlContentBoardInput.setVisibility(View.GONE);
            return this;
        }
        if (Type.SIMPLE == mType) {
            mRlBoardProgress.setVisibility(View.GONE);
            mRlContentBoardText.setVisibility(View.VISIBLE);
            mRlContentBoardInput.setVisibility(View.GONE);
            hideTitle();
            return this;
        }
        if (Type.INPUT == mType) {
            mRlBoardProgress.setVisibility(View.GONE);
            mRlContentBoardText.setVisibility(View.GONE);
            mRlContentBoardInput.setVisibility(View.VISIBLE);
//            mMainDivider1.setVisibility(View.GONE);
            return this;
        }
        mRlContentBoardInput.setVisibility(View.GONE);
        mRlBoardProgress.setVisibility(View.GONE);
        mRlContentBoardText.setVisibility(View.VISIBLE);
        return this;
    }

    /**
     * 设置输入框底部横线的颜色
     * @return
     */
    public AtworkAlertDialog setViewUnderLineColor(int lineColor){
        mViewUnderLine.setBackgroundColor(mContext.getResources().getColor(lineColor));
        return this;
    }

    /**
     * 设置输入框底部横线的宽度
     * @return
     */
    public AtworkAlertDialog setViewUnderLineWidth(int lineHeight){
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, DensityUtil.dip2px(lineHeight));
        mViewUnderLine.setLayoutParams(layoutParams);
        return this;
    }


    /**
     * 设置标题
     * @param titleId
     * @return
     */
    public AtworkAlertDialog setTitleText(int titleId){
        return setTitleText(mContext.getString(titleId));
    }

    public AtworkAlertDialog setTitleText(String title){
        mTvTitle.setText(title);
        return this;
    }

    public AtworkAlertDialog hideTitle(){
        mTvTitle.setVisibility(View.GONE);
//        mMainDivider1.setVisibility(View.GONE);
        return this;
    }

    /**
     * 设置文本提示
     * @param contentId
     * @return
     */
    public AtworkAlertDialog setContent(int contentId){
        return setContent(mContext.getString(contentId));
    }

    public AtworkAlertDialog setContent(String content){
        mTvContent.setText(content);
        adjustContentView();
        return this;
    }

    /**
     * 设置左边按钮
     * @param textId
     * @return
     */
    public AtworkAlertDialog setBrightBtnText(int textId){
        return setBrightBtnText(mContext.getString(textId));
    }

    public AtworkAlertDialog setBrightBtnText(String text){
        mTvBrightColor.setText(text);
        return this;
    }

    public AtworkAlertDialog setBrightBtnTextColor(int color) {
        mTvBrightColor.setTextColor(color);
        return this;
    }

    /**
     * 设置右边按钮
     * @param textId
     * @return
     */
    public AtworkAlertDialog setDeadBtnText(int textId){
        return setDeadBtnText(mContext.getString(textId));
    }

    public AtworkAlertDialog setDeadBtnText(String text){
        mTvDeadColor.setText(text);
        return this;
    }

    public AtworkAlertDialog setDeadBtnTextColor(int color) {
        mTvDeadColor.setTextColor(color);
        return this;
    }

    public AtworkAlertDialog setProgressDesc(int textId){
        return setProgressDesc(mContext.getString(textId));
    }

    public AtworkAlertDialog setProgressDesc(String text){
        mTvProgressDesc.setText(text);
        return this;
    }

    public AtworkAlertDialog showBrightBtn(){
//        mBottomDivider.setVisibility(View.VISIBLE);
        mTvBrightColor.setVisibility(View.VISIBLE);
        return this;
    }

    public AtworkAlertDialog showDeadBtn(){
//        mBottomDivider.setVisibility(View.VISIBLE);
        mTvDeadColor.setVisibility(View.VISIBLE);
        return this;
    }

    public AtworkAlertDialog hideBrightBtn(){
//        mBottomDivider.setVisibility(View.GONE);
        mTvBrightColor.setVisibility(View.GONE);
        return this;
    }

    public AtworkAlertDialog hideDeadBtn(){
//        mBottomDivider.setVisibility(View.GONE);
        mTvDeadColor.setVisibility(View.GONE);
        return this;
    }


    public AtworkAlertDialog setMax(int max) {
        mProgressBar.setMax(max);
        return this;
    }

    public void setProgress(int progress) {
        mTvProgress.setText(progress + "/" + mProgressBar.getMax());
        mProgressBar.setProgress(progress);
    }

    public void setEditMaxLength(int length) {
        mEtInputContent.setFilters(new InputFilter[]{new InputFilter.LengthFilter(length)});
    }

    public void setInputType(int type) {
        mEtInputContent.setInputType(type);
    }

    public void setInputContent(int textId) {
        mTvInputContent.setText(mContext.getText(textId));
    }

    public void setInputContent(String inputContent) {
        mTvInputContent.setText(inputContent);
    }

    public void setInputEditContent(String inputContent) {
        mEtInputContent.setText(inputContent);
        mEtInputContent.setSelection(mEtInputContent.getText().toString().length());
    }

    public void setInputHint(int textId) {
        mEtInputContent.setHint(mContext.getString(textId));
    }

    public void setInputHint(String hint) {
        mEtInputContent.setHint(hint);
    }


    public AtworkAlertDialog setClickDeadColorListener(final AtworkAlertInterface.OnDeadBtnClickListener listener) {
        mTvDeadColor.setOnClickListener(v -> {
            listener.onClick(AtworkAlertDialog.this);
            dismiss();
        });
        return this;
    }

    public AtworkAlertDialog setClickBrightColorListener(final AtworkAlertInterface.OnBrightBtnClickListener listener) {
        mTvBrightColor.setOnClickListener(v -> {
            if (mInputListener != null && Type.INPUT == mType) {
                mInputListener.onTextValue(mEtInputContent.getText().toString());
                dismiss();
                return;
            }

            listener.onClick(AtworkAlertDialog.this);
            dismiss();
        });
        return this;
    }
    public AtworkAlertDialog setDismissListener(OnDismissListener listener){
        setOnDismissListener(listener);
        return this;
    }

    public AtworkAlertDialog setForbiddenBack() {
        setOnKeyListener((dialog, keyCode, event) -> KeyEvent.KEYCODE_BACK == keyCode);
        return this;
    }


    public AtworkAlertDialog setAlertCanceledOnTouchOutside(boolean cancel) {
        super.setCanceledOnTouchOutside(cancel);
        return this;
    }


    public AtworkAlertDialog setDialogCancelable(boolean flag) {
        this.setCancelable(flag);
        return this;
    }

    public void hideInput(Activity activity) {
        ScreenUtils.hideInput(activity, mEtInputContent);
    }

    public void showInput(Activity activity) {
        if (activity == null) {
            return;
        }
        activity.getWindow().getDecorView().postDelayed(() -> {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                mEtInputContent.requestFocus();
                imm.showSoftInput(mEtInputContent, InputMethodManager.SHOW_FORCED);
            }
        }, 100);
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
        // 最典型的有标题, 有内容中间体部分, 以及"确认" "取消"按钮
        CLASSIC,
        // 带有进度条
        PROGRESS,
        // CLASSIC 的基础上少了标题
        SIMPLE,
        //有输入框部分
        INPUT
    }

    public interface OnEditInputListener {
        void onTextValue(String inputValue);
    }

    @Override
    public void dismiss() {
        View view = getCurrentFocus();
        if (view  != null) {
            InputMethodManager mInputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            mInputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
        }

        super.dismiss();
    }
}

