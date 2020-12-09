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
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ __           __
 * .--.--.--.-----.----.|  |--.-----.|  |.--.--.-----.
 * |  |  |  |  _  |   _||    <|  _  ||  ||  |  |__ --|
 * |________|_____|__|  |__|__|   __||__||_____|_____|
 * |__|
 */


import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;

import com.foreverht.db.service.BaseDbService;
import com.foreverht.db.service.repository.OrgRelationShipRepository;
import com.foreverht.db.service.repository.OrganizationRepository;
import com.foreveross.atwork.infrastructure.model.orgization.OrgRelationship;
import com.foreveross.atwork.infrastructure.model.orgization.Organization;
import com.foreveross.atwork.infrastructure.utils.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 *
 * Created by reyzhang22 on 16/4/18.
 */
public class OrganizationDaoService extends BaseDbService {

    private static final String TAG = OrganizationDaoService.class.getSimpleName();

    private static OrganizationDaoService sInstance = new OrganizationDaoService();

    private OrganizationDaoService() {

    }

    public static OrganizationDaoService getInstance() {
        return sInstance;

    }

    public void queryLoginOrgCodeList(Context context, OnGetOrgCodeListListener onGetOrgCodeListListener) {
        new AsyncTask<Void, Void, List<String>>() {
            @Override
            protected List<String> doInBackground(Void... params) {
                return OrganizationRepository.getInstance().queryLoginOrgCodeListSync(context);
            }

            @Override
            protected void onPostExecute(List<String> orgCodeList) {
                onGetOrgCodeListListener.onSuccess(orgCodeList);
            }
        }.executeOnExecutor(mDbExecutor);
    }


    /**
     * 获取所有organization
     * @return
     */
    public List<Organization> queryLoginOrganizationsSync(Context context) {
        List<String> loginCodeList = OrganizationRepository.getInstance().queryLoginOrgCodeListSync(context);
        List<Organization> organizationList = OrganizationRepository.getInstance().queryOrganizationList(loginCodeList);

        //按创建时间升序排序
        Collections.sort(organizationList);

        return organizationList;
    }

    public void queryLoginLocalOrganizations(Context context, final OrganizationRepository.OnLocalOrganizationListener listener) {
        new AsyncTask<Void, Void, List<Organization>>() {
            @Override
            protected List<Organization> doInBackground(Void... params) {
                return queryLoginLocalOrganizationsSync(context);
            }

            @Override
            protected void onPostExecute(List<Organization> organizationList) {
                if (listener == null) {
                    return;
                }
                listener.onLocalOrganizationCallback(organizationList);
            }
        }.executeOnExecutor(mDbExecutor);
    }

    /**
     * 获取本地的组织信息, 并且查询管理员的 userId
     * */
    @NonNull
    public List<Organization> queryLoginLocalOrganizationsSync(Context context) {
        List<Organization> orgList = queryLoginOrganizationsSync(context);
        List<String> orgCodeList = new ArrayList<>();
        for (Organization org : orgList) {
            orgCodeList.add(org.mOrgCode);
        }
        //获取相关组织的 admin 关系
        HashMap<String, String> orgRelationShipMap = OrgRelationShipRepository.getInstance().queryAdminRelationshipList(orgCodeList);
        //赋予 org 列表
        for (Organization org : orgList) {
            String adminUserId = orgRelationShipMap.get(org.mOrgCode);
            if (!StringUtils.isEmpty(adminUserId)) {
                org.mAdminUserId = adminUserId;
            }
        }

        return orgList;
    }

    /**
     * 批量插入组织架构
     * @param organizationList
     */
    public void batchInsertOrganization(List<Organization> organizationList) {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                return OrganizationRepository.getInstance().batchInsertOrUpdateOrganizations(organizationList);
            }

        }.executeOnExecutor(mDbExecutor);
    }


    /**
     * 批量插入组织关系, 不会清除旧数据, 区别于 {@link #batchInsertRelationAndClean(String, int, List)}
     *
     * */
    public void batchInsertRelation(List<OrgRelationship> orgRelationshipList) {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                return OrgRelationShipRepository.getInstance(). batchInsertRelation(orgRelationshipList);
            }

        }.executeOnExecutor(mDbExecutor);
    }

    /**
     * 批量插入组织关系, 会清除旧数据
     * */
    public void batchInsertRelationAndClean(String userId, int deletedType, List<OrgRelationship> orgRelationshipList) {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                return OrgRelationShipRepository.getInstance().batchInsertOrgRelationAndClean(userId, deletedType, orgRelationshipList);
            }

        }.executeOnExecutor(mDbExecutor);
    }

    public void queryOrgCount(String userId, OnQueryOrgCountListener queryOrgCountListener) {
        new AsyncTask<Void, Void, Integer>() {
            @Override
            protected Integer doInBackground(Void... params) {
                return OrgRelationShipRepository.getInstance().queryOrgCount(userId);
            }

            @Override
            protected void onPostExecute(Integer integer) {
                queryOrgCountListener.result(integer);
            }
        }.executeOnExecutor(mDbExecutor);
    }

    /**
     * 删除组织
     */
    public void removeOrganization(String orgCode) {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                return OrganizationRepository.getInstance().removeOrg(orgCode);
            }

        }.executeOnExecutor(mDbExecutor);
    }


    public interface OnQueryOrgCountListener {
        void result(int count);
    }

    public interface OnGetOrgCodeListListener {
        void onSuccess(List<String> codeList);
    }

}
