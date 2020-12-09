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

import com.foreverht.db.service.W6sDatabaseHelper;
import com.foreveross.atwork.infrastructure.model.i18n.CommonI18nInfoData;
import com.foreveross.atwork.infrastructure.model.orgization.Organization;
import com.foreveross.atwork.infrastructure.utils.JsonUtil;
import com.foreveross.atwork.infrastructure.utils.Logger;
import com.foreveross.db.SQLiteDatabase;

/**
 * 组织架构数据库操作类
 * Created by reyzhang22 on 16/3/23.
 */
public class OrganizationDBHelper implements DBHelper {

    private static final String TAG  = OrganizationDBHelper.class.getName();

    public static final String TABLE_NAME = "orgs_";

    /**
     * create table orgs_
     * ( org_code_ text  primary key ,domain_id_ text ,name_ text ,
     *   pinyin_ text ,initial_ text ,sn_ text ,tel text ,path_ text ,
     *   type_ text ,sort_order_ text ,level_ text ,create_ integer ,
     *   expired_ integer ,last_modified_ integer ,logo_ text ,
     *   operator_ text ,uuid_ text ,id_ text,owner_ text ,disabled_ integer  )
     */
    private static final String SQL_EXEC = CREATE_TABLE + TABLE_NAME + LEFT_BRACKET +
                                            DBColumn.ORG_CODE + TEXT + PRIMARY_KEY + COMMA +
                                            DBColumn.DOMAIN_ID + TEXT + COMMA +
                                            DBColumn.NAME + TEXT + COMMA +
                                            DBColumn.I18N + TEXT + COMMA +
                                            DBColumn.PIN_YIN + TEXT + COMMA +
                                            DBColumn.INITIAL + TEXT + COMMA +
                                            DBColumn.SN + TEXT + COMMA +
                                            DBColumn.TEL + TEXT + COMMA +
                                            DBColumn.PATH + TEXT + COMMA +
                                            DBColumn.TYPE + TEXT + COMMA +
                                            DBColumn.SORT_ORDER + TEXT +COMMA +
                                            DBColumn.LEVEL + TEXT + COMMA +
                                            DBColumn.CREATE + INTEGER + COMMA +
                                            DBColumn.EXPIRED + INTEGER + COMMA +
                                            DBColumn.REFRESH_TIME + INTEGER + COMMA +
                                            DBColumn.LOGO + TEXT + COMMA +
                                            DBColumn.OPERATOR + TEXT + COMMA +
                                            DBColumn.UUID + TEXT + COMMA +
                                            DBColumn.ID + TEXT + COMMA +
                                            DBColumn.OWNER + TEXT + COMMA +
                                            DBColumn.DISABLED + INTEGER + RIGHT_BRACKET;


    @Override
    public void onCreate(SQLiteDatabase db) {
        Logger.d(TAG, "SQL = " + SQL_EXEC);
        db.execSQL(SQL_EXEC);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion < 18) {
            W6sDatabaseHelper.addColumnToTable(db, TABLE_NAME, DBColumn.I18N, "text");

            oldVersion = 18;
        }
    }

    /**
     * 获取组织架构数据库contentValues
     * @param organization
     * @return
     */
    public static ContentValues getContentValues(Organization organization) {
        ContentValues cv = new ContentValues();
        cv.put(DBColumn.CREATE , organization.mCreated);
        cv.put(DBColumn.DISABLED , organization.mDisabled);
        cv.put(DBColumn.DOMAIN_ID , organization.mDomainId);
        cv.put(DBColumn.EXPIRED , organization.mExpired);
        cv.put(DBColumn.INITIAL , organization.mInitial);
        cv.put(DBColumn.LEVEL , organization.mLevel);
        cv.put(DBColumn.NAME , organization.mName);
        cv.put(DBColumn.I18N, JsonUtil.toJson(organization.getI18nInfo()));
        cv.put(DBColumn.ORG_CODE , organization.mOrgCode);
        cv.put(DBColumn.PATH , organization.mPath);
        cv.put(DBColumn.PIN_YIN , organization.mPinYin);
        cv.put(DBColumn.SN , organization.mSn);
        cv.put(DBColumn.SORT_ORDER , organization.mSortOrder);
        cv.put(DBColumn.TEL , organization.mTel);
        cv.put(DBColumn.TYPE , organization.mType);
        cv.put(DBColumn.REFRESH_TIME, organization.mRefreshTime);
        cv.put(DBColumn.OPERATOR, organization.mOperator);
        cv.put(DBColumn.LOGO, organization.mLogo);
        cv.put(DBColumn.UUID, organization.mUUID);
        cv.put(DBColumn.ID, organization.mId);
        cv.put(DBColumn.OWNER, organization.mOwner);
        return cv;
    }


    /**
     * 从数据库中获取到组织架构organization对象
     * @param cursor
     * @return
     */
    public static Organization fromCursor(Cursor cursor) {
        Organization organization = new Organization();
        int index = -1;

        if ((index = cursor.getColumnIndex(DBColumn.NAME)) != -1) {
            organization.mName = cursor.getString(index);
        }

        if ((index = cursor.getColumnIndex(DBColumn.I18N)) != -1) {
            CommonI18nInfoData i18nInfo = JsonUtil.fromJson(cursor.getString(index), CommonI18nInfoData.class);
            if (null != i18nInfo) {
                organization.mEnName = i18nInfo.getEnName();
                organization.mTwName = i18nInfo.getTwName();
            }
        }

        if ((index = cursor.getColumnIndex(DBColumn.CREATE)) != -1) {
            organization.mCreated = cursor.getLong(index);
        }
        if ((index = cursor.getColumnIndex(DBColumn.DISABLED)) != -1) {
            organization.mDisabled = cursor.getString(index);
        }
        if ((index = cursor.getColumnIndex(DBColumn.DOMAIN_ID)) != -1) {
            organization.mDomainId = cursor.getString(index);
        }
        if ((index = cursor.getColumnIndex(DBColumn.EXPIRED)) != -1) {
            organization.mExpired = cursor.getLong(index);
        }
        if ((index = cursor.getColumnIndex(DBColumn.INITIAL)) != -1) {
            organization.mInitial = cursor.getString(index);
        }
        if ((index = cursor.getColumnIndex(DBColumn.LEVEL)) != -1) {
            organization.mLevel = cursor.getString(index);
        }
        if ((index = cursor.getColumnIndex(DBColumn.ORG_CODE)) != -1) {
            organization.mOrgCode = cursor.getString(index);
        }
        if ((index = cursor.getColumnIndex(DBColumn.PATH)) != -1) {
            organization.mPath = cursor.getString(index);
        }
        if ((index = cursor.getColumnIndex(DBColumn.PIN_YIN)) != -1) {
            organization.mPinYin = cursor.getString(index);
        }
        if ((index = cursor.getColumnIndex(DBColumn.SN)) != -1) {
            organization.mSn = cursor.getString(index);
        }
        if ((index = cursor.getColumnIndex(DBColumn.SORT_ORDER)) != -1) {
            organization.mSortOrder = cursor.getString(index);
        }
        if ((index = cursor.getColumnIndex(DBColumn.TEL)) != -1) {
            organization.mTel = cursor.getString(index);
        }
        if ((index = cursor.getColumnIndex(DBColumn.TYPE)) != -1) {
            organization.mType = cursor.getString(index);
        }
        if ((index = cursor.getColumnIndex(DBColumn.OPERATOR)) != -1) {
            organization.mOperator = cursor.getString(index);
        }
        if ((index = cursor.getColumnIndex(DBColumn.LOGO)) != -1) {
            organization.mLogo = cursor.getString(index);
        }
        if ((index = cursor.getColumnIndex(DBColumn.UUID)) != -1) {
            organization.mUUID = cursor.getString(index);
        }
        if ((index = cursor.getColumnIndex(DBColumn.REFRESH_TIME)) != -1) {
            organization.mRefreshTime = cursor.getLong(index);
        }
        if ((index = cursor.getColumnIndex(DBColumn.ID)) != -1) {
            organization.mId = cursor.getString(index);
        }
        if ((index = cursor.getColumnIndex(DBColumn.OWNER)) != -1) {
            organization.mOwner = cursor.getString(index);
        }

        return organization;

    }


    public class DBColumn {

        public static final String ORG_CODE = "org_code_";

        public static final String DOMAIN_ID = "domain_id_";

        public static final String NAME = "name_";

        public static final String I18N = "i18n_";

        public static final String PIN_YIN = "pinyin_";

        public static final String INITIAL = "initial_";

        public static final String SN = "sn_";

        public static final String TEL = "tel";

        public static final String PATH = "path_";

        public static final String TYPE = "type_";

        public static final String SORT_ORDER = "sort_order_";

        public static final String LEVEL = "level_";

        public static final String CREATE = "create_";

        public static final String EXPIRED = "expired_";

        public static final String DISABLED = "disabled_";

        public static final String REFRESH_TIME = "refresh_time_";

        public static final String LOGO = "logo_";

        public static final String OPERATOR = "operator_";

        public static final String UUID = "uuid_";

        public static final String ID = "id_";

        public static final String OWNER = "owner_";

    }
}
