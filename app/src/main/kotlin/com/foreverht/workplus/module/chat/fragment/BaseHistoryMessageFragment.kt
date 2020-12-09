package com.foreverht.workplus.module.chat.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.foreverht.workplus.module.chat.activity.BaseMessageHistoryActivity
import com.foreveross.atwork.AtworkApplicationLike
import com.foreveross.atwork.R
import com.foreveross.atwork.api.sdk.message.MessageAsyncNetService
import com.foreveross.atwork.api.sdk.message.model.QueryMessageHistoryRequest
import com.foreveross.atwork.api.sdk.upload.MediaCenterNetManager
import com.foreveross.atwork.component.ProgressDialogHelper
import com.foreveross.atwork.component.recyclerview.BaseQuickAdapter
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage
import com.foreveross.atwork.infrastructure.newmessage.post.chat.*
import com.foreveross.atwork.infrastructure.utils.LogUtil
import com.foreveross.atwork.modules.bing.listener.VoicePlayListener
import com.foreveross.atwork.modules.chat.adapter.MessageHistoryAdapter
import com.foreveross.atwork.modules.chat.fragment.FileStatusFragment
import com.foreveross.atwork.modules.chat.util.ArticleItemHelper
import com.foreveross.atwork.modules.chat.util.AudioRecord
import com.foreveross.atwork.modules.image.activity.ImageSwitchInChatActivity
import com.foreveross.atwork.modules.image.fragment.ImageSwitchFragment
import com.foreveross.atwork.modules.voip.utils.VoipHelper
import com.foreveross.atwork.support.BackHandledFragment
import com.foreveross.atwork.utils.AtworkToast
import com.foreveross.atwork.utils.ChatMessageHelper
import com.foreveross.atwork.utils.ErrorHandleUtil
import java.util.*

const val REQUEST_LIMIT = 10

abstract class BaseHistoryMessageFragment: BackHandledFragment() , BaseQuickAdapter.RequestLoadMoreListener{


    protected lateinit var messageHistoryAdapter: MessageHistoryAdapter

    protected var progressDialogHelper: ProgressDialogHelper? = null

    protected var messageHistoryViewAction: BaseMessageHistoryActivity.MessageHistoryViewAction? = null
    protected var page = 0
    protected val messageList = ArrayList<ChatPostMessage>()
    protected var messageType = ""
    protected var tagId: String? = ""
    protected var keyword = ""


    abstract fun getNoMessagesView(): View?
    abstract fun getMessageListView(): RecyclerView?

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        progressDialogHelper?.show()
    }

    protected fun startFetchMessageData() {
        messageHistoryViewAction?.apply {
            val queryMessageHistoryRequest = QueryMessageHistoryRequest(getRequestSkip(page), REQUEST_LIMIT, app.mOrgId, app.mAppId, messageType, tagId, keyword)
            MessageAsyncNetService.queryMessageHistory(AtworkApplicationLike.baseContext, queryMessageHistoryRequest, object: MessageAsyncNetService.GetHistoryMessageListener {
                override fun networkFail(errorCode: Int, errorMsg: String) {
                    progressDialogHelper?.dismiss()

                    ErrorHandleUtil.handleError(errorCode, errorMsg)
                }

                override fun getHistoryMessageSuccess(historyMessages: MutableList<ChatPostMessage>, realOfflineSize: Int) {
                    progressDialogHelper?.dismiss()
                    messageList.clear()
                    messageList.addAll(historyMessages)
                    messageHistoryAdapter.notifyDataSetChanged()
                    showResult(historyMessages.isEmpty())

                    if(realOfflineSize < REQUEST_LIMIT) {
                        //no more data
                        messageHistoryAdapter.loadMoreEnd(false)

                    } else {
                        messageHistoryAdapter.setEnableLoadMore(true)

                    }

                }
            })
        }
    }

    private fun showResult(isEmptyMessage: Boolean) {
        getMessageListView()?.visibility = if (isEmptyMessage) View.GONE else View.VISIBLE
        getNoMessagesView()?.visibility = if (isEmptyMessage) View.VISIBLE else View.GONE
    }

    protected fun handlePlayVoice(voiceChatMessage: VoiceChatMessage, position: Int) {
        if(voiceChatMessage.playing) {
            stopVoice(voiceChatMessage, position)
            return
        }

        playVoice(voiceChatMessage, position)
    }

    protected fun stopVoice(voiceChatMessage: VoiceChatMessage, position: Int) {
        AudioRecord.stopPlaying()
        voiceChatMessage.playing = false
        messageHistoryAdapter.notifyItemChanged(position)
    }

    protected fun playVoice(voiceChatMessage: VoiceChatMessage, position: Int) {
        if (VoipHelper.isHandlingVoipCall()) {
            AtworkToast.showResToast(R.string.alert_is_handling_voip_meeting_click_voip)
            return
        }

        if (MediaCenterNetManager.isDownloading(voiceChatMessage.getMediaId())) {
            return
        }

        voiceChatMessage.playing = true
        messageHistoryAdapter.notifyItemChanged(position)

        AudioRecord.playAudio(AtworkApplicationLike.baseContext, voiceChatMessage, object : VoicePlayListener {
            override fun start() {
            }

            override fun stop(voiceMedia: VoiceMedia) {
                voiceChatMessage.playing = false
                messageHistoryAdapter.notifyItemChanged(position)
            }

        })
    }

    protected fun handleShowVideo(msg: ChatPostMessage) {
        if (VoipHelper.isHandlingVoipCall()) {
            AtworkToast.showResToast(R.string.alert_is_handling_voip_meeting_click_voip)
            return
        }

        showMediaSwitchFragment(msg)
    }

    protected fun showArticleItem(msg: ArticleChatMessage) {
        messageHistoryViewAction?.apply {
            ArticleItemHelper.startWebActivity(activity, app, msg, msg.articles[0])

        }
    }

    protected fun handleShowFile(msg: FileTransferChatMessage) {
        if (msg.isGifType || msg.isStaticImgType) {
            showMediaSwitchFragment(msg)

        } else {
            showFileStatusFragment(msg)
        }
    }


    protected fun showFileStatusFragment(fileTransferChatMessage: FileTransferChatMessage) {
        val fileStatusFragment = FileStatusFragment()
        val chatUser = ChatMessageHelper.getChatUser(fileTransferChatMessage)
        fileStatusFragment.initBundle(chatUser.mUserId, fileTransferChatMessage, null)
        fileStatusFragment.show(childFragmentManager, "FILE_DIALOG")

    }

    protected fun showMediaSwitchFragment(message: ChatPostMessage) {
        refreshImageChatMessageList()
        val count = ImageSwitchInChatActivity.sImageChatMessageList.indexOf(message)
        val intent = Intent()
        intent.putExtra(ImageSwitchFragment.INDEX_SWITCH_IMAGE, count)
        intent.setClass(AtworkApplicationLike.baseContext, ImageSwitchInChatActivity::class.java)
        startActivity(intent, false)

    }

    /**
     * 获取消息中的所有图片消息
     */
    protected fun refreshImageChatMessageList() {
        ImageSwitchInChatActivity.sImageChatMessageList.clear()

        for (message in messageList) {
            if (message.isBurn || message.isUndo) {
                continue
            }

            if (message is ImageChatMessage || message is MicroVideoChatMessage || message is FileTransferChatMessage) {
                if (message is FileTransferChatMessage) {
                    if (message.isGifType || message.isStaticImgType) {
                        ImageSwitchInChatActivity.sImageChatMessageList.add(message)
                    }
                    continue
                }
                ImageSwitchInChatActivity.sImageChatMessageList.add(message)
            }

        }
        ImageSwitchInChatActivity.sImageChatMessageList.sort()
    }

    protected fun getRequestSkip(page: Int): Int = page * 10

    override fun onLoadMoreRequested() {

        LogUtil.e("MessageHistoryFragment  onLoadMoreRequested~~")


        messageHistoryViewAction?.apply {
            val queryMessageHistoryRequest = QueryMessageHistoryRequest(getRequestSkip(page + 1), REQUEST_LIMIT, app.mOrgId, app.mAppId, messageType, tagId, keyword)
            MessageAsyncNetService.queryMessageHistory(AtworkApplicationLike.baseContext, queryMessageHistoryRequest, object: MessageAsyncNetService.GetHistoryMessageListener {
                override fun networkFail(errorCode: Int, errorMsg: String) {
                    messageHistoryAdapter.loadMoreFail()

                    ErrorHandleUtil.handleError(errorCode, errorMsg)
                }

                override fun getHistoryMessageSuccess(historyMessages: MutableList<ChatPostMessage>, realOfflineSize: Int) {
                    page += 1

                    if(realOfflineSize < REQUEST_LIMIT) {
                        //no more data
                        messageHistoryAdapter.loadMoreEnd(false)

                    } else {

                        messageHistoryAdapter.loadMoreComplete()

                    }

                    messageList.addAll(historyMessages)
                    messageHistoryAdapter.notifyDataSetChanged()

                }
            })
        }


    }

    override fun onBackPressed(): Boolean {
        finish()
        return false
    }
}