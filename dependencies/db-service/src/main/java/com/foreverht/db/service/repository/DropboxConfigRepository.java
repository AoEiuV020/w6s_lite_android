package com.foreverht.db.service.repository;

import android.database.Cursor;

import com.foreverht.db.service.W6sBaseRepository;
import com.foreverht.db.service.dbHelper.DropboxConfigDBHelper;
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
public class DropboxConfigRepository extends W6sBaseRepository {

    private static DropboxConfigRepository sInstance = new DropboxConfigRepository();

    public static DropboxConfigRepository getInstance() {
        return sInstance;
    }

    public boolean insertOrUpdateDropboxConfig(DropboxConfig config) {
        if (config == null) {
            return false;
        }
        getWritableDatabase().insertWithOnConflict(DropboxConfigDBHelper.TABLE_NAME, null, DropboxConfigDBHelper.getContentValues(config), SQLiteDatabase.CONFLICT_REPLACE);
        return true;
    }

    public DropboxConfig getDropboxConfigBySourceId(String sourceId) {
        DropboxConfig config = new DropboxConfig();
        config.mSourceId = sourceId;
        String sql = "select * from " + DropboxConfigDBHelper.TABLE_NAME + " where " + DropboxConfigDBHelper.DBColumn.SOURCE_ID  + " = ?";
        Cursor cursor = getReadableDatabase().rawQuery(sql, new String[]{sourceId});
        if (cursor  == null) {
            return config;
        }
        if (cursor.moveToFirst()) {
            config = DropboxConfigDBHelper.fromCursor(cursor);
        }
        cursor.close();
        return config;
    }
}
