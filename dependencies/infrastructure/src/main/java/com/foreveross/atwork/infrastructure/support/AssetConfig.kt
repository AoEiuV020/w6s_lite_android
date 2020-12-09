package com.foreveross.atwork.infrastructure.support

import com.foreveross.atwork.infrastructure.utils.StringUtils
import java.util.*

/**
 * Created by dasunsy on 2018/1/25.
 */

class AssetConfig: BaseConfig() {


    /**
     * 红包最大发送数
     */
    var maxEnvelopeGiveCount = 600

    /**
     * 红包最大发送金额
     */
    var maxEnvelopeGiveAmount: Long = 100000

    /**
     * 多选群发红包限制
     */
    var maxGiveMultiDiscussionCount = 5

    /**
     * 帮助说明
     */
    var helpDoc = StringUtils.EMPTY


    /**
     * 红包开关
     * */
    var isRedEnvelopeEnable  = false



    override fun parse(pro: Properties) {
        pro.getProperty("ASSET_CONFIG_HELP_DOC")?.apply {
            helpDoc = this
        }


        pro.getProperty("RED_ENVELOPE_ENABLE")?.apply {
            isRedEnvelopeEnable = toBoolean()
        }
    }
}
