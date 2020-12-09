package com.foreveross.atwork.utils;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.R;
import com.foreveross.atwork.component.CommonPopMainAndSubData;
import com.foreveross.atwork.component.CommonPopMainAndSubListDialogFragment;
import com.foreveross.atwork.infrastructure.model.Employee;
import com.foreveross.atwork.infrastructure.model.employee.DataSchema;
import com.foreveross.atwork.infrastructure.model.employee.EmployeePropertyRecord;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.infrastructure.utils.LogUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.manager.FriendManager;
import com.foreveross.atwork.manager.UserManager;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dasunsy on 16/6/21.
 */
public class EmployeeHelper {
    public static boolean havingFieldByProperty(String property) {
        if("en_name".equals(property)) {
            property = "alias";
        }

        return null != getFieldByProperty(StringUtils.camelName(property));
    }

    public static Field getFieldByProperty(String property) {
        if("en_name".equals(property)) {
            property = "alias";
        }

        Field field = null;
        try {
            field = Employee.class.getField(StringUtils.camelName(property));
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return field;
    }

    public static Object getFieldValueByProperty(String property, Employee employee) {
        Object valObj = null;

        try {
            Field field = getFieldByProperty(property);
            if (null != field) {
                valObj = field.get(employee);
            }

            if(null != valObj && valObj instanceof Boolean) {

                if ((Boolean) valObj) {
                    valObj = AtworkApplicationLike.getResourceString(R.string.common_yes);
                } else {
                    valObj = AtworkApplicationLike.getResourceString(R.string.common_no);

                }
            }

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return valObj;
    }

    /**
     * 根据自定义字段的权限, 返回邮箱地址集合
     * */
    public static List<String> getShowEmailList(Employee employee) {
        HashMap<DataSchema, String> infoTypeShowList = getInfoTypeShowList(employee, Employee.InfoType.EMAIL);
        return new ArrayList<>(infoTypeShowList.values());

    }

    /**
     * 根据自定义字段的权限, 返回手机电话号码集合
     * */
    public static List<String> getShowMobileList(Employee employee) {
        HashMap<DataSchema, String> infoTypeShowList = getShowMobileTypeSchemaMap(employee);

        return new ArrayList<>(infoTypeShowList.values());


    }


    public static HashMap<String, DataSchema> getDataSchemaStringHashMap(List<Employee> employeeList, @NonNull Employee.InfoType... dataSchemaTypes) {

        HashMap<DataSchema, String> allMobileMap = EmployeeHelper.getShowTypeSchemaMap(employeeList, dataSchemaTypes);
        HashMap<String, DataSchema> userEmployeeMobileMap = new HashMap<>();
        for(Map.Entry<DataSchema, String> entry : allMobileMap.entrySet()) {
            if(userEmployeeMobileMap.containsKey(entry.getValue())) {
                continue;
            }

            userEmployeeMobileMap.put(entry.getValue(), entry.getKey());
        }

        return userEmployeeMobileMap;
    }


    public static HashMap<DataSchema, String> getShowTypeSchemaMap(List<Employee> employeeList, @NonNull Employee.InfoType... dataSchemaTypes) {
        HashMap<DataSchema, String> allMobileMap = new HashMap<>();
        for(Employee employee : employeeList) {
            allMobileMap.putAll(EmployeeHelper.getInfoTypeShowList(employee, dataSchemaTypes));
        }
        return allMobileMap;
    }



    public static HashMap<DataSchema, String> getShowMobileTypeSchemaMap(Employee employee) {
        return getInfoTypeShowList(employee, Employee.InfoType.MOBILE_PHONE, Employee.InfoType.TEL_PHONE);
    }

    private static HashMap<DataSchema, String> getInfoTypeShowList(Employee employee, @NonNull Employee.InfoType... dataSchemaTypes) {

        HashMap<DataSchema, String> schemaValueMap = new HashMap<>();

        if (!ListUtil.isEmpty(employee.dataSchemaList)) {

            for(DataSchema dataSchema : employee.dataSchemaList) {
                if (DataSchema.FRIEND_VISIBLE_RANGE.equalsIgnoreCase(dataSchema.mVisibleRange) && !FriendManager.getInstance().containsKey(employee.userId) && !UserManager.getInstance().isMe(employee.userId)) {
                    continue;
                }
                boolean isTypeMatch = false;
                for(Employee.InfoType targetType : dataSchemaTypes) {
                    if(targetType.equalsIgnoreCase(dataSchema.type)) {
                        isTypeMatch = true;
                        break;
                    }
                }

                if(isTypeMatch) {
                    if(havingFieldByProperty(dataSchema.mProperty)) {
                        Object valueObj = getFieldValueByProperty(dataSchema.mProperty, employee);
                        if(null != valueObj && !StringUtils.isEmpty((String) valueObj)) {

                            schemaValueMap.put(dataSchema, (String) valueObj);
                        }

                    } else {
                        //如果property映射不存在，尝试匹配record列表
                        EmployeePropertyRecord record = getEmployeePropertyRecordByProperty(employee, dataSchema.mProperty, dataSchema.mId);
                        if (null != record) {
                            String value = record.mValue;
                            if(!StringUtils.isEmpty(value)) {

                                schemaValueMap.put(dataSchema, value);


                            }
                        }
                    }

                }
            }
        }


        return schemaValueMap;
    }


    public static EmployeePropertyRecord getEmployeePropertyRecordByProperty(Employee employee, String property, String dataSchemaId) {
        if (ListUtil.isEmpty(employee.properties)) {
            return null;
        }
        for (EmployeePropertyRecord record : employee.properties) {
            if (record == null) {
                continue;
            }
            record.mEmployeeId = String.valueOf(employee.id);
            if (property.equalsIgnoreCase(record.mProperty) || dataSchemaId.equalsIgnoreCase(record.mDataSchemaId)) {
                return record;
            }
        }
        return null;
    }

    public static void callPhone(FragmentActivity activity, List<Employee> employeeList) {
        HashMap<String, DataSchema> userEmployeeMobileMap = getMobileDataSchemaStringHashMap(employeeList);


        if(1 == userEmployeeMobileMap.size()) {
            callPhone(activity, userEmployeeMobileMap.keySet().iterator().next());


        } else {
            CommonPopMainAndSubData commonPopMainAndSubData = new CommonPopMainAndSubData();
            for(Map.Entry<String, DataSchema> entry : userEmployeeMobileMap.entrySet()) {
                commonPopMainAndSubData.getItemMainContentList().add(entry.getKey());
                commonPopMainAndSubData.getItemSubContentList().add(entry.getValue().mAlias);
            }

            CommonPopMainAndSubListDialogFragment commonPopMainAndSubListDialogFragment = new CommonPopMainAndSubListDialogFragment();
            commonPopMainAndSubListDialogFragment.setData(commonPopMainAndSubData);
            commonPopMainAndSubListDialogFragment.setOnClickItemListener((position, value) -> {

                callPhone(activity, value);
                commonPopMainAndSubListDialogFragment.dismiss();

            });

            commonPopMainAndSubListDialogFragment.show(activity.getSupportFragmentManager(), "mobileList");

            LogUtil.e("mUserEmployeeMobileMap size -> " + userEmployeeMobileMap.size());
        }
    }


    public static HashMap<String, DataSchema> getEmailDataSchemaStringHashMap(List<Employee> employeeList) {
        return getDataSchemaStringHashMap(employeeList, Employee.InfoType.EMAIL);
    }

    public static HashMap<String, DataSchema> getMobileDataSchemaStringHashMap(List<Employee> employeeList) {

        return  getDataSchemaStringHashMap(employeeList, Employee.InfoType.MOBILE_PHONE, Employee.InfoType.TEL_PHONE);
    }


    private static void callPhone(Activity activity, String value) {
        IntentUtil.callPhoneJump(activity, value);

//        callPhoneDirectly(value);
    }
}
