package com.foreveross.atwork.modules.chat.fragment;

import android.os.Bundle;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.foreverht.cache.MessageCache;
import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.TextChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.VoiceChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.event.UndoEventMessage;
import com.foreveross.atwork.infrastructure.utils.DensityUtil;
import com.foreveross.atwork.infrastructure.utils.FileUtil;
import com.foreveross.atwork.infrastructure.utils.LogUtil;
import com.foreveross.atwork.infrastructure.utils.ScreenUtils;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.ViewUtil;
import com.foreveross.atwork.infrastructure.utils.language.LanguageUtil;
import com.foreveross.atwork.modules.chat.activity.MsgContentDetailActivity;
import com.foreveross.atwork.modules.chat.dao.ChatDaoService;
import com.foreveross.atwork.modules.chat.util.AudioFileHelper;
import com.foreveross.atwork.modules.chat.util.AutoLinkHelper;
import com.foreveross.atwork.modules.chat.util.TextMsgHelper;
import com.foreveross.atwork.support.BackHandledFragment;
import com.foreveross.atwork.utils.ChatMessageHelper;
import com.foreveross.atwork.utils.watermark.WaterMarkHelper;
import com.foreveross.xunfei.JsonParser;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;

import io.kvh.media.amr.AmrFileDecoder;

/**
 * Created by dasunsy on 2017/5/31.
 */

public class MsgContentDetailFragment extends BackHandledFragment {

    public static final String TAG = MsgContentDetailFragment.class.getName();

    private View mRlRoot;
    private View mVWaterCover;
    private TextView mTvResult;
    private ScrollView mSlResult;
    private ProgressBar mPbTranslating;
    private TextView mTvCancel;
    private ImageView mIvTranslateFailed;
    private TextView mTvTranslateTip;
    private View mLlSwitchLanguage;
    private TextView mTvSwitchTip;
    private TextView mTvSwitchLanguage;
    private FrameLayout mFlSwitchLanguage;

    // 语音听写对象
    private SpeechRecognizer mIat;
    private ChatPostMessage mChatMessage;

    private String mVoiceResult = StringUtils.EMPTY;

    private boolean mIsVoiceTranslating = false;
    private String mVoiceTranslateTarget = "zh_ch";

    /**
     * 初始化监听器。
     */
    private InitListener mInitListener = code -> {
        LogUtil.e(TAG, "SpeechRecognizer init() code = " + code);

        if (code != ErrorCode.SUCCESS) {

        }
    };

    /**
     * 听写监听器。
     */
    private RecognizerListener mRecognizerListener = new RecognizerListener() {

        @Override
        public void onBeginOfSpeech() {
            // 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入

            LogUtil.e(TAG, "onBeginOfSpeech");
        }

        @Override
        public void onError(SpeechError error) {
            // Tips：
            // 错误码：10118(您没有说话)，可能是录音机权限被禁，需要提示用户打开应用的录音权限。
            // 如果使用本地功能（语记）需要提示用户开启语记的录音权限。

            LogUtil.e(TAG, "error -> " + error.getErrorDescription() + "  error_code -> " + error.getErrorCode());

            showFailedUI();
            mIsVoiceTranslating = false;

        }

        @Override
        public void onEndOfSpeech() {
            // 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
            LogUtil.e(TAG, "onEndOfSpeech");
        }

        @Override
        public void onResult(RecognizerResult results, boolean isLast) {
            LogUtil.e(TAG, "onResult ->" + results.getResultString());

            String result = JsonParser.parseIatResult(results.getResultString());

            LogUtil.e(TAG, "onResult text ->" + result);

            mVoiceResult += result;

            if (isLast) {
                mIsVoiceTranslating = false;

                if (!StringUtils.isEmpty(mVoiceResult)) {
                    updateVoiceMessage();
                    showTranslatedResult(mVoiceResult);

                } else {
                    showFailedUI();

                }
            }
        }

        @Override
        public void onVolumeChanged(int volume, byte[] data) {
            LogUtil.e(TAG, "返回音频数据：" + data.length);

        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            // 若使用本地能力，会话id为null
            //	if (SpeechEvent.EVENT_SESSION_ID == eventType) {
            //		String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
            //		LogUtil.e(TAG, "session id =" + sid);
            //	}
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_msg_content_detail, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        registerListener();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initData();

        handleResultText();

        handleWatermark();

    }

    private void updateVoiceMessage() {
        ((VoiceChatMessage) mChatMessage).setTranslatedResult(mVoiceResult, mVoiceTranslateTarget);

        ChatPostMessage msgCache = MessageCache.getInstance().queryMessage(mChatMessage);
        if (null != msgCache) {
            ((VoiceChatMessage) msgCache).setTranslatedResult(mVoiceResult, mVoiceTranslateTarget);
        }
        ChatDaoService.getInstance().insertOrUpdateMessage(BaseApplicationLike.baseContext, mChatMessage);
    }


    @Override
    protected void findViews(View view) {
        mRlRoot = view.findViewById(R.id.rl_root);
        mVWaterCover = view.findViewById(R.id.v_watermark_bg);
        mSlResult = view.findViewById(R.id.sl_result);
        mTvResult = view.findViewById(R.id.tv_result);
        mPbTranslating = view.findViewById(R.id.pb_loading);
        mTvCancel = view.findViewById(R.id.tv_cancel);
        mIvTranslateFailed = view.findViewById(R.id.iv_translate_failed);
        mTvTranslateTip = view.findViewById(R.id.tv_translate_tip);
        mLlSwitchLanguage = view.findViewById(R.id.ll_switch_language);
        mTvSwitchTip = view.findViewById(R.id.tv_switch_tip);
        mTvSwitchLanguage = view.findViewById(R.id.tv_switch_language);
        mFlSwitchLanguage = view.findViewById(R.id.fl_switch_language);


        mTvSwitchTip.setMaxWidth(ScreenUtils.getScreenWidth(getActivity()) - ViewUtil.getTextLength(mTvSwitchLanguage) - DensityUtil.dip2px(12 * 2 + 6 * 2 + 15 + 5) - 30);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(null != mIat) {
            mIat.destroy();
            mIat = null;
        }
    }

    @Override
    public void onUndoMsgReceive(UndoEventMessage undoEventMessage) {
        if (undoEventMessage.isMsgUndo(mChatMessage.deliveryId)) {
            showUndoDialog(getActivity(), undoEventMessage);
        }
    }

    //如果
    private void handleResultText() {
        if (isVoiceTranslateMode()) {
            VoiceChatMessage voiceChatMessage = (VoiceChatMessage) mChatMessage;
            if (voiceChatMessage.hasTranslatedBefore()) {

                showTranslatedResult(voiceChatMessage.getTranslatedResult());

                adjustResultTv();

            } else {
                if (LanguageUtil.isZhLocal(getActivity())) {
                    mVoiceTranslateTarget = "zh_cn";

                } else {
                    mVoiceTranslateTarget = "en_us";
                }

                startVoiceTranslate();

            }

        } else {

            TextChatMessage textChatMessage = (TextChatMessage) mChatMessage;
            SpannableString spannableString = AutoLinkHelper.getInstance().getSpannableString(getContext(), "", null, mTvResult, TextMsgHelper.getVisibleText(textChatMessage));
            mTvResult.setText(spannableString);
            adjustResultTv();

        }
    }

    private void startVoiceTranslate() {
        mIsVoiceTranslating = true;
        mVoiceResult = StringUtils.EMPTY;

        showTranslatingUI();
        doXunfeiVoiceRecognize();
    }

    private void handleWatermark() {
        String sessionId = ChatMessageHelper.getChatUser(mChatMessage).mUserId;
        WaterMarkHelper.setWatermark(getActivity(), mVWaterCover, sessionId);
    }

    private void adjustResultTv() {
        mTvResult.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {

                int measuredHeight = mTvResult.getMeasuredHeight();
                LogUtil.e("textview measure height -> " + measuredHeight);
                int screenHeight = ScreenUtils.getScreenHeight(BaseApplicationLike.baseContext);
                int normalPaddingTop = DensityUtil.dip2px(130);

                if (0 > (screenHeight - measuredHeight - normalPaddingTop)) {
                    int gap = DensityUtil.dip2px(15);
                    mTvResult.setPadding(gap, gap, gap, 0);

                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mSlResult.getLayoutParams();
                    layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
                    mSlResult.setLayoutParams(layoutParams);
                }
                mTvResult.getViewTreeObserver().removeOnPreDrawListener(this);
                return false;
            }
        });
    }

    private void doXunfeiVoiceRecognize() {
        mIat = SpeechRecognizer.createRecognizer(getActivity(), mInitListener);
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        // 根据文档 10000为最大的间隔
        mIat.setParameter(SpeechConstant.VAD_EOS, "10000");
        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        // 根据文档 10000为最大的间隔
        mIat.setParameter(SpeechConstant.VAD_BOS, "10000");
        //禁止静音抑制
//        mIat.setParameter(SpeechConstant.VAD_ENABLE, "0");


        mIat.setParameter(SpeechConstant.AUDIO_SOURCE, "-1");
        mIat.setParameter(SpeechConstant.SAMPLE_RATE, "8000");

        mIat.setParameter(SpeechConstant.LANGUAGE, mVoiceTranslateTarget);


        int ret = mIat.startListening(mRecognizerListener);

        if (ErrorCode.SUCCESS == ret) {

            AudioFileHelper.getVoiceFileOriginalPath(getActivity(), (VoiceChatMessage) mChatMessage, path -> {
                if (FileUtil.isExist(path)) {
                    AmrFileDecoder amrFileDecoder = new AmrFileDecoder();

                    byte[] armResult = amrFileDecoder.amr2Pcm(path);
                    LogUtil.e("armResult -> " + armResult.length);

                    mIat.writeAudio(armResult, 0, armResult.length);

                } else {
                    showFailedUI();
                    mIsVoiceTranslating = false;
                }

                mIat.stopListening();
            });


        } else {
            LogUtil.e("识别失败,错误码：" + ret);

        }
    }



    private void initData() {
        Bundle arguments = getArguments();
        mChatMessage = (ChatPostMessage) arguments.getSerializable(MsgContentDetailActivity.DATA_MESSAGE);

    }

    private void showTranslatedResult(String result) {
        mTvResult.setText(result);
        VoiceChatMessage voiceChatMessage = (VoiceChatMessage) mChatMessage;
        if (voiceChatMessage.mVoiceTranslateStatus.isMandarin()) {
            mVoiceTranslateTarget = "en_us";
        } else {
            mVoiceTranslateTarget = "zh_cn";

        }
        if (voiceChatMessage.mVoiceTranslateStatus.isMandarin()) {
            mTvSwitchTip.setText(getStrings(R.string.voice_translate_language_switch_tip, getStrings(R.string.voice_translate_mandarin), getStrings(R.string.voice_translate_english)));

        } else {
            mTvSwitchTip.setText(getStrings(R.string.voice_translate_language_switch_tip, getStrings(R.string.voice_translate_english), getStrings(R.string.voice_translate_mandarin)));
        }

        mTvResult.setVisibility(View.VISIBLE);
        mLlSwitchLanguage.setVisibility(View.VISIBLE);

        mPbTranslating.setVisibility(View.GONE);
        mTvTranslateTip.setVisibility(View.GONE);
        mTvCancel.setVisibility(View.GONE);
        mIvTranslateFailed.setVisibility(View.GONE);


    }

    private void showTranslatingUI() {

        mTvTranslateTip.setText(R.string.voice_translating);

        mPbTranslating.setVisibility(View.VISIBLE);
        mTvTranslateTip.setVisibility(View.VISIBLE);
        mTvCancel.setVisibility(View.VISIBLE);

        mTvResult.setVisibility(View.GONE);
        mIvTranslateFailed.setVisibility(View.GONE);
        mLlSwitchLanguage.setVisibility(View.GONE);


    }

    private void showFailedUI() {
        mTvTranslateTip.setText(R.string.voice_translate_failed);


        mIvTranslateFailed.setVisibility(View.VISIBLE);
        mTvTranslateTip.setVisibility(View.VISIBLE);

        mPbTranslating.setVisibility(View.GONE);
        mTvCancel.setVisibility(View.GONE);
        mTvResult.setVisibility(View.GONE);


    }

    private void registerListener() {
        mTvCancel.setOnClickListener(v -> finish());

        mRlRoot.setOnClickListener(v -> {
            if (!mIsVoiceTranslating) {
                finish();
            }
        });

        mTvResult.setOnClickListener(v -> {
            if (!mIsVoiceTranslating) {
                finish();
            }
        });

        mTvSwitchLanguage.setOnClickListener(v -> startVoiceTranslate());
        mFlSwitchLanguage.setOnClickListener(v -> startVoiceTranslate());

    }


    private boolean isVoiceTranslateMode() {
        return mChatMessage instanceof VoiceChatMessage;
    }

    @Override
    protected boolean onBackPressed() {
        finish();
        return false;
    }
}
