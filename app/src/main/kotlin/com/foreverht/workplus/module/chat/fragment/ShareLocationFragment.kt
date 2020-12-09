package com.foreverht.workplus.module.chat.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import com.amap.api.maps.*
import com.amap.api.maps.model.*
import com.amap.api.maps.model.animation.TranslateAnimation
import com.amap.api.services.core.AMapException
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.core.PoiItem
import com.amap.api.services.poisearch.PoiResult
import com.amap.api.services.poisearch.PoiSearch
import com.foreverht.workplus.module.chat.PoiItemsAdapter
import com.foreveross.atwork.R
import com.foreveross.atwork.component.recyclerview.BaseQuickAdapter
import com.foreveross.atwork.infrastructure.model.location.GetLocationInfo
import com.foreveross.atwork.infrastructure.utils.AtWorkDirUtils
import com.foreveross.atwork.infrastructure.utils.DensityUtil.dip2px
import com.foreveross.atwork.modules.chat.component.history.MessageHistoryLoadMoreView
import com.foreveross.atwork.support.BackHandledFragment
import com.foreveross.atwork.utils.AtworkToast
import com.foreveross.atwork.utils.AtworkUtil
import kotlinx.android.synthetic.main.fragment_share_location.*
import kotlinx.android.synthetic.main.view_poi_search_list.*


/**
 *  create by reyzhang22 at 2020-01-02
 */
const val POI_SEARCH_PER_PAGE = 20

const val MESSAGE_ON_FIRST_LOCATED = 0
const val MESSAGE_POI_SEARCH = 1
const val MESSAGE_POI_DATASET_CHANGE = 2
const val MESSAGE_MAP_CAMERA_CHANGE = 3
class ShareLocationFragment: BackHandledFragment(), LocationSource, AMapLocationListener,
        AMap.OnMapTouchListener, AMap.OnCameraChangeListener, PoiSearch.OnPoiSearchListener, AMap.CancelableCallback,
        BaseQuickAdapter.RequestLoadMoreListener {

    private lateinit var searchLocationView: View
    private lateinit var etSearch: EditText
    private lateinit var searchLoading: ProgressBar
    private lateinit var rvSearchListView: RecyclerView
    private var searchAdapter: PoiItemsAdapter? = null
    private var searchedItems = mutableListOf<PoiItem>()

    private var isSearchMode = false


    private lateinit var mapView: MapView
    private var tvShowSearch: TextView? = null
    private lateinit var pbPoiLoading: ProgressBar
    private lateinit var rvPoiInfoList: RecyclerView

    private lateinit var aMap: AMap
    private lateinit var locationOption:    AMapLocationClientOption

    private var locationClient:    AMapLocationClient? = null
    private var currentLocationInfo:   GetLocationInfo? =  null
    private var listener:          LocationSource.OnLocationChangedListener? = null

    private var locationMarker: Marker? = null
    private var isMapMoving = false

    private var keyWord = ""
    private var currentPage = 1
    private lateinit var query: PoiSearch.Query
    private lateinit var poiSearch: PoiSearch

    private var firstLocation: AMapLocation? = null

    private var isLocated = false

    private var poiItems = mutableListOf<PoiItem>()

    private lateinit var adapter: PoiItemsAdapter

    private var textSearchInFirstPage = false

    private val handler = @SuppressLint("HandlerLeak")
    object: Handler() {
        override fun handleMessage(msg: Message?) {
            when (msg?.what) {
                MESSAGE_POI_SEARCH -> doPoiSearchQuery(if(msg.obj == null) null else msg.obj as LatLng, firstLocation?.cityCode)
                MESSAGE_POI_DATASET_CHANGE -> {
                    onPoiDataSetChanged(if(msg.obj == null) null else msg.obj as PoiResult)
                }
                MESSAGE_MAP_CAMERA_CHANGE -> onMarkerMovingAnima()
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AtWorkDirUtils.getSdCacheDir(mActivity);
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_share_location, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mapView = view.findViewById(R.id.mapview)
        mapView.onCreate(savedInstanceState)

        tvShowSearch = view.findViewById(R.id.et_show_search_location)
        pbPoiLoading = view.findViewById(R.id.pb_loading)
        rvPoiInfoList = view.findViewById(R.id.rv_poi_list)
        searchLocationView = view.findViewById(R.id.view_poi_search_list)
        etSearch = view.findViewById(R.id.et_search_location)
        searchLoading = view.findViewById(R.id.search_loading)
        rvSearchListView = view.findViewById(R.id.rv_searched_poi_list)
        setupMap()
        initData()
        registerListener()
    }

    override fun findViews(view: View) {
    }

    override fun onBackPressed(): Boolean {
        mActivity.finish()
        return true
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
        deactivate()
    }

    override fun onFinish() {

    }

    override fun onCancel() {

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun deactivate() {
        listener = null;
        if (locationClient != null) {
            locationClient!!.stopLocation();
            locationClient!!.onDestroy();
        }
    }

    override fun activate(locationSource: LocationSource.OnLocationChangedListener?) {
        listener = locationSource
        if (locationClient == null) {
            locationClient =  AMapLocationClient(mActivity);
            locationOption =  AMapLocationClientOption();
            //设置定位监听
            locationClient!!.setLocationListener(this);
            //设置为高精度定位模式
            locationOption.locationMode = AMapLocationClientOption.AMapLocationMode.Hight_Accuracy;
            //设置定位参数
            locationClient!!.setLocationOption(locationOption);
            locationClient!!.startLocation();
        }
    }

    override fun onTouch(event: MotionEvent?) {
        if (event?.action == MotionEvent.ACTION_DOWN) {
            isMapMoving = true
        }
        if (event?.action == MotionEvent.ACTION_UP) {
            isMapMoving = false;
            handler.obtainMessage(MESSAGE_MAP_CAMERA_CHANGE).sendToTarget()
        }
//
    }

    override fun onLocationChanged(amapLocation: AMapLocation) {
        val latLng = LatLng(amapLocation.latitude, amapLocation.longitude)
        if (locationMarker == null) {
            firstLocation = amapLocation
            currentLocationInfo = GetLocationInfo()
            currentLocationInfo?.apply {
                mLatitude = amapLocation.latitude
                mLongitude = amapLocation.longitude
                mAoiName = amapLocation.aoiName
                mAddress = amapLocation.address
                mCity = amapLocation.city
                mStreet = amapLocation.street
                mProvince = amapLocation.province
                mDistrict = amapLocation.district
            }
            handler.postDelayed({
                onFirstLocated(latLng)
            }, 500)

            handler.obtainMessage(MESSAGE_POI_SEARCH, latLng).sendToTarget()
            return
        }

    }

    private fun onFirstLocated(latLng: LatLng) {
        iv_myLocation.setImageResource(R.mipmap.icon_locate_my_pos)
        val screenPosition = aMap.projection.toScreenLocation(aMap.cameraPosition.target)
        locationMarker = aMap.addMarker(MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.mipmap.icon_location_pin)))
        locationMarker!!.setPositionByPixels(screenPosition.x, screenPosition.y)
        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17f))

    }

    override fun onCameraChangeFinish(position: CameraPosition?) {

    }

    override fun onCameraChange(position: CameraPosition?) {

    }

    override fun onPoiItemSearched(poiItem: PoiItem?, p1: Int) {

    }

    override fun onLoadMoreRequested() {
        currentPage += 1
        var latLng: LatLng? = null
        if (!isSearchMode) {
            latLng = LatLng(currentLocationInfo!!.mLatitude, currentLocationInfo!!.mLongitude)
        }

        handler.obtainMessage(MESSAGE_POI_SEARCH, latLng).sendToTarget()
    }


    override fun onPoiSearched(result: PoiResult?, rcode: Int) {
        if (rcode != AMapException.CODE_AMAP_SUCCESS) {
            return
        }
        if (result?.pois!!.isEmpty() && textSearchInFirstPage) {
            textSearchInFirstPage = false
            doPoiSearchQuery(null, firstLocation?.cityCode)
            return
        }
        handler.obtainMessage(MESSAGE_POI_DATASET_CHANGE, result).sendToTarget()
    }

    private fun onPoiDataSetChanged(result: PoiResult?) {
        pbPoiLoading.visibility = View.GONE

        if (isSearchMode) {
            if (result?.query == query) {
                searchedItems.addAll(result.pois)

            }
            searchAdapter?.notifyDataSetChanged()
            searchAdapter?.loadMoreComplete()
            if (result!!.pois!!.size >= POI_SEARCH_PER_PAGE) {
                searchAdapter?.setEnableLoadMore(true)
                return
            }
            searchAdapter?.loadMoreEnd()
            return
        }

        if (result?.query == query) {

            if (currentPage == 1) {
                val selectedItem: PoiItem?
                if (!isLocated) {
                    val poiItem = PoiItem(firstLocation?.adCode, LatLonPoint(firstLocation!!.latitude, firstLocation!!.longitude), firstLocation!!.aoiName, firstLocation!!.address)
                    poiItems.add(poiItem)
                    selectedItem = poiItem
                    isLocated = true
                } else {
                    selectedItem = result.pois.getOrNull(0)
                }

                selectedItem?.let {
                    adapter.setSelectedItem(it)
                    setCurrentSelectedLocation(it)
                }

            }
            poiItems.addAll(result.pois)
        }
        adapter.notifyDataSetChanged()
        adapter.loadMoreComplete()
        if (result!!.pois!!.size >= POI_SEARCH_PER_PAGE) {
            adapter.setEnableLoadMore(true)
            return
        }
        adapter.loadMoreEnd()
    }

    private fun setCurrentSelectedLocation(item: PoiItem) {
        currentLocationInfo?.apply {
            mLatitude = item.latLonPoint.latitude
            mLongitude = item.latLonPoint.longitude
            mAddress = item.snippet
            mAoiName = item.title
            mProvince = item.provinceName
            mCity = item.cityName
            mDistrict = item.adName
        }
    }

    private fun setupMap() {
        aMap = mapView.map
        aMap.apply {
            showIndoorMap(true)
            uiSettings.apply {
                isZoomControlsEnabled       = false
            }
        }
        aMap.setLocationSource(this)
        aMap.isMyLocationEnabled = true
        aMap.setOnMapTouchListener(this)
        aMap.setOnCameraChangeListener(this)

    }

    private fun initData() {

        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        rvPoiInfoList.layoutManager = layoutManager
        adapter = PoiItemsAdapter(poiItems, firstLocation)
        adapter.setOnLoadMoreListener(this, rvPoiInfoList)
        adapter.setLoadMoreView(MessageHistoryLoadMoreView())
        adapter.setOnItemClickListener { _, _, position ->
            val item = poiItems[position]
            setCurrentSelectedLocation(item)
            adapter.apply {
                setSelectedItem(item)
                notifyDataSetChanged()
            }
        }

        rvPoiInfoList.adapter = adapter

    }

    private fun registerListener() {
        send.setOnClickListener {
            if (currentLocationInfo == null) AtworkToast.showToast("无法定位，请稍后重试") else onResult()

        }

        view_close.setOnClickListener {
            onBackPressed()
        }

        tvShowSearch?.setOnClickListener {
            isSearchMode = true
            searchLocationView.visibility = View.VISIBLE
            etSearch.requestFocus()
            AtworkUtil.showInput(mActivity, etSearch)
            val layoutManager = LinearLayoutManager(context)
            layoutManager.orientation = LinearLayoutManager.VERTICAL
            rvSearchListView.layoutManager = layoutManager
            searchAdapter = PoiItemsAdapter(searchedItems, firstLocation)
            searchAdapter?.setOnLoadMoreListener(this, rvPoiInfoList)
            searchAdapter?.setLoadMoreView(MessageHistoryLoadMoreView())
            searchAdapter?.setOnItemClickListener { _, _, position ->
                val item = searchedItems[position]
                setCurrentSelectedLocation(item)
                searchAdapter?.apply {
                    setSelectedItem(item)
                    notifyDataSetChanged()
                }
                AtworkUtil.hideInput(mActivity, etSearch)
                iv_myLocation.setImageResource(R.mipmap.icon_move_out_my_pos)
                changeCamera(CameraUpdateFactory.newLatLngZoom(LatLng(item.latLonPoint.latitude, item.latLonPoint.longitude), 17f))
            }

            rvSearchListView.adapter = searchAdapter
        }

        etSearch.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                AtworkUtil.showInput(mActivity, etSearch)
            }
        }

        rvSearchListView.setOnTouchListener { v, event ->
            AtworkUtil.hideInput(mActivity, etSearch)
            false
        }

        etSearch.addTextChangedListener(object:TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                resetPoiInfo()
                keyWord = s.toString()
                searchAdapter?.setKeyword(keyWord)
                textSearchInFirstPage = true
                doPoiSearchQuery(null, "")
            }

        })

        tv_cancel_search?.setOnClickListener {
            returnToMyPosition()
        }

        iv_myLocation.setOnClickListener {
            returnToMyPosition()
        }


    }

    private fun returnToMyPosition() {
        searchLocationView.visibility = View.GONE
        isSearchMode = false
        AtworkUtil.hideInput(mActivity)
        searchedItems.clear()
        searchAdapter?.notifyDataSetChanged()
        etSearch.setText("")
        if (firstLocation != null) {
            val latLng = LatLng(firstLocation!!.latitude, firstLocation!!.longitude)
            iv_myLocation.setImageResource(R.mipmap.icon_locate_my_pos)
            changeCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17f))
            isLocated = false
            resetPoiInfo()
            doPoiSearchQuery(latLng, firstLocation?.cityCode)
        }
    }

    private fun onResult() {
        AtworkUtil.hideInput(activity)
        val intent = Intent()
        intent.putExtra("CURRENT_LOCATION", currentLocationInfo)
        mActivity.setResult(Activity.RESULT_OK, intent)
        onBackPressed()
    }

    private fun onMarkerMovingAnima() {

        if (locationMarker != null && !isMapMoving) {
            adapter.setMyLocation(firstLocation)
            iv_myLocation.setImageResource(R.mipmap.icon_move_out_my_pos)
            //根据屏幕距离计算需要移动的目标点
            val latLng = locationMarker!!.position
            val point = aMap.projection.toScreenLocation(latLng)
            point.y -= dip2px(125f)
            val target = aMap.projection
                    .fromScreenLocation(point)
            //使用TranslateAnimation,填写一个需要移动的目标点
            val animation = TranslateAnimation(target)
            animation.setInterpolator { input ->
                // 模拟重加速度的interpolator
                if (input <= 0.5) {
                    (0.5f - 2.0 * (0.5 - input) * (0.5 - input)).toFloat()
                } else {
                    (0.5f - Math.sqrt(((input - 0.5f) * (1.5f - input)).toDouble())).toFloat()
                }
            }
            //整个移动所需要的时间
            animation.setDuration(600)
            //设置动画
            locationMarker!!.setAnimation(animation)
            //开始动画
            locationMarker!!.startAnimation()
            currentLocationInfo?.apply {
                mLatitude = latLng.latitude
                mLongitude = latLng.longitude
            }
            resetPoiInfo()
            doPoiSearchQuery(latLng, firstLocation?.cityCode)
        }
    }

    private fun resetPoiInfo() {
        currentPage = 1
        poiItems.clear()
        searchedItems.clear()
        adapter.notifyDataSetChanged()

    }

    private fun doPoiSearchQuery(latLng: LatLng?, cityCode: String?) {
        query = PoiSearch.Query(keyWord, "", cityCode)

        query.pageSize = POI_SEARCH_PER_PAGE
        query.pageNum = currentPage
        query.cityLimit = false
        query.requireSubPois(true);
        poiSearch = PoiSearch(mActivity, query);
        poiSearch.setOnPoiSearchListener(this);
        if (latLng != null) {
            poiSearch.bound = PoiSearch.SearchBound(LatLonPoint(latLng.latitude, latLng.longitude), 5000, true);
        }
        poiSearch.searchPOIAsyn();
    }

    private fun changeCamera(update: CameraUpdate) {
        aMap.animateCamera(update, 1000, this)
    }

}