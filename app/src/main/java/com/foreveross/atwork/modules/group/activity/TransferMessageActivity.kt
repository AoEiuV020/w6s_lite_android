package com.foreveross.atwork.modules.group.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment
import com.foreveross.atwork.AtworkApplicationLike
import com.foreveross.atwork.infrastructure.model.file.FileData
import com.foreveross.atwork.infrastructure.model.user.SelectedContactList
import com.foreveross.atwork.infrastructure.newmessage.ChatStatus
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.BodyType
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.ChatSendType
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage
import com.foreveross.atwork.infrastructure.newmessage.post.chat.*
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo
import com.foreveross.atwork.infrastructure.support.AtworkConfig
import com.foreveross.atwork.infrastructure.utils.*
import com.foreveross.atwork.modules.group.fragment.TransferMessageFragment
import com.foreveross.atwork.modules.group.module.TransferMessageControlAction
import com.foreveross.atwork.modules.group.service.SelectToHandleActionService
import com.foreveross.atwork.modules.main.service.HandleLoginService
import com.foreveross.atwork.support.SingleFragmentActivity
import java.util.*

class TransferMessageActivity : SingleFragmentActivity() {

    private var transferMessageControlAction: TransferMessageControlAction? = null

    private var shouldFinish = false
    override fun onCreate(savedInstanceState: Bundle?) {

        val transferMessageControlActionFromUriShare = checkFromShareIntent()

        if (null != transferMessageControlActionFromUriShare) {

            if (LoginUserInfo.getInstance().isLogin(this)) {
                SelectedContactList.clear()
                intent.putExtra(DATA_TRANSFER_MESSAGE_CONTROL_ACTION, transferMessageControlActionFromUriShare)

            } else {

                HandleLoginService.getInstance().schemaRouteAction = transferMessageControlActionFromUriShare

                HandleLoginService.toLoginHandle(this, intent, false)
                shouldFinish = true

            }
        }


        super.onCreate(savedInstanceState)


        if(shouldFinish) {
            finish()
        }


        transferMessageControlAction = intent.getParcelableExtra(DATA_TRANSFER_MESSAGE_CONTROL_ACTION)
        if(null != transferMessageControlAction) {
            finishChainTag = SelectToHandleActionService.TAG_HANDLE_SELECT_TO_ACTION
        }
    }

    private fun checkFromShareIntent(): TransferMessageControlAction? {
        var result = checkFromShareUrlIntent()

        if(null == result) {
            result = checkFromShareFileIntent()
        }

        return result
    }


    private fun checkFromShareUrlIntent(): TransferMessageControlAction? {
        if(Intent.ACTION_SEND == intent.action) {
            val bundle: Bundle? = intent.extras
            val type = intent.type

            LogUtil.e("get share from bundle -> $bundle")
            //share from system
            if(null != bundle && "text/plain" == type) {
                val url: String? = bundle.getString("url")
                val title: String? = bundle.getString("title")
                var icon: String? = bundle.getString("file")
                val content: String? = bundle.getString(Intent.EXTRA_TEXT)

                icon?.apply {
                    if(!startsWith("http")) {
                        icon = "file://$icon"
                    }
                }

                if(StringUtils.isEmpty(url)) {
                    if(!StringUtils.isEmpty(content)) {
                        return transferTextMessage(content!!)
                    }

                    return null
                }


                return transferShareUrlMessage(url!!, title, icon)
            }


            val uri = intent.data ?: return null

            val url: String? = uri.getQueryParameter("url")
            val title: String? = uri.getQueryParameter("title")
            val icon: String? = uri.getQueryParameter("icon")

            if(StringUtils.isEmpty(url)) {
                return null
            }

            return transferShareUrlMessage(url!!, title, icon)


        }

        return null
    }

    private fun transferTextMessage(text: String) : TransferMessageControlAction {
        val textChatMessage = TextChatMessage()
        textChatMessage.text = text

        textChatMessage.mBodyType = BodyType.Text
        textChatMessage.chatSendType = ChatSendType.SENDER
        textChatMessage.chatStatus = ChatStatus.Sending

        val msgList = arrayListOf<ChatPostMessage>(textChatMessage)

        return TransferMessageControlAction().apply {
            sendMessageList = msgList
            max = SELECT_MAX

        }
    }

    private fun transferShareUrlMessage(url: String, title: String?, icon: String?): TransferMessageControlAction {
        val articleItem = ArticleItem()
        articleItem.url = url
        articleItem.title = title
        articleItem.mCoverUrl = icon

        val shareChatMessage = ShareChatMessage.getShareChatMessageFromArticleItem(articleItem)
        val msgList = arrayListOf<ChatPostMessage>(shareChatMessage)

        return TransferMessageControlAction().apply {
            sendMessageList = msgList
            max = SELECT_MAX

        }
    }

    private fun checkFromShareFileIntent(): TransferMessageControlAction? {
        if (Intent.ACTION_SEND == intent.action
                || Intent.ACTION_SEND_MULTIPLE == intent.action) {

            var uriList: ArrayList<Parcelable>? = intent.getParcelableArrayListExtra<Parcelable>(Intent.EXTRA_STREAM)

            if (ListUtil.isEmpty(uriList)) {
                val streamUri = intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM)
                if (null != streamUri) {
                    uriList = ArrayList()
                    uriList.add(streamUri)
                }

            }

            if (ListUtil.isEmpty(uriList)) {
                shouldFinish = true

                return null
            }

            val msgList = arrayListOf<ChatPostMessage>()
            for (streamUri in uriList!!) {
                if (streamUri is Uri) {

                    val realPath = RealPathUtil.getRealPath(AtworkApplicationLike.baseContext, streamUri, AtWorkDirUtils.getInstance().tmpShareSavePath)

                    val fileType = FileData.getFileType(realPath)
                    if(useImageChatMessage(fileType)) {
                        msgList.add(ImageChatMessage.getImageChatMessageFromPath(realPath))

                    } else {
                        msgList.add(FileTransferChatMessage.getFileTransferChatMessageFromPath(realPath))
                    }


                }
            }

            return TransferMessageControlAction().apply {
                sendMessageList = msgList
                max = SELECT_MAX

            }
        }

        return null
    }

    private fun useImageChatMessage(fileType: FileData.FileType?) =
            (true == intent.type?.contains("image")
                    && (FileData.FileType.File_Image == fileType || FileData.FileType.File_Gif == fileType))

    override fun createFragment(): Fragment {
        return TransferMessageFragment()
    }

    companion object {
        const val DATA_TRANSFER_MESSAGE_CONTROL_ACTION = "DATA_TRANSFER_MESSAGE_CONTROL_ACTION"

        const val SELECT_MAX = AtworkConfig.TRANSFER_MESSAGE_COUNT_MAX

        fun getIntent(context : Context, transferMessageControlAction: TransferMessageControlAction): Intent {
            SelectedContactList.clear()
            transferMessageControlAction.max = SELECT_MAX

            val intent = Intent()
            intent.setClass(context, TransferMessageActivity::class.java)
            intent.putExtra(DATA_TRANSFER_MESSAGE_CONTROL_ACTION, transferMessageControlAction)
            return intent
        }
    }

}