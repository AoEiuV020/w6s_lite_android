package com.foreverht.workplus.ui.component.dialogFragment

import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import android.view.View
import android.view.ViewGroup
import com.foreverht.workplus.ui.component.statusbar.WorkplusStatusBarHelper

abstract class BasicUIDialogFragment: DialogFragment() {

    private var onDismissListener: ((DialogInterface) -> Unit)? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        changeStatusBar(view)

    }

    protected open fun changeStatusBar(view: View) {
        WorkplusStatusBarHelper.setDialogFragmentStatusBar(view as ViewGroup, dialog?.window)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)

        onDismissListener?.invoke(dialog)
    }

    open fun setOnDismissListener(onDismissListener: ((DialogInterface) -> Unit)) {
        this.onDismissListener = onDismissListener
    }
}