package com.foreverht.workplus.component

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.foreveross.atwork.R

class DeviceOnlineView(context: Context) : RelativeLayout(context) {

    var header: View? = null
    var ivSystem: ImageView? = null
    var tvContent: TextView? = null

    var listener: OnDeviceOnlineViewClickListener? = null

    init {
        val inflater = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        header = inflater.inflate(R.layout.view_device_online_bar, this)
        ivSystem = header?.findViewById(R.id.pc_online_img)
        tvContent = header?.findViewById(R.id.pc_online_content)
        registerListener()
    }

    fun setDeviceOnlineClickListener(listener: OnDeviceOnlineViewClickListener) {
        this.listener = listener
    }

    fun registerListener() {
        header?.setOnClickListener {
            listener?.onDeviceOnlineViewClicked()
        }
    }

    fun setContent(context: Context, isMute: Boolean, deviceSystem: String?) {

        val platformName = getPlatformName(deviceSystem)

        val str: StringBuilder = when {
            deviceSystem.equals("windows", ignoreCase = true) -> {
                ivSystem?.setImageResource(R.mipmap.image_win_online_gray)
                StringBuilder(context.getString(R.string.pc_online_tip, platformName))
            }
            deviceSystem.equals("mac", ignoreCase = true) -> {
                ivSystem?.setImageResource(R.mipmap.image_mac_online_gray)
                StringBuilder(context.getString(R.string.pc_online_tip, platformName))

            }
            else -> {
                ivSystem?.setImageResource(R.mipmap.icon_pc)
                StringBuilder(context.getString(R.string.pc_online_tip, platformName))
            }
        }

        if (isMute) {
            str.append(context.getString(R.string.pc_online_and_mute))
        }
        tvContent?.text = str.toString()
    }

    interface OnDeviceOnlineViewClickListener {
        fun onDeviceOnlineViewClicked()
    }

    companion object {

        fun getPlatformName(deviceSystem: String?): String {

            return when {
                deviceSystem.equals("windows", ignoreCase = true) -> {
                    "Windows"
                }
                deviceSystem.equals("mac", ignoreCase = true) -> {
                    "Mac"

                }
                else -> {
                    "PC"
                }

            }

        }

    }
}