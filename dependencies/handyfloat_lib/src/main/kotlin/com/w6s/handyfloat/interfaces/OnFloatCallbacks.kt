package com.w6s.handyfloat.interfaces

import android.view.MotionEvent
import android.view.View

/**
 *  create by reyzhang22 at 2019-08-16
 */
interface OnFloatCallbacks {
    /**
     * 浮窗的创建结果，是否创建成功
     *
     * @param isCreated     是否创建成功
     * @param msg           失败返回的结果
     * @param view          浮窗xml布局
     */
    fun createdResult(isCreated: Boolean, msg: String?, view: View?)

    fun show(view: View)

    fun hide(view: View)

    fun dismiss()

    /**
     * 触摸事件的回调
     */
    fun touchEvent(view: View,event: MotionEvent)

    /**
     * 浮窗被拖拽时的回调，坐标为浮窗的左上角坐标
     */
    fun drag(view: View, event: MotionEvent)

    /**
     * 拖拽结束时的回调，坐标为浮窗的左上角坐标
     */
    fun dragEnd(view: View)
}