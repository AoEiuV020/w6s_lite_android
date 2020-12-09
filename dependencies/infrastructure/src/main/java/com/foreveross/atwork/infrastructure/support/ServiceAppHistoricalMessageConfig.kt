package com.foreveross.atwork.infrastructure.support

import com.foreveross.atwork.infrastructure.beeworks.BeeWorks

class ServiceAppHistoricalMessageConfig (

    var searchable: Boolean = true,

    var tags: Boolean = true

): BaseConfig() {

    override fun parse() {
        BeeWorks.getInstance().mBeeworksServiceAppHistoricalMessage?.let {
            searchable = it.searchable
            tags = it.tags
        }

    }
}