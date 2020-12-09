package com.foreveross.atwork.modules.setting.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.BaseApplicationLike
import com.foreveross.atwork.infrastructure.model.setting.WebviewFloatActionSetting
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo
import com.foreveross.atwork.support.BackHandledFragment

class WebviewFloatActionSettingFragment: BackHandledFragment() {

    private lateinit var ivBack: ImageView
    private lateinit var tvTitle: TextView
    private lateinit var llBottomLeftWebviewFloatBtn: LinearLayout
    private lateinit var ivBottomLeftWebviewFloatBtn: ImageView
    private lateinit var llBottomRightWebviewFloatBtn: LinearLayout
    private lateinit var ivBottomRightWebviewFloatBtn: ImageView
    private lateinit var llCloseWebviewFloatBtn: LinearLayout
    private lateinit var ivCloseWebviewFloatBtn: ImageView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return LayoutInflater.from(activity).inflate(R.layout.fragment_webview_float_action_setting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUI()
        registerListener()
    }

    override fun findViews(view: View) {
        ivBack = view.findViewById(R.id.title_bar_common_back)
        tvTitle = view.findViewById(R.id.title_bar_common_title)
        llBottomLeftWebviewFloatBtn = view.findViewById(R.id.ll_bottom_left_webview_float_btn)
        ivBottomLeftWebviewFloatBtn = view.findViewById(R.id.iv_bottom_left_webview_float_btn)
        llBottomRightWebviewFloatBtn = view.findViewById(R.id.ll_bottom_right_webview_float_btn)
        ivBottomRightWebviewFloatBtn = view.findViewById(R.id.iv_bottom_right_webview_float_btn)
        llCloseWebviewFloatBtn = view.findViewById(R.id.ll_close_webview_float_btn)
        ivCloseWebviewFloatBtn = view.findViewById(R.id.iv_close_webview_float_btn)
    }

    override fun onBackPressed(): Boolean {
        finish()
        return false
    }

    private fun initUI() {
        tvTitle.setText(R.string.webview_float_action_helper)

        refreshSelectUI()
    }

    private fun registerListener() {
        ivBack.setOnClickListener {
            onBackPressed()
        }

        llCloseWebviewFloatBtn.setOnClickListener {
            PersonalShareInfo.getInstance().setWebviewFloatActionSetting(BaseApplicationLike.baseContext, WebviewFloatActionSetting.CLOSE)
            refreshSelectUI()
        }

        llBottomLeftWebviewFloatBtn.setOnClickListener {
            PersonalShareInfo.getInstance().setWebviewFloatActionSetting(BaseApplicationLike.baseContext, WebviewFloatActionSetting.BOTTOM_LEFT)
            refreshSelectUI()

        }

        llBottomRightWebviewFloatBtn.setOnClickListener {
            PersonalShareInfo.getInstance().setWebviewFloatActionSetting(BaseApplicationLike.baseContext, WebviewFloatActionSetting.BOTTOM_RIGHT)
            refreshSelectUI()

        }

    }

    private fun refreshSelectUI() {
        val setting = PersonalShareInfo.getInstance().getWebviewFloatActionSetting(BaseApplicationLike.baseContext)
        when(setting) {
            WebviewFloatActionSetting.CLOSE -> {
                ivCloseWebviewFloatBtn.setImageResource(R.mipmap.icon_sync_msgs_setting_select)
                ivBottomLeftWebviewFloatBtn.setImageResource(R.mipmap.icon_sync_msgs_setting_unselect)
                ivBottomRightWebviewFloatBtn.setImageResource(R.mipmap.icon_sync_msgs_setting_unselect)
            }

            WebviewFloatActionSetting.BOTTOM_LEFT -> {
                ivCloseWebviewFloatBtn.setImageResource(R.mipmap.icon_sync_msgs_setting_unselect)
                ivBottomLeftWebviewFloatBtn.setImageResource(R.mipmap.icon_sync_msgs_setting_select)
                ivBottomRightWebviewFloatBtn.setImageResource(R.mipmap.icon_sync_msgs_setting_unselect)
            }

            WebviewFloatActionSetting.BOTTOM_RIGHT -> {
                ivCloseWebviewFloatBtn.setImageResource(R.mipmap.icon_sync_msgs_setting_unselect)
                ivBottomLeftWebviewFloatBtn.setImageResource(R.mipmap.icon_sync_msgs_setting_unselect)
                ivBottomRightWebviewFloatBtn.setImageResource(R.mipmap.icon_sync_msgs_setting_select)
            }
        }
    }

}