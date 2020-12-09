package com.foreveross.atwork.modules.workbench.fragment.admin

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.bigkoo.pickerview.builder.TimePickerBuilder
import com.bigkoo.pickerview.listener.OnTimeSelectListener
import com.foreveross.atwork.AtworkApplicationLike
import com.foreveross.atwork.R
import com.foreveross.atwork.api.sdk.BaseNetWorkListener
import com.foreveross.atwork.api.sdk.net.model.UploadFileParamsMaker
import com.foreveross.atwork.api.sdk.upload.MediaCenterNetManager
import com.foreveross.atwork.api.sdk.workbench.model.response.WorkbenchAdminAddBannerItemResult
import com.foreveross.atwork.component.ProgressDialogHelper
import com.foreveross.atwork.cordova.plugin.WorkPlusImagesPlugin
import com.foreveross.atwork.cordova.plugin.model.ChooseFilesRequest.FileLimit
import com.foreveross.atwork.cordova.plugin.model.ChooseMediasRequest
import com.foreveross.atwork.cordova.plugin.model.MediaSelectedResponseJson
import com.foreveross.atwork.infrastructure.BaseApplicationLike
import com.foreveross.atwork.infrastructure.model.advertisement.AdminAdvertisementConfig
import com.foreveross.atwork.infrastructure.model.advertisement.adEnum.AdvertisementType
import com.foreveross.atwork.infrastructure.model.workbench.data.WorkbenchCardDetailData
import com.foreveross.atwork.infrastructure.model.workbench.data.WorkbenchData
import com.foreveross.atwork.infrastructure.utils.FileUtil
import com.foreveross.atwork.infrastructure.utils.LogUtil
import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.foreveross.atwork.infrastructure.utils.TimeUtil
import com.foreveross.atwork.infrastructure.utils.encryption.EncryptCacheDisk
import com.foreveross.atwork.infrastructure.utils.statusbar.StatusBarUtil
import com.foreveross.atwork.listener.TextWatcherAdapter
import com.foreveross.atwork.modules.app.adapter.AppTopAdvertsAdapter
import com.foreveross.atwork.modules.image.activity.MediaSelectActivity
import com.foreveross.atwork.modules.workbench.manager.WorkbenchAdminManager
import com.foreveross.atwork.support.BackHandledPushOrPullFragment
import com.foreveross.atwork.utils.*
import kotlinx.android.synthetic.main.fragment_workbench_admin_banner_card_banner_add.*
import java.text.SimpleDateFormat
import java.util.*


class WorkbenchAdminBannerCardBannerAddFragment: BackHandledPushOrPullFragment() {
    private val DATA_REQUEST_GET_BANNER_IMG = 0x006

    private val EMPTY_VALID_DURATION_TEXT = "- - - -/- -/- -";

    private var bannerMediaId: String? = null
    private var bannerMediaUploading = false
    private var bannerMediaUploadingMessageId: String? = null
    private var bannerImagePath: String? = null

    private lateinit var workbenchCardDetailData: WorkbenchCardDetailData
    private lateinit var workbenchData: WorkbenchData

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_workbench_admin_banner_card_banner_add, container, false)
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initData()
        initViews()
        registerListener()
    }

    override fun getAnimationRoot(): View = llRootContent

    private fun initData() {
        arguments?.getParcelable<WorkbenchCardDetailData>(WorkbenchCardDetailData::class.java.toString())?.let {
            workbenchCardDetailData = it
        }

        arguments?.getParcelable<WorkbenchData>(WorkbenchData::class.java.toString())?.let {
            workbenchData = it
        }
    }

    private fun initViews() {
        val context = AtworkApplicationLike.baseContext


        etBannerSortNumber.setInputType(InputType.TYPE_CLASS_NUMBER)
        refreshSureBtnStatus()

        LogUtil.e("StatusBarUtil.getStatusBarHeight(context) ->  ${StatusBarUtil.getStatusBarHeight(context)}")
//        ViewUtil.setTopMargin(rlTitleArea, StatusBarUtil.getStatusBarHeight(context))


    }

    private fun refreshBannerImg() {
        if(!StringUtils.isEmpty(bannerImagePath)) {
            ImageCacheHelper.displayImage(bannerImagePath, ivBannerImg, AppTopAdvertsAdapter.getDisplayImageOptions())
            ivBannerImg.isVisible = true

        } else {
            ivBannerImg.isVisible = false

        }


        if (StringUtils.isEmpty(bannerMediaId)) {
            flBannerActionAddArea.isVisible = true
            ivCancelBannerImg.isVisible = bannerMediaUploading
            flBannerImgUploading.isVisible = bannerMediaUploading

            return
        }


        flBannerActionAddArea.isVisible = false
        ivCancelBannerImg.isVisible = true
        flBannerImgUploading.isVisible = false


    }

    private fun registerListener() {
        tvCancel.setOnClickListener { onBackPressed() }

        rlRoot.setOnClickListener { onBackPressed() }

        swPutaway.setOnClickNotPerformToggle {
            swPutaway.toggle()
        }

        ivValidDurationRefresh.setOnClickListener {
            emptyAllValidDuration()
        }

        etBannerName.setInputTextWatcher(object : TextWatcherAdapter() {
            override fun afterTextChanged(text: Editable?) {
                refreshSureBtnStatus()
            }
        })

        etBannerSortNumber.setInputTextWatcher(object : TextWatcherAdapter() {
            override fun afterTextChanged(text: Editable?) {
                refreshSureBtnStatus()
            }
        })



        tvValidDurationStart.setOnClickListener {

            AtworkUtil.hideInput(activity)

            val endDateCalendar: Calendar? = getRangeDateCalendar(tvValidDurationEnd)

            val pvTime = TimePickerBuilder(it.context, OnTimeSelectListener { date, v ->
                tvValidDurationStart.text = TimeUtil.getStringForMillis(date.time, TimeUtil.getTimeFormat1(BaseApplicationLike.baseContext))
                refreshSureBtnStatus()

            }).setRangDate(null, endDateCalendar)
                    .build()

            pvTime.show()
        }


        tvValidDurationEnd.setOnClickListener {

            AtworkUtil.hideInput(activity)

            val startDateCalendar: Calendar? = getRangeDateCalendar(tvValidDurationStart)

            val pvTime =
                    TimePickerBuilder(it.context, OnTimeSelectListener { date, v ->
                        tvValidDurationEnd.text = TimeUtil.getStringForMillis(date.time, TimeUtil.getTimeFormat1(BaseApplicationLike.baseContext))
                        refreshSureBtnStatus()

                    }).setRangDate(startDateCalendar, null)
                            .build()

            pvTime.show()
        }

        flBannerActionAddArea.setOnClickListener {
            val getAlbum = MediaSelectActivity.getIntent(BaseApplicationLike.baseContext)
            getAlbum.putExtra(MediaSelectActivity.DATA_OPEN_FULL_MODE_SELECT, false)

            val fileLimit = FileLimit()
            fileLimit.mMaxSelectCount = 1
            val chooseMediasRequest = ChooseMediasRequest()
            chooseMediasRequest.mFileLimit = fileLimit

            getAlbum.putExtra(WorkPlusImagesPlugin.DATA_CHOOSE_IMAGE_REQUEST, chooseMediasRequest)

            startActivityForResult(getAlbum, DATA_REQUEST_GET_BANNER_IMG)

        }

        ivCancelBannerImg.setOnClickListener {
            MediaCenterNetManager.brokenDownloadingOrUploading(bannerMediaUploadingMessageId)
            bannerImagePath = null
            bannerMediaId = null
            refreshBannerImg()
        }

        tvSure.setOnClickListener {
            val progressDialogHelper = ProgressDialogHelper(activity)
            progressDialogHelper.show()

            val adminAdvertisementConfig = buildAdminAdvertisementConfig()


            WorkbenchAdminManager.adminAddBannerItem(
                    context = AtworkApplicationLike.baseContext,
                    workbenchData = workbenchData,
                    widgetId = workbenchCardDetailData.id.toString(),
                    workbenchAdminBannerHandleRequest = adminAdvertisementConfig,
                    listener = object : BaseNetWorkListener<WorkbenchAdminAddBannerItemResult> {
                        override fun onSuccess(result: WorkbenchAdminAddBannerItemResult) {
                            progressDialogHelper.dismiss()

                            onBackPressed()
                            toastOver(R.string.add_successfully)

                        }

                        override fun networkFail(errorCode: Int, errorMsg: String?) {
                            progressDialogHelper.dismiss()
                            ErrorHandleUtil.handleError(errorCode, errorMsg)
                        }
                    }
            )
        }
    }

    private fun buildAdminAdvertisementConfig(): AdminAdvertisementConfig {

        var beginTime = getValidDurationTimestamp(tvValidDurationStart)
        if (-1L == beginTime) {
            beginTime = TimeUtil.getCurrentTimeInMillis()
        }

        var endTime = getValidDurationTimestamp(tvValidDurationEnd)
        if (-1L == endTime) {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = beginTime
            calendar.add(Calendar.YEAR, 50)

            endTime = calendar.timeInMillis

            //            endTime = Long.MAX_VALUE
        }

        endTime += 24 * 60 * 60 * 1000L - 1

        val adminAdvertisementConfig = AdminAdvertisementConfig(
                domainId = workbenchData.domainId,
                name = etBannerName.text,
                orgCode = workbenchData.orgCode,
                serialNo = workbenchCardDetailData.id.toString(),
                mediaType = AdvertisementType.Image.valueOfString(),
                mediaId = bannerMediaId!!,
                linkUrl = etBannerLink.text,
                sort = etBannerSortNumber.text.toInt(),
                beginTime = beginTime,
                endTime = endTime,
                disabled = !swPutaway.isChecked

        )

        workbenchData.scopeRecord?.rootOrg?.mPath?.let {
            adminAdvertisementConfig.scopes = arrayOf(it)
        }

        return adminAdvertisementConfig
    }



    private fun getRangeDateCalendar(tvValidDuration: TextView): Calendar? {
        val date: Date? = getValidDuration(tvValidDuration)
        var dateCalendar: Calendar? = null
        if (null != date) {
            dateCalendar = Calendar.getInstance()
            dateCalendar.time = date
        }
        return dateCalendar
    }

    private fun getValidDurationTimestamp(tvValidDuration: TextView): Long {
        return getValidDuration(tvValidDuration)?.time ?: -1
    }

    private fun getValidDuration(tvValidDuration: TextView): Date? {
        var date: Date? = null
        try {
            date = SimpleDateFormat(TimeUtil.getTimeFormat1(AtworkApplicationLike.baseContext), Locale.getDefault()).parse(tvValidDuration.text.toString())
        } catch (e: Exception) {
            LogUtil.e(e.localizedMessage)
        }
        return date
    }

    override fun findViews(view: View) {
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)



        if (DATA_REQUEST_GET_BANNER_IMG == requestCode && WorkPlusImagesPlugin.SINGLE_SELECT_RESULT_CODE == resultCode) {

            val responseJson: MediaSelectedResponseJson = data?.getParcelableExtra(WorkPlusImagesPlugin.DATA_SELECT_IMGS) ?: return

            if (FileUtil.isEmptySize(responseJson.imageURL)) {
                return
            }
            bannerImagePath = responseJson.imageURL
            bannerImagePath = EncryptCacheDisk.getInstance().getOriginalFilePathAndCheck(bannerImagePath, false)
            uploadFile(bannerImagePath!!)


        }
    }


    private fun uploadFile(imagePath: String) {

        bannerMediaUploading = true
        tvBannerImgUploadingProgress.text = "%0"
        refreshBannerImg()

        val messageId = UUID.randomUUID().toString()
        bannerMediaUploadingMessageId = messageId

        val uploadFileParamsMaker = UploadFileParamsMaker.newRequest()
                .setType(MediaCenterNetManager.COMMON_FILE)
                .setMsgId(messageId)
                .setFilePath(imagePath)

        MediaCenterNetManager.uploadFile(activity, uploadFileParamsMaker)

        MediaCenterNetManager.removeMediaUploadListenerById(messageId, MediaCenterNetManager.UploadType.COMMON_FILE)
        val mediaUploadListener = object : MediaCenterNetManager.MediaUploadListener {

            override fun getMsgId(): String = messageId

            override fun uploadFailed(errorCode: Int, errorMsg: String?, doRefreshView: Boolean) {
                MediaCenterNetManager.removeMediaUploadListener(this);
                if (errorCode != -99) {
                    AtworkToast.showResToast(R.string.upload_file_error)
                    MediaCenterNetManager.removeUploadFailList(msgId)
                }

                bannerImagePath = null
                bannerMediaUploading = false
                refreshBannerImg()

            }


            override fun getType(): MediaCenterNetManager.UploadType = MediaCenterNetManager.UploadType.COMMON_FILE


            override fun uploadProgress(progress: Double) {
                tvBannerImgUploadingProgress.text = "%${progress.toInt()}"
            }


            override fun uploadSuccess(mediaInfo: String) {
                MediaCenterNetManager.removeMediaUploadListener(this);

                bannerMediaUploading = false
                bannerMediaId = mediaInfo
                refreshSureBtnStatus()

                refreshBannerImg()

            }

        }


        MediaCenterNetManager.addMediaUploadListener(mediaUploadListener)

    }

    private fun isEmptyValidDuration(tvValidDuration: TextView) = EMPTY_VALID_DURATION_TEXT == tvValidDuration.text

    private fun emptyAllValidDuration() {
        emptyValidDuration(tvValidDurationStart)
        emptyValidDuration(tvValidDurationEnd)

        refreshSureBtnStatus()
    }

    private fun emptyValidDuration(tvValidDuration: TextView) {
        tvValidDuration.text = EMPTY_VALID_DURATION_TEXT
    }


    private fun refreshSureBtnStatus() {
        if (inputInfoLegal()) {
            tvSure.setTextColor(ContextCompat.getColor(AtworkApplicationLike.baseContext, R.color.common_blue_bg))
            tvSure.isEnabled = true
        } else {
            tvSure.setTextColor(ContextCompat.getColor(AtworkApplicationLike.baseContext, R.color.common_text_color_999))
            tvSure.isEnabled = false
        }
    }


    private fun inputInfoLegal(): Boolean {
        if(StringUtils.isEmpty(etBannerName.text)) {
            return false
        }

        if(StringUtils.isEmpty(bannerMediaId)) {
            return false
        }

        if(StringUtils.isEmpty(etBannerSortNumber.text)) {
            return false
        }

        val inputValidDurationCount = arrayOf(tvValidDurationStart, tvValidDurationEnd).count {
            isEmptyValidDuration(it)
        }

        if(1 == inputValidDurationCount) {
            return false
        }


        return true
    }

}