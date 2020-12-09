package com.foreveross.atwork.modules.chat.adapter

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.foreveross.atwork.R
import com.foreveross.atwork.api.sdk.chat.TranslateLanguageResponse
import com.foreveross.atwork.infrastructure.BaseApplicationLike
import com.foreveross.atwork.infrastructure.model.Session
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage
import com.foreveross.atwork.infrastructure.newmessage.post.chat.TextChatMessage
import com.foreveross.atwork.infrastructure.utils.LogUtil
import com.foreveross.atwork.infrastructure.utils.NetworkStatusUtil
import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.foreveross.atwork.inter.ReSendListener
import com.foreveross.atwork.modules.chat.component.ChatDetailItemDataRefresh
import com.foreveross.atwork.modules.chat.component.SessionItemView
import com.foreveross.atwork.modules.chat.fragment.ChatDetailFragment
import com.foreveross.atwork.modules.chat.inter.*
import com.foreveross.atwork.modules.chat.util.MessageChatViewBuild
import com.foreveross.atwork.modules.chat.util.TextTranslateHelper
import com.foreveross.atwork.modules.chat.util.getMsgChatView
import com.foreveross.atwork.utils.AtworkToast
import com.foreveross.translate.OnResultListener
import com.foreveross.translate.youdao.YoudaoTranslate
import com.foreveross.translate.youdao.YoudaoTranslate.OnTranslateListener

class ChatDetailListAdapterV2(data: ArrayList<ChatPostMessage>,
                              var savedInstanceState: Bundle?,
                              var session: Session,
                              var translateLanguage: String?

) : BaseQuickAdapter<ChatPostMessage, BaseViewHolder>(-1, data) {


    var chatModeListener: ChatModeListener? = null
    var chatItemClickListener: ChatItemClickListener? = null
    var chatItemLongClickListener: ChatItemLongClickListener? = null
    var reSendListener: ReSendListener? = null

    override fun getItemId(position: Int): Long {
        return getItemOrNull(position - headerLayoutCount)?.deliveryId?.hashCode()?.toLong()?: position.toLong()
    }

    override fun getDefItemViewType(position: Int): Int {
        val message = getItem(position - headerLayoutCount)
        return MessageChatViewBuild.getMsgChatViewType(message)
    }



    override fun onCreateDefViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val chatView = getMsgChatView(context, savedInstanceState, session, viewType)
        initListener(chatView)
        return BaseViewHolder(chatView)
    }


    override fun convert(holder: BaseViewHolder, chatDetailItem: ChatPostMessage) {

        if (chatDetailItem.chatType == ChatPostMessage.ChatType.Text) {
            if (MessageChatViewBuild.isLeftView(chatDetailItem)) {
                val textChatMessage = chatDetailItem as TextChatMessage
                if (isIntelligentTranslation(textChatMessage)) {
                    //若网络无异常，进行智能翻译
                    if (NetworkStatusUtil.isNetworkAvailable(BaseApplicationLike.baseContext)) {
                        intelligentTranslation(textChatMessage, OnResultListener { result: String ->
                            if (!StringUtils.isEmpty(result)) {
                                LogUtil.e("translate result - > $result")
                                TextTranslateHelper.updateTranslateResultAndUpdateDb(textChatMessage, result, translateLanguage)
                            } else {
                                TextTranslateHelper.setTranslating(textChatMessage, false)
                            }
                        })
                    } else {
                        AtworkToast.showToast(context.resources.getString(R.string.Translate_common))
                    }
                }
            }
        }


        val convertView = holder.itemView
        //设置选中模式监听
        if (convertView is SelectModelListener) {
            val selectModelListener = convertView as SelectModelListener
            if (chatModeListener?.getChatModel() == ChatDetailFragment.ChatModel.COMMON) {
                selectModelListener.hiddenSelect()
            }
            if (chatModeListener?.getChatModel() == ChatDetailFragment.ChatModel.SELECT) {
                selectModelListener.showSelect()
            }
        }


        if (convertView is ChatDetailItemDataRefresh) {
            if (!StringUtils.isEmpty(chatDetailItem.deliveryId)) {
                val chatDetailItemDataRefresh = convertView
                chatDetailItemDataRefresh.refreshMessagesContext(data)
                chatDetailItemDataRefresh.refreshItemView(chatDetailItem)
            }
        }

        if (convertView is SkinModeUpdater) {
            if (!StringUtils.isEmpty(chatDetailItem.deliveryId)) {
                convertView.refreshSkinUI()
            }
        }


    }

    private fun isIntelligentTranslation(textChatMessage: TextChatMessage): Boolean {
        if (!StringUtils.isEmpty(translateLanguage)) {
            if (!textChatMessage.isTranslateStatusVisible) {
                if (StringUtils.isEmpty(textChatMessage.translatedResult)) {
                    if (!textChatMessage.translatedLanguage.equals(translateLanguage, ignoreCase = true)) {
                        return true
                    }
                }
            }
        }
        return false
    }


    fun intelligentTranslation(textChatMessage: TextChatMessage, listener: OnResultListener) {
        if (StringUtils.isEmpty(textChatMessage.translatedResult)) {
            TextTranslateHelper.setTranslating(textChatMessage, true)
            val youdaoTranslate = YoudaoTranslate()
            val url = youdaoTranslate.translate(textChatMessage.text, translateLanguage)
            youdaoTranslate.getTranlateLanguage(url, object : OnTranslateListener {
                override fun networkFail(errorCode: Int, errorMsg: String) {
                    LogUtil.e("translate result - > " + "网络失败！")
                    listener.onResult(null)
                }

                override fun onSuccess(translateLanguageResponse: TranslateLanguageResponse) {
                    if (!StringUtils.isEmpty(translateLanguageResponse.translation[0])) {
                        listener.onResult(translateLanguageResponse.translation[0])
                    } else {
                        listener.onResult(null)
                    }
                }
            })
        } else {
            TextTranslateHelper.showTranslateStatusAndUpdateDb(textChatMessage, true)
        }
    }


    private fun initListener(convertView: View) {
        //设置点击监听
        if (convertView is HasChatItemClickListener) {
            val hasChatItemClickListener = convertView as HasChatItemClickListener
            hasChatItemClickListener.setChatItemClickListener(chatItemClickListener)
        }

        //设置长按监听
        if (convertView is HasChatItemLongClickListener) {
            val hasChatItemLongClickListener = convertView as HasChatItemLongClickListener
            hasChatItemLongClickListener.setChatItemLongClickListener(chatItemLongClickListener)
        }
        if (convertView is HasResendListener) {
            val hasResendListener = convertView as HasResendListener
            hasResendListener.setReSendListener(reSendListener)
        }
    }

    fun setMessages(messages: ArrayList<ChatPostMessage>) {
        data = messages
    }


}


