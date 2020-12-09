package com.foreverht.db.service.dbHelper;

import android.util.Log;


import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.db.SQLiteDatabase;

/**
 * Created by lingen on 15/4/3.
 * Description:
 */
public class ContactPropertyDBHelper implements DBHelper {

    private static final String TAG = ContactPropertyDBHelper.class.getSimpleName();

    public class DBColumn {

        /**
         * 主键ID
         */
        public static final String ID = "id_";

        /**
         * 对应用户ID
         */
        public static final String IDENTIFIER = "identifier_";

        /**
         * 对应属性
         */
        public static final String PROPERTY_ID = "property_id_";

        /**
         * 属性值
         */
        public static final String VALUE = "value_";

        public static final String CREATE_TABLE_SQL = "create table contact_property (id_ integer primary key autoincrement," +
                "identifier_ text not null," +
                "property_id_ text not null," +
                "value_ text)";
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
