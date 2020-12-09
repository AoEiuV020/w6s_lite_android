package com.foreverht.db.service.repository;
/**
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@%((((((((#&@@@((#((#@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@%(((((((((((((%@*((((@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@%(((/,/(((((((((@@@*#@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@#(((@@@((((((((((@@((@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@(((((((((((((((((((((@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@(((((((((((((((((((@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@(((((((((((((((*@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 *                       __           __
 * .--.--.--.-----.----.|  |--.-----.|  |.--.--.-----.
 * |  |  |  |  _  |   _||    <|  _  ||  ||  |  |__ --|
 * |________|_____|__|  |__|__|   __||__||_____|_____|
 *                            |__|
 */


import android.database.Cursor;
import androidx.annotation.Nullable;

import com.foreverht.cache.DiscussionCache;
import com.foreverht.db.service.W6sBaseRepository;
import com.foreverht.db.service.dbHelper.DiscussionDBHelper;
import com.foreveross.atwork.infrastructure.model.discussion.Discussion;
import com.foreveross.atwork.infrastructure.model.discussion.DiscussionMember;

import com.foreveross.atwork.infrastructure.model.discussion.DiscussionOwner;
import com.foreveross.atwork.infrastructure.utils.JsonUtil;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.db.SQLiteDatabase;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by reyzhang22 on 16/4/5.
 */
public class DiscussionRepository extends W6sBaseRepository {

    private static final String TAG = DiscussionRepository.class.getSimpleName();

    private static DiscussionRepository sInstance = new DiscussionRepository();

    public static DiscussionRepository getInstance() {
        return sInstance;
    }

    private DiscussionRepository() {

    }

    /**
     * 查询所有群组
     * @return
     */
    public List<Discussion> queryAllDiscussions() {
        List<Discussion> discussionList = queryAllDiscussionBasicInfos();
        Collections.sort(discussionList);

        return discussionList;
    }

    @NotNull
    private List<Discussion> queryAllDiscussionBasicInfos() {
        List<Discussion> discussionList = new ArrayList<>();
        String sql = "select * from " + DiscussionDBHelper.TABLE_NAME;

        Cursor cursor = getWritableDatabase().rawQuery(sql, null);
        try {
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    Discussion discussion = DiscussionDBHelper.fromCursor(cursor);
                    discussionList.add(discussion);
                }

            }

        } finally {
            if (cursor != null) {
                cursor.close();
            }

        }
        return discussionList;
    }

    /**
     * 查询群组基本信息, 不带 members, template 等
     * @param discussionId
     *
     * */
    public Discussion queryDiscussionBasicInfo(String discussionId) {
        Discussion discussion = null;
        String sql = "select * from " + DiscussionDBHelper.TABLE_NAME + " where " + DiscussionDBHelper.DBColumn.DISCUSSION_ID  + " = ?";
        Cursor cursor = getWritableDatabase().rawQuery(sql, new String[]{discussionId});
        try {
            if (null != cursor && cursor.moveToNext()) {
                discussion = DiscussionDBHelper.fromCursor(cursor);
            }
        } finally {
            if (null != cursor) {
                cursor.close();
            }

        }
        return discussion;
    }

    /**
     * 根据orgid查询群组列表
     * @param orgId
     * @return
     */
    public List<Discussion> queryDiscussionsByOrgId(String orgId) {
        List<Discussion> discussions = new ArrayList<>();
        String sql = "select * from " + DiscussionDBHelper.TABLE_NAME + " where " + DiscussionDBHelper.DBColumn.ORG_ID  + " = ? or " + DiscussionDBHelper.DBColumn.OWNER_CODE + " = ?";
        Cursor cursor = getWritableDatabase().rawQuery(sql, new String[]{orgId, orgId});
        try {

            while (null != cursor && cursor.moveToNext()) {
                discussions.add(DiscussionDBHelper.fromCursor(cursor));
            }
        } finally {
            if (null != cursor) {
                cursor.close();
            }

        }
        return discussions;
    }

    /**
     * 查询群组, 包括群组 member
     * @param discussionId
     * @return
     */
    public Discussion queryDiscussionDetailInfo(String discussionId) {
        Discussion discussion = queryDiscussionBasicInfo(discussionId);
        if (discussion != null) {
            discussion.mMemberList = new CopyOnWriteArrayList<>(DiscussionMemberRepository.getInstance().queryDiscussionMembersById(discussionId));
        }
        return discussion;
    }


    /**
     * 插入一个群组, 包括群成员的插入

     * @param discussion
     * @return
     */
    public boolean insertDiscussion(Discussion discussion) {
        boolean result;

        result = insertDiscussionBasicInfo(discussion);

        if(result) {
            //赋予 member 加上 discussionId
            if (!ListUtil.isEmpty(discussion.mMemberList)) {
                for(DiscussionMember member : discussion.mMemberList) {
                    member.discussionId = discussion.mDiscussionId;
                }
            }

            DiscussionMemberRepository.getInstance().removeAllDiscussionMembers(discussion.mDiscussionId);
            DiscussionMemberRepository.getInstance().batchInsertDiscussionMembers(discussion.mMemberList);

            DiscussionCache.getInstance().setDiscussionCache(discussion.mDiscussionId, discussion);
        }

        return result;
    }

    public boolean insertDiscussionBasicInfo(Discussion discussion) {
        boolean result;
        long insertResult = getWritableDatabase().insertWithOnConflict(
                                                            DiscussionDBHelper.TABLE_NAME,
                                                            null,
                                                            DiscussionDBHelper.getContentValues(discussion),
                                                            SQLiteDatabase.CONFLICT_REPLACE
        );

        result = (-1 != insertResult);

        if(result) {
            DiscussionCache.getInstance().setDiscussionCache(discussion.mDiscussionId, discussion);
        }
        return result;
    }


    /**
     * 修改群组信息, 但不修改群组成员
     * @param discussion
     * @return
     * */
    public boolean modifyDiscussion(Discussion discussion) {
        long updateResult = getWritableDatabase().insertWithOnConflict(
                DiscussionDBHelper.TABLE_NAME,
                null,
                DiscussionDBHelper.getContentValues(discussion),
                SQLiteDatabase.CONFLICT_REPLACE
        );

        return -1 != updateResult;
    }

    /**
     * 批量插入群组
     * @param discussionList
     * @return
     */
    public boolean batchInsertDiscussions(List<Discussion> discussionList) {
        boolean result = true;
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        try {
            sqLiteDatabase.beginTransaction();

            for (Discussion discussion : discussionList) {
                insertDiscussion(discussion);

            }
            sqLiteDatabase.setTransactionSuccessful();

        } catch (Exception e) {
            e.printStackTrace();
            result = false;

        } finally {
            sqLiteDatabase.endTransaction();
        }
        return result;
    }

    public boolean removeAllDiscussions() {
        return removeAll(DiscussionDBHelper.TABLE_NAME);
    }

    public boolean removeDiscussion(String discussionId) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        int deletedCount = sqLiteDatabase.delete(DiscussionDBHelper.TABLE_NAME, DiscussionDBHelper.DBColumn.DISCUSSION_ID + "=?", new String[]{ discussionId });
        return 0 != deletedCount;
    }

    public boolean removeDiscussionByOrgId(String orgId) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        int deletedCount = sqLiteDatabase.delete(DiscussionDBHelper.TABLE_NAME, DiscussionDBHelper.DBColumn.ORG_ID + "=?", new String[]{orgId});
        return 0 != deletedCount;
    }

    /**
     * 群组搜索
     *
     * @param searchValue
     * @return
     */
    public List<Discussion> searchDiscussion(final String searchValue) {
        String sql = "select * from " + DiscussionDBHelper.TABLE_NAME + " where " + DiscussionDBHelper.DBColumn.NAME + " like ? or " + DiscussionDBHelper.DBColumn.PINYIN + " like ? or " + DiscussionDBHelper.DBColumn.INITIAL + " like ?";
        Cursor cursor = null;
        List<Discussion> discussions = new ArrayList<>();
        try {
            cursor = getReadableDatabase().rawQuery(sql, new String[]{"%" + searchValue + "%", "%" + searchValue + "%", "%" + searchValue + "%"});
            while (cursor.moveToNext()) {

                discussions.add(DiscussionDBHelper.fromCursor(cursor));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return discussions;
    }

    public List<Discussion> searchDiscussionInfo(final String searchValue) {
        List<Discussion> discussionNameList = searchDiscussion(searchValue);
        Map<String, DiscussionMember> memberNameMap = DiscussionMemberRepository.getInstance().queryDiscussionMembersByKey(searchValue);
        Map<String, Discussion> resultList = new HashMap<>();
        for(Discussion discussion : discussionNameList) {
            resultList.put(discussion.mDiscussionId, discussion);
        }
        for(String discussionId : memberNameMap.keySet()) {
            if (resultList.containsKey(discussionId)) {
                CopyOnWriteArrayList<DiscussionMember> members = new CopyOnWriteArrayList<>();
                members.add(memberNameMap.get(discussionId));
                Objects.requireNonNull(resultList.get(discussionId)).mMemberList = members;
            } else {
                Discussion discussion = queryDiscussionBasicInfo(discussionId);
                if (discussion != null) {
                    CopyOnWriteArrayList<DiscussionMember> members = new CopyOnWriteArrayList<>();
                    members.add(memberNameMap.get(discussionId));
                    discussion.mMemberList = members;
                    resultList.put(discussion.mDiscussionId, discussion);
                }

            }
        }
        return new ArrayList<>(resultList.values());
    }


    /**
     * 更新群头像
     *
     * @param discussionId
     * @param mediaId
     * @return
     */
    public boolean updateDiscussionAvatar(final String discussionId, final String mediaId) {
        String sql = "update " + DiscussionDBHelper.TABLE_NAME + " set " + DiscussionDBHelper.DBColumn.AVATAR +" = ? where " + DiscussionDBHelper.DBColumn.DISCUSSION_ID + " = ?";
        getWritableDatabase().execSQL(sql, new String[]{mediaId, discussionId});
        return true;
    }

    /**
     * 更新群名称
     * @param discussionId
     * @param name
     * @return
     */
    public boolean updateDiscussionName(final String discussionId, final String name, @Nullable final String pinyin, @Nullable final String initial) {
        if (!StringUtils.isEmpty(name) && !StringUtils.isEmpty(pinyin) && !StringUtils.isEmpty(initial)) {
            String sql = "update " + DiscussionDBHelper.TABLE_NAME + " set " + DiscussionDBHelper.DBColumn.NAME + " = ? and " + DiscussionDBHelper.DBColumn.PINYIN + " = ? and "  + DiscussionDBHelper.DBColumn.INITIAL + " = ? "  + "where " + DiscussionDBHelper.DBColumn.DISCUSSION_ID + " = ?";
            getWritableDatabase().execSQL(sql, new String[]{name, pinyin, initial, discussionId});

        } else {
            String sql = "update " + DiscussionDBHelper.TABLE_NAME + " set " + DiscussionDBHelper.DBColumn.NAME +" = ? where " + DiscussionDBHelper.DBColumn.DISCUSSION_ID + " = ?";
            getWritableDatabase().execSQL(sql, new String[]{name, discussionId});

        }


        return true;
    }


    public boolean updateDiscussionOwner(String discussionId, @Nullable DiscussionOwner newOwner) {
        String sql = "update " + DiscussionDBHelper.TABLE_NAME + " set " + DiscussionDBHelper.DBColumn.OWNER + " = ? where " + DiscussionDBHelper.DBColumn.DISCUSSION_ID + " = ?";
        String json = null;
        if (null != newOwner) {
            json = JsonUtil.toJson(newOwner);
        }
        getWritableDatabase().execSQL(sql, new String[]{json, discussionId});

        Discussion cache = DiscussionCache.getInstance().getDiscussionCache(discussionId);
        if(null != cache) {
            cache.mOwner = newOwner;
        }

        return true;
    }

    public int queryDiscussionCount() {
        String querySQL = "select count(*) as count from " + DiscussionDBHelper.TABLE_NAME;

        int count = 0;
        Cursor cursor = null;
        try {
            cursor = getReadableDatabase().rawQuery(querySQL, new String[]{});
            if (cursor.moveToNext()) {
                count = cursor.getInt(cursor.getColumnIndex("count"));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return count;

    }

}
