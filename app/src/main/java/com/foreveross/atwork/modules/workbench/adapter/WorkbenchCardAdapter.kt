package com.foreveross.atwork.modules.workbench.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.foreveross.atwork.component.recyclerview.BaseQuickAdapter
import com.foreveross.atwork.component.recyclerview.BaseViewHolder
import com.foreveross.atwork.infrastructure.model.workbench.WorkbenchCard
import com.foreveross.atwork.infrastructure.model.workbench.WorkbenchCardType
import com.foreveross.atwork.modules.app.component.AdvertisementBannerCardView
import com.foreveross.atwork.modules.workbench.component.*
import com.foreveross.atwork.modules.workbench.component.admin.WorkbenchAdminCardView


class WorkbenchCardAdapter(dataList: MutableList<WorkbenchCard>) : BaseQuickAdapter<WorkbenchCard, WorkbenchCardItemViewHolder>(dataList) {


    companion object {
        fun produceWorkbenchCardRefreshView(context: Context, viewType: Int): IWorkbenchCardRefreshView<out WorkbenchCard> {
            val cardView: IWorkbenchCardRefreshView<out WorkbenchCard> = when (viewType) {

                WorkbenchCardType.BANNER.hashCode() -> WorkbenchBannerCardView(context)
                WorkbenchCardType.COMMON_APP.hashCode() -> WorkbenchCommonAppCardView(context)
                WorkbenchCardType.SEARCH.hashCode() -> WorkbenchSearchCardView(context)
                WorkbenchCardType.SHORTCUT_0.hashCode() -> WorkbenchShortcutCard0View(context)
                WorkbenchCardType.SHORTCUT_1.hashCode() -> WorkbenchShortcutCard1View(context)
                WorkbenchCardType.LIST_0.hashCode() -> WorkbenchListCard0View(context)
                WorkbenchCardType.LIST_1.hashCode() -> WorkbenchListCard1View(context)
                WorkbenchCardType.NEWS_0.hashCode() -> WorkbenchNews0CardView(context)
                WorkbenchCardType.NEWS_1.hashCode() -> WorkbenchNews1CardView(context)
                WorkbenchCardType.NEWS_2.hashCode() -> WorkbenchNews2CardView(context)
                WorkbenchCardType.NEWS_3.hashCode() -> WorkbenchNews3CardView(context)
                WorkbenchCardType.CATEGORY.hashCode() -> WorkbenchCategoryCardView(context)
                WorkbenchCardType.CUSTOM.hashCode() -> WorkbenchCustomCardView(context)
                WorkbenchCardType.APP_MESSAGES.hashCode() -> WorkBenchNewsSummaryView(context)
                WorkbenchCardType.ADMIN.hashCode() -> WorkbenchAdminCardView(context)
                WorkbenchCardType.APP_CONTAINER_0.hashCode() -> WorkbenchAppContainerCard0View(context)
                WorkbenchCardType.APP_CONTAINER_1.hashCode() -> WorkbenchAppContainerCard1View(context)

                else -> WorkbenchUnknownCardView(context)
            }

            initWorkbenchCardView(cardView)

            return cardView
        }

        private fun initWorkbenchCardView(cardView: IWorkbenchCardRefreshView<out WorkbenchCard>) {
            when (cardView) {
                is WorkbenchBannerCardView -> {
                    cardView.mode = AdvertisementBannerCardView.Mode.WORKBENCH
                }


            }
        }


    }

    override fun onCreateDefViewHolder(parent: ViewGroup?, viewType: Int): WorkbenchCardItemViewHolder {
        val cardView: IWorkbenchCardRefreshView<out WorkbenchCard> = produceWorkbenchCardRefreshView(mContext, viewType)


        return WorkbenchCardItemViewHolder(cardView as View)
    }


    override fun getDefItemViewType(position: Int): Int {
        val msg = getItem(position)
        msg?.let {
            return it.type.hashCode()
        }

        return WorkbenchCardType.UNKNOWN.hashCode()
    }


    override fun convert(helper: WorkbenchCardItemViewHolder, item: WorkbenchCard) {
        helper.cardItemView.refresh(item)
    }


}

class WorkbenchCardItemViewHolder(itemView: View) : BaseViewHolder(itemView) {
    var cardItemView: IWorkbenchCardRefreshView<WorkbenchCard> = itemView as IWorkbenchCardRefreshView<WorkbenchCard>
}