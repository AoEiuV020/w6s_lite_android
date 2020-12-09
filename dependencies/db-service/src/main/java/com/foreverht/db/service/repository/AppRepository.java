package com.foreverht.db.service.repository;

import android.database.Cursor;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.core.text.StringKt;

import com.foreverht.cache.AppCache;
import com.foreverht.db.service.W6sBaseRepository;
import com.foreverht.db.service.W6sDatabaseHelper;
import com.foreverht.db.service.dbHelper.AppDBHelper;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.app.App;
import com.foreveross.atwork.infrastructure.model.app.AppBundles;
import com.foreveross.atwork.infrastructure.model.app.Shortcut;
import com.foreveross.atwork.infrastructure.model.app.appEnum.AppKind;
import com.foreveross.atwork.infrastructure.model.app.appEnum.AppType;
import com.foreveross.atwork.infrastructure.shared.bugFix.W6sBugFixPersonShareInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.CustomerHelper;
import com.foreveross.atwork.infrastructure.utils.JsonUtil;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.language.LanguageUtil;
import com.foreveross.db.SQLiteDatabase;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function1;
import kotlin.text.StringsKt;

/**
 * Created by lingen on 15/5/6.
 */
public class AppRepository extends W6sBaseRepository {

    private static AppRepository appRepository = new AppRepository();

    private AppRepository() {

    }

    public static AppRepository getInstance() {
        return appRepository;
    }



    public static SQLiteDatabase getWritableDatabase() {
        SQLiteDatabase writableDatabase = W6sBaseRepository.getWritableDatabase();

        makeCompatible(writableDatabase);

        return writableDatabase;
    }

    private static void makeCompatible(SQLiteDatabase writableDatabase) {
        if(shouldCheckAppDbBioAuthColumnUpdate()) {
            W6sDatabaseHelper.addColumnToTable(writableDatabase, AppDBHelper.TABLE_NAME, AppDBHelper.DBColumn.BIOMETRIC_AUTHENTICATION, "text");
            W6sBugFixPersonShareInfo.INSTANCE.setUpdateAppDbBiometricAuthenticationDbColumnValue(BaseApplicationLike.baseContext, true);
        }
    }

    private static boolean shouldCheckAppDbBioAuthColumnUpdate() {
        if(!AtworkConfig.W6SBUGFIX_CONFIG.getCompatibilityAppDbColumnBiometricAuthentication()) {
            return false;
        }

        return !W6sBugFixPersonShareInfo.INSTANCE.hasUpdateAppDbBiometricAuthenticationDbColumnValue(BaseApplicationLike.baseContext);
    }

    public static SQLiteDatabase getReadableDatabase() {
        SQLiteDatabase readableDatabase = W6sBaseRepository.getReadableDatabase();
        makeCompatible(readableDatabase);
        return readableDatabase;
    }

    /**
     * 更新MENU
     *
     * @param identifier
     * @param menuJson
     * @return
     */
    public boolean updateMenuJson(final String identifier, final String menuJson) {
        String sql = "update app_ set menu_ = ? where " + AppDBHelper.DBColumn.APP_ID + " = ?";
        getWritableDatabase().execSQL(sql, new String[]{identifier, menuJson});
        return true;
    }


    /**
     * 批量更新 shortcut
     * @param shortcutList
     *
     * */
    public boolean batchUpdateShortcutInfo(List<Shortcut> shortcutList) {
        boolean result = true;

        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        try {
            sqLiteDatabase.beginTransaction();

            for (Shortcut shortcut : shortcutList) {
                updateShortcutInfo(shortcut);
            }
            sqLiteDatabase.setTransactionSuccessful();


        } catch (Exception e) {
            e.printStackTrace();
            result = false;

        } finally {
            sqLiteDatabase.endTransaction();
        }

        return result;
    }


    public boolean updateShortcutInfo(Shortcut shortcut) {
//        int updateShowInMarket = 1;
        String updateShortcut = null;
        if (!shortcut.mClear) {
//            updateShowInMarket = shortcut.mShowInMarket ? 1 : 0;
            updateShortcut = JsonUtil.toJson(shortcut);

        }

        String sql = "update app_ set " + AppDBHelper.DBColumn.SHORTCUT_DATA + " = ? where " + AppDBHelper.DBColumn.APP_ID + " = ?";
        getWritableDatabase().execSQL(sql, new String[]{updateShortcut, shortcut.mAppId});
        return true;
    }

    public String queryMenuJson(final String identifier) {
        String sql = "select menu_ from app_ where " + AppDBHelper.DBColumn.APP_ID + " = ?";
        String menuJson = StringUtils.EMPTY;
        Cursor cursor = null;
        try {
            cursor = getReadableDatabase().rawQuery(sql, new String[]{identifier});
            if (cursor.moveToNext()) {
                menuJson = cursor.getString(cursor.getColumnIndex("menu_"));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return menuJson;
    }

    /**
     * 查询当前用户某个组织下的所有的APP
     *
     * @return
     */
    public List<App> queryAllAppByOrgId(String orgCode) {
        List<App> appList = new ArrayList<>();

        String sql = "select * from " + AppDBHelper.TABLE_NAME + " where " + AppDBHelper.DBColumn.ORG_ID + "= ?";
        sql +=  " or " + AppDBHelper.DBColumn.IS_DOMAIN_APP + "=1";

        return queryApps(sql, orgCode, appList);
    }


    /**
     * 查询当前用户某个组织下的所有的服务号 app
     *
     */
    public List<App> queryAccessServiceApps(String orgCode) {
        List<App> appList = new ArrayList<>();

        String sql = "select * from " + AppDBHelper.TABLE_NAME + " where (" +
                AppDBHelper.DBColumn.ORG_ID + " = ? or " +
                AppDBHelper.DBColumn.IS_DOMAIN_APP + " = 1 )" +  " and " +
                AppDBHelper.DBColumn.APP_TYPE + " = " + AppType.Access.intValue()+ " and " +
                AppDBHelper.DBColumn.KIND + " = '" + AppKind.ServeNo.stringValue() + "'";

        return queryApps(sql, orgCode, appList);
    }


    /**
     * 获取Access应用列表
     *
     * @param orgCode
     * @return
     */
    @NonNull
    public List<App> queryAccessApps(String orgCode) {
        return queryAccessApps(orgCode, -1);
    }

    /**
     * 获取Access应用列表
     *
     * @param orgCode
     * @param limit
     * @return
     */
    @NonNull
    public List<App> queryAccessApps(String orgCode, int limit) {
        List<App> appList = new ArrayList<>();

        String sql = "select * from " + AppDBHelper.TABLE_NAME + " where " +
                AppDBHelper.DBColumn.ORG_ID + " = ? "  + " and " +
                AppDBHelper.DBColumn.APP_TYPE + " =" + AppType.Access.intValue();


        if(0 < limit) {
            sql += "limit " + limit +  " offset 0 ";
        }

        return queryApps(sql, orgCode, appList);
    }


    /**
     * 获取Access应用列表
     *
     * @param orgCode
     * @param appIds
     * @return
     */
    @NonNull
    public List<App> queryAccessApps(String orgCode, List<String> appIds) {
        List<App> appList = new ArrayList<>();

        String sql = "select * from " + AppDBHelper.TABLE_NAME + " where (" +
                AppDBHelper.DBColumn.ORG_ID + " = ? or " +
                AppDBHelper.DBColumn.IS_DOMAIN_APP + " = 1 )"  + " and " +
                AppDBHelper.DBColumn.APP_TYPE + " =" + AppType.Access.intValue();

        if(!ListUtil.isEmpty(appIds)) {
            sql += " " + AppDBHelper.DBColumn.APP_ID + " in (" + getInStringParams(appIds) + ")";
        }


        return queryApps(sql, orgCode, appList);
    }

    public List<App> queryAccessApps(List<String> appIds) {

        List<App> appList = new ArrayList<>();
        String sql = "select * from " + AppDBHelper.TABLE_NAME + " where " +
                AppDBHelper.DBColumn.APP_TYPE + " =" + AppType.Access.intValue();

        sql += " and " + AppDBHelper.DBColumn.APP_ID + " in (" + getInStringParams(appIds) + ")";

        Cursor cursor = null;

        try {
            cursor = getReadableDatabase().rawQuery(sql, new String[]{});

            while (cursor.moveToNext()) {
                appList.add(AppDBHelper.fromCursor(cursor));
            }

        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return appList;
    }


    /**
     * 获取 app shortcut列表
     * */
    @NonNull
    public List<Shortcut> queryShortcuts(String orgCode) {
        List<App> appHasShortcutList = new ArrayList<>();
        List<Shortcut> shortcutList = new ArrayList<>();

        String sql = "select * from " + AppDBHelper.TABLE_NAME + " where (" +
                AppDBHelper.DBColumn.ORG_ID + " = ? or" +
                AppDBHelper.DBColumn.IS_DOMAIN_APP + " = 1 )"  + " and " +
                AppDBHelper.DBColumn.APP_TYPE + " =" + AppType.Access.intValue() + " and " +
                AppDBHelper.DBColumn.SHORTCUT_DATA + " IS NOT NULL"  ;

        queryApps(sql, orgCode, appHasShortcutList);

        for(App app : appHasShortcutList) {
            shortcutList.add(app.mShortcut);
        }

        return shortcutList;
    }

    /**
     * 获取Admin应用列表
     *
     * @param orgCode
     * @return
     */
    @NonNull
    public List<App> queryAdminApps(String orgCode) {
        List<App> appList = new ArrayList<>();

        String sql = "select * from " + AppDBHelper.TABLE_NAME + " where (" +
                AppDBHelper.DBColumn.ORG_ID + "= ? or " +
                AppDBHelper.DBColumn.IS_DOMAIN_APP + " = 1 )"  + " and " +
                AppDBHelper.DBColumn.APP_TYPE + " = " + AppType.Admin.intValue();


        return queryApps(sql, orgCode, appList);
    }

    private List<App> queryApps(String sql, String orgCode, List<App> appList) {
        Cursor cursor = null;

        try {
            cursor = getReadableDatabase().rawQuery(sql, new String[]{orgCode});

            while (cursor.moveToNext()) {
                appList.add(AppDBHelper.fromCursor(cursor));
            }

        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return appList;
    }




    /**
     * 获取所有轻应用
     */
    public List<App> queryAllLightApp() {
        List<App> appList = new ArrayList<>();

        String sql = "select * from " + AppDBHelper.TABLE_NAME + " where " + AppDBHelper.DBColumn.KIND + " = " + AppKind.LIGHT_APP;

        Cursor cursor = null;

        try {
            cursor = getReadableDatabase().rawQuery(sql, new String[]{});
            while (cursor.moveToNext()) {
//                App app = App.toApp("");
//                appList.add(app);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return appList;
    }

    /**
     * 更新一个APP对象
     *
     * @param app
     * @return
     */
    public boolean insertOrUpdateApp(App app) {
        App localApp = queryApp(app.mAppId);
        if(null != localApp) {
//            app.mShowInMarket = localApp.mShowInMarket;
            app.mShortcut = localApp.mShortcut;

        }

        long result = getWritableDatabase().insertWithOnConflict(
                AppDBHelper.TABLE_NAME,
                null,
                AppDBHelper.getContentValue(app), SQLiteDatabase.CONFLICT_REPLACE);
        if (result == -1) {
            return false;
        }

        AppCache.getInstance().setAppCache(app);
        return true;
    }

    /**
     * 查询一个APP信息
     *
     * @param identifier
     * @return
     */
    @Nullable
    public App queryApp(final String identifier) {
        App app = null;
        String sql = "select * from " + AppDBHelper.TABLE_NAME + " where " + AppDBHelper.DBColumn.APP_ID + " = ?";
        Cursor cursor = null;
        try {

            cursor = getWritableDatabase().rawQuery(sql, new String[]{identifier});
            if (cursor.moveToNext()) {
                app = AppDBHelper.fromCursor(cursor);
            }

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return app;
    }

    /**
     * 根据orgId查询app列表
     * @param orgId
     * @return
     */
    public List<App> queryAppsByOrgId(String orgId) {
        List<App> apps = new ArrayList<>();
        String sql = "select * from " + AppDBHelper.TABLE_NAME + " where " + AppDBHelper.DBColumn.ORG_ID + " = ?";
        sql +=  " or " + AppDBHelper.DBColumn.IS_DOMAIN_APP + "=1";

        Cursor cursor = null;
        try {

            cursor = getWritableDatabase().rawQuery(sql, new String[]{orgId});
            while (cursor.moveToNext()) {
                App app = AppDBHelper.fromCursor(cursor);
                apps.add(app);
            }

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return apps;
    }


    /**
     * 查询k9邮箱
     *
     * @return
     */
    public App queryK9EmailApp() {
        App app = null;
        String sql = "select * from " + AppDBHelper.TABLE_NAME + " where " + AppDBHelper.DBColumn.KIND + " = ?";
        Cursor cursor = null;
        try {
            cursor = getReadableDatabase().rawQuery(sql, new String[]{AppKind.NativeEmail.stringValue()});
            cursor.moveToFirst();
            app = AppDBHelper.fromCursor(cursor);

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            if (null != cursor) {
                cursor.close();
            }
        }
        return app;
    }

    /**
     * APP搜索
     *
     * @param searchKey
     * @return
     */
    public List<App> searchApps(final String searchKey, @Nullable final String orgCode) {
        String sql = "select * from " + AppDBHelper.TABLE_NAME + " where ("
                + AppDBHelper.DBColumn.NAME + " like ? COLLATE NOCASE or "
                + AppDBHelper.DBColumn.INITIAL + " like ? COLLATE NOCASE or "
                + AppDBHelper.DBColumn.I18N + " like ?  COLLATE NOCASE or "
                + AppDBHelper.DBColumn.APP_PINYIN + " like ? COLLATE NOCASE )"
                + " and " + AppDBHelper.DBColumn.SHOW_IN_MARKET + " = ?";

        if(!StringUtils.isEmpty(orgCode)) {
            sql += " and ( " + AppDBHelper.DBColumn.ORG_ID + " = '" + orgCode + "' or " + AppDBHelper.DBColumn.IS_DOMAIN_APP + "=1" + ")";

        } else {
            sql += " and ( " + AppDBHelper.DBColumn.IS_DOMAIN_APP + "=1" + ")";

        }

        Cursor cursor = null;
        List<App> appList = new ArrayList<>();
        try {
            cursor = getReadableDatabase().rawQuery(sql, new String[]{"%" + searchKey + "%", "%" + searchKey + "%", "%" + searchKey + "%", "%" + searchKey + "%", 1 + ""});
            while (cursor.moveToNext()) {
                appList.add(AppDBHelper.fromCursor(cursor));
            }


            appList = filterAppsMatchSearchKey(searchKey, appList);


        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }



        return appList;
    }

    public List<AppBundles> searchAppBundles(final String searchValue,  final String orgId) {
        String sql = "select * from " + AppDBHelper.TABLE_NAME + " where " + AppDBHelper.DBColumn.SEARCHABLE + " like ? COLLATE NOCASE ";
        if(!StringUtils.isEmpty(orgId)) {
            sql += " and ( " + AppDBHelper.DBColumn.ORG_ID + " = '" + orgId + "' or " + AppDBHelper.DBColumn.IS_DOMAIN_APP + "=1" + ")";

        } else {
            sql += " and ( " + AppDBHelper.DBColumn.IS_DOMAIN_APP + "=1" + ")";
        }
        Cursor cursor = null;
        List<App> appList = new ArrayList<>();
        List<AppBundles> appBundleList = new ArrayList<>();
        try {
            cursor = getReadableDatabase().rawQuery(sql, new String[]{"%" + searchValue + "%"});
            while (cursor.moveToNext()) {
                appList.add(AppDBHelper.fromCursor(cursor));
            }
            appBundleList = filterAppBundlesMatchSearchKey(searchValue, appList);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return appBundleList;
    }

    /**
     * 因搜索条件中{@link App#getI18nInfo()} 涉及的 i18n_ 字段为 json 数据, 会错误把 json 里的key字段也搜索出来,
     * 所以此处会把搜索结果再次过滤一遍
     * */
    @NonNull
    private List<App> filterAppsMatchSearchKey(final String searchKey, List<App> appList) {
        if(CustomerHelper.isHcbm(BaseApplicationLike.baseContext)) {
            return hcbmFilterAppsMatchSearchKey(searchKey, appList);
        }
        appList = commonFilterAppsMatchSearchKey(searchKey, appList);
        return appList;
    }

    private List<AppBundles> filterAppBundlesMatchSearchKey(final String searchKey, List<App> appList) {
        List<AppBundles> filtedAppBundle = new ArrayList<>();
        for (App app : appList) {
            filterAppBundleInAppBySearchKey(searchKey, app, filtedAppBundle);
        }
        return filtedAppBundle;
    }

    private void filterAppBundleInAppBySearchKey(final String searchKey, final App app, final List<AppBundles> appBundleList) {
        for (AppBundles bundle : app.mBundles) {
            if (filterAppBundleBySearchKey(searchKey, bundle)) {
                appBundleList.add(bundle);
            }
        }
    }

    private boolean filterAppBundleBySearchKey(final String searchKey, AppBundles app) {
        if (CustomerHelper.isHcbm(BaseApplicationLike.baseContext)) {
            return hcbmFileterAppBundleBySearchKey(searchKey, app);
        }
        return commonFileterAppBundleBySearchKey(searchKey, app);
    }

    private boolean hcbmFileterAppBundleBySearchKey(final String searchKey, AppBundles bundle) {
        if (LanguageUtil.isZhLocal(BaseApplicationLike.baseContext)) {
            if (bundle.mBundleName.toLowerCase().contains(searchKey.toLowerCase())
            || bundle.mBundleTwName.toLowerCase().contains(searchKey.toLowerCase())
            || bundle.mBundleInitial.toLowerCase().contains(searchKey.toLowerCase())
            || bundle.mBundlePy.toLowerCase().contains(searchKey.toLowerCase())) {
                return true;
            }
        }
        if (bundle.mBundleEnName.toLowerCase().contains(searchKey.toLowerCase())) {
            return true;
        }
        return false;
    }

    private boolean commonFileterAppBundleBySearchKey(final String searchKey, AppBundles bundle) {
        if (bundle.mBundleName.toLowerCase().contains(searchKey.toLowerCase())
                || bundle.mBundleTwName.toLowerCase().contains(searchKey.toLowerCase())
                || bundle.mBundleInitial.toLowerCase().contains(searchKey.toLowerCase())
                || bundle.mBundlePy.toLowerCase().contains(searchKey.toLowerCase())
                || bundle.mBundleEnName.toLowerCase().contains(searchKey.toLowerCase())) {
            return true;
        }
        return false;
    }


    @NotNull
    private List<App> hcbmFilterAppsMatchSearchKey(final String searchKey, List<App> appList) {
        appList = CollectionsKt.filter(appList, new Function1<App, Boolean>() {
            @Override
            public Boolean invoke(App app) {

                if(null != app.mAppName && app.mAppName.toLowerCase().contains(searchKey.toLowerCase())) {
                    return true;
                }

                if(LanguageUtil.isZhLocal(BaseApplicationLike.baseContext)) {
                    if(null != app.mAppTwName && app.mAppTwName.toLowerCase().contains(searchKey.toLowerCase())) {
                        return true;
                    }

                    if(null != app.mAppInitial && app.mAppInitial.toLowerCase().contains(searchKey.toLowerCase())) {
                        return true;
                    }

                    if(null != app.mAppPinYin && app.mAppPinYin.toLowerCase().contains(searchKey.toLowerCase())) {
                        return true;
                    }

                } else {
                    if(null != app.mAppEnName && app.mAppEnName.toLowerCase().contains(searchKey.toLowerCase())) {
                        return true;
                    }

                }



                return false;
            }
        });
        return appList;
    }

    @NotNull
    private List<App> commonFilterAppsMatchSearchKey(final String searchKey, List<App> appList) {
        appList = CollectionsKt.filter(appList, new Function1<App, Boolean>() {
            @Override
            public Boolean invoke(App app) {

                if(null != app.mAppName && app.mAppName.toLowerCase().contains(searchKey.toLowerCase())) {
                    return true;
                }

                if(null != app.mAppTwName && app.mAppTwName.toLowerCase().contains(searchKey.toLowerCase())) {
                    return true;
                }

                if(null != app.mAppInitial && app.mAppInitial.toLowerCase().contains(searchKey.toLowerCase())) {
                    return true;
                }

                if(null != app.mAppPinYin && app.mAppPinYin.toLowerCase().contains(searchKey.toLowerCase())) {
                    return true;
                }

                if(null != app.mAppEnName && app.mAppEnName.toLowerCase().contains(searchKey.toLowerCase())) {
                    return true;
                }



                return false;
            }
        });
        return appList;
    }


    public boolean batchInsertOrUpdateAppCheckDb(List<App> apps) {
        if (apps.isEmpty()) {
            return true;
        }

        List<App> appsInDb = queryAccessApps(CollectionsKt.map(apps, new Function1<App, String>() {
            @Override
            public String invoke(App app) {
                return app.getId();
            }
        }));

        for(App app: apps) {
            for(App appInDb : appsInDb) {

                if(app.equals(appInDb)) {
                    if (!app.mNewVersionNotice) {
                        app.mNewVersionNotice = appInDb.mNewVersionNotice;
                    }
                    break;
                }
            }
        }

        return batchInsertOrUpdateApp(apps);
    }


    /**
     * 批量更新APPS
     *
     * @param apps
     * @return
     */
    public boolean batchInsertOrUpdateApp(List<App> apps) {
        if (apps.isEmpty()) {
            return true;
        }


        boolean result = false;
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        try {
            sqLiteDatabase.beginTransaction();

            for (App app : apps) {
                getWritableDatabase().insertWithOnConflict(
                        AppDBHelper.TABLE_NAME,
                        null,
                        AppDBHelper.getContentValue(app), SQLiteDatabase.CONFLICT_REPLACE);
            }
            sqLiteDatabase.setTransactionSuccessful();

            AppCache.getInstance().setAppsCache(apps);

            result = true;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            sqLiteDatabase.endTransaction();
        }
        return result;
    }

    /**
     * 批量删除 app
     * @param appIdList
     * */
    public boolean batchRemoveApp(List<String> appIdList) {
        boolean result = false;
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        try {
            sqLiteDatabase.beginTransaction();

            for (String id : appIdList) {
                removeApp(id);
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

    public boolean batchRemoveApp(String orgCode) {
        String removeSql = "delete from " + AppDBHelper.TABLE_NAME + " where " + AppDBHelper.DBColumn.ORG_ID + " = ?";
        getWritableDatabase().execSQL(removeSql, new String[]{orgCode});

        return true;
    }

    /**
     * 删除一个APP
     *
     * @param appId
     * @return
     */
    public boolean removeApp(String appId) {
        String removeSql = "delete from " + AppDBHelper.TABLE_NAME + " where " + AppDBHelper.DBColumn.APP_ID + " = ?";

        getWritableDatabase().execSQL(removeSql, new String[]{appId});

        AppCache.getInstance().removeAppCache(appId);
        return true;
    }

}
