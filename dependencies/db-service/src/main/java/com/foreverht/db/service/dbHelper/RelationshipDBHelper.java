package com.foreverht.db.service.dbHelper;/**
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@%((((((((#&@@@((#((#@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@%(((((((((((((%@*((((@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@%(((/,/(((((((((@@@*#@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@#(((@@@((((((((((@@((@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@(((((((((((((((((((((@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@(((((((((((((((((((@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@(((((((((((((((*@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 *                       __           __
 * .--.--.--.-----.----.|  |--.-----.|  |.--.--.-----.
 * |  |  |  |  _  |   _||    <|  _  ||  ||  |  |__ --|
 * |________|_____|__|  |__|__|   __||__||_____|_____|
 *                            |__|
 */


import android.content.ContentValues;
import android.database.Cursor;

import com.foreveross.atwork.infrastructure.model.Relationship;
import com.foreveross.atwork.infrastructure.utils.Logger;

import com.foreveross.db.SQLiteDatabase;

/**
 * Created by reyzhang22 on 16/3/23.
 */
public class RelationshipDBHelper implements DBHelper {

    private static final String TAG = RelationshipDBHelper.class.getName();

    public static final String TABLE_NAME = "relationship_";

    /**
     * create table relationship_
     * ( user_id_ text ,type_ text ,
     * primary key  ( user_id_,type_ )  )
     */
    private static final String SQL_EXEC = CREATE_TABLE + TABLE_NAME + LEFT_BRACKET +
                                            DBColumn.USER_ID + TEXT +  COMMA +
                                            DBColumn.TYPE + INTEGER + COMMA +
                                            PRIMARY_KEY + LEFT_BRACKET +
                                            DBColumn.USER_ID + COMMA + DBColumn.TYPE +
                                            RIGHT_BRACKET + RIGHT_BRACKET;

    @Override
    public void onCreate(SQLiteDatabase db) {
        Logger.d(TAG, "SQL = " + SQL_EXEC);
        db.execSQL(SQL_EXEC);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /**
     * 关系表contentValue
     * @param relationship
     * @return
     */
    public static ContentValues getContentValues(Relationship relationship) {
        ContentValues cv = new ContentValues();
        cv.put(DBColumn.USER_ID, relationship.mUserId);
        cv.put(DBColumn.TYPE, relationship.mType);
        return cv;
    }

    /***
     * 从cursor获取到关系模型
     * @param cursor
     * @return
     */
    public static Relationship fromCursor(Cursor cursor) {
        int index = -1;
        Relationship relationship = new Relationship();
        if ((index = cursor.getColumnIndex(DBColumn.USER_ID)) != -1) {
            relationship.mUserId = cursor.getString(index);
        }
        if ((index = cursor.getColumnIndex(DBColumn.TYPE)) != -1) {
            relationship.mType = cursor.getInt(index);
        }
        return relationship;
    }

    public static String getDetailDBColumn(String column) {
        return TABLE_NAME + "."  + column;
    }


    public class DBColumn {
        public static final String USER_ID = "user_id_";

        public static final String TYPE = "type_";
    }
}
