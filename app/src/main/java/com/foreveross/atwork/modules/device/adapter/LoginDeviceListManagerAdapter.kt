package com.foreveross.atwork.modules.device.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import com.foreveross.atwork.AtworkApplicationLike
import com.foreveross.atwork.R
import com.foreveross.atwork.component.recyclerview.BaseQuickAdapter
import com.foreveross.atwork.component.recyclerview.BaseViewHolder
import com.foreveross.atwork.infrastructure.model.device.LoginDeviceInfo
import com.foreveross.atwork.infrastructure.utils.DensityUtil
import com.foreveross.atwork.infrastructure.utils.ScreenUtils
import com.foreveross.atwork.infrastructure.utils.StringUtils
import java.util.*


class LoginDeviceListManagerAdapter(var loginDeviceList: List<LoginDeviceInfo>): BaseQuickAdapter<LoginDeviceInfo, LoginDeviceListItemViewHolder>(R.layout.item_login_device, loginDeviceList) {


    override fun onCreateDefViewHolder(parent: ViewGroup?, viewType: Int): LoginDeviceListItemViewHolder {

        val loginDeviceListItemViewHolder = super.onCreateDefViewHolder(parent, viewType)
        //init max width
        loginDeviceListItemViewHolder.tvDeviceName.maxWidth =  ScreenUtils.getScreenWidth(AtworkApplicationLike.baseContext) / 3 * 2
        return loginDeviceListItemViewHolder
    }


    override fun convert(helper: LoginDeviceListItemViewHolder, item: LoginDeviceInfo) {
        helper.refreshUI(item)
    }


}

class LoginDeviceListItemViewHolder(view: View) : BaseViewHolder(view) {
    var tvDeviceName: TextView = view.findViewById(R.id.tvDeviceName)
    var tvWithoutAuthLabel: TextView = view.findViewById(R.id.tvWithoutAuthLabel)

    var tvWithoutAuthLabelWidth = -1

    fun refreshUI(loginDeviceInfo: LoginDeviceInfo) {

        if(StringUtils.isEmpty(loginDeviceInfo.deviceName)) {
            tvDeviceName.text = AtworkApplicationLike.getResourceString(R.string.common_nothing)

        } else {
            tvDeviceName.text = loginDeviceInfo.deviceName

        }

        handleTvDeviceNameLabelMaxWidth()


        tvWithoutAuthLabel.isVisible = loginDeviceInfo.authenticated



    }

    private fun handleTvDeviceNameLabelMaxWidth() {
        val tag = UUID.randomUUID().toString()
        tvDeviceName.tag = tag

        if (0 < tvWithoutAuthLabelWidth) {
            tvDeviceName.maxWidth = ScreenUtils.getScreenWidth(AtworkApplicationLike.baseContext) - tvWithoutAuthLabelWidth - DensityUtil.dip2px(35f)

        } else {


            tvWithoutAuthLabel.doOnPreDraw {
                if (tag == tvDeviceName.tag) {
                    tvDeviceName.maxWidth = ScreenUtils.getScreenWidth(AtworkApplicationLike.baseContext) - it.width - DensityUtil.dip2px(35f)
                }


            }
        }
    }

}