package com.foreveross.atwork.modules.contact.component;/**
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
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 *                       __           __
 * .--.--.--.-----.----.|  |--.-----.|  |.--.--.-----.
 * |  |  |  |  _  |   _||    <|  _  ||  ||  |  |__ --|
 * |________|_____|__|  |__|__|   __||__||_____|_____|
 *                            |__|
 */


import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.manager.OrganizationSettingsManager;
import com.foreveross.atwork.infrastructure.model.Employee;
import com.foreveross.atwork.infrastructure.model.WebViewControlAction;
import com.foreveross.atwork.infrastructure.model.employee.DataSchema;
import com.foreveross.atwork.infrastructure.model.employee.EmployeePropertyRecord;
import com.foreveross.atwork.infrastructure.model.employee.Position;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.manager.FriendManager;
import com.foreveross.atwork.manager.UserManager;
import com.foreveross.atwork.modules.app.activity.WebViewActivity;
import com.foreveross.atwork.utils.EmployeeHelper;
import com.foreveross.atwork.utils.watermark.WaterMarkHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by reyzhang22 on 16/5/19.
 */
public class PersonInfoPagerView extends LinearLayout{

    private Activity mActivity;

    private Fragment mFragment;

    private Employee mEmployee;

    private LinearLayout mItemLayout;

    private TextView mLogOutView;


    public PersonInfoPagerView(Activity context, Fragment fragment) {
        super(context);
        mActivity = context;
        initView();
        mFragment = fragment;
    }


    private void initView() {
        LayoutInflater inflater = LayoutInflater.from(mActivity);
        View view = inflater.inflate(R.layout.tab_layout_item, this);
        mItemLayout = view.findViewById(R.id.tab_item_layout);
        mLogOutView = view.findViewById(R.id.my_account_logout);
        mLogOutView.setVisibility(View.GONE);
    }



    public void refreshDataSchema(Employee employee, List<DataSchema> dataSchemas) {
        mEmployee = employee;
        List<DataSchema> dataSchemaList = new ArrayList<>();
        if (!ListUtil.isEmpty(dataSchemas)) {
            dataSchemaList.addAll(dataSchemas);

        }
        if (this.mEmployee == null || ListUtil.isEmpty(dataSchemaList)) {
            return;
        }

        mItemLayout.removeAllViews();
        for (int i = 0; i < dataSchemaList.size(); i++) {
            DataSchema dataSchema = dataSchemaList.get(i);
            parseSchema(dataSchema);
        }

        if (!showCircle()) {
            showWatermark(employee);
            return;
        }


        showWatermark(employee);
    }

    private boolean showCircle() {

        if(AtworkConfig.CIRCLE_CONFIG.isCommandCircleEntryShowInPersonalInfoView()) {
            return true;
        }

        return OrganizationSettingsManager.getInstance().handleMyCircleFeature(mEmployee.orgCode);
    }

    private void showWatermark(Employee employee) {
        String result = OrganizationSettingsManager.getInstance().getOrganizationWatermarkSettings(employee.orgCode);
        if ("none".equalsIgnoreCase(result)) {
            return;
        }
        WaterMarkHelper.setEmployeeWatermarkByOrgId(BaseApplicationLike.baseContext, mItemLayout, employee.orgCode);
    }

    private void parseSchema(DataSchema dataSchema) {

        if (dataSchema.mProperty == null || "avatar".equals(dataSchema.mProperty)){
            return;
        }

        if (!EmployeeHelper.havingFieldByProperty(dataSchema.mProperty)) {
            //如果property映射不存在，尝试匹配record列表
            EmployeePropertyRecord record = EmployeeHelper.getEmployeePropertyRecordByProperty(mEmployee, dataSchema.mProperty, dataSchema.mId);
            handleEmployeeRecord(record, dataSchema);
            return;
        }

        Object obj = EmployeeHelper.getFieldValueByProperty(dataSchema.mProperty, mEmployee);

        String value = "";

        if ("positions".equalsIgnoreCase(dataSchema.mProperty)) {
            List<Position> valueList = (List) obj;
            if (!AtworkConfig.EMPLOYEE_CONFIG.getShowPeerJobTitle() || ListUtil.isEmpty(valueList)) {
                return;
            }

            value = mEmployee.getPositionThreeShowStr();

        } else if ("gender".equalsIgnoreCase(dataSchema.mProperty)) {
            if (null != obj) {
                value = String.valueOf(obj);
            }

            if ("unknown".equalsIgnoreCase(value)) {
                value = "";
            }

        } else {
            if (null != obj) {
                value = String.valueOf(obj);
            }
        }


        if (Employee.InfoType.DATE.equalsIgnoreCase(dataSchema.type)) {
            if (!StringUtils.isEmpty(value)){
                long longValue = Long.valueOf(value);
                if (0 == longValue) {
                    value = "";
                }
            }
        }


        if (!StringUtils.isEmpty(value)) {
            handleCommonSchemaUI(dataSchema, value);
        }
    }

    private void handleEmployeeRecordUI(DataSchema dataSchema, String value) {
        ContactInfoItemView item = new ContactInfoItemView(mActivity);
        item.setInfoData(dataSchema, value);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mItemLayout.addView(item, params);
        handleActionByType(item, dataSchema, value);
    }


    private void handleCommonSchemaUI(DataSchema dataSchema, String value) {
        if (DataSchema.FRIEND_VISIBLE_RANGE.equalsIgnoreCase(dataSchema.mVisibleRange) && !FriendManager.getInstance().containsKey(mEmployee.userId) && !UserManager.getInstance().isMe(mEmployee.userId)) {
            return;
        }
        ContactInfoItemView item = new ContactInfoItemView(mActivity);
        item.setInfoData(dataSchema, value);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mItemLayout.addView(item, params);
        handleActionByType(item, dataSchema, value);
    }

    private void handleActionByType(ContactInfoItemView item, DataSchema dataSchema, String value) {
        if (Employee.InfoType.MOBILE_PHONE.equalsIgnoreCase(dataSchema.type)) {
            item.registerMobileListenerAndRefreshUI(mFragment, value);

        }
        else if (Employee.InfoType.TEL_PHONE.equalsIgnoreCase(dataSchema.type)) {
            item.registerTelPhoneListenerAndRefreshUI(mFragment, value);

        }
        else if (Employee.InfoType.EMAIL.equalsIgnoreCase(dataSchema.type)) {
            item.registerEmailListenerRefreshUI(mActivity, value);
        }
    }

    private void handleEmployeeRecord(EmployeePropertyRecord record, DataSchema dataSchema) {
        String value = "";
        if (null != record) {
            value = record.mValue;

            if (Employee.InfoType.DATE.equalsIgnoreCase(dataSchema.type)) {
                long longValue = Long.valueOf(value);
                if (0 == longValue) {
                    value = "";
                }
            }

        }

        if (!StringUtils.isEmpty(value)) {
            handleEmployeeRecordUI(dataSchema, value);
        }

    }


    public String getViewTitle() {
        return mEmployee.orgInfo.orgName;
    }
}
