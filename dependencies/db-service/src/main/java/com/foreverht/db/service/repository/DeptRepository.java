package com.foreverht.db.service.repository;

import android.database.Cursor;
import android.util.Pair;

import androidx.annotation.Nullable;

import com.foreverht.db.service.W6sBaseRepository;
import com.foreverht.db.service.dbHelper.DeptDBHelper;
import com.foreverht.db.service.dbHelper.DeptTreeDBHelper;
import com.foreverht.db.service.dbHelper.EmployeeDBHelper;
import com.foreveross.atwork.infrastructure.model.Employee;
import com.foreveross.atwork.infrastructure.model.orgization.Department;
import com.foreveross.atwork.infrastructure.model.orgization.DepartmentTree;
import com.foreveross.atwork.infrastructure.utils.LogUtil;
import com.foreveross.db.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class DeptRepository extends W6sBaseRepository {

    public static Pair<List<Department>, List<Employee>> queryDepartmentTrees(String parentId, int deptSkip, int deptLimit, int empSkip, int empLimit, int query) {

        List<Department> departmentList = new ArrayList<>();
        List<Employee> employeesList = new ArrayList<>();

        String orgIdInDeptDb = DeptDBHelper.getDetailDBColumn(DeptDBHelper.DBColumn.ORG_ID);
        String idInDeptTreeDb = DeptTreeDBHelper.getDetailDBColumn(DeptTreeDBHelper.DBColumn.ID);
        String parentIdInDeptTreeDb = DeptTreeDBHelper.getDetailDBColumn(DeptTreeDBHelper.DBColumn.PARENT_ID);
        String queryInDeptTreeDb = DeptTreeDBHelper.getDetailDBColumn(DeptTreeDBHelper.DBColumn.QUERY);
        String typeInDeptTreeDb = DeptTreeDBHelper.getDetailDBColumn(DeptTreeDBHelper.DBColumn.TYPE);
        String sortInDeptTreeDb = DeptTreeDBHelper.getDetailDBColumn(DeptTreeDBHelper.DBColumn.SORT);

        String deptSql = "select * from " + DeptDBHelper.TABLE_NAME + " inner join " + DeptTreeDBHelper.TABLE_NAME + " on " + orgIdInDeptDb + " = " + idInDeptTreeDb
                + " where " + queryInDeptTreeDb + " = ?" + " and " +  typeInDeptTreeDb  + " = ? " + " and (" + parentIdInDeptTreeDb + " = ? or " + idInDeptTreeDb + " = ?)"
                + " order by " + sortInDeptTreeDb + " limit " + deptLimit + " offset " + deptSkip;

        LogUtil.e("deptSql -> " + deptSql);

        Cursor cursor = getWritableDatabase().rawQuery(deptSql, new String[]{query + "", "CORP", parentId, parentId});
        try {
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    Department department = DeptDBHelper.fromCursor(cursor);
                    departmentList.add(department);
                }

            }

        } finally {
            if (cursor != null) {
                cursor.close();
            }

        }


        String employeeIdInEmployeeDb = EmployeeDBHelper.getDetailDBColumn(EmployeeDBHelper.DBColumn.EMPLOYEE_ID);

        String employeeSql = "select * from " + EmployeeDBHelper.TABLE_NAME + " inner join " + DeptTreeDBHelper.TABLE_NAME + " on " + employeeIdInEmployeeDb + " = " + idInDeptTreeDb
                + " where " + queryInDeptTreeDb + " = ?" + " and " +  typeInDeptTreeDb  + " = ? " + " and " + parentIdInDeptTreeDb + " = ?"
                + " order by " + sortInDeptTreeDb + " limit " + empLimit + " offset " + empSkip;


        LogUtil.e("employeeSql -> " + employeeSql);


        cursor = getWritableDatabase().rawQuery(employeeSql, new String[]{query + "", "EMPLOYEE", parentId});
        try {
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    Employee employee = EmployeeDBHelper.fromCursor(cursor);
                    employeesList.add(employee);
                }

            }

        } finally {
            if (cursor != null) {
                cursor.close();
            }

        }

        return new Pair<>(departmentList, employeesList);
    }


    public static boolean batchInsertDepts(List<Department> departments) {
        boolean result = true;
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        try {
            sqLiteDatabase.beginTransaction();

            for (Department department : departments) {
                long insertResult = sqLiteDatabase.insertWithOnConflict(
                        DeptDBHelper.TABLE_NAME,
                        null,
                        DeptDBHelper.getContentValue(department),
                        SQLiteDatabase.CONFLICT_REPLACE
                );
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



    public static boolean batchInsertDeptTrees(String parentId, int query, List<DepartmentTree> departmentTrees) {
        boolean result = true;
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        try {
            sqLiteDatabase.beginTransaction();

            removeDeptTrees(sqLiteDatabase, parentId, query);

            for (DepartmentTree departmentTree : departmentTrees) {
                long insertResult = sqLiteDatabase.insertWithOnConflict(
                        DeptTreeDBHelper.TABLE_NAME,
                        null,
                        DeptTreeDBHelper.getContentValue(departmentTree),
                        SQLiteDatabase.CONFLICT_REPLACE
                );
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

    public static boolean removeDeptTrees(@Nullable SQLiteDatabase writableDatabase, String parentId, int query) {
        if(null == writableDatabase) {
            writableDatabase = getWritableDatabase();
        }
        long result = writableDatabase.delete(
                DeptTreeDBHelper.TABLE_NAME,
                DeptTreeDBHelper.DBColumn.PARENT_ID + "=? and " + DeptTreeDBHelper.DBColumn.QUERY + "=?", new String[]{parentId, query + ""});

        return 0 < result;
    }


}
