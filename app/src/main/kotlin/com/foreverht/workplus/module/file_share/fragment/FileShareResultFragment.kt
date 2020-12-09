package com.foreverht.workplus.module.file_share.fragment

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.foreverht.workplus.module.file_share.activity.INTENT_KEY_IS_SHARE_FILE
import com.foreverht.workplus.module.file_share.activity.INTENT_KEY_SHARE_FILE_RESULT
import com.foreveross.atwork.R
import com.foreveross.atwork.api.sdk.dropbox.responseJson.ShareFileResponseJson
import com.foreveross.atwork.infrastructure.BaseApplicationLike
import com.foreveross.atwork.support.BackHandledFragment
import com.foreveross.atwork.utils.AtworkToast

/**
 *  create by reyzhang22 at 2019-09-02
 */
class FileShareResultFragment : BackHandledFragment() {


    private lateinit var mTvTitle: TextView
    private lateinit var mIvBack: ImageView
    private lateinit var mTvLeftest: TextView
    private lateinit var mTvRightest: TextView

    private lateinit var mTvCancel:             TextView
    private lateinit var mTvFileName:           TextView
    private lateinit var mTvShareUrl:           TextView
    private lateinit var mItemSharePassword:    View
    private lateinit var mTvSharePassword:      TextView
    private lateinit var mBtnCopy:              Button
    private lateinit var mTvSuccessFlag:        TextView

    private lateinit var result: ShareFileResponseJson.Result

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_share_file_result, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        registerListener()
    }

    override fun findViews(view: View) {
        mTvTitle = view.findViewById(R.id.title_bar_common_title)
        mIvBack = view.findViewById(R.id.title_bar_common_back)
        mTvLeftest = view.findViewById(R.id.title_bar_common_left_title)
        mTvRightest = view.findViewById(R.id.title_bar_common_right_text)

        mTvCancel = view.findViewById(R.id.title_bar_common_left_title)
        mTvFileName = view.findViewById(R.id.tv_share_file_name)
        mTvShareUrl = view.findViewById(R.id.tv_share_file_url)
        mItemSharePassword = view.findViewById(R.id.ll_share_password_view)
        mTvSharePassword = view.findViewById(R.id.tv_share_file_password)
        mBtnCopy = view.findViewById(R.id.btn_copy)
        mTvSuccessFlag = view.findViewById(R.id.tv_success)
    }

    fun initData() {

        mIvBack.visibility = VISIBLE
        mIvBack.setImageResource(R.mipmap.icon_remove_back)
        mTvRightest.visibility = GONE
        mTvTitle.text = getString(R.string.title_share_file)

        if (arguments != null) {
            result = arguments!!.getParcelable(INTENT_KEY_SHARE_FILE_RESULT)!!
        }
        mTvSuccessFlag.visibility = if (arguments!!.getBoolean(INTENT_KEY_IS_SHARE_FILE, true)) VISIBLE else GONE
        mTvFileName.text = result.mName
        mTvShareUrl.text = result.mShareUrl
        if (!TextUtils.isEmpty(result.mPassword)) {
            mItemSharePassword.visibility = VISIBLE
            mTvSharePassword.text = result.mPassword
        }

    }

    fun registerListener() {
        mIvBack.setOnClickListener {
            onBackPressed()
        }
        mBtnCopy.setOnClickListener {
            makeShareContent()
        }
    }

    private fun makeShareContent() {
        if (result == null) {
            return
        }
        val stringBuilder = StringBuilder()
        stringBuilder.append(context?.getString(R.string.share_file_name)).append(" ").append(result.mName).append("\n")
        stringBuilder.append(context?.getString(R.string.share_file_url)).append(" ").append(result.mShareUrl)
        if (!TextUtils.isEmpty(result.mPassword)) {
            stringBuilder.append("\n").append(context?.getString(R.string.share_flie_password)).append(" ").append(result.mPassword)
        }
        val cmb = BaseApplicationLike.baseContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("newPlainTextLabel", stringBuilder.toString())
        cmb.setPrimaryClip(clipData)
        AtworkToast.showToast(getString(R.string.copy_success))
    }

    override fun onBackPressed(): Boolean {
        finish()
        return true
    }

}