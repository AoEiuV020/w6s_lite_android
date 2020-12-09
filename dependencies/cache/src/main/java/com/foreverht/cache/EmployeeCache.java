package com.foreverht.cache;

import android.util.LruCache;

import com.foreveross.atwork.infrastructure.model.Employee;

import java.util.List;

/**
 * Created by dasunsy on 2017/5/9.
 */

public class EmployeeCache extends BaseCache {
    private static EmployeeCache sInstance = new EmployeeCache();

    private LruCache<String, Employee> mEmpCache = new LruCache<>(mMaxMemory / 20);

    private EmployeeCache() {

    }

    public static EmployeeCache getInstance() {
        return sInstance;
    }

    public Employee getEmpCache(String userId, String orgCode) {
        return getEmpCache(userId + "_" + orgCode);
    }

    public Employee getEmpCache(String empCombinedId) {
        return mEmpCache.get(empCombinedId);
    }

    public void removeEmpCache(String userId, String orgCode) {
        removeEmpCache(userId + "_" + orgCode);
    }
    public void removeEmpCache(String empCombinedId) {
        mEmpCache.remove(empCombinedId);
    }

    public void setEmpCache(Employee emp) {
        mEmpCache.put(emp.getCombinedKey(), emp);
    }

    public void setEmpCacheList(List<Employee> employeeList) {
        for(Employee employee : employeeList) {
            setEmpCache(employee);
        }
    }
}
