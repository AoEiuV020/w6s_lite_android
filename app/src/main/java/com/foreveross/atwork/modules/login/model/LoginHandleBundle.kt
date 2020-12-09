package com.foreveross.atwork.modules.login.model

class LoginHandleBundle {
    var username: String? = null

    var psw: String? = null

    var secureCode: String? = null

    lateinit var loginControlViewBundle: LoginControlViewBundle

    var isRefreshCode: Boolean = false
}