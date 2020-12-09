package com.foreveross.atwork.modules.group.listener;

import com.foreveross.atwork.api.sdk.organization.requstModel.QureyOrganizationViewRange;
import com.foreveross.atwork.api.sdk.organization.responseModel.OrganizationResult;

/**
 * Created by lingen on 15/4/24.
 * Description:
 */
public interface DeptSelectedListener {

    void select(OrganizationResult organization, final QureyOrganizationViewRange range);

}
