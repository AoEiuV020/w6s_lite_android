package com.foreverht.db.service.dbHelper;

import android.content.ContentValues;
import android.database.Cursor;

import com.foreveross.atwork.infrastructure.model.clickStatistics.ClickEvent;
import com.foreveross.atwork.infrastructure.model.clickStatistics.Type;
import com.foreveross.atwork.infrastructure.utils.Logger;
import com.foreveross.db.SQLiteDatabase;

import java.math.BigInteger;

@Deprecated
public class ClickStatisticsDBHelperV0 implements DBHelper {

    private static final String TAG = ClickStatisticsDBHelperV0.class.getSimpleName();

    public static final String TABLE_NAME = "click_statistics_";

    /**
     * create table click_statistics_
     * ( id_ text, count_ text, type_ integer
     * primary key  ( id_) )
     */
    private static final String SQL_EXEC = CREATE_TABLE + TABLE_NAME + LEFT_BRACKET +
            DBColumn.ID + TEXT +  COMMA +
            DBColumn.COUNT + TEXT +  COMMA +
            DBColumn.TYPE + INTEGER +  COMMA +
            PRIMARY_KEY + LEFT_BRACKET +
            DBColumn.ID +
            RIGHT_BRACKET + RIGHT_BRACKET;


    @Override
    public void onCreate(SQLiteDatabase db) {
        Logger.d(TAG, "sql = " + SQL_EXEC);
        db.execSQL(SQL_EXEC);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 15) {
            db.execSQL(SQL_EXEC);
            oldVersion = 15;
        }
    }

    public static ContentValues getContentValues(ClickEvent clickEvent) {
        ContentValues cv = new ContentValues();
        cv.put(DBColumn.ID, clickEvent.getId());
//        cv.put(DBColumn.COUNT, clickEvent.getCount() + "");
        cv.put(DBColumn.TYPE, clickEvent.getType().initValue());
        return cv;
    }

    public static ClickEvent fromCursor(Cursor cursor) {
        ClickEvent event = new ClickEvent();

        int index = -1;
        if ((index = cursor.getColumnIndex(DBColumn.ID)) != -1) {
            event.setId(cursor.getString(index));
        }
//        if ((index = cursor.getColumnIndex(DBColumn.COUNT)) != -1) {
//            event.setCount(new BigInteger(cursor.getString(index)));
//        }

        if ((index = cursor.getColumnIndex(DBColumn.TYPE)) != -1) {
            event.setType(Type.Companion.valueOf(cursor.getInt(index)));
        }


        return event;
    }



    public class DBColumn {
        public static final String ID = "id_";

        public static final String COUNT = "count_";

        public static final String TYPE = "type_";

    }
}
