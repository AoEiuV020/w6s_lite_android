package com.foreverht.db.service.repository;/**
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
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ __           __
 * .--.--.--.-----.----.|  |--.-----.|  |.--.--.-----.
 * |  |  |  |  _  |   _||    <|  _  ||  ||  |  |__ --|
 * |________|_____|__|  |__|__|   __||__||_____|_____|
 * |__|
 */


import android.database.Cursor;
import androidx.annotation.NonNull;
import android.text.TextUtils;

import com.foreverht.cache.UserCache;
import com.foreverht.db.service.W6sBaseRepository;
import com.foreverht.db.service.dbHelper.RelationshipDBHelper;
import com.foreverht.db.service.dbHelper.UserDBHelper;
import com.foreveross.atwork.infrastructure.model.Relationship;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.infrastructure.utils.Logger;

import com.foreveross.db.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by reyzhang22 on 16/4/5.
 */
public class UserRepository extends W6sBaseRepository {

    private static final String TAG = UserRepository.class.getSimpleName();

    private static UserRepository sInstance = new UserRepository();

    private UserRepository() {

    }

    public static UserRepository getInstance() {
        return sInstance;
    }


    public User queryUserByUserId(String userId) {
        User user = null;
        String sql = "select * from " + UserDBHelper.TABLE_NAME + " where " + UserDBHelper.DBColumn.USER_ID + "=?";
        Logger.d(TAG, sql);
        try {
            Cursor cursor = getWritableDatabase().rawQuery(sql, new String[]{userId});
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    user = UserDBHelper.fromCursor(cursor);
                }
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return user;
    }

    /**
     * 根据用户名查询用户信息
     * @param username
     * @return
     */
    public User queryUserByUsername(String username) {
        User user = null;
        String sql = "select * from " + UserDBHelper.TABLE_NAME + " where " + UserDBHelper.DBColumn.USERNAME + "=?";
        Logger.d(TAG, sql);
        Cursor cursor = getWritableDatabase().rawQuery(sql, new String[]{username});
        if (cursor != null) {
            if (cursor.moveToNext()) {
                user = UserDBHelper.fromCursor(cursor);
            }
            cursor.close();
        }
        return user;
    }

    /**
     * 查询星标联系人
     * @return
     */
    public List<User> queryStarFlagUsers() {
        List<Relationship> relationships = RelationshipRepository.getInstance().queryStarRelationShip();
        return queryUsersByIdsSort(Relationship.toUserIdList(relationships));
    }


    /**
     * 查询好友列表
     * @return
     */
    public List<User> queryFriendUsers() {
        List<Relationship> relationships = RelationshipRepository.getInstance().queryFriendRelationShip();

        return queryUsersByIdsSort(Relationship.toUserIdList(relationships));
    }


    public List<User> queryUsersByIdsSort(List<String> userIdsList) {
        List<User> userList = new ArrayList<>();

        String sql = "select * from " + UserDBHelper.TABLE_NAME + " where  user_id_ in (" + getInStringParams(userIdsList) + ") order by " + UserDBHelper.DBColumn.INITIAL + " asc";


        SQLiteDatabase db = getWritableDatabase();

        Cursor cursor = null;
        try {
            cursor = db.rawQuery(sql, new String[]{});
            while (cursor.moveToNext()) {
                User user = UserDBHelper.fromCursor(cursor);
                userList.add(user);

            }

        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return userList;
    }

    /**
     * 根据 ids 查询 User, 不存在的 id 则构建 User
     * @param userIdsList
     * @return
     */
    public List<User> queryUsersByIdsWithNotExist(List<String> userIdsList) {
        List<User> userList = new ArrayList<>();

        Map<String, User> contactMap = new HashMap<>();

        String sql = "select * from " + UserDBHelper.TABLE_NAME + " where  user_id_ in (" + getInStringParams(userIdsList) + ")";


        SQLiteDatabase db = getWritableDatabase();

        Cursor cursor = null;
        try {
            cursor = db.rawQuery(sql, new String[]{});
            while (cursor.moveToNext()) {
                User user = UserDBHelper.fromCursor(cursor);
                contactMap.put(user.mUserId, user);
            }

            for (String userId : userIdsList) {
                if (!contactMap.containsKey(userId)) {
                    User userNotExist = new User();
                    userNotExist.mUserId = userId;
                    contactMap.put(userId, userNotExist);
                }
            }

            userList.addAll(contactMap.values());

            Collections.sort(userList);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return userList;
    }

    /**
     * 查询本地不存在的userId，以便去网络上批量获取更新
     *
     * @param userIds
     * @return
     */
    public List<String> queryUnExistUserIds(final List<String> userIds) {

        List<String> unExistsContacts = new ArrayList<>();
        String sql = "select " + UserDBHelper.DBColumn.USER_ID +
                " from " + UserDBHelper.TABLE_NAME +
                " where  " + UserDBHelper.DBColumn.USER_ID + " in (" + getInStringParams(userIds) + ")";


        Cursor cursor = null;
        List<String> existsUserIdList = new ArrayList<>();
        try {
            cursor = getReadableDatabase().rawQuery(sql, new String[]{});
            int idIndex = cursor.getColumnIndex(UserDBHelper.DBColumn.USER_ID);

            while (cursor.moveToNext()) {
                existsUserIdList.add(cursor.getString(idIndex));
            }

            for (String userId : userIds) {
                if (!existsUserIdList.contains(userId)) {
                    unExistsContacts.add(userId);
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return unExistsContacts;
    }

    /**
     * 搜索用户，主要根据username 或者 name 或者 nickname 搜索
     * @param searchValue
     * @param type -1 表示搜索所有 user, 1表示好友, 2表示星标
     * @return
     */
    @NonNull
    public List<User> searchUsers(final String searchValue, int type) {
        StringBuilder sb = new StringBuilder("select * from ");
        sb.append(UserDBHelper.TABLE_NAME);
        if (-1 != type) {
            sb.append(" inner join " + RelationshipDBHelper.TABLE_NAME + " on " + UserDBHelper.getDetailDBColumn(UserDBHelper.DBColumn.USER_ID) + "=" + RelationshipDBHelper.getDetailDBColumn(RelationshipDBHelper.DBColumn.USER_ID));
        }
        sb.append(" where (( " + UserDBHelper.getDetailDBColumn(UserDBHelper.DBColumn.PHONE) + " = ? or " + UserDBHelper.getDetailDBColumn(UserDBHelper.DBColumn.INITIAL) + " like ? or " + UserDBHelper.getDetailDBColumn(UserDBHelper.DBColumn.NAME) + " like ? or " + UserDBHelper.getDetailDBColumn(UserDBHelper.DBColumn.PINYIN) + " like ? " + " )");

        if (-1 != type) {
            sb.append(" and ( " + RelationshipDBHelper.getDetailDBColumn(RelationshipDBHelper.DBColumn.TYPE + "=" + type + ")"));
        }
        sb.append(")");
        List<User> users = new ArrayList<>();
        Cursor cursor = getReadableDatabase().rawQuery(sb.toString(), new String[]{searchValue, "%" + searchValue + "%", "%" + searchValue + "%", "%" + searchValue + "%"});
        if (cursor != null) {
            while (cursor.moveToNext()) {
                User user = UserDBHelper.fromCursor(cursor);
                users.add(user);

            }
            cursor.close();
        }
        return users;
    }

    public boolean insertUser(User user) {
        return insertUser(user, "id");
    }


    /**
     * 向数据库插入用户信息，同时更新用户缓存
     * @param user
     * @return
     */
    public boolean insertUser(User user, String keyType) {
        user.refreshLastUpdateTime();

        long insertResult = getWritableDatabase().insertWithOnConflict(
                UserDBHelper.TABLE_NAME,
                null,
                UserDBHelper.getContentValues(user),
                SQLiteDatabase.CONFLICT_REPLACE
        );

        if (insertResult == -1) {
            return false;
        }

        UserCache.getInstance().setUserCache(user, keyType);

        return true;
    }

    /**
     * 批量插入用户
     * @param users
     * @return
     */
    public boolean batchInsertUsers(List<User> users, int conflictAlgorithm) {
        for(User user: users) {
            user.refreshLastUpdateTime();
        }

        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.beginTransaction();

        for (User user : users) {
            if (TextUtils.isEmpty(user.mUserId)) {
                continue;
            }
            getWritableDatabase().insertWithOnConflict(
                    UserDBHelper.TABLE_NAME,
                    UserDBHelper.DBColumn.USER_ID,
                    UserDBHelper.getContentValues(user),
                    conflictAlgorithm);
            UserCache.getInstance().setUserCache(user);
        }
        sqLiteDatabase.setTransactionSuccessful();
        sqLiteDatabase.endTransaction();
        return true;
    }


    public boolean removeUserById(String userId) {
        try {
            String sql = "delete from " + UserDBHelper.TABLE_NAME + " where " + UserDBHelper.DBColumn.USER_ID + " = ?";
            getWritableDatabase().execSQL(sql, new String[]{userId});
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }


}
