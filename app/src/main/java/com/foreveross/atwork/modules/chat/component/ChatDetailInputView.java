package com.foreveross.atwork.modules.chat.component;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.foreveross.atwork.AtworkApplication;
import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.file.SelectMediaType;
import com.foreveross.atwork.infrastructure.permissions.PermissionsManager;
import com.foreveross.atwork.infrastructure.permissions.PermissionsResultAction;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.infrastructure.utils.LogUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.TimeUtil;
import com.foreveross.atwork.listener.TextWatcherAdapter;
import com.foreveross.atwork.modules.chat.fragment.ChatDetailFragment;
import com.foreveross.atwork.modules.chat.inter.ChatDetailInputListener;
import com.foreveross.atwork.modules.chat.util.BurnModeHelper;
import com.foreveross.atwork.modules.chat.util.LinkTranslatingHelper;
import com.foreveross.atwork.modules.image.activity.MediaSelectActivity;
import com.foreveross.atwork.modules.voip.utils.VoipHelper;
import com.foreveross.atwork.support.BackHandledFragment;
import com.foreveross.atwork.utils.AtworkToast;
import com.foreveross.atwork.utils.AtworkUtil;
import com.foreveross.atwork.utils.IntentUtil;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * Created by lingen on 15/3/26.
 * Description:
 */
public class ChatDetailInputView extends RelativeLayout {

    private static final String TAG = ChatDetailInputView.class.getSimpleName();

    private Fragment mFragment;

    private ImageView mIvMore;

    private TextView mTvSend;

    private ImageView mIvVoiceKeyboardModeSwitch;

    private View mLlLeftConnerLayout;
    private ImageView mIvLeftConner;

    private EditText mEtInput;

    private TextView mBtnVoice;

    private RelativeLayout mRlRoot;

    private ImageView mIvFaceKeyBoardModeSwitch;

    private ChatInputType mChatInputType;

    private ChatDetailInputListener mChatDetailInputListener;

    private ToServiceModeListener mToServiceModeListener;

    private ImageView mIvServiceKeyboard;

    private LinkTranslatingHelper.LinkMatchListener mLinkMatchListener;

    private View mLineView;

    private boolean mCancel = false;

    //    private long lastRecordTime = -1;
    public static long mLastEndTime;

    private ScheduledExecutorService mCheckTouchThreadPool = Executors.newScheduledThreadPool(2);
    private ScheduledFuture scheduledFuture;

    private int mLastCollectedPositionSize = 0;
    private List<Float> mWatchingPositionList = new ArrayList<>();
    private List<Float> mCollectedPositionList = new ArrayList<>();

    private int mSwitchIconStatus = 0;  //0 代表 emoji 1代表键盘
    private int mMoreIconStatus = 0;   //0 表示 “+”  1表示叉
    private Boolean mIsBurn = false;    //true 表示阅后即焚

    /**
     * 判断是否是从选择@人员界面返回
     * 默认为否 就是正常情况下
     */
    public boolean isBackForAT = false;

    /**
     * 当前编辑框的光标
     */
    private int currentIndex = 0;

    /**
     * 多余的@字符的光标
     */
    private int deleteIndex = 0;

    /**
     * 判断是否有多个@人名称
     */
    public boolean isMore = false;

    /**
     * 判断是否是长按头像获得的@人名称
     */
    public boolean isAppendAtMembersDirectly = false;

    private boolean isFirstInsert = true;

    private int nextIndex = 0;

    /**
     * 语音初始的高度
     */
    private int mInputViewInitialHeight = 0;


    private boolean mFirst = true;
    private boolean mTimeIntervalTooShort = false;

    private Context mContext;

    public ChatDetailInputView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        findView();
        registerListener();
        setClickable(true);
        textMode(false);
    }


    /**
     * 实现编辑框根据光标进行字符串添加
     * 删除从选择@人员界面返回后的多余@字符
     */
    public void appendText(SpannableString stringText) {
        //获得当前编辑框内容的引用， 能使Spannable保持其当前状态，因此修改它会调用onTextChange()
        Spannable s = mEtInput.getText();
        //获得删除多余@字符的索引
        if (deleteIndex != -1) {
            deleteIndex = currentIndex + stringText.toString().length();
        }
        //使用SpannableStringBuilder操作编辑框里SpannableString，使其不失去相应的状态
        SpannableStringBuilder tempString = new SpannableStringBuilder();
        tempString.append(s);

        //如果是长按头像获得的 不需要删除多余的@字符
        if (isAppendAtMembersDirectly) {
            int index = mEtInput.getSelectionStart();
            //插入@人名称
            tempString.insert(index, stringText);

            //如果是有多个@人名称
        } else if (isMore) {
            //如果是第一次插入
            if (isFirstInsert) {
                tempString.insert(currentIndex, stringText);
                //获得下一个@人名称的插入索引
                nextIndex = currentIndex + stringText.toString().length();
                isFirstInsert = false;
            } else {
                tempString.insert(nextIndex, stringText);
                nextIndex = nextIndex + stringText.toString().length();
            }

            //只需要第一个@人名称需要删除多余的@字符
            if (deleteIndex != -1) {
                tempString.delete(deleteIndex, deleteIndex + 1);
                deleteIndex = -1;
            }
        } else {
            //插入@人名称
            tempString.insert(currentIndex, stringText);
            //删除多余的@
            tempString.delete(deleteIndex, deleteIndex + 1);
        }

        mEtInput.setText(tempString);

    }

    /**
     * 重置数据
     */
    public void clearData() {
        isMore = false;
        isFirstInsert = true;
        deleteIndex = 0;
        nextIndex = 0;
        isBackForAT = false;
        isAppendAtMembersDirectly = false;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);


        if (mFirst) {
            //检测初始化的inputView的高度
            mInputViewInitialHeight = getHeight();
            mFirst = false;
        }


    }

    public void setFragment(BackHandledFragment fragment) {
        this.mFragment = fragment;
    }

    public ImageView getIvVoiceKeyboardModeSwitch() {
        return mIvVoiceKeyboardModeSwitch;
    }

    public EditText getEmojiIconEditText() {
        return mEtInput;
    }

    public void hideAll() {
        mRlRoot.setVisibility(INVISIBLE);
    }



    public void hiddenServiceKeyborad() {
        mIvServiceKeyboard.setVisibility(GONE);
    }

    private void registerListener() {

        mIvServiceKeyboard.setOnClickListener(v -> {
            showMoreImage();
            if (mToServiceModeListener != null) {
                mToServiceModeListener.toServiceMenu();
            }
        });

        //监听输入，有输入右边变成发送
        mEtInput.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                super.beforeTextChanged(s, start, count, after);
                if (start != 0) {
                    currentIndex = start;
                }
            }


            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                refreshSendBtnStatus(s.toString());

                LinkTranslatingHelper.handleLinkMatch(mContext, s.toString(), mLinkMatchListener);
                handleAtInput(s, start, count);

                if (null != mChatDetailInputListener) {
                    mChatDetailInputListener.onTyping();
                }

            }


            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                // 当编辑框为空时 重置光标索引
                if (null != mChatDetailInputListener && s.toString().equals("")) {
                    mChatDetailInputListener.onEditTextEmpty();
                    currentIndex = 0;
                }
            }


        });

        mTvSend.setOnClickListener(v -> {
            if(forcedShowSend()) {
                mChatDetailInputListener.handleForcedSend();
                return;
            }


            String inputVal = mEtInput.getText().toString();
            if (inputVal != null && !inputVal.trim().equals("")) {
                if (mChatDetailInputListener != null) {
                    mChatDetailInputListener.sendText(inputVal);
                }
            }
        });


        mEtInput.setOnClickListener(v -> {
//                mChatDetailInputListener.moreFunctionHidden();
            showMoreImage();
            mChatDetailInputListener.inputShow();
        });


        mEtInput.setOnKeyListener((v, keyCode, event) -> {
//                String textValue = etInput.getText().toString();

//                //捕获删除事件,识别@字符
//                if (keyCode == KeyEvent.KEYCODE_DEL) {
//                    //如果是@
//                    if (isAtValue(textValue)) {
//                        int atIndex = textValue.lastIndexOf("@");
//                        etInput.setText(textValue.substring(0, atIndex));
//                        etInput.setSelection(etInput.getText().toString().length());
//                        return true;
//                    }
//                }
            return false;
        });

        mEtInput.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                int location = mEtInput.getSelectionEnd();
                Log.d("location", "" + location);
            }
        });

        mIvVoiceKeyboardModeSwitch.setOnClickListener(v -> {

            if(mChatInputType == ChatInputType.Text) {
                PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(mFragment, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, new PermissionsResultAction() {
                    @Override
                    public void onGranted() {
                        PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(mFragment, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, new PermissionsResultAction() {
                            @Override
                            public void onGranted() {
                                PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(mFragment, new String[]{Manifest.permission.RECORD_AUDIO}, new PermissionsResultAction() {
                                    @Override
                                    public void onGranted() {

                                        showMoreImage();
                                        voiceMode();


                                        if (null != mChatDetailInputListener) {
                                            mChatDetailInputListener.voiceShow();
                                        }

                                    }

                                    @Override
                                    public void onDenied(String permission) {
                                        AtworkUtil.popAuthSettingAlert(getContext(), permission);
                                    }
                                });
                            }

                            @Override
                            public void onDenied(String permission) {
                                AtworkUtil.popAuthSettingAlert(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
                            }
                        });
                    }

                    @Override
                    public void onDenied(String permission) {
                        AtworkUtil.popAuthSettingAlert(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    }
                });




            } else if(mChatInputType == ChatInputType.Voice) {

                textMode(true);
            }




        });

        //阅后即焚模式
        mIvLeftConner.setOnClickListener(v -> {
            if (null != mChatDetailInputListener) {
                mChatDetailInputListener.clickLeftConnerFunView();
            }
        });

        //打开更多功能窗口
        mIvMore.setOnClickListener(v -> {
            if (null != mChatDetailInputListener) {
                mChatDetailInputListener.moreFunctionShow();
                //当"更多"的面板弹出动画快结束时, 再把输入模式切换过来
                mIvMore.postDelayed(() -> textMode(false), 190);
            }
        });

        mIvFaceKeyBoardModeSwitch.setOnClickListener(v -> {
            showMoreImage();
            if (null != mChatDetailInputListener) {
                if (0 == mSwitchIconStatus) {
                    textMode(false);
                    mChatDetailInputListener.emoticonsShow();

                    showIvSwitchKeyboard();
                } else {
//                        mChatDetailInputListener.moreFunctionHidden();
                    mChatDetailInputListener.inputShow();
                }
            }
        });

        //录音功能
        mBtnVoice.setOnTouchListener((v, event) -> {
            mCollectedPositionList.add(event.getY());

            //按下录音键
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                mTimeIntervalTooShort = false;

                LogUtil.e("Audio", "action down");

                long current = TimeUtil.getCurrentTimeInMillis();
                LogUtil.e("Audio", "endTime ::" + (current - mLastEndTime));

                if (VoipHelper.isHandlingVoipCall()) {
                    AtworkToast.showResToast(R.string.alert_is_handling_voip_meeting_click_voip);
                    return true;
                }


                if (current - mLastEndTime < 400) {

                    LogUtil.e("Audio", "time interval down ");
                    mTimeIntervalTooShort = true;
                    return true;
                }


                v.setBackgroundDrawable(getResources().getDrawable(R.drawable.shape_chat_send_bg_press));

//                lastRecordTime = current;
                if (mChatDetailInputListener != null) {

                    mChatDetailInputListener.record();

                    checkTouchFunction();

                }
            }

            //松开录音键
            if (event.getAction() == MotionEvent.ACTION_UP) {


                mLastEndTime = TimeUtil.getCurrentTimeInMillis();
                if(mIsBurn){
                    v.setBackgroundDrawable(getResources().getDrawable(R.drawable.shape_chat_send_bg_burn_normal));
                }else{
                    v.setBackgroundDrawable(getResources().getDrawable(R.drawable.shape_chat_send_bg_normal));
                }
                cancelAuthCheckSchedule();
                //时间间隔太短
                if (mTimeIntervalTooShort) {
                    LogUtil.e("Audio", "time interval up ");
                    return true;
                }
                if (mChatDetailInputListener != null) {
                    if (mCancel) {
                        LogUtil.e("Audio", "action up but record Cancel");
                        mChatDetailInputListener.recordCancel();
                    } else {
                        LogUtil.e("Audio", "action up but record end");
                        mChatDetailInputListener.recordEnd();
                    }
                }


            }


            if (event.getAction() == MotionEvent.ACTION_MOVE) {

                if (mTimeIntervalTooShort) {
                    return true;
                }

                if (event.getY() < 0f) {
                    if (mChatDetailInputListener != null) {
                        mChatDetailInputListener.recordReadyCancel();
                        LogUtil.e("Audio", "action recordReadyCancel");

                    }
                    mCancel = true;
                } else {
                    if (mChatDetailInputListener != null) {
                        mChatDetailInputListener.recordNotCancel();
                        LogUtil.e("Audio", "action recordNotCancel");
                    }
                    mCancel = false;
                }

            }

            if (event.getAction() == MotionEvent.ACTION_CANCEL) {
                if(mIsBurn){
                    v.setBackgroundDrawable(getResources().getDrawable(R.drawable.shape_chat_send_bg_burn_normal));
                }else{
                    v.setBackgroundDrawable(getResources().getDrawable(R.drawable.shape_chat_send_bg_normal));
                }
                if (mChatDetailInputListener != null) {
                    LogUtil.e("Audio", "action recordCancel");
                    mChatDetailInputListener.onSystemCancel();
                    ChatDetailInputView.mLastEndTime = TimeUtil.getCurrentTimeInMillis();

                }
            }
            return true;

        });

    }

    public boolean isEmptyInput() {
        String inputVal = mEtInput.getText().toString();
        return "".equals(inputVal.trim());
    }

    public void refreshSendBtnStatus(){
        refreshSendBtnStatus(mEtInput.getText().toString());
    }

    public void refreshSendBtnStatus(String text) {
        if (!"".equals(text.trim()) || forcedShowSend()) {
            mTvSend.setVisibility(View.VISIBLE);
            mIvMore.setVisibility(View.INVISIBLE);
        } else {
            showMoreImage();
            mTvSend.setVisibility(View.INVISIBLE);
            mIvMore.setVisibility(View.VISIBLE);
        }

    }

    private boolean forcedShowSend() {
        return null != mChatDetailInputListener && mChatDetailInputListener.shouldForcedShowSend();
    }

    private void handleAtInput(CharSequence s, int start, int count) {
        //如果字符串存在@字符 长按头像获得的@人名称不需要判断跳转
        if (s.toString().contains("@") && !isAppendAtMembersDirectly) {
            if (start + 1 > s.toString().length()) {
                return;
            }
            String temp = s.toString().substring(start, start + 1);
            //如果当前光标的字符为@ 且不是从选择@人员界面返回 则进行跳转
            if (temp.equals("@") && !isBackForAT && count == 1) {
                inputAtCallBack(start);
            }
        }
    }

    public void setInputViewHeight(int height) {
        ViewGroup.LayoutParams params = getLayoutParams();
        params.height = height;
        setLayoutParams(params);
    }


    private void setInputViewHeight(ChatInputType inputType) {
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        switch (inputType) {
            case Text:
//                etInput.setScrollbarFadingEnabled(false);
                layoutParams.height = mInputViewInitialHeight;

                break;
            case Voice:
                mEtInput.setScrollbarFadingEnabled(false);
                layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                break;
            default:
                layoutParams.height = mInputViewInitialHeight;
                break;

        }
        setLayoutParams(layoutParams);
    }

    private void inputAtCallBack(int cursor) {
        if (mChatDetailInputListener == null) {
            return;
        }
        //如果输入的字符长度大于1，要判断最后一个字符是否为空格，如果不是空格，不触发at功能
        String input = mEtInput.getText().toString();
        if (TextUtils.isEmpty(input)) {
            return;
        }
        if (input.length() <= 1) {
            mChatDetailInputListener.inputAt();
            return;
        }

//        String c = String.valueOf(input.charAt(input.length() - 2));
        String c;
        if (cursor == 0) {
            c = String.valueOf(input.charAt(cursor));
        } else {
            c = String.valueOf(input.charAt(cursor - 1));
        }

        //判断前一个字符是否是字符或者是数字 用于输入邮箱时不会跳转@人
        if (isNumeric(c) || isChar(c)) {
            return;
        }

        mChatDetailInputListener.inputAt();
    }


    private void findView() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.chat_detail_input, this);
        view.setBackgroundResource(R.color.white);
        mRlRoot = view.findViewById(R.id.rl_root);
        mIvMore = view.findViewById(R.id.chat_detail_input_more);
        mTvSend = view.findViewById(R.id.chat_detail_input_send);
        mIvVoiceKeyboardModeSwitch = view.findViewById(R.id.iv_voice_keyboard_switch_mode);
        mLlLeftConnerLayout = view.findViewById(R.id.ll_left_conner_layout);
        mIvLeftConner = view.findViewById(R.id.iv_left_conner);
        mEtInput = view.findViewById(R.id.chat_detail_input_text);
        mBtnVoice = view.findViewById(R.id.chat_detail_input_voice);
        mIvFaceKeyBoardModeSwitch = view.findViewById(R.id.chat_detail_input_emoticons);
        mIvServiceKeyboard = view.findViewById(R.id.chat_detail_input_keyboard_service);
        mLineView = view.findViewById(R.id.chat_detail_input_line);
        mIvServiceKeyboard.setVisibility(GONE);
        mLineView.setVisibility(GONE);
    }

    /**
     * 当前输入框是服务号模式
     */
    public void serviceMode() {
        mIvServiceKeyboard.setVisibility(VISIBLE);
        mLineView.setVisibility(VISIBLE);
    }

    public void showLeftConnerFunView() {
        mLlLeftConnerLayout.setVisibility(VISIBLE);
    }

    public void setIvLeftConnerFunIcon(@DrawableRes int res) {
        mIvLeftConner.setImageResource(res);
    }

    public void refreshBurnUI(boolean isBurn) {

        if (isBurn) {
            mIsBurn = true;
            mBtnVoice.setBackgroundDrawable(getResources().getDrawable(R.drawable.shape_chat_send_bg_burn_normal));
            mIvLeftConner.setImageResource(R.mipmap.icon_burn_on);
            if(0 == mMoreIconStatus) {
                mIvMore.setImageResource(R.mipmap.icon_burn_more);
            } else {
                mIvMore.setImageResource(R.mipmap.icon_burn_chat_more_close);
            }
            if(0 == mSwitchIconStatus) {
                mIvFaceKeyBoardModeSwitch.setBackgroundResource(R.mipmap.icon_burn_face);
            } else {
                mIvFaceKeyBoardModeSwitch.setBackgroundResource(R.mipmap.icon_burn_keyboard);
            }

            if(ChatInputType.Text == mChatInputType) {
                mIvVoiceKeyboardModeSwitch.setImageResource(R.mipmap.icon_burn_voice);
            } else {
                mIvVoiceKeyboardModeSwitch.setImageResource(R.mipmap.icon_burn_keyboard);

            }
            if (mFragment instanceof ChatDetailFragment) {
                ChatDetailFragment fragment = (ChatDetailFragment)mFragment;
                fragment.emojiconsFragment.setBurnMode(true);
            }
            mRlRoot.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.burn_mode_chat_input_bg));
            mEtInput.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.burn_mode_chat_input_bg));
            mEtInput.setHintTextColor(ContextCompat.getColor(getContext(), R.color.common_text_color_666));
            mEtInput.setTextColor(ContextCompat.getColor(getContext(), R.color.common_text_hint_gray2));

        } else {
            mIsBurn = false;
            mBtnVoice.setBackgroundDrawable(getResources().getDrawable(R.drawable.shape_chat_send_bg_normal));
            mIvLeftConner.setImageResource(R.mipmap.icon_burn_off);

            if(0 == mMoreIconStatus) {
                mIvMore.setImageResource(R.mipmap.icon_chat_add);
            } else {
                mIvMore.setImageResource(R.mipmap.icon_chat_more_close);
            }

            if(0 == mSwitchIconStatus) {
                mIvFaceKeyBoardModeSwitch.setBackgroundResource(R.mipmap.icon_chat_face);
            } else {
                mIvFaceKeyBoardModeSwitch.setBackgroundResource(R.mipmap.icon_chat_keyboard);

            }

            if(ChatInputType.Text == mChatInputType) {
                mIvVoiceKeyboardModeSwitch.setImageResource(R.mipmap.icon_chat_voice);
            } else {
                mIvVoiceKeyboardModeSwitch.setImageResource(R.mipmap.icon_chat_keyboard);

            }
            if (mFragment instanceof ChatDetailFragment) {
                ChatDetailFragment fragment = (ChatDetailFragment)mFragment;
                fragment.emojiconsFragment.setBurnMode(false);
            }
            mRlRoot.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white));
            mEtInput.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white));
            mEtInput.setHintTextColor(ContextCompat.getColor(getContext(), R.color.common_text_color_999));
            mEtInput.setTextColor(ContextCompat.getColor(getContext(), R.color.common_text_color));

        }
    }


    //切换到语音模式
    private void voiceMode() {

//        if (mChatDetailInputListener != null) {
//            mChatDetailInputListener.voiceMode();
//        }
//        mEtInput.setVisibility(INVISIBLE);
//        mIvFaceKeyBoardModeSwitch.setVisibility(INVISIBLE);
//        mBtnVoice.setVisibility(VISIBLE);
        mChatInputType = ChatInputType.Voice;

        setIvVoiceKeyboardModeSwitchKeyboard();
    }

    private void setIvVoiceKeyboardModeSwitchKeyboard() {
        if(BurnModeHelper.isBurnMode()) {
            mIvVoiceKeyboardModeSwitch.setImageResource(R.mipmap.icon_burn_keyboard);

        } else {
            mIvVoiceKeyboardModeSwitch.setImageResource(R.mipmap.icon_chat_keyboard1);

        }
    }

    //切换到文字模式
    public void textMode(boolean showInput) {
        mChatInputType = ChatInputType.Text;
        mEtInput.setVisibility(VISIBLE);
        showIvSwitchEmoji();
        mBtnVoice.setVisibility(INVISIBLE);
        setIvVoiceKeyboardModeSwitchVoice();

        mEtInput.requestFocus();

        if (showInput) {
            new Handler().postDelayed(() -> mChatDetailInputListener.inputShow(), 300);
        }

    }

    private void setIvVoiceKeyboardModeSwitchVoice() {
        if(BurnModeHelper.isBurnMode()) {
            mIvVoiceKeyboardModeSwitch.setImageResource(R.mipmap.icon_burn_voice);

        } else {
            mIvVoiceKeyboardModeSwitch.setImageResource(R.mipmap.icon_chat_voice);

        }
    }

    public void showIvSwitchEmoji() {
        if (BurnModeHelper.isBurnMode()) {
            mIvFaceKeyBoardModeSwitch.setBackgroundResource(R.mipmap.icon_burn_face);

        } else {
            mIvFaceKeyBoardModeSwitch.setBackgroundResource(R.mipmap.icon_chat_face);

        }
        mIvFaceKeyBoardModeSwitch.setVisibility(VISIBLE);

        mSwitchIconStatus = 0;
    }

    public void showIvSwitchKeyboard() {
        if (BurnModeHelper.isBurnMode()) {
            mIvFaceKeyBoardModeSwitch.setBackgroundResource(R.mipmap.icon_burn_keyboard);

        } else {
            mIvFaceKeyBoardModeSwitch.setBackgroundResource(R.mipmap.icon_chat_keyboard1);

        }
        mIvFaceKeyBoardModeSwitch.setVisibility(VISIBLE);

        mSwitchIconStatus = 1;
    }

    public ChatDetailInputListener getChatDetailInputListner() {
        return mChatDetailInputListener;
    }

    public void setLinkMatchListener(LinkTranslatingHelper.LinkMatchListener linkMatchListener) {
        this.mLinkMatchListener = linkMatchListener;
    }

    public void setChatDetailInputListener(ChatDetailInputListener chatDetailInputListener) {
        this.mChatDetailInputListener = chatDetailInputListener;
    }

    public void clearEditText() {
        mEtInput.setText("");
    }

    public void setToServiceModeListener(ToServiceModeListener toServiceModeListener) {
        this.mToServiceModeListener = toServiceModeListener;
    }

    private boolean isAtValue(String value) {
        if (StringUtils.isEmpty(value)) {
            return false;
        }

        for (int i = value.length(); i > 0; i--) {
            String alpha = value.substring(i - 1, i);
            if (StringUtils.isEmpty(alpha)) {
                return false;
            }
            if (alpha.equals("@")) {
                return true;
            }
        }
        return false;
    }

    public interface ToServiceModeListener {
        void toServiceMenu();
    }

    /**
     * 用于判断是否失去了点触事件(down 事件还没触发), 借此用于判断系统是否弹出了权限选择框
     */
    private void checkTouchFunction() {
        if (!mCheckTouchThreadPool.isShutdown()) {
            {
                mWatchingPositionList.clear();
                mCollectedPositionList.clear();
                mLastCollectedPositionSize = 0;

                scheduledFuture = mCheckTouchThreadPool.scheduleAtFixedRate(new Runnable() {
                    int durationFly = 0;

                    @Override
                    public void run() {
                        durationFly += 500;

                        if (0 == mLastCollectedPositionSize) {
                            mLastCollectedPositionSize = mCollectedPositionList.size();
                        }

                        if (mLastCollectedPositionSize != mCollectedPositionList.size()) {
                            mLastCollectedPositionSize = mCollectedPositionList.size();
                            mWatchingPositionList.add(mCollectedPositionList.get(mLastCollectedPositionSize - 1));

                        } else {
                            mWatchingPositionList.add(-1f);
                        }

                        if (3000 <= durationFly) {
                            cancelAuthCheckSchedule();

                            if (touchFunctionHasError()) {
                                mChatDetailInputListener.recordKill();
                            }

                        }


                    }
                }, 500, 500, TimeUnit.MILLISECONDS);
            }
        }
    }

    public void cancelAuthCheckSchedule() {
        if (null != scheduledFuture) {
            scheduledFuture.cancel(true);
        }
    }

    private boolean touchFunctionHasError() {
        boolean isRecordViewVisible = mChatDetailInputListener.getRecordViewStatus();
        int errorTimes = 0;
        for (Float position : mWatchingPositionList) {
            if (-1 == position) {
                errorTimes++;
            } else {
                errorTimes = 0; //若失去 down 事件会为连续-1, 所以此处要重新刷新次数
            }
        }
//        LogUtil.e("error Times = " + errorTimes);
//        LogUtil.e("error status = " + isRecordViewVisible);
//        LogUtil.e("error collect position  = " + mCollectedPositionList.size());
        return !isRecordViewVisible && 5 < errorTimes;
    }

    //判断是否为数字
    public boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }

    //判断是否为字母
    public boolean isChar(String str) {
        Pattern pattern = Pattern.compile("[a-zA-Z]*");
        return pattern.matcher(str).matches();
    }

    public ChatInputType getChatInputType() {
        return mChatInputType;
    }

    //更新更多的图标为关闭
    public void showCloseImage(){
        if(mIsBurn){
            mIvMore.setImageResource(R.mipmap.icon_burn_chat_more_close);
        }else{
            mIvMore.setImageResource(R.mipmap.icon_chat_more_close);
        }
        mMoreIconStatus = 1;
    }
    //更新更多的图标为打开
    public void showMoreImage(){
        if(mIsBurn){
            mIvMore.setImageResource(R.mipmap.icon_burn_more);
        }else{
            mIvMore.setImageResource(R.mipmap.icon_chat_add);
        }
        mMoreIconStatus = 0;
    }
}


