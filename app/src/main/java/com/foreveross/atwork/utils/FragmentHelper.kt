package com.foreveross.atwork.utils

import android.animation.ValueAnimator
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import com.foreveross.atwork.R
import com.foreveross.atwork.support.BackHandledFragment

fun Fragment.finishByPullAnimation() {
    if(this is BackHandledFragment) {
        finish(false)
    } else {
        activity?.finish()
    }

    activity?.overridePendingTransition(0, R.anim.out_to_bottom)
}



fun Fragment.finishByNoAnimation() {
    if(this is BackHandledFragment) {
        finish(false)
    } else {
        activity?.finish()
    }

    activity?.overridePendingTransition(0, 0)
}

fun Fragment.startByPushAnimation(view: View) {
    val inFromBottomAnimation = AnimationUtils.loadAnimation(context, R.anim.in_from_bottom)
    view.startAnimation(inFromBottomAnimation)

    startPushViewBgAnimation(view, inFromBottomAnimation)

}

private fun startPushViewBgAnimation(view: View, inFromBottomAnimation: Animation) {
    val originalBg = (view.parent as View).background.mutate()
    val valueAnimator = ValueAnimator.ofInt(0, 255).setDuration(inFromBottomAnimation.duration)
    valueAnimator.addUpdateListener {
        originalBg.alpha = it.animatedValue as Int
    }

    valueAnimator.start()
}

fun Fragment.finishByPullAnimation(view: View) {
    val outToBottomAnimation = AnimationUtils.loadAnimation(context, R.anim.out_to_bottom)
    outToBottomAnimation.setAnimationListener(object : Animation.AnimationListener {
        override fun onAnimationRepeat(animation: Animation?) {
        }

        override fun onAnimationEnd(animation: Animation?) {
            finishByNoAnimation()
        }

        override fun onAnimationStart(animation: Animation?) {
        }

    })
    view.startAnimation(outToBottomAnimation)
    finishPullViewBgAnimation(view, outToBottomAnimation)
}

private fun finishPullViewBgAnimation(childView: View, childViewAnimation: Animation) {
    val originalBg = (childView.parent as View).background.mutate()
    val valueAnimator = ValueAnimator.ofInt(255, 0).setDuration(childViewAnimation.duration)
    valueAnimator.addUpdateListener {
        originalBg.alpha = it.animatedValue as Int
    }

    valueAnimator.start()
}