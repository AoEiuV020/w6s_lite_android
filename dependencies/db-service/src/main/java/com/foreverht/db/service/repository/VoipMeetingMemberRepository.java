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


import android.database.Cursor;

import com.foreverht.db.service.W6sBaseRepository;
import com.foreverht.db.service.dbHelper.VoipMeetingMemberDBHelper;
import com.foreveross.atwork.infrastructure.model.voip.VoipMeetingMember;

import com.foreveross.db.SQLiteDatabase;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class VoipMeetingMemberRepository extends W6sBaseRepository {

    private static final String TAG = VoipMeetingMemberRepository.class.getSimpleName();

    private static VoipMeetingMemberRepository sVoipMeetingRecordRepository = new VoipMeetingMemberRepository();

    public static VoipMeetingMemberRepository getInstance() {
        return sVoipMeetingRecordRepository;
    }

    /**
     * 批量插入语音会议成员
     *
     * @param discussionMemberList
     * @return
     */
    public boolean batchInsertMeetingMembers(List<VoipMeetingMember> discussionMemberList) {
        boolean result = true;
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        try {
            sqLiteDatabase.beginTransaction();

            for (VoipMeetingMember meetingMember : discussionMemberList) {
                getWritableDatabase().insertWithOnConflict(
                        VoipMeetingMemberDBHelper.TABLE_NAME,
                        null,
                        VoipMeetingMemberDBHelper.getContentValues(meetingMember), SQLiteDatabase.CONFLICT_REPLACE);
            }
            sqLiteDatabase.setTransactionSuccessful();

        } catch (Exception e) {
            e.printStackTrace();
            result = false;
        } finally {
            sqLiteDatabase.endTransaction();
        }
        return result;
    }

    /**
     * 查询会议成员列表
     *
     * @param meetingId
     * @return meetingMemberList
     */
    public CopyOnWriteArrayList<VoipMeetingMember> queryMeetingMemberList(String meetingId) {
        String sql = "select * from " + VoipMeetingMemberDBHelper.TABLE_NAME + " where " + VoipMeetingMemberDBHelper.DBColumn.MEETING_ID + " = ?";
        CopyOnWriteArrayList<VoipMeetingMember> meetingMemberList = new CopyOnWriteArrayList<>();
        Cursor cursor = getWritableDatabase().rawQuery(sql, new String[]{meetingId});
        try {
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    VoipMeetingMember discussionMember = VoipMeetingMemberDBHelper.fromCursor(cursor);
                    meetingMemberList.add(discussionMember);
                }

            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return meetingMemberList;

    }


}
