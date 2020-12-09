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

import com.foreverht.db.service.W6sDatabaseHelper;
import com.foreveross.atwork.infrastructure.model.employee.MoreInfo;
import com.foreveross.atwork.infrastructure.model.discussion.TimeInfo;
import com.foreveross.atwork.infrastructure.model.discussion.Discussion;
import com.foreveross.atwork.infrastructure.model.discussion.DiscussionCreator;
import com.foreveross.atwork.infrastructure.model.discussion.DiscussionOwner;
import com.foreveross.atwork.infrastructure.utils.JsonUtil;
import com.foreveross.atwork.infrastructure.utils.Logger;
import com.google.gson.Gson;

import com.foreveross.db.SQLiteDatabase;

/**
 * Created by reyzhang22 on 16/4/3.
 */
public class DiscussionDBHelper implements DBHelper {

    private static final String TAG = DiscussionDBHelper.class.getSimpleName();

    public static final String TABLE_NAME = "discussion_";

    /**
     * create table discussion_
     * (discussion_id_ text  primary key ,
     *   domain_id text ,org_id text ,owner_code_ text ,
     *   avatar_ text ,creator_ text ,intro_ text ,
     *  more_info_ text ,name_ text ,notice_ text ,owner_ text ,time_info_ text ,type_ text  )
     */
    private static final String SQL_EXEC = CREATE_TABLE + TABLE_NAME + LEFT_BRACKET +
                                            DBColumn.DISCUSSION_ID + TEXT + PRIMARY_KEY + COMMA +
                                            DBColumn.DOMAIN_ID + TEXT + COMMA +
                                            DBColumn.ORG_ID + TEXT + COMMA +
                                            DBColumn.OWNER_CODE + TEXT + COMMA +
                                            DBColumn.AVATAR + TEXT  + COMMA +
                                            DBColumn.CREATOR + TEXT + COMMA +
                                            DBColumn.INTRO + TEXT + COMMA +
                                            DBColumn.MORE_INFO + TEXT + COMMA +
                                            DBColumn.NAME + TEXT + COMMA +
                                            DBColumn.NOTICE + TEXT + COMMA +
                                            DBColumn.PINYIN + TEXT + COMMA +
                                            DBColumn.INITIAL + TEXT + COMMA +
                                            DBColumn.OWNER + TEXT + COMMA +
                                            DBColumn.TIME_INFO + TEXT + COMMA +
                                            DBColumn.TYPE + TEXT + RIGHT_BRACKET;

    @Override
    public void onCreate(SQLiteDatabase db) {
        Logger.i(TAG, "SQL = " + SQL_EXEC);
        db.execSQL(SQL_EXEC);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion < 70){
            W6sDatabaseHelper.addColumnToTable(db, TABLE_NAME, DBColumn.PINYIN, "text");
            W6sDatabaseHelper.addColumnToTable(db, TABLE_NAME, DBColumn.INITIAL, "text");

            oldVersion = 70;
        }


        if(oldVersion < 450){
            W6sDatabaseHelper.addColumnToTable(db, TABLE_NAME, DBColumn.OWNER_CODE, "text");
            oldVersion = 450;
        }

    }

    /**
     * 获取数据库中discussion的对象
     * @param cursor
     * @return
     */
    public static Discussion fromCursor(Cursor cursor) {
        Discussion discussion = new Discussion();
        int index = -1;
        if ((index = cursor.getColumnIndex(DBColumn.NAME)) != -1) {
            discussion.mName = cursor.getString(index);
        }
        if ((index = cursor.getColumnIndex(DBColumn.AVATAR)) != -1) {
            discussion.mAvatar = cursor.getString(index);
        }
        if ((index = cursor.getColumnIndex(DBColumn.CREATOR)) != -1) {
            String creator = cursor.getString(index);
            if (!TextUtils.isEmpty(creator)) {
                discussion.mCreator = JsonUtil.fromJson(creator, DiscussionCreator.class);
            }

        }

        if ((index = cursor.getColumnIndex(DBColumn.ORG_ID)) != -1) {
            discussion.setOrgId(cursor.getString(index));
        }

        if ((index = cursor.getColumnIndex(DBColumn.OWNER_CODE)) != -1) {
            discussion.setOwnerCode(cursor.getString(index));
        }

        if ((index = cursor.getColumnIndex(DBColumn.DISCUSSION_ID)) != -1) {
            discussion.mDiscussionId = cursor.getString(index);
        }
        if ((index = cursor.getColumnIndex(DBColumn.INTRO)) != -1) {
            discussion.mIntro = cursor.getString(index);
        }
        if ((index = cursor.getColumnIndex(DBColumn.MORE_INFO)) != -1) {
            String moreInfo = cursor.getString(index);
            if (!TextUtils.isEmpty(moreInfo)) {
                discussion.mMoreInfo = JsonUtil.fromJson(moreInfo, MoreInfo.class);
            }
        }
        if ((index = cursor.getColumnIndex(DBColumn.NOTICE)) != -1) {
            discussion.mNotice = cursor.getString(index);
        }
        if ((index = cursor.getColumnIndex(DBColumn.OWNER)) != -1) {
            String owner = cursor.getString(index);
            if (!TextUtils.isEmpty(owner)) {
                discussion.mOwner = JsonUtil.fromJson(owner, DiscussionOwner.class);
            }
        }
        if ((index = cursor.getColumnIndex(DBColumn.TIME_INFO)) != -1) {
            String timeInfo = cursor.getString(index);
            if (!TextUtils.isEmpty(timeInfo)) {
                discussion.mTimerInfo = JsonUtil.fromJson(timeInfo, TimeInfo.class);
            }
        }
        if ((index = cursor.getColumnIndex(DBColumn.TYPE)) != -1) {
            discussion.mType = cursor.getString(index);
        }
        if ((index = cursor.getColumnIndex(DBColumn.DOMAIN_ID)) != -1) {
            discussion.mDomainId = cursor.getString(index);
        }

        if ((index = cursor.getColumnIndex(DBColumn.PINYIN)) != -1) {
            discussion.mPinyin = cursor.getString(index);
        }


        if ((index = cursor.getColumnIndex(DBColumn.INITIAL)) != -1) {
            discussion.mInitial = cursor.getString(index);
        }

        return discussion;
    }

    /**
     * 组建存数据库字段
     * @param discussion
     * @return
     */
    public static ContentValues getContentValues(Discussion discussion) {
        ContentValues cv = new ContentValues();
        cv.put(DBColumn.DISCUSSION_ID , discussion.mDiscussionId);
        cv.put(DBColumn.AVATAR , discussion.mAvatar);
        cv.put(DBColumn.DOMAIN_ID, discussion.mDomainId);
        cv.put(DBColumn.ORG_ID, discussion.getOrgId());
        cv.put(DBColumn.OWNER_CODE, discussion.getOrgCodeCompatible());
        cv.put(DBColumn.CREATOR , new Gson().toJson(discussion.mCreator));
        cv.put(DBColumn.INTRO , discussion.mIntro);
        cv.put(DBColumn.MORE_INFO , new Gson().toJson(discussion.mMoreInfo));
        cv.put(DBColumn.NAME , discussion.mName);
        cv.put(DBColumn.OWNER , new Gson().toJson(discussion.mOwner));
        cv.put(DBColumn.TIME_INFO , new Gson().toJson(discussion.mTimerInfo));
        cv.put(DBColumn.TYPE , discussion.mType);
        cv.put(DBColumn.NOTICE , discussion.mNotice);
        cv.put(DBColumn.PINYIN , discussion.mPinyin);
        cv.put(DBColumn.INITIAL , discussion.mInitial);
        return cv;
    }

    public class DBColumn {

        public static final String DISCUSSION_ID = "discussion_id_";

        public static final String DOMAIN_ID = "domain_id";

        public static final String ORG_ID = "org_id";

        public static final String OWNER_CODE = "owner_code_";

        public static final String NAME = "name_";

        public static final String AVATAR = "avatar_";

        public static final String TYPE = "type_";

        public static final String NOTICE = "notice_";

        public static final String INTRO = "intro_";

        public static final String CREATOR = "creator_";

        public static final String PINYIN = "pinyin_";

        public static final String INITIAL = "initial_";

        public static final String OWNER = "owner_";

        public static final String TIME_INFO = "time_info_";

        public static final String MORE_INFO = "more_info_";

    }
}
