package com.foreveross.atwork.modules.qrcode.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.foreveross.atwork.modules.qrcode.fragment.QrInvoiceFragment
import com.foreveross.atwork.support.SingleFragmentActivity

class QrInvoiceActivity: SingleFragmentActivity() {

    companion object {
        val EXTRA_QR_INVOICE_INFO = "EXTRA_QR_INVOICE_INFO"

        fun getIntent(context: Context, invoiceInfo: Array<String>): Intent {
            val intent = Intent(context, QrInvoiceActivity::class.java)
            intent.putExtra(EXTRA_QR_INVOICE_INFO, invoiceInfo)
            return intent
        }
    }

    var invoiceInfo: Array<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        invoiceInfo = intent.getStringArrayExtra(EXTRA_QR_INVOICE_INFO);
    }

    override fun createFragment(): Fragment {
        var fragment = QrInvoiceFragment()
        fragment.arguments?.putStringArray(EXTRA_QR_INVOICE_INFO, invoiceInfo)
        return fragment
    }
}