package com.foreverht.db.service.repository;/**
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
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ __           __
 * .--.--.--.-----.----.|  |--.-----.|  |.--.--.-----.
 * |  |  |  |  _  |   _||    <|  _  ||  ||  |  |__ --|
 * |________|_____|__|  |__|__|   __||__||_____|_____|
 * |__|
 */


import android.database.Cursor;
import androidx.annotation.Nullable;
import android.text.TextUtils;

import com.foreverht.db.service.W6sBaseRepository;
import com.foreverht.db.service.dbHelper.RelationshipDBHelper;
import com.foreveross.atwork.infrastructure.model.Relationship;
import com.foreveross.atwork.infrastructure.utils.ListUtil;

import com.foreveross.db.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by reyzhang22 on 16/4/5.
 */
public class RelationshipRepository extends W6sBaseRepository {

    private static final String TAG = RelationshipRepository.class.getSimpleName();

    private static RelationshipRepository sInstance = new RelationshipRepository();

    private RelationshipRepository() {

    }

    public static RelationshipRepository getInstance() {
        return sInstance;
    }

    @Nullable
    public Relationship queryRelationShip(String userId, String type) {
        Relationship relationship = null;
        String sql = "select * from " + RelationshipDBHelper.TABLE_NAME + " where " + RelationshipDBHelper.DBColumn.USER_ID + " = ? and " + RelationshipDBHelper.DBColumn.TYPE + " = ?";
        String[] sqlValues = {userId, type};
        List<Relationship> relationshipList = queryRelationShipBasic(sql, sqlValues);

        if (!ListUtil.isEmpty(relationshipList)) {
            relationship = relationshipList.get(0);
        }

        return relationship;
    }

    /**
     * 查询所有关系表
     * @return
     */
    public List<Relationship> queryAllRelationShips() {
        String sql = "select * from " + RelationshipDBHelper.TABLE_NAME;
        return queryRelationShipBasic(sql, null);
    }

    /**
     * 查询星标联系人
     * @return
     */
    public List<Relationship> queryStarRelationShip() {
        String sql = "select * from " + RelationshipDBHelper.TABLE_NAME + " where " +
                RelationshipDBHelper.DBColumn.TYPE + " = ?";

        return queryRelationShipBasic(sql, new String[]{String.valueOf(Relationship.Type.STAR)});
    }

    public List<Relationship> queryFriendRelationShip() {
        String sql = "select * from " + RelationshipDBHelper.TABLE_NAME + " where " +
                RelationshipDBHelper.DBColumn.TYPE + " = ?";
        return queryRelationShipBasic(sql, new String[]{String.valueOf(Relationship.Type.FRIEND)});
    }

    private List<Relationship> queryRelationShipBasic(String sql, String[] query) {
        List<Relationship> relationships = new ArrayList<>();

        Cursor cursor = getReadableDatabase().rawQuery(sql, query);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Relationship relationship = RelationshipDBHelper.fromCursor(cursor);
                relationships.add(relationship);
            }
            cursor.close();
        }
        return relationships;
    }

    public boolean addFlagRelationShip(String userId) {
        Relationship relationship = new Relationship();
        relationship.mUserId = userId;
        relationship.mType = Relationship.Type.STAR;
        return insertRelationship(relationship);
    }

    /**
     * 插入一个关系
     * @param relationship
     * @return
     */
    public boolean insertRelationship(Relationship relationship) {
        long insertResult = getWritableDatabase().insertWithOnConflict(
                RelationshipDBHelper.TABLE_NAME,
                null,
                RelationshipDBHelper.getContentValues(relationship),
                SQLiteDatabase.CONFLICT_REPLACE
        );
        return insertResult != -1;
    }

    public boolean removeFriendRelationShip(String userId) {
        return removeRelationship(userId, String.valueOf(Relationship.Type.FRIEND));
    }

    public boolean removeFlagRelationShip(String userId) {
        return removeRelationship(userId, String.valueOf(Relationship.Type.STAR));
    }

    /**
     * 删除关系
     * @param userId
     * @param type
     * @return
     * */
    public boolean removeRelationship(String userId, String type) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        int deletedCount = sqLiteDatabase.delete(RelationshipDBHelper.TABLE_NAME, RelationshipDBHelper.DBColumn.USER_ID + " = ? and " + RelationshipDBHelper.DBColumn.TYPE + " = ?", new String[]{userId, type});

        return 0 != deletedCount;
    }

    /**
     * 根据类型删除关系
     * @param type
     * */
    public boolean removeRelationships(String type) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        int deletedCount = sqLiteDatabase.delete(RelationshipDBHelper.TABLE_NAME, RelationshipDBHelper.DBColumn.TYPE + " = ?", new String[]{type});

        return 0 != deletedCount;
    }

    /**
     * 清空所有关系
     * */
    public boolean removeAllRelationship() {
        return removeAll(RelationshipDBHelper.TABLE_NAME);
    }

    /**
     * 批量插入人员关系
     * @param relationships
     * @return
     */
    public boolean batchInsertRelationShips(List<Relationship> relationships) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.beginTransaction();

        for (Relationship relationship : relationships) {
            if (TextUtils.isEmpty(relationship.mUserId)) {
                continue;
            }
            getWritableDatabase().insertWithOnConflict(
                    RelationshipDBHelper.TABLE_NAME,
                    RelationshipDBHelper.DBColumn.USER_ID,
                    RelationshipDBHelper.getContentValues(relationship),
                    SQLiteDatabase.CONFLICT_REPLACE);
        }
        sqLiteDatabase.setTransactionSuccessful();
        sqLiteDatabase.endTransaction();
        return true;
    }


    public int queryFriendCount() {
        String querySQL = "select count(*) as count from " + RelationshipDBHelper.TABLE_NAME + " where " +
                RelationshipDBHelper.DBColumn.TYPE + " = ?";

        int count = 0;
        Cursor cursor = null;
        try {
            cursor = getReadableDatabase().rawQuery(querySQL, new String[]{String.valueOf(Relationship.Type.FRIEND)});
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
