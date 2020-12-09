package com.foreveross.atwork.modules.voip.support.qsy;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.foreveross.atwork.infrastructure.model.voip.CountryCode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by RocXu on 2016/2/29.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final String TAG = DBHelper.class.getSimpleName();
    private static DBHelper instance;

    /**
     * @param context 上下文环境
     * @brief 数据库管理对象构造方法
     */
    private DBHelper(Context context) {
        super(context, Constants.COUNTRY_CODE_DB_NAME, null, 1);
    }

    public static DBHelper getInstance(Context context) {
        if (instance == null) {
            synchronized (TAG) {
                if (instance == null) {
                    instance = new DBHelper(context);
                }
            }
        }
        return instance;
    }

    /**
     * @param db
     * @brief 第一次使用数据库时创建数据库
     * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    /**
     * @param db
     * @param oldVersion
     * @param newVersion
     * @brief 数据库结构变化后升级数据库
     * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase,
     * int, int)
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    /**
     * @param db 数据库
     * @return 没有被锁返回true；被锁则返回false
     * @brief 判断数据库是否被锁
     */
    public boolean isDBNotLock(SQLiteDatabase db) {
        while (db.isDbLockedByOtherThreads()) {
            Log.w(TAG, "db is locked by other threads!");
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    /**
     * @brief 查询国别码列表
     * @return List<CountryCode>类型，国别码列表，
     * @note 如果查询失败，则返回空的List
     *
     */
    public synchronized List<CountryCode> queryCountryCodeList() {
        List<CountryCode> countryCodeList = null;
        SQLiteDatabase db = null;
        Cursor c = null;
        try {
            db = getReadableDatabase();
            if (db != null && isDBNotLock(db)) {
                String columns[] = { country_code.COLUMN_ID,
                        country_code.COLUMN_COUNTRY_CODE,
                        country_code.COLUMN_COUNTRY_CH_NAME,
                        country_code.COLUMN_COUNTRY_EN_NAME };
                String orderBy = String.format("%s asc",
                        country_code.COLUMN_COUNTRY_EN_NAME);
                c = db.query(country_code.TABLE_NAME, columns,
                        null, null, null, null, orderBy);
                if(c == null){
                    countryCodeList = new ArrayList<CountryCode>(0);
                }else if(!c.moveToFirst()){
                    countryCodeList = new ArrayList<CountryCode>(0);
                }else{
                    countryCodeList = new ArrayList<CountryCode>(c.getCount());
                    int i = 0;
                    do {
                        CountryCode cCode = new CountryCode();
                        cCode.index = c.getInt(i++);
                        cCode.countryCode = c.getString(i++);
                        cCode.countryCHName = c.getString(i++);
                        cCode.countryENName = c.getString(i++);
                        countryCodeList.add(cCode);
                        i = 0;
                    } while (c.moveToNext());
                }
            } else {
                countryCodeList = new ArrayList<CountryCode>(0);
            }
        } catch (Exception e) {
            countryCodeList = new ArrayList<CountryCode>(0);
        } finally {
            if (c != null) {
                c.close();
                c = null;
            }
            if (db != null) {
                close(db);
                db = null;
            }
        }
        return countryCodeList;
    }

    public void close(SQLiteDatabase db){
        if(db != null){
            // 暂时不关闭数据库连接
            // db.close();
            db = null;
        }
    }

    /**
     * @class country_code
     * @brief 国别码信息表中表名及字段名定义
     * @author guanghua.xiao
     * @date 2013-7-19 下午2:01:22
     */
    private class country_code {
        /// 国别码信息表表名定义
        public static final String TABLE_NAME = "country_code";
        /// 记录索引（主键）
        public static final String COLUMN_ID = "_id";
        /// 国家中文名称（如：中国、美国等）
        public static final String COLUMN_COUNTRY_CH_NAME = "country_cn_name";
        /// 国家英文名称 （如：China、America等)
        public static final String COLUMN_COUNTRY_EN_NAME = "country_en_name";
        /// 国别码（电话号码中的国家前缀，如：中国为+86）
        public static final String COLUMN_COUNTRY_CODE = "country_code";
    }
}
