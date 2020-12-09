package com.foreverht.db.service.repository;

import android.database.Cursor;

import com.foreverht.db.service.W6sBaseRepository;
import com.foreverht.db.service.dbHelper.AppDBHelper;
import com.foreverht.db.service.dbHelper.SessionDBHelper;
import com.foreverht.db.service.dbHelper.SessionFaultageRecordDBHelper;
import com.foreveross.atwork.infrastructure.model.Session;
import com.foreveross.atwork.infrastructure.model.SessionFaultage;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.db.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class SessionFaultageRecordRepository extends W6sBaseRepository {



    public static boolean batchInsertNewFaultages(List<SessionFaultage> sessionFaultages) {

        if(ListUtil.isEmpty(sessionFaultages)) {
            return false;
        }

        boolean result = false;
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        try {
            sqLiteDatabase.beginTransaction();

            for (SessionFaultage sessionFaultage : sessionFaultages) {
                getWritableDatabase().insertWithOnConflict(
                        SessionFaultageRecordDBHelper.TABLE_NAME,
                        null,
                        SessionFaultageRecordDBHelper.getContentValue(sessionFaultage), SQLiteDatabase.CONFLICT_REPLACE);
            }
            sqLiteDatabase.setTransactionSuccessful();

            result = true;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            sqLiteDatabase.endTransaction();
        }
        return result;

    }


    /**
     * 清除断层
     * */
    public static boolean deleteFaultage(String sessionId) {

        String whereClause = SessionFaultageRecordDBHelper.DBColumn.SESSION_ID + " = ?";

        long result = getWritableDatabase().delete(SessionFaultageRecordDBHelper.TABLE_NAME, whereClause, new String[]{sessionId});
        return 0 < result;
    }


    public static boolean deleteFaultage(String sessionId, long beginTimeInSyncMessages, long endTimeInSyncMessages) {
        String whereClause = SessionFaultageRecordDBHelper.DBColumn.SESSION_ID + " = ? and " + SessionFaultageRecordDBHelper.DBColumn.LAST_END_TIME + " >= ? " + " and " + SessionFaultageRecordDBHelper.DBColumn.BEGIN_TIME + " <= ?" ;

        long result = getWritableDatabase().delete(SessionFaultageRecordDBHelper.TABLE_NAME, whereClause, new String[]{sessionId, beginTimeInSyncMessages + "", endTimeInSyncMessages + ""});
        return 0 < result;
    }



    public static List<SessionFaultage> querySessionFaultages(String sessionId) {
        List<SessionFaultage> sessionFaultages = new ArrayList<>();

        Cursor cursor = null;
        String sql = "select * from " + SessionFaultageRecordDBHelper.TABLE_NAME + " where " + SessionFaultageRecordDBHelper.DBColumn.SESSION_ID + " = ?";
        try {
            cursor = getReadableDatabase().rawQuery(sql, new String[]{sessionId});
            while (cursor.moveToNext()) {
                SessionFaultage sessionFaultage = SessionFaultageRecordDBHelper.fromCursor(cursor);
                sessionFaultages.add(sessionFaultage);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return sessionFaultages;
    }


}
