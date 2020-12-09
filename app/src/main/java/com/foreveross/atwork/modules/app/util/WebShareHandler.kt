package com.foreveross.atwork.modules.app.util

import com.foreverht.webview.GetWebSnapshotJs
import com.foreveross.atwork.AtworkApplicationLike
import com.foreveross.atwork.api.sdk.message.MessageAsyncNetService
import com.foreveross.atwork.api.sdk.users.UserAsyncNetService
import com.foreveross.atwork.component.ProgressDialogHelper
import com.foreveross.atwork.infrastructure.BaseApplicationLike
import com.foreveross.atwork.infrastructure.model.Session
import com.foreveross.atwork.infrastructure.model.user.User
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.BodyType
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.ParticipantType
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ArticleItem
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ShareChatMessage
import com.foreveross.atwork.infrastructure.utils.JsonUtil
import com.foreveross.atwork.infrastructure.utils.LogUtil
import com.foreveross.atwork.infrastructure.utils.RegSchemaHelper
import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.foreveross.atwork.infrastructure.webview.AtworkWebView
import com.foreveross.atwork.modules.app.model.WebJsGetSnapshot
import com.foreveross.atwork.modules.chat.data.ChatSessionDataWrap
import com.foreveross.atwork.modules.chat.service.ChatService
import com.foreveross.atwork.modules.group.activity.TransferMessageActivity
import com.foreveross.atwork.modules.group.module.TransferMessageControlAction
import com.foreveross.atwork.modules.group.module.TransferMessageMode
import com.foreveross.atwork.modules.web.adapter.WebSharePopupAdapter
import com.foreveross.atwork.modules.web.model.WebShareBuilder
import com.foreveross.atwork.utils.ErrorHandleUtil
import java.util.*


class WebShareHandler(

        builder: WebShareBuilder


) {

    private var mContext = builder.context
    private var mProgressDialog: ProgressDialogHelper = ProgressDialogHelper(builder.context)
    private var mItemData = builder.articleItem
    private var mNeedFetchInfoFromRemote = builder.isNeedFetchInfoFromRemote
    private var mFragment = builder.fragment
    private var mShareType = builder.shareType
    private val mCallbackContext = builder.callbackContext


    fun share(shareDirectlyType: String, shareSessionId: String?) {
        when(shareDirectlyType) {
            "w6s_contact" -> shareToSession()
            "w6s_current_session" -> shareToSession(shareSessionId)
        }
    }



    private fun shareToSession(shareSessionId: String? = null) {
        doShareToSession(mItemData, shareSessionId)

//        parseUrlForShare(object : WebSharePopupAdapter.ShareAdapterOnParseUrlForShareListener(mItemData, mNeedFetchInfoFromRemote, mProgressDialog) {
//            override fun onAction(articleItem: ArticleItem) {
//
//            }
//        })
    }


    private fun doShareToSession(articleItem: ArticleItem, shareSessionId: String? = null) {
        AtworkApplicationLike.getLoginUser(object : UserAsyncNetService.OnQueryUserListener {
            override fun networkFail(errorCode: Int, errorMsg: String) {
                ErrorHandleUtil.handleError(null, errorCode, errorMsg)
            }

            override fun onSuccess(user: User) {
                val shareChatMessage = ShareChatMessage.newSendShareMessage(AtworkApplicationLike.baseContext, articleItem, user.mUserId, user.mDomainId, user.showName, user.mAvatar, ParticipantType.User, BodyType.Share, mShareType)
                val singleList = ArrayList<ChatPostMessage>()
                singleList.add(shareChatMessage)

                if(StringUtils.isEmpty(shareSessionId)) {
                    val transferMessageControlAction = TransferMessageControlAction()
                    transferMessageControlAction.sendMessageList = singleList
                    transferMessageControlAction.sendMode = TransferMessageMode.SEND
                    val intent = TransferMessageActivity.getIntent(BaseApplicationLike.baseContext, transferMessageControlAction)


                    mContext.startActivity(intent)

                } else {

                    val session: Session? = ChatSessionDataWrap.getInstance().getSessionSafely(shareSessionId, null)
                    if(null == session) {
                        mCallbackContext.error()
                    } else {
                        shareChatMessage.to = session.identifier
                        shareChatMessage.mToDomain = session.domainId
                        shareChatMessage.mToType = ParticipantType.Discussion
                        shareChatMessage.mDisplayName = session.name
                        shareChatMessage.mDisplayAvatar = session.avatar

                        ChatService.sendMessageOnBackground(session, shareChatMessage)
                        mCallbackContext.success()
                    }


                }


            }


        })
    }

    private fun parseUrlForShare(listener: WebSharePopupAdapter.ShareAdapterOnParseUrlForShareListener) {
        if (!mNeedFetchInfoFromRemote) {

            parseUrlLocalCrawl(listener)
            return
        }

        MessageAsyncNetService.parseUrlForShare(BaseApplicationLike.baseContext, mItemData.url, listener)

    }

    private fun parseUrlLocalCrawl(listener: WebSharePopupAdapter.ShareAdapterOnParseUrlForShareListener) {

        if (mFragment is AtworkWebView) {

            if (mItemData.isShareAllMatch) {

                listener.onAction(mItemData)
                return
            }

            (mFragment as AtworkWebView).evaluateJavascript(GetWebSnapshotJs.JS) { value ->

                LogUtil.e("GetWebSnapshotJs.JS -> $value")

                if (!StringUtils.isNull(value)) {
                    val webJsGetSnapshot = JsonUtil.fromJson(value, WebJsGetSnapshot::class.java)

                    if (null != webJsGetSnapshot) {
                        if (StringUtils.isEmpty(mItemData.title)) {
                            mItemData.title = webJsGetSnapshot.title
                        }

                        if (StringUtils.isEmpty(mItemData.mCoverUrl) || !RegSchemaHelper.isUrlLink(mItemData.mCoverUrl)) {
                            mItemData.mCoverUrl = webJsGetSnapshot.coverUrl
                        }

                        if (StringUtils.isEmpty(mItemData.summary)) {
                            mItemData.summary = webJsGetSnapshot.description
                        }
                    }

                }

                listener.onAction(mItemData)

            }

            return
        }


        listener.onAction(mItemData)
        return
    }

}