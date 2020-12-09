package com.foreverht.db.service.dbHelper;

import android.content.ContentValues;
import android.database.Cursor;
import androidx.annotation.NonNull;
import android.text.TextUtils;

import com.foreverht.db.service.W6sBaseRepository;
import com.foreverht.db.service.W6sDatabaseHelper;
import com.foreveross.atwork.infrastructure.model.Employee;
import com.foreveross.atwork.infrastructure.model.employee.MoreInfo;
import com.foreveross.atwork.infrastructure.model.employee.Position;
import com.foreveross.atwork.infrastructure.model.employee.Settings;
import com.foreveross.atwork.infrastructure.utils.JsonUtil;
import com.foreveross.atwork.infrastructure.utils.Logger;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import com.foreveross.db.SQLiteDatabase;

import java.util.List;

/**
 * Created by shadow on 2016/4/22.
 */
public class EmployeeDBHelper implements DBHelper {

    private static final String TAG = EmployeeDBHelper.class.getSimpleName();

    public static final String TABLE_NAME = "employee_";



    /**
     * create table employee_ (
     * employee_id_ text  primary key ,user_id_ text ,domain_id_ text , username_ text,
     * org_code_ text , avatar_ text, status_ text, employee_type_id_ integer ,employee_type_ text ,name_ text , alias_ text , initial_ text ,
     * sort_order_ integer ,senior_ integer ,nickname_ text ,gender_ integer ,pinyin_ text ,more_info_ text ,
     * settings_ text ,mobile_ text ,tel_ text , work_phone_ text, fax_ text , email_ text ,create_ integer , birthday_ text, last_modified_ integer ,disabled_ integer ,positions_ blob  )
     */
    private static final String SQL_EXEC = CREATE_TABLE + TABLE_NAME + LEFT_BRACKET +
            DBColumn.EMPLOYEE_ID + TEXT + PRIMARY_KEY + COMMA +
            DBColumn.USER_ID + TEXT + COMMA +
            DBColumn.DOMAIN_ID + TEXT + COMMA +
            DBColumn.USERNAME + TEXT + COMMA +
            DBColumn.ORG_CODE + TEXT + COMMA +
            DBColumn.AVATAR + TEXT + COMMA +
            DBColumn.STATUS + TEXT + COMMA +
            DBColumn.EMPLOYEE_TYPE_ID + INTEGER + COMMA +
            DBColumn.EMPLOYEE_TYPE + TEXT + COMMA +
            DBColumn.NAME + TEXT + COMMA +
            DBColumn.ALIAS + TEXT + COMMA +
            DBColumn.INITIAL + TEXT + COMMA +
            DBColumn.SORT_ORDER + INTEGER + COMMA +
            DBColumn.SENIOR + INTEGER + COMMA +
            DBColumn.NICKNAME + TEXT + COMMA +
            DBColumn.GENDER + INTEGER + COMMA +
            DBColumn.PINYIN + TEXT + COMMA +
            DBColumn.MOVE_INFO + TEXT + COMMA +
            DBColumn.SETTINGS + TEXT + COMMA +
            DBColumn.MOBILE + TEXT + COMMA +
            DBColumn.TEL + TEXT + COMMA +
            DBColumn.SN + TEXT + COMMA +
            DBColumn.LOCATION + TEXT + COMMA +
            DBColumn.WORK_PHONE + TEXT + COMMA +
            DBColumn.FAX + TEXT + COMMA +
            DBColumn.EMAIL + TEXT + COMMA +
            DBColumn.CREATE + INTEGER + COMMA +
            DBColumn.BIRTHDAY + TEXT + COMMA +
            DBColumn.LAST_MODIFIED + INTEGER + COMMA +
            DBColumn.DISABLED + INTEGER + COMMA +
            DBColumn.POSITIONS + BLOB + RIGHT_BRACKET;


    @Override
    public void onCreate(SQLiteDatabase db) {
        Logger.i(TAG, "SQL = " + SQL_EXEC);
        db.execSQL(SQL_EXEC);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion < 5){
            W6sDatabaseHelper.addColumnToTable(db, TABLE_NAME, DBColumn.USERNAME, "text");
            W6sDatabaseHelper.addColumnToTable(db, TABLE_NAME, DBColumn.BIRTHDAY, "text");

            oldVersion = 5;
        }


        if(oldVersion < 16) {
            W6sDatabaseHelper.addColumnToTable(db, TABLE_NAME, DBColumn.TEL, "text");
            W6sDatabaseHelper.addColumnToTable(db, TABLE_NAME, DBColumn.FAX, "text");

            oldVersion = 16;
        }


        if(oldVersion < 17) {
            W6sDatabaseHelper.addColumnToTable(db, TABLE_NAME, DBColumn.WORK_PHONE, "text");

            oldVersion = 17;
        }


        if(oldVersion < 18) {
            W6sDatabaseHelper.addColumnToTable(db, TABLE_NAME, DBColumn.ALIAS, "text");

            oldVersion = 18;
        }


        if(oldVersion < 110) {
            W6sDatabaseHelper.addColumnToTable(db, TABLE_NAME, DBColumn.SN, "text");
            W6sDatabaseHelper.addColumnToTable(db, TABLE_NAME, DBColumn.LOCATION, "text");

            oldVersion = 110;
        }
    }

    /**
     * 从数据库cursor中获取用户employee对象
     */
    @NonNull
    public static Employee fromCursor(Cursor cursor) {
        Employee employee = new Employee();
        int index = -1;
        if ((index = cursor.getColumnIndex(DBColumn.EMPLOYEE_ID)) != -1) {
            employee.id = String.valueOf(cursor.getString(index));
        }
        if ((index = cursor.getColumnIndex(DBColumn.USER_ID)) != -1) {
            employee.userId = cursor.getString(index);
        }
        if ((index = cursor.getColumnIndex(DBColumn.DOMAIN_ID)) != -1) {
            employee.domainId = cursor.getString(index);
        }

        if ((index = cursor.getColumnIndex(DBColumn.USERNAME)) != -1) {
            employee.username = cursor.getString(index);
        }

        if ((index = cursor.getColumnIndex(DBColumn.ORG_CODE)) != -1) {
            employee.orgCode = cursor.getString(index);
        }

        if ((index = cursor.getColumnIndex(DBColumn.AVATAR)) != -1) {
            employee.avatar = cursor.getString(index);
        }

        if ((index = cursor.getColumnIndex(DBColumn.STATUS)) != -1) {
            employee.status = cursor.getString(index);
        }

        if ((index = cursor.getColumnIndex(DBColumn.EMPLOYEE_TYPE_ID)) != -1) {
            employee.employeeTypeId = cursor.getInt(index);
        }
        if ((index = cursor.getColumnIndex(DBColumn.EMPLOYEE_TYPE)) != -1) {
            employee.employeeType = cursor.getString(index);
        }
        if ((index = cursor.getColumnIndex(DBColumn.GENDER)) != -1) {
            employee.gender = getGenderText(cursor.getInt(index));
        }
        if ((index = cursor.getColumnIndex(DBColumn.SENIOR)) != -1) {
            employee.senior = getCursorBoolean(cursor.getInt(index));
        }

        if ((index = cursor.getColumnIndex(DBColumn.INITIAL)) != -1) {
            employee.initial = cursor.getString(index);
        }
        if ((index = cursor.getColumnIndex(DBColumn.MOVE_INFO)) != -1) {
            String moreInfo = cursor.getString(index);
            if (!TextUtils.isEmpty(moreInfo)) {
                employee.moreInfo = JsonUtil.fromJson(moreInfo, MoreInfo.class);
            }
        }
        if ((index = cursor.getColumnIndex(DBColumn.NICKNAME)) != -1) {
            employee.nickName = cursor.getString(index);
        }
        if ((index = cursor.getColumnIndex(DBColumn.NAME)) != -1) {
            employee.name = cursor.getString(index);
        }

        if ((index = cursor.getColumnIndex(DBColumn.ALIAS)) != -1) {
            employee.alias = cursor.getString(index);
        }

        if ((index = cursor.getColumnIndex(DBColumn.DISABLED)) != -1) {
            employee.disabled = getCursorBoolean(cursor.getInt(index));
        }
        if ((index = cursor.getColumnIndex(DBColumn.SORT_ORDER)) != -1) {
            employee.sortOrder = cursor.getLong(index);
        }
        if ((index = cursor.getColumnIndex(DBColumn.PINYIN)) != -1) {
            employee.pinyin = cursor.getString(index);
        }
        if ((index = cursor.getColumnIndex(DBColumn.SETTINGS)) != -1) {
            String settings = cursor.getString(index);
            if (!TextUtils.isEmpty(settings)) {
                employee.settings = JsonUtil.fromJson(settings, Settings.class);
            }
        }
        if ((index = cursor.getColumnIndex(DBColumn.MOBILE)) != -1) {
            employee.mobile = cursor.getString(index);
        }

        if ((index = cursor.getColumnIndex(DBColumn.TEL)) != -1) {
            employee.tel = cursor.getString(index);
        }


        if ((index = cursor.getColumnIndex(DBColumn.SN)) != -1) {
            employee.sn = cursor.getString(index);
        }

        if ((index = cursor.getColumnIndex(DBColumn.LOCATION)) != -1) {
            employee.location = cursor.getString(index);
        }

        if ((index = cursor.getColumnIndex(DBColumn.WORK_PHONE)) != -1) {
            employee.workPhone = cursor.getString(index);
        }

        if ((index = cursor.getColumnIndex(DBColumn.FAX)) != -1) {
            employee.fax = cursor.getString(index);
        }

        if ((index = cursor.getColumnIndex(DBColumn.EMAIL)) != -1) {
            employee.email = cursor.getString(index);
        }

        if ((index = cursor.getColumnIndex(DBColumn.CREATE)) != -1) {
            employee.created = cursor.getInt(index);
        }

        if ((index = cursor.getColumnIndex(DBColumn.BIRTHDAY)) != -1) {
            employee.birthday = cursor.getString(index);
        }

        if ((index = cursor.getColumnIndex(DBColumn.LAST_MODIFIED)) != -1) {
            employee.lastModified = cursor.getInt(index);
        }
        if ((index = cursor.getColumnIndex(DBColumn.POSITIONS)) != -1) {
            String positions = cursor.getString(index);
            if (!TextUtils.isEmpty(positions)) {
                employee.positions = new Gson().fromJson(positions, new TypeToken<List<Position>>() {
                }.getType());
            }
        }

        return employee;
    }


    /**
     * 通过Employee对象获取User数据库contentValues
     *
     * @param employee
     * @return
     */
    public static ContentValues getContentValues(Employee employee) {
        ContentValues cv = new ContentValues();
        cv.put(DBColumn.EMPLOYEE_ID, String.valueOf(employee.id));
        cv.put(DBColumn.USER_ID, employee.userId);
        cv.put(DBColumn.DOMAIN_ID, employee.domainId);
        cv.put(DBColumn.USERNAME, employee.username);
        cv.put(DBColumn.ORG_CODE, employee.orgCode);
        cv.put(DBColumn.EMPLOYEE_TYPE_ID, employee.employeeTypeId);
        cv.put(DBColumn.EMPLOYEE_TYPE, employee.employeeType);
        cv.put(DBColumn.NAME, employee.name);
        cv.put(DBColumn.ALIAS, employee.alias);
        cv.put(DBColumn.INITIAL, employee.initial);
        cv.put(DBColumn.MOVE_INFO, new Gson().toJson(employee.moreInfo));
        cv.put(DBColumn.NICKNAME, employee.nickName);
        cv.put(DBColumn.SORT_ORDER, employee.sortOrder);
        cv.put(DBColumn.PINYIN, employee.pinyin);
        cv.put(DBColumn.SENIOR, getValuesInteger(employee.senior));
        cv.put(DBColumn.SETTINGS, new Gson().toJson(employee.settings));
        cv.put(DBColumn.GENDER, getGenderInt(employee.gender));
        cv.put(DBColumn.POSITIONS, new Gson().toJson(employee.positions));
        cv.put(DBColumn.MOBILE, employee.mobile);
        cv.put(DBColumn.TEL, employee.tel);
        cv.put(DBColumn.WORK_PHONE, employee.workPhone);
        cv.put(DBColumn.EMAIL, employee.email);
        cv.put(DBColumn.FAX, employee.fax);
        cv.put(DBColumn.LAST_MODIFIED, employee.lastModified);
        cv.put(DBColumn.BIRTHDAY, employee.birthday);
        cv.put(DBColumn.DISABLED, getValuesInteger(employee.disabled));
        cv.put(DBColumn.CREATE, employee.created);
        cv.put(DBColumn.STATUS, employee.status);
        cv.put(DBColumn.SN, employee.sn);
        cv.put(DBColumn.LOCATION, employee.location);
        return cv;
    }


    private static boolean getCursorBoolean(Integer integer) {
        return integer == 1;
    }

    private static int getValuesInteger(Boolean bool) {
        return Boolean.TRUE.equals(bool) ? 1 : 0;
    }

    private static int getGenderInt(String gender){
        if ("MALE".equals(gender)){
            return 1;
        }

        if ("FEMALE".equals(gender)){
            return 2;
        }

        return 0;
    }

    private static String getGenderText(int gender){
        if (1 == gender){
            return "MALE";
        }

        if (2 == gender){
            return "FEMALE";
        }
        return "UNKNOWN";
    }

    public static String getDetailDBColumn(String column) {
        return W6sBaseRepository.getDetailDBColumn(TABLE_NAME, column);
    }


    public class DBColumn {

        public static final String EMPLOYEE_ID = "employee_id_";

        public static final String USER_ID = "user_id_";

        public static final String DOMAIN_ID = "domain_id_";

        public static final String ORG_CODE = "org_code_";

        public static final String AVATAR = "avatar_";

        public static final String STATUS = "status_";

        public static final String EMPLOYEE_TYPE_ID = "employee_type_id_";

        public static final String EMPLOYEE_TYPE = "employee_type_";

        public static final String NAME = "name_";

        public static final String ALIAS = "alias_";

        public static final String INITIAL = " initial_";

        public static final String USERNAME = "username_";

        public static final String SORT_ORDER = "sort_order_";

        public static final String SENIOR = "senior_";

        public static final String NICKNAME = "nickname_";

        public static final String GENDER = "gender_";

        public static final String PINYIN = "pinyin_";

        public static final String MOVE_INFO = "more_info_";

        public static final String SETTINGS = "settings_";

        public static final String MOBILE = "mobile_";

        public static final String EMAIL = "email_";

        public static final String TEL = "tel_";

        public static final String WORK_PHONE = "work_phone_";

        public static final String SN = "sn_";

        public static final String LOCATION = "location_";

        public static final String FAX = "fax_";

        public static final String CREATE = "create_";

        public static final String BIRTHDAY = "birthday_";

        public static final String LAST_MODIFIED = "last_modified_";

        public static final String DISABLED = "disabled_";

        public static final String POSITIONS = "positions_";

    }


}
