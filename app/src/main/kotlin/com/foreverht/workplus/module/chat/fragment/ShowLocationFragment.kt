package com.foreverht.workplus.module.chat.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.amap.api.maps.AMap
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.MapView
import com.amap.api.maps.model.BitmapDescriptorFactory
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.Marker
import com.amap.api.maps.model.MarkerOptions
import com.foreverht.workplus.module.chat.activity.BUNDLE_LOCATION_INFO
import com.foreveross.atwork.AtworkApplicationLike
import com.foreveross.atwork.R
import com.foreveross.atwork.component.WorkplusBottomPopDialog
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ShareChatMessage
import com.foreveross.atwork.infrastructure.utils.CommonUtil
import com.foreveross.atwork.infrastructure.utils.ListUtil
import com.foreveross.atwork.support.BackHandledFragment
import kotlinx.android.synthetic.main.fragment_show_location.*

/**
 *  create by reyzhang22 at 2020-01-15
 */
const val PACKAGE_NAME_AMAP = "com.autonavi.minimap"
const val PACKAGE_NAME_TENCENT = "com.tencent.map"
const val PACKAGE_NAME_BAIDU = "com.baidu.BaiduMap"
const val PACKAGE_NAME_GOOGLE = "com.google.android.apps.maps"
class ShowLocationFragment : BackHandledFragment() {


    private lateinit var tvTitle: TextView
    private lateinit var ivBack: ImageView

    private lateinit var mapView: MapView

    private lateinit var aMap: AMap

    private lateinit var locationMarker: Marker
    private lateinit var latLng: LatLng

    private val appNames = mutableListOf<String>()

    private lateinit var popupDialog: WorkplusBottomPopDialog

    private val handler = @SuppressLint("HandlerLeak")
    object: Handler() {
        override fun handleMessage(msg: Message?) {
            when (msg?.what) {
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_show_location, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mapView = view.findViewById(R.id.mapview)
        mapView.onCreate(savedInstanceState)
        super.onViewCreated(view, savedInstanceState)
        setupMap()
        initData()
        registerListener()

    }
    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun findViews(view: View) {
        tvTitle = view.findViewById(R.id.title_bar_common_title)
        ivBack = view.findViewById(R.id.title_bar_common_back)
    }

    private fun setupMap() {
        aMap = mapView.map
        aMap.apply {
            showIndoorMap(true)
            uiSettings.apply {
                isZoomControlsEnabled       = false
            }
        }
    }


    private fun initData() {
        if (arguments == null) {
            return
        }
        val locationBody = arguments!![BUNDLE_LOCATION_INFO] as ShareChatMessage.LocationBody
        tv_aoi.text = locationBody.mName
        tv_address.text = locationBody.mAddress
        tvTitle.text = getString(R.string.location_info)


        if ( AtworkApplicationLike.getInstalledApps().contains(PACKAGE_NAME_AMAP) ) {
            appNames.add(getString(R.string.map_amap))
        }
        if (AtworkApplicationLike.getInstalledApps().contains(PACKAGE_NAME_TENCENT)) {
            appNames.add(getString(R.string.map_tencent))
        }
        if (AtworkApplicationLike.getInstalledApps().contains(PACKAGE_NAME_BAIDU)) {
            appNames.add(getString(R.string.map_baidu))
        }
        if (AtworkApplicationLike.getInstalledApps().contains(PACKAGE_NAME_GOOGLE)) {
            appNames.add(getString(R.string.map_google))
        }


        popupDialog = WorkplusBottomPopDialog()

        latLng = LatLng(locationBody.mLatitude, locationBody.mLongitude)
        handler.postDelayed({
            showMarker(latLng)
        }, 500)
    }

    private fun registerListener() {
        ivBack.setOnClickListener {
            onBackPressed()
        }

        iv_show_map_apps.setOnClickListener {
            if (!CommonUtil.isFastClick(2000)) {
                popMapsDialog()
            }
        }

        popupDialog.setItemOnListener { tag ->
            popupDialog.dismiss()
            if (tag == getString(R.string.map_amap)) {
                toAmapNavi()
            }
            if (tag == getString(R.string.map_baidu)) {
                toBaidu()
            }
            if (tag == getString(R.string.map_tencent)) {
                toTencent()
            }
        }
    }

    private fun popMapsDialog() {
        popupDialog.refreshData(appNames.toTypedArray())

        if (ListUtil.isEmpty<String>(appNames)) {
            return
        }
        if (isAdded) {
            popupDialog.show(childFragmentManager, "show_more")

        }
    }

    private fun showMarker(latLng: LatLng) {
        locationMarker = aMap.addMarker(MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.mipmap.icon_location_pin)))
        locationMarker.position =latLng
        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
    }

    override fun onBackPressed(): Boolean {
        mActivity.finish()
        return true
    }

    private fun toBaidu(){
        val intent = Intent("android.intent.action.VIEW", Uri.parse("baidumap://map/geocoder?location=" + latLng.latitude + "," + latLng.longitude));
        context?.startActivity(intent);
    }
    private fun toAmapNavi(){
        val intent = Intent("android.intent.action.VIEW", android.net.Uri.parse("androidamap://route?sourceApplication=appName&slat=&slon=&sname=我的位置&dlat=" + latLng.latitude + "&dlon=" + latLng.longitude + "&dname=目的地&dev=0&t=2"));
        context?.startActivity(intent);
    }
    private fun toTencent(){
        val intent = Intent("android.intent.action.VIEW", android.net.Uri.parse("qqmap://map/routeplan?type=drive&from=&fromcoord=&to=目的地&tocoord=" + latLng.latitude + "," + latLng.longitude + "&policy=0&referer=appName"));
        context?.startActivity(intent);


    }

}