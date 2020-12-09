package com.foreveross.atwork.modules.discussion.model

import android.os.Parcelable
import com.foreveross.atwork.infrastructure.model.ShowListItem
import com.foreveross.atwork.infrastructure.model.user.SelectedContactList
import com.foreveross.atwork.infrastructure.utils.ListUtil
import com.foreveross.atwork.modules.contact.adapter.ContactListArrayListAdapter
import com.foreveross.atwork.modules.group.activity.UserSelectActivity
import kotlinx.android.parcel.Parcelize


@Parcelize
class DiscussionMemberSelectControlAction(

        var discussionId: String? = null,

        private var _selectedContacts: List<ShowListItem>? = null,

        var selectMode: Int = -1,

        var isSelectedAllowedRemove: Boolean = false,

        var displayMode: Int = ContactListArrayListAdapter.MODE_NORMAL,

        var filterMe: Boolean = true

) : Parcelable {

    var selectedContacts: List<ShowListItem>?
        get() {
            return _selectedContacts
        }
        set(value) {
            _selectedContacts = value
            if (!ListUtil.isEmpty(value)) {
                SelectedContactList.setContactList(value)
            }
        }
}