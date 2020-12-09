package com.foreverht.db.service.dbHelper;

import android.content.ContentValues;
import android.database.Cursor;

import com.foreveross.atwork.infrastructure.model.Session;
import com.foreveross.atwork.infrastructure.model.SessionFaultage;
import com.foreveross.atwork.infrastructure.model.SessionType;
import com.foreveross.atwork.infrastructure.model.app.App;
import com.foreveross.atwork.infrastructure.model.app.AppBundles;
import com.foreveross.atwork.infrastructure.model.app.ServiceApp;
import com.foreveross.atwork.infrastructure.model.app.appEnum.AppType;
import com.foreveross.atwork.infrastructure.newmessage.ChatStatus;
import com.foreveross.atwork.infrastructure.utils.JsonUtil;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.infrastructure.utils.Logger;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.db.SQLiteDatabase;

public class SessionFaultageRecordDBHelper implements DBHelper {

    private static final String TAG = SessionFaultageRecordDBHelper.class.getName();

    public static final String TABLE_NAME = "faultage_records_";

    /**
     * create table faultage_records_
     * ( id_ text,
     * session_id_ text,
     * last_end_time_ integer,
     * begin_time_ integer,
     * primary key  ( id_) )
     */
    private static final String SQL_EXEC = CREATE_TABLE + TABLE_NAME + LEFT_BRACKET +
            DBColumn.ID + TEXT + COMMA +
            DBColumn.SESSION_ID + TEXT + COMMA +
            DBColumn.LAST_END_TIME + INTEGER + COMMA +
            DBColumn.BEGIN_TIME + INTEGER + COMMA +
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
        if(oldVersion < 100){
            db.execSQL(SQL_EXEC);
            oldVersion = 100;
        }
    }


    public static ContentValues getContentValue(SessionFaultage sessionFaultage) {

        ContentValues cv = new ContentValues();
        cv.put(DBColumn.ID, sessionFaultage.getId());
        cv.put(DBColumn.SESSION_ID, sessionFaultage.getSessionId());
        cv.put(DBColumn.LAST_END_TIME, sessionFaultage.getSessionLastMessageTime());
        cv.put(DBColumn.BEGIN_TIME, sessionFaultage.getBeginTimeInSyncMessages());



        return cv;
    }


    public static SessionFaultage fromCursor(Cursor cursor) {
        String sessionFaultId = StringUtils.EMPTY;
        String sessionId = StringUtils.EMPTY;
        long sessionLastMessageTime = -1;
        long beginTimeInSyncMessages = -1;

        int index;
        if ((index = cursor.getColumnIndex(DBColumn.ID)) != -1) {
            sessionFaultId = cursor.getString(index);
        }

        if ((index = cursor.getColumnIndex(DBColumn.SESSION_ID)) != -1) {
            sessionId = cursor.getString(index);
        }

        if ((index = cursor.getColumnIndex(DBColumn.LAST_END_TIME)) != -1) {
            sessionLastMessageTime = cursor.getLong(index);
        }

        if ((index = cursor.getColumnIndex(DBColumn.BEGIN_TIME)) != -1) {
            beginTimeInSyncMessages = cursor.getLong(index);
        }



        return new SessionFaultage(sessionFaultId, sessionId, sessionLastMessageTime, beginTimeInSyncMessages);
    }




    public class DBColumn {


        public static final String ID = "id_";

        public static final String SESSION_ID = "session_id_";

        public static final String LAST_END_TIME = "last_end_time_";

        public static final String BEGIN_TIME = "begin_time_";



    }
}
