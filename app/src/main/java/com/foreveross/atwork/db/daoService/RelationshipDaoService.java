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
 *                            |__|
 */


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

import com.foreverht.db.service.BaseDbService;
import com.foreverht.db.service.repository.RelationshipRepository;
import com.foreveross.atwork.infrastructure.model.Relationship;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.modules.contact.fragment.ContactFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * 异步关系表
 * Created by reyzhang22 on 16/4/8.
 */
public class RelationshipDaoService extends BaseDbService {

    private static final String TAG = RelationshipDaoService.class.getSimpleName();

    private static RelationshipDaoService sInstance = new RelationshipDaoService();

    private RelationshipDaoService() {

    }

    public static RelationshipDaoService getInstance() {
        return sInstance;
    }

    /**
     * 异步查询关系表，返回List数据
     * @param listener
     */
    @SuppressLint("StaticFieldLeak")
    public void queryAllRelationShips(final onRelationShipsRepositoryListener listener) {
        new AsyncTask<Void, Void, List<Relationship>>() {
            @Override
            protected List<Relationship> doInBackground(Void... params) {
                return RelationshipRepository.getInstance().queryAllRelationShips();
            }

            @Override
            protected void onPostExecute(List<Relationship> relationships) {
                if (listener == null) {
                    return;
                }
                listener.onRelationShipsCallback(relationships);
            }
        }.executeOnExecutor(mDbExecutor);

    }

    /**
     * , 首先会清空, 然后批量更新关系表
     * */
    @SuppressLint("StaticFieldLeak")
    public void batchInsertRelationshipsAndClean(Context context, final List<User> users, boolean isFriend) {

        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                if(isFriend) {
                    RelationshipRepository.getInstance().removeRelationships(String.valueOf(Relationship.Type.FRIEND));

                } else {
                    RelationshipRepository.getInstance().removeRelationships(String.valueOf(Relationship.Type.STAR));

                }

                List<Relationship> relationships = new ArrayList<>();
                for (User user: users ) {
                    Relationship relationship = new Relationship();
                    relationship.mUserId = user.mUserId;
                    if (isFriend) {
                        relationship.mType = Relationship.Type.FRIEND;
                    } else {
                        relationship.mType = Relationship.Type.STAR;
                    }
                    relationships.add(relationship);
                }
                return RelationshipRepository.getInstance().batchInsertRelationShips(relationships);
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if(result) {
                    ContactFragment.contactsDataChanged(context);
                }
            }
        }.executeOnExecutor(mDbExecutor);
    }




    /**
     * 插入一个关系
     * @param relationship
     * @return
     */
    @SuppressLint("StaticFieldLeak")
    public void insertRelationship(final Relationship relationship, final onRelationShipsRepositoryListener listener) {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                return RelationshipRepository.getInstance().insertRelationship(relationship);
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                if (listener == null) {
                    return;
                }
                if (aBoolean) {
                    listener.onRelationShipsCallback("success");
                    return;
                }
                listener.onRelationShipsCallback("fail");
            }
        }.executeOnExecutor(mDbExecutor);
    }

    @SuppressLint("StaticFieldLeak")
    public void removeRelationShip(String userId, String type) {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                return RelationshipRepository.getInstance().removeRelationship(userId, type);
            }
        }.executeOnExecutor(mDbExecutor);

    }

    public interface onRelationShipsRepositoryListener {
        void onRelationShipsCallback(Object... objects);
    }
}
