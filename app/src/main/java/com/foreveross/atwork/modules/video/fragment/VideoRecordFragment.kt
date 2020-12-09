package com.foreveross.atwork.modules.video.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.foreverht.workplus.ui.component.dialogFragment.AtworkAlertDialog
import com.foreveross.atwork.AtworkApplicationLike
import com.foreveross.atwork.R

import com.foreveross.atwork.infrastructure.utils.AtWorkDirUtils
import com.foreveross.atwork.infrastructure.utils.CommonUtil
import com.foreveross.atwork.infrastructure.utils.LogUtil
import com.foreveross.atwork.modules.video.activity.VideoRecordActivity
import com.foreveross.atwork.modules.video.model.VideoRecordRequestAction
import com.foreveross.atwork.modules.video.model.VideoRecordResponse
import com.foreveross.atwork.support.BackHandledFragment
import com.foreveross.atwork.utils.TimeViewUtil
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.VideoResult
import com.otaliastudios.cameraview.VideoResult.REASON_MAX_DURATION_REACHED
import com.otaliastudios.cameraview.controls.Facing
import kotlinx.android.synthetic.main.fragment_video_record.*
import java.io.File

class VideoRecordFragment : BackHandledFragment() {

    private val TOUCH_MODE_LONG_PRESS = 0
    private val TOUCH_MODE_CLOCK = 1

    private var cancelRecord = false
    private var durationRecording = 0L

    private var stoppingTimeClock = true

    private val touchMode = TOUCH_MODE_CLOCK

    private var videoRecordRequestAction: VideoRecordRequestAction? =  null

    private val runnable = {

        LogUtil.e("recording -> ${durationRecording}s")

        if (!stoppingTimeClock) {
            durationRecording += 1000L
            startTimeClock()
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_video_record, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        videoRecordRequestAction = arguments?.getParcelable(VideoRecordActivity.DATA_VIDEO_RECORD_REQUEST_ACTION)

        refreshTakeBtnInClickMode()


        videoRecordRequestAction?.run {
            vCameraViewKit.videoMaxDuration = duration * 1000

        }


        when(videoRecordRequestAction?.quality) {
            0 -> {
                vCameraViewKit.setSnapshotMaxHeight(1600)
                vCameraViewKit.setSnapshotMaxWidth(1600)
            }

            1 -> {
                vCameraViewKit.setSnapshotMaxHeight(1200)
                vCameraViewKit.setSnapshotMaxWidth(1200)
            }

            2-> {
                vCameraViewKit.setSnapshotMaxHeight(800)
                vCameraViewKit.setSnapshotMaxWidth(800)
            }

            else-> {
                vCameraViewKit.setSnapshotMaxHeight(1200)
                vCameraViewKit.setSnapshotMaxWidth(1200)
            }
        }

        if(true == videoRecordRequestAction?.front) {
            vCameraViewKit.facing = Facing.FRONT
        } else {
            vCameraViewKit.facing = Facing.BACK
        }

        vCameraViewKit.setLifecycleOwner(this)
        vCameraViewKit.addCameraListener(object : CameraListener() {

            override fun onVideoTaken(result: VideoResult) {
                LogUtil.e("onVideoTaken" + result.file.canonicalPath)


                stopTimeClock()

                if(REASON_MAX_DURATION_REACHED == result.terminationReason) {
                    AtworkAlertDialog(activity, AtworkAlertDialog.Type.CLASSIC)
                            .setTitleText(R.string.stopped_recording)
                            .setContent(R.string.stopped_recording_tip)
                            .hideDeadBtn()
                            .setDismissListener {
                                finishAndPostResult(result)
                            }
                            .show()
                    return
                }

                if (1000L > durationRecording) {
                    return
                }

                finishAndPostResult(result)
            }
        })
        registerListener()
    }


    private fun cancelAndPostResult() {
        activity?.setResult(Activity.RESULT_CANCELED)
        finish()
    }

    private fun finishAndPostResult(result: VideoResult) {


        //单位kb
        val fileSize = result.file.length() / 1024
        val durationStr = durationRecording / 1000
        val videoRecordResponse = VideoRecordResponse(
                videoPath = result.file.canonicalPath,
                videoDuration = durationStr.toString(),
                videoSize = fileSize.toString(),
                syncSystemAlbum = videoRecordRequestAction?.syncSystemAlbum ?: false
        )

        val resultIntent = Intent().apply {
            putExtra(VideoRecordActivity.DATA_VIDEO_RECORD_RESPONSE, videoRecordResponse)
        }

        activity?.setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }

    override fun findViews(view: View?) {

    }

    override fun onDestroy() {
        super.onDestroy()

        stopTimeClock()
    }

    override fun onBackPressed(): Boolean {
        cancelAndPostResult()
        return false
    }

    private fun registerListener() {

        when(touchMode) {
            TOUCH_MODE_CLOCK -> { registerClickModeListener() }

            TOUCH_MODE_LONG_PRESS-> { registerLongPressModeListener() }
        }



        ivBack.setOnClickListener { onBackPressed() }
    }

    private fun registerLongPressModeListener() {
        ivTakePhoto.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {

                if (CommonUtil.isFastClick(1000)) {
                    LogUtil.e("ACTION_DOWN isFastClick isFastClick isFastClick")
                    return@setOnTouchListener false
                }

                stoppingTimeClock = false
                startTimeClock()
                vCameraViewKit.takeVideoSnapshot(File(AtWorkDirUtils.getInstance().getFiles(AtworkApplicationLike.baseContext) + System.currentTimeMillis() + ".mp4"))


            } else if (event.action == MotionEvent.ACTION_UP) {

                LogUtil.e("ACTION_UP")

                if (cancelRecord) {
                    reset()
                    vCameraViewKit.stopVideo()

                } else {

                    if (1000L > durationRecording) {
                        toast(R.string.recording_video_too_short)
                        reset()
                    }

                    vCameraViewKit.stopVideo()

                }

            } else if (event.action == MotionEvent.ACTION_MOVE) {
    //                if (event.y < 0f) {
    //                    toast(R.string.release_cancel)
    //
    //                    cancelRecord = true
    //
    //                } else {
    //                    toast(R.string.move_up_cancel)
    //                    cancelRecord = false
    //                }
            }
            return@setOnTouchListener true
        }
    }

    private fun registerClickModeListener() {
        ivTakePhoto.setOnClickListener {
            if (CommonUtil.isFastClick(1000)) {
                return@setOnClickListener
            }


            if (stoppingTimeClock) {
                stoppingTimeClock = false
                refreshTakeBtnInClickMode()

                startTimeClock()
                vCameraViewKit.takeVideoSnapshot(File(AtWorkDirUtils.getInstance().getFiles(AtworkApplicationLike.baseContext) + System.currentTimeMillis() + ".mp4"))

                return@setOnClickListener
            }


            if (1000L >= durationRecording) {
                toast(R.string.recording_video_too_short)
                reset()
            }


            stoppingTimeClock = true
            vCameraViewKit.stopVideo()
        }
    }

    private fun startTimeClock() {

        if (stoppingTimeClock) {
            return
        }

        AtworkApplicationLike.runOnMainThread {
            tvRecordTip?.text = TimeViewUtil.getShowDuration(durationRecording, false)
            vCameraViewKit.postDelayed(runnable, 1000L)

        }
    }

    private fun stopTimeClock() {
        stoppingTimeClock = true
        vCameraViewKit?.removeCallbacks(runnable)

        refreshTakeBtnInClickMode()

    }

    private fun refreshTakeBtnInClickMode() {
        if(TOUCH_MODE_LONG_PRESS == touchMode) {
            return
        }

        if(stoppingTimeClock) {
            ivTakePhoto?.setImageResource(R.mipmap.click_to_start_record)

        } else {
            ivTakePhoto?.setImageResource(R.mipmap.click_to_stop_record)

        }
    }

    private fun reset() {
        durationRecording = 0L

        AtworkApplicationLike.runOnMainThread {
            if (TOUCH_MODE_LONG_PRESS == touchMode) {
                tvRecordTip?.setText(R.string.long_press_to_start)

            } else {
                tvRecordTip?.setText(R.string.click_to_start)
            }

            stopTimeClock()
        }

    }

}