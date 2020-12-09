package com.foreveross.atwork.modules.newsSummary.adapter

import android.content.Context
import android.os.Handler
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.foreverht.db.service.repository.SessionRepository
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.model.biometricAuthentication.BiometricAuthenticationProtectItemType
import com.foreveross.atwork.infrastructure.model.clickStatistics.Type
import com.foreveross.atwork.infrastructure.newmessage.post.chat.FileTransferChatMessage
import com.foreveross.atwork.infrastructure.utils.ListUtil
import com.foreveross.atwork.modules.chat.component.history.MessageHistoryLoadMoreView
import com.foreveross.atwork.modules.chat.fragment.FileStatusFragment
import com.foreveross.atwork.modules.chat.util.AudioRecord
import com.foreveross.atwork.modules.clickStatistics.ClickStatisticsManager.updateClick
import com.foreveross.atwork.modules.newsSummary.adapter.holder.*
import com.foreveross.atwork.modules.newsSummary.data.DataCallBack
import com.foreveross.atwork.modules.newsSummary.data.NewsSummaryRVData
import com.foreveross.atwork.modules.newsSummary.util.CheckUnReadUtil
import com.foreveross.atwork.modules.route.model.ActivityInfo

class NewsSummaryRvAdapter(mContext: Context, mFragment: Fragment, private var list: ArrayList<NewsSummaryRVData>?) :
        RecyclerView.Adapter<RvBaseHolder>() {

    private var context: Context? = mContext
    private var fragment: Fragment? = mFragment
    private var itemClickListener: IKotlinItemClickListener? = null
    private var endHolder: EndHolder? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RvBaseHolder {
        when(viewType){
            NewsSummaryRVData.OFTEN_READ ->
                return OftenReadHolder(LayoutInflater.from(context).inflate(R.layout.holder_new_summary_often_read, parent, false))
            NewsSummaryRVData.WORDS ->
                return WordsHodler(LayoutInflater.from(context).inflate(R.layout.holder_new_summary_words, parent, false))
            NewsSummaryRVData.IMG_WORD ->
                return ImgWordHolder(LayoutInflater.from(context).inflate(R.layout.holder_new_summary_img_word, parent, false))
            NewsSummaryRVData.IMG_WORDS ->
                return ImgWordsHolder(LayoutInflater.from(context).inflate(R.layout.holder_new_summary_img_words, parent, false))
            NewsSummaryRVData.VOICE -> {
                val voiceHolder = VoiceHolder(LayoutInflater.from(context).inflate(R.layout.holder_new_summary_voice, parent, false))
                voiceHolder.setVoiceNotifyListener(object : VoiceHolder.IVoiceNotifyListener{
                    override fun onItemClickListener(newsSummaryRVData: NewsSummaryRVData,isPlay: Boolean) {
                        for(i in 0 until list!!.size){
                            if(list!![i].getChatPostMessage() != null && list!![i].getChatPostMessage()!!.deliveryId == newsSummaryRVData.getChatPostMessage()!!.deliveryId){
                                notifyItemChanged(i)
                                return
                            }
                        }
                    }
                })
                return voiceHolder
            }
            NewsSummaryRVData.VIDEO ->
                return VideoHolder(LayoutInflater.from(context).inflate(R.layout.holder_new_summary_video, parent, false))
            NewsSummaryRVData.FILE ->{
                val fileHolder =  FileHolder(LayoutInflater.from(context).inflate(R.layout.holder_new_summary_file, parent, false))
                fileHolder.setClickListener(object : FileHolder.ClickListener{
                    override fun onClick(fileTransferChatMessage: FileTransferChatMessage,id: String) {
                        if(fragment != null) {
                            val sessionList = SessionRepository.getInstance().querySessions("select * from session_ where identifier_ = \"${id}\"")
                            if(sessionList.size > 0) {
                                val session = sessionList[0]
                                Handler().postDelayed({
                                    val fileStatusFragment = FileStatusFragment()
                                    fileStatusFragment.initBundle(id, fileTransferChatMessage, null)
                                    fragment!!.fragmentManager?.let { fileStatusFragment.show(it, "FILE_DIALOG") }
                                    AudioRecord.stopPlaying()
                                    CheckUnReadUtil.CompareTime(session.identifier,fileTransferChatMessage.deliveryTime)
                                    //更新点击率
                                    updateClick(session.identifier, Type.NEWS_SUMMARY)
                                }, 500)
                            }

                        }
                    }
                })
                return fileHolder
            }
            NewsSummaryRVData.END ->{
                endHolder = EndHolder(LayoutInflater.from(context).inflate( MessageHistoryLoadMoreView().layoutId, parent, false))
                return endHolder!!
            }
            NewsSummaryRVData.SEARCH_CONTENT ->{
                val searchContentHolder = SearchContentHolder(LayoutInflater.from(context).inflate( R.layout.holder_new_summary_search_content, parent, false))
                searchContentHolder.setFileClickListener(object  : SearchContentHolder.FileClickListener{
                    override fun onClick(fileTransferChatMessage: FileTransferChatMessage, id: String) {
                        if(fragment != null) {
                            val sessionList = SessionRepository.getInstance().querySessions("select * from session_ where identifier_ = \"${id}\"")
                            if(sessionList.size > 0) {
                                val session = sessionList[0]
                                val nextActivityInfo = ActivityInfo(null, "unknown", ListUtil.makeSingleList(BiometricAuthenticationProtectItemType.FORCE.transferToActivityTag()))
                                Handler().postDelayed({
                                    val fileStatusFragment = FileStatusFragment()
                                    fileStatusFragment.initBundle(id, fileTransferChatMessage, null)
                                    fragment!!.fragmentManager?.let { fileStatusFragment.show(it, "FILE_DIALOG") }
                                    AudioRecord.stopPlaying()
                                    CheckUnReadUtil.CompareTime(session.identifier,fileTransferChatMessage.deliveryTime)
                                    //更新点击率
                                    updateClick(session.identifier, Type.NEWS_SUMMARY)
                                }, 500)
                            }

                        }
                    }
                })
                return searchContentHolder
            }
            NewsSummaryRVData.SEARCH_SERVICE ->{
                return SearchServiceHolder(LayoutInflater.from(context).inflate( R.layout.holder_new_summary_search_service, parent, false))
            }
        }
        return WordsHodler(LayoutInflater.from(context).inflate(R.layout.holder_new_summary_words, parent, false))
    }

    override fun getItemViewType(position: Int): Int {
        return list!![position].type
    }

    override fun getItemCount(): Int = list?.size!!

    override fun onBindViewHolder(holder: RvBaseHolder, position: Int) {
        holder.bindHolder(list!![position], this.context!!,object : DataCallBack<NewsSummaryRVData>(){
            override fun onResult(data: NewsSummaryRVData?) {
                if(data != null && itemClickListener != null){
                    itemClickListener!!.onItemClickListener(data)
                }
            }

            override fun onFailure(data: String) {
            }
        })

        /*// 点击事件
        holder.itemView.setOnClickListener {
            itemClickListener!!.onItemClickListener(position)
        }*/

    }

    fun setEndLoading(isShow: Boolean){
        if(endHolder != null){
            endHolder!!.showLoading(isShow)
            notifyItemChanged(itemCount)
        }
    }

    // 提供set方法
    fun setOnKotlinItemClickListener(itemClickListener: IKotlinItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    //自定义接口
    interface IKotlinItemClickListener {
        fun onItemClickListener(newsSummaryRVData: NewsSummaryRVData)
    }

}