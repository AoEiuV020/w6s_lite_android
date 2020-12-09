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
import com.foreveross.atwork.infrastructure.model.app.App;
import com.foreveross.atwork.infrastructure.model.app.AppBundles;
import com.foreveross.atwork.infrastructure.model.app.AppI18nInfo;
import com.foreveross.atwork.infrastructure.model.app.LightApp;
import com.foreveross.atwork.infrastructure.model.app.LocalApp;
import com.foreveross.atwork.infrastructure.model.app.NativeApp;
import com.foreveross.atwork.infrastructure.model.app.ServiceApp;
import com.foreveross.atwork.infrastructure.model.app.Shortcut;
import com.foreveross.atwork.infrastructure.model.app.SystemApp;
import com.foreveross.atwork.infrastructure.model.app.appEnum.AppKind;
import com.foreveross.atwork.infrastructure.model.app.appEnum.AppType;
import com.foreveross.atwork.infrastructure.model.app.appEnum.BundleType;
import com.foreveross.atwork.infrastructure.model.app.appEnum.DistributeMode;
import com.foreveross.atwork.infrastructure.model.app.appEnum.RecommendMode;
import com.foreveross.atwork.infrastructure.utils.AppWrapHelper;
import com.foreveross.atwork.infrastructure.utils.JsonUtil;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.infrastructure.utils.Logger;
import com.foreveross.db.SQLiteDatabase;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

/**
 * app数据 数据库字段模型
 * Created by reyzhang22 on 16/4/11.
 */
public class AppDBHelper implements DBHelper {

    private static final String TAG = AppDBHelper.class.getSimpleName();

    public static final String TABLE_NAME = "app_";

    /**
     * create table app_ (
     * app_id_ text ,app_type_ integer ,domain_id_ text ,
     * type_ text ,name_ text , i18n_ text , avatar_ text, app_intro_ text ,pinyin_ text ,
     * initial_ text ,category_id_ text ,category_ integer ,
     * category_sort integer ,app_refresh_time_ integer ,
     * category_refresh_time integer ,sort_ integer ,kind_ text ,
     * recommend_mode_ integer ,distribute_mode_ integer ,
     * app_params_ text , menu_ blob, app_data_ blob , show_in_market_ integer ,
     * shortcut_data_ blob, top_ integer, biometric_authentication_ text,
     * primary key  ( app_id_,app_type_ )  )
     */
    private static final String EXEC_SQL = CREATE_TABLE + TABLE_NAME + LEFT_BRACKET +
                                            DBColumn.APP_ID + TEXT+ COMMA +
                                            DBColumn.APP_TYPE + INTEGER + COMMA +
                                            DBColumn.ORG_ID + TEXT + COMMA +
                                            DBColumn.DOMAIN_ID + TEXT +COMMA +
                                            DBColumn.TYPE + TEXT + COMMA +
                                            DBColumn.NAME + TEXT + COMMA +
                                            DBColumn.I18N + TEXT + COMMA +
                                            DBColumn.AVATAR + TEXT + COMMA +
                                            DBColumn.APP_INTRO + TEXT + COMMA +
                                            DBColumn.APP_PINYIN + TEXT + COMMA +
                                            DBColumn.INITIAL + TEXT +COMMA +
                                            DBColumn.CATEGORY_ID + TEXT + COMMA +
                                            DBColumn.CATEGORY + INTEGER + COMMA +
                                            DBColumn.CATEGORY_SORT + INTEGER + COMMA +
                                            DBColumn.APP_REFRESH_TIME + INTEGER + COMMA +
                                            DBColumn.CATEGORY_REFRESH_TIME + INTEGER + COMMA +
                                            DBColumn.SORT + INTEGER + COMMA +
                                            DBColumn.KIND + TEXT + COMMA +
                                            DBColumn.RECOMMEND_MODE + INTEGER + COMMA +
                                            DBColumn.DISTRIBUTE_MODE + INTEGER + COMMA +
                                            DBColumn.APP_PARAMS + TEXT + COMMA +
                                            DBColumn.MENU + TEXT + COMMA +
                                            DBColumn.APP_DATA + BLOB + COMMA +
                                            DBColumn.SHOW_IN_MARKET + INTEGER + COMMA +
                                            DBColumn.SHORTCUT_DATA + BLOB + COMMA +
                                            DBColumn.TOP + INTEGER + COMMA +
                                            DBColumn.COMPONENT_MODE + INTEGER + COMMA +
                                            DBColumn.BIOMETRIC_AUTHENTICATION + TEXT + COMMA +
                                            DBColumn.IS_DOMAIN_APP + INTEGER + COMMA +
                                            DBColumn.SEARCHABLE + TEXT + COMMA +
                                            DBColumn.MAIN_BUNDLE_ID + TEXT + COMMA +
                                            PRIMARY_KEY + LEFT_BRACKET +
                                            DBColumn.APP_ID + COMMA +
                                            DBColumn.APP_TYPE + RIGHT_BRACKET +
                                            RIGHT_BRACKET;

    @Override
    public void onCreate(SQLiteDatabase db) {
        Logger.d(TAG, "sql = " + EXEC_SQL);
        db.execSQL(EXEC_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion < 8){
            W6sDatabaseHelper.addColumnToTable(db, TABLE_NAME, DBColumn.SHOW_IN_MARKET, "text");
            W6sDatabaseHelper.addColumnToTable(db, TABLE_NAME, DBColumn.SHORTCUT_DATA, "blob");

            oldVersion = 8;
        }

        if(oldVersion < 10) {
            W6sDatabaseHelper.addColumnToTable(db, TABLE_NAME, DBColumn.TOP, "integer");

            oldVersion = 10;
        }


        if(oldVersion < 18) {
            W6sDatabaseHelper.addColumnToTable(db, TABLE_NAME, DBColumn.I18N, "text");

            oldVersion = 18;
        }


        if(oldVersion < 32) {
            W6sDatabaseHelper.addColumnToTable(db, TABLE_NAME, DBColumn.BIOMETRIC_AUTHENTICATION, "text");

            oldVersion = 32;
        }

        if(oldVersion < 40) {
            W6sDatabaseHelper.addColumnToTable(db, TABLE_NAME, DBColumn.COMPONENT_MODE, "integer");

            oldVersion = 40;
        }

        if(oldVersion < 230) {
            W6sDatabaseHelper.addColumnToTable(db, TABLE_NAME, DBColumn.IS_DOMAIN_APP, "integer");

            oldVersion = 230;
        }
        if (oldVersion < 250) {
            W6sDatabaseHelper.addColumnToTable(db, TABLE_NAME, DBColumn.SEARCHABLE, "text");
            oldVersion = 250;
        }
        if (oldVersion < 260) {
            W6sDatabaseHelper.addColumnToTable(db, TABLE_NAME, DBColumn.MAIN_BUNDLE_ID, "text");
            oldVersion = 260;
        }

    }

    public static App fromCursor(Cursor cursor) {
        int index = -1;
        App app = null;
        AppKind appKind = null;
        List<AppBundles> appBundles = null;

        if ((index = cursor.getColumnIndex(DBColumn.APP_DATA)) != -1) {
            appBundles = new Gson().fromJson(cursor.getString(index), new TypeToken<List<AppBundles>>() {
            }.getType());

//            appBundles = JsonUtil.fromJsonList(cursor.getString(index));
        }

        if ((index = cursor.getColumnIndex(DBColumn.KIND)) != -1) {
            appKind = AppKind.fromStringValue(cursor.getString(index));

            //app 转型
            if (AppKind.LightApp.equals(appKind)) {
                app = new LightApp();
                AppWrapHelper.wrapLightApp((LightApp) app, appBundles);


            } else if (AppKind.NativeApp.equals(appKind)) {
                if (!ListUtil.isEmpty(appBundles)) {
                    AppBundles appBundle = appBundles.get(0);

                    if (BundleType.System.equals(appBundles.get(0).mBundleType)) {
                        app = new SystemApp();
                        AppWrapHelper.wrapSystemApp((SystemApp) app, appBundle);

                    } else {
                        app = new NativeApp();
                        AppWrapHelper.wrapNativeApp((NativeApp) app, appBundle);
                    }
                }

            } else if (AppKind.ServeNo.equals(appKind)) {
                app = new ServiceApp();
                if ((index = cursor.getColumnIndex(DBColumn.MENU)) != -1) {
                    ((ServiceApp) app).menuJson = cursor.getString(index);
                }

            } else if (AppKind.NativeEmail.equals(appKind)) {
                app = new LocalApp();
            }

        }

        if (null == app) {
            app = new LightApp();
        }

        if (!ListUtil.isEmpty(appBundles)) {
            for (int i = 0; i < appBundles.size(); i++) {
                AppBundles.wrapAppBundleData(app);
            }
        }

        app.mAppKind = appKind;
        app.mBundles = appBundles;

        if ((index = cursor.getColumnIndex(DBColumn.APP_ID)) != -1) {
            app.mAppId = cursor.getString(index);
        }
        if ((index = cursor.getColumnIndex(DBColumn.NAME)) != -1) {
            app.mAppName = cursor.getString(index);
        }

        if ((index = cursor.getColumnIndex(DBColumn.I18N)) != -1) {
            AppI18nInfo i18nInfo = JsonUtil.fromJson(cursor.getString(index), AppI18nInfo.class);
            if (null != i18nInfo) {
                app.mAppEnName = i18nInfo.getEnName();
                app.mAppTwName = i18nInfo.getTwName();
                app.mCategoryEnName = i18nInfo.getCategoryEnName();
                app.mCategoryTwName = i18nInfo.getCategoryTwName();
            }
        }

        if ((index = cursor.getColumnIndex(DBColumn.AVATAR)) != -1) {
            app.mAvatar = cursor.getString(index);
        }


        if ((index = cursor.getColumnIndex(DBColumn.APP_INTRO)) != -1) {
            app.mAppIntro = cursor.getString(index);
        }
//        if ((index = cursor.getColumnIndex(DBColumn.APP_PARAMS)) != -1){
//            app.mAppParams = new Gson().fromJson(cursor.getString(index), HashMap.class);
//        }
        if ((index = cursor.getColumnIndex(DBColumn.APP_REFRESH_TIME)) != -1) {
            app.mAppRefreshTime = cursor.getLong(index);
        }
        if ((index = cursor.getColumnIndex(DBColumn.APP_TYPE)) != -1) {
            app.mAppType = AppType.toAppType(cursor.getInt(index));
        }
        if ((index = cursor.getColumnIndex(DBColumn.CATEGORY)) != -1) {
            app.mCategoryName = cursor.getString(index);
        }
        if ((index = cursor.getColumnIndex(DBColumn.CATEGORY_ID)) != -1) {
            app.mCategoryId = cursor.getString(index);
        }
        if ((index = cursor.getColumnIndex(DBColumn.CATEGORY_REFRESH_TIME)) != -1) {
            app.mCategoryRefreshTime = cursor.getLong(index);
        }
        if ((index = cursor.getColumnIndex(DBColumn.CATEGORY_SORT)) != -1) {
            app.mCategorySort = cursor.getInt(index);
        }
        if ((index = cursor.getColumnIndex(DBColumn.DISTRIBUTE_MODE)) != -1) {
            app.mAppDistributeMode = DistributeMode.getDisplayMode(cursor.getInt(index));
        }
        if ((index = cursor.getColumnIndex(DBColumn.DOMAIN_ID)) != -1) {
            app.mDomainId = cursor.getString(index);
        }
        if ((index = cursor.getColumnIndex(DBColumn.INITIAL)) != -1) {
            app.mAppInitial = cursor.getString(index);
        }

        if ((index = cursor.getColumnIndex(DBColumn.APP_PINYIN)) != -1) {
            app.mAppPinYin = cursor.getString(index);
        }
        if ((index = cursor.getColumnIndex(DBColumn.RECOMMEND_MODE)) != -1) {
            app.mAppRecommendMode = RecommendMode.toRecommendMode(cursor.getInt(index));
        }
        if ((index = cursor.getColumnIndex(DBColumn.SORT)) != -1) {
            app.mAppSort = cursor.getInt(index);
        }
        if ((index = cursor.getColumnIndex(DBColumn.ORG_ID)) != -1) {
            app.mOrgId = cursor.getString(index);
        }



        if ((index = cursor.getColumnIndex(DBColumn.SHORTCUT_DATA)) != -1) {
            String result = cursor.getString(index);
            app.mShortcut = JsonUtil.fromJson(result, Shortcut.class);
        }

        if ((index = cursor.getColumnIndex(DBColumn.SHOW_IN_MARKET)) != -1) {
            int showInMarket = cursor.getInt(index);
            if(1== showInMarket) {
                app.mHideMode = 0;

            } else {
                app.mHideMode = 1;

            }
        }


        if ((index = cursor.getColumnIndex(DBColumn.TOP)) != -1) {
            app.mTop = cursor.getInt(index);

        }


        if ((index = cursor.getColumnIndex(DBColumn.BIOMETRIC_AUTHENTICATION)) != -1) {
            app.mAppBiologicalAuth = cursor.getString(index);

        }


        if ((index = cursor.getColumnIndex(DBColumn.COMPONENT_MODE)) != -1) {
            app.mComponentMode = cursor.getInt(index);

        }


        if ((index = cursor.getColumnIndex(DBColumn.IS_DOMAIN_APP)) != -1) {
            int isDomainApp = cursor.getInt(index);
            app.mIsDomainApp = 1 == isDomainApp;


        }

        if ((index = cursor.getColumnIndex(DBColumn.SEARCHABLE)) != -1) {
            app.mSearchable = cursor.getString(index);
        }

        if ((index = cursor.getColumnIndex(DBColumn.MAIN_BUNDLE_ID)) != -1) {
            app.mRealMainBundleId = cursor.getString(index);
        }

        return app;
    }


    /**
     * 获取应用contentValue
     * @param app
     * @return
     */
    public static ContentValues getContentValue(App app) {

        if (!ListUtil.isEmpty(app.mBundles)) {
            AppBundles appBundle = app.mBundles.get(0);
            appBundle.mNewVersionNotice = app.mNewVersionNotice;
        }

        ContentValues cv = new ContentValues();
        cv.put(DBColumn.APP_ID, app.mAppId);
        cv.put(DBColumn.APP_INTRO, app.mAppIntro);
        cv.put(DBColumn.APP_PARAMS, JsonUtil.toJson(app.mAppParams));
        cv.put(DBColumn.CATEGORY, app.mCategoryName);
        cv.put(DBColumn.CATEGORY_ID, app.mCategoryId);
        cv.put(DBColumn.CATEGORY_SORT, app.mCategorySort);
        cv.put(DBColumn.DISTRIBUTE_MODE, app.mAppDistributeMode.intValue());
        cv.put(DBColumn.DOMAIN_ID, app.mDomainId);
        cv.put(DBColumn.INITIAL, app.mAppInitial);
        cv.put(DBColumn.NAME, app.mAppName);
        cv.put(DBColumn.I18N, JsonUtil.toJson(app.getI18nInfo()));
        cv.put(DBColumn.AVATAR, app.mAvatar);
        cv.put(DBColumn.APP_PINYIN, app.mAppPinYin);
        cv.put(DBColumn.RECOMMEND_MODE, app.mAppRecommendMode.intValue());
        cv.put(DBColumn.SORT, app.mAppSort);
        //app type 为 移动端控制的属性, 若null 的话视为 access 类型
        if(null == app.mAppType) {
            app.mAppType = AppType.Access;
        }
        cv.put(DBColumn.APP_TYPE, app.mAppType.intValue());
        cv.put(DBColumn.TYPE, app.mAppType.intValue());
        cv.put(DBColumn.APP_REFRESH_TIME, app.mAppRefreshTime);
        cv.put(DBColumn.CATEGORY_REFRESH_TIME, app.mCategoryRefreshTime);
        cv.put(DBColumn.APP_DATA, JsonUtil.toJson(app.mBundles));
        cv.put(DBColumn.KIND, app.mAppKind.stringValue());
        cv.put(DBColumn.ORG_ID, app.mOrgId);
        if(app.isShowInMarketBasic()) {
            cv.put(DBColumn.SHOW_IN_MARKET, 1);
        } else {
            cv.put(DBColumn.SHOW_IN_MARKET, 0);

        }

        if (null != app.mShortcut) {
            cv.put(DBColumn.SHORTCUT_DATA, JsonUtil.toJson(app.mShortcut));
        }

        cv.put(DBColumn.TOP, app.mTop);
        cv.put(DBColumn.BIOMETRIC_AUTHENTICATION, app.mAppBiologicalAuth);
        cv.put(DBColumn.COMPONENT_MODE, app.mComponentMode);

        if(app.mIsDomainApp) {
            cv.put(DBColumn.IS_DOMAIN_APP, 1);
        } else {
            cv.put(DBColumn.IS_DOMAIN_APP, 0);

        }

        if(app instanceof ServiceApp) {
            ServiceApp serviceApp = (ServiceApp) app;
            cv.put(DBColumn.MENU, serviceApp.menuJson);
        }

        StringBuilder searchable = new StringBuilder();
        for (AppBundles bundle : app.mBundles) {
            searchable.append(bundle.mBundleName).append(bundle.mBundleEnName).append(bundle.mBundleTwName)
                    .append(bundle.mBundlePy).append(bundle.mBundleInitial);
        }

        cv.put(DBColumn.SEARCHABLE, searchable.toString());
        cv.put(DBColumn.MAIN_BUNDLE_ID, app.mRealMainBundleId);

        return cv;
    }


    public class DBColumn {

        public static final String APP_ID = "app_id_";

        public static final String APP_TYPE = "app_type_";

        public static final String ORG_ID = "org_id_";

        public static final String DOMAIN_ID = "domain_id_";

        public static final String TYPE = "type_";

        public static final String NAME = "name_";

        public static final String I18N = "i18n_";

        public static final String AVATAR = "avatar_";

        public static final String APP_INTRO = "app_intro_";

        public static final String APP_PINYIN = "pinyin_";

        public static final String INITIAL = "initial_";

        public static final String CATEGORY_ID = "category_id_";

        public static final String CATEGORY = "category_";

        public static final String CATEGORY_SORT = "category_sort";

        public static final String SORT = "sort_";

        public static final String RECOMMEND_MODE = "recommend_mode_";

        public static final String DISTRIBUTE_MODE = "distribute_mode_";

        public static final String APP_PARAMS = "app_params_";

        public static final String MENU = "menu_";

        public static final String APP_DATA = "app_data_";

        public static final String APP_REFRESH_TIME = "app_refresh_time_";

        public static final String CATEGORY_REFRESH_TIME = "category_refresh_time";

        public static final String KIND = "kind_";

        public static final String SHOW_IN_MARKET = "show_in_market_";

        public static final String SHORTCUT_DATA = "shortcut_data_";

        public static final String TOP = "top_";

        public static final String COMPONENT_MODE = "component_mode_";

        public static final String BIOMETRIC_AUTHENTICATION = "biometric_authentication_";

        public static final String IS_DOMAIN_APP = "is_domain_app_";

        public static final String SEARCHABLE = "searchable_";

        public static final String MAIN_BUNDLE_ID = "main_bundle_id";

    }
}
