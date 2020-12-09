package com.foreverht.workplus.ui.component.item;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;

import com.foreverht.workplus.ui.component.R;


/**
 * Created by shadow on 2016/5/18.
 */
public class PopupDialogItemView extends LinearLayout {

    private Context mContext;

    private TextView mContentView;

    private View mLineView;

    private String mContent;

    public PopupDialogItemView(Context context) {
        super(context);
        mContext = context;
        initView();
    }

    public PopupDialogItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
    }

    private void initView() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_popup_dialog, this);
        mContentView = view.findViewById(R.id.popup_dialog_item);
        mLineView = view.findViewById(R.id.popup_dialog_line);
    }

    public void refreshData(String text) {
        mContentView.setText(text);
        mContent = text;
    }

    public void hideLine() {
        mLineView.setVisibility(GONE);
    }

    public String getItemContent() {
        return mContent;
    }

    public void setTextColor(int textColor) {
        mContentView.setTextColor(textColor);
    }

    public void setDrawableLeft(@DrawableRes int left,
                                @DrawableRes int top, @DrawableRes int right, @DrawableRes int bottom) {
        mContentView.setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom);
    }

}
