package com.foreveross.atwork.modules.chat.fragment

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.foreverht.db.service.daoService.FileDaoService
import com.foreverht.threadGear.AsyncTaskThreadPool
import com.foreveross.atwork.R
import com.foreveross.atwork.api.sdk.UrlConstantManager
import com.foreveross.atwork.api.sdk.net.MediaCenterHttpURLConnectionUtil
import com.foreveross.atwork.api.sdk.net.model.DownloadFileParamsMaker
import com.foreveross.atwork.component.ProgressDialogHelper
import com.foreveross.atwork.infrastructure.BaseApplicationLike
import com.foreveross.atwork.infrastructure.manager.DomainSettingsManager
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage
import com.foreveross.atwork.infrastructure.newmessage.post.chat.FileTransferChatMessage
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ImageChatMessage
import com.foreveross.atwork.infrastructure.newmessage.post.chat.MicroVideoChatMessage
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo
import com.foreveross.atwork.infrastructure.support.AtworkConfig
import com.foreveross.atwork.infrastructure.utils.*
import com.foreveross.atwork.infrastructure.utils.aliyun.wrapCdnUrl
import com.foreveross.atwork.infrastructure.utils.encryption.EncryptCacheDisk
import com.foreveross.atwork.modules.chat.activity.INTENT_SESSION_ID
import com.foreveross.atwork.modules.chat.adapter.SearchChatMediasAdapter
import com.foreveross.atwork.modules.chat.dao.ChatDaoService
import com.foreveross.atwork.modules.chat.data.ChatSessionDataWrap
import com.foreveross.atwork.modules.chat.data.SearchMediasTimeLineList
import com.foreveross.atwork.modules.chat.data.SearchMessageItemData
import com.foreveross.atwork.modules.group.activity.TransferMessageActivity
import com.foreveross.atwork.modules.group.module.TransferMessageControlAction
import com.foreveross.atwork.support.BackHandledFragment
import com.foreveross.atwork.utils.AtworkToast
import com.foreveross.atwork.utils.FileHelper
import com.foreveross.atwork.utils.GifChatHelper
import com.foreveross.atwork.utils.ImageCacheHelper.loadImageByMediaSync
import com.foreveross.atwork.utils.MediaCenterUtils.saveBitmapToFile
import com.foreveross.atwork.utils.MessagesRemoveHelper.notifyChatDetailUI
import kotlinx.android.synthetic.main.fragment_search_chat_media.*

/**
 *  create by reyzhang22 at 2020-02-11
 */
const val CONST_FORWARD_REQUEST = 0x1
class SearchChatMediaFragment : BackHandledFragment() {

    private lateinit var sessionId: String

    private val resultList = SearchMediasTimeLineList<SearchMessageItemData>()

    private lateinit var adapter: SearchChatMediasAdapter

    private var selectMode = false

    private lateinit var tvTitle: TextView
    private lateinit var ivBack: ImageView
    private lateinit var tvRightest: TextView

    private lateinit var progress: ProgressDialogHelper


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_search_chat_media, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        progress = ProgressDialogHelper(mActivity)
        findViews(view)
        initData()
        registerListener()
    }

    override fun findViews(view: View) {
        tvTitle = view.findViewById(R.id.title_bar_common_title)
        ivBack = view.findViewById(R.id.title_bar_common_back)
        tvRightest = view.findViewById(R.id.title_bar_common_right_text)

        tvTitle.text = getString(R.string.image_and_video)
        tvRightest.apply {
            text = getString(R.string.select)
            visibility = View.VISIBLE
            setOnClickListener {
                selectMode = !selectMode
                if (selectMode) {

                    text = getString(R.string.cancel)
                    view_function_group.visibility = View.VISIBLE
                } else {
                    text = getString(R.string.select)
                    view_function_group.visibility = View.GONE
                }
                currentSelectedMessage.clear()
                currentSelectedMessageId.clear()
                adapter.apply {
                    setSelectMode(selectMode)
                    clearData()
                }
            }
        }
        iv_favorite.visibility = View.GONE

        if (!DomainSettingsManager.getInstance().handleChatFileDownloadEnabled()) {
            iv_download.visibility = View.GONE
        }
        if (!DomainSettingsManager.getInstance().handleChatFileTransferEnabled()) {
            iv_forward.visibility = View.GONE
        }
        iv_delete.visibility = View.GONE

    }

    private fun initData() {
        sessionId = arguments?.get(INTENT_SESSION_ID) as String? ?: return
        search()
    }



    private fun registerListener() {
        ivBack.setOnClickListener {
            onBackPressed()
        }
        iv_forward.setOnClickListener {
            if(CommonUtil.isFastClick(1500)) {
                return@setOnClickListener
            }

            doForward()
        }

        iv_delete.setOnClickListener {
            if(CommonUtil.isFastClick(1500)) {
                return@setOnClickListener
            }

            doDelete()
        }

        iv_download.setOnClickListener {
            if(CommonUtil.isFastClick(1500)) {
                return@setOnClickListener
            }

            doDownload()
        }
    }

    var currentSelectedMessage = ArrayList<ChatPostMessage>()
    var currentSelectedMessageId = ArrayList<String>()

    private fun getSelectedListOnClick(): Boolean {
        val selectedList = adapter.getSelectedMap()
        if (selectedList.isEmpty()) {
            AtworkToast.showResToast(R.string.please_select_media_data)
            return false
        }
        for (mediaItem in selectedList.values) {
            currentSelectedMessage.add(mediaItem.message)
            currentSelectedMessageId.add(mediaItem.message.deliveryId)
        }
        return true
    }

    private fun doForward() {
        if (!getSelectedListOnClick()) {
            return
        }
        val transferMessageControlAction = TransferMessageControlAction()
        val forwardList = currentSelectedMessage
        transferMessageControlAction.sendMessageList = forwardList
        val intent = TransferMessageActivity.getIntent(BaseApplicationLike.baseContext, transferMessageControlAction)
        startActivityForResult(intent, CONST_FORWARD_REQUEST)
        resetStatus()
    }

    private fun doDownload() {
        if (!getSelectedListOnClick()) {
            return
        }
        progress.show(false)
        object: AsyncTask<Void, Void, String>() {
            override fun doInBackground(vararg params: Void?): String {
                var successCounter = 0
                var failCounter = 0
                for (message in currentSelectedMessage) {
                    if (message is ImageChatMessage || message is FileTransferChatMessage) {
                        if (handleSaveImg(message)) successCounter++ else failCounter++
                        continue
                    }
                    if (message is MicroVideoChatMessage) {
                        if (handleSaveVideo(message)) successCounter++ else failCounter++
                    }
                }
                val stringBuilder = StringBuffer()
                if (failCounter ==0) {
                    stringBuilder.append(getString(R.string.file_transfer_status_download_success))
                    return stringBuilder.toString()
                }
                stringBuilder.append(getString(R.string.media_download_tip, successCounter.toString(), failCounter.toString()))
                return stringBuilder.toString()
            }

            override fun onPostExecute(result: String?) {
                progress.dismiss()
                AtworkToast.showToast(result)
                resetStatus()
            }

        }.executeOnExecutor(AsyncTaskThreadPool.getInstance())

    }

    private fun doDelete() {
        if (!getSelectedListOnClick()) {
            return
        }
        ChatDaoService.getInstance().batchRemoveMessages(currentSelectedMessage, object : ChatDaoService.BatchAddOrRemoveListener {
            override fun addOrRemoveFail() {
                AtworkToast.showResToast(R.string.delete_fail)
                resetStatus()
            }

            override fun addOrRemoveSuccess() {
                AtworkToast.showResToast(R.string.delete_success)
                val deleteList = currentSelectedMessageId
                //刷新 session
                ChatSessionDataWrap.getInstance().updateSessionForRemoveMsgs(sessionId, deleteList, false)
                //通知聊天界面刷新
                notifyChatDetailUI(context, deleteList)
                resetStatus()
                resultList.reset()
                search()
            }
        })

    }


    private fun search() {
        val dataList = mutableListOf<SearchMessageItemData>()
        ChatDaoService.getInstance().searchMediaMessages(mActivity.applicationContext, sessionId) { _, messages ->
            for (message in messages) {
                val data = SearchMessageItemData()
                data.mIsTimeLine = false
                data.mMessage = message
                dataList.add(data)
            }
            resultList.addAll(dataList)
            adapter = SearchChatMediasAdapter(resultList)
            adapter.setSelectMode(selectMode)
            rv_media_list.adapter = adapter
        }
    }

    @SuppressLint("StaticFieldLeak")
    private fun handleSaveImg(chatPostMessage: ChatPostMessage): Boolean {
        var path: String = getPathSaved(chatPostMessage)
        var isGif: Boolean? = null

        if (chatPostMessage is ImageChatMessage) {
            val imageChatMessage = chatPostMessage
            isGif = imageChatMessage.isGif
        }
        if (null == isGif) {
            isGif = GifChatHelper.isGif(path)
        }

        if (!FileUtil.isExist(path)) {
            val bitmap = loadImageByMediaSync((chatPostMessage as ImageChatMessage).mediaId)
                    ?: return false
            saveBitmapToFile(path, bitmap)
        }

        path = EncryptCacheDisk.getInstance().getOriginalFilePathAndCheck(path, false)
        val savedPath = ImageShowHelper.saveImageToGalleryAndGetPath(activity, null, path, isGif, false)
        if (!StringUtils.isEmpty(savedPath)) {
            FileDaoService.getInstance().insertRecentFile(savedPath)
        }
        return true
    }

    private fun handleSaveVideo(chatPostMessage: ChatPostMessage): Boolean {
        val microVideoChatMessage = chatPostMessage as MicroVideoChatMessage
        val path = getPathSaved(chatPostMessage)
        if (!FileUtil.isExist(path)) {
            val paramsMaker = DownloadFileParamsMaker.newRequest().setMediaId(microVideoChatMessage.mediaId).setDownloadId(microVideoChatMessage.deliveryId)
                    .setDownloadPath(path).setDownloadType(MediaCenterHttpURLConnectionUtil.DOWNLOAD_TYPE.FILE)
            var downloadUrl = String.format(UrlConstantManager.getInstance().V2_getDownloadUrl(true), microVideoChatMessage.mediaId, LoginUserInfo.getInstance().getLoginUserAccessToken(context))
                downloadUrl = wrapCdnUrl(downloadUrl)
            paramsMaker.setDownloadUrl(downloadUrl).setEncrypt(AtworkConfig.OPEN_DISK_ENCRYPTION)
            val httpResult = MediaCenterHttpURLConnectionUtil.getInstance().downloadFile(paramsMaker)
            if (httpResult.isIOError || httpResult.isNetError || httpResult.isNetFail) {
                return false
            }
        }

        val savedPath = MicroVideoHelper.saveVideoToGalleryAndGetPath(activity, null, path)

        if (!StringUtils.isEmpty(savedPath)) {
            FileDaoService.getInstance().insertRecentFile(savedPath)
        }
        return true
    }

    private fun getPathSaved(chatPostMessage: ChatPostMessage): String {
        var imgMediaId = StringUtils.EMPTY

        if (chatPostMessage is ImageChatMessage) {

            if (chatPostMessage.isFullImgExist) {
                return chatPostMessage.fullImgPath
            }

            imgMediaId = chatPostMessage.mediaId


        } else if (chatPostMessage is FileTransferChatMessage) {
            if (FileUtil.isExist(chatPostMessage.filePath)) {
                return chatPostMessage.filePath
            }

            imgMediaId = chatPostMessage.mediaId

        } else if (chatPostMessage is MicroVideoChatMessage) {
            return FileHelper.getMicroExistVideoFilePath(BaseApplicationLike.baseContext, chatPostMessage)
        }

        var path = ImageShowHelper.getOriginalPath(activity, imgMediaId)

        if (!FileUtil.isExist(path)) {
            path = ImageShowHelper.getOriginalPath(activity, chatPostMessage.deliveryId)
        }

        return path

    }

    private fun resetStatus() {
        selectMode = false
        tvRightest.text = getString(R.string.select)
        view_function_group.visibility = View.GONE
        adapter.apply {
            setSelectMode(selectMode)
            clearData()
        }

    }


    override fun onBackPressed(): Boolean {
        mActivity.finish()
        return true
    }

}