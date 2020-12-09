package com.foreveross.atwork.modules.group.module

import android.os.Parcelable
import com.foreveross.atwork.modules.route.action.RouteAction

abstract class SelectToHandleAction(open var max: Int = -1) : RouteAction(), Parcelable