package com.foreveross.atwork.infrastructure.model.discussion.template

import android.os.Parcelable
import androidx.annotation.ColorInt
import androidx.core.graphics.toColorInt
import com.foreveross.atwork.infrastructure.model.i18n.I18nInfo
import com.foreveross.atwork.infrastructure.utils.TimeUtil
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


@Parcelize
open class DiscussionTemplate(

        @SerializedName("domain_id")
        var domainId: String = "",

        @SerializedName("id")
        var id: String = "",

        @SerializedName("sort")
        var sort: Int = 0,

        @SerializedName("avatar")
        var avatar: String = "",

        @SerializedName("icon")
        var icon: String = "",

        @SerializedName("label")
        var label: String = "",

        @SerializedName("desc")
        var desc: String = "",

        @SerializedName("name")
        var name: String = "",

        @SerializedName("en_name")
        var enName: String? = "",

        @SerializedName("tw_name")
        var twName: String? = "",

        @SerializedName("property")
        var property: String = "",

        @SerializedName("disabled")
        var disabled: Boolean = false,

        @SerializedName("deleted")
        var deleted: Boolean = false,

        @SerializedName("inuse")
        var inuse: Boolean = true,

        @SerializedName("create_time")
        var createTime: Long = -1,

        @SerializedName("modify_time")
        var modifyTime: Long = -1,

        @SerializedName("tag_pinned")
        var tagPinned: Boolean = false,

        @SerializedName("role_pinned")
        var rolePinned: Boolean = false,

        @SerializedName("feature_pinned")
        var featurePinned: Boolean = false,

        @SerializedName("scope_pinned")
        var scopePinned: Boolean = false,

        @SerializedName("features")
        var features: List<DiscussionFeature>? = null,

        @SerializedName("tags")
        var tags: List<DiscussionMemberTag>? = null





) : I18nInfo(), Parcelable, Comparable<DiscussionTemplate> {

    override fun getStringName(): String {
        return name
    }

    override fun getStringTwName(): String? {
        return twName
    }

    override fun getStringEnName(): String? {
        return enName
    }

    fun getRequestId(): String? {
        if(id != ID_COMMON) {
            return id
        }

        return null
    }

    fun isColorLabel() = label.contains(":")

    fun getLabelContent(): String? {
        if(!isColorLabel()) {
            return null
        }

        val infoArray = label.split(":")
        val content = infoArray.getOrNull(0)
        return content
    }

    @ColorInt
    fun getLabelColor(): Int {
        return transferColorStr(getLabelColorStr())
    }

    fun getLabelColorStr(): String {
        val infoArray = label.split(":")

        return infoArray
                .getOrElse(1) { "#999999" }
    }

    private fun transferColorStr(colorStr: String): Int {
        return try {
            if (colorStr.startsWith("#")) {
                colorStr.toColorInt()
            } else {
                "#$colorStr".toColorInt()
            }
        } catch (e: Exception) {
            "#999999".toColorInt()
        }
    }

    fun isCommonNoTemplate() = ID_COMMON == id




    companion object {
        const val ID_COMMON = "common"

        fun creteCommonTemplate() = DiscussionTemplate().apply {
            id = ID_COMMON
            name = "普通群"
            disabled = false
            deleted = false
            sort = 0
            createTime = TimeUtil.getCurrentTimeInMillis()
            modifyTime = TimeUtil.getCurrentTimeInMillis()
        }
    }

    override fun compareTo(other: DiscussionTemplate): Int {
        if(ID_COMMON == id) {
            return -1
        }

        if(ID_COMMON== other.id) {
            return 1
        }

        return other.createTime.compareTo(createTime)
    }


}


