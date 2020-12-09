package com.foreveross.atwork.modules.setting.component

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.foreveross.atwork.AtworkApplicationLike
import com.foreveross.atwork.infrastructure.utils.DensityUtil

class W6sSettingItemDecoration(var settingList: List<Any>, var settingDistributeList: List<List<Any>>): RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {

        val position = parent.getChildAdapterPosition(view)
        val w6sTopSetting = settingList.getOrNull(position)


        settingDistributeList
                .find {
                    it.contains(w6sTopSetting)
                }
                ?.let {
                    if(0 == it.indexOf(w6sTopSetting)) {
                        outRect.top = DensityUtil.dip2px(10f)
                        return
                    }
                }


        outRect.top = 0



    }
}