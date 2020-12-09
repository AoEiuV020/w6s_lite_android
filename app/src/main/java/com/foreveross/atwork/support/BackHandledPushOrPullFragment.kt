package com.foreveross.atwork.support

import android.os.Bundle
import android.view.View
import com.foreveross.atwork.utils.finishByPullAnimation
import com.foreveross.atwork.utils.startByPushAnimation

abstract class BackHandledPushOrPullFragment: BackHandledFragment() {

    abstract fun getAnimationRoot(): View

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        startByPushAnimation(getAnimationRoot())
    }

    override fun onBackPressed(): Boolean {
        finishByPullAnimation(getAnimationRoot())
        return false
    }
}