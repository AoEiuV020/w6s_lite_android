package com.foreveross.atwork.modules.setting.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.foreveross.atwork.AtworkApplicationLike
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo
import com.foreveross.atwork.support.BackHandledFragment
import kotlinx.android.synthetic.main.fragment_discussion_shield_helper_setting.*

class DiscussionShieldHelperSettingFragment: BackHandledFragment() {

    private lateinit var tvTitle: TextView
    private lateinit var ivBack: ImageView


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_discussion_shield_helper_setting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUI()
        registerListener()
    }

    override fun findViews(view: View) {
        tvTitle = view.findViewById(R.id.title_bar_common_title)
        ivBack = view.findViewById(R.id.title_bar_common_back)
    }



    private fun initUI() {
        tvTitle.text = getStrings(R.string.discussion_shield_helper)

        refreshUI()
    }

    private fun registerListener() {
        ivBack.setOnClickListener { onBackPressed() }

        llOpenDiscussionShieldHelper.setOnClickListener {
            PersonalShareInfo.getInstance().setSettingDiscussionHelper(AtworkApplicationLike.baseContext, true)
            refreshUI()
        }

        llCloseDiscussionShieldHelper.setOnClickListener {
            PersonalShareInfo.getInstance().setSettingDiscussionHelper(AtworkApplicationLike.baseContext, false)
            refreshUI()
        }

    }

    private fun refreshUI() {
        if(PersonalShareInfo.getInstance().getSettingDiscussionHelper(AtworkApplicationLike.baseContext)) {
            ivOpenDiscussionShieldHelper.setImageResource(R.mipmap.icon_sync_msgs_setting_select)
            ivCloseDiscussionShieldHelper.setImageResource(R.mipmap.icon_sync_msgs_setting_unselect)
            return
        }

        ivOpenDiscussionShieldHelper.setImageResource(R.mipmap.icon_sync_msgs_setting_unselect)
        ivCloseDiscussionShieldHelper.setImageResource(R.mipmap.icon_sync_msgs_setting_select)
    }


    override fun onBackPressed(): Boolean {
        finish()
        return false
    }
}