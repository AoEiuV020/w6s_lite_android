package com.w6s.handyfloat

import android.view.Gravity
import com.w6s.handyfloat.enums.SeamMode
import com.w6s.handyfloat.enums.ShowMode
import com.w6s.handyfloat.interfaces.OnFloatCallbacks
import com.w6s.handyfloat.interfaces.ISetContentView

/**
 *  悬浮窗配置数据类
 *  create by reyzhang22 at 2019-08-16
 */

data class FloatConfig(

        // 布局id
        var layoutId: Int? = null,

        // 悬浮窗标签
        var floatTag: String? = null,

        // 能否被拖拽
        var dragEnable: Boolean = true,

        // 正在被拖拽
        var draging: Boolean = false,

        var showing: Boolean = false,

        // 粘粘模式, 默认不粘粘
        var seamMode: SeamMode = SeamMode.Default,

        // 显示模式，默认当前activity
        var showMode:   ShowMode = ShowMode.CurrentActivity,

        // 是否横向充满父布局
        var matchWidth: Boolean = false,

        //  是否纵向充满父布局
        var matchHeight: Boolean = false,

        var gravity: Int = Gravity.NO_GRAVITY,

        // 坐标的偏移量
        var offsetPair: Pair<Int, Int> = Pair(0, 0),
        // 固定的初始坐标，左上角坐标
        var locationPair: Pair<Int, Int> = Pair(0, 0),

        // Callbacks
        var setContentViewInterface: ISetContentView? = null,

        var floatCallbacks: OnFloatCallbacks? = null,

        // 不需要显示系统浮窗的页面集合，参数为类名
        val filterSet: MutableSet<String> = mutableSetOf(),
        // 是否需要显示，当过滤信息匹配上时，该值为false
        internal var needShow: Boolean = true

)
