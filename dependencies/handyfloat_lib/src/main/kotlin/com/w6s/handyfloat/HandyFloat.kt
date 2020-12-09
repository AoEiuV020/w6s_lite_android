package com.w6s.handyfloat

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.fragment.app.FragmentActivity
import android.view.View
import com.foreveross.atwork.infrastructure.utils.rom.FloatWindowPermissionUtil
import com.w6s.handyfloat.component.PermissionFragment
import com.w6s.handyfloat.enums.SeamMode
import com.w6s.handyfloat.enums.ShowMode
import com.w6s.handyfloat.interfaces.ISetContentView
import com.w6s.handyfloat.interfaces.OnFloatCallbacks
import com.w6s.handyfloat.interfaces.OnFloatPermissionResult
import com.w6s.handyfloat.utils.FloatLifecycleUtils
import java.lang.ref.WeakReference

/**
 *  create by reyzhang22 at 2019-08-15
 *
 *  悬浮窗统一对外入口
 */
class HandyFloat {


    companion object {

        internal var isDebug: Boolean = false
        // 通过弱引用持有Activity，防止内容泄漏，适用于只有一个浮窗的情况
        private var activityWr: WeakReference<Activity>? = null

        @JvmStatic
        @JvmOverloads
        fun init(application: Application) {
            FloatLifecycleUtils.setLifecycleCallbacks(application)
        }

        @JvmStatic
        fun with(activity: FragmentActivity): Builder {
            activityWr = WeakReference(activity)
            return Builder(activity)
        }

        @JvmStatic
        @JvmOverloads
        fun dismiss(activity: Activity? = null, floatTag: String? = null) =
                manager(activity)?.dismiss(floatTag)

        @JvmStatic
        @JvmOverloads
        fun hide(activity: Activity? = null, floatTag: String? = null) =
                manager(activity)?.setVisibility(floatTag, View.GONE)

        @JvmStatic
        @JvmOverloads
        fun show(activity: Activity? = null, floatTag: String? = null) =
                manager(activity)?.setVisibility(floatTag, View.VISIBLE)

        @JvmStatic
        @JvmOverloads
        fun setDragEnable(
                activity: Activity? = null, dragEnable: Boolean, floatTag: String? = null
        ) = manager(activity)?.setDragEnable(dragEnable, floatTag)

        @JvmStatic
        @JvmOverloads
        fun isShow(activity: Activity? = null, floatTag: String? = null) =
                manager(activity)?.isShow(floatTag)

        /**
         * 获取Activity浮窗管理类
         */
        private fun manager(activity: Activity?): ActivityFloatManager? {
            val a: Activity? = activity ?: activityWr?.get()
            return if (a != null) ActivityFloatManager(a) else null
        }

        // *************************** 以下系统浮窗的相关方法 ***************************
        /**
         * 关闭系统级浮窗，发送广播消息，在Service内部接收广播
         */
        @JvmStatic
        @JvmOverloads
        fun dismissAppFloat(context: Context, tag: String? = null) =
                HandyFloatService.dismiss(context, tag)

        /**
         * 隐藏系统浮窗，发送广播消息，在Service内部接收广播
         */
        @JvmStatic
        @JvmOverloads
        fun hideAppFloat(context: Context, tag: String? = null) =
                HandyFloatService.setVisible(context, false, tag)

        /**
         * 显示系统浮窗，发送广播消息，在Service内部接收广播
         */
        @JvmStatic
        @JvmOverloads
        fun showAppFloat(context: Context, tag: String? = null) =
                HandyFloatService.setVisible(context, true, tag)

        /**
         * 设置系统浮窗是否可拖拽，先获取浮窗的config，后修改相应属性
         */
        @JvmStatic
        @JvmOverloads
        fun appFloatDragEnable(dragEnable: Boolean, tag: String? = null) =
                getConfig(tag).let { it?.dragEnable = dragEnable }

        /**
         * 获取系统浮窗是否显示，通过浮窗的config，获取显示状态
         */
        @JvmStatic
        @JvmOverloads
        fun appFloatIsShow(tag: String? = null) = getConfig(tag) != null && getConfig(tag)!!.showing

        /**
         * 以下几个方法为：系统浮窗过滤页面的添加、移除、清空
         */
        @JvmStatic
        @JvmOverloads
        fun filterActivity(activity: Activity, tag: String? = null) =
                getConfig(tag).let { it?.filterSet?.add(activity.componentName.className) }

        @JvmStatic
        @JvmOverloads
        fun filterActivities(tag: String? = null, vararg clazz: Class<*>) =
                clazz.forEach { c -> getConfig(tag).let { it?.filterSet?.add(c.name) } }

        @JvmStatic
        @JvmOverloads
        fun removeFilter(activity: Activity, tag: String? = null) =
                getConfig(tag).let { it?.filterSet?.remove(activity.componentName.className) }

        @JvmStatic
        @JvmOverloads
        fun removeFilters(tag: String? = null, vararg clazz: Class<*>) =
                clazz.forEach { c -> getConfig(tag).let { it?.filterSet?.remove(c.name) } }

        @JvmStatic
        @JvmOverloads
        fun clearFilters(tag: String? = null) = getConfig(tag)?.filterSet?.clear()

        @JvmStatic
        @JvmOverloads
        fun createAppFloat(context: Context, config: FloatConfig) = HandyFloatService.startService(context, config)

        /**
         * 获取系统浮窗的config
         */
        private fun getConfig(tag: String?) =
                HandyFloatService.floatMap[tag ?: HandyFloatService.DEFAULT_TAG]?.config
    }


    /**
     * 浮窗的属性构建类，支持链式调用
     */
    class Builder(private val activity: FragmentActivity) : OnFloatPermissionResult {

        // 创建浮窗数据类，方便管理配置
        private val config = FloatConfig()

        fun setSidePattern(seamMode: SeamMode): Builder {
            config.seamMode = seamMode
            return this
        }

        fun setShowPattern(showMode: ShowMode): Builder {
            config.showMode = showMode
            return this
        }

        fun setLayout(layoutId: Int): Builder {
            config.layoutId = layoutId
            return this
        }

        fun setGravity(gravity: Int, offsetX: Int = 0, offsetY: Int = 0): Builder {
            config.gravity = gravity
            config.offsetPair = Pair(offsetX, offsetY)
            return this
        }

        fun setLocation(x: Int, y: Int): Builder {
            config.locationPair = Pair(x, y)
            return this
        }

        fun setTag(floatTag: String?): Builder {
            // 如果tag为空，默认使用类名
            config.floatTag = floatTag ?: activity.componentName.className
            return this
        }

        fun setDragEnable(dragEnable: Boolean): Builder {
            config.dragEnable = dragEnable
            return this
        }

        fun invokeView(contentView: ISetContentView): Builder {
            config.setContentViewInterface = contentView
            return this
        }

        fun registerCallbacks(callbacks: OnFloatCallbacks): Builder {
            config.floatCallbacks = callbacks
            return this
        }



        fun setMatchParent(widthMatch: Boolean = false, heightMatch: Boolean = false): Builder {
            config.matchWidth = widthMatch
            config.matchHeight = heightMatch
            return this
        }

        // 设置需要过滤的Activity，仅对系统浮窗有效
        fun setFilter(vararg clazz: Class<*>): Builder {
            clazz.forEach { config.filterSet.add(it.name) }
            return this
        }

        /**
         * 创建浮窗，包括Activity浮窗和系统浮窗，如若系统浮窗无权限，先进行权限申请
         */
        fun show() {
            if (config.layoutId != null) {
                when {
                    // 仅当页显示，则直接创建activity浮窗
                    config.showMode == ShowMode.CurrentActivity -> createActivityFloat()

                    // 系统浮窗需要先进行权限审核，有权限则创建app浮窗
                    FloatWindowPermissionUtil.isFloatWindowOpAllowed(activity) -> createAppFloat()

                    // 申请浮窗权限
                    else -> PermissionFragment.requestPermission(activity, this)
                }
            } else {
                config.floatCallbacks?.createdResult(false, "未设置浮窗布局文件", null)
            }
        }

        /**
         * 通过Activity浮窗管理类，创建Activity浮窗
         */
        private fun createActivityFloat() = ActivityFloatManager(activity).createActivityFloatView(config)

        /**
         * 通过Service创建系统浮窗
         */
        private fun createAppFloat() = HandyFloatService.startService(activity, config)

        /**
         * 申请浮窗权限的结果回调
         */
        override fun permissionResult(isOpen: Boolean) {
            if (isOpen) createAppFloat()
            else {
                config.floatCallbacks?.createdResult(false, "系统浮窗权限不足，开启失败", null)
            }
        }
    }

}
