package com.foreverht.db.service.dbHelper;

import android.content.ContentValues;
import android.database.Cursor;

import com.foreverht.db.service.W6sBaseRepository;
import com.foreveross.atwork.infrastructure.model.orgization.Department;
import com.foreveross.atwork.infrastructure.utils.ByteArrayToBase64TypeAdapter;
import com.foreveross.db.BaseDatabaseHelper;
import com.foreveross.db.SQLiteDatabase;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class DeptDBHelper implements DBHelper {

    private static final String TAG = DeptDBHelper.class.getSimpleName();



    public static final String TABLE_NAME = "dept_";


    /**
     * create table dept_ (
     * org_code_ text, org_id_ text ,name_ text ,
     * sort_ integer, path_ text,
     * employee_count_ integer, all_employee_count_ integer, data_ blob
     * primary key  ( org_code_, org_id_)  )
     */
    private static final String SQL_EXEC = CREATE_TABLE + TABLE_NAME + LEFT_BRACKET +
            DBColumn.ORG_CODE + TEXT + COMMA +
            DBColumn.ORG_ID + TEXT + COMMA +
            DBColumn.NAME + TEXT + COMMA +
            DBColumn.SORT + INTEGER + COMMA +
            DBColumn.PATH+ TEXT + COMMA +
            DBColumn.EMPLOYEE_COUNT + INTEGER + COMMA +
            DBColumn.ALL_EMPLOYEE_COUNT + INTEGER + COMMA +
            DBColumn.DATA + BLOB + COMMA +
            PRIMARY_KEY + LEFT_BRACKET +
            DBColumn.ORG_CODE + COMMA + DBColumn.ORG_ID +
            RIGHT_BRACKET + RIGHT_BRACKET;



    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_EXEC);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(500 > oldVersion) {
            BaseDatabaseHelper.createTable(db, SQL_EXEC);
            oldVersion = 500;
        }
    }


    public static Department fromCursor(Cursor cursor) {
        Department department = null;
        int index = -1;
        if ((index = cursor.getColumnIndex(DBColumn.DATA)) != -1) {
            byte[] data = cursor.getBlob(index);
            String dataStr = new String(data);

            Gson gson = new GsonBuilder().registerTypeAdapter(byte[].class, new ByteArrayToBase64TypeAdapter()).create();
            department = gson.fromJson(dataStr, Department.class);
        }

        if(null == department) {
            department = new Department();
        }

        if ((index = cursor.getColumnIndex(DBColumn.ORG_ID)) != -1) {
            department.mId = cursor.getString(index);
        }

        if ((index = cursor.getColumnIndex(DBColumn.ORG_CODE)) != -1) {
            department.mOrgCode = cursor.getString(index);
        }

        if ((index = cursor.getColumnIndex(DBColumn.NAME)) != -1) {
            department.mName = cursor.getString(index);
        }

        if ((index = cursor.getColumnIndex(DBColumn.SORT)) != -1) {
            department.mSortOrder = cursor.getInt(index);
        }


        if ((index = cursor.getColumnIndex(DBColumn.PATH)) != -1) {
            department.mPath = cursor.getString(index);
        }

        if ((index = cursor.getColumnIndex(DBColumn.EMPLOYEE_COUNT)) != -1) {
            department.mEmployeeCount = cursor.getInt(index);
        }

        if ((index = cursor.getColumnIndex(DBColumn.ALL_EMPLOYEE_COUNT)) != -1) {
            department.mAllEmployeeCount = cursor.getInt(index);
        }


        return department;
    }


    public static ContentValues getContentValue(Department department) {
        ContentValues cv = new ContentValues();
        cv.put(DBColumn.ORG_ID, department.mId);
        cv.put(DBColumn.ORG_CODE, department.mOrgCode);
        cv.put(DBColumn.NAME, department.mName);
        cv.put(DBColumn.PATH, department.mPath);
        cv.put(DBColumn.SORT, department.mSortOrder);
        cv.put(DBColumn.EMPLOYEE_COUNT, department.mEmployeeCount);
        cv.put(DBColumn.ALL_EMPLOYEE_COUNT, department.mAllEmployeeCount);

        Gson gson = new GsonBuilder().registerTypeAdapter(byte[].class, new ByteArrayToBase64TypeAdapter()).create();
        byte[] data = gson.toJson(department).getBytes();
        cv.put(DBColumn.DATA, data);


        return cv;
    }

    public static String getDetailDBColumn(String column) {
        return W6sBaseRepository.getDetailDBColumn(TABLE_NAME, column);
    }


    public class DBColumn {

        public static final String ORG_CODE = "org_code_";

        public static final String ORG_ID = "org_id_";

        public static final String NAME = "name_";

        public static final String SORT = "sort_";

        public static final String PATH = "path_";

        public static final String EMPLOYEE_COUNT = "employee_count_";

        public static final String ALL_EMPLOYEE_COUNT = "all_employee_count_";

        public static final String DATA = "data_";

    }
}
