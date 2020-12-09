package com.foreveross.atwork.modules.chat.component.chat.reference;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ImageChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.anno.AnnoImageChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.reference.ReferenceMessage;
import com.foreveross.atwork.modules.chat.component.ChatSendStatusView;
import com.foreveross.atwork.modules.chat.component.chat.MessageSourceView;
import com.foreveross.atwork.modules.chat.component.chat.SomeStatusView;
import com.foreveross.atwork.modules.chat.component.chat.definition.IAnnoImageChatView;
import com.foreveross.atwork.modules.chat.data.AnchorInfo;
import com.foreveross.atwork.modules.chat.presenter.AnnoImageChatViewRefreshUIPresenter;
import com.foreveross.atwork.modules.chat.util.AutoLinkHelper;
import com.foreveross.atwork.modules.image.activity.ImageSwitchInChatActivity;
import com.foreveross.atwork.utils.ThemeResourceHelper;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class RightReferencedAnnoImageMessageChatItemView extends RightBasicReferenceUserChatItemView implements IAnnoImageChatView {

    private View mVRoot;

    private ImageView mIvAvatar;

    private LinearLayout mLlContent;

    private ImageView mIvSelect;

    private ChatSendStatusView mChatSendStatusView;

    private ReferenceMessage mReferencedChatMessage;

    private TextView mTvAuthorName;

    private TextView mTvReply;

    private RecyclerView mRvImageContent;

    private TextView mTvComment;

    private LinearLayout mLlSomeStatusInfoWrapperParent;
    private LinearLayout mLlSomeStatusInfo;
    private TextView mTvTime;
    private ImageView mIvSomeStatus;
    private FrameLayout mFlReply;

    private AnnoImageChatViewRefreshUIPresenter mAnnoImageChatViewRefreshUIPresenter;


    public RightReferencedAnnoImageMessageChatItemView(Context context) {
        super(context);
        findView();

        mAnnoImageChatViewRefreshUIPresenter = new AnnoImageChatViewRefreshUIPresenter(context, this);
        mChatViewRefreshUIPresenter = mAnnoImageChatViewRefreshUIPresenter;

        registerListener();


    }



    @Override
    protected View getMessageRootView() {
        return mVRoot;
    }

    @Override
    protected ChatSendStatusView getChatSendStatusView() {
        return mChatSendStatusView;
    }

    @Override
    protected ImageView getAvatarView() {
        return mIvAvatar;
    }

    @Override
    protected ImageView getSelectView() {
        return mIvSelect;
    }

    @Override
    protected MessageSourceView getMessageSourceView() {
        return null;
    }

    @Override
    protected ChatPostMessage getMessage() {
        return mReferencedChatMessage;
    }

    @Override
    protected void registerListener() {
        super.registerListener();

        mAnnoImageChatViewRefreshUIPresenter.getAdapter().setOnItemClickListener((adapter, view, position) -> {

            AutoLinkHelper.getInstance().setLongClick(false);
            if (mSelectMode) {
                mReferencedChatMessage.select = !mReferencedChatMessage.select;
                select(mReferencedChatMessage.select);

            } else {
                if(mReferencedChatMessage.mReferencingMessage instanceof AnnoImageChatMessage) {
                    AnnoImageChatMessage annoImageChatMessage = (AnnoImageChatMessage) mReferencedChatMessage.mReferencingMessage;
                    List<ImageChatMessage> imageChatMessageList = annoImageChatMessage.getImageContentInfoMessages();
                    ImageSwitchInChatActivity.showImageSwitchView(getContext(), imageChatMessageList.get(position), new ArrayList<>(imageChatMessageList), null);
                }

            }



        });

        mAnnoImageChatViewRefreshUIPresenter.getAdapter().setOnItemLongClickListener((adapter, view, position) -> handleLongClick());

        mLlContent.setOnClickListener(v -> {
            AutoLinkHelper.getInstance().setLongClick(false);
            if (mSelectMode) {
                mReferencedChatMessage.select = !mReferencedChatMessage.select;
                select(mReferencedChatMessage.select);

            } else {
                mChatItemClickListener.referenceClick(mReferencedChatMessage);

            }
        });

        mLlContent.setOnLongClickListener(v -> handleLongClick());



    }

    private boolean handleLongClick() {
        AutoLinkHelper.getInstance().setLongClick(true);
        AnchorInfo anchorInfo = getAnchorInfo();
        if (!mSelectMode) {
            mChatItemLongClickListener.referenceLongClick(mReferencedChatMessage, anchorInfo);
            return true;
        }
        return false;
    }

    private void findView() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.chat_right_referenced_anno_image_message, this);
        mVRoot = view.findViewById(R.id.rl_root);
        mIvAvatar = view.findViewById(R.id.chat_right_text_avatar);
        mLlContent = view.findViewById(R.id.ll_chat_right_content);
        mTvAuthorName = view.findViewById(R.id.tv_title);
        mRvImageContent = view.findViewById(R.id.rv_image_content);
        mTvComment = view.findViewById(R.id.tv_comment);
        mTvReply = view.findViewById(R.id.tv_reply);
        mIvSelect = view.findViewById(R.id.right_text_select);
        mChatSendStatusView = view.findViewById(R.id.chat_right_text_send_status);

        mFlReply = view.findViewById(R.id.fl_reply);
        mLlSomeStatusInfoWrapperParent = view.findViewById(R.id.ll_some_status_info_wrapper_parent);
        mLlSomeStatusInfo = view.findViewById(R.id.ll_some_status_info);
        mTvTime = view.findViewById(R.id.tv_time);
        mIvSomeStatus = view.findViewById(R.id.iv_some_status);
    }

    @Override
    protected SomeStatusView getSomeStatusView() {
        return SomeStatusView
                .newSomeStatusView()
                .setVgSomeStatusWrapperParent(mLlSomeStatusInfoWrapperParent)
                .setTvContentShow(mTvReply)
                .setIvStatus(mIvSomeStatus)
                .setIconDoubleTick(R.mipmap.icon_double_tick_white)
                .setIconOneTick(R.mipmap.icon_one_tick_white)
                .setTvTime(mTvTime)
                .setMaxTvContentWidthBaseOn(mFlReply, mLlSomeStatusInfo)
                .setLlSomeStatusInfo(mLlSomeStatusInfo);

    }

    @Override
    public void refreshItemView(ChatPostMessage message) {
        super.refreshItemView(message);

        mReferencedChatMessage = (ReferenceMessage) message;



    }



    @Override
    protected void burnSkin() {
        ThemeResourceHelper.setChatRightViewColorBg9Drawable(mLlContent);

    }

    @Override
    protected void themeSkin() {
        ThemeResourceHelper.setChatRightViewColorBg9Drawable(mLlContent);

    }

    @NonNull
    @Override
    protected View getContentRootView() {
        return mLlContent;
    }


    @Override
    TextView getAuthorNameView() {
        return mTvAuthorName;
    }

    @Override
    TextView getReplyView() {
        return mTvReply;
    }



    @NotNull
    @Override
    public TextView commentView() {
        return mTvComment;
    }

    @NotNull
    @Override
    public RecyclerView mediaContentView() {
        return mRvImageContent;
    }
}
