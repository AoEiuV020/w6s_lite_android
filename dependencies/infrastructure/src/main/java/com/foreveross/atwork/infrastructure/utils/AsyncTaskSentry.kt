package com.foreveross.atwork.infrastructure.utils

import android.os.AsyncTask
import java.lang.ref.WeakReference

/**
 * 监控 AsyncTask, 维护生命周期, 防止退出账号后, 正在队列中的线程继续执行下去
 * */
object AsyncTaskSentry {

    private var asyncTaskData = hashMapOf<Int, WeakReference<AsyncTask<*, *, *>>>()


    fun addTask(task: AsyncTask<*, *, *>) {

        LogUtil.e("task.hashCode() -> $task.hashCode()")

        asyncTaskData[task.hashCode()] = WeakReference(task)
    }

    fun removeTask(task: AsyncTask<*, *, *>) {
        asyncTaskData.remove(task.hashCode())
    }

    fun clearTask() {
        asyncTaskData.forEach {
            it.value.get()?.cancel(false)
        }

        asyncTaskData.clear()
    }



    abstract class AsyncTaskWatching<Params, Progress, Result>: AsyncTask<Params, Progress, Result>() {
        override fun onPreExecute() {
            addTask(this)
        }

        override fun onPostExecute(result: Result) {
            removeTask(this)
        }
    }
}