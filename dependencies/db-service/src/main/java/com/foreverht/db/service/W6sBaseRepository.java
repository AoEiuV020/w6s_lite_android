package com.foreverht.db.service;

import android.util.Log;

import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.encryption.EncryptHelper;
import com.foreveross.db.BaseRepository;
import com.foreveross.db.SQLiteDatabase;


public abstract class W6sBaseRepository extends BaseRepository {


    @Override
    public SQLiteDatabase getWritableDatabaseCore() {
        return getWritableDatabase();
    }

    @Override
    public SQLiteDatabase getReadableDatabaseCore() {
        return getReadableDatabase();
    }

    @Override
    public String getDbNameCore() {
        return getDbName();
    }


    public static String getDbName() {
        String userId = LoginUserInfo.getInstance().getLoginUserId(BaseApplicationLike.baseContext);
        String dbName = userId + AtworkConfig.DB_SUFFIX;
        return dbName;
    }

    /**
     * 获取writeable databalse
     *
     * @return
     */
    public static SQLiteDatabase getWritableDatabase() {

        String dbName = getDbName();
        W6sDatabaseHelper atworkDatabaseHelper = W6sDatabaseHelper.getInstance(BaseApplicationLike.baseContext, dbName);
        String password = EncryptHelper.getWorkplusEncryptedKey();
        Log.d("DebugDB", "dbName = " + dbName + "encrypte password = " + password);
        SQLiteDatabase db = atworkDatabaseHelper.getStrategyWritableDatabase(password);
        return db;
    }

    /**
     * 获取readable database
     *
     * @return
     */
    public static SQLiteDatabase getReadableDatabase() {
        String dbName = getDbName();

        W6sDatabaseHelper atworkDatabaseHelper = W6sDatabaseHelper.getInstance(BaseApplicationLike.baseContext, dbName);
        String password = EncryptHelper.getWorkplusEncryptedKey();

        SQLiteDatabase db = atworkDatabaseHelper.getStrategyReadableDatabase(password);
        return db;
    }


}
