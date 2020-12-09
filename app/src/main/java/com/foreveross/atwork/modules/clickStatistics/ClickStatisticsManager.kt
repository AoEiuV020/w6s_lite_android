package com.foreveross.atwork.modules.clickStatistics

import com.foreverht.db.service.repository.ClickStatisticsRepository
import com.foreverht.threadGear.DbThreadPoolExecutor
import com.foreveross.atwork.infrastructure.model.clickStatistics.ClickEvent
import com.foreveross.atwork.infrastructure.model.clickStatistics.Type
import com.foreveross.atwork.infrastructure.utils.LogUtil
import com.foreveross.atwork.infrastructure.utils.TimeUtil
import com.foreveross.atwork.modules.app.util.AppRefreshHelper


interface IClickStatisticsManager {


    fun getClickEvents(type: Type): List<ClickEvent>

    /**
     * 更新点击数量
     *
     * @param id
     * @param type
     * */
    fun updateClick(id: String, type: Type)

    fun clear()
}

object ClickStatisticsManager: IClickStatisticsManager {





    private var clickData: HashMap<Type, ArrayList<ClickEvent>> = HashMap()


    override fun getClickEvents(type: Type): ArrayList<ClickEvent> {
        val afterTime = TimeUtil.getCurrentTimeInMillis() - 60 * 1000 * 5

        if(null == clickData[type]) {
            clickData[type] = ClickStatisticsRepository.getInstance().queryClickEvents(type, afterTime) as ArrayList

            LogUtil.e("clickData -> ${clickData[type]}")
        }

//        clickData[type] = ClickStatisticsRepository.getInstance().queryClickEvents(type, afterTime) as ArrayList

        LogUtil.e("clickData -> ${clickData[type]}")


        val filterClickData = clickData[type]!!.filter { it.begin > afterTime } as ArrayList<ClickEvent>
        clickData[type] = filterClickData
        return filterClickData
    }


    /**
     * 更新点击数量
     *
     * @param id
     * @param type
     * */
    override fun updateClick(id: String, type: Type) {

        DbThreadPoolExecutor.getInstance().execute {
            val clickEvent = ClickEvent(id = id, type = type)

            val clickEvents:ArrayList<ClickEvent> = getClickEvents(type)
            clickEvents.add(clickEvent)


            ClickStatisticsRepository.getInstance().updateClick(clickEvent)

            AppRefreshHelper.refreshAppLightly()
        }


    }

    private fun findClickEvent(id: String, clickEvents: List<ClickEvent>):ClickEvent?  {
        for(clickEvent in clickEvents) {
            if(id == clickEvent.id) {
                return clickEvent
            }
        }

        return null
    }

    private fun checkDataLoad(type: Type) {
        getClickEvents(type)
    }

    override fun clear() {
        clickData.clear()
    }
}