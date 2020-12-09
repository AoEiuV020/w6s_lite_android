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
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ __           __
 * .--.--.--.-----.----.|  |--.-----.|  |.--.--.-----.
 * |  |  |  |  _  |   _||    <|  _  ||  ||  |  |__ --|
 * |________|_____|__|  |__|__|   __||__||_____|_____|
 * |__|
 */


import android.content.ContentValues;
import android.database.Cursor;

import com.foreverht.db.service.W6sDatabaseHelper;
import com.foreveross.atwork.infrastructure.model.discussion.DiscussionMember;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.infrastructure.utils.Logger;

import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.db.BaseDatabaseHelper;
import com.foreveross.db.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by reyzhang22 on 16/4/5.
 */
public class DiscussionMemberDBHelper implements DBHelper {

    private static final String TAG = DiscussionMemberDBHelper.class.getSimpleName();

    public static final String TABLE_NAME = "discussion_member_";

    /**
     * create table discussion_member_ (
     * discussion_id_ text ,user_id_ text ,
     * join_time_ text , name_ text , avatar_ text , tags text ,
     * primary key  ( discussion_id_,user_id_ )  )
     */
    private static final String SQL_EXEC = CREATE_TABLE + TABLE_NAME + LEFT_BRACKET +
            DBColumn.DISCUSSION_ID + TEXT + COMMA +
            DBColumn.USER_ID + TEXT + COMMA +
            DBColumn.JOIN_TIME + TEXT + COMMA +
            DBColumn.NAME + TEXT + COMMA +
            DBColumn.AVATAR + TEXT + COMMA +
            DBColumn.TAGS + TEXT + COMMA +
            PRIMARY_KEY + LEFT_BRACKET +
            DBColumn.DISCUSSION_ID + COMMA + DBColumn.USER_ID +
            RIGHT_BRACKET + RIGHT_BRACKET;

    @Override
    public void onCreate(SQLiteDatabase db) {
        Logger.d(TAG, SQL_EXEC);
        db.execSQL(SQL_EXEC);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(450 > oldVersion) {
            BaseDatabaseHelper.addColumnToTable(db, TABLE_NAME, DBColumn.TAGS, "text");
            oldVersion = 450;
        }
    }

    public static DiscussionMember fromCursor(Cursor cursor) {
        DiscussionMember member = new DiscussionMember();
        int index = -1;
        if ((index = cursor.getColumnIndex(DBColumn.DISCUSSION_ID)) != -1) {
            member.discussionId = cursor.getString(index);
        }
        if ((index = cursor.getColumnIndex(DBColumn.USER_ID)) != -1) {
            member.userId = cursor.getString(index);
        }
        if ((index = cursor.getColumnIndex(DBColumn.JOIN_TIME)) != -1) {
            member.joinTime = cursor.getString(index);
        }

        if ((index = cursor.getColumnIndex(DBColumn.NAME)) != -1) {
            member.name = cursor.getString(index);
        }

        if ((index = cursor.getColumnIndex(DBColumn.AVATAR)) != -1) {
            member.avatar = cursor.getString(index);
        }

        if ((index = cursor.getColumnIndex(DBColumn.TAGS)) != -1) {
            String tagsStr = cursor.getString(index);
            if(!StringUtils.isEmpty(tagsStr)) {
                String[] tagArray = tagsStr.split(",");
                member.tags = Arrays.stream(tagArray).filter(value -> !StringUtils.isEmpty(value)).collect(Collectors.toList());
            }

        }

        return member;
    }

    public static ContentValues getContentValues(DiscussionMember discussionMember) {
        ContentValues cv = new ContentValues();
        cv.put(DBColumn.DISCUSSION_ID, discussionMember.discussionId);
        cv.put(DBColumn.USER_ID, discussionMember.userId);
        cv.put(DBColumn.JOIN_TIME, discussionMember.joinTime);
        cv.put(DBColumn.NAME, discussionMember.name);
        cv.put(DBColumn.AVATAR, discussionMember.avatar);
        cv.put(DBColumn.TAGS, StringUtils.appendSeparatorStr(discussionMember.tags, ","));

        return cv;
    }

    public class DBColumn {

        public static final String DISCUSSION_ID = "discussion_id_";

        public static final String USER_ID = "user_id_";

        public static final String JOIN_TIME = "join_time_";

        public static final String NAME = "name_";

        public static final String AVATAR = "avatar_";

        public static final String TAGS = "tags_";
    }
}
