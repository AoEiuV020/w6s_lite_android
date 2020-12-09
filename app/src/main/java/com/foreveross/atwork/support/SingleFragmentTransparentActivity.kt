package com.foreveross.atwork.support

import android.os.Bundle
import androidx.core.content.ContextCompat
import com.foreverht.workplus.ui.component.statusbar.WorkplusStatusBarHelper
import com.foreveross.atwork.AtworkApplicationLike
import com.foreveross.atwork.R
import kotlinx.android.synthetic.main.activity_fragment.*

/**
 *  pls setTheme(R.style.AppThemeTransparent) in AndroidManifest.xml

 * */
abstract class SingleFragmentTransparentActivity: SingleFragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fragmentContainer.setBackgroundColor(ContextCompat.getColor(AtworkApplicationLike.baseContext, R.color.transparent))


    }

    override fun changeStatusBar() {
        WorkplusStatusBarHelper.setCommonFullScreenStatusBar(this, true)
    }
}