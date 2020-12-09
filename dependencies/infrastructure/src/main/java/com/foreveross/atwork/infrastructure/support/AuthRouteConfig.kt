package com.foreveross.atwork.infrastructure.support

import com.foreveross.atwork.infrastructure.utils.StringUtils
import java.util.*

class AuthRouteConfig: BaseConfig() {


    /**
     * 自定义配置的"忘记密码"跳转 url (目前中集使用)
     * */
    var customForgetPwdJumpUrl: String = StringUtils.EMPTY


    /**
     * 自定义配置的"修改密码"跳转 url (目前中集使用)
     * */
    var customModifyPwdJumpUrl: String = StringUtils.EMPTY


    /**
     * 自定义验证码登录 url
     * */
    var customVerificationCodeLogin: String = StringUtils.EMPTY

    var customFaceLoginJumpUrl: String = StringUtils.EMPTY

    override fun parse(pro: Properties) {


        pro.getProperty("CUSTOM_FORGET_PWD_JUMP_URL")?.apply {
            customForgetPwdJumpUrl = this
        }


        pro.getProperty("CUSTOM_MODIFY_PWD_JUMP_URL")?.apply {
            customModifyPwdJumpUrl = this
        }


        pro.getProperty("CUSTOM_VERIFICATION_CODE_LOGIN")?.apply {
            customVerificationCodeLogin = this
        }


        pro.getProperty("CUSTOM_FACE_LOGIN_JUMP_URL")?.apply {
            customFaceLoginJumpUrl = this
        }
    }

}