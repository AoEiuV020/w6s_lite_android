package com.foreveross.atwork.modules.setting.adapter

import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.foreveross.atwork.AtworkApplicationLike
import com.foreveross.atwork.R
import com.foreveross.atwork.component.recyclerview.BaseQuickAdapter
import com.foreveross.atwork.component.recyclerview.BaseViewHolder
import com.foreveross.atwork.infrastructure.model.settingPage.W6sAccountAndSecureSetting
import com.foreveross.atwork.infrastructure.model.user.User
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo
import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.foreveross.atwork.modules.common.component.CommonItemView

class W6sAccountAndSecureSettingAdapter(var accountAndSecureSettingList: List<W6sAccountAndSecureSetting>, var accountAndSecureSettingDistributeList: List<List<W6sAccountAndSecureSetting>>, var loginUser: User?): BaseQuickAdapter<W6sAccountAndSecureSetting, W6sAccountAndSecureSettingItemViewHolder>(accountAndSecureSettingList) {

    override fun onCreateDefViewHolder(parent: ViewGroup?, viewType: Int): W6sAccountAndSecureSettingItemViewHolder {
        val commonItemView = CommonItemView(mContext)
        val w6sSettingItemViewHolder = W6sAccountAndSecureSettingItemViewHolder(commonItemView)

        return w6sSettingItemViewHolder
    }

    override fun convert(helper: W6sAccountAndSecureSettingItemViewHolder, item: W6sAccountAndSecureSetting) {
        setLeftCommonName(helper, item)
        setRightName(helper, item)

        setDivider(helper, item)

    }


    private fun setDivider(helper: W6sAccountAndSecureSettingItemViewHolder, item: W6sAccountAndSecureSetting) {
        accountAndSecureSettingDistributeList
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

    private fun setRightName(helper: W6sAccountAndSecureSettingItemViewHolder, item: W6sAccountAndSecureSetting) {
        when(item) {
            W6sAccountAndSecureSetting.ACCOUNT -> {
                helper.w6sSettingItemView.mTvRightest.text = LoginUserInfo.getInstance().getLoginUserRealUserName(AtworkApplicationLike.baseContext)
                showRightTextUI(helper)


            }

            W6sAccountAndSecureSetting.MOBILE -> {
                helper.w6sSettingItemView.mTvRightest.text = loginUser?.mPhone
                showRightTextUI(helper)

            }

            else -> showRightArrowUI(helper)
        }


    }

    private fun handleRightAreaUI(helper: W6sAccountAndSecureSettingItemViewHolder) {
        if (StringUtils.isEmpty(helper.w6sSettingItemView.mTvRightest.text.toString())) {
            showRightArrowUI(helper)

        } else {
            showRightTextUI(helper)
        }
    }

    private fun showRightArrowUI(helper: W6sAccountAndSecureSettingItemViewHolder) {
        helper.w6sSettingItemView.mTvRightest.isVisible = false
        helper.w6sSettingItemView.mCommonArrow.isVisible = true
    }

    private fun showRightTextUI(helper: W6sAccountAndSecureSettingItemViewHolder) {
        helper.w6sSettingItemView.mTvRightest.isVisible = true
        helper.w6sSettingItemView.mCommonArrow.isVisible = false
    }


    private fun setLeftCommonName(helper: W6sAccountAndSecureSettingItemViewHolder, item: W6sAccountAndSecureSetting) {
        when (item) {
            W6sAccountAndSecureSetting.ACCOUNT -> helper.w6sSettingItemView.setCommonName(mContext.getString(R.string.account_name))
            W6sAccountAndSecureSetting.MOBILE -> helper.w6sSettingItemView.setCommonName(mContext.getString(R.string.mobile_num))
            W6sAccountAndSecureSetting.MODIFY_PWD -> helper.w6sSettingItemView.setCommonName(mContext.getString(R.string.change_password))
            W6sAccountAndSecureSetting.DEVICES -> helper.w6sSettingItemView.setCommonName(mContext.getString(R.string.login_device_manager))
        }
    }


}


class W6sAccountAndSecureSettingItemViewHolder(view: View) : BaseViewHolder(view) {

    var w6sSettingItemView: CommonItemView = view as CommonItemView


}