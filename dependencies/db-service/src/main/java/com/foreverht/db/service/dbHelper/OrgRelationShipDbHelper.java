package com.foreverht.db.service.dbHelper;

import android.content.ContentValues;
import android.database.Cursor;

import com.foreveross.atwork.infrastructure.model.orgization.OrgRelationship;
import com.foreveross.atwork.infrastructure.utils.Logger;

import com.foreveross.db.SQLiteDatabase;

/**
 * Created by dasunsy on 16/5/12.
 */
public class OrgRelationShipDbHelper implements DBHelper{
    private static final String TAG = OrgRelationShipDbHelper.class.getName();

    public static final String TABLE_NAME = "org_relationship_";

    /**
     * create table org_relationship_
     * ( org_code_ text, user_id_ text ,type_ text ,time_ text, desc
     * primary key  ( org_code_,user_id_,type_,time_ )  )
     */
    private static final String SQL_EXEC = CREATE_TABLE + TABLE_NAME + LEFT_BRACKET +
            DBColumn.ORG_CODE + TEXT +  COMMA +
            DBColumn.USER_ID + TEXT +  COMMA +
            DBColumn.TYPE + INTEGER + COMMA +
            DBColumn.TIME + TEXT + COMMA +
            PRIMARY_KEY + LEFT_BRACKET +
            DBColumn.ORG_CODE + COMMA + DBColumn.USER_ID + COMMA + DBColumn.TYPE +
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
    public static ContentValues getContentValue(OrgRelationship relationship) {
        ContentValues cv = new ContentValues();
        cv.put(DBColumn.ORG_CODE, relationship.mOrgCode);
        cv.put(DBColumn.USER_ID, relationship.mUserId);
        cv.put(DBColumn.TYPE, relationship.mType);
        cv.put(DBColumn.TIME, relationship.mTime);
        return cv;
    }



    /**
     * 从数据库cursor中获取用户OrgOrganization对象
     */
    public static OrgRelationship fromCursor(Cursor cursor) {
        OrgRelationship orgRelationship = new OrgRelationship();
        int index = -1;
        if ((index = cursor.getColumnIndex(DBColumn.USER_ID)) != -1) {
            orgRelationship.mUserId = cursor.getString(index);
        }
        if ((index = cursor.getColumnIndex(DBColumn.ORG_CODE)) != -1) {
            orgRelationship.mOrgCode = cursor.getString(index);
        }
        if ((index = cursor.getColumnIndex(DBColumn.TYPE)) != -1) {
            orgRelationship.mType = cursor.getInt(index);
        }

        if ((index = cursor.getColumnIndex(DBColumn.TIME)) != -1) {
            orgRelationship.mTime = cursor.getLong(index);
        }

        return orgRelationship;
    }



    public class DBColumn {
        public static final String ORG_CODE = "org_code_";

        public static final String USER_ID = "user_id_";

        public static final String TYPE = "type_";

        public static final String TIME = "time_";
    }
}
