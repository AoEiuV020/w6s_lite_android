package com.foreveross.atwork.modules.workbench.adapter

import android.content.Context
import android.util.SparseArray
import androidx.viewpager.widget.PagerAdapter
import android.view.View
import android.view.ViewGroup
import androidx.core.util.forEach
import com.foreveross.atwork.infrastructure.model.workbench.WorkbenchCard
import com.foreveross.atwork.infrastructure.utils.LogUtil
import com.foreveross.atwork.modules.workbench.component.WorkbenchAppContainer0PerPageView
import com.foreveross.atwork.modules.workbench.model.WorkbenchAppContainerItemDataWrapper

class WorkbenchAppContainer0Adapter(
        private var context: Context,

        private var itemGroupList: List<List<WorkbenchAppContainerItemDataWrapper>>) : PagerAdapter() {


    private var pagerViewDataMap = SparseArray<WorkbenchAppContainer0PerPageView>()

    var workbenchCard: WorkbenchCard? = null
    var onRemoveItemAppBundleListener: ((position: Int) -> Unit)? = null

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = WorkbenchAppContainer0PerPageView(context)
        view.refreshViews(itemGroupList[position], workbenchCard)

        pagerViewDataMap.put(position, view)

        container.addView(view)

        return view
    }


    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        try {


            container.removeView(`object` as View)
            pagerViewDataMap.remove(position)

        } catch (e: Exception) {
            e.printStackTrace()
        }


    }


    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun getCount(): Int {
        val count = itemGroupList.size

        LogUtil.e("itemGroupList.size count  -> ${count}")

        return count
    }

    override fun getItemPosition(`object`: Any): Int {
        if(itemGroupList.size != pagerViewDataMap.size()) {
            return POSITION_NONE
        }

        return POSITION_UNCHANGED
    }


    override fun notifyDataSetChanged() {
        super.notifyDataSetChanged()

        LogUtil.e("itemGroupList.size count  -> ${itemGroupList}")


        pagerViewDataMap.forEach { index, appContainerItemsPerView ->

            itemGroupList.getOrNull(index)?.let {
                appContainerItemsPerView.refreshViews(appContainerItemDataWrapperList = it, workbenchCard = workbenchCard)

            }
        }

    }

}
