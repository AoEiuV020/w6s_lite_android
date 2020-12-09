package com.foreveross.atwork.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.foreverht.db.service.repository.EmployeeRepository;
import com.foreverht.db.service.repository.UserRepository;
import com.foreverht.threadGear.AsyncTaskThreadPool;
import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.Employee;
import com.foreveross.atwork.infrastructure.model.ShowListItem;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo;
import com.foreveross.atwork.infrastructure.utils.LogUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.manager.EmployeeManager;
import com.foreveross.atwork.manager.UserManager;
import com.foreveross.atwork.manager.listener.BaseQueryListener;

import java.util.List;

/**
 * Created by dasunsy on 2017/5/11.
 */

public class ContactQueryHelper {


    public static void getCurrentContactMobile(BaseQueryListener<String> baseQueryListener) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                return getCurrentContactMobileSync();
            }

            @Override
            protected void onPostExecute(String mobile) {
                baseQueryListener.onSuccess(mobile);
            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }

    public static String getCurrentContactMobileSync() {
        User loginUser = AtworkApplicationLike.getLoginUserSync();
        if (null != loginUser) {
            return getMobileSync(loginUser);
        }

        return StringUtils.EMPTY;
    }

    public static String getMobileSync(@NonNull User user) {
        if (!StringUtils.isEmpty(user.mPhone)) {
            return user.mPhone;
        }

        String currentOrgCode = PersonalShareInfo.getInstance().getCurrentOrg(BaseApplicationLike.baseContext);

        Employee currentEmp = null;
        if (!StringUtils.isEmpty(currentOrgCode)) {
            currentEmp = EmployeeManager.getInstance().queryEmpInSync(BaseApplicationLike.baseContext, user.mUserId, currentOrgCode);
        }

        if (null != currentEmp) {
            return currentEmp.mobile;
        }

        return StringUtils.EMPTY;
    }

    /**
     * 批量本地查询用户信息(user 或者 employe), ps :缓存->本地, 不查网络
     */
    @SuppressLint("StaticFieldLeak")
    public static void queryLocalContacts(final List<String> userIdList, @Nullable final String orgCode, BaseQueryListener<List<? extends ShowListItem>> listener) {
        new AsyncTask<Void, Void, List<? extends ShowListItem>>() {
            @Override
            protected List<? extends ShowListItem> doInBackground(Void... params) {
                return queryLocalContactsSync(orgCode, userIdList);

            }

            @Override
            protected void onPostExecute(List<? extends ShowListItem> showListItems) {
                listener.onSuccess(showListItems);
            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());

    }

    public static List<? extends ShowListItem> queryLocalContactsSync(@Nullable String orgCode, List<String> userIdList) {
        if (StringUtils.isEmpty(orgCode)) {
            return UserManager.getInstance().batchQueryLocalUsers(userIdList);


        } else {
            List<Employee> employeeList = EmployeeManager.getInstance().batchQueryLocalEmpList(userIdList, orgCode);

            long start = System.currentTimeMillis();
            makeInternalDiscussionMemberAvatarCompatible(employeeList, userIdList);

            long end = System.currentTimeMillis();
            LogUtil.e("花费时间 : " + (end - start));

            return employeeList;
        }
    }


    /**
     * 批量查询用户信息(user 或者 employee, 并携带身份信息), 本地不存在的信息, 会去网络同步更新
     *
     * @param context
     * @param userIdList
     * @param orgCode    为空时, 则查询 user 信息, 反之查询employee 信息
     * @param listener
     */
    @SuppressLint("StaticFieldLeak")
    public static void queryContactsWithParticipant(Context context, final List<String> userIdList, @Nullable final String orgCode, BaseQueryListener<List<? extends ShowListItem>> listener) {
        new AsyncTask<Void, Void, List<? extends ShowListItem>>() {
            @Override
            protected List<? extends ShowListItem> doInBackground(Void... params) {
                return queryContactsWithParticipantBySync(context, userIdList, orgCode);

            }

            @Override
            protected void onPostExecute(List<? extends ShowListItem> showListItems) {
                listener.onSuccess(showListItems);
            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }

    public static List<? extends ShowListItem> queryContactsWithParticipantBySync(Context context, List<String> userIdList, @Nullable String orgCode) {
        if (StringUtils.isEmpty(orgCode)) {
            UserManager.getInstance().syncNotExistUsersSync(context, userIdList);
            return UserRepository.getInstance().queryUsersByIdsWithNotExist(userIdList);


        } else {
            EmployeeManager.getInstance().syncNotExistEmpListSync(context, userIdList, orgCode);
            List<Employee> employeeList = EmployeeRepository.getInstance().queryEmpListByIdsWithNotExist(userIdList, orgCode);

            //使用雇员身份
            for (Employee employee : employeeList) {
                employee.setEmpParticipant();
            }
            long start = System.currentTimeMillis();
            makeInternalDiscussionMemberAvatarCompatible(employeeList, userIdList);

            long end = System.currentTimeMillis();
            LogUtil.e("花费时间 : " + (end - start));

            return employeeList;
        }
    }

    @SuppressLint("StaticFieldLeak")
    public static void queryLoginContact(@Nullable final String orgCode, BaseQueryListener<ShowListItem> listener) {
        new AsyncTask<Void, Void, ShowListItem>() {
            @Override
            protected ShowListItem doInBackground(Void... params) {
                if (StringUtils.isEmpty(orgCode)) {
                    return AtworkApplicationLike.getLoginUserSync();
                } else {
                    Employee employee = AtworkApplicationLike.getLoginUserEmpSync(orgCode);
                    if (null != employee) {
                        employee.setEmpParticipant();
                    }
                    return employee;
                }
            }

            @Override
            protected void onPostExecute(ShowListItem showListItems) {
                listener.onSuccess(showListItems);
            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());

    }

    @SuppressLint("StaticFieldLeak")
    public static void queryContact(Context context, @Nullable final String orgCode, @Nullable String domainId, String userId, BaseQueryListener<ShowListItem> listener) {
        new AsyncTask<Void, Void, ShowListItem>() {
            @Override
            protected ShowListItem doInBackground(Void... params) {
                if (StringUtils.isEmpty(orgCode)) {
                    return UserManager.getInstance().queryUserInSyncByUserId(context, userId, domainId);
                } else {
                    return EmployeeManager.getInstance().queryEmpInSync(context, userId, orgCode);
                }
            }

            @Override
            protected void onPostExecute(ShowListItem showListItems) {
                listener.onSuccess(showListItems);
            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());

    }

    private static void makeInternalDiscussionMemberAvatarCompatible(List<Employee> empList, final List<String> userIdList) {
        List<User> userList = UserRepository.getInstance().queryUsersByIdsWithNotExist(userIdList);
        for (Employee employee : empList) {

            if (StringUtils.isEmpty(employee.avatar)) {

                for (User user : userList) {
                    if (user.mUserId.equalsIgnoreCase(employee.userId)) {
                        employee.avatar = user.mAvatar;
                        break;
                    }
                }
            }
        }
    }


}
