package com.foreverht.db.service;

import android.content.Context;

import com.foreverht.db.service.dbHelper.DBHelper;
import com.foreverht.db.service.dbHelper.EmpIncomingCallDBHelper;
import com.foreverht.db.service.repository.EmpIncomingCallRepository;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.db.BaseDatabaseHelper;
import com.foreveross.db.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * create by reyzhang22 at 2019-08-19
 */
public class EmpIncomingCallDatabaseHelper extends BaseDatabaseHelper {

    private static final String SQLITE = ".sqlite";

    private static List<DBHelper> dbHelperList = new ArrayList<>();

    private static Map<String, EmpIncomingCallDatabaseHelper> atworkDatabaseHelperMap = new HashMap<>();


    static {
        dbHelperList.add(new EmpIncomingCallDBHelper());
    }

    private EmpIncomingCallDatabaseHelper(Context context, String dbName) {
        super(context, dbName, AtworkConfig.DB_VERSION_EMP_INCOMING_CALL);
    }

    public static synchronized EmpIncomingCallDatabaseHelper getInstance(Context context, String identifier) {
        EmpIncomingCallDatabaseHelper atworkDatabaseHelper = atworkDatabaseHelperMap.get(identifier);
        if (atworkDatabaseHelper == null) {
            String dbName = identifier + SQLITE;
            atworkDatabaseHelperMap.put(identifier, new EmpIncomingCallDatabaseHelper(context, dbName));
        }
        return atworkDatabaseHelperMap.get(identifier);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        for (DBHelper dbHelper : dbHelperList) {
            dbHelper.onCreate(db);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for (DBHelper dbHelper : dbHelperList) {
            dbHelper.onUpgrade(db, oldVersion, newVersion);
        }
    }


    public static void deleteDb(Context context) {
        try {
            String dbNamePrefix = EmpIncomingCallRepository.getDbName();
            atworkDatabaseHelperMap.remove(dbNamePrefix);
            String dbName = dbNamePrefix + SQLITE;
            context.deleteDatabase(dbName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
