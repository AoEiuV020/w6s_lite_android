package com.foreveross.atwork.modules.chat.component.chat;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;

/**
 * Created by dasunsy on 2017/8/25.
 */

public class MessageSourceView extends RelativeLayout {

    private View mLlRoot;
    private TextView mSourceFlag;
    private ImageView mIvIconSource;
    private TextView mSourceFlagPrefix;

    public MessageSourceView(Context context) {
        super(context);
        findView();
    }

    public MessageSourceView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        findView();
    }

    private void findView() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.component_message_source, this);
        mLlRoot = view.findViewById(R.id.ll_root);
        mSourceFlag = view.findViewById(R.id.source_flag);
        mIvIconSource = view.findViewById(R.id.iv_icon_source);
        mSourceFlagPrefix = view.findViewById(R.id.tv_source_flag_prefix);
    }

    public void refreshMsgSyncView(ChatPostMessage message) {
        this.setVisibility(TextUtils.isEmpty(message.source) ? GONE : VISIBLE);
        mSourceFlag.setText(message.source);
    }

    public void burnSkin() {
        mLlRoot.setBackgroundResource(R.drawable.message_source_burn_bg);
        mIvIconSource.setBackgroundResource(R.mipmap.icon_burn_message_source);
        mSourceFlagPrefix.setTextColor(Color.WHITE);
    }

    public void themeSkin() {
        mLlRoot.setBackgroundResource(R.drawable.message_source_bg);
        mIvIconSource.setBackgroundResource(R.mipmap.icon_message_source);
        mSourceFlagPrefix.setTextColor(ContextCompat.getColor(getContext(), R.color.grid_line));
    }
}
