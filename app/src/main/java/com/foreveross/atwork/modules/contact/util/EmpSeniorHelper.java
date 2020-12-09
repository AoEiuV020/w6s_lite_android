package com.foreveross.atwork.modules.contact.util;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.foreveross.atwork.infrastructure.manager.OrganizationSettingsManager;
import com.foreveross.atwork.infrastructure.model.Employee;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.manager.EmployeeManager;
import com.foreveross.atwork.manager.model.ExpandEmpTreeAction;

/**
 * Created by dasunsy on 2017/8/11.
 */

public class EmpSeniorHelper {
    @Nullable
    public static Boolean getFilterSeniorAction(Context context, @NonNull ExpandEmpTreeAction expandEmpTreeAction, String orgCode) {
        Boolean filterSenior = null;
        if(null != expandEmpTreeAction.mIsMandatoryFilterSenior) {
            filterSenior = expandEmpTreeAction.mIsMandatoryFilterSenior;

        } else {
            String userId = LoginUserInfo.getInstance().getLoginUserId(context);
            Employee loginSelectedEmp = EmployeeManager.getInstance().queryEmpInSync(context, userId, orgCode);
            if(null == loginSelectedEmp) {
                filterSenior = null;

            } else {
                if(loginSelectedEmp.senior) {
                    filterSenior = false;
                } else {
                    if(OrganizationSettingsManager.getInstance().isSeniorShowOpen(context, orgCode)) {
                        if(expandEmpTreeAction.mIsSelectMode) {
                            filterSenior = true;

                        } else {
                            filterSenior = false;

                        }
                    } else {
                        filterSenior = true;

                    }

                }

            }


        }
        return filterSenior;
    }



    @Nullable
    public static Boolean getSearchFilterSeniorAction(Context context, @NonNull ExpandEmpTreeAction expandEmpTreeAction, String orgCode) {
        if(null != expandEmpTreeAction.mIsMandatoryFilterSenior) {
            return expandEmpTreeAction.mIsMandatoryFilterSenior;

        }


        if(expandEmpTreeAction.mIsSelectMode) {
            return false;
        }

        Boolean filterSenior = null;

        String userId = LoginUserInfo.getInstance().getLoginUserId(context);
        Employee loginSelectedEmp = EmployeeManager.getInstance().queryEmpInSync(context, userId, orgCode);
        if(null == loginSelectedEmp) {
            filterSenior = null;

        } else {
            if(loginSelectedEmp.senior) {
                filterSenior = false;
            } else {
                if(OrganizationSettingsManager.getInstance().isSeniorShowOpen(context, orgCode)) {
                    filterSenior = false;

                } else {
                    filterSenior = true;

                }

            }

        }



        return filterSenior;
    }


}
