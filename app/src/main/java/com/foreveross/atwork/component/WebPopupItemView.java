package com.foreveross.atwork.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.foreveross.atwork.R;


public class WebPopupItemView extends LinearLayout {

    private ImageView mIconView;

    private TextView mNameView;

    public WebPopupItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WebPopupItemView(Context context) {
        super(context);
        findView(context);
    }

    private void findView(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.web_item_image_view, this);
        mIconView = view.findViewById(R.id.web_item_avatar);
        mNameView = view.findViewById(R.id.web_item_title);
    }

    public void refreshItemView(String text, int resId) {
        mIconView.setImageResource(resId);
        mNameView.setText(text);
    }

}
