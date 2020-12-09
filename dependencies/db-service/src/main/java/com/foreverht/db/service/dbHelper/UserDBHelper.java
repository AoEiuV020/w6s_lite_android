package com.foreverht.db.service.dbHelper;/**
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


import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;

import com.foreverht.db.service.W6sBaseRepository;

import com.foreverht.db.service.W6sDatabaseHelper;
import com.foreveross.atwork.infrastructure.model.employee.MoreInfo;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.infrastructure.utils.JsonUtil;
import com.foreveross.atwork.infrastructure.utils.Logger;
import com.google.gson.Gson;

import com.foreveross.db.SQLiteDatabase;

/**
 * 用户表建表
 * Created by reyzhang22 on 16/3/31.
 */
public class UserDBHelper implements DBHelper {

    private static final String TAG = UserDBHelper.class.getSimpleName();

    public static final String TABLE_NAME = "user_";

    /**
     * create table user_
     * ( user_id_ text  primary key ,domain_id_ text ,avatar_ text ,
     *   birthday_ text ,disabled_ text ,nickname_ text ,email_ text ,
     *   gender_ integer ,initial_ text ,more_info_ text ,name_ text ,
     *   phone_ text ,pinyin_ text ,status_ text ,first_letter_ text ,
     *   username_ text  )
     */
    private static final String SQL_EXEC = CREATE_TABLE + TABLE_NAME + LEFT_BRACKET +
                                            DBColumn.USER_ID + TEXT + PRIMARY_KEY + COMMA +
                                            DBColumn.DOMAIN_ID + TEXT + COMMA +
                                            DBColumn.AVATAR + TEXT + COMMA +
                                            DBColumn.BIRTHDAY + TEXT + COMMA +
                                            DBColumn.DISABLED + TEXT + COMMA +
                                            DBColumn.NICKNAME + TEXT + COMMA +
                                            DBColumn.EMAIL + TEXT + COMMA +
                                            DBColumn.GENDER + INTEGER + COMMA +
                                            DBColumn.INITIAL + TEXT + COMMA +
                                            DBColumn.MORE_INFO + TEXT + COMMA +
                                            DBColumn.NAME + TEXT + COMMA +
                                            DBColumn.PHONE + TEXT + COMMA +
                                            DBColumn.PINYIN + TEXT + COMMA +
                                            DBColumn.STATUS + TEXT + COMMA +
                                            DBColumn.FIRST_LETTER + TEXT + COMMA +
                                            DBColumn.CLOUD_AUTH_ENABLED + INTEGER + COMMA +
                                            DBColumn.CLOUD_AUTH_AVATAR + TEXT + COMMA +
                                            DBColumn.USERNAME + TEXT + COMMA +
                                            DBColumn.LAST_UPDATE_TIME + INTEGER + COMMA +
                                            DBColumn.SIGNATURE + TEXT + RIGHT_BRACKET;

    @Override
    public void onCreate(SQLiteDatabase db) {
        Logger.i(TAG, "SQL = " + SQL_EXEC);
        db.execSQL(SQL_EXEC);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion < 90) {
            W6sDatabaseHelper.addColumnToTable(db, TABLE_NAME, DBColumn.CLOUD_AUTH_ENABLED, "integer");
            W6sDatabaseHelper.addColumnToTable(db, TABLE_NAME, DBColumn.CLOUD_AUTH_AVATAR, "text");

            oldVersion = 90;
        }

        if(oldVersion < 130) {
            W6sDatabaseHelper.addColumnToTable(db, TABLE_NAME, DBColumn.SIGNATURE, "text");

            oldVersion = 130;
        }

        if(oldVersion < 150) {
            W6sDatabaseHelper.addColumnToTable(db, TABLE_NAME, DBColumn.LAST_UPDATE_TIME, "text");

            oldVersion = 150;
        }

    }

    /**
     * 从数据库cursor中获取用户user对象
     * @param cursor
     * @return
     */
    public static User fromCursor(Cursor cursor) {
        User user = new User();
        int index = -1;
        if ((index = cursor.getColumnIndex(DBColumn.NAME)) != -1) {
            user.mName = cursor.getString(index);
        }
        if ((index = cursor.getColumnIndex(DBColumn.AVATAR)) != -1) {
            user.mAvatar = cursor.getString(index);
        }
        if ((index = cursor.getColumnIndex(DBColumn.BIRTHDAY)) != -1) {
            user.mBirthday = cursor.getString(index);
        }
        if ((index = cursor.getColumnIndex(DBColumn.DISABLED)) != -1) {
            user.mDisabled = cursor.getString(index);
        }
        if ((index = cursor.getColumnIndex(DBColumn.DOMAIN_ID)) != -1) {
            user.mDomainId = cursor.getString(index);
        }
        if ((index = cursor.getColumnIndex(DBColumn.EMAIL)) != -1) {
            user.mEmail = cursor.getString(index);
        }
        if ((index = cursor.getColumnIndex(DBColumn.GENDER)) != -1) {
            user.mGender = cursor.getString(index);
        }
        if ((index = cursor.getColumnIndex(DBColumn.INITIAL)) != -1) {
            user.mInitial = cursor.getString(index);
        }
        if ((index = cursor.getColumnIndex(DBColumn.MORE_INFO)) != -1) {
            String moreInfo = cursor.getString(index);
            if (!TextUtils.isEmpty(moreInfo)) {
                user.mMoreInfo = JsonUtil.fromJson(moreInfo, MoreInfo.class);
            }
        }

        if ((index = cursor.getColumnIndex(DBColumn.USERNAME)) != -1) {
            user.mUsername = cursor.getString(index);
        }

        if ((index = cursor.getColumnIndex(DBColumn.NICKNAME)) != -1) {
            user.mNickname = cursor.getString(index);
        }
        if ((index = cursor.getColumnIndex(DBColumn.PHONE)) != -1) {
            user.mPhone = cursor.getString(index);
        }
        if ((index = cursor.getColumnIndex(DBColumn.DISABLED)) != -1) {
            user.mDisabled = cursor.getString(index);
        }
        if ((index = cursor.getColumnIndex(DBColumn.USER_ID)) != -1) {
            user.mUserId = cursor.getString(index);
        }
        if ((index = cursor.getColumnIndex(DBColumn.PINYIN)) != -1) {
            user.mPinyin = cursor.getString(index);
        }

        if ((index = cursor.getColumnIndex(DBColumn.STATUS)) != -1) {
            user.mStatus = cursor.getString(index);
        }

        if ((index = cursor.getColumnIndex(DBColumn.GENDER)) != -1) {
            user.mGender = cursor.getString(index);
        }
        if ((index = cursor.getColumnIndex(DBColumn.FIRST_LETTER)) != -1) {
            user.mFirstLetter = cursor.getString(index);
        }

        if ((index = cursor.getColumnIndex(DBColumn.CLOUD_AUTH_ENABLED)) != -1) {
            user.mCloudAuthEnabled =  (1 == cursor.getInt(index));
        }

        if ((index = cursor.getColumnIndex(DBColumn.CLOUD_AUTH_AVATAR)) != -1) {
            user.mCloudAuthAvatar = cursor.getString(index);
        }

        if ((index = cursor.getColumnIndex(DBColumn.SIGNATURE)) != -1) {
            user.mSignature = cursor.getString(index);
        }


        return user;
    }

    /**
     * 通过User对象获取User数据库contentValues
     * @param user
     * @return
     */
    public static ContentValues getContentValues(User user) {
        ContentValues cv = new ContentValues();
        cv.put(DBColumn.AVATAR, user.mAvatar);
        cv.put(DBColumn.BIRTHDAY, user.mBirthday);
        cv.put(DBColumn.DISABLED, user.mDisabled);
        cv.put(DBColumn.DOMAIN_ID, user.mDomainId);
        cv.put(DBColumn.EMAIL, user.mEmail);
        cv.put(DBColumn.GENDER, user.mGender);
        cv.put(DBColumn.INITIAL, user.mInitial);
        cv.put(DBColumn.MORE_INFO, new Gson().toJson(user.mMoreInfo));
        cv.put(DBColumn.NAME, user.mName);
        cv.put(DBColumn.NICKNAME, user.mNickname);
        cv.put(DBColumn.PHONE, user.mPhone);
        cv.put(DBColumn.PINYIN, user.mPinyin);
        cv.put(DBColumn.STATUS, user.mStatus);
        cv.put(DBColumn.USER_ID, user.mUserId);
        cv.put(DBColumn.USERNAME, user.mUsername);
        cv.put(DBColumn.FIRST_LETTER, user.mFirstLetter);
        if(user.mCloudAuthEnabled) {
            cv.put(DBColumn.CLOUD_AUTH_ENABLED, 1);
        } else {
            cv.put(DBColumn.CLOUD_AUTH_ENABLED, 0);

        }
        cv.put(DBColumn.CLOUD_AUTH_AVATAR, user.mCloudAuthAvatar);
        cv.put(DBColumn.SIGNATURE, user.mSignature);
        cv.put(DBColumn.LAST_UPDATE_TIME, user.mLastUpdateTime);
        return cv;
    }

    public static String getDetailDBColumn(String column) {
        return W6sBaseRepository.getDetailDBColumn(TABLE_NAME, column);
    }

    public class DBColumn {

        public static final String USER_ID = "user_id_";

        public static final String DOMAIN_ID = "domain_id_";

        public static final String USERNAME = "username_";

        public static final String NAME = "name_";

        public static final String NICKNAME = "nickname_";

        public static final String PINYIN = "pinyin_";

        public static final String INITIAL = "initial_";

        public static final String AVATAR = "avatar_";

        public static final String PHONE = "phone_";

        public static final String EMAIL = "email_";

        public static final String GENDER = "gender_";

        public static final String BIRTHDAY = "birthday_";

        public static final String MORE_INFO = "more_info_";

        public static final String STATUS = "status_";

        public static final String DISABLED = "disabled_";

        public static final String FIRST_LETTER = "first_letter_";

        public static final String CLOUD_AUTH_ENABLED = "cloud_auth_enabled_";

        public static final String CLOUD_AUTH_AVATAR = "cloud_auth_avatar_";

        public static final String SIGNATURE = "signature_";

        public static final String LAST_UPDATE_TIME = "last_update_time_";
    }
}
