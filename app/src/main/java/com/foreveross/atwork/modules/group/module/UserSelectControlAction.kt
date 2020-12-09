package com.foreveross.atwork.modules.group.module

import android.os.Parcelable
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.BaseApplicationLike
import com.foreveross.atwork.infrastructure.model.ShowListItem
import com.foreveross.atwork.infrastructure.model.orgization.Scope
import com.foreveross.atwork.infrastructure.model.user.SelectedContactList
import com.foreveross.atwork.infrastructure.support.AtworkConfig
import com.foreveross.atwork.infrastructure.utils.ListUtil
import com.foreveross.atwork.infrastructure.utils.SetUtil
import com.foreveross.atwork.modules.group.activity.UserSelectActivity
import com.foreveross.atwork.modules.voip.utils.VoipHelper
import kotlinx.android.parcel.Parcelize


@Parcelize
class UserSelectControlAction(

        var selectMode: UserSelectActivity.SelectMode = UserSelectActivity.SelectMode.SELECT,

        var selectToHandleAction: SelectToHandleAction? = null,

        private var _selectedContactIds: List<String>? = null,

        private var _selectScopeSet: Set<Scope>? = null,

        var isNeedSetNotAllowList: Boolean = true,

        var isSelectCanNoOne: Boolean = false,

        var isSuggestiveHideMe: Boolean = true,

        var isSendModeNeedJumpChatDetail: Boolean = true,

        var fromTag: String? = null,

        var directOrgCode: String? = null,

        var directOrgShow: Boolean = false,

        var selectAction: UserSelectActivity.SelectAction? = null,

        var isMandatoryFilterSenior: Boolean? = null,

        var callbackContactsSelected: ArrayList<out ShowListItem>? = null,

        var needCacheForCordova: Boolean = false,

        private var _max: Int = -1,

        private var _maxTip: String? = null


) : Parcelable {

    fun setSelectedContacts(selectedContacts: List<ShowListItem>?) {
        _selectedContactIds = selectedContacts?.map { it.id }
        if (!ListUtil.isEmpty(selectedContacts)) {
            SelectedContactList.setContactList(selectedContacts)
        }
    }




    var selectScopeSet:  Set<Scope>?
    get() {
        return _selectScopeSet
    }
    set(value) {
        _selectScopeSet = value
        if (!SetUtil.isEmpty(value)) {
            SelectedContactList.setScopeList(value!!.toList())
        }
    }

    var max: Int
    get() {
      if(-1 == _max) {
          when(selectAction) {

              UserSelectActivity.SelectAction.DISCUSSION -> {
                  return  AtworkConfig.DISCUSSION_MEMBER_COUNT_MAX
              }


              UserSelectActivity.SelectAction.VOIP -> {
                  return AtworkConfig.VOIP_MEMBER_COUNT_MAX

              }

              else -> {
                  return Integer.MAX_VALUE
              }

          }

      }

        return _max
    }

    set(value) {
        _max = value
    }

    var maxTip: String?
        get() {
            if (null == _maxTip) {

                when(selectAction) {
                    UserSelectActivity.SelectAction.DISCUSSION -> {
                        return BaseApplicationLike.baseContext.getString(R.string.discussion_max_user_alert)
                    }


                    UserSelectActivity.SelectAction.VOIP -> {
                        var maxUseInTip = AtworkConfig.VOIP_MEMBER_COUNT_MAX
                        if (isNeedSetNotAllowList && null != _selectedContactIds) {
                            maxUseInTip = AtworkConfig.VOIP_MEMBER_COUNT_MAX - _selectedContactIds!!.size
                        }

                        return VoipHelper.getMaxTip(BaseApplicationLike.baseContext, maxUseInTip)
                    }

                }


                return BaseApplicationLike.baseContext.getString(R.string.select_contact_max_tip, max)
            }

            return _maxTip
        }
        set(value) {
            _maxTip = value
        }


}