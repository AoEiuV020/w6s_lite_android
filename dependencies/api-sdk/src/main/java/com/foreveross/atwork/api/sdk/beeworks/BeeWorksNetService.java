package com.foreveross.atwork.api.sdk.beeworks;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.foreverht.threadGear.AsyncTaskThreadPool;
import com.foreveross.atwork.api.sdk.net.HttpResult;
import com.foreveross.atwork.api.sdk.net.HttpURLConnectionComponent;
import com.foreveross.atwork.infrastructure.beeworks.BeeWorks;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.UrlHandleHelper;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lingen on 16/1/6.
 */
public class BeeWorksNetService {

    private static final String TAG = BeeWorksNetService.class.getSimpleName();

    private static final boolean isTestEnv = true;


    private static BeeWorksNetService beeWorksNetService = new BeeWorksNetService();


    private static final String BEEWORKS_PREVIEW_CODE = "/work_plus/preview/";

    private static final String BEEWORKS_NEW_VERSION = "/work_plus/";

    private static final String SEPARATOR = "/";

    private BeeWorksNetService(){
    }


//    public void queryTabData(String tabId, final BeeWorksTabDataListener listener){
//
//        final String url = String.format(BEEWORKS_TAB_URL, BeeWorks.getInstance().config.beeWorksUrl,tabId);
//
//        new AsyncTask<Void, Void, HttpResult>() {
//            @Override
//            protected HttpResult doInBackground(Void... params) {
//                return HttpURLConnectionComponent.getInstance().getHttp(url);
//            }
//
//            @Override
//            protected void onPostExecute(HttpResult httpResult) {
//
//                if(httpResult.isNetSuccess()){
//                    listener.success(httpResult.result);
//                }
//                else{
//                    listener.fail();
//                }
//            }
//        }.execute();
//
//    }

    public String getMediaServerUrl() {
        if(StringUtils.isEmpty(BeeWorks.getInstance().config.mediaServerUrl)) {
            return isTestEnv ? "http://172.16.1.248/beeworks-res/v1/" : "http://res.workplus.io/v1/images/";
        }

        return UrlHandleHelper.makeSuffix(BeeWorks.getInstance().config.mediaServerUrl) + "v1/images/";
    }


    public void queryDefinitionViews(String url, final BeeWorksDefinitionViewsListener listener) {
        final StringBuffer sb = new StringBuffer();
        sb.append(BeeWorks.getInstance().config.beeWorksUrl);
        sb.append(url);
        new AsyncTask<Void, Void, HttpResult>() {
            @Override
            protected HttpResult doInBackground(Void... params) {
                return HttpURLConnectionComponent.getInstance().getHttp(sb.toString());
            }

            @Override
            protected void onPostExecute(HttpResult httpResult) {
                if(httpResult.isNetSuccess()){
                    listener.success(httpResult.result);
                }
                else{
                    listener.fail();
                }
            }
        }.execute();
    }

    public void queryPreviewByCode(String code, final BeeWorksPreviewListener listener) {
        final StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(BeeWorks.getInstance().config.beeWorksUrl)
                                                .append(BEEWORKS_PREVIEW_CODE)
                                                .append(BeeWorks.getInstance().beeWorksId)
                                                .append("?accessCode=")
                                                .append(code);
        new AsyncTask<Void, Void, HttpResult>() {
            @Override
            protected HttpResult doInBackground(Void... params) {
                return HttpURLConnectionComponent.getInstance().getHttp(stringBuffer.toString());
            }

            @Override
            protected void onPostExecute(HttpResult httpResult) {
                if(httpResult.isNetSuccess()){
                    try {
                        JSONObject jsonObject = new JSONObject(httpResult.result);
                        String status = jsonObject.optString("status");
                        if (TextUtils.isEmpty(status)) {
                            listener.success(httpResult.result);
                            return;
                        }
                        listener.fail();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return;
                }

                listener.fail();

            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    public void checkBeeWorksNewVersion(final BeeWorksPreviewListener listener) {
        if(!AtworkConfig.BEEWORKS_CHECK) {
            return;
        }

        final StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(BeeWorks.getInstance().config.beeWorksUrl)
                                                    .append(BEEWORKS_NEW_VERSION)
                                                    .append(BeeWorks.getInstance().beeWorksId).append(SEPARATOR)
                                                    .append("android").append(SEPARATOR)
                                                    .append("versions").append(SEPARATOR)
                                                    .append(BeeWorks.getInstance().versionId);
        new AsyncTask<Void, Void, HttpResult>() {
            @Override
            protected HttpResult doInBackground(Void... params) {
                return HttpURLConnectionComponent.getInstance().getHttp(stringBuffer.toString());
            }

            @Override
            protected void onPostExecute(HttpResult httpResult) {
                if(httpResult.isNetSuccess()){
                    try {
                        JSONObject jsonObject = new JSONObject(httpResult.result);
                        String status = jsonObject.optString("status");
                        if (TextUtils.isEmpty(status)) {
                            listener.success(httpResult.result);
                            return;
                        }
                        listener.fail();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return;
                }

                listener.fail();
            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }

    public static BeeWorksNetService getInstance(){
        return beeWorksNetService;
    }


    public interface BeeWorksTabDataListener{

        void success(String tabDatas);

        void fail();
    }

    public interface BeeWorksDefinitionViewsListener {
        void success(String data);

        void fail();
    }

    public interface BeeWorksPreviewListener{
        void success(String tabDatas);

        void fail();
    }


}
