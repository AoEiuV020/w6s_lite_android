package com.foreverht.db.service.dbHelper;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;

import com.foreveross.atwork.infrastructure.model.employee.Position;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.db.SQLiteDatabase;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.w6s.model.incomingCall.IncomingCaller;

import java.io.File;
import java.util.List;

/**
 * create by reyzhang22 at 2019-08-19
 */
public class EmpIncomingCallDBHelper implements DBHelper {

    public static final String TABLE_NAME = "emp_incoming_call_";

    public static final String SQL_EXEC = CREATE_TABLE + TABLE_NAME + LEFT_BRACKET
            + DBColumn.MOBILE + TEXT + PRIMARY_KEY + COMMA
            + DBColumn.NAME + TEXT + COMMA
            + DBColumn.ROOT_ORG_NAME + TEXT + COMMA
            + DBColumn.JOB_TITLE + TEXT + COMMA
            + DBColumn.ORG_NAME + TEXT + COMMA
            + DBColumn.CORP_NAME + TEXT + COMMA
            + DBColumn.FULL_NAME_PATH + TEXT + COMMA
            + DBColumn.EXTENSION1 + TEXT + COMMA
            + DBColumn.EXTENSION2 + TEXT
            + RIGHT_BRACKET;

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_EXEC);
        db.execSQL("create index if not exists index_" + DBColumn.MOBILE + " on " + TABLE_NAME + "(" + DBColumn.MOBILE + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public static ContentValues getContentValues(IncomingCaller caller) {
        ContentValues cv = new ContentValues();
        cv.put(DBColumn.NAME, caller.getName());
        cv.put(DBColumn.MOBILE, caller.getMobile());
        cv.put(DBColumn.ROOT_ORG_NAME, caller.getRootOrgName());
        if (!ListUtil.isEmpty(caller.getPositions())) {
            Position position = caller.getPositions().get(0);
            cv.put(DBColumn.JOB_TITLE, position.jobTitle);
            cv.put(DBColumn.ORG_NAME, position.orgName);
            cv.put(DBColumn.CORP_NAME, position.corpName);
            cv.put(DBColumn.FULL_NAME_PATH, position.fullNamePath);
        }

        return cv;
    }

    public static IncomingCaller fromCursor(Cursor cursor) {
        IncomingCaller caller = new IncomingCaller();
        if (cursor.getCount() == 0) {
            return caller;
        }
        int index = -1;
        cursor.moveToFirst();
        if ((index = cursor.getColumnIndex(DBColumn.NAME)) != -1) {
            caller.setName(cursor.getString(index));
        }
        if ((index = cursor.getColumnIndex(DBColumn.MOBILE)) != -1) {
            caller.setMobile(cursor.getString(index));
        }
        if ((index = cursor.getColumnIndex(DBColumn.ROOT_ORG_NAME)) != -1) {
            caller.setRootOrgName(cursor.getString(index));
        }
//        if ((index = cursor.getColumnIndex(DBColumn.POSITIONS)) != -1) {
//            caller.setPositions(cursor.getString(index));
//        }
        if ((index = cursor.getColumnIndex(DBColumn.JOB_TITLE)) != -1) {
            if (!TextUtils.isEmpty(cursor.getString(index))) {
                caller.setJobTitle(cursor.getString(index));
            }

        }
        if ((index = cursor.getColumnIndex(DBColumn.ORG_NAME)) != -1) {
            if (!TextUtils.isEmpty(cursor.getString(index))) {
                caller.setOrgName(cursor.getString(index));
            }

        }
        if ((index = cursor.getColumnIndex(DBColumn.CORP_NAME)) != -1) {
            if (!TextUtils.isEmpty(cursor.getString(index))) {
                caller.setCorpName(cursor.getString(index));
            }

        }
        if ((index = cursor.getColumnIndex(DBColumn.FULL_NAME_PATH)) != -1) {
            if (!TextUtils.isEmpty(cursor.getString(index))) {
                caller.setFullNamePath(cursor.getString(index));
            }

        }


        return caller;
    }


    public static class DBColumn {

        public static String NAME = "name_";

        public static String MOBILE = "mobile_";

        public static String ROOT_ORG_NAME = "root_org_name_";

        public static String JOB_TITLE = "job_title_";

        public static String ORG_NAME = "org_name_";

        public static String CORP_NAME = "corp_name_";

        public static String FULL_NAME_PATH = "full_name_path_";

        public static String EXTENSION1 = "ext1_";

        public static String EXTENSION2 = "ext2_";
    }
}
