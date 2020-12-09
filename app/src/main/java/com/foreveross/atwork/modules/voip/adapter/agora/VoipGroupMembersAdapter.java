package com.foreveross.atwork.modules.voip.adapter.agora;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.infrastructure.model.voip.UserStatus;
import com.foreveross.atwork.infrastructure.model.voip.VoipMeetingMember;
import com.foreveross.atwork.utils.AvatarHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by dasunsy on 16/7/11.
 */
public class VoipGroupMembersAdapter extends RecyclerView.Adapter {

    private Context mContext;

    private final LayoutInflater mInflater;

    private CopyOnWriteArrayList<VoipMeetingMember> mMeetingMemberList;

    public VoipGroupMembersAdapter(Context context, CopyOnWriteArrayList<VoipMeetingMember> voipMeetingGroupList) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);

        this.mMeetingMemberList = voipMeetingGroupList;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = mInflater.inflate(R.layout.item_voip_member_agora, parent, false);
        VoipMemberItemViewHolder holder = new VoipMemberItemViewHolder(rootView);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        VoipMeetingMember member = getItem(position);

        if(null != member) {
            VoipMemberItemViewHolder itemViewHolder = (VoipMemberItemViewHolder) holder;

            itemViewHolder.clearTypes();

            if(UserStatus.UserStatus_Joined == member.getUserStatus()) {

                if(member.mIsVideoShared) {
                    itemViewHolder.addType(ViewType.VIDEO_NOTICE);
                }

                boolean havingStatus = false;

                if(member.mIsMute) {
                    itemViewHolder.addType(ViewType.MUTED);
                    havingStatus = true;

                } else {
                    if(member.mIsSpeaking) {
                        itemViewHolder.addType(ViewType.SPEAKING);
                        havingStatus = true;

                    }
                }



                if(!havingStatus) {
                    itemViewHolder.addType(ViewType.SILENCE);

                }

            } else if(UserStatus.UserStatus_NotJoined == member.getUserStatus()) {
                itemViewHolder.addType(ViewType.JOINING);

            } else if(UserStatus.UserStatus_Rejected == member.getUserStatus()) {
                itemViewHolder.addType(ViewType.REJECTED);

            } else if(UserStatus.UserStatus_Left == member.getUserStatus()) {
                itemViewHolder.addType(ViewType.LEAVED);

            } else {
                itemViewHolder.addType(ViewType.SILENCE);

            }

            itemViewHolder.refreshView();

            if (User.isYou(mContext, member.getId())) {
                itemViewHolder.mTvName.setText(R.string.item_about_me);

            } else {
                itemViewHolder.mTvName.setText(member.mShowName);

            }

            AvatarHelper.setUserInfoAvatar(member, itemViewHolder.mIvAvatar);
        }

    }


    @Nullable
    public VoipMeetingMember getItem(int position) {
        if (position >= 0 && position < getItemCount()) {
            return mMeetingMemberList.get(position);

        }

        return null;
    }


    @Override
    public int getItemCount() {
        return mMeetingMemberList.size();
    }



    public void refresh() {
        //todo CopyOnWriteArrayList sort util
        synchronized (mMeetingMemberList) {
            List arrayList = Arrays.asList(mMeetingMemberList.toArray());
            Collections.sort(arrayList);

            mMeetingMemberList.clear();
            mMeetingMemberList.addAll(arrayList);
        }

        super.notifyDataSetChanged();
    }

    private static class VoipMemberItemViewHolder extends RecyclerView.ViewHolder {

        public ImageView mIvAvatar;
        public TextView mTvName;

        public FrameLayout mFlCoverSpeakingStatus;
        public ImageView mIvCoverSpeakingStatus;

        public FrameLayout mFlCoverCallingStatus;
        public TextView mTvCallingStatus;

        public ImageView mIvVideoNotice;

        public List<ViewType> mViewTypeList = new ArrayList<>();

        public VoipMemberItemViewHolder(View itemView) {
            super(itemView);
            mIvAvatar = itemView.findViewById(R.id.iv_avatar);
            mTvName = itemView.findViewById(R.id.tv_name);
            mFlCoverSpeakingStatus = itemView.findViewById(R.id.fl_cover_speaking_status);
            mIvCoverSpeakingStatus = itemView.findViewById(R.id.iv_speaking_status);
            mFlCoverCallingStatus = itemView.findViewById(R.id.fl_cover_calling_status);
            mTvCallingStatus = itemView.findViewById(R.id.tv_calling_status);
            mIvVideoNotice = itemView.findViewById(R.id.iv_mode_video);
        }

        public void addType(ViewType viewType) {
            mViewTypeList.add(viewType);
        }

        public void clearTypes() {
            mViewTypeList.clear();
        }


        public void refreshView() {

            mIvVideoNotice.setVisibility(View.GONE);

            for(ViewType type : mViewTypeList) {
                switch (type) {
                    case JOINING:
                        mFlCoverSpeakingStatus.setVisibility(View.GONE);
                        mFlCoverCallingStatus.setVisibility(View.VISIBLE);

                        mFlCoverCallingStatus.setBackgroundResource(R.drawable.round_voip_member_cover_blue);
                        mTvCallingStatus.setText(R.string.tangsdk_audio_not_join_msg);

                        break;

                    case LEAVED:
                        mFlCoverSpeakingStatus.setVisibility(View.GONE);
                        mFlCoverCallingStatus.setVisibility(View.VISIBLE);

                        mFlCoverCallingStatus.setBackgroundResource(R.drawable.round_voip_member_cover_gray);
                        mTvCallingStatus.setText(R.string.tangsdk_audio_left_msg);

                        break;

                    case REJECTED:
                        mFlCoverSpeakingStatus.setVisibility(View.GONE);
                        mFlCoverCallingStatus.setVisibility(View.VISIBLE);

                        mFlCoverCallingStatus.setBackgroundResource(R.drawable.round_voip_member_cover_gray);
                        mTvCallingStatus.setText(R.string.tangsdk_audio_reject_call_msg);

                        break;

                    case SPEAKING:
                        mFlCoverSpeakingStatus.setVisibility(View.VISIBLE);
                        mFlCoverCallingStatus.setVisibility(View.GONE);

                        mFlCoverSpeakingStatus.setBackgroundResource(R.drawable.round_voip_member_border);
                        mIvCoverSpeakingStatus.setImageResource(R.mipmap.speaking);
                        break;


                    case MUTED:
                        mFlCoverSpeakingStatus.setVisibility(View.VISIBLE);
                        mFlCoverCallingStatus.setVisibility(View.GONE);

                        mFlCoverSpeakingStatus.setBackgroundResource(0);
                        mIvCoverSpeakingStatus.setImageResource(R.mipmap.muting);
                        break;


                    case SILENCE:
                        mFlCoverSpeakingStatus.setVisibility(View.GONE);
                        mFlCoverCallingStatus.setVisibility(View.GONE);

                        break;

                    case VIDEO_NOTICE:
                        mIvVideoNotice.setVisibility(View.VISIBLE);
                        mFlCoverCallingStatus.setVisibility(View.GONE);

                        break;

                }

            }
        }




    }

    private enum ViewType {
        /**
         * 正在讲话
         * */
        SPEAKING,

        /**
         * 正在加入中
         * */
        JOINING,

        /**
         * 已退出
         * */
        LEAVED,

        /**
         * 已拒绝
         * */
        REJECTED,

        /**
         * 静音状态
         * */
        MUTED,

        /**
         * 视频提醒
         * */
        VIDEO_NOTICE,

        /**
         * 默认状态
         * */
        SILENCE
    }
}
