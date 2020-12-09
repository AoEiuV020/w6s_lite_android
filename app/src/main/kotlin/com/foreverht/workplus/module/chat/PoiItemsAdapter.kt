package com.foreverht.workplus.module.chat

import android.view.View
import android.view.ViewGroup
import com.amap.api.location.AMapLocation
import com.amap.api.services.core.PoiItem
import com.foreverht.workplus.module.chat.component.PoiItemView
import com.foreveross.atwork.component.recyclerview.BaseQuickAdapter
import com.foreveross.atwork.component.recyclerview.BaseViewHolder

/**
 *  create by reyzhang22 at 2020-01-13
 */
class PoiItemsAdapter(dataList: MutableList<PoiItem>, myLocation: AMapLocation?) : BaseQuickAdapter<PoiItem, PoiViewHolder>(dataList) {

    private var selectedPoiItem: PoiItem? = null

    private var keyword = ""

    private var myLocation = myLocation

    override fun onCreateDefViewHolder(parent: ViewGroup?, viewType: Int): PoiViewHolder {
        var messageItemView = PoiItemView(mContext)

        return PoiViewHolder(messageItemView)
    }


    override fun convert(helper: PoiViewHolder, item: PoiItem) {
        helper.messageItemView.refreshView(item, myLocation, keyword)
        helper.messageItemView.setLocationSelected(item.poiId == selectedPoiItem?.poiId)
    }

    fun setSelectedItem(selectedPoiItem: PoiItem) {
        this.selectedPoiItem = selectedPoiItem
    }

    fun setKeyword(keyword: String) {
        this.keyword = keyword
    }

    fun setMyLocation(myLocation: AMapLocation?) {
        this.myLocation = myLocation
    }
}



class PoiViewHolder(itemView: View) : BaseViewHolder(itemView) {
    var messageItemView: PoiItemView = itemView as PoiItemView
}