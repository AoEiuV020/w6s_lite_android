package com.foreveross.atwork.infrastructure.plugin.step

import android.app.Application
import android.content.Context
import com.foreveross.atwork.infrastructure.model.step.TodayStepData
import com.foreveross.atwork.infrastructure.plugin.WorkplusPlugin

interface IStepCounterPlugin: WorkplusPlugin {

    fun init(application: Application)

    /**
     * 获取当前时间运动步数
     *
     * @param context
     * @param getStepCount
     */
    fun getCurrentTimeSportStep(context: Context, getStepCount: (Int) -> Unit)

    /**
     * 获取所有步数列表
     *
     * @param context
     * @param getStepArray
     */
    fun getTodaySportStepArray(context: Context, getStepArray: (List<TodayStepData>) -> Unit)

    /**
     * 根据时间获取步数列表
     *
     * @param context
     * @param date 格式yyyy-MM-dd
     * @param getStepArray
     * @return
     */
    fun getTodaySportStepArrayByDate(context: Context, date: String, getStepArray: (List<TodayStepData>) -> Unit)

    /**
     * 根据时间和天数获取步数列表
     * 例如：
     * startDate = 2018-01-15
     * days = 3
     * 获取 2018-01-15、2018-01-16、2018-01-17三天的步数
     *
     * @param context
     * @param startDate 格式yyyy-MM-dd
     * @param days
     * @param getStepArray
     *
     * @return
     */
    fun getTodaySportStepArrayByStartDateAndDays(context: Context, date: String, days: Int, getStepArray: (List<TodayStepData>) -> Unit)
}

