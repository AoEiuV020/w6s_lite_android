package com.foreveross.atwork.modules.voip.component.agora;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.model.ShowListItem;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.infrastructure.model.voip.VoipMeetingMember;
import com.foreveross.atwork.manager.VoipMeetingController;

/**
 * Created by dasunsy on 2016/10/24.
 */

public class MeetingVideoModeItemView extends FrameLayout {

    private Context mContext;

    public VoipMeetingMember mBindingMember;

    public SurfaceView mSurface;

    public FrameLayout mFlSurfaceHome;

    public ImageView mIvBigAudioStatus;

    public ImageView mIvSmallAudioStatus;

    public TextView mTvName;

    public MeetingVideoModeItemView(Context context) {
        super(context);

        mContext = context;

        findViews();
    }

    public MeetingVideoModeItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    private void findViews() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_video_mode_view, this);
        mIvBigAudioStatus = (ImageView) view.findViewById(R.id.iv_big_audio_status);
        mIvSmallAudioStatus = (ImageView) view.findViewById(R.id.iv_small_audio_status);
        mFlSurfaceHome = (FrameLayout) view.findViewById(R.id.fl_surface_home);
        mTvName = (TextView) view.findViewById(R.id.tv_name);

        createSurface();
    }

    private void createSurface() {
        mSurface = VoipMeetingController.getInstance().createRendererView(mContext);
        if (null != mSurface) {
            LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            mSurface.setLayoutParams(params);

            mSurface.setZOrderOnTop(true);
            mSurface.setZOrderMediaOverlay(true);

            mFlSurfaceHome.addView(mSurface);
        }
    }


    public void audioMode() {
        if(mBindingMember.mIsMute) {
            mIvBigAudioStatus.setImageResource(R.mipmap.voip_silence_close);

        } else {
            mIvBigAudioStatus.setImageResource(R.mipmap.big_video_mode_audio_icon);

        }

        mIvBigAudioStatus.setVisibility(VISIBLE);
        if (null != mSurface) {
            mSurface.setVisibility(GONE);
        }
        mIvSmallAudioStatus.setVisibility(GONE);
    }

    public void videoMode() {
        if(mBindingMember.mIsMute) {
            mIvSmallAudioStatus.setVisibility(VISIBLE);

        } else {
            mIvSmallAudioStatus.setVisibility(GONE);

        }

        mIvBigAudioStatus.setVisibility(GONE);

        if(null == mSurface) {
            createSurface();
        }

        if(null != mSurface) {
            mSurface.setVisibility(VISIBLE);


            VoipMeetingController.getInstance().setupVideoView(mSurface, mBindingMember.getUid());
        }


    }


    public void bindVideoView(VoipMeetingMember member) {
        this.mBindingMember = member;
    }

    public void refresh() {
        if(this.mBindingMember.mIsVideoShared) {
            videoMode();
        } else {
            audioMode();
        }

        mTvName.setText(getShowName(getContext(), this.mBindingMember));
    }

    /**
     * 如果是自己就显示"我", 其他的显示名字
     * @param contact
     * @param context
     * */
    private String getShowName(Context context, ShowListItem contact) {
        if(User.isYou(context, contact.getId())) {
            return context.getString(R.string.item_about_me);
        }

        return contact.getParticipantTitle();
    }


}
