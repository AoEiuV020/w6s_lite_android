@file: JvmName("SessionInfoHelper")
package com.foreveross.atwork.modules.chat.util

import com.foreverht.cache.AppCache
import com.foreverht.cache.DiscussionCache
import com.foreverht.cache.UserCache
import com.foreveross.atwork.infrastructure.BaseApplicationLike
import com.foreveross.atwork.infrastructure.model.Session
import com.foreveross.atwork.infrastructure.model.SessionType
import com.foreveross.atwork.infrastructure.model.ShowListItem
import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.foreveross.atwork.modules.app.manager.AppManager
import com.foreveross.atwork.modules.discussion.manager.DiscussionManager
import com.foreveross.atwork.manager.UserManager




fun getSessionDisplayInfoSync(session: Session): SessionDisplayInfo {

    val sessionDisplayInfo = SessionDisplayInfo(session.avatar, session.name)

    var contactObj: ShowListItem?  = when (session.type){
        SessionType.Discussion -> DiscussionCache.getInstance().getDiscussionCache(session.identifier)
        SessionType.User -> UserCache.getInstance().getUserCache(session.identifier)
        SessionType.LightApp, SessionType.Service, SessionType.NativeApp, SessionType.SystemApp -> AppCache.getInstance().getAppCache(session.identifier)
        else -> null
    }

    if (null == contactObj) {
        //按照缓存->数据库->网络 的顺序读取
        contactObj = when(session.type) {
            SessionType.Discussion -> DiscussionManager.getInstance().queryDiscussionSync(BaseApplicationLike.baseContext, session.identifier)
            SessionType.User -> UserManager.getInstance().queryUserInSyncByUserId(BaseApplicationLike.baseContext, session.identifier, session.mDomainId)
            SessionType.LightApp, SessionType.Service, SessionType.NativeApp, SessionType.SystemApp  -> AppManager.getInstance().queryAppSync(BaseApplicationLike.baseContext, session.identifier, session.orgId)
            else -> null
        }
    }

    if(null != contactObj) {
        sessionDisplayInfo.name = contactObj.getTitleI18n(BaseApplicationLike.baseContext)
        sessionDisplayInfo.avatar = contactObj.avatar
    }

    return sessionDisplayInfo
}

fun getAvatarIdSync(session: Session): String? {
    return getSessionDisplayInfoSync(session).avatar
}


fun getNameSync(session: Session): String? {
    return getSessionDisplayInfoSync(session).name
}


class SessionDisplayInfo(

    var avatar: String? = StringUtils.EMPTY,

    var name: String? = StringUtils.EMPTY
)
