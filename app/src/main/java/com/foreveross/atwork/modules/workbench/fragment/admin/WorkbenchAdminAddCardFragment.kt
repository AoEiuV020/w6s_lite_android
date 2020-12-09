package com.foreveross.atwork.modules.workbench.fragment.admin

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.foreveross.atwork.AtworkApplicationLike
import com.foreveross.atwork.R
import com.foreveross.atwork.api.sdk.BaseCallBackNetWorkListener
import com.foreveross.atwork.api.sdk.workbench.model.request.WorkbenchCardHandleRequest
import com.foreveross.atwork.component.ProgressDialogHelper
import com.foreveross.atwork.infrastructure.BaseApplicationLike
import com.foreveross.atwork.infrastructure.model.WebViewControlAction
import com.foreveross.atwork.infrastructure.model.workbench.WorkbenchCardType
import com.foreveross.atwork.infrastructure.model.workbench.WorkbenchGridType
import com.foreveross.atwork.infrastructure.model.workbench.data.WorkbenchCardDisplayDefinitionData
import com.foreveross.atwork.infrastructure.model.workbench.data.WorkbenchData
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo
import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.foreveross.atwork.modules.app.route.routeUrl
import com.foreveross.atwork.modules.workbench.adapter.admin.WorkbenchAdminAddCardListAdapter
import com.foreveross.atwork.modules.workbench.manager.WorkbenchAdminManager
import com.foreveross.atwork.support.BackHandledPushOrPullFragment
import com.foreveross.atwork.utils.ErrorHandleUtil
import kotlinx.android.synthetic.main.fragment_workbench_admin_add_or_modify.*
import kotlinx.android.synthetic.main.fragment_workbench_admin_card_add.*
import kotlinx.android.synthetic.main.fragment_workbench_admin_card_add.rlRoot
import kotlinx.android.synthetic.main.fragment_workbench_admin_card_add.tvCancel
import kotlinx.android.synthetic.main.fragment_workbench_admin_card_add.tvSure

class WorkbenchAdminAddCardFragment: BackHandledPushOrPullFragment() {

    val cardDataList = ArrayList<WorkbenchCardType>()
    private lateinit var adapter: WorkbenchAdminAddCardListAdapter
    private lateinit var workbenchData: WorkbenchData

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_workbench_admin_card_add, container, false)
    }

    override fun getAnimationRoot(): View = llRootContent

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()

    }

    private fun initViews() {
        adapter = WorkbenchAdminAddCardListAdapter(cardDataList)
        rvCards.adapter = adapter

        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        rvCards.layoutManager = layoutManager

        initTvTipUI()

    }

    private fun initTvTipUI() {
        val tipFront = getStrings(R.string.admin_add_card_guide)
        val devUrl = "https://v4.workplus.io/html/assets/doc/widget-api.pdf"
        val spannableString = SpannableString(tipFront + devUrl)
        val devUrlStartIndex = spannableString.indexOf(devUrl)
        val devUrlEndIndex = spannableString.indexOf(devUrl) + devUrl.length


        spannableString.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                WebViewControlAction.newAction().setUrl(devUrl).let {
                    routeUrl(widget.context, it)
                }
            }

        }, devUrlStartIndex, devUrlEndIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        spannableString.setSpan(ForegroundColorSpan(ContextCompat.getColor(BaseApplicationLike.baseContext, R.color.common_blue_bg)), devUrlStartIndex, devUrlEndIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        tvTip.movementMethod = LinkMovementMethod.getInstance();
        tvTip.text = spannableString
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initData()
        registerListener()

        refreshSureBtnStatus()
    }


    override fun findViews(view: View) {

    }

    private fun initData() {

        arguments?.getParcelable<WorkbenchData>(WorkbenchData::class.java.toString())?.let {
            workbenchData = it
        }

        cardDataList.addAll(arrayOf(
                WorkbenchCardType.SEARCH,

                WorkbenchCardType.BANNER,

                WorkbenchCardType.APP_MESSAGES,

                WorkbenchCardType.APP_CONTAINER_0,

                WorkbenchCardType.APP_CONTAINER_1,

                WorkbenchCardType.COMMON_APP

        ))

        adapter.notifyDataSetChanged()
    }

    private fun registerListener() {
        tvCancel.setOnClickListener { onBackPressed() }

        rlRoot.setOnClickListener { onBackPressed() }

        llRootContent.setOnClickListener {  }

        tvSure.setOnClickListener {
            handleSureAction()
        }

        adapter.setOnItemClickListener { _, _, position ->
            if(adapter.selectCardPos == position) {
                adapter.selectCardPos = -1
            } else {
                adapter.selectCardPos = position
            }
            refreshSureBtnStatus()
            adapter.notifyDataSetChanged()
        }

    }

    private fun handleSureAction() {
        val progressDialogHelper = ProgressDialogHelper(activity)
        progressDialogHelper.show()

        val workbenchCardType = cardDataList[adapter.selectCardPos]
        WorkbenchAdminManager.addCard(
                context = AtworkApplicationLike.baseContext,
                orgCode = PersonalShareInfo.getInstance().getCurrentOrg(AtworkApplicationLike.baseContext),
                workbenchCardHandleRequest = buildWorkbenchHandleRequest(workbenchCardType),
                originalWorkbenchData = workbenchData,
                listener = object : BaseCallBackNetWorkListener {
                    override fun onSuccess() {
                        progressDialogHelper.dismiss()

                        onBackPressed()
                        toastOver(R.string.add_successfully)
                    }

                    override fun networkFail(errorCode: Int, errorMsg: String?) {
                        progressDialogHelper.dismiss()
                        ErrorHandleUtil.handleError(errorCode, errorMsg)

                    }
                }
        )
    }

    private fun buildWorkbenchHandleRequest(workbenchCardType: WorkbenchCardType): WorkbenchCardHandleRequest {
        val workbenchCardTypeStr = WorkbenchCardType.parse(workbenchCardType)
        val workbenchCardDisplayDefinitionData = WorkbenchCardDisplayDefinitionData()
        when(workbenchCardType) {
            WorkbenchCardType.APP_CONTAINER_0 -> {
                workbenchCardDisplayDefinitionData.entrySize = WorkbenchGridType.TYPE_1_4.toString()
            }

            WorkbenchCardType.APP_CONTAINER_1 -> {
                workbenchCardDisplayDefinitionData.listCount = 3
            }

            WorkbenchCardType.APP_MESSAGES -> {
                workbenchCardDisplayDefinitionData.listCount = 2
            }


            WorkbenchCardType.BANNER -> {
                workbenchCardDisplayDefinitionData.intervalSeconds = 3
            }
        }
        var cardName = getCardName(workbenchCardType)

        return WorkbenchCardHandleRequest(
                type = workbenchCardTypeStr,
                name = cardName,
                definition = workbenchCardDisplayDefinitionData

        )
    }

    private fun getCardName(workbenchCardType: WorkbenchCardType): String {
        val cardName = WorkbenchAdminAddCardListAdapter.getCardName(AtworkApplicationLike.baseContext, workbenchCardType)
        return StringUtils.getStringDistinctive(cardName, workbenchData.workbenchCardDetailDataList.map { it.name })
    }

    private fun inputInfoLegal(): Boolean =  (0 <= adapter.selectCardPos && cardDataList.size > adapter.selectCardPos )


    private fun refreshSureBtnStatus() {
        if (inputInfoLegal()) {
            tvSure.setTextColor(ContextCompat.getColor(AtworkApplicationLike.baseContext, R.color.common_blue_bg))
            tvSure.isEnabled = true
        } else {
            tvSure.setTextColor(ContextCompat.getColor(AtworkApplicationLike.baseContext, R.color.common_text_color_999))
            tvSure.isEnabled = false
        }
    }

}