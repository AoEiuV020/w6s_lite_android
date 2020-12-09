package com.foreverht.db.service.repository;

import android.content.Context;
import android.database.Cursor;
import androidx.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.foreverht.cache.EmployeeCache;
import com.foreverht.db.service.EmpIncomingCallDatabaseHelper;
import com.foreverht.db.service.W6sBaseRepository;
import com.foreverht.db.service.dbHelper.EmpIncomingCallDBHelper;
import com.foreverht.db.service.dbHelper.EmployeeDBHelper;
import com.foreveross.atwork.infrastructure.model.Employee;
import com.foreveross.atwork.infrastructure.utils.ContactHelper;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.db.SQLiteDatabase;
import com.w6s.model.incomingCall.IncomingCaller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by shadow on 2016/4/22.
 */
public class EmployeeRepository extends W6sBaseRepository {

    private static final String TAG = EmployeeRepository.class.getSimpleName();

    private static EmployeeRepository sInstance = new EmployeeRepository();

    private EmployeeRepository() {

    }

    public static EmployeeRepository getInstance() {
        return sInstance;
    }

    /**
     * 查询 employee 列表, 带有 schema 数据
     */
    @NonNull
    public List<Employee> queryEmployeeList(String userId, List<String> orgCodeList) {
        List<Employee> empList = new ArrayList<>();
        String sql = "select * from " + EmployeeDBHelper.TABLE_NAME + " where " + EmployeeDBHelper.DBColumn.ORG_CODE + " in (" + getInStringParams(orgCodeList) + ") and " + EmployeeDBHelper.DBColumn.USER_ID + "= ?";

        SQLiteDatabase db = getWritableDatabase();

        Cursor cursor = null;
        try {
            cursor = db.rawQuery(sql, new String[]{userId});

            if (null != cursor) {

                while (cursor.moveToNext()) {
                    Employee employee = EmployeeDBHelper.fromCursor(cursor);
                    employee.dataSchemaList = DataSchemaRepository.getInstance().queryDataSchemaByOrgCode(employee.orgCode);
                    empList.add(employee);

                    //update cache
                    EmployeeCache.getInstance().setEmpCache(employee);

                }
            }
        } finally {
            if (null != cursor) {
                cursor.close();
            }
        }

        return empList;
    }

    /**
     * 根据雇员user_id查询雇员
     */
    public List<Employee> queryEmployeeByUserId(String userId) {
        List<Employee> employeeList = new ArrayList<>();
        String sql = "select * from " + EmployeeDBHelper.TABLE_NAME + " where " + EmployeeDBHelper.DBColumn.USER_ID + "=?";

        Cursor cursor = getWritableDatabase().rawQuery(sql, new String[]{userId});
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Employee employee = EmployeeDBHelper.fromCursor(cursor);
                employee.dataSchemaList = DataSchemaRepository.getInstance().queryDataSchemaByOrgCode(employee.orgCode);

                employeeList.add(employee);
            }
            cursor.close();
        }
        return employeeList;
    }


    /**
     * 根据雇员user_id orgCode 查询雇员
     */
    public Employee queryEmployeeByUserIdAndOrgCode(String userId, String orgCode) {
        Employee employee = null;
        String sql = "select * from " + EmployeeDBHelper.TABLE_NAME + " where " + EmployeeDBHelper.DBColumn.USER_ID + " =? and "
                + EmployeeDBHelper.DBColumn.ORG_CODE + " =? ";

        Cursor cursor = getWritableDatabase().rawQuery(sql, new String[]{userId, orgCode});
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                employee = EmployeeDBHelper.fromCursor(cursor);

                //update cache
                EmployeeCache.getInstance().setEmpCache(employee);
            }
            cursor.close();
        }
        return employee;
    }


    /**
     * 根据雇员employee_id查询雇员
     */
    public Employee queryEmployeeByEmployeeId(int employeeId) {
        Employee employee = null;
        String id = String.valueOf(employeeId);
        String sql = "select * from " + EmployeeDBHelper.TABLE_NAME + " where " + EmployeeDBHelper.DBColumn.EMPLOYEE_ID + "=?";

        Cursor cursor = getWritableDatabase().rawQuery(sql, new String[]{id});
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                employee = EmployeeDBHelper.fromCursor(cursor);
            }
            cursor.close();
        }
        return employee;
    }

    /**
     * 根据雇员employee_id查询雇员
     */
    public Employee queryEmployeeByEmployeeName(String employeeName) {
        Employee employee = null;
        String name = String.valueOf(employeeName);
        String sql = "select * from " + EmployeeDBHelper.TABLE_NAME + " where " + EmployeeDBHelper.DBColumn.NAME + "=?";

        Cursor cursor = getWritableDatabase().rawQuery(sql, new String[]{name});
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                employee = EmployeeDBHelper.fromCursor(cursor);
            }
            cursor.close();
        }
        return employee;
    }

    /**
     * 向数据库插入雇员信息，同时更新雇员缓存
     *
     * @param employee
     * @return
     */
    public boolean insertEmployee(Employee employee) {
        boolean isSuccess;
        long insertResult = getWritableDatabase().insertWithOnConflict(
                EmployeeDBHelper.TABLE_NAME,
                null,
                EmployeeDBHelper.getContentValues(employee),
                SQLiteDatabase.CONFLICT_REPLACE
        );

        isSuccess = (insertResult != -1);
        if (isSuccess) {
            //update cache
            EmployeeCache.getInstance().setEmpCache(employee);

        }
        return isSuccess;
    }


    /**
     * 批量插入雇员  更新雇员缓存
     *
     * @param employeeList
     * @return
     */
    public boolean batchInsertEmployee(List<Employee> employeeList) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.beginTransaction();

        for (Employee employee : employeeList) {
            if (TextUtils.isEmpty(String.valueOf(employee.id))) {
                continue;
            }
            getWritableDatabase().insertWithOnConflict(
                    EmployeeDBHelper.TABLE_NAME,
                    EmployeeDBHelper.DBColumn.EMPLOYEE_ID,
                    EmployeeDBHelper.getContentValues(employee),
                    SQLiteDatabase.CONFLICT_REPLACE);
        }
        sqLiteDatabase.setTransactionSuccessful();
        sqLiteDatabase.endTransaction();

        //update cache
        EmployeeCache.getInstance().setEmpCacheList(employeeList);

        return true;
    }

    public boolean batchInsertEmpCaller(List<IncomingCaller> callers) {
        SQLiteDatabase sqLiteDatabase = EmpIncomingCallRepository.getWritableDatabase();
        sqLiteDatabase.beginTransaction();

        long start = System.currentTimeMillis();
        for (IncomingCaller caller : callers) {
            sqLiteDatabase.insertWithOnConflict(
                    EmpIncomingCallDBHelper.TABLE_NAME,
                    null,
                    EmpIncomingCallDBHelper.getContentValues(caller),
                    SQLiteDatabase.CONFLICT_REPLACE);
        }
        sqLiteDatabase.setTransactionSuccessful();
        long end = System.currentTimeMillis();
        Log.e("wtf", end - start + "");
        sqLiteDatabase.endTransaction();

        return true;
    }

    /**
     * 删除雇员
     */
    public boolean removeEmployee(String orgCode, String userId) {
        String removeSql = "delete from " + EmployeeDBHelper.TABLE_NAME + " where " + EmployeeDBHelper.DBColumn.ORG_CODE + " = ? and "
                + EmployeeDBHelper.DBColumn.USER_ID + "= ?";

        getWritableDatabase().execSQL(removeSql, new String[]{orgCode, userId});

        //update cache
        EmployeeCache.getInstance().removeEmpCache(userId, orgCode);

        return true;
    }


    /**
     * 搜索雇员，根据 mobile 或者 name 或者 nickname 搜索
     *
     * @param searchValue
     * @param orgCodeList 若为空, 则去数据库查询
     * @return
     */
    public List<Employee> searchEmployees(Context context, final String searchValue, List<String> orgCodeList) {

        List<String> searchCodeList = new ArrayList<>();
        if (ListUtil.isEmpty(orgCodeList)) {
            searchCodeList = OrganizationRepository.getInstance().queryLoginOrgCodeListSync(context);

        } else {
            searchCodeList.addAll(orgCodeList);
        }

        StringBuilder sb = new StringBuilder("select * from ");
        sb.append(EmployeeDBHelper.TABLE_NAME);

        sb.append(" where (( " + EmployeeDBHelper.DBColumn.NAME + " like ? or " + EmployeeDBHelper.DBColumn.ALIAS + " like ? or "
                + EmployeeDBHelper.DBColumn.PINYIN + " like ? or " + EmployeeDBHelper.DBColumn.INITIAL + " like ? or " + EmployeeDBHelper.DBColumn.MOBILE + " like ? "
                + " )");
        sb.append(" and (" + EmployeeDBHelper.DBColumn.ORG_CODE + " in (" + getInStringParams(searchCodeList) + "))");
        sb.append(")");
        List<Employee> employeeList = new ArrayList<>();
        Cursor cursor = getReadableDatabase().rawQuery(sb.toString(), new String[]{"%" + searchValue + "%", "%" + searchValue + "%", "%" + searchValue + "%", "%" + searchValue + "%", searchValue + "%"});
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Employee employee = EmployeeDBHelper.fromCursor(cursor);
                employeeList.add(employee);

            }
            cursor.close();
        }
        return employeeList;
    }


    /**
     * 查询本地不存在的userId，以便去网络上批量获取更新
     *
     * @param userIds
     * @return
     */
    public List<String> queryUnExistUserIds(final List<String> userIds, final String orgCode) {

        List<String> unExistsContacts = new ArrayList<>();
        String sql = "select " + EmployeeDBHelper.DBColumn.USER_ID +
                " from " + EmployeeDBHelper.TABLE_NAME +
                " where  " + EmployeeDBHelper.DBColumn.USER_ID + " in (" + getInStringParams(userIds) + ")" + " and " + EmployeeDBHelper.DBColumn.ORG_CODE + "=?";


        Cursor cursor = null;
        List<String> existsUserIdList = new ArrayList<>();
        try {
            cursor = getReadableDatabase().rawQuery(sql, new String[]{orgCode});
            int idIndex = cursor.getColumnIndex(EmployeeDBHelper.DBColumn.USER_ID);

            while (cursor.moveToNext()) {
                existsUserIdList.add(cursor.getString(idIndex));
            }

            for (String userId : userIds) {
                if (!existsUserIdList.contains(userId)) {
                    unExistsContacts.add(userId);
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return unExistsContacts;
    }

    /**
     * 根据 ids, org_code 查询 emp, 不存在的 id 则构建 空的 emp
     *
     * @param userIdsList
     * @param orgCode
     * @return
     */
    public List<Employee> queryEmpListByIdsWithNotExist(List<String> userIdsList, String orgCode) {
        List<Employee> empList = new ArrayList<>();
        Map<String, Employee> contactMap = new HashMap<>();

        String sql = "select * from " + EmployeeDBHelper.TABLE_NAME + " where " + " user_id_ in (" + getInStringParams(userIdsList) + ")" + " and " + EmployeeDBHelper.DBColumn.ORG_CODE + "=?";


        SQLiteDatabase db = getWritableDatabase();

        Cursor cursor = null;
        try {
            cursor = db.rawQuery(sql, new String[]{orgCode});
            while (cursor.moveToNext()) {
                Employee employee = EmployeeDBHelper.fromCursor(cursor);
                contactMap.put(employee.userId, employee);
            }

            for (String userId : userIdsList) {
                if (!contactMap.containsKey(userId)) {
                    Employee empNotExist = new Employee();
                    empNotExist.userId = userId;
                    empNotExist.orgCode = orgCode;

                    contactMap.put(userId, empNotExist);
                }
            }

            empList.addAll(contactMap.values());

            ContactHelper.sort(empList);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return empList;
    }


}
