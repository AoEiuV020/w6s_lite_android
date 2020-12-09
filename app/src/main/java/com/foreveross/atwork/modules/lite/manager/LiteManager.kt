package com.foreveross.atwork.modules.lite.manager

import com.foreveross.atwork.AtworkApplicationLike
import com.foreveross.atwork.infrastructure.beeworks.BeeWorks
import com.foreveross.atwork.infrastructure.beeworks.BeeWorksVote
import com.foreveross.atwork.infrastructure.shared.LiteCommonShareInfo
import com.foreveross.atwork.infrastructure.utils.UrlHandleHelper
import com.foreveross.atwork.infrastructure.utils.encryption.Base64Util
import com.foreveross.atwork.infrastructure.utils.extension.asType
import com.foreveross.atwork.modules.lite.module.LiteBindConfig
import java.net.URLDecoder

object LiteManager : ILiteManager {

    private val DEFAULT_CONFIG = LiteBindConfig(domainId = "khwjpn-app",domainName = "WorkPlus Lite",baseUrl = "http://lite.workplus.io:9000",apiUrl = "https://lite.workplus.io/v1/",data = "")

    override fun getBindConfigs(): Set<LiteBindConfig>? {
        val configs = LiteCommonShareInfo.getDataLiteBindConfigs(AtworkApplicationLike.baseContext)
                ?:HashSet()
        val sets = configs.map { produceBindConfig(it) }.toSet()
        return addDefaultConfig(sets)
    }

    private fun getBindConfigsWithoutDefault(): Set<LiteBindConfig>? {
        val configs = LiteCommonShareInfo.getDataLiteBindConfigs(AtworkApplicationLike.baseContext)
                ?:HashSet()
        return configs.map { produceBindConfig(it) }.toSet()
    }

    private fun addDefaultConfig(sets: Set<LiteBindConfig>): Set<LiteBindConfig> {
        val multiSets = sets.toMutableSet()
        multiSets.add(DEFAULT_CONFIG)
        return multiSets.toSet()
    }

    override fun updateBindConfig(liteBindConfig: LiteBindConfig) {

        var liteBindConfigs = getBindConfigsWithoutDefault()?.toHashSet()
        if(null == liteBindConfigs) {
            liteBindConfigs = HashSet()
        }

        liteBindConfigs.asType<HashSet<LiteBindConfig>>()?.add(liteBindConfig)

        LiteCommonShareInfo.updateDataLiteBindConfig(AtworkApplicationLike.baseContext, liteBindConfigs.map { it.data }.toSet())
        updateBindConfigCurrent(liteBindConfig)
    }

    override fun produceBindConfig(data: String) = LiteBindConfig(
            domainId = UrlHandleHelper.getValueEnsured(data, "domain_id").let { Base64Util.decode2Str(it) },
            domainName = UrlHandleHelper.getValueEnsured(data, "domain_name").let { URLDecoder.decode(Base64Util.decode2Str(it), "UTF-8") },
            baseUrl = UrlHandleHelper.getValueEnsured(data, "base_url").let { Base64Util.decode2Str(it) }.let { UrlHandleHelper.checkEndPath(it) },
            apiUrl = UrlHandleHelper.getValueEnsured(data, "api_url").let { Base64Util.decode2Str(it) }.let { UrlHandleHelper.checkEndPath(it) },
            data = data
    )


//        override fun produceBindConfig(data: String) = LiteBindConfig(
//            domainId = UrlHandleHelper.getKeyEnsured(data, "domain_id"),
//            domainName = UrlHandleHelper.getKeyEnsured(data, "domain_name").let { URLDecoder.decode(it, "UTF-8") },
//            baseUrl = UrlHandleHelper.getKeyEnsured(data, "base_url").let { UrlHandleHelper.checkEndPath(it) },
//            apiUrl = UrlHandleHelper.getKeyEnsured(data, "api_url").let { UrlHandleHelper.checkEndPath(it) },
//            data = data
//    )

    override fun updateBindConfigCurrent(liteBindConfig: LiteBindConfig) {
        LiteCommonShareInfo.updateDataLiteBindConfigCurrent(AtworkApplicationLike.baseContext, liteBindConfig.domainId)

        //数据映射到beeworks配置去
        BeeWorks.getInstance().reInitBeeWorks(AtworkApplicationLike.baseContext) {beeWorks ->
            processBeeWork(beeWorks, liteBindConfig)
        }
    }

    override fun getBindConfigCurrent():LiteBindConfig {
        return getBindConfigs()
                ?.find {
                    it.domainId == LiteCommonShareInfo.getDataLiteBindConfigCurrent(AtworkApplicationLike.baseContext)
                } ?: DEFAULT_CONFIG
    }

    override fun noBindConfigCurrent() = (null == getBindConfigCurrent())


    override fun matchBindRule(data: String)
            = data.contains("workplus://binding")
            && data.contains("domain_id=")
            && data.contains("domain_name=")
            && data.contains("base_url=")
            && data.contains("api_url=")


    override fun processBeeWork(beeWorks: BeeWorks, liteBindConfig: LiteBindConfig?) {

        val liteBindConfig = liteBindConfig?: getBindConfigCurrent() ?: return
        beeWorks.config.apiUrl = liteBindConfig.apiUrl
        beeWorks.config.domainId = liteBindConfig.domainId
        beeWorks.config.adminUrl = liteBindConfig.baseUrl
        beeWorks.config.beeWorksVote = BeeWorksVote(
                creatorUrl = liteBindConfig.baseUrl + "html/front/vote/index.html#/creator",
                myvoteUrl =  liteBindConfig.baseUrl + "html/front/vote/index.html#/myvote"
        )
    }

    override fun clearConfigs() {
        LiteCommonShareInfo.clear(AtworkApplicationLike.baseContext)
    }


}