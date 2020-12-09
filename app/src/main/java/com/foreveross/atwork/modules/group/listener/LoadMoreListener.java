package com.foreveross.atwork.modules.group.listener;

import com.foreveross.atwork.api.sdk.organization.requstModel.QureyOrganizationViewRange;
import com.foreveross.atwork.infrastructure.model.ContactModel;

public interface LoadMoreListener {
    void onLoadMore(ContactModel model, QureyOrganizationViewRange range);
}
