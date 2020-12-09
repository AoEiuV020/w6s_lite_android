package com.foreveross.atwork.modules.search.model

import android.os.Parcelable
import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.foreveross.atwork.modules.group.module.SelectToHandleAction
import kotlinx.android.parcel.Parcelize


@Parcelize
class NewSearchControlAction(
        var searchContentList: Array<SearchContent>? = null,

        var searchAction: SearchAction = SearchAction.DEFAULT,

        var selectToHandleAction: SelectToHandleAction? = null,

        var filterMe: Boolean = false,

        var searchAppForTargetOrgCode: String = StringUtils.EMPTY

) : Parcelable