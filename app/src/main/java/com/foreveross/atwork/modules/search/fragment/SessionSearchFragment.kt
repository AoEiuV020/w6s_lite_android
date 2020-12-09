package com.foreveross.atwork.modules.search.fragment

import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.BaseApplicationLike
import com.foreveross.atwork.infrastructure.model.Session
import com.foreveross.atwork.infrastructure.utils.ListUtil
import com.foreveross.atwork.modules.chat.dao.ChatDaoService
import com.foreveross.atwork.modules.contact.util.ContactInfoViewUtil
import com.foreveross.atwork.modules.search.activity.INTENT_KEY_SEARCH_MESSAGE_ITEMS
import com.foreveross.atwork.modules.search.activity.INTENT_KEY_SEARCH_MESSAGE_KEY
import com.foreveross.atwork.modules.search.adapter.SessionSearchAdapter
import com.foreveross.atwork.modules.search.model.SearchAction
import com.foreveross.atwork.modules.search.model.SearchMessageItem
import com.foreveross.atwork.modules.search.util.SearchHelper
import com.foreveross.atwork.support.BackHandledFragment
import kotlinx.android.synthetic.main.fragment_session_search.*
import kotlinx.android.synthetic.main.title_bar_new_search.*
import java.util.*

/**
 *  create by reyzhang22 at 2020-02-05
 */
class SessionSearchFragment : BackHandledFragment() {

    private var etSearch:       EditText? = null
    private var rvSessionList:  RecyclerView? = null
    private var noDataView:     View? = null

    private var globalSearchKey:    String = ""
    private var globalSearchValue:  String = ""

    private var searchRunnable:     SearchRunnable? = null

    private var resultList: MutableList<SearchMessageItem>? = null
    private var adapter: SessionSearchAdapter? = null

    private lateinit var session: Session

    private var handler = object: Handler(){}

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_session_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        findViews(view)
        initData()
        registerListener()
    }

    override fun findViews(view: View?) {
        etSearch = view?.findViewById(R.id.et_search_content)
        rvSessionList = view?.findViewById(R.id.rv_session_list)
        noDataView = view?.findViewById(R.id.ll_no_result)
    }

    private fun initData() {
        globalSearchValue = arguments?.get(INTENT_KEY_SEARCH_MESSAGE_KEY) as String
        resultList = arguments?.get(INTENT_KEY_SEARCH_MESSAGE_ITEMS) as MutableList<SearchMessageItem>?
                ?: mutableListOf()
        etSearch?.setText(globalSearchValue)
        etSearch?.setSelection(globalSearchValue.length)
        if (resultList!!.isEmpty()) {
            return
        }
        resultList?.sortByDescending { it.msgTime }
        session = resultList!![0].session
        ContactInfoViewUtil.dealWithSessionInitializedStatus(session_avatar, session_title, session, true)
        session_title.text = String.format(getString(R.string.messages_of_session), session.name)
        adapter = globalSearchValue.let { SessionSearchAdapter(resultList!!, it) }
        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        rvSessionList?.layoutManager = layoutManager
        rvSessionList?.adapter = adapter

        adapter?.setOnItemClickListener { adapter, _, position ->
            SearchHelper.handleSearchResultCommonClick(activity, globalSearchValue, adapter.getItem(position) as SearchMessageItem, SearchAction.DEFAULT, null)

        }
    }

    private fun registerListener() {
        iv_back.setOnClickListener {
            onBackPressed()
        }

        tv_cancel.setOnClickListener {
            etSearch?.setText("")
            resultList?.clear()
            globalSearchValue = ""
            adapter?.setData(resultList!!, globalSearchValue)
            rvSessionList?.visibility = View.VISIBLE
            noDataView?.visibility = View.GONE

        }

        etSearch?.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (!TextUtils.isEmpty(s.toString())) {
                    search(s.toString())
                    return
                }

            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun search(searchValue: String) {
        globalSearchKey = UUID.randomUUID().toString()
        searchRunnable = SearchRunnable(globalSearchKey, searchValue)
        handler.postDelayed(searchRunnable, 200)
    }


    inner class SearchRunnable: Runnable {

        var searchKey:      String = ""
        var searchValue:    String = ""

        constructor(searchKey: String, searchValue: String)  {
            this.searchKey = searchKey
            this.searchValue = searchValue
        }
        override fun run() {
            if(globalSearchKey == this.searchKey) {
                resultList?.clear()
                startSearch(searchKey, searchValue)
            }
        }
    }

    private fun startSearch(searchKey: String, searchValue: String) {
        ChatDaoService.getInstance().searchMessages(BaseApplicationLike.baseContext, searchKey, searchValue, session.identifier) { key, messages ->
            if (searchKey == key) {
                globalSearchValue = searchValue
                if (ListUtil.isEmpty(messages)) {
                    showNothingView()
                } else {
                    for (chatPostMessage in messages) {
                        val searchMessageItem = SearchMessageItem()
                        searchMessageItem.session = session
                        searchMessageItem.content = chatPostMessage.searchAbleString
                        searchMessageItem.msgId = chatPostMessage.deliveryId
                        searchMessageItem.msgTime = chatPostMessage.deliveryTime
                        searchMessageItem.message = chatPostMessage
                        resultList?.add(searchMessageItem)
                    }
                    resultList?.sortByDescending { it.msgTime }
                    showSearchedData()
                }
            }
        }
    }


    private fun showNothingView() {
        noDataView?.visibility = View.VISIBLE
        adapter?.notifyDataSetChanged()
        rvSessionList?.visibility = View.GONE
    }

    private fun showSearchedData() {
        adapter?.setData(resultList!!, globalSearchValue)
        adapter?.notifyDataSetChanged()
        rvSessionList?.visibility = View.VISIBLE
        noDataView?.visibility = View.GONE
    }

    override fun onBackPressed(): Boolean {
        mActivity.finish()
        return true
    }

}