package com.foreverht.db.service.dbHelper;

import com.foreveross.atwork.infrastructure.utils.Logger;
import com.foreveross.db.SQLiteDatabase;

public class WorkbenchBoardDbHelper implements DBHelper {

    private static final String TAG = WorkbenchBoardDbHelper.class.getName();

    public static final String TABLE_NAME = "workbench_board_";

    /**
     * create table workbench_board_
     * ( org_code_ text,
     * domain_id_ text,
     * name_ text,
     * remarks_ text,
     * definitions_ blob,
     * widgets_ blob,
     * primary key  ( org_code_) )
     */
    private static final String SQL_EXEC = CREATE_TABLE + TABLE_NAME + LEFT_BRACKET +
            DBColumn.ORG_CODE + TEXT + COMMA +
            DBColumn.DOMAIN_ID + TEXT + COMMA +
            DBColumn.NAME + TEXT + COMMA +
            DBColumn.REMARKS + TEXT + COMMA +
            DBColumn.DEFINITIONS + BLOB + COMMA +
            DBColumn.WIDGETS + BLOB + COMMA +
            PRIMARY_KEY + LEFT_BRACKET +
            DBColumn.ORG_CODE +
            RIGHT_BRACKET + RIGHT_BRACKET;


    @Override
    public void onCreate(SQLiteDatabase db) {
        Logger.d(TAG, "sql = " + SQL_EXEC);
        db.execSQL(SQL_EXEC);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion < 20){
            try {
                db.execSQL(SQL_EXEC);
                oldVersion = 20;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public class DBColumn {


        public static final String ORG_CODE = "org_code_";

        public static final String DOMAIN_ID = "domain_id_";

        public static final String NAME = "name_";

        public static final String REMARKS = "remarks_";

        public static final String DEFINITIONS = "definitions_";

        public static final String WIDGETS = "widgets_";


    }
}
