package com.foreveross.atwork.modules.chat.component.chat;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;



public class MessageSourceV2View extends RelativeLayout {

    private View mLlRoot;
    private TextView mTvSourceFlag;
    private ImageView mIvIconSource;

    public MessageSourceV2View(Context context) {
        super(context);
        findView();
    }

    public MessageSourceV2View(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        findView();
    }

    private void findView() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.component_message_source_v2, this);
        mLlRoot = view.findViewById(R.id.ll_root);
        mTvSourceFlag = view.findViewById(R.id.tv_source_flag);
        mIvIconSource = view.findViewById(R.id.iv_icon_source);
    }

    public void refreshMsgSyncView(ChatPostMessage message) {
        this.setVisibility(TextUtils.isEmpty(message.source) ? GONE : VISIBLE);
        mTvSourceFlag.setText(message.source);
    }

    public void refreshSourceFlag(@Nullable Integer sourceContentRes, @Nullable Integer sourceIconRes) {
        if (null != sourceContentRes) {
            mTvSourceFlag.setText(sourceContentRes);
        }

        if (null != sourceIconRes) {
            mIvIconSource.setImageResource(sourceIconRes);
        }
    }



    public void burnSkin() {

    }

    public void themeSkin() {

    }
}
