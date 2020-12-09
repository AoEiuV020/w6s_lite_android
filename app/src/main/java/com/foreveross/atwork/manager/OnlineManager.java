package com.foreveross.atwork.manager;

/**
 * Created by dasunsy on 2016/11/26.
 */

import android.content.Context;
import android.os.AsyncTask;

import com.foreverht.threadGear.AsyncTaskThreadPool;
import com.foreveross.atwork.api.sdk.net.HttpResult;
import com.foreveross.atwork.api.sdk.users.UserAsyncNetService;
import com.foreveross.atwork.api.sdk.users.UserSyncNetService;
import com.foreveross.atwork.api.sdk.users.requestJson.UserOnlineStatusRequestJson;
import com.foreveross.atwork.api.sdk.users.responseJson.UserOnlineStatusResponse;
import com.foreveross.atwork.api.sdk.util.NetWorkHttpResultHelper;
import com.foreveross.atwork.infrastructure.manager.DomainSettingsManager;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.utils.ErrorHandleUtil;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 在线离线管理
 * */
public class OnlineManager {

    private static final Object sLock = new Object();

    private static OnlineManager sInstance = null;

    private HashMap<String, Boolean> mOnlineOfflinePool = new HashMap<>();

    private OnlineManager() {

    }

    public static OnlineManager getInstance() {
        if(null == sInstance) {
            //double check
            synchronized (sLock) {
                if (null == sInstance) {
                    sInstance = new OnlineManager();
                }
            }
        }

        return sInstance;
    }

    /**
     * 返回是否在线的状态, 默认在线
     * */
    public boolean isOnline(String userId) {
        if(mOnlineOfflinePool.containsKey(userId)) {
            return mOnlineOfflinePool.get(userId);
        }

        return true;
    }

    public void setOnlineStatus(String userId, boolean isOnline) {
        mOnlineOfflinePool.put(userId, isOnline);
    }

    public void update(List<String> onlineList, List<String> offlineList) {
        for(String userId : onlineList) {
            setOnlineStatus(userId, true);
        }

        for(String userId : offlineList) {
            setOnlineStatus(userId, false);

        }

    }




    public void checkOnline(Context context, String userId, UserAsyncNetService.OnUserOnlineListener listener) {
        List<String> singleList = new ArrayList<>();
        singleList.add(userId);

        checkOnlineList(context, singleList, listener);
    }

    public void checkOnlineList(Context context, List<String> checkList, UserAsyncNetService.OnUserOnlineListener listener) {
        if (!DomainSettingsManager.getInstance().handleUserOnlineFeature() || ListUtil.isEmpty(checkList)) {
            return;
        }

        new AsyncTask<Void, Void, List<String>>() {
            @Override
            protected List<String> doInBackground(Void... params) {

                List<String> onlineList = new ArrayList<>();
                List<String> subList = new ArrayList<>();

                for (int i = 0; i < checkList.size(); i++) {
                    if (subList.size() == 100) {
                        List<String> reqList = new ArrayList<>();
                        reqList.addAll(0, subList);
                        checkOnlineSubListSync(context, onlineList, reqList);

                        subList.clear();
                    }
                    subList.add(checkList.get(i));
                }

                checkOnlineSubListSync(context, onlineList, subList);

                return onlineList;
            }

            @Override
            protected void onPostExecute(List<String> onlineList) {
                listener.onOnlineList(onlineList);
            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());


    }

    /**
     * 分批处理在线离线接口
     * @param context
     * @param onlineList
     * @param reqList
     * */
    public void checkOnlineSubListSync(Context context, List<String> onlineList, List<String> reqList) {
        HttpResult httpResult = checkOnlineSubListSync(context, reqList);

        if(httpResult.isRequestSuccess()) {
            String resultText = NetWorkHttpResultHelper.getResultText(httpResult.result);
            UserOnlineStatusResponse response = new Gson().fromJson(resultText, UserOnlineStatusResponse.class);
            onlineList.addAll(response.mOnlineUsers);


            update(response.mOnlineUsers, response.mOffLineUsers);

        } else {
            //做 token 认证
            if(null != httpResult.resultResponse) {
                ErrorHandleUtil.handleTokenError(httpResult.resultResponse.status, httpResult.resultResponse.message);
            }
        }

    }

    private HttpResult checkOnlineSubListSync(Context context, List<String> subList) {
        UserOnlineStatusRequestJson json = new UserOnlineStatusRequestJson();
        json.mUserIds = subList;
        String jsonParam = new Gson().toJson(json);
        HttpResult httpResult = UserSyncNetService.getInstance().getUserOnlineStatus(context, jsonParam);

        return httpResult;
    }

}

