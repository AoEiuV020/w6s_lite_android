package com.foreveross.atwork.modules.voip.adapter.qsy;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.model.voip.UserStatus;
import com.foreveross.atwork.infrastructure.model.voip.VoiceType;
import com.foreveross.atwork.infrastructure.model.voip.VoipMeetingMember;
import com.foreveross.atwork.infrastructure.model.voip.VoipMeetingMemberWrapData;
import com.foreveross.atwork.utils.AvatarHelper;

import java.util.Collections;
import java.util.List;

/**
 * Created by peng.xu on 2015/12/10.
 */
public class AudioUserListAdapter extends BaseAdapter {

    public static final String TAG = "AudioUserListAdapter";

    private List<VoipMeetingMemberWrapData> mMeetingPartList;
    private Context mContext;

    public AudioUserListAdapter(Context context, List<VoipMeetingMemberWrapData> objects) {
        mContext = context;
        mMeetingPartList = objects;
        Collections.sort(mMeetingPartList);
    }

    public void setData(List<VoipMeetingMemberWrapData> objects) {
        mMeetingPartList = objects;
        Collections.sort(mMeetingPartList);
        notifyDataSetChanged();
    }

    @Override
    public void notifyDataSetChanged() {
        if (mMeetingPartList.size() == getCount()) {
            Collections.sort(mMeetingPartList);
            super.notifyDataSetChanged();
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        if (convertView == null) {
            convertView = createItemView();
        }
        ViewHolder viewHolder = (ViewHolder) convertView.getTag();
        VoipMeetingMemberWrapData itemData = getItem(position);
        if (null != itemData) {
            VoipMeetingMember user = itemData.getUserEntity();
            // 加载头像和名字
            if (user == null) {
                viewHolder.mIvAvatar.setImageResource(R.mipmap.default_photo);
                viewHolder.mTvName.setText("");

            } else {
                viewHolder.mTvName.setText(user.mShowName);
                AvatarHelper.setUserInfoAvatar(user, viewHolder.mIvAvatar);


            }
            if (itemData.getUserEntity().getUserStatus().equals(UserStatus.UserStatus_Joined)) {
                viewHolder.mFlStatus.setVisibility(View.VISIBLE);
                viewHolder.mRlImageMsg.setVisibility(View.VISIBLE);
                viewHolder.mIvCoverLay.setVisibility(View.GONE);
                viewHolder.mTvTextMsg.setVisibility(View.GONE);
                viewHolder.mIvAvatar.setAlpha(1.0f);
                viewHolder.mTvName.setAlpha(1.0f);
                viewHolder.mTvTextMsg.setAlpha(1.0f);
                viewHolder.mTvTextMsg.setTextColor(mContext.getResources().getColor(android.R.color.white));
                viewHolder.mTvName.setTextColor(mContext.getResources().getColor(android.R.color.white));

                // 根据音频类型显示不同的icon
                if (itemData.getVoiceType() == VoiceType.NONE) {
                    viewHolder.mIvVoiceType.setVisibility(View.GONE);
                } else if (itemData.getVoiceType() == VoiceType.VOIP) {
                    viewHolder.mIvVoiceType.setVisibility(View.VISIBLE);
                    if (itemData.isMute()) {
                        viewHolder.mIvVoiceType.setImageResource(R.mipmap.tangsdk_mute_state_icon);
                    } else {
                        viewHolder.mIvVoiceType.setImageResource(R.mipmap.tangsdk_voip_normal_icon);
                    }
                } else if (itemData.getVoiceType() == VoiceType.TEL) {
                    viewHolder.mIvVoiceType.setVisibility(View.VISIBLE);
                    if (itemData.isMute()) {
                        viewHolder.mIvVoiceType.setImageResource(R.mipmap.tangsdk_tel_mute_icon);
                    } else {
                        viewHolder.mIvVoiceType.setImageResource(R.mipmap.tangsdk_tel_normal_icon);
                    }
                }

                viewHolder.mIvShareDesk.setVisibility(itemData.isDesktopShared() ? View.VISIBLE : View.GONE);
                // 是否开启视频
                viewHolder.mIvOpenVideo.setVisibility(itemData.isVideoShared() ? View.VISIBLE : View.GONE);
                // 是否正在说话  TODO 动画波谱
                viewHolder.mIvSpectrum.setVisibility(itemData.isSpeaking() ? View.VISIBLE : View.INVISIBLE);

            } else if (itemData.getUserEntity().getUserStatus().equals(UserStatus.UserStatus_Rejected)) {
                viewHolder.mRlImageMsg.setVisibility(View.GONE);
                viewHolder.mIvSpectrum.setVisibility(View.INVISIBLE);
                viewHolder.mFlStatus.setVisibility(View.VISIBLE);
                viewHolder.mTvTextMsg.setVisibility(View.VISIBLE);
                viewHolder.mTvTextMsg.setText(mContext.getString(R.string.tangsdk_audio_reject_call_msg));
                viewHolder.mIvAvatar.setAlpha(0.35f);
                viewHolder.mTvName.setAlpha(0.35f);
                viewHolder.mTvTextMsg.setAlpha(0.35f);
            } else if (itemData.getUserEntity().getUserStatus().equals(UserStatus.UserStatus_NotJoined)) {
                viewHolder.mRlImageMsg.setVisibility(View.GONE);
                viewHolder.mIvSpectrum.setVisibility(View.INVISIBLE);
                viewHolder.mFlStatus.setVisibility(View.VISIBLE);
                viewHolder.mTvTextMsg.setVisibility(View.VISIBLE);
                viewHolder.mTvTextMsg.setText(mContext.getString(R.string.tangsdk_audio_not_join_msg));
                viewHolder.mIvAvatar.setAlpha(0.35f);
                viewHolder.mTvName.setAlpha(0.35f);
                viewHolder.mTvTextMsg.setAlpha(0.35f);
            } else if (itemData.getUserEntity().getUserStatus().equals(UserStatus.UserStatus_Left)) {
                viewHolder.mRlImageMsg.setVisibility(View.GONE);
                viewHolder.mIvSpectrum.setVisibility(View.INVISIBLE);
                viewHolder.mFlStatus.setVisibility(View.VISIBLE);
                viewHolder.mTvTextMsg.setVisibility(View.VISIBLE);
                viewHolder.mTvTextMsg.setText(mContext.getString(R.string.tangsdk_audio_left_msg));
                viewHolder.mIvAvatar.setAlpha(0.35f);
                viewHolder.mTvName.setAlpha(0.35f);
                viewHolder.mTvTextMsg.setAlpha(0.35f);
            }

        }

        return convertView;

    }


    private View createItemView() {
        final View view = ((Activity) mContext).getLayoutInflater().inflate(R.layout.item_view_audio_user, null);
        ViewHolder viewHolder = new ViewHolder();
        viewHolder.mIvAvatar = (ImageView) view.findViewById(R.id.iv_avatar);
        viewHolder.mRlImageMsg = (RelativeLayout) view.findViewById(R.id.image_msg);
        viewHolder.mIvShareDesk = (ImageView) view.findViewById(R.id.desk_share_img);
        viewHolder.mIvOpenVideo = (ImageView) view.findViewById(R.id.video_img);
        viewHolder.mIvVoiceType = (ImageView) view.findViewById(R.id.voice_type_img);
        viewHolder.mTvName = (TextView) view.findViewById(R.id.tv_name);
        viewHolder.mIvSpectrum = (ImageView) view.findViewById(R.id.iv_spectrum);
        viewHolder.mFlStatus = (FrameLayout) view.findViewById(R.id.fl_status);
        viewHolder.mIvCoverLay = (ImageView) view.findViewById(R.id.avatar_cover_lay);
        viewHolder.mTvTextMsg = (TextView) view.findViewById(R.id.text_msg);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public int getCount() {
        return mMeetingPartList != null ? mMeetingPartList.size() : 0;
    }

    @Nullable
    @Override
    public VoipMeetingMemberWrapData getItem(int position) {
        if (position >= 0 && position < getCount()) {
            return mMeetingPartList.get(position);
        }
        return null;
    }

    public void clear() {
        mContext = null;
    }


    public final class ViewHolder {

        public ImageView mIvAvatar;
        public TextView mTvName;
        public FrameLayout mFlStatus;
        public RelativeLayout mRlImageMsg;
        public ImageView mIvShareDesk;
        public ImageView mIvOpenVideo;
        public ImageView mIvVoiceType;
        public ImageView mIvSpectrum;
        public ImageView mIvCoverLay;
        public TextView mTvTextMsg;
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    public VoipMeetingMember getUserEntityById(String userId) {
        if (mMeetingPartList == null || mMeetingPartList.size() <= 0) {
            return null;
        }
        for (VoipMeetingMemberWrapData itemData : mMeetingPartList) {
            if (!itemData.getUserEntity().mUserId.equals(userId)) {
                continue;
            }
            return itemData.getUserEntity();
        }
        return null;
    }


}
