package com.foreveross.atwork.modules.chat.component.chat.anno;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.anno.AnnoImageChatMessage;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.modules.chat.component.ChatSendStatusView;
import com.foreveross.atwork.modules.chat.component.chat.MessageSourceView;
import com.foreveross.atwork.modules.chat.component.chat.RightBasicUserChatItemView;
import com.foreveross.atwork.modules.chat.component.chat.SomeStatusView;
import com.foreveross.atwork.modules.chat.component.chat.definition.IAnnoImageChatView;
import com.foreveross.atwork.modules.chat.data.AnchorInfo;
import com.foreveross.atwork.modules.chat.presenter.AnnoImageChatViewRefreshUIPresenter;

import org.jetbrains.annotations.NotNull;


public class RightAnnoImageChatItemView extends RightBasicUserChatItemView implements IAnnoImageChatView {

    private View mVRoot;

    private ImageView mIvAvatar;
    private LinearLayout mLlContentRoot;
    private RecyclerView mRvImageContent;
    private TextView mTvComment;

    private ChatSendStatusView mChatSendStatusView;
    private AnnoImageChatMessage mAnnoImageChatMessage;

    private ImageView mIvSelect;
    private Context mContext;


    private MessageSourceView mSourceView;


    private ViewGroup mLayoutChatMessageItemTimeInfoInAnnoImageView;
    private LinearLayout mLlSomeStatusInfoInAnnoImageView;
    private TextView mTvTimeInAnnoImageView;
    private ImageView mIvSomeStatusInAnnoImageView;


    private LinearLayout mLlSomeStatusInfoWrapperParentInAnnoImageCommentView;
    private LinearLayout mLlSomeStatusInfoInAnnoImageCommentView;
    private TextView mTvTimeInAnnoImageCommentView;
    private ImageView mIvSomeStatusInAnnoImageCommentView;
    private FrameLayout mFlReplyInAnnoImageCommentView;

    private AnnoImageChatViewRefreshUIPresenter mAnnoImageChatViewRefreshUIPresenter;

    public RightAnnoImageChatItemView(Context context) {
        super(context);
        mContext = context;
        findView();

        mAnnoImageChatViewRefreshUIPresenter = new AnnoImageChatViewRefreshUIPresenter(getContext(), this);
        mChatViewRefreshUIPresenter = mAnnoImageChatViewRefreshUIPresenter;

        registerListener();

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
        return mSourceView;
    }

    @Override
    protected ChatPostMessage getMessage() {
        return mAnnoImageChatMessage;
    }

    @Override
    protected void registerListener() {
        super.registerListener();


        mAnnoImageChatViewRefreshUIPresenter.getAdapter().setOnItemClickListener((adapter, view, position) -> handleAnnoImageClick(position));

        mAnnoImageChatViewRefreshUIPresenter.getAdapter().setOnItemLongClickListener((adapter, view, position) -> handleAnnoImageLongClick());


    }

    private boolean handleAnnoImageLongClick() {
        AnchorInfo anchorInfo = getAnchorInfo();
        if (!mSelectMode) {
            mChatItemLongClickListener.annoImageLongClick(mAnnoImageChatMessage, anchorInfo);
            return true;
        }
        return false;
    }

    private void handleAnnoImageClick(int position) {
        if (mSelectMode) {
            mAnnoImageChatMessage.select = !mAnnoImageChatMessage.select;
            select(mAnnoImageChatMessage.select);
        } else {
            if (mChatItemClickListener != null) {
                mChatItemClickListener.annoImageClick(mAnnoImageChatMessage, mAnnoImageChatMessage.contentInfos.get(position));
            }
        }
    }

    private void findView() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.chat_right_image_mesasge_anno_with_comment, this);
        mVRoot = view.findViewById(R.id.rl_root);
        mIvAvatar = view.findViewById(R.id.chat_right_image_avatar);
        mLlContentRoot = view.findViewById(R.id.chat_right_image_layout);
        mRvImageContent = view.findViewById(R.id.rv_image_content);
        mChatSendStatusView = view.findViewById(R.id.chat_right_image_status);

        mIvSelect = view.findViewById(R.id.right_image_select);

        mSourceView = view.findViewById(R.id.message_srouce_from);


        mLayoutChatMessageItemTimeInfoInAnnoImageView = view.findViewById(R.id.layout_chat_message_item_time_info_in_anno_image_view);
        mLlSomeStatusInfoInAnnoImageView = view.findViewById(R.id.ll_some_status_info);
        mTvTimeInAnnoImageView = mLayoutChatMessageItemTimeInfoInAnnoImageView.findViewById(R.id.tv_time);
        mIvSomeStatusInAnnoImageView = mLayoutChatMessageItemTimeInfoInAnnoImageView.findViewById(R.id.iv_some_status);

        mLlSomeStatusInfoWrapperParentInAnnoImageCommentView = view.findViewById(R.id.ll_some_status_info_wrapper_parent_in_anno_image_comment_view);
        mFlReplyInAnnoImageCommentView = mLlSomeStatusInfoWrapperParentInAnnoImageCommentView.findViewById(R.id.fl_reply_in_anno_image_comment_view);
        mLlSomeStatusInfoInAnnoImageCommentView = mLlSomeStatusInfoWrapperParentInAnnoImageCommentView.findViewById(R.id.ll_some_status_info);
        mTvTimeInAnnoImageCommentView = mLlSomeStatusInfoWrapperParentInAnnoImageCommentView.findViewById(R.id.tv_time);
        mIvSomeStatusInAnnoImageCommentView = mLlSomeStatusInfoWrapperParentInAnnoImageCommentView.findViewById(R.id.iv_some_status);
        mTvComment = mLlSomeStatusInfoWrapperParentInAnnoImageCommentView.findViewById(R.id.tv_comment_in_anno_image_comment_view);
    }




    @Nullable
    @Override
    protected SomeStatusView getSomeStatusView() {

        if(null == mAnnoImageChatMessage) {
            return null;
        }

        if(StringUtils.isEmpty(mAnnoImageChatMessage.comment)) {
            return SomeStatusView
                    .newSomeStatusView()
                    .setIvStatus(mIvSomeStatusInAnnoImageView)
                    .setTvTime(mTvTimeInAnnoImageView)
                    .setLlSomeStatusInfo(mLlSomeStatusInfoInAnnoImageView)

                    .setSomeStatusInfoAreaGrayBg(getContext());
        }


        return SomeStatusView
                .newSomeStatusView()
                .setVgSomeStatusWrapperParent(mLlSomeStatusInfoWrapperParentInAnnoImageCommentView)
                .setTvContentShow(mTvComment)
                .setIvStatus(mIvSomeStatusInAnnoImageCommentView)
                .setTvTime(mTvTimeInAnnoImageCommentView)
                .setIconDoubleTick(R.mipmap.icon_double_tick_white)
                .setIconOneTick(R.mipmap.icon_one_tick_white)
                .setMaxTvContentWidthBaseOn(mFlReplyInAnnoImageCommentView, mLlSomeStatusInfoInAnnoImageCommentView)
                .setLlSomeStatusInfo(mLlSomeStatusInfoInAnnoImageCommentView);

    }


    @Override
    protected View getMessageRootView() {
        return mVRoot;
    }

    @Override
    public void refreshItemView(ChatPostMessage message) {
        if (message instanceof AnnoImageChatMessage) {
            mAnnoImageChatMessage = (AnnoImageChatMessage) message;
        }

        if(StringUtils.isEmpty(mAnnoImageChatMessage.comment)) {
            mLayoutChatMessageItemTimeInfoInAnnoImageView.setVisibility(View.VISIBLE);
            mLlSomeStatusInfoWrapperParentInAnnoImageCommentView.setVisibility(View.GONE);
        } else {


            mLayoutChatMessageItemTimeInfoInAnnoImageView.setVisibility(View.GONE);
            mLlSomeStatusInfoWrapperParentInAnnoImageCommentView.setVisibility(View.VISIBLE);
        }

        super.refreshItemView(message);


    }






    @Override
    protected void burnSkin() {
        super.burnSkin();
//        ThemeResourceHelper.setChatRightViewColorBg9Drawable(mIvContent);

    }

    @Override
    protected void themeSkin() {
        super.themeSkin();
//        ThemeResourceHelper.setChatRightViewColorBg9Drawable(mIvContent);
    }

    @NonNull
    @Override
    protected View getContentRootView() {
        return mLlContentRoot;
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
