package com.foreveross.atwork.modules.chat.component;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.model.Session;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.TemplateMessage;

import java.util.List;

/**
 * Created by reyzhang22 on 17/8/18.
 */

public class TemplateActionButtonsView extends BaseTemplateActionView {


    private View mAction1;
    private View mAction2;
    private View mAction3;

    private TextView mTvAction1;
    private TextView mTvAction2;
    private TextView mTvAction3;
    private Context mContext;

    public TemplateActionButtonsView(Context context) {
        super(context);
        mContext = context;
        initViews();
    }

    public void initViews() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.template_action_buttons_view, this);
        mAction1 = view.findViewById(R.id.action1);
        mTvAction1 = mAction1.findViewById(R.id.action1_text);
        mAction2 = view.findViewById(R.id.action2);
        mTvAction2 = mAction2.findViewById(R.id.action2_text);
        mAction3 = view.findViewById(R.id.action3);
        mTvAction3 = mAction3.findViewById(R.id.action3_text);
    }

    public void setButtons(List<TemplateMessage.TemplateActions> actions, List<TemplateMessage.TemplateData> datas, Session session) {
        for (int i = 0; i < actions.size(); i++) {
            if (i == 0) {
                setAction(mTvAction1, actions.get(i), datas, session);
            }
            if (i == 1) {
                mAction2.setVisibility(VISIBLE);
                setAction(mTvAction2, actions.get(i), datas, session);
            }
            if (i == 2) {
                mAction3.setVisibility(VISIBLE);
                setAction(mTvAction3, actions.get(i), datas, session);
            }
        }
    }

}
