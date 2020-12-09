package com.foreveross.atwork.component;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.model.Employee;
import com.foreveross.atwork.infrastructure.model.employee.DataSchema;
import com.foreveross.atwork.infrastructure.model.employee.EmployeePropertyRecord;
import com.foreveross.atwork.infrastructure.model.employee.Position;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.modules.aboutme.component.MyAccountEmployeeInfoItemView;
import com.foreveross.atwork.modules.aboutme.fragment.AvatarPopupFragment;
import com.foreveross.atwork.utils.AtworkUtil;
import com.foreveross.atwork.utils.EmployeeHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shadow on 2016/4/19.
 */
public class MyAccountEmployeePagerView extends LinearLayout {

    private Activity mActivity;

    private Employee mEmployee;

    private AvatarPopupFragment.OnPhotographPathListener mListener;

    private LinearLayout mItemLayout;


    public MyAccountEmployeePagerView(Context context) {
        super(context);
        mActivity = (Activity) context;
        initView();
    }

    public MyAccountEmployeePagerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mActivity = (Activity) context;
        initView();
    }

    private void initView() {
        LayoutInflater inflater = LayoutInflater.from(mActivity);
        View view = inflater.inflate(R.layout.tab_layout_item, this);
        mItemLayout = view.findViewById(R.id.tab_item_layout);

    }


    public void refreshDataSchema(User user, Employee employee, AvatarPopupFragment.OnPhotographPathListener onPhotoPathListener) {
        mEmployee = employee;
        mListener = onPhotoPathListener;
        List<DataSchema> dataSchemaList = new ArrayList<>();
        if (!ListUtil.isEmpty(mEmployee.dataSchemaList)) {
            dataSchemaList.addAll(mEmployee.dataSchemaList);

        }
        if (this.mEmployee == null || ListUtil.isEmpty(dataSchemaList)) {
            return;
        }

        mItemLayout.removeAllViews();

        for (int i = 0; i < dataSchemaList.size(); i++) {
            DataSchema dataSchema = dataSchemaList.get(i);
            parseSchema(user, dataSchema);
        }

        //隐藏最后个view 的分割线
        if (0 < mItemLayout.getChildCount()) {
            MyAccountEmployeeInfoItemView lastView = (MyAccountEmployeeInfoItemView) mItemLayout.getChildAt(mItemLayout.getChildCount() - 1);
            lastView.setDividerVisible(false);
        }

    }


    private void parseSchema(User user, DataSchema dataSchema) {

        if (dataSchema.mProperty == null || "avatar".equals(dataSchema.mProperty)) {
            return;
        }

        if (!EmployeeHelper.havingFieldByProperty(dataSchema.mProperty)) {
            //如果property映射不存在，尝试匹配record列表
            EmployeePropertyRecord record = EmployeeHelper.getEmployeePropertyRecordByProperty(mEmployee, dataSchema.mProperty, dataSchema.mId);
            handleEmployeeRecord(user, record, dataSchema);
            return;
        }

        Object obj = EmployeeHelper.getFieldValueByProperty(dataSchema.mProperty, mEmployee);

        String value = "";

        if ("positions".equalsIgnoreCase(dataSchema.mProperty)) {
            List<Position> valueList = (List) obj;
            if (ListUtil.isEmpty(valueList)) {
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
            if (!StringUtils.isEmpty(value)) {
                long longValue = Long.valueOf(value);
                if (0 == longValue) {
                    value = "";
                }
            }
        }


        if (shouldShowSchemaItemView(dataSchema, value)) {
            handleCommonSchemaUI(user, dataSchema, value);
        }
    }

    private boolean shouldShowSchemaItemView(DataSchema dataSchema, String value) {
        if(!AtworkConfig.EMPLOYEE_CONFIG.getShowSelfJobTitle() && "positions".equalsIgnoreCase(dataSchema.mProperty)) {
            return false;
        }

        if(dataSchema.mOpsable) {
            return true;
        }


        return !StringUtils.isEmpty(value);


    }


    private void handleCommonSchemaUI(User user, DataSchema dataSchema, String value) {
        MyAccountEmployeeInfoItemView item = new MyAccountEmployeeInfoItemView(mActivity);

        item.setMyAccountCommonInfo(mActivity, dataSchema, value, user, mEmployee);
        item.addListener(mListener);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mItemLayout.addView(item, params);
    }


    /**
     * 处理非雇员属性的自定义字段
     */
    private void handleEmployeeRecord(User user, EmployeePropertyRecord record, DataSchema dataSchema) {
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

        if (shouldShowSchemaItemView(dataSchema, value)) {
            handleEmployeeRecordUI(user, dataSchema, value);
        }

    }

    private void handleEmployeeRecordUI(User user, DataSchema dataSchema, String value) {
        MyAccountEmployeeInfoItemView item = new MyAccountEmployeeInfoItemView(mActivity);

        item.setMyAccountRecordInfo(mActivity, dataSchema, AtworkUtil.getEmployeeDataSchemaAliasI18n(dataSchema), value, mEmployee);
        item.addListener(mListener);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mItemLayout.addView(item, params);
    }


    public String getViewTitle() {
        return mEmployee.orgInfo.orgName;
    }

}
