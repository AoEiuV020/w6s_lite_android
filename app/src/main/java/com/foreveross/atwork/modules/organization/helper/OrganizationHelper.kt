@file: JvmName("OrganizationHelper")

package com.foreveross.atwork.modules.organization.helper

import androidx.fragment.app.FragmentActivity
import com.foreverht.workplus.ui.component.dialogFragment.CommonPopSelectData
import com.foreverht.workplus.ui.component.dialogFragment.W6sSelectDialogFragment
import com.foreveross.atwork.infrastructure.model.orgization.Organization
import com.foreveross.atwork.infrastructure.utils.extension.asType
import com.foreveross.atwork.manager.OrganizationManager

fun popOrgSelect(context: FragmentActivity, onSelectOrg: (Organization) -> Unit) {
    OrganizationManager.getInstance().getLocalOrganizations(context) { result ->
        result.getOrNull(0)
                ?.asType<List<Organization>>()
                ?.let { orgList ->
                    W6sSelectDialogFragment()
                            .setData(CommonPopSelectData(orgList.map { it.getNameI18n(context) }, null))
                            .setOnClickItemListener(object : W6sSelectDialogFragment.OnClickItemListener {
                                override fun onClick(position: Int, value: String) {
                                    onSelectOrg(orgList[position])
                                }

                            })
                            .show(context.supportFragmentManager, "orgSelect")

                }

    }
}

