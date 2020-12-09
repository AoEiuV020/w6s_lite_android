package com.foreveross.atwork.modules.contact.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.foreveross.atwork.R;

/**
 * Created by dasunsy on 16/6/3.
 */
public class ContactReadColleagueItemView extends LinearLayout{
    private View mRoot;

    public ContactReadColleagueItemView(Context context) {
        super(context);
        initView();
    }

    public ContactReadColleagueItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRoot = inflater.inflate(R.layout.item_contact_read_colleauge, this);

    }
}
