package com.foreveross.atwork.modules.chat.fragment

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.RecyclerView
import com.foreverht.threadGear.AsyncTaskThreadPool
import com.foreveross.atwork.AtworkApplicationLike
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.model.Session
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo
import com.foreveross.atwork.infrastructure.utils.TimeUtil
import com.foreveross.atwork.modules.chat.activity.ChatDetailActivity
import com.foreveross.atwork.modules.chat.activity.CollectType
import com.foreveross.atwork.modules.chat.activity.SessionsCollectActivity.Companion.DATA_COLLECT_TYPE
import com.foreveross.atwork.modules.chat.adapter.SessionsCollectAdapter
import com.foreveross.atwork.modules.chat.data.ChatSessionDataWrap
import com.foreveross.atwork.modules.chat.service.ChatService
import com.foreveross.atwork.modules.chat.util.SessionRefreshHelper
import com.foreveross.atwork.modules.chat.util.popChatItemListDialog
import com.foreveross.atwork.support.BackHandledFragment

class SessionsCollectFragment : BackHandledFragment() {

    private lateinit var ivBack: ImageView
    private lateinit var tvTitle: TextView
    private lateinit var rvSessionList: RecyclerView

    private lateinit var collectAdapter: SessionsCollectAdapter
    private val sessionList: ArrayList<Session> = arrayListOf()

    private var collectType: CollectType? = null


    private var messageRefreshBroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (SessionRefreshHelper.MESSAGE_REFRESH == intent.action) {
                refreshShieldDiscussionSessions()
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_collect_sessions, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initData()

        initViews()
        registerListener()
        registerBroadcast()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

    override fun onStart() {
        super.onStart()

        refreshShieldDiscussionSessions()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        unregisterBroadcast()
    }

    override fun findViews(view: View) {
        ivBack = view.findViewById(R.id.title_bar_common_back)
        tvTitle = view.findViewById(R.id.title_bar_common_title)
        rvSessionList = view.findViewById(R.id.rv_session_list)
    }

    override fun onBackPressed(): Boolean {
        finish()

        if(CollectType.APP_ANNOUNCE == collectType) {
            val session = ChatSessionDataWrap.getInstance().getSession(Session.COMPONENT_ANNOUNCE_APP, null)
            if(null != session) {
                ChatService.clearSessionsFoldReceipts(AtworkApplicationLike.baseContext, session)
                PersonalShareInfo.getInstance().setLastTimeEnterAnnounceApp(AtworkApplicationLike.baseContext, TimeUtil.getCurrentTimeInMillis());
            }

        }


        return false
    }


    private fun registerBroadcast() {
        LocalBroadcastManager.getInstance(AtworkApplicationLike.baseContext).registerReceiver(messageRefreshBroadcastReceiver, IntentFilter(SessionRefreshHelper.MESSAGE_REFRESH))
    }

    private fun unregisterBroadcast() {
        LocalBroadcastManager.getInstance(AtworkApplicationLike.baseContext).unregisterReceiver(messageRefreshBroadcastReceiver)

    }

    private fun registerListener() {
        ivBack.setOnClickListener { onBackPressed() }


        collectAdapter.setOnItemClickListener { adapter, view, position ->

            val session = sessionList[position]
            doRouteChatDetail(session)
        }


        collectAdapter.setOnItemLongClickListener { adapter, view, position ->
            val session = sessionList[position]
            popChatItemListDialog(this, session) {
                SessionRefreshHelper.notifyRefreshSession()
            }

            true
        }
    }

    private fun doRouteChatDetail(session: Session) {
        val activityRef = activity ?: return

        val intent = with(Intent()) {
            setClass(activityRef, ChatDetailActivity::class.java)
            putExtra(ChatDetailActivity.IDENTIFIER, session.identifier)
            putExtra(ChatDetailFragment.RETURN_BACK, true)
            putExtra(ChatDetailActivity.SESSION_LEGAL_CHECK, false)
        }

        startActivity(intent)
    }


    private fun initViews() {
        when(collectType) {
            CollectType.DISCUSSION_HELPER -> tvTitle.setText(R.string.discussion_helper)
            CollectType.APP_ANNOUNCE -> tvTitle.setText(R.string.announce_app)
        }


        collectAdapter = SessionsCollectAdapter(sessionList)
        rvSessionList.adapter = collectAdapter
    }


    private fun initData() {

        collectType = arguments?.getSerializable(DATA_COLLECT_TYPE) as CollectType?
    }


    @SuppressLint("StaticFieldLeak")
    private fun refreshShieldDiscussionSessions() {

        object : AsyncTask<Void, Void, List<Session>>() {
            override fun doInBackground(vararg params: Void?): List<Session> {
                return when(collectType) {
                    CollectType.DISCUSSION_HELPER -> ChatSessionDataWrap.getInstance().shieldDiscussionSessions


                    CollectType.APP_ANNOUNCE -> ChatSessionDataWrap.getInstance().announceAppSessions

                    else -> emptyList()
                }

            }

            override fun onPostExecute(result: List<Session>) {
                sessionList.clear()
                sessionList.addAll(result)

                collectAdapter.notifyDataSetChanged()
            }

        }.executeOnExecutor(AsyncTaskThreadPool.getInstance())
    }
}