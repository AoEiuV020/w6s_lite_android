package com.foreverht.db.service.dbHelper;

import android.content.ContentValues;
import android.database.Cursor;

import com.foreveross.atwork.infrastructure.model.Watermark;
import com.foreveross.db.SQLiteDatabase;

/**
 * 水印数据库表
 * Created by reyzhang22 on 17/3/9.
 */

public class WaterMarkDBHelper implements DBHelper {

    public static final String TABLE_NAME = "watermark_";

    /**
     * create table watermark_ (source_id_ text, type_ integer, primary key (source_id_, type_))
     */
    private static final String SQL_EXEC = CREATE_TABLE + TABLE_NAME + LEFT_BRACKET  +
                                            DBColumn.SOURCE_ID + TEXT + COMMA +
                                            DBColumn.TYPE + INTEGER + COMMA +
                                            PRIMARY_KEY + LEFT_BRACKET +
                                            DBColumn.SOURCE_ID + COMMA + DBColumn.TYPE +
                                            RIGHT_BRACKET + RIGHT_BRACKET;



    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_EXEC);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 8) {
            db.execSQL(SQL_EXEC);
            oldVersion = 8;
        }
    }

    public static class DBColumn {

        public static final String SOURCE_ID = "source_id_";

        public static final String TYPE = "type_";

    }

    public static ContentValues getContentValues(Watermark watermark) {
        ContentValues cv = new ContentValues();
        cv.put(DBColumn.SOURCE_ID, watermark.mSourceId);
        cv.put(DBColumn.TYPE, watermark.mType.toInt());
        return cv;
    }

    public static Watermark fromCursor(Cursor cursor) {
        Watermark watermark = new Watermark();
        int index = -1;
        if ((index = cursor.getColumnIndex(DBColumn.SOURCE_ID)) != -1) {
            watermark.mSourceId = cursor.getString(index);
        }
        if ((index = cursor.getColumnIndex(DBColumn.TYPE)) != -1) {
            watermark.mType = Watermark.Type.toType(cursor.getInt(index));
        }
        return watermark;
    }
}
