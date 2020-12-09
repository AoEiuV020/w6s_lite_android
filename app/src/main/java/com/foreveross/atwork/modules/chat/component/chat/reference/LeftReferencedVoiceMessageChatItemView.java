package com.foreveross.atwork.modules.chat.component.chat.reference;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.VoiceChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.reference.ReferenceMessage;
import com.foreveross.atwork.modules.chat.component.chat.MessageSourceView;
import com.foreveross.atwork.modules.chat.component.chat.SomeStatusView;
import com.foreveross.atwork.modules.chat.component.chat.definition.IVoiceChatView;
import com.foreveross.atwork.modules.chat.data.AnchorInfo;
import com.foreveross.atwork.modules.chat.inter.PlayAudioListener;
import com.foreveross.atwork.modules.chat.model.PlayAudioInChatDetailViewParams;
import com.foreveross.atwork.modules.chat.presenter.VoiceChatViewRefreshUIPresenter;
import com.foreveross.atwork.modules.chat.util.AudioRecord;
import com.foreveross.atwork.modules.chat.util.AutoLinkHelper;
import com.foreveross.atwork.modules.voip.utils.VoipHelper;
import com.foreveross.atwork.utils.AtworkToast;
import com.foreveross.atwork.utils.ThemeResourceHelper;

import org.jetbrains.annotations.NotNull;

import java.io.File;


public class LeftReferencedVoiceMessageChatItemView extends LeftBasicReferenceUserChatItemView implements PlayAudioListener, IVoiceChatView {

    private View mVRoot;

    private ImageView mIvAvatar;

    private TextView mTvName;

    private TextView mTvSubTitle;

    private LinearLayout mLlContent;

    private ImageView mIvSelect;

    private ReferenceMessage mReferencedChatMessage;
    private VoiceChatMessage mReferencingVoiceChatMessage;

    private TextView mTvAuthorName;

    private FrameLayout mFlVoiceBg;
    private ImageView mIvVoice;
    private TextView mTvDuration;

    private FrameLayout mFlReplay;
    private TextView mTvReply;

    private LinearLayout mLlSomeStatusInfoWrapperParent;
    private LinearLayout mLlSomeStatusInfo;
    private TextView mTvTime;
    private ImageView mIvSomeStatus;

    private LinearLayout mLlTags;

    private VoiceChatViewRefreshUIPresenter mVoiceChatViewRefreshUIPresenter;



    public LeftReferencedVoiceMessageChatItemView(Context context) {
        super(context);
        findView();
        registerListener();

        mVoiceChatViewRefreshUIPresenter = new VoiceChatViewRefreshUIPresenter(context, this);
        mChatViewRefreshUIPresenter = mVoiceChatViewRefreshUIPresenter;
    }

    @Override
    protected SomeStatusView getSomeStatusView() {
        return SomeStatusView
                .newSomeStatusView()
                .setVgSomeStatusWrapperParent(mLlSomeStatusInfoWrapperParent)
                .setTvContentShow(mTvReply)
                .setIvStatus(mIvSomeStatus)
                .setTvTime(mTvTime)
                .setTvTimeTextColor(ContextCompat.getColor(AtworkApplicationLike.baseContext, R.color.common_text_color_999))
                .setMaxTvContentWidthBaseOn(mFlReplay, mLlSomeStatusInfo)
                .setLlSomeStatusInfo(mLlSomeStatusInfo);

    }


    @Override
    protected View getMessageRootView() {
        return mVRoot;
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


        mLlContent.setOnClickListener(v -> {
            AutoLinkHelper.getInstance().setLongClick(false);
            if (mSelectMode) {
                mReferencedChatMessage.select = !mReferencedChatMessage.select;
                select(mReferencedChatMessage.select);

            } else {
                mChatItemClickListener.referenceClick(mReferencedChatMessage);
            }
        });

        mLlContent.setOnLongClickListener(v -> {
            AutoLinkHelper.getInstance().setLongClick(true);
            AnchorInfo anchorInfo = getAnchorInfo();
            if (!mSelectMode) {
                mChatItemLongClickListener.referenceLongClick(mReferencedChatMessage, anchorInfo);
                return true;
            }
            return false;
        });


        mFlVoiceBg.setOnClickListener(v -> {
            if (mSelectMode) {
                mReferencedChatMessage.select = !mReferencedChatMessage.select;
                select(mReferencedChatMessage.select);
            } else {
                if (mReferencingVoiceChatMessage != null) {
                    if (VoipHelper.isHandlingVoipCall()) {
                        AtworkToast.showResToast(R.string.alert_is_handling_voip_meeting_click_voip);

                    } else {
                        playAudio();
                }
                }
            }

        });
    }

    private void findView() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.chat_left_referenced_voice_message, this);
        mVRoot = view.findViewById(R.id.rl_root);
        mTvName = view.findViewById(R.id.chat_left_text_username);
        mTvSubTitle = view.findViewById(R.id.chat_left_text_sub_title);
        mIvAvatar = view.findViewById(R.id.chat_left_text_avatar);
        mLlContent = view.findViewById(R.id.ll_chat_left_content);
        mTvAuthorName = view.findViewById(R.id.tv_title);
        mFlVoiceBg = view.findViewById(R.id.fl_voice_bg);
        mIvVoice = view.findViewById(R.id.iv_voice);
        mTvDuration = view.findViewById(R.id.tv_duration);
        mFlReplay = view.findViewById(R.id.fl_reply);
        mTvReply = view.findViewById(R.id.tv_reply);
        mIvSelect = view.findViewById(R.id.left_text_select);

        mLlSomeStatusInfoWrapperParent = view.findViewById(R.id.ll_some_status_info_wrapper_parent);
        mLlSomeStatusInfo = view.findViewById(R.id.ll_some_status_info);
        mTvTime = view.findViewById(R.id.tv_time);
        mIvSomeStatus = view.findViewById(R.id.iv_some_status);

        mLlTags = view.findViewById(R.id.ll_tags);
    }

    @Override
    public void refreshItemView(ChatPostMessage message) {
        super.refreshItemView(message);

        mReferencedChatMessage = (ReferenceMessage) message;

        if (mReferencedChatMessage.mReferencingMessage instanceof VoiceChatMessage) {
            mReferencingVoiceChatMessage = (VoiceChatMessage) mReferencedChatMessage.mReferencingMessage;

        }


    }




    @Override
    protected TextView getNameView() {
        return mTvName;
    }

    @Override
    protected TextView getSubTitleView() {
        return mTvSubTitle;
    }

    @Nullable
    @Override
    public View getTagLinerLayout() {
        return mLlTags;
    }

    @Override
    protected TextView getConfirmEmergencyView() {
        return null;
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



    @Override
    public void playAudio() {

        String path = VoiceChatMessage.getAudioPath(getContext(), mReferencingVoiceChatMessage.deliveryId);
        if (!new File(path).exists()) {
            mReferencingVoiceChatMessage.playing = false;
        }


        if (mReferencingVoiceChatMessage.playing) {
            AudioRecord.stopPlaying();
            mReferencingVoiceChatMessage.playing = false;

            mVoiceChatViewRefreshUIPresenter.stopPlayingAnimation();
        } else {
            PlayAudioInChatDetailViewParams playAudioInChatDetailViewParams = new PlayAudioInChatDetailViewParams();
            playAudioInChatDetailViewParams.setContext(getContext());
            playAudioInChatDetailViewParams.setVoiceChatMessage(mReferencingVoiceChatMessage);
            playAudioInChatDetailViewParams.setNeedTryPlayNext(false);
            playAudioInChatDetailViewParams.setVoicePlayingListener(mVoiceChatViewRefreshUIPresenter);

            AudioRecord.playAudioInChatDetailView(playAudioInChatDetailViewParams);
            mReferencingVoiceChatMessage.playing = true;
        }
    }

    @NotNull
    @Override
    public FrameLayout voiceBgFlView() {
        return mFlVoiceBg;
    }

    @NotNull
    @Override
    public ImageView voiceView() {
        return mIvVoice;
    }

    @NotNull
    @Override
    public TextView durationView() {
        return mTvDuration;
    }
}
