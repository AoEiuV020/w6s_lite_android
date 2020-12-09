package com.foreveross.atwork.component;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.CheckBox;
import android.widget.LinearLayout;

public class PasswordInputView extends LinearLayout {
    private static final int PASSOWRD_COUNT = 4;
    private static final int PASSWORD_NONE = -1;
    private OnPasswordInputListener listener;
    private int[] passwords = new int[4];

    public PasswordInputView(Context context) {
        this(context, null);
    }

    public PasswordInputView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PasswordInputView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setOrientation(HORIZONTAL);
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        ShapeDrawable sd = new ShapeDrawable();
        sd.setAlpha(0);
        sd.setIntrinsicWidth((int) (0.05 * displayMetrics.widthPixels));
        sd.setIntrinsicHeight(1);
        setDividerDrawable(sd);
        setShowDividers(SHOW_DIVIDER_MIDDLE);
        int inputDotSize = dipToPixels(displayMetrics, 14.0f);

        GradientDrawable normalDrawable = new GradientDrawable();
        normalDrawable.setSize(inputDotSize, inputDotSize);
        normalDrawable.setCornerRadius(inputDotSize);
        normalDrawable.setColor(Color.TRANSPARENT);
        normalDrawable.setStroke(2, Color.DKGRAY);

        GradientDrawable pressedDrawable = new GradientDrawable();
        pressedDrawable.setSize(inputDotSize, inputDotSize);
        pressedDrawable.setCornerRadius(inputDotSize);
        pressedDrawable.setColor(Color.DKGRAY);

        LayoutParams params = new LayoutParams(inputDotSize, inputDotSize);
        for (int j = 0; j < PASSOWRD_COUNT; j++) {
            CheckBox inputView = new CheckBox(context);
            inputView.setLayoutParams(params);
            inputView.setClickable(false);
            StateListDrawable states = new StateListDrawable();
            states.addState(new int[]{16842912}, pressedDrawable);
            states.addState(new int[0], normalDrawable);
            inputView.setButtonDrawable(states);
            addView(inputView);
        }
        resetPasswords();
    }

    private  int dipToPixels(DisplayMetrics displayMetrics, float dip) {
        return (int) TypedValue.applyDimension(1, dip, displayMetrics);
    }

    private void resetPasswords() {
        for (int i = 0; i < this.passwords.length; i++) {
            passwords[i] = PASSWORD_NONE;
        }
    }

    public void addPassword(int password) {
        for(int i = 0; i < passwords.length; i++) {
            CheckBox inputView = (CheckBox)getChildAt(i);
            if(passwords[i] == PASSWORD_NONE) {
                passwords[i] = password;
                inputView.setChecked(true);
                break;
            }
        }
        StringBuilder builder = new StringBuilder();
        if(listener != null && passwords[3] != PASSWORD_NONE) {
            for(int i = 0; i < passwords.length; i++) {
                if(passwords[i] != PASSWORD_NONE) {
                    builder.append(passwords[i]);
                }
            }
            listener.onPasswordInputDone(builder.toString());
        }
    }

    public void deletePassword() {
        for(int i = passwords.length - 1; i >= 0; i--) {
            CheckBox inputView = (CheckBox)getChildAt(i);
            if(passwords[i] != PASSWORD_NONE) {
                passwords[i] = PASSWORD_NONE;
                inputView.setChecked(false);
                if(i == 0 && listener != null) {
                    listener.onPasswordDeleteDone();
                    break;
                }
            }
        }
    }

    public void clearPassword() {
        for(int i = passwords.length - 1; i >= 0; i--) {
            passwords[i] = PASSWORD_NONE;
            CheckBox inputView = (CheckBox)getChildAt(i);
            inputView.setChecked(false);
        }
    }

    public void shake() {
        TranslateAnimation translateAnimation = new TranslateAnimation(0.0f, 20.0f, 0.0f, 0.0f);
        translateAnimation.setDuration(400);
        translateAnimation.setInterpolator(new CycleInterpolator(4.0f));
        startAnimation(translateAnimation);
    }

    public void setOnPasswordInputListener(OnPasswordInputListener listener) {
        this.listener = listener;
    }

    public interface OnPasswordInputListener {

        void onPasswordDeleteDone();

        void onPasswordInputDone(String inputedPassword);
    }
}
