package com.foreveross.atwork.modules.newsSummary.data

import com.foreveross.atwork.infrastructure.model.app.App
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage
import com.google.gson.annotations.SerializedName

class NewsSummaryRVData{
    companion object{
        /**纯文字*/
        const val WORDS = 1
        /**图文*/
        const val IMG_WORD = 2
        /**多图文消息*/
        const val IMG_WORDS = 3
        /**语音*/
        const val VOICE = 4
        /**视频*/
        const val VIDEO = 5
        /**文件*/
        const val FILE = 6
        /**结束*/
        const val END = 7
        /**常读服务号*/
        const val OFTEN_READ = 8
        /**搜索内容显示*/
        const val SEARCH_CONTENT = 9
        /**搜索服务号显示*/
        const val SEARCH_SERVICE = 10
    }
    /**类型*/
    @SerializedName("type")
    internal var type: Int = 1
    /**会话id*/
    @SerializedName("chatId")
    internal var chatId: String? = null
    /**消息详情*/
    @SerializedName("chatPostMessage")
    internal var chatPostMessage: ChatPostMessage? = null
    /**列表（用于常读显示）*/
    internal var appList: ArrayList<App>? = null
    /**搜索的字符串（用于服务号搜索页面）*/
    @SerializedName("searchValue")
    internal var searchValue: String? = null
    /**是否需要显示标题（用于服务号搜索页面）*/
    @SerializedName("showTitle")
    internal var showTitle: Boolean = false

    fun setType(type: Int){
        this.type = type
    }

    fun getType() : Int{
        return this.type
    }

    fun setChatId(chatId: String){
        this.chatId = chatId
    }

    fun getChatId() : String?{
        return this.chatId
    }

    fun setChatPostMessage(chatPostMessage: ChatPostMessage){
        this.chatPostMessage = chatPostMessage
    }

    fun getChatPostMessage() : ChatPostMessage?{
        return this.chatPostMessage
    }

    fun setAppList(appList: ArrayList<App>){
        this.appList = appList
    }

    fun getAppList() : ArrayList<App>?{
        return this.appList
    }

    fun setSearchValue(searchValue: String){
        this.searchValue = searchValue
    }

    fun getSearchValue() : String?{
        return this.searchValue
    }

    fun setShowTitle(showTitle: Boolean){
        this.showTitle = showTitle
    }

    fun getShowTitle() : Boolean{
        return this.showTitle
    }
}