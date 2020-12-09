package com.foreveross.atwork.modules.chat.dao;/**
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


import android.annotation.SuppressLint;
import android.os.AsyncTask;

import com.foreverht.db.service.BaseDbService;
import com.foreverht.db.service.repository.VoipMeetingRecordRepository;
import com.foreveross.atwork.infrastructure.model.voip.MeetingStatus;
import com.foreveross.atwork.infrastructure.model.voip.VoipMeetingGroup;
import com.foreveross.atwork.modules.voip.fragment.VoipHistoryFragment;

/**
 * Created by reyzhang22 on 16/2/22.
 */
public class VoipMeetingDaoService extends BaseDbService {

    private static final String TAG = VoipMeetingDaoService.class.getSimpleName();

    private static VoipMeetingDaoService sInstance = new VoipMeetingDaoService();

    public static VoipMeetingDaoService getInstance() {
        return sInstance;
    }

    /**
     * 插入会议
     * @param meetingGroup
     */
    public void insertVoipMeeting(final VoipMeetingGroup meetingGroup) {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                return VoipMeetingRecordRepository.getInstance().insertVoipMeeting(meetingGroup);
            }

        }.executeOnExecutor(mDbExecutor);

    }

    /**
     * 删除会议室
     * @param sessionId
     */
    @SuppressLint("StaticFieldLeak")
    public void removeVoipConf(final String sessionId) {
        new AsyncTask<Void, Void, Void>() {


            @Override
            protected Void doInBackground(Void... params) {
                VoipMeetingRecordRepository.getInstance().deleteVoipMeeting(sessionId);
                return null;
            }
        }.executeOnExecutor(mDbExecutor);
    }

    @SuppressLint("StaticFieldLeak")
    public void queryVoipConfById(final String sessionId, final onVoipConfOptListener listener) {
        new AsyncTask<Void, Void, VoipMeetingGroup>() {


            @Override
            protected VoipMeetingGroup doInBackground(Void... params) {

                return VoipMeetingRecordRepository.getInstance().queryVoipMeeting(sessionId);
            }

            @Override
            protected void onPostExecute(VoipMeetingGroup voipConference) {
                if (listener == null) {
                    return;
                }

                if (voipConference == null) {
                    listener.onOptFail();
                    return;
                }
                listener.onOptSuccess(voipConference);
            }
        }.executeOnExecutor(mDbExecutor);
    }

    /**
     * 更新 meeting 状态以及通话时长
     * */
    @SuppressLint("StaticFieldLeak")
    public void updateMeetingInfo(String meetingId, MeetingStatus status, long callDuration) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                boolean success = VoipMeetingRecordRepository.getInstance().updateMeetingInfo(meetingId, status, callDuration);
                if(success) {
                    VoipHistoryFragment.refresh();
                }
                return null;
            }
        }.executeOnExecutor(mDbExecutor);
    }

    public interface onVoipConfOptListener {
        void onOptSuccess(Object... objects);

        void onOptFail();
    }

}
