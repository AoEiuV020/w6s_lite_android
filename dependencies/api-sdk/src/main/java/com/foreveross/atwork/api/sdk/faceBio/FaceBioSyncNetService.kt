package com.foreveross.atwork.api.sdk.faceBio

import android.content.Context
import com.foreveross.atwork.api.sdk.UrlConstantManager
import com.foreveross.atwork.api.sdk.auth.model.LoginTokenResponseJSON
import com.foreveross.atwork.api.sdk.faceBio.responseModel.FaceBioTokenResult
import com.foreveross.atwork.api.sdk.faceBio.responseModel.IsFaceBioAuthResult
import com.foreveross.atwork.api.sdk.faceBio.responseModel.SetFaceBioFilmResult
import com.foreveross.atwork.api.sdk.model.BasicResponseJSON
import com.foreveross.atwork.api.sdk.net.HttpResult
import com.foreveross.atwork.api.sdk.net.HttpURLConnectionComponent
import com.foreveross.atwork.api.sdk.util.NetGsonHelper
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo
import com.foreveross.atwork.infrastructure.support.AtworkConfig

object FaceBioSyncNetService {

    //同步, 刷脸登录
    fun loginByFaceBio(context: Context? = null, params: String): HttpResult {
        val url = UrlConstantManager.getInstance().loginByFaceBioUrl()
        val httpResult = HttpURLConnectionComponent.getInstance().postHttp(url, params)
        return handleHttpResult(httpResult, LoginTokenResponseJSON::class.java)
    }

    //同步，设置底片
    fun setFaceBioFilm(context: Context, params: String): HttpResult {
        val accessToken = LoginUserInfo.getInstance().getLoginUserAccessToken(context)
        val userId = LoginUserInfo.getInstance().getLoginUserId(context);
        val url = String.format(UrlConstantManager.getInstance().setFaceBioFilmUrl(), userId, accessToken)
        val httpResult = HttpURLConnectionComponent.getInstance().postHttp(url, params)
        return handleHttpResult(httpResult, SetFaceBioFilmResult::class.java)
    }

    //同步，验证人脸
    fun verifyFaceBio(context: Context, params: String): HttpResult {
        val accessToken = LoginUserInfo.getInstance().getLoginUserAccessToken(context)
        val userId = LoginUserInfo.getInstance().getLoginUserId(context);
        val url = String.format(UrlConstantManager.getInstance().verifyFaceBioAuthUrl(), userId, accessToken)
        val httpResult = HttpURLConnectionComponent.getInstance().postHttp(url, params)
        return handleHttpResult(httpResult, BasicResponseJSON::class.java)
    }

    //同步，是否开启人脸
    fun isFaceBioAuthOpen(username: String): HttpResult {
        val url = String.format(UrlConstantManager.getInstance().isFaceBioAuthOpenUrl, AtworkConfig.DOMAIN_ID, username)
        val httpResult = HttpURLConnectionComponent.getInstance().getHttp(url)
        return handleHttpResult(httpResult, IsFaceBioAuthResult::class.java)
    }

    //同步, 获取⼈脸识别提供商token(用于登录)
    fun getUnAuthorizeFaceBioTokenUrl(username: String): HttpResult {
        val url = String.format(UrlConstantManager.getInstance().faceBioTokenUrl, AtworkConfig.DOMAIN_ID, username)
        val httpResult = HttpURLConnectionComponent.getInstance().getHttp(url)
        return handleHttpResult(httpResult, FaceBioTokenResult::class.java)
    }

    //同步，获取⼈脸识别提供商token(登录后)
    fun getAuthorizedFaceBioToken(context: Context): HttpResult {
        val accessToken = LoginUserInfo.getInstance().getLoginUserAccessToken(context)
        val userId = LoginUserInfo.getInstance().getLoginUserId(context)
        val url = String.format(UrlConstantManager.getInstance().authorizedFaceBioTokenUrl, userId, accessToken)
        val httpResult = HttpURLConnectionComponent.getInstance().getHttp(url)
        return handleHttpResult(httpResult, FaceBioTokenResult::class.java)
    }

    //开启⼈人脸验证
    fun enableFaceIdAuth(context: Context, params: String): HttpResult {
        val accessToken = LoginUserInfo.getInstance().getLoginUserAccessToken(context)
        val userId = LoginUserInfo.getInstance().getLoginUserId(context)
        val url = String.format(UrlConstantManager.getInstance().enableFaceIdAuthUrl(), userId, accessToken)
        val httpResult = HttpURLConnectionComponent.getInstance().postHttp(url, params)
        return handleHttpResult(httpResult, BasicResponseJSON::class.java)
    }

    //关闭⼈人脸验证
    fun disableFaceIdAuth(context: Context, params: String): HttpResult  {
        val accessToken = LoginUserInfo.getInstance().getLoginUserAccessToken(context)
        val userId = LoginUserInfo.getInstance().getLoginUserId(context)
        val url = String.format(UrlConstantManager.getInstance().disableFaceIdAuthUrl(), userId, accessToken)
        val httpResult = HttpURLConnectionComponent.getInstance().postHttp(url, params)
        return handleHttpResult(httpResult, BasicResponseJSON::class.java)
    }

    private fun handleHttpResult(httpResult: HttpResult, clz: Class<out  BasicResponseJSON>): HttpResult {
        if(httpResult.isNetSuccess) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, clz))
        }
        return httpResult
    }
}