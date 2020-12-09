package com.foreverht.db.service;

import android.content.Context;

import com.foreverht.db.service.dbHelper.AppDBHelper;
import com.foreverht.db.service.dbHelper.ClickStatisticsDBHelper;
import com.foreverht.db.service.dbHelper.ConfigSettingDBHelper;
import com.foreverht.db.service.dbHelper.ContactPropertyDBHelper;
import com.foreverht.db.service.dbHelper.CustomerMessageNoticeDBHelper;
import com.foreverht.db.service.dbHelper.DBHelper;
import com.foreverht.db.service.dbHelper.DataSchemasDBHelper;
import com.foreverht.db.service.dbHelper.DeptDBHelper;
import com.foreverht.db.service.dbHelper.DeptTreeDBHelper;
import com.foreverht.db.service.dbHelper.DiscussionDBHelper;
import com.foreverht.db.service.dbHelper.DiscussionMemberDBHelper;
import com.foreverht.db.service.dbHelper.DropboxConfigDBHelper;
import com.foreverht.db.service.dbHelper.DropboxDBHelper;
import com.foreverht.db.service.dbHelper.EmployeeDBHelper;
import com.foreverht.db.service.dbHelper.EmployeePropertyRecordDBHelper;
import com.foreverht.db.service.dbHelper.MessageAppDBHelper;
import com.foreverht.db.service.dbHelper.MessageDBHelper;
import com.foreverht.db.service.dbHelper.MessageRecordDBHelper;
import com.foreverht.db.service.dbHelper.MessageTagsDBHelper;
import com.foreverht.db.service.dbHelper.OrgApplyDBHelper;
import com.foreverht.db.service.dbHelper.OrgRelationShipDbHelper;
import com.foreverht.db.service.dbHelper.OrganizationDBHelper;
import com.foreverht.db.service.dbHelper.PropertyDBHelper;
import com.foreverht.db.service.dbHelper.ReceiptDBHelper;
import com.foreverht.db.service.dbHelper.RecentFileDBHelper;
import com.foreverht.db.service.dbHelper.RelationshipDBHelper;
import com.foreverht.db.service.dbHelper.SessionDBHelper;
import com.foreverht.db.service.dbHelper.SessionFaultageRecordDBHelper;
import com.foreverht.db.service.dbHelper.UnreadMessageDbHelper;
import com.foreverht.db.service.dbHelper.UnreadSubcriptionMessageDBHelper;
import com.foreverht.db.service.dbHelper.UserDBHelper;
import com.foreverht.db.service.dbHelper.VoipMeetingMemberDBHelper;
import com.foreverht.db.service.dbHelper.VoipMeetingRecordDBHelper;
import com.foreverht.db.service.dbHelper.VolumeDBHelper;
import com.foreverht.db.service.dbHelper.WaterMarkDBHelper;
import com.foreverht.db.service.dbHelper.WorkbenchBoardDbHelper;
import com.foreverht.db.service.dbHelper.WorkbenchCardDataDbHelper;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.db.BaseDatabaseHelper;
import com.foreveross.db.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class W6sDatabaseHelper extends BaseDatabaseHelper {

    private static final String SQLITE = ".sqlite";

    private static List<DBHelper> dbHelperList = new ArrayList<>();

    private static Map<String, W6sDatabaseHelper> atworkDatabaseHelperMap = new HashMap<>();


    static {
        dbHelperList.add(new SessionDBHelper());
        dbHelperList.add(new ContactPropertyDBHelper());
        dbHelperList.add(new MessageDBHelper());
        dbHelperList.add(new SessionFaultageRecordDBHelper());
        dbHelperList.add(new MessageAppDBHelper());
        dbHelperList.add(new UnreadSubcriptionMessageDBHelper());
        dbHelperList.add(new PropertyDBHelper());
        dbHelperList.add(new ReceiptDBHelper());
        dbHelperList.add(new RecentFileDBHelper());
        dbHelperList.add(new CustomerMessageNoticeDBHelper());
        dbHelperList.add(new EmployeePropertyRecordDBHelper());
        dbHelperList.add(new VoipMeetingRecordDBHelper());
        dbHelperList.add(new VoipMeetingMemberDBHelper());
        dbHelperList.add(new UserDBHelper());
        dbHelperList.add(new DiscussionDBHelper());
        dbHelperList.add(new OrganizationDBHelper());
        dbHelperList.add(new DeptDBHelper());
        dbHelperList.add(new DeptTreeDBHelper());
        dbHelperList.add(new RelationshipDBHelper());
        dbHelperList.add(new AppDBHelper());
        dbHelperList.add(new DiscussionMemberDBHelper());
        dbHelperList.add(new DataSchemasDBHelper());
        dbHelperList.add(new EmployeeDBHelper());
        dbHelperList.add(new OrgRelationShipDbHelper());
        dbHelperList.add(new UnreadMessageDbHelper());
        dbHelperList.add(new OrgApplyDBHelper());
        dbHelperList.add(new DropboxDBHelper());
        dbHelperList.add(new DropboxConfigDBHelper());
        dbHelperList.add(new WaterMarkDBHelper());
//        dbHelperList.add(new ConfigSettingDBHelperV0());
        dbHelperList.add(new ConfigSettingDBHelper());
        dbHelperList.add(new ClickStatisticsDBHelper());
        dbHelperList.add(new WorkbenchBoardDbHelper());
        dbHelperList.add(new WorkbenchCardDataDbHelper());
        dbHelperList.add(new MessageTagsDBHelper());
        dbHelperList.add(new MessageRecordDBHelper());
        dbHelperList.add(new VolumeDBHelper());
    }

    private W6sDatabaseHelper(Context context, String dbName) {
        super(context, dbName, AtworkConfig.DB_VERSION);
    }

    public static synchronized W6sDatabaseHelper getInstance(Context context, String identifier) {
        W6sDatabaseHelper atworkDatabaseHelper = atworkDatabaseHelperMap.get(identifier);
        if (atworkDatabaseHelper == null) {
            String dbName = identifier + SQLITE;
            atworkDatabaseHelperMap.put(identifier, new W6sDatabaseHelper(context, dbName));
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
            String dbNamePrefix = W6sBaseRepository.getDbName();
            atworkDatabaseHelperMap.remove(dbNamePrefix);
            String dbName = dbNamePrefix + SQLITE;
            context.deleteDatabase(dbName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
