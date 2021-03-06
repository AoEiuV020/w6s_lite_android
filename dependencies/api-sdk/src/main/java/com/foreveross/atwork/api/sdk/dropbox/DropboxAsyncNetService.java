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
     * ???????????????????????????????????????
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
     * ???????????????????????????????????????????????????
     * @param domainId      ???id
     * @param sourceType    ????????? USER DISCUSSION ORG
     * @param sourceId      ??????????????????id
     * @param parent        ????????????
     * @param keyword       ?????????
     * @param fileType      ???????????? TEXT ARCHIVE IMAGE VIDEO AUDIO APPLICATION OTHER
     * @param skip          ????????? ?????????
     * @param limit         ????????? ?????????????????????
     * @param sort          ??????  TIME NAME SIZE   ??????TIME
     * @param order         ??????  ASC  DESC
     * @param unlimited     true parent???????????????????????? false ????????????
     * @param refreshTime   ????????????????????????
     * @param ownerId       ????????????????????????id
     * @param ownerDomainId ??????????????????domainId
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
     * ???????????????????????????????????????
     * @param domainId      ???id
     * @param sourceType    ?????????
     * @param sourceId      ???????????????id
     * @param isDir         0??????????????? ?????????(0,1)  ??????1
     * @param requestParams ??????????????????
     *                          {
                                    "parent": "????????????????????????????????????????????????????????????????????????",
                                    "name" : "???????????????",
                                    "user" : {
                                        "name":"???????????????"
                                    }
                                }

                                ????????????
                                {
                                    "name" : "????????????.xml",
                                    "size":1000,
                                    "digest":"md5???",
                                    "file_id":"?????????????????????",
                                    "user" : {
                                        "name":"???????????????"
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
     * ????????????????????????
     * @param domainId      ???id
     * @param sourceType    ?????????
     * @param sourceId      ???????????????id
     * @param requestParams ?????????
     *                      {
                                "parent" : "???????????????(?????????)????????????,?????????????????????????????????",
                                "ids":[???????????????(?????????)?????????,????????????],
                                "target_parent" : "??????(?????????)???????????????????????????",
                                "user" : {
                                    "name":"???????????????"
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
     * ????????????????????????
     * @param domainId      ???id
     * @param sourceType    ?????????
     * @param sourceId      ???????????????id
     * @param requestParams  ?????????
                            ?????????
                            {
                                "parent" : "???????????????(?????????)????????????",?????????????????????????????????",
                                "ids":[???????????????(?????????)?????????,????????????],
                                "target_parent" : "??????(?????????)???????????????????????????",
                                "target_source_id":"??????(?????????)???????????????????????????????????????????????????ORG_CODE ??? ???????????? ID  ???  ????????? ID",
                                "target_source_type":"??????(?????????)?????????????????????????????????PERSONAL(??????), DISCUSSION(?????????), ORG(??????)",
                                "user" : {
                                    "name":"???????????????"
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
     * ????????????????????????
     * @param domainId      ???id
     * @param sourceType    ?????????
     * @param sourceId      ???????????????id
     * @param requestParams ?????????
                            ?????????
                            {
                                "parent" : "???????????????(?????????)????????????",?????????????????????????????????",
                                "ids":[???????????????(?????????)?????????,????????????],
                                "user" : {
                                    "name":"???????????????"
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
     * ???????????????????????????
     * @param domainId      ???id
     * @param sourceType    ?????????
     * @param sourceId      ???????????????id
     * @param requestParams ?????????
                            ?????????
                            {
                                "name" : "???????????????",
                                "user" : {
                                    "name":"???????????????"
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
     * ????????????????????????
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
     * ??????dropbox???http result
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
     * ??????records??????
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
