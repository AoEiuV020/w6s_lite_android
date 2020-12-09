package com.foreverht.workplus.module.file_share.fragment

import android.content.Context
import android.org.apache.commons.lang3.RandomStringUtils
import android.os.Bundle
import android.os.Handler
import android.text.InputType
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.foreverht.workplus.module.file_share.FileShareAction
import com.foreverht.workplus.module.file_share.activity.FileShareResultActivity
import com.foreverht.workplus.module.file_share.activity.INTENT_FILE_SHARE_ACTION
import com.foreverht.workplus.ui.component.dialogFragment.AtworkAlertDialog
import com.foreveross.atwork.R
import com.foreveross.atwork.api.sdk.dropbox.DropboxAsyncNetService
import com.foreveross.atwork.api.sdk.dropbox.responseJson.ShareFileResponseJson
import com.foreveross.atwork.component.CommonPasswordDialog
import com.foreveross.atwork.component.ProgressDialogHelper
import com.foreveross.atwork.infrastructure.utils.NetworkStatusUtil
import com.foreveross.atwork.manager.DropboxManager
import com.foreveross.atwork.support.BackHandledFragment
import com.foreveross.atwork.utils.AtworkToast
import com.foreveross.atwork.utils.ErrorHandleUtil
import org.json.JSONObject

/**
 *  create by reyzhang22 at 2019-08-30
 */
class FileShareFragment : BackHandledFragment() {

    private lateinit var mTvTitle: TextView
    private lateinit var mIvBack: ImageView
    private lateinit var mTvLeftest: TextView
    private lateinit var mTvRightest: TextView

    private lateinit var mLayout: View
    private lateinit var mTvCancel: TextView

    private lateinit var mBtnEncryptShare: Button
    private lateinit var mBtnPublicShare: Button

    private lateinit var mTvShareTip: TextView

    private lateinit var mSetPasswordGroup: View
    private lateinit var mItemRandomPassword: View
    private lateinit var mIndicatorRandomPassword: View
    private lateinit var mItemSelfPassword: View
    private lateinit var mIndicatorSelfPassword: View
    private lateinit var mTvRandomPasswordLabel: TextView
    private lateinit var mTvSelfPasswordLabel: TextView
    private lateinit var mTvSelfPasswordValueLabel: TextView


    private lateinit var mItemValidForever: View
    private lateinit var mIndicatorValidForever: View
    private lateinit var mItemValidInTime: View
    private lateinit var mIndicatorValidInTime: View
    private lateinit var mValidForeverLabel: TextView
    private lateinit var mTvValidInTimeLabel: TextView
    private lateinit var mTvValidInTimeValueLabel: TextView

    private lateinit var mItemDownloadTimes: View
    private lateinit var mTvDownloadTimes: TextView

    private lateinit var mBtnCreateUrl: Button

    private lateinit var fileShareAction: FileShareAction

    private var isEncryptMode = true
    private var downloadTimes = 10
    private var validTime = -1
    private var shareFilePassword = RandomStringUtils.random(4, true, true)

    private lateinit var dialog: ProgressDialogHelper


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_share_file, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog = ProgressDialogHelper(context)
        initData()
        registerListener()
    }

    override fun findViews(view: View) {

        mTvTitle = view.findViewById(R.id.title_bar_common_title)
        mIvBack = view.findViewById(R.id.title_bar_common_back)
        mTvLeftest = view.findViewById(R.id.title_bar_common_left_title)
        mTvRightest = view.findViewById(R.id.title_bar_common_right_text)

        mLayout = view.findViewById(R.id.layout)
        mTvCancel = view.findViewById(R.id.title_bar_common_left_title)
        mBtnEncryptShare = view.findViewById(R.id.btn_share_encrypt)
        mBtnPublicShare = view.findViewById(R.id.btn_share_public)
        mTvShareTip = view.findViewById(R.id.tv_share_tip_by_type)
        mSetPasswordGroup = view.findViewById(R.id.ll_set_password_group)
        mItemRandomPassword = mSetPasswordGroup.findViewById(R.id.rl_random_password_item)
        mIndicatorRandomPassword = mItemRandomPassword.findViewById(R.id.indicator_random_password)
        mItemSelfPassword = mSetPasswordGroup.findViewById(R.id.rl_self_password_item)
        mIndicatorSelfPassword = mItemSelfPassword.findViewById(R.id.indicator_self_password)
        mTvSelfPasswordValueLabel = mItemSelfPassword.findViewById(R.id.tv_self_password_value_label)
        mItemValidForever = view.findViewById(R.id.rl_valid_forever_item)
        mIndicatorValidForever = mItemValidForever.findViewById(R.id.indicator_valid_forever)
        mItemValidInTime = view.findViewById(R.id.rl_valid_in_times_item)
        mIndicatorValidInTime = mItemValidInTime.findViewById(R.id.indicator_valid_in_times)
        mTvValidInTimeValueLabel = mItemValidInTime.findViewById(R.id.tv_valid_in_times_value_label)
        mItemDownloadTimes = view.findViewById(R.id.rl_set_download_times_item)
        mTvDownloadTimes = mItemDownloadTimes.findViewById(R.id.tv_set_download_times)
        mBtnCreateUrl = view.findViewById(R.id.btn_create_share_link)

        mTvRandomPasswordLabel = view.findViewById(R.id.tv_random_password_label)
        mTvSelfPasswordLabel = view.findViewById(R.id.tv_self_password_label)
        mValidForeverLabel = view.findViewById(R.id.tv_valid_forever_label)
        mTvValidInTimeLabel = view.findViewById(R.id.tv_valid_intimes_label)
    }

    private fun initData() {

        mIvBack.visibility = VISIBLE
        mTvRightest.visibility = GONE
        mTvTitle.text = getString(R.string.title_share_file)

        if (arguments != null) {
            fileShareAction = arguments!!.getParcelable(INTENT_FILE_SHARE_ACTION)!!
        }

    }

    private fun registerListener() {
        mIvBack.setOnClickListener {
            onBackPressed()
        }

        mBtnEncryptShare.setOnClickListener {
            isEncryptMode = true
            refreshEncryptModeUI()
        }

        mBtnPublicShare.setOnClickListener {
            isEncryptMode = false
            refreshPublicModeUI()
        }

        mItemRandomPassword.setOnClickListener {
            onIndicatorStatus(mIndicatorRandomPassword, mTvRandomPasswordLabel, true)
            onIndicatorStatus(mIndicatorSelfPassword, mTvSelfPasswordLabel, false)
            mTvSelfPasswordValueLabel.text = ""
            shareFilePassword = RandomStringUtils.random(4, true, true)
        }

        mItemSelfPassword.setOnClickListener {
            val passwordDialog = CommonPasswordDialog(mActivity)
            passwordDialog.setCancelable(false)
            passwordDialog.setClickBrightColorListener {
                when {
                    TextUtils.isEmpty(it) -> AtworkToast.showToast(getString(R.string.please_input_password))
                    it.length < 4 -> AtworkToast.showToast(getString(R.string.set_share_password_content))
                    else -> {
                        onIndicatorStatus(mIndicatorRandomPassword, mTvRandomPasswordLabel, false)
                        onIndicatorStatus(mIndicatorSelfPassword, mTvSelfPasswordLabel, true)
                        mTvSelfPasswordValueLabel.text = it
                        shareFilePassword = it
                        passwordDialog.dismiss()
                    }
                }
            }
            passwordDialog.setClickDeadColorListener {  }
            passwordDialog.show()
            showInputMethod()
        }



        mItemValidForever.setOnClickListener {
            onIndicatorStatus(mIndicatorValidForever, mValidForeverLabel, true)
            onIndicatorStatus(mIndicatorValidInTime, mTvValidInTimeLabel, false)
            mTvValidInTimeValueLabel.text = ""
            validTime = -1
        }

        mItemValidInTime.setOnClickListener {
            val dialog = AtworkAlertDialog(context, AtworkAlertDialog.Type.INPUT)
            dialog.setTitleText(getString(R.string.valid_in_times))
            dialog.setCancelable(false)
            dialog.setOnEditInputListener {
                try {
                    validTime = it.toInt()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                when {
                    TextUtils.isEmpty(it) -> AtworkToast.showToast(getString(R.string.please_input_valid_times))
                    !(validTime in 1..30)  -> AtworkToast.showToast(getString(R.string.valid_times_error_tip))
                    else -> {

                        onIndicatorStatus(mIndicatorValidForever, mValidForeverLabel, false)
                        onIndicatorStatus(mIndicatorValidInTime, mTvValidInTimeLabel, true)
                        mTvValidInTimeValueLabel.text = String.format(getString(R.string.day_label), validTime)
                        dialog.dismiss()
                    }
                }
            }
            dialog.setInputHint(R.string.please_input_valid_times)
            dialog.setInputContent(R.string.valid_time_hint)
            dialog.setBrightBtnText(R.string.ok)
            dialog.setClickBrightColorListener {  }
            dialog.setDeadBtnText(R.string.cancel)
            dialog.setInputType(InputType.TYPE_CLASS_NUMBER)
            dialog.show()
//            AtworkUtil.showInput(mActivity, dialog.mEtInputContent)
            showInputMethod()
        }

        mItemDownloadTimes.setOnClickListener {

            val dialog = AtworkAlertDialog(context, AtworkAlertDialog.Type.INPUT)
            dialog.setTitleText(getString(R.string.allow_download_times))
            dialog.setCancelable(false)
            dialog.setOnEditInputListener {
                try {
                    downloadTimes = it.toInt()

                } catch (e: Exception) {
                    e.printStackTrace()
                }

                when {
                    TextUtils.isEmpty(it) -> AtworkToast.showToast(getString(R.string.please_input_download_times))
                    downloadTimes <= 0 -> AtworkToast.showToast(getString(R.string.download_times_error_tip))
                    else -> {


                        mTvDownloadTimes.text = it
                        dialog.dismiss()
                    }
                }
            }
            dialog.setInputHint(R.string.please_input_download_times)
            dialog.setBrightBtnText(R.string.ok)
            dialog.setInputContent(R.string.download_times_hint)
            dialog.setClickBrightColorListener {  }
            dialog.setDeadBtnText(R.string.cancel)
            dialog.setInputType(InputType.TYPE_CLASS_NUMBER)
            dialog.setEditMaxLength(3)
            dialog.show()
            showInputMethod()
        }

        mBtnCreateUrl.setOnClickListener {
            makeFileShareRequest()
        }
    }

    private fun makeFileShareRequest() {
        dialog.show()
        DropboxManager.getInstance().shareDropboxFile(context, fileShareAction, makeRequestJson().toString(), object: DropboxAsyncNetService.OnDropboxSharedListener {
            override fun onShearSuccess(response: ShareFileResponseJson) {
                dialog.dismiss()
                FileShareResultActivity.startActivity(context!!, response.mResult, true)
                finish()
            }

            override fun networkFail(errorCode: Int, errorMsg: String?) {
                dialog.dismiss()
                if (NetworkStatusUtil.isNetworkAvailable(mActivity)) {
                    AtworkToast.showResToast(R.string.contact_admin_with_errorCode, errorCode)
                    return;
                }

                ErrorHandleUtil.handleError(errorCode, errorMsg)
            }

        })
    }

    private fun makeRequestJson(): JSONObject {
        val requestJson = JSONObject()
        requestJson.put("id", fileShareAction.fileId)
        requestJson.put("type", fileShareAction.type)
        if (isEncryptMode) {
            requestJson.put("password", shareFilePassword)
        }
        if (validTime != -1) {
            requestJson.put("expire_after_days", validTime)
        }
        requestJson.put("limit",downloadTimes)
        return requestJson
    }


    private fun refreshEncryptModeUI() {
        mTvShareTip.text = getString(R.string.share_file_encrypt_tip)
        mBtnEncryptShare.setBackgroundColor(resources.getColor(R.color.blue_lock))
        mBtnEncryptShare.setTextColor(resources.getColor(R.color.white))
        mBtnPublicShare.setBackgroundColor(resources.getColor(R.color.color_e6e6e6))
        mBtnPublicShare.setTextColor(resources.getColor(R.color.title_bar_rightest_text_gray))
        mSetPasswordGroup.visibility = VISIBLE
    }

    private fun refreshPublicModeUI() {
        mTvShareTip.text = getString(R.string.share_file_plublic_tip)
        mBtnEncryptShare.setBackgroundColor(resources.getColor(R.color.color_e6e6e6))
        mBtnEncryptShare.setTextColor(resources.getColor(R.color.title_bar_rightest_text_gray))
        mBtnPublicShare.setBackgroundColor(resources.getColor(R.color.blue_lock))
        mBtnPublicShare.setTextColor(resources.getColor(R.color.white))
        mSetPasswordGroup.visibility = GONE
    }

    private fun onIndicatorStatus(indicator: View, selectedTv: TextView, selected: Boolean) {
        indicator.visibility = if (selected) VISIBLE else INVISIBLE
        selectedTv.setTextColor(if (selected) resources.getColor(R.color.common_text_color) else resources.getColor(R.color.common_text_gray_color_aaa))
    }

    private fun showInputMethod() {
        Handler().postDelayed({
            val imm = mActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
        }, 200)
    }


    override fun onBackPressed(): Boolean {
        finish()
        return true
    }
}