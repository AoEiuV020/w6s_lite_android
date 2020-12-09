package com.foreveross.atwork.modules.chat.component.chat;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.foreverht.workplus.ui.component.foregroundview.ForegroundLinearLayout;
import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.TextChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.UnknownChatMessage;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.ViewUtil;
import com.foreveross.atwork.modules.chat.component.ChatSendStatusView;
import com.foreveross.atwork.modules.chat.data.AnchorInfo;
import com.foreveross.atwork.modules.chat.util.AutoLinkHelper;
import com.foreveross.atwork.modules.chat.util.TextMsgHelper;
import com.foreveross.atwork.modules.chat.util.TextTranslateHelper;
import com.foreveross.atwork.utils.ThemeResourceHelper;
import com.foreveross.theme.manager.SkinHelper;

import java.util.UUID;



public class RightTextChatItemView extends RightBasicUserChatItemView {

    private View mVRoot;

    private FrameLayout mFlChatRightTextSendStatus;

    private ImageView mAvatarView;

    private ForegroundLinearLayout mLlTextContent;

    private TextView mTvContent;

    private View mVLineTranslate;

    private TextView mTvTextTranslate;

    private TextView mTvTranslateSourceLabel;

    private ChatSendStatusView mChatSendStatusView;

    private ImageView mSelectView;

    private LinearLayout mLlSomeStatusInfoWrapperParent;
    private LinearLayout mLlSomeStatusInfo;
    private TextView mTvTime;
    private ImageView mIvSomeStatus;

    /**
     * textMessage or unknownMessage
     * */
    private ChatPostMessage mChatMessage;


    private MessageSourceView mSourceView;

    private String mViewDoubleClickTag;
    private String mSessionId;


    public RightTextChatItemView(Context context) {
        super(context);
        findView();
        registerListener();

        mViewDoubleClickTag = UUID.randomUUID().toString();

    }

    @Override
    protected View getMessageRootView() {
        return mVRoot;
    }

    public void setSessionId(String sessionId) {
        mSessionId = sessionId;
    }

    @Override
    protected ChatSendStatusView getChatSendStatusView() {
        return mChatSendStatusView;
    }

    @Override
    protected ImageView getAvatarView() {
        return mAvatarView;
    }

    @Override
    protected ImageView getSelectView() {
        return mSelectView;
    }

    @Override
    protected MessageSourceView getMessageSourceView() {
        return mSourceView;
    }

    @Override
    protected ChatPostMessage getMessage() {
        return mChatMessage;
    }

    @Override
    protected void registerListener() {
        super.registerListener();

        setContentViewClick();

        setContentViewLongClick();
    }

    private void setContentViewLongClick() {
        mLlTextContent.setOnLongClickListener(v -> {
            return handleLongClick();
        });

        mTvContent.setOnLongClickListener(v -> {
            return handleLongClick();
        });

        mTvTextTranslate.setOnLongClickListener(v -> {
            return handleLongClick();
        });
    }

    private void setContentViewClick() {
        mLlTextContent.setOnClickListener(v -> handleClick());

        mTvContent.setOnClickListener(v -> handleClick());

        mTvTextTranslate.setOnClickListener(v -> handleClick());
    }

    private boolean handleLongClick() {
        AutoLinkHelper.getInstance().setLongClick(true);

        AnchorInfo anchorInfo = getAnchorInfo();

        if (!mSelectMode) {
            mChatItemLongClickListener.textLongClick(mChatMessage, anchorInfo);
            return true;
        }
        return false;
    }

    private void handleClick() {
        AutoLinkHelper.getInstance().setLongClick(false);
        if (mSelectMode) {
            mChatMessage.select = !mChatMessage.select;
            select(mChatMessage.select);
        } else {
            if (mChatMessage instanceof TextChatMessage) {
                if(null != mChatItemClickListener) {
                    mChatItemClickListener.textClick((TextChatMessage) mChatMessage, mViewDoubleClickTag);
                }
            }
        }
    }

    private void findView() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.chat_right_text_message, this);
        mVRoot = view.findViewById(R.id.rl_root);
        mFlChatRightTextSendStatus = view.findViewById(R.id.fl_chat_right_text_send_status);
        mAvatarView = view.findViewById(R.id.chat_right_text_avatar);
        mLlTextContent = view.findViewById(R.id.ll_chat_right_content);
        mTvContent = view.findViewById(R.id.chat_right_text_content);
        mTvTextTranslate = view.findViewById(R.id.chat_right_text_translate);
        mVLineTranslate = view.findViewById(R.id.v_line);
        mTvTranslateSourceLabel = view.findViewById(R.id.tv_translate_source);
        mChatSendStatusView = view.findViewById(R.id.chat_right_text_send_status);
        mSelectView = view.findViewById(R.id.right_text_select);

        mTvContent.setTextColor(SkinHelper.getPrimaryTextColor());

        mSourceView = view.findViewById(R.id.message_srouce_from);

        mLlSomeStatusInfoWrapperParent = view.findViewById(R.id.ll_some_status_info_wrapper_parent);
        mLlSomeStatusInfo = view.findViewById(R.id.ll_some_status_info);
        mTvTime = view.findViewById(R.id.tv_time);
        mIvSomeStatus = view.findViewById(R.id.iv_some_status);

    }


    @Override
    public void refreshItemView(ChatPostMessage message) {
        mChatMessage = message;

        super.refreshItemView(message);

        if (message instanceof TextChatMessage) {
            TextChatMessage textChatMessage = (TextChatMessage) message;
            String text = TextMsgHelper.getShowText(textChatMessage);
            mTvContent.setText(AutoLinkHelper.getInstance().getSpannableString(getContext(), mSessionId, message, mTvContent, text));

            refreshTranslateStatusUI(textChatMessage);
        } else if(message instanceof UnknownChatMessage) {
            String text = AtworkApplicationLike.getResourceString(R.string.unknown_message);
            mTvContent.setText(text);

        }



//        if(shouldHoldAvatarView(message)) {
//            ViewUtil.setLeftMarginDp(mLlTextContent, 60);
//            ViewUtil.setLeftMarginDp(mFlChatRightTextSendStatus, -55);
//
//        } else {
//            ViewUtil.setLeftMarginDp(mLlTextContent,100);
//            ViewUtil.setLeftMarginDp(mFlChatRightTextSendStatus, -95);
//
//        }
    }

    @Override
    protected SomeStatusView getSomeStatusView() {
        return SomeStatusView
                .newSomeStatusView()
                .setVgSomeStatusWrapperParent(mLlSomeStatusInfoWrapperParent)
                .setTvContentShow(mTvContent)
                .setIvStatus(mIvSomeStatus)
                .setIconDoubleTick(R.mipmap.icon_double_tick_white)
                .setIconOneTick(R.mipmap.icon_one_tick_white)
                .setTvTime(mTvTime)
                .setLlSomeStatusInfo(mLlSomeStatusInfo);

    }

    @Override
    protected void burnSkin() {
        super.burnSkin();
        ThemeResourceHelper.setChatRightViewColorBg9Drawable(mLlTextContent);

    }

    @Override
    protected void themeSkin() {
        super.themeSkin();
        ThemeResourceHelper.setChatRightViewColorBg9Drawable(mLlTextContent);
        mTvContent.setTextColor(ContextCompat.getColor(getContext(), R.color.white));

    }

    @NonNull
    @Override
    protected View getContentRootView() {
        return mLlTextContent;
    }


    @Override
    protected Drawable getBlinkDrawable() {
        return ContextCompat.getDrawable(getContext(), R.drawable.shape_chat_message_blink_with_corners);
    }


    private void refreshTranslateStatusUI(TextChatMessage textChatMessage) {

        if (textChatMessage.isTranslateStatusVisible()) {

            if(textChatMessage.isTranslating()) {
                showTranslatingView();
                mTvTranslateSourceLabel.setText(R.string.text_translating);
            } else {
                if(!StringUtils.isEmpty(textChatMessage.getTranslatedResult()) && textChatMessage.isTranslateStatusVisible()) {
                    showTranslateUI(true);
                    mTvTextTranslate.setText(textChatMessage.getTranslatedResult());
                    mTvTranslateSourceLabel.setText(TextTranslateHelper.getSource(textChatMessage));

                } else {
                    //hide translate ui
                    showTranslateUI(false);
                }
            }
        } else {
            //hide translate ui
            showTranslateUI(false);
        }

    }

    private void refreshContentViewGravity(TextChatMessage textChatMessage) {
        boolean gravityLeft = true;

        if(textChatMessage.isEmergency()) {
            gravityLeft = true;

        } else {
            if (textChatMessage.isTranslateStatusVisible()) {

                if (!textChatMessage.isTranslating()) {
                    if(!StringUtils.isEmpty(textChatMessage.getTranslatedResult())) {
                        if(textChatMessage.getTranslatedResult().length() != TextMsgHelper.getShowText(textChatMessage).length()) {
                            gravityLeft = true;
                        }
                    }
                }
            }
        }

        setContentViewGravity(gravityLeft, mLlSomeStatusInfoWrapperParent);
        setContentViewGravity(gravityLeft, mTvTextTranslate);

    }

    private void setContentViewGravity(boolean gravityLeft, View view) {
        LinearLayout.LayoutParams layoutParamsContent = (LinearLayout.LayoutParams) view.getLayoutParams();

        if(gravityLeft) {
            layoutParamsContent.gravity = Gravity.LEFT;
        } else {
            layoutParamsContent.gravity = Gravity.CENTER;
        }

        view.setLayoutParams(layoutParamsContent);

    }


    private void showTranslatingView() {
        ViewUtil.setVisible(mVLineTranslate, false);
        ViewUtil.setVisible(mTvTextTranslate, false);
        ViewUtil.setVisible(mTvTranslateSourceLabel, true);
    }



    private void showTranslateUI(boolean visible) {
        ViewUtil.setVisible(mVLineTranslate, visible);
        ViewUtil.setVisible(mTvTextTranslate, visible);
        ViewUtil.setVisible(mTvTranslateSourceLabel, visible);
    }
}
