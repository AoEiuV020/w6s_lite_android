package com.foreverht.db.service.repository;

import android.database.Cursor;
import androidx.annotation.NonNull;
import android.text.TextUtils;

import com.foreverht.db.service.W6sBaseRepository;
import com.foreverht.db.service.dbHelper.OrgRelationShipDbHelper;
import com.foreveross.atwork.infrastructure.model.orgization.OrgRelationship;

import com.foreveross.db.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by shadow on 2016/5/12.
 */
public class OrgRelationShipRepository extends W6sBaseRepository {

    private static final String TAG = OrgRelationShipRepository.class.getSimpleName();

    private static OrgRelationShipRepository sInstance = new OrgRelationShipRepository();

    public static OrgRelationShipRepository getInstance() {
        return sInstance;
    }

    private OrgRelationShipRepository() {
    }


    /**
     * 查询用户对应有存在哪些组织(雇员关系)
     *
     * @param userId
     * @return 组织 code list
     */
    @NonNull
    public List<String> queryOrgCodeList(String userId) {
        List<String> orgCodeList = new ArrayList<>();
        if (TextUtils.isEmpty(userId)) {
            return orgCodeList;
        }
        String sql = "select * from " + OrgRelationShipDbHelper.TABLE_NAME + " where " + OrgRelationShipDbHelper.DBColumn.USER_ID + "=? and " + OrgRelationShipDbHelper.DBColumn.TYPE + "=" + OrgRelationship.Type.EMPLOYEE;

        Cursor cursor = getWritableDatabase().rawQuery(sql, new String[]{userId});
        if (cursor != null) {
            while (cursor.moveToNext()) {
                OrgRelationship orgRelationship = OrgRelationShipDbHelper.fromCursor(cursor);
                orgCodeList.add(orgRelationship.mOrgCode);
            }
            cursor.close();
        }

        return orgCodeList;

    }

    /**
     * 根据类型返回关系 list
     * @param type
     *
     * return orgRelationshipList
     * */
    @NonNull
    public List<OrgRelationship> queryRelationList(int type) {
        List<OrgRelationship> orgRelationshipList = new ArrayList<>();

        String sql = "select * from " + OrgRelationShipDbHelper.TABLE_NAME + " where " + OrgRelationShipDbHelper.DBColumn.TYPE + "=?";
        Cursor cursor = getWritableDatabase().rawQuery(sql, new String[]{String.valueOf(type)});

        try {
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    OrgRelationship orgRelationship = OrgRelationShipDbHelper.fromCursor(cursor);
                    orgRelationshipList.add(orgRelationship);
                }

            }
        } finally {

            if(null != cursor) {
                cursor.close();
            }
        }
        return orgRelationshipList;
    }


    public HashMap<String, String> queryAdminRelationshipList(List<String> orgCodeList) {
        HashMap<String, String> orgRelationshipMap = new HashMap<>();
        String sql = "select * from " + OrgRelationShipDbHelper.TABLE_NAME + " where " + OrgRelationShipDbHelper.DBColumn.ORG_CODE + " in (" + getInStringParams(orgCodeList) + ") and " + OrgRelationShipDbHelper.DBColumn.TYPE + "=" + OrgRelationship.Type.ADMIN;
        SQLiteDatabase db = getWritableDatabase();

        Cursor cursor = null;
        try {
            cursor = db.rawQuery(sql, new String[]{});
            while (null != cursor && cursor.moveToNext()) {
                OrgRelationship orgRelationship = OrgRelationShipDbHelper.fromCursor(cursor);
                orgRelationshipMap.put(orgRelationship.mOrgCode, orgRelationship.mUserId);
            }
        } finally {
            if (null != cursor) {
                cursor.close();
            }
        }

        return orgRelationshipMap;
    }

    /**
     * 查询管理着的群组
     * */
    @NonNull
    public List<String> queryAdminRelationshipList(String userId) {
        List<String> adminOrgCodeList = new ArrayList<>();
        String sql = "select * from " + OrgRelationShipDbHelper.TABLE_NAME + " where " + OrgRelationShipDbHelper.DBColumn.USER_ID + "=?" + " and " + OrgRelationShipDbHelper.DBColumn.TYPE + "=" + OrgRelationship.Type.ADMIN;
        SQLiteDatabase db = getWritableDatabase();

        Cursor cursor = null;
        try {
            cursor = db.rawQuery(sql, new String[]{userId});
            while (null != cursor && cursor.moveToNext()) {
                OrgRelationship orgRelationship = OrgRelationShipDbHelper.fromCursor(cursor);

                adminOrgCodeList.add(orgRelationship.mOrgCode);
            }
        } finally {
            if (null != cursor) {
                cursor.close();
            }
        }

        return adminOrgCodeList;
    }

    /**
     * 向数据库插入OrgRelationship
     *
     * @param orgRelationship
     * @return
     */
    public boolean insertOrUpdateOrgRelation(OrgRelationship orgRelationship) {
        boolean result;
        long insertResult = getWritableDatabase().insertWithOnConflict(
                OrgRelationShipDbHelper.TABLE_NAME,
                null,
                OrgRelationShipDbHelper.getContentValue(orgRelationship),
                SQLiteDatabase.CONFLICT_REPLACE
        );

        result = -1 != insertResult;
        return result;
    }

    /**
     * 批量更新组织关系, 不清楚数据
     */
    public boolean batchInsertRelation(List<OrgRelationship> orgRelationshipList) {
        boolean result = true;
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        try {
            sqLiteDatabase.beginTransaction();

            for (OrgRelationship relationship : orgRelationshipList) {
                insertOrUpdateOrgRelation(relationship);
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

    /**
     * 更新组织关系, 会清除就数据
     *
     * @param userId
     * @param deletedType         需要删除的类型, -1表示所有类型都删除
     * @param orgRelationshipList
     */
    public boolean batchInsertOrgRelationAndClean(String userId, int deletedType, List<OrgRelationship> orgRelationshipList) {
        boolean result = true;
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        try {
            sqLiteDatabase.beginTransaction();
            removeOrgRelationList(userId, deletedType);

            for (OrgRelationship relationship : orgRelationshipList) {
                insertOrUpdateOrgRelation(relationship);
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

    /**
     * 根据类型 删除 org relation
     *
     * @param userId
     * @param type   若 type -1 则全部删除
     */
    public boolean removeOrgRelationList(String userId, int type) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        int deletedCount;
        if (-1 == type) {
            deletedCount = sqLiteDatabase.delete(OrgRelationShipDbHelper.TABLE_NAME, OrgRelationShipDbHelper.DBColumn.USER_ID + " = ?", new String[]{userId});

        } else {
            deletedCount = sqLiteDatabase.delete(OrgRelationShipDbHelper.TABLE_NAME, OrgRelationShipDbHelper.DBColumn.USER_ID + " = ? and " + OrgRelationShipDbHelper.DBColumn.TYPE + " = ?", new String[]{userId, String.valueOf(type)});

        }

        return 0 != deletedCount;
    }

    /**
     * 根据orgCodeList 删除关系
     *
     * @param userId
     * @param type           若 type -1 则删除根据条件所以类型的
     * @param orgRemovedList
     */
    public boolean removeOrgRelationList(String userId, int type, List<String> orgRemovedList) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        int deletedCount;
        if (-1 == type) {
            deletedCount = sqLiteDatabase.delete(OrgRelationShipDbHelper.TABLE_NAME, OrgRelationShipDbHelper.DBColumn.USER_ID + " = ? and " + OrgRelationShipDbHelper.DBColumn.ORG_CODE + " in (" + getInStringParams(orgRemovedList) + ")", new String[]{userId});

        } else {
            deletedCount = sqLiteDatabase.delete(OrgRelationShipDbHelper.TABLE_NAME, OrgRelationShipDbHelper.DBColumn.USER_ID + " = ? and " + OrgRelationShipDbHelper.DBColumn.TYPE + " = ? and " + OrgRelationShipDbHelper.DBColumn.ORG_CODE + " in (" + getInStringParams(orgRemovedList) + ")", new String[]{userId, String.valueOf(type)});

        }

        return 0 != deletedCount;
    }

    /**
     * 根据 userId 返回 机构数量
     */
    public int queryOrgCount(String userId) {
        String querySQL = "select count(*) as count from " + OrgRelationShipDbHelper.TABLE_NAME + " where " + OrgRelationShipDbHelper.DBColumn.USER_ID + " = ? and " + OrgRelationShipDbHelper.DBColumn.TYPE + " = ?";

        int count = 0;
        Cursor cursor = null;
        try {
            cursor = getReadableDatabase().rawQuery(querySQL, new String[]{userId, String.valueOf(OrgRelationship.Type.EMPLOYEE)});
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
