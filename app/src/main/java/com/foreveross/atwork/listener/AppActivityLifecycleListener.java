package com.foreveross.atwork.listener;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.WindowManager;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.foreverht.cache.MessageCache;
import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.Session;
import com.foreveross.atwork.infrastructure.model.SessionType;
import com.foreveross.atwork.infrastructure.model.biometricAuthentication.BiometricAuthenticationProtectItemType;
import com.foreveross.atwork.infrastructure.model.biometricAuthentication.IBiometricAuthenticationProtected;
import com.foreveross.atwork.infrastructure.model.chat.SystemChatMessage;
import com.foreveross.atwork.infrastructure.shared.CommonShareInfo;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.infrastructure.utils.LogUtil;
import com.foreveross.atwork.manager.DropboxManager;
import com.foreveross.atwork.manager.MessageNoticeManager;
import com.foreveross.atwork.modules.chat.data.ChatSessionDataWrap;
import com.foreveross.atwork.modules.chat.model.EntrySessionRequest;
import com.foreveross.atwork.modules.chat.util.SystemChatMessageHelper;
import com.foreveross.atwork.modules.route.manager.ActivityStack;
import com.foreveross.atwork.modules.route.model.ActivityInfo;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by dasunsy on 2018/3/15.
 */

public class AppActivityLifecycleListener implements Application.ActivityLifecycleCallbacks {

    private static final String TAG = "ActivityLifecycleListener";

    private int mRefCount = 0;

    private ScheduledExecutorService mTryHideService = Executors.newScheduledThreadPool(1);
    private ScheduledFuture mScheduledFuture;

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {
        LogUtil.e(TAG, "onActivityCreated  ~~~~~~~~~~~   activity : " + activity.getLocalClassName());

        if(!AtworkConfig.KPPA_VERIFY_CONFIG.getCanScreenCapture()) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        }

        Intent intent = activity.getIntent();
        String from = "default";
        List<String> tags = new ArrayList<>();

        if (null != intent) {
            if (intent.hasExtra(ActivityInfo.INTENT_DATA_KEY_FROM)) {
                from = intent.getStringExtra(ActivityInfo.INTENT_DATA_KEY_FROM);
            }

        }

        assembleIntentTags(intent, tags);

        assembleProtectedActivityTags(activity, tags);

        ActivityInfo activityInfo = new ActivityInfo(activity.hashCode(), activity.getLocalClassName(), tags, from);

        ActivityStack.INSTANCE.getActivityInfoStack().add(activityInfo);
        LogUtil.e(TAG, "activity stack -> "  + ActivityStack.INSTANCE.getActivityInfoStack());

    }

    private void assembleIntentTags(Intent intent, List<String> tags) {
        if(null != intent) {
            if(intent.hasExtra(ActivityInfo.INTENT_DATA_KEY_TAG)) {
                String tag = intent.getStringExtra(ActivityInfo.INTENT_DATA_KEY_TAG);
                tags.add(tag);

            } else if(intent.hasExtra(ActivityInfo.INTENT_DATA_KEY_TAGS)) {
                List<String> tagsReceiving = intent.getStringArrayListExtra(ActivityInfo.INTENT_DATA_KEY_TAGS);
                tags.addAll(tagsReceiving);

            }
        }
    }

    private void assembleProtectedActivityTags(Activity activity, List<String> tags) {
        if(ListUtil.isEmpty(tags)) {

            if(activity instanceof IBiometricAuthenticationProtected) {
                IBiometricAuthenticationProtected elementProtected = (IBiometricAuthenticationProtected) activity;
                BiometricAuthenticationProtectItemType type = elementProtected.getBiometricAuthenticationProtectItemTag();
                if (null == type) {

                    BiometricAuthenticationProtectItemType[] types = elementProtected.getBiometricAuthenticationProtectTags();
                    if(null != types) {
                        for(BiometricAuthenticationProtectItemType typeEach: types) {
                            tags.add(typeEach.transferToActivityTag());

                        }
                    }

                } else {
                    tags.add(type.transferToActivityTag());
                }


            }
        }
    }

    @Override
    public void onActivityStarted(Activity activity) {
        int preCount = mRefCount;

        mRefCount++;

        LogUtil.e(TAG, "onActivityStarted  ~~~~~~~~~~~   count : " + mRefCount);

        if(0 == preCount) {


            if(0 >= EnterLeaveAppListener.CHECK_DELAY_GAP) {
                if(1 <= mRefCount) {
                    onEnterApp();

                }
            }


        }

    }

    private void checkEnterAppDelay() {
        cancel();

        mScheduledFuture = mTryHideService.schedule(() -> {
            if(1 <= mRefCount) {
                onEnterApp();

            }
        }, EnterLeaveAppListener.CHECK_DELAY_GAP, TimeUnit.MILLISECONDS);
    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

        int preCount = mRefCount;

        mRefCount--;

        LogUtil.e(TAG, "onActivityStopped  ~~~~~~~~~~~ mRefCount : " + mRefCount);


        if (1 == preCount) {

            if(0 >= EnterLeaveAppListener.CHECK_DELAY_GAP) {
                if(0 >= mRefCount) {
                    onLeaveApp();

                }

            }


        }
    }


    private void checkLeaveAppDelay() {
        cancel();

        mScheduledFuture = mTryHideService.schedule(() -> {
            if(0 >= mRefCount) {
                onLeaveApp();

            }
        }, EnterLeaveAppListener.CHECK_DELAY_GAP, TimeUnit.MILLISECONDS);
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        LogUtil.e(TAG, "onActivityDestroyed  ~~~~~~~~~~~   activity : " + activity.getLocalClassName());

        ActivityStack.INSTANCE.removeActivityInfo(activity.hashCode());
        LogUtil.e(TAG, "activity stack -> "  + ActivityStack.INSTANCE.getActivityInfoStack());

    }

    private void onEnterApp() {
        LogUtil.e(TAG, "setAppGoBack  ~~~~~~~~~~~   count : " + mRefCount);
        if (!TextUtils.isEmpty(LoginUserInfo.getInstance().getLoginUserAccessToken(AtworkApplicationLike.baseContext))) {
            DropboxManager.getInstance().getIsGoingOverdueDropboxes(dropboxList -> {
                if (dropboxList.size() <= 0) {
                    return;
                }
                SystemChatMessage systemChatMessage = SystemChatMessageHelper.createDropboxIsGoingOverdueMsg(dropboxList.size());
                EntrySessionRequest  request = new EntrySessionRequest();
                request.mName = AtworkApplicationLike.baseContext.getString(R.string.share_file_notice);
                request.mChatType = SessionType.Notice;
                request.mDomainId = AtworkConfig.DOMAIN_ID;
                request.mIdentifier = Session.DROPBOX_OVERDUE_REMIND;
                request.mMessage = systemChatMessage;
                request.mUpdateDb = true;
                request.mChatType = SessionType.Notice;
                Session session = ChatSessionDataWrap.getInstance().entrySession(request);
                ChatSessionDataWrap.getInstance().updateSession(session, systemChatMessage, true, true, false);
                MessageNoticeManager.getInstance().showChatMsgNotification(BaseApplicationLike.baseContext, systemChatMessage, session, false);
                //存入消息缓存队列
                MessageCache.getInstance().receiveSystemMessage(systemChatMessage, Session.DROPBOX_OVERDUE_REMIND);
            });
        }

        notifyActivity(EnterLeaveAppListener.ENTER);

    }


    private void onLeaveApp() {
        LogUtil.e(TAG, "setAppGoBackGround  ~~~~~~~~~~~");

        CommonShareInfo.catchKeyHomeBtnAndScreenLock(AtworkApplicationLike.baseContext);

        notifyActivity(EnterLeaveAppListener.LEAVE);
    }

    private void notifyActivity(int enter) {
        Intent intent = new Intent(EnterLeaveAppListener.ACTION_ENTER_LEAVE_APP);
        intent.putExtra(EnterLeaveAppListener.DATA_ENTER_LEAVE, enter);
        LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).sendBroadcast(intent);
    }


    private void cancel() {
        if(null != mScheduledFuture) {
            mScheduledFuture.cancel(true);
            mScheduledFuture = null;
        }
    }
}

