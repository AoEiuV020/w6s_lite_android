package com.foreverht.workplus.module.contact.utlis

import android.text.TextUtils
import com.foreveross.atwork.infrastructure.model.orgization.DepartmentPath
import com.foreveross.atwork.infrastructure.utils.ListUtil

fun splitPathToList(fullPath: String, prefix: String): List<String> {
    var fullNamePath = fullPath
    if (TextUtils.isEmpty(fullNamePath)) {
        return mutableListOf()
    }
    //截取前后反斜杠
    if (fullNamePath.startsWith(prefix)) {
        fullNamePath = fullNamePath.substring(1, fullNamePath.length)
    }
    if (fullNamePath.endsWith(prefix)) {
        fullNamePath = fullNamePath.substring(0, fullNamePath.length - 1)
    }
    return fullNamePath.split(prefix)
}

fun makeDepartmentPathList(fullNamePath: String, path: String): Array<DepartmentPath> {
    val fullNameArray = splitPathToList(fullNamePath, "/")
    val fullNameIdArray = splitPathToList(path, "/")
    if (ListUtil.isEmpty(fullNameArray) || ListUtil.isEmpty(fullNameIdArray)) {
        return arrayOf()
    }
    val departmentPathList = ArrayList<DepartmentPath>()
    try {
        for ((index, pathName) in fullNameArray.withIndex()) {
            val departmentPath = DepartmentPath()
            departmentPath.mDepartmentPathName = pathName
            departmentPath.mDepartmentPathId = fullNameIdArray.get(index)
            departmentPathList.add(departmentPath)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return departmentPathList.toTypedArray()
}
