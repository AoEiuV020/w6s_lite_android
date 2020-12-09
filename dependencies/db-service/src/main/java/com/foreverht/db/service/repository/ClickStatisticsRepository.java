package com.foreverht.db.service.repository;

import android.database.Cursor;

import com.foreverht.db.service.W6sBaseRepository;
import com.foreverht.db.service.dbHelper.ClickStatisticsDBHelper;
import com.foreveross.atwork.infrastructure.model.clickStatistics.ClickEvent;
import com.foreveross.atwork.infrastructure.model.clickStatistics.Type;
import com.foreveross.atwork.infrastructure.utils.LogUtil;
import com.foreveross.db.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class ClickStatisticsRepository extends W6sBaseRepository {

    private static ClickStatisticsRepository sInstance = new ClickStatisticsRepository();

    public static ClickStatisticsRepository getInstance() {
        return sInstance;
    }


    public List<ClickEvent> queryClickEvents(Type type, long afterTime) {
        String sql = "select * from " + ClickStatisticsDBHelper.TABLE_NAME + " where "  + ClickStatisticsDBHelper.DBColumn.TYPE + " = ? and " + ClickStatisticsDBHelper.DBColumn.BEGIN + " > " + afterTime;

        List<ClickEvent> clickEvents = new ArrayList<>();
        Cursor cursor = null;
        try {

            cursor = getReadableDatabase().rawQuery(sql, new String[]{type.initValue() + ""});
            while (cursor.moveToNext()) {
                ClickEvent clickEvent = ClickStatisticsDBHelper.fromCursor(cursor);

                clickEvents.add(clickEvent);
            }

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return clickEvents;

    }

    public List<ClickEvent> queryClickEventsForNS(Type type) {
        String sql = "select * from " + ClickStatisticsDBHelper.TABLE_NAME + " where "  + ClickStatisticsDBHelper.DBColumn.TYPE + " = ? ";

        List<ClickEvent> clickEvents = new ArrayList<>();
        Cursor cursor = null;
        try {

            cursor = getReadableDatabase().rawQuery(sql, new String[]{type.initValue() + ""});
            while (cursor.moveToNext()) {
                ClickEvent clickEvent = ClickStatisticsDBHelper.fromCursor(cursor);

                clickEvents.add(clickEvent);
            }

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return clickEvents;

    }



    public boolean updateClick(ClickEvent clickEvent) {

        long insertResult = getWritableDatabase().insertWithOnConflict(ClickStatisticsDBHelper.TABLE_NAME, null, ClickStatisticsDBHelper.getContentValues(clickEvent), SQLiteDatabase.CONFLICT_REPLACE);

        LogUtil.e("insertResult-> " + insertResult + " updateClickUp id -> " + clickEvent.getId() + " type -> " + clickEvent.getType() + "  begin -> " + clickEvent.getBegin());


        return -1 != insertResult;
    }
}
