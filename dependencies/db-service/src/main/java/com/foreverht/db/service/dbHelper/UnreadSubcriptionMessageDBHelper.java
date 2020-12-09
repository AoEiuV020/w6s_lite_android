package com.foreverht.db.service.dbHelper;

import android.content.ContentValues;
import android.database.Cursor;

import com.foreveross.atwork.infrastructure.model.newsSummary.UnreadNewsSummaryData;
import com.foreveross.atwork.infrastructure.utils.LogUtil;
import com.foreveross.db.BaseDatabaseHelper;
import com.foreveross.db.SQLiteDatabase;

public class UnreadSubcriptionMessageDBHelper implements DBHelper {

    private static final String TAG = UnreadSubcriptionMessageDBHelper.class.getSimpleName();
    public static final String TABLE_NAME = "unread_subcription_message_";

    public static ContentValues getContentValues(String appId, String msgID, String deliveryTime) {
        ContentValues values = new ContentValues();
        if (null != appId) {
            values.put(UnreadSubcriptionMessageDBHelper.DBColumn.APP_ID, appId);

        }

        if (null != msgID) {
            values.put(UnreadSubcriptionMessageDBHelper.DBColumn.MSG_ID, msgID);

        }
        if (deliveryTime != null) {
            values.put(UnreadSubcriptionMessageDBHelper.DBColumn.DELIVERY_TIME, deliveryTime);

        }
        return values;
    }

    /**
     * 从CURSOR中读取一个消息
     *
     * @param cursor
     * @return
     */
    public static UnreadNewsSummaryData fromCursor(Cursor cursor) {
        UnreadNewsSummaryData message = new UnreadNewsSummaryData();
        String appIdRaw = cursor.getString(cursor.getColumnIndex(DBColumn.APP_ID));
        String msgIdRaw = cursor.getString(cursor.getColumnIndex(DBColumn.MSG_ID));
        String deliveryTimeRaw = cursor.getString(cursor.getColumnIndex(DBColumn.DELIVERY_TIME));
        message.setAppId(appIdRaw);
        message.setMasId(msgIdRaw);
        message.setDeliveryTime(deliveryTimeRaw);

        return message;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String systemTable = String.format(DBColumn.CREATE_TABLE_SQL, TABLE_NAME);
        db.execSQL(systemTable);
        LogUtil.d(TAG, systemTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if(200 > oldVersion){
            String systemTable = String.format(DBColumn.CREATE_TABLE_SQL, TABLE_NAME);
            BaseDatabaseHelper.createTable(db, systemTable);
            oldVersion = 200;
        }


        /*if (oldVersion < 83) {
            try {
                String systemTable = String.format(DBColumn.CREATE_TABLE_SQL, TABLE_NAME);
                db.execSQL(systemTable);
            } catch (Exception e) {
                e.printStackTrace();
            }
            oldVersion = 83;
        }*/

    }

    public class DBColumn {

        /**
         * 应用ID
         */
        public static final String APP_ID = "app_id_";

        /**
         * 消息id
         */
        public static final String MSG_ID = "msg_id_";

        /**
         * 消息时间
         */
        public static final String DELIVERY_TIME = "delivery_time_";

        public static final String CREATE_TABLE_SQL = "create table %s (msg_id_ text primary key," +
                "app_id_ text," +
                "delivery_time_ text)";
    }

}
