package com.foreveross.atwork.modules.workbench.component

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import com.foreveross.atwork.AtworkApplicationLike
import com.foreveross.atwork.R
import com.foreveross.atwork.api.sdk.app.model.LightNoticeData
import com.foreveross.atwork.infrastructure.model.workbench.WorkbenchCard
import com.foreveross.atwork.infrastructure.model.workbench.content.WorkbenchContentEventType
import com.foreveross.atwork.infrastructure.model.workbench.content.WorkbenchShortCardShowType
import com.foreveross.atwork.infrastructure.model.workbench.content.WorkbenchShortcutCardItem
import com.foreveross.atwork.infrastructure.utils.DensityUtil
import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.foreveross.atwork.infrastructure.utils.ViewUtil
import com.foreveross.atwork.modules.main.data.TabNoticeManager
import com.foreveross.atwork.modules.workbench.manager.WorkbenchManager
import com.foreveross.atwork.utils.WorkplusTextSizeChangeHelper
import kotlinx.android.synthetic.main.component_workbench_item_shortcut_card.view.*


class WorkbenchShortcutCardItemView : RelativeLayout {

    private var shortcutCardItem: WorkbenchShortcutCardItem? = null

    constructor(context: Context) : super(context) {
        findViews()
        initViews()
//        registerListener()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        findViews()
        initViews()
//        registerListener()
    }


    private fun findViews() {
        LayoutInflater.from(context).inflate(R.layout.component_workbench_item_shortcut_card, this)
    }

    private fun initViews() {
        val size = DensityUtil.dip2px(30f)
        ViewUtil.setSize(unreadImageView.iconView, size, size)

        WorkplusTextSizeChangeHelper.handleViewEnlargedTextSizeStatus(unreadImageView.iconView)

    }

    private fun switchType(workbenchShortcutCardViewType: WorkbenchShortCardShowType) {

        when (workbenchShortcutCardViewType) {

            WorkbenchShortCardShowType.NUMBER -> {
                tvContent.visibility = View.VISIBLE
                unreadImageView.visibility = View.VISIBLE
                unreadImageView.setIcon(0)

                tvCardItemTitle.setTextColor(ContextCompat.getColor(context, R.color.common_text_color_999))

            }

            WorkbenchShortCardShowType.ICON -> {
                tvContent.visibility = View.GONE
                unreadImageView.visibility = View.VISIBLE

//                tvCardItemTitle.setTextColor(ContextCompat.getColor(context, R.color.common_text_color))
                tvCardItemTitle.setTextColor(ContextCompat.getColor(context, R.color.common_text_color_999))

            }


        }
    }


    fun refresh(workbenchShortcutCardItem: WorkbenchShortcutCardItem?) {
        this.shortcutCardItem = workbenchShortcutCardItem

        workbenchShortcutCardItem?.let {
            val showType = WorkbenchShortCardShowType.parse(it.showType)


            tvCardItemTitle.text = it.title

            when (showType) {
                WorkbenchShortCardShowType.ICON -> refreshIconView(it)
                WorkbenchShortCardShowType.NUMBER -> refreshTextView(it)
            }

            switchType(showType)


            visibility = View.VISIBLE


            return
        }

        visibility = View.INVISIBLE
    }


    private fun refreshNoticeData(noticeData: LightNoticeData?) {
        if (null == noticeData) {
            unreadImageView.hideUnread()
            return
        }

        if (noticeData.isNothing) {
            unreadImageView.hideUnread()
            return
        }

        if (noticeData.isDot) {
            unreadImageView.showDot()
            return
        }


        if (noticeData.isDigit) {
            unreadImageView.unreadNum(noticeData.tip.num.toInt())
            return
        }
    }

    private fun getNoticeData(item: WorkbenchShortcutCardItem?): LightNoticeData? {
        if (null == item) {
            return null
        }

        if (StringUtils.isEmpty(item.tipUrl)) {
            return null
        }

        return TabNoticeManager.getInstance().getLightNoticeData(item.getNoticeDataId())

    }


//    private fun registerListener() {
//        setOnClickListener {
//            this.shortcutCardItem?.let { shortcutCardItem ->
//                val eventType = WorkbenchContentEventType.parse(shortcutCardItem.eventType)
//
//                shortcutCardItem.eventValue?.let {
//                    WorkbenchManager.route(context, eventType, it)
//
//                }
//            }
//        }
//
//    }

    fun clickAction(card: WorkbenchCard?, shortcutCardItem: WorkbenchShortcutCardItem) {
        WorkbenchManager.handleClickAction(card) {
            doClickAction(card, shortcutCardItem)
        }
    }

    private fun doClickAction(card: WorkbenchCard?, shortcutCardItem: WorkbenchShortcutCardItem) {
        val eventType = WorkbenchContentEventType.parse(shortcutCardItem.eventType)

        shortcutCardItem.eventValue?.let {
            WorkbenchManager.route(context, card, eventType, eventValue = it)
        }
    }


    private fun refreshIconView(shortcutCardItem: WorkbenchShortcutCardItem) {
        com.foreveross.atwork.modules.workbench.component.refreshIconView(unreadImageView.iconView, shortcutCardItem.iconType, shortcutCardItem.iconValue, holdingRes = R.mipmap.appstore_loading_icon_size)

        val noticeData = getNoticeData(shortcutCardItem)
        refreshNoticeData(noticeData)

    }


    private fun refreshTextView(shortcutCardItem: WorkbenchShortcutCardItem) {
        tvContent.text = shortcutCardItem.number.toString()

        val noticeData = getNoticeData(shortcutCardItem)
        refreshNoticeData(noticeData)
    }


}
