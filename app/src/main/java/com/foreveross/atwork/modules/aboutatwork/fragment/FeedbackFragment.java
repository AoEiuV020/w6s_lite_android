package com.foreveross.atwork.modules.aboutatwork.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.setting.FeedbackNetService;
import com.foreveross.atwork.component.ProgressDialogHelper;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.listener.TextWatcherAdapter;
import com.foreveross.atwork.support.BackHandledFragment;
import com.foreveross.atwork.utils.AtworkToast;
import com.rockerhieu.emojicon.EmojiconEditText;


public class FeedbackFragment extends BackHandledFragment implements FeedbackNetService.OnFeedbackSuccess {

    private static final String TAG = FeedbackFragment.class.getSimpleName();

    private Activity mActivity;
    //----------顶部栏-------
    private ImageView mIvBackBtn;
    private TextView mTvTitle;
    private TextView mTvSend;

    private EmojiconEditText mEtFeedback;
    private ProgressDialogHelper mLoadingHelper;

    private TextView mTvNumTv;

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
        View view = inflater.inflate(R.layout.fragment_feedback, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        registerListener();
        initData();

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    protected void findViews(View view) {
        mIvBackBtn = view.findViewById(R.id.title_bar_common_back);
        mTvTitle = view.findViewById(R.id.title_bar_common_title);
        mTvSend = view.findViewById(R.id.title_bar_common_right_text);
        mTvSend.setEnabled(false);
        mTvSend.setTextColor(getResources().getColor(R.color.title_bar_rightest_text_gray));
        mEtFeedback = view.findViewById(R.id.feedback_edit);
        mTvNumTv = view.findViewById(R.id.feedback_number_tv);
    }


    private void registerListener() {
        mEtFeedback.addTextChangedListener(new TextWatcherAdapter() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (StringUtils.isEmpty(s.toString())) {
                    mTvSend.setEnabled(false);
                    mTvSend.setTextColor(getResources().getColor(R.color.title_bar_rightest_text_gray));
                } else {
                    mTvSend.setEnabled(true);
                    mTvSend.setTextColor(getResources().getColor(R.color.common_item_black));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                String temp = StringUtils.getWordCount(s.toString()) + "/1000";
                mTvNumTv.setText(temp);
            }
        });

        mIvBackBtn.setOnClickListener(v -> onBackPressed());


        mTvSend.setOnClickListener(v -> {
            String feedbackContent = mEtFeedback.getText().toString();
            if (StringUtils.getWordCount(feedbackContent) == 0 || StringUtils.getWordCount(feedbackContent) > 1000) {
                AtworkToast.showToast(getResources().getString(R.string.feedback_num_limit));
                return;
            }
            mLoadingHelper = new ProgressDialogHelper(mActivity);
            mLoadingHelper.show();
            FeedbackNetService.postFeedback(mActivity, feedbackContent, FeedbackFragment.this);
        });

    }



    private void initData() {
        mTvTitle.setText(getString(R.string.feedback));
        mTvSend.setVisibility(View.VISIBLE);
        mTvSend.setText(getString(R.string.send_ok));

    }


    @Override
    public void onThankYou() {
        mLoadingHelper.dismiss();
        AtworkToast.showToast(getString(R.string.feedback_success));
        finish();

    }

    @Override
    public void networkFail(int errorCode, String errorMsg) {
        mLoadingHelper.dismiss();
        AtworkToast.showToast(getString(R.string.feedback_fail));
    }

    @Override
    protected boolean onBackPressed() {
        finish();
        return false;
    }


}
