package com.foreverht.workplus.module.contact.adapter

import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import com.foreveross.atwork.api.sdk.organization.responseModel.EmployeeResult
import com.foreveross.atwork.component.recyclerview.BaseQuickAdapter
import com.foreveross.atwork.component.recyclerview.BaseViewHolder
import com.foreveross.atwork.infrastructure.utils.ListUtil
import com.foreveross.atwork.modules.contact.component.ContactListItemView
import com.foreveross.atwork.modules.contact.util.ContactInfoViewUtil

class DepartmentEmployeeAdapter (employeeResults: MutableList<EmployeeResult>): BaseQuickAdapter<EmployeeResult, DepartmentEmployeeHolder>(employeeResults) {

    override fun onCreateDefViewHolder(parent: ViewGroup?, viewType: Int): DepartmentEmployeeHolder {
        val employeeItem = ContactListItemView(mContext)
        return DepartmentEmployeeHolder(employeeItem)
    }

    override fun convert(helper: DepartmentEmployeeHolder?, item: EmployeeResult) {
        helper?.apply {
            ContactInfoViewUtil.dealWithContactInitializedStatus(contactItem.avatarView, contactItem.titleView, item, true, true)

            var fullNamePath = ""
            if (!ListUtil.isEmpty(item.positions)) {
                fullNamePath = item.positions[0].convertFullNamePath()
            }
            if (TextUtils.isEmpty(fullNamePath)) {
                contactItem.infoView?.visibility = View.GONE
            } else {
                contactItem.infoView?.visibility = View.VISIBLE
                contactItem.infoView?.text = fullNamePath
            }
        }
    }

}

class DepartmentEmployeeHolder(itemView: View) : BaseViewHolder(itemView) {
    var contactItem: ContactListItemView = itemView as ContactListItemView
}