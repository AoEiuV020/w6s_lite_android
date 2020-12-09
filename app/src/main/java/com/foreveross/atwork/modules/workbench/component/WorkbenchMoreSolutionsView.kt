package com.foreveross.atwork.modules.workbench.component

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.foreveross.atwork.AtworkApplicationLike
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.utils.DensityUtil
import com.foreveross.atwork.infrastructure.utils.ScreenUtils
import com.foreveross.atwork.infrastructure.utils.ViewUtil
import com.foreveross.atwork.infrastructure.utils.extension.asType
import kotlinx.android.synthetic.main.component_workbench_card_more_solutions.view.*

class WorkbenchMoreSolutionsView: FrameLayout {


    constructor(context: Context) : super(context) {
        findViews(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        findViews(context)
    }

    private fun findViews(context: Context) {
        LayoutInflater.from(context).inflate(R.layout.component_workbench_card_more_solutions, this)


        val info = getInfo()

        ViewUtil.setSize(flMoreSolutions, info[1], info[0])


    }

    /**
     *  array[0] = width
     *  array[1] = height
     * */
    fun getInfo(): Array<Int> {
        flMoreSolutions.background.asType<BitmapDrawable>()?.bitmap?.let {
            val width = ScreenUtils.getScreenWidth(AtworkApplicationLike.baseContext) - DensityUtil.dip2px(10f) * 2
            val height = width * it.height / it.width
            return arrayOf(width, height)
        }

        return arrayOf(-1, -1)
    }

}