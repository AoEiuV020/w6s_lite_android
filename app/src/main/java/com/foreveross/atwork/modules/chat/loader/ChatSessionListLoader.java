package com.foreveross.atwork.modules.chat.loader;

import android.content.Context;

import androidx.loader.content.AsyncTaskLoader;

import com.foreveross.atwork.infrastructure.manager.DomainSettingsManager;
import com.foreveross.atwork.infrastructure.model.Session;
import com.foreveross.atwork.modules.chat.data.ChatSessionDataWrap;
import com.foreveross.atwork.modules.chat.service.ChatService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lingen on 15/4/16.
 * Description:
 */
public class ChatSessionListLoader extends AsyncTaskLoader<List<Session>> {

    public ChatSessionListLoader(Context context) {
        super(context);
    }

    @Override
    public List<Session> loadInBackground() {
        List<Session> sessions = new ArrayList<>();
        sessions.addAll(ChatSessionDataWrap.getInstance().getSessions());
        if (sessions.size() == 0) {
            if (DomainSettingsManager.getInstance().handleFriendsRelationshipsFeature() && DomainSettingsManager.getInstance().handleEmailSettingsFeature()
                    && DomainSettingsManager.getInstance().handleOrgApplyFeature()) {
                sessions = ChatService.queryAllSessionsDb();
            } else {
                sessions = ChatService.queryFilteredSessionsFromDb();
            }

            ChatSessionDataWrap.getInstance().setSessionList(sessions);
        }
        return sessions;
    }
}
