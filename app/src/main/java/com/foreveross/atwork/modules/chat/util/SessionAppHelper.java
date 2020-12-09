package com.foreveross.atwork.modules.chat.util;

import com.foreverht.db.service.repository.SessionRepository;
import com.foreveross.atwork.infrastructure.model.Session;
import com.foreveross.atwork.infrastructure.model.SessionTop;
import com.foreveross.atwork.infrastructure.model.app.App;
import com.foreveross.atwork.infrastructure.model.app.componentMode.AppComponentMode;
import com.foreveross.atwork.infrastructure.model.setting.BusinessCase;
import com.foreveross.atwork.infrastructure.model.setting.ConfigSetting;
import com.foreveross.atwork.infrastructure.model.setting.SourceType;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.modules.chat.data.ChatSessionDataWrap;
import com.foreveross.atwork.modules.configSettings.manager.ConfigSettingsManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dasunsy on 2017/8/17.
 */

public class SessionAppHelper {

    public static void handleSessionAppComponentMode(List<App> updateAppList) {

        List<ConfigSetting> configSettings = new ArrayList<>();
        for(App updateApp : updateAppList) {
            ConfigSetting configSetting = new ConfigSetting();
            configSetting.mSourceId = updateApp.getId();
            configSetting.mSourceType = SourceType.APP;
            configSetting.mBusinessCase = BusinessCase.ANNOUNCE_APP;

            if(AppComponentMode.ANNOUNCE == updateApp.getAppComponentMode()) {
                configSetting.mValue = 1;
            } else {
                configSetting.mValue = 0;

            }

            configSettings.add(configSetting);
        }

        if(!ListUtil.isEmpty(configSettings)) {
            ConfigSettingsManager.INSTANCE.setSessionSettingLocalSync(configSettings);
//            ChatSessionDataWrap.getInstance().refreshSessionMapData();
            SessionRefreshHelper.notifyRefreshSessionAndCount();

        }

    }

    public static void handleSessions(List<App> updateAppList) {

        handleSessionTop(updateAppList);
        handleSessionAppComponentMode(updateAppList);

    }

    private static void handleSessionTop(List<App> updateAppList) {
        List<Session> sessionUpdatedList = new ArrayList<>();

        for(App app : updateAppList) {
            if(app.isStickTop()) {
                handleStickTop(sessionUpdatedList, app);

            } else {
                handleNoneStickTop(sessionUpdatedList, app);

            }

        }

        if (!ListUtil.isEmpty(sessionUpdatedList)) {
            ChatSessionDataWrap.getInstance().refreshSessionMapData();
            SessionRepository.getInstance().batchUpdateSession(sessionUpdatedList);
            SessionRefreshHelper.notifyRefreshSessionAndCount();
        }
    }

    private static void handleNoneStickTop(List<Session> sessionUpdatedList, App app) {
        boolean hasUpdated = false;
        Session session = ChatSessionDataWrap.getInstance().getSessionSafely(app.mAppId, null);
        if(null != session) {

            if(SessionTop.REMOTE_TOP == session.top) {
                hasUpdated = true;
                session.top = SessionTop.NONE;
            }


            if(hasUpdated) {
                sessionUpdatedList.add(session);
            }
        }
    }

    private static void handleStickTop(List<Session> sessionUpdatedList, App app) {
        boolean hasUpdated = false;
        Session session = ChatSessionDataWrap.getInstance().getSessionSafely(app.mAppId, null);

        if (null != session) {
            if(SessionTop.REMOTE_TOP != session.top) {
                hasUpdated = true;

                session.top = SessionTop.REMOTE_TOP;

            }
        }


        if(hasUpdated) {
            sessionUpdatedList.add(session);
        }
    }

}

