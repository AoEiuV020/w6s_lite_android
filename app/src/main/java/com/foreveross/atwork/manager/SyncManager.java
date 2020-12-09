package com.foreveross.atwork.manager;

import android.content.Context;

import androidx.annotation.NonNull;

import com.foreverht.threadGear.AsyncTaskThreadPool;
import com.foreveross.atwork.api.sdk.BaseNetWorkListener;
import com.foreveross.atwork.api.sdk.auth.LoginAsyncNetService;
import com.foreveross.atwork.api.sdk.users.UserAsyncNetService;
import com.foreveross.atwork.api.sdk.users.responseJson.ContactSyncItemJson;
import com.foreveross.atwork.api.sdk.users.responseJson.ContactSyncResponse;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.discussion.Discussion;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.infrastructure.shared.LoginSyncStatus;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.modules.discussion.manager.DiscussionManager;
import com.foreveross.atwork.modules.configSettings.manager.ConfigSettingsManager;
import com.foreveross.atwork.utils.ErrorHandleUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by dasunsy on 2017/1/17.
 */

public class SyncManager {
    public static boolean SYNCING = false;

    public Set<Type> mSyncingType = new HashSet<>();

    private boolean mSyncingLoginUser = false;

    private static final Object sLock = new Object();

    public static SyncManager sInstance = null;

    public static SyncManager getInstance() {
        /**
         * double check
         * */
        if (null == sInstance) {
            synchronized (sLock) {
                if (null == sInstance) {
                    sInstance = new SyncManager();
                }

            }
        }

        return sInstance;
    }


    public void checkSyncStatus(Context context) {

        if(!LoginUserInfo.getInstance().isLogin(context)) {
            return;
        }

        if(!SyncManager.SYNCING) {

            clearSyncTypeSet();

            LoginSyncStatus syncStatus = LoginUserInfo.getInstance().getLoginSyncStatus(context);
            LoginAsyncNetService service = new LoginAsyncNetService(context);

            if(!syncStatus.mDiscussionResult) {
                SyncManager.SYNCING = true;

                mSyncingType.add(Type.DISCUSSION);

            }

            if(!syncStatus.mStarContactResult) {
                SyncManager.SYNCING = true;

                mSyncingType.add(Type.FLAT_CONTACT);

            }


            if(!syncStatus.mDiscussionResult) {
                service.syncDiscussions(new BaseNetWorkListener<List<Discussion>>() {
                    @Override
                    public void onSuccess(List<Discussion> discussions) {
                        removeSyncType(Type.DISCUSSION);
                        LoginUserInfo.getInstance().setDiscussionSyncStatus(context, true);

                        DiscussionManager.getInstance().syncInsertDiscussions(context, discussions);

                    }

                    @Override
                    public void networkFail(int errorCode, String errorMsg) {
                        removeSyncType(Type.DISCUSSION);

                        ErrorHandleUtil.handleTokenError(errorCode, errorMsg);
                    }
                });
            }

            if(!syncStatus.mStarContactResult) {
                service.syncFlagContacts(new BaseNetWorkListener<ContactSyncResponse>() {
                    @Override
                    public void onSuccess(ContactSyncResponse contactSyncResponse) {
                        removeSyncType(Type.FLAT_CONTACT);
                        LoginUserInfo.getInstance().setStarContactSyncStatus(context, true);

                        List<User> flagList = new ArrayList<>();
                        flagList.addAll(ContactSyncItemJson.toUserList(contactSyncResponse.result));

                        UserManager.getInstance().asyncAddUserRelationShipToLocal(context, flagList, false);


                    }

                    @Override
                    public void networkFail(int errorCode, String errorMsg) {
                        removeSyncType(Type.FLAT_CONTACT);

                        ErrorHandleUtil.handleTokenError(errorCode, errorMsg);
                    }
                });

            }


        }

        ConfigSettingsManager.INSTANCE.syncConfigSettings();
    }

    public void checkLoginUserSyncing() {
        if(mSyncingLoginUser) {
            return;
        }

        mSyncingLoginUser = true;

        AsyncTaskThreadPool.getInstance().execute(() -> {
            final Context context = BaseApplicationLike.baseContext;

            User loginUser = UserManager.getInstance().queryLocalUser(LoginUserInfo.getInstance().getLoginUserId(context));
            if(null != loginUser) {
                mSyncingLoginUser = false;
            }


            if(null == loginUser) {

                UserManager.getInstance().asyncFetchLoginUserFromRemote(context, new UserAsyncNetService.OnQueryUserListener() {
                    @Override
                    public void networkFail(int errorCode, String errorMsg) {
                        ErrorHandleUtil.handleTokenError(errorCode, errorMsg);

                        mSyncingLoginUser = false;
                    }

                    @Override
                    public void onSuccess(@NonNull User loginUser) {

                        if (User.isYou(context, loginUser.mUserId)) {
                            LoginUserInfo.getInstance().setLoginUserBasic(context, loginUser.mUserId, loginUser.mDomainId, null, loginUser.mUsername, loginUser.mName, loginUser.mAvatar, loginUser.mSignature);
                        }
                        UserManager.getInstance().asyncAddUserToLocal(loginUser);

                        mSyncingLoginUser = false;

                    }


                });

            }
        });
    }

    public void removeSyncType(Type type) {
        mSyncingType.remove(type);
        if(mSyncingType.isEmpty()) {
            SYNCING = false;
        }
    }

    public void clearSyncTypeSet() {
        mSyncingType.clear();
    }

    enum Type {
        DISCUSSION, FLAT_CONTACT, CONVERSATION_SETTING
    }
}
