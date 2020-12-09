package com.foreveross.atwork.modules.group.service

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.text.TextUtils
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.foreverht.workplus.ui.component.dialogFragment.AtworkAlertDialog
import com.foreveross.atwork.R
import com.foreveross.atwork.api.sdk.discussion.DiscussionAsyncNetService
import com.foreveross.atwork.api.sdk.net.model.UploadFileParamsMaker
import com.foreveross.atwork.api.sdk.upload.MediaCenterNetManager
import com.foreveross.atwork.component.ProgressDialogHelper
import com.foreveross.atwork.infrastructure.BaseApplicationLike
import com.foreveross.atwork.infrastructure.manager.DomainSettingsManager
import com.foreveross.atwork.infrastructure.model.Employee
import com.foreveross.atwork.infrastructure.model.Session
import com.foreveross.atwork.infrastructure.model.SessionType
import com.foreveross.atwork.infrastructure.model.ShowListItem
import com.foreveross.atwork.infrastructure.model.app.App
import com.foreveross.atwork.infrastructure.model.app.LightApp
import com.foreveross.atwork.infrastructure.model.app.ServiceApp
import com.foreveross.atwork.infrastructure.model.discussion.Discussion
import com.foreveross.atwork.infrastructure.model.user.User
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.ParticipantType
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage
import com.foreveross.atwork.infrastructure.newmessage.post.chat.*
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo
import com.foreveross.atwork.infrastructure.support.AtworkConfig
import com.foreveross.atwork.infrastructure.utils.*
import com.foreveross.atwork.modules.discussion.manager.DiscussionManager
import com.foreveross.atwork.modules.chat.data.ChatSessionDataWrap
import com.foreveross.atwork.modules.chat.model.EntrySessionRequest
import com.foreveross.atwork.modules.chat.service.ChatService
import com.foreveross.atwork.modules.chat.util.MultipartMsgHelper
import com.foreveross.atwork.modules.chat.util.TextMsgHelper
import com.foreveross.atwork.modules.file.service.FileTransferService
import com.foreveross.atwork.modules.group.module.SelectToHandleAction
import com.foreveross.atwork.modules.group.module.TransferMessageControlAction
import com.foreveross.atwork.modules.group.module.TransferMessageMode
import com.foreveross.atwork.support.BaseActivity
import com.foreveross.atwork.utils.AtworkToast
import com.foreveross.atwork.utils.ChatMessageHelper
import com.foreveross.atwork.utils.ErrorHandleUtil
import java.util.*


object SelectToHandleActionService {

    const val TAG_HANDLE_SELECT_TO_ACTION = "TAG_HANDLE_SELECT_TO_ACTION"

    const val ACTION_SEND_SUCCESSFULLY = "ACTION_SEND_SUCCESSFULLY"
    const val ACTION_HANDLE_SELECT = "ACTION_HANDLE_SELECT"

    const val DATA_SELECT_CONTACTS = "DATA_SELECT_CONTACTS"
    const val DATA_SELECT_STATUS = "DATA_SELECT_STATUS"

    var contactList  = ArrayList<ShowListItem>()

    fun checkIsToThreshold(selectToHandleAction: SelectToHandleAction, contactWillSelect: ShowListItem? = null): Boolean {
        if(!isAllowed(selectToHandleAction)) {
            AtworkToast.showResToast(R.string.select_contact_max_tip, selectToHandleAction.max)
            return true
        }


        if (!isAllowedByDiscussionLimit(contactWillSelect)) {
            AtworkToast.showResToast(R.string.select_discussion_max_tip, AtworkConfig.CHAT_CONFIG.maxCountByTransferDiscussion)
            return true

        }


        return false
    }

    private fun isAllowedByDiscussionLimit(contactWillSelect: ShowListItem?): Boolean {
        if(null == contactWillSelect) {
            return true
        }

        if (AtworkConfig.CHAT_CONFIG.isTransferDiscussionHavingLimit()) {
            if (ContactHelper.isDiscussionType(contactWillSelect)) {
                if ((contactList.filter { ContactHelper.isDiscussionType(it) } + 1).size > AtworkConfig.CHAT_CONFIG.maxCountByTransferDiscussion) {
                    return false
                }
            }



        }
        return true
    }

    private fun isAllowed(selectToHandleAction: SelectToHandleAction): Boolean {
        if(SelectToHandleActionService.contactList.size + 1 <= selectToHandleAction.max) {
            return true
        }

        return false
    }


    fun clear() {
        contactList.clear()
    }

    fun handleSelect(contact: ShowListItem) {
        handleSelect(ListUtil.makeSingleList(contact) as ArrayList<ShowListItem>, contact.isSelect)
    }


    fun handleSelect(contactList: List<ShowListItem>, select: Boolean) {
        if(select) {
            select(contactList)
        } else {
            unselect(contactList)
        }
    }


    fun select(contactSelected: ShowListItem) {
        select(ListUtil.makeSingleList(contactSelected) as ArrayList<ShowListItem>)

    }


    fun select(contactSelectedList: List<ShowListItem>) {
        for(contact in contactSelectedList) {
            contact.select(true)
        }

        ContactHelper.addContacts(contactList, contactSelectedList)
        notifyHandleSelect(contactSelectedList, select = true)
    }

    fun unselect(contactUnselected: ShowListItem) {
        unselect(ListUtil.makeSingleList(contactUnselected) as ArrayList<ShowListItem>)

    }


    fun unselect(contactUnselectedList: List<ShowListItem>) {
        for(contact in contactUnselectedList) {
            contact.select(false)
        }


        ContactHelper.removeContacts(contactList, contactUnselectedList)

        notifyHandleSelect(contactUnselectedList, select = false)

    }

    private fun notifyHandleSelect(contactListSelected: List<ShowListItem>, select: Boolean) {
        val intent = Intent(ACTION_HANDLE_SELECT)
        intent.putParcelableArrayListExtra(DATA_SELECT_CONTACTS, ArrayList(contactListSelected))
        intent.putExtra(DATA_SELECT_STATUS, select)

        LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).sendBroadcast(intent)
    }


    private fun notifyHandleSelect(contactSelected: ShowListItem) {
        notifyHandleSelect(ListUtil.makeSingleList(contactSelected) as ArrayList<ShowListItem>, contactSelected.isSelect)
    }



    fun action(activity: Activity?, handleAction: SelectToHandleAction?, contact : ShowListItem) {
        action(activity, handleAction, ListUtil.makeSingleList(contact))
    }


    fun action(activity: Activity?, handleAction: SelectToHandleAction?, contactList : List<ShowListItem>) {

        if(null == activity) {
            return
        }


        if(handleAction is TransferMessageControlAction) {

            if (!ListUtil.isEmpty(handleAction.sendMessageList)) {

                if (1 < contactList.size && handleAction.isNeedCreateDiscussion) {
                    val dialog = AtworkAlertDialog(activity, AtworkAlertDialog.Type.CLASSIC)
                            .setTitleText(getSendCreateDiscussionAlertTitle(handleAction))
                            .setContent(getCreateDiscussionMsg(contactList))
                            .setClickBrightColorListener {
                                createDiscussionOrSendMessage(activity, handleAction.sendMessageList!!, contactList)
                            }

                    dialog.show()


                } else {
                    val dialog = AtworkAlertDialog(activity, AtworkAlertDialog.Type.CLASSIC)
                            .setTitleText(getSendAlertTitle(handleAction))
                            .setContent(getShowSelectContactLabel(contactList))
                            .setClickBrightColorListener {
                                sendMessage(activity, handleAction.sendMessageList!!, contactList)
                            }

                    dialog.show()
                }
            }
        }


    }

    fun getUserTypeContactList() :List<ShowListItem>{
        val contactUserTypeList = ArrayList<ShowListItem>()
        for(contact in contactList) {

            when(contact) {
                is User -> contactUserTypeList.add(contact)
                is Employee -> contactUserTypeList.add(contact)
                is Session -> {
                    if(contact.isUserType) {
                        contactUserTypeList.add(contact)
                    }
                }
            }


        }

        return contactUserTypeList
    }

    @SuppressLint("StringFormatInvalid")
    private fun getShowSelectContactLabel(selectContacts: List<ShowListItem>): String {
        if (ListUtil.isEmpty<ShowListItem>(selectContacts)) {
            return StringUtils.EMPTY
        }

        val showName = FileTransferService.getVariationName(selectContacts.get(0))
        return if (1 == selectContacts.size) {
            BaseApplicationLike.baseContext.getString(R.string.search_select_contact_prefix, showName)

        } else {
            (BaseApplicationLike.baseContext.getString(R.string.search_select_contact_prefix, showName)
                    + BaseApplicationLike.baseContext.getString(R.string.search_select_contact_suffix, selectContacts.size.toString()))
        }
    }

    /**
     * 得到最多前3个名字的拼接
     */
    private fun getStitchContacts(userSelectedList: List<ShowListItem>): String {
        val sb = StringBuilder()
        for (i in userSelectedList.indices) {
            sb.append(userSelectedList[i].title).append(",")
            if (2 == i) {
                break
            }
        }
        //去除最后的, 字符
        return sb.subSequence(0, sb.length - 2) as String
    }

    private fun getCreateDiscussionMsg(userSelectedList: List<ShowListItem>): String {
        val msg = StringBuilder(getStitchContacts(userSelectedList))
        if (3 < userSelectedList.size) {
            msg.append("(").append(userSelectedList.size).append(")")
        }
        return msg.toString()
    }

    private fun sendMessage(activity: Activity, messageList: List<ChatPostMessage>, contactList: List<ShowListItem>) {

        if (checkMultipartChatMessageNeedUpload(activity, messageList, contactList)) return

        if(checkShareMessageNeedUploadLocalCover(activity, messageList, contactList)) return

        doSendMessage(contactList, messageList)
    }

    private fun checkMultipartChatMessageNeedUpload(activity: Activity, messageList: List<ChatPostMessage>, contactList: List<ShowListItem>): Boolean {
        if (1 == messageList.size) {
            val message = messageList[0]
            if (message is MultipartChatMessage && StringUtils.isEmpty(message.mFileId)) {

                assembleMsgAndUpload(activity, message, contactList)

                return true

            }

        }
        return false
    }

    private fun assembleMsgAndUpload(activity: Activity, message: MultipartChatMessage, contactList: List<ShowListItem>) {

        val progressDialogHelper = ProgressDialogHelper(activity)
        progressDialogHelper.show(false)
        val messageList = message.mMsgList
        val sessionId = ChatMessageHelper.getChatUser(messageList[0]).mUserId

        val session = ChatSessionDataWrap.getInstance().getSession(sessionId, null)

        if(null == session) {
            Logger.e("IM", "assembleMsgAndUpload session null")
            return
        }

        MultipartMsgHelper.assembleMsg(BaseApplicationLike.baseContext, message, session) { multipartChatMessageBack ->

            val fileType = MediaCenterNetManager.COMMON_FILE
            val filePath = MultipartMsgHelper.getMultipartPath(multipartChatMessageBack)

            val uploadFileParamsMaker = UploadFileParamsMaker.newRequest()
                    .setType(fileType)
                    .setMsgId(message.deliveryId)
                    .setFilePath(filePath)
                    .setNeedCheckSum(false)
                    .setExpireLimit(-1)

            MediaCenterNetManager.uploadFile(BaseApplicationLike.baseContext, uploadFileParamsMaker)

            var mediaUploadListener = object : MediaCenterNetManager.MediaUploadListener {
                override fun getMsgId(): String {
                    return multipartChatMessageBack.deliveryId
                }

                override fun getType(): MediaCenterNetManager.UploadType {
                    return MediaCenterNetManager.UploadType.MULTIPART
                }

                override fun uploadSuccess(mediaInfo: String?) {
                    MediaCenterNetManager.removeMediaUploadListener(this)

                    multipartChatMessageBack.mFileId = mediaInfo
                    multipartChatMessageBack.fileStatus = FileStatus.SENDED

                    val newPath = MultipartMsgHelper.getMultipartPath(multipartChatMessageBack)
                    FileUtil.rename(filePath, newPath)

                    doSendMessage(contactList, ListUtil.makeSingleList(multipartChatMessageBack))

                    progressDialogHelper.dismiss()
                }

                override fun uploadFailed(errorCode: Int, errorMsg: String?, doRefreshView: Boolean) {
                    MediaCenterNetManager.removeMediaUploadListener(this)

                    progressDialogHelper.dismiss()
                    AtworkToast.showResToast(R.string.network_error)


                }

                override fun uploadProgress(progress: Double) {
                }

            }

            MediaCenterNetManager.addMediaUploadListener(mediaUploadListener)


        }
    }


    private fun checkShareMessageNeedUploadLocalCover(activity: Activity, messageList: List<ChatPostMessage>, contactList: List<ShowListItem>): Boolean {
        if(1 == messageList.size) {
            val message = messageList[0]
            if(message is ShareChatMessage && message.mArticleItem.isCoverUrlFromLocal) {

                uploadLocalCoverAndSendMsg(activity, message, contactList)


                return true
            }
        }

        return false

    }

    private fun uploadLocalCoverAndSendMsg(activity: Activity, message: ShareChatMessage, contactList: List<ShowListItem>) {
        val progressDialogHelper = ProgressDialogHelper(activity)
        progressDialogHelper.show(false)


//        mediaCenterNetManager.uploadFile(BaseApplicationLike.baseContext, MediaCenterNetManager.COMMON_FILE, message.deliveryId, message.mArticleItem.coverUrlLocal, true) { errorCode, errorMsg -> ErrorHandleUtil.handleError(ErrorHandleUtil.Module.Media, errorCode, errorMsg) }

        val uploadFileParamsMaker = UploadFileParamsMaker.newRequest()
                .setType(MediaCenterNetManager.COMMON_FILE)
                .setMsgId(message.deliveryId)
                .setFilePath(message.mArticleItem.coverUrlLocal)
                .setNeedCheckSum(true)
                .setExpireLimit(-1)
        MediaCenterNetManager.uploadFile(BaseApplicationLike.baseContext, uploadFileParamsMaker)


        val mediaUploadListener = object : MediaCenterNetManager.MediaUploadListener {
            override fun getMsgId(): String {
                return message.deliveryId
            }

            override fun getType(): MediaCenterNetManager.UploadType {
                return MediaCenterNetManager.UploadType.COMMON_FILE
            }

            override fun uploadSuccess(mediaInfo: String?) {
                MediaCenterNetManager.removeMediaUploadListener(this)

                message.mArticleItem.mCoverUrl = StringUtils.EMPTY
                message.mArticleItem.coverMediaId = mediaInfo

                doSendMessage(contactList, ListUtil.makeSingleList(message))


                progressDialogHelper.dismiss()
            }

            override fun uploadFailed(errorCode: Int, errorMsg: String?, doRefreshView: Boolean) {
                MediaCenterNetManager.removeMediaUploadListener(this)

                progressDialogHelper.dismiss()
                AtworkToast.showResToast(R.string.network_error)


            }

            override fun uploadProgress(progress: Double) {
            }

        }

        MediaCenterNetManager.addMediaUploadListener(mediaUploadListener)
    }

    private fun doSendMessage(contactList: List<ShowListItem>, messageList: List<ChatPostMessage>) {
        for (contact in contactList) {
            //TODO: link question
            val mediaIds = mutableListOf<String>()

            for (message in messageList) {
                val messageClone = CloneUtil.cloneTo(message)
                val context = BaseApplicationLike.baseContext
                val meUserId = LoginUserInfo.getInstance().getLoginUserId(context)
                val meUserName = LoginUserInfo.getInstance().getLoginUserName(context)
                val meUserAvatar = LoginUserInfo.getInstance().getLoginUserAvatar(context)

                regenerateMsg(mediaIds, messageClone, object: OnMsgRegenerateListener{
                    override fun onMsgRegeneratedFinish() {
                        val toType = getToType(contact) ?: return
                        messageClone.reGenerate(context, meUserId, contact.id, contact.domainId, ParticipantType.User, toType, messageClone.mBodyType, getOrgCode(contact), contact, meUserName, meUserAvatar)
                        handleSendBackwardAction(contact, messageClone)
//                        if (!ListUtil.isEmpty<String>(mediaIds)) {
//                            MediaCenterNetManager.linkMedias(BaseApplicationLike.baseContext, mediaIds) { }
//                        }
                    }
                })
            }

        }


        BaseActivity.triggerFinishChain(TAG_HANDLE_SELECT_TO_ACTION)
        notifySendSuccessfully()
        AtworkToast.showResToast(R.string.send_success)
    }

    private fun notifySendSuccessfully() {
        LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).sendBroadcast(Intent(ACTION_SEND_SUCCESSFULLY))
    }


    private fun createDiscussionOrSendMessage(activity: Activity, messageList: List<ChatPostMessage>, contactList: List<ShowListItem>) {
        DiscussionManager.getInstance().createDiscussion(BaseApplicationLike.baseContext, contactList, null, null, null, null, true, object : DiscussionAsyncNetService.OnCreateDiscussionListener {
            override fun networkFail(errorCode: Int, errorMsg: String) {
                ErrorHandleUtil.handleError(ErrorHandleUtil.Module.Group, errorCode, errorMsg)

            }

            override fun onDiscussionSuccess(discussion: Discussion?) {
                if (null != discussion) {
                    sendMessage(activity, messageList, ListUtil.makeSingleList(discussion))
                }
            }


        })
    }


    private fun handleSendBackwardAction(contact: ShowListItem, message: ChatPostMessage) {
        var sessionSend: Session? = null
        sessionSend = if (contact is Session) {
            contact

        } else {
            if (ChatSessionDataWrap.getInstance().mSessionMap.containsKey(contact.id)) {
                ChatSessionDataWrap.getInstance().mSessionMap[contact.id]
            } else {

                val sessionType = getSessionType(contact)
                val entrySessionRequest = EntrySessionRequest.newRequest(sessionType, contact)
                ChatSessionDataWrap.getInstance().entrySession(entrySessionRequest)
                ChatSessionDataWrap.getInstance().mSessionMap[contact.id]
            }
        }

        if (null != sessionSend) {
            ChatService.sendMessageOnBackground(sessionSend, message)
        }
    }


    private fun regenerateMsg(mediaIds: MutableList<String>, chatPostMessage: ChatPostMessage, listener: OnMsgRegenerateListener) {
        if(ListUtil.isEmpty(mediaIds)) {
            listener.onMsgRegeneratedFinish()
            return
        }

        when(chatPostMessage) {
            is TextChatMessage ->{
                TextMsgHelper.resetText(chatPostMessage)
                listener.onMsgRegeneratedFinish()
            }
            is FileTransferChatMessage ->{
                mediaIds.add(chatPostMessage.mediaId)
                val overtime = if (DomainSettingsManager.getInstance().handleChatFileExpiredFeature()) TimeUtil.getTimeInMillisDaysAfter(DomainSettingsManager.getInstance().chatFileExpiredDay) else -1
                chatPostMessage.expiredTime = overtime
                MediaCenterNetManager.linkMedia(BaseApplicationLike.baseContext, chatPostMessage.mediaId) {
                    if (!TextUtils.isEmpty(it)) {
                        chatPostMessage.mediaId = it
                    }
                    listener.onMsgRegeneratedFinish()
                }
            }
            is MicroVideoChatMessage -> MediaCenterNetManager.linkMedia(BaseApplicationLike.baseContext, chatPostMessage.mediaId) {
                mediaIds.add(chatPostMessage.mediaId)
                if (!TextUtils.isEmpty(it)) {
                    chatPostMessage.mediaId = it
                }
                listener.onMsgRegeneratedFinish()
            }
            is VoiceChatMessage -> MediaCenterNetManager.linkMedia(BaseApplicationLike.baseContext, chatPostMessage.mediaId) {
                mediaIds.add(chatPostMessage.mediaId)
                if (!TextUtils.isEmpty(it)) {
                    chatPostMessage.mediaId = it
                }
                listener.onMsgRegeneratedFinish()
            }
            is MultipartChatMessage -> MediaCenterNetManager.linkMedia(BaseApplicationLike.baseContext, chatPostMessage.mFileId, -1) {
                mediaIds.add(chatPostMessage.mFileId)
                if (!TextUtils.isEmpty(it)) {
                    chatPostMessage.mFileId = it
                }
                listener.onMsgRegeneratedFinish()
            }
            is ImageChatMessage -> {
                mediaIds.add(chatPostMessage.mediaId)
                MediaCenterNetManager.linkMedia(BaseApplicationLike.baseContext, chatPostMessage.mediaId) {mediaId ->
                    if (!TextUtils.isEmpty(mediaId)) {
                        chatPostMessage.mediaId = mediaId
                    }

                    if(StringUtils.isEmpty(chatPostMessage.fullMediaId)) {
                        listener.onMsgRegeneratedFinish()
                        return@linkMedia;
                    }


                    chatPostMessage.fullMediaId!!.apply { MediaCenterNetManager.linkMedia(BaseApplicationLike.baseContext, this) {fullMediaId ->
                        if (!TextUtils.isEmpty(fullMediaId)) {
                            chatPostMessage.fullMediaId = fullMediaId
                        }

                        listener.onMsgRegeneratedFinish()
                    } }
                }
            }

            else -> {
                listener.onMsgRegeneratedFinish()
            }
        }
    }


    private fun getSessionType(contact: ShowListItem): SessionType {
        return when(contact) {
            is User, is Employee -> SessionType.User
            is LightApp -> SessionType.LightApp
            is ServiceApp -> SessionType.Service
            is Discussion -> SessionType.Discussion
            else -> SessionType.User
        }
    }

    private fun getOrgCode(contact: ShowListItem): String? {
        return when(contact) {
            is App -> contact.mOrgId
            is Discussion -> contact.orgCodeCompatible
            is Session -> contact.orgId
            else -> null
        }
    }

    private fun getToType(contact: ShowListItem): ParticipantType? {
        return when(contact) {
            is User -> ParticipantType.User
            is Employee -> ParticipantType.User
            is App -> ParticipantType.App
            is Discussion -> ParticipantType.Discussion
            is Session -> {
                when(contact.type) {
                    SessionType.User ->  ParticipantType.User
                    SessionType.Discussion -> ParticipantType.Discussion
                    SessionType.Service -> ParticipantType.App
                    SessionType.LightApp -> ParticipantType.App
                    SessionType.NativeApp -> ParticipantType.App
                    SessionType.Local -> ParticipantType.App
                    SessionType.SystemApp -> ParticipantType.App
                    SessionType.Notice -> ParticipantType.System
                    SessionType.Custom -> ParticipantType.System
                    null -> null
                }
            }
            else -> null

        }
    }


    private fun getSendAlertTitle(transferMessageControlAction: TransferMessageControlAction): String {
        return if(TransferMessageMode.FORWARD == transferMessageControlAction.sendMode)
            BaseApplicationLike.baseContext.getString(R.string.confirm_resend_to_session)
        else
            BaseApplicationLike.baseContext.getString(R.string.confirm_send_to_session)

    }

    private fun getSendCreateDiscussionAlertTitle(transferMessageControlAction: TransferMessageControlAction): String {
        return if (TransferMessageMode.FORWARD == transferMessageControlAction.sendMode) {
            BaseApplicationLike.baseContext.getString(R.string.confirm_create_discussion_and_resend)

        } else {
            BaseApplicationLike.baseContext.getString(R.string.confirm_create_discussion_and_send)

        }
    }

    interface OnMsgRegenerateListener {
        fun onMsgRegeneratedFinish()
    }
}
