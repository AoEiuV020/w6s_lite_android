package com.foreverht.workplus.module.file_share

import android.os.Parcelable
import com.foreveross.atwork.infrastructure.model.dropbox.Dropbox
import com.foreveross.atwork.infrastructure.support.AtworkConfig
import kotlinx.android.parcel.Parcelize

/**
 *  create by reyzhang22 at 2019-08-30
 *  @domainId 域id
 *  @SourceType 源 orgs users discussion
 *  @opsId 操作id userId orgId discussionId
 *  @FileId 文件id
 *  @type id:对应网盘id file_id：对应文件mediaId
 */

@Parcelize
data class FileShareAction(var domainId: String? = AtworkConfig.DOMAIN_ID,
                      var sourceType: Dropbox.SourceType = Dropbox.SourceType.User,
                      var opsId: String? = "",
                      var fileId: String? = "",
                      var type: String = "id") : Parcelable