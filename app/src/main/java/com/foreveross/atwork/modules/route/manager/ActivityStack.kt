package com.foreveross.atwork.modules.route.manager

import com.foreveross.atwork.modules.route.model.ActivityInfo

object ActivityStack {

    val activityInfoStack = ArrayList<ActivityInfo>()


    fun removeActivityInfo(id: Int): Boolean? {
        activityInfoStack.findLast {
            it.id == id

        }?.let {
            return activityInfoStack.remove(it)

        }

        return false

    }

    fun updateActivityInfo(updateInfo: ActivityInfo) {
        val info = activityInfoStack.findLast {
            it.id == updateInfo.id

        }

        if(null == info) {
            activityInfoStack.add(updateInfo)
        } else {
            info.let {
                it.tags = updateInfo.tags
                it.from  = updateInfo.from
            }
        }
    }
}