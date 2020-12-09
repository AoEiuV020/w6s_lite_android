package com.foreveross.atwork.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.foreverht.cache.DiscussionCache;
import com.foreverht.cache.EmployeeCache;
import com.foreverht.cache.UserCache;
import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.discussion.DiscussionAsyncNetService;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.Employee;
import com.foreveross.atwork.infrastructure.model.discussion.Discussion;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.modules.discussion.manager.DiscussionManager;
import com.foreveross.atwork.manager.EmployeeManager;
import com.foreveross.atwork.manager.UserManager;
import com.foreveross.atwork.manager.model.SetReadableNameParams;

import java.util.UUID;

/**
 * Created by dasunsy on 2017/9/13.
 */

public class ContactShowNameHelper {



    public static String getReadableNameSync(SetReadableNameParams setReadableNameParams) {

        if (!StringUtils.isEmpty(setReadableNameParams.mOrgCode)) {
            return getEmployeeReadableNameSync(setReadableNameParams);
        }

        if(StringUtils.isEmpty(setReadableNameParams.mDiscussionId)) {
            return getUserReadableNameSync(setReadableNameParams);
        }

        return getDiscussionMemberReadableNameSync(setReadableNameParams);

    }

    private static String getDiscussionMemberReadableNameSync(SetReadableNameParams setReadableNameParams) {
        Discussion discussion = DiscussionManager.getInstance().queryDiscussionSync(BaseApplicationLike.baseContext, setReadableNameParams.mDiscussionId);
        if(null != discussion && discussion.showEmployeeInfo()) {
            setReadableNameParams.setOrgCode(discussion.getOrgCodeCompatible());
            return getEmployeeReadableNameSync(setReadableNameParams);

        }

        return getUserReadableNameSync(setReadableNameParams);
    }

    private static String getEmployeeReadableNameSync(SetReadableNameParams setReadableNameParams) {
        Employee employee = EmployeeManager.getInstance().queryEmpInSync(BaseApplicationLike.baseContext, setReadableNameParams.mUserId, setReadableNameParams.mOrgCode);

        if(null != employee) {
            return getShowText(setReadableNameParams.mTitleHolder, employee.getShowName());
        }

        if(!StringUtils.isEmpty(setReadableNameParams.mFailName)) {
            return setReadableNameParams.mFailName;
        }

        return getUserReadableNameSync(setReadableNameParams);
    }

    private static String getUserReadableNameSync(SetReadableNameParams setReadableNameParams) {
        User user = UserManager.getInstance().queryUserInSyncByUserId(BaseApplicationLike.baseContext, setReadableNameParams.mUserId, setReadableNameParams.mDomainId);
        if(null != user) {
            return getShowText(setReadableNameParams.mTitleHolder, user.getShowName());
        }

        if(!StringUtils.isEmpty(setReadableNameParams.mFailName)) {
            return setReadableNameParams.mFailName;
        }


        return getShowText(setReadableNameParams.mTitleHolder, setReadableNameParams.mUserId);

    }

    public static void setReadableNames(SetReadableNameParams setReadableNameParams) {

        if (!StringUtils.isEmpty(setReadableNameParams.mOrgCode)) {
            setEmpReadableNames(setReadableNameParams);
            return;
        }

        if(StringUtils.isEmpty(setReadableNameParams.mDiscussionId)) {
            setUserReadableNames(setReadableNameParams);
            return;
        }

        setReadableNameWithLoadDiscussion(setReadableNameParams);

    }

    private static void setReadableNameWithLoadDiscussion(SetReadableNameParams setReadableNameParams) {
        String tag = UUID.randomUUID().toString();
        setReadableNameParams.mTextView.setTag(R.id.key_name_loading, tag);

        Discussion discussionCache = DiscussionCache.getInstance().getDiscussionCache(setReadableNameParams.mDiscussionId);
        if(null != discussionCache) {
            setReadableNamesWithDiscussion(setReadableNameParams, discussionCache);
            return;
        }

        DiscussionManager.getInstance().queryDiscussion(setReadableNameParams.mTextView.getContext(), setReadableNameParams.mDiscussionId, new DiscussionAsyncNetService.OnQueryDiscussionListener() {
            @Override
            public void onSuccess(@NonNull Discussion discussion) {
                if (isLoadLegal(tag, setReadableNameParams.mTextView)) {
                    setReadableNamesWithDiscussion(setReadableNameParams, discussion);
                }
            }


            @Override
            public void networkFail(int errorCode, String errorMsg) {
                if(!ErrorHandleUtil.handleTokenError(errorCode, errorMsg)) {

                    if (isLoadLegal(tag, setReadableNameParams.mTextView)){
                        setUserReadableNames(setReadableNameParams);
                    }
                }
            }
        });
    }

    private static void setReadableNamesWithDiscussion(SetReadableNameParams setReadableNameParams, Discussion discussionCache) {
        if(discussionCache.showEmployeeInfo()) {
            setReadableNameParams.setOrgCode(discussionCache.getOrgCodeCompatible());
            setEmpReadableNames(setReadableNameParams);

        } else {
            setUserReadableNames(setReadableNameParams);

        }
    }

    /**
     * 设置显示的名字
     * */
    @SuppressLint("StaticFieldLeak")
    private static void setUserReadableNames(SetReadableNameParams setReadableNameParams) {
        TextView textView = setReadableNameParams.mTextView;
        String userId = setReadableNameParams.mUserId;
        String domainId = setReadableNameParams.mDomainId;
        String titleHolder = setReadableNameParams.mTitleHolder;
        boolean loadingHolder = setReadableNameParams.mLoadingHolder;

        final String readableNameTag = UUID.randomUUID().toString();
        textView.setTag(R.id.key_name_loading, readableNameTag);

        final Context context = textView.getContext();

        textView.setText(StringUtils.EMPTY);
        User user = UserCache.getInstance().getUserCache(userId);

        if (null != user) {
            textView.setText(getShowText(titleHolder, user.getShowName()));
            if(setReadableNameParams.neeHighlight()) {
                TextViewHelper.highlightKey(textView, user.getShowName(), setReadableNameParams.mHighlightColor);
            }

        } else {


            if(StringUtils.isEmpty(userId) || StringUtils.isEmpty(domainId)) {
                if (handleFailName(setReadableNameParams)) {
                    return;
                }

                textView.setText(getShowText(titleHolder, userId));
                return;
            }


            new AsyncTask<Void, Void, User>() {

                @Override
                protected User doInBackground(Void... params) {
                    //查询用户
                    User user = UserManager.getInstance().queryUserInSyncByUserId(context, userId, domainId);
                    return user;
                }

                @Override
                protected void onPostExecute(User user) {
                    if (isLoadLegal(readableNameTag, textView)) {

                        if (user != null) {
                            textView.setText(getShowText(titleHolder, user.getShowName()));
                            if(setReadableNameParams.neeHighlight()) {
                                TextViewHelper.highlightKey(textView, user.getShowName(), setReadableNameParams.mHighlightColor);
                            }

                            UserCache.getInstance().setUserCache(user);

                            return;
                        }

                        if (handleFailName(setReadableNameParams)) {
                            return;
                        }

                        textView.setText(getShowText(titleHolder, userId));
                    }
                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    private static boolean isLoadLegal(String readableNameTag, TextView textView) {
        return readableNameTag.equals(textView.getTag(R.id.key_name_loading));
    }


    /**
     * 设置显示的名字
     * */
    @SuppressLint("StaticFieldLeak")
    private static void setEmpReadableNames(SetReadableNameParams setReadableNameParams) {
        TextView textView = setReadableNameParams.mTextView;
        String userId = setReadableNameParams.mUserId;
        String orgCode = setReadableNameParams.mOrgCode;
        String domainId = setReadableNameParams.mDomainId;
        String titleHolder = setReadableNameParams.mTitleHolder;
        boolean loadingHolder = setReadableNameParams.mLoadingHolder;


        final String readableNameTag = UUID.randomUUID().toString();
        textView.setTag(R.id.key_name_loading, readableNameTag);

        final Context context = textView.getContext();

        textView.setText(StringUtils.EMPTY);
        Employee employee = EmployeeCache.getInstance().getEmpCache(userId, orgCode);

        if (null != employee) {
            textView.setText(getShowText(titleHolder, employee.getShowName()));
            if(setReadableNameParams.neeHighlight()) {
                TextViewHelper.highlightKey(textView, employee.getShowName(), setReadableNameParams.mHighlightColor);
            }
        } else {

            if(loadingHolder) {
                textView.setText(StringUtils.EMPTY);
            }

            new AsyncTask<Void, Void, Employee>() {
                @Override
                protected Employee doInBackground(Void... params) {
                    //查询用户
                    Employee emp = EmployeeManager.getInstance().queryEmpInSync(context, userId, orgCode);
                    return emp;
                }

                @Override
                protected void onPostExecute(Employee employee) {
                    if (isLoadLegal(readableNameTag, textView)) {

                        if (employee != null) {
                            textView.setText(getShowText(titleHolder, employee.getShowName()));
                            if(setReadableNameParams.neeHighlight()) {
                                TextViewHelper.highlightKey(textView, employee.getShowName(), setReadableNameParams.mHighlightColor);
                            }

                            return;
                        }


                        if (handleFailName(setReadableNameParams)) {
                            return;
                        }


                        setUserReadableNames(setReadableNameParams);

                    }
                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    private static boolean handleFailName(SetReadableNameParams setReadableNameParams) {
        if(!StringUtils.isEmpty(setReadableNameParams.mFailName)) {
            setReadableNameParams.mTextView.setText(getShowText(setReadableNameParams.mTitleHolder, setReadableNameParams.mFailName));
            if(setReadableNameParams.neeHighlight()) {
                TextViewHelper.highlightKey(setReadableNameParams.mTextView, setReadableNameParams.mFailName, setReadableNameParams.mHighlightColor);
            }
            return true;
        }
        return false;
    }

    private static String getShowText(String titleHolder, String name) {
        if(StringUtils.isEmpty(titleHolder)) {
            return name;
        }

        return String.format(titleHolder, name);
    }

}
