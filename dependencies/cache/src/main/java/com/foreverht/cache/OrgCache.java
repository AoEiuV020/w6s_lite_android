package com.foreverht.cache;

import androidx.annotation.Nullable;
import androidx.collection.LruCache;

import com.foreveross.atwork.infrastructure.model.orgization.Organization;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
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
 * Created by reyzhang22 on 16/8/18.
 */
public class OrgCache extends BaseCache {

    private static OrgCache sInstance = new OrgCache();

    private CopyOnWriteArrayList<String> mOrgCodeAdminListCache = null;

    private LruCache<String, Organization> mOrgListCache = new LruCache<>(mMaxMemory / 10);

    private OrgCache() {

    }

    public static OrgCache getInstance() {
        return sInstance;
    }


    @Nullable
    public List<String> getAdminOrgListCache() {
        return mOrgCodeAdminListCache;
    }


    public void clear() {
        if (mOrgCodeAdminListCache != null) {
            mOrgCodeAdminListCache.clear();

        }

        mOrgCodeAdminListCache = null;

    }


    public void setAdminOrg(String orgCode) {
        if (null == mOrgCodeAdminListCache) {
            mOrgCodeAdminListCache = new CopyOnWriteArrayList<>();
        }
        mOrgCodeAdminListCache.add(orgCode);
    }

    public void setOrganization(Organization org) {
        mOrgListCache.put(org.mOrgCode, org);
    }

    public Organization getOrganization(String orgCode) {
        return mOrgListCache.get(orgCode);
    }
}

