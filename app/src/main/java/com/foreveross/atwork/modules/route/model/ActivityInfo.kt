package com.foreveross.atwork.modules.route.model

import android.app.Activity
import com.foreveross.atwork.infrastructure.utils.ListUtil

class ActivityInfo @JvmOverloads constructor(

        var id: Int? = null,

        var name: String,

        var tags: List<String> = ArrayList(),

        var from: String = "default"

) {

    constructor(activity: Activity, tag: String) : this(activity.hashCode(), activity.localClassName, ListUtil.makeSingleList(tag))


    companion object {

        const val INTENT_DATA_KEY_FROM = "from_source"

        const val INTENT_DATA_KEY_TAG = "activity_tag"

        const val INTENT_DATA_KEY_TAGS = "activity_tags"

    }

    override fun toString(): String {
        return "ActivityInfo(id=$id, name='$name', tags=$tags, from='$from')"
    }

}

