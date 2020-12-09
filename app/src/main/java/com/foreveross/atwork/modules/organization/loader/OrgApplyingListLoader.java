package com.foreveross.atwork.modules.organization.loader;

import android.content.Context;

import androidx.loader.content.AsyncTaskLoader;

import com.foreverht.db.service.repository.OrganizationRepository;
import com.foreveross.atwork.infrastructure.model.orgization.Organization;
import com.foreveross.atwork.manager.OrgApplyManager;
import com.foreveross.atwork.manager.OrganizationManager;
import com.foreveross.atwork.manager.model.ApplyingOrganization;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lingen on 15/4/24.
 * Description:
 */
public class OrgApplyingListLoader extends AsyncTaskLoader<List<ApplyingOrganization>> {

    private Context mContext;
    public OrgApplyingListLoader(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public List<ApplyingOrganization> loadInBackground() {

        List<ApplyingOrganization> originList = OrganizationManager.getInstance().queryApplyingListBySystemMsgSync(getContext());
        List<String> adminOrgListCache = OrganizationRepository.getInstance().queryLoginAdminOrgSync(getContext());


        List<ApplyingOrganization> convertedApplyOrganizationList = new ArrayList<>();

        int adminSize = adminOrgListCache.size();

        for (int i = 0; i < adminSize; i++) {

            String orgCode = adminOrgListCache.get(i);
            ApplyingOrganization applyOrg = findApplyOrgInOriginList(orgCode, originList);
            if (applyOrg != null) {
                convertedApplyOrganizationList.add(applyOrg);

            } else {
                Organization organization = OrganizationManager.getInstance().getOrganizationSyncByOrgCode(mContext, orgCode);
                convertedApplyOrganizationList.add(OrgApplyManager.getInstance().convertModelToApplyOrganization(organization));
            }

        }


        return convertedApplyOrganizationList;
    }


    private ApplyingOrganization findApplyOrgInOriginList(String orgCode, List<ApplyingOrganization> originList) {
        for (ApplyingOrganization applyingOrganization : originList) {
            if (applyingOrganization.mOrgCode.equalsIgnoreCase(orgCode)) {
                return applyingOrganization;
            }
        }
        return null;
    }


    @Override
    public void deliverResult(List<ApplyingOrganization> data) {
        super.deliverResult(data);

    }


}
