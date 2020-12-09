package com.foreverht.db.service.repository;

import android.database.Cursor;

import com.foreverht.db.service.W6sBaseRepository;
import com.foreverht.db.service.dbHelper.OrgApplyDBHelper;
import com.foreveross.atwork.infrastructure.model.orgization.OrgApply;

import com.foreveross.db.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

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
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ __           __
 * .--.--.--.-----.----.|  |--.-----.|  |.--.--.-----.
 * |  |  |  |  _  |   _||    <|  _  ||  ||  |  |__ --|
 * |________|_____|__|  |__|__|   __||__||_____|_____|
 * |__|
 * Created by reyzhang22 on 16/8/16.
 */
public class OrgApplyRepository extends W6sBaseRepository {

    private static final String TAG = OrgApplyRepository.class.getSimpleName();

    private final static OrgApplyRepository sInstance = new OrgApplyRepository();

    private OrgApplyRepository() {
    }

    public static OrgApplyRepository getInstance() {
        return sInstance;
    }

    /**
     * 插入或更新一个组织申请对象
     *
     * @param apply
     * @return
     */
    public boolean saveOrUpdateOrgApply(OrgApply apply) {
        boolean result;
        long insertResult = getWritableDatabase().insertWithOnConflict(
                OrgApplyDBHelper.TABLE_NAME,
                null,
                OrgApplyDBHelper.getContentValues(apply),
                SQLiteDatabase.CONFLICT_REPLACE
        );

        result = -1 != insertResult;
        return result;
    }

    /**
     * 根据某个组织code查询相应的组织申请
     *
     * @param orgCode
     * @return
     */
    public OrgApply queryOrgApplyByOrgCode(String orgCode) {
        String querySQL = "select * from " + OrgApplyDBHelper.TABLE_NAME + " where " + OrgApplyDBHelper.DBColumn.ORG_CODE + " = ?";
        OrgApply orgApply = null;
        Cursor cursor = null;

        try {
            cursor = getReadableDatabase().rawQuery(querySQL, new String[]{orgCode});
            if (cursor != null) {
                orgApply = OrgApplyDBHelper.fromCursor(cursor);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return orgApply;
    }


    /**
     * 查询所有组织申请
     *
     * @return
     */
    public List<OrgApply> getAllOrgApply() {
        String querySQL = "select * from " + OrgApplyDBHelper.TABLE_NAME + " order by " + OrgApplyDBHelper.DBColumn.LAST_MSG_TIME + " desc";
        List<OrgApply> allList = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = getReadableDatabase().rawQuery(querySQL, new String[]{});
            while (cursor.moveToNext()) {
                OrgApply orgApply = OrgApplyDBHelper.fromCursor(cursor);
                allList.add(orgApply);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return allList;
    }

    public boolean removeOrgApply(String orgCode) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        int deletedCount;

        deletedCount = sqLiteDatabase.delete(OrgApplyDBHelper.TABLE_NAME, OrgApplyDBHelper.DBColumn.ORG_CODE + " = ?", new String[]{orgCode});


        return 0 != deletedCount;
    }


    /**
     * 根据orgCode删除组织申请
     *
     * @param orgCode
     * @return
     */
    public boolean removeOrgApplyByOrgCode(String orgCode) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        int deletedCount = sqLiteDatabase.delete(OrgApplyDBHelper.TABLE_NAME, OrgApplyDBHelper.DBColumn.ORG_CODE + " = ? ", new String[]{orgCode});
        return 0 != deletedCount;
    }


}
