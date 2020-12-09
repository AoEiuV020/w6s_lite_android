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

import com.foreveross.atwork.infrastructure.model.employee.EmployeePropertyRecord;

import com.foreveross.db.SQLiteDatabase;

/**
 * Created by reyzhang22 on 15/12/18.
 */
public class EmployeePropertyRecordDBHelper implements DBHelper {

    private static final String TAG = EmployeePropertyRecordDBHelper.class.getSimpleName();

    public static final String EMPLOYEE_PROPERTY_RECORD_TABLE_NAME = "employee_property_record_";

    private static final String SQL_EXEC = CREATE_TABLE + EMPLOYEE_PROPERTY_RECORD_TABLE_NAME + LEFT_BRACKET +
                                            DBColumn.DATA_SCHEMA_ID + TEXT + PRIMARY_KEY + COMMA +
                                            DBColumn.ALIAS + TEXT + COMMA +
                                            DBColumn.NAME + TEXT + COMMA +
                                            DBColumn.PROPERTY + TEXT + COMMA +
                                            DBColumn.VALUE + TEXT + COMMA +
                                            DBColumn.CONTACT_ID + TEXT + RIGHT_BRACKET;


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_EXEC);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion == newVersion) {
            return;
        }

    }

    public static ContentValues getContentValues(EmployeePropertyRecord record) {
        ContentValues cv = new ContentValues();
        cv.put(DBColumn.ALIAS, record.mAlias);
        cv.put(DBColumn.DATA_SCHEMA_ID, record.mDataSchemaId);
        cv.put(DBColumn.NAME, record.mName);
        cv.put(DBColumn.PROPERTY, record.mProperty);
        cv.put(DBColumn.VALUE, record.mValue);
        cv.put(DBColumn.CONTACT_ID, record.mEmployeeId);
        return cv;
    }

    public static EmployeePropertyRecord fromCursor(Cursor cursor) {
        EmployeePropertyRecord record = new EmployeePropertyRecord();
        int index = -1;

        if ((index = cursor.getColumnIndex(DBColumn.NAME)) != -1) {
            record.mName = cursor.getString(index);
        }

        if ((index = cursor.getColumnIndex(DBColumn.ALIAS)) != -1) {
            record.mAlias = cursor.getString(index);
        }

        if ((index = cursor.getColumnIndex(DBColumn.DATA_SCHEMA_ID)) != -1) {
            record.mDataSchemaId = cursor.getString(index);
        }

        if ((index = cursor.getColumnIndex(DBColumn.PROPERTY)) != -1) {
            record.mProperty = cursor.getString(index);
        }

        if ((index = cursor.getColumnIndex(DBColumn.VALUE)) != -1) {
            record.mValue = cursor.getString(index);
        }

        if ((index = cursor.getColumnIndex(DBColumn.CONTACT_ID)) != -1) {
            record.mEmployeeId = cursor.getString(index);
        }

        return record;

    }

    public class DBColumn {


        public static final String DATA_SCHEMA_ID = "data_schema_id_";

        public static final String NAME = "name_";

        public static final String ALIAS = "alias_";

        public static final String PROPERTY = "property_";

        public static final String VALUE = "value_";

        public static final String CONTACT_ID = "contact_id_";
    }
}
