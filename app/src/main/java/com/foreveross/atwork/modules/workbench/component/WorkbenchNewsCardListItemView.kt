package com.foreveross.atwork.modules.workbench.component

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import androidx.core.view.updateLayoutParams
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.model.workbench.WorkbenchCardType
import com.foreveross.atwork.infrastructure.model.workbench.content.WorkbenchListItem
import com.foreveross.atwork.infrastructure.utils.DensityUtil
import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.foreveross.atwork.infrastructure.utils.ViewUtil
import com.foreveross.atwork.utils.WorkplusTextSizeChangeHelper
import kotlinx.android.synthetic.main.component_workbench_news_item_list.view.*

class WorkbenchNewsCardListItemView: RelativeLayout {

    constructor(context: Context?) : super(context) {
        findViews()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        findViews()
    }


    private fun findViews() {
        LayoutInflater.from(context).inflate(R.layout.component_workbench_news_item_list, this)

    }


    fun refreshViews(position: Int, workbenchListItem: WorkbenchListItem, type: WorkbenchCardType) {
        refreshViewsLayout(position, type)

        refreshIconView(ivCover, workbenchListItem.iconType, workbenchListItem.iconValue, R.mipmap.fail_cover_square_size)

        tvTitle.text = workbenchListItem.title
        ViewUtil.setVisible(tvTitle, !StringUtils.isEmpty(workbenchListItem.title))


        tvContent.text = workbenchListItem.subTitle
        ViewUtil.setVisible(tvContent, !StringUtils.isEmpty(workbenchListItem.subTitle))


        tvSource.text = workbenchListItem.source
        ViewUtil.setVisible(tvSource, !StringUtils.isEmpty(workbenchListItem.source))


//        val time = workbenchListItem.getDateTime()

        if(StringUtils.isEmpty(workbenchListItem.dateTime)) {
            tvTime.visibility = View.GONE

        } else {
//            val showTime = TimeUtil.getStringForMillis(time, TimeUtil.getTimeFormat2(AtworkApplicationLike.baseContext))
            tvTime.text = workbenchListItem.dateTime
            tvTime.visibility = View.VISIBLE

        }


    }

    private fun refreshViewsLayout(position: Int, type: WorkbenchCardType) {
        when(type) {
            WorkbenchCardType.NEWS_0 -> refreshNews0TypeLayout(position)
            WorkbenchCardType.NEWS_1 -> refreshNews1TypeLayout()
            WorkbenchCardType.NEWS_2 -> refreshNews2TypeLayout()



        }
    }

    private fun refreshNews0TypeLayout(position: Int) {
        if (0 == position) {
            val size = DensityUtil.dip2px(100f)
            ViewUtil.setSize(ivCover, size, size)
            tvContent.maxLines = 2

        } else {
            val size = DensityUtil.dip2px( 60f)
            ViewUtil.setSize(ivCover, size, size)
            tvContent.maxLines = 2

        }

        WorkplusTextSizeChangeHelper.handleViewEnlargedTextSizeStatus(ivCover)


        ivCover.updateLayoutParams<RelativeLayout.LayoutParams> {

            removeRule(RelativeLayout.ALIGN_PARENT_RIGHT)
            addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE)

        }

        rlContentArea.updateLayoutParams<RelativeLayout.LayoutParams> {
            removeRule(RelativeLayout.LEFT_OF)
            addRule(RelativeLayout.RIGHT_OF, R.id.ivCover)

            setMargins(DensityUtil.dip2px( 15f), 0, 0, 0)
        }

    }

    private fun refreshNews1TypeLayout() {
        val size = DensityUtil.dip2px( 80f)
        ViewUtil.setSize(ivCover, size, size)
        WorkplusTextSizeChangeHelper.handleViewEnlargedTextSizeStatus(ivCover)

        tvContent.maxLines = 2
        ivCover.updateLayoutParams<RelativeLayout.LayoutParams> {

            removeRule(RelativeLayout.ALIGN_PARENT_LEFT)
            addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE)


        }


        rlContentArea.updateLayoutParams<RelativeLayout.LayoutParams> {
            removeRule(RelativeLayout.RIGHT_OF)
            addRule(RelativeLayout.LEFT_OF, R.id.ivCover)

            setMargins(0, 0, DensityUtil.dip2px(15f), 0)
        }

    }


    private fun refreshNews2TypeLayout() {
        val size = DensityUtil.dip2px(80f)
        ViewUtil.setSize(ivCover, size, size)
        WorkplusTextSizeChangeHelper.handleViewEnlargedTextSizeStatus(ivCover)

        tvContent.maxLines = 2



        ivCover.updateLayoutParams<RelativeLayout.LayoutParams> {

            removeRule(RelativeLayout.ALIGN_PARENT_RIGHT)
            addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE)

        }


        rlContentArea.updateLayoutParams<RelativeLayout.LayoutParams> {
            removeRule(RelativeLayout.LEFT_OF)
            addRule(RelativeLayout.RIGHT_OF, R.id.ivCover)

            setMargins(DensityUtil.dip2px(15f), 0, 0, 0)
        }
    }
}