package com.foreveross.atwork.modules.app.fragment

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ListView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.view.doOnPreDraw
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.foreverht.db.service.repository.AppRepository
import com.foreverht.threadGear.DbThreadPoolExecutor
import com.foreverht.workplus.ui.component.recyclerview.RecyclerViewDragDropManager
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.BaseApplicationLike
import com.foreveross.atwork.infrastructure.model.app.AppBundles
import com.foreveross.atwork.infrastructure.model.app.NativeApp
import com.foreveross.atwork.infrastructure.shared.OrgPersonalShareInfo
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo
import com.foreveross.atwork.infrastructure.utils.DensityUtil
import com.foreveross.atwork.infrastructure.utils.FirstLetterUtil
import com.foreveross.atwork.infrastructure.utils.ListUtil
import com.foreveross.atwork.infrastructure.utils.ScreenUtils
import com.foreveross.atwork.modules.app.manager.AppManager
import com.foreveross.atwork.modules.app.adapter.AppGridCustomSortAdapter
import com.foreveross.atwork.modules.app.adapter.AppGridCustomSortItemViewHolder
import com.foreveross.atwork.modules.app.adapter.AppsCommonAdapter
import com.foreveross.atwork.modules.app.inter.OnAppItemClickEventListener
import com.foreveross.atwork.modules.app.model.GroupAppItem
import com.foreveross.atwork.modules.app.util.AppRefreshHelper
import com.foreveross.atwork.support.BackHandledFragment
import java.util.*


class AppCustomSortFragment : BackHandledFragment() {

    private lateinit var vLayoutAppCustomSort: View
    private lateinit var vFakeView: View
    private lateinit var ivBack: ImageView
    private lateinit var tvCancel: TextView
    private lateinit var tvTitle: TextView
    private lateinit var tvFinish: TextView
    private lateinit var lwAppList: ListView
    private lateinit var rvGridAppListCustomSort: RecyclerView
    private lateinit var rlAddMaxTipArea: RelativeLayout
    private lateinit var ivMaxTipRemove: ImageView

    private lateinit var appListAdapter: AppsCommonAdapter
    private lateinit var gridCustomSortAdapter: AppGridCustomSortAdapter
    private lateinit var customSortWrappedAdapter: RecyclerView.Adapter<AppGridCustomSortItemViewHolder>

    private var appAllItems = arrayListOf<AppBundles>()
    private var groupAppItems = ArrayList<GroupAppItem>()
    private var customAppBundleSortItems = ArrayList<AppBundles>()

    private lateinit var mRecyclerViewDragDropManager: RecyclerViewDragDropManager


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_apps_custom_sort, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUI()
        registerListener()

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initData()

    }

    override fun findViews(view: View) {
        vLayoutAppCustomSort = view.findViewById(R.id.v_layout_app_custom_sort)
        vFakeView= view.findViewById(R.id.v_fake_statusbar)
        ivBack = view.findViewById(R.id.title_bar_common_back)
        tvCancel = view.findViewById(R.id.title_bar_common_left_title)
        tvTitle = view.findViewById(R.id.title_bar_common_title)
        tvFinish = view.findViewById(R.id.title_bar_common_right_text)
        lwAppList = view.findViewById(R.id.apps_list)
        rvGridAppListCustomSort = view.findViewById(R.id.gv_app_custom_sort)
        rlAddMaxTipArea = view.findViewById(R.id.rl_add_max_tip_area)
        ivMaxTipRemove = view.findViewById(R.id.iv_remove_max_tip)
    }


    private fun initUI() {
//        ivBack.visibility = View.GONE

        tvTitle.setText(R.string.edit_custom_sort_app)
        tvFinish.setText(R.string.done)
        tvCancel.setText(R.string.cancel)

        ivBack.setImageResource(R.mipmap.icon_remove_back)
//        tvCancel.visibility = View.VISIBLE
        tvFinish.visibility = View.VISIBLE

        val padding = getPaddingLength()
        rvGridAppListCustomSort.updatePadding(left = padding, right = padding)

        vLayoutAppCustomSort.doOnPreDraw {
            rlAddMaxTipArea.layoutParams.height = vLayoutAppCustomSort.height
        }

//        ViewCompat.setElevation(rvGridAppListCustomSort, 15f)

    }

    private fun registerListener() {

        tvCancel.setOnClickListener { v ->
            onBackPressed()
        }

        ivBack.setOnClickListener { v ->
            onBackPressed()
        }

        tvFinish.setOnClickListener { v ->
            updateCustomSortIdsData()
            AppRefreshHelper.refreshAppLightly()

            finish()
        }

        ivMaxTipRemove.setOnClickListener {
            rlAddMaxTipArea.visibility = View.GONE

            unfullScreen()
        }

    }



    private fun initData() {
        appListAdapter = AppsCommonAdapter(activity)
        appListAdapter.setCustomMode(true)
        appListAdapter.setCustomModeIcon(R.mipmap.icon_app_custom_sort_add)
        appListAdapter.setOnAppItemClickEventListener(object : OnAppItemClickEventListener {

            override fun onCustomModeClick(appBundle: AppBundles) {
                if (customAppBundleSortItems.contains(appBundle)) {
                    return
                }

                if (8 < customAppBundleSortItems.size + 1) {
//                    AtworkToast.showToast("最多添加8个常用应用")
                    rlAddMaxTipArea.visibility = View.VISIBLE

                    fullScreen()

                    return
                }

                customAppBundleSortItems.add(appBundle)

                refreshAppViews()

            }

        })

        lwAppList.adapter = appListAdapter


        gridCustomSortAdapter = AppGridCustomSortAdapter(activity!!, customAppBundleSortItems)
        gridCustomSortAdapter.setCustomModeIcon(R.mipmap.icon_app_custom_sort_remove)
        gridCustomSortAdapter.setOnAppItemClickEventListener(object : OnAppItemClickEventListener {

            override fun onCustomModeClick(appBundle: AppBundles) {
                customAppBundleSortItems.remove(appBundle)

                refreshAppViews()
            }

        })


        initCustomSortWrapAdapter()

        rvGridAppListCustomSort.adapter = customSortWrappedAdapter
        rvGridAppListCustomSort.layoutManager = GridLayoutManager(activity!!, 4)
        mRecyclerViewDragDropManager.attachRecyclerView(rvGridAppListCustomSort)


        loadAppList()
    }




    private fun initCustomSortWrapAdapter() {
        mRecyclerViewDragDropManager = RecyclerViewDragDropManager()
        mRecyclerViewDragDropManager.setInitiateOnLongPress(true)
        mRecyclerViewDragDropManager.setInitiateOnMove(false)
        mRecyclerViewDragDropManager.setLongPressTimeout(750)

        // setup dragging item effects (NOTE: DraggableItemAnimator is required)
        mRecyclerViewDragDropManager.dragStartItemAnimationDuration = 250
//        mRecyclerViewDragDropManager.draggingItemAlpha = 0.8f
//        mRecyclerViewDragDropManager.draggingItemScale = 1.3f
        mRecyclerViewDragDropManager.draggingItemRotation = 15.0f

        customSortWrappedAdapter = mRecyclerViewDragDropManager.createWrappedAdapter(gridCustomSortAdapter) as RecyclerView.Adapter<AppGridCustomSortItemViewHolder>      // wrap for dragging
    }

    private fun getPaddingLength(): Int {
        return (ScreenUtils.getScreenWidth(BaseApplicationLike.baseContext) - 4 * DensityUtil.dip2px(82f)) / 5
    }

    private fun updateCustomSortIdsData() {
        val appIdList = AppBundles.toAppBundleIdList(customAppBundleSortItems)
        val appIdStrBuilder = StringBuilder()
        for (appId in appIdList) {
            appIdStrBuilder.append(appId).append(",")
        }
        val currentOrg = PersonalShareInfo.getInstance().getCurrentOrg(BaseApplicationLike.baseContext)
        OrgPersonalShareInfo.getInstance().setCustomAppBundleIdSort(BaseApplicationLike.baseContext, currentOrg, appIdStrBuilder.toString())
    }


    @SuppressLint("StaticFieldLeak")
    private fun loadAppList() {
        object : AsyncTask<Void, Void, Boolean>() {
            override fun doInBackground(vararg params: Void?): Boolean {
                var appBundleList = AppManager.getInstance().appBundleList
                val currentOrgCode = PersonalShareInfo.getInstance().getCurrentOrg(BaseApplicationLike.baseContext)

                if (ListUtil.isEmpty(appBundleList)) {
                    val apps = AppRepository.getInstance().queryAccessApps(currentOrgCode)
                    for (app in apps) {
                        appBundleList.addAll(app.mBundles)
                    }

                }

                appAllItems.addAll(appBundleList)


                val customIdSort = OrgPersonalShareInfo.getInstance().getCustomAppBundleIdSort(context, currentOrgCode)
                val appListCustomSort = AppManager.getInstance().queryListInAppBundleIds(appBundleList, customIdSort, 8)
                customAppBundleSortItems.addAll(appListCustomSort)



                return true
            }


            override fun onPostExecute(result: Boolean) {
                refreshAppViews()


            }

        }.executeOnExecutor(DbThreadPoolExecutor.getInstance())
    }

    private fun refreshAppViews() {
        refreshGroupAppItemsData()
        appListAdapter.refreshGroupAppList(groupAppItems)
        gridCustomSortAdapter.notifyDataSetChanged()
    }

    private fun refreshGroupAppItemsData() {
        val tempList = arrayListOf<AppBundles>()
        tempList.addAll(appAllItems)
        tempList.removeAll(customAppBundleSortItems)

        sortApp(tempList)
    }


    private fun sortApp(data: List<AppBundles>?) {

        if (null == data) {
            return
        }

        val appMap = HashMap<String, ArrayList<AppBundles>>()
        var apps: ArrayList<AppBundles>
        for (appBundle in data) {

//            if (!app.isShowInMarket) {
//                continue
//            }


            val categoryNameI18n = appBundle.getCategoryNameI18n(BaseApplicationLike.baseContext)
            apps = if (appMap.containsKey(categoryNameI18n)) {
                appMap[categoryNameI18n]!!
            } else {
                ArrayList()
            }

            appMap[categoryNameI18n] = apps.apply { add(appBundle) }

        }

        val groupAppItems = ArrayList<GroupAppItem>()

        val keySet = appMap.keys
        for (key in keySet) {
            val groupAppItem = GroupAppItem(key, appMap[key], GroupAppItem.TYPE_APP)
            val sort = appMap[key]!![0].mSort
            groupAppItem.order = sort
            val titleAppItem = GroupAppItem(key, GroupAppItem.TYPE_TITLE)
            titleAppItem.order = sort
            groupAppItems.add(titleAppItem)
            groupAppItems.add(groupAppItem)
        }

        groupAppItems.sortWith(Comparator { lhs, rhs ->
            var result = lhs.order - rhs.order
            if (result == 0) {
                val lhsPinyin = FirstLetterUtil.getFullLetter(lhs.title)
                val rhsPinyin = FirstLetterUtil.getFullLetter(rhs.title)
                result = lhsPinyin.compareTo(rhsPinyin)
            }
            result
        })

        for (groupAppItem in groupAppItems) {
            groupAppItem.sortAppBundles()
        }

        this.groupAppItems = groupAppItems

    }

    //TODO: 无调用，暂时关闭 后期确认再无调用，请删除
    private fun setAppStatusDependsOnLocal(files: Array<String>?, app: NativeApp) {
//        if (files == null) {
//            return
//        }
//        for (filePath in files) {
//            val pkgName = AppUtil.getAPKPkgName(mActivity, AtWorkDirUtils.getInstance().getAppUpgrade(LoginUserInfo.getInstance().getLoginUserUserName(mActivity)) + File.separator + filePath)
//            if (!pkgName.equals(app.mPackageName, ignoreCase = true)) {
//                continue
//            }
//            var info: NativeAppDownloadManager.DownloadAppInfo? = NativeAppDownloadManager.getInstance().mDownLoadInfoMap[app.mAppId]
//            if (info == null) {
//                info = NativeAppDownloadManager.DownloadAppInfo(mActivity, app)
//                info.status = NativeAppDownloadManager.DownLoadStatus.STATUS_DOWNLOADED_NOT_INSTALL
//                NativeAppDownloadManager.getInstance().mDownLoadInfoMap[app.mAppId] = info
//                break
//            }
//            info.status = NativeAppDownloadManager.DownLoadStatus.STATUS_DOWNLOADED_NOT_INSTALL
//            break
//        }
    }


    override fun getFakeStatusBar(): View {
        return vFakeView
    }



    override fun onBackPressed(): Boolean {
        finish()
        return false
    }
}
