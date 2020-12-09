package com.foreveross.atwork.modules.voip.service;

import com.foreveross.atwork.infrastructure.model.voip.VoipMeetingMember;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.manager.VoipMeetingController;

import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by dasunsy on 2016/10/20.
 */

public final class SpeakingMonitor {
    private ScheduledExecutorService mSpeakingHandlingScheduled = Executors.newScheduledThreadPool(AtworkConfig.VOIP_MEMBER_COUNT_MAX);

    private HashMap<String, ScheduledFuture> mFutureResultMap = new HashMap<>();

    public void refreshTimer(String userId) {
        //first cancel the pre timer
        ScheduledFuture futureRunning = mFutureResultMap.get(userId);
        if(null != futureRunning) {
            futureRunning.cancel(true);

            mFutureResultMap.remove(userId);
        }


        ScheduledFuture future = mSpeakingHandlingScheduled.schedule(()->{
            //revert the speaking status
            VoipMeetingMember member = VoipMeetingController.getInstance().findMember(userId);
            if(null != member) {
                member.mIsSpeaking = false;

                if(null != VoipMeetingController.getInstance().getVoipStatusListener()) {
                    VoipMeetingController.getInstance().getVoipStatusListener().onUsersProfileRefresh();
                }
            }
        }, 3000, TimeUnit.MILLISECONDS);

        mFutureResultMap.put(userId, future);
    }

    public void cancelAll() {
        for (ScheduledFuture scheduledFuture : mFutureResultMap.values()) {
            scheduledFuture.cancel(true);
        }

        mFutureResultMap.clear();
    }



}