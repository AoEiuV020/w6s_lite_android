package com.foreveross.atwork.modules.chat.component;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.foreveross.atwork.R;

/**
 * Created by reyzhang22 on 17/8/18.
 */

public class TemplateActionDetailView extends BaseTemplateActionView {

    public TextView mTvAction;
    private Context mContext;

    public TemplateActionDetailView(Context context) {
        super(context);
        mContext = context;
        initViews();
    }

    public void initViews() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.template_action_detail_view, this);
        mTvAction = view.findViewById(R.id.action_text);
    }
}
