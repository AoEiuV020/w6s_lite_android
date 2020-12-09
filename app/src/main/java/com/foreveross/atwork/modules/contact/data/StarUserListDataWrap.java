package com.foreveross.atwork.modules.contact.data;

import android.content.Context;

import com.foreveross.atwork.api.sdk.users.UserAsyncNetService;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.manager.OnlineManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * Created by lingen on 15/4/15.
 * Description:
 */
public class StarUserListDataWrap {

    private static StarUserListDataWrap starUserListDataWrap = new StarUserListDataWrap();

    public Map<String, User> contactIdentifierMap = new HashMap<>();

    public Vector<User> mUserList = new Vector<>();

    private StarUserListDataWrap() {

    }

    public static StarUserListDataWrap getInstance() {
        return starUserListDataWrap;
    }

    public boolean containsKey(String key) {
        return contactIdentifierMap.containsKey(key);
    }

    public Vector<User> getContactList() {
        return mUserList;
    }

    public void setContactList(List<User> contactList) {
        synchronized (this) {
            for (User user : contactList) {
                if (!mUserList.contains(user)) {
                    mUserList.add(user);
                }
            }
            refreshData();
        }
    }

    public void addUser(User user) {
        synchronized (this) {
            if (!mUserList.contains(user)) {
                mUserList.add(user);
                refreshData();
            }
        }

    }

    public void removeUser(User user) {
        synchronized (this) {
            if (mUserList.contains(user)) {
                mUserList.remove(user);
                refreshData();
            }

        }
    }


    private void refreshData() {
        contactIdentifierMap.clear();
        Collections.sort(mUserList);
        for (User user : mUserList) {
            contactIdentifierMap.put(user.mUserId, user);
        }
    }

    public User getUserById(String userId) {
        return contactIdentifierMap.get(userId);
    }




    public void clear() {
        this.mUserList.clear();
        refreshData();
    }

    public void checkStarUserOnline(Context context, UserAsyncNetService.OnUserOnlineListener listener) {
        if (mUserList.isEmpty()) {
            return;
        }
        List<String> checkList = new ArrayList<>();
        for (User user : mUserList) {
            checkList.add(user.mUserId);
        }
        OnlineManager.getInstance().checkOnlineList(context, checkList, listener);
    }

}
