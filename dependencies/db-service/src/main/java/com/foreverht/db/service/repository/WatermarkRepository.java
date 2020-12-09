package com.foreverht.db.service.repository;

import android.database.Cursor;
import androidx.annotation.Nullable;

import com.foreverht.db.service.W6sBaseRepository;
import com.foreverht.db.service.dbHelper.WaterMarkDBHelper;
import com.foreveross.atwork.infrastructure.model.Watermark;
import com.foreveross.db.SQLiteDatabase;

/**
 * Created by reyzhang22 on 17/3/9.
 */

public class WatermarkRepository extends W6sBaseRepository {

    private static final WatermarkRepository sInstance = new WatermarkRepository();

    public static final WatermarkRepository getInstance() {
        return sInstance;
    }

    public long insertOrUpdateWatermark(Watermark watermark) {
        return getWritableDatabase().insertWithOnConflict(WaterMarkDBHelper.TABLE_NAME, "", WaterMarkDBHelper.getContentValues(watermark), SQLiteDatabase.CONFLICT_REPLACE);
    }

    public Watermark queryWatermark(Watermark watermark) {
        return queryWatermark(watermark.mSourceId, watermark.mType.toInt());
    }

    @Nullable
    public Watermark queryWatermark(String sourceId, int type) {
        String sql = "select * from " + WaterMarkDBHelper.TABLE_NAME + " where " + WaterMarkDBHelper.DBColumn.SOURCE_ID + " = ? and " + WaterMarkDBHelper.DBColumn.TYPE + " = ?";
        Cursor cursor = getReadableDatabase().rawQuery(sql, new String[]{"'" + sourceId + "'", "'" + type + "'"});
        if (cursor == null) {
            return null;
        }
        if (cursor.getCount() == 0) {
            cursor.close();
            return null;
        }
        cursor.moveToFirst();
        Watermark watermark = WaterMarkDBHelper.fromCursor(cursor);

        return watermark;
    }

    public int deleteWatermark(String sourceId, int type) {
       return getWritableDatabase().delete(WaterMarkDBHelper.TABLE_NAME, WaterMarkDBHelper.DBColumn.SOURCE_ID + " = ? and " + WaterMarkDBHelper.DBColumn.TYPE + " = ?", new String[]{sourceId, String.valueOf(type)});
    }

    public int deleteWatermark(Watermark watermark) {
        return deleteWatermark(watermark.mSourceId, watermark.mType.toInt());
    }
}
