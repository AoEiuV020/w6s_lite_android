package com.foreverht.workplus.module.chat.component

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import com.amap.api.location.AMapLocation
import com.amap.api.maps.AMapUtils
import com.amap.api.maps.model.LatLng
import com.amap.api.services.core.PoiItem
import com.foreveross.atwork.R
import kotlinx.android.synthetic.main.component_poi_item.view.*
import kotlin.math.roundToInt

/**
 *  create by reyzhang22 at 2020-01-13
 */
class PoiItemView(context: Context) : RelativeLayout(context) {

    init {
        findViews(context)
    }

    private fun findViews(context: Context) {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.component_poi_item, this)

    }

    fun refreshView(poiItem: PoiItem, myLocation: AMapLocation?, keyword: String) {
        if (TextUtils.isEmpty(keyword)) {
            tv_aoi.text = poiItem.title
        }  else {
            val spannableString = SpannableString(poiItem.title)
            val start = poiItem.title.indexOf(keyword)
            if (start >= 0) {
                spannableString.setSpan(ForegroundColorSpan(context.resources.getColor(R.color.blue_lock)), start, start + keyword.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                tv_aoi.text = spannableString
            } else {
                tv_aoi.text = poiItem.title
            }

        }
        var distance = poiItem.distance
        if (myLocation != null) {
            val latLng1 = LatLng(poiItem.latLonPoint.latitude, poiItem.latLonPoint.longitude)
            val latLng2 = LatLng(myLocation.latitude, myLocation.longitude)
            distance = AMapUtils.calculateLineDistance(latLng1,latLng2).toInt()
        }

        var textDistance: String = if (distance < 100) String.format(context.getString(R.string.`in`), "100m") else if (distance > 1000) (((distance / 100f).roundToInt())/10f).toString() + "km" else distance.toString() + "m"
        tv_address.text = textDistance + " | " + poiItem.snippet
    }

    fun setLocationSelected(selected: Boolean) {
        iv_location_select.visibility = if (selected) View.VISIBLE else View.GONE
    }

}