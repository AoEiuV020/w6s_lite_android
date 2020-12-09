package com.foreveross.atwork.modules.aboutme.fragment

import android.Manifest
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import com.foreveross.atwork.AtworkApplicationLike
import com.foreveross.atwork.R
import com.foreveross.atwork.api.sdk.Employee.EmployeeAsyncNetService
import com.foreveross.atwork.api.sdk.qrcode.QrcodeAsyncNetService
import com.foreveross.atwork.infrastructure.BaseApplicationLike
import com.foreveross.atwork.infrastructure.model.Employee
import com.foreveross.atwork.infrastructure.model.user.User
import com.foreveross.atwork.infrastructure.permissions.PermissionsManager
import com.foreveross.atwork.infrastructure.permissions.PermissionsResultAction
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo
import com.foreveross.atwork.infrastructure.utils.BitmapUtil
import com.foreveross.atwork.infrastructure.utils.ImageShowHelper
import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.foreveross.atwork.manager.EmployeeManager
import com.foreveross.atwork.modules.aboutme.activity.PersonalQrcodeActivity
import com.foreveross.atwork.modules.image.activity.MediaSelectActivity
import com.foreveross.atwork.modules.qrcode.activity.QrcodeScanActivity
import com.foreveross.atwork.modules.qrcode.fragment.QrcodeScanFragment
import com.foreveross.atwork.modules.voip.utils.VoipHelper
import com.foreveross.atwork.support.BackHandledFragment
import com.foreveross.atwork.utils.AtworkToast
import com.foreveross.atwork.utils.AtworkUtil
import com.foreveross.atwork.utils.ErrorHandleUtil
import com.foreveross.atwork.utils.ImageCacheHelper
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.runtime.Permission
import kotlinx.android.synthetic.main.fragment_personal_qrcode_v2.*
import java.util.*

class PersonalQrcodeFragmentV2: BackHandledFragment() {

    private lateinit var ivBack: ImageView
    private lateinit var tvTitle: TextView

    private var user: User? = null
    private var w6sUserQrcodeCardBitmap: Bitmap? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_personal_qrcode_v2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initData()
        initUI()
        registerListener()
    }

    override fun findViews(view: View) {
        ivBack = vTitleBar.findViewById(R.id.title_bar_common_back)
        tvTitle = vTitleBar.findViewById(R.id.title_bar_common_title)
    }

    private fun registerListener() {
        ivBack.setOnClickListener { onBackPressed() }

        tvSave.setOnClickListener {
            AndPermission
                    .with(mActivity)
                    .runtime()
                    .permission(Permission.WRITE_EXTERNAL_STORAGE, Permission.READ_EXTERNAL_STORAGE)
                    .onGranted {
                        val qrBytes = BitmapUtil.Bitmap2Bytes(w6sUserQrcodeCardBitmap, false)
                        val result = ImageShowHelper.saveImageToGallery(activity, qrBytes, null, false)
                        if (result) {
                            AtworkToast.showResToast(R.string.save_success)
                        } else {
                            AtworkToast.showResToast(R.string.save_wrong)
                        }
                    }
                    .onDenied { AtworkUtil.popAuthSettingAlert(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) }
                    .start()
        }


        tvScan.setOnClickListener {
            if (VoipHelper.isHandlingVoipCall()) {
                AtworkToast.showResToast(R.string.alert_is_handling_voip_meeting_click_voip)
                return@setOnClickListener
            }


            PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(mActivity, arrayOf(Manifest.permission.CAMERA), object : PermissionsResultAction() {
                override fun onGranted() {
                    val intent = QrcodeScanActivity.getIntent(mActivity)
                    startActivity(intent)
                }

                override fun onDenied(permission: String) {
                    AtworkUtil.popAuthSettingAlert(mActivity, permission)
                }
            })

        }

    }

    private fun initUI() {
        tvTitle.setText(R.string.qr_postcard)
        tvHint.text = getString(R.string.personal_qrcode_hint, getString(R.string.app_name))


        user?.let {
            ImageCacheHelper.displayImageByMediaId(it.mAvatar, ivAvatar, ImageCacheHelper.getRoundAvatarOptions())


            EmployeeManager.getInstance().queryEmp(AtworkApplicationLike.baseContext, it.mUserId, PersonalShareInfo.getInstance().getCurrentOrg(AtworkApplicationLike.baseContext), object : EmployeeAsyncNetService.QueryEmployeeInfoListener {
                override fun onSuccess(employee: Employee) {
                    tvName.text = employee.showName
                    tvOrg.text = employee.lastOrgName
                    tvOrg.isVisible = !StringUtils.isEmpty(tvOrg.text.toString())

                    tvJobTitle.text = employee.lastTwoShowJobTitleFrom
                    tvJobTitle.isVisible = !StringUtils.isEmpty(tvJobTitle.text.toString())
                }

                override fun networkFail(errorCode: Int, errorMsg: String?) {
                    ErrorHandleUtil.handleError(errorCode, errorMsg)
                }

            })



            val tag = UUID.randomUUID().toString()
            ivQrcode.tag = tag

            QrcodeAsyncNetService.getInstance().fetchPersonalQrcode(mActivity, it.mUserId, object : QrcodeAsyncNetService.OnGetQrcodeListener {
                override fun success(qrcodeBitmap: Bitmap, effectTime: Long) {
                    w6sUserQrcodeCardBitmap = qrcodeBitmap

                    if (tag == ivQrcode.tag) {
                        setW6sUserCard(qrcodeBitmap)
                    }
                }


                override fun networkFail(errorCode: Int, errorMsg: String) {
                    ErrorHandleUtil.handleError(errorCode, errorMsg)
                }
            })

        }



    }


    private fun setW6sUserCard(qrcodeBitmap: Bitmap) {
        ivQrcode.scaleType = ImageView.ScaleType.FIT_XY
        ivQrcode.setImageBitmap(qrcodeBitmap)
    }

    private fun initData() {
        user = arguments?.getParcelable(PersonalQrcodeActivity.INTENT_DATA)

    }

    override fun onBackPressed(): Boolean {
        finish()
        return false
    }
}