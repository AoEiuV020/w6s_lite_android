package com.foreveross.atwork.modules.chat.component.chat;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.ParticipantType;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.modules.chat.inter.SelectModelListener;

/**
 * Created by dasunsy on 2017/8/30.
 */

public abstract class BasicUserChatItemView extends BasicChatItemView implements SelectModelListener {

    protected boolean mSelectMode;

    protected abstract ImageView getSelectView();

    public BasicUserChatItemView(Context context) {
        super(context);
    }

    public BasicUserChatItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected void registerListener() {
        getAvatarView().setOnClickListener(v -> {
            if (!mSelectMode && mChatItemClickListener != null) {
                if (ParticipantType.App.equals(getMessage().mFromType)) {
                    return;
                }
                mChatItemClickListener.avatarClick(getMessage().from, getMessage().mFromDomain);
            } else {
                getMessage().select = !getMessage().select;
                select(getMessage().select);
            }
        });


        setOnClickListener(v -> {

            mChatItemClickListener.hideAll();

            ChatPostMessage message = getMessage();
            if(null == message) {
                return;
            }


            if (mSelectMode) {
                message.select = !message.select;
                select(message.select);
            }
        });

    }

    @Override
    public void refreshItemView(ChatPostMessage message) {
        super.refreshItemView(message);
        select(message.select);

    }

    public void hiddenSelect() {
        getSelectView().setVisibility(GONE);
        mSelectMode = false;
    }


    protected void select(boolean select) {
        ImageView selectView = getSelectView();
        if(null == selectView) {
            return;
        }


        if (select) {
            selectView.setImageResource(R.mipmap.icon_select_tick_new);
        } else {
            selectView.setImageResource(R.mipmap.icon_select_no_tick_new);
        }

        if(null != mChatItemClickListener) {
            mChatItemClickListener.selectClick(getMessage());
        }

    }

    public void showSelect() {
        mSelectMode = true;
        getSelectView().setVisibility(VISIBLE);
    }


}
