package com.foreveross.atwork.component.viewPager

import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter

class XmlPagerAdapter(private val count: Int): PagerAdapter() {

    override fun getCount(): Int = count

    override fun instantiateItem(parent: ViewGroup, position: Int): Any {
        return parent.getChildAt(position)
    }

    override fun isViewFromObject(view: View, any: Any): Boolean = (view === any)


    override fun destroyItem(parent: ViewGroup, position: Int, any: Any) {
        parent.removeView(any as View)
    }

}