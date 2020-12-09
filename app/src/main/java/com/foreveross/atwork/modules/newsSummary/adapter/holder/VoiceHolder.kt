package com.foreveross.atwork.modules.newsSummary.adapter.holder

import android.content.Context
import android.os.Handler
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.foreverht.db.service.repository.SessionRepository
import com.foreveross.atwork.AtworkApplicationLike
import com.foreveross.atwork.R
import com.foreveross.atwork.api.sdk.upload.MediaCenterNetManager
import com.foreveross.atwork.infrastructure.BaseApplicationLike
import com.foreveross.atwork.infrastructure.model.Session
import com.foreveross.atwork.infrastructure.model.clickStatistics.Type
import com.foreveross.atwork.infrastructure.newmessage.post.chat.VoiceChatMessage
import com.foreveross.atwork.infrastructure.newmessage.post.chat.VoiceMedia
import com.foreveross.atwork.modules.chat.dao.ChatDaoService
import com.foreveross.atwork.modules.chat.util.AudioRecord
import com.foreveross.atwork.modules.clickStatistics.ClickStatisticsManager
import com.foreveross.atwork.modules.newsSummary.data.DataCallBack
import com.foreveross.atwork.modules.newsSummary.data.NewsSummaryRVData
import com.foreveross.atwork.modules.newsSummary.util.CheckUnReadUtil
import com.foreveross.atwork.modules.route.model.ActivityInfo
import com.foreveross.atwork.modules.voip.utils.VoipHelper
import com.foreveross.atwork.utils.AtworkToast
import com.foreveross.atwork.utils.AvatarHelper
import com.foreveross.atwork.utils.TimeViewUtil
import com.foreveross.atwork.modules.bing.listener.VoicePlayListener

class VoiceHolder(itemView: View) : RvBaseHolder(itemView) {

    private var tvTitleName: TextView = itemView.findViewById(R.id.tvTitleName)
    private var ivAvatar: ImageView = itemView.findViewById(R.id.chat_item_avatar)
    private var mTvVoice: TextView = itemView.findViewById(R.id.tvVoice)
    private var tvTitleNTime: TextView = itemView.findViewById(R.id.tvTitleNTime)
    private var ivDot: ImageView = itemView.findViewById(R.id.chat_voice_dot)
    private var mContext: Context? = null
    private var mVoiceChatMessage: VoiceChatMessage? = null
    private var itemVoiceNotifyListener: IVoiceNotifyListener? = null

    override fun bindHolder(rvData: NewsSummaryRVData, context: Context, dataCallBack: DataCallBack<NewsSummaryRVData>) {
        mContext = context
        if(!TextUtils.isEmpty(rvData.chatPostMessage!!.mDisplayName)){
            tvTitleName.text = rvData.chatPostMessage!!.mDisplayName
        }
        if (rvData.chatPostMessage is VoiceChatMessage) {
            mVoiceChatMessage = rvData.chatPostMessage as VoiceChatMessage

            mTvVoice.text = "${mVoiceChatMessage!!.duration}\""
            if (mVoiceChatMessage!!.playing) {
                mTvVoice.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(context, R.mipmap.icon_history_item_voice_stop), null, null, null)
            } else {
                mTvVoice.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(context, R.mipmap.icon_history_item_voice_play), null, null, null)
            }

            if (!mVoiceChatMessage!!.play) {
                ivDot.visibility = View.VISIBLE
            } else {
                ivDot.visibility = View.GONE
            }
            mTvVoice.setOnClickListener {
                if (!mVoiceChatMessage!!.playing) {
                    //先搜索出服务号的id
                    val sessionList = SessionRepository.getInstance().querySessions("select * from session_ where identifier_ = \"${rvData.getChatId()}\"")
                    if (sessionList.size > 0) {
                        val session = sessionList[0]
                        Handler().postDelayed({
                            handlePlayVoice(mVoiceChatMessage!!,rvData,session)
                        }, 500)
                    }
                }else{
                    AudioRecord.stopPlaying()
                    mVoiceChatMessage!!.playing = false
                    itemVoiceNotifyListener?.onItemClickListener(rvData,false)
                }
            }
        }
        tvTitleNTime.text = TimeViewUtil.getNewsSummaryItemViewTime(BaseApplicationLike.baseContext, rvData.chatPostMessage!!.deliveryTime)
        AvatarHelper.setAppAvatarById(ivAvatar, rvData.chatPostMessage!!.from, rvData.chatPostMessage!!.mOrgId, true, true)
        ivAvatar.setOnClickListener {
            dataCallBack.onResult(rvData)
        }
    }

    private fun handlePlayVoice(voiceChatMessage: VoiceChatMessage, rvData: NewsSummaryRVData,session: Session) {
        if(voiceChatMessage.playing) {
            stopVoice(voiceChatMessage, rvData)
            return
        }

        playVoice(voiceChatMessage, rvData,session)
    }

    private fun stopVoice(voiceChatMessage: VoiceChatMessage, rvData: NewsSummaryRVData) {
        AudioRecord.stopPlaying()
        voiceChatMessage.playing = false
        itemVoiceNotifyListener?.onItemClickListener(rvData,false)
    }

    private fun playVoice(voiceChatMessage: VoiceChatMessage, rvData: NewsSummaryRVData,session: Session) {
        if (VoipHelper.isHandlingVoipCall()) {
            AtworkToast.showResToast(R.string.alert_is_handling_voip_meeting_click_voip)
            return
        }

        if (MediaCenterNetManager.isDownloading(voiceChatMessage.getMediaId())) {
            return
        }

        voiceChatMessage.playing = true
        voiceChatMessage.play = true
        ChatDaoService.getInstance().updateNesSummaryMessage(voiceChatMessage)
        ivDot.visibility = View.GONE
        itemVoiceNotifyListener?.onItemClickListener(rvData,true)
        CheckUnReadUtil.CompareTime(session.identifier,mVoiceChatMessage!!.deliveryTime)
        //更新点击率
        ClickStatisticsManager.updateClick(session.identifier, Type.NEWS_SUMMARY)

        AudioRecord.playAudio(AtworkApplicationLike.baseContext, voiceChatMessage, object : VoicePlayListener {
            override fun start() {
            }

            override fun stop(voiceMedia: VoiceMedia) {
                voiceChatMessage.playing = false
                itemVoiceNotifyListener?.onItemClickListener(rvData,false)
            }

        })
    }

    // 提供set方法
    fun setVoiceNotifyListener(itemVoiceNotifyListener: IVoiceNotifyListener) {
        this.itemVoiceNotifyListener = itemVoiceNotifyListener
    }

    //自定义接口
    interface IVoiceNotifyListener {
        fun onItemClickListener(newsSummaryRVData: NewsSummaryRVData,isPlay: Boolean)
    }
}