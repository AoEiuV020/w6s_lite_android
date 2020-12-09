package com.foreverht.db.service.repository;/**
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
import androidx.annotation.Nullable;

import com.foreverht.db.service.W6sBaseRepository;
import com.foreverht.db.service.dbHelper.VoipMeetingRecordDBHelper;
import com.foreveross.atwork.infrastructure.model.voip.MeetingStatus;
import com.foreveross.atwork.infrastructure.model.voip.VoipMeetingGroup;
import com.foreveross.db.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by reyzhang22 on 16/2/22.
 */
public class VoipMeetingRecordRepository extends W6sBaseRepository {

    private static final String TAG = VoipMeetingRecordRepository.class.getSimpleName();

    private static VoipMeetingRecordRepository sVoipMeetingRecordRepository = new VoipMeetingRecordRepository();

    public static VoipMeetingRecordRepository getInstance() {
        return sVoipMeetingRecordRepository;
    }

    public boolean insertVoipMeeting(VoipMeetingGroup voipMeeting) {
        boolean result;

        long resultLong = getWritableDatabase().insertWithOnConflict(
                VoipMeetingRecordDBHelper.TABLE_NAME,
                null,
                VoipMeetingRecordDBHelper.getContentValues(voipMeeting), SQLiteDatabase.CONFLICT_REPLACE);

        result = (-1 != resultLong);

        if(result) {
            result = VoipMeetingMemberRepository.getInstance().batchInsertMeetingMembers(voipMeeting.mParticipantList);
        }

        return result;
    }

    public void deleteVoipMeeting(String meetingId) {
        String removeSql = "delete from " + VoipMeetingRecordDBHelper.TABLE_NAME + " where " + VoipMeetingRecordDBHelper.DBColumn.MEETING_ID + " = ? ";
        getWritableDatabase().execSQL(removeSql, new String[]{meetingId});
    }

    /**
     * 获取语音会议的基本信息
     * */
    @Nullable
    public VoipMeetingGroup queryVoipMeeting(String meetingId) {
        String querySql = "select * from " + VoipMeetingRecordDBHelper.TABLE_NAME + " where " + VoipMeetingRecordDBHelper.DBColumn.MEETING_ID + " = ?";
        Cursor cursor = null;
        VoipMeetingGroup voipMeeting = null;
        try {
            cursor = getReadableDatabase().rawQuery(querySql, new String[]{meetingId});
            while (cursor.moveToNext()) {
                voipMeeting = VoipMeetingRecordDBHelper.fromCursor(cursor);
                voipMeeting.mParticipantList = VoipMeetingMemberRepository.getInstance().queryMeetingMemberList(voipMeeting.mMeetingId);

            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return voipMeeting;
    }

    public List<VoipMeetingGroup> queryVoipMeetingRecordList() {
        List<VoipMeetingGroup> meetingList = new ArrayList<>();
        String sql = "select * from " + VoipMeetingRecordDBHelper.TABLE_NAME  + " where " + VoipMeetingRecordDBHelper.DBColumn.ENABLE + "=1" + " order by " + VoipMeetingRecordDBHelper.DBColumn.CALL_TIME + " DESC";

        Cursor cursor = getWritableDatabase().rawQuery(sql, null);
        try {
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    VoipMeetingGroup meetingGroup = VoipMeetingRecordDBHelper.fromCursor(cursor);
                    meetingGroup.mParticipantList = VoipMeetingMemberRepository.getInstance().queryMeetingMemberList(meetingGroup.mMeetingId);

                    meetingList.add(meetingGroup);
                }

            }

        } finally {
            if (cursor != null) {
                cursor.close();
            }

        }

        Collections.sort(meetingList);

        return meetingList;

    }

    /**
     * 更新 meeting 状态以及通话时长
     * */
    public boolean updateMeetingInfo(String meetingId, MeetingStatus status, long callDuration) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(VoipMeetingRecordDBHelper.DBColumn.STATUS, status.intValue());
        contentValues.put(VoipMeetingRecordDBHelper.DBColumn.DURATION, callDuration);

        String whereArg = VoipMeetingRecordDBHelper.DBColumn.MEETING_ID + "=?";
        String whereValues[] = {meetingId};

        boolean success = 0 < getWritableDatabase().update(VoipMeetingRecordDBHelper.TABLE_NAME, contentValues, whereArg, whereValues);

        return success;
    }


}
