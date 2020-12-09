package com.foreveross.atwork.modules.file.service

import android.widget.ImageView
import android.widget.TextView
import com.foreveross.atwork.AtworkApplicationLike
import com.foreveross.atwork.R
import com.foreveross.atwork.api.sdk.users.UserAsyncNetService
import com.foreveross.atwork.infrastructure.BaseApplicationLike
import com.foreveross.atwork.infrastructure.model.Session
import com.foreveross.atwork.infrastructure.model.ShowListItem
import com.foreveross.atwork.infrastructure.model.user.User
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage
import com.foreveross.atwork.infrastructure.utils.CloneUtil
import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.foreveross.atwork.utils.ErrorHandleUtil
import com.foreveross.atwork.utils.ImageCacheHelper

object FileTransferService {


    fun getVariationName(contact: ShowListItem): String {

        when(contact) {
            is Session -> {
                if(needVariation(contact)) {
                    return BaseApplicationLike.baseContext.getString(R.string.file_transfer)
                }
            }

            is User -> {
                if(contact.mFileTransfer) {
                    return BaseApplicationLike.baseContext.getString(R.string.file_transfer)

                }
            }
        }

        if (null == contact.participantTitle) {
            return StringUtils.EMPTY
        }

        return contact.participantTitle

    }

    fun checkVariation(avatarView: ImageView?, titleView: TextView?, session: Session): Boolean {

        if(needVariation(session)) {
            avatarView?.apply {
                ImageCacheHelper.setImageResource(this, R.mipmap.icon_file_transfer)
            }
            titleView?.setText(R.string.file_transfer)
            return true
        }

        return false
    }

    fun checkVariation(avatarView: ImageView?, titleView: TextView?, user: User): Boolean {
        if(user.mFileTransfer) {
            avatarView?.apply {
                ImageCacheHelper.setImageResource(this, R.mipmap.icon_file_transfer)
            }
            titleView?.setText(R.string.file_transfer)
            return true
        }

        return false
    }


    fun needVariation(id: String) =
            User.isYou(BaseApplicationLike.baseContext, id)


    fun needVariation(session: Session) =
            needVariation(session.identifier)


    fun needVariation(message: ChatPostMessage): Boolean =
            User.isYou(BaseApplicationLike.baseContext, message.from) && message.from == message.to



    fun isClickFileTransfer(contactItem: ShowListItem): Boolean {
        when(contactItem) {
            is Session -> return FileTransferService.needVariation(contactItem)
            is User -> return  contactItem.mFileTransfer
        }

        return false
    }

    fun getFileTransfer(poxyListener: UserAsyncNetService.OnQueryUserListener) {
        AtworkApplicationLike.getLoginUser(object : UserAsyncNetService.OnQueryUserListener {
            override fun networkFail(errorCode: Int, errorMsg: String?) {

                poxyListener.networkFail(errorCode, errorMsg)
            }

            override fun onSuccess(user: User) {
                val fileTransfer = CloneUtil.cloneTo<User>(user)
                fileTransfer.mFileTransfer = true

                poxyListener.onSuccess(fileTransfer)
            }

        })

    }


    //todo  rxjava refactor
    private val searchKeys = arrayOf("文件传输助手", "文件傳輸助手", "File Transfer", "wenjianchuanshuzhushou", "wjcszs")

    fun search(key: String, value: String, onSearchListener: OnSearchListener) {
        var match = false
        for(key in searchKeys) {
            if(key.contains(value, true)) {
                match = true
                break
            }
        }

        if(match) {

            getFileTransfer(object : UserAsyncNetService.OnQueryUserListener {
                override fun networkFail(errorCode: Int, errorMsg: String?) {
                    ErrorHandleUtil.handleBaseError(errorCode, errorMsg)
                }

                override fun onSuccess(user: User) {

                    onSearchListener.onResult(searchKey = key, fileTransfer = user )
                }

            })
        } else {
            onSearchListener.onResult(searchKey = key, fileTransfer = null )

        }
    }



}

interface OnSearchListener {
    fun onResult(searchKey: String , fileTransfer: ShowListItem?)
}