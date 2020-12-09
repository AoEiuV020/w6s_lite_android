package com.foreveross.atwork.infrastructure.utils.extension

import com.foreveross.atwork.infrastructure.utils.DensityUtil

val Float.px2dp
    get() = DensityUtil.px2dip(this)

val Float.dp2px
    get() = DensityUtil.dip2px(this)


val Float.sp2px
    get() = DensityUtil.sp2px(this)


val Float.px2Dp
    get() = DensityUtil.px2dip(this)

