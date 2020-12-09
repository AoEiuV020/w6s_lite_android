package com.foreveross.atwork.modules.chat.component;

import android.content.Context;
import android.graphics.Color;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.model.Session;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.TemplateMessage;
import com.foreveross.atwork.modules.chat.util.TemplateHelper;

import java.util.List;

/**
 * Created by reyzhang22 on 17/8/18.
 */

public abstract class BaseTemplateActionView extends LinearLayout {

    public Context mContext;

    public BaseTemplateActionView(Context context) {
        super(context);
        mContext = context;
    }

    public abstract void initViews();

    public void setAction(TextView textView, TemplateMessage.TemplateActions templateActions, List<TemplateMessage.TemplateData> templateDatas, Session session) {
        int color = -1;
        try {
            color = Color.parseColor(templateActions.mColor);
        } catch (Exception e) {
            color = mContext.getResources().getColor(R.color.light_black);
        }
        textView.setTextColor(color);
        textView.setText(templateActions.mName);
        textView.setOnClickListener(view -> TemplateHelper.routeWebview(mContext, templateActions, templateDatas, session));
    }


}
