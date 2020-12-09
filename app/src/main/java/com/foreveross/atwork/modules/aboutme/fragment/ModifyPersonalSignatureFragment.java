package com.foreveross.atwork.modules.aboutme.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.users.UserAsyncNetService;
import com.foreveross.atwork.api.sdk.users.requestJson.ModifyPersonalSignatureJson;
import com.foreveross.atwork.component.MaxInputEditText;
import com.foreveross.atwork.component.ProgressDialogHelper;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.manager.UserManager;
import com.foreveross.atwork.modules.aboutme.activity.ModifyMyInfoActivity;
import com.foreveross.atwork.support.BackHandledFragment;
import com.foreveross.atwork.utils.AtworkToast;
import com.foreveross.atwork.utils.AtworkUtil;
import com.foreveross.atwork.utils.EditTextUtil;
import com.foreveross.atwork.utils.ErrorHandleUtil;

/**
 * Created by wuzejie on 2019/10/14.
 * description: 修改个性签名
 */
public class ModifyPersonalSignatureFragment extends BackHandledFragment implements View.OnClickListener {

    private static final String TAG = ModifyPersonalSignatureFragment.class.getSimpleName();

    private int MAX_PERSONAL_SIGNATURE_LENGTH = 40;

    private Activity mActivity;

    private ImageView mIvBack;
    private TextView mTvTitle;
    private TextView mTvSave;

    private RelativeLayout mRlModifyRoot;
    private ImageView mIvDelete;
    private MaxInputEditText mEtModify;

    private ProgressDialogHelper mUpdatingHelper;
    private String mDataValue;

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
        View view = inflater.inflate(R.layout.fragment_modify_personal_signature, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        registerListener();
        initData();

        handlePersonalSignatureUI();
    }

    @Override
    public void onPause() {
        super.onPause();
        AtworkUtil.hideInput(getActivity(), mEtModify);

    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    protected void findViews(View view) {
        mIvBack = view.findViewById(R.id.title_bar_common_back);
        mTvTitle = view.findViewById(R.id.title_bar_common_title);
        mTvSave = view.findViewById(R.id.title_bar_common_right_text);
        mEtModify = view.findViewById(R.id.modify_tel_edit);
        mIvDelete = view.findViewById(R.id.modify_tel_cancelBtn);
        mRlModifyRoot = view.findViewById(R.id.rl_modify_text);
    }

    private void registerListener() {
        mIvBack.setOnClickListener(this);
        mTvSave.setOnClickListener(this);
        mIvDelete.setOnClickListener(this);
        EditTextUtil.setEditTextMaxCharLengthInput(mEtModify, MAX_PERSONAL_SIGNATURE_LENGTH, true, "");
    }

    private boolean shouldInterceptEmpty(String text) {
        return !AtworkConfig.EMPLOYEE_CONFIG.getCanModifyPropertyEmpty() && TextUtils.isEmpty(text);
    }

    private void initData() {
        mTvTitle.setText(getString(R.string.modify_personal_signature));
        Bundle bundle = getArguments();
        if (bundle != null) {
            mDataValue = getArguments().getString(ModifyMyInfoActivity.INTENT_DATA_VALUE);
            mIvDelete.setVisibility(View.VISIBLE);

        }

    }

    private void handlePersonalSignatureUI() {
        mRlModifyRoot.setVisibility(View.VISIBLE);
        mEtModify.setHint(R.string.please_input_modify_signature);
        if (StringUtils.isEmpty(mDataValue)) {
            mIvDelete.setVisibility(View.GONE);


        } else {
            mIvDelete.setVisibility(View.VISIBLE);

            mEtModify.setText(mDataValue);
            mEtModify.setSelection(mDataValue.length());

        }
        mEtModify.requestFocus();
        mEtModify.postDelayed(() -> AtworkUtil.showInput(mActivity, mEtModify), 300);
        mTvSave.setVisibility(View.VISIBLE);
        mTvSave.setText(getString(R.string.save));
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.title_bar_common_back:
                AtworkUtil.hideInput(mActivity, mEtModify);
                finish();
                break;

            case R.id.title_bar_common_right_text:
                PersonalSignatureSave();
                break;

            case R.id.modify_tel_cancelBtn:
                mEtModify.setText("");
                break;
        }
    }

    private void PersonalSignatureSave(){
        String value = "";
        value = mEtModify.getText().toString();
        if (StringUtils.getWordCount(value) > MAX_PERSONAL_SIGNATURE_LENGTH) {
            AtworkToast.showToast(getString(R.string.signature_too_long));
            return;
        }
        changePersonalSignature(value);
    }
    /**
     * 描述：访问服务器，修改数据库中的个性签名
     */
    private void changePersonalSignature(final String value) {
        mUpdatingHelper = new ProgressDialogHelper(getActivity());
        mUpdatingHelper.show(getModifyString(R.string.updating_contact_info));

        ModifyPersonalSignatureJson modifyPersonalSignatureJson = new ModifyPersonalSignatureJson();
        modifyPersonalSignatureJson.moments = value;

        UserManager.getInstance().modifyPersonalSignature(getActivity(), modifyPersonalSignatureJson, new UserAsyncNetService.OnHandleUserInfoListener() {

            @Override
            public void networkFail(int errorCode, String errorMsg) {
                mUpdatingHelper.dismiss();

                ErrorHandleUtil.handleError(errorCode, errorMsg);
            }

            @Override
            public void success() {
                mUpdatingHelper.dismiss();

                AtworkToast.showToast(getModifyString(R.string.update_contact_info_success));
                AtworkUtil.hideInput(getActivity(), mEtModify);
                finish();
            }
        });
    }

    @Override
    protected boolean onBackPressed() {
        finish();

        return false;
    }

    private String getModifyString(int resString) {
        return mActivity.getString(resString, "个人签名");

    }
}
