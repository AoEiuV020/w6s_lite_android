package com.foreverht.db.service.dbHelper;

import android.content.ContentValues;
import android.database.Cursor;

import com.foreveross.atwork.infrastructure.model.setting.BusinessCase;
import com.foreveross.atwork.infrastructure.model.setting.ConfigSetting;
import com.foreveross.atwork.infrastructure.model.setting.SourceType;
import com.foreveross.db.SQLiteDatabase;

/**
 * 全局的开关设置数据库表(因主键问题, 已废弃)
 */
@Deprecated
public class ConfigSettingDBHelperV0 implements DBHelper {

    public static final String TABLE_NAME = "settings_";

    /**
     * create table settings_ (source_id_ text, type_ integer, value_ integer, primary key (source_id_, type_))
     */
    private static final String SQL_EXEC = CREATE_TABLE + TABLE_NAME + LEFT_BRACKET  +
                                            DBColumn.SOURCE_ID + TEXT + COMMA +
                                            DBColumn.SOURCE_TYPE + INTEGER + COMMA +
                                            DBColumn.BUSINESS_CASE + TEXT + COMMA +
                                            DBColumn.VALUE + INTEGER + COMMA +
                                            PRIMARY_KEY + LEFT_BRACKET +
                                            DBColumn.SOURCE_ID + COMMA + DBColumn.SOURCE_TYPE +
                                            RIGHT_BRACKET + RIGHT_BRACKET;



    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_EXEC);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 9) {
            db.execSQL(SQL_EXEC);
            oldVersion = 9;
        }



    }

    public static class DBColumn {

        public static final String SOURCE_ID = "source_id_";

        public static final String SOURCE_TYPE = "source_type_";

        public static final String BUSINESS_CASE = "business_case_";

        public static final String VALUE = "value_";

    }

    public static ContentValues getContentValues(ConfigSetting configSetting) {
        ContentValues cv = new ContentValues();
        cv.put(DBColumn.SOURCE_ID, configSetting.mSourceId);
        cv.put(DBColumn.SOURCE_TYPE, configSetting.mSourceType.initValue());
        cv.put(DBColumn.BUSINESS_CASE, configSetting.mBusinessCase.toString());
        cv.put(DBColumn.VALUE, configSetting.mValue);
        return cv;
    }

    public static ConfigSetting fromCursor(Cursor cursor) {
        ConfigSetting configSetting = new ConfigSetting();
        int index = -1;
        if ((index = cursor.getColumnIndex(DBColumn.SOURCE_ID)) != -1) {
            configSetting.mSourceId = cursor.getString(index);
        }
        if ((index = cursor.getColumnIndex(DBColumn.SOURCE_TYPE)) != -1) {
            configSetting.mSourceType = SourceType.valueOf(cursor.getInt(index));
        }

        if ((index = cursor.getColumnIndex(DBColumn.BUSINESS_CASE)) != -1) {
            configSetting.mBusinessCase = BusinessCase.valueOf(cursor.getString(index));
        }

        if ((index = cursor.getColumnIndex(DBColumn.VALUE)) != -1) {
            configSetting.mValue = cursor.getInt(index);
        }
        return configSetting;
    }
}
