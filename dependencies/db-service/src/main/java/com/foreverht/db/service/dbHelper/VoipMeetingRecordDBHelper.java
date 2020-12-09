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

import com.foreveross.atwork.infrastructure.model.user.UserHandleInfo;
import com.foreveross.atwork.infrastructure.model.voip.MeetingInfo;
import com.foreveross.atwork.infrastructure.model.voip.MeetingStatus;
import com.foreveross.atwork.infrastructure.model.voip.VoipMeetingGroup;
import com.foreveross.atwork.infrastructure.model.voip.VoipType;
import com.foreveross.atwork.infrastructure.utils.JsonUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;

import com.foreveross.db.SQLiteDatabase;

public class VoipMeetingRecordDBHelper implements DBHelper {

    private static final String TAG = VoipMeetingRecordDBHelper.class.getSimpleName();

    public static final String TABLE_NAME = "meeting_record_";

    /**
     * create table conference_record_
     * ( meeting_id_ text  primary key ,creator_ text , avatar_ text, session_id_ text ,
     *   session_type text ,call_time_ text , status_ integer, duration_ text, mode_ text, enable_ integer )
     */
    private static final String SQL_EXEC = CREATE_TABLE + TABLE_NAME + LEFT_BRACKET +
            DBColumn.MEETING_ID + TEXT + PRIMARY_KEY + COMMA +
            DBColumn.CREATOR + TEXT + COMMA +
            DBColumn.AVATAR + TEXT + COMMA +
            DBColumn.SESSION_ID + TEXT + COMMA +
            DBColumn.SESSION_TYPE + TEXT + COMMA +
            DBColumn.CALL_TIME + TEXT + COMMA +
            DBColumn.STATUS + INTEGER + COMMA +
            DBColumn.DURATION + TEXT + COMMA +
            DBColumn.MODE + TEXT + COMMA +
            DBColumn.ENABLE + INTEGER +
            RIGHT_BRACKET;


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_EXEC);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 5) {
            db.execSQL(SQL_EXEC);
            oldVersion = 5;
        }

    }

    public static ContentValues getContentValues(VoipMeetingGroup voipConference) {
        ContentValues cv = new ContentValues();
        cv.put(DBColumn.MEETING_ID, voipConference.mMeetingId);
        cv.put(DBColumn.CREATOR, JsonUtil.toJson(voipConference.mCreator));
        cv.put(DBColumn.AVATAR, voipConference.mAvatar);
        if(null != voipConference.mMeetingInfo) {
            cv.put(DBColumn.SESSION_TYPE, voipConference.mMeetingInfo.mType.toString());
            cv.put(DBColumn.SESSION_ID, voipConference.mMeetingInfo.mId);
            cv.put(DBColumn.MODE, voipConference.mVoipType.toString());
        }

        cv.put(DBColumn.DURATION, voipConference.mDuration);
        cv.put(DBColumn.CALL_TIME, voipConference.mCallTime);
        cv.put(DBColumn.STATUS, voipConference.mStatus.intValue());
        int intEnable;
        if(voipConference.mEnable) {
            intEnable = 1;
        } else {
            intEnable = 0;

        }
        cv.put(DBColumn.ENABLE, intEnable);

        return cv;
    }

    public static VoipMeetingGroup fromCursor(Cursor cursor) {
        VoipMeetingGroup voipConference = new VoipMeetingGroup();
        int index = -1;

        if ((index = cursor.getColumnIndex(DBColumn.MEETING_ID)) != -1) {
            voipConference.mMeetingId = cursor.getString(index);
        }

        if ((index = cursor.getColumnIndex(DBColumn.CREATOR)) != -1) {
            voipConference.mCreator = JsonUtil.fromJson(cursor.getString(index), UserHandleInfo.class);
        }

        if ((index = cursor.getColumnIndex(DBColumn.AVATAR)) != -1) {
            voipConference.mAvatar = cursor.getString(index);
        }

        if ((index = cursor.getColumnIndex(DBColumn.SESSION_TYPE)) != -1) {
            String sessionType = cursor.getString(index);
            if(!StringUtils.isEmpty(sessionType)) {
                voipConference.mMeetingInfo = new MeetingInfo();
                voipConference.mMeetingInfo.mType = MeetingInfo.Type.valueOf(sessionType);

                if ((index = cursor.getColumnIndex(DBColumn.SESSION_ID)) != -1) {
                    voipConference.mMeetingInfo.mId = cursor.getString(index);
                }

                if ((index = cursor.getColumnIndex(DBColumn.MODE)) != -1) {
                    voipConference.mVoipType = VoipType.valueOf(cursor.getString(index));
                }
            }
        }



        if ((index = cursor.getColumnIndex(DBColumn.CALL_TIME)) != -1) {
            voipConference.mCallTime = cursor.getLong(index);
        }

        if ((index = cursor.getColumnIndex(DBColumn.DURATION)) != -1) {
            voipConference.mDuration = cursor.getLong(index);
        }

        if ((index = cursor.getColumnIndex(DBColumn.STATUS)) != -1) {
            voipConference.mStatus = MeetingStatus.valueOf(cursor.getInt(index));
        }

        if ((index = cursor.getColumnIndex(DBColumn.ENABLE)) != -1) {
            int intEnable = cursor.getInt(index);
            voipConference.mEnable = 1 == intEnable;
        }


        return voipConference;
    }

    public static class DBColumn {

        public static final String MEETING_ID = "meeting_id_";

        public static final String CREATOR = "creator_";

        public static final String AVATAR = "avatar_";

        public static final String DURATION = "duration_";

        public static final String CALL_TIME = "call_time_";

        public static final String SESSION_TYPE = "session_type_";

        public static final String SESSION_ID = "session_id_";

        public static final String STATUS = "status_";

        public static final String MODE = "mode_";

        public static final String ENABLE = "enable_";

    }
}
