package com.foreveross.atwork.infrastructure.model.discussion.template

import android.os.Parcelable
import com.foreveross.atwork.infrastructure.model.discussion.template.DiscussionDefinition
import com.foreveross.atwork.infrastructure.utils.FirstLetterUtil
import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class DiscussionFeature(
        @SerializedName("id")
        var id: String = "",

        @SerializedName("name")
        var name: String = "",

        @SerializedName("icon")
        var icon: String = "",

        @SerializedName("feature")
        var feature: String = "",

        @SerializedName("disabled")
        var disabled: Boolean = false,

        @SerializedName("layouts")
        var layouts: MutableList<String> = mutableListOf(),

        @SerializedName("definitions")
        var definitions: DiscussionDefinition? = DiscussionDefinition(),

        @SerializedName("sort")
        var sort: Int = Int.MAX_VALUE,

        var rawFeatureData: String? = null

) : Parcelable, Comparable<DiscussionFeature> {

    fun isEntryFeature() = isAppFeature() || LEGAL_ENTRY_FEATURES.any { it.equals(feature, true) }

    fun isOnlyChatDetailEntryFeatures() = CHAT_DETAIL_ENTRY_FEATURES.any { it.equals(feature, true) }

    fun isAppFeature() = (DomainApp == feature || OrgApp == feature)

    fun isLegalEntry(legalEntryIds: List<String>, layout: Int): Boolean {
        if (!isEntryFeature()) {
            return false
        }
        if (!layouts.contains(layout.toString())) {
            return false
        }

        if (isAppFeature() && !legalEntryIds.contains(definitions?.entryId)) {
            return false
        }

        if (LAYOUT_CHAT_INFO == layout && isOnlyChatDetailEntryFeatures()) {
            return false
        }

        return true
    }



    companion object {

        const val ID_MORE = "ID_MORE"

        //自定义入口
        const val CUSTOM = "Custom"

        //域应用入口
        const val DomainApp = "DomainApp"

        //组织应用入口
        const val OrgApp = "OrgApp"

        //群成员标签
        const val TAG = "Tag"

        //禁言
        const val MUT = "Mut"

        //@用户
        const val AT_USER = "AtUser"

        //@所有人
        const val AT_ALL = "AtAll"

        //发送语音
        const val VOICE = "Voice"

        //发送文本
        const val TEXT = "Text"

        //发送表情
        const val STICKER = "Sticker"

        //发送图片
        const val IMAGE = "Image"

        //发送文件
        const val FILE = "File"

        //发送视频
        const val VIDEO = "Video"

        //位置
        const val LOCATION = "Location"

        //名片
        const val BUSINESS_CARD = "BusinessCard"

        //语音通话
        const val VOIP = "VoIP"

        //网盘
        const val VOLUME = "Volume"

        //投票
        const val POLL = "Poll"

        //必应消息
        const val BING = "Bing"

        //发起会议
        const val CONFERENCE = "Conference"

        //收藏
        const val FAVORITE = "Favorite"

        //Pin消息
        const val PIN = "Pin"

        //消息免打扰
        const val NOTIFICATION = "Notification"

        //消息漫游
        const val MESSAGE_ROAMING = "MessageRoaming"

        //添加成员
        const val ADD_MEMBER = "AddMember"

        //删除成员
        const val KICK_MEMBER = "KickMember"

        //邀请成员
        const val INVITE_MEMBER = "InviteMember"

        //退出群聊
        const val LEAVE_DISCUSSION = "LeaveDiscussion"

        //修改群信息
        const val DISCUSSION_PROFILE = "DiscussionProfile"

        //成员角色管理
        const val MEMBER_ROLE = "MemberRole"

        //成员标签管理
        const val MEMBER_TAG = "MemberTag"

        //群管理
        const val ADMINISTRATOR = "Administrator"

        //群入口列表
        const val ENTRY_LIST = "EntryList"

        //聊天窗口下方
        const val LAYOUT_SESSION_MORE_BOTTOM = 3

        //群详情页面
        const val LAYOUT_CHAT_INFO = 2

        //聊天窗口上方
        const val LAYOUT_SESSION_BAR_UP = 1


        val LEGAL_ENTRY_FEATURES = arrayListOf(CUSTOM, VOIP, LOCATION, VIDEO, CONFERENCE, FILE)
        val CHAT_DETAIL_ENTRY_FEATURES = arrayListOf(VOIP, LOCATION, VIDEO, CONFERENCE, FILE)


        fun assembleLayouts(sessionBarDisplay: Boolean, chatInfoDisplay: Boolean): MutableList<String> {
            val layoutList = ArrayList<String>()
            if(sessionBarDisplay) {
                layoutList.add(LAYOUT_SESSION_BAR_UP.toString())
            }

            if(chatInfoDisplay) {
                layoutList.add(LAYOUT_CHAT_INFO.toString())

            }

            return layoutList;
        }

    }

    override fun toString(): String {
        return "DiscussionFeature(id='$id', name='$name', icon='$icon', feature='$feature', disabled=$disabled, layouts=$layouts, definitions=$definitions, rawFeatureData=$rawFeatureData)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DiscussionFeature

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun compareTo(other: DiscussionFeature): Int {
        if(sort == other.sort) {
            return FirstLetterUtil.getFullLetter(name).compareTo(FirstLetterUtil.getFullLetter(other.name))
        }

        return sort.compareTo(other.sort)
    }

}



