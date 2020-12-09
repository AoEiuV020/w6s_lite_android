package com.foreveross.atwork.infrastructure.model.face.aliyun

import com.foreveross.atwork.infrastructure.BaseApplicationLike
import com.foreveross.atwork.infrastructure.model.face.aliyun.request.GetAliyunFaceBioTokenRequest
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo
import com.foreveross.atwork.infrastructure.support.AtworkConfig


class StartBioAuthAction(

        domainId: String = AtworkConfig.DOMAIN_ID,

        userId: String? = LoginUserInfo.getInstance().getLoginUserId(BaseApplicationLike.baseContext),

        ticketId: String? = null

) : GetAliyunFaceBioTokenRequest(domainId, userId, ticketId)