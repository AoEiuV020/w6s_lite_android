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


import android.content.Context;
import android.database.Cursor;
import androidx.annotation.NonNull;
import android.text.TextUtils;

import com.foreverht.cache.OrgCache;
import com.foreverht.db.service.W6sBaseRepository;
import com.foreverht.db.service.dbHelper.OrganizationDBHelper;
import com.foreveross.atwork.infrastructure.model.orgization.Organization;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.utils.LogUtil;
import com.foreveross.db.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by reyzhang22 on 16/4/5.
 */
public class OrganizationRepository extends W6sBaseRepository {

    private static final String TAG = OrganizationRepository.class.getSimpleName();

    public static OrganizationRepository sInstance = new OrganizationRepository();

    private OrganizationRepository() {

    }

    public static OrganizationRepository getInstance() {
        return sInstance;
    }


    /**
     * 获取当前登录用户拥有管理者身份的组织(按照缓存->本地的顺序)
     * */
    @NonNull
    public List<String> queryLoginAdminOrgSync(Context context) {

        List<String> adminOrgCodeList = OrgCache.getInstance().getAdminOrgListCache();

        if(null == adminOrgCodeList) {
            adminOrgCodeList = OrgRelationShipRepository.getInstance().queryAdminRelationshipList(LoginUserInfo.getInstance().getLoginUserId(context));

        }

        return adminOrgCodeList;
    }

    /**
     * 查询所有组织 code
     * */
    @NonNull
    public List<String> queryLoginOrgCodeListSync(Context context) {
        String meUserId = LoginUserInfo.getInstance().getLoginUserId(context);
        List<String> codeList = OrgRelationShipRepository.getInstance().queryOrgCodeList(meUserId);
        return codeList;
    }


    /**
     * 根据orgCode获取组织架构信息
     *
     * @param orgCode
     * @return
     */
    public Organization queryOrganization(String orgCode) {
        Organization organization = null;
        String sql = "select * from " + OrganizationDBHelper.TABLE_NAME + " where " +
                OrganizationDBHelper.DBColumn.ORG_CODE + " = ?";

        Cursor cursor = getWritableDatabase().rawQuery(sql, new String[]{orgCode});
        if (cursor != null) {
            if (cursor.moveToNext()) {
                organization = OrganizationDBHelper.fromCursor(cursor);
                cursor.close();
            }

        }
        return organization;
    }


    //查询改用户下所有的组织id列表
    public List<String> queryAllOrgCodeList() {
        List<String> orgList = new ArrayList<>();
        String sql = "select " + OrganizationDBHelper.DBColumn.ORG_CODE + " from " + OrganizationDBHelper.TABLE_NAME + " order by " + OrganizationDBHelper.DBColumn.CREATE + " asc";
        Cursor cursor = getReadableDatabase().rawQuery(sql, new String[] {});
        if (cursor == null) {
            return orgList;
        }
        while (cursor.moveToNext()) {
            String orgCode = cursor.getString(cursor.getColumnIndex(OrganizationDBHelper.DBColumn.ORG_CODE));
            orgList.add(orgCode);
        }
        cursor.close();
        return orgList;

    }


    /**
     * 查询 org list
     */
    @NonNull
    public List<Organization> queryOrganizationList(List<String> orgCodeList) {
        List<Organization> organizationList = new ArrayList<>();
        String sql = "select * from " + OrganizationDBHelper.TABLE_NAME + " where " +
                OrganizationDBHelper.DBColumn.ORG_CODE + " in (" + getInStringParams(orgCodeList) + ")";

        Cursor cursor = getWritableDatabase().rawQuery(sql, new String[]{});
        if (null != cursor) {

            while (cursor.moveToNext()) {

                Organization organization = OrganizationDBHelper.fromCursor(cursor);
                organizationList.add(organization);
            }

            cursor.close();
        }

        return organizationList;
    }

    /**
     * 插入组织架构到数据库
     *
     * @param organization
     * @return
     */
    public boolean insertOrganization(Organization organization) {
        boolean result = false;
        long insertResult = getWritableDatabase().insertWithOnConflict(
                OrganizationDBHelper.TABLE_NAME,
                OrganizationDBHelper.DBColumn.ORG_CODE,
                OrganizationDBHelper.getContentValues(organization),
                SQLiteDatabase.CONFLICT_REPLACE);

        if (insertResult == -1) {
            return result;
        }
        result = true;
        return result;
    }


    public List<String> queryAllOrganization() {
        List<String> orgCodes = new ArrayList<>();
        String sql = "select " + OrganizationDBHelper.DBColumn.ORG_CODE + " from " + OrganizationDBHelper.TABLE_NAME;
        Cursor cursor = getReadableDatabase().rawQuery(sql, new String[]{});
        try {
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String orgCode = cursor.getString(0);
                    orgCodes.add(orgCode);
                }
            }

        } catch (Exception e) {

        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return orgCodes;
    }


    /**
     * 批量插入组织架构
     *
     * @param organizationList
     * @return
     */
    public boolean batchInsertOrUpdateOrganizations(List<Organization> organizationList) {
        boolean batchInsertResult = false;

        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.beginTransaction();

        try {
            for (Organization organization : organizationList) {
                if (TextUtils.isEmpty(organization.mOrgCode)) {
                    continue;
                }

                long result = sqLiteDatabase.insertWithOnConflict(
                        OrganizationDBHelper.TABLE_NAME,
                        null,
                        OrganizationDBHelper.getContentValues(organization),
                        SQLiteDatabase.CONFLICT_REPLACE);

                LogUtil.e("insert result ->" + result);
            }

            sqLiteDatabase.setTransactionSuccessful();
            batchInsertResult = true;

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            try {
                sqLiteDatabase.endTransaction();

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return batchInsertResult;
    }


    public boolean removeOrg(String orgCode) {
        String removeSql = "delete from " + OrganizationDBHelper.TABLE_NAME + " where " + OrganizationDBHelper.DBColumn.ORG_CODE + " = ?";

        getWritableDatabase().execSQL(removeSql, new String[]{orgCode});

        return true;
    }

    /**
     * 批量删除组织
     *
     * @param orgCodeList
     */
    public boolean batchRemoveOrgs(List<String> orgCodeList) {
        boolean result = false;

        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        try {
            sqLiteDatabase.beginTransaction();

            for (String orgCode : orgCodeList) {
                removeOrg(orgCode);
            }
            sqLiteDatabase.setTransactionSuccessful();
            result = true;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            sqLiteDatabase.endTransaction();
        }

        return result;
    }


    /**
     * 查询本地
     */
    public interface OnLocalOrganizationListener {
        void onLocalOrganizationCallback(Object... localData);
    }
}

