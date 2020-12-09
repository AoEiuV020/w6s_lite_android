package com.foreverht.db.service.dbHelper;

import android.content.ContentValues;
import android.database.Cursor;

import com.foreveross.atwork.infrastructure.model.clickStatistics.ClickEvent;
import com.foreveross.atwork.infrastructure.model.clickStatistics.Type;
import com.foreveross.atwork.infrastructure.utils.Logger;
import com.foreveross.atwork.infrastructure.utils.TimeUtil;
import com.foreveross.db.BaseDatabaseHelper;
import com.foreveross.db.SQLiteDatabase;

import java.math.BigInteger;

public class ClickStatisticsDBHelper implements DBHelper {

    private static final String TAG = ClickStatisticsDBHelper.class.getSimpleName();

    public static final String TABLE_NAME = "click_statistics_v2_";

    /**
     * create table click_statistics_v2_
     * ( identifier_ text, id_ text, type_ integer, day_ text, begin_ integer
     * primary key  ( identifier_) )
     */
    private static final String SQL_EXEC = CREATE_TABLE + TABLE_NAME + LEFT_BRACKET +
            DBColumn.IDENTIFIER + TEXT + COMMA +
            DBColumn.ID + TEXT + COMMA +
            DBColumn.DAY + TEXT + COMMA +
            DBColumn.BEGIN + INTEGER + COMMA +
            DBColumn.TYPE + INTEGER + COMMA +
            PRIMARY_KEY + LEFT_BRACKET +
            DBColumn.IDENTIFIER +
            RIGHT_BRACKET + RIGHT_BRACKET;


    @Override
    public void onCreate(SQLiteDatabase db) {
        Logger.d(TAG, "sql = " + SQL_EXEC);
        db.execSQL(SQL_EXEC);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 60) {
            BaseDatabaseHelper.createTable(db, SQL_EXEC);
            oldVersion = 60;
        }
    }

    public static ContentValues getContentValues(ClickEvent clickEvent) {
        ContentValues cv = new ContentValues();
        cv.put(DBColumn.IDENTIFIER, clickEvent.getIdentifier());
        cv.put(DBColumn.ID, clickEvent.getId());
        cv.put(DBColumn.BEGIN, clickEvent.getBegin());
        cv.put(DBColumn.DAY, TimeUtil.getStringForMillis(clickEvent.getBegin(), TimeUtil.YYYY_MM_DD));
        cv.put(DBColumn.TYPE, clickEvent.getType().initValue());
        return cv;
    }

    public static ClickEvent fromCursor(Cursor cursor) {
        ClickEvent event = new ClickEvent();

        int index = -1;


        if ((index = cursor.getColumnIndex(DBColumn.IDENTIFIER)) != -1) {
            event.setIdentifier(cursor.getString(index));
        }

        if ((index = cursor.getColumnIndex(DBColumn.ID)) != -1) {
            event.setId(cursor.getString(index));
        }

        if ((index = cursor.getColumnIndex(DBColumn.TYPE)) != -1) {
            event.setType(Type.Companion.valueOf(cursor.getInt(index)));
        }

        if ((index = cursor.getColumnIndex(DBColumn.BEGIN)) != -1) {
            event.setBegin(cursor.getLong(index));
        }



        return event;
    }


    public class DBColumn {

        public static final String IDENTIFIER = "identifier_";

        public static final String ID = "id_";

        public static final String TYPE = "type_";

        public static final String DAY = "day_";

        public static final String BEGIN = "begin_";


    }
}
