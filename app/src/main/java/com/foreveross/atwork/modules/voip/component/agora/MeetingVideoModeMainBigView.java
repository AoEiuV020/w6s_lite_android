package com.foreveross.atwork.modules.voip.component.agora;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.model.voip.VoipMeetingMember;
import com.foreveross.atwork.manager.VoipMeetingController;

/**
 * Created by dasunsy on 2016/10/25.
 */

public class MeetingVideoModeMainBigView extends FrameLayout {

    private Context mContext;

    public SurfaceView mSurface;

    public FrameLayout mFlVideoHome;

    private ImageView mIvAudioMode;

    public VoipMeetingMember mBindingMember;


    public MeetingVideoModeMainBigView(Context context) {
        super(context);
    }

    public MeetingVideoModeMainBigView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mContext = context;
        findViews();
    }

    private void findViews() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.view_big_video, this);
        mIvAudioMode = (ImageView) view.findViewById(R.id.iv_audio);
        mFlVideoHome = (FrameLayout) view.findViewById(R.id.fl_surface_home);

        createSurface();
    }

    private void createSurface() {
        mSurface = VoipMeetingController.getInstance().createRendererView(mContext);

        if (null != mSurface) {
            mFlVideoHome.addView(mSurface);
        }
    }

    public void audioMode() {
        if(mBindingMember.mIsMute) {
            mIvAudioMode.setImageResource(R.mipmap.voip_silence_close);

        } else {
            mIvAudioMode.setImageResource(R.mipmap.big_video_mode_audio_icon);
        }

        mIvAudioMode.setVisibility(VISIBLE);
        if (null != mSurface) {
            mSurface.setVisibility(GONE);
        }
    }

    public void videoMode() {
        mIvAudioMode.setVisibility(GONE);
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
        if (null != mBindingMember) {
            if (this.mBindingMember.mIsVideoShared) {
                videoMode();
            } else {
                audioMode();
            }
        }

    }
}
