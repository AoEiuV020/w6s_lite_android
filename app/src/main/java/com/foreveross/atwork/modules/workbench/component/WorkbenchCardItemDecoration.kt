package com.foreveross.atwork.modules.workbench.component

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.foreveross.atwork.AtworkApplicationLike
import com.foreveross.atwork.infrastructure.model.workbench.WorkbenchBannerCard
import com.foreveross.atwork.infrastructure.model.workbench.WorkbenchCard
import com.foreveross.atwork.infrastructure.model.workbench.WorkbenchSearchCard
import com.foreveross.atwork.infrastructure.model.workbench.content.WorkbenchBannerCardContent
import com.foreveross.atwork.infrastructure.utils.DensityUtil
import com.foreveross.atwork.infrastructure.utils.FileUtil
import com.foreveross.atwork.infrastructure.utils.ListUtil

class WorkbenchCardItemDecoration(var displayCardList: List<WorkbenchCard>): RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view)
        val workbenchCard = displayCardList.getOrNull(position)

        if(null == workbenchCard) {
            return
        }

        if(workbenchCard is WorkbenchBannerCard) {
            val bannerRecord = workbenchCard.getCardContent<WorkbenchBannerCardContent>()?.records

            val bannerRecordLegal = bannerRecord?.filter {
                FileUtil.isExist(it.getLocalBannerPath(view.context, workbenchCard.orgCode))
            }

            if(ListUtil.isEmpty(bannerRecordLegal)) {
                outRect.bottom = 0
                return
            }

        }


        if(workbenchCard is WorkbenchSearchCard) {
            val nextCard = displayCardList.getOrNull(position + 1)


            if(nextCard is WorkbenchBannerCard) {
                outRect.bottom = 0
                return
            }
        }


        outRect.bottom = DensityUtil.dip2px(10f)
    }
}