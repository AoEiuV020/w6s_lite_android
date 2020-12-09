package com.foreveross.atwork.modules.setting.adapter

import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.foreveross.atwork.AtworkApplicationLike
import com.foreveross.atwork.R
import com.foreveross.atwork.component.recyclerview.BaseQuickAdapter
import com.foreveross.atwork.component.recyclerview.BaseViewHolder
import com.foreveross.atwork.infrastructure.model.setting.WebviewFloatActionSetting
import com.foreveross.atwork.infrastructure.model.settingPage.W6sGeneralSetting
import com.foreveross.atwork.infrastructure.shared.EmpIncomingCallShareInfo
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo
import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.foreveross.atwork.modules.common.component.CommonItemView

class W6sGeneralSettingAdapter(var generalSettingList: List<W6sGeneralSetting>, var generalSettingDistributeList: List<List<W6sGeneralSetting>>): BaseQuickAdapter<W6sGeneralSetting, W6sGeneralSettingItemViewHolder>(generalSettingList) {

    override fun onCreateDefViewHolder(parent: ViewGroup?, viewType: Int): W6sGeneralSettingItemViewHolder {
        val commonItemView = CommonItemView(mContext)
        val w6sSettingItemViewHolder = W6sGeneralSettingItemViewHolder(commonItemView)

        return w6sSettingItemViewHolder
    }

    override fun convert(helper: W6sGeneralSettingItemViewHolder, item: W6sGeneralSetting) {
        setLeftCommonName(helper, item)
        setRightName(helper, item)

        setDivider(helper, item)

    }


    private fun setDivider(helper: W6sGeneralSettingItemViewHolder, item: W6sGeneralSetting) {
        generalSettingDistributeList
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


    private fun setLeftCommonName(helper: W6sGeneralSettingItemViewHolder, item: W6sGeneralSetting) {
        when (item) {
            W6sGeneralSetting.LANGUAGE -> helper.w6sSettingItemView.setCommonName(mContext.getString(R.string.language_setting))
            W6sGeneralSetting.FONT_SIZE -> helper.w6sSettingItemView.setCommonName(mContext.getString(R.string.text_size))
            W6sGeneralSetting.DISCUSSION_HELPER -> helper.w6sSettingItemView.setCommonName(mContext.getString(R.string.discussion_shield_helper))
            W6sGeneralSetting.WEBVIEW_FLOAT_ACTION_HELPER -> helper.w6sSettingItemView.setCommonName(mContext.getString(R.string.webview_float_action_helper))
//            W6sGeneralSetting.CLEAR_MESSAGE_HISTORY -> helper.w6sSettingItemView.setCommonName(mContext.getString(R.string.clean_messages_data))
            W6sGeneralSetting.STORAGE_SPACE -> helper.w6sSettingItemView.setCommonName(mContext.getString(R.string.storage_space))
            W6sGeneralSetting.CLEAR_MESSAGE_HISTORY -> helper.w6sSettingItemView.setCommonName(mContext.getString(R.string.clean_messages_data))
            W6sGeneralSetting.EMP_INCOMING_ASSISTANT -> helper.w6sSettingItemView.setCommonName(mContext.getString(R.string.emp_incoming_call_assistant))
        }
    }


    private fun setRightName(helper: W6sGeneralSettingItemViewHolder, item: W6sGeneralSetting) {
        when(item) {

            W6sGeneralSetting.DISCUSSION_HELPER -> {
                if(PersonalShareInfo.getInstance().getSettingDiscussionHelper(AtworkApplicationLike.baseContext)) {
                    helper.w6sSettingItemView.mTvRightest.setText(R.string.common_open)

                } else {
                    helper.w6sSettingItemView.mTvRightest.setText(R.string.close)

                }
            }

            W6sGeneralSetting.WEBVIEW_FLOAT_ACTION_HELPER -> {
                if(WebviewFloatActionSetting.CLOSE == EmpIncomingCallShareInfo.getInstance().getWebviewFloatActionSetting(AtworkApplicationLike.baseContext)) {
                    helper.w6sSettingItemView.mTvRightest.setText(R.string.close)

                } else {
                    helper.w6sSettingItemView.mTvRightest.setText(R.string.common_open)

                }
            }

            W6sGeneralSetting.EMP_INCOMING_ASSISTANT -> {
                helper.w6sSettingItemView.mTvRightest?.apply {
                    val isOpen = EmpIncomingCallShareInfo.getInstance().getEmpIncomingCallAssistantSetting(AtworkApplicationLike.baseContext)
                    setText(if (isOpen) R.string.common_open else R.string.close)
                }
            }

        }

        helper.w6sSettingItemView.mTvRightest.isVisible = !StringUtils.isEmpty(helper.w6sSettingItemView.mTvRightest.text.toString())

    }


}


class W6sGeneralSettingItemViewHolder(view: View) : BaseViewHolder(view) {

    var w6sSettingItemView: CommonItemView = view as CommonItemView


}