package com.foreverht.workplus.module.contact.component

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.foreveross.atwork.infrastructure.model.orgization.DepartmentPath

class DepartmentPathAdapter(context: Context, departmentPathList: Array<DepartmentPath>) : ArrayAdapter<DepartmentPath>(context, 0, departmentPathList) {


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val departmentPath = getItem(position)
        var tempView = convertView
        if (tempView == null) {
            tempView = DepartmentPathItem(context)
        }
        val returnView = tempView as DepartmentPathItem
        returnView.showPath(departmentPath!!, position == (count - 1))
        return returnView
    }
}