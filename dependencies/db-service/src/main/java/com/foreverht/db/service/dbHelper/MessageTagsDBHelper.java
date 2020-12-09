package com.foreverht.db.service.dbHelper;

import android.content.ContentValues;
import android.database.Cursor;

import com.foreveross.db.SQLiteDatabase;
import com.w6s.module.MessageTags;

public class MessageTagsDBHelper implements DBHelper {

    public static final String TABLE_NAME = "message_tags_";

    public static final String SQL_EXEC = CREATE_TABLE + TABLE_NAME + LEFT_BRACKET +
            DBColumn.ID + TEXT+ COMMA +
            DBColumn.APP_ID + TEXT + COMMA +
            DBColumn.DOMAIN_ID + TEXT + COMMA +
            DBColumn.ORG_ID + TEXT + COMMA +
            DBColumn.NAME + TEXT + COMMA +
            DBColumn.EN_NAME + TEXT + COMMA +
            DBColumn.TW_NAME + TEXT + COMMA +
            DBColumn.EXTENSION1 + TEXT + COMMA +
            DBColumn.EXTENSION2 + TEXT + COMMA +
            PRIMARY_KEY + LEFT_BRACKET +
            DBColumn.ID +
            RIGHT_BRACKET + RIGHT_BRACKET;


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_EXEC);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 50) {
            try {
                db.execSQL(SQL_EXEC);
            } catch (Exception e) {
                e.printStackTrace();
            }
            oldVersion = 50;
        }
    }

    public static ContentValues getContentValues(MessageTags tags) {
        ContentValues cv = new ContentValues();
        cv.put(DBColumn.ID, tags.getTagId());
        cv.put(DBColumn.APP_ID, tags.getAppId());
        cv.put(DBColumn.DOMAIN_ID, tags.getDomainId());
        cv.put(DBColumn.ORG_ID, tags.getOrgId());
        cv.put(DBColumn.NAME, tags.getName());
        cv.put(DBColumn.EN_NAME, tags.getEnName());
        cv.put(DBColumn.TW_NAME, tags.getTwName());
        return cv;
    }

    public static MessageTags fromCursor(Cursor cursor) {
        int index = -1;
        MessageTags tags = new MessageTags();
        if ((index = cursor.getColumnIndex(DBColumn.APP_ID)) != -1) {
            tags.setAppId(cursor.getString(index));
        }
        if ((index = cursor.getColumnIndex(DBColumn.DOMAIN_ID)) != -1) {
            tags.setDomainId(cursor.getString(index));
        }
        if ((index = cursor.getColumnIndex(DBColumn.ORG_ID)) != -1) {
            tags.setOrgId(cursor.getString(index));
        }
        if ((index = cursor.getColumnIndex(DBColumn.NAME)) != -1) {
            tags.setName(cursor.getString(index));
        }
        if ((index = cursor.getColumnIndex(DBColumn.EN_NAME)) != -1) {
            tags.setEnName(cursor.getString(index));
        }
        if ((index = cursor.getColumnIndex(DBColumn.TW_NAME)) != -1) {
            tags.setTwName(cursor.getString(index));
        }
        if ((index = cursor.getColumnIndex(DBColumn.ID)) != -1) {
            tags.setTagId(cursor.getString(index));
        }
        return tags;
    }

    public static class DBColumn {

        public static final String ID = "id_";

        public static final String APP_ID = "app_id_";

        public static final String DOMAIN_ID = "domain_id_";

        public static final String ORG_ID = "org_id_";

        public static final String NAME = "name_";

        public static final String EN_NAME = "en_name";

        public static final String TW_NAME = "tw_name";

        public static final String EXTENSION1 = "extension1_";

        public static final String EXTENSION2 = "extension2_";
    }
}
