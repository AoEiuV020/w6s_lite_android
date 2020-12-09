package com.foreveross.atwork.modules.workbench.component

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.view.updatePadding
import com.foreveross.atwork.AtworkApplicationLike
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.model.WebViewControlAction
import com.foreveross.atwork.infrastructure.model.workbench.WorkbenchCardHeaderButton
import com.foreveross.atwork.infrastructure.model.workbench.WorkbenchCommonTitleCard
import com.foreveross.atwork.infrastructure.support.AtworkConfig
import com.foreveross.atwork.infrastructure.utils.DensityUtil
import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.foreveross.atwork.modules.workbench.manager.WorkbenchManager.handleClickAction
import kotlinx.android.synthetic.main.component_workbench_common_title_view.view.*
import kotlinx.android.synthetic.main.component_workbench_custom_icon_or_text_view.view.*


class WorkbenchCommonTitleView: RelativeLayout {

//    private var headerButton: MutableList<WorkbenchCardHeaderButton> = arrayListOf()
    private var commonTitleCard: WorkbenchCommonTitleCard? = null

    constructor(context: Context) : super(context) {
        findViews()
        registerListener()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        findViews()
        registerListener()
    }

    private fun findViews() {
        LayoutInflater.from(context).inflate(R.layout.component_workbench_common_title_view, this)

    }


    private fun registerListener() {
        registerCustomTextOrIconViewsListener(context, commonTitleCard, vWorkbenchCustomTextOrIconView00, vWorkbenchCustomTextOrIconView01)
    }


    fun getTvTitle(): TextView = tvTitle

    fun getCustomTextOrIconView01(): WorkbenchCustomTextOrIconView = vWorkbenchCustomTextOrIconView01

    fun getCustomTextOrIconView00(): WorkbenchCustomTextOrIconView = vWorkbenchCustomTextOrIconView00

    fun getCustomTextView01(): TextView  = getCustomTextOrIconView01().tvCustomText

    fun getCustomIconView01(): ImageView = getCustomTextOrIconView01().ivCustomIcon


    fun getCustomTextView00(): TextView = getCustomTextOrIconView00().tvCustomText

    fun getCustomIconView00(): ImageView = getCustomTextOrIconView00().ivCustomIcon

    fun refreshView(commonTitleCard: WorkbenchCommonTitleCard) {
        this.commonTitleCard = commonTitleCard


        if (!commonTitleCard.headerShowInCommonTitleView()) {

            handleHideView(commonTitleCard)

            return
        }

        handleShowView(commonTitleCard)

        registerListener()

    }

    private fun handleShowView(commonTitleCard: WorkbenchCommonTitleCard) {


        if (commonTitleCard.isSubCard) {
            rlRoot.updatePadding(top = DensityUtil.dip2px(6f))
        } else {
            rlRoot.updatePadding(top = DensityUtil.dip2px(16f))

        }


        rlRoot.visibility = View.VISIBLE

        tvTitle.text = commonTitleCard.getTitle(AtworkApplicationLike.baseContext)

        handleCustomTextOrIconViews(commonTitleCard, vWorkbenchCustomTextOrIconView00, vWorkbenchCustomTextOrIconView01)

    }


    private fun handleHideView(commonTitleCard: WorkbenchCommonTitleCard) {
        rlRoot.visibility = View.GONE

    }

    companion object {

        @JvmStatic
        fun registerCustomTextOrIconViewsListener(context: Context, commonTitleCard: WorkbenchCommonTitleCard?, vWorkbenchCustomTextOrIconView00: WorkbenchCustomTextOrIconView, vWorkbenchCustomTextOrIconView01: WorkbenchCustomTextOrIconView) {
            if(null == commonTitleCard) {
                return
            }


            vWorkbenchCustomTextOrIconView00.setOnClickListener {

                handleClickAction(commonTitleCard) {
                    val headerBtn0 = commonTitleCard.headerButton.getOrNull(0)
                    routeUrl(context, headerBtn0)
                }



            }

            vWorkbenchCustomTextOrIconView01.setOnClickListener {
                handleClickAction(commonTitleCard) {
                    val headerBtn1 = commonTitleCard.headerButton.getOrNull(1)
                    routeUrl(context, headerBtn1)
                }

            }
        }

        private fun routeUrl(context: Context, headerBtn: WorkbenchCardHeaderButton?) {
            if (null != headerBtn && !StringUtils.isEmpty(headerBtn.url)) {


                val webViewControlAction = WebViewControlAction.newAction()
                        .setUrl(headerBtn.url)

                com.foreveross.atwork.modules.app.route.routeUrl(context, webViewControlAction)


            }
        }


        @JvmStatic
        fun handleCustomTextOrIconViews(commonTitleCard: WorkbenchCommonTitleCard, vWorkbenchCustomTextOrIconView00: WorkbenchCustomTextOrIconView, vWorkbenchCustomTextOrIconView01: WorkbenchCustomTextOrIconView) {

            val headerBtn0 = commonTitleCard.headerButton.getOrNull(0)
            val headerBtn1 = commonTitleCard.headerButton.getOrNull(1)

            if (shouldHideHeaderBtn(headerBtn0, commonTitleCard)) {
                vWorkbenchCustomTextOrIconView00.visibility = View.GONE

            } else {
                vWorkbenchCustomTextOrIconView00.visibility = View.VISIBLE
                vWorkbenchCustomTextOrIconView00.refreshView(headerBtn0!!)
            }



            if (shouldHideHeaderBtn(headerBtn1, commonTitleCard)) {
                vWorkbenchCustomTextOrIconView01.visibility = View.GONE

            } else {
                vWorkbenchCustomTextOrIconView01.visibility = View.VISIBLE

                vWorkbenchCustomTextOrIconView01.refreshView(headerBtn1!!)

            }
        }

        private fun shouldHideHeaderBtn(headerBtn0: WorkbenchCardHeaderButton?, commonTitleCard: WorkbenchCommonTitleCard) =
                null == headerBtn0
                        || !headerBtn0.isLegal()
                        || shouldHideHeaderBtnIfNoData(commonTitleCard)


        private fun shouldHideHeaderBtnIfNoData(commonTitleCard: WorkbenchCommonTitleCard) =
                !AtworkConfig.WORKBENCH_CONFIG.isNeedTitleBtnIfNoData && commonTitleCard.isContentDataEmpty()
    }





}