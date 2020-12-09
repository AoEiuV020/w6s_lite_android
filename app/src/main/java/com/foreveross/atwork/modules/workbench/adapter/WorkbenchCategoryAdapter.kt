package com.foreveross.atwork.modules.workbench.adapter

import android.content.Context
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.util.forEach
import androidx.core.view.isVisible
import androidx.viewpager.widget.PagerAdapter
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.model.workbench.WorkbenchCard
import com.foreveross.atwork.infrastructure.model.workbench.WorkbenchCardSubModule
import com.foreveross.atwork.infrastructure.model.workbench.WorkbenchCardSubModuleType
import com.foreveross.atwork.infrastructure.model.workbench.WorkbenchCardType
import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.foreveross.atwork.modules.workbench.component.*
import com.foreveross.atwork.utils.ImageCacheHelper

class WorkbenchCategoryAdapter(
        private var context: Context,
        private var subModuleDataList: List<WorkbenchCardSubModule>,
        private var categoryCardList: List<WorkbenchCard>) : PagerAdapter() {


    var pagerViews = SparseArray<IWorkbenchCardRefreshView<WorkbenchCard>>()

    override fun instantiateItem(container: ViewGroup, position: Int): Any {

        val workbenchCard = categoryCardList[position]

        val cardView: IWorkbenchCardRefreshView<out WorkbenchCard> = when (workbenchCard.type) {
            WorkbenchCardType.SHORTCUT_0 -> WorkbenchShortcutCard0View(context)
            WorkbenchCardType.SHORTCUT_1 -> WorkbenchShortcutCard1View(context)
            WorkbenchCardType.LIST_0 -> WorkbenchListCard0View(context)
            WorkbenchCardType.LIST_1 -> WorkbenchListCard1View(context)
            WorkbenchCardType.NEWS_0 -> WorkbenchNews0CardView(context)
            WorkbenchCardType.NEWS_1 -> WorkbenchNews1CardView(context)
            WorkbenchCardType.NEWS_2 -> WorkbenchNews2CardView(context)
            WorkbenchCardType.NEWS_3 -> WorkbenchNews3CardView(context)
            WorkbenchCardType.CUSTOM -> WorkbenchCustomCardView(context)
            WorkbenchCardType.APP_MESSAGES -> WorkBenchNewsSummaryView(context)
            else -> WorkbenchUnknownCardView(context)
        }


        val cardRefreshView = cardView as IWorkbenchCardRefreshView<WorkbenchCard>
        cardRefreshView.refresh(workbenchCard)

        pagerViews.put(position, cardRefreshView)

        container.addView(cardView as View)


        return cardView
    }


    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        try {


            container.removeView(`object` as View)

            pagerViews.remove(position)

        } catch (e: Exception) {
            e.printStackTrace()
        }


    }


    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun getCount(): Int = categoryCardList.size


    override fun getPageTitle(position: Int): CharSequence? = categoryCardList[position].getNameI18n(context)


    fun getTabView(context: Context, position: Int): View {
        val tab = LayoutInflater.from(context).inflate(R.layout.component_workbench_category_card_custom_tab, null)
        val ivCustom = tab.findViewById<ImageView>(R.id.iv_custom)
        val tvCustom = tab.findViewById<TextView>(R.id.tv_custom)

        tab.tag = CategoryCardCustomTabHolder(tvCustom)

        tvCustom.text = getPageTitle(position)

        refreshIconView(ivCustom, position)

        return tab
    }

    private fun refreshIconView(ivCustom: ImageView, position: Int) {
        val subModule = subModuleDataList[position]
        if (WorkbenchCardSubModuleType.ICON == subModule.type && !StringUtils.isEmpty(subModule.icon)) {
            doDisplayIconView(ivCustom, subModule.icon!!)
            ivCustom.isVisible = true

        } else {
            ivCustom.isVisible = false
        }
    }


    private fun doDisplayIconView(iconView: ImageView, iconValue: String) {
        var isHttpUrl = false
        iconValue.startsWith("http").let {
            isHttpUrl = it
        }

        if (isHttpUrl) {
            ImageCacheHelper.displayImage(iconValue, iconView, ImageCacheHelper.getRectOptions(R.mipmap.appstore_loading_icon_size))
        } else {
            ImageCacheHelper.displayImageByMediaId(iconValue, iconView, ImageCacheHelper.getRectOptions(R.mipmap.appstore_loading_icon_size))
        }
    }


    override fun getItemPosition(`object`: Any): Int {
        if(categoryCardList.size != pagerViews.size()) {
            return POSITION_NONE
        }

        return POSITION_UNCHANGED
    }


    override fun notifyDataSetChanged() {
        super.notifyDataSetChanged()

        pagerViews.forEach { index, workbenchCardRefreshView ->
            categoryCardList.getOrNull(index)?.let {
                workbenchCardRefreshView.refresh(it)
            }
        }


    }

    class CategoryCardCustomTabHolder (
        var tvCustom: TextView
    )

}
