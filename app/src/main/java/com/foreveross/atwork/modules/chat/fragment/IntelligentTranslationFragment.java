package com.foreveross.atwork.modules.chat.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.BaseCallBackNetWorkListener;
import com.foreveross.atwork.api.sdk.configSetting.conversationSetting.model.ConversionConfigSettingParticipant;
import com.foreveross.atwork.api.sdk.configSetting.conversationSetting.model.request.ConversionConfigSettingRequest;
import com.foreveross.atwork.component.ProgressDialogHelper;
import com.foreveross.atwork.infrastructure.model.SessionType;
import com.foreveross.atwork.infrastructure.model.setting.BusinessCase;
import com.foreveross.atwork.infrastructure.model.setting.ConfigSetting;
import com.foreveross.atwork.infrastructure.model.setting.SourceType;
import com.foreveross.atwork.modules.chat.activity.ChatInfoActivity;
import com.foreveross.atwork.modules.chat.adapter.TranslateLanguageRecyclerAdapter;
import com.foreveross.atwork.modules.chat.model.TranslateLanguageType;
import com.foreveross.atwork.modules.configSettings.manager.ConfigSettingsManager;
import com.foreveross.atwork.support.BackHandledFragment;
import com.foreveross.atwork.utils.ErrorHandleUtil;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.foreveross.atwork.modules.chat.activity.IntelligentTranslationActivity.DATA_SELECTEDLANGUAGE;

/**
 * Created by wuzejie on 19/12/6.
 * Description:
 */
public class IntelligentTranslationFragment  extends BackHandledFragment {

    private RecyclerView mRecyclerLanguageView;
    private List<TranslateLanguageType> mLanguageList = new ArrayList<>();
    private TranslateLanguageRecyclerAdapter mTranslateLanguageRecyclerAdapter;
    private TextView mTvTitle;
    private String mLanguageSelected;
    private String mChatIdentifier;
    private String mDomainId;
    private SessionType mChatInfoType;
    private ProgressDialogHelper mProgressDialogHelper;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_intelligent_translation, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initData();
        initView();
        registerListener();
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    protected void findViews(View view) {
        mRecyclerLanguageView = view.findViewById(R.id.recycler_language_view);
        mTvTitle = view.findViewById(R.id.title_bar_common_title);
    }

    @Override
    protected boolean onBackPressed() {
        finish();
        return false;
    }
    private void registerListener() {
        getView().findViewById(R.id.title_bar_common_back).setOnClickListener(v -> onBackPressed());
    }

    private void initView() {
        mTvTitle.setText(R.string.intelligent_translation);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerLanguageView.setLayoutManager(staggeredGridLayoutManager);

        mTranslateLanguageRecyclerAdapter = new TranslateLanguageRecyclerAdapter(mActivity, mLanguageList);
        mTranslateLanguageRecyclerAdapter.setOnClickEventListener(new TranslateLanguageRecyclerAdapter.OnClickEventListener() {
            @Override
            public void onClick(String languageSelected) {
                syncConversationSettings(languageSelected);
            }
        });
        mRecyclerLanguageView.setAdapter(mTranslateLanguageRecyclerAdapter);
    }

    /**
     * Description:获取服务器最新的会话配置
     * @param languageSelected
     */
    private void syncConversationSettings(String languageSelected) {

        ConversionConfigSettingRequest conversationsRequest = new ConversionConfigSettingRequest();
        ConversionConfigSettingParticipant participant = new ConversionConfigSettingParticipant(mChatIdentifier, mDomainId, mChatInfoType);
        conversationsRequest.setParticipant(participant);
        conversationsRequest.setLanguage(languageSelected);
        mProgressDialogHelper.show();
        ConfigSettingsManager.INSTANCE.setConversationSettingRemote(conversationsRequest, new BaseCallBackNetWorkListener() {
            @Override
            public void onSuccess() {
                setSessionTranslationSetting(languageSelected);
            }

            @Override
            public void networkFail(int errorCode, String errorMsg) {
                ErrorHandleUtil.handleError(errorCode, errorMsg);
                mProgressDialogHelper.dismiss();
            }
        });

    }

    /**
     * Description:设置本地Session的智能翻译
     * @param shortLanguage
     */
    private void setSessionTranslationSetting(String shortLanguage) {
        ConfigSetting configSetting = new ConfigSetting();
        configSetting.mSourceId = mChatIdentifier;
        configSetting.mSourceType = SourceType.valueOf(mChatInfoType);
        configSetting.mBusinessCase = BusinessCase.SESSION_TRANSLATION;


        configSetting.mValue = TranslateLanguageType.TranslateLanguage.getTranslateLanguageValue(shortLanguage);

        ConfigSettingsManager.INSTANCE.setSessionSettingLocal(configSetting, result -> {
            if(result) {

            }
            mProgressDialogHelper.dismiss();
            Intent intent = new Intent();
            intent.putExtra(DATA_SELECTEDLANGUAGE, shortLanguage);
            getActivity().setResult(RESULT_OK, intent);
            finish();
            return null;
        });

    }

    private void initData(){

        mProgressDialogHelper = new ProgressDialogHelper(getActivity());
        if (getArguments() != null) {
            Bundle bundle = getArguments();

            mLanguageSelected = bundle.getString(DATA_SELECTEDLANGUAGE, "");
            mChatInfoType = (SessionType) bundle.getSerializable(ChatInfoActivity.CHAT_INFO_TYPE);
            mChatIdentifier = bundle.getString(ChatInfoActivity.DATA_CHAT_IDENTIFIER);
            mDomainId = bundle.getString(ChatInfoActivity.DATA_CHAT_DOMAIN_ID);
        }

        initTralateLanguageList();
        setLanguageSelected();
    }

    private void initTralateLanguageList() {
        TranslateLanguageType translateLanguageType = new TranslateLanguageType("简体中文", "简体中文", "zh-CHS", false);
        mLanguageList.add(translateLanguageType);
        TranslateLanguageType translateLanguageType0 = new TranslateLanguageType("英语", "English", "en", false);
        mLanguageList.add(translateLanguageType0);
        TranslateLanguageType translateLanguageType1 = new TranslateLanguageType("日文", "日本語", "ja", false);
        mLanguageList.add(translateLanguageType1);
        TranslateLanguageType translateLanguageType2 = new TranslateLanguageType("韩文", "한국어", "ko", false);
        mLanguageList.add(translateLanguageType2);
        TranslateLanguageType translateLanguageType3 = new TranslateLanguageType("法文", "Français", "fr", false);
        mLanguageList.add(translateLanguageType3);
        TranslateLanguageType translateLanguageType4 = new TranslateLanguageType("西班牙文", "Español", "es", false);
        mLanguageList.add(translateLanguageType4);
        TranslateLanguageType translateLanguageType5 = new TranslateLanguageType("葡萄牙文", "Português", "pt", false);
        mLanguageList.add(translateLanguageType5);
        TranslateLanguageType translateLanguageType6 = new TranslateLanguageType("俄文", "русский язык", "ru", false);
        mLanguageList.add(translateLanguageType6);
        TranslateLanguageType translateLanguageType7 = new TranslateLanguageType("越南文", "Ngôn ngữ Việt", "vi", false);
        mLanguageList.add(translateLanguageType7);
        TranslateLanguageType translateLanguageType8 = new TranslateLanguageType("德文", "Deutsch", "de", false);
        mLanguageList.add(translateLanguageType8);
        TranslateLanguageType translateLanguageType9 = new TranslateLanguageType("阿拉伯文", "اللغة العربية", "ar", false);
        mLanguageList.add(translateLanguageType9);
        TranslateLanguageType translateLanguageType10 = new TranslateLanguageType("印尼文", "Bahasa Indonesia", "id", false);
        mLanguageList.add(translateLanguageType10);
        TranslateLanguageType translateLanguageType11 = new TranslateLanguageType("意大利文", "italiano", "it", false);
        mLanguageList.add(translateLanguageType11);
    }

    /**
     * Description:设置已选中的语种
     */
    private void setLanguageSelected(){
        for(int i = 0; i < mLanguageList.size(); i++){
            if(mLanguageSelected.equalsIgnoreCase(mLanguageList.get(i).getShortName()))
                mLanguageList.get(i).setIsSelected(true);
        }

    }
}
