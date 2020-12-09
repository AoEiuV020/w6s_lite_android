package com.foreveross.atwork.modules.main.helper;

import android.app.Activity;
import android.view.View;

import androidx.fragment.app.Fragment;

import com.foreverht.workplus.ui.component.dialogFragment.CommonPopSelectData;
import com.foreverht.workplus.ui.component.dialogFragment.W6sSelectDialogFragment;
import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.orgization.Organization;
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.manager.OrganizationManager;
import com.foreveross.atwork.modules.aboutme.fragment.AboutMeFragment;
import com.foreveross.atwork.modules.app.fragment.AppFragment;
import com.foreveross.atwork.modules.app.util.AppRefreshHelper;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by reyzhang22 on 17/8/16.
 */

public class OrgSwitcherHelper {

    public static void checkShowOrgSwitcher(View... orgSwitchers) {
        if(null == orgSwitchers) {
            return;
        }

        OrganizationManager.getInstance().queryLoginOrgCodeListTryCache(result -> {

            for(View orgSwitch : orgSwitchers) {
                if(null == orgSwitch) {
                    continue;
                }

                if(null != result && 1 < result.size()) {

                    orgSwitch.setVisibility(View.VISIBLE);
                } else {
                    orgSwitch.setVisibility(View.GONE);

                }

            }
        });


    }

    public static void setOrgPopupView(Activity activity, Fragment fragment) {
        OrganizationManager manager = OrganizationManager.getInstance();
        manager.getLocalOrganizations(activity, localData -> {
            if (localData == null) {
                return;
            }
            List<Organization> organizations = (List<Organization>) localData[0];
            if (ListUtil.isEmpty(organizations)) {
                return;
            }

            //popupOrgSelectView(activity, fragment,organizations);
            OrgSelectDialog(activity, fragment,organizations);

        });

    }

    private static void OrgSelectDialog(Activity activity, Fragment fragment, List<Organization> organizations) {

        String currentOrgCode = PersonalShareInfo.getInstance().getCurrentOrg(activity);
        String currentOrgName = StringUtils.EMPTY;
        List<String> orgNameLists = new ArrayList<>();
        List<String> orgOrgLists = new ArrayList<>();
        //各组织的名字
        for (Organization organization : organizations) {
            orgNameLists.add(organization.getNameI18n(BaseApplicationLike.baseContext));
            orgOrgLists.add(organization.mOrgCode);
            if (currentOrgCode.equalsIgnoreCase(organization.mOrgCode)) {
                currentOrgName = organization.getNameI18n(BaseApplicationLike.baseContext);
            }
        }
        W6sSelectDialogFragment w6sSelectDialogFragment = new W6sSelectDialogFragment();
        w6sSelectDialogFragment.setTitle(activity.getString(R.string.switch_orgs))
                .setData(new CommonPopSelectData(orgNameLists, currentOrgName))
                .setOnClickItemListener((position, value) -> {

                    w6sSelectDialogFragment.dismiss();

//            OrganizationSettingsHelper.getInstance().setCurrentOrgCodeAndRefreshSetting(activity, organization.mOrgCode, true);
                    if (fragment instanceof AppFragment) {
                        AppRefreshHelper.refreshAppAbsolutely(PersonalShareInfo.getInstance().getCurrentOrg(BaseApplicationLike.baseContext), true);
                        ((AppFragment) fragment).setTitle();
                    }


                    if (fragment instanceof AboutMeFragment) {
                        AboutMeFragment.refreshUserMsg();
                        ((AboutMeFragment) fragment).refreshColleagueLightNoticeModel();
                    }





                })
                .show(fragment.getFragmentManager(), "org_switch");
    }



}
