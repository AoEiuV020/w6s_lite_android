package com.foreverht.db.service.dbHelper;

import android.content.ContentValues;
import android.database.Cursor;

import com.foreverht.db.service.W6sDatabaseHelper;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.Session;
import com.foreveross.atwork.infrastructure.model.SessionType;
import com.foreveross.atwork.infrastructure.newmessage.ChatStatus;
import com.foreveross.atwork.infrastructure.utils.LogUtil;
import com.foreveross.db.SQLiteDatabase;

/**
 * Created by lingen on 15/4/15.
 * Description:
 */
public class SessionDBHelper implements DBHelper {

    public static final String TAG = SessionDBHelper.class.getSimpleName();


    public static ContentValues getContentValues(Session session) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBColumn.IDENTIFIER, session.identifier);
        contentValues.put(DBColumn.TYPE, session.type.initValue());
        contentValues.put(DBColumn.NAME, session.name);
        contentValues.put(DBColumn.LAST_MESSAGE_ID, session.lastMessageId);
        contentValues.put(DBColumn.AT_MESSAGE_ID, session.atMessageId);
        contentValues.put(DBColumn.LAST_MESSAGE_TEXT, session.lastMessageText);
        contentValues.put(DBColumn.LAST_AT_MESSAGE_TEXT, session.lastAtMessageText);
        contentValues.put(DBColumn.LAST_MESSAGE_TIMESTAMP, session.lastTimestamp);
        if (session.lastMessageStatus != null) {
            contentValues.put(DBColumn.LAST_MESSAGE_STATUS, session.lastMessageStatus.intValue());
        }
        if (session.lastMessageShowType != null) {
            contentValues.put(DBColumn.LAST_MESSAGE_SHOW_TYPE, session.lastMessageShowType.intValue());
        }
        contentValues.put(DBColumn.TOP, session.top);
        contentValues.put(DBColumn.DRAFT, session.draft);
        contentValues.put(DBColumn.ENTRY_VALUE, session.entryValue);
        contentValues.put(DBColumn.DOMAIN_ID, session.mDomainId);
        contentValues.put(DBColumn.ORG_ID, session.orgId);
        contentValues.put(DBColumn.ENTRY_TYPE, session.entryType == null ? 0 : session.entryType.intValue());
        return contentValues;
    }

    /**
     * 从数据库中获取一条SESSION
     *
     * @param cursor
     * @return
     */
    public static Session fromCursor(Cursor cursor) {
        Session session = new Session();
        int index;
        if ((index = cursor.getColumnIndex(DBColumn.IDENTIFIER)) != -1) {
            session.identifier = cursor.getString(index);
        }


        if ((index = cursor.getColumnIndex(DBColumn.TYPE)) != -1) {
            session.type = SessionType.valueOf(cursor.getInt(index));
        }

        if ((index = cursor.getColumnIndex(DBColumn.NAME)) != -1) {
            session.name = cursor.getString(index);
        }

        if ((index = cursor.getColumnIndex(DBColumn.LAST_MESSAGE_ID)) != -1) {
            session.lastMessageId = cursor.getString(index);
        }

        if ((index = cursor.getColumnIndex(DBColumn.AT_MESSAGE_ID)) != -1) {
            session.atMessageId = cursor.getString(index);
        }

        if ((index = cursor.getColumnIndex(DBColumn.LAST_AT_MESSAGE_TEXT)) != -1) {
            session.lastAtMessageText = cursor.getString(index);
        }

        if ((index = cursor.getColumnIndex(DBColumn.LAST_MESSAGE_TEXT)) != -1) {
            session.lastMessageText = cursor.getString(index);
        }

        if ((index = cursor.getColumnIndex(DBColumn.LAST_MESSAGE_TIMESTAMP)) != -1) {
            session.lastTimestamp = cursor.getLong(index);
        }

        if ((index = cursor.getColumnIndex(DBColumn.LAST_MESSAGE_STATUS)) != -1) {
            session.lastMessageStatus = ChatStatus.fromIntValue(cursor.getInt(index));
            //从数据库查询出来的发送中状态，一律修正为发送失败状态
//            if(ChatStatus.Sending.equals(session.lastMessageStatus)){
//                session.lastMessageStatus = ChatStatus.Not_Send;
//            }
        }

        if ((index = cursor.getColumnIndex(DBColumn.LAST_MESSAGE_TEXT)) != -1) {
            session.lastMessageText = cursor.getString(index);
        }

        if ((index = cursor.getColumnIndex(DBColumn.LAST_MESSAGE_SHOW_TYPE)) != -1) {
            session.lastMessageShowType = Session.ShowType.valueOf(cursor.getInt(index));
        }

        if ((index = cursor.getColumnIndex(DBColumn.TOP)) != -1) {
            session.top = cursor.getInt(index);
        }

        if ((index = cursor.getColumnIndex(DBColumn.DRAFT)) != -1) {
            session.draft = cursor.getString(index);
        }

        if ((index = cursor.getColumnIndex(DBColumn.ENTRY_TYPE)) != -1) {
            session.entryType = Session.EntryType.valueOfInt(cursor.getInt(index));
        }

        if ((index = cursor.getColumnIndex(DBColumn.ENTRY_VALUE)) != -1) {
            session.entryValue = cursor.getString(index);
        }

        if ((index = cursor.getColumnIndex(DBColumn.NEW_MESSAGE_TYPE)) != -1) {
            session.newMessageType = Session.NewMessageType.valueOfInt(cursor.getInt(index));
        }

        if ((index = cursor.getColumnIndex(DBColumn.NEW_MESSAGE_CONTENT)) != -1) {
            session.newMessageContent = cursor.getBlob(index);
        }

        if ((index = cursor.getColumnIndex(DBColumn.DOMAIN_ID)) != -1) {
            session.mDomainId = cursor.getString(index);
        }

        if ((index = cursor.getColumnIndex(DBColumn.ORG_ID)) != -1) {
            session.orgId = cursor.getString(index);
        }

        //从数据库中查出来的
        session.savedToDb = true;

        return session;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DBColumn.CREATE_TABLE_SQL);
        if (BaseApplicationLike.sIsDebug) {
            LogUtil.d(TAG, "CREATE TABLE:" + DBColumn.CREATE_TABLE_SQL);
        }
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion < 270){
            W6sDatabaseHelper.addColumnToTable(db, "session_", "entry_id_", "text");
            oldVersion = 270;
        }
    }



    public class DBColumn {

        public static final String TABLE_NAME = "session_";

        public static final String IDENTIFIER = "identifier_";

        public static final String TYPE = "type_";

        public static final String NAME = "name_";

        public static final String LAST_MESSAGE_ID = "last_message_id_";

        public static final String LAST_MESSAGE_TEXT = "last_message_text_";

        public static final String LAST_AT_MESSAGE_TEXT = "last_at_message_text_";

        public static final String LAST_MESSAGE_TIMESTAMP = "last_message_timestamp_";

        public static final String LAST_MESSAGE_STATUS = "last_message_status_";

        public static final String LAST_MESSAGE_SHOW_TYPE = "last_message_show_type_";

        public static final String AT_MESSAGE_ID = "at_message_id_";

        public static final String TOP = "top_";

        public static final String DRAFT = "draft_";

        public static final String ENTRY_ID = "entry_id_";

        public static final String ENTRY_TYPE = "entry_type_";

        public static final String ENTRY_VALUE = "entry_value_";

        public static final String NEW_MESSAGE_TYPE = "new_message_type_";

        public static final String NEW_MESSAGE_CONTENT = "new_message_content_";

        public static final String DOMAIN_ID = "domain_id_";

        public static final String ORG_ID = "org_id_";


        public static final String CREATE_TABLE_SQL = "create table session_ (identifier_ text primary key," +
                "type_ integer," +
                "name_ text," +
                "last_message_id_ text," +
                "at_message_id_ text," +
                "last_message_text_ text," +
                "last_at_message_text_ ," +
                "last_message_timestamp_ integer," +
                "last_message_status_ integer," +
                "last_message_show_type_ integer," +
                "top_ integer," +
                "entry_type_ integer," +
                "entry_value_ text," +
                "entry_id_ text," +
                "new_message_type_ integer," +
                "new_message_content_ blob," +
                "domain_id_ text," +
                "org_id_ text," +
                "draft_ text)";
    }
}
