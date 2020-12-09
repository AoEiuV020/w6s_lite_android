package com.foreveross.atwork.modules.contact.component;
/**
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@%((((((((#&@@@((#((#@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@%(((((((((((((%@*((((@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@%(((/,/(((((((((@@@*#@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@#(((@@@((((((((((@@((@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@(((((((((((((((((((((@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@(((((((((((((((((((@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@(((((((((((((((*@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ __           __
 * .--.--.--.-----.----.|  |--.-----.|  |.--.--.-----.
 * |  |  |  |  _  |   _||    <|  _  ||  ||  |  |__ --|
 * |________|_____|__|  |__|__|   __||__||_____|_____|
 * |__|
 */


import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.foreverht.workplus.ui.component.dialogFragment.AtworkAlertDialog;
import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.Employee;
import com.foreveross.atwork.infrastructure.model.employee.DataSchema;
import com.foreveross.atwork.infrastructure.permissions.PermissionsManager;
import com.foreveross.atwork.infrastructure.permissions.PermissionsResultAction;
import com.foreveross.atwork.infrastructure.utils.TimeUtil;
import com.foreveross.atwork.utils.AtworkToast;
import com.foreveross.atwork.utils.AtworkUtil;
import com.foreveross.atwork.utils.IntentUtil;
import com.foreveross.theme.manager.SkinHelper;

/**
 * Created by reyzhang22 on 15/12/15.
 */
public class ContactInfoItemView extends LinearLayout {

    private TextView mInfoKey;

    private TextView mInfoValue;

    private TextView mSendSMS;

    private TextView mCopy;

    public ContactInfoItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews(context);
    }

    public ContactInfoItemView(Context context) {
        super(context);
        initViews(context);
    }

    public ContactInfoItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews(context);
    }

    private void initViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_contact_info, this);
        mInfoKey = view.findViewById(R.id.contact_info_key);
        mInfoValue = view.findViewById(R.id.contact_info_value);
        mSendSMS = view.findViewById(R.id.send_sms_to);
        mCopy = view.findViewById(R.id.copy);
    }


    public void setInfoData(DataSchema dataSchema, String value) {
        if (Employee.InfoType.DATE.equalsIgnoreCase(dataSchema.type)) {
            value = TimeUtil.getStringForMillis(Long.valueOf(value), TimeUtil.getTimeFormat1(BaseApplicationLike.baseContext));
        }

        mInfoKey.setText(AtworkUtil.getEmployeeDataSchemaAliasI18n(dataSchema));
        mInfoValue.setText(value);
    }

    public void setInfoData(DataSchema dataSchema, String key, String value) {
        if (Employee.InfoType.DATE.equalsIgnoreCase(dataSchema.type)) {
            value = TimeUtil.getStringForMillis(Long.valueOf(value), TimeUtil.getTimeFormat1(BaseApplicationLike.baseContext));
        }
        mInfoValue.setText(value);
        mInfoKey.setText(key);
    }

    //发送短信的点击事件的响应监听器
    public void registerMobileListenerAndRefreshUI(final Fragment fragment, final String mobile) {
        mInfoValue.setTextColor(SkinHelper.getSideTextColor());

        registerCallPhoneListener(fragment, mobile);

        mSendSMS.setVisibility(View.VISIBLE);
        mSendSMS.setOnClickListener(v -> {
            if (TextUtils.isEmpty(mobile)) {
                AtworkToast.showToast(getResources().getString(R.string.personal_info_no_mobile));

            } else {
                IntentUtil.sendSms(fragment.getActivity(), mobile);
            }
        });

        registerCopyListenerRefreshUI(fragment.getActivity(), mobile);

    }

    public void registerTelPhoneListenerAndRefreshUI(final Fragment fragment, final String mobile) {
        mInfoValue.setTextColor(SkinHelper.getSideTextColor());

        registerCallPhoneListener(fragment, mobile);
        mSendSMS.setVisibility(View.GONE);
        registerCopyListenerRefreshUI(fragment.getActivity(), mobile);

    }


    public void registerEmailListenerRefreshUI(final Activity activity, final String email) {
       mInfoValue.setTextColor(SkinHelper.getSideTextColor());

        mInfoValue.setOnClickListener(v -> IntentUtil.email(activity, email));

        registerCopyListenerRefreshUI(activity, email);
    }

    private void registerCopyListenerRefreshUI(final Activity activity, final String copyValue) {
        mCopy.setVisibility(View.VISIBLE);
        mCopy.setOnClickListener(v -> {
            ClipboardManager clip = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
            clip.setText(copyValue);
            AtworkToast.showToast(activity.getString(R.string.copy_success));
        });
    }

    private void registerCallPhoneListener(final Fragment fragment, final String num) {
        mInfoValue.setOnClickListener(v -> callPhone(fragment, num));
    }

    private void callPhone(final Fragment fragment, final String num) {
        IntentUtil.callPhoneJump(getContext(), num);
//        callPhoneDirectly(fragment, num);
    }

    private void callPhoneDirectly(Fragment fragment, String num) {
        PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(fragment, new String[]{android.Manifest.permission.CALL_PHONE}, new PermissionsResultAction() {
            @Override
            public void onGranted() {

                new AtworkAlertDialog(fragment.getActivity(), AtworkAlertDialog.Type.SIMPLE)
                        .setContent(String.format(fragment.getString(R.string.call_phone), num))
                        .setClickBrightColorListener(dialog -> IntentUtil.callPhoneDirectly(AtworkApplicationLike.baseContext, num))
                        .show();

            }

            @Override
            public void onDenied(String permission) {
                AtworkUtil.popAuthSettingAlert(fragment.getActivity(), permission);
            }
        });
    }

}
