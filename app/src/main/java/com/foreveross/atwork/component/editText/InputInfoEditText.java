package com.foreveross.atwork.component.editText;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.utils.ViewHelper;

/**
 * Created by dasunsy on 2018/1/28.
 */

public class InputInfoEditText extends RelativeLayout implements View.OnFocusChangeListener, TextWatcher {



    private EditText mEtInput;
    private TextView mTvHint;



    private ImageView mIvCancel;
    private ImageView mIvPwdInputShowOrHide;
    private View mVLineBottom;

    private int mUserInputType;
    private int mUserNormalInputType;
    private boolean mPasswordMode;
    private String mHint;



    private OnFocusChangeListener mOnFocusChangeListener;
    private TextWatcher mTextWatcher;

    public InputInfoEditText(Context context) {
        super(context);
        findViews();
        registerListener();

    }

    public InputInfoEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        findViews();
        getAtt(attrs);
        initAttRefresh();

        registerListener();

    }

    private void initAttRefresh() {
        if(!StringUtils.isEmpty(mHint)) {
            setHint(mHint);
        }

        if(-1 != mUserInputType) {
            setInputType(mUserInputType);
        }
    }

    private void getAtt(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.InputInfoEditText);
        mUserInputType = typedArray.getInt(R.styleable.InputInfoEditText_inputType, -1);
        mUserNormalInputType = typedArray.getInt(R.styleable.InputInfoEditText_normalInputType, -1);
        CharSequence text = typedArray.getText(R.styleable.InputInfoEditText_hint);
        if(null != text) {
            mHint = text.toString();

        }

        typedArray.recycle();
    }

    private void findViews() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.layout_input_info_edittext, this);
        mEtInput = view.findViewById(R.id.et_input);
        mTvHint = view.findViewById(R.id.tv_hint);
        mIvCancel = view.findViewById(R.id.iv_cancel_btn);
        mIvPwdInputShowOrHide = view.findViewById(R.id.iv_pwd_input_show_or_hide);
        mVLineBottom = view.findViewById(R.id.v_input_line);
    }


    private void registerListener() {
        mEtInput.setOnFocusChangeListener(this);


        mEtInput.addTextChangedListener(this);


        mIvCancel.setOnClickListener(v -> {
            mEtInput.setText(StringUtils.EMPTY);
        });

        mIvPwdInputShowOrHide.setOnClickListener(v -> {
            if (isPasswordInputType()) {
                mIvPwdInputShowOrHide.setImageResource(R.mipmap.icon_open_eye);
                mEtInput.setInputType(mUserNormalInputType);
                mEtInput.setSelection(mEtInput.getText().length());

            } else {
                mIvPwdInputShowOrHide.setImageResource(R.mipmap.icon_close_eye);
                mEtInput.setInputType(mUserInputType);
                mEtInput.setSelection(mEtInput.getText().length());

            }

        });



    }

    private void onHandleFocusInputBtn() {
        String text = mEtInput.getText().toString();

        if(StringUtils.isEmpty(text)) {
            mTvHint.setVisibility(VISIBLE);
            mIvCancel.setVisibility(GONE);

            if(mPasswordMode) {
                mIvPwdInputShowOrHide.setVisibility(GONE);
            }

        } else {
            mTvHint.setVisibility(GONE);
            mIvCancel.setVisibility(VISIBLE);

            if(mPasswordMode) {
                mIvPwdInputShowOrHide.setVisibility(VISIBLE);
            }
        }
    }

    public void setHint(int hintResId) {
        mTvHint.setText(hintResId);

    }

    public void setHint(String hint) {
        mTvHint.setText(hint);
    }

    public void setPasswordInputType(int passwordInputType, int userNormalInputType) {
        mEtInput.setInputType(passwordInputType);
        mUserInputType = passwordInputType;
        mUserNormalInputType = userNormalInputType;
        mPasswordMode = true;
    }


    public void setInputType(int type) {
        mEtInput.setInputType(type);
        mUserInputType = type;

    }



    public String getText() {
        return mEtInput.getText().toString();
    }

    public void setInputOnFocusChangeListener(OnFocusChangeListener onFocusChangeListener) {
        mOnFocusChangeListener = onFocusChangeListener;
    }

    public void setInputTextWatcher(TextWatcher textWatcher) {
        mTextWatcher = textWatcher;
    }


    public boolean isPasswordInputType() {
        int inputType = mEtInput.getInputType();

        if(!mPasswordMode) {
            return false;
        }

        if(mUserInputType == inputType) {
            return true;
        }

//        if((InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT) == inputType) {
//            return true;
//        }
//
//        if((InputType.TYPE_NUMBER_VARIATION_PASSWORD | InputType.TYPE_CLASS_NUMBER) == inputType) {
//            return true;
//        }

        return false;
    }

    public ImageView getIvCancel() {
        return mIvCancel;
    }

    public EditText getEtInput() {
        return mEtInput;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        ViewHelper.focusOnLine(mVLineBottom, hasFocus);


        if(hasFocus) {
            onHandleFocusInputBtn();
        } else {
            mIvCancel.setVisibility(GONE);
            if(mPasswordMode) {
                mIvPwdInputShowOrHide.setVisibility(GONE);
            }
        }

        if(null != mOnFocusChangeListener) {
            mOnFocusChangeListener.onFocusChange(v, hasFocus);
        }

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        if(null != mTextWatcher) {
            mTextWatcher.beforeTextChanged(s, start, count, after);
        }
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if(null != mTextWatcher) {
            mTextWatcher.onTextChanged(s, start, before, count);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        onHandleFocusInputBtn();
        if(null != mTextWatcher) {
            mTextWatcher.afterTextChanged(s);
        }
    }
}
