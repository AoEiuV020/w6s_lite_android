package com.foreveross.atwork.modules.dropbox.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.model.dropbox.Dropbox
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo
import com.foreveross.atwork.infrastructure.support.AtworkConfig
import com.foreveross.atwork.modules.common.adapter.CommonAdapter
import com.foreveross.atwork.modules.dropbox.activity.MyDropboxFileActivity
import com.foreveross.atwork.modules.dropbox.activity.MyDropboxShareActivity
import com.foreveross.atwork.support.BackHandledFragment

/**
 *  create by reyzhang22 at 2019-11-12
 */
class MyDropboxFileFragment : BackHandledFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_my_dropbox, container, false)
    }

    override fun findViews(view: View?) {
        val myDropboxList = view?.findViewById<ListView>(R.id.lv_my_dropbox)
        myDropboxList?.apply {
            adapter = CommonAdapter(mActivity, resources.getStringArray(R.array.my_dropbox_array), resources.obtainTypedArray(R.array.my_dropbox_img_array))
            setOnItemClickListener { _, _, position, _ ->
                when(position) {
                    0 -> handleMyFileAction()
                    1 -> handleMyShareAction()
                }
            }
        }
    }

    private fun handleMyFileAction() {
        val userId = LoginUserInfo.getInstance().getLoginUserId(context)
        MyDropboxFileActivity.startActivity(mActivity, Dropbox.SourceType.User, userId, AtworkConfig.DOMAIN_ID)
    }

    private fun handleMyShareAction() {
        MyDropboxShareActivity.startActivity(mActivity)
    }

    override fun onBackPressed(): Boolean {
        return false
    }


}