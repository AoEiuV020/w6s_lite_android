package com.foreveross.atwork.modules.aboutme.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.Employee.EmployeeAsyncNetService;
import com.foreveross.atwork.api.sdk.users.UserAsyncNetService;
import com.foreveross.atwork.api.sdk.users.requestJson.ModifyUserInfoJson;
import com.foreveross.atwork.component.ProgressDialogHelper;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.Employee;
import com.foreveross.atwork.infrastructure.model.domain.UserSchemaSettingItem;
import com.foreveross.atwork.infrastructure.model.employee.DataSchema;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.infrastructure.utils.PatternUtils;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.TimeUtil;
import com.foreveross.atwork.manager.EmployeeManager;
import com.foreveross.atwork.manager.UserManager;
import com.foreveross.atwork.modules.aboutme.activity.ModifyMyInfoActivity;
import com.foreveross.atwork.support.BackHandledFragment;
import com.foreveross.atwork.utils.AtworkToast;
import com.foreveross.atwork.utils.AtworkUtil;
import com.foreveross.atwork.utils.ErrorHandleUtil;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * 修改联系电话页面
 * Created by ReyZhang on 2015/5/6.
 */
public class ModifyMyInfoFragment extends BackHandledFragment implements View.OnClickListener {

    private static final String TAG = ModifyMyInfoFragment.class.getSimpleName();

    private int MAX_NICKNAME_LENGTH = 40;

    private Activity mActivity;

    private ImageView mIvBack;
    private TextView mTvTitle;
    private TextView mTvSave;

    private RelativeLayout mRlModifyRoot;
    private RelativeLayout mRlRadio;
    private RelativeLayout mRlDatePicker;
    private RadioGroup mRgSelect;
    private ImageView mIvDelete;
    private EditText mEtModify;
    private DatePicker mDpPicker;

    private ProgressDialogHelper mUpdatingHelper;

    private DataSchema mDataSchema;
    private String mDataValue;
    private SparseArray<String> mRadioValueArray = new SparseArray<>();
    private UserSchemaSettingItem mUserSchema;
    private String[] mOptions;

    private boolean mModifyType = false;

    private Employee mEmployee;

    private String title = "";

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
        View view = inflater.inflate(R.layout.fragment_modify_my_info, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        registerListener();
        initData();
        if (mModifyType) {
            handleEmployeeUI();
        } else {
            handleUserUI();
        }
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
        mRlRadio = view.findViewById(R.id.rl_radio);
        mRgSelect = view.findViewById(R.id.rg_select);
        mRlDatePicker = view.findViewById(R.id.rl_date_picker);
        mDpPicker = view.findViewById(R.id.dpPicker);
    }

    private void registerListener() {
        mIvBack.setOnClickListener(this);
        mTvSave.setOnClickListener(this);
        mIvDelete.setOnClickListener(this);

        mEtModify.addTextChangedListener(new TextWatcher() {
            String nameBefore;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (null != mUserSchema && "name".equals(mUserSchema.getProperty())) {
                    nameBefore = s.toString();
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (shouldInterceptEmpty(s.toString())) {
                    mTvSave.setTextColor(getResources().getColor(R.color.title_bar_rightest_text_gray));
                    mIvDelete.setVisibility(View.GONE);
                    return;
                }
//                if (getString(R.string.nickname).equals(mUserSchema) && StringUtils.getWordCount(s.toString()) > (MAX_NICKNAME_LENGTH)) {
//                    AtworkToast.showToast(getString(R.string.nickname_too_long));
//                    mEtModify.setText(nameBefore);
//                    return;
//                }

//                mTvSave.setEnabled(true);
                mTvSave.setTextColor(getResources().getColor(R.color.common_item_black));
                mIvDelete.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {
//                mEtModify.setSelection(mEtModify.getText().length());
            }
        });

        mRgSelect.setOnCheckedChangeListener((group, checkedId) -> mTvSave.setTextColor(getResources().getColor(R.color.common_item_black)));
    }

    private boolean shouldInterceptEmpty(String text) {
        return !AtworkConfig.EMPLOYEE_CONFIG.getCanModifyPropertyEmpty() && TextUtils.isEmpty(text);
    }

    private void initData() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            mModifyType = getArguments().getBoolean(ModifyMyInfoActivity.INTENT_TYPE);

            if (mModifyType) {
                mDataSchema = (DataSchema) getArguments().getSerializable(ModifyMyInfoActivity.INTENT_DATA_SCHEMA);
                mEmployee = getArguments().getParcelable(ModifyMyInfoActivity.INTENT_DATA_EMPLOYEE);
            } else {
                mUserSchema = getArguments().getParcelable(ModifyMyInfoActivity.INTENT_DATA_TYPE);
            }

            mDataValue = getArguments().getString(ModifyMyInfoActivity.INTENT_DATA_VALUE);

            makeGenderCompatible();
            mIvDelete.setVisibility(View.VISIBLE);

        }

    }

    private void handleUserUI() {

        if ("name".equals(mUserSchema.getProperty())
                || "email".equals(mUserSchema.getProperty())
                || "phone".equals(mUserSchema.getProperty())) {
            if("email".equals(mUserSchema.getProperty())) {
                title = getString(R.string.email);
            }
            if ("phone".equals(mUserSchema.getProperty())) {
                title = getString(R.string.auth_phone_name);
            }
            mRlModifyRoot.setVisibility(View.VISIBLE);
            mEtModify.setText(mDataValue);
            mEtModify.setSelection(mDataValue.length());
            mEtModify.setHint(getModifyString(R.string.please_input_modify_info));
            setEditTextInputType();

            if (TextUtils.isEmpty(mDataValue)) {
                mIvDelete.setVisibility(View.GONE);

            } else {
                mIvDelete.setVisibility(View.VISIBLE);

            }
            mEtModify.requestFocus();
            mEtModify.postDelayed(() -> AtworkUtil.showInput(mActivity, mEtModify), 300);

        } else if ("gender".equals(mUserSchema.getProperty())) {
            mRlRadio.setVisibility(View.VISIBLE);
            mTvSave.setTextColor(getResources().getColor(R.color.title_bar_rightest_text_gray));
            title = getString(R.string.sex);

            for (String option : mOptions) {
                RadioButton rb = new RadioButton(mActivity);

                if ("gender".equals(mUserSchema.getProperty())) {
                    if (getString(R.string.male).equalsIgnoreCase(option)) {
                        rb.setText(R.string.male);

                    } else {
                        rb.setText(R.string.female);

                    }

                } else {
                    rb.setText(option);
                }
                mRgSelect.addView(rb);
                if (mDataValue.equalsIgnoreCase(option)) {
                    rb.setChecked(true);
                }

                mRadioValueArray.put(rb.getId(), option);
            }

        } else if ("birthday".equals(mUserSchema.getProperty())) {
            title = getString(R.string.birthday);
            mRlDatePicker.setVisibility(View.VISIBLE);
            mTvSave.setTextColor(getResources().getColor(R.color.common_item_black));

            try {
                String value = TimeUtil.getStringForMillis(Long.parseLong(mDataValue), TimeUtil.YYYY_MM_DD);
                String[] dateStrArray = value.split("-");
                int year = Integer.valueOf(dateStrArray[0]);
                int month = Integer.valueOf(dateStrArray[1]);
                int day = Integer.valueOf(dateStrArray[2]);
                mDpPicker.init(year, month - 1, day, (view, year1, monthOfYear, dayOfMonth) -> {

                });
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        mTvTitle.setText(title);

        mTvSave.setVisibility(View.VISIBLE);
        mTvSave.setText(getString(R.string.save));
    }

    private void handleEmployeeUI() {
        if (Employee.InfoType.TEXT.equalsIgnoreCase(mDataSchema.type)
                || Employee.InfoType.MOBILE_PHONE.equalsIgnoreCase(mDataSchema.type)
                || Employee.InfoType.EMAIL.equalsIgnoreCase(mDataSchema.type)
                || Employee.InfoType.TEL_PHONE.equalsIgnoreCase(mDataSchema.type)
                ) {

            mRlModifyRoot.setVisibility(View.VISIBLE);
            mEtModify.setText(mDataValue);
            mEtModify.setSelection(mDataValue.length());
            mEtModify.setHint(getModifyString(R.string.please_input_modify_info));
            setEditTextInputType();

            if (TextUtils.isEmpty(mDataValue)) {
                mIvDelete.setVisibility(View.GONE);

            } else {
                mIvDelete.setVisibility(View.VISIBLE);

            }
            mEtModify.requestFocus();
            mEtModify.postDelayed(() -> AtworkUtil.showInput(mActivity, mEtModify), 300);

        } else if (Employee.InfoType.RADIO.equalsIgnoreCase(mDataSchema.type)) {
            mRlRadio.setVisibility(View.VISIBLE);
            mTvSave.setTextColor(getResources().getColor(R.color.title_bar_rightest_text_gray));


            for (int i = 0; i < mDataSchema.mOptions.size(); i++) {
                String option = mDataSchema.mOptions.get(i);

                RadioButton rb = new RadioButton(mActivity);

                if ("gender".equalsIgnoreCase(mDataSchema.mProperty)) {
                    if ("male".equalsIgnoreCase(option)) {
                        rb.setText(R.string.male);

                    } else {
                        rb.setText(R.string.female);
                    }
                } else {
                    rb.setText(option);
                }
                mRgSelect.addView(rb);
                if (mDataValue.equalsIgnoreCase(option)) {
                    rb.setChecked(true);
                }

                mRadioValueArray.put(rb.getId(), option);
            }

        } else if (Employee.InfoType.DATE.equalsIgnoreCase(mDataSchema.type)) {
            mRlDatePicker.setVisibility(View.VISIBLE);
            mTvSave.setTextColor(getResources().getColor(R.color.common_item_black));

            try {
                String value = TimeUtil.getStringForMillis(Long.valueOf(mDataValue), TimeUtil.getTimeFormat1(BaseApplicationLike.baseContext));
                String[] dateStrArray = value.split("-");
                int year = Integer.valueOf(dateStrArray[0]);
                int month = Integer.valueOf(dateStrArray[1]);
                int day = Integer.valueOf(dateStrArray[2]);
                mDpPicker.init(year, month - 1, day, (view, year1, monthOfYear, dayOfMonth) -> {

                });
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        mTvTitle.setText(AtworkUtil.getEmployeeDataSchemaAliasI18n(mDataSchema));
        mTvSave.setVisibility(View.VISIBLE);
        mTvSave.setText(getString(R.string.save));
    }


    private void makeGenderCompatible() {
        if (!mModifyType && "gender".equals(mUserSchema.getProperty())) {
            mOptions = new String[]{getString(R.string.male), getString(R.string.female)};
            return;
        }

        if (mModifyType && "gender".equalsIgnoreCase(mDataSchema.mProperty) && ListUtil.isEmpty(mDataSchema.mOptions)) {
            mDataSchema.mOptions = new ArrayList<>();
            mDataSchema.mOptions.add("male");
            mDataSchema.mOptions.add("female");
        }

    }


    private void changeUserInfo(final String value) {
        mUpdatingHelper = new ProgressDialogHelper(getActivity());
        mUpdatingHelper.show(getModifyString(R.string.updating_contact_info));

        ModifyUserInfoJson userInfoJson = new ModifyUserInfoJson();

        if ("name".equals(mUserSchema.getProperty())) {
            userInfoJson.name = value;
            UserManager.getInstance().modifyUserName(mActivity, mUserSchema.getProperty(), userInfoJson, new UserAsyncNetService.OnHandleUserInfoListener() {
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

            return;

        }

        if ("phone".equals(mUserSchema.getProperty())) {
            userInfoJson.phone = value;
        }
        if ("email".equals(mUserSchema.getProperty())) {
            userInfoJson.email = value;
        }
        if ("birthday".equals(mUserSchema.getProperty())) {
            userInfoJson.birthday = Long.valueOf(value);
        }
        if ("gender".equals(mUserSchema.getProperty())) {
            if (getString(R.string.male).equalsIgnoreCase(value)) {
                userInfoJson.gender = "male";
            }
            if (getString(R.string.female).equalsIgnoreCase(value)) {
                userInfoJson.gender = "female";
            }
        }

        UserManager.getInstance().modifyUserInfo(getActivity(), mUserSchema.getProperty(), userInfoJson, new UserAsyncNetService.OnHandleUserInfoListener() {
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


    private void changeEmployeeInfo(final String value) {

        if (mEmployee == null || mDataSchema == null) {
            return;
        }

        mUpdatingHelper = new ProgressDialogHelper(getActivity());
        mUpdatingHelper.show(getModifyString(R.string.updating_contact_info));


        String postPrams = EmployeeManager.getInstance().getChangeInfoJson(mEmployee, mDataSchema, value);

        EmployeeAsyncNetService.getInstance().modifyEmployeeInfo(mActivity, mEmployee.orgCode, mEmployee.id, postPrams, new EmployeeAsyncNetService.OnHandleEmployeeInfoListener() {
            @Override
            public void networkFail(int errorCode, String errorMsg) {

                mUpdatingHelper.dismiss();

                if(!ErrorHandleUtil.handleBaseError(errorCode, errorMsg)) {
                    AtworkToast.showToast(getModifyString(R.string.ModifyEmployee_common));
                }
            }

            @Override
            public void onSuccess() {
                mUpdatingHelper.dismiss();
                AtworkToast.showToast(getModifyString(R.string.update_contact_info_success));
                AtworkUtil.hideInput(getActivity(), mEtModify);
                finish();
            }


        });


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
                if (mModifyType) {
                    employeeSave();
                } else {
                    userSave();
                }
                break;

            case R.id.modify_tel_cancelBtn:
                mEtModify.setText("");
                break;
        }
    }

    private void userSave() {
        String value = "";
        if("name".equals(mUserSchema.getProperty())) {
            value = mEtModify.getText().toString();

            if (TextUtils.isEmpty(value)) {
                AtworkToast.showToast(getModifyString(R.string.please_input_modify_info));
                return;
            }

            if (StringUtils.getWordCount(value) > MAX_NICKNAME_LENGTH) {
                AtworkToast.showToast(getString(R.string.nickname_too_long));
                return;

            } else if(PatternUtils.hasEmoji(value)) {
                AtworkToast.showToast(getString(R.string.not_support_emoji));
                return;
            }

        } else if ("email".equals(mUserSchema.getProperty())
                || "phone".equals(mUserSchema.getProperty())) {

            value = mEtModify.getText().toString();

            if (TextUtils.isEmpty(value)) {
                AtworkToast.showToast(getModifyString(R.string.please_input_modify_info));
                return;
            }


            if (!reviewUserSchemaType(value)) {
                AtworkToast.showToast(getModifyString(R.string.please_input_right_form));
                return;
            }



        } else if ("gender".equals(mUserSchema.getProperty())) {
            int checkId = mRgSelect.getCheckedRadioButtonId();
            if (-1 == checkId) {
                AtworkToast.showToast(getModifyString(R.string.please_radio_modify_info));
                return;
            } else {
                value = mRadioValueArray.get(checkId);

            }

        } else if ("birthday".equals(mUserSchema.getProperty())) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(mDpPicker.getYear(), mDpPicker.getMonth(), mDpPicker.getDayOfMonth());

            value = String.valueOf(calendar.getTimeInMillis());
        }

        changeUserInfo(value);
    }


    private void employeeSave() {
        String value = "";
        if (Employee.InfoType.TEXT.equalsIgnoreCase(mDataSchema.type)
                || Employee.InfoType.MOBILE_PHONE.equalsIgnoreCase(mDataSchema.type)
                || Employee.InfoType.EMAIL.equalsIgnoreCase(mDataSchema.type)
                || Employee.InfoType.TEL_PHONE.equalsIgnoreCase(mDataSchema.type)
                ) {

            value = mEtModify.getText().toString();

            if (shouldInterceptEmpty(value)) {
                AtworkToast.showToast(getModifyString(R.string.please_input_modify_info));
                return;

            } else if (!TextUtils.isEmpty(value) && !reviewDataSchemaType(value)) {
                AtworkToast.showToast(getModifyString(R.string.please_input_right_form));
                return;
            }


        } else if (Employee.InfoType.RADIO.equalsIgnoreCase(mDataSchema.type)) {
            int checkId = mRgSelect.getCheckedRadioButtonId();
            if (-1 == checkId) {
                AtworkToast.showToast(getModifyString(R.string.please_radio_modify_info));
                return;
            } else {
                value = mRadioValueArray.get(checkId);

            }

        } else if (Employee.InfoType.DATE.equalsIgnoreCase(mDataSchema.type)) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(mDpPicker.getYear(), mDpPicker.getMonth(), mDpPicker.getDayOfMonth());

            value = String.valueOf(calendar.getTimeInMillis());
        }

        changeEmployeeInfo(value);
    }

    @Override
    protected boolean onBackPressed() {
        finish();

        return false;
    }


    private String getModifyString(int resString) {
        if (mModifyType) {
            return mActivity.getString(resString, AtworkUtil.getEmployeeDataSchemaAliasI18n(mDataSchema));
        }
        if (TextUtils.isEmpty(title)) {
            return mActivity.getString(resString, mUserSchema.getAlias());
        }
        return mActivity.getString(resString, title);
    }

    /**
     * 检查 type 为 MOBILE_PHONE, EMAIL, TEL_PHONE类型的 dataSchema 的值是否合理
     */
    private boolean reviewDataSchemaType(String value) {
        boolean isRight = true;
        if (Employee.InfoType.MOBILE_PHONE.equalsIgnoreCase(mDataSchema.type)) {
            isRight = PatternUtils.isPhoneForEdit(value);

        } else if (Employee.InfoType.TEL_PHONE.equalsIgnoreCase(mDataSchema.type)) {
            isRight = PatternUtils.isPhoneForEdit(value);

        } else if (Employee.InfoType.EMAIL.equalsIgnoreCase(mDataSchema.type)) {
            isRight = PatternUtils.isEmail(value);
        }

        return isRight;
    }


    private boolean reviewUserSchemaType(String value) {
        boolean isRight = true;
        if ("phone".equals(mUserSchema.getProperty())) {
            isRight = PatternUtils.isPhoneForEdit(value);

        } else if ("email".equals(mUserSchema.getProperty())) {
            isRight = PatternUtils.isEmail(value);
        }

        return isRight;
    }

    private void setEditTextInputType() {
        if (!mModifyType && "phone".equals(mUserSchema.getProperty())) {
            mEtModify.setInputType(EditorInfo.TYPE_CLASS_PHONE);
            mEtModify.setKeyListener(DigitsKeyListener.getInstance("0123456789-"));
            return;
        }

        if (mModifyType && (Employee.InfoType.MOBILE_PHONE.equalsIgnoreCase(mDataSchema.type)
                || Employee.InfoType.TEL_PHONE.equalsIgnoreCase(mDataSchema.type))) {
            mEtModify.setInputType(EditorInfo.TYPE_CLASS_PHONE);
            mEtModify.setKeyListener(DigitsKeyListener.getInstance("0123456789-"));
        }

    }
}
