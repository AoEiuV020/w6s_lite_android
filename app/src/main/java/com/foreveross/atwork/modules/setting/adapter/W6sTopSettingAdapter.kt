package com.foreveross.atwork.modules.setting.adapter

import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.foreveross.atwork.AtworkApplicationLike
import com.foreveross.atwork.R
import com.foreveross.atwork.component.recyclerview.BaseQuickAdapter
import com.foreveross.atwork.component.recyclerview.BaseViewHolder
import com.foreveross.atwork.infrastructure.model.settingPage.W6sTopSetting
import com.foreveross.atwork.infrastructure.utils.AppUtil
import com.foreveross.atwork.modules.common.component.CommonItemView
import com.foreveross.atwork.utils.AtworkUtil

class W6sTopSettingAdapter(var topSettingList: List<W6sTopSetting>, var topSettingDistributeList: List<List<W6sTopSetting>>): BaseQuickAdapter<W6sTopSetting, W6sSettingItemViewHolder>(topSettingList) {

    override fun onCreateDefViewHolder(parent: ViewGroup?, viewType: Int): W6sSettingItemViewHolder {
        val commonItemView = CommonItemView(mContext)
        val w6sSettingItemViewHolder = W6sSettingItemViewHolder(commonItemView)

        return w6sSettingItemViewHolder
    }

    override fun convert(helper: W6sSettingItemViewHolder, item: W6sTopSetting) {
        setLeftCommonName(helper, item)

        setDivider(helper, item)

        setNoticeDot(helper, item)

        setRightCommonName(helper, item)
    }

    private fun setNoticeDot(helper: W6sSettingItemViewHolder, item: W6sTopSetting) {
        if (W6sTopSetting.ABOUT == item && AtworkUtil.isFoundNewVersion(mContext)) {
            helper.w6sSettingItemView.showCommonTip(true)
            helper.w6sSettingItemView.mCommonArrow.isVisible = false
            return
        }

        helper.w6sSettingItemView.showCommonTip(false)
        helper.w6sSettingItemView.mCommonArrow.isVisible = true

    }

    private fun setDivider(helper: W6sSettingItemViewHolder, item: W6sTopSetting) {
        topSettingDistributeList
                .find {
                    it.contains(item)
                }
                ?.let {
                    if (it.size - 1 == it.indexOf(item)) {
                        helper.w6sSettingItemView.setLineVisible(false)
                        return
                    }
                }


        helper.w6sSettingItemView.setLineVisible(true)
    }


    private fun setRightCommonName(helper: W6sSettingItemViewHolder, item: W6sTopSetting){
        if(W6sTopSetting.ABOUT == item) {
            helper.w6sSettingItemView.mTvRightest.text = AppUtil.getVersionName(AtworkApplicationLike.baseContext)
            helper.w6sSettingItemView.mTvRightest.isVisible = true
            return
        }

        helper.w6sSettingItemView.mTvRightest.isVisible = false


    }

    private fun setLeftCommonName(helper: W6sSettingItemViewHolder, item: W6sTopSetting) {
        when (item) {

            W6sTopSetting.ACCOUNT_AND_SECURE -> helper.w6sSettingItemView.setCommonName(mContext.getString(R.string.account_and_secure))
            W6sTopSetting.MESSAGE_PUSH -> helper.w6sSettingItemView.setCommonName(mContext.getString(R.string.new_message_notification))
            W6sTopSetting.GENERAL -> helper.w6sSettingItemView.setCommonName(mContext.getString(R.string.common_general))
            W6sTopSetting.FEEDBACK -> helper.w6sSettingItemView.setCommonName(mContext.getString(R.string.feedback))
            W6sTopSetting.INFORM -> helper.w6sSettingItemView.setCommonName(mContext.getString(R.string.inform))
            W6sTopSetting.ABOUT -> helper.w6sSettingItemView.setCommonName(mContext.getString(R.string.about))
        }
    }


}


class W6sSettingItemViewHolder(view: View) : BaseViewHolder(view) {

    var w6sSettingItemView: CommonItemView = view as CommonItemView


}