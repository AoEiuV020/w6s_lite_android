package com.foreveross.atwork.modules.workbench.component

import android.annotation.SuppressLint
import android.content.Context
import android.os.AsyncTask
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import com.foreverht.threadGear.AsyncTaskThreadPool
import com.foreveross.atwork.AtworkApplicationLike
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.model.app.AppBundles
import com.foreveross.atwork.infrastructure.model.workbench.WorkbenchCardType
import com.foreveross.atwork.infrastructure.model.workbench.WorkbenchCommonAppCard
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo
import com.foreveross.atwork.infrastructure.utils.ListUtil
import com.foreveross.atwork.modules.app.manager.AppManager
import com.foreveross.atwork.manager.model.GetAppListRequest
import com.foreveross.atwork.modules.app.activity.AppListCustomSortActivity
import com.foreveross.atwork.modules.app.adapter.AppsAdapter
import com.foreveross.atwork.modules.app.model.GroupAppItem
import com.foreveross.atwork.modules.app.util.AppRefreshHelper
import com.foreveross.atwork.modules.main.activity.AppActivity
import com.foreveross.atwork.modules.workbench.manager.WorkbenchManager
import kotlinx.android.synthetic.main.component_workbench_common_app_card.view.*
import java.util.*


class WorkbenchCommonAppCardView: WorkbenchBasicCardView<WorkbenchCommonAppCard> {


    override lateinit var workbenchCard: WorkbenchCommonAppCard



    constructor(context: Context) : super(context) {
        initViews()
        registerListener()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initViews()
        registerListener()
    }


    private fun initViews() {
//        vWorkbenchCommonTitleView.getTvTitle().setText(R.string.common_apps)

//        vWorkbenchCommonTitleView.getCustomTextView01().setText(R.string.edit)
//        vWorkbenchCommonTitleView.getCustomTextOrIconView01().switchType(WorkbenchCustomTextOrIconViewType.TEXT)


        vWorkbenchCommonTitleView.getCustomIconView00().setImageResource(R.mipmap.icon_app_store)
        vWorkbenchCommonTitleView.getCustomIconView01().setImageResource(R.mipmap.icon_common_settings)

        vWorkbenchCommonTitleView.getCustomTextOrIconView00().switchType(WorkbenchCustomTextOrIconViewType.ICON)
        vWorkbenchCommonTitleView.getCustomTextOrIconView01().switchType(WorkbenchCustomTextOrIconViewType.ICON)


    }

    private fun registerListener() {
        vWorkbenchCommonTitleView.getCustomTextOrIconView00().setOnClickListener {

            WorkbenchManager.handleClickAction(workbenchCard) {
                val intent = AppActivity.getIntent(context, PersonalShareInfo.getInstance().getCurrentOrg(context))
                context.startActivity(intent)
            }



        }


        vWorkbenchCommonTitleView.getCustomTextOrIconView01().setOnClickListener {

            WorkbenchManager.handleClickAction(workbenchCard) {
                val intent = AppListCustomSortActivity.getIntent(context)
                context.startActivity(intent)
            }


        }
    }

    fun show() {
        rvRoot.visibility = View.VISIBLE
    }

    fun hide() {
        rvRoot.visibility = View.GONE
    }

    override fun refresh(workbenchCard: WorkbenchCommonAppCard) {
        this.workbenchCard = workbenchCard

        refreshView(workbenchCard)
    }

    override fun refreshView(workbenchCard: WorkbenchCommonAppCard) {
        vWorkbenchCommonTitleView.getTvTitle().text = workbenchCard.getTitle(AtworkApplicationLike.baseContext)

        refreshCommonUsedApps()
    }

    @SuppressLint("StaticFieldLeak")
    private fun refreshCommonUsedApps() {

        val tag = UUID.randomUUID()
        vCommonApps.tag = tag

        object : AsyncTask<Void, Void, List<AppBundles>?> (){

            override fun doInBackground(vararg params: Void?): List<AppBundles>? {
                return getCommonUsedApps()

            }

            override fun onPostExecute(result: List<AppBundles>?) {

                if(tag != vCommonApps.tag) {
                    return
                }

                if(ListUtil.isEmpty(result)) {
                    hide()
                    return
                }

                val key = AtworkApplicationLike.getResourceString(R.string.common_apps)
                val groupAppItemAdded = GroupAppItem(key, result, GroupAppItem.TYPE_APP)
                groupAppItemAdded.custom = true

                vCommonApps.refreshView(groupAppItemAdded, false, !workbenchCard.adminDisplay, AppsAdapter.getPaddingLength())
                show()
            }

        }.executeOnExecutor(AsyncTaskThreadPool.getInstance())
    }

    private fun getCommonUsedApps(): MutableList<AppBundles>? {
        val groupAppItemsSorted = AppRefreshHelper.sortApp(AtworkApplicationLike.baseContext, AppManager.getInstance().appList)

        if (ListUtil.isEmpty<GroupAppItem>(groupAppItemsSorted)) {
            return null
        }


        val groupAppItems = ArrayList<GroupAppItem>()
        groupAppItems.addAll(groupAppItemsSorted!!)


        val appBundlesListSort = ArrayList<AppBundles>()
        for (groupAppItem in groupAppItems) {

            if (!groupAppItem.custom) {
                appBundlesListSort.addAll(groupAppItem.mAppBundleList)
            }
        }

        val getAppListRequest = GetAppListRequest()
        getAppListRequest.strategy = GetAppListRequest.STRATEGY_CUSTOM
        getAppListRequest.limit = 8

        val appListInCustomStrategy = AppManager.getInstance().getAppListInCustomStrategy(AtworkApplicationLike.baseContext, getAppListRequest, appBundlesListSort)
        return appListInCustomStrategy
    }

    override fun findViews(context: Context) {
        LayoutInflater.from(context).inflate(R.layout.component_workbench_common_app_card, this)
    }


    override fun getViewType(): Int = WorkbenchCardType.COMMON_APP.hashCode()
}