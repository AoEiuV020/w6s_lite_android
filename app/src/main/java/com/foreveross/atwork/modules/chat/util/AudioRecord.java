package com.foreveross.atwork.modules.chat.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.VoiceChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.VoiceMedia;
import com.foreveross.atwork.infrastructure.utils.AudioUtil;
import com.foreveross.atwork.infrastructure.utils.LogUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.encryption.EncryptCacheDisk;
import com.foreveross.atwork.infrastructure.utils.file.FileStreamHelper;
import com.foreveross.atwork.modules.bing.listener.VoicePlayListener;
import com.foreveross.atwork.modules.chat.fragment.ChatDetailFragment;
import com.foreveross.atwork.modules.chat.fragment.OnSensorChangedFragment;
import com.foreveross.atwork.modules.chat.inter.VoicePlayingListener;
import com.foreveross.atwork.modules.chat.model.PlayAudioInChatDetailViewParams;
import com.foreveross.atwork.utils.WakeLockUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by lingen on 15/4/9.
 * Description:
 * 录音辅助类
 */
public class AudioRecord {


    private static final int TIMEOUT = 55 * 1000;
    private static String sCurrentPlayId;
    //录音
    private static MediaRecorder sRecorder = null;
    private static String timeoutRandom;
    long beginRecord;
    long endRecord;
    private Handler mHandler = new Handler();
    //消息ID
    private String msgId;
    private RecordListener recordListener;
    //录音时错误的次数(音频收集为0,  音频文件不存在) 连续5次说明录音有问题, 极有可能没有权限
    private List<Double> mVoiceRecordList = new ArrayList<>();
    private List<Long> mAudioFileSizeRecordList = new ArrayList<>();

    private static ScheduledExecutorService mScheduledThreadPool = Executors.newScheduledThreadPool(2);
    private ScheduledFuture scheduledFuture;

    private String mAudioFileRecordingPath;
    private String mAudioFilePath;

    private static boolean sReplayMode = false;
    public static VoiceMedia sPlayingVoiceMedia;
    private static PowerManager.WakeLock sWakeLockHolding;

    private static HashMap<String, MediaPlayer> sMediaPlayerPlayingMap = new HashMap<>();

    public AudioRecord() {
        msgId = UUID.randomUUID().toString();
        mVoiceRecordList.clear();
        mAudioFileSizeRecordList.clear();
        initMediaRecord(VoiceChatMessage.getAudioRecordingPath(BaseApplicationLike.baseContext, msgId), VoiceChatMessage.getAudioPath(BaseApplicationLike.baseContext, msgId));
    }

    /**
     * 停止播放语音
     */
    public static void stopPlaying() {
        sCurrentPlayId = UUID.randomUUID().toString();
    }


    /**
     * 重新播放语音
     */
    public static void replay() {
        sReplayMode = true;
    }

    @SuppressLint("StaticFieldLeak")
    public static void playAudio(final Context context, final String voicePath, final VoicePlayingListener voicePlayingListener) {
        sCurrentPlayId = UUID.randomUUID().toString();
        new AsyncTask<String, Void, Void>() {
            @Override
            protected Void doInBackground(String... params) {
                boolean result = AudioPlayHelper.muteAudioFocus(BaseApplicationLike.baseContext, true);

                String path = EncryptCacheDisk.getInstance().getOriginalFilePathAndCheck(voicePath, false);

                if (result) {

                    commonStartPlay(context);

                    AudioPlayHelper.getInstance().useSavedAudioMode(context);

                    OnSensorChangedFragment.registerSensor();

                    if (null != voicePlayingListener) {
                        voicePlayingListener.playingAnimation();
                    }

                    MediaPlayer player = startPlayAudio(path, -1);

                    while (player.isPlaying()) {

                        String lastPlayId = params[0];

                        if (!sCurrentPlayId.equals(lastPlayId)) {
                            break;
                        }
                        try {
                            Thread.sleep(300l);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    playEndShortVoice(context, player, mp -> {
                        mp.reset();
                        mp.release();


                        if (null != voicePlayingListener) {
                            voicePlayingListener.stopPlayingAnimation();

                        }
                        commonEndPlay(context);

                    });



                }
                return null;
            }


        }.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, sCurrentPlayId);

    }

    /**
     * 播放声音
     *
     * @param voiceMedia
     */
    @SuppressLint("StaticFieldLeak")
    public static void playAudio(final Context context, final VoiceMedia voiceMedia, final VoicePlayListener voicePlayListener) {
        sCurrentPlayId = UUID.randomUUID().toString();
        new AsyncTask<String, Void, Void>() {
            @Override
            protected Void doInBackground(String... params) {
                boolean result = AudioPlayHelper.muteAudioFocus(BaseApplicationLike.baseContext, true);

                String path = AudioFileHelper.getVoiceFileOriginalPathSync(context, voiceMedia);

                if (result) {
                    AudioPlayHelper.getInstance().useSavedAudioMode(context);

                    startPlay(context, voiceMedia);

                    if (null != voicePlayListener) {
                        voicePlayListener.start();
                    }

                    MediaPlayer player = startPlayAudio(path, -1);

                    while (player.isPlaying()) {

                        String lastPlayId = params[0];

                        if (!sCurrentPlayId.equals(lastPlayId)) {
                            break;
                        }
                        try {
                            Thread.sleep(300l);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    playEndShortVoice(context, player, mp -> {
                        mp.reset();
                        mp.release();

                        if (null != voicePlayListener) {
                            voicePlayListener.stop(voiceMedia);

                        }

                        commonEndPlay(context);

                    });

                }
                return null;
            }


        }.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, sCurrentPlayId);

    }



    /**
     * 播放声音
     * @param playAudioInChatDetailViewParams
     */

    @SuppressLint("StaticFieldLeak")
    public static String playAudioInChatDetailView(PlayAudioInChatDetailViewParams playAudioInChatDetailViewParams) {
        VoicePlayingListener voicePlayingListener = playAudioInChatDetailViewParams.getVoicePlayingListener();
        int progress = playAudioInChatDetailViewParams.getSeekPosition();
        VoiceChatMessage voiceChatMessage = playAudioInChatDetailViewParams.getVoiceChatMessage();
        String currentPlayId = voiceChatMessage.currentPlayId;

        if (!StringUtils.isEmpty(currentPlayId)) {

            MediaPlayer mediaPlayerPlaying = sMediaPlayerPlayingMap.get(currentPlayId);

            if(null != mediaPlayerPlaying && !mediaPlayerPlaying.isPlaying()) {
                mediaPlayerPlaying.seekTo(progress);
                mediaPlayerPlaying.start();
                if (null != voicePlayingListener) {
                    voicePlayingListener.playingAnimation();
                }
                return currentPlayId;
            }
        }


        currentPlayId = UUID.randomUUID().toString();
        AudioRecord.sCurrentPlayId = currentPlayId;
        Context context = playAudioInChatDetailViewParams.getContext();
        boolean needTryPlayNext = playAudioInChatDetailViewParams.getNeedTryPlayNext();

        String finalCurrentPlayId = currentPlayId;
        new AsyncTask<String, Void, Void>() {
            @Override
            protected Void doInBackground(String... params) {
                //是否中断播放
                boolean playingBreak = false;

                boolean result = AudioPlayHelper.muteAudioFocus(BaseApplicationLike.baseContext, true);



                String path = AudioFileHelper.getVoiceFileOriginalPathSync(context, voiceChatMessage);

                if (result) {
                    if (!playAudioInChatDetailViewParams.isInSuccession()) {
                        AudioPlayHelper.getInstance().useSavedAudioMode(context);
                    }


                    startPlay(context, voiceChatMessage);

                    if (null != voicePlayingListener) {
                        voicePlayingListener.playingAnimation();
                    }

                    MediaPlayer player = startPlayAudio(path, progress);
                    AtomicBoolean isEnd = new AtomicBoolean(false);
                    player.setOnCompletionListener(mediaPlayer -> {
                        isEnd.set(true);
                    });

                    sMediaPlayerPlayingMap.put(finalCurrentPlayId, player);

                    while (sReplayMode || player.isPlaying() || !isEnd.get()) {

                        player = checkReplay(path, player);


                        String lastPlayId = params[0];

                        if (!AudioRecord.sCurrentPlayId.equals(lastPlayId)) {
                            playingBreak = true;
                            break;
                        }
                        try {
                            Thread.sleep(300l);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }


                    if(!playingBreak) {

                        playEndShortVoice(context, player, mp -> {
                            releaseAndClean(mp);


                            if (voiceChatMessage.isFavPlay && voicePlayingListener != null) {
                                voicePlayingListener.stopPlayingAnimation();
                            }


                            stopPlayingAnimation(false);

                            commonEndPlay(context);

                            if (needTryPlayNext) {
                                playNext(voiceChatMessage);
                            }


                        });

                    } else {
                        releaseAndClean(player);

                        stopPlayingAnimation(true);

                        commonEndPlay(context);

                    }



                } else {
                    voiceChatMessage.playing = false;

                }
                return null;
            }

            private void releaseAndClean(MediaPlayer mp) {
                release(mp);
                sMediaPlayerPlayingMap.remove(finalCurrentPlayId);
                voiceChatMessage.currentPlayId = StringUtils.EMPTY;
            }

            private void release(MediaPlayer mp) {
                mp.reset();
                mp.release();

                voiceChatMessage.playing = false;
            }


            private MediaPlayer checkReplay(String path, MediaPlayer player) {
                if(sReplayMode) {
                    player.stop();
                    player.release();

                    try {
                        Thread.sleep(500L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    player = startPlayAudio(path, -1);

                    sMediaPlayerPlayingMap.put(finalCurrentPlayId, player);

                    sReplayMode = false;
                }
                return player;
            }



            private void playNext(VoiceChatMessage voiceChatMessage) {
                Intent intent = new Intent(ChatDetailFragment.PLAYING_NEXT_VOICE);
                intent.putExtra(ChatDetailFragment.STOP_PLAYING_MSG_ID, voiceChatMessage);
                LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).sendBroadcast(intent);
            }

            private void stopPlayingAnimation(boolean playingBreak) {
                ChatDetailExposeBroadcastSender.refreshMessageListViewUI();

                if (playingBreak) {
                    endPlay();
                }
            }


        }.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, AudioRecord.sCurrentPlayId);

        return currentPlayId;
    }



    private static void commonStartPlay(Context context) {
        sWakeLockHolding = WakeLockUtil.lock(context, sWakeLockHolding);

    }


    private static void commonEndPlay(Context context) {
        WakeLockUtil.unlock(sWakeLockHolding);
        sWakeLockHolding = null;
    }


    private static void endPlay() {
        OnSensorChangedFragment.unregisterSensor();
    }

    private static void startPlay(Context context, VoiceMedia voiceMedia) {
        sPlayingVoiceMedia = voiceMedia;

        commonStartPlay(context);

        OnSensorChangedFragment.registerSensor();
    }

    private static void playEndShortVoice(Context context, MediaPlayer player, OnEndShortVoicePlayListener onEndShortVoicePlayListener) {
        try {
            AssetFileDescriptor file = context.getResources().openRawResourceFd(
                    R.raw.playend);

            if (player.isPlaying()) {
                player.stop();
            }
            player.reset();
            player.setDataSource(file.getFileDescriptor(),
                    file.getStartOffset(), file.getLength());
            player.prepare();
            player.start();
            player.setOnCompletionListener(onEndShortVoicePlayListener::onPlayFinish);



        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean isAnyPlaying() {
        for(MediaPlayer mediaPlayer : sMediaPlayerPlayingMap.values()) {
            if(null != mediaPlayer && mediaPlayer.isPlaying()) {
                return true;
            }
        }

        return false;
    }

    public static boolean isPlaying(String playId) {
        try {
            if(null != sMediaPlayerPlayingMap.get(playId)) {
                return sMediaPlayerPlayingMap.get(playId).isPlaying();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }


    public static int getPlayingProgress(String playId) {
        try {
            if(null != sMediaPlayerPlayingMap.get(playId)) {
                return sMediaPlayerPlayingMap.get(playId).getCurrentPosition();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }


    public static void pause(String playId, VoicePlayingListener voicePlayingListener) {
        if(null != sMediaPlayerPlayingMap.get(playId)){
            sMediaPlayerPlayingMap.get(playId).pause();

            if (null != voicePlayingListener) {
                voicePlayingListener.stopPlayingAnimation();
            }
        }
    }




    @NonNull
    private static MediaPlayer startPlayAudio(String path, int progress) {
        MediaPlayer player = new MediaPlayer();

        // 使用设置MediaPlayer的方式设置音频流，不影响系统的全局设置

        int streamType;
        if (AudioUtil.isSpeakModel(BaseApplicationLike.baseContext)) {
            streamType = AudioManager.STREAM_MUSIC;

        } else {
            streamType = AudioManager.STREAM_VOICE_CALL;

        }

        player.setAudioStreamType(streamType);
        try {
            if (player.isPlaying()) {
                player.stop();
            }
            player.reset();
            player.setDataSource(Uri.fromFile(new File(path)).getPath());
            player.prepare();
            if (-1 != progress) {
                player.seekTo(progress);
            }
            player.start();
        } catch (Exception e) {
            Log.e("error!", e.getMessage(), e);
        }
        return player;
    }



    private static void pauseMusic(Context context) {
        Intent freshIntent = new Intent();
        freshIntent.setAction("com.android.music.musicservicecommand.pause");
        freshIntent.putExtra("command", "pause");
        context.sendBroadcast(freshIntent);
    }

    /**
     * 删除录音文件。取消发送，录音时间太短，录音失败会执行
     */
    private void deleteAudioFile() {
        File file = new File(mAudioFileRecordingPath);
        if (file != null && file.exists()) {
            LogUtil.e("Audio", "temp file delete");
            file.delete();
        }
    }

    public synchronized static double getAmplitude() {
        if (sRecorder != null) {
            //获取在前一次调用此方法之后录音中出现的最大振幅
            int maxAmplitude;
            try {
                maxAmplitude = sRecorder.getMaxAmplitude();
//                LogUtil.e("record" + "amplitude -> " + maxAmplitude);
                return (maxAmplitude / 2700.0);
            } catch (Exception e) {
                return -1;
            }
        } else {
            return -1;
        }
    }

    private String getOutputFilePath(Context context) {
        return VoiceChatMessage.getAudioPath(context, msgId);
    }

    private void initMediaRecord(String audioFileRecording, String audioFile) {
        mAudioFileRecordingPath = audioFileRecording;
        mAudioFilePath = audioFile;
        sRecorder = new MediaRecorder();
        sRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        sRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
        sRecorder.setOutputFile(audioFileRecording);
        sRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        sRecorder.setAudioSamplingRate(8000);
    }

    public void startRecord() {

        if (sRecorder == null) {
            return;
        }
        beginRecord = System.currentTimeMillis();
        try {
            LogUtil.e("Audio", "prepare");
            sRecorder.prepare();
            LogUtil.e("Audio", "start");
            sRecorder.start();
        } catch (RuntimeException | IOException e) {
            if (sRecorder != null) {
                sRecorder.reset();
                sRecorder.release();
                sRecorder = null;
            }
        }
        timeoutRandom = UUID.randomUUID().toString();
        //超时检测
        new TimeoutCheck(recordListener, timeoutRandom).start();

        //权限检测
        try {
            authCheckRecord();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void releaseRecord() {
        cancelRecord();
    }

    /**
     * 修改为在主线程取消，多线程环境下执行native release会更容易crash
     */
    public void cancelRecord() {

        deleteAudioFile();
        cancelAuthCheckSchedule();
        if (sRecorder == null) {
            return;
        }
        try {
            sRecorder.stop();
            sRecorder.release();
            sRecorder = null;
        } catch (Exception e) {
            if (sRecorder != null) {
                sRecorder.release();
                sRecorder = null;
            }
        }
    }

    public void stopRecord() {
        if (sRecorder == null) {
            return;
        }
        cancelAuthCheckSchedule();

        sRecorder.setOnErrorListener(null);
        try {
            sRecorder.stop();
            sRecorder.release();
            sRecorder = null;

        } catch (Exception e) {
            if (sRecorder != null) {
                sRecorder.release();
                sRecorder = null;
            }
        } finally {
            //rename
            FileStreamHelper.rewrite(mAudioFileRecordingPath, mAudioFilePath);
        }


        endRecord = System.currentTimeMillis();
        long playTimeLong = endRecord - beginRecord;
        int playtime = (int) (playTimeLong / 1000);

        if (playtime < 1) {
            deleteAudioFile();
            recordListener.tooShort();
        } else {
            if (playtime > 60) {
                playtime = 60;
            }
            //连续5次错误则判断为录音功能有问题
            if (!recordHasError(mAudioFilePath)) {
                recordListener.recordFinished(msgId, playtime);
            }
        }

    }

    private void authCheckRecord() {
        if (!mScheduledThreadPool.isShutdown()) {
            scheduledFuture = mScheduledThreadPool.scheduleAtFixedRate(new Runnable() {
                int durationFly = 0;

                @Override
                public void run() {
                    durationFly += 100;

                    //连续为0的时候才判断为录音存在问题
                    double maxAmplitude = getAmplitude();

                    File file = new File(mAudioFileRecordingPath);
                    LogUtil.e("record", "record  file size -> " + file.length());

                    mVoiceRecordList.add(maxAmplitude);
                    mAudioFileSizeRecordList.add(file.length());

                    if (1000 <= durationFly) {
                        if (recordHasError(mAudioFileRecordingPath)) {

//                            deleteAudioFile();
                            recordListener.recordFail();
                        }
                        cancelAuthCheckSchedule();
                    }

                }
            }, 100, 100, TimeUnit.MILLISECONDS);
        }

    }

    private boolean recordHasError(String audioPath) {
        boolean hasError = true;
        File file = new File(audioPath);

        if (!file.exists() || 0 == file.length()) {
            hasError = true;


        } else {

            for (Double maxAmplitude : mVoiceRecordList) {
                if (0 != maxAmplitude) {
                    hasError = false;
                    break;
                }
            }
            //若已经判断为录音有问题, 则直接返回
            if (hasError) {
                return hasError;
            }

            long lastFileSize = 0;
            //假设回 true
            hasError = true;
            for (Long fileSize : mAudioFileSizeRecordList) {
                if (0 == fileSize || 0 == lastFileSize) {
                    lastFileSize = fileSize;
                    continue;
                } else {

                    if (lastFileSize != fileSize) {
                        hasError = false;

                        return hasError;
                    }
                }
            }

        }
        return hasError;
    }

    public void cancelAuthCheckSchedule() {
        if (null != scheduledFuture) {
            scheduledFuture.cancel(true);
        }
    }

    public void setRecordListener(RecordListener recordListener) {
        this.recordListener = recordListener;
    }

    public interface RecordListener {
        /**
         * 录音超时
         */
        void timeout();

        /**
         * 录音结束
         *
         * @param msgId    录音ID
         * @param playtime 录音时长
         */
        void recordFinished(String msgId, int playtime);

        /**
         * 录音时间过短
         */
        void tooShort();

        void recordFail();
    }

    public class TimeoutCheck extends Thread {

        private RecordListener recordTimeoutListener;

        private String timeoutKey;

        public TimeoutCheck(RecordListener recordTimeoutListener, String timeoutRandom) {
            this.recordTimeoutListener = recordTimeoutListener;
            this.timeoutKey = timeoutRandom;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(TIMEOUT);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (sRecorder == null) {
                return;
            }

            if (!timeoutKey.equals(timeoutRandom)) {
                return;
            }

            if (recordTimeoutListener != null) {
                recordTimeoutListener.timeout();
            }
            try {
                Thread.sleep(5 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (sRecorder == null) {
                return;
            }
            stopRecord();
        }
    }

    interface OnEndShortVoicePlayListener {
        void onPlayFinish(MediaPlayer mp);
    }
}
