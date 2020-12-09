package com.foreverht.db.service.dbHelper;

import android.content.ContentValues;
import android.database.Cursor;

import com.foreveross.db.SQLiteDatabase;

/**
 * 用户自定义通知屏蔽，主要屏蔽用户选择的联系人，群组，应用，服务的消息通知
 * Created by ReyZhang on 2015/5/13.
 */
public class CustomerMessageNoticeDBHelper implements DBHelper {

    private static final String TAG = CustomerMessageNoticeDBHelper.class.getSimpleName();

    public static final String TABLE_NAME = "customer_message_notice_";

    public class DBColumn {
        public static final String _ID = "identifier_";

        public static final String _SHIELD_ID = "shield_id_";
    }

    private static final String CREATE_TABLE_SQL = "create table " + TABLE_NAME + " (" +
            DBColumn._ID + " integer primary key autoincrement," +
            DBColumn._SHIELD_ID + " text)";


    public static ContentValues getContentValues(String id) {
        ContentValues cv = new ContentValues();
        cv.put(DBColumn._SHIELD_ID, id);
        return cv;
    }

    public static String getIdFromCursor(Cursor cursor) {
        int index = -1;
        String id = null;
        if ((index = cursor.getColumnIndex(DBColumn._SHIELD_ID)) != -1) {
            id = cursor.getString(index);
        }
        return id;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
