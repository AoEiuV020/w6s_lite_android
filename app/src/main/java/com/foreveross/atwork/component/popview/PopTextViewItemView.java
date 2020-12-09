package com.foreveross.atwork.component.popview;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.app.ServiceApp;

/**
 * Created by lingen on 15/5/14.
 * Description:
 */
public class PopTextViewItemView extends LinearLayout {

    private TextView text;

    public PopTextViewItemView(Context context) {
        super(context);
        initView();
    }

    private void initView() {
        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.component_text_item, this);
        text = view.findViewById(R.id.list_view_item_text);
    }

    public void refreshView(ServiceApp.ServiceMenu value, int width, int xoff) {
        text.setText(value.getNameI18n(BaseApplicationLike.baseContext));
        text.getLayoutParams().width = width;
        if(-1 == xoff){
            text.setGravity(Gravity.CENTER);
        }else{
            text.setGravity(Gravity.CENTER);
            text.setPadding(8, text.getPaddingTop(), 8, text.getPaddingBottom());
        }
    }
}
