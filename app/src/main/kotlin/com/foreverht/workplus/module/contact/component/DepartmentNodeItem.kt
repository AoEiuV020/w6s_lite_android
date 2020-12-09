package com.foreverht.workplus.module.contact.component

import android.content.Context
import android.view.LayoutInflater
import android.widget.RelativeLayout
import android.widget.TextView
import com.foreveross.atwork.R

class DepartmentNodeItem(context: Context) : RelativeLayout(context) {

    private var nodeName:  TextView

    init {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.item_department_node, this)
        nodeName = view.findViewById(R.id.department_node_name)
    }

    fun setNodeName(name: String) {
        nodeName.text = name
    }
}