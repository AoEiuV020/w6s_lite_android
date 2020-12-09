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
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ __           __
 * .--.--.--.-----.----.|  |--.-----.|  |.--.--.-----.
 * |  |  |  |  _  |   _||    <|  _  ||  ||  |  |__ --|
 * |________|_____|__|  |__|__|   __||__||_____|_____|
 * |__|
 */


import android.content.ContentValues;
import android.database.Cursor;

import com.foreveross.atwork.infrastructure.model.voip.VoipMeetingMember;
import com.foreveross.atwork.infrastructure.utils.Logger;

import com.foreveross.db.SQLiteDatabase;


public class VoipMeetingMemberDBHelper implements DBHelper {

    private static final String TAG = VoipMeetingMemberDBHelper.class.getSimpleName();

    public static final String TABLE_NAME = "conference_member_";

    /**
     * create table conference_member_ (
     * meeting_id_ text ,user_id_ text ,
     * join_time_ text , name_ text , avatar_ text ,status_ text
     * primary key  ( meeting_id_, user_id_ )  )
     */
    private static final String SQL_EXEC = CREATE_TABLE + TABLE_NAME + LEFT_BRACKET +
            DBColumn.MEETING_ID + TEXT + COMMA +
            DBColumn.USER_ID + TEXT + COMMA +
            DBColumn.DOMAIN_ID + TEXT + COMMA +
            DBColumn.JOIN_TIME + TEXT + COMMA +
            DBColumn.NAME + TEXT + COMMA +
            DBColumn.AVATAR + TEXT + COMMA +
            DBColumn.STATUS + TEXT + COMMA +
            PRIMARY_KEY + LEFT_BRACKET +
            DBColumn.MEETING_ID + COMMA + DBColumn.USER_ID +
            RIGHT_BRACKET + RIGHT_BRACKET;

    @Override
    public void onCreate(SQLiteDatabase db) {
        Logger.d(TAG, SQL_EXEC);
        db.execSQL(SQL_EXEC);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 5) {
            db.execSQL(SQL_EXEC);
            oldVersion = 5;
        }

    }

    public static VoipMeetingMember fromCursor(Cursor cursor) {
        VoipMeetingMember member = new VoipMeetingMember();
        int index = -1;
        if ((index = cursor.getColumnIndex(DBColumn.MEETING_ID)) != -1) {
            member.mMeetingId = cursor.getString(index);
        }
        if ((index = cursor.getColumnIndex(DBColumn.USER_ID)) != -1) {
            member.mUserId = cursor.getString(index);
        }

        if ((index = cursor.getColumnIndex(DBColumn.DOMAIN_ID)) != -1) {
            member.mDomainId = cursor.getString(index);
        }

        if ((index = cursor.getColumnIndex(DBColumn.JOIN_TIME)) != -1) {
            member.mJoinTime = Long.valueOf(cursor.getString(index));
        }

        if ((index = cursor.getColumnIndex(DBColumn.NAME)) != -1) {
            member.mShowName = cursor.getString(index);
        }

        if ((index = cursor.getColumnIndex(DBColumn.AVATAR)) != -1) {
            member.mAvatar = cursor.getString(index);
        }

        if ((index = cursor.getColumnIndex(DBColumn.STATUS)) != -1) {
            member.mMeetingStatus = cursor.getString(index);
        }

        return member;
    }

    public static ContentValues getContentValues(VoipMeetingMember member) {
        ContentValues cv = new ContentValues();
        cv.put(DBColumn.MEETING_ID, member.mMeetingId);
        cv.put(DBColumn.USER_ID, member.mUserId);
        cv.put(DBColumn.DOMAIN_ID, member.mDomainId);
        cv.put(DBColumn.JOIN_TIME, member.mJoinTime);
        cv.put(DBColumn.NAME, member.mShowName);
        cv.put(DBColumn.AVATAR, member.mAvatar);
        cv.put(DBColumn.STATUS, member.mMeetingStatus);
        return cv;
    }

    public class DBColumn {

        public static final String MEETING_ID = "meeting_id_";

        public static final String USER_ID = "user_id_";

        public static final String DOMAIN_ID = "domain_id_";

        public static final String AVATAR = "avatar_";

        public static final String NAME = "name_";

        public static final String JOIN_TIME = "join_time_";

        public static final String STATUS = "status_";

    }
}
