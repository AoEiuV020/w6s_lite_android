package com.foreverht.db.service.repository;

import com.foreverht.db.service.EmpIncomingCallDatabaseHelper;
import com.foreverht.db.service.W6sDatabaseHelper;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.utils.encryption.EncryptHelper;
import com.foreveross.db.BaseRepository;
import com.foreveross.db.SQLiteDatabase;

/**
 * create by reyzhang22 at 2019-08-19
 */
public class EmpIncomingCallRepository extends BaseRepository {

    private static final String EMP_INCOMING_CALL_DB_PREFIX = "emp_incoming_call_";

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
        String dbName = EMP_INCOMING_CALL_DB_PREFIX + userId;
        return dbName;
    }

    /**
     * 获取writeable databalse
     *
     * @return
     */
    public static SQLiteDatabase getWritableDatabase() {
        String dbName = getDbName();

        EmpIncomingCallDatabaseHelper atworkDatabaseHelper = EmpIncomingCallDatabaseHelper.getInstance(BaseApplicationLike.baseContext, dbName);
        String password = EncryptHelper.getWorkplusEncryptedKey();

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

        EmpIncomingCallDatabaseHelper atworkDatabaseHelper = EmpIncomingCallDatabaseHelper.getInstance(BaseApplicationLike.baseContext, dbName);
        String password = EncryptHelper.getWorkplusEncryptedKey();

        SQLiteDatabase db = atworkDatabaseHelper.getStrategyReadableDatabase(password);
        return db;
    }

}
