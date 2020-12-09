package com.foreveross.atwork.modules.chat.component.chat;

import android.content.Context;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.ParticipantType;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ESpaceChatMessage;
import com.foreveross.atwork.modules.chat.component.ChatDetailItemDataRefresh;
import com.foreveross.atwork.modules.chat.data.AnchorInfo;
import com.foreveross.atwork.modules.chat.inter.ChatItemClickListener;
import com.foreveross.atwork.modules.chat.inter.ChatItemLongClickListener;
import com.foreveross.atwork.modules.chat.inter.HasChatItemClickListener;
import com.foreveross.atwork.modules.chat.inter.HasChatItemLongClickListener;
import com.foreveross.atwork.modules.chat.inter.SelectModelListener;
import com.foreveross.atwork.modules.chat.util.AutoLinkHelper;
import com.foreveross.atwork.utils.AvatarHelper;

import java.util.List;

/**
 * Created by lingen on 15/3/25.
 * Description:聊天界面，左边
 */
public class LeftESpaceChatItemView extends BasicESpaceChatItemView implements HasChatItemLongClickListener, HasChatItemClickListener, ChatDetailItemDataRefresh, SelectModelListener {

    private ImageView ivAvatar;

    private TextView username;

    private TextView content;

    private ImageView selectView;

    private ChatItemClickListener chatItemClickListener;

    private ChatItemLongClickListener chatItemLongClickListener;

    private ESpaceChatMessage mESpaceChatMessage;

    private boolean selectMode;

    private String asyncId;

    private Context mContext;

    public LeftESpaceChatItemView(Context context) {
        super(context);
        mContext = context;
        findView();
        registerListener();
    }

    @NonNull
    @Override
    protected View getChatRootView() {
        return content;
    }

    private void registerListener() {

        ivAvatar.setOnClickListener(v -> {
            if(ParticipantType.App.equals(mESpaceChatMessage.mFromType)){
                return;
            }
            if (!selectMode && chatItemClickListener != null) {
                chatItemClickListener.avatarClick(mESpaceChatMessage.from, mESpaceChatMessage.mFromDomain);
            }
        });

        ivAvatar.setOnLongClickListener(v -> {
            //只有群聊的时候才触发
            if (ParticipantType.Discussion.equals(mESpaceChatMessage.mToType)) {

                if (!selectMode && chatItemClickListener != null) {
                    chatItemClickListener.avatarLongClick(mESpaceChatMessage.from, mESpaceChatMessage.mFromDomain);
                }
            }
            return true;
        });

        content.setOnClickListener(v -> {
            chatItemClickListener.voipClick(null);
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

        setOnClickListener(v -> {

            chatItemClickListener.voipClick(null);
            if (selectMode) {
                mESpaceChatMessage.select = !mESpaceChatMessage.select;
                select(mESpaceChatMessage.select);
            }
        });

    }

    private void findView() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.chat_left_text_message, this);
        ivAvatar = (ImageView) view.findViewById(R.id.chat_left_text_avatar);
        username = (TextView) view.findViewById(R.id.chat_left_text_username);
        content = (TextView) view.findViewById(R.id.chat_left_text_content);
        selectView = (ImageView) view.findViewById(R.id.left_text_select);
        selectView.setVisibility(GONE);
        Linkify.addLinks(content, Linkify.WEB_URLS | Linkify.EMAIL_ADDRESSES | Linkify.PHONE_NUMBERS);
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
    public void refreshItemView(ChatPostMessage message) {
        if (message instanceof ESpaceChatMessage) {
            mESpaceChatMessage = (ESpaceChatMessage) message;
            if (!mESpaceChatMessage.mIsActivity) {
                return;
            }
            //只有群聊需要显示名称
            if(ParticipantType.Discussion.equals(message.mToType)){
                username.setVisibility(VISIBLE);
//                UserManager.getInstance().setReadableNames(username, message.from, message.mFromDomain);
            }

            content.setText(String.format(getContext().getString(R.string.someone_create_audio_meeting), mESpaceChatMessage.from));
            select(mESpaceChatMessage.select);
            //显示头像
            refreshAvatar();
        }
    }

    @Override
    public void refreshMessagesContext(List<ChatPostMessage> messages) {

    }

    private void refreshAvatar() {
        if (ParticipantType.App.equals(mESpaceChatMessage.mFromType)) {
            AvatarHelper.setAppAvatarById(ivAvatar, mESpaceChatMessage.from, mESpaceChatMessage.mOrgId, false, true);

        } else {
            AvatarHelper.setUserAvatarById(ivAvatar, mESpaceChatMessage.from, mESpaceChatMessage.mFromDomain, false, true);

        }
    }

    @Override
    public String getMsgId() {
        if (mESpaceChatMessage != null) {
            return mESpaceChatMessage.deliveryId;
        }
        return null;
    }

    @Override
    public void setChatItemLongClickListener(ChatItemLongClickListener chatItemLongClickListener) {
        this.chatItemLongClickListener = chatItemLongClickListener;
    }

    @Override
    public void setChatItemClickListener(ChatItemClickListener chatItemClickListener) {
        this.chatItemClickListener = chatItemClickListener;
    }


}

