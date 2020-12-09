package com.foreverht.db.service.repository;

import android.database.Cursor;
import androidx.annotation.Nullable;

import com.foreverht.db.service.W6sBaseRepository;
import com.foreverht.db.service.dbHelper.ConfigSettingDBHelper;
import com.foreveross.atwork.infrastructure.manager.bugFix.W6sBugFixManager;
import com.foreveross.atwork.infrastructure.model.setting.BusinessCase;
import com.foreveross.atwork.infrastructure.model.setting.ConfigSetting;
import com.foreveross.atwork.infrastructure.model.setting.SourceType;
import com.foreveross.db.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dasunsy on 2017/3/31.
 */

public class ConfigSettingRepository extends W6sBaseRepository {


    /**
     * 获取开关设置
     *
     * @param sourceId
     * @param sourceType
     * @param businessCase
     */
    public static ConfigSetting getConfigSetting(String sourceId, SourceType sourceType, BusinessCase businessCase) {
        W6sBugFixManager.getInstance().fixedSettingDbPrimaryKeyInMinjieVersion();


        String sql = "select * from " + ConfigSettingDBHelper.TABLE_NAME + " where "
                + ConfigSettingDBHelper.DBColumn.SOURCE_ID + "=? and "
//                + ConfigSettingDBHelper.DBColumn.SOURCE_TYPE + "=? and "
                + ConfigSettingDBHelper.DBColumn.BUSINESS_CASE + "=?";

        ConfigSetting configSetting = null;

        Cursor cursor = null;
        try {

//            cursor = getWritableDatabase().rawQuery(sql, new String[]{sourceId, String.valueOf(sourceType.initValue()), businessCase.toString()});
            cursor = getWritableDatabase().rawQuery(sql, new String[]{sourceId, businessCase.toString()});
            if (cursor.moveToNext()) {
                configSetting = ConfigSettingDBHelper.fromCursor(cursor);
            }

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return configSetting;

    }

    public static List<ConfigSetting> getSessionConfigSettings() {
        W6sBugFixManager.getInstance().fixedSettingDbPrimaryKeyInMinjieVersion();

        List<String> businessCasesQuery = new ArrayList<>();
        businessCasesQuery.add(BusinessCase.SESSION_TOP.toString());
        businessCasesQuery.add(BusinessCase.SESSION_TRANSLATION.toString());
        businessCasesQuery.add(BusinessCase.SESSION_SHIELD.toString());
        businessCasesQuery.add(BusinessCase.ANNOUNCE_APP.toString());

        String sql = "select * from " + ConfigSettingDBHelper.TABLE_NAME + " where "
                + ConfigSettingDBHelper.DBColumn.BUSINESS_CASE + " in (" + getInStringParams(businessCasesQuery) + ")";

        List<ConfigSetting> configSettings = new ArrayList<>();

        Cursor cursor = null;
        try {

            cursor = getWritableDatabase().rawQuery(sql, new String[]{});
            while (cursor.moveToNext()) {
                ConfigSetting configSetting = ConfigSettingDBHelper.fromCursor(cursor);
                configSettings.add(configSetting);
            }

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return configSettings;
    }


    public static void batchRemoveConfigSettings(List<String> businessCases) {
        W6sBugFixManager.getInstance().fixedSettingDbPrimaryKeyInMinjieVersion();

        String sql = "delete * from " + ConfigSettingDBHelper.TABLE_NAME + " where "
                + ConfigSettingDBHelper.DBColumn.BUSINESS_CASE + " in (" + getInStringParams(businessCases) + ")";

        getWritableDatabase().execSQL(sql);
    }

    public static boolean batchInsertOrUpdateConfigSetting(List<ConfigSetting> configSettings) {
        boolean result = false;

        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        try {
            sqLiteDatabase.beginTransaction();

            for(ConfigSetting configSetting : configSettings) {
                insertOrUpdateConfigSetting(sqLiteDatabase, configSetting);
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


    public static boolean insertOrUpdateConfigSetting(ConfigSetting configSetting) {
        return insertOrUpdateConfigSetting(null, configSetting);
    }

    public static boolean insertOrUpdateConfigSetting(@Nullable SQLiteDatabase db, ConfigSetting configSetting) {
        W6sBugFixManager.getInstance().fixedSettingDbPrimaryKeyInMinjieVersion();

        if(null == db) {
            db = getWritableDatabase();
        }
        long insertResult = db.insertWithOnConflict(
                ConfigSettingDBHelper.TABLE_NAME,
                null,
                ConfigSettingDBHelper.getContentValues(configSetting),
                SQLiteDatabase.CONFLICT_REPLACE
        );

        if (-1 == insertResult) {
            return false;
        }

        return true;

    }
}
