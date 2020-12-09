package com.foreveross.atwork.api.sdk.model

import com.foreveross.atwork.infrastructure.BaseApplicationLike
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo

open class BasicWithAuthRequest (

    val token: String = LoginUserInfo.getInstance().getLoginUserAccessToken(BaseApplicationLike.baseContext),

    val userId: String = LoginUserInfo.getInstance().getLoginUserAccessToken(BaseApplicationLike.baseContext)
)