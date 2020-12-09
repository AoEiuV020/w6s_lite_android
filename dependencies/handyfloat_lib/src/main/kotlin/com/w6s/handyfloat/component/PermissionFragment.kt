package com.w6s.handyfloat.component

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.foreveross.atwork.infrastructure.utils.rom.FloatWindowPermissionUtil
import com.w6s.handyfloat.interfaces.OnFloatPermissionResult

internal class PermissionFragment : Fragment() {

    companion object {
        private var onPermissionResult: OnFloatPermissionResult? = null

        @SuppressLint("CommitTransaction")
        fun requestPermission(activity: FragmentActivity, onPermissionResult: OnFloatPermissionResult) {
            this.onPermissionResult = onPermissionResult
            activity.supportFragmentManager
                .beginTransaction()
                .add(PermissionFragment(), activity.localClassName)
                .commitAllowingStateLoss()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // 权限申请
        FloatWindowPermissionUtil.requestFloatPermission(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == FloatWindowPermissionUtil.COMMON_REQUEST_FLOAT_CODE) {
            // 需要延迟执行，不然即使授权，仍有部分机型获取不到权限
            Handler(Looper.getMainLooper()).postDelayed({
                val check = FloatWindowPermissionUtil.isFloatWindowOpAllowed(activity)
                // 回调权限结果
                onPermissionResult?.permissionResult(check)
                // 将Fragment移除
                fragmentManager?.beginTransaction()?.remove(this)?.commitAllowingStateLoss()
            }, 500)
        }
    }

}