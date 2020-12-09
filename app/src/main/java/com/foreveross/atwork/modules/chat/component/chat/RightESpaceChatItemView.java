package com.foreveross.atwork.modules.chat.component.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.newmessage.ChatStatus;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ESpaceChatMessage;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.inter.ReSendListener;
import com.foreveross.atwork.modules.chat.component.ChatDetailItemDataRefresh;
import com.foreveross.atwork.modules.chat.component.ChatSendStatusView;
import com.foreveross.atwork.modules.chat.data.AnchorInfo;
import com.foreveross.atwork.modules.chat.inter.ChatItemClickListener;
import com.foreveross.atwork.modules.chat.inter.ChatItemLongClickListener;
import com.foreveross.atwork.modules.chat.inter.HasChatItemClickListener;
import com.foreveross.atwork.modules.chat.inter.HasChatItemLongClickListener;
import com.foreveross.atwork.modules.chat.inter.HasResendListener;
import com.foreveross.atwork.modules.chat.inter.SelectModelListener;
import com.foreveross.atwork.modules.chat.util.AutoLinkHelper;
import com.foreveross.atwork.utils.AvatarHelper;

import java.util.List;

/**
 * Created by lingen on 15/3/25.
 * Description:
 */
public class RightESpaceChatItemView extends BasicESpaceChatItemView implements HasResendListener, HasChatItemLongClickListener, HasChatItemClickListener, ChatDetailItemDataRefresh, SelectModelListener {

    private ImageView avatar;

    private TextView content;

    private ChatSendStatusView chatSendStatusView;

    private ReSendListener reSendListener;

    private ImageView selectView;

    private ChatItemClickListener chatItemClickListener;

    private ChatItemLongClickListener chatItemLongClickListener;

    private ESpaceChatMessage mESpaceChatMessage;

    private ChatStatus lastChatStatus;

    private boolean selectMode;

//    private Contact lastContact;

    public RightESpaceChatItemView(Context context) {
        super(context);
        findView();
        registerListener();
    }

    @NonNull
    @Override
    protected View getChatRootView() {
        return content;
    }

    private void registerListener() {
        avatar.setOnClickListener(v -> {
            if (selectMode == false) {
                chatItemClickListener.avatarClick(LoginUserInfo.getInstance().getLoginUserId(getContext()), LoginUserInfo.getInstance().getLoginUserDomainId(getContext()));
            }
            if (selectMode) {
                mESpaceChatMessage.select = !mESpaceChatMessage.select;
                select(mESpaceChatMessage.select);
            }
        });

        setOnClickListener(v -> {
            chatItemClickListener.hideAll();
            if (selectMode) {
                mESpaceChatMessage.select = !mESpaceChatMessage.select;
                select(mESpaceChatMessage.select);
            }
        });

        content.setOnClickListener(v -> {
            AutoLinkHelper.getInstance().setLongClick(false);
            if (selectMode) {
                mESpaceChatMessage.select = !mESpaceChatMessage.select;
                select(mESpaceChatMessage.select);
            }
        });

        content.setOnLongClickListener(v -> {
            AutoLinkHelper.getInstance().setLongClick(true);
            AnchorInfo anchorInfo = getAnchorInfo();
            if (!selectMode) {
                chatItemLongClickListener.voipLongClick(null, anchorInfo);
                return true;
            }
            return false;
        });
    }

    private void findView() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.chat_right_text_message, this);
        avatar = (ImageView) view.findViewById(R.id.chat_right_text_avatar);
        content = (TextView) view.findViewById(R.id.chat_right_text_content);
        chatSendStatusView = (ChatSendStatusView) view.findViewById(R.id.chat_right_text_send_status);
        selectView = (ImageView) view.findViewById(R.id.right_text_select);
    }

    @Override
    public void refreshItemView(ChatPostMessage message) {
        if (message instanceof ESpaceChatMessage) {

            mESpaceChatMessage = (ESpaceChatMessage) message;
            if (!mESpaceChatMessage.mIsActivity) {
                return;
            }
            lastChatStatus = ((ESpaceChatMessage) message).chatStatus;
            content.setText(String.format(getContext().getString(R.string.someone_create_audio_meeting), mESpaceChatMessage.from));

            chatSendStatusView.setChatPostMessage(message);
            chatSendStatusView.setReSendListener(reSendListener);
            select(mESpaceChatMessage.select);

            //显示头像
            AvatarHelper.setUserAvatarById(avatar, LoginUserInfo.getInstance().getLoginUserId(getContext()), LoginUserInfo.getInstance().getLoginUserDomainId(getContext()), false, true);
        }
    }

    @Override
    public void refreshMessagesContext(List<ChatPostMessage> messages) {

    }


    @Override
    public String getMsgId() {
        if (mESpaceChatMessage != null) {
            return mESpaceChatMessage.deliveryId;
        }
        return null;
    }

    public void hiddenSelect() {
        selectMode = false;
        selectView.setVisibility(GONE);
    }


    public void select(boolean select) {
        if (select) {
            selectView.setImageResource(R.mipmap.icon_selected);
        } else {
            selectView.setImageResource(R.mipmap.icon_seclect_no_circular);
        }
    }

    public void showSelect() {
        selectMode = true;
        selectView.setVisibility(VISIBLE);
    }

    @Override
    public void setChatItemLongClickListener(ChatItemLongClickListener chatItemLongClickListener) {
        this.chatItemLongClickListener = chatItemLongClickListener;
    }

    @Override
    public void setChatItemClickListener(ChatItemClickListener chatItemClickListener) {
        this.chatItemClickListener = chatItemClickListener;
    }

    public void setReSendListener(ReSendListener reSendListener) {
        this.reSendListener = reSendListener;
    }
}
