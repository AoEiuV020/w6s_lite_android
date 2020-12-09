package com.foreveross.atwork.modules.chat.component.chat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.VoiceChatMessage;
import com.foreveross.atwork.infrastructure.utils.DensityUtil;
import com.foreveross.atwork.infrastructure.utils.LogUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.ViewUtil;
import com.foreveross.atwork.modules.chat.component.ChatSendStatusView;
import com.foreveross.atwork.modules.chat.dao.ChatDaoService;
import com.foreveross.atwork.modules.chat.data.AnchorInfo;
import com.foreveross.atwork.modules.chat.inter.PlayAudioListener;
import com.foreveross.atwork.modules.chat.inter.VoicePlayingListener;
import com.foreveross.atwork.modules.chat.model.PlayAudioInChatDetailViewParams;
import com.foreveross.atwork.modules.chat.util.AudioRecord;
import com.foreveross.atwork.modules.chat.util.ChatDetailExposeBroadcastSender;
import com.foreveross.atwork.modules.voip.utils.VoipHelper;
import com.foreveross.atwork.utils.AtworkToast;
import com.foreveross.atwork.utils.AtworkUtil;
import com.foreveross.atwork.utils.ThemeResourceHelper;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

import java.io.File;

/**
 * Created by lingen on 15/3/25.
 * Description:
 */
public class RightVoiceChatItemView extends RightBasicUserChatItemView implements PlayAudioListener, VoicePlayingListener {

    private View mVRoot;
    private ImageView mAvatarView;
    private SeekBar mSbSeek;
    private ImageView mIvVoiceHandle;

    private TextView mTvPlayTime;
    private VoiceChatMessage mVoiceChatMessage;
    private ImageView mSelectView;
    private View mViewVoice;

    private View mViewVoiceParent;
    private ChatSendStatusView mChatSendStatusView;

    private ImageView mIvPlaying;

    private Context mContext;

    private MessageSourceView mSourceView;
    private TextView mChatRightVoiceTranslate;
    private FrameLayout mChatRightVoiceTranslateLayout;
    private LinearLayout mVoiceShowOriginalWhite, mVoiceTranslating;
    private RelativeLayout mVoiceContent;

    private LinearLayout mLlSomeStatusInfoWrapperParent;
    private LinearLayout mLlSomeStatusInfo;
    private TextView mTvTime;
    private ImageView mIvSomeStatus;

    public RightVoiceChatItemView(Context context) {
        super(context);
        mContext = context;
        findView();
        registerListener();
    }

    @Override
    protected View getMessageRootView() {
        return mVRoot;
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
        return mVoiceChatMessage;
    }

    @Override
    protected void registerListener() {
        super.registerListener();

        mSbSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            boolean isTrackingHandled = false;

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(isTrackingHandled) {
                    refreshTvPlayTime(seekBar);

                } else {
                    if(AudioRecord.isPlaying(mVoiceChatMessage.currentPlayId)) {
                        refreshTvPlayTime(seekBar);
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isTrackingHandled = true;
                mVoiceChatMessage.playing = false;
                AudioRecord.pause(mVoiceChatMessage.currentPlayId, RightVoiceChatItemView.this);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isTrackingHandled = false;
                playAudio(seekBar);

            }
        });

        mChatRightVoiceTranslateLayout.setOnClickListener(v -> {
            //设置隐藏
            mVoiceChatMessage.mVoiceTranslateStatus.mTranslating = false;
            mVoiceChatMessage.mVoiceTranslateStatus.mVisible = false;
            //更新
            ChatDaoService.getInstance().insertOrUpdateMessage(BaseApplicationLike.baseContext, mVoiceChatMessage);
            //发送广播
            ChatDetailExposeBroadcastSender.refreshMessageListViewUI();
            return;
        });


        findViewWithTag("select_able").setOnClickListener(v -> {
            if (mSelectMode) {
                mVoiceChatMessage.select = !mVoiceChatMessage.select;
                select(mVoiceChatMessage.select);
            }
        });


        findViewById(R.id.chat_right_voice_frame).setOnClickListener(v -> {
            if (mSelectMode) {
                mVoiceChatMessage.select = !mVoiceChatMessage.select;
                select(mVoiceChatMessage.select);
            } else {
                if (mVoiceChatMessage != null) {


                    if(mVoiceChatMessage.playing) {
                        mVoiceChatMessage.playing = false;
                        AudioRecord.pause(mVoiceChatMessage.currentPlayId, RightVoiceChatItemView.this);
                        return;
                    }


                    if (VoipHelper.isHandlingVoipCall()) {
                        AtworkToast.showResToast(R.string.alert_is_handling_voip_meeting_click_voip);

                    } else {

                        playAudio(mSbSeek);
                    }
                }
            }
        });

        findViewById(R.id.chat_right_voice_frame).setOnLongClickListener(v -> {
            AnchorInfo anchorInfo = getAnchorInfo();
            if (!mSelectMode) {
                mChatItemLongClickListener.voiceLongClick(mVoiceChatMessage, anchorInfo);
                return true;
            }
            return false;
        });

    }

    private void refreshTvPlayTime(SeekBar seekBar) {
        String durationClock = seekBar.getProgress() / 1000 + "\"";

        if (!durationClock.equals(mTvPlayTime.getText().toString())) {
            mTvPlayTime.setText(durationClock);
        }
    }

    public void playingAnimation() {
        Activity activity = (Activity) getContext();
        activity.runOnUiThread(() -> {
            mIvVoiceHandle.setImageResource(R.mipmap.icon_voice_chat_pause_white);

            startProgressTask();
//            mIvPlaying.setBackgroundResource(R.drawable.right_voice_animation);
//            AnimationDrawable frameAnimation = (AnimationDrawable) mIvPlaying.getBackground();
            // Start the animation (looped playback by default).
//            frameAnimation.start();
        });
    }

    public void stopPlayingAnimation() {
        Activity activity = (Activity) getContext();
        activity.runOnUiThread(() -> {
            mIvVoiceHandle.setImageResource(R.mipmap.icon_voice_chat_play_white);
            cancelProgressTask();
//            if (mIvPlaying.getBackground() instanceof AnimationDrawable) {
//                AnimationDrawable frameAnimation = (AnimationDrawable) mIvPlaying.getBackground();
//                frameAnimation.stop();
//                mIvPlaying.setBackgroundResource(R.mipmap.icon_sound_left);
//            }

        });

    }

    private void findView() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.chat_right_voice_message, this);
        mVRoot = view.findViewById(R.id.rl_root);
        mVoiceContent = view.findViewById(R.id.voice_content);
        mAvatarView = view.findViewById(R.id.chat_right_voice_avatar);

        mIvVoiceHandle = view.findViewById(R.id.iv_voice_handle);
        mSbSeek = view.findViewById(R.id.sb_seek);
        mTvPlayTime = view.findViewById(R.id.chat_right_voice_time);
        mSelectView = view.findViewById(R.id.right_voice_select);
        mViewVoice = view.findViewById(R.id.right_voice_bg_view);
        mViewVoiceParent = view.findViewById(R.id.chat_right_voice_frame);
        mChatSendStatusView = view.findViewById(R.id.chat_right_voice_status);
        mIvPlaying = view.findViewById(R.id.right_voice_playing);
        mSourceView = view.findViewById(R.id.message_srouce_from);
        mChatRightVoiceTranslate = view.findViewById(R.id.chat_right_voice_translate);
        mVoiceShowOriginalWhite = view.findViewById(R.id.voice_show_original_white);
        mChatRightVoiceTranslateLayout = view.findViewById(R.id.chat_right_voice_translate_layout);
        mVoiceTranslating = view.findViewById(R.id.voice_tranalateing);

        mLlSomeStatusInfoWrapperParent = view.findViewById(R.id.ll_some_status_info_wrapper_parent);
        mLlSomeStatusInfo = view.findViewById(R.id.ll_some_status_info);
        mTvTime = view.findViewById(R.id.tv_time);
        mIvSomeStatus = view.findViewById(R.id.iv_some_status);
    }


    @Override
    public void refreshItemView(ChatPostMessage message) {
        super.refreshItemView(message);

        mVoiceChatMessage = (VoiceChatMessage) message;

        calVoiceLength();
        mSbSeek.setMax(mVoiceChatMessage.duration * 1000);
        mSbSeek.setSecondaryProgress(mVoiceChatMessage.duration * 1000);
        if (!mVoiceChatMessage.playing) {

            int playingProgress = AudioRecord.getPlayingProgress(mVoiceChatMessage.currentPlayId);

            if(0 < playingProgress) {
                mSbSeek.setProgress(playingProgress);
                refreshTvPlayTime(mSbSeek);

            } else {
                mSbSeek.setProgress(0);
                mTvPlayTime.setText(mVoiceChatMessage.duration + "\"");
            }


            stopPlayingAnimation();


        } else {
            refreshVoiceView();

            playingAnimation();

        }

        refreshTranslateStatusUI(mVoiceChatMessage);
    }

    @Override
    protected SomeStatusView getSomeStatusView() {
        return SomeStatusView
                .newSomeStatusView()
                .setVgSomeStatusWrapperParent(mLlSomeStatusInfoWrapperParent)
                .setIvStatus(mIvSomeStatus)
                .setIconDoubleTick(R.mipmap.icon_double_tick_white)
                .setIconOneTick(R.mipmap.icon_one_tick_white)
                .setTvTime(mTvTime)
                .setLlSomeStatusInfo(mLlSomeStatusInfo);

    }

    @NonNull
    @Override
    protected View getContentBlinkView() {
        return mViewVoiceParent;
    }


    @Override
    protected Drawable getBlinkDrawable() {
        return ContextCompat.getDrawable(getContext(), R.drawable.shape_chat_message_blink_with_corners);
    }

    private void calVoiceLength() {
        //动态计算声音长度
        int length = 4 + mVoiceChatMessage.duration;
        if (length > 18) {
            length = 18;
        }
        mSbSeek.getLayoutParams().width = DensityUtil.DP_1O_TO_PX * length;


    }

    private void refreshTranslateStatusUI(VoiceChatMessage voiceChatMessage) {
        if (voiceChatMessage.isTranslateStatusVisible()) {

            if(voiceChatMessage.isTranslating()) {
                showTranslatingView();

            } else {
                if(!StringUtils.isEmpty(voiceChatMessage.getTranslatedResult())) {
                    showTranslateUI(true);
                    mChatRightVoiceTranslate.setText(voiceChatMessage.getTranslatedResult());

                } else {
                    //hide translate ui
                    showTranslateUI(false);
                    //AtworkToast.showToast("翻译失败");
                }
            }
        } else {
            //hide translate ui
            showTranslateUI(false);
        }
    }

    @Override
    public void playAudio() {
        Activity activity = (Activity) getContext();
        AndPermission
                .with(activity)
                .runtime()
                .permission(Permission.WRITE_EXTERNAL_STORAGE, Permission.READ_EXTERNAL_STORAGE)
                .onGranted(data -> {
                    playAudio(null);
                })
                .onDenied(data ->  AtworkUtil.popAuthSettingAlert(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE))
                .start();
    }

    public void playAudio(SeekBar seekBar) {
        String path = VoiceChatMessage.getAudioPath(mContext, mVoiceChatMessage.deliveryId);
        if (!new File(path).exists()) {
            mVoiceChatMessage.playing = false;
        }


        if (mVoiceChatMessage.playing) {
            mVoiceChatMessage.play = true;
            AudioRecord.stopPlaying();
            RightVoiceChatItemView.this.stopPlayingAnimation();

        } else {
            if (null != seekBar) {
                refreshTvPlayTime(seekBar);
            } else {
                mTvPlayTime.setText("0\"");
            }

            PlayAudioInChatDetailViewParams playAudioInChatDetailViewParams = new PlayAudioInChatDetailViewParams();
            playAudioInChatDetailViewParams.setContext(getContext());
            playAudioInChatDetailViewParams.setVoiceChatMessage(mVoiceChatMessage);
            playAudioInChatDetailViewParams.setVoicePlayingListener(RightVoiceChatItemView.this);
            if (null != seekBar) {
                playAudioInChatDetailViewParams.setSeekPosition(seekBar.getProgress());
            } else {
                playAudioInChatDetailViewParams.setSeekPosition(-1);

            }

            if(AudioRecord.isAnyPlaying()) {
                playAudioInChatDetailViewParams.setInSuccession(true);
            }


            mVoiceChatMessage.currentPlayId = AudioRecord.playAudioInChatDetailView(playAudioInChatDetailViewParams);
            mVoiceChatMessage.playing = true;
        }

    }


    @Override
    protected void burnSkin() {
        super.burnSkin();

        ThemeResourceHelper.setChatRightViewColorBg9Drawable(mViewVoice);
        mTvPlayTime.setTextColor(ContextCompat.getColor(getContext(), R.color.white));


    }

    @Override
    protected void themeSkin() {
        super.themeSkin();

        ThemeResourceHelper.setChatRightViewColorBg9Drawable(mViewVoice);
        mTvPlayTime.setTextColor(ContextCompat.getColor(getContext(), R.color.white));

    }

    @NonNull
    @Override
    protected View getContentRootView() {
        return mVoiceContent;
    }

    //显示正在翻译中的UI
    private void showTranslatingView() {
        showTranslateUI(false);
        ViewUtil.setVisible(mVoiceTranslating, true);
    }
    //显示翻译成功的UI
    private void showTranslateUI(boolean visible) {
        ViewUtil.setVisible(mVoiceTranslating, false);
        ViewUtil.setVisible(mChatRightVoiceTranslateLayout, visible);
        ViewUtil.setVisible(mChatRightVoiceTranslate, visible);
        ViewUtil.setVisible(mVoiceShowOriginalWhite, visible);
    }

    private void startProgressTask() {

        cancelProgressTask();

        doStartProgressTask();
    }

    private void doStartProgressTask() {
        mSbSeek.postDelayed(mProgressTask, 1000 / 30);
    }

    private void cancelProgressTask() {
        mSbSeek.removeCallbacks(mProgressTask);
    }


    private Runnable mProgressTask = () -> {
        if(AudioRecord.isPlaying(mVoiceChatMessage.currentPlayId)) {

            refreshVoiceView();

            doStartProgressTask();
        }
    };

    private void refreshVoiceView() {
        int playingProgress = AudioRecord.getPlayingProgress(mVoiceChatMessage.currentPlayId);
        LogUtil.e("playingProgress :  " + playingProgress);
        mSbSeek.setProgress(playingProgress);


        int progressShow = (playingProgress + 500) / 1000;
        if(progressShow > mVoiceChatMessage.duration) {
            progressShow = mVoiceChatMessage.duration;
        }
        String progressShowStr = progressShow + "\"";
        if (!progressShowStr.equals(mTvPlayTime.getText().toString())) {
            mTvPlayTime.setText(progressShowStr);
        }
    }
}
