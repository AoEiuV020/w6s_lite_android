package com.foreverht.workplus.component

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.foreveross.atwork.AtworkApplicationLike
import com.foreveross.atwork.R
import com.foreveross.atwork.api.sdk.BaseCallBackNetWorkListener
import com.foreveross.atwork.api.sdk.users.UserAsyncNetService
import com.foreveross.atwork.component.WorkplusSwitchCompat
import com.foreveross.atwork.infrastructure.model.SessionType
import com.foreveross.atwork.infrastructure.model.user.User
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo
import com.foreveross.atwork.modules.chat.activity.ChatDetailActivity
import com.foreveross.atwork.modules.chat.data.ChatSessionDataWrap
import com.foreveross.atwork.modules.chat.fragment.ChatDetailFragment
import com.foreveross.atwork.modules.chat.fragment.ChatListFragment
import com.foreveross.atwork.modules.chat.fragment.ChatListFragment.DEVICE_ONLINE_STATUS
import com.foreveross.atwork.modules.chat.model.EntrySessionRequest
import com.foreveross.atwork.modules.configSettings.manager.ConfigSettingsManager
import com.foreveross.atwork.utils.AtworkToast
import com.foreveross.atwork.utils.ErrorHandleUtil

class DeviceOnlinePopup(context: Context?) : PopupWindow(context) {

    var popupView:          View?                   = null
    var transferFileView:   View?                   = null
    var mobileMuteBtn:      WorkplusSwitchCompat?   = null
    var muteContent:        TextView?               = null
    var muteTip:            TextView?               = null
    var muteImg:            ImageView?              = null
    var logoutPc:           TextView?               = null
    val ctx:                Context?                = context

    init {
        val inflater = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        popupView = inflater.inflate(R.layout.popup_device_online, null)
        transferFileView = popupView?.findViewById(R.id.transfer_file_item)
        mobileMuteBtn = popupView?.findViewById(R.id.switcher_mobile_mute)
        muteContent = popupView?.findViewById(R.id.online_mute_tip)
        muteImg = popupView?.findViewById(R.id.mobile_mute_img)
        muteTip = popupView?.findViewById(R.id.online_mute_tip)
        logoutPc = popupView?.findViewById(R.id.tv_logout_pc)
        contentView = popupView
        isOutsideTouchable = false
        width = ViewGroup.LayoutParams.MATCH_PARENT
        height = ViewGroup.LayoutParams.WRAP_CONTENT

        logoutPc?.text = context.getString(R.string.logout_pc, DeviceOnlineView.getPlatformName(PersonalShareInfo.getInstance().getDeviceSystem(context)))
        setBackgroundDrawable(ContextCompat.getDrawable(context, R.color.white))

        initData(context)
        registerListener()
    }

    fun initData(context: Context?) {
        val isMute = PersonalShareInfo.getInstance().isDeviceOnlineMuteMode(context)
        mobileMuteBtn?.isChecked = isMute
        muteTip?.text = if (isMute)  ctx?.getString(R.string.mobile_notification_content_off) else ctx?.getString(R.string.mobile_notification_content_on)
        muteImg?.setBackgroundResource(if (isMute) R.mipmap.icon_mute else R.mipmap.icon_mute_close)
    }

    fun registerListener() {
        mobileMuteBtn?.setOnClickNotPerformToggle {
            val isChecked = mobileMuteBtn!!.isChecked
            ConfigSettingsManager.setDevicesMode(ctx, !isChecked, object: BaseCallBackNetWorkListener{
                override fun onSuccess() {
                    muteTip?.text = if (!isChecked)  ctx?.getString(R.string.mobile_notification_content_off) else ctx?.getString(R.string.mobile_notification_content_on)
                    muteImg?.setBackgroundResource(if (!isChecked) R.mipmap.icon_mute else R.mipmap.icon_mute_close)
                    mobileMuteBtn?.isChecked = !isChecked
                    PersonalShareInfo.getInstance().setDeviceOnlineMuteMode(ctx, !isChecked)
                    val intent = Intent(DEVICE_ONLINE_STATUS)
                    intent.putExtra(ChatListFragment.INTENT_DEVICE_ONLINE_STATUS, true)
                    LocalBroadcastManager.getInstance(AtworkApplicationLike.sApp).sendBroadcast(intent)
                }

                override fun networkFail(errorCode: Int, errorMsg: String?) {
                    AtworkToast.showResToast(R.string.set_mute_error)
                }

            })
        }

        transferFileView?.setOnClickListener {
            AtworkApplicationLike.getLoginUser(object : UserAsyncNetService.OnQueryUserListener {
                override fun onSuccess(user: User) {
                        val entrySessionRequest = EntrySessionRequest.newRequest(SessionType.User, user)
                        ChatSessionDataWrap.getInstance().entrySession(entrySessionRequest)

                        val intent = ChatDetailActivity.getIntent(ctx, user.mUserId)
                        intent.putExtra(ChatDetailFragment.RETURN_BACK, true)

                        ctx!!.startActivity(intent)
                }

                override fun networkFail(errorCode: Int, errorMsg: String) {
                    ErrorHandleUtil.handleError(errorCode, errorMsg)
                }
            })
            dismiss()
        }

        logoutPc?.setOnClickListener {
            ConfigSettingsManager.logoutPc(ctx, object: BaseCallBackNetWorkListener{
                override fun networkFail(errorCode: Int, errorMsg: String?) {
                    ErrorHandleUtil.handleError(errorCode, errorMsg)
                }

                override fun onSuccess() {
                    dismiss()
                }

            })
        }

    }
}