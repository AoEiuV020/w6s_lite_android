package com.foreveross.atwork.modules.dropbox.fragment

import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.foreverht.workplus.module.file_share.activity.FileShareResultActivity
import com.foreveross.atwork.R
import com.foreveross.atwork.api.sdk.dropbox.DropboxAsyncNetService
import com.foreveross.atwork.api.sdk.dropbox.responseJson.ShareFileResponseJson
import com.foreveross.atwork.api.sdk.dropbox.responseJson.ShareItemsRespJson
import com.foreveross.atwork.component.recyclerview.loadmore.SimpleLoadMoreView
import com.foreveross.atwork.infrastructure.model.dropbox.ShareItem
import com.foreveross.atwork.infrastructure.model.dropbox.ShareItemRequester
import com.foreveross.atwork.infrastructure.shared.TempDropboxShareInfo
import com.foreveross.atwork.infrastructure.support.AtworkConfig
import com.foreveross.atwork.infrastructure.utils.ListUtil
import com.foreveross.atwork.manager.DropboxManager
import com.foreveross.atwork.modules.dropbox.activity.DropboxShareSearchActivity
import com.foreveross.atwork.modules.dropbox.adapter.DropboxShareItemAdapter
import com.foreveross.atwork.modules.dropbox.isFileOverdue
import com.foreveross.atwork.modules.dropbox.route.ShareTimeLineList
import com.foreveross.atwork.support.BackHandledFragment
import com.foreveross.atwork.utils.AtworkToast
import com.foreveross.atwork.utils.ErrorHandleUtil
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_my_dropbox_share.*

/**
 *  create by reyzhang22 at 2019-11-12
 */
class MyDropboxShareFragment : BackHandledFragment() {

    private lateinit var tvTitle:   TextView
    private lateinit var ivBack:    ImageView
    private lateinit var vNoResult: View

    private lateinit var rvDropboxShareItems: RecyclerView

    private var shareItemsList: ShareTimeLineList<ShareItem> = ShareTimeLineList()
    private var shareItemAdapter: DropboxShareItemAdapter? = null
    private val request = ShareItemRequester()
    private var hasTempData = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_my_dropbox_share, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListener()
        initData()
        Handler().postDelayed({
            makeRequest()
        }, 1000)
    }

    private fun makeRequest() {
        DropboxManager.getInstance().getDropboxShareItems(mActivity, request, object: DropboxAsyncNetService.OnFetchShareItemsListener {

            override fun getShareItems(result: ShareItemsRespJson.Result) {
                if (isDetached) {
                    return
                }
                if (request.mSkip == 0) {
                    if (result.mTotalCount > 0) {
                        TempDropboxShareInfo.getInstance().setTempShareData(mActivity, Gson().toJson(result))
                    }
                    shareItemsList.apply {
                        clear()
                        reset()
                    }
                    shareItemAdapter = null
                }
                shareItemsList.addAll(result.mShareItemList)
                setDropboxItemView(shareItemsList.size < result.mTotalCount - 1)
            }

            override fun networkFail(errorCode: Int, errorMsg: String?) {
                if (isDetached) {
                    return
                }
                if (!hasTempData && ListUtil.isEmpty(shareItemsList)) {
                    showNoDataView()
                }
                if (!ErrorHandleUtil.handleTokenError(errorCode, errorMsg)) {
                    AtworkToast.showResToast(R.string.network_not_good)
                }
            }


        })
    }

    private fun setDropboxItemView(loadMore: Boolean) {

        if (shareItemAdapter == null) {
            shareItemAdapter = DropboxShareItemAdapter(shareItemsList)
            shareItemAdapter?.setOnItemClickListener { adapter, view, position  ->
                val item = shareItemsList[position] as ShareItem
                checkItemClick(item)
            }
            rvDropboxShareItems.adapter = shareItemAdapter
        } else {
            shareItemAdapter?.notifyDataSetChanged()
        }
        if (loadMore) {
            request.mSkip += ShareItemRequester.REQUEST_LIMIT
            shareItemAdapter?.apply {
                setOnLoadMoreListener({
                    makeRequest()
                }, rvDropboxShareItems)
                setLoadMoreView(SimpleLoadMoreView())
            }
        } else {
            shareItemAdapter?.loadMoreEnd()
        }

    }

    override fun findViews(view: View) {
        tvTitle = view.findViewById(R.id.title_bar_common_title)
        tvTitle.text = getString(R.string.my_share)
        ivBack = view.findViewById(R.id.title_bar_common_back)
        vNoResult = view.findViewById(R.id.ll_no_result)
        rvDropboxShareItems = view.findViewById(R.id.rv_my_share_list)
    }

    private fun initListener() {
        tv_search.setOnClickListener {
            DropboxShareSearchActivity.startActivity(mActivity)
        }

        ivBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun initData() {
        makeTempData()
    }

    private fun makeTempData() {
        val data = TempDropboxShareInfo.getInstance().getTempShareData(mActivity)
        if (TextUtils.isEmpty(data)) {
            return;
        }

        try {
            val  result = Gson().fromJson(data, ShareItemsRespJson.Result::class.java)
            if (result != null && !ListUtil.isEmpty(result.mShareItemList)) {
                hasTempData = true
                shareItemsList.addAll(result.mShareItemList)
                setDropboxItemView(false)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun checkItemClick(item: ShareItem) {
        if (item.mIsTimeLine) {
            return
        }
        when (isFileOverdue(item.mExpireTime)) {
            1, 0 ->{
                val result = ShareFileResponseJson.Result().apply {
                    mName = item.mName
                    mPassword = item.mPassword
                    mIsPrivate = item.mIsPrivate
                    var adminUrl = AtworkConfig.ADMIN_URL
                    if (!adminUrl.endsWith("/")) {
                        adminUrl += "/"
                    }
                    mShareUrl = StringBuilder(adminUrl).append("s/").append(item.mId).toString()
                    FileShareResultActivity.startActivity(mActivity, this, false)
                }
            }
            -1 -> {
                AtworkToast.showResToast(R.string.file_share_overdue)
            }
        }

    }

    private fun showNoDataView() {
        vNoResult.visibility = View.VISIBLE
        rvDropboxShareItems.visibility = View.GONE
    }


    override fun onBackPressed(): Boolean {
        mActivity.finish()
        return false;
    }

}