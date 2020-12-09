package com.foreverht.db.service.repository;

import android.database.Cursor;

import com.foreverht.db.service.W6sBaseRepository;
import com.foreverht.db.service.dbHelper.DiscussionMemberDBHelper;
import com.foreveross.atwork.infrastructure.model.discussion.DiscussionMember;

import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.db.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dasunsy on 16/4/20.
 */
public class DiscussionMemberRepository extends W6sBaseRepository {
    private static DiscussionMemberRepository sInstance = new DiscussionMemberRepository();

    private DiscussionMemberRepository() {

    }

    public static DiscussionMemberRepository getInstance() {
        return sInstance;
    }

    /**
     * 查询这个群组下的成员列表
     * @param discussionId
     * @return
     */
    public List<DiscussionMember> queryDiscussionMembersById(String discussionId) {
        String sql = "select * from " + DiscussionMemberDBHelper.TABLE_NAME + " where " + DiscussionMemberDBHelper.DBColumn.DISCUSSION_ID + " = ?";
        List<DiscussionMember> discussionMembers = new ArrayList<>();
        Cursor cursor = getWritableDatabase().rawQuery(sql, new String[]{discussionId});
        try {
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    DiscussionMember discussionMember = DiscussionMemberDBHelper.fromCursor(cursor);
                    discussionMembers.add(discussionMember);
                }

            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return discussionMembers;
    }

    public Map<String, DiscussionMember> queryDiscussionMembersByKey(String keyword) {
        String sql = "select * from " + DiscussionMemberDBHelper.TABLE_NAME + " where " + DiscussionMemberDBHelper.DBColumn.NAME + " like ?";
        Map<String, DiscussionMember> discussionMembers = new HashMap<>();
        Cursor cursor = getWritableDatabase().rawQuery(sql, new String[]{"%" + keyword + "%"});
        try {
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    DiscussionMember discussionMember = DiscussionMemberDBHelper.fromCursor(cursor);
                    if (!discussionMembers.containsKey(discussionMember.discussionId)) {
                        discussionMembers.put(discussionMember.discussionId, discussionMember);
                    }
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return discussionMembers;
    }

    /**
     * 插入群组成员
     * @param discussionMember
     * @return
     */
    public boolean insertDiscussionMember(DiscussionMember discussionMember) {
        long insertResult = getWritableDatabase().insertWithOnConflict(
                DiscussionMemberDBHelper.TABLE_NAME,
                null,
                DiscussionMemberDBHelper.getContentValues(discussionMember),
                SQLiteDatabase.CONFLICT_REPLACE
        );
        return  (-1 != insertResult);
    }

    /**
     * 批量插入群组成员
     * @param discussionMemberList
     * @return
     */
    public boolean batchInsertDiscussionMembers(List<DiscussionMember> discussionMemberList) {
        boolean result = true;
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        try {
            sqLiteDatabase.beginTransaction();

            for (DiscussionMember discussionMember : discussionMemberList) {
                getWritableDatabase().insertWithOnConflict(
                        DiscussionMemberDBHelper.TABLE_NAME,
                        null,
                        DiscussionMemberDBHelper.getContentValues(discussionMember), SQLiteDatabase.CONFLICT_REPLACE);
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

    public boolean batchUpdateDiscussionMembersTags(List<DiscussionMember> discussionMemberList) {
        boolean result = true;
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        try {
            sqLiteDatabase.beginTransaction();

            for (DiscussionMember discussionMember : discussionMemberList) {
                updateDiscussionMemberTags(sqLiteDatabase, discussionMember);
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

    public static boolean updateDiscussionMemberTags(SQLiteDatabase sqLiteDatabase, DiscussionMember member) {

        String sql = "update " + DiscussionMemberDBHelper.TABLE_NAME + " set " + DiscussionMemberDBHelper.DBColumn.TAGS + " = ? where " + DiscussionMemberDBHelper.DBColumn.USER_ID + " = ? and " + DiscussionMemberDBHelper.DBColumn.DISCUSSION_ID + " = ?";
        sqLiteDatabase.execSQL(sql, new String[]{StringUtils.appendSeparatorStr(member.tags, ","), member.userId, member.discussionId});
        return true;
    }

    public boolean removeAllDiscussionMembers(String discussionId) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        int deletedCount = sqLiteDatabase.delete(DiscussionMemberDBHelper.TABLE_NAME, DiscussionMemberDBHelper.DBColumn.DISCUSSION_ID + " = ?", new String[]{ discussionId });
        return 0 != deletedCount;
    }

    public boolean removeDiscussionMember(String discussionId, String userId) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        int deletedCount = sqLiteDatabase.delete(DiscussionMemberDBHelper.TABLE_NAME, DiscussionMemberDBHelper.DBColumn.DISCUSSION_ID + " = ? and " + DiscussionMemberDBHelper.DBColumn.USER_ID + " = ?", new String[]{ discussionId,  userId});

        return 0 != deletedCount;
    }
}
