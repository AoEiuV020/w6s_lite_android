package com.foreveross.atwork.modules.contact.manager

import android.annotation.SuppressLint
import android.os.AsyncTask
import com.foreverht.cache.AppCache
import com.foreverht.cache.DiscussionCache
import com.foreverht.cache.UserCache
import com.foreverht.db.service.repository.DiscussionRepository
import com.foreverht.db.service.repository.RelationshipRepository
import com.foreverht.db.service.repository.UserRepository
import com.foreverht.threadGear.DbThreadPoolExecutor
import com.foreveross.atwork.infrastructure.BaseApplicationLike
import com.foreveross.atwork.infrastructure.model.ShowListItem
import com.foreveross.atwork.infrastructure.model.app.App
import com.foreveross.atwork.infrastructure.model.discussion.Discussion
import com.foreveross.atwork.infrastructure.model.user.User
import com.foreveross.atwork.infrastructure.utils.FirstLetterUtil
import com.foreveross.atwork.infrastructure.utils.ListUtil
import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.foreveross.atwork.modules.discussion.manager.DiscussionManager
import com.foreveross.atwork.manager.FriendManager
import com.foreveross.atwork.manager.listener.BaseQueryListener
import com.foreveross.atwork.modules.contact.data.StarUserListDataWrap
import kotlin.Comparator

object ContactSimpleModeManager {


    private const val RELATIONSHIP_WEIGHT_STAR = 100

    private const val RELATIONSHIP_WEIGHT_FRIEND = 99

    private const val RELATIONSHIP_WEIGHT_STAR_AND_FRIEND = 98

    private const val RELATIONSHIP_WEIGHT_DISCUSSION_INTERNAL = 97

    private const val RELATIONSHIP_WEIGHT_DISCUSSION_NORMAL = 96

    private const val RELATIONSHIP_WEIGHT_DEFAULT = 0



    /**
     * 用以比较缓冲数据与本地数据库数据, 若发现有偏差, 为了避免显示不全, 则需要进行对缓冲校准
     * */
    @SuppressLint("StaticFieldLeak")
    fun calibrateData(baseQueryListener: BaseQueryListener<Boolean>) {


        object : AsyncTask<Void, Void, Boolean>(){
            override fun doInBackground(vararg params: Void?): Boolean {
                val friendList = FriendManager.getInstance().friendListNotCheck
                if(null != friendList) {
                    if(friendList.size != RelationshipRepository.getInstance().queryFriendCount()) {
                        return true
                    }
                }


                val discussionList = DiscussionManager.getInstance().discussionListNotCheck
                if(null != discussionList) {

                    if(discussionList.size != DiscussionRepository.getInstance().queryDiscussionCount()) {
                        return true
                    }
                }

                return false
            }


            override fun onPostExecute(result: Boolean) {
                baseQueryListener.onSuccess(result)
            }

        }.executeOnExecutor(DbThreadPoolExecutor.getInstance())
    }


    @SuppressLint("StaticFieldLeak")
    fun fetchData(baseQueryListener: BaseQueryListener<List<ShowListItem>>) {


        object : AsyncTask<Void, Void, List<ShowListItem>>(){
            override fun doInBackground(vararg p0: Void?): List<ShowListItem> {
                return fetchDataSync()
            }

            override fun onPostExecute(result: List<ShowListItem>) {
                baseQueryListener.onSuccess(result)
            }

        }.executeOnExecutor(DbThreadPoolExecutor.getInstance())
    }

    fun fetchDataSync(): List<ShowListItem> {
        var starUserList = ArrayList<User>()

        if (ListUtil.isEmpty(starUserList)) {
            StarUserListDataWrap.getInstance().setContactList(UserRepository.getInstance().queryStarFlagUsers())
        }

        starUserList.addAll(StarUserListDataWrap.getInstance().contactList)


        var friendUserList = ArrayList<User>()
        val friendListFromManager = FriendManager.getInstance().friendListSync
        if (!ListUtil.isEmpty(friendListFromManager)) {
            friendUserList.addAll(friendListFromManager)
        }

        var discussionList = ArrayList<Discussion>()
        val discussionListFromManager = DiscussionManager.getInstance().internalDiscussionListSync
        if (!ListUtil.isEmpty(discussionListFromManager)) {
            discussionList.addAll(discussionListFromManager)
        }


        val totalContactList = arrayListOf<ShowListItem>()

        addContactToTargetContactList(starUserList, totalContactList)

        addContactToTargetContactList(friendUserList, totalContactList)

        addContactToTargetContactList(discussionList, totalContactList)

        totalContactList.sortWith(Comparator { p0, p1 ->
//            var p0Pinyin = p0.titlePinyin
//            if(StringUtils.isEmpty(p0Pinyin)) {
//                p0Pinyin = FirstLetterUtil.getFullLetter(p0.title)
//            }

//            var p1Pinyin = p1.titlePinyin
//            if(StringUtils.isEmpty(p1Pinyin)) {
//                p1Pinyin = FirstLetterUtil.getFullLetter(p1.title)
//            }

            var p0Pinyin = p0.titlePinyin
            if(StringUtils.isEmpty(p0Pinyin)) {
                p0Pinyin = FirstLetterUtil.getFullLetter(p0.getTitleI18n(BaseApplicationLike.baseContext))
            }

            var p1Pinyin = p1.titlePinyin
            if(StringUtils.isEmpty(p1Pinyin)) {
                p1Pinyin = FirstLetterUtil.getFullLetter(p1.getTitleI18n(BaseApplicationLike.baseContext))
            }



            var p0FirstLetter = p0Pinyin.substring(0, 1).toLowerCase()
            if(!FirstLetterUtil.isLetter(p0FirstLetter)) {
                p0FirstLetter = "#"
            }

            var p1FirstLetter = p1Pinyin.substring(0, 1).toLowerCase()
            if(!FirstLetterUtil.isLetter(p1FirstLetter)) {
                p1FirstLetter = "#"
            }

            //相同首字母的, 按照如下顺序:
            if(p0FirstLetter.equals(p1FirstLetter, true)) {
                var p0Weight = getRelationShipWeight(contact = p0, starUserList = starUserList, friendUserList = friendUserList)
                var p1Weight = getRelationShipWeight(contact = p1, starUserList = starUserList, friendUserList = friendUserList)

                if(0 < p0Weight - p1Weight) {
                    return@Comparator -1
                }

                if(0 > p0Weight - p1Weight) {
                    return@Comparator 1
                }

                return@Comparator p0Pinyin.compareTo(p1Pinyin)



            }


            if("#" == p0FirstLetter) {
                return@Comparator 1
            }

            if("#" == p1FirstLetter) {
                return@Comparator -1
            }

            return@Comparator p0FirstLetter.compareTo(p1FirstLetter)


        })

        return totalContactList
    }


    private fun transferFromCache(contact: ShowListItem): ShowListItem {
        var contact = contact

        when(contact) {
            is User -> {
                val userInCache = UserCache.getInstance().getUserCache(contact.getId())
                if (null != userInCache) {
                    contact = userInCache
                }
            }

            is App -> {
                val appInCache = AppCache.getInstance().getAppCache(contact.getId())
                if (null != appInCache) {
                    contact = appInCache
                }
            }


            is Discussion -> {
                val discussionInCache = DiscussionCache.getInstance().getDiscussionCache(contact.getId())
                if (null != discussionInCache) {
                    contact = discussionInCache
                }
            }
        }

        return contact
    }

    private fun getRelationShipWeight(contact: ShowListItem, starUserList: List<ShowListItem>, friendUserList: List<ShowListItem>): Int {

        when(contact) {
            is Discussion -> {
                return if(contact.isInternalDiscussion) {
                    RELATIONSHIP_WEIGHT_DISCUSSION_INTERNAL
                } else {
                    RELATIONSHIP_WEIGHT_DISCUSSION_NORMAL

                }
            }

            else -> {
                if(starUserList.contains(contact) && friendUserList.contains(contact)) {
                    return RELATIONSHIP_WEIGHT_STAR_AND_FRIEND
                }

                if(starUserList.contains(contact)) {
                    return RELATIONSHIP_WEIGHT_STAR

                }

                if(friendUserList.contains(contact)) {
                    return RELATIONSHIP_WEIGHT_FRIEND

                }
            }
        }

        return RELATIONSHIP_WEIGHT_DEFAULT

    }


    private fun addContactToTargetContactList(contactList: List<ShowListItem>, targetContactList: ArrayList<ShowListItem>) {
        contactList.forEach { contact ->
            if (!targetContactList.contains(contact)) {
                targetContactList.add(transferFromCache(contact))
            }
        }
    }


}