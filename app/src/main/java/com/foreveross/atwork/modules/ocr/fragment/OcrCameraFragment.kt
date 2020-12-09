package com.foreveross.atwork.modules.ocr.fragment

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import com.foreverht.threadGear.HighPriorityCachedTreadPoolExecutor
import com.foreveross.atwork.AtworkApplicationLike
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.utils.*
import com.foreveross.atwork.infrastructure.utils.file.FileStreamHelper
import com.foreveross.atwork.infrastructure.utils.qmui.QMUIDisplayHelper
import com.foreveross.atwork.modules.ocr.activity.OcrCameraActivity
import com.foreveross.atwork.modules.ocr.model.OcrRequestAction
import com.foreveross.atwork.modules.ocr.model.OcrResultResponse
import com.foreveross.atwork.support.BackHandledFragment
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.PictureResult
import kotlinx.android.synthetic.main.fragment_camera_ocr.*


class OcrCameraFragment: BackHandledFragment() {

    private val TAG = "OCR"

    //  高/宽
    private var rate = 1/1f

    private lateinit var recognizeRect: Rect
    private var ocrRequestAction: OcrRequestAction? = null
    private var mOrientation: Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_camera_ocr, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        initData()
        registerListener()

        ocrRequestAction?.let {
            ScreenUtils.handleOrientation(activity, it.orientation)
        }

        if(ScreenUtils.isLandscape(AtworkApplicationLike.baseContext)) {
            rlBioCameraTitlebar.background = null
        } else {
//            rlBioCameraTitlebar.setBackgroundColor(ContextCompat.getColor(AtworkApplication.baseContext, R.color.translation_333))
        }


        initCameraKit()

        flRoot.doOnPreDraw {
            produceRecognizeRect()
            vViewfinder.frame = (recognizeRect)
            vViewfinder.drawViewfinder()
            vViewfinder.isVisible = true
        }



    }


    private fun initCameraKit() {
        vCameraKit.setLifecycleOwner(this)
        vCameraKit.addCameraListener(object : CameraListener() {


            override fun onOrientationChanged(orientation: Int) {
                LogUtil.e("vCameraKit.onOrientationChanged ~~~  rotate : ${orientation}")
                this@OcrCameraFragment.mOrientation = orientation
            }

            override fun onPictureTaken(result: PictureResult) {
                LogUtil.e("vCameraKit.captureImage ~~~")

                HighPriorityCachedTreadPoolExecutor.getInstance().run {
                    val fullScreenImg = BitmapUtil.Bytes2Bitmap(result.data)

                    val cropRect = CloneUtil.cloneTo<Rect>(recognizeRect)
//                    val screenHeight = ScreenUtils.getScreenHeight(AtworkApplication.baseContext)
                    val screenHeight = getDamnScreenHeight()
                    val screenWidth = getDamnScreenWidth()

                    val radio = fullScreenImg.height / screenHeight.toFloat()
                    //计算偏移, 得出对应抠图的区域rect
                    cropRect.set((cropRect.left * radio).toInt(), (cropRect.top * radio).toInt(), (cropRect.right * radio).toInt(), (cropRect.bottom * radio).toInt())

                    var x = cropRect.left
                    var y = cropRect.top
                    var width = cropRect.width()
                    var height = cropRect.height()

                    if (x + width >= fullScreenImg.width) {
                        x = 0
                        width = fullScreenImg.width
                    }

                    if (y + height >= fullScreenImg.height) {
                        y = 0
                        height = fullScreenImg.height
                    }

                    var newImg = Bitmap.createBitmap(fullScreenImg, x, y, width, height)
                    newImg = BitmapUtil.degree(newImg, this@OcrCameraFragment.mOrientation)

                    val photoPath = AtWorkDirUtils.getInstance().getFiles(context) + System.currentTimeMillis() + ".jpg"
                    val newImgData = BitmapUtil.Bitmap2Bytes(newImg)
                    FileStreamHelper.saveFile(photoPath, newImgData)


                    AtworkApplicationLike.runOnMainThread {
                        //                    toast("拍照成功")

                        val ocrResultResponse = OcrResultResponse(cropPath = photoPath)
                        val resultIntent = Intent().apply {
                            putExtra(OcrCameraActivity.DATA_OCR_RESPONSE_ACTION, ocrResultResponse)
                        }
                        activity?.setResult(Activity.RESULT_OK, resultIntent)
                        finish()
                    }
                }

            }

        })
    }


    private fun produceRecognizeRect() {
        val realScreenHeight = getDamnScreenHeight()

        var screenHeight = realScreenHeight
        val realScreenWidth = getDamnScreenWidth()
        var screenWidth = realScreenWidth

        LogUtil.e(TAG,"getUsefulScreenHeight -> ${QMUIDisplayHelper.getUsefulScreenHeight(activity)}    getUsefulScreenWidth -> ${QMUIDisplayHelper.getUsefulScreenWidth(activity)}")
        LogUtil.e(TAG,"getScreenWidth -> ${QMUIDisplayHelper.getScreenWidth(activity)}    getScreenHeight -> ${QMUIDisplayHelper.getScreenHeight(activity)}")
        LogUtil.e(TAG, "getNavMenuHeight : ${QMUIDisplayHelper.getNavMenuHeight(activity)}")

//        LogUtil.e(TAG,"realScreenHeight -> $screenHeight    screenWidth -> $screenWidth")


        val padding = if (null == ocrRequestAction?.padding) {
            DensityUtil.dip2px(10f)
        } else {
            DensityUtil.dip2px(ocrRequestAction?.padding!!.toFloat())

        }

        screenHeight -= (padding * 2)

        screenWidth -= (padding * 2)

        LogUtil.e("height -> $screenHeight    width -> $screenWidth")

        var rectWidth = 0
        var rectHeight = 0
        if (screenWidth <= screenHeight) {

            if (1 >= rate || rate < (screenHeight / screenWidth.toFloat())) {
                rectWidth = screenWidth
                rectHeight = (rectWidth * rate).toInt()
            } else {
                rectHeight = screenHeight
                rectWidth = (rectHeight / rate).toInt()
            }

        } else {
            if (1 < rate || rate > (screenHeight / screenWidth.toFloat())) {
                rectHeight = screenHeight
                rectWidth = (rectHeight / rate).toInt()

            } else {
                rectWidth = screenWidth
                rectHeight = (rectWidth * rate).toInt()
            }
        }


//        if (rectWidth == screenWidth) {
//        }
//
//        if (rectHeight == screenHeight) {
//        }

        val left = (realScreenWidth - rectWidth) / 2
        val top = (realScreenHeight - rectHeight) / 2
        recognizeRect = Rect(left, top, left + rectWidth, top + rectHeight)


        if(0 >= rectWidth  || 0 >= rectHeight) {
            activity?.setResult(Activity.RESULT_OK, null)
            finish()
            return
        }
    }
    private fun getDamnScreenHeight(): Int {

        return flRoot.height

//        val screenHeight = if (QMUINotchHelper.hasNotch(activity)) {
//            QMUIDisplayHelper.getUsefulScreenHeight(activity)
//
//        } else {
//            QMUIDisplayHelper.getUsefulScreenHeight(activity)
//
//        }
//        return screenHeight
    }


    //    private fun getDamnScreenWidth() = QMUIDisplayHelper.getScreenWidth(activity)
    private fun getDamnScreenWidth() = flRoot.width



    override fun onStart() {
//        vCameraKit.onStart()
        super.onStart()


    }

    override fun onResume() {
//        vCameraKit.onResume()

        super.onResume()

    }

    override fun onPause() {
//        vCameraKit.onPause()

        super.onPause()


    }

    override fun onStop() {
//        vCameraKit.onStop()

        super.onStop()

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        vCameraKit.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun findViews(view: View?) {
    }

    override fun onBackPressed(): Boolean {
        finish()
        return false
    }

    private fun initData() {
        arguments?.let {
            ocrRequestAction = it.getParcelable(OcrCameraActivity.DATA_OCR_REQUEST_ACTION)
        }

        ocrRequestAction?.let {
            rate = it.rate
        }
    }

    private fun registerListener() {
        ivTakePhoto.setOnClickListener { captureImage() }

        ivBack.setOnClickListener { onBackPressed() }

    }

    private fun captureImage() {
        vCameraKit.takePictureSnapshot()
    }


}