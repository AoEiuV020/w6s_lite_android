package com.foreveross.atwork.api.sdk.Employee;

import android.content.Context;
import android.os.AsyncTask;
import androidx.annotation.NonNull;

import com.foreverht.threadGear.AsyncTaskThreadPool;
import com.foreveross.atwork.api.sdk.NetWorkFailListener;
import com.foreveross.atwork.api.sdk.net.HttpResult;
import com.foreveross.atwork.api.sdk.util.NetworkHttpResultErrorHandler;
import com.foreveross.atwork.infrastructure.model.Employee;

import java.util.List;

/**
 * Created by shadow on 2016/4/21.
 */
public class EmployeeAsyncNetService {

    private static final String TAG = EmployeeAsyncNetService.class.getSimpleName();

    private static EmployeeAsyncNetService sInstance = new EmployeeAsyncNetService();

    private EmployeeAsyncNetService() {

    }

    public static EmployeeAsyncNetService getInstance() {
        return sInstance;
    }




    /**
     * 修改雇员
     */
    public void modifyEmployeeInfo(final Context context, final String orgCode, final String employeeId, final String postPrams, final OnHandleEmployeeInfoListener listener) {

        new AsyncTask<Void, Void, HttpResult>() {

            @Override
            protected HttpResult doInBackground(Void... params) {
                return EmployeeSyncNetService.getInstance().modifyEmployeeInfo(context, orgCode, employeeId, postPrams);
            }

            @Override
            protected void onPostExecute(HttpResult httpResult) {

                if (httpResult.isRequestSuccess()) {
                    listener.onSuccess();

                } else {
                    NetworkHttpResultErrorHandler.handleHttpError(httpResult, listener);
                }
            }


        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());

    }

    public interface FetchEmployeeListener {

        void onFetchSuccess(List<Employee> employeeList);

        void onFail();
    }


    public interface QueryEmployeeInfoListener extends NetWorkFailListener{

        void onSuccess(@NonNull Employee employee);
    }

    public interface OnHandleEmployeeInfoListener extends NetWorkFailListener{

        void onSuccess();
    }


}
