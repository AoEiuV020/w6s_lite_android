package com.foreveross.atwork.modules.image.component;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.foreveross.atwork.R;
import com.foreveross.atwork.component.MaxInputEditText;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.modules.image.fragment.ImageEditFragment;
import com.foreveross.atwork.utils.AtworkToast;
import com.foreveross.atwork.utils.AtworkUtil;

import cn.jarlen.photoedit.operate.TextObject;

/**
 * Created by dasunsy on 2016/11/21.
 */

public class ImageEditTextInputDialogFragment extends DialogFragment {

    public static final String DATA_TEXT_OBJ = "data_text_obj";

    private View mRootView;
    private MaxInputEditText mEtInput;
    private android.widget.TextView mTvCancel;
    private android.widget.TextView mTvClear;
    private android.widget.TextView mTvFinish;
    private TextObject mTextObject;


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //该方法需要放在onViewCreated比较合适, 若在 onStart 在部分机型(如:小米3)会出现闪烁的情况
        Window window = getDialog().getWindow();
        window.setBackgroundDrawableResource(R.color.transparent_70);
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Holo_Light_NoActionBar);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view_imgedit_text_edit, null);
        initViews(view);
        initData();
        registerListener();

        refreshUI();

        AtworkUtil.showInput(getActivity(), mEtInput);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        toastInput();
    }

    private void initViews(View view) {
        mRootView = view;
        this.mTvFinish = view.findViewById(R.id.tv_finish);
        this.mTvClear = view.findViewById(R.id.tv_clear);
        this.mTvCancel = view.findViewById(R.id.tv_cancel);
        this.mEtInput = view.findViewById(R.id.et_input);
    }

    private void initData() {
        Bundle bundle = getArguments();
        if(null != bundle) {
            mTextObject = bundle.getParcelable(DATA_TEXT_OBJ);

        }
    }

    private void refreshUI() {
        if (null != mTextObject) {
            mEtInput.setTextColor(mTextObject.getColor());
            mEtInput.setText(mTextObject.getEditText());

            mEtInput.setSelection(mEtInput.getText().length());
        }
    }

    private void registerListener() {

        mRootView.setOnClickListener((v)->{
            updateTextAndDismiss();
        });

        mTvCancel.setOnClickListener((v)->{

            AtworkUtil.hideInput(getActivity(), mEtInput);

            dismissAllowingStateLoss();

        });

        mTvClear.setOnClickListener((v -> {
            mEtInput.setText(StringUtils.EMPTY);
        }));

        mTvFinish.setOnClickListener((v)-> {

            if (30 < StringUtils.getWordCount(mEtInput.getText().toString())) {

                AtworkToast.showResToast(R.string.edit_text_letter_max);

            } else {
                updateTextAndDismiss();
            }
        });

        mEtInput.setMaxInput(30, true);

    }

    private void updateTextAndDismiss() {
        mTextObject.setText(mEtInput.getText().toString());
        mTextObject.setInputContent(!StringUtils.isEmpty(mEtInput.getText().toString()));
        mTextObject.commit();

        AtworkUtil.hideInput(getActivity(), mEtInput);

        dismissAllowingStateLoss();
    }

    private void toastInput() {

        mEtInput.setFocusable(true);
        //延迟处理 避免View没绘制好 软键盘无法弹出
        getActivity().getWindow().getDecorView().postDelayed(() -> {
            if (isAdded()) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    mEtInput.requestFocus();
                    imm.showSoftInput(mEtInput, InputMethodManager.SHOW_FORCED);
                }
            }
        }, 100);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

        ImageEditFragment.refreshInputTexts();
    }

    @Override
    public void dismissAllowingStateLoss() {
        super.dismissAllowingStateLoss();
    }
}
