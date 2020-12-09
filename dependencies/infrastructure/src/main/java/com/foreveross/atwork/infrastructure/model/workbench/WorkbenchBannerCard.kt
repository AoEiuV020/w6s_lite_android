package com.foreveross.atwork.infrastructure.model.workbench

import com.foreveross.atwork.infrastructure.BaseApplicationLike
import com.foreveross.atwork.infrastructure.model.advertisement.AdvertisementConfig
import com.foreveross.atwork.infrastructure.model.workbench.content.WorkbenchBannerCardContent
import com.foreveross.atwork.infrastructure.model.workbench.data.WorkbenchAdvertisementEachData
import com.foreveross.atwork.infrastructure.model.workbench.data.WorkbenchCardDetailData
import com.foreveross.atwork.infrastructure.model.workbench.data.WorkbenchDefinitionData
import com.foreveross.atwork.infrastructure.utils.FileUtil
import com.foreveross.atwork.infrastructure.utils.ListUtil

class WorkbenchBannerCard: WorkbenchCard() {

    var intervalSeconds: Long? = null

    var bannerHeights: String? = null

    fun parse(advertisements: List<AdvertisementConfig>?, workbenchDefinitionData: WorkbenchDefinitionData, workbenchCardDetailData: WorkbenchCardDetailData) {
        super.parse(workbenchDefinitionData, workbenchCardDetailData)

        intervalSeconds = workbenchCardDetailData.workbenchCardDisplayDefinitions?.intervalSeconds
        bannerHeights = workbenchCardDetailData.workbenchCardDisplayDefinitions?.bannerHeight

        workbenchCardContent = WorkbenchBannerCardContent(workbenchCardDetailData.id)

        val workbenchBannerCardContent=  (workbenchCardContent as WorkbenchBannerCardContent)

        advertisements?.forEach { advertisementConfig ->

            if(workbenchCardDetailData.id.toString() == advertisementConfig.mSerialNo) {
                workbenchBannerCardContent.records.add(advertisementConfig)
            }

        }
    }



    fun getBannerRecordsLegal(): List<AdvertisementConfig>? {

        val bannerRecord = getCardContent<WorkbenchBannerCardContent>()?.records

        val bannerRecordLegal = bannerRecord
                ?.filter {
                    FileUtil.isExist(it.getLocalBannerPath(BaseApplicationLike.baseContext, orgCode))
                            && it.isValidDuration
                }

        return bannerRecordLegal
    }


    fun shouldVisible() = !ListUtil.isEmpty(getBannerRecordsLegal())
}