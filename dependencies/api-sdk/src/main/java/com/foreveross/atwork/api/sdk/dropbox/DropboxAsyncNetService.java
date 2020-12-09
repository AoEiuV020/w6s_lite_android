package com.foreveross.atwork.api.sdk.dropbox;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.text.TextUtils;

import com.foreverht.threadGear.AsyncTaskThreadPool;
import com.foreveross.atwork.api.sdk.NetWorkFailListener;
import com.foreveross.atwork.api.sdk.dropbox.requestJson.FileTranslateRequest;
import com.foreveross.atwork.api.sdk.dropbox.responseJson.FileTranslateResponseJson;
import com.foreveross.atwork.api.sdk.dropbox.responseJson.ShareFileResponseJson;
import com.foreveross.atwork.api.sdk.dropbox.responseJson.ShareItemsRespJson;
import com.foreveross.atwork.api.sdk.net.HttpResult;
import com.foreveross.atwork.api.sdk.util.NetWorkHttpResultHelper;
import com.foreveross.atwork.infrastructure.model.dropbox.Dropbox;
import com.foreveross.atwork.infrastructure.model.dropbox.Requester;
import com.foreveross.atwork.infrastructure.model.dropbox.DropboxConfig;
import com.foreveross.atwork.infrastructure.model.dropbox.ShareItemRequester;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@%((((((((#&@@@((#((#@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@%(((((((((((((%@*((((@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@%(((/,/(((((((((@@@*#@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@#(((@@@((((((((((@@((@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@(((((((((((((((((((((@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@(((((((((((((((((((@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@(((((((((((((((*@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 *                       __           __
 * .--.--.--.-----.----.|  |--.-----.|  |.--.--.-----.
 * |  |  |  |  _  |   _||    <|  _  ||  ||  |  |__ --|
 * |________|_____|__|  |__|__|   __||__||_____|_____|
 *                            |__|
 * Created by reyzhang22 on 16/9/12.
 */
public class DropboxAsyncNetService {

    private static DropboxAsyncNetService sInstance = new DropboxAsyncNetService();

    public static DropboxAsyncNetService getInstance() {
        return sInstance;
    }


    /**
     * 全量或增量查询网盘全部数据
     * @return
     */
    @SuppressLint("StaticFieldLeak")
    public void getDropboxBySource(final Context context, final Requester requester, final DropboxConfig config, final List<Dropbox> dropboxes, final OnDropboxListener listener) {
        new AsyncTask<Void, Void, List<Dropbox>>() {

            @Override
            protected List<Dropbox> doInBackground(Void... params) {
                HttpResult httpResult = DropboxSyncNetService.getInstance().getDropboxBySource(context, requester, config);
                int status = NetWorkHttpResultHelper.getResultStatus(httpResult.result);
                List<Dropbox> curList = new ArrayList<>();
                if (httpResult.isNetSuccess() && status == 0) {
                    parseRecords(context, curList, config, httpResult);
                    dropboxes.addAll(curList);
                    if (curList.size() == requester.mLimit) {
                        requester.mSkip = dropboxes.size() + 1;
                        getDropboxBySource(context, requester, config, dropboxes, listener);
                        return null;
                    }

                }
                return dropboxes;
            }

            @Override
            protected void onPostExecute(List<Dropbox> resultList) {

                if (listener == null || resultList == null) {
                    return;
                }
                listener.onDropboxOpsSuccess(dropboxes);
            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }

    /**
     * 根据查询条件全量或增量查询网盘数据
     * @param domainId      域id
     * @param sourceType    源类型 USER DISCUSSION ORG
     * @param sourceId      对应源类型的id
     * @param parent        目录节点
     * @param keyword       关键字
     * @param fileType      文件类型 TEXT ARCHIVE IMAGE VIDEO AUDIO APPLICATION OTHER
     * @param skip          分页， 第几页
     * @param limit         分页， 一页多少条数据
     * @param sort          排序  TIME NAME SIZE   默认TIME
     * @param order         排序  ASC  DESC
     * @param unlimited     true parent无效，全量查询， false 分批查询
     * @param refreshTime   上次查询刷新时间
     * @param ownerId       查询该拥有者用户id
     * @param ownerDomainId 查询该拥有者domainId
     * @return
     */
//    public void getDropboxByParams(final Context context, final String domainId, final String sourceType, final String sourceId,
//                                   final String parent, final String keyword, final String fileType, final String skip,
//                                   final String limit, final String sort, final String order, final boolean unlimited,
//                                   final String refreshTime, final String ownerId, final String ownerDomainId, final DropboxConfig dropboxConfig) {
//        new AsyncTask<Void, Void, Void>() {
//
//            @Override
//            protected Void doInBackground(Void... params) {
//                DropboxSyncNetService service = DropboxSyncNetService.getInstance();
//                service.getDropboxByParams(context, domainId, sourceType, sourceId, parent, keyword, fileType, skip,
//                        limit, sort, order, unlimited, refreshTime, ownerId, ownerDomainId,dropboxConfig);
//                return null;
//            }
//        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());
//    }




    /**
     *
     * 根据域与类型创建相应文件夹
     * @param domainId      域id
     * @param sourceType    源类型
     * @param sourceId      相对于源的id
     * @param isDir         0表示是文件 取值为(0,1)  默认1
     * @param requestParams 文件夹示例：
     *                          {
                                    "parent": "父节点名称，如果在根节点创建，不传此字段或传空值",
                                    "name" : "文件夹名称",
                                    "user" : {
                                        "name":"操作者名字"
                                    }
                                }

                                文件示例
                                {
                                    "name" : "文件名称.xml",
                                    "size":1000,
                                    "digest":"md5码",
                                    "file_id":"文件的媒体标识",
                                    "user" : {
                                        "name":"操作者名字"
                                    }
                                }
     * @return
     */
    @SuppressLint("StaticFieldLeak")
    public void makeDropboxFileOrDir(final Context context, final String domainId, final Dropbox.SourceType sourceType, final String sourceId, final int isDir, final String requestParams, final DropboxConfig dropboxConfig, final boolean showError, final OnDropboxListener listener) {
        new AsyncTask<Void, Void, HttpResult>() {

            @Override
            protected HttpResult doInBackground(Void... params) {
                DropboxSyncNetService service = DropboxSyncNetService.getInstance();
                return service.makeDropboxFileOrDir(context, domainId, sourceType.toString(), sourceId, isDir, requestParams, dropboxConfig);
            }

            @Override
            protected void onPostExecute(HttpResult httpResult) {
                super.onPostExecute(httpResult);
                if (listener == null) {
                    return;
                }
                List<Dropbox> dropboxes = parseDropboxResult(context, httpResult, showError);
                int status = NetWorkHttpResultHelper.getResultStatus(httpResult.result);
                if (status == 0) {
                    listener.onDropboxOpsSuccess(dropboxes);
                    return;
                }
                listener.onDropboxOpsFail(status);

            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }

    /**
     * 移动文件或文件夹
     * @param domainId      域id
     * @param sourceType    源类型
     * @param sourceId      相对于源的id
     * @param requestParams 文件夹
     *                      {
                                "parent" : "待移动文件(文件夹)的父标识,如无父标识可不传或传空",
                                "ids":[待移动文件(文件夹)的标识,数据形式],
                                "target_parent" : "文件(文件夹)的移动到的新父标识",
                                "user" : {
                                    "name":"操作者名字"
                                }
                            }
     * @return
     */
    public void moveDropbox(final Context context, final String domainId, final String sourceType, final String sourceId, final String requestParams, final DropboxConfig dropboxConfig, final OnDropboxListener listener) {
        new AsyncTask<Void, Void, HttpResult>() {

            @Override
            protected HttpResult doInBackground(Void... params) {
                DropboxSyncNetService service = DropboxSyncNetService.getInstance();
                return service.moveDropbox(context, domainId, sourceType, sourceId, requestParams, dropboxConfig);
            }

            @Override
            protected void onPostExecute(HttpResult httpResult) {
                if (listener == null) {
                    return;
                }
                int status = NetWorkHttpResultHelper.getResultStatus(httpResult.result);
                if (httpResult.isNetSuccess() && status == 0) {
                    List<Dropbox> dropboxList = new ArrayList<>();
                    parseArrayDropbox(dropboxList, httpResult);
                    listener.onDropboxOpsSuccess(dropboxList);
                    return;
                }
                listener.onDropboxOpsFail(status);
            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }


    /**
     * 复制文件或文件夹
     * @param domainId      域id
     * @param sourceType    源类型
     * @param sourceId      相对于源的id
     * @param requestParams  文件夹
                            参数：
                            {
                                "parent" : "待移动文件(文件夹)的父标识",如无父标识可不传或传空",
                                "ids":[待复制文件(文件夹)的标识,数据形式],
                                "target_parent" : "文件(文件夹)的移动到的新父标识",
                                "target_source_id":"文件(文件夹)的移动到的新的网盘标识，对应组织的ORG_CODE 或 讨论组的 ID  或  用户的 ID",
                                "target_source_type":"文件(文件夹)的移动到的新的网盘类型PERSONAL(个人), DISCUSSION(讨论组), ORG(组织)",
                                "user" : {
                                    "name":"操作者名字"
                                }
                            }
     * @return
     */
    public void copyDropbox(final Context context, final String domainId, final String sourceType, final String sourceId, final String requestParams, final OnDropboxListener listener) {
        new AsyncTask<Void, Void, HttpResult>() {

            @Override
            protected HttpResult doInBackground(Void... params) {
                DropboxSyncNetService service = DropboxSyncNetService.getInstance();
                return service.copyDropbox(context, domainId, sourceType, sourceId, requestParams);
            }

            @Override
            protected void onPostExecute(HttpResult result) {
                super.onPostExecute(result);
                if (listener == null) {
                    return;
                }
                if (result == null) {
                    listener.onDropboxOpsFail(-1);
                }
                int status = NetWorkHttpResultHelper.getResultStatus(result.result);
                if (status == 0) {
                    List<Dropbox> list = new ArrayList<Dropbox>();
                    parseArrayDropbox(list, result);
                    listener.onDropboxOpsSuccess(list);
                    return;
                }
                listener.onDropboxOpsFail(status);
            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }

    /**
     * 移除文件或文件夹
     * @param domainId      域id
     * @param sourceType    源类型
     * @param sourceId      相对于源的id
     * @param requestParams 文件夹
                            参数：
                            {
                                "parent" : "待移动文件(文件夹)的父标识",如无父标识可不传或传空",
                                "ids":[待删除文件(文件夹)的标识,数据形式],
                                "user" : {
                                    "name":"操作者名字"
                                }
                            }

     * @return
     */
    public void removeDropbox(final Context context, final String domainId, final String sourceType, final String sourceId, final String requestParams, final OnDropboxListener listener) {
        new AsyncTask<Void, Void, HttpResult>() {

            @Override
            protected HttpResult doInBackground(Void... params) {
                return DropboxSyncNetService.getInstance().removeDropbox(context, domainId, sourceType, sourceId, requestParams);
            }

            @Override
            protected void onPostExecute(HttpResult result) {
                if (listener == null) {
                    return;
                }
                if (result == null) {
                    listener.onDropboxOpsFail(-1);
                }
                int status = NetWorkHttpResultHelper.getResultStatus(result.result);
                if (status == 0) {
                    listener.onDropboxOpsSuccess(null);
                    return;
                }
                listener.onDropboxOpsFail(status);

            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }



    /**
     * 重命名文件或文件夹
     * @param domainId      域id
     * @param sourceType    源类型
     * @param sourceId      相对于源的id
     * @param requestParams 文件夹
                            参数：
                            {
                                "name" : "新文件名称",
                                "user" : {
                                    "name":"操作者名字"
                                }
                            }

     * @return
     */
    @SuppressLint("StaticFieldLeak")
    public void renameDropbox(final Context context, final String domainId, final String sourceType, final String sourceId, final String fileId, final String requestParams, final OnDropboxListener listener) {
        new AsyncTask<Void, Void, HttpResult>() {

            @Override
            protected HttpResult doInBackground(Void... params) {
                DropboxSyncNetService service = DropboxSyncNetService.getInstance();
                return service.renameDropbox(context, domainId, sourceType, sourceId, fileId, requestParams);
            }

            @Override
            protected void onPostExecute(HttpResult result) {
                if (listener == null) {
                    return;
                }
                if (result == null) {
                    listener.onDropboxOpsFail(-1);
                }
                List<Dropbox> list = null;
                int status = NetWorkHttpResultHelper.getResultStatus(result.result);
                if (status == 0) {
                    list = parseDropboxResult(context, result, true);
                    listener.onDropboxOpsSuccess(list);
                    return;
                }
                Intent intent = new Intent("ACTION_FILTER_ERR_CODE");
                intent.putExtra("ERROR_CODE", status);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                listener.onDropboxOpsFail(status);

            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }

    public void renameDropbox(final Context context, Dropbox dropbox, final String requestParams, OnDropboxListener listener) {
        renameDropbox(context, dropbox.mDomainId, dropbox.mSourceType.toString(), dropbox.mSourceId, dropbox.mFileId, requestParams, listener);
    }

    /**
     * 设置网盘读写权限
     * @param context
     * @param domainId
     * @param sourceType
     * @param opsUserId
     * @param requestParams
     * @param listener
     */
    @SuppressLint("StaticFieldLeak")
    public void setDropboxRWSetting(final Context context, final String domainId, final Dropbox.SourceType sourceType, final String opsUserId, final String requestParams, final OnDropboxListener listener) {
        new AsyncTask<Void, Void, HttpResult>() {
            @Override
            protected HttpResult doInBackground(Void... params) {

                return DropboxSyncNetService.getInstance().setDropboxRWSetting(context, domainId, sourceType, opsUserId, requestParams);
            }

            @Override
            protected void onPostExecute(HttpResult httpResult) {
                if (listener == null) {
                    return;
                }
                if (httpResult == null) {
                    listener.onDropboxOpsFail(-1);
                }
                List<Dropbox> list = null;
                int status = NetWorkHttpResultHelper.getResultStatus(httpResult.result);
                if (status == 0) {
                    listener.onDropboxOpsSuccess(null);
                    return;
                }
                listener.onDropboxOpsFail(status);

            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }

    public void shareDropbox(final Context context, final String domainId, final Dropbox.SourceType sourceType, final String opsUserId, final String requestParams, final OnDropboxSharedListener listener) {
        new AsyncTask<Void, Void, ShareFileResponseJson>() {
            @Override
            protected ShareFileResponseJson doInBackground(Void... params) {
                HttpResult result = DropboxSyncNetService.getInstance().shareDropbox(context, domainId, sourceType, opsUserId, requestParams);
                if (result.isRequestSuccess()) {
                    return (ShareFileResponseJson) result.resultResponse;
                }
                if(result != null && result.resultResponse instanceof ShareFileResponseJson) {
                    return (ShareFileResponseJson) result.resultResponse;
                }
                return null;
            }

            @Override
            protected void onPostExecute(ShareFileResponseJson responseJson) {
                if (listener == null) {
                    return;
                }
                if (responseJson == null) {
                    listener.networkFail(-1, "");
                    return;
                }
                if (responseJson.status != 0) {
                    listener.networkFail(responseJson.status, responseJson.message);
                    return;
                }
                listener.onShearSuccess(responseJson);

            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }

    @SuppressLint("StaticFieldLeak")
    public void translateFile(final Context context, final FileTranslateRequest request, final OnFileTranslateListener listener) {
        new AsyncTask<Void, Void, HttpResult>() {
            @Override
            protected HttpResult doInBackground(Void... params) {
                return DropboxSyncNetService.getInstance().translateFile(context, request);
            }

            @Override
            protected void onPostExecute(HttpResult result) {
                if (result == null || listener == null) {
                    return;
                }
                int status = NetWorkHttpResultHelper.getResultStatus(result.result);
                if (status == 0) {
                    String resultText = NetWorkHttpResultHelper.getResultText(result.result);
                    FileTranslateResponseJson json = new Gson().fromJson(resultText, FileTranslateResponseJson.class);
                    listener.onFileTranslateSuccess(json.mTotalPage, json.mDestIds);
                    return;
                }
                listener.networkFail(status, result.error);

            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }

    public void getDropboxShareItems(final Context context, final ShareItemRequester requester, final OnFetchShareItemsListener listener) {
        new AsyncTask<Void, Void, HttpResult>() {
            @Override
            protected HttpResult doInBackground(Void... voids) {
                return DropboxSyncNetService.getInstance().getDropboxShareItems(context, requester);
            }

            @Override
            protected void onPostExecute(HttpResult result) {
                if (result == null || listener == null) {
                    return;
                }
                if (result.isRequestSuccess()) {
                    listener.getShareItems(((ShareItemsRespJson)result.resultResponse).mResult);
                    return;
                }
                listener.networkFail(result.statusCode, result.error);
            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }



    /**
     * 解析dropbox的http result
     * @param result
     * @return
     */
    private List<Dropbox> parseDropboxResult(Context context, HttpResult result, boolean showError) {
        List<Dropbox> dropboxList = null;
        int status = NetWorkHttpResultHelper.getResultStatus(result.result);
        if (result.isNetSuccess() && status == 0) {
            dropboxList = new ArrayList<>();
            paresSingleDropbox(dropboxList, result);
        } else {

            if(needTriggerBroadcast(status)) {
                if (result.statusCode == 400) {
                    status = 400;
                }
                Intent intent = new Intent("ACTION_FILTER_ERR_CODE");
                intent.putExtra("ERROR_CODE", status);
                intent.putExtra("SHOW_ERROR", showError);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            }


        }
        return dropboxList;
    }

    private boolean needTriggerBroadcast(int status) {
        return 204015 != status && 204014 != status;
    }

    /**
     * 解析records列表
     * @param dropboxes
     * @param result
     */
    public static void parseRecords(Context context, List<Dropbox> dropboxes, DropboxConfig dropboxConfig, HttpResult result) {
        String resultText = NetWorkHttpResultHelper.getResultText(result.result);
        try {
            JSONObject  json  = new JSONObject(resultText);
            if (dropboxConfig == null) {
                dropboxConfig = new DropboxConfig();
            }
            dropboxConfig.mReadOnly = json.optBoolean("readonly");
            dropboxConfig.mWatermark = json.optString("watermark");
            dropboxConfig.mMaxVolume = json.optString("total_size");
            JSONArray records = json.optJSONArray("records");
            if (records != null) {
                for (int i = 0 ; i < records.length(); i++) {
                    JSONObject subRecord = records.getJSONObject(i);
                    Dropbox dropbox = Dropbox.parser(subRecord);
                    if (dropbox == null) {
                        continue;
                    }
                    dropboxes.add(dropbox);
                }
            }
            Intent configIntent = new Intent("ACTION_DATA_DROPBOX_CONFIG");
            configIntent.putExtra("KEY_INTENT_DROPBOX_CONFIG", dropboxConfig);
            LocalBroadcastManager.getInstance(context).sendBroadcast(configIntent);
            JSONArray deleteList = json.optJSONArray("deleted_list");
            ArrayList<String> deleteStringList = new ArrayList<>();
            if (deleteList != null) {

                for (int i =0; i < deleteList.length(); i++) {
                    deleteStringList.add(deleteList.optString(i));
                }
            }
            if (deleteStringList.isEmpty()) {
                return;
            }
            Intent intent = new Intent("ACTION_DATA_DELETED");
            intent.putStringArrayListExtra("DELETED_LIST", deleteStringList);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void paresSingleDropbox(List<Dropbox> dropboxes, HttpResult result) {
        String resultText = NetWorkHttpResultHelper.getResultText(result.result);
        try {
            if (!TextUtils.isEmpty(resultText)) {
                JSONObject record = new JSONObject(resultText);
                Dropbox dropbox = Dropbox.parser(record);
                dropboxes.add(dropbox);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseArrayDropbox(List<Dropbox> dropboxes, HttpResult result) {
        String resultText = NetWorkHttpResultHelper.getResultText(result.result);
        int status = NetWorkHttpResultHelper.getResultStatus(result.result);
        if (!TextUtils.isEmpty(resultText)) {
            try {
                JSONArray jsonArray = new JSONArray(resultText);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject record = jsonArray.optJSONObject(i);
                    Dropbox dropbox = Dropbox.parser(record);
                    dropboxes.add(dropbox);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (dropboxes.isEmpty() && status == 0) {
            dropboxes.add(new Dropbox());
        }
    }

    public interface OnDropboxListener {
        void onDropboxOpsSuccess(List<Dropbox> dropboxes);
        void onDropboxOpsFail(int errorCode);
    }

    public interface OnFileTranslateListener extends NetWorkFailListener {
        void onFileTranslateSuccess(int totalPage, List<String> translateList);
    }

    public interface OnDropboxSharedListener extends NetWorkFailListener {
        void onShearSuccess(ShareFileResponseJson response);
    }

    public interface OnFetchShareItemsListener extends NetWorkFailListener {
        void getShareItems(ShareItemsRespJson.Result result);
    }

}
