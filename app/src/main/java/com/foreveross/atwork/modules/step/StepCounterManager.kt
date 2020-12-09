package com.foreveross.atwork.modules.step

import android.app.Application
import android.content.Context
import com.foreveross.atwork.infrastructure.model.step.TodayStepData
import com.foreveross.atwork.infrastructure.plugin.WorkplusPluginCore
import com.foreveross.atwork.infrastructure.plugin.step.IStepCounterPlugin
import com.foreveross.atwork.infrastructure.utils.reflect.ReflectException

object StepCounterManager : IStepCounterPlugin{


    private var stepCounterPlugin: IStepCounterPlugin? = null

    private fun checkPlugin() {
        if (null == stepCounterPlugin) {
            try {
                WorkplusPluginCore.registerPresenterPlugin("com.foreverht.workplus.stepCounter.StepCounterPlugin")
                stepCounterPlugin = WorkplusPluginCore.getPlugin(IStepCounterPlugin::class.java)

            } catch (e: ReflectException) {
                e.printStackTrace()
            }

        }
    }


    override fun init(application: Application) {
        checkPlugin()

        stepCounterPlugin?.apply {
            init(application)
        }
    }

    override fun getCurrentTimeSportStep(context: Context, getStepCount: (Int) -> Unit) {
        checkPlugin()

        stepCounterPlugin?.apply {
            getCurrentTimeSportStep(context, getStepCount)
        }
    }

    override fun getTodaySportStepArray(context: Context, getStepArray: (List<TodayStepData>) -> Unit) {
        checkPlugin()

        stepCounterPlugin?.apply {
            getTodaySportStepArray(context, getStepArray)
        }
    }

    override fun getTodaySportStepArrayByDate(context: Context, date: String, getStepArray: (List<TodayStepData>) -> Unit) {
        checkPlugin()

        stepCounterPlugin?.apply {
            getTodaySportStepArrayByDate(context, date, getStepArray)
        }
    }

    override fun getTodaySportStepArrayByStartDateAndDays(context: Context, date: String, days: Int, getStepArray: (List<TodayStepData>) -> Unit) {
        checkPlugin()

        stepCounterPlugin?.apply {
            getTodaySportStepArrayByStartDateAndDays(context, date, days, getStepArray)
        }
    }

}