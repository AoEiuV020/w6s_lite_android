package com.foreveross.atwork.modules.app.adapter

import android.os.Handler
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.foreverht.workplus.ui.component.recyclerview.DraggableItemAdapter
import com.foreverht.workplus.ui.component.recyclerview.ItemDraggableRange
import com.foreverht.workplus.ui.component.recyclerview.utils.AbstractDraggableItemViewHolder
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.model.app.AppBundles
import com.foreveross.atwork.infrastructure.utils.LogUtil
import com.foreveross.atwork.modules.app.component.AppItemCommonView
import com.foreveross.atwork.modules.app.inter.OnAppItemClickEventListener
import java.util.*

class AppGridCustomSortAdapter(var activity: FragmentActivity, var dataList: MutableList<AppBundles>)
    : RecyclerView.Adapter<AbstractDraggableItemViewHolder>()

    , DraggableItemAdapter<AbstractDraggableItemViewHolder> {



    private val dataIdMap = hashMapOf<String, Long>()

    init {
        setHasStableIds(true)


    }

    private var onAppItemClickEventListener: OnAppItemClickEventListener? = null

    private var customModeIcon: Int = 0

    private var isDragging = false


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AbstractDraggableItemViewHolder {
        val appItemCommonView = AppItemCommonView(activity)
        appItemCommonView.setOnAppItemClickEventListener(onAppItemClickEventListener)

        val appGridCustomSortItemViewHolder = AppGridCustomSortItemViewHolder(appItemCommonView)

        return appGridCustomSortItemViewHolder


    }

    override fun getItemCount(): Int {


        return 8
    }

    override fun onBindViewHolder(holder: AbstractDraggableItemViewHolder, position: Int) {
        if (holder is AppGridCustomSortItemViewHolder) {


            if (dataList.size > position) {
                holder.appItemView.appIconView.visibility = View.VISIBLE
                holder.appItemView.appNameView.visibility = View.VISIBLE
                holder.appItemView.customView.visibility = View.VISIBLE
                holder.appItemView.showAppShadow()

                holder.appItemView.refreshView(null, dataList[position], true)
                holder.appItemView.customView.setBackgroundResource(customModeIcon)
                return
            }

            if (!isDragging && dataList.size == position) {
                holder.appItemView.appIconView.visibility = View.VISIBLE
                holder.appItemView.appNameView.visibility = View.INVISIBLE
                holder.appItemView.customView.visibility = View.GONE

                holder.appItemView.hideAppShadow()


                holder.appItemView.appIconView.setImageResource(R.mipmap.app_custom_sort_gridlines)
                return
            }

            holder.appItemView.appIconView.visibility = View.INVISIBLE
            holder.appItemView.appNameView.visibility = View.INVISIBLE
            holder.appItemView.customView.visibility = View.GONE
            holder.appItemView.hideAppShadow()

        }


    }







    override fun getItemId(position: Int): Long {
        if(dataList.size <= position) {
            return -1L
        }

        val app = dataList[position]

        var longId = dataIdMap[app.mBundleId]

        if(null == longId || -1L == longId) {
            longId = Random().nextLong()
            dataIdMap[app.mBundleId] = longId
        }


        LogUtil.e("AppGridCustomSortAdapter longId -> $longId")
        return longId


    }


    fun setOnAppItemClickEventListener(onAppItemClickEventListener: OnAppItemClickEventListener) {
        this.onAppItemClickEventListener = onAppItemClickEventListener
    }

    fun setCustomModeIcon(customModeIcon: Int) {
        this.customModeIcon = customModeIcon
    }


    override fun onGetItemDraggableRange(holder: AbstractDraggableItemViewHolder, position: Int): ItemDraggableRange? {
        // no drag-sortable range specified
        return ItemDraggableRange(0, dataList.size - 1)
    }


    override fun onCheckCanStartDrag(holder: AbstractDraggableItemViewHolder, position: Int, x: Int, y: Int): Boolean {
        return true
    }

    override fun onCheckCanDrop(draggingPosition: Int, dropPosition: Int): Boolean {
        return true
    }

    override fun onMoveItem(fromPosition: Int, toPosition: Int) {
        LogUtil.e("AppGridCustomSortAdapter onMoveItem fromPosition : $fromPosition   toPosition : $toPosition ")

        if (fromPosition == toPosition) {
            return
        }


        val dataMoved = dataList.removeAt(fromPosition)
        dataList.add(toPosition, dataMoved)

    }

    override fun onItemDragStarted(position: Int) {
        LogUtil.e("AppGridCustomSortAdapter onItemDragStarted position : $position")

        isDragging = true
        notifyDataSetChanged()
    }

    override fun onItemDragFinished(fromPosition: Int, toPosition: Int, result: Boolean) {
        LogUtil.e("AppGridCustomSortAdapter onItemDragFinished fromPosition : $fromPosition  + toPosition : $toPosition + result : $result  ")

        isDragging = false
        Handler().postDelayed({ notifyDataSetChanged() }, 300)
    }



}


class AppGridCustomSortItemViewHolder(itemView: View) : AbstractDraggableItemViewHolder(itemView) {
    var appItemView: AppItemCommonView = itemView as AppItemCommonView
}



