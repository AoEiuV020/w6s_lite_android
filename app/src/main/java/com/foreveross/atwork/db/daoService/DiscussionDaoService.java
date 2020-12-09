package com.foreveross.atwork.db.daoService;/**
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@%((((((((#&@@@((#((#@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@%(((((((((((((%@*((((@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@%(((/,/(((((((((@@@*#@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@#(((@@@((((((((((@@((@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@(((((((((((((((((((((@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@(((((((((((((((((((@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@(((((((((((((((*@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 *                       __           __
 * .--.--.--.-----.----.|  |--.-----.|  |.--.--.-----.
 * |  |  |  |  _  |   _||    <|  _  ||  ||  |  |__ --|
 * |________|_____|__|  |__|__|   __||__||_____|_____|
 * |__|
 */


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

import com.foreverht.cache.DiscussionCache;
import com.foreverht.db.service.BaseDbService;
import com.foreverht.db.service.repository.DiscussionMemberRepository;
import com.foreverht.db.service.repository.DiscussionRepository;
import com.foreveross.atwork.api.sdk.discussion.DiscussionAsyncNetService;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.discussion.Discussion;
import com.foreveross.atwork.modules.discussion.manager.DiscussionManager;

import java.util.List;

/**
 * 群组异步操作数据库服务
 * Created by reyzhang22 on 16/4/19.
 */
public class DiscussionDaoService extends BaseDbService {

    private static final String TAG = DiscussionDaoService.class.getSimpleName();

    private static DiscussionDaoService sInstance = new DiscussionDaoService();

    private DiscussionDaoService() {

    }

    public static DiscussionDaoService getInstance() {
        return sInstance;
    }

    @SuppressLint("StaticFieldLeak")
    public void getAllDiscussions(Context context, DiscussionAsyncNetService.OnDiscussionListListener listener) {
        new AsyncTask<Void, Void, List<Discussion>>() {
            @Override
            protected List<Discussion> doInBackground(Void... params) {
                List<Discussion> discussionList = DiscussionRepository.getInstance().queryAllDiscussions();
                return discussionList;
            }

            @Override
            protected void onPostExecute(List<Discussion> discussionList) {
                listener.onDiscussionSuccess(discussionList);
            }
        }.executeOnExecutor(mDbExecutor);

    }


    /**
     * 获取群组基本信息
     * @param discussionId
     * @param listener
     */
    public void queryDiscussionBasicInfo(final String discussionId, final OnQueryDiscussionListener listener) {
        new AsyncTask<Void, Void, Discussion>() {
            @Override
            protected Discussion doInBackground(Void... params) {
                return DiscussionRepository.getInstance().queryDiscussionBasicInfo(discussionId);

            }

            @Override
            protected void onPostExecute(Discussion discussion) {
                listener.onSuccess(discussion);
            }
        }.executeOnExecutor(mDbExecutor);

    }



    /**
     * 根据组织架构id查找该组织架构下的部门群
     * @param orgId
     * @param listener
     */
    public void queryLocalDiscussionByOrgId(final String orgId, final DiscussionAsyncNetService.OnDiscussionListListener listener) {
        new AsyncTask<Void, Void, List<Discussion>>() {
            @Override
            protected List<Discussion> doInBackground(Void... params) {
                return DiscussionRepository.getInstance().queryDiscussionsByOrgId(orgId);
            }

            @Override
            protected void onPostExecute(List<Discussion> discussions) {
                if (listener == null) {
                    return;
                }

                listener.onDiscussionSuccess(discussions);
            }
        }.executeOnExecutor(mDbExecutor);
    }


    public void removeDiscussion(final String discussionId) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                removeDiscussionSync(discussionId);
                return null;
            }
        }.executeOnExecutor(mDbExecutor);

    }

    public boolean removeDiscussionSync(String discussionId) {
        Discussion discussion = DiscussionManager.getInstance().queryDiscussionSync(BaseApplicationLike.baseContext, discussionId);

        boolean result = DiscussionRepository.getInstance().removeDiscussion(discussionId);
        if (result) {
            DiscussionCache.getInstance().removeDiscussionCache(discussionId);
            DiscussionManager.getInstance().removeDiscussion(discussionId);
            DiscussionMemberRepository.getInstance().removeAllDiscussionMembers(discussionId);
        }
        return result;
    }


    /**
     * 按关键字搜索群组
     *
     * @param searchValue
     * @param searchDiscussionListener
     */
    public void searchDiscussion(final String searchKey, final String searchValue, final SearchDiscussionListener searchDiscussionListener) {

        new AsyncTask<Void, Void, List<Discussion>>() {
            @Override
            protected List<Discussion> doInBackground(Void... params) {
                return DiscussionRepository.getInstance().searchDiscussion(searchValue);
            }

            @Override
            protected void onPostExecute(List<Discussion> discussionList) {
                searchDiscussionListener.searchDiscussionSuccess(searchKey, discussionList);
            }
        }.executeOnExecutor(mDbExecutor);
    }

    public void searchDiscussionInfo(final String searchKey, final String searchValue, final SearchDiscussionListener listener) {

        new AsyncTask<Void, Void, List<Discussion>>() {
            @Override
            protected List<Discussion> doInBackground(Void... params) {
                return DiscussionRepository.getInstance().searchDiscussionInfo(searchValue);
            }

            @Override
            protected void onPostExecute(List<Discussion> infoList) {
                if (listener == null) {
                    return;
                }
                listener.searchDiscussionSuccess(searchKey, infoList);
            }
        }.executeOnExecutor(mDbExecutor);
    }

    public interface SearchDiscussionListener {

        void searchDiscussionSuccess(String searchKey, List<Discussion> discussionList);
    }


    public interface OnQueryDiscussionListener {
        void onSuccess(Discussion discussion);
    }


    /**
     * 群组操作数据库 listener
     */
    public interface OnDiscussionDbListener {
        void success();

        void fail();
    }

}
