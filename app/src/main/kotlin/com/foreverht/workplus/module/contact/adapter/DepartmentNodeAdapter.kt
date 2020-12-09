package com.foreverht.workplus.module.contact.adapter

import android.view.View
import android.view.ViewGroup
import com.foreverht.workplus.module.contact.component.DepartmentNodeItem
import com.foreveross.atwork.api.sdk.organization.responseModel.OrganizationResult
import com.foreveross.atwork.component.recyclerview.BaseQuickAdapter
import com.foreveross.atwork.component.recyclerview.BaseViewHolder

class DepartmentNodeAdapter(nodeList: MutableList<OrganizationResult>): BaseQuickAdapter<OrganizationResult, DepartmentNodeHolder>(nodeList) {

    override fun onCreateDefViewHolder(parent: ViewGroup?, viewType: Int): DepartmentNodeHolder {
        val departmentNodeItem = DepartmentNodeItem(mContext)
        return DepartmentNodeHolder(departmentNodeItem)
    }

    override fun convert(helper: DepartmentNodeHolder?, item: OrganizationResult) {
        helper?.departmentNodeItem?.setNodeName(item.name)
    }

}

class DepartmentNodeHolder(itemView: View) : BaseViewHolder(itemView) {
    var departmentNodeItem: DepartmentNodeItem = itemView as DepartmentNodeItem
}