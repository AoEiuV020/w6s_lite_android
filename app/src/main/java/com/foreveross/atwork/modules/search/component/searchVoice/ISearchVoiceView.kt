package com.foreveross.atwork.modules.search.component.searchVoice


interface ISearchVoiceView {

    fun handleInit()

    fun handleDestroy()

    fun setOnSearchVoiceViewHandleListener(listener: OnSearchVoiceViewHandleListener)

    fun setVisibility(visibility: Int)

}