package com.foreveross.atwork.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.foreveross.atwork.R;

/**
 * Created by reyzhang22 on 15/6/30.
 */
public class SelectDialogListItem extends LinearLayout {

    private TextView mText;

    public SelectDialogListItem(Context context) {
        super(context);
        initViews(context);
    }

    public SelectDialogListItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews(context);
    }

    public SelectDialogListItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews(context);
    }

    private void initViews(Context context) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_dialog_listview, this);
        mText = (TextView)view.findViewById(R.id.dialog_item);

    }

    public void setItem(String text) {
        mText.setText(text);
    }

}
