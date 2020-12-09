package com.foreveross.atwork.modules.voip.loader;

import android.content.Context;

import androidx.loader.content.AsyncTaskLoader;

import com.foreverht.db.service.repository.VoipMeetingRecordRepository;
import com.foreveross.atwork.infrastructure.model.voip.VoipMeetingGroup;

import java.util.List;

/**
 * Created by lingen on 15/4/16.
 * Description:
 */
public class VoipMeetingRecordListLoader extends AsyncTaskLoader<List<VoipMeetingGroup>> {

    public VoipMeetingRecordListLoader(Context context) {
        super(context);
    }

    @Override
    public List<VoipMeetingGroup> loadInBackground() {
        List<VoipMeetingGroup> voipMeetingGroups = VoipMeetingRecordRepository.getInstance().queryVoipMeetingRecordList();
        return voipMeetingGroups;
    }
}
