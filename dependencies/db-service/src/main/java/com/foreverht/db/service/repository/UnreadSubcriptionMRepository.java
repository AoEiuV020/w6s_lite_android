package com.foreverht.db.service.repository;

import android.database.Cursor;

import com.foreverht.db.service.W6sBaseRepository;
import com.foreverht.db.service.dbHelper.UnreadSubcriptionMessageDBHelper;
import com.foreveross.atwork.infrastructure.model.newsSummary.UnreadNewsSummaryData;
import com.foreveross.db.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class UnreadSubcriptionMRepository extends W6sBaseRepository {
    private static final UnreadSubcriptionMRepository unreadSubcriptionMRepository = new UnreadSubcriptionMRepository();
    public static UnreadSubcriptionMRepository getInstance() {
        return unreadSubcriptionMRepository;
    }

    public List<UnreadNewsSummaryData> queryByAppId(String appId) {
        String sql = "";
        Cursor cursor ;
        List<UnreadNewsSummaryData> unreadNewsSummaryDataList = new ArrayList<>();
        sql = "select * from " + UnreadSubcriptionMessageDBHelper.TABLE_NAME + " where app_id_ = ?";
        cursor = getWritableDatabase().rawQuery(sql, new String[]{appId});
        try {
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    UnreadNewsSummaryData unreadNewsSummaryData = UnreadSubcriptionMessageDBHelper.fromCursor(cursor);
                    unreadNewsSummaryDataList.add(unreadNewsSummaryData);
                }

            }

        } finally {
            if (cursor != null) {
                cursor.close();
            }

        }

        Collections.sort(unreadNewsSummaryDataList, new Comparator<UnreadNewsSummaryData>() {
            @Override
            public int compare(UnreadNewsSummaryData t2, UnreadNewsSummaryData t1) {
                return Long.compare(Long.valueOf(t1.deliveryTime), Long.valueOf(t2.deliveryTime));
            }
        });

        return unreadNewsSummaryDataList;
    }

    public List<UnreadNewsSummaryData> queryByMsgId(String msgId) {
        String sql = "";
        Cursor cursor ;
        List<UnreadNewsSummaryData> unreadNewsSummaryDataList = new ArrayList<>();
        sql = "select * from " + UnreadSubcriptionMessageDBHelper.TABLE_NAME + " where msg_id_ = ?";
        cursor = getWritableDatabase().rawQuery(sql, new String[]{msgId});
        try {
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    UnreadNewsSummaryData unreadNewsSummaryData = UnreadSubcriptionMessageDBHelper.fromCursor(cursor);
                    unreadNewsSummaryDataList.add(unreadNewsSummaryData);
                }

            }

        } finally {
            if (cursor != null) {
                cursor.close();
            }

        }

        return unreadNewsSummaryDataList;
    }

    /**
     * 插入单条数据
     * @param message
     * @return
     */
    public boolean insertOrUpdateMessageApp(UnreadNewsSummaryData message) {
        boolean result = true;
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        try {
            sqLiteDatabase.beginTransaction();
            getWritableDatabase().insertWithOnConflict(
                    UnreadSubcriptionMessageDBHelper.TABLE_NAME,
                    null,
                    UnreadSubcriptionMessageDBHelper.getContentValues(message.getAppId(), message.getMasId(),message.getDeliveryTime()), SQLiteDatabase.CONFLICT_REPLACE);
            sqLiteDatabase.setTransactionSuccessful();

        } catch (Exception e) {
            e.printStackTrace();
            result = false;
        } finally {
            sqLiteDatabase.endTransaction();
        }
        return result;
    }

    public boolean removeByAppId(String appId) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        int deletedCount = sqLiteDatabase.delete(UnreadSubcriptionMessageDBHelper.TABLE_NAME , UnreadSubcriptionMessageDBHelper.DBColumn.APP_ID + "=?", new String[]{appId});
        return 0 != deletedCount;
    }

    public boolean removeByMsgId(String msgId) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        List<UnreadNewsSummaryData> dataList = queryByMsgId(msgId);
        if(dataList.size() > 0) {
            int deletedCount = sqLiteDatabase.delete(UnreadSubcriptionMessageDBHelper.TABLE_NAME , UnreadSubcriptionMessageDBHelper.DBColumn.APP_ID + "=?", new String[]{dataList.get(0).getAppId()});
            return 0 != deletedCount;
        }else {
            return false;
        }
    }
}
