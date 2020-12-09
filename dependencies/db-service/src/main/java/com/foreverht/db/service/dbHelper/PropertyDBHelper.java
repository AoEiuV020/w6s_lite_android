package com.foreverht.db.service.dbHelper;

import android.util.Log;


import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.db.SQLiteDatabase;

/**
 * Created by lingen on 15/4/3.
 * Description:
 */
public class PropertyDBHelper implements DBHelper {

    private static final String TAG = PropertyDBHelper.class.getSimpleName();

    public class DBColumn {

        /**
         * 主键ID
         */
        public static final String ID = "id_";

        /**
         * 属性名
         */
        public static final String KEY = "key_";

        /**
         * 属性中文描述
         */
        public static final String DESCRIPTION = "description_";

        /**
         * 属性值
         */
        public static final String REGEX = "regex_";

        /**
         * 排序
         */
        public static final String SORT = "sort_";

        public static final String CREATE_TABLE_SQL = "create table property (id_ integer primary key autoincrement," +
                "key_ text not null,description_ text not null," +
                "regex_ text,sort_ integer)";
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DBColumn.CREATE_TABLE_SQL);
        if (BaseApplicationLike.sIsDebug) {
            Log.d(TAG, "CREATE TABLE:" + DBColumn.CREATE_TABLE_SQL);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
