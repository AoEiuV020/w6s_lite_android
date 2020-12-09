package com.foreveross.atwork.manager;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;

import com.foreverht.db.service.dbHelper.EmpIncomingCallDBHelper;
import com.foreverht.db.service.repository.EmpIncomingCallRepository;
import com.foreverht.db.service.repository.EmployeeRepository;
import com.foreverht.threadGear.AsyncTaskThreadPool;
import com.foreverht.threadGear.DbThreadPoolExecutor;
import com.foreveross.atwork.api.sdk.Employee.EmployeeSyncNetService;
import com.foreveross.atwork.api.sdk.Employee.requestModel.CallerRequester;
import com.foreveross.atwork.api.sdk.Employee.responseModel.CallerResponser;
import com.foreveross.atwork.api.sdk.net.HttpResult;
import com.foreveross.atwork.infrastructure.shared.EmpIncomingCallShareInfo;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.db.SQLiteDatabase;
import com.w6s.model.incomingCall.IncomingCaller;

import java.util.List;

public class EmpIncomingCallManager {

    private static final EmpIncomingCallManager sInstance = new EmpIncomingCallManager();

    public static EmpIncomingCallManager getInstance() {
        return sInstance;
    }


    public void startFetchInComingCallRemote(Context context) {
        EmpIncomingCallShareInfo.getInstance().setEmpIncomingCallSyncStatus(context, 1);
        CallerRequester requester = new CallerRequester();
        requester.mUserId = LoginUserInfo.getInstance().getLoginUserId(context);
        requester.mRefreshTime = EmpIncomingCallShareInfo.getInstance().getLastTimeFetchEmpCaller(context);
        asyncEmpIncomingCallRemote(context, requester, null);
    }

    /**
     * 异步查询企业通讯录数据
     * @param context
     * @param requester
     * @param listener
     */
    public void asyncEmpIncomingCallRemote(Context context, CallerRequester requester, EmployeeManager.OnEmpIncomingCallerListener listener) {

        new AsyncTask<Void, Void, HttpResult>() {
            @Override
            protected HttpResult doInBackground(Void... params) {
                return syncEmpIncomingCall(context, requester);
            }

            @Override
            protected void onPostExecute(HttpResult httpResult) {
                if (httpResult.isRequestSuccess()) {
                    CallerResponser callerResponser = (CallerResponser)httpResult.resultResponse;
                    List<IncomingCaller> callers = callerResponser.mResult.mCaller;
                    if (callers.size() == CallerRequester.REQUEST_CALLER_LIMIT) {
                        requester.mSkip += requester.mLimit;
//                        requester.mRefreshTime = callers.get(callers.size() - 1).getModifyTime();
                        asyncEmpIncomingCallRemote(context, requester, listener);
                        return;
                    }
                    EmpIncomingCallShareInfo.getInstance().setEmpIncomingCallSyncStatus(context, 0);
                    EmpIncomingCallShareInfo.getInstance().setlastSyncFinishTime(context, System.currentTimeMillis());
                    if (listener != null) {
                        listener.onSuccess();
                        return;
                    }
                }
                EmpIncomingCallShareInfo.getInstance().setEmpIncomingCallSyncStatus(context, -1);
                if (listener != null) {
                    listener.onFailure();
                }
            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }

    /**
     * 通过手机号码，异步查询企业通讯录数据
     * @param phoneNum
     * @param listener
     */
    public void asyncQueryEmpIncomingCaller(String phoneNum, EmployeeManager.OnQueryEmpIncomingCallerListener listener) {
        new AsyncTask<Void, Void, IncomingCaller>() {
            @Override
            protected IncomingCaller doInBackground(Void... voids) {
                return doSyncQueryEmpIncomingCaller(phoneNum);
            }

            @Override
            protected void onPostExecute(IncomingCaller caller) {
                if (listener == null) {
                    return;
                }
                listener.onFinish(caller);
            }
        }.executeOnExecutor(DbThreadPoolExecutor.getInstance());
    }

    /**
     * 同步获取远程企业通信录数据
     * @param context
     * @param requester
     * @return
     */
    private HttpResult syncEmpIncomingCall(Context context, CallerRequester requester) {
        HttpResult httpResult = EmployeeSyncNetService.getInstance().fetchEmpIncomingCallRemote(context, requester);
        if (httpResult.isRequestSuccess()) {
            if (httpResult.resultResponse instanceof CallerResponser) {
                CallerResponser response = (CallerResponser)httpResult.resultResponse;
                List<IncomingCaller> callers = response.mResult.mCaller;
                if (!ListUtil.isEmpty(callers)) {
                    EmployeeRepository.getInstance().batchInsertEmpCaller(callers);
                    EmpIncomingCallShareInfo.getInstance().setLastTimeFetchEmpCaller(context, callers.get(callers.size() - 1).getModifyTime());
                }
                doSyncBatchDeleteEmpIncomingCaller(response.mResult.mDeletes);
            }
        }
        return  httpResult;
    }

    /**
     * 同步产需企业通讯数据
     * @param phoneNum
     * @return
     */
    private IncomingCaller doSyncQueryEmpIncomingCaller(String phoneNum) {

        String sql = "select * from " + EmpIncomingCallDBHelper.TABLE_NAME + " where " + EmpIncomingCallDBHelper.DBColumn.MOBILE + " = ?";
        Cursor cursor = EmpIncomingCallRepository.getWritableDatabase().rawQuery(sql, new String[]{phoneNum});
        if (cursor == null) {
            return null;
        }
        IncomingCaller caller = EmpIncomingCallDBHelper.fromCursor(cursor);
        cursor.close();
        return caller;
    }

    /**
     * 同步删除数据
     * @param deletes
     */
    private void doSyncBatchDeleteEmpIncomingCaller(List<String> deletes) {
        if (ListUtil.isEmpty(deletes)){
            return;
        }

        SQLiteDatabase sqLiteDatabase = EmpIncomingCallRepository.getWritableDatabase();
        try {
            sqLiteDatabase.beginTransaction();
            for (String mobile : deletes) {
                String sql = "delete from " + EmpIncomingCallDBHelper.TABLE_NAME + " where " + EmpIncomingCallDBHelper.DBColumn.MOBILE + " = ?";
                sqLiteDatabase.execSQL(sql, new String[]{mobile});
            }
            sqLiteDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            sqLiteDatabase.endTransaction();
        }
    }
}
