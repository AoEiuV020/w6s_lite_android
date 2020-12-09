package com.foreveross.atwork.manager.model;

import android.content.Context;

import com.foreveross.atwork.infrastructure.model.i18n.I18nInfo;
import com.foreveross.atwork.infrastructure.model.orgization.Organization;

import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created by dasunsy on 16/6/14.
 */
public class ApplyingOrganization extends I18nInfo implements Comparable<ApplyingOrganization> {

    public Organization mOrg;
    public String mOrgCode;
    public String mOrgLogo;
    public String mApplicantId;
    public String mContent;
    public long mAppliedTime = -1;
    public String mOrgName;
    public String mEnOrgName;
    public String mTwOrgName;
    public List<String> mUnreadMsgIdList;


    @Nullable
    @Override
    public String getStringName() {
        return mOrgName;
    }

    @Nullable
    @Override
    public String getStringTwName() {
        return mTwOrgName;
    }

    @Nullable
    @Override
    public String getStringEnName() {
        return mEnOrgName;
    }


    public String getOrgNameI18n(Context context) {

        return getNameI18n(context);
    }

    @Override
    public int compareTo(ApplyingOrganization another) {
        int result;

        long resultLong = another.mAppliedTime - mAppliedTime;

        if (0 < resultLong) {
            result = 1;
        } else if (0 == resultLong) {
            result = 0;
        } else {
            result = -1;
        }

        return result;
    }
}
