package com.foreveross.atwork.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.foreveross.atwork.R;

/**
 * Created by dasunsy on 2017/9/6.
 */

public class UnreadImageView extends RelativeLayout  {

    private ImageView mIvIcon;
    private NewMessageView mNewMessageView;
    private ImageView mIvUnreadDot;

    public UnreadImageView(Context context) {
        super(context);
        initView();
    }

    public UnreadImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.componet_imageview_with_unread_tip, this);
        mIvIcon = view.findViewById(R.id.iv_icon);
        mNewMessageView = view.findViewById(R.id.v_unread_tip);
        mIvUnreadDot = view.findViewById(R.id.iv_unread_dot);
    }

    public void setIcon(int icon) {
        mIvIcon.setImageResource(icon);
    }


    public ImageView getIconView() {
        return mIvIcon;
    }

    public void unreadNum(int num) {
        mNewMessageView.setNum(num);
        mIvUnreadDot.setVisibility(GONE);
    }

    public void showDot() {
        mNewMessageView.setVisibility(GONE);
        mIvUnreadDot.setVisibility(VISIBLE);
    }

    public void hideUnread() {
        mNewMessageView.setVisibility(GONE);
        mIvUnreadDot.setVisibility(GONE);
    }

}
