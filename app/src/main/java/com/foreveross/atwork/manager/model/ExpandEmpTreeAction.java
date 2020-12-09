package com.foreveross.atwork.manager.model;

/**
 * Created by dasunsy on 2017/8/9.
 */

public class ExpandEmpTreeAction {

    public boolean mIsSelectMode;

    public Boolean mIsMandatoryFilterSenior;


    public boolean mViewAcl = true;



    public static ExpandEmpTreeAction newExpandEmpTreeAction() {
        return new ExpandEmpTreeAction();
    }

    public ExpandEmpTreeAction setSelectMode(boolean isSelectMode) {
        this.mIsSelectMode = isSelectMode;
        return this;
    }

    public ExpandEmpTreeAction setMandatoryFilterSenior(Boolean isMandatoryFilterSenior) {
        this.mIsMandatoryFilterSenior = isMandatoryFilterSenior;
        return this;
    }

    public ExpandEmpTreeAction setViewAcl(boolean viewAcl) {
        mViewAcl = viewAcl;
        return this;
    }
}
