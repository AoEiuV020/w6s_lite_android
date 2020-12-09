package com.foreveross.atwork.modules.meeting.adapter

import android.view.View
import android.view.ViewGroup
import com.foreveross.atwork.R
import com.foreveross.atwork.component.recyclerview.BaseQuickAdapter
import com.foreveross.atwork.component.recyclerview.BaseViewHolder
import com.foreveross.atwork.modules.common.component.CommonItemView
import com.foreveross.atwork.modules.meeting.model.ZoomInstantMeetingSelectMode

class ZoomInstantMeetingSelectModeAdapter(generalSettingList: List<ZoomInstantMeetingSelectMode>): BaseQuickAdapter<ZoomInstantMeetingSelectMode, ZoomInstantMeetingSelectModeItemViewHolder>(generalSettingList) {

    override fun onCreateDefViewHolder(parent: ViewGroup?, viewType: Int): ZoomInstantMeetingSelectModeItemViewHolder {
        val commonItemView = CommonItemView(mContext)
        val selectModeItemViewHolder = ZoomInstantMeetingSelectModeItemViewHolder(commonItemView)

        return selectModeItemViewHolder
    }

    override fun convert(helper: ZoomInstantMeetingSelectModeItemViewHolder, item: ZoomInstantMeetingSelectMode) {
        setLeftCommonName(helper, item)

    }



    private fun setLeftCommonName(helper: ZoomInstantMeetingSelectModeItemViewHolder, item: ZoomInstantMeetingSelectMode) {
        when (item) {
            ZoomInstantMeetingSelectMode.DISCUSSION -> helper.w6sSettingItemView.setCommonName(mContext.getString(R.string.select_members_in_discussion))
            ZoomInstantMeetingSelectMode.ORGANIZATION -> helper.w6sSettingItemView.setCommonName(mContext.getString(R.string.select_members_in_organization))
        }
    }



}


class ZoomInstantMeetingSelectModeItemViewHolder(view: View) : BaseViewHolder(view) {

    var w6sSettingItemView: CommonItemView = view as CommonItemView


}