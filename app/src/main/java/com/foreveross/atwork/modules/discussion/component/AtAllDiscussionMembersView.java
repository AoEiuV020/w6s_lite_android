package com.foreveross.atwork.modules.discussion.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.foreveross.atwork.R;

/**
 * Created by reyzhang22 on 15/10/26.
 */
public class AtAllDiscussionMembersView extends LinearLayout {

    private static final String TAG = AtAllDiscussionMembersView.class.getSimpleName();

    public AtAllDiscussionMembersView(Context context) {
        super(context);
        initViews(context);
    }

    public AtAllDiscussionMembersView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews(context);
    }

    public AtAllDiscussionMembersView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews(context);
    }

    private void initViews(Context context) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.component_at_all_members, this);
        TextView text = (TextView)view.findViewById(R.id.at_all_discussion_members_text);
        text.setText(new StringBuffer().append("@").append(context.getString(R.string.at_all_group)));
    }

}
