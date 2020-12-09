package com.foreverht.db.service.dbHelper;

import android.content.ContentValues;
import android.text.TextUtils;

import com.foreverht.db.service.W6sDatabaseHelper;
import com.foreverht.db.service.W6sBaseRepository;
import com.foreveross.atwork.infrastructure.utils.Logger;

import com.foreveross.db.SQLiteDatabase;

/**
 * Created by dasunsy on 16/5/12.
 */
public class UnreadMessageDbHelper implements DBHelper{
    private static final String TAG = UnreadMessageDbHelper.class.getName();

    public static final String TABLE_NAME = "unread_message_";

    /**
     * create table unread_message_
     * ( chat_id_ text, msg_id_ text , org_id_ text,
     * primary key  ( chat_id_, msg_id_ )  )
     */
    private static final String SQL_EXEC = CREATE_TABLE + TABLE_NAME + LEFT_BRACKET +
            DBColumn.CHAT_ID + TEXT +  COMMA +
            DBColumn.MSG_ID + TEXT +  COMMA +
            DBColumn.ORG_CODE + TEXT + COMMA +
            PRIMARY_KEY + LEFT_BRACKET +
            DBColumn.CHAT_ID + COMMA + DBColumn.MSG_ID +
            RIGHT_BRACKET + RIGHT_BRACKET;



    @Override
    public void onCreate(SQLiteDatabase db) {
        Logger.d(TAG, "SQL = " + SQL_EXEC);
        db.execSQL(SQL_EXEC);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 3) {
            W6sDatabaseHelper.addColumnToTable(db, TABLE_NAME, DBColumn.ORG_CODE, "text");
            oldVersion = 3;
        }
    }

    /**
     * 关系表contentValue
     * @param chatId
     * @param messageId
     * @return
     */
    public static ContentValues getContentValue(String chatId, String messageId, String orgCode) {
        ContentValues cv = new ContentValues();
        cv.put(DBColumn.CHAT_ID, chatId);
        cv.put(DBColumn.MSG_ID, messageId);
        if (!TextUtils.isEmpty(orgCode)) {
            cv.put(DBColumn.ORG_CODE, orgCode);
        }
        return cv;
    }

    public static String getDetailDBColumn(String column) {
        return W6sBaseRepository.getDetailDBColumn(TABLE_NAME, column);
    }


    public class DBColumn {
        public static final String CHAT_ID = "chat_id_";

        public static final String MSG_ID = "msg_id_";

        public static final String ORG_CODE = "org_code_";
    }
}
