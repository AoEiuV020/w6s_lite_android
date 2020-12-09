package com.foreveross.atwork.modules.dropbox.fragment

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.foreverht.workplus.module.file_share.activity.FileShareResultActivity
import com.foreveross.atwork.R
import com.foreveross.atwork.api.sdk.dropbox.DropboxAsyncNetService
import com.foreveross.atwork.api.sdk.dropbox.responseJson.ShareFileResponseJson
import com.foreveross.atwork.api.sdk.dropbox.responseJson.ShareItemsRespJson
import com.foreveross.atwork.component.recyclerview.loadmore.SimpleLoadMoreView
import com.foreveross.atwork.infrastructure.model.dropbox.ShareItem
import com.foreveross.atwork.infrastructure.model.dropbox.ShareItemRequester
import com.foreveross.atwork.infrastructure.support.AtworkConfig
import com.foreveross.atwork.infrastructure.utils.ListUtil
import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.foreveross.atwork.listener.TextWatcherAdapter
import com.foreveross.atwork.manager.DropboxManager
import com.foreveross.atwork.modules.dropbox.adapter.DropboxShareItemAdapter
import com.foreveross.atwork.modules.dropbox.isFileOverdue
import com.foreveross.atwork.modules.dropbox.route.ShareTimeLineList
import com.foreveross.atwork.support.BackHandledFragment
import com.foreveross.atwork.utils.AtworkToast
import com.foreveross.atwork.utils.AtworkUtil
import com.foreveross.atwork.utils.ErrorHandleUtil
import kotlinx.android.synthetic.main.title_bar_search.*
import java.util.*

/**
 *  create by reyzhang22 at 2019-11-12
 */
class DropboxShareSearchFragment : BackHandledFragment() {

    lateinit var etSearch:            AutoCompleteTextView
    lateinit var cancelView:          ImageView
    lateinit var rvDropboxShareItems: RecyclerView
    lateinit var vNoResult:            View


    private val shareItemsList: ShareTimeLineList<ShareItem> = ShareTimeLineList()
    private var shareItemAdapter: DropboxShareItemAdapter? = null
    private val request = ShareItemRequester()

    private var globalSearchRunnable: SearchRunnable? = null
    lateinit var searchKey: String

    private val handler = Handler()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_my_dropbox_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListener()
        toastInput()
    }

    private fun makeRequest() {
        DropboxManager.getInstance().getDropboxShareItems(mActivity, request, object: DropboxAsyncNetService.OnFetchShareItemsListener {

            override fun getShareItems(result: ShareItemsRespJson.Result) {
                if (isDetached) {
                    return
                }
                if (request.mSkip == 0) {
                    shareItemsList.clear()
                    showDataStatus(ListUtil.isEmpty(result.mShareItemList))
                }
                shareItemsList.addAll(result.mShareItemList)
                setDropboxItemView(shareItemsList.size - shareItemsList.timeSize < result.mTotalCount)
            }

            override fun networkFail(errorCode: Int, errorMsg: String?) {
                if (isDetached) {
                    return
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

    private fun showDataStatus(noData: Boolean) {
        vNoResult.visibility = if (noData) View.VISIBLE else View.GONE
        rvDropboxShareItems.visibility = if (noData) View.GONE else View.VISIBLE
    }

    override fun findViews(view: View) {
        rvDropboxShareItems = view.findViewById(R.id.rv_my_share_list)
        etSearch = view.findViewById(R.id.title_bar_chat_search_key)
        cancelView = view.findViewById(R.id.title_bar_chat_search_cancel)
        vNoResult = view.findViewById(R.id.ll_no_result)
    }

    private fun initListener() {

        title_bar_chat_search_back.setOnClickListener {
            onBackPressed()
        }

        cancelView.setOnClickListener { _ ->
            etSearch.setText("")
            vNoResult.visibility = View.GONE
            rvDropboxShareItems.visibility = View.VISIBLE
            shareItemsList.clear()
            shareItemAdapter?.notifyDataSetChanged()
        }

        rvDropboxShareItems.setOnTouchListener { v, event ->
            AtworkUtil.hideInput(activity, etSearch)
            false
        }
        etSearch.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus && !TextUtils.isEmpty(etSearch.text.toString())) {
                cancelView.visibility = View.VISIBLE
            } else {
                cancelView.visibility = View.GONE
            }
        }

        etSearch.addTextChangedListener(object: TextWatcherAdapter(){
            override fun afterTextChanged(s: Editable?) {
                if (!TextUtils.isEmpty(s.toString())) {
                    cancelView.visibility = View.VISIBLE
                } else {
                    cancelView.visibility = View.GONE
                }
                search(s.toString())
            }
        })
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

    private fun search(value: String) {
        searchKey = UUID.randomUUID().toString()
        if (StringUtils.isEmpty(value)) {
            return
        }
        globalSearchRunnable = SearchRunnable(searchKey, value)
        handler.postDelayed(globalSearchRunnable, 800)
    }

    inner class SearchRunnable(searchKey: String, searchValue: String) : Runnable {

        var  mSearchKey: String = searchKey
        var  mSearchValue: String = searchValue

        override fun run() {
            //searchKey一样时,才进行搜索
            if (mSearchKey == this@DropboxShareSearchFragment.searchKey) {
                shareItemsList.clear()
                request.mSkip = 0
                request.mKw = mSearchValue
                makeRequest()
            }

        }


    }

    private fun toastInput() {
        etSearch.setFocusable(true)
        //延迟处理 避免View没绘制好 软键盘无法弹出
        activity!!.window.decorView.postDelayed({
            if (isAdded) {
                val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                if (imm != null) {
                    etSearch.requestFocus()
                    imm.showSoftInput(etSearch, InputMethodManager.SHOW_FORCED)
                }
            }
        }, 100)
    }

    override fun onBackPressed(): Boolean {
        AtworkUtil.hideInput(activity, etSearch)
        mActivity.finish()
        return false;
    }

}