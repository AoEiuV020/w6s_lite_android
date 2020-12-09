package com.foreverht.db.service.dbHelper;/**
 @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@@@@%((((((((#&@@@((#((#@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@@%(((((((((((((%@*((((@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@%(((/,/(((((((((@@@*#@@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@#(((@@@((((((((((@@((@@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@(((((((((((((((((((((@@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@@(((((((((((((((((((@@@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@@@@(((((((((((((((*@@@@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 __           __
 .--.--.--.-----.----.|  |--.-----.|  |.--.--.-----.
 |  |  |  |  _  |   _||    <|  _  ||  ||  |  |__ --|
 |________|_____|__|  |__|__|   __||__||_____|_____|
 |__|
 */


import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;

import com.foreverht.db.service.W6sDatabaseHelper;
import com.foreveross.atwork.infrastructure.model.employee.DataSchema;
import com.foreveross.atwork.infrastructure.model.i18n.CommonI18nInfoData;
import com.foreveross.atwork.infrastructure.utils.JsonUtil;
import com.foreveross.atwork.infrastructure.utils.Logger;
import com.foreveross.db.SQLiteDatabase;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

/**
 * Created by reyzhang22 on 15/12/18.
 */
@Deprecated
public class DataSchemasDBHelperV0 implements DBHelper {

    private static final String TAG = DataSchemasDBHelper.class.getSimpleName();

    public static final String TABLE_NAME = "schemas_";

    /**
     * create table schemas_ (
     * data_schema_id_ text  primary key ,org_code_ text ,
     * options_ text ,domain_id_ text ,alias_ text , i18n_ text ,
     * name_ text ,property_ text ,sort_order_ integer ,type_ text ,opsable_ integer ,
     * foreign key  ( org_code_ )  references orgs_ ( org_code_ )  )
     *
     * */
    private static final String SQL_EXEC = CREATE_TABLE + TABLE_NAME + LEFT_BRACKET +
            DBColumn.DATA_SCHEMA_ID + TEXT + PRIMARY_KEY + COMMA +
            DBColumn.ORG_CODE + TEXT  + COMMA +
            DBColumn.OPTIONS + TEXT + COMMA +
            DBColumn.DOMAIN_ID + TEXT + COMMA +
            DBColumn.ALIAS + TEXT + COMMA +
            DBColumn.I18N + TEXT + COMMA +
            DBColumn.NAME + TEXT + COMMA +
            DBColumn.PROPERTY + TEXT + COMMA +
            DBColumn.SORT_ORDER + INTEGER + COMMA +
            DBColumn.TYPE + TEXT + COMMA +
            DBColumn.OPSABLE + INTEGER + COMMA +
            FOREIGN_KEY + LEFT_BRACKET + DBColumn.ORG_CODE + RIGHT_BRACKET
            + REFERENCES + OrganizationDBHelper.TABLE_NAME + LEFT_BRACKET + OrganizationDBHelper.DBColumn.ORG_CODE + RIGHT_BRACKET +RIGHT_BRACKET;

    @Override
    public void onCreate(SQLiteDatabase db) {
        Logger.i(TAG, "SQL = " + SQL_EXEC);
        db.execSQL(SQL_EXEC);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion < 18) {
            W6sDatabaseHelper.addColumnToTable(db, TABLE_NAME, DBColumn.I18N, "text");

            oldVersion = 18;
        }
    }

    /**
     * 获取dataSchema 的 contentValue值
     * @param dataSchema
     * @return
     */
    public static ContentValues getContentValues(DataSchema dataSchema) {
        ContentValues cv = new ContentValues();
        cv.put(DBColumn.DATA_SCHEMA_ID, String.valueOf(dataSchema.mId));
        cv.put(DBColumn.ORG_CODE,dataSchema.mOrgCode);
        cv.put(DBColumn.OPTIONS,new Gson().toJson(dataSchema.mOptions));
        cv.put(DBColumn.DOMAIN_ID,dataSchema.mDomainId);
        cv.put(DBColumn.ALIAS, dataSchema.mAlias);
        cv.put(DBColumn.NAME, dataSchema.mName);
        cv.put(DBColumn.I18N, JsonUtil.toJson(dataSchema.getI18nInfo()));
        cv.put(DBColumn.PROPERTY, dataSchema.mProperty);
        cv.put(DBColumn.TYPE, dataSchema.type);
        cv.put(DBColumn.SORT_ORDER, dataSchema.mSortOrder);
        cv.put(DBColumn.OPSABLE, getValuesInteger(dataSchema.mOpsable));
        return cv;
    }

    /**
     * 通过cursor获取dataSchema对象
     * @param cursor
     * @return
     */
    public static DataSchema fromCursor(Cursor cursor) {
        DataSchema dataSchema = new DataSchema();
        int index = -1;

        if ((index = cursor.getColumnIndex(DBColumn.DATA_SCHEMA_ID)) != -1) {
            dataSchema.mId = cursor.getString(index);
        }

        if ((index = cursor.getColumnIndex(DBColumn.ORG_CODE)) != -1) {
            dataSchema.mOrgCode = cursor.getString(index);
        }

        if ((index = cursor.getColumnIndex(DBColumn.NAME)) != -1) {
            dataSchema.mName = cursor.getString(index);
        }

        if ((index = cursor.getColumnIndex(DBColumn.ALIAS)) != -1) {
            dataSchema.mAlias = cursor.getString(index);
        }

        if ((index = cursor.getColumnIndex(DBColumn.I18N)) != -1) {
            CommonI18nInfoData i18nAliasInfo = JsonUtil.fromJson(cursor.getString(index), CommonI18nInfoData.class);
            if (null != i18nAliasInfo) {
                dataSchema.mEnAlias = i18nAliasInfo.getEnName();
                dataSchema.mTwAlias = i18nAliasInfo.getTwName();
            }
        }



        if ((index = cursor.getColumnIndex(DBColumn.PROPERTY)) != -1) {
            dataSchema.mProperty = cursor.getString(index);
        }

        if ((index = cursor.getColumnIndex(DBColumn.SORT_ORDER)) != -1) {
            dataSchema.mSortOrder = cursor.getInt(index);
        }

        if ((index = cursor.getColumnIndex(DBColumn.TYPE)) != -1) {
            dataSchema.type = cursor.getString(index);
        }

        if ((index = cursor.getColumnIndex(DBColumn.OPSABLE)) != -1) {
            dataSchema.mOpsable = getCursorBoolean(cursor.getInt(index));
        }

        if ((index = cursor.getColumnIndex(DBColumn.DOMAIN_ID)) != -1) {
            dataSchema.mDomainId = cursor.getString(index);
        }

        if ((index = cursor.getColumnIndex(DBColumn.OPTIONS)) != -1) {
            String options = cursor.getString(index);
            if (!TextUtils.isEmpty(options)) {
                dataSchema.mOptions = new Gson().fromJson(options, new TypeToken<List<String>>() {
                }.getType());
            }
        }

        return dataSchema;

    }


    private static int getValuesInteger(Boolean bool) {
        return Boolean.TRUE.equals(bool) ? 1 : 0;
    }

    private static boolean getCursorBoolean(Integer integer) {
        return integer == 1;
    }


    public class DBColumn {

        public static final String DATA_SCHEMA_ID = "data_schema_id_";

        public static final String ORG_CODE = "org_code_";

        public static final String ALIAS = "alias_";

        public static final String I18N = "i18n_";

        public static final String NAME = "name_";

        public static final String DOMAIN_ID ="domain_id_";

        public static final String PROPERTY = "property_";

        public static final String SORT_ORDER = "sort_order_";

        public static final String TYPE = "type_";

        public static final String OPSABLE = "opsable_";

        public static final String OPTIONS ="options_";

    }


}
