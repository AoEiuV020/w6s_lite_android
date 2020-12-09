package com.foreveross.atwork.modules.discussion.model

import android.os.Parcelable
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.BaseApplicationLike
import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.foreveross.atwork.modules.group.module.SelectToHandleAction
import kotlinx.android.parcel.Parcelize

@Parcelize
class DiscussionSelectControlAction(var max: Int = -1,
                                    private var _maxTip: String? = null,
                                    var viewTitle: String = StringUtils.EMPTY,
                                    var needSearchBtn: Boolean = true,
                                    var selectToHandleAction: SelectToHandleAction? = null,
                                    var discussionIdListPreSelected: List<String>? = null) : Parcelable {



    var maxTip: String?
    set(value) {
        _maxTip = value
    }

    get() {
        if(null == _maxTip) {
            return BaseApplicationLike.baseContext.getString(R.string.select_contact_max_tip, max)
        }

        return _maxTip
    }
}