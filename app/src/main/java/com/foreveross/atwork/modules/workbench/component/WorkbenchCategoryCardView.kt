package com.foreveross.atwork.modules.workbench.component

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.view.postDelayed
import androidx.viewpager.widget.ViewPager
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.model.workbench.*
import com.foreveross.atwork.modules.workbench.adapter.WorkbenchCategoryAdapter
import com.foreveross.theme.manager.SkinHelper
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.component_workbench_category_card.view.*

class WorkbenchCategoryCardView: WorkbenchBasicCardView<WorkbenchCategoryCard> {

    override lateinit var workbenchCard: WorkbenchCategoryCard

    private lateinit var adapter: WorkbenchCategoryAdapter

    private var categorySubModules = arrayListOf<WorkbenchCardSubModule>()
    private var categoryCards = arrayListOf<WorkbenchCard>()

    private var lastSelectedIndex = 0

    constructor(context: Context) : super(context) {
        initViews()
        registerListener()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initViews()
        registerListener()
    }

    override fun findViews(context: Context) {
        LayoutInflater.from(context).inflate(R.layout.component_workbench_category_card, this)

    }



    private fun initViews() {
        adapter = WorkbenchCategoryAdapter(context, categorySubModules, categoryCards)
        vpCategoryCards.adapter = adapter

        tabLayoutCategory.setupWithViewPager(vpCategoryCards)
        tabLayoutCategory.setTabTextColors(SkinHelper.getTabInactiveColor(), SkinHelper.getActiveColor())
        tabLayoutCategory.tabMode = TabLayout.MODE_SCROLLABLE
    }

    private fun registerListener() {
        tabLayoutCategory.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab) {
                tab.customView?.tag?.let {
                    val holder = it as WorkbenchCategoryAdapter.CategoryCardCustomTabHolder
                    holder.tvCustom.setTextColor(SkinHelper.getTabActiveColor())
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                tab.customView?.tag?.let {
                    val holder = it as WorkbenchCategoryAdapter.CategoryCardCustomTabHolder
                    holder.tvCustom.setTextColor(ContextCompat.getColor(context, R.color.common_text_color))
                }
            }

            override fun onTabSelected(tab: TabLayout.Tab) {
                tab.customView?.tag?.let {
                    val holder = it as WorkbenchCategoryAdapter.CategoryCardCustomTabHolder
                    holder.tvCustom.setTextColor(SkinHelper.getTabActiveColor())
                }
            }

        })


        vpCategoryCards.addOnPageChangeListener(object: ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                lastSelectedIndex = position

            }

            override fun onPageSelected(position: Int) {
                reMeasureCurrentPage(position)

                handleCardTitleShowInTabBar(position)
            }

        })
    }

    private fun handleCardTitleShowInTabBar(position: Int) {
        val card = categoryCards[position]
        if (card is WorkbenchCommonTitleCard) {
            if (card.headerShow) {
                llRightArea.isVisible = true
                WorkbenchCommonTitleView.handleCustomTextOrIconViews(card, vWorkbenchCustomTextOrIconView00, vWorkbenchCustomTextOrIconView01)
                WorkbenchCommonTitleView.registerCustomTextOrIconViewsListener(context, card, vWorkbenchCustomTextOrIconView00, vWorkbenchCustomTextOrIconView01)
                return
            }
        }

        llRightArea.isVisible = false
    }

    fun reMeasureCurrentPage(position: Int) {
        val selectCardView = adapter.pagerViews[position]
        if(null == selectCardView)  {
            vpCategoryCards.reMeasureCurrentPage(null)

        } else {
            vpCategoryCards.reMeasureCurrentPage(selectCardView as View)

        }
    }

    override fun refresh(workbenchCard: WorkbenchCategoryCard) {
        this.workbenchCard = workbenchCard

        //refresh local firstly
        refreshView(workbenchCard)
    }

    override fun refreshView(workbenchCard: WorkbenchCategoryCard) {
        categoryCards.clear()
        categoryCards.addAll(workbenchCard.subCards)

        categorySubModules.clear()
        categorySubModules.addAll(workbenchCard.subModules)

        adapter.notifyDataSetChanged()

        refreshTab()

        vpCategoryCards.postDelayed(500) {
            reMeasureCurrentPage(vpCategoryCards.currentItem)
        }
    }


    private fun refreshTab() {
        //自定义tab
        for (i in 0 until tabLayoutCategory.tabCount) {
            val tab = tabLayoutCategory.getTabAt(i)

            if (tab != null) {
                tab.customView = adapter.getTabView(context, i)

                if (lastSelectedIndex == i) {
                    tab.select()
                }
            }

        }
    }


    override fun getViewType(): Int = WorkbenchCardType.CATEGORY.hashCode()
}