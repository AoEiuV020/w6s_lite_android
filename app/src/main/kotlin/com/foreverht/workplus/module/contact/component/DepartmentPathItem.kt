package com.foreverht.workplus.module.contact.component

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.model.orgization.DepartmentPath

class DepartmentPathItem(context: Context): LinearLayout(context) {

    private var pathName:  TextView
    private var pathArrow: ImageView

    init {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.item_department_path, this)
        pathName = view.findViewById(R.id.department_path_name)
        pathArrow = view.findViewById(R.id.department_path_arrow)
    }

    fun showPath(departmentPath: DepartmentPath, isLastChild: Boolean) {
        pathName.text = departmentPath.mDepartmentPathName
        if (isLastChild) {
            pathName.setTextColor(resources.getColor(R.color.common_text_color))
            pathArrow.visibility = View.GONE
        } else {
            pathName.setTextColor(resources.getColor(R.color.blue_lock))
            pathArrow.visibility = View.VISIBLE
        }
    }
}