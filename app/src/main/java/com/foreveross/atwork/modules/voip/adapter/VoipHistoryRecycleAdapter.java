package com.foreveross.atwork.modules.voip.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.infrastructure.model.voip.MeetingInfo;
import com.foreveross.atwork.infrastructure.model.voip.MeetingStatus;
import com.foreveross.atwork.infrastructure.model.voip.VoipMeetingGroup;
import com.foreveross.atwork.infrastructure.model.voip.VoipMeetingMember;
import com.foreveross.atwork.infrastructure.model.voip.VoipType;
import com.foreveross.atwork.manager.model.SetReadableNameParams;
import com.foreveross.atwork.modules.voip.activity.VoipSelectModeActivity;
import com.foreveross.atwork.modules.voip.utils.VoipHelper;
import com.foreveross.atwork.utils.AtworkToast;
import com.foreveross.atwork.utils.AtworkUtil;
import com.foreveross.atwork.utils.AvatarHelper;
import com.foreveross.atwork.utils.ContactShowNameHelper;
import com.foreveross.atwork.utils.TimeViewUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dasunsy on 16/8/8.
 */
public class VoipHistoryRecycleAdapter extends RecyclerView.Adapter {

    public Context mContext;
    private final LayoutInflater mInflater;

    private List<VoipMeetingGroup> mVoipHistoryList;


    public VoipHistoryRecycleAdapter(Context context, List<VoipMeetingGroup> voipMeetingGroupList) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);

        this.mVoipHistoryList = voipMeetingGroupList;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = mInflater.inflate(R.layout.item_list_voip_history, parent, false);
        HistoryItemViewHolder itemViewHolder = new HistoryItemViewHolder(rootView);

        rootView.setOnClickListener(v -> {
            if (AtworkUtil.isSystemCalling()) {
                AtworkToast.showResToast(R.string.alert_is_handling_system_call);
                return;
            }

            if (VoipHelper.isHandlingVoipCall()) {
                AtworkToast.showResToast(R.string.alert_is_handling_voip_meeting_click_voip);

            } else {
                VoipMeetingGroup voipMeetingGroup = mVoipHistoryList.get(itemViewHolder.getAdapterPosition());
                Intent intent = VoipSelectModeActivity.getIntent(mContext, new ArrayList<>(voipMeetingGroup.mParticipantList));
                mContext.startActivity(intent);

            }

        });

        return itemViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        HistoryItemViewHolder itemViewHolder = (HistoryItemViewHolder) holder;
        VoipMeetingGroup voipMeetingGroup = mVoipHistoryList.get(position);

        if (null != voipMeetingGroup.mMeetingInfo && MeetingInfo.Type.USER.equals(voipMeetingGroup.mMeetingInfo.mType)) {
            VoipMeetingMember peer = voipMeetingGroup.getPeer(mContext);
            if (null != peer) {
                SetReadableNameParams setReadableNameParams = SetReadableNameParams
                        .newSetReadableNameParams()
                        .setTextView(itemViewHolder.mTvTitle)
                        .setUserId(peer.mUserId)
                        .setDomainId(peer.mDomainId);

                ContactShowNameHelper.setReadableNames(setReadableNameParams);
                AvatarHelper.setUserAvatarByAvaId(peer.mAvatar, itemViewHolder.mIvAvatar, true, true);
            }

        } else {

            itemViewHolder.mTvTitle.setText(mContext.getString(R.string.tip_history_list_item_audio_title, voipMeetingGroup.mParticipantList.size()));

            AvatarHelper.setDiscussionAvatarByAvaId(itemViewHolder.mIvAvatar, voipMeetingGroup.mAvatar, true, true);

        }


        if (MeetingStatus.SUCCESS.equals(voipMeetingGroup.mStatus)) {
            if (VoipType.VIDEO.equals(voipMeetingGroup.mVoipType)) {
                itemViewHolder.mIvStatus.setImageResource(R.mipmap.icon_voip_video_accept);

            } else {
                itemViewHolder.mIvStatus.setImageResource(R.mipmap.icon_voip_audio_accept);

            }

            String durationTimeStr = VoipHelper.toCallDurationShow(voipMeetingGroup.mDuration / 1000);

            itemViewHolder.mTvTipStatus.setText(mContext.getString(R.string.call_duration, durationTimeStr));

        } else {
            if (VoipType.VIDEO.equals(voipMeetingGroup.mVoipType)) {
                itemViewHolder.mIvStatus.setImageResource(R.mipmap.icon_voip_video_cancel);

            } else {
                itemViewHolder.mIvStatus.setImageResource(R.mipmap.icon_voip_audio_cancel);

            }

            if (MeetingStatus.FAILED.equals(voipMeetingGroup.mStatus)) {
                if (User.isYou(mContext, voipMeetingGroup.mCreator.mUserId)) {
                    itemViewHolder.mTvTipStatus.setText(R.string.tip_voip_failed_self);

                } else {
                    itemViewHolder.mTvTipStatus.setText(R.string.tip_voip_failed_other);

                }

            }

        }

        itemViewHolder.mTvCallTime.setText(TimeViewUtil.getSimpleUserCanViewTime(BaseApplicationLike.baseContext , voipMeetingGroup.mCallTime));


    }

    @Override
    public int getItemCount() {
        return mVoipHistoryList.size();
    }

    private static class HistoryItemViewHolder extends RecyclerView.ViewHolder {

        public ImageView mIvAvatar;
        public TextView mTvTitle;
        public TextView mTvTipStatus;
        public TextView mTvCallTime;
        public ImageView mIvStatus;

        public HistoryItemViewHolder(View itemView) {
            super(itemView);
            mIvAvatar = itemView.findViewById(R.id.iv_avatar);
            mTvTitle = itemView.findViewById(R.id.tv_title);
            mTvTipStatus = itemView.findViewById(R.id.tv_tip_status);
            mTvCallTime = itemView.findViewById(R.id.tv_calltime);
            mIvStatus = itemView.findViewById(R.id.iv_status);


        }
    }
}
