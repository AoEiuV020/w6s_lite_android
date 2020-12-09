package com.foreveross.atwork.db.daoService;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.Nullable;

import com.foreverht.db.service.BaseDbService;
import com.foreverht.db.service.repository.DataSchemaRepository;
import com.foreverht.db.service.repository.EmployeeRepository;
import com.foreveross.atwork.infrastructure.model.Employee;
import com.foreveross.atwork.infrastructure.model.employee.DataSchema;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.manager.EmployeeManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by shadow on 2016/4/22.
 */
public class EmployeeDaoService extends BaseDbService {

    private static final String TAG = EmployeeDaoService.class.getSimpleName();

    private static EmployeeDaoService sInstance = new EmployeeDaoService();

    private EmployeeDaoService() {

    }

    public static EmployeeDaoService getInstance() {
        return sInstance;
    }


    /**
     * 查询本地雇员列表, 根据 user_id, orgList
     *
     * */
    @SuppressLint("StaticFieldLeak")
    public void queryEmpListLocal(String userId, List<String> orgList, final QueryEmployeeListListener listListener) {
        new AsyncTask<Void, Void, List<Employee>>() {
            @Override
            protected List<Employee> doInBackground(Void... params) {
                List<Employee> employees = new ArrayList<>();
                if (orgList.isEmpty()) {
                    return employees;
                }
                employees = EmployeeRepository.getInstance().queryEmployeeList(userId, orgList);

                return employees;
            }

            @Override
            protected void onPostExecute(List<Employee> employees) {
                if (listListener == null) {
                    return;

                }
                listListener.onEmployeeListCallback(employees);
            }
        }.executeOnExecutor(mDbExecutor);
    }



    /**
     * 根据雇员employee_id查询雇员
     */
    public void queryEmployeeByEmployeeId(final int employeeId, final OnQueryEmployeeListener listener) {
        new AsyncTask<Void, Void, Employee>() {
            @Override
            protected Employee doInBackground(Void... params) {
                return EmployeeRepository.getInstance().queryEmployeeByEmployeeId(employeeId);
            }

            @Override
            protected void onPostExecute(Employee employee) {
                if (listener == null) {
                    return;
                }
                listener.onEmployeeDataCallback(employee);
            }
        }.executeOnExecutor(mDbExecutor);
    }

    /**
     * 根据雇员 user_id orgCode 查询雇员
     */
    public void queryEmployeeByUserIdAndOrgCode(final String userId,final String orgCode, final OnQueryEmployeeListener listener) {
        new AsyncTask<Void, Void, Employee>() {
            @Override
            protected Employee doInBackground(Void... params) {
                return EmployeeRepository.getInstance().queryEmployeeByUserIdAndOrgCode(userId, orgCode);
            }

            @Override
            protected void onPostExecute(Employee employee) {
                if (listener == null) {
                    return;
                }
                listener.onEmployeeDataCallback(employee);
            }
        }.executeOnExecutor(mDbExecutor);
    }

    /**
     * 根据雇员employee_id查询雇员
     */
    public void queryEmployeeByEmployeeName(final String employeeName, final OnQueryEmployeeListener listener) {
        new AsyncTask<Void, Void, Employee>() {
            @Override
            protected Employee doInBackground(Void... params) {
                return EmployeeRepository.getInstance().queryEmployeeByEmployeeName(employeeName);
            }

            @Override
            protected void onPostExecute(Employee employee) {
                if (listener == null) {
                    return;
                }
                listener.onEmployeeDataCallback(employee);
            }
        }.executeOnExecutor(mDbExecutor);
    }

    /**
     * 插入一个雇员 ,同时更新雇员缓存
     *
     * @param employee
     */
    public void insertEmployee(final Employee employee) {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                return EmployeeRepository.getInstance().insertEmployee(employee);
            }
        }.executeOnExecutor(mDbExecutor);
    }

    /**
     * 批量异步插入雇员，同时更新雇员缓存, 更新 dataSchema
     *
     * @param employeeList
     */
    @SuppressLint("StaticFieldLeak")
    public void batchInsertEmployee(final List<Employee> employeeList) {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                return EmployeeRepository.getInstance().batchInsertEmployee(employeeList);
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if(result) {
                    DataSchemaDaoService.getInstance().batchInsertMulEmpsDataSchema(employeeList);
                }
            }
        }.executeOnExecutor(mDbExecutor);
    }


    @SuppressLint("StaticFieldLeak")
    public void batchInsertEmployeeCheckDataSchema(final List<Employee> employeeList) {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {

                Set<String> orgCodeSet = new HashSet<>();
                for(Employee employee : employeeList) {
                    if(null == employee.dataSchemaList) {
                        orgCodeSet.add(employee.orgCode);

                    }
                }

                HashMap<String, List<DataSchema>> dataSchemaHashMap = DataSchemaRepository.getInstance().queryDataSchemaByOrgCodes(orgCodeSet);

                for(Employee employee : employeeList) {
                    if(null == employee.dataSchemaList) {
                        employee.dataSchemaList = dataSchemaHashMap.get(employee.orgCode);
                    }
                }


                return EmployeeRepository.getInstance().batchInsertEmployee(employeeList);
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if(result) {
                    DataSchemaDaoService.getInstance().batchInsertMulEmpsDataSchema(employeeList);
                }
            }
        }.executeOnExecutor(mDbExecutor);
    }


    @SuppressLint("StaticFieldLeak")
    public void updateEmpAvatarAndName(Context ctx, final String userId, String orgCode, final String name, final String avatar) {
        if (StringUtils.isEmpty(name) || StringUtils.isEmpty(userId)) {
            return;
        }
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                Employee employee = EmployeeManager.getInstance().queryEmpInSync(ctx, userId, orgCode);

                if (isDifferent(employee, name)) {
                    //update cache
                    employee.name = name;

                    //update db
                    EmployeeRepository.getInstance().insertEmployee(employee);
                }
                return null;
            }

        }.executeOnExecutor(mDbExecutor);

    }

    private boolean isDifferent(@Nullable Employee employee, String name) {
        if(null == employee) {
            return false;
        }

        if(StringUtils.isEmpty(employee.name)) {
            return true;
        }

        if(employee.name.equals(name)) {
            return false;
        }

        return true;
    }


    /**
     * 删除雇员
     *
     */
    public void removeEmployee(String code ,String userId) {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                return EmployeeRepository.getInstance().removeEmployee(code,userId);
            }
        }.executeOnExecutor(mDbExecutor);
    }

    /**
     * 查询用户与登录用户之间机构集合监听器
     */
    public interface QueryEmployeeListListener {
        void onEmployeeListCallback(List<Employee> setList);
    }

    public interface OnQueryEmployeeListener {

        void onEmployeeDataCallback(Object... objects);
    }

}
