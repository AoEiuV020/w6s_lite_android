package com.foreveross.atwork.modules.chat.component.chat;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.util.Linkify;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.BaseCallBackNetWorkListener;
import com.foreveross.atwork.api.sdk.message.MessageAsyncNetService;
import com.foreveross.atwork.api.sdk.message.model.EmergencyMessageConfirmRequest;
import com.foreveross.atwork.component.ProgressDialogHelper;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.TextChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.UnknownChatMessage;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.ViewUtil;
import com.foreveross.atwork.modules.chat.data.AnchorInfo;
import com.foreveross.atwork.modules.chat.util.AutoLinkHelper;
import com.foreveross.atwork.modules.chat.util.EmergencyMessageConfirmHelper;
import com.foreveross.atwork.modules.chat.util.TextMsgHelper;
import com.foreveross.atwork.modules.chat.util.TextTranslateHelper;
import com.foreveross.atwork.utils.ErrorHandleUtil;
import com.foreveross.theme.manager.SkinHelper;

import java.util.UUID;

public class LeftTextChatItemView extends LeftBasicUserChatItemView {

    private View mVRoot;

    private ImageView mAvatarView;

    private TextView mTvName;

    private TextView mTvSubTitle;

    private LinearLayout mLlTags;

    private LinearLayout mLlTextContent;

    private TextView mTvContent;

    private View mVLineTranslate;

    private TextView mTvTextTranslate;

    private TextView mTvTranslateSourceLabel;

    private ImageView mSelectView;

    private TextView mTvConfirmEmergency;

    /**
     * textMessage or unknownMessage
     * */
    private ChatPostMessage mChatMessage;

    private MessageSourceView mSourceView;

    private LinearLayout mLlSomeStatusInfoWrapperParent;
    private LinearLayout mLlSomeStatusInfo;
    private TextView mTvTime;

    private String mViewDoubleClickTag;
    private String mSessionId;

    public LeftTextChatItemView(Context context) {
        super(context);
        findView();
        registerListener();

        mViewDoubleClickTag = UUID.randomUUID().toString();
    }

    public void setSessionId(String sessionId) {
        mSessionId = sessionId;
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
    protected TextView getNameView() {
        return mTvName;
    }

    @Override
    protected TextView getSubTitleView() {
        return mTvSubTitle;
    }

    public View getTagLinerLayout() {
        return mLlTags;
    }

    @Override
    protected TextView getConfirmEmergencyView() {
        return mTvConfirmEmergency;
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

        mTvConfirmEmergency.setOnClickListener(v -> {

            doClickEmergencyInfo();
        });

    }

    private void doClickEmergencyInfo() {
        if(!mChatMessage.isEmergencyConfirmed()) {
            EmergencyMessageConfirmRequest emergencyMessageConfirmRequest = EmergencyMessageConfirmRequest.newInstance()
                    .setDeliveryId(mChatMessage.deliveryId)
                    .setAppId(mChatMessage.from)
                    .setOrgCode(mChatMessage.mOrgId)
                    .setType(EmergencyMessageConfirmRequest.Type.SERVE_NO)
                    .setPlanId(mChatMessage.mEmergencyInfo.mPlanId);

            ProgressDialogHelper progressDialogHelper = new ProgressDialogHelper(getContext());
            progressDialogHelper.show();

            MessageAsyncNetService.confirmEmergencyMessage(getContext(), emergencyMessageConfirmRequest, new BaseCallBackNetWorkListener() {
                @Override
                public void onSuccess() {
                    progressDialogHelper.dismiss();

                    EmergencyMessageConfirmHelper.updateConfirmResultAndUpdateDb((TextChatMessage) mChatMessage);
                }

                @Override
                public void networkFail(int errorCode, String errorMsg) {
                    progressDialogHelper.dismiss();

                    ErrorHandleUtil.handleError(errorCode, errorMsg);
                }
            });
        }
    }

    private void setContentViewLongClick() {
        mLlTextContent.setOnLongClickListener(v -> handleLongClick());

        mTvContent.setOnLongClickListener(v -> handleLongClick());

        mTvTextTranslate.setOnLongClickListener(v -> handleLongClick());

    }

    private void setContentViewClick() {
        mLlTextContent.setOnClickListener(v -> handleClick());

        mTvContent.setOnClickListener(v -> handleClick());

        mTvTextTranslate.setOnClickListener(v -> handleClick());
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

    private boolean handleLongClick() {
        AutoLinkHelper.getInstance().setLongClick(true);
        AnchorInfo anchorInfo = getAnchorInfo();

        if (!mSelectMode) {
            mChatItemLongClickListener.textLongClick(mChatMessage, anchorInfo);
            return true;
        }
        return false;
    }

    private void findView() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.chat_left_text_message, this);
        mVRoot = view.findViewById(R.id.rl_root);
        mAvatarView = view.findViewById(R.id.chat_left_text_avatar);
        mTvName = view.findViewById(R.id.chat_left_text_username);
        mTvSubTitle = view.findViewById(R.id.chat_left_text_sub_title);
        mLlTags = view.findViewById(R.id.ll_tags);
        mLlTextContent = view.findViewById(R.id.ll_chat_left_content);
        mTvContent = view.findViewById(R.id.chat_left_text_content);
        mTvTextTranslate = view.findViewById(R.id.chat_left_text_translate);
        mVLineTranslate = view.findViewById(R.id.v_line);
        mTvTranslateSourceLabel = view.findViewById(R.id.tv_translate_source);
        mSelectView = view.findViewById(R.id.left_text_select);
        mSelectView.setVisibility(GONE);
        Linkify.addLinks(mTvContent, Linkify.WEB_URLS | Linkify.EMAIL_ADDRESSES | Linkify.PHONE_NUMBERS);
        mTvContent.setTextColor(SkinHelper.getPrimaryTextColor());
        mSourceView = view.findViewById(R.id.message_srouce_from);
        mTvConfirmEmergency = view.findViewById(R.id.tv_confirm_emergency);

        mLlSomeStatusInfoWrapperParent = view.findViewById(R.id.ll_some_status_info_wrapper_parent);
        mLlSomeStatusInfo = view.findViewById(R.id.ll_some_status_info);
        mTvTime = view.findViewById(R.id.tv_time);

        mTvTime.setTextColor(ContextCompat.getColor(getContext(), R.color.common_text_color_999));

    }

    @Override
    protected SomeStatusView getSomeStatusView() {
        return SomeStatusView
                .newSomeStatusView()
                .setVgSomeStatusWrapperParent(mLlSomeStatusInfoWrapperParent)
                .setTvContentShow(mTvContent)
                .setTvTime(mTvTime)
                .setLlSomeStatusInfo(mLlSomeStatusInfo);

    }

    @Override
    protected View getMessageRootView() {
        return mVRoot;
    }

    @Override
    protected void refreshEmergencyStatus(ChatPostMessage message) {
        super.refreshEmergencyStatus(message);

        if(message.isEmergency()) {
            mLlTextContent.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
        } else {
            mLlTextContent.getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;

        }
    }

    @Override
    public void refreshItemView(ChatPostMessage message) {
        mChatMessage = message;

        super.refreshItemView(message);

        if (message instanceof TextChatMessage) {
            TextChatMessage textChatMessage = (TextChatMessage) message;

            mTvContent.setText(AutoLinkHelper.getInstance().getTextMessageSpannableString(getContext(), mTvContent, mSessionId, textChatMessage));


            refreshTranslateStatusUI(textChatMessage);

            refreshContentViewGravity(textChatMessage);

        } else if(message instanceof UnknownChatMessage) {
            String text = AtworkApplicationLike.getResourceString(R.string.unknown_message);
            mTvContent.setText(AutoLinkHelper.getInstance().getSpannableString(getContext(), mSessionId, message, mTvContent, text));

        }


//        if(shouldHoldAvatarView(message)) {
//            ViewUtil.setRightMarginDp(mLlTextContent, 60);
//
//        } else {
//            ViewUtil.setRightMarginDp(mLlTextContent,100);
//
//        }

    }

    @Override
    protected Drawable getBlinkDrawable() {
        return ContextCompat.getDrawable(getContext(), R.drawable.shape_chat_message_blink_with_corners);
    }

    @Override
    protected void themeSkin() {
        super.themeSkin();
//        mLlTextContent.setBackgroundResource(R.mipmap.bg_chat_left);

    }

    @NonNull
    @Override
    protected View getContentRootView() {
        return mLlTextContent;
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

