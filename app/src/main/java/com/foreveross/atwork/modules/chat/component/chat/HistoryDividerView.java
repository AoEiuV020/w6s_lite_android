package com.foreveross.atwork.modules.chat.component.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.foreveross.atwork.R;

/**
 * Created by jamin on 10/16/15.
 */
public class HistoryDividerView extends RelativeLayout {

    public HistoryDividerView(Context context) {
        super(context);
        initView();
    }

  private void initView() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.chat_history_divider, this);
        view.setVisibility(VISIBLE);
    }

}
