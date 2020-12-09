package com.foreveross.atwork.modules.search.component.searchVoice

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.foreveross.atwork.infrastructure.support.AtworkConfig
import com.foreveross.atwork.infrastructure.support.VoiceTypeSdk
import com.foreveross.atwork.modules.search.component.SearchVoiceAliyunFloatView
import com.foreveross.atwork.modules.search.component.SearchVoiceXunfeiFloatView

class SearchVoiceFloatView: FrameLayout, ISearchVoiceView {


    var searchVoiceCoreView: ISearchVoiceView? = null

    constructor(context: Context) : super(context) {
        initCoreView()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initCoreView()
    }

    private fun initCoreView() {
        if(AtworkConfig.VOICE_CONFIG.isEnabled) {
            when(AtworkConfig.VOICE_CONFIG.sdk) {
                VoiceTypeSdk.ALIYUN -> {
                    searchVoiceCoreView = SearchVoiceAliyunFloatView(context)
                    addView(searchVoiceCoreView as SearchVoiceAliyunFloatView)
                }

                VoiceTypeSdk.XUNFEI -> {
                    searchVoiceCoreView = SearchVoiceXunfeiFloatView(context)
                    addView(searchVoiceCoreView as SearchVoiceXunfeiFloatView)
                }
            }
        }
    }


    override fun handleInit() {
        searchVoiceCoreView?.handleInit()
    }

    override fun handleDestroy() {
        searchVoiceCoreView?.handleDestroy()
    }

    override fun setOnSearchVoiceViewHandleListener(listener: OnSearchVoiceViewHandleListener) {
        searchVoiceCoreView?.setOnSearchVoiceViewHandleListener(listener)
    }

    override fun setVisibility(visibility: Int) {
        super.setVisibility(visibility)
        searchVoiceCoreView?.setVisibility(visibility)
    }
}