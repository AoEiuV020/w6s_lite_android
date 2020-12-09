package com.foreveross.atwork.modules.setting.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.shared.CommonShareInfo;
import com.foreveross.atwork.infrastructure.utils.language.LanguageSetting;
import com.foreveross.atwork.infrastructure.utils.language.LanguageUtil;
import com.foreveross.atwork.modules.chat.util.MessageItemPopUpHelp;
import com.foreveross.atwork.modules.main.activity.MainActivity;
import com.foreveross.atwork.modules.setting.adapter.LanguageSettingAdapter;
import com.foreveross.atwork.support.BackHandledFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by dasunsy on 2017/4/20.
 */

public class LanguageSettingFragment extends BackHandledFragment {

    private ImageView mIvBack;
    private TextView mTvTitle;
    private TextView mTvRightest;
    private RecyclerView mRvLanguageSelect;

    private List<Integer> mLanguageList;
    private LanguageSettingAdapter mLanguageSettingAdapter;

    private boolean mForbiddenHandle = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_language_setting, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews();
        registerListener();

        initData();
    }

    @Override
    protected void findViews(View view) {
        mIvBack = view.findViewById(R.id.title_bar_common_back);
        mTvTitle = view.findViewById(R.id.title_bar_common_title);
        mTvRightest = view.findViewById(R.id.title_bar_common_right_text);
        mRvLanguageSelect = view.findViewById(R.id.rw_language_setting);
    }

    private void initViews() {
        mTvTitle.setText(R.string.language_setting);
//        mTvLeftest.setText(R.string.cancel);
//        mTvLeftest.setTextSize(16);
        mTvRightest.setText(R.string.save);

//        mIvBack.setVisibility(View.GONE);
//        mTvLeftest.setVisibility(View.VISIBLE);
        mTvRightest.setVisibility(View.VISIBLE);
    }

    private void registerListener() {
        mIvBack.setOnClickListener(v -> onBackPressed());

        mTvRightest.setOnClickListener(v -> {
            if(mForbiddenHandle) {
                return;
            }

            mForbiddenHandle = true;

            CommonShareInfo.setLanguageSetting(getActivity(), mLanguageSettingAdapter.getSelectLanguage());
            Locale locale = LanguageUtil.getCurrentSettingLocale(BaseApplicationLike.baseContext);

            LanguageUtil.changeLanguage(getActivity(), locale);
            MainActivity.recreateMainPage();
            MessageItemPopUpHelp.refreshViewItemText();
            AtworkApplicationLike.modifyDeviceSettings();
            new Handler().postDelayed(() -> {
                Intent intent = MainActivity.getMainActivityIntent(getActivity(), true);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }, 100);
        });
    }



    private void initData() {
        mLanguageList = new ArrayList<>();

        mLanguageList.add(LanguageSetting.AUTO);
        mLanguageList.add(LanguageSetting.SIMPLIFIED_CHINESE);
        mLanguageList.add(LanguageSetting.TRADITIONAL_CHINESE);
        mLanguageList.add(LanguageSetting.ENGLISH);

        //list布局
        LinearLayoutManager linearLayoutMgr = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mLanguageSettingAdapter = new LanguageSettingAdapter(getActivity(), mLanguageList);

        mRvLanguageSelect.setLayoutManager(linearLayoutMgr);
        mRvLanguageSelect.setAdapter(mLanguageSettingAdapter);

    }

    @Override
    protected boolean onBackPressed() {

        if (!mForbiddenHandle) {
            finish();
        }
        return false;
    }
}
