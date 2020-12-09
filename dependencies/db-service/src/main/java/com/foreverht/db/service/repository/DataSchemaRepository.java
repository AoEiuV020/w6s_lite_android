package com.foreverht.db.service.repository;

import android.database.Cursor;
import android.text.TextUtils;

import com.foreverht.db.service.W6sBaseRepository;
import com.foreverht.db.service.dbHelper.DataSchemasDBHelper;
import com.foreveross.atwork.infrastructure.model.employee.DataSchema;
import com.foreveross.atwork.infrastructure.model.Employee;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.infrastructure.utils.Logger;

import com.foreveross.db.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Created by shadow on 2016/5/4.
 */
public class DataSchemaRepository extends W6sBaseRepository {

    private static final String TAG = DataSchemaRepository.class.getSimpleName();

    private static DataSchemaRepository sInstance = new DataSchemaRepository();

    private DataSchemaRepository() {

    }

    public static DataSchemaRepository getInstance() {
        return sInstance;

    }


    /**
     * 根据雇员orgCode查询DataSchema
     */
    public List<DataSchema> queryDataSchemaByOrgCode(String orgCode) {
        List<DataSchema> dataSchemaList = new ArrayList<>();
        String sql = "select * from " + DataSchemasDBHelper.TABLE_NAME + " where " + DataSchemasDBHelper.DBColumn.ORG_CODE + "=?";
        Logger.d(TAG, sql);
        Cursor cursor = getWritableDatabase().rawQuery(sql, new String[]{orgCode});
        if (cursor != null) {
            while (cursor.moveToNext()) {
                DataSchema dataSchema = DataSchemasDBHelper.fromCursor(cursor);
                dataSchemaList.add(dataSchema);
            }
            cursor.close();
        }
        return dataSchemaList;
    }


    public HashMap<String, List<DataSchema>> queryDataSchemaByOrgCodes(Set<String> orgCodes) {
        HashMap<String, List<DataSchema>> dataSchemaMap = new HashMap<>();
        String sql = "select * from " + DataSchemasDBHelper.TABLE_NAME + " where " + DataSchemasDBHelper.DBColumn.ORG_CODE + " in (" + getInStringParams(new ArrayList<>(orgCodes)) + ")";
        Logger.d(TAG, sql);
        Cursor cursor = getWritableDatabase().rawQuery(sql, new String[]{});

        if (cursor != null) {
            while (cursor.moveToNext()) {
                DataSchema dataSchema = DataSchemasDBHelper.fromCursor(cursor);

                List<DataSchema> dataSchemaList = dataSchemaMap.get(dataSchema.mOrgCode);
                if(null == dataSchemaList) {
                    dataSchemaList = new ArrayList<>();
                    dataSchemaMap.put(dataSchema.mOrgCode, dataSchemaList);
                }

                dataSchemaList.add(dataSchema);
            }
            cursor.close();
        }
        return dataSchemaMap;

    }

    /**
     * 批量插入 emp list 的 dataSchema
     * */
    public boolean batchInsertMulEmpsDataSchema(List<Employee> employeeList) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.beginTransaction();

        for(Employee employee : employeeList) {
            removeDataSchemaList(employee.orgCode);
            insertDataSchemaList(employee.dataSchemaList);
        }

        sqLiteDatabase.setTransactionSuccessful();
        sqLiteDatabase.endTransaction();

        return true;
    }

    public void insertDataSchemaList(List<DataSchema> dataSchemaList) {
        if(ListUtil.isEmpty(dataSchemaList)) {
            return;
        }

        for (DataSchema dataSchema : dataSchemaList) {
            if (TextUtils.isEmpty(String.valueOf(dataSchema.mId))) {
                continue;
            }
            getWritableDatabase().insertWithOnConflict(
                    DataSchemasDBHelper.TABLE_NAME,
                    DataSchemasDBHelper.DBColumn.DATA_SCHEMA_ID,
                    DataSchemasDBHelper.getContentValues(dataSchema),
                    SQLiteDatabase.CONFLICT_REPLACE);
        }
    }

    /**
     * 根据组织 code 删除 dataschema
     * */
    public boolean removeDataSchemaList(String orgCode) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        int deletedCount = sqLiteDatabase.delete(DataSchemasDBHelper.TABLE_NAME, DataSchemasDBHelper.DBColumn.ORG_CODE + " = ?", new String[]{orgCode});

        return -1 != deletedCount;
    }


}
