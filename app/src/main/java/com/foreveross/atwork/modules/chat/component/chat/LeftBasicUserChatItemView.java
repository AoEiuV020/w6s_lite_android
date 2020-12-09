package com.foreveross.atwork.modules.chat.component.chat;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.discussion.DiscussionAsyncNetService;
import com.foreveross.atwork.infrastructure.model.Employee;
import com.foreveross.atwork.infrastructure.model.discussion.Discussion;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.ParticipantType;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.manager.model.SetReadableNameParams;
import com.foreveross.atwork.modules.chat.component.chat.extension.LeftBasicUserChatItemViewExtensionKt;
import com.foreveross.atwork.modules.discussion.manager.DiscussionManager;
import com.foreveross.atwork.utils.AvatarHelper;
import com.foreveross.atwork.utils.ContactShowNameHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dasunsy on 2017/5/10.
 */

public abstract class LeftBasicUserChatItemView extends BasicUserChatItemView {

    public List<TextView> tvTagList = new ArrayList<>();

    public LeftBasicUserChatItemView(Context context) {
        super(context);
    }

    public LeftBasicUserChatItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    protected abstract TextView getNameView();

    protected abstract TextView getSubTitleView();

    public abstract @Nullable View getTagLinerLayout();

    protected abstract TextView getConfirmEmergencyView();

    @Override
    public void refreshItemView(ChatPostMessage message) {
        super.refreshItemView(message);

        refreshDiscussionMemberNameUI(getNameView(), message);

        refreshEmergencyStatus(message);

        refreshSubTitleView(message);

        refreshTagView(message);
    }

    private void refreshTagView(ChatPostMessage message) {
        View tagView = getTagLinerLayout();
        if(null == tagView) {
            return;
        }


        LeftBasicUserChatItemViewExtensionKt.doRefreshTagView(this, message);


    }

    private  void refreshSubTitleView(ChatPostMessage message) {
        if(null == getSubTitleView()) {
            return;
        }

        if(!AtworkConfig.CHAT_CONFIG.isChatDetailViewNeedOrgPosition()) {
            return;
        }

        if(!message.isFromDiscussionChat()) {
            return;
        }

        if(null == message.orgPositionInfo || !message.orgPositionInfo.isLegal() ) {
            getSubTitleView().setVisibility(GONE);
            return;
        }

        getSubTitleView().setText(Employee.getJobTitleWithLast3OrgName(message.orgPositionInfo.orgDeptInfos, message.orgPositionInfo.jobTitle));
        getSubTitleView().setVisibility(VISIBLE);

    }

    protected void refreshEmergencyStatus(ChatPostMessage message) {
        TextView confirmEmergencyView = getConfirmEmergencyView();
        if(null != confirmEmergencyView) {
            if(message.isEmergency()) {
                confirmEmergencyView.setVisibility(VISIBLE);

                if(message.mEmergencyInfo.mConfirmed) {
                    confirmEmergencyView.setText(R.string.had_confirmed);
                } else {
                    confirmEmergencyView.setText(R.string.confirm_received);

                }

            } else {
                confirmEmergencyView.setVisibility(GONE);

            }
        }
    }

    @Override
    protected void refreshAvatar(ChatPostMessage message) {
        if (ParticipantType.App.equals(message.mFromType)) {
            AvatarHelper.setAppAvatarById(getAvatarView(), message.from, message.mOrgId, true, true);

        } else {
            AvatarHelper.setUserAvatarById(getAvatarView(), message.from, message.mFromDomain, true, true);

        }
    }



    @Override
    protected void registerListener() {
        super.registerListener();



        getAvatarView().setOnLongClickListener(v -> {
            //只有群聊的时候才触发
            if (ParticipantType.Discussion.equals(getMessage().mToType)) {

                if (!mSelectMode && mChatItemClickListener != null) {
                    mChatItemClickListener.avatarLongClick(getMessage().from, getMessage().mFromDomain);
                }
            }
            return true;
        });

    }

    public void refreshDiscussionMemberNameUI(@Nullable TextView textView, ChatPostMessage message) {
        if(null == textView) {
            return;
        }

        if (shouldShowAvatarInDiscussionChat(message)) {
            textView.setVisibility(View.VISIBLE);

            SetReadableNameParams setReadableNameParams = SetReadableNameParams.newSetReadableNameParams()
                    .setDiscussionId(message.to)
                    .setTextView(textView)
                    .setUserId(message.from)
                    .setDomainId(message.mFromDomain);

            ContactShowNameHelper.setReadableNames(setReadableNameParams);




        } else {
            textView.setVisibility(View.GONE);

        }
    }


}
