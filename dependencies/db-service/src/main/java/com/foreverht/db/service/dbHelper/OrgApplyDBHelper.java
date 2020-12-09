package com.foreverht.db.service.dbHelper;

import android.content.ContentValues;
import android.database.Cursor;

import com.foreveross.atwork.infrastructure.model.orgization.OrgApply;
import com.foreveross.atwork.infrastructure.utils.Logger;

import com.foreveross.db.SQLiteDatabase;

/**
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
 *
 * 组织申请数据库
 * Created by reyzhang22 on 16/8/16.
 */
public class OrgApplyDBHelper implements DBHelper {

    public static final String TAG = OrgApplyDBHelper.class.getSimpleName();

    public static final String TABLE_NAME = "org_apply_";

    public static final String SQL_EXEC = CREATE_TABLE + TABLE_NAME + LEFT_BRACKET +
                                DBColumn.ORG_CODE + TEXT + PRIMARY_KEY + COMMA +
                                DBColumn.MSG_ID + TEXT + COMMA +
                                DBColumn.LAST_MSG_TEXT + TEXT + COMMA +
                                DBColumn.LAST_MSG_TIME + INTEGER + COMMA +
                                DBColumn.EXTENSION1 + TEXT + COMMA +
                                DBColumn.EXTENSION2 + TEXT + RIGHT_BRACKET;

    @Override
    public void onCreate(SQLiteDatabase db) {
        Logger.i(TAG, SQL_EXEC);
        db.execSQL(SQL_EXEC);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 4) {
            db.execSQL(SQL_EXEC);
            oldVersion = 4;
        }
    }

    public static ContentValues getContentValues (OrgApply orgApply) {
        ContentValues cv = new ContentValues();
        cv.put(DBColumn.ORG_CODE, orgApply.mOrgCode);
        cv.put(DBColumn.MSG_ID, orgApply.mMsgId);
        cv.put(DBColumn.LAST_MSG_TEXT, orgApply.mLastMsgText);
        cv.put(DBColumn.LAST_MSG_TIME, orgApply.mLastMsgTime);
        cv.put(DBColumn.EXTENSION1, orgApply.mExtendsion1);
        cv.put(DBColumn.EXTENSION2, orgApply.mExtendsion2);
        return cv;
    }

    public static OrgApply fromCursor(Cursor cursor) {
        OrgApply orgApply = new OrgApply();
        int index = -1;
        if ((index = cursor.getColumnIndex(DBColumn.ORG_CODE)) != -1) {
            orgApply.mOrgCode = cursor.getString(index);
        }

        if ((index = cursor.getColumnIndex(DBColumn.MSG_ID)) != -1)  {
            orgApply.mMsgId = cursor.getString(index);
        }

        if ((index = cursor.getColumnIndex(DBColumn.LAST_MSG_TEXT)) != -1) {
            orgApply.mLastMsgText = cursor.getString(index);
        }

        if ((index = cursor.getColumnIndex(DBColumn.LAST_MSG_TIME)) != -1) {
            orgApply.mLastMsgTime = cursor.getLong(index);
        }

        if ((index = cursor.getColumnIndex(DBColumn.EXTENSION1)) != -1) {
            orgApply.mExtendsion1 = cursor.getString(index);
        }

        if ((index = cursor.getColumnIndex(DBColumn.EXTENSION2)) != -1) {
            orgApply.mExtendsion2 = cursor.getString(index);
        }
        return orgApply;

    }

    public static class DBColumn {

        public static final String ORG_CODE = "org_code_";

        public static final String MSG_ID = "msg_id_";

        public static final String LAST_MSG_TEXT = "last_msg_text_";

        public static final String LAST_MSG_TIME = "last_msg_time_";

        public static final String EXTENSION1 = "extension1_";

        public static final String EXTENSION2 = "extension2_";

    }
}
