package com.foreveross.atwork.modules.workbench.adapter.admin

import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import com.foreveross.atwork.R
import com.foreveross.atwork.component.WorkplusSwitchCompat
import com.foreveross.atwork.component.recyclerview.BaseQuickAdapter
import com.foreveross.atwork.component.recyclerview.BaseViewHolder
import com.foreveross.atwork.infrastructure.utils.extension.setOnClickEventListener
import com.foreveross.atwork.modules.app.adapter.AppTopAdvertsAdapter.Companion.displayBanner
import com.foreveross.atwork.modules.workbench.model.WorkbenchAdminBannerCardBannerItemDataWrapper

class WorkbenchAdminBannerCardBannerListAdapter(dataList: List<WorkbenchAdminBannerCardBannerItemDataWrapper>) : BaseQuickAdapter<WorkbenchAdminBannerCardBannerItemDataWrapper, BaseViewHolder>(dataList){


    var onItemSwPutawayClickListener: ((position: Int, swView: WorkplusSwitchCompat) -> Unit)? = null
    var onItemInfoClickListener: ((position: Int, infoView: ImageView, event: MotionEvent) -> Unit)? = null

    override fun getDefItemViewType(position: Int): Int {
        if(true == getItem(position)?.isItemDisplay()) {
            return 0
        }

        return 1
    }

    override fun onCreateDefViewHolder(parent: ViewGroup?, viewType: Int): BaseViewHolder {
        if(0 == viewType) {

            val workbenchAdminBannerCardBannerItemImg = WorkbenchAdminBannerCardBannerItemImg(LayoutInflater.from(mContext).inflate(R.layout.component_workbench_banner_card_banner_item_img, parent, false))
            workbenchAdminBannerCardBannerItemImg.swPutaway.setOnClickNotPerformToggle {
                onItemSwPutawayClickListener?.invoke(workbenchAdminBannerCardBannerItemImg.adapterPosition, workbenchAdminBannerCardBannerItemImg.swPutaway)
            }


            workbenchAdminBannerCardBannerItemImg.ivInfo.setOnClickEventListener {event->
                onItemInfoClickListener?.invoke(workbenchAdminBannerCardBannerItemImg.adapterPosition, workbenchAdminBannerCardBannerItemImg.ivInfo, event)

            }

            return workbenchAdminBannerCardBannerItemImg
        }

        return WorkbenchAdminBannerCardBannerItemAdd(LayoutInflater.from(mContext).inflate(R.layout.component_workbench_banner_card_banner_item_add, parent, false))

    }


    override fun convert(helper: BaseViewHolder, item: WorkbenchAdminBannerCardBannerItemDataWrapper) {
        if(helper is WorkbenchAdminBannerCardBannerItemImg) {
            item.advertisementConfig?.let {
                displayBanner(helper.ivBanner, it.parse())
            }

            helper.swPutaway.isChecked = item.advertisementConfig?.disabled?.not() ?: false
            helper.tvTimeInfo.isVisible = item.advertisementConfig?.parse()?.isValidDuration?.not()
                    ?: false
        }
    }


}

class WorkbenchAdminBannerCardBannerItemImg(itemView: View) : BaseViewHolder(itemView) {
    val ivBanner = itemView.findViewById<ImageView>(R.id.ivBanner)
    val swPutaway = itemView.findViewById<WorkplusSwitchCompat>(R.id.swPutaway)
    val ivInfo = itemView.findViewById<ImageView>(R.id.ivInfo)
    val tvTimeInfo = itemView.findViewById<TextView>(R.id.tvTimeInfo)
}

class WorkbenchAdminBannerCardBannerItemAdd(itemView: View) : BaseViewHolder(itemView) {

}