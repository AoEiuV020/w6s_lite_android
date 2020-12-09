package com.foreveross.atwork.modules.qrcode.fragment

import android.app.Activity
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.foreveross.atwork.R
import com.foreveross.atwork.modules.qrcode.activity.QrInvoiceActivity
import com.foreveross.atwork.support.BackHandledFragment

class QrInvoiceFragment: BackHandledFragment() {

    var activity: Activity? = null

    var backBtn:            ImageView?  = null
    var tvTitle:            TextView?   = null
    var tvInvoiceTitle:     TextView?   = null
    var tvInvoiceCode:      TextView?   = null
    var tvInvoiceNum:       TextView?   = null
    var tvInvoiceCoast:     TextView?   = null
    var tvInvoiceDate:      TextView?   = null
    var invoiceValidView:   View?     = null
    var tvInvoiceValidCode: TextView? = null


    var invoiceInfo: Array<String>? = null

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        this.activity = activity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        invoiceInfo = arguments?.getStringArray(QrInvoiceActivity.EXTRA_QR_INVOICE_INFO)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_invoice, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initDatas()
        registerListener()
    }

    override fun findViews(view: View?) {
        val titleView: View? = view?.findViewById(R.id.title_bar)
        tvTitle = titleView?.findViewById(R.id.title_bar_common_title)
        tvTitle?.text = getString(R.string.invoice_helper)
        backBtn = titleView?.findViewById(R.id.title_bar_common_back)
        tvInvoiceTitle = view?.findViewById(R.id.invoice_type_text)
        tvInvoiceCode = view?.findViewById(R.id.tv_invoice_code)
        tvInvoiceNum = view?.findViewById(R.id.tv_invoice_num)
        tvInvoiceCoast = view?.findViewById(R.id.tv_invoice_coast)
        tvInvoiceDate = view?.findViewById(R.id.tv_invoice_date)
        invoiceValidView = view?.findViewById(R.id.invoice_valid_code_layout)
        tvInvoiceValidCode = view?.findViewById(R.id.tv_invoice_valid_code)

    }

    fun initDatas() {
        when(invoiceInfo?.get(1)) {
            "01" -> tvInvoiceTitle?.text = getString(R.string.invoice_type_01)
            "04" -> tvInvoiceTitle?.text = getString(R.string.invoice_type_04)
            else -> tvInvoiceTitle?.text = getString(R.string.invoice_type_10)
        }
        tvInvoiceCode?.text = invoiceInfo?.get(2)
        tvInvoiceNum?.text = invoiceInfo?.get(3)
        tvInvoiceCoast?.text = invoiceInfo?.get(4)
        val dateString = invoiceInfo?.get(5)
        tvInvoiceDate?.text = dateString?.substring(0, 4) + "." + dateString?.subSequence(4, 6) + "." + dateString?.subSequence(6, 8)
        val invoiceValidCode = invoiceInfo?.get(6)
        invoiceValidView?.visibility = if (TextUtils.isEmpty(invoiceValidCode)) View.GONE else View.VISIBLE
        tvInvoiceValidCode?.text = invoiceValidCode
    }

    fun registerListener() {
        backBtn?.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onBackPressed(): Boolean {
        activity?.finish()
        return true
    }
}