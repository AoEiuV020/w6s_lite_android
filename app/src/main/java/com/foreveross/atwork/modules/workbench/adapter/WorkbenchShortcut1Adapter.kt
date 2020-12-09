package com.foreveross.atwork.modules.workbench.adapter

import android.content.Context
import android.util.SparseArray
import androidx.viewpager.widget.PagerAdapter
import android.view.View
import android.view.ViewGroup
import androidx.core.util.forEach
import com.foreveross.atwork.infrastructure.model.workbench.WorkbenchCard
import com.foreveross.atwork.infrastructure.model.workbench.content.WorkbenchShortcutCardItem
import com.foreveross.atwork.modules.workbench.component.WorkbenchShortcut1PerPageView

class WorkbenchShortcut1Adapter(
        private var context: Context,

        private var itemGroupList: List<List<WorkbenchShortcutCardItem>>) : PagerAdapter() {


    private var pagerViewDataMap = SparseArray<WorkbenchShortcut1PerPageView>()

    var workbenchCard: WorkbenchCard? = null


    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = WorkbenchShortcut1PerPageView(context)
        view.refreshViews(itemGroupList[position])

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

    override fun getCount(): Int = itemGroupList.size

    override fun getItemPosition(`object`: Any): Int {
        if(itemGroupList.size != pagerViewDataMap.size()) {
            return POSITION_NONE
        }

        return POSITION_UNCHANGED
    }

    override fun notifyDataSetChanged() {
        super.notifyDataSetChanged()

        pagerViewDataMap.forEach  { index, shortcutItemsPerView ->

            itemGroupList.getOrNull(index)?.let {
                shortcutItemsPerView.workbenchCard = workbenchCard
                shortcutItemsPerView.refreshViews(shortcutCardItems = it)

            }
        }

    }

}
