package com.foreveross.atwork.modules.workbench.component

//import com.tencent.smtt.sdk.WebChromeClient
//import com.tencent.smtt.sdk.WebViewClient
import android.content.Context
import android.net.Uri
import android.text.TextUtils
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.WindowManager
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.model.WebViewControlAction
import com.foreveross.atwork.infrastructure.model.workbench.WorkbenchCardType
import com.foreveross.atwork.infrastructure.model.workbench.WorkbenchCustomCard
import com.foreveross.atwork.infrastructure.utils.DensityUtil
import com.foreveross.atwork.infrastructure.utils.UrlHandleHelper
import com.foreveross.atwork.modules.app.activity.WebViewActivity
import com.foreveross.atwork.modules.setting.util.getWebviewTextSizeSetNative
import com.foreveross.atwork.modules.web.util.UrlReplaceHelper
import com.foreveross.atwork.modules.workbench.manager.WorkbenchCardContentManager
import kotlinx.android.synthetic.main.component_workbench_custom_card.view.*


class WorkbenchCustomCardView : WorkbenchBasicCardView<WorkbenchCustomCard> {

    override lateinit var workbenchCard: WorkbenchCustomCard

    private var loadingUrl: String? = null

    val TAG = "WorkbenchCustomCardView"

    constructor(context: Context) : super(context) {
//        initWebViewSettings()

        initSystemWebviewSetting()


    }


    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initSystemWebviewSetting()
    }


    override fun findViews(context: Context) {
        LayoutInflater.from(context).inflate(R.layout.component_workbench_custom_card, this)
    }


    override fun refresh(workbenchCard: WorkbenchCustomCard) {
        this.workbenchCard = workbenchCard

        if(WorkbenchCardContentManager.checkRequestLegal(workbenchCard, false)) {
            refreshView(workbenchCard)

            loadingUrl = workbenchCard.linkUrl
        }




    }

    override fun refreshView(workbenchCard: WorkbenchCustomCard) {
        checkParameter(Uri.parse(workbenchCard.linkUrl))
        workbenchCard.linkUrl
                ?.let {
                    UrlHandleHelper.fixProtocolHead(it)

                }?.let {

                    WorkbenchCardContentManager.cardContentRequestRecord[workbenchCard.requestId] = System.currentTimeMillis()

                    UrlReplaceHelper.replaceKeyParams(it) { replaceResult ->
                        webView.loadUrl(replaceResult.url)
                    }
                }
    }

    private fun checkParameter(url: Uri){
        val customHeight = url.getQueryParameter("custom_height")
        val customScale = url.getQueryParameter("custom_scale")
        val openWithOut = url.getBooleanQueryParameter("open_with_out",false)

        if(!TextUtils.isEmpty(customScale)){
            val params = webView.layoutParams as FrameLayout.LayoutParams
            val wm: WindowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val dm = DisplayMetrics()
            wm.defaultDisplay.getMetrics(dm)
            val webViewWith = dm.widthPixels
            val customScaleLong: Float = customScale!!.toFloat()
            params.height = DensityUtil.dip2px(customScaleLong * webViewWith)
            webView.layoutParams = params
            webView.setCanIntercept(false)
        }else{
            if(!TextUtils.isEmpty(customHeight)){
                val params = webView.layoutParams as FrameLayout.LayoutParams
                val webviewHeight = customHeight!!.toInt()
                params.height = DensityUtil.dip2px(webviewHeight.toFloat())
                webView.layoutParams = params
                webView.setCanIntercept(false)
            }
        }

        if(openWithOut){
            webView.setCanIntercept(false)
        }
    }


    override fun getViewType(): Int = WorkbenchCardType.CUSTOM.hashCode()


    private fun initSystemWebviewSetting() {
        val settings = webView.settings           //?????????webview??????
        settings.javaScriptEnabled = true                    //??????Javascript ???js??????
        //        settings.setJavaScriptCanOpenWindowsAutomatically(true);//????????????JS???????????????
        settings.allowFileAccess = true                      //????????????????????????
        settings.setSupportZoom(true)                          //????????????
        settings.builtInZoomControls = true                  //???????????????????????????
        settings.useWideViewPort = true                      //???????????????
        settings.setSupportMultipleWindows(true)               //?????????
        settings.defaultTextEncodingName = "utf-8"            //??????????????????
        settings.setAppCacheEnabled(true)
        settings.domStorageEnabled = true
        settings.setAppCacheMaxSize(Long.MAX_VALUE)
        settings.cacheMode = WebSettings.LOAD_NO_CACHE

        settings.textZoom = getWebviewTextSizeSetNative()

        webView.webChromeClient = WebChromeClient()

        webView.webViewClient = object : WebViewClient() {

            override fun onPageFinished(view: WebView, url: String) {
                WorkbenchCardContentManager.cardContentRefreshSuccessfullyRecord[workbenchCard.requestId] = System.currentTimeMillis()


            }

            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {


                val result = handleInterceptRoute(url)
                if(result) {
                    return true
                }

                return super.shouldOverrideUrlLoading(view, url)
            }
        }


    }

    private fun handleInterceptRoute(url: String): Boolean {
        if(workbenchCard.adminDisplay) {
            return false
        }

        val uri = Uri.parse(url)
        uri.getQueryParameter("open_with_out")?.let {
            if (it.toBoolean()) {

                val webViewControlAction = WebViewControlAction.newAction().setUrl(url)
                context.startActivity(WebViewActivity.getIntent(context, webViewControlAction))

                return true
            }
        }

        return false
    }


}