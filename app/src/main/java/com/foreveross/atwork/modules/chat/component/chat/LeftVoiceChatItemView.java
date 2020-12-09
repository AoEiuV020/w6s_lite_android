package com.foreveross.atwork.modules.chat.component.chat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.VoiceChatMessage;
import com.foreveross.atwork.infrastructure.utils.DensityUtil;
import com.foreveross.atwork.infrastructure.utils.LogUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.ViewUtil;
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
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

import java.io.File;


public class LeftVoiceChatItemView extends LeftBasicUserChatItemView implements PlayAudioListener, VoicePlayingListener {

    private View mVRoot;
    private ImageView mAvatarView;
    private SeekBar mSbSeek;
    private ImageView mIvVoiceHandle;

    private TextView mTvName;
    private TextView mTvSubTitle;
    private TextView mTvPlayTime;
    private VoiceChatMessage mVoiceChatMessage;
    private ImageView mIvSelect;
    private View mViewVoice;
    private View mViewVoiceParent;
    private ImageView mIvDotView;
    private ImageView mIvPlaying;

    private Context mContext;

    private MessageSourceView mSourceView;

    private TextView mChatLeftVoiceTranslate;
    private FrameLayout mChatLeftVoiceTranslateLayout;
    private LinearLayout mVoiceShowOriginalWhite, mVoiceTranalateing;
    private RelativeLayout mVoiceContent;

    private LinearLayout mLlSomeStatusInfoWrapperParent;
    private LinearLayout mLlSomeStatusInfo;
    private TextView mTvTime;

    private LinearLayout mLlTags;


    public LeftVoiceChatItemView(Context context) {
        super(context);
        mContext = context;
        findView();
        registerListener();
    }

    @Override
    protected SomeStatusView getSomeStatusView() {
        return SomeStatusView
                .newSomeStatusView()
                .setVgSomeStatusWrapperParent(mLlSomeStatusInfoWrapperParent)
                .setTvTime(mTvTime)
                .setLlSomeStatusInfo(mLlSomeStatusInfo);

    }

    @Override
    protected ImageView getAvatarView() {
        return mAvatarView;
    }

    @Override
    protected ImageView getSelectView() {
        return mIvSelect;
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
    protected ChatPostMessage getMessage() {
        return mVoiceChatMessage;
    }

    /**
     * 注册监听事件
     */
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
                AudioRecord.pause(mVoiceChatMessage.currentPlayId, LeftVoiceChatItemView.this);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isTrackingHandled = false;

                playAudio(seekBar);

            }
        });


        mVoiceShowOriginalWhite.setOnClickListener(v -> {
            //设置隐藏
            mVoiceChatMessage.mVoiceTranslateStatus.mTranslating = false;
            mVoiceChatMessage.mVoiceTranslateStatus.mVisible = false;
            //更新
            ChatDaoService.getInstance().insertOrUpdateMessage(BaseApplicationLike.baseContext, mVoiceChatMessage);
            //发送广播
            ChatDetailExposeBroadcastSender.refreshMessageListViewUI();
            return;
        });

        mViewVoiceParent.setOnClickListener(v -> {
            mChatItemClickListener.hideAll();
            if (mSelectMode) {
                mVoiceChatMessage.select = !mVoiceChatMessage.select;
                select(mVoiceChatMessage.select);

            } else {
                if (mVoiceChatMessage != null) {
                    if(mVoiceChatMessage.playing) {
                        mVoiceChatMessage.playing = false;
                        AudioRecord.pause(mVoiceChatMessage.currentPlayId, LeftVoiceChatItemView.this);
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

        mViewVoiceParent.setOnLongClickListener(v -> {
            AnchorInfo anchorInfo = getAnchorInfo();
            if (!mSelectMode) {
                mChatItemLongClickListener.voiceLongClick(mVoiceChatMessage, anchorInfo);
            }
            return true;
        });


    }

    private void refreshTvPlayTime(SeekBar seekBar) {
        String durationClock = seekBar.getProgress() / 1000 + "\"";
        if (!durationClock.equals(mTvPlayTime.getText().toString())) {
            mTvPlayTime.setText(durationClock);
        }
    }

    private void findView() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.chat_left_voice_message, this);
        mVRoot = view.findViewById(R.id.rl_root);
        mVoiceContent = view.findViewById(R.id.voice_content);
        mAvatarView = view.findViewById(R.id.chat_left_voice_avatar);
        mIvVoiceHandle = view.findViewById(R.id.iv_voice_handle);
        mSbSeek = view.findViewById(R.id.sb_seek);
        mTvName = view.findViewById(R.id.chat_left_voice_username);
        mTvSubTitle = view.findViewById(R.id.chat_left_voice_sub_title);
        mTvPlayTime = view.findViewById(R.id.chat_left_voice_time);
        mIvSelect = view.findViewById(R.id.left_voice_select);
        mViewVoice = view.findViewById(R.id.chat_left_voice_voice);
        mViewVoiceParent = view.findViewById(R.id.chat_left_voice_framelayout);
        mIvDotView = view.findViewById(R.id.chat_left_voice_dot);
        mIvPlaying = view.findViewById(R.id.chat_left_voice_playing);
        mSourceView = view.findViewById(R.id.message_srouce_from);
        mChatLeftVoiceTranslate = view.findViewById(R.id.chat_left_voice_translate);
        mVoiceShowOriginalWhite = view.findViewById(R.id.voice_show_original_white);
        mChatLeftVoiceTranslateLayout = view.findViewById(R.id.chat_left_voice_translate_layout);
        mVoiceTranalateing = view.findViewById(R.id.voice_tranalateing);

        mLlSomeStatusInfoWrapperParent = view.findViewById(R.id.ll_some_status_info_wrapper_parent);
        mLlSomeStatusInfo = view.findViewById(R.id.ll_some_status_info);
        mTvTime = view.findViewById(R.id.tv_time);

        mLlTags = view.findViewById(R.id.ll_tags);

        mTvTime.setTextColor(ContextCompat.getColor(getContext(), R.color.common_text_color_999));

    }

    private void calVoiceLength() {
        //动态计算声音长度
        int length = 4 + mVoiceChatMessage.duration;
        if (length > 18) {
            length = 18;
        }
        mSbSeek.getLayoutParams().width = DensityUtil.DP_1O_TO_PX * length;


    }

    @Override
    protected View getMessageRootView() {
        return mVRoot;
    }

    @Override
    public void refreshItemView(ChatPostMessage message) {
        super.refreshItemView(message);
        mVoiceChatMessage = (VoiceChatMessage) message;

        if (!mVoiceChatMessage.play) {
            mIvDotView.setVisibility(VISIBLE);
        } else {
            mIvDotView.setVisibility(GONE);
        }

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

    @NonNull
    @Override
    protected View getContentBlinkView() {
        return mViewVoiceParent;
    }

    @Override
    protected Drawable getBlinkDrawable() {
        return ContextCompat.getDrawable(getContext(), R.drawable.shape_chat_message_blink_with_corners);
    }

    private void refreshTranslateStatusUI(VoiceChatMessage voiceChatMessage) {
        if (voiceChatMessage.isTranslateStatusVisible()) {

            if (voiceChatMessage.isTranslating()) {
                showTranslatingView();

            } else {
                if (!StringUtils.isEmpty(voiceChatMessage.getTranslatedResult())) {
                    showTranslateUI(true);
                    Log.e("显示翻译后的UI", "refreshTranslateStatusUI: " + voiceChatMessage.getTranslatedResult());
                    mChatLeftVoiceTranslate.setText(voiceChatMessage.getTranslatedResult());

                } else {
                    //hide translate ui
                    showTranslateUI(false);
                    AtworkToast.showToast("翻译失败");
                }
            }
        } else {
            //hide translate ui
            showTranslateUI(false);
        }
    }


    public void playingAnimation() {
        Activity activity = (Activity) getContext();
        activity.runOnUiThread(() -> {
            mIvVoiceHandle.setImageResource(R.mipmap.icon_voice_chat_pause_black);
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
            mIvVoiceHandle.setImageResource(R.mipmap.icon_voice_chat_play_black);
            cancelProgressTask();
//            if (mIvPlaying.getBackground() instanceof AnimationDrawable) {
//                AnimationDrawable frameAnimation = (AnimationDrawable) mIvPlaying.getBackground();
//                frameAnimation.stop();
//                mIvPlaying.setBackgroundResource(R.mipmap.icon_sound_left);
//            }

        });

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
            AudioRecord.stopPlaying();
            mVoiceChatMessage.play = true;
            LeftVoiceChatItemView.this.stopPlayingAnimation();
            mVoiceChatMessage.playing = false;

        } else {
            if (null != seekBar) {
                refreshTvPlayTime(seekBar);
            } else {
                mTvPlayTime.setText("0\"");
            }

            PlayAudioInChatDetailViewParams playAudioInChatDetailViewParams = new PlayAudioInChatDetailViewParams();
            playAudioInChatDetailViewParams.setContext(getContext());
            playAudioInChatDetailViewParams.setVoiceChatMessage(mVoiceChatMessage);
            playAudioInChatDetailViewParams.setVoicePlayingListener(LeftVoiceChatItemView.this);
            if (null != seekBar) {
                playAudioInChatDetailViewParams.setSeekPosition(seekBar.getProgress());
            } else {
                playAudioInChatDetailViewParams.setSeekPosition(-1);

            }
            if (AudioRecord.isAnyPlaying()) {
                playAudioInChatDetailViewParams.setInSuccession(true);
            }

            mVoiceChatMessage.currentPlayId = AudioRecord.playAudioInChatDetailView(playAudioInChatDetailViewParams);
            mVoiceChatMessage.play = true;
            mVoiceChatMessage.playing = true;
            ChatDaoService.getInstance().updateMessage(mVoiceChatMessage);
            mIvDotView.setVisibility(GONE);

        }
    }


    protected void burnSkin() {
//        mViewVoice.setBackgroundResource(R.mipmap.bg_chat_left_burn);
        mTvPlayTime.setTextColor(ContextCompat.getColor(getContext(), R.color.common_text_color));
        super.burnSkin();

    }

    protected void themeSkin() {
//        mViewVoice.setBackgroundResource(R.mipmap.bg_chat_left);
        mTvPlayTime.setTextColor(ContextCompat.getColor(getContext(), R.color.common_text_color));
        super.themeSkin();

    }

    @NonNull
    @Override
    protected View getContentRootView() {
        return mVoiceContent;
    }

    //显示正在翻译中的UI
    private void showTranslatingView() {
        showTranslateUI(false);
        ViewUtil.setVisible(mVoiceTranalateing, true);
    }

    //显示翻译成功的UI
    private void showTranslateUI(boolean visible) {
        ViewUtil.setVisible(mVoiceTranalateing, false);
        ViewUtil.setVisible(mChatLeftVoiceTranslateLayout, visible);
        ViewUtil.setVisible(mChatLeftVoiceTranslate, visible);
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
        if (AudioRecord.isPlaying(mVoiceChatMessage.currentPlayId)) {


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
