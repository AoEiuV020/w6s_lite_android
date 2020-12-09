package com.foreverht.db.service.dbHelper;

import android.content.ContentValues;
import android.database.Cursor;

import com.foreveross.atwork.infrastructure.model.dropbox.DropboxConfig;

import com.foreveross.db.SQLiteDatabase;

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
 * Created by reyzhang22 on 16/9/13.
 */
public class DropboxConfigDBHelper implements DBHelper {

    public static final String TABLE_NAME = "cloud_disk_config_";

    private static final String SQL_EXEC = CREATE_TABLE + TABLE_NAME + LEFT_BRACKET +
                                            DBColumn.SOURCE_ID + TEXT + PRIMARY_KEY + COMMA +
                                            DBColumn.MAX_VOLUME + TEXT + COMMA +
                                            DBColumn.LAST_REFRESH_TIME + INTEGER + COMMA +
                                            DBColumn.READ_ONLY + TEXT +  RIGHT_BRACKET;

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_EXEC);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 7) {
            db.execSQL(SQL_EXEC);
            oldVersion = 7;
        }

    }

    public static ContentValues getContentValues(DropboxConfig config) {
        ContentValues cv = new ContentValues();
        cv.put(DBColumn.SOURCE_ID, config.mSourceId);
        cv.put(DBColumn.MAX_VOLUME, config.mMaxVolume);
        cv.put(DBColumn.LAST_REFRESH_TIME, config.mLastRefreshTime);
        cv.put(DBColumn.READ_ONLY, config.mReadOnly);
        return cv;
    }

    public static DropboxConfig fromCursor(Cursor cursor) {
        DropboxConfig config = new DropboxConfig();
        int index = -1;
        if ((index = cursor.getColumnIndex(DBColumn.SOURCE_ID)) != -1) {
            config.mSourceId = cursor.getString(index);
        }
        if ((index = cursor.getColumnIndex(DBColumn.MAX_VOLUME)) != -1) {
            config.mMaxVolume = cursor.getString(index);
        }
        if ((index = cursor.getColumnIndex(DBColumn.LAST_REFRESH_TIME)) != -1) {
            config.mLastRefreshTime = cursor.getLong(index);
        }
        if ((index = cursor.getColumnIndex(DBColumn.READ_ONLY)) != -1) {
            config.mReadOnly = "1".equalsIgnoreCase(cursor.getString(index));
        }
        return config;
    }

    public static class DBColumn {

        public static final String SOURCE_ID = "source_id_";

        public static final String MAX_VOLUME = "max_volume_";

        public static final String LAST_REFRESH_TIME = "last_refresh_time_";

        public static final String READ_ONLY = "read_only";
    }
}
